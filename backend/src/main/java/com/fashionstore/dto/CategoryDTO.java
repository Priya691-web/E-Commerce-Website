package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Category Data Transfer Object
 */
public class CategoryDTO {

    private Integer categoryId;
    private String categoryName;
    private String slug;
    private Boolean active;
    private LocalDateTime createdAt;

    public CategoryDTO() {}

    public CategoryDTO(Integer categoryId, String categoryName, String slug) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.slug = slug;
    }

    @JsonProperty("id")
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("name")
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("slug")
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
