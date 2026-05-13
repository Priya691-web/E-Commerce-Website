package com.fashionstore.logging;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Enhanced logging filter with correlation IDs, request tracking, and metrics collection
 * Provides comprehensive request logging with security filtering and performance monitoring
 */
public class LoggingFilter implements Filter {

    private EnterpriseLogger enterpriseLogger;
    private MetricsCollector metricsCollector;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        enterpriseLogger = EnterpriseLogger.getLogger(LoggingFilter.class);
        metricsCollector = MetricsCollector.getInstance();
        
        enterpriseLogger.logBusinessEvent("SYSTEM_STARTUP", "logging-filter", "filter", 
                                         "Enterprise logging filter initialized", null);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Generate correlation and request IDs
        String correlationId = generateOrGetCorrelationId(httpRequest);
        String requestId = EnterpriseLogger.generateRequestId();
        
        // Set up logging context
        enterpriseLogger.setCorrelationId(correlationId);
        enterpriseLogger.setRequestId(requestId);
        
        // Add IDs to request attributes for downstream use
        httpRequest.setAttribute("correlationId", correlationId);
        httpRequest.setAttribute("requestId", requestId);
        
        // Add to MDC for all loggers
        MDC.put("correlationId", correlationId);
        MDC.put("requestId", requestId);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Wrap response to capture status and timing
            ResponseWrapper wrappedResponse = new ResponseWrapper(httpResponse);
            
            // Log request start
            logRequestStart(httpRequest, correlationId, requestId);
            
            // Continue with the request
            chain.doFilter(request, wrappedResponse);
            
            // Calculate duration and log completion
            long duration = System.currentTimeMillis() - startTime;
            
            // Record metrics
            metricsCollector.recordHttpRequest(
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                wrappedResponse.getStatus(),
                duration
            );
            
            // Log request completion
            logRequestEnd(httpRequest, wrappedResponse, duration, correlationId, requestId);
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log failed request
            metricsCollector.recordHttpRequest(
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                duration
            );
            
            enterpriseLogger.logFailedRequest(
                httpRequest.getRequestURI(),
                httpRequest.getMethod(),
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                e.getMessage(),
                duration,
                createRequestMetadata(httpRequest)
            );
            
