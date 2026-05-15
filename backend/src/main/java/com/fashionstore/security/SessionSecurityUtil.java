package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced Session Security Utilities
 * Provides session fixation protection, concurrent session management, and session timeout
 */
public class SessionSecurityUtil {
    private static final Logger logger = LoggerFactory.getLogger(SessionSecurityUtil.class);
    
    // Session configuration
    private static final int MAX_SESSION_AGE_SECONDS = 30 * 60; // 30 minutes
    private static final int MAX_CONCURRENT_SESSIONS = 3;
    private static final int SESSION_INACTIVITY_TIMEOUT_SECONDS = 15 * 60; // 15 minutes
    
    // Session registry for concurrent session management
    private static final Map<String, SessionInfo> sessionRegistry = new ConcurrentHashMap<>();
    
    /**
     * Create secure session with protection against session fixation
     */
    public static HttpSession createSecureSession(HttpServletRequest request) {
        // Invalidate existing session if present
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        
        // Create new session
        HttpSession newSession = request.getSession(true);
        
        // Set session security attributes
        newSession.setMaxInactiveInterval(MAX_SESSION_AGE_SECONDS);
        newSession.setAttribute("sessionCreationTime", System.currentTimeMillis());
        newSession.setAttribute("sessionLastAccessedTime", System.currentTimeMillis());
        newSession.setAttribute("sessionIP", getClientIP(request));
        newSession.setAttribute("sessionUserAgent", request.getHeader("User-Agent"));
        
        logger.info("Secure session created: {}", newSession.getId());
        
        return newSession;
    }
    
    /**
     * Validate session security
     */
    public static SessionValidationResult validateSession(HttpServletRequest request) {
        SessionValidationResult result = new SessionValidationResult();
        
        HttpSession session = request.getSession(false);
        if (session == null) {
            result.setValid(false);
            result.setReason("No active session");
            return result;
        }
        
        // Check session age
        Long creationTime = (Long) session.getAttribute("sessionCreationTime");
        if (creationTime == null) {
            result.setValid(false);
            result.setReason("Session creation time not set");
            return result;
        }
        
        long sessionAge = System.currentTimeMillis() - creationTime;
        long maxAge = MAX_SESSION_AGE_SECONDS * 1000L;
        
        if (sessionAge > maxAge) {
            result.setValid(false);
            result.setReason("Session expired due to age");
            session.invalidate();
            return result;
        }
        
        // Check session inactivity
        Long lastAccessedTime = (Long) session.getAttribute("sessionLastAccessedTime");
        if (lastAccessedTime != null) {
            long inactivity = System.currentTimeMillis() - lastAccessedTime;
            long maxInactivity = SESSION_INACTIVITY_TIMEOUT_SECONDS * 1000L;
            
            if (inactivity > maxInactivity) {
                result.setValid(false);
                result.setReason("Session expired due to inactivity");
                session.invalidate();
                return result;
            }
        }
        
        // Update last accessed time
        session.setAttribute("sessionLastAccessedTime", System.currentTimeMillis());
        
        // Check session IP binding (optional, can be disabled for mobile users)
        String sessionIP = (String) session.getAttribute("sessionIP");
        String currentIP = getClientIP(request);
        
        if (sessionIP != null && !sessionIP.equals(currentIP)) {
            logger.warn("Session IP mismatch: expected {}, got {}", sessionIP, currentIP);
            // Don't invalidate immediately, log for monitoring
            result.setIPMismatch(true);
        }
        
        // Check session User-Agent binding
        String sessionUA = (String) session.getAttribute("sessionUserAgent");
        String currentUA = request.getHeader("User-Agent");
        
        if (sessionUA != null && !sessionUA.equals(currentUA)) {
            logger.warn("Session User-Agent mismatch: expected {}, got {}", sessionUA, currentUA);
            result.setUserAgentMismatch(true);
        }
        
        // Check concurrent sessions
        String userId = (String) session.getAttribute("userId");
        if (userId != null) {
            if (!validateConcurrentSessions(userId, session.getId())) {
                result.setValid(false);
                result.setReason("Concurrent session limit exceeded");
                session.invalidate();
                return result;
            }
        }
        
        result.setValid(true);
        result.setSessionId(session.getId());
        
        return result;
    }
    
