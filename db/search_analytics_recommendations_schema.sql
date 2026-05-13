-- FashionStore Search Analytics & Recommendation Intelligence Schema
-- For advanced search, analytics, and recommendation system

-- 1. Search Logs Table
CREATE TABLE IF NOT EXISTS search_logs (
    search_log_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    query VARCHAR(500) NOT NULL,
    normalized_query VARCHAR(500),
    category_id INT,
    brand_id INT,
    price_min DECIMAL(10,2),
    price_max DECIMAL(10,2),
    filters JSON,
    sort_by VARCHAR(50),
    results_count INT DEFAULT 0,
    page_number INT DEFAULT 1,
    results_per_page INT DEFAULT 20,
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer VARCHAR(500),
    search_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_ms INT,
    click_count INT DEFAULT 0,
    conversion_count INT DEFAULT 0,
    is_failed BOOLEAN DEFAULT FALSE,
    failure_reason VARCHAR(255),
    autocomplete_used BOOLEAN DEFAULT FALSE,
    voice_search BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (brand_id) REFERENCES brands(brand_id) ON DELETE SET NULL,
    INDEX idx_search_logs_user (user_id),
    INDEX idx_search_logs_session (session_id),
    INDEX idx_search_logs_query (query),
    INDEX idx_search_logs_normalized (normalized_query),
    INDEX idx_search_logs_timestamp (search_timestamp),
    INDEX idx_search_logs_category (category_id),
    INDEX idx_search_logs_brand (brand_id),
    INDEX idx_search_logs_failed (is_failed),
    INDEX idx_search_logs_duration (duration_ms)
);

-- 2. Search Click Events Table
CREATE TABLE IF NOT EXISTS search_click_events (
    click_id INT AUTO_INCREMENT PRIMARY KEY,
    search_log_id INT NOT NULL,
    user_id INT,
    product_id INT NOT NULL,
    position INT NOT NULL,
    page_number INT DEFAULT 1,
    click_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    time_to_click_ms INT,
    converted BOOLEAN DEFAULT FALSE,
    conversion_timestamp TIMESTAMP NULL,
    conversion_value DECIMAL(10,2),
    FOREIGN KEY (search_log_id) REFERENCES search_logs(search_log_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_search_clicks_search (search_log_id),
    INDEX idx_search_clicks_user (user_id),
    INDEX idx_search_clicks_product (product_id),
    INDEX idx_search_clicks_position (position),
    INDEX idx_search_clicks_timestamp (click_timestamp),
    INDEX idx_search_clicks_converted (converted)
);

-- 3. Recommendation Events Table
CREATE TABLE IF NOT EXISTS recommendation_events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    recommendation_type ENUM('homepage', 'pdp', 'cart', 'checkout', 'category', 'search') NOT NULL,
    algorithm ENUM('collaborative', 'content_based', 'hybrid', 'trending', 'personalized') NOT NULL,
    product_id INT NOT NULL,
    position INT NOT NULL,
    context JSON,
    impression_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    click_timestamp TIMESTAMP NULL,
    conversion_timestamp TIMESTAMP NULL,
    feedback_score INT, -- 1-5 rating
    feedback_timestamp TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_recommendations_user (user_id),
    INDEX idx_recommendations_session (session_id),
    INDEX idx_recommendations_type (recommendation_type),
    INDEX idx_recommendations_algorithm (algorithm),
    INDEX idx_recommendations_product (product_id),
    INDEX idx_recommendations_impression (impression_timestamp),
    INDEX idx_recommendations_click (click_timestamp),
    INDEX idx_recommendations_conversion (conversion_timestamp)
);

-- 4. Product Clickstream Table
CREATE TABLE IF NOT EXISTS product_clickstream (
    clickstream_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    product_id INT NOT NULL,
    action_type ENUM('view', 'add_to_cart', 'wishlist', 'purchase', 'share', 'compare') NOT NULL,
    source ENUM('search', 'recommendation', 'category', 'homepage', 'direct', 'external') NOT NULL,
    source_context JSON,
    page_url VARCHAR(500),
    referrer_url VARCHAR(500),
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    duration_ms INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    INDEX idx_clickstream_user (user_id),
    INDEX idx_clickstream_session (session_id),
    INDEX idx_clickstream_product (product_id),
    INDEX idx_clickstream_action (action_type),
    INDEX idx_clickstream_source (source),
    INDEX idx_clickstream_timestamp (timestamp)
);

-- 5. User Behavior Tracking Table
CREATE TABLE IF NOT EXISTS user_behavior_tracking (
    behavior_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    session_id VARCHAR(255),
    event_type ENUM('page_view', 'product_view', 'search', 'filter', 'sort', 'scroll', 'hover', 'click') NOT NULL,
    entity_type ENUM('product', 'category', 'brand', 'search', 'page') NOT NULL,
    entity_id INT,
    page_url VARCHAR(500),
    referrer_url VARCHAR(500),
    scroll_depth INT,
    time_on_page_ms INT,
    mouse_movements INT,
    clicks INT,
    keypresses INT,
    device_info JSON,
    browser_info JSON,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_behavior_user (user_id),
    INDEX idx_behavior_session (session_id),
    INDEX idx_behavior_event (event_type),
    INDEX idx_behavior_entity (entity_type, entity_id),
    INDEX idx_behavior_timestamp (timestamp)
);

