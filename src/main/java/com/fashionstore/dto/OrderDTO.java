package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fashionstore.model.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Data Transfer Object
 * Provides safe order data representation without internal details
 */
@JsonPropertyOrder({"orderId", "userId", "fullName", "address", "totalAmount", "subtotal", "taxAmount", "shippingAmount", "discountAmount", "paymentMethod", "status", "paymentStatus", "orderDate", "items", "couponCode"})
public class OrderDTO {

    private Integer orderId;
    private Integer userId;
    private String fullName;
    private AddressDTO address;
    private BigDecimal totalAmount;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal shippingAmount;
    private BigDecimal discountAmount;
    private String paymentMethod;
    private String status;
    private String paymentStatus;
    private LocalDateTime orderDate;
    private List<OrderItem> items;
    private String couponCode;

    // Internal fields (not exposed)
    @JsonIgnore
    private String phone;

    @JsonIgnore
    private String email;

    @JsonIgnore
    private String trackingNumber;

    @JsonIgnore
    private LocalDateTime shippedDate;

    @JsonIgnore
    private LocalDateTime deliveredDate;

    // Constructors
    public OrderDTO() {}

    public OrderDTO(Integer orderId, Integer userId, String fullName, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.fullName = fullName;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters and Setters
    @JsonProperty("id")
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("full_name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @JsonProperty("address")
    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    @JsonProperty("total_amount")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @JsonProperty("subtotal")
    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @JsonProperty("tax_amount")
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    @JsonProperty("shipping_amount")
    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    public void setShippingAmount(BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    @JsonProperty("discount_amount")
    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    @JsonProperty("payment_method")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("payment_status")
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @JsonProperty("order_date")
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    @JsonProperty("items")
    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @JsonProperty("coupon_code")
    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    // Internal setters (not exposed)
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void setShippedDate(LocalDateTime shippedDate) {
        this.shippedDate = shippedDate;
    }

    public void setDeliveredDate(LocalDateTime deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    // Utility methods
    public boolean isPending() {
        return "Pending".equalsIgnoreCase(status);
    }

    public boolean isConfirmed() {
        return "Confirmed".equalsIgnoreCase(status);
    }

    public boolean isShipped() {
        return "Shipped".equalsIgnoreCase(status);
    }

    public boolean isDelivered() {
        return "Delivered".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "Cancelled".equalsIgnoreCase(status);
    }

    public boolean isRefunded() {
        return "Refunded".equalsIgnoreCase(status);
    }

    public boolean isPaymentPending() {
        return "pending".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPaymentCompleted() {
        return "completed".equalsIgnoreCase(paymentStatus) || "paid".equalsIgnoreCase(paymentStatus);
    }

    public boolean isPaymentFailed() {
        return "failed".equalsIgnoreCase(paymentStatus);
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public int getTotalQuantity() {
        if (items == null) return 0;
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public boolean hasDiscount() {
        return discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasCoupon() {
        return couponCode != null && !couponCode.trim().isEmpty();
    }

    public boolean isCodPayment() {
        return "COD".equalsIgnoreCase(paymentMethod);
    }

    public boolean isOnlinePayment() {
        return !isCodPayment() && paymentMethod != null;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + orderId +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDTO orderDTO = (OrderDTO) o;

        if (orderId != null ? !orderId.equals(orderDTO.orderId) : orderDTO.orderId != null) return false;
        if (userId != null ? !userId.equals(orderDTO.userId) : orderDTO.userId != null) return false;
        return orderDate != null ? orderDate.equals(orderDTO.orderDate) : orderDTO.orderDate == null;
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (orderDate != null ? orderDate.hashCode() : 0);
        return result;
    }
}
