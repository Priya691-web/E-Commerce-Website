package com.fashionstore.controller;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.OrderItemDAO;
import com.fashionstore.daoimpl.OrderDAOImpl;
import com.fashionstore.daoimpl.OrderItemDAOImpl;
import com.fashionstore.model.Order;
import com.fashionstore.util.SecurityUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/legacy-admin/orders")
public class AdminOrderController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;

    @Override
    public void init() {
        orderDAO = new OrderDAOImpl();
        orderItemDAO = new OrderItemDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!SecurityUtil.requireAdmin(request, response)) {
            return;
        }

        List<Order> orders = orderDAO.getAllOrders();
        // Use batch loading to avoid N+1 query problem
        orderItemDAO.batchLoadOrderItems(orders);

        request.setAttribute("orders", orders);
        request.getRequestDispatcher("/WEB-INF/views/admin-orders.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!SecurityUtil.requireAdmin(request, response)) {
            return;
        }

        try {
            Integer orderId = parseIntOrNull(request.getParameter("orderId"));
            HttpSession session = request.getSession(true);
            if (orderId == null || orderId <= 0) {
                session.setAttribute("error", "Invalid order id.");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }

            // Handle shipment simulation
            if (request.getParameter("simulateDelivery") != null) {
                boolean updated = orderDAO.updateOrderStatus(orderId, "Delivered");
                if (updated) {
                    session.setAttribute("message", "Order marked as delivered successfully.");
                } else {
                    session.setAttribute("error", "Failed to simulate delivery.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }

            // Handle refund simulation
            if (request.getParameter("simulateRefund") != null) {
                boolean updated = orderDAO.updateOrderStatus(orderId, "Refunded");
                if (updated) {
                    session.setAttribute("message", "Refund processed and order marked as refunded successfully.");
                } else {
                    session.setAttribute("error", "Failed to process refund.");
                }
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }

            // Handle regular status update
            String status = request.getParameter("status");
            if (!isValidStatus(status)) {
                session.setAttribute("error", "Invalid order status.");
                response.sendRedirect(request.getContextPath() + "/admin/orders");
                return;
            }

            boolean updated = orderDAO.updateOrderStatus(orderId, status);
            if (updated) {
                session.setAttribute("message", "Order status updated successfully.");
            } else {
                session.setAttribute("error", "Failed to update order status.");
            }
        } catch (Exception e) {
            request.getSession(true).setAttribute("error", "Invalid order update request.");
        }

        response.sendRedirect(request.getContextPath() + "/admin/orders");
    }

    private boolean isValidStatus(String status) {
        if (status == null) return false;
        return "Pending".equalsIgnoreCase(status)
                || "Confirmed".equalsIgnoreCase(status)
                || "Processing".equalsIgnoreCase(status)
                || "Packing".equalsIgnoreCase(status)
                || "Shipped".equalsIgnoreCase(status)
                || "Out for Delivery".equalsIgnoreCase(status)
                || "Delivered".equalsIgnoreCase(status)
                || "Cancelled".equalsIgnoreCase(status)
                || "Refunded".equalsIgnoreCase(status);
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
