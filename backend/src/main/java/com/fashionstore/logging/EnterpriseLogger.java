package com.fashionstore.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enterprise-grade structured logger with correlation IDs, security filtering, and performance optimization
 * Provides centralized logging with structured data, security compliance, and monitoring integration
 */
public class EnterpriseLogger {

    private static final Map<String, EnterpriseLogger> loggers = new ConcurrentHashMap<>();
    private final Logger logger;
    private final String className;
    
    // Sensitive data patterns to filter from logs
    private static final String[] SENSITIVE_PATTERNS = {
        "password", "pwd", "secret", "token", "key", "credit", "card", "ssn", "social_security",
        "api_key", "access_token", "refresh_token", "authorization", "bearer", "basic_auth"
    };
    
    private EnterpriseLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.className = clazz.getSimpleName();
    }
    
    /**
     * Get or create enterprise logger for a class
     */
    public static EnterpriseLogger getLogger(Class<?> clazz) {
        return loggers.computeIfAbsent(clazz.getName(), k -> new EnterpriseLogger(clazz));
    }
    
    /**
     * Log authentication events with structured data
     */
    public void logAuthentication(String eventType, String userId, String email, boolean success, String reason, Map<String, Object> metadata) {
        MDC.put("event", "AUTHENTICATION");
        MDC.put("eventType", eventType);
        MDC.put("success", String.valueOf(success));
        MDC.put("timestamp", Instant.now().toString());
        
        if (userId != null) MDC.put("userId", sanitize(userId));
        if (email != null) MDC.put("email", sanitizeEmail(email));
        if (reason != null) MDC.put("reason", sanitize(reason));
        
        addMetadata(metadata);
        
        if (success) {
            logger.info("Authentication successful - type={}, userId={}, email={}", eventType, userId, maskEmail(email));
        } else {
            logger.warn("Authentication failed - type={}, userId={}, email={}, reason={}", eventType, userId, maskEmail(email), reason);
        }
        
        clearMDC();
    }
    
    /**
     * Log payment operations with detailed tracking
     */
    public void logPayment(String eventType, String orderId, String paymentMethod, double amount, String status, Map<String, Object> metadata) {
        MDC.put("event", "PAYMENT");
        MDC.put("eventType", eventType);
        MDC.put("orderId", sanitize(orderId));
        MDC.put("paymentMethod", sanitize(paymentMethod));
        MDC.put("amount", String.valueOf(amount));
        MDC.put("status", sanitize(status));
        MDC.put("timestamp", Instant.now().toString());
        
        addMetadata(metadata);
        
        if ("success".equalsIgnoreCase(status) || "completed".equalsIgnoreCase(status)) {
            logger.info("Payment successful - orderId={}, method={}, amount={}, status={}", orderId, paymentMethod, amount, status);
        } else {
            logger.error("Payment failed - orderId={}, method={}, amount={}, status={}", orderId, paymentMethod, amount, status);
        }
        
        clearMDC();
    }
    
    /**
     * Log admin actions with audit trail
     */
    public void logAdminAction(String action, String target, Long userId, String details, Map<String, Object> metadata) {
        MDC.put("event", "ADMIN_ACTION");
        MDC.put("action", sanitize(action));
        MDC.put("target", sanitize(target));
        MDC.put("timestamp", Instant.now().toString());
        
        if (userId != null) MDC.put("userId", sanitize(userId.toString()));
        if (details != null) MDC.put("details", sanitize(details));
        
        addMetadata(metadata);
        
        logger.info("Admin action performed - action={}, target={}, userId={}, details={}", action, target, userId, details);
        
        clearMDC();
    }
    
    /**
     * Log inventory updates with change tracking
     */
    public void logInventoryUpdate(String eventType, int productId, String productName, int oldStock, int newStock, String reason, Map<String, Object> metadata) {
        MDC.put("event", "INVENTORY");
        MDC.put("eventType", eventType);
        MDC.put("productId", String.valueOf(productId));
        MDC.put("productName", sanitize(productName));
        MDC.put("oldStock", String.valueOf(oldStock));
        MDC.put("newStock", String.valueOf(newStock));
        MDC.put("change", String.valueOf(newStock - oldStock));
        MDC.put("reason", sanitize(reason));
        MDC.put("timestamp", Instant.now().toString());
        
        addMetadata(metadata);
        
        logger.info("Inventory updated - productId={}, product={}, old={}, new={}, change={}, reason={}", 
                   productId, productName, oldStock, newStock, newStock - oldStock, reason);
        
        clearMDC();
    }
    
    /**
     * Log failed requests with error details
     */
    public void logFailedRequest(String endpoint, String method, int statusCode, String errorMessage, long duration, Map<String, Object> metadata) {
        MDC.put("event", "FAILED_REQUEST");
        MDC.put("endpoint", sanitize(endpoint));
        MDC.put("method", sanitize(method));
        MDC.put("statusCode", String.valueOf(statusCode));
        MDC.put("duration", String.valueOf(duration));
        MDC.put("timestamp", Instant.now().toString());
        
        if (errorMessage != null) MDC.put("errorMessage", sanitize(errorMessage));
        
        addMetadata(metadata);
        
        logger.error("Request failed - endpoint={}, method={}, status={}, duration={}ms, error={}", 
                   endpoint, method, statusCode, duration, errorMessage);
        
        clearMDC();
    }
    
    /**
     * Log performance metrics
     */
    public void logPerformance(String operation, long duration, boolean success, Map<String, Object> metadata) {
        MDC.put("event", "PERFORMANCE");
        MDC.put("operation", sanitize(operation));
        MDC.put("duration", String.valueOf(duration));
        MDC.put("success", String.valueOf(success));
        MDC.put("timestamp", Instant.now().toString());
        
        addMetadata(metadata);
        
        if (duration > 1000) {
            logger.warn("Slow operation detected - operation={}, duration={}ms, success={}", operation, duration, success);
        } else {
            logger.info("Performance metric - operation={}, duration={}ms, success={}", operation, duration, success);
        }
        
        clearMDC();
    }
    
    /**
     * Log business events
     */
    public void logBusinessEvent(String eventType, String entityId, String entityType, String description, Map<String, Object> metadata) {
        MDC.put("event", "BUSINESS");
        MDC.put("eventType", sanitize(eventType));
        MDC.put("entityId", sanitize(entityId));
        MDC.put("entityType", sanitize(entityType));
        MDC.put("timestamp", Instant.now().toString());
        
        if (description != null) MDC.put("description", sanitize(description));
        
        addMetadata(metadata);
        
        logger.info("Business event - type={}, entityType={}, entityId={}, description={}", 
                   eventType, entityType, entityId, description);
        
        clearMDC();
    }
    
    /**
     * Log security events
     */
    public void logSecurityEvent(String eventType, String severity, String userId, String source, String description, Map<String, Object> metadata) {
        MDC.put("event", "SECURITY");
        MDC.put("eventType", sanitize(eventType));
        MDC.put("severity", sanitize(severity));
        MDC.put("source", sanitize(source));
        MDC.put("timestamp", Instant.now().toString());
        
        if (userId != null) MDC.put("userId", sanitize(userId.toString()));
        if (description != null) MDC.put("description", sanitize(description));
        
        addMetadata(metadata);
        
        // Security events always use WARN or ERROR level
        if ("HIGH".equalsIgnoreCase(severity) || "CRITICAL".equalsIgnoreCase(severity)) {
            logger.error("Security event - type={}, severity={}, userId={}, source={}, description={}", 
                        eventType, severity, userId, source, description);
        } else {
            logger.warn("Security event - type={}, severity={}, userId={}, source={}, description={}", 
                      eventType, severity, userId, source, description);
        }
        
        clearMDC();
    }
    
    /**
     * Log database operations
     */
    public void logDatabaseOperation(String operation, String table, boolean success, long duration, String error, Map<String, Object> metadata) {
        MDC.put("event", "DATABASE");
        MDC.put("operation", sanitize(operation));
        MDC.put("table", sanitize(table));
        MDC.put("success", String.valueOf(success));
        MDC.put("duration", String.valueOf(duration));
        MDC.put("timestamp", Instant.now().toString());
        
        if (error != null) MDC.put("error", sanitize(error));
        
        addMetadata(metadata);
        
        if (success) {
            if (duration > 500) {
                logger.warn("Slow database operation - operation={}, table={}, duration={}ms", operation, table, duration);
            } else {
                logger.debug("Database operation - operation={}, table={}, duration={}ms", operation, table, duration);
            }
        } else {
            logger.error("Database operation failed - operation={}, table={}, duration={}ms, error={}", operation, table, duration, error);
        }
        
        clearMDC();
    }
    
    /**
     * Log cache operations
     */
    public void logCacheOperation(String operation, String key, boolean hit, long duration, Map<String, Object> metadata) {
        MDC.put("event", "CACHE");
        MDC.put("operation", sanitize(operation));
        MDC.put("key", sanitize(key));
        MDC.put("hit", String.valueOf(hit));
        MDC.put("duration", String.valueOf(duration));
        MDC.put("timestamp", Instant.now().toString());
        
        addMetadata(metadata);
        
        logger.debug("Cache operation - operation={}, key={}, hit={}, duration={}ms", operation, key, hit, duration);
        
        clearMDC();
    }
    
    /**
     * Add correlation ID to current context
     */
    public void setCorrelationId(String correlationId) {
        MDC.put("correlationId", correlationId);
    }
    
    /**
     * Add request ID to current context
     */
    public void setRequestId(String requestId) {
        MDC.put("requestId", requestId);
    }
    
    /**
     * Add user context
     */
    public void setUserContext(Long userId, String email, String sessionId) {
        if (userId != null) MDC.put("userId", sanitize(userId.toString()));
        if (email != null) MDC.put("email", maskEmail(email));
        if (sessionId != null) MDC.put("sessionId", sessionId);
    }
    
    /**
     * Clear logging context
     */
    public void clearContext() {
        MDC.clear();
    }
    
    // Helper methods
    
    private void addMetadata(Map<String, Object> metadata) {
        if (metadata != null) {
            metadata.forEach((key, value) -> {
                if (value != null) {
                    MDC.put(sanitize(key), sanitize(value.toString()));
                }
            });
        }
    }
    
    private void clearMDC() {
        MDC.clear();
    }
    
    /**
     * Sanitize sensitive data from log messages
     */
    private String sanitize(String input) {
        if (input == null) return null;
        
        String sanitized = input;
        
        // Remove or mask sensitive patterns
        for (String pattern : SENSITIVE_PATTERNS) {
            sanitized = sanitized.replaceAll("(?i)" + pattern + "[\\s]*[=:][\\s]*[^\\s,}]+", pattern + "=***");
        }
        
        return sanitized;
    }
    
    /**
     * Sanitize email addresses for logging
     */
    private String sanitizeEmail(String email) {
        if (email == null) return null;
        return maskEmail(email);
    }
    
    /**
     * Mask email addresses for privacy
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        
        String[] parts = email.split("@");
        if (parts.length != 2) return email;
        
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return username.charAt(0) + "***@" + domain;
        }
        
        return username.charAt(0) + "***" + username.charAt(username.length() - 1) + "@" + domain;
    }
    
    /**
     * Generate correlation ID
     */
    public static String generateCorrelationId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    /**
     * Generate request ID
     */
    public static String generateRequestId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }
}
