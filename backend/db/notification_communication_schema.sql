-- FashionStore Notification & Communication System Schema
-- For enterprise-grade notification and communication ecosystem

-- 1. Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    type ENUM('order_update', 'delivery_update', 'payment_update', 'wishlist_alert', 'price_drop', 'promotional', 'admin_announcement', 'system_alert', 'support_message', 'fraud_alert', 'account_update') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    content TEXT,
    category VARCHAR(100) DEFAULT 'general',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    status ENUM('unread', 'read', 'archived', 'dismissed') DEFAULT 'unread',
    source_entity_type ENUM('order', 'payment', 'wishlist', 'product', 'user', 'admin', 'system') DEFAULT 'system',
    source_entity_id INT,
    action_url VARCHAR(500),
    action_required BOOLEAN DEFAULT FALSE,
    action_text VARCHAR(255),
    image_url VARCHAR(500),
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_notifications_user (user_id),
    INDEX idx_notifications_type (type),
    INDEX idx_notifications_status (status),
    INDEX idx_notifications_priority (priority),
    INDEX idx_notifications_category (category),
    INDEX idx_notifications_created (created_at),
    INDEX idx_notifications_expires (expires_at),
    INDEX idx_notifications_source (source_entity_type, source_entity_id)
);

-- 2. Notification Preferences Table
CREATE TABLE IF NOT EXISTS notification_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    channel ENUM('email', 'push', 'sms', 'in_app') NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    frequency ENUM('immediate', 'hourly', 'daily', 'weekly', 'never') DEFAULT 'immediate',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_notification_preferences_user_type_channel (user_id, notification_type, channel),
    INDEX idx_notification_preferences_user (user_id),
    INDEX idx_notification_preferences_type (notification_type),
    INDEX idx_notification_preferences_channel (channel)
);

-- 3. Email Templates Table
CREATE TABLE IF NOT EXISTS email_templates (
    template_id INT AUTO_INCREMENT PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL UNIQUE,
    template_code VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    html_content TEXT NOT NULL,
    text_content TEXT,
    variables JSON, -- List of template variables
    language VARCHAR(10) DEFAULT 'en',
    category VARCHAR(50) DEFAULT 'general',
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_email_templates_code (template_code),
    INDEX idx_email_templates_category (category),
    INDEX idx_email_templates_language (language),
    INDEX idx_email_templates_active (is_active)
);

-- 4. Email Queue Table
CREATE TABLE IF NOT EXISTS email_queue (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    recipient_email VARCHAR(255) NOT NULL,
    recipient_user_id INT,
    template_code VARCHAR(100) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    html_content TEXT NOT NULL,
    text_content TEXT,
    variables JSON,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    status ENUM('pending', 'processing', 'sent', 'failed', 'retry') DEFAULT 'pending',
    attempts INT DEFAULT 0,
    max_attempts INT DEFAULT 3,
    last_attempt_at TIMESTAMP NULL,
    next_attempt_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_email_queue_status (status),
    INDEX idx_email_queue_priority (priority),
    INDEX idx_email_queue_next_attempt (next_attempt_at),
    INDEX idx_email_queue_recipient (recipient_email),
    INDEX idx_email_queue_template (template_code)
);

-- 5. Push Notification Queue Table
CREATE TABLE IF NOT EXISTS push_notification_queue (
    queue_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    device_token VARCHAR(500),
    platform ENUM('web', 'ios', 'android') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    data JSON,
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    status ENUM('pending', 'sent', 'failed', 'retry') DEFAULT 'pending',
    attempts INT DEFAULT 0,
    max_attempts INT DEFAULT 3,
    last_attempt_at TIMESTAMP NULL,
    next_attempt_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_push_queue_status (status),
    INDEX idx_push_queue_priority (priority),
    INDEX idx_push_queue_next_attempt (next_attempt_at),
    INDEX idx_push_queue_user (user_id),
    INDEX idx_push_queue_platform (platform)
);

