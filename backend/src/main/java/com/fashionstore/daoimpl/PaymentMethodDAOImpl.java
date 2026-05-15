package com.fashionstore.daoimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fashionstore.dao.PaymentMethodDAO;
import com.fashionstore.model.PaymentMethod;
import com.fashionstore.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodDAOImpl implements PaymentMethodDAO {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodDAOImpl.class);

    @Override
    public boolean addPaymentMethod(PaymentMethod paymentMethod) {
        String sql = "INSERT INTO payment_methods (user_id, method_type, provider, method_alias, " +
                     "last_four, expiry_month, expiry_year, card_brand, is_default, " +
                     "is_active, gateway_token) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (paymentMethod.isDefault()) {
                unsetDefaultPaymentMethods(conn, paymentMethod.getUserId());
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, paymentMethod.getUserId());
                pstmt.setString(2, paymentMethod.getMethodType());
                pstmt.setString(3, paymentMethod.getProvider());
                pstmt.setString(4, paymentMethod.getMethodAlias());
                pstmt.setString(5, paymentMethod.getLastFour());
                pstmt.setObject(6, paymentMethod.getExpiryMonth());
                pstmt.setObject(7, paymentMethod.getExpiryYear());
                pstmt.setString(8, paymentMethod.getCardBrand());
                pstmt.setBoolean(9, paymentMethod.isDefault());
                pstmt.setBoolean(10, paymentMethod.isActive());
                pstmt.setString(11, paymentMethod.getGatewayToken());

                int result = pstmt.executeUpdate();
                if (result > 0) {
                    try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            paymentMethod.setPaymentMethodId(generatedKeys.getInt(1));
                        }
                    }
                    conn.commit();
                    return true;
                }
                conn.rollback();
            }
        } catch (SQLException e) {
            rollbackQuietly(conn);
            logger.error("PaymentMethodDAOImpl.addPaymentMethod Error: {}", e.getMessage(), e);
        } finally {
            closeQuietly(conn);
        }
        return false;
    }

    @Override
    public boolean updatePaymentMethod(PaymentMethod paymentMethod) {
        String sql = "UPDATE payment_methods SET method_type = ?, provider = ?, method_alias = ?, " +
                     "last_four = ?, expiry_month = ?, expiry_year = ?, card_brand = ?, " +
                     "is_default = ?, is_active = ?, gateway_token = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE payment_method_id = ? AND user_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            if (paymentMethod.isDefault()) {
                unsetDefaultPaymentMethods(conn, paymentMethod.getUserId());
            }

            boolean updated;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, paymentMethod.getMethodType());
                pstmt.setString(2, paymentMethod.getProvider());
                pstmt.setString(3, paymentMethod.getMethodAlias());
                pstmt.setString(4, paymentMethod.getLastFour());
                pstmt.setObject(5, paymentMethod.getExpiryMonth());
                pstmt.setObject(6, paymentMethod.getExpiryYear());
                pstmt.setString(7, paymentMethod.getCardBrand());
                pstmt.setBoolean(8, paymentMethod.isDefault());
                pstmt.setBoolean(9, paymentMethod.isActive());
                pstmt.setString(10, paymentMethod.getGatewayToken());
                pstmt.setInt(11, paymentMethod.getPaymentMethodId());
                pstmt.setInt(12, paymentMethod.getUserId());
                updated = pstmt.executeUpdate() > 0;
            }

            if (updated) {
                conn.commit();
            } else {
                conn.rollback();
            }
            return updated;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            logger.error("PaymentMethodDAOImpl.updatePaymentMethod Error: {}", e.getMessage(), e);
        } finally {
            closeQuietly(conn);
        }
        return false;
    }

    @Override
    public boolean deletePaymentMethod(int paymentMethodId, int userId) {
        String sql = "DELETE FROM payment_methods WHERE payment_method_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paymentMethodId);
            pstmt.setInt(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.deletePaymentMethod Error: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public PaymentMethod getPaymentMethodById(int paymentMethodId, int userId) {
        String sql = "SELECT * FROM payment_methods WHERE payment_method_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paymentMethodId);
            pstmt.setInt(2, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentMethodFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.getPaymentMethodById Error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<PaymentMethod> getPaymentMethodsByUserId(int userId) {
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        String sql = "SELECT * FROM payment_methods WHERE user_id = ? AND is_active = TRUE " +
                     "ORDER BY is_default DESC, created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    paymentMethods.add(extractPaymentMethodFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.getPaymentMethodsByUserId Error: {}", e.getMessage());
        }
        return paymentMethods;
    }

    @Override
    public PaymentMethod getDefaultPaymentMethod(int userId) {
        String sql = "SELECT * FROM payment_methods WHERE user_id = ? AND is_default = TRUE " +
                     "AND is_active = TRUE ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentMethodFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.getDefaultPaymentMethod Error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean setDefaultPaymentMethod(int paymentMethodId, int userId) {
        String unsetSql = "UPDATE payment_methods SET is_default = FALSE WHERE user_id = ?";
        String updateSql = "UPDATE payment_methods SET is_default = TRUE, " +
                           "updated_at = CURRENT_TIMESTAMP WHERE payment_method_id = ? AND user_id = ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(unsetSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }

            boolean updated;
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, paymentMethodId);
                updatePstmt.setInt(2, userId);
                updated = updatePstmt.executeUpdate() > 0;
            }

            if (updated) {
                conn.commit();
            } else {
                // No row matched the (paymentMethodId, userId) pair – revert the unset.
                conn.rollback();
            }
            return updated;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            logger.error("PaymentMethodDAOImpl.setDefaultPaymentMethod Error: {}", e.getMessage(), e);
        } finally {
            closeQuietly(conn);
        }
        return false;
    }

    @Override
    public boolean paymentMethodExists(int paymentMethodId, int userId) {
        String sql = "SELECT COUNT(*) FROM payment_methods WHERE payment_method_id = ? AND user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, paymentMethodId);
            pstmt.setInt(2, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.paymentMethodExists Error: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public int getPaymentMethodCount(int userId) {
        String sql = "SELECT COUNT(*) FROM payment_methods WHERE user_id = ? AND is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.getPaymentMethodCount Error: {}", e.getMessage());
        }
        return 0;
    }

    @Override
    public PaymentMethod getPaymentMethodByToken(String gatewayToken, String gateway) {
        String sql = "SELECT * FROM payment_methods WHERE gateway_token = ? AND provider = ? AND is_active = TRUE";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, gatewayToken);
            pstmt.setString(2, gateway);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractPaymentMethodFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.getPaymentMethodByToken Error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public boolean updateGatewayToken(int paymentMethodId, String gatewayToken) {
        String sql = "UPDATE payment_methods SET gateway_token = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE payment_method_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, gatewayToken);
            pstmt.setInt(2, paymentMethodId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("PaymentMethodDAOImpl.updateGatewayToken Error: {}", e.getMessage());
        }
        return false;
    }

    // Helper method to extract PaymentMethod from ResultSet
    private PaymentMethod extractPaymentMethodFromResultSet(ResultSet rs) throws SQLException {
        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setPaymentMethodId(rs.getInt("payment_method_id"));
        paymentMethod.setUserId(rs.getInt("user_id"));
        paymentMethod.setMethodType(rs.getString("method_type"));
        paymentMethod.setProvider(rs.getString("provider"));
        paymentMethod.setMethodAlias(rs.getString("method_alias"));
        paymentMethod.setLastFour(rs.getString("last_four"));
        
        Integer expiryMonth = rs.getObject("expiry_month", Integer.class);
        Integer expiryYear = rs.getObject("expiry_year", Integer.class);
        paymentMethod.setExpiryMonth(expiryMonth);
        paymentMethod.setExpiryYear(expiryYear);
        
        paymentMethod.setCardBrand(rs.getString("card_brand"));
        paymentMethod.setDefault(rs.getBoolean("is_default"));
        paymentMethod.setActive(rs.getBoolean("is_active"));
        paymentMethod.setGatewayToken(rs.getString("gateway_token"));
        paymentMethod.setCreatedAt(rs.getTimestamp("created_at"));
        paymentMethod.setUpdatedAt(rs.getTimestamp("updated_at"));
        return paymentMethod;
    }

    // Helper method to unset default payment methods – reuses the caller's connection
    // so that the unset and the subsequent insert/update participate in the same transaction.
    private void unsetDefaultPaymentMethods(Connection conn, int userId) throws SQLException {
        String sql = "UPDATE payment_methods SET is_default = FALSE WHERE user_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn == null) return;
        try {
            conn.rollback();
        } catch (SQLException ex) {
            logger.error("PaymentMethodDAOImpl rollback failed: {}", ex.getMessage());
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn == null) return;
        try {
            conn.setAutoCommit(true);
        } catch (SQLException ignored) { /* connection may already be invalid */ }
        try {
            conn.close();
        } catch (SQLException ex) {
            logger.error("PaymentMethodDAOImpl close failed: {}", ex.getMessage());
        }
    }
}
