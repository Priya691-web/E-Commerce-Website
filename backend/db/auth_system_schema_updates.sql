-- FashionStore Authentication System Schema Updates
-- For premium commerce identity ecosystem

-- 1. User Addresses Table
CREATE TABLE IF NOT EXISTS user_addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    address_type ENUM('home', 'work', 'other') DEFAULT 'home',
    recipient_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    landmark VARCHAR(255),
    area VARCHAR(100),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    country VARCHAR(100) DEFAULT 'India',
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_addresses_user (user_id),
    INDEX idx_user_addresses_default (user_id, is_default),
    INDEX idx_user_addresses_pincode (pincode),
    INDEX idx_user_addresses_active (is_active)
);

-- 2. User Sessions Table
CREATE TABLE IF NOT EXISTS user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT,
    device_type VARCHAR(50),
    device_name VARCHAR(100),
    operating_system VARCHAR(100),
    browser VARCHAR(100),
    ip_address VARCHAR(45),
    user_agent TEXT,
    location VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_remembered BOOLEAN DEFAULT FALSE,
    login_method ENUM('password', 'social', 'otp', 'biometric') DEFAULT 'password',
    security_token VARCHAR(255),
    failed_attempts INT DEFAULT 0,
    last_failed_attempt TIMESTAMP NULL,
    is_suspicious BOOLEAN DEFAULT FALSE,
    suspicious_reason VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_sessions_user (user_id),
    INDEX idx_user_sessions_active (is_active),
    INDEX idx_user_sessions_expires (expires_at),
    INDEX idx_user_sessions_suspicious (is_suspicious),
    INDEX idx_user_sessions_created (created_at)
);

-- 3. User Notifications Table
CREATE TABLE IF NOT EXISTS user_notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type ENUM('order', 'payment', 'shipping', 'promotion', 'security', 'account', 'wishlist', 'cart') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    action_url VARCHAR(500),
    action_text VARCHAR(100),
    image_url VARCHAR(500),
    is_read BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    category ENUM('info', 'success', 'warning', 'error') DEFAULT 'info',
    metadata JSON,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_notifications_user (user_id),
    INDEX idx_user_notifications_read (is_read),
    INDEX idx_user_notifications_type (type),
    INDEX idx_user_notifications_priority (priority),
    INDEX idx_user_notifications_created (created_at),
    INDEX idx_user_notifications_active (is_active)
);

-- 4. Login Activity Table
CREATE TABLE IF NOT EXISTS login_activity (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    email VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    device_type VARCHAR(50),
    operating_system VARCHAR(100),
    browser VARCHAR(100),
    location VARCHAR(255),
    login_result ENUM('success', 'failure', 'blocked', 'suspicious') NOT NULL,
    failure_reason VARCHAR(255),
    session_id VARCHAR(255),
    login_method ENUM('password', 'social', 'otp', 'biometric') DEFAULT 'password',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_login_activity_user (user_id),
    INDEX idx_login_activity_email (email),
    INDEX idx_login_activity_result (login_result),
    INDEX idx_login_activity_ip (ip_address),
    INDEX idx_login_activity_created (created_at)
);

-- 5. User Profile Extensions Table
CREATE TABLE IF NOT EXISTS user_profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    gender ENUM('Male', 'Female', 'Other', 'Prefer not to say'),
    date_of_birth DATE,
    profile_image VARCHAR(500),
    bio TEXT,
    preferences JSON,
    membership_tier ENUM('basic', 'premium', 'elite') DEFAULT 'basic',
    membership_expiry TIMESTAMP NULL,
    loyalty_points INT DEFAULT 0,
    referral_code VARCHAR(20) UNIQUE,
    referred_count INT DEFAULT 0,
    is_email_verified BOOLEAN DEFAULT FALSE,
    is_phone_verified BOOLEAN DEFAULT FALSE,
    is_profile_complete BOOLEAN DEFAULT FALSE,
    last_login_at TIMESTAMP NULL,
    last_login_ip VARCHAR(45),
    last_login_device VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_user_profiles_membership (membership_tier),
    INDEX idx_user_profiles_referral (referral_code),
    INDEX idx_user_profiles_verified (is_email_verified, is_phone_verified),
    INDEX idx_user_profiles_complete (is_profile_complete)
);

