package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Environment Variable Security Utilities
 * Validates and secures environment variable access
 */
public class EnvironmentSecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(EnvironmentSecurityUtil.class);
    
    // Required environment variables
    private static final Set<String> REQUIRED_ENV_VARS = new HashSet<>(Arrays.asList(
        "DB_URL",
        "DB_USERNAME",
        "DB_PASSWORD",
        "JWT_SECRET_KEY"
    ));
    
    // Sensitive environment variables (should never be logged)
    private static final Set<String> SENSITIVE_ENV_VARS = new HashSet<>(Arrays.asList(
        "DB_PASSWORD",
        "JWT_SECRET_KEY",
        "API_KEY",
        "SECRET_KEY",
        "STRIPE_SECRET_KEY",
        "STRIPE_PUBLISHABLE_KEY",
        "EMAIL_PASSWORD",
        "SMTP_PASSWORD",
        "OAUTH_CLIENT_SECRET"
    ));
    
    // Allowed environment variable prefixes
    private static final Set<String> ALLOWED_PREFIXES = new HashSet<>(Arrays.asList(
        "DB_", "JWT_", "API_", "STRIPE_", "EMAIL_", "SMTP_", "OAUTH_", "APP_", "SERVER_"
    ));
    
    /**
     * Validate required environment variables
     */
    public static boolean validateRequiredEnvironmentVariables() {
        boolean allValid = true;
        
        for (String varName : REQUIRED_ENV_VARS) {
            String value = System.getenv(varName);
            if (value == null || value.isEmpty()) {
                logger.error("Required environment variable not set: {}", varName);
                allValid = false;
            }
        }
        
        return allValid;
    }
    
    /**
     * Get environment variable with security check
     */
    public static String getEnvVar(String varName) {
        // Check if variable name is allowed
        if (!isAllowedEnvVar(varName)) {
            logger.warn("Attempted to access disallowed environment variable: {}", varName);
            return null;
        }
        
        String value = System.getenv(varName);
        
        // Log access (but not the value if it's sensitive)
        if (isSensitiveEnvVar(varName)) {
            logger.debug("Accessed sensitive environment variable: {}", varName);
        } else {
            logger.debug("Accessed environment variable: {} = {}", varName, value);
        }
        
        return value;
    }
    
    /**
     * Get environment variable with default value
     */
    public static String getEnvVar(String varName, String defaultValue) {
        String value = getEnvVar(varName);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Check if environment variable name is allowed
     */
    public static boolean isAllowedEnvVar(String varName) {
        if (varName == null || varName.isEmpty()) {
            return false;
        }
        
        // Check if it has an allowed prefix
        for (String prefix : ALLOWED_PREFIXES) {
            if (varName.startsWith(prefix)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Check if environment variable is sensitive
     */
    public static boolean isSensitiveEnvVar(String varName) {
        if (varName == null) {
            return false;
        }
        
        String upperName = varName.toUpperCase();
        for (String sensitive : SENSITIVE_ENV_VARS) {
            if (upperName.equals(sensitive) || upperName.contains(sensitive)) {
                return true;
            }
        }
        
        // Check for common sensitive patterns
        return upperName.contains("PASSWORD") || 
               upperName.contains("SECRET") || 
               upperName.contains("KEY") || 
               upperName.contains("TOKEN");
    }
    
    /**
     * Mask sensitive value for logging
     */
    public static String maskSensitiveValue(String varName, String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        if (isSensitiveEnvVar(varName)) {
            if (value.length() <= 4) {
                return "****";
            }
            return value.substring(0, 2) + "****" + value.substring(value.length() - 2);
        }
        
        return value;
    }
    
    /**
     * Validate environment variable format
     */
    public static boolean validateEnvVarFormat(String varName, String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        
        // Specific validations for known variables
        switch (varName) {
            case "DB_URL":
                return value.startsWith("jdbc:") && (value.contains("mysql") || value.contains("postgresql"));
            case "JWT_SECRET_KEY":
                return value.length() >= 32; // Minimum 32 characters
            case "API_KEY":
                return value.length() >= 16; // Minimum 16 characters
            default:
                return true;
        }
    }
    
    /**
     * Get all environment variables (masked)
     */
    public static java.util.Map<String, String> getAllEnvVars() {
        java.util.Map<String, String> envVars = new java.util.HashMap<>();
        java.util.Map<String, String> systemEnv = System.getenv();
        
        for (String key : systemEnv.keySet()) {
            if (isAllowedEnvVar(key)) {
                String value = systemEnv.get(key);
                envVars.put(key, maskSensitiveValue(key, value));
            }
        }
        
        return envVars;
    }
    
    /**
     * Validate production environment configuration
     */
    public static boolean validateProductionConfig() {
        boolean isValid = true;
        
        // Check if running in production
        String env = System.getProperty("spring.profiles.active", "development");
        String envVar = System.getenv("SPRING_PROFILES_ACTIVE");
        if (envVar != null) {
            env = envVar;
        }
        
        if (!"production".equals(env)) {
            logger.info("Not running in production mode");
            return true;
        }
        
        // Production-specific validations
        logger.info("Validating production environment configuration");
        
        // Check for secure HTTPS
        String httpsEnabled = System.getenv("HTTPS_ENABLED");
        if (!"true".equals(httpsEnabled)) {
            logger.warn("HTTPS not enabled in production");
            isValid = false;
        }
        
        // Check for secure cookies
        String secureCookies = System.getenv("SECURE_COOKIES");
        if (!"true".equals(secureCookies)) {
            logger.warn("Secure cookies not enabled in production");
            isValid = false;
        }
        
        // Check for strong JWT secret
        String jwtSecret = System.getenv("JWT_SECRET_KEY");
        if (jwtSecret != null && jwtSecret.length() < 64) {
            logger.warn("JWT secret key too short for production (should be at least 64 characters)");
            isValid = false;
        }
        
        // Check for database SSL
        String dbSSL = System.getenv("DB_SSL");
        if (!"true".equals(dbSSL)) {
            logger.warn("Database SSL not enabled in production");
            isValid = false;
        }
        
        return isValid;
    }
    
    /**
     * Log environment variable status (for debugging)
     */
    public static void logEnvVarStatus() {
        logger.info("Environment Variable Status:");
        
        for (String varName : REQUIRED_ENV_VARS) {
            String value = System.getenv(varName);
            String status = value != null && !value.isEmpty() ? "SET" : "NOT SET";
            logger.info("  {}: {}", varName, status);
        }
        
        // Check production config
        boolean prodConfigValid = validateProductionConfig();
        logger.info("Production Configuration Valid: {}", prodConfigValid);
    }
}
