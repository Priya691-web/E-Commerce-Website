package com.fashionstore.dto;

import java.time.LocalDateTime;

/**
 * DTO for user session information
 * Used for session management and security tracking
 */
public class SessionDTO {
    private String sessionId;
    private int userId;
    private String deviceType;
    private String deviceName;
    private String operatingSystem;
    private String browser;
    private String ipAddress;
    private String userAgent;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime expiresAt;
    private boolean isActive;
    private boolean isRemembered;
    private String loginMethod; // password, social, otp
    private String securityToken;
    private int failedAttempts;
    private LocalDateTime lastFailedAttempt;
    private boolean isSuspicious;
    private String suspiciousReason;

    public SessionDTO() {}

    public SessionDTO(String sessionId, int userId, String ipAddress, String userAgent) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(24);
        this.isActive = true;
        this.isRemembered = false;
        this.loginMethod = "password";
        this.failedAttempts = 0;
        this.isSuspicious = false;
        
        // Parse user agent to extract device info
        parseUserAgent(userAgent);
    }

    // Getters and Setters
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getOperatingSystem() { return operatingSystem; }
    public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public void setLastAccessedAt(LocalDateTime lastAccessedAt) { this.lastAccessedAt = lastAccessedAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public boolean isRemembered() { return isRemembered; }
    public void setRemembered(boolean remembered) { isRemembered = remembered; }

    public String getLoginMethod() { return loginMethod; }
    public void setLoginMethod(String loginMethod) { this.loginMethod = loginMethod; }

    public String getSecurityToken() { return securityToken; }
    public void setSecurityToken(String securityToken) { this.securityToken = securityToken; }

    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }

    public LocalDateTime getLastFailedAttempt() { return lastFailedAttempt; }
    public void setLastFailedAttempt(LocalDateTime lastFailedAttempt) { this.lastFailedAttempt = lastFailedAttempt; }

    public boolean isSuspicious() { return isSuspicious; }
    public void setSuspicious(boolean suspicious) { isSuspicious = suspicious; }

    public String getSuspiciousReason() { return suspiciousReason; }
    public void setSuspiciousReason(String suspiciousReason) { this.suspiciousReason = suspiciousReason; }

    // Helper method to parse user agent
    private void parseUserAgent(String userAgent) {
        if (userAgent == null) return;
        
        String ua = userAgent.toLowerCase();
        
        // Detect device type
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone") || ua.contains("ipad")) {
            this.deviceType = "mobile";
            if (ua.contains("iphone")) {
                this.deviceName = "iPhone";
            } else if (ua.contains("ipad")) {
                this.deviceName = "iPad";
            } else if (ua.contains("android")) {
                this.deviceName = "Android Device";
            } else {
                this.deviceName = "Mobile Device";
            }
        } else {
            this.deviceType = "desktop";
            this.deviceName = "Desktop";
        }
        
        // Detect operating system
        if (ua.contains("windows")) {
            this.operatingSystem = "Windows";
        } else if (ua.contains("mac")) {
            this.operatingSystem = "macOS";
        } else if (ua.contains("linux")) {
            this.operatingSystem = "Linux";
        } else if (ua.contains("android")) {
            this.operatingSystem = "Android";
        } else if (ua.contains("ios") || ua.contains("iphone") || ua.contains("ipad")) {
            this.operatingSystem = "iOS";
        } else {
            this.operatingSystem = "Unknown";
        }
        
        // Detect browser
        if (ua.contains("chrome")) {
            this.browser = "Chrome";
        } else if (ua.contains("firefox")) {
            this.browser = "Firefox";
        } else if (ua.contains("safari")) {
            this.browser = "Safari";
        } else if (ua.contains("edge")) {
            this.browser = "Edge";
        } else if (ua.contains("opera")) {
            this.browser = "Opera";
        } else {
            this.browser = "Unknown";
        }
    }

    // Method to check if session is expired
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    // Method to check if session needs refresh
    public boolean needsRefresh() {
        return LocalDateTime.now().isAfter(lastAccessedAt.plusMinutes(30));
    }

    // Method to extend session
    public void extendSession(int hours) {
        this.lastAccessedAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusHours(hours);
    }

    // Method to mark as suspicious
    public void markSuspicious(String reason) {
        this.isSuspicious = true;
        this.suspiciousReason = reason;
    }

    // Method to increment failed attempts
    public void incrementFailedAttempts() {
        this.failedAttempts++;
        this.lastFailedAttempt = LocalDateTime.now();
        
        // Mark as suspicious if too many failed attempts
        if (this.failedAttempts >= 5) {
            markSuspicious("Multiple failed login attempts");
        }
    }

    @Override
    public String toString() {
        return "SessionDTO{" +
                "sessionId='" + sessionId + '\'' +
                ", userId=" + userId +
                ", deviceType='" + deviceType + '\'' +
                ", browser='" + browser + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", isActive=" + isActive +
                ", isSuspicious=" + isSuspicious +
                ", createdAt=" + createdAt +
                '}';
    }
}
