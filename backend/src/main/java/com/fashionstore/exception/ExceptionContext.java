package com.fashionstore.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Context information for exception handling
 * Provides additional details about where and why an exception occurred
 */
public class ExceptionContext {

    private final String path;
    private final String method;
    private final String controller;
    private final String action;
    private final Map<String, Object> additionalData;
    private final Long userId;
    private final String sessionId;

    private ExceptionContext(Builder builder) {
        this.path = builder.path;
        this.method = builder.method;
        this.controller = builder.controller;
        this.action = builder.action;
        this.additionalData = builder.additionalData;
        this.userId = builder.userId;
        this.sessionId = builder.sessionId;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public String getController() {
        return controller;
    }

    public String getAction() {
        return action;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("path", path);
        map.put("method", method);
        map.put("controller", controller);
        map.put("action", action);
        map.put("userId", userId);
        map.put("sessionId", sessionId);
        map.put("additionalData", additionalData);
        return map;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String path;
        private String method;
        private String controller;
        private String action;
        private Map<String, Object> additionalData = new HashMap<>();
        private Long userId;
        private String sessionId;

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public Builder controller(String controller) {
            this.controller = controller;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder addData(String key, Object value) {
            this.additionalData.put(key, value);
            return this;
        }

        public Builder addData(Map<String, Object> data) {
            if (data != null) {
                this.additionalData.putAll(data);
            }
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public ExceptionContext build() {
            return new ExceptionContext(this);
        }
    }
}
