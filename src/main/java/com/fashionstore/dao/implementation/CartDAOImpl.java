package com.fashionstore.dao.implementation;

import com.fashionstore.dao.interfaces.CartDAO;
import com.fashionstore.model.Cart;
import com.fashionstore.model.CartItem;
import com.fashionstore.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAOImpl implements CartDAO {
    @Override
    public Cart getCartByUserId(int userId) {
        String query = "SELECT * FROM carts WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Cart cart = new Cart();
                        cart.setId(rs.getInt("id"));
                        cart.setUserId(rs.getInt("user_id"));
                        cart.setCreatedAt(rs.getTimestamp("created_at"));
                        return cart;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean createCart(int userId) {
        String query = "INSERT INTO carts (user_id) VALUES (?)";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, userId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addItemToCart(int cartId, int variantId, int quantity) {
        String checkQuery = "SELECT id, quantity FROM cart_items WHERE cart_id = ? AND product_variant_id = ?";
        String updateQuery = "UPDATE cart_items SET quantity = quantity + ? WHERE id = ?";
        String insertQuery = "INSERT INTO cart_items (cart_id, product_variant_id, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
                checkPs.setInt(1, cartId);
                checkPs.setInt(2, variantId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        int itemId = rs.getInt("id");
                        try (PreparedStatement updatePs = conn.prepareStatement(updateQuery)) {
                            updatePs.setInt(1, quantity);
                            updatePs.setInt(2, itemId);
                            return updatePs.executeUpdate() > 0;
                        }
                    } else {
                        try (PreparedStatement insertPs = conn.prepareStatement(insertQuery)) {
                            insertPs.setInt(1, cartId);
                            insertPs.setInt(2, variantId);
                            insertPs.setInt(3, quantity);
                            return insertPs.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<CartItem> getCartItems(int cartId) {
        List<CartItem> items = new ArrayList<>();
        String query = "SELECT * FROM cart_items WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, cartId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        CartItem item = new CartItem();
                        item.setId(rs.getInt("id"));
                        item.setCartId(rs.getInt("cart_id"));
                        item.setProductVariantId(rs.getInt("product_variant_id"));
                        item.setQuantity(rs.getInt("quantity"));
                        items.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public boolean removeItemFromCart(int cartItemId) {
        String query = "DELETE FROM cart_items WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, cartItemId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean clearCart(int cartId) {
        String query = "DELETE FROM cart_items WHERE cart_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) throw new SQLException("Database connection is null");
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, cartId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
