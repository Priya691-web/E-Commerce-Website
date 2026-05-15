package com.fashionstore.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Cache service interface
 * Provides cache operations with type safety
 */
public interface CacheService {

    /**
     * Get value from cache with type safety
     */
    <T> T get(String key, Class<T> type);

    /**
     * Get value from cache asynchronously
     */
    <T> CompletableFuture<T> getAsync(String key, Class<T> type);

    /**
     * Put value in cache with TTL
     */
    void put(String key, Object value, long ttl, TimeUnit timeUnit);

    /**
     * Put value in cache asynchronously
     */
    CompletableFuture<Void> putAsync(String key, Object value, long ttl, TimeUnit timeUnit);

    /**
     * Put value in cache with default TTL
     */
    void put(String key, Object value);

    /**
     * Remove value from cache
     */
    void remove(String key);

    /**
     * Remove value from cache asynchronously
     */
    CompletableFuture<Boolean> removeAsync(String key);

    /**
     * Check if key exists
     */
    boolean exists(String key);

    /**
     * Clear all cache
     */
    void clear();

    /**
     * Get cache size
     */
    int size();

    /**
     * Get cache statistics
     */
    Map<String, Object> getStats();

    /**
     * Invalidate cache by pattern
     */
    void invalidatePattern(String pattern);

    /**
     * Invalidate cache by pattern asynchronously
     */
    CompletableFuture<Integer> invalidateByPatternAsync(String pattern);

    /**
     * Invalidate product cache
     */
    void invalidateProduct(int productId);

    /**
     * Invalidate user cache
     */
    void invalidateUser(int userId);

    /**
     * Invalidate cart cache
     */
    void invalidateCart(int userId);

    /**
     * Invalidate order cache
     */
    void invalidateOrder(int orderId);

    /**
     * Invalidate coupon cache
     */
    void invalidateCoupon(String couponCode);

    /**
     * Batch get values
     */
    <T> Map<String, T> batchGet(List<String> keys, Class<T> type);

    /**
     * Batch put values
     */
    void batchPut(Map<String, Object> values, long ttl, TimeUnit timeUnit);

    /**
     * Cleanup expired entries
     */
    void cleanupExpired();

    /**
     * Check if Redis is enabled
     */
    boolean isRedisEnabled();
}
