package com.fashionstore.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Cache key generator
 * Provides consistent, safe cache key generation
 */
public class CacheKeyGenerator {

    private static volatile CacheKeyGenerator instance;
    private static final String KEY_PREFIX = "fashionstore";
    private static final String KEY_SEPARATOR = ":";
    
    // Key versioning for cache invalidation
    private final AtomicLong keyVersion = new AtomicLong(1);
    
    // Key templates for consistency
    private final ConcurrentHashMap<String, String> keyTemplates = new ConcurrentHashMap<>();

    private CacheKeyGenerator() {
        initializeKeyTemplates();
    }

    public static CacheKeyGenerator getInstance() {
        if (instance == null) {
            synchronized (CacheKeyGenerator.class) {
                if (instance == null) {
                    instance = new CacheKeyGenerator();
                }
            }
        }
        return instance;
    }

    /**
     * Generate product cache key
     */
    public String generateProductKey(int productId) {
        return generateKey("product", String.valueOf(productId));
    }

    /**
     * Generate product list cache key
     */
    public String generateProductListKey(String category, int page, int limit) {
        return generateKey("products", category, "page", String.valueOf(page), "limit", String.valueOf(limit));
    }

    /**
     * Generate user cache key
     */
    public String generateUserKey(int userId) {
        return generateKey("user", String.valueOf(userId));
    }

    /**
     * Generate user profile cache key
     */
    public String generateUserProfileKey(int userId) {
        return generateKey("profile", String.valueOf(userId));
    }

    /**
     * Generate cart cache key
     */
    public String generateCartKey(int userId) {
        return generateKey("cart", String.valueOf(userId));
    }

    /**
     * Generate order cache key
     */
    public String generateOrderKey(int orderId) {
        return generateKey("order", String.valueOf(orderId));
    }

    /**
     * Generate user orders cache key
     */
    public String generateUserOrdersKey(int userId, String status, int page, int limit) {
        return generateKey("orders", String.valueOf(userId), status, "page", String.valueOf(page), "limit", String.valueOf(limit));
    }

    /**
     * Generate coupon cache key
     */
    public String generateCouponKey(String couponCode) {
        return generateKey("coupon", couponCode.toLowerCase());
    }

    /**
     * Generate coupon list cache key
     */
    public String generateCouponListKey(boolean activeOnly) {
        return generateKey("coupons", activeOnly ? "active" : "all");
    }

    /**
     * Generate category cache key
     */
    public String generateCategoryKey(int categoryId) {
        return generateKey("category", String.valueOf(categoryId));
    }

    /**
     * Generate category list cache key
     */
    public String generateCategoryListKey() {
        return generateKey("categories");
    }

    /**
     * Generate search cache key
     */
    public String generateSearchKey(String query, String category, String sortBy, int page, int limit) {
        String queryHash = hashQuery(query);
        return generateKey("search", queryHash, category, sortBy, "page", String.valueOf(page), "limit", String.valueOf(limit));
    }

    /**
     * Generate recommendation cache key
     */
    public String generateRecommendationKey(int userId, String type, int limit) {
        return generateKey("recommendations", String.valueOf(userId), type, "limit", String.valueOf(limit));
    }

    /**
     * Generate inventory cache key
     */
    public String generateInventoryKey(int productId, String size) {
        return generateKey("inventory", String.valueOf(productId), size);
    }

    /**
     * Generate session cache key
     */
    public String generateSessionKey(String sessionId) {
        return generateKey("session", sessionId);
    }

    /**
     * Generate permission cache key
     */
    public String generatePermissionKey(int userId, String resource) {
        return generateKey("permissions", String.valueOf(userId), resource);
    }

    /**
     * Generate rate limit cache key
     */
    public String generateRateLimitKey(String identifier, String window) {
        return generateKey("ratelimit", identifier, window);
    }

    /**
     * Generate analytics cache key
     */
    public String generateAnalyticsKey(String type, String period, String dimension) {
        return generateKey("analytics", type, period, dimension);
    }

