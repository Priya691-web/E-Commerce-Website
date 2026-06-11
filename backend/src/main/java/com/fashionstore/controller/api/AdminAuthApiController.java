package com.fashionstore.controller.api;

import com.fashionstore.controller.ApiResponse;
import com.fashionstore.model.User;
import com.fashionstore.registry.ServiceRegistry;
import com.fashionstore.security.CookieSecurityUtil;
import com.fashionstore.security.JWTUtil;
import com.fashionstore.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.util.*;

/**
 * AdminAuthApiController - Admin JWT Authentication
 * 
 * HYBRID AUTHENTICATION ARCHITECTURE:
 * ===================================
 * - Customer Frontend: Session-based authentication (LoginController)
 * - Admin Frontend: JWT-based authentication (THIS CONTROLLER)
 * 
 * CRITICAL: This controller handles ADMIN LOGIN ONLY using JWT tokens.
 * DO NOT add session logic here. Sessions are for customer MVC only (/login).
 * 
 * AUTHENTICATION FLOW:
 * ====================
 * 1. Admin submits login form (email/password) to /api/admin/login (JSON)
 * 2. Validate credentials via UserService.validateAndLoginUser()
 * 3. Check user has admin role
 * 4. Generate JWT access token (15 minutes)
 * 5. Generate JWT refresh token (7 days)
 * 6. Set tokens in HTTP-only cookies
 * 7. Return JSON response with tokens
 * 8. Frontend stores tokens and includes in Authorization header
 * 9. Admin is authenticated via JWT for API requests
 * 
 * JWT TOKEN MANAGEMENT:
 * ====================
 * - Access Token: 15 minutes expiration
 * - Refresh Token: 7 days expiration
 * - Token Storage: HTTP-only cookies (secure, not accessible to JS)
 * - Token Injection: Authorization header (Bearer scheme)
 * - Token Validation: JWTAuthenticationFilter validates on each request
 * 
 * JWT SECURITY:
 * =============
 * - HTTP-only cookies: Prevents XSS token theft
 * - Secure flag: Only sent over HTTPS
 * - SameSite: CSRF protection
 * - Token signing: HMAC-SHA256 with secret key
 * - Token validation: Signature, expiration, role check
 * - Audit logging: All login attempts logged
 * 
 * IMPORTANT SEPARATION:
 * ====================
 * ✓ This controller uses ONLY JWT tokens
 * ✓ NO HttpSession creation here
 * ✓ NO session attributes set here
 * ✓ NO JSESSIONID cookie set here
 * ✓ NO CSRF token logic here
 * 
 * Customer session authentication is handled by:
 * - LoginController (/login)
 * - SecurityHardeningFilter (validates sessions)
 * - CSRFProtection (CSRF tokens for forms)
 * 
 * LOGOUT BEHAVIOR:
 * ================
 * - Admin logout: Clears JWT tokens (handled by React frontend)
 * - Customer logout: Invalidates HttpSession (handled by LogoutController)
 * - Logout does NOT affect the other authentication method
 * - Admin logout does NOT invalidate customer sessions
 * - Customer logout does NOT clear admin JWT tokens
 * 
 * ENDPOINTS:
 * ==========
 * POST /api/admin/login - Admin login with email/password
 * POST /api/admin/logout - Admin logout (clears tokens)
 * GET /api/admin/me - Get current admin user info
 * POST /api/admin/register - Register new admin (if enabled)
 * POST /api/admin/refresh - Refresh access token using refresh token
 */
@WebServlet(urlPatterns = {
    "/api/admin/login",
    "/api/admin/logout",
    "/api/admin/me",
    "/api/admin/register",
    "/api/admin/refresh",
    "/api/admin/auth/*"
})
public class AdminAuthApiController extends AdminApiBaseController {

    private static final long serialVersionUID = 1L;

    private UserService userService;

