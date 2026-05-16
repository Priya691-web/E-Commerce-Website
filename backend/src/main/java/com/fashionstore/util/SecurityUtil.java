package com.fashionstore.util;

import com.fashionstore.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class SecurityUtil {

    /**
     * Get the authenticated customer user (storefront only)
     * NEVER returns admin users - completely isolated
     */
    public static User getAuthenticatedCustomer(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object customerAuth = session.getAttribute("customerAuth");
        return (customerAuth instanceof User) ? (User) customerAuth : null;
    }

    /**
     * Get the authenticated admin user (admin only)
     * NEVER returns customer users - completely isolated
     */
    public static User getAuthenticatedAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object adminAuth = session.getAttribute("adminAuth");
        return (adminAuth instanceof User) ? (User) adminAuth : null;
    }

    /**
     * Check if the current user is authenticated (customer or admin)
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        // Check for either customer or admin authentication
        Object customerAuth = session.getAttribute("customerAuth");
        Object adminAuth = session.getAttribute("adminAuth");
        return (customerAuth instanceof User) || (adminAuth instanceof User);
    }

    /**
     * Check if the current user is an authenticated customer
     */
    public static boolean isAuthenticatedCustomer(HttpServletRequest request) {
        return getAuthenticatedCustomer(request) != null;
    }

    /**
     * Check if the current user is an authenticated admin
     */
    public static boolean isAuthenticatedAdmin(HttpServletRequest request) {
        return getAuthenticatedAdmin(request) != null;
    }

    /**
     * Check if the current user is an admin
     */
    public static boolean isAdmin(HttpServletRequest request) {
        User adminUser = getAuthenticatedAdmin(request);
        return adminUser != null && adminUser.isAdmin();
    }

    /**
     * Check if the current customer is the owner of a resource
     * Only checks customer auth - never falls back to admin
     */
    public static boolean isCustomerOwner(HttpServletRequest request, int resourceUserId) {
        User customerUser = getAuthenticatedCustomer(request);
        return customerUser != null && customerUser.getUserId() == resourceUserId;
    }

    /**
     * Check if the current admin is the owner of a resource
     * Only checks admin auth - never falls back to customer
     */
    public static boolean isAdminOwner(HttpServletRequest request, int resourceUserId) {
        User adminUser = getAuthenticatedAdmin(request);
        return adminUser != null && adminUser.getUserId() == resourceUserId;
    }

    /**
     * Get the current authenticated customer user (storefront only)
     * DEPRECATED: Use getAuthenticatedCustomer() instead
     */
    public static User getCurrentUser(HttpServletRequest request) {
        return getAuthenticatedCustomer(request);
    }

    /**
     * Require authentication - redirect to login if not authenticated
     */
    public static boolean requireAuthentication(HttpServletRequest request, HttpServletResponse response, String contextPath) {
        if (!isAuthenticated(request)) {
            try {
                response.sendRedirect(contextPath + "/login");
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Require admin role - send 403 if not admin
     */
    public static boolean requireAdmin(HttpServletRequest request, HttpServletResponse response) {
        if (!isAuthenticated(request)) {
            try {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        if (!isAdmin(request)) {
            try {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate CSRF token
     * DEPRECATED: Use CSRFProtection.validateRequest() instead
     * This method is kept for backward compatibility but delegates to CSRFProtection
     */
    public static boolean validateCSRF(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;
        
        String sessionToken = (String) session.getAttribute("csrfToken");
        String requestToken = request.getParameter("csrfToken");
        
        return sessionToken != null && sessionToken.equals(requestToken);
    }
}
