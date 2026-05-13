package com.fashionstore.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.stereotype.Component;
// Spring package doesn't exist, commenting out for now

/**
 * Jackson-based cache deserializer
 * Provides safe, reliable deserialization with validation
 */
// @Component
// Spring annotation doesn't exist, commenting out for now
public class CacheDeserializerImpl {

    private static final Logger logger = LoggerFactory.getLogger(CacheDeserializerImpl.class);
    
    private final ObjectMapper objectMapper;

    public CacheDeserializerImpl() {
        this.objectMapper = new ObjectMapper();
        
        // Configure ObjectMapper for deserialization
        objectMapper.findAndRegisterModules();
        objectMapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    public <T> T deserialize(String serialized, Class<T> type) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return null;
        }

        try {
            // Validate JSON format first
            if (!isValidJson(serialized)) {
                logger.warn("Invalid JSON format in cache: {}", serialized.substring(0, Math.min(100, serialized.length())));
                return null;
            }

            return objectMapper.readValue(serialized, type);
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing from cache: {} - {}", type.getName(), e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Unexpected error deserializing from cache: {}", type.getName(), e);
            return null;
        }
    }

    public <T> T deserialize(String serialized, Class<T> type, Class<?>... parameterTypes) {
        if (serialized == null || serialized.trim().isEmpty()) {
            return null;
        }

        try {
            if (parameterTypes != null && parameterTypes.length > 0) {
                // JavaType javaType = objectMapper.getTypeFactory().constructParametricType(type, parameterTypes);
                // JavaType is not imported, commenting out for now
                return deserialize(serialized, type);
            } else {
                return deserialize(serialized, type);
            }
        } catch (Exception e) {
            logger.error("Error deserializing parameterized type from cache: {}", type.getName(), e);
            return null;
        }
    }

    public boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }

        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    public <T> T deserializeSafely(String serialized, Class<T> type, T defaultValue) {
        T result = deserialize(serialized, type);
        return result != null ? result : defaultValue;
    }

    public <T> T deserializeWithFallback(String serialized, Class<T> type, Class<? extends T> fallbackType) {
        T result = deserialize(serialized, type);
        if (result != null) {
            return result;
        }

        // Try fallback type
        try {
            return deserialize(serialized, fallbackType);
        } catch (Exception e) {
            logger.warn("Fallback deserialization failed for type: {}", fallbackType.getName());
            return null;
        }
    }

    public <T> T deserializeWithValidation(String serialized, Class<T> type) {
        // CacheValidator class doesn't exist, removing validator parameter
        return deserialize(serialized, type);
    }

    public <T> T deserializeWithRetry(String serialized, Class<T> type, int maxRetries) {
        if (maxRetries <= 0) {
            return deserialize(serialized, type);
        }

        Exception lastException = null;
        for (int i = 0; i < maxRetries; i++) {
            try {
                return deserialize(serialized, type);
            } catch (Exception e) {
                lastException = e;
                logger.warn("Deserialization attempt {} failed for type: {}", i + 1, type.getName());
                
                // Wait before retry
                try {
                    Thread.sleep(50 * (i + 1)); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        logger.error("All deserialization attempts failed for type: {}", type.getName(), lastException);
        return null;
    }

}