-- 6. Search Suggestions Table
CREATE TABLE IF NOT EXISTS search_suggestions (
    suggestion_id INT AUTO_INCREMENT PRIMARY KEY,
    query VARCHAR(500) NOT NULL,
    suggestion VARCHAR(500) NOT NULL,
    category_id INT,
    brand_id INT,
    product_id INT,
    suggestion_type ENUM('autocomplete', 'correction', 'popular', 'trending') NOT NULL,
    score DECIMAL(5,4) DEFAULT 0.0000,
    click_count INT DEFAULT 0,
    conversion_count INT DEFAULT 0,
    last_used TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (brand_id) REFERENCES brands(brand_id) ON DELETE SET NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    INDEX idx_suggestions_query (query),
    INDEX idx_suggestions_type (suggestion_type),
    INDEX idx_suggestions_score (score),
    INDEX idx_suggestions_clicks (click_count),
    INDEX idx_suggestions_active (is_active),
    FULLTEXT INDEX ft_suggestions_text (query, suggestion)
);

-- 7. Trending Searches Table
CREATE TABLE IF NOT EXISTS trending_searches (
    trend_id INT AUTO_INCREMENT PRIMARY KEY,
    query VARCHAR(500) NOT NULL,
    normalized_query VARCHAR(500),
    category_id INT,
    search_count INT DEFAULT 0,
    click_count INT DEFAULT 0,
    conversion_count INT DEFAULT 0,
    trend_score DECIMAL(10,4) DEFAULT 0.0000,
    time_range ENUM('hour', 'day', 'week', 'month') NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    previous_count INT DEFAULT 0,
    growth_rate DECIMAL(5,2) DEFAULT 0.00,
    is_seasonal BOOLEAN DEFAULT FALSE,
    season VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_trending_query (query),
    INDEX idx_trending_time_range (time_range),
    INDEX idx_trending_period (period_start, period_end),
    INDEX idx_trending_score (trend_score),
    INDEX idx_trending_growth (growth_rate),
    UNIQUE KEY uk_trending_query_period (query, normalized_query, category_id, time_range, period_start, period_end)
);

-- 8. Recommendation Cache Table
CREATE TABLE IF NOT EXISTS recommendation_cache (
    cache_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    cache_key VARCHAR(255) NOT NULL,
    recommendation_type ENUM('homepage', 'pdp', 'cart', 'checkout', 'category', 'search') NOT NULL,
    algorithm ENUM('collaborative', 'content_based', 'hybrid', 'trending', 'personalized') NOT NULL,
    product_ids JSON NOT NULL,
    scores JSON,
    context JSON,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    hit_count INT DEFAULT 0,
    last_hit TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_cache_user (user_id),
    INDEX idx_cache_key (cache_key),
    INDEX idx_cache_type (recommendation_type),
    INDEX idx_cache_algorithm (algorithm),
    INDEX idx_cache_expires (expires_at),
    INDEX idx_cache_hits (hit_count),
    UNIQUE KEY uk_cache_user_key (user_id, cache_key, recommendation_type, algorithm)
);

-- 9. User Preferences Table
CREATE TABLE IF NOT EXISTS user_search_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    preference_type ENUM('search_history', 'recommendation_algorithm', 'category_preferences', 'brand_preferences', 'price_range', 'notification_preferences') NOT NULL,
    preference_data JSON NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_preferences_user (user_id),
    INDEX idx_preferences_type (preference_type),
    UNIQUE KEY uk_preferences_user_type (user_id, preference_type)
);

-- 10. Search Analytics Summary Table
CREATE TABLE IF NOT EXISTS search_analytics_summary (
    summary_id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    category_id INT,
    total_searches INT DEFAULT 0,
    unique_searches INT DEFAULT 0,
    total_clicks INT DEFAULT 0,
    total_conversions INT DEFAULT 0,
    avg_results_per_search DECIMAL(8,2) DEFAULT 0.00,
    avg_click_position DECIMAL(8,2) DEFAULT 0.00,
    conversion_rate DECIMAL(5,4) DEFAULT 0.0000,
    click_through_rate DECIMAL(5,4) DEFAULT 0.0000,
    avg_search_duration_ms INT DEFAULT 0,
    failed_searches INT DEFAULT 0,
    voice_searches INT DEFAULT 0,
    autocomplete_searches INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_summary_date (date),
    INDEX idx_summary_category (category_id),
    UNIQUE KEY uk_summary_date_category (date, category_id)
);

