package com.fashionstore.controller.api;

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
import java.util.*;

/**
 * Modular API controller for admin authentication
 * Handles: POST /api/admin/login, POST /api/admin/logout, GET /api/admin/me, POST /api/admin/register
 */
@WebServlet("/api/admin/auth/*")
public class AdminAuthApiController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthApiController.class);

    private UserService userService;
    private Set<String> allowedOrigins;

    @Override
    public void init() {
        userService = new UserService();
        
        // Initialize allowed origins from environment variable
        String allowedOriginsEnv = System.getenv("CORS_ALLOWED_ORIGINS");
        if (allowedOriginsEnv != null && !allowedOriginsEnv.isBlank()) {
            allowedOrigins = new HashSet<>(Arrays.asList(allowedOriginsEnv.split(",")));
            logger.info("AdminAuthApiController initialized with allowed origins from env: {}", allowedOrigins);
        } else {
            // Fallback to localhost for local development only
            allowedOrigins = new HashSet<>(Arrays.asList(
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:3000",
                "http://127.0.0.1:3000"
            ));
            logger.info("AdminAuthApiController initialized with default localhost origins for development");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        
        String pathInfo = request.getPathInfo();
        
        // GET /api/admin/auth/me - Get current admin user
        if (pathInfo != null && pathInfo.equals("/me")) {
            User user = SecurityUtil.getCurrentUser(request);
            if (user == null || !user.isAdmin()) {
                writeJson(response, 401, Map.of("success", false, "message", "Not authenticated"));
                return;
            }
            writeJson(response, 200, Map.of("success", true, "user", publicUser(user)));
            return;
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        
        String pathInfo = request.getPathInfo();
        
        // POST /api/admin/auth/login - Admin login
        if (pathInfo != null && pathInfo.equals("/login")) {
            loginEndpoint(request, response);
            return;
        }
        
        // POST /api/admin/auth/logout - Admin logout
        if (pathInfo != null && pathInfo.equals("/logout")) {
            logoutEndpoint(request, response);
            return;
        }
        
        // POST /api/admin/auth/register - Admin registration
        if (pathInfo != null && pathInfo.equals("/register")) {
            registerEndpoint(request, response);
            return;
        }
        
        writeJson(response, 404, Map.of("success", false, "message", "Not found"));
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

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
        writeJson(response, 200, Map.of("success", true, "user", publicUser(user)));
    }

    private void logoutEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        writeJson(response, 200, Map.of("success", true));
    }

    private void registerEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> body = readJsonBody(request);
        String fullName = strParam(body, "fullName");
        String email = strParam(body, "email");
        String phone = strParam(body, "phone");
        String password = strParam(body, "password");
        String confirmPassword = strParam(body, "confirmPassword");
        String adminKey = strParam(body, "adminKey");

        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            writeJson(response, 400, Map.of("success", false, "message", "All fields are required"));
            return;
        }

        if (!password.equals(confirmPassword)) {
            writeJson(response, 400, Map.of("success", false, "message", "Passwords do not match"));
            return;
        }

        if (password.length() < 8) {
            writeJson(response, 400, Map.of("success", false, "message", "Password must be at least 8 characters"));
            return;
        }

        // Validate admin secret key
        String expectedKey = System.getenv("FASHIONSTORE_ADMIN_KEY");
        if (expectedKey == null || expectedKey.isBlank()) {
            expectedKey = "FS_ADMIN_SECRET_2026";
        }

        if (!expectedKey.equals(adminKey)) {
            writeJson(response, 403, Map.of("success", false, "message", "Invalid admin secret key"));
            return;
        }

        // Check if email already exists
        if (userService.isEmailExists(email)) {
            writeJson(response, 409, Map.of("success", false, "message", "Email already registered"));
            return;
        }

        try {
            User user = new User();
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setPassword(password);
            user.setGender("other");
            user.setAddress("");
            user.setRole("admin");

            int userId = userService.registerUser(user);
            if (userId > 0) {
                writeJson(response, 201, Map.of("success", true, "message", "Admin account created successfully"));
            } else {
                writeJson(response, 500, Map.of("success", false, "message", "Failed to create admin account"));
            }
        } catch (Exception e) {
            logger.error("Error creating admin account: {}", e.getMessage(), e);
            writeJson(response, 500, Map.of("success", false, "message", "An error occurred"));
        }
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

    private String strParam(Map<String, Object> body, String key) {
        Object v = body.get(key);
        return v == null ? "" : String.valueOf(v).trim();
    }
}
