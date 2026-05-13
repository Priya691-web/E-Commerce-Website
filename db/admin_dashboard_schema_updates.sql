-- FashionStore Admin Dashboard System Schema Updates
-- For enterprise-grade commerce management system

-- 1. Admin Notifications Table
CREATE TABLE IF NOT EXISTS admin_notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id INT NOT NULL,
    notification_type ENUM('low_stock', 'out_of_stock', 'failed_payment', 'suspicious_activity', 'order_escalation', 'system_alert', 'fraud_detection', 'customer_complaint', 'inventory_alert', 'performance_warning') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    severity ENUM('info', 'warning', 'error', 'critical') DEFAULT 'info',
    status ENUM('unread', 'read', 'dismissed', 'archived') DEFAULT 'unread',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    source_entity_type ENUM('product', 'order', 'user', 'payment', 'system') DEFAULT 'system',
    source_entity_id INT,
    action_url VARCHAR(500),
    action_required BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    dismissed_at TIMESTAMP NULL,
    expires_at TIMESTAMP NULL,
    metadata JSON,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_admin_notifications_user (admin_user_id),
    INDEX idx_admin_notifications_type (notification_type),
    INDEX idx_admin_notifications_status (status),
    INDEX idx_admin_notifications_severity (severity),
    INDEX idx_admin_notifications_priority (priority),
    INDEX idx_admin_notifications_created (created_at),
    INDEX idx_admin_notifications_expires (expires_at)
);

-- 2. Support Tickets Table
CREATE TABLE IF NOT EXISTS support_tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INT,
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
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
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

-- 3. Support Ticket Messages Table
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

