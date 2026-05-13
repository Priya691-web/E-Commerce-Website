package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for search suggestions
 * Used for live search suggestions and trending searches
 */
public class SearchSuggestionDTO {
    private int suggestionId;
    private String query;
    private String category;
    private String type; // product, category, brand, trending
    private int popularity;
    private String imageUrl;
    private String productUrl;
    private int productId;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SearchSuggestionDTO() {}

    public SearchSuggestionDTO(String query, String type, int popularity) {
        this.query = query;
        this.type = type;
        this.popularity = popularity;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public int getSuggestionId() { return suggestionId; }
    public void setSuggestionId(int suggestionId) { this.suggestionId = suggestionId; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getPopularity() { return popularity; }
    public void setPopularity(int popularity) { this.popularity = popularity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "SearchSuggestionDTO{" +
                "suggestionId=" + suggestionId +
                ", query='" + query + '\'' +
                ", category='" + category + '\'' +
                ", type='" + type + '\'' +
                ", popularity=" + popularity +
                ", isActive=" + isActive +
                '}';
    }
}
