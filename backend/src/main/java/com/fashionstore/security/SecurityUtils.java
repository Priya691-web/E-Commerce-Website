package com.fashionstore.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Security utility class providing CSRF validation and other security checks.
 * This class delegates to CSRFProtection for CSRF token validation.
 * 
 * @deprecated Use {@link CSRFProtection} directly for new code.
 * This class is provided for backward compatibility with existing controllers.
 */
@Deprecated
public class SecurityUtils {

    /**
     * Validates CSRF token from the request.
     * Delegates to {@link CSRFProtection#validateRequest(HttpServletRequest)}.
     * 
     * @param request HTTP request
     * @param session HTTP session (ignored, kept for backward compatibility)
     * @return true if CSRF validation passes, false otherwise
     */
    public static boolean validateCSRFToken(HttpServletRequest request, HttpSession session) {
        return CSRFProtection.validateRequest(request);
    }

    /**
     * Validates CSRF token from the request.
     * Delegates to {@link CSRFProtection#validateRequest(HttpServletRequest)}.
     * 
     * @param request HTTP request
     * @return true if CSRF validation passes, false otherwise
     */
    public static boolean validateCSRFToken(HttpServletRequest request) {
        return CSRFProtection.validateRequest(request);
    }

    /**
     * Checks if the request requires CSRF protection.
     * Delegates to {@link CSRFProtection#requiresProtection(HttpServletRequest)}.
     * 
     * @param request HTTP request
     * @return true if the request requires CSRF protection
     */
    public static boolean requiresCSRFProtection(HttpServletRequest request) {
        return CSRFProtection.requiresProtection(request);
    }

    /**
     * Generates a new CSRF token for the session.
     * Delegates to {@link CSRFProtection#generateToken(HttpServletRequest)}.
     * 
     * @param request HTTP request
     * @return CSRF token
     */
    public static String generateCSRFToken(HttpServletRequest request) {
        return CSRFProtection.generateToken(request);
    }
}