-- 4. User Moderation Table
CREATE TABLE IF NOT EXISTS user_moderation (
    moderation_id INT AUTO_INCREMENT PRIMARY KEY,
    target_user_id INT NOT NULL,
    admin_user_id INT NOT NULL,
    action ENUM('suspend', 'ban', 'verify', 'warn', 'restrict', 'unban', 'unsuspend') NOT NULL,
    reason TEXT NOT NULL,
    duration_days INT DEFAULT NULL, -- For suspend/ban actions
    status ENUM('active', 'expired', 'appealed', 'overturned') DEFAULT 'active',
    notes TEXT,
    evidence JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    lifted_at TIMESTAMP NULL,
    lifted_by INT NULL,
    FOREIGN KEY (target_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (lifted_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_user_moderation_target (target_user_id),
    INDEX idx_user_moderation_admin (admin_user_id),
    INDEX idx_user_moderation_action (action),
    INDEX idx_user_moderation_status (status),
    INDEX idx_user_moderation_created (created_at),
    INDEX idx_user_moderation_expires (expires_at)
);

-- 5. Fraud Detection Cases Table
CREATE TABLE IF NOT EXISTS fraud_detection_cases (
    case_id INT AUTO_INCREMENT PRIMARY KEY,
    case_number VARCHAR(50) NOT NULL UNIQUE,
    user_id INT,
    order_id INT,
    risk_score DECIMAL(5, 4) DEFAULT 0.0000,
    risk_level ENUM('low', 'medium', 'high', 'critical') DEFAULT 'medium',
    status ENUM('pending', 'investigating', 'confirmed_fraud', 'false_positive', 'insufficient_evidence', 'escalated') DEFAULT 'pending',
    fraud_type ENUM('payment_fraud', 'account_takeover', 'identity_theft', 'friendly_fraud', 'card_testing', 'return_fraud', 'other') DEFAULT 'other',
    detection_method ENUM('automated', 'manual', 'customer_report', 'bank_alert', 'pattern_analysis') DEFAULT 'automated',
    investigation_notes TEXT,
    evidence JSON,
    flagged_transactions JSON,
    resolution_action ENUM('ban_user', 'refund_order', 'monitor_account', 'block_payment', 'no_action', 'legal_action') DEFAULT NULL,
    resolution_details TEXT,
    financial_impact DECIMAL(15, 2) DEFAULT 0.00,
    prevented_loss DECIMAL(15, 2) DEFAULT 0.00,
    assigned_to INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP NULL,
    escalated_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL,
    FOREIGN KEY (assigned_to) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_fraud_cases_number (case_number),
    INDEX idx_fraud_cases_user (user_id),
    INDEX idx_fraud_cases_order (order_id),
    INDEX idx_fraud_cases_status (status),
    INDEX idx_fraud_cases_risk (risk_level, risk_score),
    INDEX idx_fraud_cases_type (fraud_type),
    INDEX idx_fraud_cases_assigned (assigned_to),
    INDEX idx_fraud_cases_created (created_at),
    INDEX idx_fraud_cases_resolved (resolved_at)
);

-- 6. Analytics Snapshots Table
CREATE TABLE IF NOT EXISTS analytics_snapshots (
    snapshot_id INT AUTO_INCREMENT PRIMARY KEY,
    snapshot_type ENUM('daily', 'weekly', 'monthly', 'quarterly', 'yearly', 'realtime') NOT NULL,
    snapshot_date DATE NOT NULL,
    metric_category ENUM('revenue', 'orders', 'users', 'inventory', 'payments', 'refunds', 'traffic', 'conversion', 'performance') NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(20, 4) NOT NULL,
    metric_change_percent DECIMAL(5, 2) DEFAULT 0.00,
    comparison_period ENUM('previous_day', 'previous_week', 'previous_month', 'previous_quarter', 'previous_year') DEFAULT NULL,
    metadata JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_analytics_snapshot (snapshot_type, snapshot_date, metric_category, metric_name),
    INDEX idx_analytics_snapshots_type (snapshot_type),
    INDEX idx_analytics_snapshots_date (snapshot_date),
    INDEX idx_analytics_snapshots_category (metric_category),
    INDEX idx_analytics_snapshots_created (created_at)
);

-- 7. Refund Requests Table
CREATE TABLE IF NOT EXISTS refund_requests (
    refund_id INT AUTO_INCREMENT PRIMARY KEY,
    refund_number VARCHAR(50) NOT NULL UNIQUE,
    order_id INT NOT NULL,
    user_id INT NOT NULL,
    admin_user_id INT,
    refund_amount DECIMAL(10, 2) NOT NULL,
    refund_reason ENUM('damaged_item', 'wrong_item', 'not_as_described', 'late_delivery', 'customer_request', 'defective_product', 'size_issue', 'quality_issue', 'other') NOT NULL,
    refund_type ENUM('full_refund', 'partial_refund', 'exchange', 'store_credit') DEFAULT 'full_refund',
    status ENUM('pending', 'approved', 'rejected', 'processed', 'cancelled') DEFAULT 'pending',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    customer_notes TEXT,
    admin_notes TEXT,
    evidence JSON, -- Photos, videos, documents
    processing_fee DECIMAL(8, 2) DEFAULT 0.00,
    refund_method ENUM('original_payment', 'store_credit', 'bank_transfer', 'wallet') DEFAULT 'original_payment',
    refund_reference VARCHAR(255),
    approved_at TIMESTAMP NULL,
    processed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_refund_requests_number (refund_number),
    INDEX idx_refund_requests_order (order_id),
    INDEX idx_refund_requests_user (user_id),
    INDEX idx_refund_requests_admin (admin_user_id),
    INDEX idx_refund_requests_status (status),
    INDEX idx_refund_requests_reason (refund_reason),
    INDEX idx_refund_requests_priority (priority),
    INDEX idx_refund_requests_created (created_at),
    INDEX idx_refund_requests_approved (approved_at),
    INDEX idx_refund_requests_processed (processed_at)
);

-- 8. Admin Audit Log Table
CREATE TABLE IF NOT EXISTS admin_audit_log (
    audit_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_user_id INT NOT NULL,
    action_type ENUM('create', 'update', 'delete', 'view', 'login', 'logout', 'export', 'import', 'approve', 'reject', 'escalate', 'assign', 'unassign') NOT NULL,
    entity_type ENUM('product', 'order', 'user', 'category', 'support_ticket', 'fraud_case', 'refund_request', 'inventory', 'payment', 'system_setting') NOT NULL,
    entity_id INT,
    old_values JSON,
    new_values JSON,
    action_description TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    session_id VARCHAR(255),
    request_id VARCHAR(255),
    success BOOLEAN DEFAULT TRUE,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_admin_audit_admin (admin_user_id),
    INDEX idx_admin_audit_action (action_type),
    INDEX idx_admin_audit_entity (entity_type, entity_id),
    INDEX idx_admin_audit_success (success),
    INDEX idx_admin_audit_created (created_at),
    INDEX idx_admin_audit_session (session_id),
    INDEX idx_admin_audit_request (request_id)
);

-- 9. Admin Roles and Permissions Table
CREATE TABLE IF NOT EXISTS admin_roles (
    role_id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    role_description TEXT,
    permissions JSON NOT NULL, -- Array of permission strings
    is_system_role BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_admin_roles_name (role_name),
    INDEX idx_admin_roles_active (is_active)
);

-- 10. Admin User Roles Assignment Table
CREATE TABLE IF NOT EXISTS admin_user_roles (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    assigned_by INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES admin_roles(role_id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_by) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_admin_user_role (user_id, role_id),
    INDEX idx_admin_user_roles_user (user_id),
    INDEX idx_admin_user_roles_role (role_id),
    INDEX idx_admin_user_roles_active (is_active),
    INDEX idx_admin_user_roles_expires (expires_at)
);

-- 11. Canned Responses Table
CREATE TABLE IF NOT EXISTS canned_responses (
    response_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category ENUM('technical', 'billing', 'shipping', 'general', 'product', 'account', 'fraud', 'complaint') DEFAULT 'general',
    language VARCHAR(10) DEFAULT 'en',
    tags JSON, -- Array of tags for search
    usage_count INT DEFAULT 0,
    created_by INT NOT NULL,
    updated_by INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (updated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_canned_responses_category (category),
    INDEX idx_canned_responses_language (language),
    INDEX idx_canned_responses_active (is_active),
    INDEX idx_canned_responses_usage (usage_count),
    FULLTEXT INDEX ft_canned_responses_search (title, content)
);

-- 12. SLA Settings Table
CREATE TABLE IF NOT EXISTS sla_settings (
    sla_id INT AUTO_INCREMENT PRIMARY KEY,
    sla_type ENUM('response_time', 'resolution_time', 'escalation_time') NOT NULL,
    ticket_category ENUM('technical', 'billing', 'shipping', 'general', 'product', 'account', 'fraud', 'complaint') DEFAULT 'general',
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    target_minutes INT NOT NULL,
    warning_minutes INT DEFAULT NULL,
    business_hours_only BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sla_settings (sla_type, ticket_category, priority),
    INDEX idx_sla_settings_type (sla_type),
    INDEX idx_sla_settings_category (ticket_category),
    INDEX idx_sla_settings_priority (priority),
    INDEX idx_sla_settings_active (is_active)
);

-- 13. Inventory Movements Table
CREATE TABLE IF NOT EXISTS inventory_movements (
    movement_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    variant_id INT,
    movement_type ENUM('in', 'out', 'adjustment', 'transfer', 'return', 'damage', 'theft', 'expired') NOT NULL,
    quantity INT NOT NULL,
    quantity_before INT NOT NULL,
    quantity_after INT NOT NULL,
    reason VARCHAR(255),
    reference_type ENUM('order', 'purchase', 'adjustment', 'transfer', 'return', 'audit', 'damage_report') DEFAULT NULL,
    reference_id INT DEFAULT NULL,
    location_from VARCHAR(100),
    location_to VARCHAR(100),
    cost_per_unit DECIMAL(10, 2) DEFAULT 0.00,
    total_cost DECIMAL(15, 2) DEFAULT 0.00,
    performed_by INT,
    approved_by INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    FOREIGN KEY (performed_by) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (approved_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_inventory_movements_product (product_id),
    INDEX idx_inventory_movements_variant (variant_id),
    INDEX idx_inventory_movements_type (movement_type),
    INDEX idx_inventory_movements_reference (reference_type, reference_id),
    INDEX idx_inventory_movements_performed (performed_by),
    INDEX idx_inventory_movements_created (created_at)
);

-- 14. Reorder Requests Table
CREATE TABLE IF NOT EXISTS reorder_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    request_number VARCHAR(50) NOT NULL UNIQUE,
    product_id INT NOT NULL,
    variant_id INT,
    requested_quantity INT NOT NULL,
    current_stock INT DEFAULT 0,
    reorder_level INT DEFAULT 0,
    max_stock INT DEFAULT 0,
    supplier VARCHAR(255),
    priority ENUM('low', 'medium', 'high', 'urgent') DEFAULT 'medium',
    status ENUM('pending', 'approved', 'rejected', 'ordered', 'received', 'cancelled') DEFAULT 'pending',
    unit_cost DECIMAL(10, 2) DEFAULT 0.00,
    total_cost DECIMAL(15, 2) DEFAULT 0.00,
    expected_delivery_date DATE,
    actual_delivery_date DATE,
    requested_by INT NOT NULL,
    approved_by INT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    approved_at TIMESTAMP NULL,
    ordered_at TIMESTAMP NULL,
    received_at TIMESTAMP NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    FOREIGN KEY (requested_by) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_reorder_requests_number (request_number),
    INDEX idx_reorder_requests_product (product_id),
    INDEX idx_reorder_requests_status (status),
    INDEX idx_reorder_requests_priority (priority),
    INDEX idx_reorder_requests_supplier (supplier),
    INDEX idx_reorder_requests_created (created_at),
    INDEX idx_reorder_requests_approved (approved_at)
);

-- 15. Dashboard Cache Table
CREATE TABLE IF NOT EXISTS dashboard_cache (
    cache_key VARCHAR(255) PRIMARY KEY,
    cache_data JSON NOT NULL,
    cache_type ENUM('analytics', 'inventory', 'orders', 'users', 'products', 'reports') NOT NULL,
    user_id INT,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_dashboard_cache_type (cache_type),
    INDEX idx_dashboard_cache_user (user_id),
    INDEX idx_dashboard_cache_expires (expires_at),
    INDEX idx_dashboard_cache_created (created_at)
);

-- Insert default admin roles and permissions
INSERT IGNORE INTO admin_roles (role_name, role_description, permissions, is_system_role) VALUES
('super_admin', 'Full system access with all permissions', 
 JSON_ARRAY('dashboard_view', 'analytics_view', 'analytics_export', 'product_view', 'product_create', 'product_update', 'product_delete', 'inventory_view', 'inventory_manage', 'order_view', 'order_update', 'user_view', 'user_manage', 'support_view', 'support_manage', 'fraud_view', 'fraud_manage', 'refund_view', 'refund_manage', 'system_settings', 'audit_log_view', 'report_generate'), 
 TRUE),
('admin', 'Administrative access to most features', 
 JSON_ARRAY('dashboard_view', 'analytics_view', 'analytics_export', 'product_view', 'product_create', 'product_update', 'inventory_view', 'inventory_manage', 'order_view', 'order_update', 'user_view', 'support_view', 'support_manage', 'refund_view', 'refund_manage', 'audit_log_view', 'report_generate'), 
 FALSE),
('manager', 'Manager level access with limited permissions', 
 JSON_ARRAY('dashboard_view', 'analytics_view', 'product_view', 'product_update', 'inventory_view', 'order_view', 'user_view', 'support_view', 'refund_view', 'report_generate'), 
 FALSE),
('support_agent', 'Customer support agent access', 
 JSON_ARRAY('dashboard_view', 'support_view', 'support_manage', 'user_view', 'order_view'), 
 FALSE),
('inventory_manager', 'Inventory management specialist', 
 JSON_ARRAY('dashboard_view', 'analytics_view', 'product_view', 'product_update', 'inventory_view', 'inventory_manage', 'report_generate'), 
 FALSE),
('analyst', 'Data analyst with reporting access', 
 JSON_ARRAY('dashboard_view', 'analytics_view', 'analytics_export', 'report_generate'), 
 FALSE);

-- Insert default SLA settings
INSERT IGNORE INTO sla_settings (sla_type, ticket_category, priority, target_minutes, warning_minutes, business_hours_only) VALUES
('response_time', 'general', 'low', 480, 360, TRUE),    -- 8 hours, warn at 6 hours
('response_time', 'general', 'medium', 240, 180, TRUE),  -- 4 hours, warn at 3 hours
('response_time', 'general', 'high', 120, 60, TRUE),     -- 2 hours, warn at 1 hour
('response_time', 'general', 'urgent', 30, 15, TRUE),     -- 30 minutes, warn at 15 minutes
('response_time', 'fraud', 'medium', 60, 30, TRUE),      -- 1 hour, warn at 30 minutes
('response_time', 'fraud', 'high', 30, 15, TRUE),       -- 30 minutes, warn at 15 minutes
('response_time', 'fraud', 'urgent', 15, 5, TRUE),       -- 15 minutes, warn at 5 minutes
('resolution_time', 'general', 'low', 2880, 2160, TRUE),  -- 48 hours, warn at 36 hours
('resolution_time', 'general', 'medium', 1440, 1080, TRUE), -- 24 hours, warn at 18 hours
('resolution_time', 'general', 'high', 720, 480, TRUE),   -- 12 hours, warn at 8 hours
('resolution_time', 'general', 'urgent', 240, 120, TRUE),  -- 4 hours, warn at 2 hours
('escalation_time', 'general', 'medium', 1440, 1080, TRUE), -- 24 hours, warn at 18 hours
('escalation_time', 'general', 'high', 720, 480, TRUE),    -- 12 hours, warn at 8 hours
('escalation_time', 'general', 'urgent', 240, 120, TRUE),   -- 4 hours, warn at 2 hours
('escalation_time', 'fraud', 'medium', 60, 30, TRUE),       -- 1 hour, warn at 30 minutes
('escalation_time', 'fraud', 'high', 30, 15, TRUE),        -- 30 minutes, warn at 15 minutes
('escalation_time', 'fraud', 'urgent', 15, 5, TRUE);        -- 15 minutes, warn at 5 minutes

-- Create views for common queries
CREATE OR REPLACE VIEW admin_dashboard_summary AS
SELECT 
    u.user_id,
    u.username,
    COUNT(CASE WHEN an.status = 'unread' THEN 1 END) as unread_notifications,
    COUNT(CASE WHEN st.status IN ('open', 'pending', 'in_progress') THEN 1 END) as active_tickets,
    COUNT(CASE WHEN fdc.status IN ('pending', 'investigating') THEN 1 END) as pending_fraud_cases,
    COUNT(CASE WHEN rr.status = 'pending' THEN 1 END) as pending_refunds,
    COUNT(CASE WHEN im.movement_type = 'out' AND DATE(im.created_at) = CURDATE() THEN 1 END) as today_inventory_out,
    COUNT(CASE WHEN im.movement_type = 'in' AND DATE(im.created_at) = CURDATE() THEN 1 END) as today_inventory_in
FROM users u
LEFT JOIN admin_notifications an ON u.user_id = an.admin_user_id AND an.status = 'unread'
LEFT JOIN support_tickets st ON u.user_id = st.assigned_to AND st.status IN ('open', 'pending', 'in_progress')
LEFT JOIN fraud_detection_cases fdc ON u.user_id = fdc.assigned_to AND fdc.status IN ('pending', 'investigating')
LEFT JOIN refund_requests rr ON u.user_id = rr.admin_user_id AND rr.status = 'pending'
LEFT JOIN inventory_movements im ON u.user_id = im.performed_by AND DATE(im.created_at) = CURDATE()
WHERE u.user_id IN (SELECT user_id FROM admin_user_roles WHERE is_active = TRUE)
GROUP BY u.user_id, u.username;

CREATE OR REPLACE VIEW support_performance_metrics AS
SELECT 
    sa.assigned_to,
    COUNT(*) as total_tickets,
    COUNT(CASE WHEN st.status = 'resolved' THEN 1 END) as resolved_tickets,
    COUNT(CASE WHEN st.status = 'escalated' THEN 1 END) as escalated_tickets,
    AVG(st.resolution_time_minutes) as avg_resolution_time,
    AVG(st.first_response_time_minutes) as avg_first_response_time,
    AVG(st.customer_satisfaction) as avg_customer_satisfaction,
    COUNT(CASE WHEN st.status = 'resolved' AND st.resolution_time_minutes <= (
        SELECT target_minutes FROM sla_settings 
        WHERE sla_type = 'resolution_time' 
            AND st.category = ticket_category 
            AND st.priority = priority
    ) THEN 1 END) as sla_compliant_tickets,
    ROUND(COUNT(CASE WHEN st.status = 'resolved' THEN 1 END) * 100.0 / COUNT(*), 2) as resolution_rate
FROM support_tickets st
JOIN users sa ON st.assigned_to = sa.user_id
WHERE st.created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY sa.assigned_to
ORDER BY resolution_rate DESC;

CREATE OR REPLACE VIEW inventory_health_report AS
SELECT 
    p.product_id,
    p.product_name,
    p.stock as current_stock,
    p.reorder_level,
    p.max_stock,
    CASE 
        WHEN p.stock = 0 THEN 'out_of_stock'
        WHEN p.stock <= p.reorder_level THEN 'low_stock'
        WHEN p.stock >= p.max_stock * 0.9 THEN 'overstock'
        ELSE 'optimal'
    END as stock_status,
    COALESCE(SUM(CASE WHEN im.movement_type = 'out' AND DATE(im.created_at) >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN im.quantity END), 0) as monthly_outgoing,
    COALESCE(SUM(CASE WHEN im.movement_type = 'in' AND DATE(im.created_at) >= DATE_SUB(NOW(), INTERVAL 30 DAY) THEN im.quantity END), 0) as monthly_incoming,
    p.price * p.stock as inventory_value,
    p.cost * p.stock as inventory_cost,
    (p.price - p.cost) * p.stock as potential_profit
FROM products p
LEFT JOIN inventory_movements im ON p.product_id = im.product_id
WHERE p.is_active = TRUE
GROUP BY p.product_id, p.product_name, p.stock, p.reorder_level, p.max_stock, p.price, p.cost
ORDER BY inventory_value DESC;

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE GenerateDailyAnalyticsSnapshot(IN p_snapshot_date DATE)
BEGIN
    -- Revenue Analytics
    INSERT INTO analytics_snapshots (snapshot_type, snapshot_date, metric_category, metric_name, metric_value, comparison_period)
    SELECT 
        'daily' as snapshot_type,
        p_snapshot_date as snapshot_date,
        'revenue' as metric_category,
        'total_revenue' as metric_name,
        COALESCE(SUM(o.total_amount), 0) as metric_value,
        'previous_day' as comparison_period
    FROM orders o 
    WHERE DATE(o.created_at) = p_snapshot_date 
        AND o.status NOT IN ('cancelled', 'refunded')
    ON DUPLICATE KEY UPDATE
        metric_value = VALUES(metric_value),
        created_at = NOW();
    
    -- Order Analytics
    INSERT INTO analytics_snapshots (snapshot_type, snapshot_date, metric_category, metric_name, metric_value, comparison_period)
    SELECT 
        'daily' as snapshot_type,
        p_snapshot_date as snapshot_date,
        'orders' as metric_category,
        'total_orders' as metric_name,
        COUNT(*) as metric_value,
        'previous_day' as comparison_period
    FROM orders o 
    WHERE DATE(o.created_at) = p_snapshot_date
    ON DUPLICATE KEY UPDATE
        metric_value = VALUES(metric_value),
        created_at = NOW();
    
    -- User Analytics
    INSERT INTO analytics_snapshots (snapshot_type, snapshot_date, metric_category, metric_name, metric_value, comparison_period)
    SELECT 
        'daily' as snapshot_type,
        p_snapshot_date as snapshot_date,
        'users' as metric_category,
        'new_users' as metric_name,
        COUNT(*) as metric_value,
        'previous_day' as comparison_period
    FROM users u 
    WHERE DATE(u.created_at) = p_snapshot_date
    ON DUPLICATE KEY UPDATE
        metric_value = VALUES(metric_value),
        created_at = NOW();
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CheckInventoryAlerts()
BEGIN
    -- Low stock alerts
    INSERT INTO admin_notifications (admin_user_id, notification_type, title, message, severity, priority, source_entity_type, source_entity_id, action_required)
    SELECT 
        u.user_id as admin_user_id,
        'low_stock' as notification_type,
        CONCAT('Low Stock Alert: ', p.product_name) as title,
        CONCAT('Product "', p.product_name, '" has only ', p.stock, ' units remaining (Reorder level: ', p.reorder_level, ')') as message,
        'warning' as severity,
        'medium' as priority,
        'product' as source_entity_type,
        p.product_id as source_entity_id,
        TRUE as action_required
    FROM products p
    JOIN admin_user_roles aur ON p.product_id > 0
    JOIN users u ON aur.user_id = u.user_id
    WHERE p.stock > 0 
        AND p.stock <= p.reorder_level
        AND aur.is_active = TRUE
        AND JSON_CONTAINS(aur.permissions, '"inventory_manage"')
    ON DUPLICATE KEY UPDATE
        message = VALUES(message),
        created_at = NOW();
    
    -- Out of stock alerts
    INSERT INTO admin_notifications (admin_user_id, notification_type, title, message, severity, priority, source_entity_type, source_entity_id, action_required)
    SELECT 
        u.user_id as admin_user_id,
        'out_of_stock' as notification_type,
        CONCAT('Out of Stock: ', p.product_name) as title,
        CONCAT('Product "', p.product_name, '" is now out of stock') as message,
        'error' as severity,
        'high' as priority,
        'product' as source_entity_type,
        p.product_id as source_entity_id,
        TRUE as action_required
    FROM products p
    JOIN admin_user_roles aur ON p.product_id > 0
    JOIN users u ON aur.user_id = u.user_id
    WHERE p.stock = 0
        AND aur.is_active = TRUE
        AND JSON_CONTAINS(aur.permissions, '"inventory_manage"')
    ON DUPLICATE KEY UPDATE
        message = VALUES(message),
        created_at = NOW();
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE LogAdminAction(IN p_admin_user_id INT, IN p_action_type VARCHAR(50), IN p_entity_type VARCHAR(50), IN p_entity_id INT, IN p_old_values JSON, IN p_new_values JSON, IN p_action_description TEXT, IN p_ip_address VARCHAR(45), IN p_user_agent TEXT, IN p_session_id VARCHAR(255), IN p_request_id VARCHAR(255))
BEGIN
    INSERT INTO admin_audit_log (
        admin_user_id, action_type, entity_type, entity_id, 
        old_values, new_values, action_description, 
        ip_address, user_agent, session_id, request_id
    ) VALUES (
        p_admin_user_id, p_action_type, p_entity_type, p_entity_id,
        p_old_values, p_new_values, p_action_description,
        p_ip_address, p_user_agent, p_session_id, p_request_id
    );
END//
DELIMITER ;

-- Create triggers for automatic audit logging
DELIMITER //
CREATE TRIGGER audit_product_update
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    IF OLD.product_name != NEW.product_name 
        OR OLD.price != NEW.price 
        OR OLD.stock != NEW.stock 
        OR OLD.description != NEW.description
        OR OLD.is_active != NEW.is_active THEN
        CALL LogAdminAction(
            NULL, -- Will be set by application
            'update',
            'product',
            NEW.product_id,
            JSON_OBJECT(
                'product_name', OLD.product_name,
                'price', OLD.price,
                'stock', OLD.stock,
                'description', OLD.description,
                'is_active', OLD.is_active
            ),
            JSON_OBJECT(
                'product_name', NEW.product_name,
                'price', NEW.price,
                'stock', NEW.stock,
                'description', NEW.description,
                'is_active', NEW.is_active
            ),
            CONCAT('Updated product: ', NEW.product_name),
            NULL, NULL, NULL, NULL
        );
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER audit_order_update
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status 
        OR OLD.total_amount != NEW.total_amount 
        OR OLD.shipping_address != NEW.shipping_address THEN
        CALL LogAdminAction(
            NULL,
            'update',
            'order',
            NEW.order_id,
            JSON_OBJECT(
                'status', OLD.status,
                'total_amount', OLD.total_amount,
                'shipping_address', OLD.shipping_address
            ),
            JSON_OBJECT(
                'status', NEW.status,
                'total_amount', NEW.total_amount,
                'shipping_address', NEW.shipping_address
            ),
            CONCAT('Updated order: ', NEW.order_id),
            NULL, NULL, NULL, NULL
        );
    END IF;
END//
DELIMITER ;

-- Schedule background jobs (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS generate_daily_analytics
ON SCHEDULE EVERY 1 DAY
STARTS '00:05:00'
DO CALL GenerateDailyAnalyticsSnapshot(CURDATE());

CREATE EVENT IF NOT EXISTS check_inventory_alerts
ON SCHEDULE EVERY 1 HOUR
DO CALL CheckInventoryAlerts();

CREATE EVENT IF NOT EXISTS cleanup_old_notifications
ON SCHEDULE EVERY 1 DAY
STARTS '02:00:00'
DO DELETE FROM admin_notifications WHERE status = 'archived' AND created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

CREATE EVENT IF NOT EXISTS cleanup_old_audit_logs
ON SCHEDULE EVERY 1 DAY
STARTS '03:00:00'
DO DELETE FROM admin_audit_log WHERE created_at < DATE_SUB(NOW(), INTERVAL 365 DAY);

CREATE EVENT IF NOT EXISTS cleanup_dashboard_cache
ON SCHEDULE EVERY 30 MINUTE
DO DELETE FROM dashboard_cache WHERE expires_at < NOW();

-- Create indexes for better performance
CREATE INDEX idx_admin_notifications_composite ON admin_notifications(admin_user_id, status, priority, created_at);
CREATE INDEX idx_support_tickets_composite ON support_tickets(status, priority, assigned_to, created_at);
CREATE INDEX idx_fraud_detection_cases_composite ON fraud_detection_cases(status, risk_level, assigned_to, created_at);
CREATE INDEX idx_refund_requests_composite ON refund_requests(status, priority, created_at);
CREATE INDEX idx_admin_audit_log_composite ON admin_audit_log(admin_user_id, action_type, entity_type, created_at);
CREATE INDEX idx_inventory_movements_composite ON inventory_movements(product_id, movement_type, created_at);
CREATE INDEX idx_analytics_snapshots_composite ON analytics_snapshots(snapshot_type, snapshot_date, metric_category);

-- Add foreign key constraints for better data integrity
ALTER TABLE admin_notifications ADD CONSTRAINT chk_admin_notifications_severity CHECK (severity IN ('info', 'warning', 'error', 'critical'));
ALTER TABLE admin_notifications ADD CONSTRAINT chk_admin_notifications_status CHECK (status IN ('unread', 'read', 'dismissed', 'archived'));
ALTER TABLE admin_notifications ADD CONSTRAINT chk_admin_notifications_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));

ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_status CHECK (status IN ('open', 'pending', 'in_progress', 'resolved', 'closed', 'escalated'));
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_escalation CHECK (escalation_level IN ('level1', 'level2', 'level3'));
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_satisfaction CHECK (customer_satisfaction IS NULL OR (customer_satisfaction >= 1 AND customer_satisfaction <= 5));

ALTER TABLE user_moderation ADD CONSTRAINT chk_user_moderation_action CHECK (action IN ('suspend', 'ban', 'verify', 'warn', 'restrict', 'unban', 'unsuspend'));
ALTER TABLE user_moderation ADD CONSTRAINT chk_user_moderation_status CHECK (status IN ('active', 'expired', 'appealed', 'overturned'));

ALTER TABLE fraud_detection_cases ADD CONSTRAINT chk_fraud_cases_status CHECK (status IN ('pending', 'investigating', 'confirmed_fraud', 'false_positive', 'insufficient_evidence', 'escalated'));
ALTER TABLE fraud_detection_cases ADD CONSTRAINT chk_fraud_cases_risk_level CHECK (risk_level IN ('low', 'medium', 'high', 'critical'));
ALTER TABLE fraud_detection_cases ADD CONSTRAINT chk_fraud_cases_fraud_type CHECK (fraud_type IN ('payment_fraud', 'account_takeover', 'identity_theft', 'friendly_fraud', 'card_testing', 'return_fraud', 'other'));

ALTER TABLE refund_requests ADD CONSTRAINT chk_refund_requests_status CHECK (status IN ('pending', 'approved', 'rejected', 'processed', 'cancelled'));
ALTER TABLE refund_requests ADD CONSTRAINT chk_refund_requests_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));
ALTER TABLE refund_requests ADD CONSTRAINT chk_refund_requests_reason CHECK (reason IN ('damaged_item', 'wrong_item', 'not_as_described', 'late_delivery', 'customer_request', 'defective_product', 'size_issue', 'quality_issue', 'other'));

