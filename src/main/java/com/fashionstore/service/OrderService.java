package com.fashionstore.service;

import com.fashionstore.model.Order;
import com.fashionstore.model.OrderItem;

import java.util.List;
import java.util.Map;

/**
 * Service interface for order operations and business logic
 * Handles order creation, processing, status updates, and management
 */
public interface OrderService {
    
    /**
     * Create new order with business logic validation
     */
    Order createOrder(int userId, Map<String, Object> orderData);
    
    /**
     * Get order by ID with business rules
     */
    Order getOrderById(int orderId, int requestingUserId);
    
    /**
     * Get orders for user
     */
    List<Order> getOrdersForUser(int userId);
    
    /**
     * Get all orders (admin)
     */
    List<Order> getAllOrders();
    
    /**
     * Get recent orders
     */
    List<Order> getRecentOrders(int limit);
    
    /**
     * Update order status with business validation
     */
    boolean updateOrderStatus(int orderId, String newStatus, int requestingUserId);
    
    /**
     * Process order payment
     */
    boolean processOrderPayment(int orderId, String paymentMethod, double amount);
    
    /**
     * Cancel order with business rules
     */
    boolean cancelOrder(int orderId, int requestingUserId);
    
    /**
     * Refund order with business rules
     */
    boolean refundOrder(int orderId, int requestingUserId);
    
    /**
     * Get order items
     */
    List<OrderItem> getOrderItems(int orderId);
    
    /**
     * Calculate order total
     */
    double calculateOrderTotal(int orderId);
    
    /**
     * Validate order for processing
     */
    boolean validateOrderForProcessing(int orderId);
    
    /**
     * Get order statistics
     */
    Map<String, Object> getOrderStatistics();
    
    /**
     * Batch load order items for multiple orders
     */
    void batchLoadOrderItems(List<Order> orders);
    
    /**
     * Get orders by status
     */
    List<Order> getOrdersByStatus(String status);
    
    /**
     * Get total revenue
     */
    double getTotalRevenue();
}
