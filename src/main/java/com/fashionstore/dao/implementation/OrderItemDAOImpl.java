package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.OrderItemDAO;
import com.fashionstore.model.OrderItem;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAOImpl implements OrderItemDAO {
    @Override
    public boolean addOrderItems(List<OrderItem> items) {
        String query = "INSERT INTO order_items (order_id, product_variant_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (OrderItem item : items) {
                    ps.setInt(1, item.getOrderId());
                    ps.setInt(2, item.getProductVariantId());
                    ps.setInt(3, item.getQuantity());
                    ps.setBigDecimal(4, item.getUnitPrice());
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                conn.commit();
                return results.length == items.size();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<OrderItem> getItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String query = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        OrderItem item = new OrderItem();
                        item.setId(rs.getInt("id"));
                        item.setOrderId(rs.getInt("order_id"));
                        item.setProductVariantId(rs.getInt("product_variant_id"));
                        item.setQuantity(rs.getInt("quantity"));
                        item.setUnitPrice(rs.getBigDecimal("unit_price"));
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
}
