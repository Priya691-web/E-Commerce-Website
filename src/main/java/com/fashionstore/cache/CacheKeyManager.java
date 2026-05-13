package com.fashionstore.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Centralized cache key management with consistency and validation
 * Provides type-safe key generation and pattern-based invalidation
 */
public class CacheKeyManager {

    private static final Logger logger = LoggerFactory.getLogger(CacheKeyManager.class);
    
    private static final String APP_PREFIX = "fashionstore";
    private static final String SEPARATOR = ":";
    
    // Key versioning for cache invalidation
    private static final AtomicLong KEY_VERSION = new AtomicLong(1);
    
    // Key patterns for different entity types
    public static final class Patterns {
        public static final String PRODUCT = APP_PREFIX + SEPARATOR + "product" + SEPARATOR + "*";
        public static final String USER = APP_PREFIX + SEPARATOR + "user" + SEPARATOR + "*";
        public static final String ORDER = APP_PREFIX + SEPARATOR + "order" + SEPARATOR + "*";
        public static final String CART = APP_PREFIX + SEPARATOR + "cart" + SEPARATOR + "*";
        public static final String COUPON = APP_PREFIX + SEPARATOR + "coupon" + SEPARATOR + "*";
        public static final String CATEGORY = APP_PREFIX + SEPARATOR + "category" + SEPARATOR + "*";
        public static final String SEARCH = APP_PREFIX + SEPARATOR + "search" + SEPARATOR + "*";
        public static final String STATS = APP_PREFIX + SEPARATOR + "stats" + SEPARATOR + "*";
        public static final String SESSION = APP_PREFIX + SEPARATOR + "session" + SEPARATOR + "*";
    }

    /**
     * Generate cache key for product
     * @param productId Product ID
     * @return Cache key
     */
    public static String product(int productId) {
        validateId(productId, "product");
        return APP_PREFIX + SEPARATOR + "product" + SEPARATOR + productId;
    }

    /**
     * Generate cache key for user
     * @param userId User ID
     * @return Cache key
     */
    public static String user(int userId) {
        validateId(userId, "user");
        return APP_PREFIX + SEPARATOR + "user" + SEPARATOR + userId;
    }

    /**
     * Generate cache key for order
     * @param orderId Order ID
     * @return Cache key
     */
    public static String order(int orderId) {
        validateId(orderId, "order");
        return APP_PREFIX + SEPARATOR + "order" + SEPARATOR + orderId;
    }

    /**
     * Generate cache key for cart
     * @param userId User ID
     * @return Cache key
     */
    public static String cart(int userId) {
        validateId(userId, "cart");
        return APP_PREFIX + SEPARATOR + "cart" + SEPARATOR + userId;
    }

