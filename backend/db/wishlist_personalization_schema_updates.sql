-- FashionStore Wishlist & Personalization System Schema Updates
-- For production-grade commerce engagement system

-- 1. Enhanced Wishlist Table (if not exists)
CREATE TABLE IF NOT EXISTS wishlist_items (
    wishlist_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    variant_id INT,
    quantity INT DEFAULT 1,
    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    priority ENUM('high', 'medium', 'low') DEFAULT 'medium',
    notes TEXT,
    price_when_added DECIMAL(10, 2),
    discount_when_added DECIMAL(5, 2),
    notify_price_drop BOOLEAN DEFAULT FALSE,
    notify_back_in_stock BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    UNIQUE KEY uk_wishlist_user_product (user_id, product_id, variant_id),
    INDEX idx_wishlist_user (user_id),
    INDEX idx_wishlist_product (product_id),
    INDEX idx_wishlist_date_added (date_added),
    INDEX idx_wishlist_active (is_active),
    INDEX idx_wishlist_priority (priority),
    INDEX idx_wishlist_notifications (notify_price_drop, notify_back_in_stock)
);

-- 2. Saved Items Table
CREATE TABLE IF NOT EXISTS saved_items (
    saved_item_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    variant_id INT,
    quantity INT DEFAULT 1,
    date_saved TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    is_archived BOOLEAN DEFAULT FALSE,
    source ENUM('cart', 'wishlist', 'direct') DEFAULT 'direct',
    original_price DECIMAL(10, 2),
    current_price DECIMAL(10, 2),
    price_difference DECIMAL(10, 2),
    notes TEXT,
    notify_price_drop BOOLEAN DEFAULT FALSE,
    notify_back_in_stock BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    UNIQUE KEY uk_saved_user_product (user_id, product_id, variant_id),
    INDEX idx_saved_user (user_id),
    INDEX idx_saved_product (product_id),
    INDEX idx_saved_date_saved (date_saved),
    INDEX idx_saved_active (is_active),
    INDEX idx_saved_archived (is_archived),
    INDEX idx_saved_source (source),
    INDEX idx_saved_notifications (notify_price_drop, notify_back_in_stock)
);

-- 3. Product Views Table (Enhanced)
CREATE TABLE IF NOT EXISTS product_views (
    view_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_id INT NOT NULL,
    variant_id INT,
    view_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_seconds INT DEFAULT 0,
    source ENUM('product_list', 'search', 'recommendation', 'direct', 'wishlist', 'cart', 'saved_items') DEFAULT 'direct',
    context VARCHAR(100), -- homepage, category_page, product_page, etc.
    referrer VARCHAR(500),
    user_agent TEXT,
    ip_address VARCHAR(45),
    is_converted BOOLEAN DEFAULT FALSE, -- Added to cart or purchased later
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    INDEX idx_product_views_user (user_id),
    INDEX idx_product_views_product (product_id),
    INDEX idx_product_views_date (view_date),
    INDEX idx_product_views_source (source),
    INDEX idx_product_views_session (session_id),
    INDEX idx_product_views_converted (is_converted)
);

-- 4. Recently Viewed Table (Optimized for quick access)
CREATE TABLE IF NOT EXISTS recently_viewed (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_id INT NOT NULL,
    variant_id INT,
    last_viewed TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    view_count INT DEFAULT 1,
    total_duration INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    expires_at TIMESTAMP DEFAULT (DATE_ADD(NOW(), INTERVAL 30 DAY)),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    UNIQUE KEY uk_recently_viewed_user_product (user_id, product_id, variant_id),
    INDEX idx_recently_viewed_user (user_id),
    INDEX idx_recently_viewed_session (session_id),
    INDEX idx_recently_viewed_product (product_id),
    INDEX idx_recently_viewed_last_viewed (last_viewed),
    INDEX idx_recently_viewed_active (is_active),
    INDEX idx_recently_viewed_expires (expires_at)
);

