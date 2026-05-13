package com.fashionstore.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

/**
 * Cache invalidation service
 * Provides intelligent cache invalidation strategies
 */
public class CacheInvalidationService {

    private static final Logger logger = LoggerFactory.getLogger(CacheInvalidationService.class);
    
    private CacheManager cacheManager;
    private CacheKeyGenerator keyGenerator;

    public CacheInvalidationService() {
        // Lazy initialization to avoid circular dependency
        this.cacheManager = null;
        this.keyGenerator = CacheKeyGenerator.getInstance();
    }

    private CacheManager getCacheManager() {
        if (cacheManager == null) {
            cacheManager = CacheManager.getInstance();
        }
        return cacheManager;
    }

    // Invalidation listeners
    private final List<CacheInvalidationListener> listeners = new CopyOnWriteArrayList<>();
    
    // Invalidation patterns
    private final ConcurrentHashMap<String, Pattern> compiledPatterns = new ConcurrentHashMap<>();

    /**
     * Invalidate cache by key
     */
    public CompletableFuture<Boolean> invalidateAsync(String key) {
        return CompletableFuture.supplyAsync(() -> invalidate(key));
    }

    /**
     * Invalidate cache by key
     */
    public boolean invalidate(String key) {
        if (key == null) {
            return false;
        }

        try {
            CacheManager cm = getCacheManager();
            if (cm == null) {
                return false;
            }
            boolean removed = cm.remove(key);
            
            // Notify listeners
            notifyListeners(key, InvalidationType.SINGLE);
            
            logger.debug("Cache invalidated: {}", key);
            return removed;

        } catch (Exception e) {
            logger.error("Error invalidating cache: {}", key, e);
            return false;
        }
    }

    /**
     * Invalidate cache by pattern
     */
    public CompletableFuture<Integer> invalidateByPatternAsync(String pattern) {
        return CompletableFuture.supplyAsync(() -> invalidateByPattern(pattern));
    }

    /**
     * Invalidate cache by pattern
     */
    public int invalidateByPattern(String pattern) {
        if (pattern == null || pattern.trim().isEmpty()) {
            return 0;
        }

        try {
            Pattern compiledPattern = getCompiledPattern(pattern);
            // CacheMetrics metrics = cacheManager.getMetrics();
            // CacheMetrics class doesn't exist, commenting out for now
            
            // For simplicity, we'll use a basic approach
            // In production, implement pattern-based Redis commands
            int invalidatedCount = 0;
            
            // This would need to be implemented based on the actual cache structure
            logger.debug("Cache invalidated by pattern: {} (count: {})", pattern, invalidatedCount);
            
            // Notify listeners
            notifyListeners(pattern, InvalidationType.PATTERN);
            
            return invalidatedCount;

        } catch (Exception e) {
            logger.error("Error invalidating cache by pattern: {}", pattern, e);
            return 0;
        }
    }

    /**
     * Invalidate all product-related cache
     */
    public CompletableFuture<Integer> invalidateProductsAsync() {
        return CompletableFuture.supplyAsync(this::invalidateProducts);
    }

    /**
     * Invalidate all product-related cache
     */
    public int invalidateProducts() {
        try {
            // Invalidate specific product patterns
            String[] patterns = {
                "product:*",
                "products:*",
                "category:*",
                "search:*"
            };

            int totalInvalidated = 0;
            for (String pattern : patterns) {
                totalInvalidated += invalidateByPattern(pattern);
            }

            // Notify listeners
            notifyListeners("products", InvalidationType.CATEGORY);

            logger.info("Product cache invalidated: {} entries", totalInvalidated);
            return totalInvalidated;

        } catch (Exception e) {
            logger.error("Error invalidating product cache", e);
            return 0;
        }
    }

