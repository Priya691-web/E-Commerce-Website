package com.fashionstore.exception;

import java.time.Instant;
import java.util.Map;

/**
 * Standardized error response for API endpoints
 * Provides consistent error format across the application
 */
public class ErrorResponse {

    private final boolean success;
    private final String error;
    private final String message;
    private final String type;
    private final int httpStatus;
    private final long timestamp;
    private final String path;
    private final Map<String, Object> details;

    private ErrorResponse(Builder builder) {
        this.success = builder.success;
        this.error = builder.error;
        this.message = builder.message;
        this.type = builder.type;
        this.httpStatus = builder.httpStatus;
        this.timestamp = builder.timestamp;
        this.path = builder.path;
        this.details = builder.details;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String error;
        private String message;
        private String type;
        private int httpStatus;
        private long timestamp;
        private String path;
        private Map<String, Object> details = new java.util.HashMap<>();

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder httpStatus(int httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp.toEpochMilli();
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder addDetail(String key, Object value) {
            this.details.put(key, value);
            return this;
        }

        public Builder addDetails(Map<String, Object> details) {
            if (details != null) {
                this.details.putAll(details);
            }
            return this;
        }

        public ErrorResponse build() {
            // Set defaults if not provided
            if (this.timestamp == 0) {
                this.timestamp = System.currentTimeMillis();
            }
            if (this.httpStatus == 0) {
                this.httpStatus = 500;
            }
            return new ErrorResponse(this);
        }
    }
}
