-- FashionStore Checkout & Payment System Schema Updates
-- For enterprise-grade checkout flow with payment processing

-- 1. Payment Attempts Table
CREATE TABLE IF NOT EXISTS payment_attempts (
    attempt_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_id VARCHAR(100) NOT NULL,
    order_id INT NOT NULL,
    user_id INT,
    checkout_session_id VARCHAR(255),
    payment_method ENUM('upi', 'card', 'net_banking', 'wallet', 'emi', 'cod', 'other') NOT NULL,
    payment_gateway VARCHAR(50) NOT NULL,
    gateway_transaction_id VARCHAR(255),
    amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    status ENUM('initiated', 'processing', 'success', 'failed', 'cancelled', 'refunded', 'partially_refunded') NOT NULL DEFAULT 'initiated',
    failure_reason VARCHAR(500),
    failure_code VARCHAR(100),
    retry_count INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    next_retry_at TIMESTAMP NULL,
    idempotency_key VARCHAR(255) UNIQUE,
    request_data JSON,
    response_data JSON,
    gateway_request_time TIMESTAMP NULL,
    gateway_response_time TIMESTAMP NULL,
    processing_time_ms INT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_payment_attempts_payment_id (payment_id),
    INDEX idx_payment_attempts_order (order_id),
    INDEX idx_payment_attempts_user (user_id),
    INDEX idx_payment_attempts_status (status),
    INDEX idx_payment_attempts_created (created_at),
    INDEX idx_payment_attempts_idempotency (idempotency_key),
    INDEX idx_payment_attempts_next_retry (next_retry_at)
);

