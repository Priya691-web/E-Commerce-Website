package com.fashionstore.controller;

import java.io.Serializable;
import java.util.List;

/**
 * Standard enterprise-grade API Response wrapper for unified JSON response bodies
 * 
 * Standard Format:
 * {
 *   "success": true,
 *   "message": "Operation successful",
 *   "data": {},
 *   "timestamp": 1234567890
 * }
 * 
 * Error Format:
 * {
 *   "success": false,
 *   "message": "Error message",
 *   "errors": [],
 *   "timestamp": 1234567890
 * }
 */
public class ApiResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, T data) {
        this(success, message);
        this.data = data;
    }

    public ApiResponse(boolean success, String message, T data, List<String> errors) {
        this(success, message, data);
        this.errors = errors;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation successful", data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    public static <T> ApiResponse<T> validationError(List<String> errors) {
        return new ApiResponse<>(false, "Validation failed", null, errors);
    }

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
