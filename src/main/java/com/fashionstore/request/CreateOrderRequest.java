package com.fashionstore.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Create Order Request DTO
 * Handles order creation requests with validation
 */
public class CreateOrderRequest {

    @NotNull(message = "Address ID is required")
    @Positive(message = "Address ID must be positive")
    @JsonProperty("address_id")
    private Integer addressId;

    @NotEmpty(message = "Order items cannot be empty")
    @JsonProperty("items")
    private List<OrderItemRequest> items;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("payment_method")
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("shipping_method")
    private String shippingMethod = "standard";

    @JsonProperty("gift_wrap")
    private Boolean giftWrap = false;

    @JsonProperty("gift_message")
    private String giftMessage;

    // Constructors
    public CreateOrderRequest() {}

    public CreateOrderRequest(Integer addressId, List<OrderItemRequest> items, String paymentMethod) {
        this.addressId = addressId;
        this.items = items;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public Boolean getGiftWrap() {
        return giftWrap;
    }

    public void setGiftWrap(Boolean giftWrap) {
        this.giftWrap = giftWrap;
    }

    public String getGiftMessage() {
        return giftMessage;
    }

    public void setGiftMessage(String giftMessage) {
        this.giftMessage = giftMessage;
    }

    // Utility methods
    public boolean hasCouponCode() {
        return couponCode != null && !couponCode.trim().isEmpty();
    }

    public boolean hasNotes() {
        return notes != null && !notes.trim().isEmpty();
    }

    public boolean isGiftWrap() {
        return Boolean.TRUE.equals(giftWrap);
    }

    public boolean hasGiftMessage() {
        return giftMessage != null && !giftMessage.trim().isEmpty();
    }

    public boolean isCodPayment() {
        return "COD".equalsIgnoreCase(paymentMethod);
    }

    public boolean isOnlinePayment() {
        return !isCodPayment() && paymentMethod != null;
    }

    public boolean isValidPaymentMethod() {
        return paymentMethod != null && (
            "STRIPE".equalsIgnoreCase(paymentMethod) ||
            "RAZORPAY".equalsIgnoreCase(paymentMethod) ||
            "PAYPAL".equalsIgnoreCase(paymentMethod) ||
            "COD".equalsIgnoreCase(paymentMethod)
        );
    }

    public int getTotalQuantity() {
        if (items == null) return 0;
        return items.stream().mapToInt(OrderItemRequest::getQuantity).sum();
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public boolean containsValidItems() {
        if (!hasItems()) return false;
        return items.stream().allMatch(OrderItemRequest::isValid);
    }

    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "addressId=" + addressId +
                ", itemCount=" + (items != null ? items.size() : 0) +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", couponCode='" + couponCode + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CreateOrderRequest that = (CreateOrderRequest) o;

        if (addressId != null ? !addressId.equals(that.addressId) : that.addressId != null) return false;
        if (items != null ? !items.equals(that.items) : that.items != null) return false;
        if (paymentMethod != null ? !paymentMethod.equals(that.paymentMethod) : that.paymentMethod != null) return false;
        if (couponCode != null ? !couponCode.equals(that.couponCode) : that.couponCode != null) return false;
        if (notes != null ? !notes.equals(that.notes) : that.notes != null) return false;
        if (shippingMethod != null ? !shippingMethod.equals(that.shippingMethod) : that.shippingMethod != null) return false;
        if (giftWrap != null ? !giftWrap.equals(that.giftWrap) : that.giftWrap != null) return false;
        return giftMessage != null ? giftMessage.equals(that.giftMessage) : that.giftMessage == null;
    }

    @Override
    public int hashCode() {
        int result = addressId != null ? addressId.hashCode() : 0;
        result = 31 * result + (items != null ? items.hashCode() : 0);
        result = 31 * result + (paymentMethod != null ? paymentMethod.hashCode() : 0);
        result = 31 * result + (couponCode != null ? couponCode.hashCode() : 0);
        result = 31 * result + (notes != null ? notes.hashCode() : 0);
        result = 31 * result + (shippingMethod != null ? shippingMethod.hashCode() : 0);
        result = 31 * result + (giftWrap != null ? giftWrap.hashCode() : 0);
        result = 31 * result + (giftMessage != null ? giftMessage.hashCode() : 0);
        return result;
    }

    /**
     * Inner class for order items
     */
    public static class OrderItemRequest {

        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        @JsonProperty("product_id")
        private Integer productId;

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        @JsonProperty("quantity")
        private Integer quantity;

        @NotBlank(message = "Size is required")
        @JsonProperty("size")
        private String size;

        // Constructors
        public OrderItemRequest() {}

        public OrderItemRequest(Integer productId, Integer quantity, String size) {
            this.productId = productId;
            this.quantity = quantity;
            this.size = size;
        }

        // Getters and Setters
        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        // Utility methods
        public boolean isValid() {
            return productId != null && productId > 0 &&
                   quantity != null && quantity > 0 &&
                   size != null && !size.trim().isEmpty();
        }

        @Override
        public String toString() {
            return "OrderItemRequest{" +
                    "productId=" + productId +
                    ", quantity=" + quantity +
                    ", size='" + size + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OrderItemRequest that = (OrderItemRequest) o;

            if (productId != null ? !productId.equals(that.productId) : that.productId != null) return false;
            if (quantity != null ? !quantity.equals(that.quantity) : that.quantity != null) return false;
            return size != null ? size.equals(that.size) : that.size == null;
        }

        @Override
        public int hashCode() {
            int result = productId != null ? productId.hashCode() : 0;
            result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
            result = 31 * result + (size != null ? size.hashCode() : 0);
            return result;
        }
    }
}
