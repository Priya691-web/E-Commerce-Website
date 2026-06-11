package com.fashionstore.dto.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DTO Validator
 * Centralized validation logic for Data Transfer Objects
 * Provides reusable validation methods for common data types
 */
public class DtoValidator {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    // Phone validation pattern (international)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[1-9]\\d{1,14}$");
    
    // URL validation pattern
    private static final Pattern URL_PATTERN = 
        Pattern.compile("^(https?|ftp)://[^\\s/$.?#].[^\\s]*$");
    
    /**
     * Validate email format
     * @param email Email to validate
     * @return True if valid
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validate phone number format
     * @param phone Phone number to validate
     * @return True if valid
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * Validate URL format
     * @param url URL to validate
     * @return True if valid
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url.trim()).matches();
    }
    
    /**
     * Validate string length
     * @param value String to validate
     * @param minLength Minimum length (inclusive)
     * @param maxLength Maximum length (inclusive)
     * @return True if valid
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return minLength == 0;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validate numeric range
     * @param value Number to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return True if valid
     */
    public static boolean isValidRange(Number value, double min, double max) {
        if (value == null) {
            return false;
        }
        double doubleValue = value.doubleValue();
        return doubleValue >= min && doubleValue <= max;
    }
    
    /**
     * Validate positive number
     * @param value Number to validate
     * @return True if valid and positive
     */
    public static boolean isPositive(Number value) {
        if (value == null) {
            return false;
        }
        return value.doubleValue() > 0;
    }
    
    /**
     * Validate non-negative number
     * @param value Number to validate
     * @return True if valid and non-negative
     */
    public static boolean isNonNegative(Number value) {
        if (value == null) {
            return false;
        }
        return value.doubleValue() >= 0;
    }
    
    /**
     * Validate required field
     * @param value Value to validate
     * @return True if not null and not empty
     */
    public static boolean isRequired(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String) {
            return !((String) value).trim().isEmpty();
        }
        if (value instanceof List) {
            return !((List<?>) value).isEmpty();
        }
        return true;
    }
    
    /**
     * Validate enum value
     * @param value Value to validate
     * @param enumClass Enum class
     * @return True if valid enum value
     */
    public static boolean isValidEnum(String value, Class<? extends Enum<?>> enumClass) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        try {
            Enum.valueOf(enumClass.asSubclass(Enum.class), value.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Validate password strength
     * @param password Password to validate
     * @return True if meets minimum requirements
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasLower = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = !password.matches("[A-Za-z0-9]*");
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
        
        public ValidationResult() {
            this.valid = true;
            this.errors = new ArrayList<>();
        }
        
        public void addError(String error) {
            this.valid = false;
            this.errors.add(error);
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
        
        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
    
    /**
     * Create new validation result
     * @return New ValidationResult instance
     */
    public static ValidationResult createResult() {
        return new ValidationResult();
    }
}
