package com.fashionstore.exception;

/**
 * Exception thrown when a conflict occurs (e.g., duplicate resource)
 */

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String resource, String identifier) {
        super(String.format("%s with identifier '%s' already exists", resource, identifier));
    }
}
