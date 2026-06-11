package com.fashionstore.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BruteForceProtectionFilter - Protects against brute force authentication attacks
 * 
 * ROOT CAUSE: Without brute force protection, attackers can repeatedly attempt to guess credentials,
 * leading to account compromise, credential stuffing, and authentication service degradation.
 * 
 * FIX: This filter tracks failed authentication attempts per IP and per user, implementing
 * progressive delays and temporary blocking to prevent automated brute force attacks.
 */
@WebFilter(urlPatterns = {"/login", "/register", "/api/admin/login", "/api/admin/register", "/api/auth/*"}, 
           dispatcherTypes = {DispatcherType.REQUEST})
public class BruteForceProtectionFilter implements Filter {
    
    private static final Logger logger = LoggerFactory.getLogger(BruteForceProtectionFilter.class);
    
    // Phase 2.3: Brute-force attack protection configuration
    private static final int MAX_FAILED_ATTEMPTS_PER_IP = 5;
    private static final int MAX_FAILED_ATTEMPTS_PER_USER = 3;
    private static final long IP_BLOCK_DURATION_MS = 15 * 60 * 1000; // 15 minutes
    private static final long USER_BLOCK_DURATION_MS = 30 * 60 * 1000; // 30 minutes
    private static final long ATTEMPT_WINDOW_MS = 5 * 60 * 1000; // 5 minutes window
    
    // Tracking storage
    private static final ConcurrentHashMap<String, FailedAttemptEntry> ipAttempts = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, FailedAttemptEntry> userAttempts = new ConcurrentHashMap<>();
    
    private boolean bruteForceProtectionEnabled;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Check if brute force protection is enabled
        String bfEnv = System.getenv("BRUTE_FORCE_PROTECTION_ENABLED");
        if (bfEnv == null || bfEnv.trim().isEmpty()) {
            bfEnv = System.getProperty("brute.force.protection.enabled", "true");
        }
        bruteForceProtectionEnabled = Boolean.parseBoolean(bfEnv);
        
        logger.info("BruteForceProtectionFilter initialized (enabled: {})", bruteForceProtectionEnabled);
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Skip if brute force protection is disabled
        if (!bruteForceProtectionEnabled) {
            chain.doFilter(request, response);
            return;
        }
        
        // Skip internal dispatcher forwards/includes/errors
        DispatcherType dt = httpRequest.getDispatcherType();
        if (dt == DispatcherType.FORWARD || dt == DispatcherType.INCLUDE || dt == DispatcherType.ERROR) {
            chain.doFilter(request, response);
            return;
        }
        
        String clientIp = getClientIp(httpRequest);
        String path = httpRequest.getRequestURI();
        
        // Check IP-based blocking
        if (isIpBlocked(clientIp)) {
            logger.warn("Blocked brute force attempt from IP: {} on path: {}", clientIp, path);
            sendBruteForceError(httpResponse, "IP temporarily blocked due to too many failed attempts", 
                              IP_BLOCK_DURATION_MS);
            return;
        }
        
        // Extract user identifier (email or username) from request if available
        String userId = extractUserId(httpRequest);
        
        // Check user-based blocking
        if (userId != null && isUserBlocked(userId)) {
            logger.warn("Blocked brute force attempt for user: {} from IP: {}", userId, clientIp);
            sendBruteForceError(httpResponse, "Account temporarily locked due to too many failed attempts", 
                              USER_BLOCK_DURATION_MS);
            return;
        }
        
        // Add tracking attribute for later use by authentication controllers
        httpRequest.setAttribute("bruteForceProtectionEnabled", true);
        httpRequest.setAttribute("clientIp", clientIp);
        if (userId != null) {
            httpRequest.setAttribute("userId", userId);
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        logger.info("BruteForceProtectionFilter destroyed");
        ipAttempts.clear();
        userAttempts.clear();
    }
    
    // ---------------------------------------------------------------------------
    // Public API for Authentication Controllers
    // ---------------------------------------------------------------------------
    
    /**
     * Record a failed authentication attempt
     * Called by authentication controllers when login fails
     */
    public static void recordFailedAttempt(HttpServletRequest request, String userId) {
        String clientIp = getClientIpStatic(request);
        
        // Record IP attempt
        FailedAttemptEntry ipEntry = ipAttempts.computeIfAbsent(clientIp, k -> new FailedAttemptEntry());
        ipEntry.recordAttempt();
        
        logger.warn("Failed authentication attempt recorded - IP: {}, total failures: {}", 
                   clientIp, ipEntry.getAttemptCount());
        
        // Record user attempt if userId provided
        if (userId != null && !userId.trim().isEmpty()) {
            FailedAttemptEntry userEntry = userAttempts.computeIfAbsent(userId, k -> new FailedAttemptEntry());
            userEntry.recordAttempt();
            
            logger.warn("Failed authentication attempt recorded - User: {}, total failures: {}", 
                       userId, userEntry.getAttemptCount());
        }
    }
    
