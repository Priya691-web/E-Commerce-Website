package com.fashionstore.util;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * ValidationUtils - Centralized Input Validation and NPE Prevention
 * 
 * Provides comprehensive validation utilities for:
 * - Null pointer exception prevention
 * - Input sanitization
 * - Business rule validation
 * - Type safety checks
 * 
 * All validation methods return safe defaults instead of throwing exceptions
 * to prevent application crashes from invalid input.
 */
public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9+\\-\\s()]{10,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]{2,100}$");
    private static final Pattern POSTAL_CODE_PATTERN = Pattern.compile("^[A-Za-z0-9\\-\\s]{3,10}$");

    // ================= NULL SAFETY METHODS =================

    /**
     * Safe string getter - returns empty string if null
     */
    public static String safeString(String value) {
        return value == null ? "" : value.trim();
    }

    /**
     * Safe string getter with default
     */
    public static String safeString(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value.trim();
    }

    /**
     * Safe integer getter - returns 0 if null or invalid
     */
    public static int safeInt(Integer value) {
        return safeInt(value, 0);
    }

    /**
     * Safe integer getter with default
     */
    public static int safeInt(Integer value, int defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * Safe integer getter from string
     */
    public static int safeIntFromString(String value) {
        return safeIntFromString(value, 0);
    }

    /**
     * Safe integer getter from string with default
     */
    public static int safeIntFromString(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safe long getter - returns 0 if null or invalid
     */
    public static long safeLong(Long value) {
        return safeLong(value, 0L);
    }

    /**
     * Safe long getter with default
     */
    public static long safeLong(Long value, long defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * Safe double getter - returns 0.0 if null or invalid
     */
    public static double safeDouble(Double value) {
        return safeDouble(value, 0.0);
    }

    /**
     * Safe double getter with default
     */
    public static double safeDouble(Double value, double defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * Safe double getter from string
     */
    public static double safeDoubleFromString(String value) {
        return safeDoubleFromString(value, 0.0);
    }

    /**
     * Safe double getter from string with default
     */
    public static double safeDoubleFromString(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safe boolean getter - returns false if null
     */
    public static boolean safeBoolean(Boolean value) {
        return safeBoolean(value, false);
    }

    /**
     * Safe boolean getter with default
     */
    public static boolean safeBoolean(Boolean value, boolean defaultValue) {
        return value == null ? defaultValue : value;
    }

    /**
     * Safe boolean getter from string
     */
    public static boolean safeBooleanFromString(String value) {
        return safeBooleanFromString(value, false);
    }

    /**
     * Safe boolean getter from string with default
     */
    public static boolean safeBooleanFromString(String value, boolean defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        String trimmed = value.trim().toLowerCase();
        return "true".equals(trimmed) || "yes".equals(trimmed) || "1".equals(trimmed);
    }

    // ================= INPUT VALIDATION METHODS =================

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validate phone format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validate name format (letters, spaces, hyphens, apostrophes)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validate postal code format
     */
    public static boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return false;
        }
        return POSTAL_CODE_PATTERN.matcher(postalCode.trim()).matches();
    }

    /**
     * Validate positive number
     */
    public static boolean isPositive(Number value) {
        return value != null && value.doubleValue() > 0;
    }

    /**
     * Validate non-negative number
     */
    public static boolean isNonNegative(Number value) {
        return value != null && value.doubleValue() >= 0;
    }

    /**
     * Validate string length
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Validate collection is not null or empty
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && !collection.isEmpty();
    }

    /**
     * Validate map is not null or empty
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    /**
     * Validate string is not null or empty
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // ================= SANITIZATION METHODS =================

    /**
     * Sanitize string - remove potentially dangerous characters
     */
    public static String sanitizeString(String value) {
        if (value == null) {
            return "";
        }
        // Remove null bytes and control characters
        return value.trim().replaceAll("[\\x00-\\x1F\\x7F]", "");
    }

    /**
     * Sanitize for SQL - basic SQL injection prevention
     * Note: This is a basic check - always use prepared statements
     */
    public static boolean containsSqlInjection(String value) {
        if (value == null) {
            return false;
        }
        String lower = value.toLowerCase();
        String[] sqlKeywords = {"select", "insert", "update", "delete", "drop", "union", "exec", "script", "javascript", "alert"};
        
        for (String keyword : sqlKeywords) {
            if (lower.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sanitize for XSS - basic XSS prevention
     */
    public static String sanitizeForXss(String value) {
        if (value == null) {
            return "";
        }
        return value.trim()
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("&", "&amp;");
    }

    // ================= BUSINESS RULE VALIDATION =================

    /**
     * Validate quantity (must be positive, max 100)
     */
    public static boolean isValidQuantity(int quantity) {
        return quantity > 0 && quantity <= 100;
    }

    /**
     * Validate price (must be non-negative)
     */
    public static boolean isValidPrice(double price) {
        return price >= 0 && price <= 1000000; // Max 1 million
    }

    /**
     * Validate discount percentage (0-100)
     */
    public static boolean isValidDiscountPercent(double discount) {
        return discount >= 0 && discount <= 100;
    }

    /**
     * Validate user role
     */
    public static boolean isValidRole(String role) {
        if (role == null) {
            return false;
        }
        String normalized = role.trim().toLowerCase();
        return "customer".equals(normalized) || "admin".equals(normalized);
    }

    /**
     * Validate payment method
     */
    public static boolean isValidPaymentMethod(String method) {
        if (method == null) {
            return false;
        }
        String normalized = method.trim().toUpperCase();
        return "COD".equals(normalized) || "STRIPE".equals(normalized) || "RAZORPAY".equals(normalized);
    }

    // ================= REQUIRED FIELD VALIDATION =================

    /**
     * Validate required string field
     */
    public static boolean isRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * Validate required number field
     */
    public static boolean isRequired(Number value, String fieldName) {
        return value != null;
    }

    /**
     * Validate required object field
     */
    public static boolean isRequired(Object value, String fieldName) {
        return value != null;
    }

    // ================= RANGE VALIDATION =================

    /**
     * Validate number in range
     */
    public static boolean isInRange(Number value, double min, double max) {
        if (value == null) {
            return false;
        }
        double numValue = value.doubleValue();
        return numValue >= min && numValue <= max;
    }

    /**
     * Validate string length in range
     */
    public static boolean isLengthInRange(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    // ================= COLLECTION VALIDATION =================

    /**
     * Validate collection size
     */
    public static boolean isValidSize(Collection<?> collection, int maxSize) {
        if (collection == null) {
            return false;
        }
        return collection.size() <= maxSize;
    }

    /**
     * Validate collection has minimum size
     */
    public static boolean hasMinimumSize(Collection<?> collection, int minSize) {
        if (collection == null) {
            return false;
        }
        return collection.size() >= minSize;
    }

    // ================= OBJECT VALIDATION =================

    /**
     * Safe equals - prevents NPE
     */
    public static boolean safeEquals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    /**
     * Safe hash code - prevents NPE
     */
    public static int safeHashCode(Object obj) {
        return Objects.hashCode(obj);
    }

    /**
     * Check if all required fields are present in an object
     */
    public static boolean validateRequiredFields(Object obj, String... fieldNames) {
        if (obj == null) {
            return false;
        }
        
        try {
            for (String fieldName : fieldNames) {
                java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                Object value = field.get(obj);
                
                if (value == null) {
                    return false;
                }
                
                if (value instanceof String && ((String) value).trim().isEmpty()) {
                    return false;
                }
                
                if (value instanceof Collection && ((Collection<?>) value).isEmpty()) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
