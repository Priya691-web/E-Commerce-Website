package com.fashionstore.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Request Validator - Centralized input validation and sanitization
 * Prevents SQL injection, XSS, and other common attacks
 */
public class RequestValidator {
    private static final Logger logger = LoggerFactory.getLogger(RequestValidator.class);
    
    // SQL injection patterns
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "('.*(--|;|\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|UNION|EXECUTE)\\b.*')",
        Pattern.CASE_INSENSITIVE
    );
    
    // XSS patterns
    private static final Pattern XSS_PATTERN = Pattern.compile(
        "<script.*?>.*?</script>|<.*?on\\w+.*?>|javascript:",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );
    
    // Path traversal patterns
    private static final Pattern PATH_TRAVERSAL_PATTERN = Pattern.compile(
        "(\\.\\./|\\.\\\\|%2e%2e%2f|%2e%2e%5c)",
        Pattern.CASE_INSENSITIVE
    );
    
    /**
     * Validate string input with length check
     */
    public static ValidationResult validateString(String input, String fieldName, int maxLength) {
        ValidationResult result = new ValidationResult();
        
        if (input == null || input.trim().isEmpty()) {
            result.setValid(false);
            result.setError(fieldName + " is required");
            return result;
        }
        
        if (input.length() > maxLength) {
            result.setValid(false);
            result.setError(fieldName + " exceeds maximum length of " + maxLength);
            return result;
        }
        
        // Check for SQL injection
        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            logger.warn("SQL injection attempt detected in field: {}", fieldName);
            result.setValid(false);
            result.setError("Invalid input detected");
            return result;
        }
        
        // Check for XSS
        if (XSS_PATTERN.matcher(input).find()) {
            logger.warn("XSS attempt detected in field: {}", fieldName);
            result.setValid(false);
            result.setError("Invalid input detected");
            return result;
        }
        
        // Check for path traversal
        if (PATH_TRAVERSAL_PATTERN.matcher(input).find()) {
            logger.warn("Path traversal attempt detected in field: {}", fieldName);
            result.setValid(false);
            result.setError("Invalid input detected");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    /**
     * Validate optional string input
     */
    public static ValidationResult validateOptionalString(String input, String fieldName, int maxLength) {
        if (input == null || input.trim().isEmpty()) {
            ValidationResult result = new ValidationResult();
            result.setValid(true);
            return result;
        }
        return validateString(input, fieldName, maxLength);
    }
    
    /**
     * Validate email format
     */
    public static ValidationResult validateEmail(String email) {
        ValidationResult result = new ValidationResult();
        
        if (email == null || email.trim().isEmpty()) {
            result.setValid(false);
            result.setError("Email is required");
            return result;
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailRegex)) {
            result.setValid(false);
            result.setError("Invalid email format");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    /**
     * Validate phone number (international format)
     */
    public static ValidationResult validatePhone(String phone) {
        ValidationResult result = new ValidationResult();
        
        if (phone == null || phone.trim().isEmpty()) {
            result.setValid(false);
            result.setError("Phone number is required");
            return result;
        }
        
        // Remove common separators
        String cleanedPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        
        // Validate phone number (10-15 digits)
        if (!cleanedPhone.matches("^\\+?[1-9]\\d{9,14}$")) {
            result.setValid(false);
            result.setError("Invalid phone number format");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    /**
     * Validate numeric input within range
     */
    public static ValidationResult validateNumber(Number value, String fieldName, double min, double max) {
        ValidationResult result = new ValidationResult();
        
        if (value == null) {
            result.setValid(false);
            result.setError(fieldName + " is required");
            return result;
        }
        
        double doubleValue = value.doubleValue();
        if (doubleValue < min || doubleValue > max) {
            result.setValid(false);
            result.setError(fieldName + " must be between " + min + " and " + max);
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    /**
     * Validate positive integer
     */
    public static ValidationResult validatePositiveInteger(Integer value, String fieldName) {
        ValidationResult result = new ValidationResult();
        
        if (value == null) {
            result.setValid(false);
            result.setError(fieldName + " is required");
            return result;
        }
        
        if (value <= 0) {
            result.setValid(false);
            result.setError(fieldName + " must be positive");
            return result;
        }
        
        result.setValid(true);
        return result;
    }
    
    /**
     * Sanitize string input (HTML escaping)
     */
    public static String sanitizeString(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("<", "&lt;")
                   .replaceAll(">", "&gt;")
                   .replaceAll("\"", "&quot;")
                   .replaceAll("'", "&#x27;")
                   .replaceAll("/", "&#x2F;");
    }
    
    /**
     * Sanitize for SQL (basic escaping)
     * Note: This is NOT a substitute for prepared statements
     */
    public static String sanitizeForSQL(String input) {
        if (input == null) return null;
        return input.trim()
                   .replaceAll("'", "''")
                   .replaceAll("\\\\", "\\\\\\\\");
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private String error;
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}