-- 11. Product Popularity Scores Table
CREATE TABLE IF NOT EXISTS product_popularity_scores (
    score_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    category_id INT,
    popularity_score DECIMAL(10,6) DEFAULT 0.000000,
    trending_score DECIMAL(10,6) DEFAULT 0.000000,
    conversion_score DECIMAL(10,6) DEFAULT 0.000000,
    view_score DECIMAL(10,6) DEFAULT 0.000000,
    click_score DECIMAL(10,6) DEFAULT 0.000000,
    purchase_score DECIMAL(10,6) DEFAULT 0.000000,
    wishlist_score DECIMAL(10,6) DEFAULT 0.000000,
    share_score DECIMAL(10,6) DEFAULT 0.000000,
    time_range ENUM('hour', 'day', 'week', 'month', 'quarter', 'year') NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    total_views INT DEFAULT 0,
    total_clicks INT DEFAULT 0,
    total_purchases INT DEFAULT 0,
    total_wishlist_adds INT DEFAULT 0,
    total_shares INT DEFAULT 0,
    revenue DECIMAL(12,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_popularity_product (product_id),
    INDEX idx_popularity_category (category_id),
    INDEX idx_popularity_time_range (time_range),
    INDEX idx_popularity_period (period_start, period_end),
    INDEX idx_popularity_score (popularity_score),
    UNIQUE KEY uk_popularity_product_time (product_id, category_id, time_range, period_start, period_end)
);

-- 12. Search Performance Metrics Table
CREATE TABLE IF NOT EXISTS search_performance_metrics (
    metric_id INT AUTO_INCREMENT PRIMARY KEY,
    query VARCHAR(500) NOT NULL,
    normalized_query VARCHAR(500),
    category_id INT,
    total_searches INT DEFAULT 0,
    unique_users INT DEFAULT 0,
    avg_results_count DECIMAL(8,2) DEFAULT 0.00,
    avg_click_position DECIMAL(8,2) DEFAULT 0.00,
    click_through_rate DECIMAL(5,4) DEFAULT 0.0000,
    conversion_rate DECIMAL(5,4) DEFAULT 0.0000,
    bounce_rate DECIMAL(5,4) DEFAULT 0.0000,
    avg_time_to_click_ms INT DEFAULT 0,
    zero_result_rate DECIMAL(5,4) DEFAULT 0.0000,
    autocomplete_usage_rate DECIMAL(5,4) DEFAULT 0.0000,
    voice_usage_rate DECIMAL(5,4) DEFAULT 0.0000,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL,
    INDEX idx_performance_query (query),
    INDEX idx_performance_normalized (normalized_query),
    INDEX idx_performance_category (category_id),
    INDEX idx_performance_ctr (click_through_rate),
    INDEX idx_performance_conversion (conversion_rate),
    UNIQUE KEY uk_performance_query_category (query, normalized_query, category_id)
);

-- Create views for common analytics queries
CREATE OR REPLACE VIEW search_performance_dashboard AS
SELECT 
    DATE(search_timestamp) as search_date,
    COUNT(*) as total_searches,
    COUNT(DISTINCT user_id) as unique_users,
    COUNT(DISTINCT query) as unique_queries,
    AVG(results_count) as avg_results,
    AVG(duration_ms) as avg_duration,
    SUM(click_count) as total_clicks,
    SUM(conversion_count) as total_conversions,
    (SUM(conversion_count) * 100.0 / COUNT(*)) as conversion_rate,
    (SUM(click_count) * 100.0 / COUNT(*)) as click_through_rate,
    SUM(CASE WHEN is_failed THEN 1 ELSE 0 END) as failed_searches,
    SUM(CASE WHEN voice_search THEN 1 ELSE 0 END) as voice_searches,
    SUM(CASE WHEN autocomplete_used THEN 1 ELSE 0 END) as autocomplete_searches
FROM search_logs
GROUP BY DATE(search_timestamp);

CREATE OR REPLACE VIEW recommendation_performance_dashboard AS
SELECT 
    DATE(impression_timestamp) as recommendation_date,
    recommendation_type,
    algorithm,
    COUNT(*) as total_impressions,
    COUNT(DISTINCT user_id) as unique_users,
    COUNT(click_timestamp) as total_clicks,
    COUNT(conversion_timestamp) as total_conversions,
    (COUNT(click_timestamp) * 100.0 / COUNT(*)) as click_through_rate,
    (COUNT(conversion_timestamp) * 100.0 / COUNT(*)) as conversion_rate,
    AVG(feedback_score) as avg_feedback_score
FROM recommendation_events
GROUP BY DATE(impression_timestamp), recommendation_type, algorithm;

CREATE OR REPLACE VIEW product_trending_dashboard AS
SELECT 
    p.product_id,
    p.product_name,
    c.category_name,
    COALESCE(pps.popularity_score, 0) as popularity_score,
    COALESCE(pps.trending_score, 0) as trending_score,
    COALESCE(pps.total_views, 0) as total_views,
    COALESCE(pps.total_clicks, 0) as total_clicks,
    COALESCE(pps.total_purchases, 0) as total_purchases,
    COALESCE(pps.revenue, 0) as revenue
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN product_popularity_scores pps ON p.product_id = pps.product_id 
    AND pps.time_range = 'week' 
    AND pps.period_end >= DATE_SUB(NOW(), INTERVAL 1 DAY)
    AND pps.period_end <= NOW();

-- Create stored procedures for common operations
DELIMITER //
CREATE PROCEDURE LogSearch(IN p_user_id INT, IN p_session_id VARCHAR(255), IN p_query VARCHAR(500), IN p_normalized_query VARCHAR(500), IN p_category_id INT, IN p_brand_id INT, IN p_filters JSON, IN p_sort_by VARCHAR(50), IN p_results_count INT, IN p_duration_ms INT, IN p_ip_address VARCHAR(45), IN p_user_agent TEXT, IN p_autocomplete_used BOOLEAN, IN p_voice_search BOOLEAN)
BEGIN
    INSERT INTO search_logs (user_id, session_id, query, normalized_query, category_id, brand_id, filters, sort_by, results_count, duration_ms, ip_address, user_agent, autocomplete_used, voice_search)
    VALUES (p_user_id, p_session_id, p_query, p_normalized_query, p_category_id, p_brand_id, p_filters, p_sort_by, p_results_count, p_duration_ms, p_ip_address, p_user_agent, p_autocomplete_used, p_voice_search);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE LogSearchClick(IN p_search_log_id INT, IN p_user_id INT, IN p_product_id INT, IN p_position INT, IN p_time_to_click_ms INT)
BEGIN
    INSERT INTO search_click_events (search_log_id, user_id, product_id, position, time_to_click_ms)
    VALUES (p_search_log_id, p_user_id, p_product_id, p_position, p_time_to_click_ms);
    
    -- Update search log click count
    UPDATE search_logs 
    SET click_count = click_count + 1 
    WHERE search_log_id = p_search_log_id;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE LogRecommendationImpression(IN p_user_id INT, IN p_session_id VARCHAR(255), IN p_recommendation_type VARCHAR(50), IN p_algorithm VARCHAR(50), IN p_product_id INT, IN p_position INT, IN p_context JSON)
BEGIN
    INSERT INTO recommendation_events (user_id, session_id, recommendation_type, algorithm, product_id, position, context)
    VALUES (p_user_id, p_session_id, p_recommendation_type, p_algorithm, p_product_id, p_position, p_context);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE UpdateTrendingSearches(IN p_time_range VARCHAR(20))
BEGIN
    -- Calculate trending searches for the specified time range
    INSERT INTO trending_searches (query, normalized_query, category_id, search_count, click_count, conversion_count, trend_score, time_range, period_start, period_end, previous_count, growth_rate)
    SELECT 
        normalized_query,
        normalized_query,
        category_id,
        COUNT(*) as search_count,
        SUM(click_count) as click_count,
        SUM(conversion_count) as conversion_count,
        (COUNT(*) * 1.0 + SUM(click_count) * 2.0 + SUM(conversion_count) * 5.0) as trend_score,
        p_time_range,
        DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            ELSE 1 DAY END) as period_start,
        NOW() as period_end,
        0 as previous_count,
        0.00 as growth_rate
    FROM search_logs
    WHERE search_timestamp >= DATE_SUB(NOW(), INTERVAL CASE p_time_range 
        WHEN 'hour' THEN 1 HOUR
        WHEN 'day' THEN 1 DAY
        WHEN 'week' THEN 1 WEEK
        WHEN 'month' THEN 1 MONTH
        ELSE 1 DAY END)
    GROUP BY normalized_query, category_id
    ON DUPLICATE KEY UPDATE
        search_count = VALUES(search_count),
        click_count = VALUES(click_count),
        conversion_count = VALUES(conversion_count),
        trend_score = VALUES(trend_score),
        period_end = VALUES(period_end);
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE UpdateProductPopularityScores(IN p_time_range VARCHAR(20))
BEGIN
    -- Update product popularity scores
    INSERT INTO product_popularity_scores (product_id, category_id, popularity_score, trending_score, conversion_score, view_score, click_score, purchase_score, wishlist_score, share_score, time_range, period_start, period_end, total_views, total_clicks, total_purchases, total_wishlist_adds, total_shares, revenue)
    SELECT 
        p.product_id,
        p.category_id,
        -- Calculate composite popularity score
        (COALESCE(views.total_views, 0) * 0.3 + 
         COALESCE(clicks.total_clicks, 0) * 0.2 + 
         COALESCE(purchases.total_purchases, 0) * 0.4 + 
         COALESCE(wishlist.total_wishlist_adds, 0) * 0.05 + 
         COALESCE(shares.total_shares, 0) * 0.05) as popularity_score,
        -- Trending score based on recent growth
        (COALESCE(recent_views.total_views, 0) * 0.4 + 
         COALESCE(recent_clicks.total_clicks, 0) * 0.3 + 
         COALESCE(recent_purchases.total_purchases, 0) * 0.3) as trending_score,
        COALESCE(purchases.total_purchases, 0) as conversion_score,
        COALESCE(views.total_views, 0) as view_score,
        COALESCE(clicks.total_clicks, 0) as click_score,
        COALESCE(purchases.total_purchases, 0) as purchase_score,
        COALESCE(wishlist.total_wishlist_adds, 0) as wishlist_score,
        COALESCE(shares.total_shares, 0) as share_score,
        p_time_range,
        DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            WHEN 'quarter' THEN 1 QUARTER
            WHEN 'year' THEN 1 YEAR
            ELSE 1 DAY END) as period_start,
        NOW() as period_end,
        COALESCE(views.total_views, 0) as total_views,
        COALESCE(clicks.total_clicks, 0) as total_clicks,
        COALESCE(purchases.total_purchases, 0) as total_purchases,
        COALESCE(wishlist.total_wishlist_adds, 0) as total_wishlist_adds,
        COALESCE(shares.total_shares, 0) as total_shares,
        COALESCE(purchases.revenue, 0) as revenue
    FROM products p
    LEFT JOIN (
        SELECT product_id, COUNT(*) as total_views
        FROM product_clickstream
        WHERE action_type = 'view'
        AND timestamp >= DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            WHEN 'quarter' THEN 1 QUARTER
            WHEN 'year' THEN 1 YEAR
            ELSE 1 DAY END)
        GROUP BY product_id
    ) views ON p.product_id = views.product_id
    LEFT JOIN (
        SELECT product_id, COUNT(*) as total_clicks
        FROM product_clickstream
        WHERE action_type = 'add_to_cart'
        AND timestamp >= DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            WHEN 'quarter' THEN 1 QUARTER
            WHEN 'year' THEN 1 YEAR
            ELSE 1 DAY END)
        GROUP BY product_id
    ) clicks ON p.product_id = clicks.product_id
    LEFT JOIN (
        SELECT product_id, COUNT(*) as total_purchases, SUM(price) as revenue
        FROM product_clickstream pc
        JOIN order_items oi ON pc.product_id = oi.product_id AND pc.session_id = oi.session_id
        WHERE pc.action_type = 'purchase'
        AND pc.timestamp >= DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            WHEN 'quarter' THEN 1 QUARTER
            WHEN 'year' THEN 1 YEAR
            ELSE 1 DAY END)
        GROUP BY product_id
    ) purchases ON p.product_id = purchases.product_id
    LEFT JOIN (
        SELECT product_id, COUNT(*) as total_wishlist_adds
        FROM product_clickstream
        WHERE action_type = 'wishlist'
        AND timestamp >= DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            WHEN 'quarter' THEN 1 QUARTER
            WHEN 'year' THEN 1 YEAR
            ELSE 1 DAY END)
        GROUP BY product_id
    ) wishlist ON p.product_id = wishlist.product_id
    LEFT JOIN (
        SELECT product_id, COUNT(*) as total_shares
        FROM product_clickstream
        WHERE action_type = 'share'
        AND timestamp >= DATE_SUB(NOW(), INTERVAL CASE p_time_range 
            WHEN 'hour' THEN 1 HOUR
            WHEN 'day' THEN 1 DAY
            WHEN 'week' THEN 1 WEEK
            WHEN 'month' THEN 1 MONTH
            WHEN 'quarter' THEN 1 QUARTER
            WHEN 'year' THEN 1 YEAR
            ELSE 1 DAY END)
        GROUP BY product_id
    ) shares ON p.product_id = shares.product_id
    ON DUPLICATE KEY UPDATE
        popularity_score = VALUES(popularity_score),
        trending_score = VALUES(trending_score),
        conversion_score = VALUES(conversion_score),
        view_score = VALUES(view_score),
        click_score = VALUES(click_score),
        purchase_score = VALUES(purchase_score),
        wishlist_score = VALUES(wishlist_score),
        share_score = VALUES(share_score),
        period_end = VALUES(period_end),
        total_views = VALUES(total_views),
        total_clicks = VALUES(total_clicks),
        total_purchases = VALUES(total_purchases),
        total_wishlist_adds = VALUES(total_wishlist_adds),
        total_shares = VALUES(total_shares),
        revenue = VALUES(revenue);
