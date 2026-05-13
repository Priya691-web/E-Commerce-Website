package com.fashionstore.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.fashionstore.util.IdempotencyKeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Concurrency control filter for preventing duplicate operations
 * Implements idempotency keys and request deduplication
 */
public class ConcurrencyControlFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(ConcurrencyControlFilter.class);
    
    // Idempotency key storage
    private final Map<String, ProcessedRequest> processedRequests = new ConcurrentHashMap<>();
    
    // Request deduplication for critical operations
    private final Map<String, RequestLock> requestLocks = new ConcurrentHashMap<>();
    
    // Critical paths that require concurrency control
    private static final Set<String> CRITICAL_PATHS = Set.of(
        "/api/order/submit",
        "/api/payment/process",
        "/api/cart/add",
        "/api/cart/update",
        "/api/inventory/update",
        "/api/user/register",
        "/api/user/update"
    );
    
    // Paths that require inventory locking
    private static final Set<String> INVENTORY_LOCK_PATHS = Set.of(
        "/api/order/submit",
        "/api/cart/add",
        "/api/cart/update",
        "/api/inventory/update"
    );
    
    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("ConcurrencyControlFilter initialized");
        // Start cleanup thread for expired locks
        startCleanupThread();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // Apply concurrency control measures
            if (!applyConcurrencyControl(httpRequest, httpResponse)) {
                return; // Concurrency control blocked the request
            }
            
            // Continue with the request
            chain.doFilter(request, response);
            
            // Mark request as successfully processed
            markRequestProcessed(httpRequest);
            
        } catch (Exception e) {
            logger.error("Concurrency control error: {}", e.getMessage(), e);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            // Always release locks
            releaseRequestLocks(httpRequest);
        }
    }
    
    /**
     * Apply concurrency control measures
     */
    private boolean applyConcurrencyControl(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // Only apply to critical paths
        if (!isCriticalPath(path, method)) {
            return true;
        }
        
        // 1. Check idempotency key
        if (!checkIdempotencyKey(request, response)) {
            return false;
        }
        
        // 2. Apply request deduplication
        if (!applyRequestDeduplication(request, response)) {
            return false;
        }
        
        // 3. Apply inventory locking for relevant paths
        if (INVENTORY_LOCK_PATHS.stream().anyMatch(path::contains)) {
            if (!applyInventoryLocking(request, response)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Check if this is a critical path that needs concurrency control
     */
    private boolean isCriticalPath(String path, String method) {
        // Only POST, PUT, PATCH, DELETE operations
        if (!Set.of("POST", "PUT", "PATCH", "DELETE").contains(method)) {
            return false;
        }
        
        return CRITICAL_PATHS.stream().anyMatch(path::contains);
    }
    
    /**
     * Check idempotency key to prevent duplicate requests
     */
    private boolean checkIdempotencyKey(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String idempotencyKey = request.getHeader("Idempotency-Key");
        
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            // Generate idempotency key for critical operations
            idempotencyKey = IdempotencyKeyGenerator.generate();
            request.setAttribute("generatedIdempotencyKey", idempotencyKey);
        }
        
        // Check if this request was already processed
        ProcessedRequest processed = processedRequests.get(idempotencyKey);
        if (processed != null) {
            if (processed.isCompleted()) {
                // Return the cached response
                logger.info("Duplicate request detected with idempotency key: {}", idempotencyKey);
                sendCachedResponse(response, processed);
                return false;
            } else if (processed.isInProgress()) {
                // Request is still being processed
                logger.info("Concurrent request detected with idempotency key: {}", idempotencyKey);
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.getWriter().write("{\"error\":\"Request already in progress\"}");
                return false;
            }
        }
        
        // Mark request as in progress
        processedRequests.put(idempotencyKey, new ProcessedRequest(idempotencyKey));
        
        // Store idempotency key in request for later use
        request.setAttribute("idempotencyKey", idempotencyKey);
        
        return true;
    }
    
    /**
     * Apply request deduplication for critical operations
     */
    private boolean applyRequestDeduplication(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String lockKey = generateLockKey(request);
        RequestLock lock = requestLocks.computeIfAbsent(lockKey, k -> new RequestLock());
        
        if (!lock.tryLock()) {
            logger.warn("Request deduplication blocked request for key: {}", lockKey);
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"Request already being processed\"}");
            return false;
        }
        
        // Store lock reference for cleanup
        request.setAttribute("requestLock", lock);
        
        return true;
    }
    
    /**
     * Apply inventory locking for stock-sensitive operations
     */
    private boolean applyInventoryLocking(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Extract product IDs from request
        Set<String> productIDs = extractProductIDs(request);
        
        if (productIDs.isEmpty()) {
            return true; // No inventory locking needed
        }
        
        // Try to acquire locks for all products
        List<InventoryLock> acquiredLocks = new ArrayList<>();
        
        try {
            for (String productID : productIDs) {
                InventoryLock inventoryLock = acquireInventoryLock(productID);
                if (inventoryLock != null) {
                    acquiredLocks.add(inventoryLock);
                } else {
                    // Failed to acquire lock, release all acquired locks
                    releaseInventoryLocks(acquiredLocks);
                    logger.warn("Inventory lock failed for product: {}", productID);
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    response.getWriter().write("{\"error\":\"Product is being updated by another request\"}");
                    return false;
                }
            }
            
            // Store locks for cleanup
            request.setAttribute("inventoryLocks", acquiredLocks);
            
            return true;
            
        } catch (Exception e) {
            // Release any acquired locks on error
            releaseInventoryLocks(acquiredLocks);
            throw e;
        }
    }
    
    /**
     * Extract product IDs from request
     */
    private Set<String> extractProductIDs(HttpServletRequest request) {
        Set<String> productIDs = new HashSet<>();
        
        // Extract from query parameters
        String productIDParam = request.getParameter("productId");
        if (productIDParam != null) {
            productIDs.add(productIDParam);
        }
        
        // Extract from request body (simplified - in real implementation would parse JSON)
        String body = getRequestBody(request);
        if (body != null && body.contains("productId")) {
            // Simple regex extraction - in production, use proper JSON parsing
            String[] matches = body.split("\"productId\":\"([^\"]+)\"");
            for (int i = 1; i < matches.length; i += 2) {
                productIDs.add(matches[i]);
            }
        }
        
        return productIDs;
    }
    
    /**
     * Get request body (simplified implementation)
     */
    private String getRequestBody(HttpServletRequest request) {
        // In a real implementation, this would read the request body stream
        // For now, return null as this is a placeholder
        return null;
    }
    
    /**
     * Generate lock key for request deduplication
     */
    private String generateLockKey(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String userID = getUserID(request);
        
        return method + ":" + path + ":" + userID;
    }
    
    /**
     * Get user ID from request
     */
    private String getUserID(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object user = session.getAttribute("user");
            if (user != null) {
                return String.valueOf(user.hashCode());
            }
        }
        return "anonymous";
    }
    
    /**
     * Acquire inventory lock for a product
     */
    private InventoryLock acquireInventoryLock(String productID) {
        String lockKey = "inventory:" + productID;
        RequestLock lock = requestLocks.computeIfAbsent(lockKey, k -> new RequestLock());
        
        if (lock.tryLock()) {
            return new InventoryLock(productID, lock);
        }
        
        return null;
    }
    
    /**
     * Release inventory locks
     */
    private void releaseInventoryLocks(List<InventoryLock> locks) {
        if (locks != null) {
            for (InventoryLock inventoryLock : locks) {
                inventoryLock.release();
            }
        }
    }
    
    /**
     * Release request locks
     */
    private void releaseRequestLocks(HttpServletRequest request) {
        // Release request deduplication lock
        RequestLock requestLock = (RequestLock) request.getAttribute("requestLock");
        if (requestLock != null) {
            requestLock.unlock();
        }
        
        // Release inventory locks
        @SuppressWarnings("unchecked")
        List<InventoryLock> inventoryLocks = (List<InventoryLock>) request.getAttribute("inventoryLocks");
        releaseInventoryLocks(inventoryLocks);
    }
    
    /**
     * Mark request as successfully processed
     */
    private void markRequestProcessed(HttpServletRequest request) {
        String idempotencyKey = (String) request.getAttribute("idempotencyKey");
        if (idempotencyKey != null) {
            ProcessedRequest processed = processedRequests.get(idempotencyKey);
            if (processed != null) {
                processed.markCompleted();
            }
        }
    }
    
    /**
     * Send cached response for duplicate request
     */
    private void sendCachedResponse(HttpServletResponse response, ProcessedRequest processed) 
            throws IOException {
        
        response.setStatus(processed.getStatusCode());
        response.setContentType(processed.getContentType());
        
        String responseBody = processed.getResponseBody();
        if (responseBody != null) {
            response.getWriter().write(responseBody);
        }
    }
    
    /**
     * Start cleanup thread for expired locks
     */
    private void startCleanupThread() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000); // Clean up every 30 seconds
                    cleanupExpiredLocks();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        
        cleanupThread.setDaemon(true);
        cleanupThread.setName("ConcurrencyControlCleanup");
        cleanupThread.start();
    }
    
    /**
     * Clean up expired locks
     */
    private void cleanupExpiredLocks() {
        long now = System.currentTimeMillis();
        
        // Clean up processed requests
        processedRequests.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(now));
        
        // Clean up request locks
        requestLocks.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(now));
    }
    
    /**
     * Processed request information
     */
    private static class ProcessedRequest {
        private final String idempotencyKey;
        private volatile boolean inProgress = true;
        private volatile boolean completed = false;
        private volatile long timestamp = System.currentTimeMillis();
        private volatile int statusCode = 200;
        private volatile String contentType = "application/json";
        private volatile String responseBody;
        
        public ProcessedRequest(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
        }
        
        public boolean isInProgress() {
            return inProgress;
        }
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void markCompleted() {
            this.inProgress = false;
            this.completed = true;
        }
        
        public boolean isExpired(long now) {
            return now - timestamp > 300000; // Expire after 5 minutes
        }
        
        public int getStatusCode() {
            return statusCode;
        }
        
        public String getContentType() {
            return contentType;
        }
        
        public String getResponseBody() {
            return responseBody;
        }
    }
    
    /**
     * Request lock for deduplication
     */
    private static class RequestLock {
        private final ReentrantLock lock = new ReentrantLock();
        private volatile long timestamp = System.currentTimeMillis();
        
        public boolean tryLock() {
            return lock.tryLock();
        }
        
        public void unlock() {
            lock.unlock();
        }
        
        public boolean isExpired(long now) {
            return now - timestamp > 60000; // Expire after 1 minute
        }
    }
    
    /**
     * Inventory lock wrapper
     */
    private static class InventoryLock {
        private final String productID;
        private final RequestLock lock;
        
        public InventoryLock(String productID, RequestLock lock) {
            this.productID = productID;
            this.lock = lock;
        }
        
        public void release() {
            lock.unlock();
        }
        
        public String getProductID() {
            return productID;
        }
    }
    
    @Override
    public void destroy() {
        logger.info("ConcurrencyControlFilter destroyed");
        // Cleanup resources
        processedRequests.clear();
        requestLocks.clear();
    }
}
