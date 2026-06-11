package com.fashionstore.controller;

import com.fashionstore.model.User;
import com.fashionstore.security.AuthContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin Dashboard API Controller
 * Provides session check and basic admin dashboard data
 * Supports both JWT (via AuthContext) and session-based authentication
 */
@WebServlet("/api/admin/dashboard")
public class AdminDashboardController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = null;

        // Try to get user from JWT (via AuthContext)
        AuthContext authContext = AuthContext.fromRequest(request);
        if (authContext.isAuthenticated() && authContext.isAdmin()) {
            // User authenticated via JWT
            user = new User();
            String userIdStr = authContext.getUserId();
            if (userIdStr != null) {
                try {
                    user.setUserId(Integer.parseInt(userIdStr));
                } catch (NumberFormatException e) {
                    user.setUserId(0);
                }
            }
            user.setEmail(authContext.getEmail());
            user.setRole(authContext.getRole());
        } else {
            // Try to get user from session
            HttpSession session = request.getSession(false);
            if (session != null) {
                user = (User) session.getAttribute("customerAuth");
            }
        }

        // Check if user is authenticated and is admin
        if (user == null || !"admin".equalsIgnoreCase(user.getRole())) {
            response.setStatus(401);
            response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized\"}");
            return;
        }

        // Return user data
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getUserId());
        userData.put("email", user.getEmail());
        userData.put("fullName", user.getFullName());
        userData.put("role", user.getRole());
        
        data.put("user", userData);
        data.put("success", true);

        Map<String, Object> response_data = new HashMap<>();
        response_data.put("success", true);
        response_data.put("data", data);

        // Simple JSON response
        response.getWriter().write(com.fashionstore.util.JsonUtil.toJson(response_data));
    }
}