-- 2. Delivery Slots Table
CREATE TABLE IF NOT EXISTS delivery_slots (
    slot_id INT AUTO_INCREMENT PRIMARY KEY,
    pincode VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    slot_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    slot_type ENUM('standard', 'express', 'premium') DEFAULT 'standard',
    max_orders INT DEFAULT 50,
    current_orders INT DEFAULT 0,
    delivery_fee DECIMAL(8, 2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    cutoff_time TIME,
    delivery_partner VARCHAR(100),
    estimated_delivery_hours INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_delivery_slots_pincode (pincode),
    INDEX idx_delivery_slots_date (slot_date),
    INDEX idx_delivery_slots_active (is_active),
    INDEX idx_delivery_slots_cutoff (cutoff_time),
    UNIQUE KEY uk_delivery_slot_unique (pincode, slot_date, start_time, end_time)
);

-- 3. Order Tracking Table
CREATE TABLE IF NOT EXISTS order_tracking (
    tracking_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    tracking_number VARCHAR(100) NOT NULL UNIQUE,
    carrier_id INT,
    carrier_name VARCHAR(100),
    tracking_url VARCHAR(500),
    current_status ENUM('order_placed', 'order_confirmed', 'processing', 'shipped', 'out_for_delivery', 'delivered', 'cancelled', 'returned') NOT NULL DEFAULT 'order_placed',
    estimated_delivery_date TIMESTAMP,
    actual_delivery_date TIMESTAMP,
    pickup_date TIMESTAMP,
    weight DECIMAL(8, 2),
    dimensions VARCHAR(100), -- LxWxH format
    delivery_address TEXT,
    delivery_instructions TEXT,
    delivery_contact_name VARCHAR(255),
    delivery_contact_phone VARCHAR(20),
    delivery_signature_required BOOLEAN DEFAULT FALSE,
    delivery_photo_url VARCHAR(500),
    tracking_events JSON,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    INDEX idx_order_tracking_order (order_id),
    INDEX idx_order_tracking_number (tracking_number),
    INDEX idx_order_tracking_status (current_status),
    INDEX idx_order_tracking_delivery (estimated_delivery_date),
    INDEX idx_order_tracking_carrier (carrier_id)
);

-- 4. Invoices Table
CREATE TABLE IF NOT EXISTS invoices (
    invoice_id INT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(100) NOT NULL UNIQUE,
    order_id INT NOT NULL,
    user_id INT,
    invoice_type ENUM('tax_invoice', 'proforma_invoice', 'credit_note', 'debit_note') DEFAULT 'tax_invoice',
    invoice_date DATE NOT NULL,
    due_date DATE,
    status ENUM('draft', 'sent', 'paid', 'overdue', 'cancelled', 'refunded') DEFAULT 'draft',
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0.00,
    discount_amount DECIMAL(10, 2) DEFAULT 0.00,
    shipping_amount DECIMAL(10, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    tax_breakdown JSON,
    payment_terms VARCHAR(255),
    notes TEXT,
    generated_by INT,
    sent_at TIMESTAMP NULL,
    paid_at TIMESTAMP NULL,
    file_path VARCHAR(500),
    file_hash VARCHAR(255),
    download_count INT DEFAULT 0,
    last_downloaded_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (generated_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_invoices_order (order_id),
    INDEX idx_invoices_user (user_id),
    INDEX idx_invoices_number (invoice_number),
    INDEX idx_invoices_status (status),
    INDEX idx_invoices_date (invoice_date),
    INDEX idx_invoices_due (due_date)
);

-- 5. Payment Recovery Table
CREATE TABLE IF NOT EXISTS payment_recovery (
    recovery_id INT AUTO_INCREMENT PRIMARY KEY,
    payment_attempt_id INT NOT NULL,
    order_id INT NOT NULL,
    user_id INT,
    recovery_type ENUM('retry', 'alternative_method', 'manual_intervention', 'escalation') NOT NULL,
    recovery_status ENUM('initiated', 'in_progress', 'resolved', 'failed', 'cancelled') DEFAULT 'initiated',
    original_payment_method VARCHAR(50),
    new_payment_method VARCHAR(50),
    recovery_attempts INT DEFAULT 0,
    max_recovery_attempts INT DEFAULT 3,
    next_recovery_at TIMESTAMP NULL,
    recovery_reason TEXT,
    resolution_details TEXT,
    resolved_by INT,
    resolved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_attempt_id) REFERENCES payment_attempts(attempt_id) ON DELETE CASCADE,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (resolved_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_payment_recovery_attempt (payment_attempt_id),
    INDEX idx_payment_recovery_order (order_id),
    INDEX idx_payment_recovery_user (user_id),
    INDEX idx_payment_recovery_status (recovery_status),
    INDEX idx_payment_recovery_next (next_recovery_at)
);

-- 6. Checkout Sessions Table
CREATE TABLE IF NOT EXISTS checkout_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT,
    cart_data JSON NOT NULL,
    shipping_address_id INT,
    billing_address_id INT,
    delivery_slot_id INT,
    payment_method VARCHAR(50),
    coupon_code VARCHAR(100),
    applied_discount DECIMAL(10, 2) DEFAULT 0.00,
    shipping_cost DECIMAL(8, 2) DEFAULT 0.00,
    tax_amount DECIMAL(8, 2) DEFAULT 0.00,
    total_amount DECIMAL(10, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'INR',
    checkout_status ENUM('initialized', 'address_selected', 'delivery_selected', 'payment_selected', 'processing', 'completed', 'abandoned', 'expired') DEFAULT 'initialized',
    current_step INT DEFAULT 1,
    total_steps INT DEFAULT 4,
    completed_steps JSON,
    abandoned_at TIMESTAMP NULL,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (shipping_address_id) REFERENCES user_addresses(address_id) ON DELETE SET NULL,
    FOREIGN KEY (billing_address_id) REFERENCES user_addresses(address_id) ON DELETE SET NULL,
    FOREIGN KEY (delivery_slot_id) REFERENCES delivery_slots(slot_id) ON DELETE SET NULL,
    INDEX idx_checkout_sessions_user (user_id),
    INDEX idx_checkout_sessions_status (checkout_status),
    INDEX idx_checkout_sessions_expires (expires_at),
    INDEX idx_checkout_sessions_active (is_active)
);

-- 7. Payment Methods Table
CREATE TABLE IF NOT EXISTS payment_methods (
    method_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    method_type ENUM('upi', 'card', 'net_banking', 'wallet') NOT NULL,
    method_name VARCHAR(100) NOT NULL,
    provider VARCHAR(100),
    account_identifier VARCHAR(255) NOT NULL, -- UPI ID, last 4 digits of card, etc.
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    expiry_date DATE,
    card_type VARCHAR(50), -- credit, debit, etc.
    card_brand VARCHAR(50), -- visa, mastercard, etc.
    wallet_provider VARCHAR(100),
    bank_name VARCHAR(100),
    billing_address JSON,
    metadata JSON,
    last_used_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_payment_methods_user (user_id),
    INDEX idx_payment_methods_type (method_type),
    INDEX idx_payment_methods_active (is_active),
    INDEX idx_payment_methods_default (is_default)
);

-- 8. Order Notifications Table
CREATE TABLE IF NOT EXISTS order_notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    user_id INT,
    notification_type ENUM('order_placed', 'order_confirmed', 'processing', 'shipped', 'out_for_delivery', 'delivered', 'cancelled', 'payment_failed', 'payment_success', 'refund_initiated', 'refund_completed') NOT NULL,
    channel ENUM('email', 'sms', 'push', 'whatsapp') NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    template_name VARCHAR(100),
    template_data JSON,
    status ENUM('pending', 'sent', 'delivered', 'failed', 'bounced') DEFAULT 'pending',
    sent_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    failure_reason VARCHAR(500),
    retry_count INT DEFAULT 0,
    external_id VARCHAR(255), -- Email service ID, SMS gateway ID, etc.
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_order_notifications_order (order_id),
    INDEX idx_order_notifications_user (user_id),
    INDEX idx_order_notifications_type (notification_type),
    INDEX idx_order_notifications_status (status),
    INDEX idx_order_notifications_channel (channel)
);

-- 9. Inventory Locks Table
CREATE TABLE IF NOT EXISTS inventory_locks (
    lock_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    variant_id INT,
    order_id INT,
    checkout_session_id VARCHAR(255),
    quantity_locked INT NOT NULL,
    lock_type ENUM('checkout', 'order', 'return') DEFAULT 'checkout',
    lock_status ENUM('active', 'released', 'expired', 'converted') DEFAULT 'active',
    locked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    released_at TIMESTAMP NULL,
    released_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE SET NULL,
    FOREIGN KEY (released_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_inventory_locks_product (product_id),
    INDEX idx_inventory_locks_variant (variant_id),
    INDEX idx_inventory_locks_order (order_id),
    INDEX idx_inventory_locks_session (checkout_session_id),
    INDEX idx_inventory_locks_status (lock_status),
    INDEX idx_inventory_locks_expires (expires_at)
);

-- 10. Payment Analytics Table
CREATE TABLE IF NOT EXISTS payment_analytics (
    analytics_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_gateway VARCHAR(50) NOT NULL,
    total_attempts INT DEFAULT 0,
    successful_payments INT DEFAULT 0,
    failed_payments INT DEFAULT 0,
    total_amount DECIMAL(15, 2) DEFAULT 0.00,
    success_rate DECIMAL(5, 2) DEFAULT 0.00,
    avg_processing_time_ms INT DEFAULT 0,
    failure_breakdown JSON, -- Count by failure reason
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_payment_analytics_date (date, payment_method, payment_gateway),
    INDEX idx_payment_analytics_date (date),
    INDEX idx_payment_analytics_method (payment_method),
    INDEX idx_payment_analytics_gateway (payment_gateway)
);

-- Insert initial delivery slots for major cities
INSERT IGNORE INTO delivery_slots (pincode, city, state, slot_date, start_time, end_time, slot_type, max_orders, delivery_fee, cutoff_time, estimated_delivery_hours) VALUES
-- Delhi
('110001', 'Delhi', 'Delhi', CURDATE() + INTERVAL 1 DAY, '09:00:00', '12:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('110001', 'Delhi', 'Delhi', CURDATE() + INTERVAL 1 DAY, '14:00:00', '17:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('110001', 'Delhi', 'Delhi', CURDATE() + INTERVAL 1 DAY, '18:00:00', '21:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('110001', 'Delhi', 'Delhi', CURDATE(), '19:00:00', '22:00:00', 'express', 30, 50.00, '18:30:00', 3),

-- Mumbai
('400001', 'Mumbai', 'Maharashtra', CURDATE() + INTERVAL 1 DAY, '09:00:00', '12:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('400001', 'Mumbai', 'Maharashtra', CURDATE() + INTERVAL 1 DAY, '14:00:00', '17:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('400001', 'Mumbai', 'Maharashtra', CURDATE() + INTERVAL 1 DAY, '18:00:00', '21:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('400001', 'Mumbai', 'Maharashtra', CURDATE(), '19:00:00', '22:00:00', 'express', 30, 50.00, '18:30:00', 3),

-- Bangalore
('560001', 'Bangalore', 'Karnataka', CURDATE() + INTERVAL 1 DAY, '09:00:00', '12:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('560001', 'Bangalore', 'Karnataka', CURDATE() + INTERVAL 1 DAY, '14:00:00', '17:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('560001', 'Bangalore', 'Karnataka', CURDATE() + INTERVAL 1 DAY, '18:00:00', '21:00:00', 'standard', 50, 0.00, '22:00:00', 24),
('560001', 'Bangalore', 'Karnataka', CURDATE(), '19:00:00', '22:00:00', 'express', 30, 50.00, '18:30:00', 3);

-- Create views for common queries
CREATE OR REPLACE VIEW payment_summary_view AS
SELECT 
    DATE(pa.created_at) as payment_date,
    pa.payment_method,
    pa.payment_gateway,
    COUNT(*) as total_attempts,
    COUNT(CASE WHEN pa.status = 'success' THEN 1 END) as successful_payments,
    COUNT(CASE WHEN pa.status = 'failed' THEN 1 END) as failed_payments,
    SUM(pa.amount) as total_amount,
    ROUND(COUNT(CASE WHEN pa.status = 'success' THEN 1 END) * 100.0 / COUNT(*), 2) as success_rate,
    AVG(pa.processing_time_ms) as avg_processing_time
FROM payment_attempts pa
GROUP BY DATE(pa.created_at), pa.payment_method, pa.payment_gateway
ORDER BY payment_date DESC;

CREATE OR REPLACE VIEW delivery_performance_view AS
SELECT 
    ds.pincode,
    ds.city,
    ds.state,
    ds.slot_type,
    COUNT(*) as total_slots,
    SUM(ds.current_orders) as total_orders,
    AVG(ds.current_orders) as avg_orders_per_slot,
    MAX(ds.current_orders) as max_orders_per_slot,
    SUM(ds.delivery_fee) as total_revenue,
    AVG(ds.estimated_delivery_hours) as avg_delivery_hours
FROM delivery_slots ds
WHERE ds.is_active = TRUE
GROUP BY ds.pincode, ds.city, ds.state, ds.slot_type
ORDER BY total_orders DESC;

CREATE OR REPLACE VIEW checkout_conversion_view AS
SELECT 
    DATE(cs.created_at) as checkout_date,
    COUNT(*) as total_sessions,
    COUNT(CASE WHEN cs.checkout_status = 'completed' THEN 1 END) as completed_checkouts,
    COUNT(CASE WHEN cs.checkout_status = 'abandoned' THEN 1 END) as abandoned_checkouts,
    COUNT(CASE WHEN cs.checkout_status = 'expired' THEN 1 END) as expired_checkouts,
    ROUND(COUNT(CASE WHEN cs.checkout_status = 'completed' THEN 1 END) * 100.0 / COUNT(*), 2) as conversion_rate,
    AVG(cs.total_amount) as avg_order_value
FROM checkout_sessions cs
GROUP BY DATE(cs.created_at)
ORDER BY checkout_date DESC;

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE ProcessPaymentRetry(IN p_payment_id VARCHAR(100), IN p_user_id INT, IN p_new_payment_method VARCHAR(50))
BEGIN
    DECLARE v_attempt_id INT;
    DECLARE v_retry_count INT;
    DECLARE v_max_retries INT;
    
    -- Get the latest failed payment attempt
    SELECT attempt_id, retry_count, max_retries 
    INTO v_attempt_id, v_retry_count, v_max_retries
    FROM payment_attempts 
    WHERE payment_id = p_payment_id AND status = 'failed'
    ORDER BY created_at DESC 
    LIMIT 1;
    
    -- Check if retry is allowed
    IF v_retry_count < v_max_retries THEN
        -- Create new payment attempt
        INSERT INTO payment_attempts (
            payment_id, order_id, user_id, payment_method, 
            retry_count, max_retries, status
        )
        SELECT 
            p_payment_id, order_id, p_user_id, p_new_payment_method,
            v_retry_count + 1, v_max_retries, 'initiated'
        FROM payment_attempts 
        WHERE attempt_id = v_attempt_id;
        
        SELECT LAST_INSERT_ID() as new_attempt_id;
    ELSE
        SELECT -1 as error_code, 'Maximum retry attempts exceeded' as error_message;
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE LockInventory(IN p_checkout_session_id VARCHAR(255), IN p_product_id INT, IN p_quantity INT)
BEGIN
    DECLARE v_current_stock INT;
    DECLARE v_locked_quantity INT;
    
    -- Check current stock and locked quantity
    SELECT 
        p.stock, 
        COALESCE(SUM(il.quantity_locked), 0)
    INTO v_current_stock, v_locked_quantity
    FROM products p
    LEFT JOIN inventory_locks il ON p.product_id = il.product_id 
        AND il.lock_status = 'active' AND il.expires_at > NOW()
    WHERE p.product_id = p_product_id;
    
    -- Check if inventory can be locked
    IF (v_current_stock - v_locked_quantity) >= p_quantity THEN
        -- Lock the inventory
        INSERT INTO inventory_locks (
            product_id, checkout_session_id, quantity_locked, 
            lock_type, lock_status, expires_at
        ) VALUES (
            p_product_id, p_checkout_session_id, p_quantity,
            'checkout', 'active', NOW() + INTERVAL 30 MINUTE
        );
        
        SELECT TRUE as success, 'Inventory locked successfully' as message;
    ELSE
        SELECT FALSE as success, 'Insufficient inventory' as message;
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE ReleaseInventoryLocks(IN p_checkout_session_id VARCHAR(255))
BEGIN
    UPDATE inventory_locks 
    SET lock_status = 'released', released_at = NOW()
    WHERE checkout_session_id = p_checkout_session_id 
        AND lock_status = 'active';
        
    SELECT ROW_COUNT() as locks_released;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GenerateInvoice(IN p_order_id INT, IN p_user_id INT)
BEGIN
    DECLARE v_invoice_number VARCHAR(100);
    DECLARE v_subtotal DECIMAL(10, 2);
    DECLARE v_tax_amount DECIMAL(10, 2);
    DECLARE v_shipping_amount DECIMAL(8, 2);
    DECLARE v_total_amount DECIMAL(10, 2);
    
    -- Generate invoice number
    SET v_invoice_number = CONCAT('INV-', DATE_FORMAT(NOW(), '%Y%m%d'), '-', LPAD(p_order_id, 6, '0'));
    
    -- Calculate order totals
    SELECT 
        o.total_amount - COALESCE(o.tax_amount, 0) - COALESCE(o.shipping_cost, 0),
        COALESCE(o.tax_amount, 0),
        COALESCE(o.shipping_cost, 0),
        o.total_amount
    INTO v_subtotal, v_tax_amount, v_shipping_amount, v_total_amount
    FROM orders o WHERE o.order_id = p_order_id;
    
    -- Create invoice
    INSERT INTO invoices (
        invoice_number, order_id, user_id, invoice_date, 
        subtotal, tax_amount, shipping_amount, total_amount, status
    ) VALUES (
        v_invoice_number, p_order_id, p_user_id, CURDATE(),
        v_subtotal, v_tax_amount, v_shipping_amount, v_total_amount, 'sent'
    );
    
    SELECT LAST_INSERT_ID() as invoice_id, v_invoice_number as invoice_number;
END//
DELIMITER ;

-- Create triggers for automatic updates
DELIMITER //
CREATE TRIGGER update_payment_analytics_on_payment
AFTER INSERT ON payment_attempts
FOR EACH ROW
BEGIN
    INSERT INTO payment_analytics (
        date, payment_method, payment_gateway, 
        total_attempts, successful_payments, failed_payments,
        total_amount, success_rate
    )
    VALUES (
        DATE(NEW.created_at), NEW.payment_method, NEW.payment_gateway,
        1, CASE WHEN NEW.status = 'success' THEN 1 ELSE 0 END,
        CASE WHEN NEW.status = 'failed' THEN 1 ELSE 0 END,
        NEW.amount, CASE WHEN NEW.status = 'success' THEN 100 ELSE 0 END
    )
    ON DUPLICATE KEY UPDATE
        total_attempts = total_attempts + 1,
        successful_payments = successful_payments + VALUES(successful_payments),
        failed_payments = failed_payments + VALUES(failed_payments),
        total_amount = total_amount + NEW.amount,
        success_rate = (successful_payments * 100.0) / total_attempts;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER expire_checkout_sessions
AFTER INSERT ON checkout_sessions
FOR EACH ROW
BEGIN
    -- Schedule cleanup for expired sessions (this would be handled by a scheduled job)
END//
DELIMITER ;

-- Schedule cleanup procedures (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS cleanup_expired_checkout_sessions
ON SCHEDULE EVERY 1 HOUR
DO CALL ReleaseInventoryLocks(NULL);

CREATE EVENT IF NOT EXISTS cleanup_expired_inventory_locks
ON SCHEDULE EVERY 30 MINUTE
DO UPDATE inventory_locks 
    SET lock_status = 'expired', released_at = NOW()
    WHERE lock_status = 'active' AND expires_at < NOW();

CREATE EVENT IF NOT EXISTS update_payment_analytics
ON SCHEDULE EVERY 1 DAY
DO CALL UpdateDailyPaymentAnalytics();

-- Create indexes for better performance
CREATE INDEX idx_payment_attempts_composite ON payment_attempts(order_id, status, created_at);
CREATE INDEX idx_order_tracking_composite ON order_tracking(order_id, current_status, last_updated);
CREATE INDEX idx_checkout_sessions_composite ON checkout_sessions(user_id, checkout_status, expires_at);
CREATE INDEX idx_inventory_locks_composite ON inventory_locks(product_id, lock_status, expires_at);

-- Add foreign key constraints for better data integrity
ALTER TABLE payment_attempts ADD CONSTRAINT chk_payment_attempts_status 
CHECK (status IN ('initiated', 'processing', 'success', 'failed', 'cancelled', 'refunded', 'partially_refunded'));

ALTER TABLE payment_attempts ADD CONSTRAINT chk_payment_attempts_retry 
CHECK (retry_count >= 0 AND retry_count <= max_retries);

ALTER TABLE delivery_slots ADD CONSTRAINT chk_delivery_slots_orders 
CHECK (current_orders >= 0 AND current_orders <= max_orders);

ALTER TABLE delivery_slots ADD CONSTRAINT chk_delivery_slots_fee 
CHECK (delivery_fee >= 0);

ALTER TABLE order_tracking ADD CONSTRAINT chk_order_tracking_dates 
CHECK (estimated_delivery_date IS NULL OR estimated_delivery_date >= created_at);

ALTER TABLE invoices ADD CONSTRAINT chk_invoices_amounts 
CHECK (subtotal >= 0 AND tax_amount >= 0 AND discount_amount >= 0 AND shipping_amount >= 0 AND total_amount >= 0);

ALTER TABLE invoices ADD CONSTRAINT chk_invoices_dates 
CHECK (invoice_date <= due_date OR due_date IS NULL);

ALTER TABLE payment_recovery ADD CONSTRAINT chk_payment_recovery_attempts 
CHECK (recovery_attempts >= 0 AND recovery_attempts <= max_recovery_attempts);

ALTER TABLE checkout_sessions ADD CONSTRAINT chk_checkout_sessions_steps 
CHECK (current_step >= 1 AND current_step <= total_steps);

ALTER TABLE checkout_sessions ADD CONSTRAINT chk_checkout_sessions_amounts 
CHECK (applied_discount >= 0 AND shipping_cost >= 0 AND tax_amount >= 0 AND total_amount >= 0);

ALTER TABLE payment_methods ADD CONSTRAINT chk_payment_methods_dates 
CHECK (expiry_date IS NULL OR expiry_date >= created_at);

ALTER TABLE order_notifications ADD CONSTRAINT chk_order_notifications_retry 
CHECK (retry_count >= 0);

ALTER TABLE inventory_locks ADD CONSTRAINT chk_inventory_locks_quantity 
CHECK (quantity_locked > 0);

ALTER TABLE inventory_locks ADD CONSTRAINT chk_inventory_locks_dates 
CHECK (locked_at <= expires_at);

ALTER TABLE payment_analytics ADD CONSTRAINT chk_payment_analytics_rates 
CHECK (success_rate >= 0 AND success_rate <= 100);

ALTER TABLE payment_analytics ADD CONSTRAINT chk_payment_analytics_counts 
CHECK (total_attempts >= 0 AND successful_payments >= 0 AND failed_payments >= 0);

-- Add check constraints for data validation
ALTER TABLE payment_attempts ADD CONSTRAINT chk_payment_attempts_amount 
CHECK (amount > 0);

ALTER TABLE delivery_slots ADD CONSTRAINT chk_delivery_slots_time 
CHECK (start_time < end_time);

ALTER TABLE order_tracking ADD CONSTRAINT chk_order_tracking_status 
CHECK (current_status IN ('order_placed', 'order_confirmed', 'processing', 'shipped', 'out_for_delivery', 'delivered', 'cancelled', 'returned'));

ALTER TABLE invoices ADD CONSTRAINT chk_invoices_status 
CHECK (status IN ('draft', 'sent', 'paid', 'overdue', 'cancelled', 'refunded'));

ALTER TABLE payment_recovery ADD CONSTRAINT chk_payment_recovery_status 
CHECK (recovery_status IN ('initiated', 'in_progress', 'resolved', 'failed', 'cancelled'));

ALTER TABLE checkout_sessions ADD CONSTRAINT chk_checkout_sessions_status 
CHECK (checkout_status IN ('initialized', 'address_selected', 'delivery_selected', 'payment_selected', 'processing', 'completed', 'abandoned', 'expired'));

ALTER TABLE payment_methods ADD CONSTRAINT chk_payment_methods_type 
CHECK (method_type IN ('upi', 'card', 'net_banking', 'wallet'));

ALTER TABLE order_notifications ADD CONSTRAINT chk_order_notifications_type 
CHECK (notification_type IN ('order_placed', 'order_confirmed', 'processing', 'shipped', 'out_for_delivery', 'delivered', 'cancelled', 'payment_failed', 'payment_success', 'refund_initiated', 'refund_completed'));

ALTER TABLE order_notifications ADD CONSTRAINT chk_order_notifications_channel 
CHECK (channel IN ('email', 'sms', 'push', 'whatsapp'));

ALTER TABLE inventory_locks ADD CONSTRAINT chk_inventory_locks_type 
CHECK (lock_type IN ('checkout', 'order', 'return'));

ALTER TABLE inventory_locks ADD CONSTRAINT chk_inventory_locks_status 
CHECK (lock_status IN ('active', 'released', 'expired', 'converted'));