-- 6. Password Reset Tokens Table
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    token_type ENUM('password_reset', 'email_verification', 'phone_verification') DEFAULT 'password_reset',
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_password_tokens_user (user_id),
    INDEX idx_password_tokens_email (email),
    INDEX idx_password_tokens_token (token),
    INDEX idx_password_tokens_expires (expires_at),
    INDEX idx_password_tokens_used (is_used)
);

-- 7. Social Login Accounts Table
CREATE TABLE IF NOT EXISTS social_login_accounts (
    social_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    provider ENUM('google', 'facebook', 'apple', 'instagram', 'twitter') NOT NULL,
    provider_user_id VARCHAR(255) NOT NULL,
    provider_email VARCHAR(255),
    access_token TEXT,
    refresh_token TEXT,
    token_expires_at TIMESTAMP NULL,
    profile_data JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_social_provider_user (provider, provider_user_id),
    INDEX idx_social_login_user (user_id),
    INDEX idx_social_login_provider (provider),
    INDEX idx_social_login_active (is_active)
);

-- 8. User Security Settings Table
CREATE TABLE IF NOT EXISTS user_security_settings (
    settings_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    two_factor_enabled BOOLEAN DEFAULT FALSE,
    two_factor_secret VARCHAR(255),
    backup_codes JSON,
    login_alerts BOOLEAN DEFAULT TRUE,
    email_alerts BOOLEAN DEFAULT TRUE,
    sms_alerts BOOLEAN DEFAULT FALSE,
    session_timeout_minutes INT DEFAULT 30,
    concurrent_sessions_allowed BOOLEAN DEFAULT TRUE,
    max_concurrent_sessions INT DEFAULT 3,
    suspicious_login_protection BOOLEAN DEFAULT TRUE,
    password_change_required BOOLEAN DEFAULT FALSE,
    last_password_change TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_security_settings_2fa (two_factor_enabled),
    INDEX idx_security_settings_alerts (login_alerts, email_alerts, sms_alerts)
);

-- 9. User Activity Log Table
CREATE TABLE IF NOT EXISTS user_activity_log (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    activity_type ENUM('login', 'logout', 'profile_update', 'password_change', 'address_add', 'address_update', 'address_delete', 'order_place', 'payment', 'wishlist_add', 'cart_add') NOT NULL,
    activity_data JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_activity_log_user (user_id),
    INDEX idx_activity_log_session (session_id),
    INDEX idx_activity_log_type (activity_type),
    INDEX idx_activity_log_created (created_at)
);

-- 10. OTP Verification Table
CREATE TABLE IF NOT EXISTS otp_verifications (
    otp_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    email VARCHAR(255),
    phone VARCHAR(20),
    otp_code VARCHAR(10) NOT NULL,
    otp_type ENUM('login', 'register', 'password_reset', 'email_verify', 'phone_verify', 'transaction') NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    used_at TIMESTAMP NULL,
    attempts INT DEFAULT 0,
    max_attempts INT DEFAULT 3,
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_until TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_otp_user (user_id),
    INDEX idx_otp_email (email),
    INDEX idx_otp_phone (phone),
    INDEX idx_otp_code (otp_code),
    INDEX idx_otp_expires (expires_at),
    INDEX idx_otp_used (is_used)
);

-- Insert initial data for security settings
INSERT IGNORE INTO user_security_settings (user_id, two_factor_enabled, login_alerts, email_alerts, session_timeout_minutes, concurrent_sessions_allowed, max_concurrent_sessions, suspicious_login_protection)
SELECT user_id, FALSE, TRUE, TRUE, 30, TRUE, 3, TRUE
FROM users;

