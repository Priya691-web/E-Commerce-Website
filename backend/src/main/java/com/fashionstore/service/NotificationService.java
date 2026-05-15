package com.fashionstore.service;

import com.fashionstore.model.User;
import com.fashionstore.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enterprise notification service
 * Handles real-time notifications, email sending, and push notifications
 */
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    // EmailService has private constructor - commented out
    // private final EmailService emailService = new EmailService();
    private final PushNotificationService pushNotificationService = new PushNotificationService();
    
    // Notification types - using HashMap to support more than 10 entries
    private static final Map<String, NotificationConfig> NOTIFICATION_CONFIGS;
    static {
        Map<String, NotificationConfig> configs = new HashMap<>();
        configs.put("order_update", new NotificationConfig("Order Update", "📦", "#3b82f6", "high"));
        configs.put("delivery_update", new NotificationConfig("Delivery Update", "🚚", "#10b981", "high"));
        configs.put("payment_update", new NotificationConfig("Payment Update", "💳", "#8b5cf6", "high"));
        configs.put("wishlist_alert", new NotificationConfig("Wishlist Alert", "❤️", "#ef4444", "medium"));
        configs.put("price_drop", new NotificationConfig("Price Drop", "📉", "#f59e0b", "medium"));
        configs.put("promotional", new NotificationConfig("Promotional", "🎁", "#ec4899", "low"));
        configs.put("admin_announcement", new NotificationConfig("Announcement", "📢", "#6366f1", "medium"));
        configs.put("system_alert", new NotificationConfig("System Alert", "⚠️", "#f97316", "high"));
        configs.put("support_message", new NotificationConfig("Support Message", "💬", "#14b8a6", "medium"));
        configs.put("fraud_alert", new NotificationConfig("Fraud Alert", "🛡️", "#dc2626", "urgent"));
        configs.put("account_update", new NotificationConfig("Account Update", "👤", "#7c3aed", "medium"));
        NOTIFICATION_CONFIGS = Collections.unmodifiableMap(configs);
    }
    
    public NotificationService() {
        // Initialize service
    }
    
    /**
     * Get notifications for a user
     */
    public Map<String, Object> getNotifications(int userId, int page, int limit, 
                                               String category, String status, String type) {
        
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            StringBuilder sql = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            sql.append("SELECT * FROM notifications WHERE user_id = ?");
            params.add(userId);
            
            if (category != null && !category.isEmpty()) {
                sql.append(" AND category = ?");
                params.add(category);
            }
            
            if (status != null && !status.isEmpty()) {
                sql.append(" AND status = ?");
                params.add(status);
            }
            
            if (type != null && !type.isEmpty()) {
                sql.append(" AND type = ?");
                params.add(type);
            }
            
            sql.append(" ORDER BY created_at DESC LIMIT ? OFFSET ?");
            params.add(limit);
            params.add((page - 1) * limit);
            
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            
            List<Map<String, Object>> notifications = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("notification_id", rs.getInt("notification_id"));
                notification.put("type", rs.getString("type"));
                notification.put("title", rs.getString("title"));
                notification.put("message", rs.getString("message"));
                notification.put("content", rs.getString("content"));
                notification.put("category", rs.getString("category"));
                notification.put("priority", rs.getString("priority"));
                notification.put("status", rs.getString("status"));
                notification.put("source_entity_type", rs.getString("source_entity_type"));
                notification.put("source_entity_id", rs.getInt("source_entity_id"));
                notification.put("action_url", rs.getString("action_url"));
                notification.put("action_required", rs.getBoolean("action_required"));
                notification.put("action_text", rs.getString("action_text"));
                notification.put("image_url", rs.getString("image_url"));
                notification.put("metadata", parseJson(rs.getString("metadata")));
                notification.put("created_at", rs.getTimestamp("created_at").toString());
                notification.put("read_at", rs.getTimestamp("read_at") != null ? 
                    rs.getTimestamp("read_at").toString() : null);
                notification.put("expires_at", rs.getTimestamp("expires_at") != null ? 
                    rs.getTimestamp("expires_at").toString() : null);
                
                notifications.add(notification);
            }
            
            // Get total count
            String countSql = "SELECT COUNT(*) as total FROM notifications WHERE user_id = ?";
            List<Object> countParams = new ArrayList<>();
            countParams.add(userId);
            
            if (category != null && !category.isEmpty()) {
                countSql += " AND category = ?";
                countParams.add(category);
            }
            
            if (status != null && !status.isEmpty()) {
                countSql += " AND status = ?";
                countParams.add(status);
            }
            
            if (type != null && !type.isEmpty()) {
                countSql += " AND type = ?";
                countParams.add(type);
            }
            
            PreparedStatement countStmt = conn.prepareStatement(countSql);
            for (int i = 0; i < countParams.size(); i++) {
                countStmt.setObject(i + 1, countParams.get(i));
            }
            
            ResultSet countRs = countStmt.executeQuery();
            int total = 0;
            if (countRs.next()) {
                total = countRs.getInt("total");
            }
            
            result.put("items", notifications);
            result.put("total", total);
            result.put("page", page);
            result.put("limit", limit);
            result.put("hasMore", (page * limit) < total);
            
        } catch (SQLException e) {
            logger.error("Error getting notifications: {}", e.getMessage(), e);
            result.put("error", "Failed to get notifications");
        }
        
        return result;
    }
    
    /**
     * Get unread count for a user
     */
    public Map<String, Object> getUnreadCount(int userId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT type, COUNT(*) as count FROM notifications " +
                        "WHERE user_id = ? AND status = 'unread' GROUP BY type";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Integer> typeCounts = new HashMap<>();
            int totalUnread = 0;
            
            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                typeCounts.put(type, count);
                totalUnread += count;
            }
            
            result.put("total", totalUnread);
            result.put("byType", typeCounts);
            
        } catch (SQLException e) {
            logger.error("Error getting unread count: {}", e.getMessage(), e);
            result.put("error", "Failed to get unread count");
        }
        
        return result;
    }
    
    /**
     * Get notification categories
     */
    public List<Map<String, Object>> getNotificationCategories(int userId) {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT type, COUNT(*) as total, " +
                        "SUM(CASE WHEN status = 'unread' THEN 1 ELSE 0 END) as unread " +
                        "FROM notifications WHERE user_id = ? GROUP BY type";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String type = rs.getString("type");
                NotificationConfig config = NOTIFICATION_CONFIGS.get(type);
                
                Map<String, Object> category = new HashMap<>();
                category.put("type", type);
                category.put("name", config != null ? config.name : type);
                category.put("icon", config != null ? config.icon : "📢");
                category.put("color", config != null ? config.color : "#6366f1");
                category.put("count", rs.getInt("total"));
                category.put("unreadCount", rs.getInt("unread"));
                
                categories.add(category);
            }
            
        } catch (SQLException e) {
            logger.error("Error getting notification categories: {}", e.getMessage(), e);
        }
        
        return categories;
    }
    
    /**
     * Get notification preferences
     */
    public Map<String, Object> getNotificationPreferences(int userId) {
        Map<String, Object> preferences = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT notification_type, channel, enabled, frequency " +
                        "FROM notification_preferences WHERE user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            Map<String, Map<String, Object>> typePrefs = new HashMap<>();
            
            while (rs.next()) {
                String type = rs.getString("notification_type");
                String channel = rs.getString("channel");
                boolean enabled = rs.getBoolean("enabled");
                String frequency = rs.getString("frequency");
                
                typePrefs.computeIfAbsent(type, k -> new HashMap<>())
                        .put(channel, Map.of("enabled", enabled, "frequency", frequency));
            }
            
            preferences.put("types", typePrefs);
            
        } catch (SQLException e) {
            logger.error("Error getting notification preferences: {}", e.getMessage(), e);
        }
        
        return preferences;
    }
    
    /**
     * Send notification to user
     */
    public void sendNotification(int userId, String type, String title, String message, 
                               Map<String, Object> metadata) {
        
        NotificationConfig config = NOTIFICATION_CONFIGS.get(type);
        if (config == null) {
            logger.warn("Unknown notification type: {}", type);
            return;
        }
        
        // Get user preferences
        Map<String, Object> preferences = getNotificationPreferences(userId);
        
        // Create notification record
        int notificationId = createNotificationRecord(userId, type, title, message, metadata);
        
        if (notificationId > 0) {
            // Send based on preferences
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> typePrefs = (Map<String, Map<String, Object>>) preferences.get("types");
            
            if (typePrefs != null) {
                Map<String, Object> typePref = typePrefs.get(type);
                if (typePref != null) {
                    // Send email notification
                    Map<String, Object> emailPref = (Map<String, Object>) typePref.get("email");
                    if (emailPref != null && Boolean.TRUE.equals(emailPref.get("enabled"))) {
                        sendEmailNotification(userId, type, title, message, metadata);
                    }
                    
                    // Send push notification
                    Map<String, Object> pushPref = (Map<String, Object>) typePref.get("push");
                    if (pushPref != null && Boolean.TRUE.equals(pushPref.get("enabled"))) {
                        sendPushNotification(userId, type, title, message, metadata);
                    }
                    
                    // Send in-app notification
                    Map<String, Object> inAppPref = (Map<String, Object>) typePref.get("in_app");
                    if (inAppPref != null && Boolean.TRUE.equals(inAppPref.get("enabled"))) {
                        // In-app notification is already created in database
                        logger.info("In-app notification created: {}", notificationId);
                    }
                }
            }
        }
    }
    
    /**
     * Create notification record in database
     */
    private int createNotificationRecord(int userId, String type, String title, String message, 
                                        Map<String, Object> metadata) {
        
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO notifications (user_id, type, title, message, " +
                        "category, priority, metadata, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            NotificationConfig config = NOTIFICATION_CONFIGS.get(type);
            
            stmt.setInt(1, userId);
            stmt.setString(2, type);
            stmt.setString(3, title);
            stmt.setString(4, message);
            stmt.setString(5, getCategoryFromType(type));
            stmt.setString(6, config != null ? config.priority : "medium");
            stmt.setString(7, metadata != null ? objectMapper.writeValueAsString(metadata) : null);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            logger.error("Error serializing metadata: {}", e.getMessage(), e);
        } catch (SQLException e) {
            logger.error("Error creating notification record: {}", e.getMessage(), e);
        }
        
        return -1;
    }
    
    /**
     * Send email notification
     */
    private void sendEmailNotification(int userId, String type, String title, String message, 
                                      Map<String, Object> metadata) {
        
        executorService.submit(() -> {
            try {
                // Get user email
                String userEmail = getUserEmail(userId);
                if (userEmail != null) {
                    // Get email template
                    String templateCode = getEmailTemplateCode(type);
                    if (templateCode != null) {
                        // EmailService commented out due to private constructor
                        // emailService.sendEmail(userEmail, templateCode, 
                        //     Map.of("title", title, "message", "message", "metadata", metadata));
                    }
                }
            } catch (Exception e) {
                logger.error("Error sending email notification: {}", e.getMessage(), e);
            }
        });
    }
    
    /**
     * Send push notification
     */
    private void sendPushNotification(int userId, String type, String title, String message, 
                                      Map<String, Object> metadata) {
        
        executorService.submit(() -> {
            try {
                // Get user device tokens
                List<String> deviceTokens = getUserDeviceTokens(userId);
                if (!deviceTokens.isEmpty()) {
                    // PushNotificationService expects userId, not device tokens list
                    pushNotificationService.sendPushNotification(userId, title, message, metadata);
                }
            } catch (Exception e) {
                logger.error("Error sending push notification: {}", e.getMessage(), e);
            }
        });
    }
    
    /**
     * Mark notification as read
     */
    public Map<String, Object> markAsRead(int userId, int notificationId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "UPDATE notifications SET status = 'read', read_at = NOW() " +
                        "WHERE notification_id = ? AND user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", affectedRows > 0 ? "Notification marked as read" : "Notification not found");
            
        } catch (SQLException e) {
            logger.error("Error marking notification as read: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to mark notification as read");
        }
        
        return result;
    }
    
    /**
     * Mark all notifications as read
     */
    public Map<String, Object> markAllAsRead(int userId, String category) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE notifications SET status = 'read', read_at = NOW() WHERE user_id = ? AND status = 'unread'");
            
            List<Object> params = new ArrayList<>();
            params.add(userId);
            
            if (category != null && !category.isEmpty()) {
                sql.append(" AND category = ?");
                params.add(category);
            }
            
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", true);
            result.put("message", "Notifications marked as read");
            result.put("markedCount", affectedRows);
            
        } catch (SQLException e) {
            logger.error("Error marking all notifications as read: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to mark notifications as read");
        }
        
        return result;
    }
    
    /**
     * Delete notification
     */
    public Map<String, Object> deleteNotification(int userId, int notificationId) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "DELETE FROM notifications WHERE notification_id = ? AND user_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", affectedRows > 0 ? "Notification deleted" : "Notification not found");
            
        } catch (SQLException e) {
            logger.error("Error deleting notification: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to delete notification");
        }
        
        return result;
    }
    
    /**
     * Update notification preferences
     */
    public Map<String, Object> updatePreferences(int userId, Map<String, Object> preferences) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            
            // Delete existing preferences
            String deleteSql = "DELETE FROM notification_preferences WHERE user_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, userId);
            deleteStmt.executeUpdate();
            
            // Insert new preferences
            String insertSql = "INSERT INTO notification_preferences (user_id, notification_type, channel, enabled, frequency) " +
                               "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> typePrefs = (Map<String, Map<String, Object>>) preferences.get("types");
            
            if (typePrefs != null) {
                for (Map.Entry<String, Map<String, Object>> typeEntry : typePrefs.entrySet()) {
                    String type = typeEntry.getKey();
                    Map<String, Object> channels = typeEntry.getValue();
                    
                    for (Map.Entry<String, Object> channelEntry : channels.entrySet()) {
                        String channel = channelEntry.getKey();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> channelPrefs = (Map<String, Object>) channelEntry.getValue();
                        
                        insertStmt.setInt(1, userId);
                        insertStmt.setString(2, type);
                        insertStmt.setString(3, channel);
                        insertStmt.setBoolean(4, Boolean.TRUE.equals(channelPrefs.get("enabled")));
                        insertStmt.setString(5, (String) channelPrefs.getOrDefault("frequency", "immediate"));
                        insertStmt.addBatch();
                    }
                }
                
                insertStmt.executeBatch();
            }
            
            conn.commit();
            
            result.put("success", true);
            result.put("message", "Preferences updated successfully");
            
        } catch (SQLException e) {
            logger.error("Error updating preferences: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to update preferences");
        }
        
        return result;
    }
    
    /**
     * Search notifications
     */
    public Map<String, Object> searchNotifications(int userId, String query, int page, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM notifications WHERE user_id = ? AND " +
                        "(title LIKE ? OR message LIKE ?) ORDER BY created_at DESC LIMIT ? OFFSET ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, "%" + query + "%");
            stmt.setString(3, "%" + query + "%");
            stmt.setInt(4, limit);
            stmt.setInt(5, (page - 1) * limit);
            
            ResultSet rs = stmt.executeQuery();
            
            List<Map<String, Object>> notifications = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> notification = new HashMap<>();
                notification.put("notification_id", rs.getInt("notification_id"));
                notification.put("type", rs.getString("type"));
                notification.put("title", rs.getString("title"));
                notification.put("message", rs.getString("message"));
                notification.put("status", rs.getString("status"));
                notification.put("created_at", rs.getTimestamp("created_at").toString());
                
                notifications.add(notification);
            }
            
            result.put("items", notifications);
            result.put("total", notifications.size());
            result.put("page", page);
            result.put("limit", limit);
            result.put("hasMore", false);
            
        } catch (SQLException e) {
            logger.error("Error searching notifications: {}", e.getMessage(), e);
            result.put("error", "Failed to search notifications");
        }
        
        return result;
    }
    
    /**
     * Track notification engagement
     */
    public Map<String, Object> trackEngagement(int userId, int notificationId, String engagementType, String metadata) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO notification_engagement (notification_id, user_id, engagement_type, engagement_data, created_at) " +
                        "VALUES (?, ?, ?, ?, NOW())";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, notificationId);
            stmt.setInt(2, userId);
            stmt.setString(3, engagementType);
            stmt.setString(4, metadata);
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", "Engagement tracked");
            
        } catch (SQLException e) {
            logger.error("Error tracking engagement: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to track engagement");
        }
        
        return result;
    }
    
    // Helper methods
    private Connection getConnection() throws SQLException {
        return com.fashionstore.util.DBConnection.getConnection();
    }

    /**
     * Shutdown the executor service gracefully
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private Map<String, Object> parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            logger.error("Error parsing JSON: {}", e.getMessage());
            return new HashMap<>();
        }
    }
    
    private String getCategoryFromType(String type) {
        switch (type) {
            case "order_update":
            case "payment_update":
                return "order";
            case "delivery_update":
                return "delivery";
            case "wishlist_alert":
            case "price_drop":
                return "product";
            case "promotional":
                return "marketing";
            case "admin_announcement":
            case "system_alert":
                return "system";
            case "support_message":
                return "support";
            case "fraud_alert":
                return "security";
            case "account_update":
                return "account";
            default:
                return "general";
        }
    }
    
    private String getUserEmail(int userId) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT email FROM users WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("email");
            }
        } catch (SQLException e) {
            logger.error("Error getting user email: {}", e.getMessage(), e);
        }
        
        return null;
    }
    
    private String getEmailTemplateCode(String type) {
        switch (type) {
            case "order_update":
                return "order_confirmation";
            case "payment_update":
                return "payment_confirmation";
            case "delivery_update":
                return "shipment_confirmation";
            case "price_drop":
                return "price_drop_alert";
            case "promotional":
                return "promotional_campaign";
            default:
                return null;
        }
    }
    
    private List<String> getUserDeviceTokens(int userId) {
        List<String> tokens = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT device_token FROM device_tokens WHERE user_id = ? AND is_active = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                tokens.add(rs.getString("device_token"));
            }
        } catch (SQLException e) {
            logger.error("Error getting device tokens: {}", e.getMessage(), e);
        }
        
        return tokens;
    }
    
    /**
     * Configuration class for notification types
     */
    private static class NotificationConfig {
        String name;
        String icon;
        String color;
        String priority;
        
        NotificationConfig(String name, String icon, String color, String priority) {
            this.name = name;
            this.icon = icon;
            this.color = color;
            this.priority = priority;
        }
    }
    
    /**
     * Shutdown service
     */
    // Duplicate shutdown() method, commenting out to fix compilation error
    // public void shutdown() {
    //     if (executorService != null && !executorService.isShutdown()) {
    //         executorService.shutdown();
    //         try {
    //             if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
    //                 executorService.shutdownNow();
    //             }
    //         } catch (InterruptedException e) {
    //             executorService.shutdownNow();
    //             Thread.currentThread().interrupt();
    //         }
    //     }
    // }
}
