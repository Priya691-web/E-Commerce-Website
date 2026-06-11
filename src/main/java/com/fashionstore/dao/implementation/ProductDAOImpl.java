package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.ProductDAO;
import com.fashionstore.model.Product;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAOImpl implements ProductDAO {
    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    products.add(extractProduct(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products WHERE category_id = ? ORDER BY created_at DESC";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, categoryId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        products.add(extractProduct(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public Product getProductById(int id) {
        String query = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractProduct(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setImageUrl(rs.getString("image_url"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        return product;
    }
}