            throw e;
        } finally {
            // Clear MDC context
            MDC.clear();
        }
    }
    
    @Override
    public void destroy() {
        enterpriseLogger.logBusinessEvent("SYSTEM_SHUTDOWN", "logging-filter", "filter", 
                                         "Enterprise logging filter destroyed", null);
    }
    
    /**
     * Log request start
     */
    private void logRequestStart(HttpServletRequest request, String correlationId, String requestId) {
        Map<String, Object> metadata = createRequestMetadata(request);
        
        enterpriseLogger.logBusinessEvent(
            "REQUEST_START",
            requestId,
            "http_request",
            "Request started: " + request.getMethod() + " " + request.getRequestURI(),
            metadata
        );
    }
    
    /**
     * Log request completion
     */
    private void logRequestEnd(HttpServletRequest request, ResponseWrapper response, 
                              long duration, String correlationId, String requestId) {
        
        Map<String, Object> metadata = new HashMap<>();
        metadata.putAll(createRequestMetadata(request));
        metadata.put("responseStatus", response.getStatus());
        metadata.put("responseHeaders", getResponseHeaders(response));
        
        String eventType = duration > 1000 ? "SLOW_REQUEST" : "REQUEST_END";
        
        enterpriseLogger.logPerformance(
            request.getMethod() + " " + request.getRequestURI(),
            duration,
            response.getStatus() < 400,
            metadata
        );
        
        enterpriseLogger.logBusinessEvent(
            eventType,
            requestId,
            "http_request",
            "Request completed: " + request.getMethod() + " " + request.getRequestURI() + 
            " in " + duration + "ms with status " + response.getStatus(),
            metadata
        );
    }
    
    /**
     * Create request metadata for logging
     */
    private Map<String, Object> createRequestMetadata(HttpServletRequest request) {
        Map<String, Object> metadata = new HashMap<>();
        
        // Request information
        metadata.put("method", request.getMethod());
        metadata.put("uri", request.getRequestURI());
        metadata.put("queryString", request.getQueryString());
        metadata.put("protocol", request.getProtocol());
        metadata.put("remoteAddr", request.getRemoteAddr());
        metadata.put("remoteHost", request.getRemoteHost());
        
        // Headers (sensitive ones filtered)
        metadata.put("headers", getFilteredHeaders(request));
        
        // User agent
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            metadata.put("userAgent", userAgent);
        }
        
        // Content type and length
        String contentType = request.getContentType();
        if (contentType != null) {
            metadata.put("contentType", contentType);
        }
        
        int contentLength = request.getContentLength();
        if (contentLength > 0) {
            metadata.put("contentLength", contentLength);
        }
        
        // Session information
        if (request.getSession(false) != null) {
            metadata.put("sessionId", request.getSession(false).getId());
            metadata.put("sessionCreationTime", request.getSession(false).getCreationTime());
        }
        
        // User information
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            metadata.put("userId", userId.toString());
            enterpriseLogger.setUserContext((Long) userId, null, request.getSession(false) != null ? request.getSession(false).getId() : null);
        }
        
        return metadata;
    }
    
    /**
     * Get filtered headers (remove sensitive information)
     */
    private Map<String, String> getFilteredHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            
            // Skip sensitive headers
            if (isSensitiveHeader(headerName)) {
                continue;
            }
            
            String headerValue = request.getHeader(headerName);
            if (headerValue != null) {
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }
    
    /**
     * Get response headers
     */
    private Map<String, String> getResponseHeaders(ResponseWrapper response) {
        Map<String, String> headers = new HashMap<>();
        
        for (String headerName : response.getHeaderNames()) {
            headers.put(headerName, response.getHeader(headerName));
        }
        
        return headers;
    }
    
    /**
     * Check if header is sensitive
     */
    private boolean isSensitiveHeader(String headerName) {
        String lowerName = headerName.toLowerCase();
        return lowerName.contains("authorization") ||
               lowerName.contains("token") ||
               lowerName.contains("password") ||
               lowerName.contains("secret") ||
               lowerName.contains("key") ||
               lowerName.contains("cookie") ||
               lowerName.contains("session");
    }
    
    /**
     * Generate or get correlation ID from request
     */
    private String generateOrGetCorrelationId(HttpServletRequest request) {
        // Check if correlation ID is already present (from upstream service)
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = request.getHeader("X-Request-ID");
        }
        if (correlationId == null || correlationId.trim().isEmpty()) {
            correlationId = EnterpriseLogger.generateCorrelationId();
        }
        
        // Add to response header for downstream services
        request.setAttribute("correlationId", correlationId);
        
        return correlationId;
    }
    
    /**
     * Response wrapper to capture status and headers
     */
    private static class ResponseWrapper extends HttpServletResponseWrapper {
        
        private int status = HttpServletResponse.SC_OK;
        private final Map<String, String> headers = new HashMap<>();
        
        public ResponseWrapper(HttpServletResponse response) {
            super(response);
        }
        
        @Override
        public void setStatus(int sc) {
            super.setStatus(sc);
            this.status = sc;
        }
        
        public void setStatus(int sc, String sm) {
            super.setStatus(sc);
            this.status = sc;
        }
        
        @Override
        public void sendError(int sc) throws IOException {
            super.sendError(sc);
            this.status = sc;
        }
        
        @Override
        public void sendError(int sc, String msg) throws IOException {
            super.sendError(sc, msg);
            this.status = sc;
        }
        
        @Override
        public void addHeader(String name, String value) {
            super.addHeader(name, value);
            headers.put(name, value);
        }
        
        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, value);
            headers.put(name, value);
        }
        
        public int getStatus() {
            return status;
        }
        
        public Map<String, String> getHeaders() {
            return new HashMap<>(headers);
        }
        
        public String getHeader(String name) {
            return headers.get(name);
        }
        
        public java.util.Collection<String> getHeaderNames() {
            return headers.keySet();
        }
    }
}