-- Create views for common queries
CREATE OR REPLACE VIEW user_session_summary_view AS
SELECT 
    u.user_id,
    u.email,
    u.full_name,
    COUNT(s.session_id) as active_sessions,
    MAX(s.last_accessed_at) as last_activity,
    s.device_type,
    s.browser,
    s.ip_address,
    s.location,
    s.is_suspicious
FROM users u
LEFT JOIN user_sessions s ON u.user_id = s.user_id AND s.is_active = TRUE
GROUP BY u.user_id, u.email, u.full_name, s.device_type, s.browser, s.ip_address, s.location, s.is_suspicious
ORDER BY s.last_accessed_at DESC;

CREATE OR REPLACE VIEW user_notification_stats_view AS
SELECT 
    user_id,
    COUNT(*) as total_notifications,
    SUM(CASE WHEN is_read = FALSE THEN 1 ELSE 0 END) as unread_notifications,
    SUM(CASE WHEN priority = 'urgent' THEN 1 ELSE 0 END) as urgent_notifications,
    SUM(CASE WHEN category = 'security' THEN 1 ELSE 0 END) as security_notifications,
    MAX(created_at) as latest_notification
FROM user_notifications 
WHERE is_active = TRUE
GROUP BY user_id;

CREATE OR REPLACE VIEW login_activity_summary_view AS
SELECT 
    user_id,
    email,
    COUNT(*) as total_attempts,
    SUM(CASE WHEN login_result = 'success' THEN 1 ELSE 0 END) as successful_logins,
    SUM(CASE WHEN login_result = 'failure' THEN 1 ELSE 0 END) as failed_logins,
    SUM(CASE WHEN login_result = 'suspicious' THEN 1 ELSE 0 END) as suspicious_logins,
    MAX(created_at) as last_login,
    COUNT(DISTINCT ip_address) as unique_ips,
    COUNT(DISTINCT device_type) as unique_devices
FROM login_activity 
GROUP BY user_id, email
ORDER BY last_login DESC;

-- Create indexes for better performance
CREATE INDEX idx_user_notifications_user_unread ON user_notifications(user_id, is_read, created_at DESC);
CREATE INDEX idx_user_sessions_user_active ON user_sessions(user_id, is_active, last_accessed_at DESC);
CREATE INDEX idx_login_activity_user_result ON login_activity(user_id, login_result, created_at DESC);
CREATE INDEX idx_user_activity_log_user_type ON user_activity_log(user_id, activity_type, created_at DESC);
CREATE INDEX idx_otp_verifications_user_type ON otp_verifications(user_id, otp_type, created_at DESC);

-- Add foreign key constraints for better data integrity
ALTER TABLE user_addresses ADD CONSTRAINT fk_user_addresses_type 
CHECK (address_type IN ('home', 'work', 'other'));

ALTER TABLE user_sessions ADD CONSTRAINT fk_user_sessions_login_method 
CHECK (login_method IN ('password', 'social', 'otp', 'biometric'));

ALTER TABLE user_notifications ADD CONSTRAINT fk_user_notifications_type 
CHECK (type IN ('order', 'payment', 'shipping', 'promotion', 'security', 'account', 'wishlist', 'cart'));

ALTER TABLE user_notifications ADD CONSTRAINT fk_user_notifications_priority 
CHECK (priority IN ('low', 'medium', 'high', 'urgent'));

ALTER TABLE user_notifications ADD CONSTRAINT fk_user_notifications_category 
CHECK (category IN ('info', 'success', 'warning', 'error'));

ALTER TABLE login_activity ADD CONSTRAINT fk_login_activity_result 
CHECK (login_result IN ('success', 'failure', 'blocked', 'suspicious'));

ALTER TABLE login_activity ADD CONSTRAINT fk_login_activity_method 
CHECK (login_method IN ('password', 'social', 'otp', 'biometric'));

ALTER TABLE user_profiles ADD CONSTRAINT fk_user_profiles_gender 
CHECK (gender IN ('Male', 'Female', 'Other', 'Prefer not to say'));