    /**
     * Record a successful authentication attempt
     * Called by authentication controllers when login succeeds
     */
    public static void recordSuccessfulAttempt(HttpServletRequest request, String userId) {
        String clientIp = getClientIpStatic(request);
        
        // Clear IP attempts on success
        ipAttempts.remove(clientIp);
        
        // Clear user attempts on success
        if (userId != null && !userId.trim().isEmpty()) {
            userAttempts.remove(userId);
        }
        
        logger.info("Successful authentication - clearing failed attempt records for IP: {}, User: {}", 
                   clientIp, userId);
    }
    
    /**
     * Check if an IP is currently blocked
     */
    public static boolean isIpBlocked(String clientIp) {
        FailedAttemptEntry entry = ipAttempts.get(clientIp);
        if (entry == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Reset if window expired
        if (currentTime - entry.getFirstAttemptTime() > ATTEMPT_WINDOW_MS) {
            ipAttempts.remove(clientIp);
            return false;
        }
        
        // Check if blocked
        return entry.getAttemptCount() >= MAX_FAILED_ATTEMPTS_PER_IP && 
               (currentTime - entry.getLastAttemptTime() < IP_BLOCK_DURATION_MS);
    }
    
    /**
     * Check if a user is currently blocked
     */
    public static boolean isUserBlocked(String userId) {
        FailedAttemptEntry entry = userAttempts.get(userId);
        if (entry == null) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        
        // Reset if window expired
        if (currentTime - entry.getFirstAttemptTime() > ATTEMPT_WINDOW_MS) {
            userAttempts.remove(userId);
            return false;
        }
        
        // Check if blocked
        return entry.getAttemptCount() >= MAX_FAILED_ATTEMPTS_PER_USER && 
               (currentTime - entry.getLastAttemptTime() < USER_BLOCK_DURATION_MS);
    }
    
    // ---------------------------------------------------------------------------
    // Helper Methods
    // ---------------------------------------------------------------------------
    
    private String getClientIp(HttpServletRequest request) {
        return getClientIpStatic(request);
    }
    
    private static String getClientIpStatic(HttpServletRequest request) {
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
    
    private String extractUserId(HttpServletRequest request) {
        // Try to get email from request parameters (for login endpoints)
        String email = request.getParameter("email");
        if (email != null && !email.trim().isEmpty()) {
            return email.toLowerCase();
        }
        
        // Try to get username from request parameters
        String username = request.getParameter("username");
        if (username != null && !username.trim().isEmpty()) {
            return username.toLowerCase();
        }
        
        return null;
    }
    
    private void sendBruteForceError(HttpServletResponse response, String message, long blockDuration) 
            throws IOException {
        response.setStatus(429); // HTTP 429 Too Many Requests
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Retry-After", String.valueOf(blockDuration / 1000));
        response.getWriter().write(
            String.format(
                "{\"success\":false,\"message\":\"%s\",\"error\":\"TOO_MANY_ATTEMPTS\",\"retryAfter\":%d}", 
                message, blockDuration / 1000
            )
        );
    }
    
    // ---------------------------------------------------------------------------
    // Inner Classes
    // ---------------------------------------------------------------------------
    
    private static class FailedAttemptEntry {
        private AtomicInteger attemptCount = new AtomicInteger(0);
        private long firstAttemptTime = System.currentTimeMillis();
        private long lastAttemptTime = System.currentTimeMillis();
        
        public synchronized void recordAttempt() {
            long currentTime = System.currentTimeMillis();
            
            // Reset if window expired
            if (currentTime - firstAttemptTime > ATTEMPT_WINDOW_MS) {
                attemptCount.set(0);
                firstAttemptTime = currentTime;
            }
            
            attemptCount.incrementAndGet();
            lastAttemptTime = currentTime;
        }
        
        public synchronized int getAttemptCount() {
            // Reset if window expired
            if (System.currentTimeMillis() - firstAttemptTime > ATTEMPT_WINDOW_MS) {
                attemptCount.set(0);
                firstAttemptTime = System.currentTimeMillis();
            }
            return attemptCount.get();
        }
        
        public synchronized long getFirstAttemptTime() {
            return firstAttemptTime;
        }
        
        public synchronized long getLastAttemptTime() {
            return lastAttemptTime;
        }
    }
}
