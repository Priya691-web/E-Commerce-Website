package com.fashionstore.exception;

/**
 * Enumeration of exception types in the FashionStore application
 * Used for categorizing and handling different types of errors
 */
public enum ExceptionType {
    
    /**
     * Validation errors - invalid input data
     */
    VALIDATION_ERROR("Validation Error", 400),
    
    /**
     * Authentication errors - user not logged in
     */
    AUTHENTICATION_ERROR("Authentication Error", 401),
    
    /**
     * Authorization errors - user lacks permission
     */
    AUTHORIZATION_ERROR("Authorization Error", 403),
    
    /**
     * Not found errors - resource doesn't exist
     */
    NOT_FOUND("Not Found", 404),
    
    /**
     * Payment errors - payment processing failures
     */
    PAYMENT_ERROR("Payment Error", 402),
    
    /**
     * Database errors - data access issues
     */
    DATABASE_ERROR("Database Error", 500),
    
    /**
     * System errors - unexpected system failures
     */
    SYSTEM_ERROR("System Error", 500);
    
    private final String displayName;
    private final int defaultHttpStatus;
    
    ExceptionType(String displayName, int defaultHttpStatus) {
        this.displayName = displayName;
        this.defaultHttpStatus = defaultHttpStatus;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public int getDefaultHttpStatus() {
        return defaultHttpStatus;
    }
}
