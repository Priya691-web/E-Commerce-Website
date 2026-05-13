package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for user recommendations
 * Used to store personalized product recommendations for users
 */
public class UserRecommendationDTO {
    private int recommendationId;
    private int userId;
    private int productId;
    private String productName;
    private String productImage;
    private double price;
    private double discountPercent;
    private String category;
    private String brand;
    private String recommendationType; // collaborative, content_based, trending, personalized
    private double score;
    private String reason;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private boolean clicked;
    private LocalDateTime clickedAt;

    public UserRecommendationDTO() {}

    public UserRecommendationDTO(int userId, int productId, String recommendationType, double score) {
        this.userId = userId;
        this.productId = productId;
        this.recommendationType = recommendationType;
        this.score = score;
        this.generatedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusDays(7); // Expire after 7 days
        this.isActive = true;
        this.clicked = false;
    }

    // Getters and Setters
    public int getRecommendationId() { return recommendationId; }
    public void setRecommendationId(int recommendationId) { this.recommendationId = recommendationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

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

    public String getRecommendationType() { return recommendationType; }
    public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isClicked() { return clicked; }
    public void setClicked(boolean clicked) { this.clicked = clicked; }

    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }

    @Override
    public String toString() {
        return "UserRecommendationDTO{" +
                "recommendationId=" + recommendationId +
                ", userId=" + userId +
                ", productId=" + productId +
                ", recommendationType='" + recommendationType + '\'' +
                ", score=" + score +
                ", isActive=" + isActive +
                '}';
    }
}