    /**
     * Generate cache key for coupon
     * @param couponCode Coupon code
     * @return Cache key
     */
    public static String coupon(String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Coupon code cannot be null or empty");
        }
        return APP_PREFIX + SEPARATOR + "coupon" + SEPARATOR + couponCode.toLowerCase().trim();
    }

    /**
     * Generate cache key for category
     * @param categoryId Category ID
     * @return Cache key
     */
    public static String category(int categoryId) {
        validateId(categoryId, "category");
        return APP_PREFIX + SEPARATOR + "category" + SEPARATOR + categoryId;
    }

    /**
     * Generate cache key for search results
     * @param query Search query
     * @param page Page number
     * @return Cache key
     */
    public static String search(String query, int page) {
        if (query == null) {
            query = "";
        }
        validateId(page, "search page");
        String normalizedQuery = query.toLowerCase().trim().replaceAll("\\s+", "_");
        return APP_PREFIX + SEPARATOR + "search" + SEPARATOR + normalizedQuery + SEPARATOR + page;
    }

    /**
     * Generate cache key for statistics
     * @param statType Type of statistic
     * @param parameters Additional parameters
     * @return Cache key
     */
    public static String stats(String statType, String... parameters) {
        if (statType == null || statType.trim().isEmpty()) {
            throw new IllegalArgumentException("Stat type cannot be null or empty");
        }
        
        StringBuilder key = new StringBuilder(APP_PREFIX + SEPARATOR + "stats" + SEPARATOR + statType.toLowerCase().trim());
        for (String param : parameters) {
            if (param != null) {
                key.append(SEPARATOR).append(param.trim().toLowerCase().replaceAll("\\s+", "_"));
            }
        }
        return key.toString();
    }

    /**
     * Generate cache key for session data
     * @param sessionId Session ID
     * @param dataType Type of session data
     * @return Cache key
     */
    public static String session(String sessionId, String dataType) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Session ID cannot be null or empty");
        }
        if (dataType == null || dataType.trim().isEmpty()) {
            throw new IllegalArgumentException("Data type cannot be null or empty");
        }
        return APP_PREFIX + SEPARATOR + "session" + SEPARATOR + sessionId + SEPARATOR + dataType.toLowerCase().trim();
    }

    /**
     * Generate cache key with custom namespace
     * @param namespace Custom namespace
     * @param keyParts Key parts
     * @return Cache key
     */
    public static String custom(String namespace, String... keyParts) {
        if (namespace == null || namespace.trim().isEmpty()) {
            throw new IllegalArgumentException("Namespace cannot be null or empty");
        }
        
        StringBuilder key = new StringBuilder(APP_PREFIX + SEPARATOR + namespace.toLowerCase().trim());
        for (String part : keyParts) {
            if (part != null && !part.trim().isEmpty()) {
                key.append(SEPARATOR).append(part.trim().toLowerCase().replaceAll("\\s+", "_"));
            }
        }
        return key.toString();
    }

    /**
     * Generate versioned cache key
     * @param baseKey Base key
     * @return Versioned cache key
     */
    public static String versioned(String baseKey) {
        if (baseKey == null || baseKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Base key cannot be null or empty");
        }
        return baseKey + SEPARATOR + "v" + KEY_VERSION.get();
    }

    /**
     * Increment key version (invalidates all versioned keys)
     */
    public static void incrementVersion() {
        long newVersion = KEY_VERSION.incrementAndGet();
        logger.info("Cache key version incremented to: {}", newVersion);
    }

    /**
     * Get current key version
     * @return Current version
     */
    public static long getCurrentVersion() {
        return KEY_VERSION.get();
    }

    /**
     * Validate cache key format
     * @param key Cache key to validate
     * @return true if valid
     */
    public static boolean isValidKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // Check if key starts with app prefix
        if (!key.startsWith(APP_PREFIX + SEPARATOR)) {
            return false;
        }
        
        // Check for invalid characters
        return key.matches("^[a-zA-Z0-9:_-]+$");
    }

    /**
     * Extract entity type from cache key
     * @param key Cache key
     * @return Entity type or null if invalid
     */
    public static String extractEntityType(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        
        String[] parts = key.split(SEPARATOR);
        if (parts.length >= 2) {
            return parts[1]; // Entity type is second part
        }
        
        return null;
    }

    /**
     * Extract entity ID from cache key
     * @param key Cache key
     * @return Entity ID or null if invalid
     */
    public static String extractEntityId(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        
        String[] parts = key.split(SEPARATOR);
        if (parts.length >= 3) {
            return parts[2]; // Entity ID is third part
        }
        
        return null;
    }

    /**
     * Validate numeric ID
     * @param id ID to validate
     * @param entity Entity name for error message
     */
    private static void validateId(int id, String entity) {
        if (id <= 0) {
            throw new IllegalArgumentException(entity + " ID must be positive: " + id);
        }
    }

    /**
     * Generate pattern for invalidating all keys for an entity type
     * @param entityType Entity type
     * @return Pattern string
     */
    public static String getInvalidationPattern(String entityType) {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type cannot be null or empty");
        }
        
        return APP_PREFIX + SEPARATOR + entityType.toLowerCase().trim() + SEPARATOR + "*";
    }
}
