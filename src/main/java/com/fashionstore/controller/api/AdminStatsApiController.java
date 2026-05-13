package com.fashionstore.controller.api;

import com.fashionstore.dao.*;
import com.fashionstore.daoimpl.*;
import com.fashionstore.model.*;
import com.fashionstore.util.JsonUtil;
import com.fashionstore.util.SecurityUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Modular API controller for dashboard stats and analytics in admin dashboard
 * Handles: GET /api/admin/dashboard, GET /api/admin/stats
 */
@WebServlet("/api/admin/dashboard/*")
public class AdminStatsApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminStatsApiController.class);

    private OrderDAO orderDAO;
    private UserDAO userDAO;
    private ProductDAO productDAO;
    private OrderItemDAO orderItemDAO;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        orderDAO = new OrderDAOImpl();
        userDAO = new UserDAOImpl();
        productDAO = new ProductDAOImpl();
        orderItemDAO = new OrderItemDAOImpl();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminStatsApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminStatsApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/admin/dashboard - Dashboard with stats and recent orders
            double totalRevenue = orderDAO.getTotalRevenue();
            int totalUsers = userDAO.getTotalUserCount();
            int totalOrders = orderDAO.getTotalOrderCount();
            int lowStockCount = productDAO.getLowStockProductCount(10);
            List<Order> recentOrders = orderDAO.getRecentOrders(10);
            writeJson(response, 200, Map.of(
                    "success", true,
                    "stats", Map.of("totalRevenue", totalRevenue, "totalUsers", totalUsers, "totalOrders", totalOrders, "lowStockCount", lowStockCount),
                    "recentOrders", recentOrders.stream().map(this::publicOrder).toList()
            ));
            return;
        }
        
        // GET /api/admin/dashboard/stats - Stats only
        if (pathInfo.equals("/stats")) {
            double totalRevenue = orderDAO.getTotalRevenue();
            int totalUsers = userDAO.getTotalUserCount();
            int totalOrders = orderDAO.getTotalOrderCount();
            int lowStockCount = productDAO.getLowStockProductCount(10);
            writeJson(response, 200, Map.of(
                    "success", true,
                    "revenue", totalRevenue,
                    "orders", totalOrders,
                    "products", productDAO.getAllProducts().size(),
                    "customers", totalUsers,
                    "pending", orderDAO.getRecentOrders(1000).stream().filter(o -> "Pending".equalsIgnoreCase(o.getStatus())).count(),
                    "lowStock", lowStockCount
            ));
            return;
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // Helper methods
    private boolean ensureAdmin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = SecurityUtil.getCurrentUser(request);
        if (user == null) {
            writeJson(response, 401, Map.of("success", false, "message", "Authentication required"));
            return false;
        }
        if (!user.isAdmin()) {
            writeJson(response, 403, Map.of("success", false, "message", "Admin access required"));
            return false;
        }
        return true;
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
        List<Map<String, Object>> items = new ArrayList<>();
        if (o.getItems() != null) {
            for (OrderItem item : o.getItems()) {
                Map<String, Object> im = new LinkedHashMap<>();
                im.put("productId", item.getProductId());
                im.put("quantity", item.getQuantity());
                im.put("price", item.getPrice());
                im.put("sizeLabel", item.getSizeLabel());
                items.add(im);
            }
        }
        m.put("items", items);
        return m;
    }

    private void applyCors(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Vary", "Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,X-CSRF-Token");
            response.setHeader("Access-Control-Max-Age", "3600");
        }
    }

    private void writeJson(HttpServletResponse response, int status, Object data) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.toJson(data));
    }
}