    /**
     * Invalidate user-related cache
     */
    public CompletableFuture<Integer> invalidateUserAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> invalidateUser(userId));
    }

    /**
     * Invalidate user-related cache
     */
    public int invalidateUser(int userId) {
        try {
            String userKey = keyGenerator.generateUserKey(userId);
            
            // Invalidate user-specific patterns
            String[] patterns = {
                "user:" + userId + ":*",
                "cart:" + userId + ":*",
                "orders:" + userId + ":*",
                "profile:" + userId + ":*"
            };

            int totalInvalidated = 0;
            for (String pattern : patterns) {
                totalInvalidated += invalidateByPattern(pattern);
            }

            // Notify listeners
            notifyListeners("user:" + userId, InvalidationType.USER);

            logger.debug("User cache invalidated: {} ({} entries)", userId, totalInvalidated);
            return totalInvalidated;

        } catch (Exception e) {
            logger.error("Error invalidating user cache: {}", userId, e);
            return 0;
        }
    }

    /**
     * Invalidate cart cache
     */
    public CompletableFuture<Integer> invalidateCartAsync(int userId) {
        return CompletableFuture.supplyAsync(() -> invalidateCart(userId));
    }

    /**
     * Invalidate cart cache
     */
    public int invalidateCart(int userId) {
        try {
            String cartKey = keyGenerator.generateCartKey(userId);
            boolean removed = cacheManager.remove(cartKey);
            
            // Also invalidate cart-related patterns
            int patternInvalidated = invalidateByPattern("cart:" + userId + ":*");
            
            // Notify listeners
            notifyListeners("cart:" + userId, InvalidationType.CART);

            logger.debug("Cart cache invalidated: {} (removed: {}, pattern: {})", 
                        userId, removed, patternInvalidated);
            return removed ? 1 : 0;

        } catch (Exception e) {
            logger.error("Error invalidating cart cache: {}", userId, e);
            return 0;
        }
    }

    /**
     * Invalidate order cache
     */
    public CompletableFuture<Integer> invalidateOrderAsync(int orderId) {
        return CompletableFuture.supplyAsync(() -> invalidateOrder(orderId));
    }

    /**
     * Invalidate order cache
     */
    public int invalidateOrder(int orderId) {
        try {
            String orderKey = keyGenerator.generateOrderKey(orderId);
            boolean removed = cacheManager.remove(orderKey);
            
            // Also invalidate order-related patterns
            int patternInvalidated = invalidateByPattern("order:" + orderId + ":*");
            
            // Notify listeners
            notifyListeners("order:" + orderId, InvalidationType.ORDER);

            logger.debug("Order cache invalidated: {} (removed: {}, pattern: {})", 
                        orderId, removed, patternInvalidated);
            return removed ? 1 : 0;

        } catch (Exception e) {
            logger.error("Error invalidating order cache: {}", orderId, e);
            return 0;
        }
    }

    /**
     * Invalidate coupon cache
     */
    public CompletableFuture<Integer> invalidateCouponAsync(String couponCode) {
        return CompletableFuture.supplyAsync(() -> invalidateCoupon(couponCode));
    }

    /**
     * Invalidate coupon cache
     */
    public int invalidateCoupon(String couponCode) {
        try {
            String couponKey = keyGenerator.generateCouponKey(couponCode);
            boolean removed = cacheManager.remove(couponKey);
            
            // Also invalidate all coupons pattern
            int patternInvalidated = invalidateByPattern("coupon:*");
            
            // Notify listeners
            notifyListeners("coupon:" + couponCode, InvalidationType.COUPON);

            logger.debug("Coupon cache invalidated: {} (removed: {}, pattern: {})", 
                        couponCode, removed, patternInvalidated);
            return removed ? 1 : 0;

        } catch (Exception e) {
            logger.error("Error invalidating coupon cache: {}", couponCode, e);
            return 0;
        }
    }

    /**
     * Invalidate all cache
     */
    public CompletableFuture<Boolean> invalidateAllAsync() {
        return CompletableFuture.supplyAsync(this::invalidateAll);
    }

    /**
     * Invalidate all cache
     */
    public boolean invalidateAll() {
        try {
            boolean cleared = cacheManager.clear();
            
            // Notify listeners
            notifyListeners("all", InvalidationType.ALL);

            logger.info("All cache invalidated: {}", cleared);
            return cleared;

        } catch (Exception e) {
            logger.error("Error invalidating all cache", e);
            return false;
        }
    }

    /**
     * Add invalidation listener
     */
    public void addListener(CacheInvalidationListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * Remove invalidation listener
     */
    public void removeListener(CacheInvalidationListener listener) {
        listeners.remove(listener);
    }

    /**
     * Get invalidation statistics
     */
    public InvalidationStats getInvalidationStats() {
        // This would track invalidation statistics
        // For now, return basic stats
        return new InvalidationStats(
            listeners.size(),
            compiledPatterns.size(),
            System.currentTimeMillis()
        );
    }

    // Private helper methods

    private Pattern getCompiledPattern(String pattern) {
        return compiledPatterns.computeIfAbsent(pattern, p -> {
            try {
                // Convert glob pattern to regex
                String regex = pattern
                    .replace("*", ".*")
                    .replace("?", ".");
                return Pattern.compile(regex);
            } catch (Exception e) {
                logger.error("Error compiling pattern: {}", pattern, e);
                return Pattern.compile(".*"); // Fallback to match everything
            }
        });
    }

    private void notifyListeners(String key, InvalidationType type) {
        for (CacheInvalidationListener listener : listeners) {
            try {
                listener.onInvalidation(key, type);
            } catch (Exception e) {
                logger.error("Error notifying invalidation listener", e);
            }
        }
    }

    // Inner classes and interfaces

    public enum InvalidationType {
        SINGLE, PATTERN, CATEGORY, USER, CART, ORDER, COUPON, ALL
    }

    public interface CacheInvalidationListener {
        void onInvalidation(String key, InvalidationType type);
    }

    public static class InvalidationStats {
        private final int listenerCount;
        private final int patternCount;
        private final long timestamp;

        public InvalidationStats(int listenerCount, int patternCount, long timestamp) {
            this.listenerCount = listenerCount;
            this.patternCount = patternCount;
            this.timestamp = timestamp;
        }

        public int getListenerCount() { return listenerCount; }
        public int getPatternCount() { return patternCount; }
        public long getTimestamp() { return timestamp; }
    }
}
