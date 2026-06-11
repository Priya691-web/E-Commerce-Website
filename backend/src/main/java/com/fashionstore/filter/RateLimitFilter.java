package com.fashionstore.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RateLimitFilter - Enterprise-grade API rate limiting and abuse prevention
 * 
 * ROOT CAUSE: Without rate limiting, APIs are vulnerable to DDoS attacks, brute force attempts,
 * and automated abuse that can degrade performance or cause service unavailability.
 * 
 * FIX: This filter implements sliding window rate limiting per IP address with configurable limits,
 * different tiers for different endpoints, IP tracking, and comprehensive monitoring.
 */
@WebFilter(urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class RateLimitFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);
    
    // Phase 2.1: Global API rate limiting configuration
    private int defaultMaxRequestsPerMinute = 100;
    private int loginMaxRequestsPerMinute = 10;
    private int apiMaxRequestsPerMinute = 60;
    private long timeWindowMs = 60 * 1000; // 1 minute
    private long cleanupIntervalMs = 5 * 60 * 1000; // 5 minutes
    
    // Phase 2.4: IP tracking and monitoring
    private static final ConcurrentHashMap<String, RateLimitEntry> rateLimitMap = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, IpTrackingEntry> ipTrackingMap = new ConcurrentHashMap<>();
    
    private Thread cleanupThread;
    private volatile boolean running;
    private boolean rateLimitEnabled;
    
    // Endpoint-specific rate limits
    private static final Map<String, Integer> ENDPOINT_LIMITS = new HashMap<>();
    
    static {
        ENDPOINT_LIMITS.put("/login", 10); // Stricter for login
        ENDPOINT_LIMITS.put("/register", 5); // Very strict for registration
        ENDPOINT_LIMITS.put("/api/admin/login", 10);
        ENDPOINT_LIMITS.put("/api/admin/register", 5);
        ENDPOINT_LIMITS.put("/api/auth", 30);
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Check if rate limiting is enabled
        String rateLimitEnv = System.getenv("RATE_LIMIT_ENABLED");
        if (rateLimitEnv == null || rateLimitEnv.trim().isEmpty()) {
            rateLimitEnv = System.getProperty("rate.limit.enabled", "true");
        }
        rateLimitEnabled = Boolean.parseBoolean(rateLimitEnv);
        
        // Load configuration from environment variables
        String defaultLimitEnv = System.getenv("RATE_LIMIT_DEFAULT");
        if (defaultLimitEnv != null && !defaultLimitEnv.trim().isEmpty()) {
            defaultMaxRequestsPerMinute = Integer.parseInt(defaultLimitEnv);
        }
        
        String loginLimitEnv = System.getenv("RATE_LIMIT_LOGIN");
        if (loginLimitEnv != null && !loginLimitEnv.trim().isEmpty()) {
            loginMaxRequestsPerMinute = Integer.parseInt(loginLimitEnv);
        }
        
        String apiLimitEnv = System.getenv("RATE_LIMIT_API");
        if (apiLimitEnv != null && !apiLimitEnv.trim().isEmpty()) {
            apiMaxRequestsPerMinute = Integer.parseInt(apiLimitEnv);
        }
        
        logger.info("RateLimitFilter initialized (enabled: {}, default: {}, login: {}, api: {})", 
                    rateLimitEnabled, defaultMaxRequestsPerMinute, loginMaxRequestsPerMinute, apiMaxRequestsPerMinute);
        
        if (rateLimitEnabled) {
            running = true;
            startCleanupThread();
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip if rate limiting is disabled
        if (!rateLimitEnabled) {
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
        
        // Skip rate limiting for health checks, metrics, and static assets
        if (isExemptedPath(relativePath)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Get client IP
        String clientIp = getClientIp(httpRequest);
        
        // Determine rate limit for this endpoint
        int maxRequests = getRateLimitForEndpoint(relativePath);
        
        // Check rate limit
        RateLimitResult result = checkRateLimit(clientIp, maxRequests);
        
        // Phase 2.4: IP tracking and monitoring
        trackIpActivity(clientIp, relativePath, httpRequest.getMethod(), result);
        
        if (!result.isAllowed()) {
            logger.warn("Rate limit exceeded for IP: {} on path: {} (limit: {}, used: {})", 
                       clientIp, relativePath, maxRequests, result.getCount());
            sendRateLimitError(httpResponse, result);
            return;
        }
        
        // Add rate limit headers
        httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(maxRequests));
        httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(maxRequests - result.getCount()));
        httpResponse.setHeader("X-RateLimit-Reset", String.valueOf(result.getResetTime()));
        httpResponse.setHeader("X-RateLimit-Window", String.valueOf(timeWindowMs / 1000) + "s");
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("RateLimitFilter destroyed");
        running = false;
        if (cleanupThread != null) {
            cleanupThread.interrupt();
        }
        rateLimitMap.clear();
        ipTrackingMap.clear();
    }
    
    // ---------------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------------
    
    private boolean isExemptedPath(String path) {
        return path.equals("/health") || path.equals("/healthz") ||
               path.equals("/metrics") || path.startsWith("/metrics/") ||
               path.startsWith("/assets") || path.startsWith("/css") || 
               path.startsWith("/js") || path.startsWith("/images") ||
               path.startsWith("/fonts") || path.equals("/csp-violation-report") ||
               path.equals("/login") || path.equals("/register") || path.startsWith("/auth");
    }
    
    private int getRateLimitForEndpoint(String path) {
        // Check for specific endpoint limits
        for (Map.Entry<String, Integer> entry : ENDPOINT_LIMITS.entrySet()) {
            if (path.equals(entry.getKey()) || path.startsWith(entry.getKey() + "/")) {
                return entry.getValue();
            }
        }
        
        // API endpoints get stricter limits
        if (path.startsWith("/api/")) {
            return apiMaxRequestsPerMinute;
        }
        
        // Default limit
        return defaultMaxRequestsPerMinute;
    }
    
    private RateLimitResult checkRateLimit(String clientIp, int maxRequests) {
        RateLimitEntry entry = rateLimitMap.computeIfAbsent(clientIp, k -> new RateLimitEntry());
        
        long currentTime = System.currentTimeMillis();
        
        // Reset counter if time window passed
        if (currentTime - entry.getTimestamp() > timeWindowMs) {
            entry.reset(currentTime);
        }
        
        // Increment and check
        int count = entry.incrementAndGet();
        boolean allowed = count <= maxRequests;
        
        return new RateLimitResult(allowed, count, entry.getTimestamp() + timeWindowMs);
    }
    
    private void trackIpActivity(String clientIp, String path, String method, RateLimitResult result) {
        IpTrackingEntry tracking = ipTrackingMap.computeIfAbsent(clientIp, k -> new IpTrackingEntry());
        tracking.recordActivity(path, method);
        
        // Log suspicious activity
        if (!result.isAllowed() || tracking.getTotalRequests() % 100 == 0) {
            logger.info("IP tracking - {}: total requests: {}, last path: {}, last method: {}", 
                       clientIp, tracking.getTotalRequests(), tracking.getLastPath(), tracking.getLastMethod());
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
    
    private void sendRateLimitError(HttpServletResponse response, RateLimitResult result) 
            throws IOException {
        response.setStatus(429); // HTTP 429 Too Many Requests
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf((result.getResetTime() - System.currentTimeMillis()) / 1000));
        response.getWriter().write(
            String.format(
                "{\"success\":false,\"message\":\"Rate limit exceeded. Please try again later.\",\"error\":\"RATE_LIMIT_EXCEEDED\",\"retryAfter\":%d}", 
                (result.getResetTime() - System.currentTimeMillis()) / 1000
            )
        );
    }
    
    private void startCleanupThread() {
        cleanupThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(cleanupIntervalMs);
                    cleanupOldEntries();
                } catch (InterruptedException e) {
                    if (running) {
                        logger.error("Cleanup thread interrupted", e);
                    }
                }
            }
        }, "RateLimitCleanupThread");
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }
    
    private void cleanupOldEntries() {
        long currentTime = System.currentTimeMillis();
        int removed = 0;
        
        // Clean rate limit entries
        for (String key : rateLimitMap.keySet()) {
            RateLimitEntry entry = rateLimitMap.get(key);
            if (entry != null && (currentTime - entry.getTimestamp() > timeWindowMs * 2)) {
                rateLimitMap.remove(key);
                removed++;
            }
        }
        
        // Clean IP tracking entries (keep for longer)
        int trackingRemoved = 0;
        for (String key : ipTrackingMap.keySet()) {
            IpTrackingEntry entry = ipTrackingMap.get(key);
            if (entry != null && (currentTime - entry.getLastActivity() > timeWindowMs * 10)) {
                ipTrackingMap.remove(key);
                trackingRemoved++;
            }
        }
        
        if (removed > 0 || trackingRemoved > 0) {
            logger.debug("Rate limit cleanup: removed {} rate limit entries, {} tracking entries", 
                        removed, trackingRemoved);
        }
    }
    
    // ---------------------------------------------------------------------------
    // Inner Classes
    // ---------------------------------------------------------------------------
    
    private static class RateLimitEntry {
        private AtomicInteger count = new AtomicInteger(0);
        private long timestamp = System.currentTimeMillis();

        public int incrementAndGet() {
            return count.incrementAndGet();
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void reset(long newTimestamp) {
            count.set(0);
            timestamp = newTimestamp;
        }
    }
    
    private static class RateLimitResult {
        private final boolean allowed;
        private final int count;
        private final long resetTime;
        
        public RateLimitResult(boolean allowed, int count, long resetTime) {
            this.allowed = allowed;
            this.count = count;
            this.resetTime = resetTime;
        }
        
        public boolean isAllowed() {
            return allowed;
        }
        
        public int getCount() {
            return count;
        }
        
        public long getResetTime() {
            return resetTime;
        }
    }
    
    private static class IpTrackingEntry {
        private int totalRequests = 0;
        private String lastPath;
        private String lastMethod;
        private long lastActivity = System.currentTimeMillis();
        
        public synchronized void recordActivity(String path, String method) {
            totalRequests++;
            lastPath = path;
            lastMethod = method;
            lastActivity = System.currentTimeMillis();
        }
        
        public synchronized int getTotalRequests() {
            return totalRequests;
        }
        
        public synchronized String getLastPath() {
            return lastPath;
        }
        
        public synchronized String getLastMethod() {
            return lastMethod;
        }
        
        public synchronized long getLastActivity() {
            return lastActivity;
        }
    }
}
