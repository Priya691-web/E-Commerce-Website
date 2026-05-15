package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT (JSON Web Token) Utility for Secure Token Management
 * Provides secure JWT generation, validation, and claims management
 */
public class JWTUtil {
    private static final Logger logger = LoggerFactory.getLogger(JWTUtil.class);
    
    // JWT configuration
    private static final String SECRET_KEY = getSecretKey();
    private static final String ALGORITHM = "HmacSHA256";
    private static final long TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 days
    
    // JWT claims
    private static final String ISSUER = "FashionStore";
    private static final String AUDIENCE = "FashionStoreUsers";
    
    /**
     * Get secret key from environment variable or use default
     */
    private static String getSecretKey() {
        String key = System.getenv("JWT_SECRET_KEY");
        if (key == null || key.isEmpty()) {
            logger.warn("JWT_SECRET_KEY not set in environment, using default (not recommended for production)");
            key = "FashionStoreDefaultSecretKey2026-ChangeInProduction";
        }
        return key;
    }
    
    /**
     * Generate JWT token
     */
    public static String generateToken(String userId, String email, String role) {
        return generateToken(userId, email, role, TOKEN_EXPIRATION_MS);
    }
    
    /**
     * Generate JWT token with custom expiration
     */
    public static String generateToken(String userId, String email, String role, long expirationMs) {
        try {
            long now = System.currentTimeMillis();
            long expiration = now + expirationMs;
            
            // Header
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String encodedHeader = base64UrlEncode(header.toString());
            
            // Payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", userId);
            payload.put("email", email);
            payload.put("role", role);
            payload.put("iss", ISSUER);
            payload.put("aud", AUDIENCE);
            payload.put("iat", now);
            payload.put("exp", expiration);
            payload.put("jti", generateJTI());
            String encodedPayload = base64UrlEncode(payload.toString());
            
            // Signature
            String signature = generateSignature(encodedHeader + "." + encodedPayload);
            
            return encodedHeader + "." + encodedPayload + "." + signature;
            
        } catch (Exception e) {
            logger.error("Error generating JWT token: {}", e.getMessage(), e);
            throw new SecurityException("Failed to generate token");
        }
    }
    
    /**
     * Generate refresh token
     */
    public static String generateRefreshToken(String userId) {
        try {
            long now = System.currentTimeMillis();
            long expiration = now + REFRESH_TOKEN_EXPIRATION_MS;
            
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            String encodedHeader = base64UrlEncode(header.toString());
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("sub", userId);
            payload.put("type", "refresh");
            payload.put("iss", ISSUER);
            payload.put("aud", AUDIENCE);
            payload.put("iat", now);
            payload.put("exp", expiration);
            payload.put("jti", generateJTI());
            String encodedPayload = base64UrlEncode(payload.toString());
            
            String signature = generateSignature(encodedHeader + "." + encodedPayload);
            
            return encodedHeader + "." + encodedPayload + "." + signature;
            
        } catch (Exception e) {
            logger.error("Error generating refresh token: {}", e.getMessage(), e);
            throw new SecurityException("Failed to generate refresh token");
        }
    }
    
    /**
     * Validate JWT token
     */
    public static TokenValidationResult validateToken(String token) {
        TokenValidationResult result = new TokenValidationResult();
        
        try {
            if (token == null || token.isEmpty()) {
                result.setValid(false);
                result.setError("Token is null or empty");
                return result;
            }
            
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                result.setValid(false);
                result.setError("Invalid token format");
                return result;
            }
            
            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];
            
            // Verify signature
            String expectedSignature = generateSignature(header + "." + payload);
            if (!signature.equals(expectedSignature)) {
                result.setValid(false);
                result.setError("Invalid token signature");
                return result;
            }
            
            // Decode payload
            String decodedPayload = base64UrlDecode(payload);
            Map<String, Object> claims = parseClaims(decodedPayload);
            
            // Check expiration
            long exp = ((Number) claims.get("exp")).longValue();
            if (System.currentTimeMillis() > exp) {
                result.setValid(false);
                result.setError("Token has expired");
                return result;
            }
            