    @Override
    public void init() {
        super.init();
        ServiceRegistry registry = ServiceRegistry.getInstance();
        userService = registry.getUserService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        applyCors(request, response);
        
        try {
            String servletPath = request.getServletPath();
            String pathInfo = request.getPathInfo();
            
            // GET /api/admin/auth/me or GET /api/admin/me
            if ((servletPath != null && servletPath.contains("/me")) || (pathInfo != null && pathInfo.equals("/me"))) {
                meEndpoint(request, response);
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
        
        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        
        try {
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
            
            // POST /api/admin/refresh
            if ((servletPath != null && servletPath.contains("/refresh")) || (pathInfo != null && pathInfo.equals("/refresh"))) {
                refreshEndpoint(request, response);
                return;
            }
            
            writeApiResponse(response, 404, ApiResponse.error("Not found"));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private void meEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get AuthContext from request attribute (set by JWTAuthenticationFilter)
        com.fashionstore.security.AuthContext authContext = 
            (com.fashionstore.security.AuthContext) request.getAttribute("authContext");
        
        if (authContext == null || !authContext.isAuthenticated()) {
            writeApiResponse(response, 401, ApiResponse.error("Authentication required"));
            return;
        }
        
        // Get user ID from authenticated context
        String userIdStr = authContext.getUserId();
        if (userIdStr == null) {
            writeApiResponse(response, 401, ApiResponse.error("Invalid token: missing user ID"));
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userService.getUserById(userId);
            
            if (user == null) {
                writeApiResponse(response, 404, ApiResponse.error("User not found"));
                return;
            }
            
            writeApiResponse(response, 200, ApiResponse.success("Authenticated", publicUser(user)));
        } catch (NumberFormatException e) {
            writeApiResponse(response, 401, ApiResponse.error("Invalid token: malformed user ID"));
        }
    }

    /**
     * Admin Login Endpoint - JWT Token Generation
     * 
     * AUTHENTICATION FLOW:
     * ====================
     * 1. Receive JSON: { email, password }
     * 2. Validate required parameters
     * 3. Authenticate user via UserService.validateAndLoginUser()
     * 4. Check user has admin role
     * 5. Generate JWT access token (15 minutes)
     * 6. Generate JWT refresh token (7 days)
     * 7. Set tokens in HTTP-only cookies
     * 8. Return tokens in JSON response
     * 
     * SECURITY:
     * =========
     * - Credentials validated against password hash
     * - Admin role required (not customer)
     * - Tokens signed with HMAC-SHA256
     * - HTTP-only cookies prevent XSS theft
     * - Secure flag ensures HTTPS only
     * - Token expiration prevents long-lived tokens
     * 
     * IMPORTANT: This is JWT-only authentication
     * - NO HttpSession created
     * - NO session attributes set
     * - NO JSESSIONID cookie
     * - Customer session auth is separate (LoginController)
     */
    private void loginEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Parse request body
        Map<String, Object> body = readJsonBody(request);
        if (!validateParams(response, body, "email", "password")) {
            return;
        }

        String email = strParam(body, "email");
        String password = strParam(body, "password");

        // Validate credentials using UserService
        User user;
        try {
            user = userService.validateAndLoginUser(email, password);
        } catch (IllegalArgumentException e) {
            writeApiResponse(response, 400, ApiResponse.error(e.getMessage()));
            return;
        }

        if (user == null) {
            writeApiResponse(response, 401, ApiResponse.error("Invalid email or password"));
            return;
        }

        // Check if user has admin role
        if (!"admin".equalsIgnoreCase(user.getRole())) {
            writeApiResponse(response, 403, ApiResponse.error("Admin access required"));
            return;
        }

        // ============================================================
        // JWT TOKEN GENERATION - NO SESSION CREATION
        // ============================================================
        
        // Generate JWT access token (15 minutes)
        String accessToken = JWTUtil.generateToken(
            String.valueOf(user.getUserId()),
            user.getEmail(),
            user.getRole()
        );
        
        // Generate JWT refresh token (7 days)
        String refreshToken = JWTUtil.generateRefreshToken(
            String.valueOf(user.getUserId()),
            user.getEmail(),
            user.getRole()
        );

        // Set access token in HTTP-only cookie (Phase 1.2 & 1.3: Secure cookie configuration)
        Cookie accessCookie = CookieSecurityUtil.createAccessTokenCookie(accessToken, request);
        response.addCookie(accessCookie);

        // Set refresh token in HTTP-only cookie (Phase 1.2 & 1.3: Secure cookie configuration)
        Cookie refreshCookie = CookieSecurityUtil.createRefreshTokenCookie(refreshToken, request);
        response.addCookie(refreshCookie);

        // Return success response with tokens
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);
        responseData.put("refreshToken", refreshToken);
        responseData.put("tokenType", "Bearer");
        responseData.put("expiresIn", 15 * 60); // 15 minutes in seconds
        responseData.put("user", publicUser(user));
        
        writeApiResponse(response, 200, ApiResponse.success("Login successful", responseData));
    }

    private void logoutEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Clear auth cookies (Phase 1.2 & 1.3: Secure cookie configuration)
        Cookie accessCookie = CookieSecurityUtil.createClearCookie("access_token");
        response.addCookie(accessCookie);
        
        Cookie refreshCookie = CookieSecurityUtil.createClearCookie("refresh_token");
        response.addCookie(refreshCookie);
        
        writeApiResponse(response, 200, ApiResponse.success("Logout successful", null));
    }

    private void refreshEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Get refresh token from cookie
        com.fashionstore.security.AuthContext authContext = 
            com.fashionstore.security.AuthContext.fromRequest(request);
        
        String refreshToken = authContext.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            writeApiResponse(response, 401, ApiResponse.error("Refresh token not provided"));
            return;
        }

        // Validate refresh token and generate new access token
        JWTUtil.TokenRefreshResult refreshResult = JWTUtil.refreshToken(refreshToken);
        
        if (!refreshResult.isSuccess()) {
            writeApiResponse(response, 401, ApiResponse.error(refreshResult.getError()));
            return;
        }

        // Set new access token in cookie (Phase 1.2 & 1.3: Secure cookie configuration)
        String newAccessToken = refreshResult.getAccessToken();
        Cookie accessCookie = CookieSecurityUtil.createAccessTokenCookie(newAccessToken, request);
        response.addCookie(accessCookie);

        // Return success response with new token
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", newAccessToken);
        responseData.put("tokenType", "Bearer");
        responseData.put("expiresIn", refreshResult.getExpiresIn());
        
        writeApiResponse(response, 200, ApiResponse.success("Token refreshed successfully", responseData));
    }

    private void registerEndpoint(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, Object> body = readJsonBody(request);
        if (!validateParams(response, body, "email", "phone", "password", "confirmPassword", "adminKey")) return;

        String fullName = strParam(body, "fullName");
        if (fullName == null || fullName.isBlank()) {
            fullName = "Admin";
        }
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
