package com.fashionstore.cache;

import com.fashionstore.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Safe cache deserializer with type safety and error handling
 * Handles complex objects, null values, and deserialization errors
 */
public class CacheDeserializer {

    private static final Logger logger = LoggerFactory.getLogger(CacheDeserializer.class);

    // Cache for class loaders to improve performance
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

    /**
     * Deserialize JSON string to object with type safety
     * @param json JSON string to deserialize
     * @param expectedType Expected class type
     * @param <T> Type parameter
     * @return Deserialized object or null if deserialization fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserialize(String json, Class<T> expectedType) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            return JsonUtil.gson().fromJson(json, expectedType);
        } catch (Exception e) {
            logger.error("Cache deserialization failed for type {}: {}", 
                        expectedType.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Deserialize JSON string with metadata for type safety
     * @param json JSON string with metadata
     * @param <T> Type parameter
     * @return Deserialized object or null if deserialization fails
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeWithMetadata(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }

        try {
            CacheMetadata metadata = JsonUtil.gson().fromJson(json, CacheMetadata.class);
            if (metadata == null) {
                logger.warn("Cache metadata is null");
                return null;
            }

            Class<?> targetClass = getClassForName(metadata.getClassName());
            if (targetClass == null) {
                logger.warn("Cannot find class: {}", metadata.getClassName());
                return null;
            }

            return (T) JsonUtil.gson().fromJson(JsonUtil.toJson(metadata.getData()), targetClass);
        } catch (Exception e) {
            logger.error("Cache deserialization with metadata failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Safe deserialization with fallback
     * @param json JSON string
     * @param expectedType Expected type
     * @param fallbackValue Value to return on failure
     * @param <T> Type parameter
     * @return Deserialized object or fallback value
     */
    public static <T> T deserializeSafe(String json, Class<T> expectedType, T fallbackValue) {
        T result = deserialize(json, expectedType);
        return result != null ? result : fallbackValue;
    }

    /**
     * Check if JSON string is valid
     * @param json JSON string to validate
     * @return true if valid JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            JsonUtil.gson().fromJson(json, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get class by name with caching
     * @param className Fully qualified class name
     * @return Class object or null if not found
     */
    private static Class<?> getClassForName(String className) {
        if (className == null || className.trim().isEmpty()) {
            return null;
        }

        // Check cache first
        Class<?> cachedClass = CLASS_CACHE.get(className);
        if (cachedClass != null) {
            return cachedClass;
        }

        try {
            Class<?> clazz = Class.forName(className);
            CLASS_CACHE.put(className, clazz);
            return clazz;
        } catch (ClassNotFoundException e) {
            logger.warn("Class not found: {}", className);
            return null;
        }
    }

    /**
     * Clear class cache (useful for testing)
     */
    public static void clearClassCache() {
        CLASS_CACHE.clear();
    }

    /**
     * Metadata wrapper for deserialization
     */
    private static class CacheMetadata {
        private String className;
        private Object data;
        private long timestamp;

        // Getters needed for Gson deserialization
        public String getClassName() { return className; }
        public Object getData() { return data; }
        public long getTimestamp() { return timestamp; }

        // Setters needed for Gson deserialization
        public void setClassName(String className) { this.className = className; }
        public void setData(Object data) { this.data = data; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}
