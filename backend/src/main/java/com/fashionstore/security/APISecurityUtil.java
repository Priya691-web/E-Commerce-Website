package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * API Security Utilities
 * Provides API endpoint security, CORS configuration, and API key validation
 */
public class APISecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(APISecurityUtil.class);
    
    // Allowed origins for CORS
    private static final Set<String> ALLOWED_ORIGINS = new HashSet<>(Arrays.asList(
        "http://localhost:5173",  // Vite dev server
        "http://localhost:3000",  // Alternative dev server
        "https://fashionstore.com",  // Production
        "https://www.fashionstore.com"  // Production with www
    ));
    
    // Allowed HTTP methods
    private static final Set<String> ALLOWED_METHODS = new HashSet<>(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
    ));
    
    // Allowed headers
    private static final Set<String> ALLOWED_HEADERS = new HashSet<>(Arrays.asList(
        "Content-Type", "Authorization", "X-CSRF-Token", "X-Requested-With",
        "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"
    ));
    
    // Public API endpoints (no authentication required)
    private static final Set<String> PUBLIC_ENDPOINTS = new HashSet<>(Arrays.asList(
        "/api/metrics",
        "/api/health",
        "/api/products",
        "/api/categories",
        "/api/search",
        "/login",
        "/register",
        "/home",
        "/",
        "/assets"
    ));
    
    // Admin API endpoints (admin authentication required)
    private static final Set<String> ADMIN_ENDPOINTS = new HashSet<>(Arrays.asList(
        "/api/admin/products",
        "/api/admin/orders",
        "/api/admin/users",
        "/api/admin/categories",
        "/api/admin/coupons",
        "/api/admin/dashboard",
        "/api/admin/reports"
    ));
    
    /**
     * Configure CORS headers
     */
    public static void configureCORS(HttpServletRequest request, HttpServletResponse response) {
        String origin = request.getHeader("Origin");
        
        if (origin != null && isOriginAllowed(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", String.join(", ", ALLOWED_METHODS));
            response.setHeader("Access-Control-Allow-Headers", String.join(", ", ALLOWED_HEADERS));
            response.setHeader("Access-Control-Max-Age", "3600");
        }
    }
    
    /**
     * Check if origin is allowed
     */
    public static boolean isOriginAllowed(String origin) {
        // Allow all origins in development
        if (isDevelopmentEnvironment()) {
            return true;
        }
        
        return ALLOWED_ORIGINS.contains(origin);
    }
    
    /**
     * Check if endpoint is public
     */
    public static boolean isPublicEndpoint(String path) {
        for (String endpoint : PUBLIC_ENDPOINTS) {
            if (path.startsWith(endpoint)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if endpoint is admin-only
     */
    public static boolean isAdminEndpoint(String path) {
        for (String endpoint : ADMIN_ENDPOINTS) {
            if (path.startsWith(endpoint)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Validate API key (if using API key authentication)
     */
    public static boolean validateAPIKey(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }
        
        // Validate API key against database or configuration
        String validApiKey = System.getenv("API_KEY");
        if (validApiKey == null) {
            logger.warn("API_KEY not set in environment");
            return false;
        }
        
        return apiKey.equals(validApiKey);
    }
    
    /**
     * Validate Bearer token (JWT)
     */
    public static JWTUtil.TokenValidationResult validateBearerToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new JWTUtil.TokenValidationResult();
        }
        
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        return JWTUtil.validateToken(token);
    }
    
    /**
     * Check if request is preflight (OPTIONS)
     */
    public static boolean isPreflightRequest(HttpServletRequest request) {
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }
    
    /**
     * Handle preflight request
     */
    public static void handlePreflightRequest(HttpServletRequest request, HttpServletResponse response) {
        configureCORS(request, response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
    
    /**
     * Validate API request
     */
    public static APIValidationResult validateAPIRequest(HttpServletRequest request) {
        APIValidationResult result = new APIValidationResult();
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Check HTTP method
        if (!ALLOWED_METHODS.contains(method)) {
            result.setValid(false);
            result.setError("Method not allowed: " + method);
            return result;
        }
        
        // Check if preflight request
        if (isPreflightRequest(request)) {
            result.setValid(true);
            result.setPreflight(true);
            return result;
        }
        
        // Check if public endpoint
        if (isPublicEndpoint(path)) {
            result.setValid(true);
            result.setPublic(true);
            return result;
        }
        
        // Check if admin endpoint
        if (isAdminEndpoint(path)) {
            // Validate admin authorization
            AdminAuthorizationUtil.AuthorizationResult authResult = 
                AdminAuthorizationUtil.validateAdminAccess(request);
            
            if (!authResult.isAuthorized()) {
                result.setValid(false);
                result.setError("Admin authorization required: " + authResult.getReason());
                return result;
            }
            
            result.setValid(true);
            result.setAdmin(true);
            return result;
        }
        
        // For protected endpoints, validate authentication
        JWTUtil.TokenValidationResult tokenResult = validateBearerToken(request);
        
        if (!tokenResult.isValid()) {
            result.setValid(false);
            result.setError("Authentication required: " + tokenResult.getError());
            return result;
        }
        
        result.setValid(true);
        result.setUserId(tokenResult.getUserId());
        result.setEmail(tokenResult.getEmail());
        result.setRole(tokenResult.getRole());
        
        return result;
    }
    
    /**
     * Sanitize API response
     */
    public static String sanitizeAPIResponse(String response) {
        if (response == null || response.isEmpty()) {
            return response;
        }
        
        // Remove sensitive information
        String sanitized = response
            .replaceAll("\"password\":\"[^\"]+\"", "\"password\":\"*****\"")
            .replaceAll("\"apiKey\":\"[^\"]+\"", "\"apiKey\":\"*****\"")
            .replaceAll("\"secret\":\"[^\"]+\"", "\"secret\":\"*****\"")
            .replaceAll("\"token\":\"[^\"]+\"", "\"token\":\"*****\"");
        
        return sanitized;
    }
    
    /**
     * Check if running in development environment
     */
    private static boolean isDevelopmentEnvironment() {
        String env = System.getProperty("spring.profiles.active", "development");
        String envVar = System.getenv("SPRING_PROFILES_ACTIVE");
        if (envVar != null) {
            env = envVar;
        }
        return "development".equals(env) || "dev".equals(env);
    }
    
    /**
     * API validation result class
     */
    public static class APIValidationResult {
        private boolean valid;
        private boolean preflight;
        private boolean isPublic;
        private boolean isAdmin;
        private String error;
        private String userId;
        private String email;
        private String role;
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public boolean isPreflight() {
            return preflight;
        }
        
        public void setPreflight(boolean preflight) {
            this.preflight = preflight;
        }
        
        public boolean isPublic() {
            return isPublic;
        }
        
        public void setPublic(boolean isPublic) {
            this.isPublic = isPublic;
        }
        
        public boolean isAdmin() {
            return isAdmin;
        }
        
        public void setAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
    }
}
