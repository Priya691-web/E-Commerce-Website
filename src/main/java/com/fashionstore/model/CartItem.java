package com.fashionstore.model;

public class CartItem {
    private int id;
    private int cartId;
    private int productVariantId;
    private int quantity;

    public CartItem() {}

    public CartItem(int id, int cartId, int productVariantId, int quantity) {
        this.id = id;
        this.cartId = cartId;
        this.productVariantId = productVariantId;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public int getProductVariantId() { return productVariantId; }
    public void setProductVariantId(int productVariantId) { this.productVariantId = productVariantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
