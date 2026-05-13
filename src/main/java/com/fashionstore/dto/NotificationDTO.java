package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for user notifications
 * Used for managing user notifications and alerts
 */
public class NotificationDTO {
    private int notificationId;
    private int userId;
    private String type; // order, payment, shipping, promotion, security, account
    private String title;
    private String message;
    private String actionUrl;
    private String actionText;
    private String imageUrl;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private int priority; // 1=low, 2=medium, 3=high, 4=urgent
    private String category; // info, success, warning, error
    private String metadata; // JSON string for additional data

    public NotificationDTO() {}

    public NotificationDTO(int userId, String type, String title, String message) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
        this.priority = 2; // default medium priority
        this.category = "info";
        this.isActive = true;
    }

    // Getters and Setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }

    public String getActionText() { return actionText; }
    public void setActionText(String actionText) { this.actionText = actionText; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    // Helper methods
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isHighPriority() {
        return priority >= 3;
    }

    public boolean isUrgent() {
        return priority == 4;
    }

    // Factory methods for common notification types
    public static NotificationDTO createOrderNotification(int userId, String orderId, String status) {
        NotificationDTO notification = new NotificationDTO(userId, "order", 
            "Order Update", "Your order #" + orderId + " is " + status);
        notification.setActionUrl("/orders");
        notification.setActionText("View Order");
        notification.setPriority(status.equals("delivered") ? 2 : 3);
        notification.setCategory(status.equals("delivered") ? "success" : "info");
        return notification;
    }

    public static NotificationDTO createPaymentNotification(int userId, String message, boolean isSuccess) {
        NotificationDTO notification = new NotificationDTO(userId, "payment", 
            "Payment Update", message);
        notification.setActionUrl("/orders");
        notification.setActionText("View Orders");
        notification.setPriority(isSuccess ? 2 : 4);
        notification.setCategory(isSuccess ? "success" : "error");
        return notification;
    }

    public static NotificationDTO createSecurityNotification(int userId, String message) {
        NotificationDTO notification = new NotificationDTO(userId, "security", 
            "Security Alert", message);
        notification.setActionUrl("/account/security");
        notification.setActionText("Review Security");
        notification.setPriority(4);
        notification.setCategory("warning");
        return notification;
    }

    public static NotificationDTO createPromotionNotification(int userId, String title, String message, String imageUrl) {
        NotificationDTO notification = new NotificationDTO(userId, "promotion", title, message);
        notification.setImageUrl(imageUrl);
        notification.setActionUrl("/products?tag=promotion");
        notification.setActionText("Shop Now");
        notification.setPriority(2);
        notification.setCategory("info");
        return notification;
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", isRead=" + isRead +
                ", priority=" + priority +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
