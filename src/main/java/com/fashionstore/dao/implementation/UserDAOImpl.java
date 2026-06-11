package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.UserDAO;
import com.fashionstore.model.User;
import com.fashionstore.util.DBConnection;

import java.sql.*;

public class UserDAOImpl implements UserDAO {
    @Override
    public boolean createUser(User user) {
        String query = "INSERT INTO users (first_name, last_name, email, password_hash, role, address, phone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPasswordHash());
                ps.setString(5, user.getRole() != null ? user.getRole() : "CUSTOMER");
                ps.setString(6, user.getAddress());
                ps.setString(7, user.getPhone());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public User getUserByEmail(String email) {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User getUserById(int id) {
        String query = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return extractUser(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUser(User user) {
        String query = "UPDATE users SET first_name=?, last_name=?, address=?, phone=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, user.getFirstName());
                ps.setString(2, user.getLastName());
                ps.setString(3, user.getAddress());
                ps.setString(4, user.getPhone());
                ps.setInt(5, user.getId());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setAddress(rs.getString("address"));
        user.setPhone(rs.getString("phone"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
