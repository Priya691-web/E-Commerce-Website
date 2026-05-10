package com.fashionstore.controller;

import com.fashionstore.dao.OrderDAO;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.UserDAO;
import com.fashionstore.daoimpl.OrderDAOImpl;
import com.fashionstore.daoimpl.ProductDAOImpl;
import com.fashionstore.daoimpl.UserDAOImpl;
import com.fashionstore.model.Order;
import com.fashionstore.model.Product;
import com.fashionstore.model.User;
import com.fashionstore.service.UserService;
import com.fashionstore.util.JsonUtil;
import com.fashionstore.util.SecurityUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JSON API for the React + Vite admin dashboard.
 * Mounted at /api/admin/*  (servlet runs alongside the existing JSP admin pages).
 *
 * Session-based auth: same JSESSIONID is used. The Vite dev server proxies /api -> :8080
 * so the cookie is shared in development. In production both apps are served from the
 * same origin behind a reverse proxy.
 */
@WebServlet("/api/admin/*")
public class AdminApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminApiController.class);

    private UserService userService;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        userService = new UserService();
        orderDAO = new OrderDAOImpl();
        productDAO = new ProductDAOImpl();
        userDAO = new UserDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        String path = path(request);

        try {
            switch (path) {
                case "/me" -> meEndpoint(request, response);
                case "/dashboard" -> dashboardEndpoint(request, response);
                case "/orders" -> ordersEndpoint(request, response);
                case "/products" -> productsEndpoint(request, response);
                case "/users" -> usersEndpoint(request, response);
                default -> writeJson(response, 404, Map.of("success", false, "message", "Not found"));
            }
        } catch (Exception e) {
            logger.error("Admin API error on {}: {}", path, e.getMessage(), e);
            writeJson(response, 500, Map.of("success", false, "message", "Internal server error"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        String path = path(request);

        try {
            if (!isTrustedStateChangingRequest(request)) {
                writeJson(response, 403, Map.of("success", false, "message", "Blocked by origin policy"));
                return;
            }
            switch (path) {
                case "/login" -> loginEndpoint(request, response);
                case "/logout" -> logoutEndpoint(request, response);
                default -> writeJson(response, 404, Map.of("success", false, "message", "Not found"));
            }
        } catch (Exception e) {
            logger.error("Admin API error on {}: {}", path, e.getMessage(), e);
            writeJson(response, 500, Map.of("success", false, "message", "Internal server error"));
        }
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    // ============================================================
    // Endpoints
    // ============================================================

    private void loginEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> body = readJsonBody(request);
        String email = strParam(body, "email");
        String password = strParam(body, "password");

        if (email.isBlank() || password.isBlank()) {
            writeJson(response, 400, Map.of("success", false, "message", "Email and password required"));
            return;
        }

        User user = userService.loginUser(email, password);
        if (user == null) {
            writeJson(response, 401, Map.of("success", false, "message", "Invalid credentials"));
            return;
        }
        if (!user.isAdmin()) {
            writeJson(response, 403, Map.of("success", false, "message", "Admin access required"));
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        writeJson(response, 200, Map.of(
                "success", true,
                "user", publicUser(user)
        ));
    }

    private void logoutEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        writeJson(response, 200, Map.of("success", true));
    }

    private void meEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = SecurityUtil.getCurrentUser(request);
        if (user == null || !user.isAdmin()) {
            writeJson(response, 401, Map.of("success", false, "message", "Not authenticated"));
            return;
        }
        writeJson(response, 200, Map.of("success", true, "user", publicUser(user)));
    }

    private void dashboardEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ensureAdmin(request, response)) return;

        double totalRevenue = orderDAO.getTotalRevenue();
        int totalUsers = userDAO.getTotalUserCount();
        int totalOrders = orderDAO.getTotalOrderCount();
        int lowStockCount = productDAO.getLowStockProductCount(10);
        List<Order> recentOrders = orderDAO.getRecentOrders(10);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("success", true);
        data.put("stats", Map.of(
                "totalRevenue", totalRevenue,
                "totalUsers", totalUsers,
                "totalOrders", totalOrders,
                "lowStockCount", lowStockCount
        ));
        data.put("recentOrders", recentOrders);
        writeJson(response, 200, data);
    }

    private void ordersEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ensureAdmin(request, response)) return;
        int limit = parseInt(request.getParameter("limit"), 50);
        List<Order> orders = orderDAO.getRecentOrders(limit);
        writeJson(response, 200, Map.of("success", true, "orders", orders, "count", orders.size()));
    }

    private void productsEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ensureAdmin(request, response)) return;
        List<Product> products = productDAO.getAllProducts();
        writeJson(response, 200, Map.of("success", true, "products", products, "count", products.size()));
    }

    private void usersEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!ensureAdmin(request, response)) return;
        List<User> users = userDAO.getAllUsers();
        writeJson(response, 200, Map.of("success", true, "users", users.stream().map(this::publicUser).toList(), "count", users.size()));
    }

    // ============================================================
    // Helpers
    // ============================================================

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

    private Map<String, Object> publicUser(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("userId", u.getUserId());
        m.put("fullName", u.getFullName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole());
        m.put("phone", u.getPhone());
        return m;
    }

    private String path(HttpServletRequest request) {
        String p = request.getPathInfo();
        return (p == null) ? "/" : p;
    }

    private void applyCors(HttpServletRequest request, HttpServletResponse response) {
        // Permit the Vite dev server only. In production both apps share the same origin
        // so this header is harmless.
        String origin = request.getHeader("Origin");
        if (origin != null && (origin.startsWith("http://localhost:5173")
                || origin.startsWith("http://127.0.0.1:5173"))) {
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
            // Form-encoded fallback: support "key=value&key=value" too
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
            return origin.equals(local)
                    || origin.startsWith("http://localhost:5173")
                    || origin.startsWith("http://127.0.0.1:5173");
        }

        if (referer != null && !referer.isBlank()) {
            return referer.startsWith(local)
                    || referer.startsWith("http://localhost:5173")
                    || referer.startsWith("http://127.0.0.1:5173");
        }

        return false;
    }
}