-- 6. User Conversations Table
CREATE TABLE IF NOT EXISTS user_conversations (
    conversation_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    participant_id INT, -- For admin-user conversations
    type ENUM('support', 'admin', 'user', 'delivery', 'system') NOT NULL,
    title VARCHAR(255) NOT NULL,
    status ENUM('active', 'archived', 'closed') DEFAULT 'active',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    last_message_at TIMESTAMP NULL,
    last_message_preview TEXT,
    unread_count INT DEFAULT 0,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (participant_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_conversations_user (user_id),
    INDEX idx_conversations_participant (participant_id),
    INDEX idx_conversations_type (type),
    INDEX idx_conversations_status (status),
    INDEX idx_conversations_priority (priority),
    INDEX idx_conversations_last_message (last_message_at)
);

-- 7. User Messages Table
CREATE TABLE IF NOT EXISTS user_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    conversation_id INT NOT NULL,
    sender_id INT NOT NULL,
    recipient_id INT,
    message_type ENUM('text', 'image', 'file', 'system') NOT NULL DEFAULT 'text',
    content TEXT NOT NULL,
    attachments JSON,
    is_read BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    read_at TIMESTAMP NULL,
    deleted_at TIMESTAMP NULL,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (conversation_id) REFERENCES user_conversations(conversation_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (recipient_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_messages_conversation (conversation_id),
    INDEX idx_messages_sender (sender_id),
    INDEX idx_messages_recipient (recipient_id),
    INDEX idx_messages_type (message_type),
    INDEX idx_messages_read (is_read),
    INDEX idx_messages_created (created_at)
);

-- 8. Support Tickets Table (Enhanced)
CREATE TABLE IF NOT EXISTS support_tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    admin_user_id INT,
    subject VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('technical', 'billing', 'shipping', 'general', 'product', 'account', 'fraud', 'complaint') DEFAULT 'general',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    status ENUM('open', 'pending', 'in_progress', 'resolved', 'closed', 'escalated') DEFAULT 'open',
    source ENUM('email', 'chat', 'phone', 'web', 'mobile_app', 'admin') DEFAULT 'web',
    assigned_to INT,
    escalation_level ENUM('level1', 'level2', 'level3') DEFAULT 'level1',
    customer_satisfaction INT DEFAULT NULL, -- 1-5 rating
    resolution TEXT,
    resolution_time_minutes INT DEFAULT NULL,
    first_response_time_minutes INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    closed_at TIMESTAMP NULL,
    last_customer_reply_at TIMESTAMP NULL,
    last_admin_reply_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_support_tickets_number (ticket_number),
    INDEX idx_support_tickets_user (user_id),
    INDEX idx_support_tickets_admin (admin_user_id),
    INDEX idx_support_tickets_assigned (assigned_to),
    INDEX idx_support_tickets_status (status),
    INDEX idx_support_tickets_priority (priority),
    INDEX idx_support_tickets_category (category),
    INDEX idx_support_tickets_created (created_at),
    INDEX idx_support_tickets_escalation (escalation_level),
    INDEX idx_support_tickets_satisfaction (customer_satisfaction)
);

-- 9. Support Ticket Messages Table (Enhanced)
CREATE TABLE IF NOT EXISTS support_ticket_messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    sender_type ENUM('user', 'admin', 'system') NOT NULL,
    sender_id INT NOT NULL,
    message TEXT NOT NULL,
    message_type ENUM('text', 'attachment', 'note', 'system_update') DEFAULT 'text',
    is_internal BOOLEAN DEFAULT FALSE,
    is_visible_to_customer BOOLEAN DEFAULT TRUE,
    attachment_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ticket_id) REFERENCES support_tickets(ticket_id) ON DELETE CASCADE,
    FOREIGN KEY (sender_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_support_messages_ticket (ticket_id),
    INDEX idx_support_messages_sender (sender_type, sender_id),
    INDEX idx_support_messages_created (created_at),
    INDEX idx_support_messages_internal (is_internal),
    INDEX idx_support_messages_visible (is_visible_to_customer)
);

