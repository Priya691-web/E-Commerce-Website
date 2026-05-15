package com.fashionstore.exception;

/**
 * Exception thrown when a requested resource is not found
 */

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Integer id) {
        super(String.format("%s with id %d not found", resource, id));
    }
}
