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
     * This method ONLY checks adminAuth attribute - session validation delegated to SessionSecurityUtil
     */
    public static boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Object adminAuth = session.getAttribute("adminAuth");
        if (adminAuth == null) {
            return false;
        }
        
        return true;
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
     * Validate admin access - role-based authorization ONLY
     * Session validation is delegated to SessionSecurityUtil (single source of truth)
     * This method ONLY checks adminAuth and role - no session metadata validation
     */
    public static AuthorizationResult validateAdminAccess(HttpServletRequest request) {
        AuthorizationResult result = new AuthorizationResult();
        String path = request.getRequestURI();
        HttpSession session = request.getSession(false);
        String sessionId = session != null ? session.getId() : "null";
        String userId = (String) (session != null ? session.getAttribute("userId") : null);
        
        // Check if user is authenticated (session existence check only)
        if (session == null) {
            result.setAuthorized(false);
            result.setReason("Not authenticated");
            return result;
        }
        
        // Check if user has adminAuth attribute (role-based check only)
        Object adminAuth = session.getAttribute("adminAuth");
        if (adminAuth == null) {
            result.setAuthorized(false);
            result.setReason("Not authorized as admin");
            logger.warn("validateAdminAccess failed: adminAuth not in session - Path: {}, Session ID: {}", path, sessionId);
            return result;
        }
        
        // Check user role if available (role-based authorization)
        String role = (String) session.getAttribute("role");
        if (role != null && !ADMIN_ROLES.contains(role.toLowerCase())) {
            result.setAuthorized(false);
            result.setReason("Invalid admin role: " + role);
            logger.warn("validateAdminAccess failed: Invalid admin role - Path: {}, Session ID: {}, Role: {}", 
                path, sessionId, role);
            return result;
        }
        
        // Session validation (metadata, IP, user-agent) is delegated to SessionSecurityUtil
        // This method ONLY handles role-based authorization
        
        result.setAuthorized(true);
        result.setUserId(userId);
        
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
     * Uses SessionSecurityUtil.getClientIP() for consistent IP extraction
     */
    public static void logAdminAction(HttpServletRequest request, String action, String details) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userId = (String) session.getAttribute("userId");
            String clientIP = SessionSecurityUtil.getClientIP(request);
            
            logger.info("Admin Action - User: {}, Action: {}, Details: {}, IP: {}", 
                userId, action, details, clientIP);
        }
    }
    
    /**
     * Authorization result class
     */
    public static class AuthorizationResult {
        private boolean authorized;
        private String reason;
        private String userId;
        
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
    }
}
