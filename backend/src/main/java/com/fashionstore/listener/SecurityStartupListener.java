package com.fashionstore.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SecurityStartupListener - Validates critical security configuration on application startup
 * 
 * ROOT CAUSE: Without startup validation, security misconfigurations may go undetected until runtime,
 * leading to security vulnerabilities or production failures.
 * 
 * FIX: This listener validates all critical security settings at startup and fails fast if any are missing or invalid.
 */
@WebListener
public class SecurityStartupListener implements ServletContextListener {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityStartupListener.class);
    
    private static final String ENV_PRODUCTION = "production";
    private static final String ENV_PROD = "prod";
    private static final int MIN_JWT_SECRET_LENGTH = 32;
    private static final int MIN_ADMIN_KEY_LENGTH = 16;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("========================================");
        logger.info("SECURITY STARTUP VALIDATION INITIATED");
        logger.info("========================================");
        
        String environment = getEnvironment();
        logger.info("Environment: {}", environment);
        
        // Phase 1.1: Enforce JWT_SECRET_KEY validation
        validateJwtSecret(environment);
        
        // Validate Admin Key
        validateAdminKey(environment);
        
        // Validate Database Configuration
        validateDatabaseConfig(environment);
        
        // Validate Security Headers Configuration
        validateSecurityHeadersConfig(environment);
        
        logger.info("========================================");
        logger.info("SECURITY STARTUP VALIDATION COMPLETED");
        logger.info("========================================");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("SecurityStartupListener destroyed");
    }
    
    private String getEnvironment() {
        String env = System.getProperty("spring.profiles.active");
        if (env == null || env.trim().isEmpty()) {
            env = System.getenv("FASHIONSTORE_PROFILE");
        }
        if (env == null || env.trim().isEmpty()) {
            env = System.getenv("ENVIRONMENT");
        }
        if (env == null || env.trim().isEmpty()) {
            env = "development";
        }
        return env;
    }
    
    private String getEnvWithFallback(String varName, String defaultValue) {
        String value = System.getenv(varName);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
    
    private boolean isProduction(String environment) {
        return ENV_PRODUCTION.equalsIgnoreCase(environment) || ENV_PROD.equalsIgnoreCase(environment);
    }
    
    private void validateJwtSecret(String environment) {
        logger.info("Validating JWT_SECRET_KEY...");
        
        String jwtSecret = System.getenv("JWT_SECRET_KEY");
        
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            if (isProduction(environment)) {
                String error = "CRITICAL SECURITY FAILURE: JWT_SECRET_KEY environment variable is not set in production. " +
                               "Application startup ABORTED. This is a security requirement.";
                logger.error(error);
                throw new IllegalStateException(error);
            } else {
                logger.warn("JWT_SECRET_KEY not set - using development mode. Set JWT_SECRET_KEY for production.");
            }
        } else {
            if (jwtSecret.length() < MIN_JWT_SECRET_LENGTH) {
                if (isProduction(environment)) {
                    String error = String.format(
                        "CRITICAL SECURITY FAILURE: JWT_SECRET_KEY is too short (%d chars). " +
                        "Minimum %d characters required for production. Application startup ABORTED.",
                        jwtSecret.length(), MIN_JWT_SECRET_LENGTH
                    );
                    logger.error(error);
                    throw new IllegalStateException(error);
                } else {
                    logger.warn("JWT_SECRET_KEY is too short ({} chars). Minimum {} characters recommended.", 
                                jwtSecret.length(), MIN_JWT_SECRET_LENGTH);
                }
            } else {
                logger.info("JWT_SECRET_KEY validated successfully (length: {} chars)", jwtSecret.length());
            }
        }
    }
    
    private void validateAdminKey(String environment) {
        logger.info("Validating FASHIONSTORE_ADMIN_KEY...");
        
        String adminKey = System.getenv("FASHIONSTORE_ADMIN_KEY");
        
        if (adminKey == null || adminKey.trim().isEmpty()) {
            if (isProduction(environment)) {
                String error = "CRITICAL SECURITY FAILURE: FASHIONSTORE_ADMIN_KEY environment variable is not set in production. " +
                               "Application startup ABORTED. This protects admin account creation.";
                logger.error(error);
                throw new IllegalStateException(error);
            } else {
                logger.warn("FASHIONSTORE_ADMIN_KEY not set - using development mode. Set FASHIONSTORE_ADMIN_KEY for production.");
            }
        } else {
            if (adminKey.length() < MIN_ADMIN_KEY_LENGTH) {
                if (isProduction(environment)) {
                    String error = String.format(
                        "CRITICAL SECURITY FAILURE: FASHIONSTORE_ADMIN_KEY is too short (%d chars). " +
                        "Minimum %d characters required for production. Application startup ABORTED.",
                        adminKey.length(), MIN_ADMIN_KEY_LENGTH
                    );
                    logger.error(error);
                    throw new IllegalStateException(error);
                } else {
                    logger.warn("FASHIONSTORE_ADMIN_KEY is too short ({} chars). Minimum {} characters recommended.", 
                                adminKey.length(), MIN_ADMIN_KEY_LENGTH);
                }
            } else {
                logger.info("FASHIONSTORE_ADMIN_KEY validated successfully (length: {} chars)", adminKey.length());
            }
        }
    }
    
    private void validateDatabaseConfig(String environment) {
        logger.info("Validating database configuration...");
        
        String dbUrl = System.getenv("FASHIONSTORE_DB_URL");
        String dbUser = System.getenv("FASHIONSTORE_DB_USER");
        String dbPassword = System.getenv("FASHIONSTORE_DB_PASSWORD");
        
        if (isProduction(environment)) {
            if (dbUrl == null || dbUrl.trim().isEmpty()) {
                String error = "CRITICAL CONFIGURATION FAILURE: FASHIONSTORE_DB_URL not set in production. " +
                               "Application startup ABORTED.";
                logger.error(error);
                throw new IllegalStateException(error);
            }
            
            if (dbUser == null || dbUser.trim().isEmpty()) {
                String error = "CRITICAL CONFIGURATION FAILURE: FASHIONSTORE_DB_USER not set in production. " +
                               "Application startup ABORTED.";
                logger.error(error);
                throw new IllegalStateException(error);
            }
            
            if (dbPassword == null || dbPassword.trim().isEmpty()) {
                String error = "CRITICAL CONFIGURATION FAILURE: FASHIONSTORE_DB_PASSWORD not set in production. " +
                               "Application startup ABORTED.";
                logger.error(error);
                throw new IllegalStateException(error);
            }
            
            // Check if using SSL in production
            if (!dbUrl.contains("useSSL=true") && !dbUrl.contains("useSSL=true")) {
                logger.warn("Database connection does not use SSL. Enable SSL for production security.");
            }
        }
        
        logger.info("Database configuration validated successfully");
    }
    
    private void validateSecurityHeadersConfig(String environment) {
        logger.info("Validating security headers configuration...");
        
        boolean httpsEnabled = Boolean.parseBoolean(
            getEnvWithFallback("HTTPS_ENABLED", 
            System.getProperty("https.enabled", "false"))
        );
        
        boolean secureCookies = Boolean.parseBoolean(
            getEnvWithFallback("SECURE_COOKIES", 
            System.getProperty("secure.cookies", "false"))
        );
        
        if (isProduction(environment)) {
            if (!httpsEnabled) {
                logger.warn("HTTPS_ENABLED is false in production. Enable HTTPS for security.");
            }
            
            if (!secureCookies) {
                logger.warn("SECURE_COOKIES is false in production. Enable secure cookies for security.");
            }
        }
        
        logger.info("Security headers configuration validated (HTTPS: {}, Secure Cookies: {})", 
                    httpsEnabled, secureCookies);
    }
}