            // Check issuer
            if (!claims.get("iss").equals(ISSUER)) {
                result.setValid(false);
                result.setError("Invalid token issuer");
                return result;
            }
            
            // Check audience
            if (!claims.get("aud").equals(AUDIENCE)) {
                result.setValid(false);
                result.setError("Invalid token audience");
                return result;
            }
            
            // Check if refresh token (refresh tokens should not be used for API access)
            if (claims.containsKey("type") && "refresh".equals(claims.get("type"))) {
                result.setValid(false);
                result.setError("Refresh token cannot be used for API access");
                return result;
            }
            
            result.setValid(true);
            result.setUserId((String) claims.get("sub"));
            result.setEmail((String) claims.get("email"));
            result.setRole((String) claims.get("role"));
            result.setClaims(claims);
            
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage(), e);
            result.setValid(false);
            result.setError("Token validation failed");
        }
        
        return result;
    }
    
    /**
     * Refresh token
     */
    public static String refreshToken(String refreshToken) {
        TokenValidationResult result = validateToken(refreshToken);
        
        if (!result.isValid()) {
            throw new SecurityException("Invalid refresh token");
        }
        
        Map<String, Object> claims = result.getClaims();
        if (!claims.containsKey("type") || !"refresh".equals(claims.get("type"))) {
            throw new SecurityException("Not a refresh token");
        }
        
        String userId = (String) claims.get("sub");
        String email = (String) claims.get("email");
        String role = (String) claims.get("role");
        
        return generateToken(userId, email, role);
    }
    
    /**
     * Generate signature
     */
    private static String generateSignature(String data) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        mac.init(secretKeySpec);
        byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return base64UrlEncode(new String(signature, StandardCharsets.ISO_8859_1));
    }
    
    /**
     * Base64 URL encode
     */
    private static String base64UrlEncode(String input) {
        return Base64.getUrlEncoder()
                      .withoutPadding()
                      .encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base64 URL decode
     */
    private static String base64UrlDecode(String input) {
        byte[] decoded = Base64.getUrlDecoder().decode(input);
        return new String(decoded, StandardCharsets.UTF_8);
    }
    
    /**
     * Generate JTI (JWT ID)
     */
    private static String generateJTI() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Parse claims from payload
     */
    private static Map<String, Object> parseClaims(String payload) {
        Map<String, Object> claims = new HashMap<>();
        // Simple JSON parsing (in production, use a proper JSON library)
        String[] pairs = payload.replace("{", "").replace("}", "").split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.split(":", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim().replace("\"", "");
                String value = keyValue[1].trim().replace("\"", "");
                
                // Try to parse as number
                try {
                    if (value.contains(".")) {
                        claims.put(key, Double.parseDouble(value));
                    } else {
                        claims.put(key, Long.parseLong(value));
                    }
                } catch (NumberFormatException e) {
                    claims.put(key, value);
                }
            }
        }
        return claims;
    }
    
    /**
     * Get token expiration time
     */
    public static long getTokenExpirationTime(String token) {
        TokenValidationResult result = validateToken(token);
        if (result.isValid() && result.getClaims() != null) {
            return ((Number) result.getClaims().get("exp")).longValue();
        }
        return 0;
    }
    
    /**
     * Check if token is expired
     */
    public static boolean isTokenExpired(String token) {
        long expirationTime = getTokenExpirationTime(token);
        return expirationTime > 0 && System.currentTimeMillis() > expirationTime;
    }
    
    /**
     * Get time until token expiration
     */
    public static long getTimeUntilExpiration(String token) {
        long expirationTime = getTokenExpirationTime(token);
        if (expirationTime == 0) {
            return 0;
        }
        return Math.max(0, expirationTime - System.currentTimeMillis());
    }
    
    /**
     * Token validation result class
     */
    public static class TokenValidationResult {
        private boolean valid;
        private String error;
        private String userId;
        private String email;
        private String role;
        private Map<String, Object> claims;
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
        
        public String getUserId() {
            return userId;
        }
        
        public void setUserId(String userId) {
            this.userId = userId;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public Map<String, Object> getClaims() {
            return claims;
        }
        
        public void setClaims(Map<String, Object> claims) {
            this.claims = claims;
        }
    }
}