-- 5. Recommendation Logs Table
CREATE TABLE IF NOT EXISTS recommendation_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_id INT NOT NULL,
    recommendation_type ENUM('for_you', 'wishlist_based', 'purchase_based', 'similar', 'trending', 'collaborative', 'content_based') NOT NULL,
    algorithm_version VARCHAR(50),
    confidence_score DECIMAL(5, 4),
    position_in_list INT,
    context VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_clicked BOOLEAN DEFAULT FALSE,
    is_added_to_cart BOOLEAN DEFAULT FALSE,
    is_purchased BOOLEAN DEFAULT FALSE,
    clicked_at TIMESTAMP NULL,
    added_to_cart_at TIMESTAMP NULL,
    purchased_at TIMESTAMP NULL,
    feedback ENUM('like', 'dislike', 'not_interested') DEFAULT NULL,
    feedback_reason VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_recommendation_logs_user (user_id),
    INDEX idx_recommendation_logs_product (product_id),
    INDEX idx_recommendation_logs_type (recommendation_type),
    INDEX idx_recommendation_logs_created (created_at),
    INDEX idx_recommendation_logs_clicked (is_clicked),
    INDEX idx_recommendation_logs_converted (is_added_to_cart, is_purchased),
    INDEX idx_recommendation_logs_session (session_id)
);

-- 6. Product Notifications Table
CREATE TABLE IF NOT EXISTS product_notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    notification_type ENUM('price_drop', 'back_in_stock', 'low_stock', 'new_arrival', 'trending', 'recommendation') NOT NULL,
    title VARCHAR(255),
    message TEXT,
    status ENUM('pending', 'sent', 'delivered', 'read', 'dismissed') DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    delivered_at TIMESTAMP NULL,
    read_at TIMESTAMP NULL,
    expires_at TIMESTAMP DEFAULT (DATE_ADD(NOW(), INTERVAL 7 DAY)),
    is_active BOOLEAN DEFAULT TRUE,
    channels JSON, -- ['email', 'sms', 'push', 'in_app']
    metadata JSON, -- Additional notification data
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_product_notifications_user (user_id),
    INDEX idx_product_notifications_product (product_id),
    INDEX idx_product_notifications_type (notification_type),
    INDEX idx_product_notifications_status (status),
    INDEX idx_product_notifications_created (created_at),
    INDEX idx_product_notifications_expires (expires_at),
    INDEX idx_product_notifications_active (is_active)
);

-- 7. User Preferences Table
CREATE TABLE IF NOT EXISTS user_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    preference_type ENUM('category', 'brand', 'price_range', 'size', 'color', 'style', 'occasion') NOT NULL,
    preference_value VARCHAR(255) NOT NULL,
    weight DECIMAL(3, 2) DEFAULT 1.0, -- Preference weight (0.0 to 1.0)
    source ENUM('explicit', 'implicit', 'learned') DEFAULT 'implicit',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_preferences_type_value (user_id, preference_type, preference_value),
    INDEX idx_user_preferences_user (user_id),
    INDEX idx_user_preferences_type (preference_type),
    INDEX idx_user_preferences_weight (weight),
    INDEX idx_user_preferences_source (source),
    INDEX idx_user_preferences_active (is_active)
);

-- 8. Trending Products Table
CREATE TABLE IF NOT EXISTS trending_products (
    trending_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    category_id INT,
    location VARCHAR(100), -- City, region, or 'global'
    trend_score DECIMAL(10, 4) DEFAULT 0.0,
    view_count INT DEFAULT 0,
    click_count INT DEFAULT 0,
    add_to_cart_count INT DEFAULT 0,
    purchase_count INT DEFAULT 0,
    wishlist_count INT DEFAULT 0,
    search_count INT DEFAULT 0,
    trend_period ENUM('hourly', 'daily', 'weekly', 'monthly') DEFAULT 'daily',
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    rank_position INT DEFAULT 0,
    previous_rank INT DEFAULT 0,
    rank_change INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_trending_products_product (product_id),
    INDEX idx_trending_products_category (category_id),
    INDEX idx_trending_products_location (location),
    INDEX idx_trending_products_score (trend_score),
    INDEX idx_trending_products_period (trend_period, period_start),
    INDEX idx_trending_products_rank (rank_position),
    UNIQUE KEY uk_trending_product_period (product_id, location, trend_period, period_start)
);

