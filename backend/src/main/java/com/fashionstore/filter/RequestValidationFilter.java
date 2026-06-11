package com.fashionstore.filter;

import com.fashionstore.security.XSSProtectionUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * RequestValidationFilter - Validates incoming requests for security and integrity
 * 
 * ROOT CAUSE: Without request validation, malformed or malicious requests can bypass security checks,
 * leading to injection attacks, buffer overflows, or other vulnerabilities.
 * 
 * FIX: This filter validates all incoming requests for proper structure, content type, method,
 * and security headers before processing continues.
 */
@WebFilter(urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class RequestValidationFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestValidationFilter.class);
    
    // Phase 1.6: Request validation middleware configuration
    private static final Set<String> ALLOWED_CONTENT_TYPES = new HashSet<>(Arrays.asList(
        "application/json",
        "application/x-www-form-urlencoded",
        "multipart/form-data",
        "text/plain",
        "text/html"
    ));
    
    private static final Set<String> SAFE_METHODS = new HashSet<>(Arrays.asList(
        "GET", "HEAD", "OPTIONS", "TRACE"
    ));
    
    private static final Set<String> EXEMPTED_PATHS = new HashSet<>(Arrays.asList(
        "/health",
        "/healthz",
        "/metrics",
        "/csp-violation-report"
    ));
    
    private static final int MAX_URI_LENGTH = 2000;
    private static final int MAX_HEADER_LENGTH = 8192;
    private static final int MAX_QUERY_STRING_LENGTH = 1000;
    
    private boolean validationEnabled;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Check if request validation is enabled via environment variable
        String validationEnv = System.getenv("REQUEST_VALIDATION_ENABLED");
        if (validationEnv == null || validationEnv.trim().isEmpty()) {
            validationEnv = System.getProperty("request.validation.enabled", "true");
        }
        validationEnabled = Boolean.parseBoolean(validationEnv);
        
        logger.info("RequestValidationFilter initialized (enabled: {})", validationEnabled);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip if validation is disabled
        if (!validationEnabled) {
            chain.doFilter(request, response);
            return;
        }
        
        // Skip internal dispatcher forwards/includes/errors
        DispatcherType dt = httpRequest.getDispatcherType();
        if (dt == DispatcherType.FORWARD || dt == DispatcherType.INCLUDE || dt == DispatcherType.ERROR) {
            chain.doFilter(request, response);
            return;
        }
        
        String path = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String relativePath = path.length() > contextPath.length() 
            ? path.substring(contextPath.length()) 
            : path;
        
        // Skip exempted paths
        if (isExemptedPath(relativePath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Validate request
        ValidationResult result = validateRequest(httpRequest);
        if (!result.isValid()) {
            logger.warn("Request validation failed for {}: {}", relativePath, result.getError());
            sendValidationError(httpResponse, result.getError(), result.getStatusCode());
            return;
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("RequestValidationFilter destroyed");
    }
    
    // ---------------------------------------------------------------------------
    // Validation Methods
    // ---------------------------------------------------------------------------
    
    private ValidationResult validateRequest(HttpServletRequest request) {
        // Validate URI length
        String uri = request.getRequestURI();
        if (uri != null && uri.length() > MAX_URI_LENGTH) {
            return new ValidationResult(false, "URI too long", 414);
        }
        
        // Validate query string length
        String queryString = request.getQueryString();
        if (queryString != null && queryString.length() > MAX_QUERY_STRING_LENGTH) {
            return new ValidationResult(false, "Query string too long", 414);
        }
        
        // Validate query string for XSS
        if (queryString != null && XSSProtectionUtil.containsXSS(queryString)) {
            return new ValidationResult(false, "Potentially malicious query string", HttpServletResponse.SC_BAD_REQUEST);
        }
        
        // Validate method
        String method = request.getMethod();
        if (method == null || method.trim().isEmpty()) {
            return new ValidationResult(false, "Invalid HTTP method", HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
        
        // Validate content type for state-changing methods
        if (!SAFE_METHODS.contains(method.toUpperCase())) {
            String contentType = request.getContentType();
            if (contentType != null) {
                // Extract content type without charset
                String contentTypeOnly = contentType.split(";")[0].trim();
                if (!ALLOWED_CONTENT_TYPES.contains(contentTypeOnly)) {
                    return new ValidationResult(false, "Unsupported content type: " + contentTypeOnly, 
                                               HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                }
            }
        }
        
        // Validate headers
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // Validate header length
            if (headerValue != null && headerValue.length() > MAX_HEADER_LENGTH) {
                return new ValidationResult(false, "Header too long: " + headerName, 
                                           HttpServletResponse.SC_BAD_REQUEST);
            }
            
            // Check for suspicious headers
            if (isSuspiciousHeader(headerName, headerValue)) {
                return new ValidationResult(false, "Suspicious header detected", 
                                           HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        
        return new ValidationResult(true, null, 0);
    }
    
    private boolean isExemptedPath(String path) {
        for (String exemptPath : EXEMPTED_PATHS) {
            if (path.equals(exemptPath) || path.startsWith(exemptPath + "/")) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isSuspiciousHeader(String headerName, String headerValue) {
        if (headerValue == null) {
            return false;
        }
        
        String lowerName = headerName.toLowerCase();
        String lowerValue = headerValue.toLowerCase();
        
        // Check for XSS in headers
        if (XSSProtectionUtil.containsXSS(headerValue)) {
            logger.warn("Potential XSS in header {}: {}", headerName, headerValue);
            return true;
        }
        
        // Check for suspicious patterns in User-Agent
        if ("user-agent".equals(lowerName)) {
            if (lowerValue.contains("sqlmap") || 
                lowerValue.contains("nikto") || 
                lowerValue.contains("nmap") ||
                lowerValue.contains("burp") ||
                lowerValue.contains("owasp")) {
                logger.warn("Suspicious User-Agent detected: {}", headerValue);
                return true;
            }
        }
        
        return false;
    }
    
    private void sendValidationError(HttpServletResponse response, String error, int statusCode) 
            throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            String.format("{\"success\":false,\"message\":\"%s\",\"error\":\"VALIDATION_ERROR\"}", 
                         error.replace("\"", "\\\""))
        );
    }
    
    // ---------------------------------------------------------------------------
    // Validation Result Class
    // ---------------------------------------------------------------------------
    
    private static class ValidationResult {
        private final boolean valid;
        private final String error;
        private final int statusCode;
        
        public ValidationResult(boolean valid, String error, int statusCode) {
            this.valid = valid;
            this.error = error;
            this.statusCode = statusCode;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getError() {
            return error;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
    }
}
