package com.fashionstore.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * SecureHeadersFilter - Adds security headers to HTTP responses
 * 
 * ROOT CAUSE: Without security headers, the application is vulnerable to various attacks including:
 * - Clickjacking (X-Frame-Options)
 * - MIME sniffing (X-Content-Type-Options)
 * - XSS attacks (X-XSS-Protection, Content-Security-Policy)
 * - Man-in-the-middle attacks (Strict-Transport-Security)
 * - Information disclosure (Referrer-Policy)
 * 
 * FIX: This filter adds comprehensive security headers to all HTTP responses following OWASP best practices.
 */
@WebFilter(urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class SecureHeadersFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(SecureHeadersFilter.class);
    
    // Phase 1.8: Secure headers configuration
    private boolean httpsEnabled;
    private boolean headersEnabled;
    
    // Security header values
    private static final String CSP_HEADER = "Content-Security-Policy";
    private static final String CSP_VALUE = "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self' data:; connect-src 'self'; frame-ancestors 'none';";
    
    private static final String FRAME_OPTIONS_HEADER = "X-Frame-Options";
    private static final String FRAME_OPTIONS_VALUE = "DENY";
    
    private static final String CONTENT_TYPE_OPTIONS_HEADER = "X-Content-Type-Options";
    private static final String CONTENT_TYPE_OPTIONS_VALUE = "nosniff";
    
    private static final String XSS_PROTECTION_HEADER = "X-XSS-Protection";
    private static final String XSS_PROTECTION_VALUE = "1; mode=block";
    
    private static final String STS_HEADER = "Strict-Transport-Security";
    private static final String STS_VALUE = "max-age=31536000; includeSubDomains; preload";
    
    private static final String REFERRER_POLICY_HEADER = "Referrer-Policy";
    private static final String REFERRER_POLICY_VALUE = "strict-origin-when-cross-origin";
    
    private static final String PERMISSIONS_POLICY_HEADER = "Permissions-Policy";
    private static final String PERMISSIONS_POLICY_VALUE = "geolocation=(), microphone=(), camera=(), payment=(), usb=()";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Check if HTTPS is enabled
        String httpsEnv = System.getenv("HTTPS_ENABLED");
        if (httpsEnv == null || httpsEnv.trim().isEmpty()) {
            httpsEnv = System.getProperty("https.enabled", "false");
        }
        httpsEnabled = Boolean.parseBoolean(httpsEnv);
        
        // Check if secure headers are enabled
        String headersEnv = System.getenv("SECURE_HEADERS_ENABLED");
        if (headersEnv == null || headersEnv.trim().isEmpty()) {
            headersEnv = System.getProperty("secure.headers.enabled", "true");
        }
        headersEnabled = Boolean.parseBoolean(headersEnv);
        
        logger.info("SecureHeadersFilter initialized (enabled: {}, https: {})", headersEnabled, httpsEnabled);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip if headers are disabled
        if (!headersEnabled) {
            chain.doFilter(request, response);
            return;
        }
        
        // Skip internal dispatcher forwards/includes/errors
        DispatcherType dt = httpRequest.getDispatcherType();
        if (dt == DispatcherType.FORWARD || dt == DispatcherType.INCLUDE || dt == DispatcherType.ERROR) {
            chain.doFilter(request, response);
            return;
        }
        
        // Add security headers
        addSecurityHeaders(httpRequest, httpResponse);
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("SecureHeadersFilter destroyed");
    }
    
    // ---------------------------------------------------------------------------
    // Header Addition Methods
    // ---------------------------------------------------------------------------
    
    private void addSecurityHeaders(HttpServletRequest request, HttpServletResponse response) {
        // Content-Security-Policy - Prevents XSS and data injection attacks
        if (!response.containsHeader(CSP_HEADER)) {
            response.setHeader(CSP_HEADER, CSP_VALUE);
        }
        
        // X-Frame-Options - Prevents clickjacking
        if (!response.containsHeader(FRAME_OPTIONS_HEADER)) {
            response.setHeader(FRAME_OPTIONS_HEADER, FRAME_OPTIONS_VALUE);
        }
        
        // X-Content-Type-Options - Prevents MIME sniffing
        if (!response.containsHeader(CONTENT_TYPE_OPTIONS_HEADER)) {
            response.setHeader(CONTENT_TYPE_OPTIONS_HEADER, CONTENT_TYPE_OPTIONS_VALUE);
        }
        
        // X-XSS-Protection - Enables XSS filtering
        if (!response.containsHeader(XSS_PROTECTION_HEADER)) {
            response.setHeader(XSS_PROTECTION_HEADER, XSS_PROTECTION_VALUE);
        }
        
        // Strict-Transport-Security - Enforces HTTPS (only if HTTPS is enabled)
        if (httpsEnabled && !response.containsHeader(STS_HEADER)) {
            response.setHeader(STS_HEADER, STS_VALUE);
        }
        
        // Referrer-Policy - Controls referrer information
        if (!response.containsHeader(REFERRER_POLICY_HEADER)) {
            response.setHeader(REFERRER_POLICY_HEADER, REFERRER_POLICY_VALUE);
        }
        
        // Permissions-Policy - Controls browser features
        if (!response.containsHeader(PERMISSIONS_POLICY_HEADER)) {
            response.setHeader(PERMISSIONS_POLICY_HEADER, PERMISSIONS_POLICY_VALUE);
        }
        
        // Additional security headers
        addAdditionalSecurityHeaders(response);
        
        logger.debug("Security headers added for request: {}", request.getRequestURI());
    }
    
    private void addAdditionalSecurityHeaders(HttpServletResponse response) {
        // X-Permitted-Cross-Domain-Policies - Restricts cross-domain policies
        if (!response.containsHeader("X-Permitted-Cross-Domain-Policies")) {
            response.setHeader("X-Permitted-Cross-Domain-Policies", "none");
        }
        
        // Cross-Origin-Opener-Policy - Isolates browsing contexts
        if (!response.containsHeader("Cross-Origin-Opener-Policy")) {
            response.setHeader("Cross-Origin-Opener-Policy", "same-origin");
        }
        
        // Cross-Origin-Resource-Policy - Controls cross-origin resource sharing
        if (!response.containsHeader("Cross-Origin-Resource-Policy")) {
            response.setHeader("Cross-Origin-Resource-Policy", "same-origin");
        }
        
        // Cross-Origin-Embedder-Policy - Controls cross-origin embedding
        if (!response.containsHeader("Cross-Origin-Embedder-Policy")) {
            response.setHeader("Cross-Origin-Embedder-Policy", "require-corp");
        }
    }
}
