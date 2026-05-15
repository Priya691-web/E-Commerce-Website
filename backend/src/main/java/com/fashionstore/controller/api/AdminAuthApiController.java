package com.fashionstore.controller.api;

import com.fashionstore.controller.ApiResponse;
import com.fashionstore.model.User;
import com.fashionstore.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

/**
 * Modular API controller for admin authentication.
 * Refactored to leverage AdminApiBaseController and BaseController.
 */
@WebServlet(urlPatterns = {
    "/api/admin/auth/*",
    "/api/admin/login",
    "/api/admin/logout",
    "/api/admin/me",
    "/api/admin/register"
})
public class AdminAuthApiController extends AdminApiBaseController {

    private static final long serialVersionUID = 1L;

    private UserService userService;

    @Override
    public void init() {
        super.init();
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        
        try {
            String servletPath = request.getServletPath();
            String pathInfo = request.getPathInfo();
            
            // GET /api/admin/auth/me or GET /api/admin/me
            if ((servletPath != null && servletPath.contains("/me")) || (pathInfo != null && pathInfo.equals("/me"))) {
                User user = com.fashionstore.util.SecurityUtil.getCurrentUser(request);
                if (user == null || !user.isAdmin()) {
                    writeApiResponse(response, 401, ApiResponse.error("Not authenticated"));
                    return;
                }
                writeApiResponse(response, 200, ApiResponse.success("Authenticated", publicUser(user)));
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        
        try {
            String servletPath = request.getServletPath();
            String pathInfo = request.getPathInfo();
            
            // POST /api/admin/auth/login or POST /api/admin/login
            if ((servletPath != null && servletPath.contains("/login")) || (pathInfo != null && pathInfo.equals("/login"))) {
                loginEndpoint(request, response);
                return;
            }
            
            // POST /api/admin/auth/logout or POST /api/admin/logout
            if ((servletPath != null && servletPath.contains("/logout")) || (pathInfo != null && pathInfo.equals("/logout"))) {
                logoutEndpoint(request, response);
                return;
            }
            
            // POST /api/admin/auth/register or POST /api/admin/register
            if ((servletPath != null && servletPath.contains("/register")) || (pathInfo != null && pathInfo.equals("/register"))) {
                registerEndpoint(request, response);
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private void loginEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> body = readJsonBody(request);
        if (!validateParams(response, body, "email", "password")) return;

        String email = strParam(body, "email");
        String password = strParam(body, "password");

        User user = userService.loginUser(email, password);
        if (user == null) {
            writeApiResponse(response, 401, ApiResponse.error("Invalid credentials"));
            return;
        }
        if (!user.isAdmin()) {
            writeApiResponse(response, 403, ApiResponse.error("Admin access required"));
            return;
        }

        HttpSession session = request.getSession(true);
        // Use separate session key for admin to prevent collision with customer auth
        session.setAttribute("adminAuth", user);
        session.setAttribute("adminId", user.getUserId());
        writeApiResponse(response, 200, ApiResponse.success("Login successful", publicUser(user)));
    }

    private void logoutEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // Only clear admin session attributes, preserve customer session if exists
            session.removeAttribute("adminAuth");
            session.removeAttribute("adminId");
        }
        writeApiResponse(response, 200, ApiResponse.success("Logout successful", null));
    }

    private void registerEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> body = readJsonBody(request);
        if (!validateParams(response, body, "fullName", "email", "phone", "password", "confirmPassword", "adminKey")) return;

        String fullName = strParam(body, "fullName");
        String email = strParam(body, "email");
        String phone = strParam(body, "phone");
        String password = strParam(body, "password");
        String confirmPassword = strParam(body, "confirmPassword");
        String adminKey = strParam(body, "adminKey");

        if (!password.equals(confirmPassword)) {
            writeApiResponse(response, 400, ApiResponse.error("Passwords do not match"));
            return;
        }

        if (password.length() < 8) {
            writeApiResponse(response, 400, ApiResponse.error("Password must be at least 8 characters"));
            return;
        }

        // Validate admin secret key
        String expectedKey = System.getenv("FASHIONSTORE_ADMIN_KEY");
        if (expectedKey == null || expectedKey.isBlank()) {
            expectedKey = "FS_ADMIN_SECRET_2026";
        }

        if (!expectedKey.equals(adminKey)) {
            writeApiResponse(response, 403, ApiResponse.error("Invalid admin secret key"));
            return;
        }

        // Check if email already exists
        if (userService.isEmailExists(email)) {
            writeApiResponse(response, 409, ApiResponse.error("Email already registered"));
            return;
        }

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
            writeApiResponse(response, 201, ApiResponse.success("Admin account created successfully", null));
        } else {
            writeApiResponse(response, 500, ApiResponse.error("Failed to create admin account"));
        }
    }
}
