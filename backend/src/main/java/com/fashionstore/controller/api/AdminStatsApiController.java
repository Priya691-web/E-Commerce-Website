package com.fashionstore.controller.api;

import com.fashionstore.controller.ApiResponse;
import com.fashionstore.dao.*;
import com.fashionstore.daoimpl.*;
import com.fashionstore.model.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

/**
 * Modular API controller for dashboard stats and analytics in admin dashboard.
 * Maps both dashboard and stats endpoints under a single standard component.
 */
@WebServlet(urlPatterns = {"/api/admin/dashboard/*", "/api/admin/stats/*"})
public class AdminStatsApiController extends AdminApiBaseController {

    private static final long serialVersionUID = 1L;

    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;

    @Override
    public void init() {
        super.init();
        orderDAO = new OrderDAOImpl();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        try {
            String servletPath = request.getServletPath();
            String pathInfo = request.getPathInfo();
            
            if (servletPath != null && servletPath.contains("/stats")) {
                // GET /api/admin/stats
                double totalRevenue = orderDAO.getTotalRevenue();
                int totalUsers = userDAO.getTotalUserCount();
                int totalOrders = orderDAO.getTotalOrderCount();
                int lowStockCount = productDAO.getLowStockProductCount(10);
                Map<String, Object> stats = Map.of(
                        "revenue", totalRevenue,
                        "orders", totalOrders,
                        "products", productDAO.getAllProducts().size(),
                        "customers", totalUsers,
                        "pending", orderDAO.getRecentOrders(1000).stream().filter(o -> "Pending".equalsIgnoreCase(o.getStatus())).count(),
                        "lowStock", lowStockCount
                );
                writeApiResponse(response, 200, ApiResponse.success("Stats retrieved successfully", stats));
                return;
            }
            
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/admin/dashboard - Dashboard with stats and recent orders
                double totalRevenue = orderDAO.getTotalRevenue();
                int totalUsers = userDAO.getTotalUserCount();
                int totalOrders = orderDAO.getTotalOrderCount();
                int lowStockCount = productDAO.getLowStockProductCount(10);
                List<Order> recentOrders = orderDAO.getRecentOrders(10);
                writeApiResponse(response, 200, ApiResponse.success("Dashboard data retrieved successfully", Map.of(
                        "stats", Map.of("totalRevenue", totalRevenue, "totalUsers", totalUsers, "totalOrders", totalOrders, "lowStockCount", lowStockCount),
                        "recentOrders", recentOrders.stream().map(this::publicOrder).toList()
                )));
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private Map<String, Object> publicOrder(Order o) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", o.getOrderId());
        m.put("userId", o.getUserId());
        m.put("customerName", o.getFullName());
        m.put("total", o.getTotalAmount());
        m.put("status", o.getStatus());
        m.put("paymentStatus", "pending");
        m.put("paymentMethod", o.getPaymentMethod());
        m.put("createdAt", o.getOrderDate() != null ? o.getOrderDate().getTime() : null);
        m.put("address", o.getAddress());
        m.put("city", o.getCity());
        m.put("state", o.getState());
        m.put("zip", o.getZip());
        m.put("phone", o.getPhone());
        return m;
    }
}
