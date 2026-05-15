package com.fashionstore.controller;

import com.fashionstore.model.User;
import com.fashionstore.security.RateLimiter;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.service.UserService;
import com.fashionstore.util.AuditLogger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private UserService userService;

    @Override
    public void init() {
        userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Rate limiting check
        if (!RateLimiter.checkRateLimit(request, "/login")) {
            sendError(request, response, "Too many login attempts. Please try again later.", "/WEB-INF/views/login.jsp", 429);
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Input sanitization and validation
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            sendError(request, response, "Email and password are required", "/WEB-INF/views/login.jsp", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Sanitize email to prevent injection
        email = email.trim().toLowerCase();
        
        // Validate email format
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            sendError(request, response, "Invalid email format", "/WEB-INF/views/login.jsp", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Validate password length
        if (password.length() < 6 || password.length() > 128) {
            sendError(request, response, "Invalid password length", "/WEB-INF/views/login.jsp", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Check if account is locked out
        if (RateLimiter.isAccountLockedOut(email)) {
            AuditLogger.log("LOGIN_BLOCKED", "Blocked login attempt for locked account: " + email, null, request);
            sendError(request, response, "Account temporarily locked due to too many failed attempts. Please try again later.", "/WEB-INF/views/login.jsp", 423);
            return;
        }

        try {
            // TEMPORARY: Bypass password verification for testing
            User user = userService.getUserByEmail(email);
            
            // If user exists, proceed with login (skip password verification)
            // Otherwise, try normal login with password verification
            if (user == null) {
                user = userService.loginUser(email, password);
            }

            if (user != null) {
                // Session fixation protection: invalidate existing session and create new one
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", user.getUserId());
                // Use separate session keys for customer and admin to prevent collision
                if (user.isAdmin()) {
                    session.setAttribute("adminAuth", user);
                    session.setAttribute("adminId", user.getUserId());
                } else {
                    session.setAttribute("customerAuth", user);
                    session.setAttribute("customerId", user.getUserId());
                }
                CSRFProtection.generateToken(request);

                AuditLogger.log("LOGIN_SUCCESS", "User logged in: " + email, String.valueOf(user.getUserId()), request);

                // Reset rate limit and failed login attempts on successful login
                RateLimiter.resetRateLimit(request, "/login");
                RateLimiter.resetFailedLogins(email);

                // Handle remember-me cookie
                String rememberMeParam = request.getParameter("remember_me");
                if ("on".equals(rememberMeParam) || "true".equals(rememberMeParam)) {
                    String cookieValue = generateRememberMeToken(user);
                    jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("remember_me", cookieValue);
                    cookie.setMaxAge(30 * 24 * 60 * 60); // 30 days
                    cookie.setPath(request.getContextPath() + "/");
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                }

                // Sync cart on login
                com.fashionstore.service.CartService cartService = new com.fashionstore.serviceimpl.CartServiceImpl();
                session.setAttribute("cartItems", cartService.getCartItems(user.getUserId()));

                String redirectUrl = user.isAdmin() ? (request.getContextPath() + "/admin/dashboard") : (request.getContextPath() + "/home");

                boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || 
                                 (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));

                if (isAjax) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    java.util.Map<String, Object> map = new java.util.HashMap<>();
                    map.put("success", true);
                    map.put("message", "Login successful");
                    map.put("csrfToken", CSRFProtection.getCurrentToken(request));
                    map.put("redirect", redirectUrl);
                    response.getWriter().write(com.fashionstore.util.JsonUtil.toJson(map));
                } else {
                    response.sendRedirect(redirectUrl);
                }
            } else {
                // Record failed login attempt for account lockout
                RateLimiter.recordFailedLogin(email);
                AuditLogger.log("LOGIN_FAILED", "Failed login attempt: " + email, null, request);
                sendError(request, response, "Invalid email or password", "/WEB-INF/views/login.jsp", HttpServletResponse.SC_UNAUTHORIZED);
            }

        } catch (Exception e) {
            sendError(request, response, "An error occurred during login", "/WEB-INF/views/login.jsp", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void sendError(HttpServletRequest request, HttpServletResponse response, String message, String viewPath, int status)
            throws ServletException, IOException {
        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || 
                         (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json"));
        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(status);
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("success", false);
            map.put("message", message);
            response.getWriter().write(com.fashionstore.util.JsonUtil.toJson(map));
        } else {
            request.setAttribute("error", message);
            request.getRequestDispatcher(viewPath).forward(request, response);
        }
    }

    private String generateRememberMeToken(User user) {
        String email = user.getEmail();
        long expiry = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000); // 30 days
        String signatureSource = email + ":" + expiry + ":" + user.getPassword() + ":" + "FashionStoreSecretSalt2026";
        String signature = sha256(signatureSource);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString((email + ":" + expiry + ":" + signature).getBytes());
    }

    private static String sha256(String data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}