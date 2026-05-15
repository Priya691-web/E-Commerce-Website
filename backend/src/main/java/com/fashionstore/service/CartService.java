package com.fashionstore.service;

import com.fashionstore.model.CartItem;
import com.fashionstore.model.Coupon;
import com.fashionstore.model.User;

import java.util.List;

/**
 * Service interface for cart operations and business logic
 * Handles cart calculations, validations, and coupon processing
 */
public interface CartService {
    
    /**
     * Get cart items for a user
     */
    List<CartItem> getCartItems(int userId);
    
    /**
     * Add item to cart with business logic validation
     */
    boolean addToCart(int userId, int productId, String size, int quantity);
    
    /**
     * Update cart item quantity with validation
     */
    boolean updateCartItemQuantity(int cartItemId, int userId, int quantity);
    
    /**
     * Remove item from cart
     */
    boolean removeCartItem(int cartItemId, int userId);
    
    /**
     * Calculate cart total with business rules
     */
    double calculateCartTotal(int userId);
    
    /**
     * Calculate cart total with coupon discount
     */
    double calculateCartTotalWithCoupon(int userId, String couponCode);
    
    /**
     * Validate cart for checkout
     */
    boolean validateCartForCheckout(int userId);
    
    /**
     * Clear cart after successful order
     */
    boolean clearCart(int userId);
    
    /**
     * Get cart item count
     */
    int getCartItemCount(int userId);
    
    /**
     * Check if product is already in cart
     */
    boolean isProductInCart(int userId, int productId, String size);
}
