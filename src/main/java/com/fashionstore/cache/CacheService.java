package com.fashionstore.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class CacheService {

    private static final Logger logger = LoggerFactory.getLogger(CacheService.class);
    
    private static volatile CacheService instance;
    private final Map<String, CacheEntry> localCache;
    private final JedisPool redisPool;
    private final boolean redisEnabled;
    private final boolean useLocalFallback;
    
    private static final String REDIS_HOST = System.getProperty("redis.host", "localhost");
    private static final int REDIS_PORT = Integer.parseInt(System.getProperty("redis.port", "6379"));
    private static final String REDIS_PASSWORD = System.getProperty("redis.password", "");
    private static final boolean REDIS_ENABLED = Boolean.parseBoolean(System.getProperty("redis.enabled", "true"));
    
    private CacheService() {
        this.localCache = new ConcurrentHashMap<>();
        this.useLocalFallback = true;
        
        JedisPool pool = null;
        boolean enabled = false;
        
        if (REDIS_ENABLED) {
            try {
                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxTotal(20);
                poolConfig.setMaxIdle(10);
                poolConfig.setMinIdle(5);
                poolConfig.setTestOnBorrow(true);
                poolConfig.setTestOnReturn(false);
                poolConfig.setTestWhileIdle(true);
                poolConfig.setBlockWhenExhausted(false);
                
                if (REDIS_PASSWORD != null && !REDIS_PASSWORD.isEmpty()) {
                    pool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 2000, REDIS_PASSWORD);
                } else {
                    pool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT, 2000);
                }
                
                // Test connection
                try (Jedis jedis = pool.getResource()) {
                    jedis.ping();
                    enabled = true;
                    logger.info("Redis cache enabled - connected to {}:{}", REDIS_HOST, REDIS_PORT);
                }
            } catch (JedisException e) {
                logger.warn("Redis connection failed, falling back to local cache: {}", e.getMessage());
                if (pool != null) {
                    pool.close();
                }
                pool = null;
            }
        } else {
            logger.info("Redis cache disabled, using local cache only");
        }
        
        this.redisPool = pool;
        this.redisEnabled = enabled;
        
        if (!redisEnabled) {
            logger.info("Cache service initialized with in-memory fallback");
        }
    }
    
    public static CacheService getInstance() {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService();
                }
            }
        }
        return instance;
    }
    
    public void put(String key, Object value, long ttl, TimeUnit timeUnit) {
        if (key == null || value == null) {
            return;
        }
        
        int ttlSeconds = (int) timeUnit.toSeconds(ttl);
        
        // Try Redis first
        if (redisEnabled && redisPool != null) {
            try (Jedis jedis = redisPool.getResource()) {
                String serializedValue = serialize(value);
                jedis.setex(key, ttlSeconds, serializedValue);
                logger.debug("Cached key in Redis: {} with TTL: {}s", key, ttlSeconds);
                return;
            } catch (JedisException e) {
                logger.warn("Redis put failed, falling back to local cache: {}", e.getMessage());
            }
        }
        
        // Fallback to local cache
        if (useLocalFallback) {
            long expiryTime = System.currentTimeMillis() + timeUnit.toMillis(ttl);
            localCache.put(key, new CacheEntry(value, expiryTime));
            logger.debug("Cached key in local cache: {} with TTL: {} {}", key, ttl, timeUnit);
        }
    }
    
    public void put(String key, Object value) {
        put(key, value, 1, TimeUnit.HOURS);
    }
    
    public Object get(String key) {
        if (key == null) {
            return null;
        }
        
        // Try Redis first
        if (redisEnabled && redisPool != null) {
            try (Jedis jedis = redisPool.getResource()) {
                String value = jedis.get(key);
                if (value != null) {
                    Object deserialized = deserialize(value);
                    logger.debug("Cache hit in Redis for key: {}", key);
                    return deserialized;
                }
            } catch (JedisException e) {
                logger.warn("Redis get failed, checking local cache: {}", e.getMessage());
            }
        }
        
        // Fallback to local cache
        if (useLocalFallback) {
            CacheEntry entry = localCache.get(key);
            if (entry == null) {
                return null;
            }
            
            if (System.currentTimeMillis() > entry.expiryTime) {
                localCache.remove(key);
                logger.debug("Cache expired for key: {}", key);
                return null;
            }
            
            logger.debug("Cache hit in local cache for key: {}", key);
            return entry.value;
        }
        
        return null;
    }
    
    public <T> T get(String key, Class<T> type) {
        Object value = get(key);
        if (value == null) {
            return null;
        }
        
        try {
            return type.cast(value);
        } catch (ClassCastException e) {
            logger.warn("Type mismatch for cached key: {}", key);
            return null;
        }
    }
    
    public void remove(String key) {
        if (key == null) {
            return;
        }
        
        // Remove from Redis
        if (redisEnabled && redisPool != null) {
            try (Jedis jedis = redisPool.getResource()) {
                jedis.del(key);
                logger.debug("Removed from Redis: {}", key);
            } catch (JedisException e) {
                logger.warn("Redis remove failed: {}", e.getMessage());
            }
        }
        
        // Remove from local cache
        if (useLocalFallback) {
            localCache.remove(key);
            logger.debug("Removed from local cache: {}", key);
        }
    }
    
    public void clear() {
        // Clear Redis
        if (redisEnabled && redisPool != null) {
            try (Jedis jedis = redisPool.getResource()) {
                jedis.flushDB();
                logger.info("Redis cache cleared");
            } catch (JedisException e) {
                logger.warn("Redis clear failed: {}", e.getMessage());
            }
        }
        
        // Clear local cache
        if (useLocalFallback) {
            localCache.clear();
            logger.info("Local cache cleared");
        }
    }
    
    public void invalidatePattern(String pattern) {
        // Invalidate in Redis using SCAN
        if (redisEnabled && redisPool != null) {
            try (Jedis jedis = redisPool.getResource()) {
                String cursor = "0";
                do {
                    var scanResult = jedis.scan(cursor);
                    cursor = scanResult.getCursor();
                    for (String key : scanResult.getResult()) {
                        if (key.matches(pattern)) {
                            jedis.del(key);
                        }
                    }
                } while (!cursor.equals("0"));
                logger.debug("Invalidated Redis pattern: {}", pattern);
            } catch (JedisException e) {
                logger.warn("Redis pattern invalidation failed: {}", e.getMessage());
            }
        }
        
        // Invalidate in local cache
        if (useLocalFallback) {
            localCache.keySet().removeIf(key -> key.matches(pattern));
            logger.debug("Invalidated local cache pattern: {}", pattern);
        }
    }
    
    public int size() {
        cleanupExpired();
        
        if (redisEnabled && redisPool != null) {
            try (Jedis jedis = redisPool.getResource()) {
                return (int) jedis.dbSize();
            } catch (JedisException e) {
                logger.warn("Redis size check failed: {}", e.getMessage());
            }
        }
        
        return localCache.size();
    }
    
    public void cleanupExpired() {
        if (useLocalFallback) {
            long currentTime = System.currentTimeMillis();
            localCache.entrySet().removeIf(entry -> currentTime > entry.getValue().expiryTime);
        }
    }
    
    public boolean isRedisEnabled() {
        return redisEnabled;
    }
    
    public void shutdown() {
        if (redisPool != null) {
            redisPool.close();
            logger.info("Redis pool shut down");
        }
    }
    
    private String serialize(Object value) {
        if (value == null) {
            return null;
        }
        // Simple serialization using toString for basic types
        // For complex objects, use Gson or similar
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }
    
    private Object deserialize(String value) {
        if (value == null) {
            return null;
        }
        // Return as String for simplicity
        // For complex objects, use Gson deserialization
        return value;
    }
    
    private static class CacheEntry {
        final Object value;
        final long expiryTime;
        
        CacheEntry(Object value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }
    }
}