END//
DELIMITER ;

-- Create triggers for automatic updates
DELIMITER //
CREATE TRIGGER update_search_click_count
AFTER INSERT ON search_click_events
FOR EACH ROW
BEGIN
    UPDATE search_logs 
    SET click_count = click_count + 1 
    WHERE search_log_id = NEW.search_log_id;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_search_conversion_count
AFTER UPDATE ON search_click_events
FOR EACH ROW
BEGIN
    IF NEW.converted = TRUE AND OLD.converted = FALSE THEN
        UPDATE search_logs 
        SET conversion_count = conversion_count + 1 
        WHERE search_log_id = NEW.search_log_id;
    END IF;
END//
DELIMITER ;

DELIMITER //
CREATE TRIGGER update_recommendation_hit_count
AFTER INSERT ON recommendation_events
FOR EACH ROW
BEGIN
    UPDATE recommendation_cache 
    SET hit_count = hit_count + 1,
        last_hit = NOW()
    WHERE user_id = NEW.user_id 
        AND recommendation_type = NEW.recommendation_type 
        AND algorithm = NEW.algorithm;
END//
DELIMITER ;

-- Schedule background jobs (MySQL event scheduler)
-- Note: This requires the event scheduler to be enabled: SET GLOBAL event_scheduler = ON;
CREATE EVENT IF NOT EXISTS update_trending_searches_hourly
ON SCHEDULE EVERY 1 HOUR
DO CALL UpdateTrendingSearches('hour');

