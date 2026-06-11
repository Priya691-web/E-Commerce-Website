package com.fashionstore.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CookieSecurityUtil - Centralized cookie security configuration
 * 
 * ROOT CAUSE: Cookie security settings were scattered across controllers, leading to inconsistent
 * security configurations and potential vulnerabilities.
 * 
 * FIX: Centralized cookie configuration with production-ready security settings including
 * SameSite, HttpOnly, Secure, and Path attributes.
 */
public class CookieSecurityUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(CookieSecurityUtil.class);
    
    // Phase 1.2 & 1.3: Cookie security configuration
    private static final String SAME_SITE_STRICT = "Strict";
    private static final String SAME_SITE_LAX = "Lax";
    
    private static final String COOKIE_PATH = "/";
    private static final int ACCESS_TOKEN_MAX_AGE = 15 * 60; // 15 minutes
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7 days
    
    /**
     * Get environment variable with fallback
     */
    private static String getEnvWithFallback(String varName, String defaultValue) {
        String value = System.getenv(varName);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
    
    /**
     * Determine if we're in production mode
     */
    private static boolean isProduction() {
        String env = System.getProperty("spring.profiles.active");
        if (env == null || env.trim().isEmpty()) {
            env = getEnvWithFallback("FASHIONSTORE_PROFILE", 
                 getEnvWithFallback("ENVIRONMENT", "development"));
        }
        return "production".equalsIgnoreCase(env) || "prod".equalsIgnoreCase(env);
    }
    
    /**
     * Get the appropriate SameSite attribute value based on environment
     */
    private static String getSameSiteValue() {
        // In production with HTTPS, use Strict for maximum security
        // In development, use Lax to avoid issues with local testing
        boolean httpsEnabled = Boolean.parseBoolean(
            getEnvWithFallback("HTTPS_ENABLED", 
            System.getProperty("https.enabled", "false"))
        );
        
        if (httpsEnabled) {
            return SAME_SITE_STRICT;
        } else {
            return SAME_SITE_LAX;
        }
    }
    
    /**
     * Check if cookies should be marked as Secure
     */
    private static boolean shouldUseSecureCookies() {
        return Boolean.parseBoolean(
            getEnvWithFallback("SECURE_COOKIES", 
            System.getProperty("secure.cookies", String.valueOf(isProduction())))
        );
    }
    
    /**
     * Create a secure access token cookie
     * Phase 1.2: SameSite cookie configuration
     * Phase 1.3: Secure HTTP-only cookies for production
     */
    public static Cookie createAccessTokenCookie(String tokenValue, HttpServletRequest request) {
        Cookie cookie = new Cookie("access_token", tokenValue);
        
        // Phase 1.3: HttpOnly prevents JavaScript access (XSS protection)
        cookie.setHttpOnly(true);
        
        // Phase 1.3: Secure flag prevents transmission over non-HTTPS
        cookie.setSecure(shouldUseSecureCookies() || request.isSecure());
        
        // Phase 1.2: SameSite attribute prevents CSRF
        String sameSite = getSameSiteValue();
        cookie.setAttribute("SameSite", sameSite);
        
        // Set path and max age
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(ACCESS_TOKEN_MAX_AGE);
        
        logger.debug("Created access token cookie (SameSite: {}, Secure: {}, HttpOnly: {})", 
                     sameSite, cookie.getSecure(), cookie.isHttpOnly());
        
        return cookie;
    }
    
    /**
     * Create a secure refresh token cookie
     * Phase 1.2: SameSite cookie configuration
     * Phase 1.3: Secure HTTP-only cookies for production
     */
    public static Cookie createRefreshTokenCookie(String tokenValue, HttpServletRequest request) {
        Cookie cookie = new Cookie("refresh_token", tokenValue);
        
        // Phase 1.3: HttpOnly prevents JavaScript access (XSS protection)
        cookie.setHttpOnly(true);
        
        // Phase 1.3: Secure flag prevents transmission over non-HTTPS
        cookie.setSecure(shouldUseSecureCookies() || request.isSecure());
        
        // Phase 1.2: SameSite attribute prevents CSRF
        String sameSite = getSameSiteValue();
        cookie.setAttribute("SameSite", sameSite);
        
        // Set path and max age
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        
        logger.debug("Created refresh token cookie (SameSite: {}, Secure: {}, HttpOnly: {})", 
                     sameSite, cookie.getSecure(), cookie.isHttpOnly());
        
        return cookie;
    }
    
    /**
     * Create a cookie clearing cookie (to delete a cookie)
     */
    public static Cookie createClearCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        
        // Set SameSite for consistency
        String sameSite = getSameSiteValue();
        cookie.setAttribute("SameSite", sameSite);
        
        logger.debug("Created clear cookie for {} (SameSite: {})", cookieName, sameSite);
        
        return cookie;
    }
    
    /**
     * Validate that a cookie meets security requirements
     */
    public static boolean isCookieSecure(Cookie cookie) {
        if (cookie == null) {
            return false;
        }
        
        boolean httpOnly = cookie.isHttpOnly();
        boolean secure = cookie.getSecure();
        String sameSite = cookie.getAttribute("SameSite");
        
        boolean isSecure = httpOnly && 
                          (secure || !isProduction()) && 
                          (sameSite != null && !sameSite.isEmpty());
        
        if (!isSecure && isProduction()) {
            logger.warn("Cookie {} does not meet production security requirements (HttpOnly: {}, Secure: {}, SameSite: {})", 
                        cookie.getName(), httpOnly, secure, sameSite);
        }
        
        return isSecure;
    }
}
