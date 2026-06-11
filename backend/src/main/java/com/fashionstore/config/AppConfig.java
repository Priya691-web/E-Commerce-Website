package com.fashionstore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Application Configuration
 * Centralized application configuration management
 * Loads configuration from environment variables with fallback to defaults
 */
public class AppConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    
    // Application profile
    private static final String DEFAULT_PROFILE = "dev";
    private static final String PROFILE = getEnvOrDefault("FASHIONSTORE_PROFILE", DEFAULT_PROFILE);
    
    // Security configuration
    private static final boolean DEFAULT_CSRF_ENABLED = true;
    private static final boolean CSRF_ENABLED = getEnvBooleanOrDefault("CSRF_ENABLED", DEFAULT_CSRF_ENABLED);
    
    private static final boolean DEFAULT_RATE_LIMIT_ENABLED = true;
    private static final boolean RATE_LIMIT_ENABLED = getEnvBooleanOrDefault("RATE_LIMIT_ENABLED", DEFAULT_RATE_LIMIT_ENABLED);
    
    // JWT configuration
    private static final long DEFAULT_JWT_EXPIRATION = 15 * 60 * 1000L; // 15 minutes
    private static final long JWT_EXPIRATION = getEnvLongOrDefault("JWT_EXPIRATION_MS", DEFAULT_JWT_EXPIRATION);
    
    private static final long DEFAULT_REFRESH_EXPIRATION = 7 * 24 * 60 * 60 * 1000L; // 7 days
    private static final long REFRESH_EXPIRATION = getEnvLongOrDefault("JWT_REFRESH_EXPIRATION_MS", DEFAULT_REFRESH_EXPIRATION);
    
    // Session configuration
    private static final int DEFAULT_SESSION_TIMEOUT = 30; // minutes
    private static final int SESSION_TIMEOUT = getEnvIntOrDefault("SESSION_TIMEOUT_MINUTES", DEFAULT_SESSION_TIMEOUT);
    
    // CORS configuration
    private static final String DEFAULT_CORS_ORIGINS = "http://localhost:5173,http://localhost:3000";
    private static final String CORS_ORIGINS = getEnvOrDefault("CORS_ALLOWED_ORIGINS", DEFAULT_CORS_ORIGINS);
    
    // File upload configuration
    private static final long DEFAULT_MAX_FILE_SIZE = 5 * 1024 * 1024L; // 5MB
    private static final long MAX_FILE_SIZE = getEnvLongOrDefault("MAX_FILE_SIZE_BYTES", DEFAULT_MAX_FILE_SIZE);
    
    // Cache configuration
    private static final long DEFAULT_CACHE_TTL = 5 * 60 * 1000L; // 5 minutes
    private static final long CACHE_TTL = getEnvLongOrDefault("CACHE_TTL_MS", DEFAULT_CACHE_TTL);
    
    private AppConfig() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get application profile
     */
    public static String getProfile() {
        return PROFILE;
    }
    
    /**
     * Check if running in production
     */
    public static boolean isProduction() {
        return "prod".equalsIgnoreCase(PROFILE);
    }
    
    /**
     * Check if running in development
     */
    public static boolean isDevelopment() {
        return "dev".equalsIgnoreCase(PROFILE);
    }
    
    /**
     * Check if CSRF protection is enabled
     */
    public static boolean isCsrfEnabled() {
        return CSRF_ENABLED;
    }
    
    /**
     * Check if rate limiting is enabled
     */
    public static boolean isRateLimitEnabled() {
        return RATE_LIMIT_ENABLED;
    }
    
    /**
     * Get JWT expiration time in milliseconds
     */
    public static long getJwtExpiration() {
        return JWT_EXPIRATION;
    }
    
    /**
     * Get JWT refresh token expiration time in milliseconds
     */
    public static long getRefreshExpiration() {
        return REFRESH_EXPIRATION;
    }
    
    /**
     * Get session timeout in minutes
     */
    public static int getSessionTimeout() {
        return SESSION_TIMEOUT;
    }
    
    /**
     * Get allowed CORS origins
     */
    public static String[] getCorsOrigins() {
        return CORS_ORIGINS.split(",");
    }
    
    /**
     * Get maximum file upload size in bytes
     */
    public static long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
    
    /**
     * Get cache TTL in milliseconds
     */
    public static long getCacheTTL() {
        return CACHE_TTL;
    }
    
    /**
     * Get all configuration as properties
     */
    public static Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("profile", PROFILE);
        props.setProperty("csrf.enabled", String.valueOf(CSRF_ENABLED));
        props.setProperty("rateLimit.enabled", String.valueOf(RATE_LIMIT_ENABLED));
        props.setProperty("jwt.expiration", String.valueOf(JWT_EXPIRATION));
        props.setProperty("jwt.refresh.expiration", String.valueOf(REFRESH_EXPIRATION));
        props.setProperty("session.timeout", String.valueOf(SESSION_TIMEOUT));
        props.setProperty("cors.origins", CORS_ORIGINS);
        props.setProperty("maxFileSize", String.valueOf(MAX_FILE_SIZE));
        props.setProperty("cache.ttl", String.valueOf(CACHE_TTL));
        return props;
    }
    
    /**
     * Get environment variable or default value
     */
    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default: {}", envVar, defaultValue);
            return defaultValue;
        }
        logger.debug("Environment variable {} set to: {}", envVar, value);
        return value.trim();
    }
    
    /**
     * Get environment variable as boolean or default value
     */
    private static boolean getEnvBooleanOrDefault(String envVar, boolean defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default: {}", envVar, defaultValue);
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
    
    /**
     * Get environment variable as integer or default value
     */
    private static int getEnvIntOrDefault(String envVar, int defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default: {}", envVar, defaultValue);
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for {}, using default: {}", envVar, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Get environment variable as long or default value
     */
    private static long getEnvLongOrDefault(String envVar, long defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default: {}", envVar, defaultValue);
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid long value for {}, using default: {}", envVar, defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Log current configuration
     */
    public static void logConfiguration() {
        logger.info("========================================");
        logger.info("FashionStore Application Configuration");
        logger.info("========================================");
        logger.info("Profile: {}", PROFILE);
        logger.info("CSRF Protection: {}", CSRF_ENABLED);
        logger.info("Rate Limiting: {}", RATE_LIMIT_ENABLED);
        logger.info("JWT Expiration: {} ms", JWT_EXPIRATION);
        logger.info("Refresh Expiration: {} ms", REFRESH_EXPIRATION);
        logger.info("Session Timeout: {} minutes", SESSION_TIMEOUT);
        logger.info("CORS Origins: {}", CORS_ORIGINS);
        logger.info("Max File Size: {} bytes", MAX_FILE_SIZE);
        logger.info("Cache TTL: {} ms", CACHE_TTL);
        logger.info("========================================");
    }
}