CREATE EVENT IF NOT EXISTS update_trending_searches_daily
ON SCHEDULE EVERY 1 DAY
STARTS '00:00:00'
DO CALL UpdateTrendingSearches('day');

CREATE EVENT IF NOT EXISTS update_trending_searches_weekly
ON SCHEDULE EVERY 1 WEEK
STARTS '00:00:00'
DO CALL UpdateTrendingSearches('week');

CREATE EVENT IF NOT EXISTS update_trending_searches_monthly
ON SCHEDULE EVERY 1 MONTH
STARTS '00:00:00'
DO CALL UpdateTrendingSearches('month');

CREATE EVENT IF NOT EXISTS update_product_popularity_daily
ON SCHEDULE EVERY 1 DAY
STARTS '01:00:00'
DO CALL UpdateProductPopularityScores('day');

CREATE EVENT IF NOT EXISTS update_product_popularity_weekly
ON SCHEDULE EVERY 1 WEEK
STARTS '01:00:00'
DO CALL UpdateProductPopularityScores('week');

CREATE EVENT IF NOT EXISTS update_product_popularity_monthly
ON SCHEDULE EVERY 1 MONTH
STARTS '01:00:00'
DO CALL UpdateProductPopularityScores('month');

CREATE EVENT IF NOT EXISTS cleanup_old_search_logs
ON SCHEDULE EVERY 1 DAY
STARTS '02:00:00'
DO DELETE FROM search_logs WHERE search_timestamp < DATE_SUB(NOW(), INTERVAL 90 DAY);

CREATE EVENT IF NOT EXISTS cleanup_old_clickstream
ON SCHEDULE EVERY 1 DAY
STARTS '02:30:00'
DO DELETE FROM product_clickstream WHERE timestamp < DATE_SUB(NOW(), INTERVAL 90 DAY);

CREATE EVENT IF NOT EXISTS cleanup_old_recommendation_events
ON SCHEDULE EVERY 1 DAY
STARTS '03:00:00'
DO DELETE FROM recommendation_events WHERE impression_timestamp < DATE_SUB(NOW(), INTERVAL 90 DAY);

CREATE EVENT IF NOT EXISTS cleanup_expired_recommendation_cache
ON SCHEDULE EVERY 6 HOURS
DO DELETE FROM recommendation_cache WHERE expires_at < NOW();

-- Create indexes for better performance
CREATE INDEX idx_search_logs_composite ON search_logs(user_id, search_timestamp, query);
CREATE INDEX idx_search_logs_performance ON search_logs(query, results_count, click_count, conversion_count);
CREATE INDEX idx_search_clicks_composite ON search_click_events(search_log_id, product_id, click_timestamp);
CREATE INDEX idx_recommendation_events_composite ON recommendation_events(user_id, recommendation_type, algorithm, impression_timestamp);
CREATE INDEX idx_clickstream_composite ON clickstream(user_id, product_id, action_type, timestamp);
CREATE INDEX idx_behavior_composite ON user_behavior_tracking(user_id, event_type, entity_type, timestamp);
CREATE INDEX idx_trending_composite ON trending_searches(time_range, trend_score, period_start);
CREATE INDEX idx_popularity_composite ON product_popularity_scores(product_id, time_range, popularity_score, period_start);

-- Add foreign key constraints for better data integrity
ALTER TABLE search_logs ADD CONSTRAINT chk_search_logs_duration CHECK (duration_ms >= 0);
ALTER TABLE search_logs ADD CONSTRAINT chk_search_logs_results CHECK (results_count >= 0);
ALTER TABLE search_logs ADD CONSTRAINT chk_search_logs_page CHECK (page_number > 0);
ALTER TABLE search_logs ADD CONSTRAINT chk_search_logs_per_page CHECK (results_per_page > 0);

