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
 * Modular API controller for user management in admin dashboard
 * Handles: GET /api/admin/users, GET /api/admin/users/{id}, PUT /api/admin/users/{id}, DELETE /api/admin/users/{id}
 */
@WebServlet("/api/admin/users/*")
public class AdminUserApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserApiController.class);

    private UserDAO userDAO;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        userDAO = new UserDAOImpl();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminUserApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminUserApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            // GET /api/admin/users - List all users
            List<User> users = userDAO.getAllUsers();
            writeJson(response, 200, Map.of("success", true, "users", users.stream().map(this::publicUser).toList(), "count", users.size()));
            return;
        }
        
        // GET /api/admin/users/recent - Get recent users
        if (pathInfo.equals("/recent")) {
            int limit = parseInt(request.getParameter("limit"), 10);
            List<User> users = userDAO.getAllUsers();
            writeJson(response, 200, Map.of("success", true, "users", users.stream().limit(limit).map(this::publicUser).toList()));
            return;
        }
        
        // GET /api/admin/users/{id} - Get single user
        String[] segments = pathInfo.split("/");
        if (segments.length == 2) {
            try {
                int userId = Integer.parseInt(segments[1]);
                User user = userDAO.getUserById(userId);
                if (user == null) {
                    writeJson(response, 404, Map.of("success", false, "message", "User not found"));
                    return;
                }
                writeJson(response, 200, Map.of("success", true, "user", publicUser(user)));
            } catch (NumberFormatException e) {
                writeJson(response, 400, Map.of("success", false, "message", "Invalid user ID"));
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
            if (segments.length == 2) {
                try {
                    int userId = Integer.parseInt(segments[1]);
                    User existing = userDAO.getUserById(userId);
                    if (existing == null) {
                        writeJson(response, 404, Map.of("success", false, "message", "User not found"));
                        return;
                    }
                    
                    Map<String, Object> body = readJsonBody(request);
                    Object roleObj = body.get("role");
                    if (roleObj != null) {
                        boolean success = userDAO.updateUserRole(userId, String.valueOf(roleObj));
                        writeJson(response, success ? 200 : 400, Map.of("success", success));
                        return;
                    }
                    
                    Object blockedObj = body.get("blocked");
                    if (blockedObj != null) {
                        boolean blocked = Boolean.parseBoolean(String.valueOf(blockedObj));
                        boolean success = userDAO.updateUserRole(userId, blocked ? "disabled" : "user");
                        writeJson(response, success ? 200 : 400, Map.of("success", success));
                        return;
                    }
                    
                    writeJson(response, 400, Map.of("success", false, "message", "No valid update field"));
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid user ID"));
                }
                return;
            }
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        if (!isTrustedStateChangingRequest(request)) {
            writeJson(response, 403, Map.of("success", false, "message", "Blocked by origin policy"));
            return;
        }
        if (!ensureAdmin(request, response)) return;
        
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String[] segments = pathInfo.split("/");
            if (segments.length == 2) {
                try {
                    int userId = Integer.parseInt(segments[1]);
                    // Soft-delete by disabling
                    boolean success = userDAO.updateUserRole(userId, "disabled");
                    writeJson(response, success ? 200 : 400, Map.of("success", success));
                } catch (NumberFormatException e) {
                    writeJson(response, 400, Map.of("success", false, "message", "Invalid user ID"));
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

    private Map<String, Object> publicUser(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getUserId());
        m.put("fullName", u.getFullName());
        m.put("email", u.getEmail());
        m.put("role", u.getRole());
        m.put("phone", u.getPhone());
        m.put("blocked", "disabled".equalsIgnoreCase(u.getRole()));
        m.put("orderCount", 0); // computed lazily if needed
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
