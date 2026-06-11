package com.fashionstore.daoimpl;

import com.fashionstore.dao.SettingsDAO;
import com.fashionstore.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * SettingsDAOImpl - Settings Data Access Object Implementation
 */
public class SettingsDAOImpl implements SettingsDAO {

    private static final Logger logger = LoggerFactory.getLogger(SettingsDAOImpl.class);

    @Override
    public String getSetting(String key) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, key);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("setting_value");
            }
            
        } catch (SQLException e) {
            logger.error("Error getting setting {}: {}", key, e.getMessage(), e);
        }
        
        return null;
    }

    @Override
    public Map<String, String> getAllSettings() {
        String sql = "SELECT setting_key, setting_value FROM settings";
        Map<String, String> settings = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                settings.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
            
        } catch (SQLException e) {
            logger.error("Error getting all settings: {}", e.getMessage(), e);
        }
        
        return settings;
    }

    @Override
    public boolean setSetting(String key, String value) {
        String checkSql = "SELECT setting_id FROM settings WHERE setting_key = ?";
        String updateSql = "UPDATE settings SET setting_value = ?, updated_at = CURRENT_TIMESTAMP WHERE setting_key = ?";
        String insertSql = "INSERT INTO settings (setting_key, setting_value, setting_type, description) VALUES (?, ?, 'string', '')";
        
        try (Connection conn = DBConnection.getConnection()) {
            // Check if setting exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, key);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Update existing
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setString(1, value);
                        updateStmt.setString(2, key);
                        return updateStmt.executeUpdate() > 0;
                    }
                } else {
                    // Insert new
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, key);
                        insertStmt.setString(2, value);
                        return insertStmt.executeUpdate() > 0;
                    }
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error setting {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteSetting(String key) {
        String sql = "DELETE FROM settings WHERE setting_key = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, key);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting setting {}: {}", key, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean settingExists(String key) {
        return getSetting(key) != null;
    }
}