ALTER TABLE admin_audit_log ADD CONSTRAINT chk_admin_audit_action_type CHECK (action_type IN ('create', 'update', 'delete', 'view', 'login', 'logout', 'export', 'import', 'approve', 'reject', 'escalate', 'assign', 'unassign'));

ALTER TABLE inventory_movements ADD CONSTRAINT chk_inventory_movements_type CHECK (movement_type IN ('in', 'out', 'adjustment', 'transfer', 'return', 'damage', 'theft', 'expired'));
ALTER TABLE reorder_requests ADD CONSTRAINT chk_reorder_requests_status CHECK (status IN ('pending', 'approved', 'rejected', 'ordered', 'received', 'cancelled'));
ALTER TABLE reorder_requests ADD CONSTRAINT chk_reorder_requests_priority CHECK (priority IN ('low', 'medium', 'high', 'urgent'));

-- Add check constraints for data validation
ALTER TABLE admin_notifications ADD CONSTRAINT chk_admin_notifications_entity CHECK (source_entity_id IS NOT NULL OR source_entity_type = 'system');
ALTER TABLE support_tickets ADD CONSTRAINT chk_support_tickets_resolution CHECK (resolution IS NOT NULL OR status NOT IN ('resolved', 'closed'));
ALTER TABLE user_moderation ADD CONSTRAINT chk_user_moderation_duration CHECK (duration_days IS NULL OR duration_days > 0);
ALTER TABLE fraud_detection_cases ADD CONSTRAINT chk_fraud_cases_risk_score CHECK (risk_score >= 0 AND risk_score <= 1);
ALTER TABLE refund_requests ADD CONSTRAINT chk_refund_requests_amount CHECK (refund_amount > 0);
ALTER TABLE inventory_movements ADD CONSTRAINT chk_inventory_movements_quantity CHECK (quantity != 0);
ALTER TABLE reorder_requests ADD CONSTRAINT chk_reorder_requests_quantity CHECK (requested_quantity > 0);
ALTER TABLE dashboard_cache ADD CONSTRAINT chk_dashboard_cache_expires CHECK (expires_at > created_at);

