package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for recent searches
 * Used to track user search history and provide personalized suggestions
 */
public class RecentSearchDTO {
    private int searchId;
    private int userId;
    private String query;
    private String category;
    private int resultCount;
    private LocalDateTime searchedAt;
    private boolean isActive;

    public RecentSearchDTO() {}

    public RecentSearchDTO(int userId, String query, String category) {
        this.userId = userId;
        this.query = query;
        this.category = category;
        this.searchedAt = LocalDateTime.now();
        this.isActive = true;
    }

    // Getters and Setters
    public int getSearchId() { return searchId; }
    public void setSearchId(int searchId) { this.searchId = searchId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getResultCount() { return resultCount; }
    public void setResultCount(int resultCount) { this.resultCount = resultCount; }

    public LocalDateTime getSearchedAt() { return searchedAt; }
    public void setSearchedAt(LocalDateTime searchedAt) { this.searchedAt = searchedAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "RecentSearchDTO{" +
                "searchId=" + searchId +
                ", userId=" + userId +
                ", query='" + query + '\'' +
                ", category='" + category + '\'' +
                ", searchedAt=" + searchedAt +
                '}';
    }
}
