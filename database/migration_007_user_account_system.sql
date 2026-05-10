-- ============================================================
-- MIGRATION 007: USER ACCOUNT SYSTEM ENHANCEMENTS
-- Adds address management, user settings, and profile features
-- Created: May 10, 2026
-- ============================================================

USE fashionstore;

-- ============================================================
-- 1. ADDRESSES TABLE (Multiple addresses per user)
-- ============================================================
CREATE TABLE IF NOT EXISTS addresses (
    address_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    address_type ENUM('billing', 'shipping', 'both') DEFAULT 'both',
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) DEFAULT 'India',
    is_default BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_is_default (user_id, is_default),
    INDEX idx_address_type (user_id, address_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User shipping and billing addresses';

-- ============================================================
-- 2. USER SETTINGS TABLE (Account preferences)
-- ============================================================
CREATE TABLE IF NOT EXISTS user_settings (
    setting_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    email_notifications BOOLEAN DEFAULT TRUE,
    sms_notifications BOOLEAN DEFAULT FALSE,
    order_updates BOOLEAN DEFAULT TRUE,
    promotional_emails BOOLEAN DEFAULT FALSE,
    newsletter_subscription BOOLEAN DEFAULT FALSE,
    language VARCHAR(10) DEFAULT 'en',
    currency VARCHAR(3) DEFAULT 'INR',
    theme_preference ENUM('light', 'dark', 'auto') DEFAULT 'auto',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User account preferences and settings';

-- ============================================================
-- 3. USER PROFILES TABLE (Extended profile information)
-- ============================================================
CREATE TABLE IF NOT EXISTS user_profiles (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    date_of_birth DATE,
    profile_image_url VARCHAR(255),
    bio TEXT,
    preferred_shipping_address_id INT,
    preferred_billing_address_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (preferred_shipping_address_id) REFERENCES addresses(address_id) ON DELETE SET NULL,
    FOREIGN KEY (preferred_billing_address_id) REFERENCES addresses(address_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Extended user profile information';

-- ============================================================
-- 4. UPDATE ORDERS TABLE TO USE ADDRESS REFERENCES
-- ============================================================
-- Add address reference columns to orders table (if they don't exist)
SET @col_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'fashionstore'
    AND TABLE_NAME = 'orders'
    AND COLUMN_NAME = 'shipping_address_id'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE orders ADD COLUMN shipping_address_id INT NULL AFTER user_id',
    'SELECT "Column shipping_address_id already exists" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @col_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.COLUMNS
    WHERE TABLE_SCHEMA = 'fashionstore'
    AND TABLE_NAME = 'orders'
    AND COLUMN_NAME = 'billing_address_id'
);

SET @sql = IF(@col_exists = 0,
    'ALTER TABLE orders ADD COLUMN billing_address_id INT NULL AFTER shipping_address_id',
    'SELECT "Column billing_address_id already exists" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add foreign keys and indexes (if they don't exist)
SET @fk_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'fashionstore'
    AND TABLE_NAME = 'orders'
    AND COLUMN_NAME = 'shipping_address_id'
    AND REFERENCED_TABLE_NAME = 'addresses'
);

SET @sql = IF(@fk_exists = 0,
    'ALTER TABLE orders ADD FOREIGN KEY (shipping_address_id) REFERENCES addresses(address_id) ON DELETE SET NULL',
    'SELECT "Foreign key for shipping_address_id already exists" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fk_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE
    WHERE TABLE_SCHEMA = 'fashionstore'
    AND TABLE_NAME = 'orders'
    AND COLUMN_NAME = 'billing_address_id'
    AND REFERENCED_TABLE_NAME = 'addresses'
);

SET @sql = IF(@fk_exists = 0,
    'ALTER TABLE orders ADD FOREIGN KEY (billing_address_id) REFERENCES addresses(address_id) ON DELETE SET NULL',
    'SELECT "Foreign key for billing_address_id already exists" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Add indexes (if they don't exist)
SET @index_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'fashionstore'
    AND TABLE_NAME = 'orders'
    AND INDEX_NAME = 'idx_shipping_address'
);

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_shipping_address ON orders (shipping_address_id)',
    'SELECT "Index idx_shipping_address already exists" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @index_exists = (
    SELECT COUNT(*)
    FROM INFORMATION_SCHEMA.STATISTICS
    WHERE TABLE_SCHEMA = 'fashionstore'
    AND TABLE_NAME = 'orders'
    AND INDEX_NAME = 'idx_billing_address'
);

SET @sql = IF(@index_exists = 0,
    'CREATE INDEX idx_billing_address ON orders (billing_address_id)',
    'SELECT "Index idx_billing_address already exists" as message'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- ============================================================
-- 5. MIGRATE EXISTING ADDRESS DATA (if any)
-- ============================================================
-- Migrate existing user.address field to addresses table
INSERT INTO addresses (user_id, address_type, full_name, phone, address_line1, city, state, postal_code, country, is_default)
SELECT 
    user_id, 
    'both' as address_type,
    full_name,
    COALESCE(phone, 'N/A') as phone,
    COALESCE(address, 'N/A') as address_line1,
    'Unknown' as city,
    'Unknown' as state,
    '000000' as postal_code,
    'India' as country,
    TRUE as is_default
FROM users 
WHERE address IS NOT NULL AND address != ''
ON DUPLICATE KEY UPDATE address_id = address_id;

-- ============================================================
-- 6. CREATE DEFAULT USER SETTINGS FOR EXISTING USERS
-- ============================================================
INSERT INTO user_settings (user_id, email_notifications, order_updates, language, currency, theme_preference)
SELECT user_id, TRUE, TRUE, 'en', 'INR', 'auto'
FROM users
ON DUPLICATE KEY UPDATE user_id = user_id;

-- ============================================================
-- 7. CREATE DEFAULT USER PROFILES FOR EXISTING USERS
-- ============================================================
INSERT INTO user_profiles (user_id)
SELECT user_id
FROM users
ON DUPLICATE KEY UPDATE user_id = user_id;

-- ============================================================
-- 8. INDEXES FOR PERFORMANCE
-- ============================================================
CREATE INDEX idx_addresses_user_type ON addresses(user_id, address_type, is_active);
CREATE INDEX idx_user_settings_user ON user_settings(user_id);
CREATE INDEX idx_user_profiles_user ON user_profiles(user_id);

-- ============================================================
-- 9. TRIGGERS FOR DATA INTEGRITY
-- ============================================================

-- Trigger to ensure only one default address per user per type
DELIMITER //
CREATE TRIGGER ensure_single_default_address
BEFORE UPDATE ON addresses
FOR EACH ROW
BEGIN
    IF NEW.is_default = TRUE AND (OLD.is_default = FALSE OR OLD.is_default IS NULL) THEN
        UPDATE addresses SET is_default = FALSE 
        WHERE user_id = NEW.user_id 
        AND address_type = NEW.address_type 
        AND address_id != NEW.address_id;
    END IF;
END//
DELIMITER ;

-- Trigger to set default address on first insert
DELIMITER //
CREATE TRIGGER set_default_on_first_address
BEFORE INSERT ON addresses
FOR EACH ROW
BEGIN
    DECLARE address_count INT;
    SELECT COUNT(*) INTO address_count FROM addresses WHERE user_id = NEW.user_id AND address_type = NEW.address_type;
    IF address_count = 0 THEN
        SET NEW.is_default = TRUE;
    END IF;
END//
DELIMITER ;

-- ============================================================
-- 10. VIEWS FOR CONVENIENT QUERIES
-- ============================================================

-- View for user addresses with user info
CREATE OR REPLACE VIEW v_user_addresses AS
SELECT 
    a.address_id,
    a.user_id,
    u.email,
    a.address_type,
    a.full_name,
    a.phone,
    a.address_line1,
    a.address_line2,
    a.city,
    a.state,
    a.postal_code,
    a.country,
    a.is_default,
    a.is_active,
    a.created_at,
    a.updated_at
FROM addresses a
JOIN users u ON a.user_id = u.user_id
WHERE a.is_active = TRUE;

-- View for complete user profile
CREATE OR REPLACE VIEW v_user_complete_profile AS
SELECT 
    u.user_id,
    u.full_name,
    u.email,
    u.phone,
    u.gender,
    u.role,
    u.is_active,
    u.created_at as user_created_at,
    us.email_notifications,
    us.sms_notifications,
    us.order_updates,
    us.promotional_emails,
    us.newsletter_subscription,
    us.language,
    us.currency,
    us.theme_preference,
    up.date_of_birth,
    up.profile_image_url,
    up.bio,
    up.preferred_shipping_address_id,
    up.preferred_billing_address_id,
    (SELECT COUNT(*) FROM addresses WHERE user_id = u.user_id AND is_active = TRUE) as total_addresses
FROM users u
LEFT JOIN user_settings us ON u.user_id = us.user_id
LEFT JOIN user_profiles up ON u.user_id = up.user_id;

-- ============================================================
-- END OF MIGRATION 007
-- ============================================================
