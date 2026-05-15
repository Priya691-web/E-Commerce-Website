package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for recently viewed products
 * Used to track user browsing history and provide recommendations
 */
public class RecentlyViewedDTO {
    private int viewId;
    private int userId;
    private String sessionId;
    private int productId;
    private String productName;
    private String productImage;
    private double price;
    private double discountPercent;
    private String category;
    private String brand;
    private LocalDateTime viewedAt;
    private boolean isActive;

    public RecentlyViewedDTO() {}

    public RecentlyViewedDTO(int userId, int productId, String productName, String productImage) {
        this.userId = userId;
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.viewedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public int getViewId() { return viewId; }
    public void setViewId(int viewId) { this.viewId = viewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(double discountPercent) { this.discountPercent = discountPercent; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "RecentlyViewedDTO{" +
                "viewId=" + viewId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", viewedAt=" + viewedAt +
                '}';
    }
}
