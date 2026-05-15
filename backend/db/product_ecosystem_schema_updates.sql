-- FashionStore Product Ecosystem Schema Updates
-- For premium commerce PLP/PDP experience

-- 1. Product Views Table
CREATE TABLE IF NOT EXISTS product_views (
    view_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT,
    session_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer VARCHAR(500),
    source ENUM('listing', 'search', 'recommendation', 'direct', 'social', 'email', 'ad') DEFAULT 'direct',
    duration_seconds INT DEFAULT 0,
    is_bounce BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_product_views_product (product_id),
    INDEX idx_product_views_user (user_id),
    INDEX idx_product_views_session (session_id),
    INDEX idx_product_views_created (created_at),
    INDEX idx_product_views_source (source)
);

-- 2. Product Comparisons Table
CREATE TABLE IF NOT EXISTS product_comparisons (
    comparison_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_ids JSON NOT NULL, -- Array of product IDs being compared
    comparison_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_product_comparisons_user (user_id),
    INDEX idx_product_comparisons_session (session_id),
    INDEX idx_product_comparisons_active (is_active),
    INDEX idx_product_comparisons_created (created_at)
);

-- 3. Recently Viewed Table (Enhanced)
CREATE TABLE IF NOT EXISTS recently_viewed (
    view_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_id INT NOT NULL,
    product_name VARCHAR(255),
    product_image VARCHAR(500),
    product_price DECIMAL(10, 2),
    product_category VARCHAR(100),
    product_brand VARCHAR(100),
    discount_percent DECIMAL(5, 2),
    rating DECIMAL(3, 2),
    source ENUM('listing', 'search', 'recommendation', 'direct', 'social', 'email', 'ad') DEFAULT 'direct',
    viewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_recently_viewed_user (user_id),
    INDEX idx_recently_viewed_session (session_id),
    INDEX idx_recently_viewed_product (product_id),
    INDEX idx_recently_viewed_viewed (viewed_at),
    INDEX idx_recently_viewed_active (is_active)
);

-- 4. Product FAQ Table
CREATE TABLE IF NOT EXISTS product_faq (
    faq_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    question TEXT NOT NULL,
    answer TEXT NOT NULL,
    category ENUM('general', 'sizing', 'material', 'care', 'shipping', 'returns', 'warranty') DEFAULT 'general',
    priority INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    is_featured BOOLEAN DEFAULT FALSE,
    view_count INT DEFAULT 0,
    helpful_count INT DEFAULT 0,
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_product_faq_product (product_id),
    INDEX idx_product_faq_category (category),
    INDEX idx_product_faq_priority (priority),
    INDEX idx_product_faq_active (is_active),
    INDEX idx_product_faq_featured (is_featured)
);

