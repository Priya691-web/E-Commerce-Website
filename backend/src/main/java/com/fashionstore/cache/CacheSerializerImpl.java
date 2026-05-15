package com.fashionstore.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jackson-based cache serializer
 * Provides safe, reliable serialization with version control
 */
public class CacheSerializerImpl {

    private static final Logger logger = LoggerFactory.getLogger(CacheSerializerImpl.class);
    private static volatile CacheSerializerImpl instance;
    
    private final ObjectMapper objectMapper;
    private final String cacheVersion;

    private CacheSerializerImpl() {
        this.objectMapper = new ObjectMapper();
        this.cacheVersion = "1.0";
        
        // Configure ObjectMapper for caching
        objectMapper.findAndRegisterModules();
        objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT);
    }

    public static CacheSerializerImpl getInstance() {
        if (instance == null) {
            synchronized (CacheSerializerImpl.class) {
                if (instance == null) {
                    instance = new CacheSerializerImpl();
                }
            }
        }
        return instance;
    }

    public String serialize(Object object) {
        if (object == null) {
            return null;
        }

        try {
            // Create cache wrapper with version
            CacheWrapper wrapper = new CacheWrapper(object, cacheVersion);
            return objectMapper.writeValueAsString(wrapper);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object to cache: {}", object.getClass().getName(), e);
            return null;
        }
    }

    public <T> T deserialize(String serialized, Class<T> type) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return null;
        }

        try {
            return objectMapper.readValue(serialized, type);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing from cache: {}", e.getMessage(), e);
            return null;
        }
    }

    private static class CacheWrapper {
        private final Object data;
        private final String version;
        private final long timestamp;

        public CacheWrapper(Object data, String version) {
            this.data = data;
            this.version = version;
            this.timestamp = System.currentTimeMillis();
        }

        public Object getData() { return data; }
        public String getVersion() { return version; }
        public long getTimestamp() { return timestamp; }
    }
}
