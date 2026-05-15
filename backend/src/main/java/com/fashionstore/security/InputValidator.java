package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Comprehensive Input Validation for Security
 * Prevents XSS, SQL injection, and other injection attacks
 */
public class InputValidator {
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);

    // XSS patterns
    private static final Pattern[] XSS_PATTERNS = {
        Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<iframe[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<object[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<embed[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<link[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<meta[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<style[^>]*>.*?</style>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("<img[^>]*on\\w+[^>]*>", Pattern.CASE_INSENSITIVE),
        Pattern.compile("eval\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        Pattern.compile("data:text/html", Pattern.CASE_INSENSITIVE)
    };

    // SQL injection patterns
    private static final Pattern[] SQL_INJECTION_PATTERNS = {
        Pattern.compile("'('|'|')|;|\\b(ALTER|CREATE|DELETE|DROP|EXEC(UTE)?|INSERT|SELECT|UNION|UPDATE)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(OR|AND)\\s+\\d+\\s*=\\s*\\d+", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(OR|AND)\\s+['\"]?\\w+['\"]?\\s*=\\s*['\"]?\\w+['\"]?", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(--|#|\\/\\*|\\*\\/)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(SCRIPT|JAVASCRIPT|VBSCRIPT|ONLOAD|ONERROR)\\b", Pattern.CASE_INSENSITIVE)
    };

    // Path traversal patterns
    private static final Pattern[] PATH_TRAVERSAL_PATTERNS = {
        Pattern.compile("\\.\\."),
        Pattern.compile("(/|\\\\)etc(/|\\\\)passwd", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(/|\\\\)bin(/|\\\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(/|\\\\)usr(/|\\\\)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(%2e%2e)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(%2f)", Pattern.CASE_INSENSITIVE),
        Pattern.compile("(%5c)", Pattern.CASE_INSENSITIVE)
    };

    // Command injection patterns
    private static final Pattern[] COMMAND_INJECTION_PATTERNS = {
        Pattern.compile("(;|\\||&)"),
        Pattern.compile("\\$\\("),
        Pattern.compile("\\`[^\\`]*\\`"),
        Pattern.compile("\\$\\{[^}]*\\}"),
        Pattern.compile("\\b(curl|wget|nc|netcat|telnet|ssh|ftp)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(rm|mv|cp|chmod|chown)\\b", Pattern.CASE_INSENSITIVE)
    };

    /**
     * Sanitize input to prevent XSS attacks
     */
    public static String sanitizeXSS(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        String sanitized = input;
        
        // Remove XSS patterns
        for (Pattern pattern : XSS_PATTERNS) {
            sanitized = pattern.matcher(sanitized).replaceAll("");
        }

        // HTML entity encoding
        sanitized = sanitized.replace("&", "&amp;")
                           .replace("<", "&lt;")
                           .replace(">", "&gt;")
                           .replace("\"", "&quot;")
                           .replace("'", "&#x27;");

        return sanitized;
    }

    /**
     * Check for SQL injection patterns
     */
    public static boolean containsSQLInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        String upperInput = input.toUpperCase();
        for (Pattern pattern : SQL_INJECTION_PATTERNS) {
            if (pattern.matcher(upperInput).find()) {
                logger.warn("SQL injection pattern detected: {}", input);
                return true;
            }
        }
        return false;
    }

    /**
     * Check for XSS patterns
     */
    public static boolean containsXSS(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (Pattern pattern : XSS_PATTERNS) {
            if (pattern.matcher(input).find()) {
                logger.warn("XSS pattern detected: {}", input);
                return true;
            }
        }
        return false;
    }

    /**
     * Check for path traversal patterns
     */
    public static boolean containsPathTraversal(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (Pattern pattern : PATH_TRAVERSAL_PATTERNS) {
            if (pattern.matcher(input).find()) {
                logger.warn("Path traversal pattern detected: {}", input);
                return true;
            }
        }
        return false;
    }

    /**
     * Check for command injection patterns
     */
    public static boolean containsCommandInjection(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        for (Pattern pattern : COMMAND_INJECTION_PATTERNS) {
            if (pattern.matcher(input).find()) {
                logger.warn("Command injection pattern detected: {}", input);
                return true;
            }
        }
        return false;
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex) && email.length() <= 254;
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        
        // Allow international formats: +1-234-567-8900, 123-456-7890, 1234567890
        String phoneRegex = "^[+]?[0-9\\-\\s()]{7,20}$";
        return phone.matches(phoneRegex);
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        
        // Minimum 8 characters, at least one uppercase, one lowercase, one digit
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
        return password.matches(passwordRegex) && password.length() <= 128;
    }

    /**
     * Validate name (letters, spaces, hyphens, apostrophes only)
     */
    public static boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        
        String nameRegex = "^[a-zA-Z\\s\\-']{2,100}$";
        return name.matches(nameRegex);
    }

    /**
     * Validate URL format
     */
    public static boolean isValidURL(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        try {
            new java.net.URL(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate numeric input
     */
    public static boolean isValidNumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate integer input
     */
    public static boolean isValidInteger(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate positive integer
     */
    public static boolean isValidPositiveInteger(String input) {
        if (!isValidInteger(input)) {
            return false;
        }
        
        try {
            return Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Sanitize and validate input
     */
    public static String sanitizeAndValidate(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        // Check for injection attacks
        if (containsSQLInjection(input) || 
            containsXSS(input) || 
            containsCommandInjection(input)) {
            throw new SecurityException("Input contains malicious patterns");
        }

        // Sanitize XSS
        return sanitizeXSS(input);
    }

    /**
     * Validate input length
     */
    public static boolean isValidLength(String input, int minLength, int maxLength) {
        if (input == null) {
            return false;
        }
        
        return input.length() >= minLength && input.length() <= maxLength;
    }

    /**
     * Validate alphanumeric input
     */
    public static boolean isAlphanumeric(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        return input.matches("^[a-zA-Z0-9]+$");
    }

    /**
     * Validate alphanumeric with spaces
     */
    public static boolean isAlphanumericWithSpaces(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        
        return input.matches("^[a-zA-Z0-9\\s]+$");
    }

    /**
     * Validate date format (YYYY-MM-DD)
     */
    public static boolean isValidDate(String date) {
        if (date == null || date.isEmpty()) {
            return false;
        }
        
        String dateRegex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!date.matches(dateRegex)) {
            return false;
        }
        
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate IP address (IPv4)
     */
    public static boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        String ipRegex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        return ip.matches(ipRegex);
    }

    /**
     * Validate IP address (IPv6)
     */
    public static boolean isValidIPv6(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        
        String ipRegex = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
        return ip.matches(ipRegex);
    }

    /**
     * Sanitize file name
     */
    public static String sanitizeFileName(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }

        // Remove path traversal and special characters
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // Limit length
        if (sanitized.length() > 255) {
            sanitized = sanitized.substring(0, 255);
        }

        return sanitized;
    }

    /**
     * Validate file extension
     */
    public static boolean isValidFileExtension(String fileName, String[] allowedExtensions) {
        if (fileName == null || fileName.isEmpty() || allowedExtensions == null) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        
        for (String allowed : allowedExtensions) {
            if (allowed.toLowerCase().equals(extension)) {
                return true;
            }
        }
        
        return false;
    }
}
