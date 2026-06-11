package com.fashionstore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Standardized API Response Wrapper
 * Provides consistent response structure across all API endpoints
 * Follows enterprise-grade API response standards
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private boolean success;
    private String message;
    private T data;
    private String error;
    private String errorCode;
    private String timestamp;
    private String path;
    private Meta meta;
    
    // Private constructor - use builder
    private ApiResponse() {
        this.timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message("Operation successful")
                .build();
    }
    
    /**
     * Create success response with data and custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }
    
    /**
     * Create success response with message only
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }
    
    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .build();
    }
    
    /**
     * Create error response with error code
     */
    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode(errorCode)
                .build();
    }
    
    /**
     * Create error response with error code and data
     */
    public static <T> ApiResponse<T> error(String message, String errorCode, T data) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode(errorCode)
                .data(data)
                .build();
    }
    
    /**
     * Create not found response
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode("NOT_FOUND")
                .build();
    }
    
    /**
     * Create validation error response
     */
    public static <T> ApiResponse<T> validationError(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode("VALIDATION_ERROR")
                .build();
    }
    
    /**
     * Create unauthorized response
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode("UNAUTHORIZED")
                .build();
    }
    
    /**
     * Create forbidden response
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode("FORBIDDEN")
                .build();
    }
    
    /**
     * Create server error response
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(message)
                .errorCode("SERVER_ERROR")
                .build();
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public Meta getMeta() {
        return meta;
    }
    
    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    
    /**
     * Builder class for ApiResponse
     */
    public static class Builder<T> {
        private ApiResponse<T> response;
        
        public Builder() {
            this.response = new ApiResponse<>();
        }
        
        public Builder<T> success(boolean success) {
            response.success = success;
            return this;
        }
        
        public Builder<T> message(String message) {
            response.message = message;
            return this;
        }
        
        public Builder<T> data(T data) {
            response.data = data;
            return this;
        }
        
        public Builder<T> error(String error) {
            response.error = error;
            return this;
        }
        
        public Builder<T> errorCode(String errorCode) {
            response.errorCode = errorCode;
            return this;
        }
        
        public Builder<T> path(String path) {
            response.path = path;
            return this;
        }
        
        public Builder<T> meta(Meta meta) {
            response.meta = meta;
            return this;
        }
        
        public ApiResponse<T> build() {
            return response;
        }
    }
    
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    /**
     * Meta information for paginated responses
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Meta {
        private int page;
        private int pageSize;
        private long total;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
        
        public Meta() {}
        
        public Meta(int page, int pageSize, long total) {
            this.page = page;
            this.pageSize = pageSize;
            this.total = total;
            this.totalPages = (int) Math.ceil((double) total / pageSize);
            this.hasNext = page < totalPages;
            this.hasPrevious = page > 1;
        }
        
        // Getters and Setters
        public int getPage() {
            return page;
        }
        
        public void setPage(int page) {
            this.page = page;
        }
        
        public int getPageSize() {
            return pageSize;
        }
        
        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
        
        public long getTotal() {
            return total;
        }
        
        public void setTotal(long total) {
            this.total = total;
        }
        
        public int getTotalPages() {
            return totalPages;
        }
        
        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
        
        public boolean isHasNext() {
            return hasNext;
        }
        
        public void setHasNext(boolean hasNext) {
            this.hasNext = hasNext;
        }
        
        public boolean isHasPrevious() {
            return hasPrevious;
        }
        
        public void setHasPrevious(boolean hasPrevious) {
            this.hasPrevious = hasPrevious;
        }
        
        public static Meta of(int page, int pageSize, long total) {
            return new Meta(page, pageSize, total);
        }
    }
}
