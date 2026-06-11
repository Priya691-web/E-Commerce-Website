package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * XSSProtectionUtil - Cross-Site Scripting (XSS) protection and input sanitization
 * 
 * ROOT CAUSE: Without input sanitization, malicious users can inject scripts into the application
 * via form inputs, URL parameters, or API requests, leading to XSS attacks that steal cookies,
 * redirect users, or perform unauthorized actions.
 * 
 * FIX: This utility provides comprehensive XSS protection by sanitizing and escaping all user input
 * before it's used in the application, following OWASP best practices.
 */
public class XSSProtectionUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(XSSProtectionUtil.class);
    
    // Phase 1.5: XSS sanitization configuration
    private static final int MAX_INPUT_LENGTH = 10000; // Maximum input length for text fields
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MAX_PHONE_LENGTH = 20;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_ADDRESS_LENGTH = 500;
    
    /**
     * Sanitize a string input to prevent XSS attacks
     * Phase 1.5: XSS sanitization layer
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Trim whitespace
        String sanitized = input.trim();
        
        // Check maximum length
        if (sanitized.length() > MAX_INPUT_LENGTH) {
            logger.warn("Input exceeds maximum length of {} characters, truncating", MAX_INPUT_LENGTH);
            sanitized = sanitized.substring(0, MAX_INPUT_LENGTH);
        }
        
        // HTML entity encoding (custom implementation)
        sanitized = encodeForHtml(sanitized);
        
        // Remove null bytes
        sanitized = sanitized.replace("\0", "");
        
        return sanitized;
    }
    
    /**
     * Sanitize an email address
     */
    public static String sanitizeEmail(String email) {
        if (email == null) {
            return null;
        }
        
        String sanitized = email.trim().toLowerCase();
        
        if (sanitized.length() > MAX_EMAIL_LENGTH) {
            logger.warn("Email exceeds maximum length of {} characters", MAX_EMAIL_LENGTH);
            return null;
        }
        
        // HTML encode to prevent XSS
        sanitized = encodeForHtml(sanitized);
        
        return sanitized;
    }
    
    /**
     * Sanitize a phone number
     */
    public static String sanitizePhone(String phone) {
        if (phone == null) {
            return null;
        }
        
        String sanitized = phone.trim();
        
        if (sanitized.length() > MAX_PHONE_LENGTH) {
            logger.warn("Phone exceeds maximum length of {} characters", MAX_PHONE_LENGTH);
            return null;
        }
        
        // Remove all non-digit characters (except + for international format)
        sanitized = sanitized.replaceAll("[^0-9+]", "");
        
        return sanitized;
    }
    
    /**
     * Sanitize a name (first name, last name, full name)
     */
    public static String sanitizeName(String name) {
        if (name == null) {
            return null;
        }
        
        String sanitized = name.trim();
        
        if (sanitized.length() > MAX_NAME_LENGTH) {
            logger.warn("Name exceeds maximum length of {} characters", MAX_NAME_LENGTH);
            return null;
        }
        
        // HTML encode to prevent XSS
        sanitized = encodeForHtml(sanitized);
        
        // Remove special characters that shouldn't be in names
        sanitized = sanitized.replaceAll("[<>\"'&]", "");
        
        return sanitized;
    }
    
    /**
     * Sanitize an address
     */
    public static String sanitizeAddress(String address) {
        if (address == null) {
            return null;
        }
        
        String sanitized = address.trim();
        
        if (sanitized.length() > MAX_ADDRESS_LENGTH) {
            logger.warn("Address exceeds maximum length of {} characters", MAX_ADDRESS_LENGTH);
            return null;
        }
        
        // HTML encode to prevent XSS
        sanitized = encodeForHtml(sanitized);
        
        return sanitized;
    }
    
    /**
     * Sanitize a URL parameter
     */
    public static String sanitizeUrlParam(String param) {
        if (param == null) {
            return null;
        }
        
        String sanitized = param.trim();
        
        if (sanitized.length() > MAX_INPUT_LENGTH) {
            logger.warn("URL parameter exceeds maximum length of {} characters", MAX_INPUT_LENGTH);
            return null;
        }
        
        // URL encoding for safe use in URLs
        sanitized = encodeForUrl(sanitized);
        
        return sanitized;
    }
    
    /**
     * Sanitize a JSON string value
     */
    public static String sanitizeJsonString(String jsonValue) {
        if (jsonValue == null) {
            return null;
        }
        
        // JSON encode to prevent JSON injection
        String sanitized = encodeForJson(jsonValue);
        
        return sanitized;
    }
    
    /**
     * Sanitize content for use in JavaScript context
     */
    public static String sanitizeForJavaScript(String content) {
        if (content == null) {
            return null;
        }
        
        // JavaScript encoding to prevent script injection
        String sanitized = encodeForJavaScript(content);
        
        return sanitized;
    }
    
    /**
     * Sanitize content for use in CSS context
     */
    public static String sanitizeForCss(String content) {
        if (content == null) {
            return null;
        }
        
        // CSS encoding to prevent CSS injection
        String sanitized = encodeForCss(content);
        
        return sanitized;
    }
    
    /**
     * Validate and sanitize a numeric string
     */
    public static String sanitizeNumeric(String numeric) {
        if (numeric == null) {
            return null;
        }
        
        String sanitized = numeric.trim();
        
        // Remove all non-numeric characters except decimal point and minus sign
        sanitized = sanitized.replaceAll("[^0-9.-]", "");
        
        return sanitized;
    }
    
    /**
     * Check if a string contains potential XSS patterns
     */
    public static boolean containsXSS(String input) {
        if (input == null) {
            return false;
        }
        
        String lowerInput = input.toLowerCase();
        
        // Common XSS patterns
        String[] xssPatterns = {
            "<script",
            "javascript:",
            "onload=",
            "onerror=",
            "onclick=",
            "onmouseover=",
            "onfocus=",
            "onblur=",
            "eval(",
            "expression(",
            "vbscript:",
            "data:text/html",
            "<iframe",
            "<object",
            "<embed",
            "fromcharcode",
            "&#x",
            "&lt;script",
            "&gt;script"
        };
        
        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                logger.warn("Potential XSS pattern detected: {}", pattern);
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Strip HTML tags from input
     */
    public static String stripHtmlTags(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove HTML tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        
        return sanitized.trim();
    }
    
    /**
     * Validate input length against maximum allowed
     */
    public static boolean validateLength(String input, int maxLength) {
        if (input == null) {
            return true;
        }
        
        return input.length() <= maxLength;
    }
    
    /**
     * Sanitize an array of strings
     */
    public static String[] sanitizeArray(String[] inputs) {
        if (inputs == null) {
            return null;
        }
        
        String[] sanitized = new String[inputs.length];
        for (int i = 0; i < inputs.length; i++) {
            sanitized[i] = sanitize(inputs[i]);
        }
        
        return sanitized;
    }
    
    // ---------------------------------------------------------------------------
    // HTML Entity Encoding Methods (Custom Implementation)
    // ---------------------------------------------------------------------------
    
    private static String encodeForHtml(String input) {
        if (input == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder(input.length() * 2);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '/':
                    sb.append("&#x2F;");
                    break;
                default:
                    sb.append(c);
            }
        }
        
        return sb.toString();
    }
    
    private static String encodeForUrl(String input) {
        if (input == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder(input.length() * 2);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || 
                (c >= '0' && c <= '9') || c == '-' || c == '_' || c == '.' || c == '~') {
                sb.append(c);
            } else {
                sb.append('%');
                sb.append(String.format("%02X", (int) c));
            }
        }
        
        return sb.toString();
    }
    
    private static String encodeForJson(String input) {
        if (input == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder(input.length() * 2);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        
        return sb.toString();
    }
    
    private static String encodeForJavaScript(String input) {
        if (input == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder(input.length() * 2);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            switch (c) {
                case '\'':
                    sb.append("\\'");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '/':
                    sb.append("\\/");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\x%02x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        
        return sb.toString();
    }
    
    private static String encodeForCss(String input) {
        if (input == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder(input.length() * 2);
        
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || 
                (c >= '0' && c <= '9')) {
                sb.append(c);
            } else {
                sb.append('\\');
                sb.append(String.format("%04x", (int) c));
            }
        }
        
        return sb.toString();
    }
}
