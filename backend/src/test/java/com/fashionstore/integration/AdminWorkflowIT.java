package com.fashionstore.integration;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.daoimpl.OrderDAOImpl;
import com.fashionstore.model.Order;
import com.fashionstore.service.OrderService;
import com.fashionstore.serviceimpl.OrderServiceImpl;
import com.fashionstore.util.DBConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class AdminWorkflowIT {

    private static OrderService orderService;
    private static OrderDAO orderDAO;

    @BeforeAll
    public static void setUp() throws Exception {
        // Boot Testcontainers
        DatabaseTestContainer.start();
        orderService = new OrderServiceImpl();
        orderDAO = new OrderDAOImpl();

        // Seed some admin and order data
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT IGNORE INTO users (user_id, full_name, email, password, role) VALUES (100, 'Admin User', 'admin@test.com', 'pass', 'admin')")) {
                ps.executeUpdate();
            }
        }
    }

    @Test
    public void testOrderLifecycleTransitions() {
        // Create an order in "Pending" status
        Order order = new Order();
        order.setUserId(999);
        order.setTotalAmount(100.00);
        order.setFullName("Customer Name");
        order.setAddress("Customer Road");
        order.setCity("City");
        order.setState("State");
        order.setZip("12345");
        order.setPhone("123456789");
        order.setPaymentMethod("COD");
        order.setStatus("Pending");

        int orderId = orderDAO.createOrder(order);
        assertTrue(orderId > 0, "Order should be inserted in DB");

        // Transition: Pending -> Confirmed
        boolean transition1 = orderService.updateOrderStatus(orderId, "Confirmed", 100);
        assertTrue(transition1, "Pending -> Confirmed should be a valid transition");

        // Transition: Confirmed -> Processing
        boolean transition2 = orderService.updateOrderStatus(orderId, "Processing", 100);
        assertTrue(transition2, "Confirmed -> Processing should be a valid transition");

        // Transition: Processing -> Packing
        boolean transition3 = orderService.updateOrderStatus(orderId, "Packing", 100);
        assertTrue(transition3, "Processing -> Packing should be a valid transition");

        // Transition: Packing -> Shipped
        boolean transition4 = orderService.updateOrderStatus(orderId, "Shipped", 100);
        assertTrue(transition4, "Packing -> Shipped should be a valid transition");

        // Transition: Shipped -> Out for Delivery
        boolean transition5 = orderService.updateOrderStatus(orderId, "Out for Delivery", 100);
        assertTrue(transition5, "Shipped -> Out for Delivery should be a valid transition");

        // Transition: Out for Delivery -> Delivered
        boolean transition6 = orderService.updateOrderStatus(orderId, "Delivered", 100);
        assertTrue(transition6, "Out for Delivery -> Delivered should be a valid transition");

        // Fetch final order status from DB
        Order updatedOrder = orderDAO.getOrderById(orderId);
        assertEquals("Delivered", updatedOrder.getStatus(), "Order status should finish as Delivered");
    }

    @Test
    public void testInvalidTransitionsAreGracefullyRejected() {
        Order order = new Order();
        order.setUserId(999);
        order.setTotalAmount(50.00);
        order.setStatus("Pending");

        int orderId = orderDAO.createOrder(order);
        assertTrue(orderId > 0);

        // Attempt invalid state jump: Pending -> Shipped (should fail state validation checks!)
        boolean invalidJump = orderService.updateOrderStatus(orderId, "Shipped", 100);
        assertFalse(invalidJump, "Direct jump from Pending to Shipped should be rejected by the state machine");

        // Assert status remains unchanged
        Order currentOrder = orderDAO.getOrderById(orderId);
        assertEquals("Pending", currentOrder.getStatus(), "Status should remain Pending");
    }
}