ALTER TABLE search_click_events ADD CONSTRAINT chk_search_clicks_position CHECK (position > 0);
ALTER TABLE search_click_events ADD CONSTRAINT chk_search_clicks_time CHECK (time_to_click_ms >= 0);
ALTER TABLE search_click_events ADD CONSTRAINT chk_search_clicks_conversion CHECK (conversion_value >= 0);

ALTER TABLE recommendation_events ADD CONSTRAINT chk_recommendation_position CHECK (position > 0);
ALTER TABLE recommendation_events ADD CONSTRAINT chk_recommendation_feedback CHECK (feedback_score BETWEEN 1 AND 5);

ALTER TABLE product_clickstream ADD CONSTRAINT chk_clickstream_duration CHECK (duration_ms >= 0);

ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_scroll CHECK (scroll_depth BETWEEN 0 AND 100);
ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_time CHECK (time_on_page_ms >= 0);
ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_movements CHECK (mouse_movements >= 0);
ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_clicks CHECK (clicks >= 0);
ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_keypresses CHECK (keypresses >= 0);

ALTER TABLE search_suggestions ADD CONSTRAINT chk_suggestions_score CHECK (score BETWEEN 0 AND 1);
ALTER TABLE search_suggestions ADD CONSTRAINT chk_suggestions_clicks CHECK (click_count >= 0);

ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_count CHECK (search_count >= 0);
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_clicks CHECK (click_count >= 0);
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_conversions CHECK (conversion_count >= 0);
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_score CHECK (trend_score >= 0);
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_growth CHECK (growth_rate >= -100);

