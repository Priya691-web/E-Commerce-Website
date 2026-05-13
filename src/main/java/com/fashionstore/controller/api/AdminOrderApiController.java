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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * Modular API controller for order management in admin dashboard
 * Handles: GET /api/admin/orders, GET /api/admin/orders/{id}, PUT /api/admin/orders/{id}/status
 */
@WebServlet("/api/admin/orders/*")
public class AdminOrderApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderApiController.class);

    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        orderDAO = new OrderDAOImpl();
        orderItemDAO = new OrderItemDAOImpl();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminOrderApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminOrderApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/admin/orders - List all orders
            int limit = parseInt(request.getParameter("limit"), 50);
            List<Order> orders = orderDAO.getRecentOrders(limit);
            orderItemDAO.batchLoadOrderItems(orders);
            writeJson(response, 200, Map.of("success", true, "orders", orders.stream().map(this::publicOrder).toList(), "count", orders.size()));
            return;
        }
        
        // GET /api/admin/orders/recent - Get recent orders
        if (pathInfo.equals("/recent")) {
            int limit = parseInt(request.getParameter("limit"), 10);
            List<Order> orders = orderDAO.getRecentOrders(limit);
            orderItemDAO.batchLoadOrderItems(orders);
            writeJson(response, 200, Map.of("success", true, "orders", orders.stream().map(this::publicOrder).toList()));
            return;
        }
        
        // GET /api/admin/orders/{id} - Get single order
        String[] segments = pathInfo.split("/");
        if (segments.length == 2) {
            try {
                int orderId = Integer.parseInt(segments[1]);
                Order order = orderDAO.getOrderById(orderId);
                if (order == null) {
                    writeJson(response, 404, Map.of("success", false, "message", "Order not found"));
                    return;
                }
                order.setItems(orderItemDAO.getItemsByOrderId(order.getOrderId()));
                writeJson(response, 200, Map.of("success", true, "order", publicOrder(order)));
            } catch (NumberFormatException e) {
                writeJson(response, 400, Map.of("success", false, "message", "Invalid order ID"));
            }
            return;
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeJson(response, 403, Map.of("success", false, "message", "Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String[] segments = pathInfo.split("/");
            if (segments.length >= 3) {
                try {
                    int orderId = Integer.parseInt(segments[1]);
                    String action = segments[2];
                    
                    if (orderDAO.getOrderById(orderId) == null) {
                        writeJson(response, 404, Map.of("success", false, "message", "Order not found"));
                        return;
                    }
                    
                    boolean success = false;
                    switch (action) {
                        case "status" -> {
                            Map<String, Object> body = readJsonBody(request);
                            String status = strParam(body, "status");
                            if (status.isBlank()) {
                                writeJson(response, 400, Map.of("success", false, "message", "Status required"));
                                return;
                            }
                            success = orderDAO.updateOrderStatus(orderId, status);
                            writeJson(response, success ? 200 : 400, Map.of("success", success, "message", success ? "Updated" : "Failed"));
                        }
                        case "approve" -> {
                            success = orderDAO.updateOrderStatus(orderId, "Processing");
                            writeJson(response, success ? 200 : 400, Map.of("success", success));
                        }
                        case "cancel", "refund" -> {
                            success = orderDAO.updateOrderStatus(orderId, "Cancelled");
                            writeJson(response, success ? 200 : 400, Map.of("success", success));
                        }
                        case "ship" -> {
                            success = orderDAO.updateOrderStatus(orderId, "Shipped");
                            writeJson(response, success ? 200 : 400, Map.of("success", success));
                        }
                        case "deliver" -> {
                            success = orderDAO.updateOrderStatus(orderId, "Delivered");
                            writeJson(response, success ? 200 : 400, Map.of("success", success));
                        }
                        default -> {
                            writeJson(response, 404, Map.of("success", false, "message", "Not found"));
                        }
                    }
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid order ID"));
                }
                return;
            }
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

    private Map<String, Object> readJsonBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = request.getReader()) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
        }
        String body = sb.toString().trim();
        if (body.isEmpty()) return new HashMap<>();
        try {
            Map<String, Object> parsed = JsonUtil.gson().fromJson(body,
                    new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType());
            return parsed != null ? parsed : new HashMap<>();
        } catch (Exception e) {
            Map<String, Object> form = new HashMap<>();
            for (String pair : body.split("&")) {
                int idx = pair.indexOf('=');
                if (idx > 0) {
                    form.put(java.net.URLDecoder.decode(pair.substring(0, idx), java.nio.charset.StandardCharsets.UTF_8),
                             java.net.URLDecoder.decode(pair.substring(idx + 1), java.nio.charset.StandardCharsets.UTF_8));
                }
            }
            return form;
        }
    }

    private String strParam(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return v == null ? "" : String.valueOf(v).trim();
    }

    private int parseInt(String s, int defaultVal) {
        if (s == null || s.isBlank()) return defaultVal;
        try { return Integer.parseInt(s.trim()); } catch (NumberFormatException e) { return defaultVal; }
    }

    private boolean isTrustedStateChangingRequest(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        String referer = request.getHeader("Referer");
        String local = request.getScheme() + "://" + request.getServerName()
                + ((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort());

        if (origin != null && !origin.isBlank()) {
            return origin.equals(local) || allowedOrigins.contains(origin);
        }
        if (referer != null && !referer.isBlank()) {
            return referer.startsWith(local) || allowedOrigins.stream().anyMatch(referer::startsWith);
        }
        return false;
    }
}
