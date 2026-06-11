package com.fashionstore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Security Configuration
 * Centralized security configuration management
 */
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    // JWT configuration
    private static final String DEFAULT_JWT_SECRET = "fashionstore-secret-key-change-in-production";
    private static final String JWT_SECRET = getEnvOrDefault("JWT_SECRET", DEFAULT_JWT_SECRET);
    
    // Password encryption
    private static final int DEFAULT_BCRYPT_ROUNDS = 12;
    private static final int BCRYPT_ROUNDS = getEnvIntOrDefault("BCRYPT_ROUNDS", DEFAULT_BCRYPT_ROUNDS);
    
    // Session configuration
    private static final boolean DEFAULT_SECURE_COOKIE = false;
    private static final boolean SECURE_COOKIE = getEnvBooleanOrDefault("SESSION_COOKIE_SECURE", DEFAULT_SECURE_COOKIE);
    
    private static final boolean DEFAULT_HTTP_ONLY_COOKIE = true;
    private static final boolean HTTP_ONLY_COOKIE = getEnvBooleanOrDefault("SESSION_COOKIE_HTTP_ONLY", DEFAULT_HTTP_ONLY_COOKIE);
    
    private static final String DEFAULT_COOKIE_SAME_SITE = "Lax";
    private static final String COOKIE_SAME_SITE = getEnvOrDefault("SESSION_COOKIE_SAME_SITE", DEFAULT_COOKIE_SAME_SITE);
    
    // Rate limiting
    private static final int DEFAULT_RATE_LIMIT_REQUESTS = 100;
    private static final int RATE_LIMIT_REQUESTS = getEnvIntOrDefault("RATE_LIMIT_REQUESTS", DEFAULT_RATE_LIMIT_REQUESTS);
    
    private static final long DEFAULT_RATE_LIMIT_WINDOW = 60000L; // 1 minute
    private static final long RATE_LIMIT_WINDOW = getEnvLongOrDefault("RATE_LIMIT_WINDOW_MS", DEFAULT_RATE_LIMIT_WINDOW);
    
    private SecurityConfig() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Get JWT secret key
     */
    public static String getJwtSecret() {
        return JWT_SECRET;
    }
    
    /**
     * Get JWT expiration time from AppConfig
     */
    public static long getJwtExpiration() {
        return AppConfig.getJwtExpiration();
    }
    
    /**
     * Get JWT refresh token expiration time from AppConfig
     */
    public static long getRefreshExpiration() {
        return AppConfig.getRefreshExpiration();
    }
    
    /**
     * Get bcrypt rounds for password hashing
     */
    public static int getBcryptRounds() {
        return BCRYPT_ROUNDS;
    }
    
    /**
     * Check if secure cookie should be used
     */
    public static boolean isSecureCookie() {
        return SECURE_COOKIE || AppConfig.isProduction();
    }
    
    /**
     * Check if HTTP-only cookie should be used
     */
    public static boolean isHttpOnlyCookie() {
        return HTTP_ONLY_COOKIE;
    }
    
    /**
     * Get cookie SameSite policy
     */
    public static String getCookieSameSite() {
        return COOKIE_SAME_SITE;
    }
    
    /**
     * Get rate limit requests per window
     */
    public static int getRateLimitRequests() {
        return RATE_LIMIT_REQUESTS;
    }
    
    /**
     * Get rate limit window in milliseconds
     */
    public static long getRateLimitWindow() {
        return RATE_LIMIT_WINDOW;
    }
    
    /**
     * Check if rate limiting is enabled
     */
    public static boolean isRateLimitEnabled() {
        return AppConfig.isRateLimitEnabled();
    }
    
    /**
     * Check if CSRF protection is enabled
     */
    public static boolean isCsrfEnabled() {
        return AppConfig.isCsrfEnabled();
    }
    
    /**
     * Validate JWT secret
     */
    public static boolean validateJwtSecret() {
        if (JWT_SECRET == null || JWT_SECRET.length() < 32) {
            logger.warn("JWT secret is too short. Minimum 32 characters recommended.");
            return false;
        }
        if (JWT_SECRET.equals(DEFAULT_JWT_SECRET) && AppConfig.isProduction()) {
            logger.warn("Using default JWT secret in production. This is insecure!");
            return false;
        }
        return true;
    }
    
    /**
     * Get environment variable or default value
     */
    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default", envVar);
            return defaultValue;
        }
        return value.trim();
    }
    
    /**
     * Get environment variable as boolean or default value
     */
    private static boolean getEnvBooleanOrDefault(String envVar, boolean defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default", envVar);
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
            logger.debug("Environment variable {} not set, using default", envVar);
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for {}, using default", envVar);
            return defaultValue;
        }
    }
    
    /**
     * Get environment variable as long or default value
     */
    private static long getEnvLongOrDefault(String envVar, long defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default", envVar);
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Invalid long value for {}, using default", envVar);
            return defaultValue;
        }
    }
    
    /**
     * Log security configuration
     */
    public static void logSecurityConfiguration() {
        logger.info("========================================");
        logger.info("Security Configuration");
        logger.info("========================================");
        logger.info("JWT Secret: {}", JWT_SECRET.length() > 0 ? "***HIDDEN***" : "NOT SET");
        logger.info("JWT Expiration: {} ms", getJwtExpiration());
        logger.info("Refresh Expiration: {} ms", getRefreshExpiration());
        logger.info("BCrypt Rounds: {}", BCRYPT_ROUNDS);
        logger.info("Secure Cookie: {}", isSecureCookie());
        logger.info("HTTP-Only Cookie: {}", isHttpOnlyCookie());
        logger.info("Cookie SameSite: {}", COOKIE_SAME_SITE);
        logger.info("Rate Limit Enabled: {}", isRateLimitEnabled());
        logger.info("Rate Limit: {} requests per {} ms", RATE_LIMIT_REQUESTS, RATE_LIMIT_WINDOW);
        logger.info("CSRF Enabled: {}", isCsrfEnabled());
        logger.info("========================================");
    }
}