ALTER TABLE recommendation_cache ADD CONSTRAINT chk_cache_hits CHECK (hit_count >= 0);

ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_scores CHECK (popularity_score >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_trending CHECK (trending_score >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_views CHECK (total_views >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_clicks CHECK (total_clicks >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_purchases CHECK (total_purchases >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_wishlist CHECK (total_wishlist_adds >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_shares CHECK (total_shares >= 0);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_revenue CHECK (revenue >= 0);

ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_searches CHECK (total_searches >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_unique CHECK (unique_searches >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_clicks CHECK (total_clicks >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_conversions CHECK (total_conversions >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_results CHECK (avg_results_per_search >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_position CHECK (avg_click_position >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_rates CHECK (conversion_rate BETWEEN 0 AND 1);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_ctr CHECK (click_through_rate BETWEEN 0 AND 1);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_duration CHECK (avg_search_duration_ms >= 0);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_failed CHECK (failed_searches >= 0);

ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_searches CHECK (total_searches >= 0);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_users CHECK (unique_users >= 0);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_results CHECK (avg_results_count >= 0);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_position CHECK (avg_click_position >= 0);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_rates CHECK (click_through_rate BETWEEN 0 AND 1);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_conversion CHECK (conversion_rate BETWEEN 0 AND 1);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_bounce CHECK (bounce_rate BETWEEN 0 AND 1);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_time CHECK (avg_time_to_click_ms >= 0);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_zero CHECK (zero_result_rate BETWEEN 0 AND 1);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_autocomplete CHECK (autocomplete_usage_rate BETWEEN 0 AND 1);
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_voice CHECK (voice_usage_rate BETWEEN 0 AND 1);

-- Add check constraints for data validation
ALTER TABLE search_logs ADD CONSTRAINT chk_search_logs_query CHECK (query IS NOT NULL AND query != '');
ALTER TABLE search_logs ADD CONSTRAINT chk_search_logs_timestamp CHECK (search_timestamp IS NOT NULL);

ALTER TABLE search_click_events ADD CONSTRAINT chk_search_clicks_search_id CHECK (search_log_id IS NOT NULL);
ALTER TABLE search_click_events ADD CONSTRAINT chk_search_clicks_product_id CHECK (product_id IS NOT NULL);
ALTER TABLE search_click_events ADD CONSTRAINT chk_search_clicks_timestamp CHECK (click_timestamp IS NOT NULL);

ALTER TABLE recommendation_events ADD CONSTRAINT chk_recommendation_type CHECK (recommendation_type IS NOT NULL);
ALTER TABLE recommendation_events ADD CONSTRAINT chk_recommendation_algorithm CHECK (algorithm IS NOT NULL);
ALTER TABLE recommendation_events ADD CONSTRAINT chk_recommendation_product_id CHECK (product_id IS NOT NULL);
ALTER TABLE recommendation_events ADD CONSTRAINT chk_recommendation_timestamp CHECK (impression_timestamp IS NOT NULL);

ALTER TABLE product_clickstream ADD CONSTRAINT chk_clickstream_action CHECK (action_type IS NOT NULL);
ALTER TABLE product_clickstream ADD CONSTRAINT chk_clickstream_source CHECK (source IS NOT NULL);
ALTER TABLE product_clickstream ADD CONSTRAINT chk_clickstream_product_id CHECK (product_id IS NOT NULL);
ALTER TABLE product_clickstream ADD CONSTRAINT chk_clickstream_timestamp CHECK (timestamp IS NOT NULL);

ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_event CHECK (event_type IS NOT NULL);
ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_entity CHECK (entity_type IS NOT NULL);
ALTER TABLE user_behavior_tracking ADD CONSTRAINT chk_behavior_timestamp CHECK (timestamp IS NOT NULL);

ALTER TABLE search_suggestions ADD CONSTRAINT chk_suggestions_query CHECK (query IS NOT NULL AND query != '');
ALTER TABLE search_suggestions ADD CONSTRAINT chk_suggestions_suggestion CHECK (suggestion IS NOT NULL AND suggestion != '');
ALTER TABLE search_suggestions ADD CONSTRAINT chk_suggestions_type CHECK (suggestion_type IS NOT NULL);

ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_query CHECK (query IS NOT NULL AND query != '');
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_time_range CHECK (time_range IS NOT NULL);
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_period CHECK (period_start IS NOT NULL AND period_end IS NOT NULL);
ALTER TABLE trending_searches ADD CONSTRAINT chk_trending_period_order CHECK (period_start <= period_end);

ALTER TABLE recommendation_cache ADD CONSTRAINT chk_cache_key CHECK (cache_key IS NOT NULL AND cache_key != '');
ALTER TABLE recommendation_cache ADD CONSTRAINT chk_cache_type CHECK (recommendation_type IS NOT NULL);
ALTER TABLE recommendation_cache ADD CONSTRAINT chk_cache_algorithm CHECK (algorithm IS NOT NULL);
ALTER TABLE recommendation_cache ADD CONSTRAINT chk_cache_expires CHECK (expires_at IS NOT NULL);
ALTER TABLE recommendation_cache ADD CONSTRAINT chk_cache_created CHECK (created_at IS NOT NULL);

ALTER TABLE user_search_preferences ADD CONSTRAINT chk_preferences_type CHECK (preference_type IS NOT NULL);
ALTER TABLE user_search_preferences ADD CONSTRAINT chk_preferences_data CHECK (preference_data IS NOT NULL);
ALTER TABLE user_search_preferences ADD CONSTRAINT chk_preferences_created CHECK (created_at IS NOT NULL);

ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_date CHECK (date IS NOT NULL);
ALTER TABLE search_analytics_summary ADD CONSTRAINT chk_summary_created CHECK (created_at IS NOT NULL);

ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_product_id CHECK (product_id IS NOT NULL);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_time_range CHECK (time_range IS NOT NULL);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_period CHECK (period_start IS NOT NULL AND period_end IS NOT NULL);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_period_order CHECK (period_start <= period_end);
ALTER TABLE product_popularity_scores ADD CONSTRAINT chk_popularity_created CHECK (created_at IS NOT NULL);

ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_query CHECK (query IS NOT NULL AND query != '');
ALTER TABLE search_performance_metrics ADD CONSTRAINT chk_performance_updated CHECK (last_updated IS NOT NULL);

-- Add full-text indexes for search optimization
CREATE FULLTEXT INDEX ft_search_logs_query ON search_logs(query);
CREATE FULLTEXT INDEX ft_search_logs_normalized ON search_logs(normalized_query);
-- Note: ft_suggestions_text already defined in table creation at line 170
CREATE FULLTEXT INDEX ft_trending_query ON trending_searches(query, normalized_query);
CREATE FULLTEXT INDEX ft_performance_query ON search_performance_metrics(query, normalized_query);

-- Add partitioning for large tables (optional for very high traffic)
-- ALTER TABLE search_logs PARTITION BY RANGE (TO_DAYS(search_timestamp)) (
--     PARTITION p_old VALUES LESS THAN (TO_DAYS('2024-01-01')),
--     PARTITION p_2024 VALUES LESS THAN (TO_DAYS('2025-01-01')),
--     PARTITION p_2025 VALUES LESS THAN (TO_DAYS('2026-01-01')),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- Create additional stored procedures for maintenance
DELIMITER //
CREATE PROCEDURE NormalizeSearchQuery(IN p_query VARCHAR(500))
BEGIN
    -- Normalize search query by removing special characters, converting to lowercase, etc.
    SELECT LOWER(TRIM(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(p_query, '.', ''), ',', ''), ';', ''), ':', ''), '''', '')));
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE CalculateSearchSimilarity(IN p_query1 VARCHAR(500), IN p_query2 VARCHAR(500))
BEGIN
    -- Calculate similarity between two search queries (simplified implementation)
    DECLARE similarity DECIMAL(5,4) DEFAULT 0.0000;
    DECLARE len1 INT DEFAULT 0;
    DECLARE len2 INT DEFAULT 0;
    DECLARE common INT DEFAULT 0;
    
    SET len1 = CHAR_LENGTH(p_query1);
    SET len2 = CHAR_LENGTH(p_query2);
    
    IF len1 > 0 AND len2 > 0 THEN
        -- Simple character-based similarity calculation
        SET common = len1 + len2 - CHAR_LENGTH(CONCAT(p_query1, p_query2));
        SET similarity = (common * 2.0) / (len1 + len2);
    END IF;
    
    SELECT similarity;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetPersonalizedRecommendations(IN p_user_id INT, IN p_limit INT, IN p_context VARCHAR(50))
BEGIN
    -- Get personalized recommendations based on user behavior
    SELECT 
        p.product_id,
        p.product_name,
        p.price,
        p.image_url,
        p.category_id,
        COALESCE(pps.popularity_score, 0) as score,
        'personalized' as recommendation_type,
        'hybrid' as algorithm
    FROM products p
    LEFT JOIN product_popularity_scores pps ON p.product_id = pps.product_id 
        AND pps.time_range = 'week'
    WHERE p.is_active = 1
    AND p.stock_quantity > 0
    ORDER BY score DESC
    LIMIT p_limit;
END//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetCollaborativeRecommendations(IN p_user_id INT, IN p_limit INT, IN p_algorithm VARCHAR(50))
BEGIN
    -- Get collaborative filtering recommendations
    -- This is a simplified implementation - in production, use more sophisticated algorithms
    SELECT 
        p.product_id,
        p.product_name,
        p.price,
        p.image_url,
        p.category_id,
        COALESCE(pps.popularity_score, 0) as score,
        'collaborative' as recommendation_type,
        p_algorithm as algorithm
    FROM products p
    LEFT JOIN product_popularity_scores pps ON p.product_id = pps.product_id 
        AND pps.time_range = 'week'
    WHERE p.is_active = 1
    AND p.stock_quantity > 0
    AND p.product_id NOT IN (
        -- Exclude products user has already purchased
        SELECT DISTINCT oi.product_id 
        FROM order_items oi
        JOIN orders o ON oi.order_id = o.order_id
        WHERE o.user_id = p_user_id
    )
    ORDER BY score DESC
    LIMIT p_limit;
END//
DELIMITER ;

-- Create functions for common calculations
DELIMITER //
CREATE FUNCTION GetSearchSuccessRate(p_query VARCHAR(500)) RETURNS DECIMAL(5,4)
DETERMINISTIC
BEGIN
    DECLARE success_rate DECIMAL(5,4) DEFAULT 0.0000;
    DECLARE total_searches INT DEFAULT 0;
    DECLARE successful_searches INT DEFAULT 0;
    
    SELECT COUNT(*), SUM(CASE WHEN results_count > 0 AND NOT is_failed THEN 1 ELSE 0 END)
    INTO total_searches, successful_searches
    FROM search_logs
    WHERE normalized_query = LOWER(TRIM(p_query));
    
    IF total_searches > 0 THEN
        SET success_rate = successful_searches * 1.0 / total_searches;
    END IF;
    
    RETURN success_rate;
END//
DELIMITER ;

DELIMITER //
CREATE FUNCTION GetProductTrendScore(p_product_id INT) RETURNS DECIMAL(10,6)
DETERMINISTIC
BEGIN
    DECLARE trend_score DECIMAL(10,6) DEFAULT 0.000000;
    
    SELECT COALESCE(trending_score, 0)
    INTO trend_score
    FROM product_popularity_scores
    WHERE product_id = p_product_id
    AND time_range = 'week'
    ORDER BY period_end DESC
    LIMIT 1;
    
    RETURN trend_score;
END//
DELIMITER ;

-- Insert initial data for trending searches and recommendations
INSERT IGNORE INTO search_suggestions (query, suggestion, suggestion_type, score, click_count) VALUES
('dress', 'dress', 'popular', 0.8500, 1250),
('shirt', 'shirt', 'popular', 0.8200, 980),
('jeans', 'jeans', 'popular', 0.7900, 890),
('shoes', 'shoes', 'popular', 0.8800, 1450),
('bag', 'bag', 'popular', 0.7500, 670),
('watch', 'watch', 'popular', 0.7200, 540),
('jacket', 'jacket', 'trending', 0.6800, 420),
('skirt', 'skirt', 'trending', 0.6500, 380),
('sneakers', 'sneakers', 'trending', 0.9000, 1680),
('sunglasses', 'sunglasses', 'trending', 0.7000, 560);

-- Create default user search preferences for existing users
INSERT IGNORE INTO user_search_preferences (user_id, preference_type, preference_data)
SELECT 
    user_id,
    'search_history',
    JSON_OBJECT('enabled', true, 'max_items', 10, 'retention_days', 30)
FROM users
WHERE user_id IS NOT NULL;

INSERT IGNORE INTO user_search_preferences (user_id, preference_type, preference_data)
SELECT 
    user_id,
    'recommendation_algorithm',
    JSON_OBJECT('homepage', 'hybrid', 'pdp', 'collaborative', 'cart', 'content_based', 'checkout', 'trending')
FROM users
WHERE user_id IS NOT NULL;

-- Create indexes for JSON fields (MySQL 5.7+)
CREATE INDEX idx_search_logs_filters ON search_logs((CAST(filters AS CHAR(255) ARRAY)));
CREATE INDEX idx_recommendation_events_context ON recommendation_events((CAST(context AS CHAR(255) ARRAY)));
CREATE INDEX idx_clickstream_source_context ON product_clickstream((CAST(source_context AS CHAR(255) ARRAY)));
CREATE INDEX idx_behavior_device_info ON user_behavior_tracking((CAST(device_info AS CHAR(255) ARRAY)));
CREATE INDEX idx_preferences_data ON user_search_preferences((CAST(preference_data AS CHAR(255) ARRAY)));

-- Create additional views for analytics
CREATE OR REPLACE VIEW user_search_patterns AS
SELECT 
    u.user_id,
    u.username,
    COUNT(sl.search_log_id) as total_searches,
    COUNT(DISTINCT sl.normalized_query) as unique_queries,
    AVG(sl.results_count) as avg_results,
    AVG(sl.duration_ms) as avg_duration,
    SUM(sl.click_count) as total_clicks,
    SUM(sl.conversion_count) as total_conversions,
    (SUM(sl.conversion_count) * 100.0 / COUNT(sl.search_log_id)) as conversion_rate,
    MAX(sl.search_timestamp) as last_search_date
FROM users u
LEFT JOIN search_logs sl ON u.user_id = sl.user_id
GROUP BY u.user_id, u.username;

CREATE OR REPLACE VIEW product_recommendation_performance AS
SELECT 
    p.product_id,
    p.product_name,
    c.category_name,
    COUNT(CASE WHEN re.recommendation_type = 'homepage' THEN 1 END) as homepage_impressions,
    COUNT(CASE WHEN re.recommendation_type = 'pdp' THEN 1 END) as pdp_impressions,
    COUNT(CASE WHEN re.recommendation_type = 'cart' THEN 1 END) as cart_impressions,
    COUNT(CASE WHEN re.recommendation_type = 'checkout' THEN 1 END) as checkout_impressions,
    COUNT(re.click_timestamp) as total_clicks,
    COUNT(re.conversion_timestamp) as total_conversions,
    AVG(re.feedback_score) as avg_feedback_score,
    (COUNT(re.click_timestamp) * 100.0 / COUNT(*)) as overall_ctr,
    (COUNT(re.conversion_timestamp) * 100.0 / COUNT(*)) as overall_conversion_rate
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN recommendation_events re ON p.product_id = re.product_id
GROUP BY p.product_id, p.product_name, c.category_name;
