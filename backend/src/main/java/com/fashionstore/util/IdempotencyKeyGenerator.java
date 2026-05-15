package com.fashionstore.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * Utility class for generating idempotency keys
 * Ensures uniqueness and security for request deduplication
 */
public class IdempotencyKeyGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    /**
     * Generate a unique idempotency key
     * @return Unique idempotency key
     */
    public static String generate() {
        // Combine timestamp, random number, and UUID for uniqueness
        long timestamp = System.currentTimeMillis();
        int random = secureRandom.nextInt(1000000);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        
        return String.format("%d-%d-%s", timestamp, random, uuid);
    }
    
    /**
     * Generate a shorter idempotency key (32 characters)
     * @return Short idempotency key
     */
    public static String generateShort() {
        StringBuilder sb = new StringBuilder(32);
        
        // Add timestamp prefix (8 characters)
        String timestampHex = Long.toHexString(System.currentTimeMillis());
        sb.append(timestampHex.substring(0, 8));
        
        // Add random characters (24 characters)
        for (int i = 0; i < 24; i++) {
            sb.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        
        return sb.toString();
    }
    
    /**
     * Generate idempotency key with prefix
     * @param prefix Prefix to identify the operation type
     * @return Idempotency key with prefix
     */
    public static String generateWithPrefix(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return generate();
        }
        
        return prefix + "-" + generate();
    }
    
    /**
     * Validate idempotency key format
     * @param key Idempotency key to validate
     * @return True if valid format
     */
    public static boolean isValid(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }
        
        // Check minimum length
        if (key.length() < 16) {
            return false;
        }
        
        // Check for valid characters (alphanumeric, hyphen, underscore)
        return key.matches("^[a-zA-Z0-9\\-_]+$");
    }
    
    /**
     * Extract timestamp from idempotency key
     * @param key Idempotency key
     * @return Timestamp extracted from key, or -1 if invalid
     */
    public static long extractTimestamp(String key) {
        if (!isValid(key)) {
            return -1;
        }
        
        try {
            String[] parts = key.split("-");
            if (parts.length > 0) {
                return Long.parseLong(parts[0]);
            }
        } catch (NumberFormatException e) {
            // Invalid format
        }
        
        return -1;
    }
    
    /**
     * Check if idempotency key is expired
     * @param key Idempotency key
     * @param maxAgeMinutes Maximum age in minutes
     * @return True if expired
     */
    public static boolean isExpired(String key, int maxAgeMinutes) {
        long timestamp = extractTimestamp(key);
        if (timestamp == -1) {
            return true; // Invalid keys are considered expired
        }
        
        long maxAgeMillis = maxAgeMinutes * 60 * 1000L;
        return (System.currentTimeMillis() - timestamp) > maxAgeMillis;
    }
}