-- 10. Notification Engagement Tracking Table
CREATE TABLE IF NOT EXISTS notification_engagement (
    engagement_id INT AUTO_INCREMENT PRIMARY KEY,
    notification_id INT NOT NULL,
    user_id INT NOT NULL,
    engagement_type ENUM('open', 'click', 'dismiss', 'mark_read', 'share') NOT NULL,
    engagement_data JSON,
    user_agent TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (notification_id) REFERENCES notifications(notification_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_engagement_notification (notification_id),
    INDEX idx_engagement_user (user_id),
    INDEX idx_engagement_type (engagement_type),
    INDEX idx_engagement_created (created_at)
);

-- 11. Email Delivery Tracking Table
CREATE TABLE IF NOT EXISTS email_delivery_tracking (
    tracking_id INT AUTO_INCREMENT PRIMARY KEY,
    email_queue_id INT NOT NULL,
    recipient_email VARCHAR(255) NOT NULL,
    template_code VARCHAR(100) NOT NULL,
    status ENUM('sent', 'delivered', 'bounced', 'opened', 'clicked', 'spam', 'failed') NOT NULL,
    delivery_timestamp TIMESTAMP NULL,
    open_timestamp TIMESTAMP NULL,
    click_timestamp TIMESTAMP NULL,
    bounce_reason VARCHAR(255),
    error_message TEXT,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (email_queue_id) REFERENCES email_queue(queue_id) ON DELETE CASCADE,
    INDEX idx_email_tracking_queue (email_queue_id),
    INDEX idx_email_tracking_status (status),
    INDEX idx_email_tracking_recipient (recipient_email),
    INDEX idx_email_tracking_template (template_code),
    INDEX idx_email_tracking_created (created_at)
);

-- 12. Push Notification Tracking Table
CREATE TABLE IF NOT EXISTS push_notification_tracking (
    tracking_id INT AUTO_INCREMENT PRIMARY KEY,
    push_queue_id INT NOT NULL,
    user_id INT NOT NULL,
    device_token VARCHAR(500),
    platform ENUM('web', 'ios', 'android') NOT NULL,
    status ENUM('sent', 'delivered', 'opened', 'clicked', 'failed', 'dismissed') NOT NULL,
    delivery_timestamp TIMESTAMP NULL,
    open_timestamp TIMESTAMP NULL,
    click_timestamp TIMESTAMP NULL,
    error_message TEXT,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (push_queue_id) REFERENCES push_notification_queue(queue_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_push_tracking_queue (push_queue_id),
    INDEX idx_push_tracking_user (user_id),
    INDEX idx_push_tracking_status (status),
    INDEX idx_push_tracking_platform (platform),
    INDEX idx_push_tracking_created (created_at)
);

-- 13. Communication Preferences Table
CREATE TABLE IF NOT EXISTS communication_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    communication_type ENUM('marketing', 'promotional', 'transactional', 'support', 'newsletter') NOT NULL,
    channel ENUM('email', 'push', 'sms', 'in_app') NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    frequency ENUM('immediate', 'hourly', 'daily', 'weekly', 'monthly', 'never') DEFAULT 'immediate',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_communication_preferences_user_type_channel (user_id, communication_type, channel),
    INDEX idx_communication_preferences_user (user_id),
    INDEX idx_communication_preferences_type (communication_type),
    INDEX idx_communication_preferences_channel (channel)
);

-- 14. Device Tokens Table (for Push Notifications)
CREATE TABLE IF NOT EXISTS device_tokens (
    token_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    device_token VARCHAR(500) NOT NULL,
    platform ENUM('web', 'ios', 'android') NOT NULL,
    device_info JSON,
    is_active BOOLEAN DEFAULT TRUE,
    last_used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_device_tokens_user_token (user_id, device_token),
    INDEX idx_device_tokens_user (user_id),
    INDEX idx_device_tokens_platform (platform),
    INDEX idx_device_tokens_active (is_active),
    INDEX idx_device_tokens_expires (expires_at)
);

-- Insert default notification preferences
INSERT IGNORE INTO notification_preferences (user_id, notification_type, channel, enabled, frequency)
SELECT 
    u.user_id,
    np.notification_type,
    np.channel,
    np.enabled,
    np.frequency
FROM users u
CROSS JOIN (
    SELECT 'order_update' as notification_type, 'email' as channel, true as enabled, 'immediate' as frequency
    UNION SELECT 'order_update', 'push', true, 'immediate'
    UNION SELECT 'delivery_update', 'email', true, 'immediate'
    UNION SELECT 'delivery_update', 'push', true, 'immediate'
    UNION SELECT 'payment_update', 'email', true, 'immediate'
    UNION SELECT 'payment_update', 'push', true, 'immediate'
    UNION SELECT 'wishlist_alert', 'email', true, 'immediate'
    UNION SELECT 'wishlist_alert', 'push', true, 'immediate'
    UNION SELECT 'price_drop', 'email', true, 'immediate'
    UNION SELECT 'price_drop', 'push', true, 'immediate'
    UNION SELECT 'promotional', 'email', false, 'daily'
    UNION SELECT 'promotional', 'push', false, 'daily'
    UNION SELECT 'admin_announcement', 'email', true, 'immediate'
    UNION SELECT 'admin_announcement', 'push', true, 'immediate'
    UNION SELECT 'support_message', 'email', true, 'immediate'
    UNION SELECT 'support_message', 'push', true, 'immediate'
) np;

-- Insert default communication preferences
INSERT IGNORE INTO communication_preferences (user_id, communication_type, channel, enabled, frequency)
SELECT 
    u.user_id,
    cp.communication_type,
    cp.channel,
    cp.enabled,
    cp.frequency
FROM users u
CROSS JOIN (
    SELECT 'transactional' as communication_type, 'email' as channel, true as enabled, 'immediate' as frequency
    UNION SELECT 'transactional', 'push', true, 'immediate'
    UNION SELECT 'transactional', 'sms', true, 'immediate'
    UNION SELECT 'support', 'email', true, 'immediate'
    UNION SELECT 'support', 'push', true, 'immediate'
    UNION SELECT 'marketing', 'email', false, 'weekly'
    UNION SELECT 'marketing', 'push', false, 'weekly'
    UNION SELECT 'promotional', 'email', false, 'daily'
    UNION SELECT 'promotional', 'push', false, 'daily'
    UNION SELECT 'newsletter', 'email', false, 'weekly'
    UNION SELECT 'newsletter', 'push', false, 'weekly'
) cp;

-- Insert default email templates
INSERT IGNORE INTO email_templates (template_name, template_code, subject, html_content, text_content, variables, language, category) VALUES
('Order Confirmation', 'order_confirmation', 'Your FashionStore Order #{orderNumber} is Confirmed', 
'<h1>Order Confirmed</h1><p>Thank you for your order #{orderNumber}.</p><p>Order Total: #{orderAmount}</p><p>Estimated Delivery: #{deliveryDate}</p>', 
'Thank you for your order #{orderNumber}. Order Total: #{orderAmount}. Estimated Delivery: #{deliveryDate}', 
'["orderNumber", "orderAmount", "deliveryDate"], 'en', 'transactional'),

('Payment Confirmation', 'payment_confirmation', 'Payment Successful for Order #{orderNumber}', 
'<h1>Payment Successful</h1><p>Your payment of #{paymentAmount} for order #{orderNumber} has been successfully processed.</p><p>Payment Method: #{paymentMethod}</p>', 
'Your payment of #{paymentAmount} for order #{orderNumber} has been successfully processed. Payment Method: #{paymentMethod}', 
'["orderNumber", "paymentAmount", "paymentMethod"], 'en', 'transactional'),

('Shipment Confirmation', 'shipment_confirmation', 'Your Order #{orderNumber} Has Been Shipped', 
'<h1>Order Shipped</h1><p>Your order #{orderNumber} has been shipped and is on its way!</p><p>Tracking Number: #{trackingNumber}</p><p>Estimated Delivery: #{deliveryDate}</p>', 
'Your order #{orderNumber} has been shipped and is on its way! Tracking Number: #{trackingNumber}. Estimated Delivery: #{deliveryDate}', 
'["orderNumber", "trackingNumber", "deliveryDate"], 'en', 'transactional'),

('Welcome Email', 'welcome_email', 'Welcome to FashionStore!', 
'<h1>Welcome to FashionStore!</h1><p>Thank you for joining FashionStore, #{username}!</p><p>We\'re excited to have you as part of our community.</p><p>Start shopping now and discover amazing fashion deals.</p>', 
'Thank you for joining FashionStore, #{username}! We\'re excited to have you as part of our community. Start shopping now and discover amazing fashion deals.', 
'["username"], 'en', 'marketing'),

('Password Reset', 'password_reset', 'Reset Your FashionStore Password', 
'<h1>Reset Your Password</h1><p>Hi #{username},</p><p>We received a request to reset your password for your FashionStore account.</p><p>Click the link below to reset your password:</p><p><a href="#{resetLink}">Reset Password</a></p><p>This link will expire in 24 hours.</p>', 
'Hi #{username}, We received a request to reset your password for your FashionStore account. Click the link below to reset your password: #{resetLink}. This link will expire in 24 hours.', 
'["username", "resetLink"], 'en', 'transactional'),

('Price Drop Alert', 'price_drop_alert', 'Price Drop Alert for #{productName}', 
'<h1>Price Drop Alert!</h1><p>Good news! The price of #{productName} has dropped from #{oldPrice} to #{newPrice}.</p><p>Save #{savingsAmount} on your favorite item!</p><p><a href="#{productUrl}">View Product</a></p>', 
'Good news! The price of #{productName} has dropped from #{oldPrice} to #{newPrice}. Save #{savingsAmount} on your favorite item! View Product: #{productUrl}', 
'["productName", "oldPrice", "newPrice", "savingsAmount", "productUrl"], 'en', 'promotional'),

('Wishlist Back in Stock', 'wishlist_back_in_stock', 'Your Wishlist Item is Back in Stock!', 
'<h1>Back in Stock!</h1><p>Great news! #{productName} from your wishlist is now back in stock.</p><p>Don\'t miss out - get it before it\'s gone again!</p><p><a href="#{productUrl}">View Product</a></p>', 
'Great news! #{productName} from your wishlist is now back in stock. Don\'t miss out - get it before it\'s gone again! View Product: #{productUrl}', 
'["productName", "productUrl"], 'en', 'promotional'),

('Promotional Campaign', 'promotional_campaign', 'Special Offer: #{campaignTitle}', 
'<h1>#{campaignTitle}</h1><p>#{campaignMessage}</p><p>Use code: #{promoCode}</p><p>Valid until: #{expiryDate}</p><p><a href="#{campaignUrl}">Shop Now</a></p>', 
'#{campaignMessage}. Use code: #{promoCode}. Valid until: #{expiryDate}. Shop Now: #{campaignUrl}', 
'["campaignTitle", "campaignMessage", "promoCode", "expiryDate", "campaignUrl"], 'en', 'promotional');

-- Create views for common queries
CREATE OR REPLACE VIEW notification_summary AS
SELECT 
    u.user_id,
    u.username,
    COUNT(CASE WHEN n.status = 'unread' THEN 1 END) as unread_count,
    COUNT(CASE WHEN n.status = 'read' THEN 1 END) as read_count,
    COUNT(*) as total_notifications,
    COUNT(CASE WHEN n.priority = 'urgent' THEN 1 END) as urgent_notifications,
    MAX(n.created_at) as last_notification_date,
    COUNT(CASE WHEN n.type IN ('promotional', 'marketing') THEN 1 END) as marketing_notifications
FROM users u
LEFT JOIN notifications n ON u.user_id = n.user_id
GROUP BY u.user_id, u.username;

CREATE OR REPLACE VIEW email_queue_summary AS
SELECT 
    status,
    COUNT(*) as count,
    COUNT(CASE WHEN attempts > 0 THEN 1 END) as retried_count,
    AVG(attempts) as avg_attempts,
    MAX(created_at) as last_created,
    COUNT(CASE WHEN status = 'failed' THEN 1 END) as failed_count
FROM email_queue
GROUP BY status;

CREATE OR REPLACE VIEW conversation_summary AS
SELECT 
    uc.conversation_id,
    uc.username as user_name,
    uc.type,
    uc.title,
    uc.status,
    uc.priority,
    uc.unread_count,
    uc.last_message_at,
    uc.last_message_preview,
    um.message_count
FROM user_conversations uc
JOIN users uc ON uc.user_id = uc.user_id
LEFT JOIN (
    SELECT conversation_id, COUNT(*) as message_count
    FROM user_messages
    GROUP BY conversation_id
) um ON uc.conversation_id = um.conversation_id;

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE SendNotification(IN p_user_id INT, IN p_type VARCHAR(50), IN p_title VARCHAR(255), IN p_message TEXT, IN p_priority VARCHAR(20))
BEGIN
    INSERT INTO notifications (user_id, type, title, message, priority, created_at)
    VALUES (p_user_id, p_type, p_title, p_message, p_priority, NOW());
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE MarkNotificationAsRead(IN p_user_id INT, IN p_notification_id INT)
BEGIN
    UPDATE notifications 
    SET status = 'read', read_at = NOW()
    WHERE notification_id = p_notification_id AND user_id = p_user_id;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE ArchiveOldNotifications(IN p_days_old INT)
BEGIN
    UPDATE notifications 
    SET status = 'archived'
    WHERE status != 'archived' 
        AND created_at < DATE_SUB(NOW(), INTERVAL p_days_old DAY);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CleanupExpiredNotifications()
BEGIN
    DELETE FROM notifications 
    WHERE expires_at IS NOT NULL 
        AND expires_at < NOW();
END//
DELIMITER ;

-- Create triggers for automatic updates
DELIMITER //
CREATE TRIGGER update_conversation_unread_count
AFTER INSERT ON user_messages
FOR EACH ROW
BEGIN
    UPDATE user_conversations 
    SET unread_count = unread_count + 1,
        last_message_at = NEW.created_at,
        last_message_preview = LEFT(NEW.content, 100),
        updated_at = NOW()
    WHERE conversation_id = NEW.conversation_id;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER decrement_unread_count_on_read
AFTER UPDATE ON user_messages
FOR EACH ROW
BEGIN
    IF NEW.is_read = TRUE AND OLD.is_read = FALSE THEN
        UPDATE user_conversations 
        SET unread_count = GREATEST(unread_count - 1, 0),
            updated_at = NOW()
        WHERE conversation_id = NEW.conversation_id;
    END IF;
END//
DELIMITER ;

-- Schedule background jobs (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS cleanup_expired_notifications
ON SCHEDULE EVERY 1 HOUR
DO CALL CleanupExpiredNotifications();

CREATE EVENT IF NOT EXISTS archive_old_notifications
ON SCHEDULE EVERY 1 DAY
STARTS '02:00:00'
DO CALL ArchiveOldNotifications(30);

CREATE EVENT IF NOT EXISTS process_email_queue
ON SCHEDULE EVERY 1 MINUTE
DO CALL ProcessEmailQueue();

CREATE EVENT IF NOT EXISTS process_push_queue
ON SCHEDULE EVERY 1 MINUTE
DO CALL ProcessPushQueue();

-- Create indexes for better performance
CREATE INDEX idx_notifications_composite ON notifications(user_id, status, priority, created_at);
CREATE INDEX idx_email_queue_composite ON email_queue(status, priority, next_attempt_at, created_at);
CREATE INDEX idx_push_queue_composite ON push_notification_queue(status, priority, next_attempt_at, created_at);
CREATE INDEX idx_conversations_composite ON user_conversations(user_id, status, priority, last_message_at);
CREATE INDEX idx_messages_composite ON user_messages(conversation_id, sender_id, is_read, created_at);
CREATE INDEX idx_support_tickets_composite ON support_tickets(user_id, status, priority, created_at);
CREATE INDEX idx_support_messages_composite ON support_ticket_messages(ticket_id, sender_type, created_at);

-- Add foreign key constraints for better data integrity
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_type CHECK (type IN ('order_update', 'delivery_update', 'payment_update', 'wishlist_alert', 'price_drop', 'promotional', 'admin_announcement', 'system_alert', 'support_message', 'fraud_alert', 'account_update'));
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_status CHECK (status IN ('unread', 'read', 'archived', 'dismissed'));

ALTER TABLE notification_preferences ADD CONSTRAINT chk_notification_preferences_channel CHECK (channel IN ('email', 'push', 'sms', 'in_app'));
ALTER TABLE notification_preferences ADD CONSTRAINT chk_notification_preferences_frequency CHECK (frequency IN ('immediate', 'hourly', 'daily', 'weekly', 'never'));

ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));
ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_status CHECK (status IN ('pending', 'processing', 'sent', 'failed', 'retry'));

ALTER TABLE push_notification_queue ADD CONSTRAINT chk_push_queue_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));
ALTER TABLE push_notification_queue ADD CONSTRAINT chk_push_queue_status CHECK (status IN ('pending', 'sent', 'failed', 'retry'));
ALTER TABLE push_notification_queue ADD CONSTRAINT chk_push_queue_platform CHECK (platform IN ('web', 'ios', 'android'));

ALTER TABLE user_conversations ADD CONSTRAINT chk_conversations_type CHECK (type IN ('support', 'admin', 'user', 'delivery', 'system'));
ALTER TABLE user_conversations ADD CONSTRAINT chk_conversations_status CHECK (status IN ('active', 'archived', 'closed'));
ALTER TABLE user_conversations ADD CONSTRAINT chk_conversations_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));

ALTER TABLE user_messages ADD CONSTRAINT chk_messages_type CHECK (message_type IN ('text', 'image', 'file', 'system'));

ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_status CHECK (status IN ('open', 'pending', 'in_progress', 'resolved', 'closed', 'escalated'));
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_escalation CHECK (escalation_level IN ('level1', 'level2', 'level3'));

ALTER TABLE support_ticket_messages ADD CONSTRAINT chk_support_messages_sender_type CHECK (sender_type IN ('user', 'admin', 'system'));
ALTER TABLE support_ticket_messages ADD CONSTRAINT chk_support_messages_type CHECK (message_type IN ('text', 'attachment', 'note', 'system_update'));

ALTER TABLE notification_engagement ADD CONSTRAINT chk_engagement_type CHECK (engagement_type IN ('open', 'click', 'dismiss', 'mark_read', 'share'));

ALTER TABLE email_delivery_tracking ADD CONSTRAINT chk_email_tracking_status CHECK (status IN ('sent', 'delivered', 'bounced', 'opened', 'clicked', 'spam', 'failed'));

ALTER TABLE push_notification_tracking ADD CONSTRAINT chk_push_tracking_status CHECK (status IN ('sent', 'delivered', 'opened', 'clicked', 'failed', 'dismissed'));

ALTER TABLE communication_preferences ADD CONSTRAINT chk_communication_preferences_type CHECK (communication_type IN ('marketing', 'promotional', 'transactional', 'support', 'newsletter'));
ALTER TABLE communication_preferences ADD CONSTRAINT chk_communication_preferences_channel CHECK (channel IN ('email', 'push', 'sms', 'in_app'));
ALTER TABLE communication_preferences ADD CONSTRAINT chk_communication_preferences_frequency CHECK (frequency IN ('immediate', 'hourly', 'daily', 'weekly', 'monthly', 'never'));

ALTER TABLE device_tokens ADD CONSTRAINT chk_device_tokens_platform CHECK (platform IN ('web', 'ios', 'android'));

-- Add check constraints for data validation
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_title CHECK (title IS NOT NULL AND title != '');
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_message CHECK (message IS NOT NULL AND message != '');
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_priority CHECK (priority IS NOT NULL);
ALTER TABLE notifications ADD CONSTRAINT chk_notifications_created CHECK (created_at IS NOT NULL);

ALTER TABLE email_templates ADD CONSTRAINT chk_email_templates_name CHECK (template_name IS NOT NULL AND template_name != '');
ALTER TABLE email_templates ADD CONSTRAINT chk_email_templates_code CHECK (template_code IS NOT NULL AND template_code != '');
ALTER TABLE email_templates ADD CONSTRAINT chk_email_templates_subject CHECK (subject IS NOT NULL AND subject != '');
ALTER TABLE email_templates ADD CONSTRAINT chk_email_templates_content CHECK (html_content IS NOT NULL AND html_content != '');

ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_recipient CHECK (recipient_email IS NOT NULL AND recipient_email != '');
ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_template CHECK (template_code IS NOT NULL AND template_code != '');
ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_subject CHECK (subject IS NOT NULL AND subject != '');
ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_content CHECK (html_content IS NOT NULL AND html_content != '');
ALTER TABLE email_queue ADD CONSTRAINT chk_email_queue_attempts CHECK (attempts >= 0 AND attempts <= max_attempts);

ALTER TABLE push_notification_queue ADD CONSTRAINT chk_push_queue_title CHECK (title IS NOT NULL AND title != '');
ALTER TABLE push_notification_queue ADD CONSTRAINT chk_push_queue_message CHECK (message IS NOT NULL AND message != '');
ALTER TABLE push_notification_queue ADD CONSTRAINT chk_push_queue_attempts CHECK (attempts >= 0 AND attempts <= max_attempts);

ALTER TABLE user_conversations ADD CONSTRAINT chk_conversations_title CHECK (title IS NOT NULL AND title != '');
ALTER TABLE user_conversations ADD CONSTRAINT chk_conversations_unread CHECK (unread_count >= 0);

ALTER TABLE user_messages ADD CONSTRAINT chk_messages_content CHECK (content IS NOT NULL AND content != '');
ALTER TABLE user_messages ADD CONSTRAINT chk_messages_sender_id CHECK (sender_id IS NOT NULL);

ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_number CHECK (ticket_number IS NOT NULL AND ticket_number != '');
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_subject CHECK (subject IS NOT NULL AND subject != '');
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_description CHECK (description IS NOT NULL AND description != '');

ALTER TABLE support_ticket_messages ADD CONSTRAINT chk_support_messages_message CHECK (message IS NOT NULL AND message != '');

ALTER TABLE notification_engagement ADD CONSTRAINT chk_engagement_notification_id CHECK (notification_id IS NOT NULL);
ALTER TABLE notification_engagement ADD CONSTRAINT chk_engagement_user_id CHECK (user_id IS NOT NULL);
ALTER TABLE notification_engagement ADD CONSTRAINT chk_engagement_type CHECK (engagement_type IS NOT NULL);

-- Add full-text indexes for search optimization
CREATE FULLTEXT INDEX ft_notifications_search ON notifications(title, message, content);
CREATE FULLTEXT INDEX ft_email_templates_search ON email_templates(subject, html_content, text_content);
CREATE FULLTEXT INDEX ft_support_tickets_search ON support_tickets(subject, description);
CREATE FULLTEXT INDEX ft_support_messages_search ON support_ticket_messages(message);

-- Add partitioning for large tables (optional for very high traffic)
-- ALTER TABLE notifications PARTITION BY RANGE (TO_DAYS(created_at)) (
--     PARTITION p_old VALUES LESS THAN (TO_DAYS('2024-01-01')),
--     PARTITION p_2024 VALUES LESS THAN (TO_DAYS('2025-01-01')),
--     PARTITION p_2025 VALUES LESS THAN (TO_DAYS('2026-01-01')),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- Create additional stored procedures for maintenance
DELIMITER //
CREATE PROCEDURE ProcessEmailQueue()
BEGIN
    -- Process pending emails with retry logic
    UPDATE email_queue 
    SET status = 'processing',
        attempts = attempts + 1,
        last_attempt_at = NOW(),
        next_attempt_at = CASE 
            WHEN status = 'failed' THEN DATE_ADD(NOW(), INTERVAL attempts * 5 MINUTE)
            ELSE NOW()
        END
    WHERE status = 'pending' 
        OR (status = 'retry' AND next_attempt_at <= NOW());
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE ProcessPushQueue()
BEGIN
    -- Process pending push notifications with retry logic
    UPDATE push_notification_queue 
    SET status = 'sent',
        attempts = attempts + 1,
        last_attempt_at = NOW(),
        next_attempt_at = CASE 
            WHEN status = 'failed' THEN DATE_ADD(NOW(), INTERVAL attempts * 2 MINUTE)
            ELSE NOW()
        END
    WHERE status = 'pending' 
        OR (status = 'retry' AND next_attempt_at <= NOW());
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE UpdateNotificationUnreadCount(IN p_user_id INT)
BEGIN
    UPDATE user_conversations 
    SET unread_count = (
        SELECT COUNT(*) 
        FROM user_messages 
        WHERE conversation_id = user_conversations.conversation_id 
            AND recipient_id = p_user_id 
            AND is_read = FALSE
    )
    WHERE user_id = p_user_id;
END//
DELIMITER ;
