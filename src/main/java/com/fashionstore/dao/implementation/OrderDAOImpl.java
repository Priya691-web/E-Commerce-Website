package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.OrderDAO;
import com.fashionstore.model.Order;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    @Override
    public int createOrder(Order order) {
        String query = "INSERT INTO orders (user_id, total_amount, status, shipping_address) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                ps.setBigDecimal(2, order.getTotalAmount());
                ps.setString(3, order.getStatus() != null ? order.getStatus() : "PENDING");
                ps.setString(4, order.getShippingAddress());
                
                if (ps.executeUpdate() > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            return rs.getInt(1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public Order getOrderById(int orderId) {
        String query = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractOrder(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        orders.add(extractOrder(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean updateOrderStatus(int orderId, String status) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, status);
                ps.setInt(2, orderId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Order extractOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        order.setShippingAddress(rs.getString("shipping_address"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        return order;
    }
}