    /**
     * Generate custom cache key
     */
    public String generateCustomKey(String namespace, Object... parts) {
        String[] stringParts = new String[parts.length];
        for (int i = 0; i < parts.length; i++) {
            stringParts[i] = String.valueOf(parts[i]);
        }
        String[] combinedParts = new String[stringParts.length + 1];
        combinedParts[0] = namespace;
        System.arraycopy(stringParts, 0, combinedParts, 1, stringParts.length);
        return generateKey(combinedParts);
    }

    /**
     * Increment key version (for cache invalidation)
     */
    public long incrementKeyVersion() {
        return keyVersion.incrementAndGet();
    }

    /**
     * Get current key version
     */
    public long getKeyVersion() {
        return keyVersion.get();
    }

    /**
     * Validate cache key format
     */
    public boolean isValidKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // Basic validation - should start with prefix
        return key.startsWith(KEY_PREFIX + KEY_SEPARATOR);
    }

    /**
     * Extract namespace from cache key
     */
    public String extractNamespace(String key) {
        if (!isValidKey(key)) {
            return null;
        }
        
        String[] parts = key.split(KEY_SEPARATOR);
        return parts.length >= 2 ? parts[1] : null;
    }

    /**
     * Extract key parts from cache key
     */
    public String[] extractKeyParts(String key) {
        if (!isValidKey(key)) {
            return new String[0];
        }
        
        String withoutPrefix = key.substring(KEY_PREFIX.length() + KEY_SEPARATOR.length());
        return withoutPrefix.split(KEY_SEPARATOR);
    }

    // Private helper methods

    private void initializeKeyTemplates() {
        keyTemplates.put("product", "product:{id}");
        keyTemplates.put("products", "products:{category}:{page}:{limit}");
        keyTemplates.put("user", "user:{id}");
        keyTemplates.put("profile", "profile:{id}");
        keyTemplates.put("cart", "cart:{id}");
        keyTemplates.put("order", "order:{id}");
        keyTemplates.put("orders", "orders:{id}:{status}:{page}:{limit}");
        keyTemplates.put("coupon", "coupon:{code}");
        keyTemplates.put("coupons", "coupons:{active}");
        keyTemplates.put("category", "category:{id}");
        keyTemplates.put("categories", "categories");
        keyTemplates.put("search", "search:{query}:{category}:{sort}:{page}:{limit}");
        keyTemplates.put("recommendations", "recommendations:{id}:{type}:{limit}");
        keyTemplates.put("inventory", "inventory:{id}:{size}");
        keyTemplates.put("session", "session:{id}");
        keyTemplates.put("permissions", "permissions:{id}:{resource}");
        keyTemplates.put("ratelimit", "ratelimit:{id}:{window}");
        keyTemplates.put("analytics", "analytics:{type}:{period}:{dimension}");
    }

    private String generateKey(String... parts) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(KEY_PREFIX).append(KEY_SEPARATOR);
        keyBuilder.append(keyVersion.get()); // Include version for invalidation
        
        for (String part : parts) {
            keyBuilder.append(KEY_SEPARATOR);
            keyBuilder.append(sanitizeKeyPart(part));
        }
        
        return keyBuilder.toString();
    }

    private String sanitizeKeyPart(String part) {
        if (part == null) {
            return "null";
        }
        
        // Replace invalid characters
        return part.replace(":", "_")
                   .replace("*", "_")
                   .replace("?", "_")
                   .replace(" ", "_")
                   .toLowerCase();
    }

    private String hashQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return "empty";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(query.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fallback to simple hash
            return String.valueOf(query.hashCode());
        }
    }

    /**
     * Key pattern for matching
     */
    public static class KeyPattern {
        private final String pattern;
        private final String regex;

        public KeyPattern(String pattern) {
            this.pattern = pattern;
            this.regex = pattern.replace("*", ".*").replace("?", ".");
        }

        public boolean matches(String key) {
            return key.matches(regex);
        }

        public String getPattern() {
            return pattern;
        }
    }
}