ALTER TABLE user_profiles ADD CONSTRAINT fk_user_profiles_membership 
CHECK (membership_tier IN ('basic', 'premium', 'elite'));

ALTER TABLE password_reset_tokens ADD CONSTRAINT fk_password_tokens_type 
CHECK (token_type IN ('password_reset', 'email_verification', 'phone_verification'));

ALTER TABLE social_login_accounts ADD CONSTRAINT fk_social_login_provider 
CHECK (provider IN ('google', 'facebook', 'apple', 'instagram', 'twitter'));

ALTER TABLE user_activity_log ADD CONSTRAINT fk_activity_log_type 
CHECK (activity_type IN ('login', 'logout', 'profile_update', 'password_change', 'address_add', 'address_update', 'address_delete', 'order_place', 'payment', 'wishlist_add', 'cart_add'));

ALTER TABLE otp_verifications ADD CONSTRAINT fk_otp_type 
CHECK (otp_type IN ('login', 'register', 'password_reset', 'email_verify', 'phone_verify', 'transaction'));

-- Create triggers for automatic updates
DELIMITER //
CREATE TRIGGER update_user_profile_timestamp 
BEFORE UPDATE ON user_profiles 
FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_user_address_timestamp 
BEFORE UPDATE ON user_addresses 
FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_social_login_timestamp 
BEFORE UPDATE ON social_login_accounts 
FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_security_settings_timestamp 
BEFORE UPDATE ON user_security_settings 
FOR EACH ROW 
BEGIN
    SET NEW.updated_at = CURRENT_TIMESTAMP;
END//
DELIMITER ;

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE GetUserActiveSessions(IN p_user_id INT)
BEGIN
    SELECT 
        session_id,
        device_type,
        device_name,
        browser,
        operating_system,
        ip_address,
        location,
        created_at,
        last_accessed_at,
        expires_at,
        is_remembered,
        is_suspicious
    FROM user_sessions 
    WHERE user_id = p_user_id AND is_active = TRUE 
    ORDER BY last_accessed_at DESC;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetUserUnreadNotifications(IN p_user_id INT)
BEGIN
    SELECT 
        notification_id,
        type,
        title,
        message,
        action_url,
        action_text,
        image_url,
        priority,
        category,
        created_at
    FROM user_notifications 
    WHERE user_id = p_user_id AND is_read = FALSE AND is_active = TRUE 
    ORDER BY priority DESC, created_at DESC;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CleanupExpiredSessions()
BEGIN
    UPDATE user_sessions 
    SET is_active = FALSE 
    WHERE expires_at < NOW() OR (last_accessed_at < DATE_SUB(NOW(), INTERVAL 30 DAY));
    
    DELETE FROM user_sessions 
    WHERE is_active = FALSE AND last_accessed_at < DATE_SUB(NOW(), INTERVAL 7 DAY);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CleanupExpiredNotifications()
BEGIN
    UPDATE user_notifications 
    SET is_active = FALSE 
    WHERE (expires_at IS NOT NULL AND expires_at < NOW()) 
       OR (created_at < DATE_SUB(NOW(), INTERVAL 90 DAY));
    
    DELETE FROM user_notifications 
    WHERE is_active = FALSE AND created_at < DATE_SUB(NOW(), INTERVAL 30 DAY);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE MarkNotificationAsRead(IN p_notification_id INT, IN p_user_id INT)
BEGIN
    UPDATE user_notifications 
    SET is_read = TRUE, read_at = NOW() 
    WHERE notification_id = p_notification_id AND user_id = p_user_id;
END//
DELIMITER ;

-- Schedule cleanup procedures (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS cleanup_expired_sessions
ON SCHEDULE EVERY 1 HOUR
DO CALL CleanupExpiredSessions();

CREATE EVENT IF NOT EXISTS cleanup_expired_notifications
ON SCHEDULE EVERY 6 HOUR
DO CALL CleanupExpiredNotifications();
