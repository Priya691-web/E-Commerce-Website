package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.ProductVariantDAO;
import com.fashionstore.model.ProductVariant;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAOImpl implements ProductVariantDAO {
    @Override
    public List<ProductVariant> getVariantsByProductId(int productId) {
        List<ProductVariant> variants = new ArrayList<>();
        String query = "SELECT * FROM product_variants WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        variants.add(extractVariant(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return variants;
    }

    @Override
    public ProductVariant getVariantById(int id) {
        String query = "SELECT * FROM product_variants WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractVariant(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateStock(int variantId, int newQuantity) {
        String query = "UPDATE product_variants SET stock_quantity = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, newQuantity);
                ps.setInt(2, variantId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ProductVariant extractVariant(ResultSet rs) throws SQLException {
        ProductVariant variant = new ProductVariant();
        variant.setId(rs.getInt("id"));
        variant.setProductId(rs.getInt("product_id"));
        variant.setSize(rs.getString("size"));
        variant.setColor(rs.getString("color"));
        variant.setStockQuantity(rs.getInt("stock_quantity"));
        return variant;
    }
}
