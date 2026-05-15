package com.fashionstore.exception;

/**
 * Exception thrown when authentication fails
 */

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException() {
        super("Unauthorized access");
    }
}
