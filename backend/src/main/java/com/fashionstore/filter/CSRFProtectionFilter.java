package com.fashionstore.filter;

import com.fashionstore.security.CSRFProtection;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * CSRFProtectionFilter - Protects against Cross-Site Request Forgery attacks
 *
 * This filter delegates all verification, path-based exemption logic, and token
 * generation directly to com.fashionstore.security.CSRFProtection. This ensures
 * robust protection while remaining perfectly synchronized with standard views and MVC flows.
 */
@WebFilter(urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class CSRFProtectionFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(CSRFProtectionFilter.class);
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("CSRFProtectionFilter initialized (enabled: {})", CSRFProtection.isEnabled());
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip if CSRF is disabled
        if (!CSRFProtection.isEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        
        // Skip internal dispatcher forwards/includes/errors
        DispatcherType dt = httpRequest.getDispatcherType();
        if (dt == DispatcherType.FORWARD || dt == DispatcherType.INCLUDE || dt == DispatcherType.ERROR) {
            chain.doFilter(request, response);
            return;
        }
        
        String method = httpRequest.getMethod();
        
        // Generate and bind CSRF token for safe requests (GET/HEAD/OPTIONS/TRACE) so frontend pages/forms can render it
        if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method) || "OPTIONS".equalsIgnoreCase(method) || "TRACE".equalsIgnoreCase(method)) {
            CSRFProtection.addTokenToRequest(httpRequest);
            chain.doFilter(request, response);
            return;
        }
        
        // Validate CSRF token for state-changing requests using standard security engine
        if (!CSRFProtection.validateRequest(httpRequest)) {
            logger.warn("CSRF token validation failed for {} {}", method, httpRequest.getRequestURI());
            sendCsrfError(httpResponse);
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("CSRFProtectionFilter destroyed");
    }
    
    private void sendCsrfError(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            "{\"success\":false,\"message\":\"CSRF token validation failed\",\"error\":\"CSRF_INVALID\"}"
        );
    }

    /**
     * Get CSRF token for current session (retained for backward compatibility)
     */
    public static String getCsrfToken(HttpServletRequest request) {
        return CSRFProtection.getTokenForAjax(request);
    }
}
