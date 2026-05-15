package com.fashionstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler
 * Centralized exception handling for the entire application
 * Provides consistent error responses across all endpoints
 */

public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle all exceptions and return consistent error response
     */
    public static Map<String, Object> handleException(Exception e, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", getStatusCode(e));
        errorResponse.put("error", getErrorType(e));
        errorResponse.put("message", getErrorMessage(e));
        errorResponse.put("path", request.getRequestURI());
        
        // Log the error
        logger.error("Exception occurred: {} at {}", e.getMessage(), request.getRequestURI(), e);
        
        return errorResponse;
    }

    /**
     * Get HTTP status code based on exception type
     */
    private static int getStatusCode(Exception e) {
        if (e instanceof ResourceNotFoundException) {
            return 404;
        } else if (e instanceof ValidationException) {
            return 400;
        } else if (e instanceof UnauthorizedException) {
            return 401;
        } else if (e instanceof ForbiddenException) {
            return 403;
        } else if (e instanceof ConflictException) {
            return 409;
        } else {
            return 500;
        }
    }

    /**
     * Get error type based on exception
     */
    private static String getErrorType(Exception e) {
        if (e instanceof ResourceNotFoundException) {
            return "Not Found";
        } else if (e instanceof ValidationException) {
            return "Validation Error";
        } else if (e instanceof UnauthorizedException) {
            return "Unauthorized";
        } else if (e instanceof ForbiddenException) {
            return "Forbidden";
        } else if (e instanceof ConflictException) {
            return "Conflict";
        } else {
            return "Internal Server Error";
        }
    }

    /**
     * Get user-friendly error message
     */
    private static String getErrorMessage(Exception e) {
        if (e instanceof ResourceNotFoundException) {
            return ((ResourceNotFoundException) e).getMessage();
        } else if (e instanceof ValidationException) {
            return ((ValidationException) e).getMessage();
        } else if (e instanceof UnauthorizedException) {
            return ((UnauthorizedException) e).getMessage();
        } else if (e instanceof ForbiddenException) {
            return ((ForbiddenException) e).getMessage();
        } else if (e instanceof ConflictException) {
            return ((ConflictException) e).getMessage();
        } else {
            return "An unexpected error occurred. Please try again later.";
        }
    }
}
