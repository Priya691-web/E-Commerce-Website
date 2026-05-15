package com.fashionstore.security;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Endpoint Security Filter
 * Applies security measures to all backend endpoints
 */
@WebFilter(urlPatterns = "/*")
public class EndpointSecurityFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(EndpointSecurityFilter.class);

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("EndpointSecurityFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // Apply endpoint security measures
            if (!applyEndpointSecurity(httpRequest, httpResponse)) {
                return; // Security measure blocked the request
            }

            // Continue with the request
            chain.doFilter(request, response);

        } catch (Exception e) {
            logger.error("Endpoint security filter error: {}", e.getMessage(), e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Apply endpoint security measures
     */
    private boolean applyEndpointSecurity(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Configure CORS for API requests
        if (path.startsWith("/api")) {
            APISecurityUtil.configureCORS(request, response);
            
            // Handle preflight requests
            if (APISecurityUtil.isPreflightRequest(request)) {
                APISecurityUtil.handlePreflightRequest(request, response);
                return false;
            }
        }

        // 2. Validate API request
        if (path.startsWith("/api")) {
            APISecurityUtil.APIValidationResult validationResult = 
                APISecurityUtil.validateAPIRequest(request);
            
            if (!validationResult.isValid()) {
                logger.warn("API validation failed for path: {}, error: {}", path, validationResult.getError());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, validationResult.getError());
                return false;
            }
        }

        // 3. Validate session for non-public endpoints
        if (!APISecurityUtil.isPublicEndpoint(path) && !path.startsWith("/api")) {
            SessionSecurityUtil.SessionValidationResult sessionResult = 
                SessionSecurityUtil.validateSession(request);
            
            if (!sessionResult.isValid()) {
                logger.warn("Session validation failed for path: {}, reason: {}", path, sessionResult.getReason());
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }

        // 4. Validate CSRF for state-changing operations
        if (CSRFProtection.requiresProtection(request)) {
            if (!CSRFProtection.validateRequest(request)) {
                logger.warn("CSRF validation failed for path: {}", path);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "CSRF validation failed");
                return false;
            }
        }

        // 5. Validate admin access for admin endpoints
        if (path.startsWith("/admin") || path.startsWith("/api/admin")) {
            AdminAuthorizationUtil.AuthorizationResult authResult = 
                AdminAuthorizationUtil.validateAdminAccess(request);
            
            if (!authResult.isAuthorized()) {
                logger.warn("Admin authorization failed for path: {}, reason: {}", path, authResult.getReason());
                response.sendError(HttpServletResponse.SC_FORBIDDEN, authResult.getReason());
                return false;
            }
            
            // Log admin action
            AdminAuthorizationUtil.logAdminAction(request, method, path);
        }

        // 6. Input validation for POST/PUT/PATCH requests
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || 
            "PATCH".equalsIgnoreCase(method)) {
            if (!validateRequestInput(request)) {
                logger.warn("Input validation failed for path: {}", path);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid input");
                return false;
            }
        }

        return true;
    }

    /**
     * Validate request input
     */
    private boolean validateRequestInput(HttpServletRequest request) {
        String queryString = request.getQueryString();
        
        // Check query string for injection attacks
        if (queryString != null) {
            if (InputValidator.containsSQLInjection(queryString) ||
                InputValidator.containsXSS(queryString) ||
                InputValidator.containsCommandInjection(queryString) ||
                InputValidator.containsPathTraversal(queryString)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void destroy() {
        logger.info("EndpointSecurityFilter destroyed");
    }
}
