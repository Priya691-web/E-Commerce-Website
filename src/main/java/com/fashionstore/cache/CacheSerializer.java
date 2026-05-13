package com.fashionstore.cache;

import com.fashionstore.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Safe cache serializer using Gson for proper object serialization
 * Handles complex objects, null values, and serialization errors
 */
public class CacheSerializer {

    private static final Logger logger = LoggerFactory.getLogger(CacheSerializer.class);

    /**
     * Serialize object to JSON string safely
     * @param object Object to serialize
     * @return JSON string or null if serialization fails
     */
    public static String serialize(Object object) {
        if (object == null) {
            return null;
        }

        try {
            // Use existing JsonUtil for consistency
            return JsonUtil.toJson(object);
        } catch (Exception e) {
            logger.error("Cache serialization failed for object {}: {}", 
                        object.getClass().getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Serialize object with metadata for type safety
     * @param object Object to serialize
     * @return JSON string with type metadata
     */
    public static String serializeWithMetadata(Object object) {
        if (object == null) {
            return null;
        }

        try {
            CacheMetadata metadata = new CacheMetadata(object);
            return JsonUtil.toJson(metadata);
        } catch (Exception e) {
            logger.error("Cache serialization with metadata failed for object {}: {}", 
                        object.getClass().getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * Metadata wrapper for type-safe deserialization
     */
    private static class CacheMetadata {
        private final String className;
        private final Object data;
        private final long timestamp;

        public CacheMetadata(Object data) {
            this.data = data;
            this.className = data.getClass().getName();
            this.timestamp = System.currentTimeMillis();
        }

        public String getClassName() { return className; }
        public Object getData() { return data; }
        public long getTimestamp() { return timestamp; }
    }
}