    /**
     * Validate concurrent sessions
     */
    private static boolean validateConcurrentSessions(String userId, String sessionId) {
        SessionInfo sessionInfo = sessionRegistry.get(userId);
        
        if (sessionInfo == null) {
            // First session for this user
            sessionRegistry.put(userId, new SessionInfo(sessionId, System.currentTimeMillis()));
            return true;
        }
        
        // Check if this is the same session
        if (sessionInfo.getSessionId().equals(sessionId)) {
            sessionInfo.setLastAccessedTime(System.currentTimeMillis());
            return true;
        }
        
        // Check concurrent session limit
        if (sessionInfo.getSessionCount() >= MAX_CONCURRENT_SESSIONS) {
            return false;
        }
        
        // Add new session
        sessionInfo.addSession(sessionId);
        sessionInfo.setLastAccessedTime(System.currentTimeMillis());
        sessionRegistry.put(userId, sessionInfo);
        
        return true;
    }
    
    /**
     * Invalidate session
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userId = (String) session.getAttribute("userId");
            String sessionId = session.getId();
            
            // Remove from registry
            if (userId != null) {
                SessionInfo sessionInfo = sessionRegistry.get(userId);
                if (sessionInfo != null) {
                    sessionInfo.removeSession(sessionId);
                    if (sessionInfo.getSessionCount() == 0) {
                        sessionRegistry.remove(userId);
                    } else {
                        sessionRegistry.put(userId, sessionInfo);
                    }
                }
            }
            
            session.invalidate();
            logger.info("Session invalidated: {}", sessionId);
        }
    }
    
    /**
     * Invalidate all sessions for a user
     */
    public static void invalidateAllUserSessions(String userId) {
        SessionInfo sessionInfo = sessionRegistry.get(userId);
        if (sessionInfo != null) {
            sessionRegistry.remove(userId);
            logger.info("All sessions invalidated for user: {}", userId);
        }
    }
    
    /**
     * Get client IP address
     */
    private static String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * Session info class for concurrent session management
     */
    private static class SessionInfo {
        private String sessionId;
        private java.util.Set<String> sessionIds = new java.util.HashSet<>();
        private long lastAccessedTime;
        
        public SessionInfo(String sessionId, long lastAccessedTime) {
            this.sessionId = sessionId;
            this.sessionIds.add(sessionId);
            this.lastAccessedTime = lastAccessedTime;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public int getSessionCount() {
            return sessionIds.size();
        }
        
        public void addSession(String sessionId) {
            this.sessionIds.add(sessionId);
        }
        
        public void removeSession(String sessionId) {
            this.sessionIds.remove(sessionId);
            if (this.sessionId.equals(sessionId) && !this.sessionIds.isEmpty()) {
                this.sessionId = this.sessionIds.iterator().next();
            }
        }
        
        public long getLastAccessedTime() {
            return lastAccessedTime;
        }
        
        public void setLastAccessedTime(long lastAccessedTime) {
            this.lastAccessedTime = lastAccessedTime;
        }
    }
    
    /**
     * Session validation result class
     */
    public static class SessionValidationResult {
        private boolean valid;
        private String reason;
        private String sessionId;
        private boolean ipMismatch;
        private boolean userAgentMismatch;
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
        
        public String getSessionId() {
            return sessionId;
        }
        
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }
        
        public boolean isIPMismatch() {
            return ipMismatch;
        }
        
        public void setIPMismatch(boolean ipMismatch) {
            this.ipMismatch = ipMismatch;
        }
        
        public boolean isUserAgentMismatch() {
            return userAgentMismatch;
        }
        
        public void setUserAgentMismatch(boolean userAgentMismatch) {
            this.userAgentMismatch = userAgentMismatch;
        }
    }
}