-- 9. User Engagement Metrics Table
CREATE TABLE IF NOT EXISTS user_engagement_metrics (
    metric_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    metric_date DATE NOT NULL,
    session_count INT DEFAULT 0,
    page_views INT DEFAULT 0,
    product_views INT DEFAULT 0,
    search_queries INT DEFAULT 0,
    add_to_cart_count INT DEFAULT 0,
    wishlist_additions INT DEFAULT 0,
    saved_items_additions INT DEFAULT 0,
    checkout_initiated INT DEFAULT 0,
    purchases_completed INT DEFAULT 0,
    time_spent_minutes INT DEFAULT 0,
    bounce_rate DECIMAL(5, 2) DEFAULT 0.0,
    conversion_rate DECIMAL(5, 2) DEFAULT 0.0,
    avg_session_duration INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_engagement_date (user_id, metric_date),
    INDEX idx_user_engagement_user (user_id),
    INDEX idx_user_engagement_date (metric_date),
    INDEX idx_user_engagement_conversions (purchases_completed, conversion_rate)
);

-- 10. Price History Table
CREATE TABLE IF NOT EXISTS price_history (
    price_history_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    variant_id INT,
    price DECIMAL(10, 2) NOT NULL,
    original_price DECIMAL(10, 2),
    discount_percent DECIMAL(5, 2) DEFAULT 0.0,
    effective_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_by INT, -- Admin or system
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (variant_id) REFERENCES product_variants(variant_id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_price_history_product (product_id),
    INDEX idx_price_history_variant (variant_id),
    INDEX idx_price_history_date (effective_date),
    INDEX idx_price_history_active (is_active),
    INDEX idx_price_history_price (price)
);

-- Insert initial trending data for major cities
INSERT IGNORE INTO trending_products (product_id, category_id, location, trend_score, trend_period, period_start, period_end, rank_position) 
SELECT 
    p.product_id,
    p.category_id,
    'global' as location,
    (p.view_count * 0.3 + p.purchase_count * 0.5 + p.wishlist_count * 0.2) as trend_score,
    'daily' as trend_period,
    CURDATE() as period_start,
    DATE_ADD(CURDATE(), INTERVAL 1 DAY) as period_end,
    ROW_NUMBER() OVER (ORDER BY (p.view_count * 0.3 + p.purchase_count * 0.5 + p.wishlist_count * 0.2) DESC) as rank_position
FROM products p 
WHERE p.is_active = TRUE 
LIMIT 50;

-- Create views for common queries
CREATE OR REPLACE VIEW user_wishlist_summary AS
SELECT 
    u.user_id,
    u.username,
    COUNT(wi.wishlist_id) as total_items,
    COUNT(CASE WHEN wi.notify_price_drop = TRUE THEN 1 END) as price_drop_notifications,
    COUNT(CASE WHEN wi.notify_back_in_stock = TRUE THEN 1 END) as back_in_stock_notifications,
    SUM(wi.price_when_added) as total_value,
    MAX(wi.date_added) as last_added,
    AVG(wi.discount_when_added) as avg_discount
FROM users u
LEFT JOIN wishlist_items wi ON u.user_id = wi.user_id AND wi.is_active = TRUE
GROUP BY u.user_id, u.username;

CREATE OR REPLACE VIEW user_saved_items_summary AS
SELECT 
    u.user_id,
    u.username,
    COUNT(si.saved_item_id) as total_saved_items,
    COUNT(CASE WHEN si.is_archived = FALSE THEN 1 END) as active_saved_items,
    COUNT(CASE WHEN si.is_archived = TRUE THEN 1 END) as archived_items,
    SUM(si.current_price) as total_saved_value,
    MAX(si.date_saved) as last_saved,
    AVG(si.price_difference) as avg_price_difference
FROM users u
LEFT JOIN saved_items si ON u.user_id = si.user_id AND si.is_active = TRUE
GROUP BY u.user_id, u.username;

CREATE OR REPLACE VIEW recommendation_performance AS
SELECT 
    recommendation_type,
    algorithm_version,
    COUNT(*) as total_recommendations,
    COUNT(CASE WHEN is_clicked = TRUE THEN 1 END) as clicked_count,
    COUNT(CASE WHEN is_added_to_cart = TRUE THEN 1 END) as added_to_cart_count,
    COUNT(CASE WHEN is_purchased = TRUE THEN 1 END) as purchased_count,
    ROUND(COUNT(CASE WHEN is_clicked = TRUE THEN 1 END) * 100.0 / COUNT(*), 2) as click_rate,
    ROUND(COUNT(CASE WHEN is_added_to_cart = TRUE THEN 1 END) * 100.0 / COUNT(*), 2) as add_to_cart_rate,
    ROUND(COUNT(CASE WHEN is_purchased = TRUE THEN 1 END) * 100.0 / COUNT(*), 2) as purchase_rate,
    AVG(confidence_score) as avg_confidence_score
FROM recommendation_logs
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY recommendation_type, algorithm_version
ORDER BY click_rate DESC;

CREATE OR REPLACE VIEW trending_products_view AS
SELECT 
    tp.product_id,
    p.product_name,
    p.category_id,
    c.category_name,
    tp.location,
    tp.trend_score,
    tp.view_count,
    tp.click_count,
    tp.add_to_cart_count,
    tp.purchase_count,
    tp.wishlist_count,
    tp.rank_position,
    tp.rank_change,
    tp.period_start,
    tp.period_end
FROM trending_products tp
JOIN products p ON tp.product_id = p.product_id
JOIN categories c ON p.category_id = c.category_id
WHERE tp.trend_period = 'daily' 
    AND tp.period_start = CURDATE()
ORDER BY tp.rank_position;

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE UpdateRecentlyViewed(IN p_user_id INT, IN p_session_id VARCHAR(255), IN p_product_id INT, IN p_variant_id INT)
BEGIN
    INSERT INTO recently_viewed (user_id, session_id, product_id, variant_id, view_count, total_duration, last_viewed)
    VALUES (p_user_id, p_session_id, p_product_id, p_variant_id, 1, 0, NOW())
    ON DUPLICATE KEY UPDATE
        view_count = view_count + 1,
        total_duration = total_duration + VALUES(total_duration),
        last_viewed = NOW(),
        expires_at = DATE_ADD(NOW(), INTERVAL 30 DAY),
        is_active = TRUE;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE TrackProductEngagement(IN p_product_id INT, IN p_user_id INT, IN p_action VARCHAR(50), IN p_source VARCHAR(100))
BEGIN
    -- Update trending products
    INSERT INTO trending_products (product_id, location, trend_score, trend_period, period_start, period_end)
    VALUES (p_product_id, 'global', 1.0, 'daily', CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 DAY))
    ON DUPLICATE KEY UPDATE
        CASE 
            WHEN p_action = 'view' THEN view_count = view_count + 1, trend_score = trend_score + 0.3
            WHEN p_action = 'click' THEN click_count = click_count + 1, trend_score = trend_score + 0.5
            WHEN p_action = 'add_to_cart' THEN add_to_cart_count = add_to_cart_count + 1, trend_score = trend_score + 0.7
            WHEN p_action = 'purchase' THEN purchase_count = purchase_count + 1, trend_score = trend_score + 1.0
            WHEN p_action = 'wishlist' THEN wishlist_count = wishlist_count + 1, trend_score = trend_score + 0.6
            WHEN p_action = 'search' THEN search_count = search_count + 1, trend_score = trend_score + 0.4
            ELSE trend_score = trend_score + 0.1
        END;
    
    -- Update user engagement metrics
    IF p_user_id IS NOT NULL THEN
        INSERT INTO user_engagement_metrics (user_id, metric_date)
        VALUES (p_user_id, CURDATE())
        ON DUPLICATE KEY UPDATE
            CASE 
                WHEN p_action = 'view' THEN product_views = product_views + 1
                WHEN p_action = 'search' THEN search_queries = search_queries + 1
                WHEN p_action = 'add_to_cart' THEN add_to_cart_count = add_to_cart_count + 1
                WHEN p_action = 'wishlist' THEN wishlist_additions = wishlist_additions + 1
                WHEN p_action = 'purchase' THEN purchases_completed = purchases_completed + 1
                ELSE page_views = page_views + 1
            END,
            updated_at = NOW();
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GeneratePersonalizedRecommendations(IN p_user_id INT, IN p_limit INT)
BEGIN
    DECLARE v_category_preference VARCHAR(100);
    DECLARE v_brand_preference VARCHAR(100);
    DECLARE v_price_min DECIMAL(10, 2);
    DECLARE v_price_max DECIMAL(10, 2);
    
    -- Get user preferences
    SELECT preference_value INTO v_category_preference
    FROM user_preferences 
    WHERE user_id = p_user_id AND preference_type = 'category' AND is_active = TRUE
    ORDER BY weight DESC LIMIT 1;
    
    SELECT preference_value INTO v_brand_preference
    FROM user_preferences 
    WHERE user_id = p_user_id AND preference_type = 'brand' AND is_active = TRUE
    ORDER BY weight DESC LIMIT 1;
    
    -- Generate recommendations based on preferences
    INSERT INTO recommendation_logs (user_id, product_id, recommendation_type, confidence_score, context)
    SELECT 
        p_user_id,
        p.product_id,
        'personalized',
        (CASE 
            WHEN p.category_id = (SELECT category_id FROM categories WHERE category_name = v_category_preference) THEN 0.8
            WHEN p.brand = v_brand_preference THEN 0.7
            ELSE 0.5
        END) as confidence_score,
        'user_preferences'
    FROM products p
    WHERE p.is_active = TRUE
        AND p.product_id NOT IN (
            SELECT product_id FROM wishlist_items WHERE user_id = p_user_id AND is_active = TRUE
            UNION
            SELECT product_id FROM saved_items WHERE user_id = p_user_id AND is_active = TRUE
        )
        AND (
            v_category_preference IS NULL 
            OR p.category_id = (SELECT category_id FROM categories WHERE category_name = v_category_preference)
            OR v_brand_preference IS NULL 
            OR p.brand = v_brand_preference
        )
    ORDER BY confidence_score DESC, p.view_count DESC
    LIMIT p_limit;
