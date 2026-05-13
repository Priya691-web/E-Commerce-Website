package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fashionstore.model.CartItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Cart Data Transfer Object
 * Provides safe cart data representation without internal details
 */
@JsonPropertyOrder({"cartId", "userId", "items", "totalAmount", "totalQuantity", "createdAt", "updatedAt"})
public class CartDTO {

    private Integer cartId;
    private Integer userId;
    private List<CartItem> items;
    private BigDecimal totalAmount;
    private Integer totalQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Internal fields (not exposed)
    @JsonIgnore
    private Boolean active;

    @JsonIgnore
    private String sessionId;

    // Constructors
    public CartDTO() {}

    public CartDTO(Integer cartId, Integer userId, List<CartItem> items, BigDecimal totalAmount) {
        this.cartId = cartId;
        this.userId = userId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.totalQuantity = items != null ? items.stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }

    // Getters and Setters
    @JsonProperty("id")
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("items")
    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        // Recalculate total quantity when items change
        this.totalQuantity = items != null ? items.stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }

    @JsonProperty("total_amount")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @JsonProperty("total_quantity")
    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("updated_at")
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Internal setters (not exposed)
    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    // Utility methods
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }

    public boolean hasItems() {
        return !isEmpty();
    }

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public boolean containsProduct(Integer productId) {
        if (items == null) return false;
        return items.stream().anyMatch(item -> productId.equals(item.getProductId()));
    }

    public CartItem getItem(Integer productId) {
        if (items == null) return null;
        return items.stream()
                .filter(item -> productId.equals(item.getProductId()))
                .findFirst()
                .orElse(null);
    }

    public BigDecimal calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return items.stream()
                .map(item -> java.math.BigDecimal.valueOf(item.getPrice()))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    public void recalculateTotals() {
        this.totalAmount = calculateTotalAmount();
        this.totalQuantity = items != null ? items.stream().mapToInt(CartItem::getQuantity).sum() : 0;
    }

    public boolean exceedsMaxQuantity(int maxQuantity) {
        return totalQuantity > maxQuantity;
    }

    public boolean exceedsMaxAmount(BigDecimal maxAmount) {
        return totalAmount.compareTo(maxAmount) > 0;
    }

    public boolean containsOutOfStockItems() {
        if (items == null) return false;
        return items.stream().anyMatch(item -> !item.isAvailable());
    }

    public List<CartItem> getOutOfStockItems() {
        if (items == null) return List.of();
        return items.stream()
                .filter(item -> !item.isAvailable())
                .toList();
    }

    public boolean isValidForCheckout() {
        return hasItems() && !containsOutOfStockItems();
    }

    public List<String> getValidationErrors() {
        List<String> errors = new java.util.ArrayList<>();
        
        if (isEmpty()) {
            errors.add("Cart is empty");
        }
        
        if (containsOutOfStockItems()) {
            errors.add("Some items are out of stock");
        }
        
        return errors;
    }

    @Override
    public String toString() {
        return "CartDTO{" +
                "cartId=" + cartId +
                ", userId=" + userId +
                ", itemCount=" + getItemCount() +
                ", totalAmount=" + totalAmount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CartDTO cartDTO = (CartDTO) o;

        if (cartId != null ? !cartId.equals(cartDTO.cartId) : cartDTO.cartId != null) return false;
        if (userId != null ? !userId.equals(cartDTO.userId) : cartDTO.userId != null) return false;
        return items != null ? items.equals(cartDTO.items) : cartDTO.items == null;
    }

    @Override
    public int hashCode() {
        int result = cartId != null ? cartId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }
}
