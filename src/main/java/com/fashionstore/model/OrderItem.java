package com.fashionstore.model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private int orderId;
    private int productVariantId;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderItem() {}

    public OrderItem(int id, int orderId, int productVariantId, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.orderId = orderId;
        this.productVariantId = productVariantId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getProductVariantId() { return productVariantId; }
    public void setProductVariantId(int productVariantId) { this.productVariantId = productVariantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
}