END//
DELIMITER ;

-- Create triggers for automatic updates
DELIMITER //
CREATE TRIGGER update_price_history_on_price_change
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    IF NEW.price != OLD.price OR NEW.original_price != OLD.original_price THEN
        INSERT INTO price_history (product_id, price, original_price, discount_percent, effective_date)
        VALUES (NEW.product_id, NEW.price, NEW.original_price, 
                CASE WHEN NEW.original_price > 0 THEN ROUND(((NEW.original_price - NEW.price) / NEW.original_price) * 100, 2) ELSE 0 END,
                NOW());
        
        -- Create price drop notifications for users who requested them
        INSERT INTO product_notifications (user_id, product_id, notification_type, title, message, channels)
        SELECT 
            wi.user_id,
            NEW.product_id,
            'price_drop',
            'Price Drop Alert',
            CONCAT('The price of ', NEW.product_name, ' has dropped from ₹', OLD.price, ' to ₹', NEW.price),
            JSON_ARRAY('email', 'push')
        FROM wishlist_items wi
        WHERE wi.product_id = NEW.product_id 
            AND wi.is_active = TRUE 
            AND wi.notify_price_drop = TRUE;
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_stock_notifications
AFTER UPDATE ON products
FOR EACH ROW
BEGIN
    -- Back in stock notification
    IF NEW.stock > 0 AND OLD.stock = 0 THEN
        INSERT INTO product_notifications (user_id, product_id, notification_type, title, message, channels)
        SELECT 
            wi.user_id,
            NEW.product_id,
            'back_in_stock',
            'Back in Stock Alert',
            CONCAT(NEW.product_name, ' is now back in stock!'),
            JSON_ARRAY('email', 'push')
        FROM wishlist_items wi
        WHERE wi.product_id = NEW.product_id 
            AND wi.is_active = TRUE 
            AND wi.notify_back_in_stock = TRUE;
    END IF;
    
    -- Low stock notification
    IF NEW.stock <= 5 AND NEW.stock > 0 AND OLD.stock > 5 THEN
        INSERT INTO product_notifications (user_id, product_id, notification_type, title, message, channels)
        SELECT 
            wi.user_id,
            NEW.product_id,
            'low_stock',
            'Low Stock Alert',
            CONCAT('Only ', NEW.stock, ' items left for ', NEW.product_name),
            JSON_ARRAY('push')
        FROM wishlist_items wi
        WHERE wi.product_id = NEW.product_id 
            AND wi.is_active = TRUE 
            AND wi.notify_back_in_stock = TRUE;
    END IF;
