package com.fashionstore.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * RequestBodySizeLimitFilter - Limits request body size to prevent DoS attacks
 * 
 * ROOT CAUSE: Without request body size limits, attackers can send extremely large payloads
 * causing memory exhaustion, server crashes, or slow responses (DoS attacks).
 * 
 * FIX: This filter enforces size limits on request bodies based on content type and endpoint,
 * rejecting requests that exceed the configured limits.
 */
@WebFilter(urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class RequestBodySizeLimitFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestBodySizeLimitFilter.class);
    
    // Phase 1.7: Request body size limits configuration
    private static final int DEFAULT_MAX_BODY_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_JSON_BODY_SIZE = 1 * 1024 * 1024; // 1MB for JSON
    private static final int MAX_FORM_DATA_SIZE = 10 * 1024 * 1024; // 10MB for form data
    private static final int MAX_FILE_UPLOAD_SIZE = 50 * 1024 * 1024; // 50MB for file uploads
    
    private static final Set<String> EXEMPTED_PATHS = new HashSet<>();
    
    static {
        EXEMPTED_PATHS.add("/health");
        EXEMPTED_PATHS.add("/healthz");
        EXEMPTED_PATHS.add("/metrics");
        EXEMPTED_PATHS.add("/csp-violation-report");
    }
    
    private int maxBodySize;
    private boolean limitEnabled;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Check if body size limiting is enabled via environment variable
        String limitEnv = System.getenv("REQUEST_BODY_LIMIT_ENABLED");
        if (limitEnv == null || limitEnv.trim().isEmpty()) {
            limitEnv = System.getProperty("request.body.limit.enabled", "true");
        }
        limitEnabled = Boolean.parseBoolean(limitEnv);
        
        // Get max body size from environment variable
        String maxSizeEnv = System.getenv("MAX_REQUEST_BODY_SIZE");
        if (maxSizeEnv == null || maxSizeEnv.trim().isEmpty()) {
            maxSizeEnv = System.getProperty("max.request.body.size", String.valueOf(DEFAULT_MAX_BODY_SIZE));
        }
        
        try {
            maxBodySize = Integer.parseInt(maxSizeEnv);
        } catch (NumberFormatException e) {
            logger.warn("Invalid MAX_REQUEST_BODY_SIZE value: {}, using default: {}", maxSizeEnv, DEFAULT_MAX_BODY_SIZE);
            maxBodySize = DEFAULT_MAX_BODY_SIZE;
        }
        
        logger.info("RequestBodySizeLimitFilter initialized (enabled: {}, maxSize: {} bytes)", 
                    limitEnabled, maxBodySize);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip if limit is disabled
        if (!limitEnabled) {
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
        
        // Get content length from request
        int contentLength = httpRequest.getContentLength();
        
        // If content length is available and exceeds limit, reject immediately
        if (contentLength > 0 && contentLength > maxBodySize) {
            logger.warn("Request body size {} bytes exceeds limit {} bytes for {}", 
                       contentLength, maxBodySize, relativePath);
            sendBodySizeError(httpResponse, contentLength, maxBodySize);
            return;
        }
        
        // Determine appropriate limit based on content type
        int contentTypeLimit = getLimitForContentType(httpRequest.getContentType());
        
        if (contentLength > 0 && contentLength > contentTypeLimit) {
            logger.warn("Request body size {} bytes exceeds content type limit {} bytes for {}", 
                       contentLength, contentTypeLimit, relativePath);
            sendBodySizeError(httpResponse, contentLength, contentTypeLimit);
            return;
        }
        
        // Wrap request to enforce size limit during reading
        SizeLimitingRequestWrapper wrappedRequest = new SizeLimitingRequestWrapper(
            httpRequest, contentTypeLimit
        );
        
        try {
            chain.doFilter(wrappedRequest, response);
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().contains("exceeds maximum")) {
                logger.warn("Request body size exceeded limit during reading: {}", e.getMessage());
                sendBodySizeError(httpResponse, 0, contentTypeLimit);
            } else {
                throw e;
            }
        }
    }
    
    @Override
    public void destroy() {
        logger.info("RequestBodySizeLimitFilter destroyed");
    }
    
    // ---------------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------------
    
    private boolean isExemptedPath(String path) {
        for (String exemptPath : EXEMPTED_PATHS) {
            if (path.equals(exemptPath) || path.startsWith(exemptPath + "/")) {
                return true;
            }
        }
        return false;
    }
    
    private int getLimitForContentType(String contentType) {
        if (contentType == null) {
            return maxBodySize;
        }
        
        String lowerContentType = contentType.toLowerCase();
        
        if (lowerContentType.contains("application/json")) {
            return MAX_JSON_BODY_SIZE;
        } else if (lowerContentType.contains("multipart/form-data")) {
            return MAX_FILE_UPLOAD_SIZE;
        } else if (lowerContentType.contains("application/x-www-form-urlencoded")) {
            return MAX_FORM_DATA_SIZE;
        }
        
        return maxBodySize;
    }
    
    private void sendBodySizeError(HttpServletResponse response, int actualSize, int maxSize) 
            throws IOException {
        response.setStatus(413); // HTTP 413 Payload Too Large
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
            String.format(
                "{\"success\":false,\"message\":\"Request body size %d bytes exceeds maximum allowed size of %d bytes\",\"error\":\"PAYLOAD_TOO_LARGE\"}", 
                actualSize, maxSize
            )
        );
    }
    
    // ---------------------------------------------------------------------------
    // Size Limiting Request Wrapper
    // ---------------------------------------------------------------------------
    
    private static class SizeLimitingRequestWrapper extends jakarta.servlet.http.HttpServletRequestWrapper {
        
        private final int maxSize;
        
        public SizeLimitingRequestWrapper(HttpServletRequest request, int maxSize) {
            super(request);
            this.maxSize = maxSize;
        }
        
        @Override
        public jakarta.servlet.ServletInputStream getInputStream() throws IOException {
            return new SizeLimitingInputStream(super.getInputStream(), maxSize);
        }
        
        @Override
        public java.io.BufferedReader getReader() throws IOException {
            return new java.io.BufferedReader(
                new java.io.InputStreamReader(getInputStream())
            );
        }
    }
    
    private static class SizeLimitingInputStream extends jakarta.servlet.ServletInputStream {
        
        private final jakarta.servlet.ServletInputStream delegate;
        private final int maxSize;
        private long bytesRead = 0;
        
        public SizeLimitingInputStream(jakarta.servlet.ServletInputStream delegate, int maxSize) {
            this.delegate = delegate;
            this.maxSize = maxSize;
        }
        
        @Override
        public int read() throws IOException {
            if (bytesRead >= maxSize) {
                throw new IOException("Request body size exceeds maximum allowed size of " + maxSize + " bytes");
            }
            
            int data = delegate.read();
            if (data != -1) {
                bytesRead++;
            }
            return data;
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            if (bytesRead >= maxSize) {
                throw new IOException("Request body size exceeds maximum allowed size of " + maxSize + " bytes");
            }
            
            int remaining = (int) (maxSize - bytesRead);
            if (len > remaining) {
                len = remaining;
            }
            
            int count = delegate.read(b, off, len);
            if (count > 0) {
                bytesRead += count;
            }
            return count;
        }
        
        @Override
        public boolean isFinished() {
            return delegate.isFinished() || bytesRead >= maxSize;
        }
        
        @Override
        public boolean isReady() {
            return delegate.isReady();
        }
        
        @Override
        public void setReadListener(jakarta.servlet.ReadListener readListener) {
            delegate.setReadListener(readListener);
        }
    }
}
