package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Product Data Transfer Object
 * Provides safe product data representation without internal details
 */
@JsonPropertyOrder({"productId", "name", "description", "price", "imageUrl", "active", "category", "brand", "sizes", "createdAt", "averageRating", "reviewCount", "isTrending"})
public class ProductDTO {

    private Integer productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private Boolean active;
    private CategoryDTO category;
    private String brand;
    private List<ProductSizeDTO> sizes;
    private LocalDateTime createdAt;
    private Double averageRating;
    private Integer reviewCount;
    private Boolean isTrending;

    // Internal fields (not exposed)
    @JsonIgnore
    private Integer categoryId;

    @JsonIgnore
    private Boolean featured;

    @JsonIgnore
    private Integer sortOrder;

    // Constructors
    public ProductDTO() {}

    public ProductDTO(Integer productId, String name, String description, BigDecimal price, String imageUrl, Boolean active) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.active = active;
    }

    // Getters and Setters
    @JsonProperty("id")
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty("image_url")
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("category")
    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    @JsonProperty("brand")
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @JsonProperty("sizes")
    public List<ProductSizeDTO> getSizes() {
        return sizes;
    }

    public void setSizes(List<ProductSizeDTO> sizes) {
        this.sizes = sizes;
    }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("average_rating")
    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    @JsonProperty("review_count")
    public Integer getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(Integer reviewCount) {
        this.reviewCount = reviewCount;
    }

    @JsonProperty("is_trending")
    public Boolean getIsTrending() {
        return isTrending;
    }

    public void setIsTrending(Boolean trending) {
        isTrending = trending;
    }

    // Internal setters (not exposed)
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public void setFeatured(Boolean featured) {
        this.featured = featured;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    // Utility methods
    public boolean isInStock() {
        return sizes != null && sizes.stream().anyMatch(ProductSizeDTO::isAvailable);
    }

    public int getTotalStock() {
        if (sizes == null) return 0;
        return sizes.stream().mapToInt(ProductSizeDTO::getStockQuantity).sum();
    }

    public BigDecimal getMinPrice() {
        if (sizes == null || sizes.isEmpty()) return price;
        return price; // Assuming same price for all sizes
    }

    public BigDecimal getMaxPrice() {
        if (sizes == null || sizes.isEmpty()) return price;
        return price; // Assuming same price for all sizes
    }

    public boolean hasReviews() {
        return reviewCount != null && reviewCount > 0;
    }

    public boolean isRated() {
        return averageRating != null && averageRating > 0;
    }

    @Override
    public String toString() {
        return "ProductDTO{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDTO that = (ProductDTO) o;

        if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
