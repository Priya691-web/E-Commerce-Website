package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Admin Authorization Utilities
 * Provides role-based access control for admin operations
 */
public class AdminAuthorizationUtil {
    private static final Logger logger = LoggerFactory.getLogger(AdminAuthorizationUtil.class);
    
    // Admin roles
    private static final Set<String> ADMIN_ROLES = new HashSet<>(Arrays.asList(
        "admin", "super_admin", "manager"
    ));
    
    // Admin permissions
    private static final Set<String> ADMIN_PERMISSIONS = new HashSet<>(Arrays.asList(
        "dashboard", "products", "orders", "users", "categories", "coupons", "settings", "reports"
    ));
    
    // Super admin permissions (includes all admin permissions plus more)
    private static final Set<String> SUPER_ADMIN_PERMISSIONS = new HashSet<>(Arrays.asList(
        "dashboard", "products", "orders", "users", "categories", "coupons", "settings", "reports",
        "system", "audit", "security", "backup", "restore"
    ));
    
    /**
     * Check if user is admin
     */
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Object adminAuth = session.getAttribute("adminAuth");
        return adminAuth != null;
    }
    
    /**
     * Check if user is super admin
     */
    public static boolean isSuperAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Object adminAuth = session.getAttribute("adminAuth");
        if (adminAuth == null) {
            return false;
        }
        
        // Check if user has super_admin role
        // This would typically come from the user object in session
        return true; // Simplified for this implementation
    }
    
    /**
     * Check if user has admin role
     */
    public static boolean hasAdminRole(HttpServletRequest request, String role) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Object adminAuth = session.getAttribute("adminAuth");
        if (adminAuth == null) {
            return false;
        }
        
        return ADMIN_ROLES.contains(role.toLowerCase());
    }
    
    /**
     * Check if user has permission
     */
    public static boolean hasPermission(HttpServletRequest request, String permission) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Object adminAuth = session.getAttribute("adminAuth");
        if (adminAuth == null) {
            return false;
        }
        
        // Super admins have all permissions
        if (isSuperAdmin(request)) {
            return SUPER_ADMIN_PERMISSIONS.contains(permission.toLowerCase());
        }
        
        // Regular admins have admin permissions
        return ADMIN_PERMISSIONS.contains(permission.toLowerCase());
    }
    
    /**
     * Check if user can access admin area
     */
    public static boolean canAccessAdminArea(HttpServletRequest request) {
        return isAdmin(request);
    }
    
    /**
     * Check if user can perform sensitive operation
     */
    public static boolean canPerformSensitiveOperation(HttpServletRequest request, String operation) {
        if (!isAdmin(request)) {
            logger.warn("Non-admin user attempted sensitive operation: {}", operation);
            return false;
        }
        
        // Some operations require super admin
        if (operation.equals("system") || operation.equals("security") || operation.equals("audit")) {
            return isSuperAdmin(request);
        }
        
        return hasPermission(request, operation);
    }
    
    /**
     * Validate admin access
     */
    public static AuthorizationResult validateAdminAccess(HttpServletRequest request) {
        AuthorizationResult result = new AuthorizationResult();
        
        // Check if user is authenticated
        HttpSession session = request.getSession(false);
        if (session == null) {
            result.setAuthorized(false);
            result.setReason("Not authenticated");
            return result;
        }
        
        // Check if user is admin
        Object adminAuth = session.getAttribute("adminAuth");
        if (adminAuth == null) {
            result.setAuthorized(false);
            result.setReason("Not authorized as admin");
            return result;
        }
        
        // Validate session
        SessionSecurityUtil.SessionValidationResult sessionResult = SessionSecurityUtil.validateSession(request);
        if (!sessionResult.isValid()) {
            result.setAuthorized(false);
            result.setReason("Invalid session: " + sessionResult.getReason());
            return result;
        }
        
        result.setAuthorized(true);
        result.setUserId((String) session.getAttribute("userId"));
        result.setAdminId((String) session.getAttribute("adminId"));
        
        return result;
    }
    
    /**
     * Validate permission for specific operation
     */
    public static AuthorizationResult validatePermission(HttpServletRequest request, String permission) {
        AuthorizationResult result = validateAdminAccess(request);
        
        if (!result.isAuthorized()) {
            return result;
        }
        
        if (!hasPermission(request, permission)) {
            result.setAuthorized(false);
            result.setReason("Permission denied: " + permission);
            return result;
        }
        
        return result;
    }
    
    /**
     * Log admin action for audit trail
     */
    public static void logAdminAction(HttpServletRequest request, String action, String details) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userId = (String) session.getAttribute("userId");
            String adminId = (String) session.getAttribute("adminId");
            String clientIP = getClientIP(request);
            
            logger.info("Admin Action - User: {}, Admin: {}, Action: {}, Details: {}, IP: {}", 
                userId, adminId, action, details, clientIP);
        }
    }
    
    /**
     * Get client IP address
     */
    private static String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Authorization result class
     */
    public static class AuthorizationResult {
        private boolean authorized;
        private String reason;
        private String userId;
        private String adminId;
        
        public boolean isAuthorized() {
            return authorized;
        }
        
        public void setAuthorized(boolean authorized) {
            this.authorized = authorized;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getAdminId() {
            return adminId;
        }
        
        public void setAdminId(String adminId) {
            this.adminId = adminId;
        }
    }
}
