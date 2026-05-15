package com.fashionstore.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Refactored Cache Service implementation
 * Provides thread-safe, reliable caching with proper serialization
 */
public class CacheServiceImpl implements CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheServiceImpl.class);
    private static volatile CacheServiceImpl instance;
    
    private CacheManager cacheManager;
    private CacheInvalidationService invalidationService;
    private CacheKeyGenerator keyGenerator;

    // Default TTL values
    private static final Duration DEFAULT_TTL = Duration.ofHours(1);
    private static final Duration SHORT_TTL = Duration.ofMinutes(15);
    private static final Duration LONG_TTL = Duration.ofHours(6);
    private static final Duration SESSION_TTL = Duration.ofMinutes(30);

    private CacheServiceImpl() {
        this.cacheManager = CacheManager.getInstance();
        this.invalidationService = new CacheInvalidationService();
        this.keyGenerator = CacheKeyGenerator.getInstance();
    }

    public static CacheServiceImpl getInstance() {
        if (instance == null) {
            synchronized (CacheServiceImpl.class) {
                if (instance == null) {
                    instance = new CacheServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        return cacheManager.get(key, type);
    }

    @Override
    public <T> CompletableFuture<T> getAsync(String key, Class<T> type) {
        return cacheManager.getAsync(key, type);
    }

    @Override
    public void put(String key, Object value, long ttl, TimeUnit timeUnit) {
        Duration duration = Duration.ofMillis(timeUnit.toMillis(ttl));
        cacheManager.put(key, value, duration);
    }

    @Override
    public CompletableFuture<Void> putAsync(String key, Object value, long ttl, TimeUnit timeUnit) {
        Duration duration = Duration.ofMillis(timeUnit.toMillis(ttl));
        return cacheManager.putAsync(key, value, duration).thenApply(b -> null);
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, DEFAULT_TTL.toMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void remove(String key) {
        cacheManager.remove(key);
    }

    @Override
    public CompletableFuture<Boolean> removeAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            remove(key);
            return true;
        });
    }

    @Override
    public boolean exists(String key) {
        return cacheManager.get(key, Object.class) != null;
    }

    @Override
    public void clear() {
        cacheManager.clear();
    }

    @Override
    public int size() {
        return cacheManager.size();
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", cacheManager.size());
        return stats;
    }

    @Override
    public void invalidatePattern(String pattern) {
        invalidationService.invalidateByPattern(pattern);
    }

    @Override
    public CompletableFuture<Integer> invalidateByPatternAsync(String pattern) {
        return invalidationService.invalidateByPatternAsync(pattern);
    }

    @Override
    public void invalidateProduct(int productId) {
        String key = keyGenerator.generateProductKey(productId);
        remove(key);
    }

    @Override
    public void invalidateUser(int userId) {
        String key = keyGenerator.generateUserKey(userId);
        remove(key);
    }

    @Override
    public void invalidateCart(int userId) {
        String key = keyGenerator.generateCartKey(userId);
        remove(key);
    }

    @Override
    public void invalidateOrder(int orderId) {
        String key = keyGenerator.generateOrderKey(orderId);
        remove(key);
    }

    @Override
    public void invalidateCoupon(String couponCode) {
        String key = keyGenerator.generateCouponKey(couponCode);
        remove(key);
    }

    @Override
    public <T> Map<String, T> batchGet(List<String> keys, Class<T> type) {
        Map<String, T> result = new HashMap<>();
        for (String key : keys) {
            T value = get(key, type);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public void batchPut(Map<String, Object> values, long ttl, TimeUnit timeUnit) {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            put(entry.getKey(), entry.getValue(), ttl, timeUnit);
        }
    }

    @Override
    public void cleanupExpired() {
        // CacheManager doesn't have this method - placeholder
    }

    @Override
    public boolean isRedisEnabled() {
        // CacheManager doesn't have this method - return false for now
        return false;
    }
}
