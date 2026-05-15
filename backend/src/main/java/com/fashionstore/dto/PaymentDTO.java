package com.fashionstore.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Payment Data Transfer Object
 * Provides safe payment data representation without sensitive information
 */
@JsonPropertyOrder({"paymentId", "orderId", "amount", "paymentMethod", "status", "transactionId", "createdAt", "processedAt"})
public class PaymentDTO {

    private Integer paymentId;
    private Integer orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    // Internal fields (not exposed)
    @JsonIgnore
    private String gatewayResponse;

    @JsonIgnore
    private String refundReason;

    @JsonIgnore
    private String failureReason;

    @JsonIgnore
    private String merchantTransactionId;

    @JsonIgnore
    private String gatewayTransactionId;

    @JsonIgnore
    private String cardLastFour;

    @JsonIgnore
    private String cardType;

    // Constructors
    public PaymentDTO() {}

    public PaymentDTO(Integer paymentId, Integer orderId, BigDecimal amount, String paymentMethod, String status) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Getters and Setters
    @JsonProperty("id")
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    @JsonProperty("order_id")
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @JsonProperty("amount")
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    @JsonProperty("transaction_id")
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @JsonProperty("created_at")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @JsonProperty("processed_at")
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    // Internal setters (not exposed)
    public void setGatewayResponse(String gatewayResponse) {
        this.gatewayResponse = gatewayResponse;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setMerchantTransactionId(String merchantTransactionId) {
        this.merchantTransactionId = merchantTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public void setCardLastFour(String cardLastFour) {
        this.cardLastFour = cardLastFour;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    // Utility methods
    public boolean isPending() {
        return "pending".equalsIgnoreCase(status);
    }

    public boolean isProcessing() {
        return "processing".equalsIgnoreCase(status);
    }

    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status) || "paid".equalsIgnoreCase(status);
    }

    public boolean isFailed() {
        return "failed".equalsIgnoreCase(status);
    }

    public boolean isRefunded() {
        return "refunded".equalsIgnoreCase(status);
    }

    public boolean isPartiallyRefunded() {
        return "partially_refunded".equalsIgnoreCase(status);
    }

    public boolean isCancelled() {
        return "cancelled".equalsIgnoreCase(status);
    }

    public boolean isSuccessful() {
        return isCompleted();
    }

    public boolean hasFailed() {
        return isFailed() || isCancelled();
    }

    public boolean isRefundable() {
        return isCompleted() && !isRefunded() && !isPartiallyRefunded();
    }

    public boolean isCodPayment() {
        return "COD".equalsIgnoreCase(paymentMethod);
    }

    public boolean isOnlinePayment() {
        return !isCodPayment() && paymentMethod != null;
    }

    public boolean isStripePayment() {
        return "STRIPE".equalsIgnoreCase(paymentMethod);
    }

    public boolean isRazorpayPayment() {
        return "RAZORPAY".equalsIgnoreCase(paymentMethod);
    }

    public boolean isPayPalPayment() {
        return "PAYPAL".equalsIgnoreCase(paymentMethod);
    }

    public String getDisplayStatus() {
        switch (status.toLowerCase()) {
            case "pending":
                return "Pending";
            case "processing":
                return "Processing";
            case "completed":
            case "paid":
                return "Completed";
            case "failed":
                return "Failed";
            case "refunded":
                return "Refunded";
            case "partially_refunded":
                return "Partially Refunded";
            case "cancelled":
                return "Cancelled";
            default:
                return status;
        }
    }

    public String getDisplayPaymentMethod() {
        switch (paymentMethod.toLowerCase()) {
            case "stripe":
                return "Credit/Debit Card";
            case "razorpay":
                return "Razorpay";
            case "paypal":
                return "PayPal";
            case "cod":
                return "Cash on Delivery";
            default:
                return paymentMethod;
        }
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "paymentId=" + paymentId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PaymentDTO that = (PaymentDTO) o;

        if (paymentId != null ? !paymentId.equals(that.paymentId) : that.paymentId != null) return false;
        if (orderId != null ? !orderId.equals(that.orderId) : that.orderId != null) return false;
        return transactionId != null ? transactionId.equals(that.transactionId) : that.transactionId == null;
    }

    @Override
    public int hashCode() {
        int result = paymentId != null ? paymentId.hashCode() : 0;
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        result = 31 * result + (transactionId != null ? transactionId.hashCode() : 0);
        return result;
    }
}