END//
DELIMITER ;

-- Schedule background jobs (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS cleanup_expired_recently_viewed
ON SCHEDULE EVERY 1 HOUR
DO UPDATE recently_viewed SET is_active = FALSE WHERE expires_at < NOW();

CREATE EVENT IF NOT EXISTS cleanup_old_notifications
ON SCHEDULE EVERY 1 DAY
DO DELETE FROM product_notifications WHERE expires_at < NOW() OR status = 'dismissed';

CREATE EVENT IF NOT EXISTS update_daily_trending
ON SCHEDULE EVERY 1 DAY
STARTS '00:00:00'
DO CALL UpdateDailyTrendingProducts();

CREATE EVENT IF NOT EXISTS generate_recommendations
ON SCHEDULE EVERY 6 HOURS
DO CALL GenerateBatchRecommendations();

-- Create indexes for better performance
CREATE INDEX idx_wishlist_composite ON wishlist_items(user_id, is_active, date_added);
CREATE INDEX idx_saved_items_composite ON saved_items(user_id, is_active, is_archived, date_saved);
CREATE INDEX idx_product_views_composite ON product_views(user_id, product_id, view_date);
CREATE INDEX idx_recommendation_logs_composite ON recommendation_logs(user_id, recommendation_type, created_at);
CREATE INDEX idx_notifications_composite ON product_notifications(user_id, notification_type, status);
CREATE INDEX idx_trending_composite ON trending_products(location, trend_period, period_start, trend_score);

-- Add foreign key constraints for better data integrity
ALTER TABLE wishlist_items ADD CONSTRAINT chk_wishlist_quantity CHECK (quantity > 0);
ALTER TABLE wishlist_items ADD CONSTRAINT chk_wishlist_discount CHECK (discount_when_added >= 0 AND discount_when_added <= 100);
ALTER TABLE saved_items ADD CONSTRAINT chk_saved_quantity CHECK (quantity > 0);
ALTER TABLE product_views ADD CONSTRAINT chk_product_views_duration CHECK (duration_seconds >= 0);
ALTER TABLE recently_viewed ADD CONSTRAINT chk_recently_viewed_count CHECK (view_count > 0 AND total_duration >= 0);
ALTER TABLE recommendation_logs ADD CONSTRAINT chk_recommendation_confidence CHECK (confidence_score >= 0 AND confidence_score <= 1);
ALTER TABLE user_preferences ADD CONSTRAINT chk_user_preferences_weight CHECK (weight >= 0 AND weight <= 1);
ALTER TABLE trending_products ADD CONSTRAINT chk_trending_score CHECK (trend_score >= 0);
ALTER TABLE user_engagement_metrics ADD CONSTRAINT chk_engagement_bounce CHECK (bounce_rate >= 0 AND bounce_rate <= 100);
ALTER TABLE user_engagement_metrics ADD CONSTRAINT chk_engagement_conversion CHECK (conversion_rate >= 0 AND conversion_rate <= 100);
ALTER TABLE price_history ADD CONSTRAINT chk_price_history_price CHECK (price > 0 AND original_price >= 0);
ALTER TABLE price_history ADD CONSTRAINT chk_price_history_discount CHECK (discount_percent >= 0 AND discount_percent <= 100);

-- Add check constraints for data validation
ALTER TABLE wishlist_items ADD CONSTRAINT chk_wishlist_priority CHECK (priority IN ('high', 'medium', 'low'));
ALTER TABLE saved_items ADD CONSTRAINT chk_saved_source CHECK (source IN ('cart', 'wishlist', 'direct'));
ALTER TABLE product_views ADD CONSTRAINT chk_product_views_source CHECK (source IN ('product_list', 'search', 'recommendation', 'direct', 'wishlist', 'cart', 'saved_items'));
ALTER TABLE recommendation_logs ADD CONSTRAINT chk_recommendation_type CHECK (recommendation_type IN ('for_you', 'wishlist_based', 'purchase_based', 'similar', 'trending', 'collaborative', 'content_based'));
ALTER TABLE product_notifications ADD CONSTRAINT chk_notification_type CHECK (notification_type IN ('price_drop', 'back_in_stock', 'low_stock', 'new_arrival', 'trending', 'recommendation'));
ALTER TABLE product_notifications ADD CONSTRAINT chk_notification_status CHECK (status IN ('pending', 'sent', 'delivered', 'read', 'dismissed'));
ALTER TABLE user_preferences ADD CONSTRAINT chk_preference_type CHECK (preference_type IN ('category', 'brand', 'price_range', 'size', 'color', 'style', 'occasion'));
ALTER TABLE user_preferences ADD CONSTRAINT chk_preference_source CHECK (source IN ('explicit', 'implicit', 'learned'));
ALTER TABLE trending_products ADD CONSTRAINT chk_trending_period CHECK (trend_period IN ('hourly', 'daily', 'weekly', 'monthly'));
ALTER TABLE price_history ADD CONSTRAINT chk_price_history_active CHECK (is_active IN (TRUE, FALSE));

-- Add full-text indexes for search optimization
CREATE FULLTEXT INDEX ft_products_search ON products(product_name, description, brand);
CREATE FULLTEXT INDEX ft_categories_search ON categories(category_name, description);
CREATE FULLTEXT INDEX ft_notifications_search ON product_notifications(title, message);

-- Add partitioning for large tables (optional for very high traffic)
-- ALTER TABLE product_views PARTITION BY RANGE (TO_DAYS(view_date)) (
--     PARTITION p_old VALUES LESS THAN (TO_DAYS('2024-01-01')),
--     PARTITION p_2024 VALUES LESS THAN (TO_DAYS('2025-01-01')),
--     PARTITION p_2025 VALUES LESS THAN (TO_DAYS('2026-01-01')),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- Create additional stored procedures for maintenance
DELIMITER //
CREATE PROCEDURE CleanupOldProductViews()
BEGIN
    DELETE FROM product_views WHERE view_date < DATE_SUB(NOW(), INTERVAL 90 DAY);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE UpdateUserPreferences(IN p_user_id INT)
BEGIN
    -- Update preferences based on user behavior
    INSERT INTO user_preferences (user_id, preference_type, preference_value, weight, source)
    SELECT 
        p_user_id,
        'category' as preference_type,
        c.category_name as preference_value,
        COUNT(*) * 0.1 as weight,
        'learned' as source
    FROM product_views pv
    JOIN products p ON pv.product_id = p.product_id
    JOIN categories c ON p.category_id = c.category_id
    WHERE pv.user_id = p_user_id 
        AND pv.view_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)
    GROUP BY c.category_name
    HAVING COUNT(*) >= 3
    ON DUPLICATE KEY UPDATE
        weight = VALUES(weight),
        updated_at = NOW();
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GeneratePriceDropNotifications()
BEGIN
    -- Generate notifications for significant price drops
    INSERT INTO product_notifications (user_id, product_id, notification_type, title, message, channels)
    SELECT 
        wi.user_id,
        p.product_id,
        'price_drop',
        'Significant Price Drop',
        CONCAT(p.product_name, ' price dropped by more than 20%!'),
        JSON_ARRAY('email', 'push')
    FROM products p
    JOIN wishlist_items wi ON p.product_id = wi.product_id
    JOIN price_history ph ON p.product_id = ph.product_id
    WHERE wi.is_active = TRUE 
        AND wi.notify_price_drop = TRUE
        AND ph.effective_date >= DATE_SUB(NOW(), INTERVAL 1 DAY)
        AND ph.discount_percent >= 20
        AND ph.is_active = TRUE;
END//
DELIMITER ;