-- 5. Product Recommendations Table
CREATE TABLE IF NOT EXISTS product_recommendations (
    recommendation_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    recommended_product_id INT NOT NULL,
    recommendation_type ENUM('related', 'similar', 'frequently_bought', 'cross_sell', 'up_sell', 'trending', 'personalized') NOT NULL,
    score DECIMAL(5, 2) DEFAULT 0.00,
    algorithm VARCHAR(100) DEFAULT 'collaborative',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (recommended_product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY uk_product_recommendation (product_id, recommended_product_id, recommendation_type),
    INDEX idx_product_recommendations_product (product_id),
    INDEX idx_product_recommendations_recommended (recommended_product_id),
    INDEX idx_product_recommendations_type (recommendation_type),
    INDEX idx_product_recommendations_score (score),
    INDEX idx_product_recommendations_active (is_active)
);

-- 6. Product Filter Cache Table
CREATE TABLE IF NOT EXISTS product_filter_cache (
    cache_id INT AUTO_INCREMENT PRIMARY KEY,
    cache_key VARCHAR(255) NOT NULL UNIQUE,
    cache_data JSON NOT NULL,
    cache_type ENUM('brands', 'categories', 'sizes', 'colors', 'price_range', 'filter_options') NOT NULL,
    category VARCHAR(100),
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_filter_cache_key (cache_key),
    INDEX idx_filter_cache_type (cache_type),
    INDEX idx_filter_cache_category (category),
    INDEX idx_filter_cache_expires (expires_at),
    INDEX idx_filter_cache_active (is_active)
);

-- 7. Product Analytics Table
CREATE TABLE IF NOT EXISTS product_analytics (
    analytics_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    date DATE NOT NULL,
    view_count INT DEFAULT 0,
    unique_views INT DEFAULT 0,
    add_to_cart_count INT DEFAULT 0,
    wishlist_count INT DEFAULT 0,
    compare_count INT DEFAULT 0,
    search_count INT DEFAULT 0,
    conversion_rate DECIMAL(5, 2) DEFAULT 0.00,
    avg_session_duration INT DEFAULT 0,
    bounce_rate DECIMAL(5, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    UNIQUE KEY uk_product_analytics_date (product_id, date),
    INDEX idx_product_analytics_product (product_id),
    INDEX idx_product_analytics_date (date),
    INDEX idx_product_analytics_views (view_count)
);

-- 8. Product Search Analytics Table
CREATE TABLE IF NOT EXISTS product_search_analytics (
    search_id INT AUTO_INCREMENT PRIMARY KEY,
    search_query VARCHAR(255) NOT NULL,
    user_id INT,
    session_id VARCHAR(255),
    results_count INT DEFAULT 0,
    clicked_product_id INT,
    category_filter VARCHAR(100),
    price_min DECIMAL(10, 2),
    price_max DECIMAL(10, 2),
    brand_filter VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (clicked_product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    INDEX idx_search_analytics_query (search_query),
    INDEX idx_search_analytics_user (user_id),
    INDEX idx_search_analytics_session (session_id),
    INDEX idx_search_analytics_date (created_at)
);

-- 9. Product Click Tracking Table
CREATE TABLE IF NOT EXISTS product_click_tracking (
    click_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    user_id INT,
    session_id VARCHAR(255),
    click_type ENUM('product_card', 'quick_view', 'add_to_cart', 'wishlist', 'compare', 'recommendation', 'search_result') NOT NULL,
    source_page VARCHAR(255),
    source_position INT,
    referrer VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_click_tracking_product (product_id),
    INDEX idx_click_tracking_user (user_id),
    INDEX idx_click_tracking_session (session_id),
    INDEX idx_click_tracking_type (click_type),
    INDEX idx_click_tracking_date (created_at)
);

-- 10. Product Quick View Sessions Table
CREATE TABLE IF NOT EXISTS product_quick_view_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT,
    product_id INT NOT NULL,
    view_duration INT DEFAULT 0,
    interactions JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_quick_view_user (user_id),
    INDEX idx_quick_view_product (product_id),
    INDEX idx_quick_view_expires (expires_at),
    INDEX idx_quick_view_active (is_active)
);

-- Insert initial data for product recommendations
INSERT IGNORE INTO product_recommendations (product_id, recommended_product_id, recommendation_type, score, algorithm)
SELECT 
    p1.product_id,
    p2.product_id,
    'related',
    (RAND() * 4 + 1) as score,
    'collaborative'
FROM products p1
CROSS JOIN products p2
WHERE p1.product_id != p2.product_id
  AND p1.category_id = p2.category_id
  AND p1.is_active = 1
  AND p2.is_active = 1
LIMIT 1000;

-- Insert sample FAQ data for popular products
INSERT IGNORE INTO product_faq (product_id, question, answer, category, priority, is_featured)
SELECT 
    p.product_id,
    CONCAT('What is the material of this ', p.product_name, '?'),
    'This product is made from high-quality materials that ensure comfort and durability. Please check the product description for specific material details.',
    'material',
    1,
    TRUE
FROM products p
WHERE p.is_active = 1
LIMIT 50;

INSERT IGNORE INTO product_faq (product_id, question, answer, category, priority, is_featured)
SELECT 
    p.product_id,
    CONCAT('How do I care for this ', p.product_name, '?'),
    'Please follow the care instructions provided with the product. Generally, we recommend gentle washing and avoiding harsh chemicals.',
    'care',
    2,
    TRUE
FROM products p
WHERE p.is_active = 1
LIMIT 50;

INSERT IGNORE INTO product_faq (product_id, question, answer, category, priority, is_featured)
SELECT 
    p.product_id,
    CONCAT('What is the return policy for this ', p.product_name, '?'),
    'We offer a 30-day return policy for all products. Please ensure the item is in its original condition with all tags attached.',
    'returns',
    3,
    TRUE
FROM products p
WHERE p.is_active = 1
LIMIT 50;

-- Create views for common queries
CREATE OR REPLACE VIEW product_view_summary_view AS
SELECT 
    p.product_id,
    p.product_name,
    p.category_id,
    COUNT(pv.view_id) as total_views,
    COUNT(DISTINCT pv.user_id) as unique_viewers,
    AVG(pv.duration_seconds) as avg_view_duration,
    COUNT(CASE WHEN pv.is_bounce = TRUE THEN 1 END) as bounce_count,
    ROUND(COUNT(CASE WHEN pv.is_bounce = TRUE THEN 1 END) * 100.0 / COUNT(*), 2) as bounce_rate,
    MAX(pv.created_at) as last_viewed_at
FROM products p
LEFT JOIN product_views pv ON p.product_id = pv.product_id
WHERE p.is_active = 1
GROUP BY p.product_id, p.product_name, p.category_id
ORDER BY total_views DESC;

CREATE OR REPLACE VIEW product_comparison_summary_view AS
SELECT 
    pc.comparison_id,
    pc.user_id,
    JSON_LENGTH(pc.product_ids) as product_count,
    pc.comparison_name,
    pc.created_at,
    (SELECT JSON_ARRAYAGG(p.product_name) 
     FROM products p 
     WHERE JSON_CONTAINS(pc.product_ids, JSON_QUOTE(p.product_id, '$'))
    ) as product_names
FROM product_comparisons pc
WHERE pc.is_active = TRUE
ORDER BY pc.created_at DESC;

CREATE OR REPLACE VIEW product_recommendation_stats_view AS
SELECT 
    pr.recommendation_type,
    COUNT(*) as total_recommendations,
    AVG(pr.score) as avg_score,
    MIN(pr.score) as min_score,
    MAX(pr.score) as max_score,
    COUNT(DISTINCT pr.product_id) as unique_products
FROM product_recommendations pr
WHERE pr.is_active = TRUE
GROUP BY pr.recommendation_type
ORDER BY avg_score DESC;

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE UpdateProductAnalytics(IN p_product_id INT, IN p_date DATE)
BEGIN
    INSERT INTO product_analytics (product_id, date, view_count, unique_views, add_to_cart_count, wishlist_count, compare_count, search_count)
    SELECT 
        p_product_id,
        p_date,
        COUNT(pv.view_id),
        COUNT(DISTINCT pv.user_id),
        0, -- Will be updated by other triggers
        0,
        0,
        0
    FROM product_views pv
    WHERE pv.product_id = p_product_id 
      AND DATE(pv.created_at) = p_date
    ON DUPLICATE KEY UPDATE
        view_count = VALUES(view_count),
        unique_views = VALUES(unique_views);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CleanupExpiredProductSessions()
BEGIN
    DELETE FROM product_quick_view_sessions 
    WHERE expires_at < NOW() OR is_active = FALSE;
    
    DELETE FROM product_filter_cache 
    WHERE expires_at < NOW() OR is_active = FALSE;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetProductRecommendations(IN p_product_id INT, IN p_type VARCHAR(50), IN p_limit INT)
BEGIN
    SELECT 
        pr.recommended_product_id,
        pr.score,
        p.product_name,
        p.price,
        p.image_url,
        p.discount_percent,
        p.rating
    FROM product_recommendations pr
    JOIN products p ON pr.recommended_product_id = p.product_id
    WHERE pr.product_id = p_product_id 
      AND pr.recommendation_type = p_type
      AND pr.is_active = TRUE
      AND p.is_active = TRUE
    ORDER BY pr.score DESC
    LIMIT p_limit;
END//
DELIMITER ;

-- Create triggers for automatic updates
DELIMITER //
CREATE TRIGGER update_product_analytics_on_view
AFTER INSERT ON product_views
FOR EACH ROW
BEGIN
    CALL UpdateProductAnalytics(NEW.product_id, DATE(NEW.created_at));
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_product_analytics_on_click
AFTER INSERT ON product_click_tracking
FOR EACH ROW
BEGIN
    IF NEW.click_type = 'add_to_cart' THEN
        UPDATE product_analytics 
        SET add_to_cart_count = add_to_cart_count + 1 
        WHERE product_id = NEW.product_id AND DATE = DATE(NEW.created_at);
    END IF;
END//
DELIMITER ;

-- Schedule cleanup procedures (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS cleanup_expired_product_sessions
ON SCHEDULE EVERY 1 HOUR
DO CALL CleanupExpiredProductSessions();

-- Create indexes for better performance
CREATE INDEX idx_product_views_product_date ON product_views(product_id, DATE(created_at));
CREATE INDEX idx_product_click_tracking_product_date ON product_click_tracking(product_id, DATE(created_at));
CREATE INDEX idx_recently_viewed_user_viewed ON recently_viewed(user_id, viewed_at DESC);
CREATE INDEX idx_product_search_analytics_query_date ON product_search_analytics(search_query, DATE(created_at));

-- Add foreign key constraints for better data integrity
ALTER TABLE product_views ADD CONSTRAINT fk_product_views_source 
CHECK (source IN ('listing', 'search', 'recommendation', 'direct', 'social', 'email', 'ad'));

ALTER TABLE product_comparisons ADD CONSTRAINT fk_product_comparisons_active 
CHECK (is_active IN (0, 1));

ALTER TABLE recently_viewed ADD CONSTRAINT fk_recently_viewed_source 
CHECK (source IN ('listing', 'search', 'recommendation', 'direct', 'social', 'email', 'ad'));

ALTER TABLE recently_viewed ADD CONSTRAINT fk_recently_viewed_active 
CHECK (is_active IN (0, 1));

ALTER TABLE product_faq ADD CONSTRAINT fk_product_faq_category 
CHECK (category IN ('general', 'sizing', 'material', 'care', 'shipping', 'returns', 'warranty'));

ALTER TABLE product_faq ADD CONSTRAINT fk_product_faq_active 
CHECK (is_active IN (0, 1));

ALTER TABLE product_faq ADD CONSTRAINT fk_product_faq_featured 
CHECK (is_featured IN (0, 1));

ALTER TABLE product_recommendations ADD CONSTRAINT fk_product_recommendations_type 
CHECK (recommendation_type IN ('related', 'similar', 'frequently_bought', 'cross_sell', 'up_sell', 'trending', 'personalized'));

ALTER TABLE product_recommendations ADD CONSTRAINT fk_product_recommendations_active 
CHECK (is_active IN (0, 1));

ALTER TABLE product_filter_cache ADD CONSTRAINT fk_product_filter_cache_type 
CHECK (cache_type IN ('brands', 'categories', 'sizes', 'colors', 'price_range', 'filter_options'));

ALTER TABLE product_filter_cache ADD CONSTRAINT fk_product_filter_cache_active 
CHECK (is_active IN (0, 1));

ALTER TABLE product_analytics ADD CONSTRAINT fk_product_analytics_created 
CHECK (created_at <= updated_at);

ALTER TABLE product_search_analytics ADD CONSTRAINT fk_search_analytics_created 
CHECK (created_at IS NOT NULL);

ALTER TABLE product_click_tracking ADD CONSTRAINT fk_click_tracking_type 
CHECK (click_type IN ('product_card', 'quick_view', 'add_to_cart', 'wishlist', 'compare', 'recommendation', 'search_result'));

ALTER TABLE product_quick_view_sessions ADD CONSTRAINT fk_quick_view_sessions_active 
CHECK (is_active IN (0, 1));

-- Add check constraints for data validation
ALTER TABLE product_views ADD CONSTRAINT chk_product_views_duration 
CHECK (duration_seconds >= 0);

ALTER TABLE product_comparisons ADD CONSTRAINT chk_product_comparisons_name 
CHECK (comparison_name IS NOT NULL OR (comparison_name IS NULL AND JSON_LENGTH(product_ids) >= 2));

ALTER TABLE recently_viewed ADD CONSTRAINT chk_recently_viewed_rating 
CHECK (rating >= 0 AND rating <= 5);

ALTER TABLE recently_viewed ADD CONSTRAINT chk_recently_viewed_discount 
CHECK (discount_percent >= 0 AND discount_percent <= 100);

ALTER TABLE product_faq ADD CONSTRAINT chk_product_faq_priority 
CHECK (priority >= 0);

ALTER TABLE product_faq ADD CONSTRAINT chk_product_faq_counts 
CHECK (view_count >= 0 AND helpful_count >= 0);

ALTER TABLE product_recommendations ADD CONSTRAINT chk_product_recommendations_score 
CHECK (score >= 0 AND score <= 5);

ALTER TABLE product_analytics ADD CONSTRAINT chk_product_analytics_rates 
CHECK (conversion_rate >= 0 AND conversion_rate <= 100 AND bounce_rate >= 0 AND bounce_rate <= 100);

ALTER TABLE product_analytics ADD CONSTRAINT chk_product_analytics_counts 
CHECK (view_count >= 0 AND unique_views >= 0 AND add_to_cart_count >= 0 AND wishlist_count >= 0 AND compare_count >= 0 AND search_count >= 0 AND avg_session_duration >= 0);

ALTER TABLE product_click_tracking ADD CONSTRAINT chk_click_tracking_position 
CHECK (source_position >= 0);

ALTER TABLE product_quick_view_sessions ADD CONSTRAINT chk_quick_view_duration 
CHECK (view_duration >= 0);
