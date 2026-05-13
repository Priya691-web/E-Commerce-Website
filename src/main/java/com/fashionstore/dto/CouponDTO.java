package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Coupon Data Transfer Object
 * Provides safe coupon data representation without internal details
 */
@JsonPropertyOrder({"couponId", "code", "description", "discountType", "discountValue", "minimumOrderAmount", "maximumDiscountAmount", "usageLimit", "usageCount", "userUsageLimit", "validFrom", "validUntil", "active"})
public class CouponDTO {

    private Integer couponId;
    private String code;
    private String description;
    private String discountType;
    private Double discountValue;
    private BigDecimal minimumOrderAmount;
    private BigDecimal maximumDiscountAmount;
    private Integer usageLimit;
    private Integer usageCount;
    private Integer userUsageLimit;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean active;

    // Internal fields (not exposed)
    @JsonIgnore
    private LocalDateTime createdAt;

    @JsonIgnore
    private LocalDateTime updatedAt;

    @JsonIgnore
    private String createdBy;

    // Constructors
    public CouponDTO() {}

    public CouponDTO(Integer couponId, String code, String description, String discountType, Double discountValue) {
        this.couponId = couponId;
        this.code = code;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    // Getters and Setters
    @JsonProperty("id")
    public Integer getCouponId() {
        return couponId;
    }

    public void setCouponId(Integer couponId) {
        this.couponId = couponId;
    }

    @JsonProperty("code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("discount_type")
    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    @JsonProperty("discount_value")
    public Double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }

    @JsonProperty("minimum_order_amount")
    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    @JsonProperty("maximum_discount_amount")
    public BigDecimal getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }

    public void setMaximumDiscountAmount(BigDecimal maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
    }

    @JsonProperty("usage_limit")
    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    @JsonProperty("usage_count")
    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    @JsonProperty("user_usage_limit")
    public Integer getUserUsageLimit() {
        return userUsageLimit;
    }

    public void setUserUsageLimit(Integer userUsageLimit) {
        this.userUsageLimit = userUsageLimit;
    }

    @JsonProperty("valid_from")
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    @JsonProperty("valid_until")
    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    // Internal setters (not exposed)
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    // Utility methods
    public boolean isPercentageDiscount() {
        return "percentage".equalsIgnoreCase(discountType);
    }

    public boolean isFixedAmountDiscount() {
        return "fixed".equalsIgnoreCase(discountType) || "amount".equalsIgnoreCase(discountType);
    }

    public boolean isExpired() {
        return validUntil != null && LocalDateTime.now().isAfter(validUntil);
    }

    public boolean isNotYetValid() {
        return validFrom != null && LocalDateTime.now().isBefore(validFrom);
    }

    public boolean isValid() {
        return Boolean.TRUE.equals(active) && !isExpired() && !isNotYetValid();
    }

    public boolean hasUsageLimit() {
        return usageLimit != null && usageLimit > 0;
    }

    public boolean hasReachedUsageLimit() {
        return hasUsageLimit() && usageCount != null && usageCount >= usageLimit;
    }

    public boolean hasUserUsageLimit() {
        return userUsageLimit != null && userUsageLimit > 0;
    }

    public boolean hasMinimumOrderAmount() {
        return minimumOrderAmount != null && minimumOrderAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasMaximumDiscountAmount() {
        return maximumDiscountAmount != null && maximumDiscountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isApplicable(BigDecimal orderAmount) {
        if (!isValid()) return false;
        if (hasReachedUsageLimit()) return false;
        if (hasMinimumOrderAmount() && orderAmount.compareTo(minimumOrderAmount) < 0) return false;
        return true;
    }

    public BigDecimal calculateDiscountAmount(BigDecimal orderAmount) {
        if (!isApplicable(orderAmount)) return BigDecimal.ZERO;

        BigDecimal discount = BigDecimal.ZERO;

        if (isPercentageDiscount()) {
            discount = orderAmount.multiply(BigDecimal.valueOf(discountValue / 100.0));
        } else if (isFixedAmountDiscount()) {
            discount = BigDecimal.valueOf(discountValue);
        }

        // Apply maximum discount limit if set
        if (hasMaximumDiscountAmount() && discount.compareTo(maximumDiscountAmount) > 0) {
            discount = maximumDiscountAmount;
        }

        return discount;
    }

    public String getDisplayDiscountType() {
        if (isPercentageDiscount()) {
            return discountValue + "% OFF";
        } else if (isFixedAmountDiscount()) {
            return "$" + discountValue + " OFF";
        }
        return discountType;
    }

    public String getValidityStatus() {
        if (!Boolean.TRUE.equals(active)) {
            return "Inactive";
        } else if (isExpired()) {
            return "Expired";
        } else if (isNotYetValid()) {
            return "Not Yet Valid";
        } else if (hasReachedUsageLimit()) {
            return "Usage Limit Reached";
        } else {
            return "Valid";
        }
    }

    @Override
    public String toString() {
        return "CouponDTO{" +
                "couponId=" + couponId +
                ", code='" + code + '\'' +
                ", discountType='" + discountType + '\'' +
                ", discountValue=" + discountValue +
                ", active=" + active +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CouponDTO that = (CouponDTO) o;

        if (couponId != null ? !couponId.equals(that.couponId) : that.couponId != null) return false;
        return code != null ? code.equals(that.code) : that.code == null;
    }

    @Override
    public int hashCode() {
        int result = couponId != null ? couponId.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        return result;
    }
}
