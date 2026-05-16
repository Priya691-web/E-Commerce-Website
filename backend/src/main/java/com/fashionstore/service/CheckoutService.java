package com.fashionstore.service;

import com.fashionstore.model.Address;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Order;
import com.fashionstore.model.User;

import java.util.List;
import java.util.Map;

/**
 * Service interface for checkout operations and business logic
 * Handles checkout validation, address management, and order preparation
 */
public interface CheckoutService {
    
    /**
     * Validate cart for checkout
     */
    boolean validateCartForCheckout(int userId);
    
    /**
     * Get cart items for checkout
     */
    List<CartItem> getCheckoutCartItems(int userId);
    
    /**
     * Calculate checkout totals with discounts and taxes
     */
    Map<String, Double> calculateCheckoutTotals(int userId, String couponCode);
    
    /**
     * Validate shipping address
     */
    boolean validateShippingAddress(Address address);
    
    /**
     * Validate billing address
     */
    boolean validateBillingAddress(Address address);
    
    /**
     * Get user addresses for checkout
     */
    List<Address> getUserCheckoutAddresses(int userId);
    
    /**
     * Get default shipping address
     */
    Address getDefaultShippingAddress(int userId);
    
    /**
     * Get default billing address
     */
    Address getDefaultBillingAddress(int userId);
    
    /**
     * Prepare order for payment
     */
    Order prepareOrderForPayment(int userId, Address shippingAddress, Address billingAddress, String couponCode);
    
    /**
     * Validate checkout data
     */
    boolean validateCheckoutData(int userId, Map<String, Object> checkoutData);
    
    /**
     * Apply coupon to checkout
     */
    boolean applyCouponToCheckout(int userId, String couponCode);
    
    /**
     * Remove coupon from checkout
     */
    boolean removeCouponFromCheckout(int userId);

    /**
     * Process complete checkout flow including validation, stock reservation, and order creation
     */
    Order processCheckoutOrder(int userId, Map<String, Object> checkoutData) throws Exception;
}