-- Add full-text indexes for search optimization
CREATE FULLTEXT INDEX ft_support_tickets_search ON support_tickets(subject, description);
CREATE FULLTEXT INDEX ft_support_messages_search ON support_ticket_messages(message);
CREATE FULLTEXT INDEX ft_canned_responses_search ON canned_responses(title, content);

-- Add partitioning for large tables (optional for very high traffic)
-- ALTER TABLE admin_audit_log PARTITION BY RANGE (TO_DAYS(created_at)) (
--     PARTITION p_old VALUES LESS THAN (TO_DAYS('2024-01-01')),
--     PARTITION p_2024 VALUES LESS THAN (TO_DAYS('2025-01-01')),
--     PARTITION p_2025 VALUES LESS THAN (TO_DAYS('2026-01-01')),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- Create additional stored procedures for maintenance
DELIMITER //
CREATE PROCEDURE CleanupExpiredNotifications()
BEGIN
    UPDATE admin_notifications 
    SET status = 'archived' 
    WHERE expires_at < NOW() AND status != 'archived';
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE UpdateSLACompliance()
BEGIN
    -- Update tickets that missed SLA
    UPDATE support_tickets st
    JOIN sla_settings sla ON st.category = sla.ticket_category 
        AND st.priority = sla.priority 
        AND sla.sla_type = 'resolution_time'
    SET st.status = 'escalated',
        st.escalation_level = CASE 
            WHEN sla.target_minutes = 15 THEN 'level3'
            WHEN sla.target_minutes = 30 THEN 'level2'
            ELSE 'level1'
        END
    WHERE st.status IN ('open', 'pending', 'in_progress')
        AND st.created_at < DATE_SUB(NOW(), INTERVAL sla.target_minutes MINUTE)
        AND st.escalation_level = 'level1';
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GenerateFraudReport(IN p_admin_user_id INT, IN p_start_date DATE, IN p_end_date DATE)
BEGIN
    SELECT 
        fdc.case_id,
        fdc.case_number,
        fdc.risk_score,
        fdc.risk_level,
        fdc.fraud_type,
        fdc.status,
        fdc.created_at,
        u.username,
        u.email,
        o.order_id,
        o.total_amount,
        fdc.financial_impact,
        fdc.prevented_loss
    FROM fraud_detection_cases fdc
    LEFT JOIN users u ON fdc.user_id = u.user_id
    LEFT JOIN orders o ON fdc.order_id = o.order_id
    WHERE DATE(fdc.created_at) BETWEEN p_start_date AND p_end_date
    ORDER BY fdc.created_at DESC;
END//
DELIMITER ;
