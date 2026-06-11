package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.CategoryDAO;
import com.fashionstore.model.Category;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM categories";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    categories.add(extractCategory(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[CategoryDAO] Error in getAllCategories: " + e.getMessage());
            e.printStackTrace();
        }
        return categories;
    }

    @Override
    public Category getCategoryById(int id) {
        String query = "SELECT * FROM categories WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractCategory(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Category extractCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getInt("id"));
        category.setName(rs.getString("name"));
        category.setDescription(rs.getString("description"));
        category.setImageUrl(rs.getString("image_url"));
        return category;
    }
}
