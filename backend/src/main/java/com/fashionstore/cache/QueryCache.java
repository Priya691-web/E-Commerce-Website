package com.fashionstore.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Map;
import java.util.Set;

/**
 * Query Cache - Redis-based caching layer for frequently accessed data
 * Reduces database load and improves response times for common queries
 */
public class QueryCache {
    private static final Logger logger = LoggerFactory.getLogger(QueryCache.class);
    
    private static JedisPool jedisPool;
    private static boolean enabled = true;
    
    // Default cache TTL values (in seconds)
    private static final int DEFAULT_TTL = 300; // 5 minutes
    private static final int SHORT_TTL = 60; // 1 minute
    private static final int LONG_TTL = 3600; // 1 hour
    private static final int VERY_LONG_TTL = 86400; // 24 hours
    
    private QueryCache() {
        // Private constructor
    }
    
    /**
     * Initialize Redis connection pool
     */
    public static synchronized void initialize() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            logger.info("QueryCache already initialized");
            return;
        }
        
        try {
            String redisHost = System.getenv("FASHIONSTORE_REDIS_HOST");
            String redisPort = System.getenv("FASHIONSTORE_REDIS_PORT");
            String redisPassword = System.getenv("FASHIONSTORE_REDIS_PASSWORD");
            
            if (redisHost == null || redisHost.isEmpty()) {
                logger.warn("Redis host not configured, query caching disabled");
                enabled = false;
                return;
            }
            
            int port = 6379;
            if (redisPort != null && !redisPort.isEmpty()) {
                port = Integer.parseInt(redisPort);
            }
            
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            poolConfig.setMaxTotal(50);
            poolConfig.setMaxIdle(10);
            poolConfig.setMinIdle(5);
            poolConfig.setTestOnBorrow(true);
            poolConfig.setTestWhileIdle(true);
            poolConfig.setMinEvictableIdleTimeMillis(60000);
            poolConfig.setTimeBetweenEvictionRunsMillis(30000);
            
            if (redisPassword != null && !redisPassword.isEmpty()) {
                jedisPool = new JedisPool(poolConfig, redisHost, port, 2000, redisPassword);
            } else {
                jedisPool = new JedisPool(poolConfig, redisHost, port);
            }
            
            // Test connection
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.ping();
                logger.info("QueryCache initialized successfully - Redis: {}:{}", redisHost, port);
            }
            
        } catch (Exception e) {
            logger.error("Failed to initialize QueryCache", e);
            enabled = false;
        }
    }
    
    /**
     * Get cached value
     */
    public static String get(String key) {
        if (!enabled || jedisPool == null) {
            return null;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("Cache get error for key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Set cached value with default TTL
     */
    public static void set(String key, String value) {
        set(key, value, DEFAULT_TTL);
    }
    
    /**
     * Set cached value with custom TTL
     */
    public static void set(String key, String value, int ttlSeconds) {
        if (!enabled || jedisPool == null) {
            return;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(key, ttlSeconds, value);
        } catch (Exception e) {
            logger.error("Cache set error for key: {}", key, e);
        }
    }
    
    /**
     * Delete cached value
     */
    public static void delete(String key) {
        if (!enabled || jedisPool == null) {
            return;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        } catch (Exception e) {
            logger.error("Cache delete error for key: {}", key, e);
        }
    }
    
    /**
     * Delete multiple keys by pattern
     */
    public static void deleteByPattern(String pattern) {
        if (!enabled || jedisPool == null) {
            return;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                jedis.del(keys.toArray(new String[0]));
                logger.debug("Deleted {} cache entries matching pattern: {}", keys.size(), pattern);
            }
        } catch (Exception e) {
            logger.error("Cache delete pattern error for pattern: {}", pattern, e);
        }
    }
    
    /**
     * Check if key exists
     */
    public static boolean exists(String key) {
        if (!enabled || jedisPool == null) {
            return false;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.exists(key);
        } catch (Exception e) {
            logger.error("Cache exists error for key: {}", key, e);
            return false;
        }
    }
    
    /**
     * Increment counter
     */
    public static long increment(String key) {
        if (!enabled || jedisPool == null) {
            return 0;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(key);
        } catch (Exception e) {
            logger.error("Cache increment error for key: {}", key, e);
            return 0;
        }
    }
    
    /**
     * Get or compute pattern - get from cache or compute and cache
     */
    public static String getOrCompute(String key, Cacheable<String> supplier, int ttlSeconds) {
        String cached = get(key);
        if (cached != null) {
            logger.debug("Cache hit for key: {}", key);
            return cached;
        }
        
        logger.debug("Cache miss for key: {}", key);
        try {
            String value = supplier.compute();
            if (value != null) {
                set(key, value, ttlSeconds);
            }
            return value;
        } catch (Exception e) {
            logger.error("Error computing value for key: {}", key, e);
            return null;
        }
    }
    
    /**
     * Clear all cache entries (use with caution)
     */
    public static void clearAll() {
        if (!enabled || jedisPool == null) {
            return;
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushDB();
            logger.warn("Cache cleared - all entries deleted");
        } catch (Exception e) {
            logger.error("Cache clear error", e);
        }
    }
    
    /**
     * Get cache statistics
     */
    public static Map<String, String> getStats() {
        if (!enabled || jedisPool == null) {
            return Map.of("enabled", "false");
        }
        
        try (Jedis jedis = jedisPool.getResource()) {
            Map<String, String> stats = new java.util.HashMap<>();
            stats.put("enabled", "true");
            stats.put("db_size", String.valueOf(jedis.dbSize()));
            stats.put("info", jedis.info("stats"));
            return stats;
        } catch (Exception e) {
            logger.error("Cache stats error", e);
            return Map.of("enabled", "true", "error", e.getMessage());
        }
    }
    
    /**
     * Close connection pool
     */
    public static synchronized void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            logger.info("Closing QueryCache connection pool");
            jedisPool.close();
            jedisPool = null;
        }
    }
    
    /**
     * Check if cache is enabled
     */
    public static boolean isEnabled() {
        return enabled && jedisPool != null && !jedisPool.isClosed();
    }
    
    /**
     * Functional interface for cacheable operations
     */
    @FunctionalInterface
    public interface Cacheable<T> {
        T compute() throws Exception;
    }
    
    /**
     * Cache key builders for common patterns
     */
    public static class Keys {
        public static String product(int productId) {
            return "product:" + productId;
        }
        
        public static String category(int categoryId) {
            return "category:" + categoryId;
        }
        
        public static String userCart(int userId) {
            return "cart:" + userId;
        }
        
        public static String userWishlist(int userId) {
            return "wishlist:" + userId;
        }
        
        public static String productCategory(int categoryId) {
            return "products:category:" + categoryId;
        }
        
        public static String searchResults(String query) {
            return "search:" + query.hashCode();
        }
        
        public static String order(int orderId) {
            return "order:" + orderId;
        }
        
        public static String userOrders(int userId) {
            return "orders:user:" + userId;
        }
    }
    
    /**
     * TTL constants
     */
    public static class TTL {
        public static final int SHORT = SHORT_TTL;
        public static final int DEFAULT = DEFAULT_TTL;
        public static final int LONG = LONG_TTL;
        public static final int VERY_LONG = VERY_LONG_TTL;
    }
}
