package com.fashionstore.dao.interfaces;

import com.fashionstore.model.Order;
import java.util.List;

public interface OrderDAO {
    int createOrder(Order order); // Returns order ID
    Order getOrderById(int orderId);
    List<Order> getOrdersByUserId(int userId);
    boolean updateOrderStatus(int orderId, String status);
}
