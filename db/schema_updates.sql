-- FashionStore Navigation System Schema Updates
-- For Amazon/Flipkart-style e-commerce navigation

-- 1. Recent Searches Table
CREATE TABLE IF NOT EXISTS recent_searches (
    search_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    query VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    result_count INT DEFAULT 0,
    searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    INDEX idx_recent_searches_user (user_id),
    INDEX idx_recent_searches_query (query),
    INDEX idx_recent_searches_date (searched_at)
);

-- 2. Search Suggestions Table
CREATE TABLE IF NOT EXISTS search_suggestions (
    suggestion_id INT AUTO_INCREMENT PRIMARY KEY,
    query VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    type ENUM('product', 'category', 'brand', 'trending') DEFAULT 'product',
    popularity INT DEFAULT 0,
    image_url VARCHAR(500),
    product_url VARCHAR(500),
    product_id INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE SET NULL,
    INDEX idx_search_suggestions_query (query),
    INDEX idx_search_suggestions_type (type),
    INDEX idx_search_suggestions_popularity (popularity),
    INDEX idx_search_suggestions_category (category)
);

-- 3. Saved Locations Table
CREATE TABLE IF NOT EXISTS saved_locations (
    location_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    pincode VARCHAR(10) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) DEFAULT 'India',
    area VARCHAR(255),
    landmark VARCHAR(255),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_default BOOLEAN DEFAULT FALSE,
    is_serviceable BOOLEAN DEFAULT TRUE,
    estimated_delivery_days INT DEFAULT 3,
    delivery_time_slot VARCHAR(50) DEFAULT '9 AM - 6 PM',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_location (user_id, pincode, area),
    INDEX idx_saved_locations_user (user_id),
    INDEX idx_saved_locations_pincode (pincode),
    INDEX idx_saved_locations_default (user_id, is_default)
);

-- 4. Serviceable Pincodes Table
CREATE TABLE IF NOT EXISTS serviceable_pincodes (
    pincode_id INT AUTO_INCREMENT PRIMARY KEY,
    pincode VARCHAR(10) NOT NULL UNIQUE,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    country VARCHAR(100) DEFAULT 'India',
    estimated_delivery_days INT DEFAULT 3,
    delivery_time_slot VARCHAR(50) DEFAULT '9 AM - 6 PM',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_serviceable_pincodes_pincode (pincode),
    INDEX idx_serviceable_pincodes_city (city)
);

-- 5. User Session Tracking Table (for search analytics)
CREATE TABLE IF NOT EXISTS user_search_analytics (
    analytics_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255),
    user_id INT,
    query VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    result_count INT DEFAULT 0,
    ip_address VARCHAR(45),
    user_agent TEXT,
    searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_search_analytics_session (session_id),
    INDEX idx_search_analytics_user (user_id),
    INDEX idx_search_analytics_query (query),
    INDEX idx_search_analytics_date (searched_at)
);

-- 6. Navigation Preferences Table
CREATE TABLE IF NOT EXISTS navigation_preferences (
    preference_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    default_pincode VARCHAR(10),
    preferred_delivery_time_slot VARCHAR(50),
    show_suggestions BOOLEAN DEFAULT TRUE,
    enable_search_history BOOLEAN DEFAULT TRUE,
    auto_detect_location BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_preferences (user_id)
);

-- Insert initial data for serviceable pincodes
INSERT IGNORE INTO serviceable_pincodes (pincode, city, state, estimated_delivery_days, delivery_time_slot) VALUES
('110001', 'New Delhi', 'Delhi', 2, '9 AM - 8 PM'),
('110002', 'New Delhi', 'Delhi', 2, '9 AM - 8 PM'),
('110003', 'New Delhi', 'Delhi', 2, '9 AM - 8 PM'),
('110004', 'New Delhi', 'Delhi', 2, '9 AM - 8 PM'),
('110005', 'New Delhi', 'Delhi', 2, '9 AM - 8 PM'),
('400001', 'Mumbai', 'Maharashtra', 2, '9 AM - 8 PM'),
('400002', 'Mumbai', 'Maharashtra', 2, '9 AM - 8 PM'),
('400003', 'Mumbai', 'Maharashtra', 2, '9 AM - 8 PM'),
('400004', 'Mumbai', 'Maharashtra', 2, '9 AM - 8 PM'),
('400005', 'Mumbai', 'Maharashtra', 2, '9 AM - 8 PM'),
('560001', 'Bangalore', 'Karnataka', 2, '9 AM - 8 PM'),
('560002', 'Bangalore', 'Karnataka', 2, '9 AM - 8 PM'),
('560003', 'Bangalore', 'Karnataka', 2, '9 AM - 8 PM'),
('560004', 'Bangalore', 'Karnataka', 2, '9 AM - 8 PM'),
('560005', 'Bangalore', 'Karnataka', 2, '9 AM - 8 PM'),
('600001', 'Chennai', 'Tamil Nadu', 3, '10 AM - 7 PM'),
('600002', 'Chennai', 'Tamil Nadu', 3, '10 AM - 7 PM'),
('600003', 'Chennai', 'Tamil Nadu', 3, '10 AM - 7 PM'),
('600004', 'Chennai', 'Tamil Nadu', 3, '10 AM - 7 PM'),
('600005', 'Chennai', 'Tamil Nadu', 3, '10 AM - 7 PM'),
('700001', 'Kolkata', 'West Bengal', 3, '10 AM - 7 PM'),
('700002', 'Kolkata', 'West Bengal', 3, '10 AM - 7 PM'),
('700003', 'Kolkata', 'West Bengal', 3, '10 AM - 7 PM'),
('700004', 'Kolkata', 'West Bengal', 3, '10 AM - 7 PM'),
('700005', 'Kolkata', 'West Bengal', 3, '10 AM - 7 PM'),
('500001', 'Hyderabad', 'Telangana', 3, '10 AM - 7 PM'),
('500002', 'Hyderabad', 'Telangana', 3, '10 AM - 7 PM'),
('500003', 'Hyderabad', 'Telangana', 3, '10 AM - 7 PM'),
('500004', 'Hyderabad', 'Telangana', 3, '10 AM - 7 PM'),
('500005', 'Hyderabad', 'Telangana', 3, '10 AM - 7 PM'),
('380001', 'Ahmedabad', 'Gujarat', 3, '10 AM - 6 PM'),
('380002', 'Ahmedabad', 'Gujarat', 3, '10 AM - 6 PM'),
('380003', 'Ahmedabad', 'Gujarat', 3, '10 AM - 6 PM'),
('380004', 'Ahmedabad', 'Gujarat', 3, '10 AM - 6 PM'),
('380005', 'Ahmedabad', 'Gujarat', 3, '10 AM - 6 PM');

-- Insert initial search suggestions
INSERT IGNORE INTO search_suggestions (query, category, type, popularity) VALUES
('t-shirt', 'clothing', 'product', 150),
('jeans', 'clothing', 'product', 120),
('sneakers', 'footwear', 'product', 100),
('dress', 'clothing', 'product', 90),
('jacket', 'clothing', 'product', 80),
('handbag', 'accessories', 'product', 70),
('watch', 'accessories', 'product', 60),
('shirt', 'clothing', 'product', 50),
('boots', 'footwear', 'product', 45),
('trousers', 'clothing', 'product', 40),
('skirt', 'clothing', 'product', 35),
('coat', 'clothing', 'product', 30),
('sandals', 'footwear', 'product', 28),
('wallet', 'accessories', 'product', 25),
('belt', 'accessories', 'product', 22),
('sunglasses', 'accessories', 'product', 20),
('men', 'category', 'category', 200),
('women', 'category', 'category', 180),
('kids', 'category', 'category', 80),
('footwear', 'category', 'category', 150),
('accessories', 'category', 'category', 120),
('bags', 'category', 'category', 100),
('watches', 'category', 'category', 90),
('jewelry', 'category', 'category', 70),
('Nike', 'brand', 'brand', 95),
('Adidas', 'brand', 'brand', 85),
('Puma', 'brand', 'brand', 75),
('Zara', 'brand', 'brand', 90),
('H&M', 'brand', 'brand', 80),
('new arrivals', 'trending', 'trending', 110),
('sale', 'trending', 'trending', 100),
('summer collection', 'trending', 'trending', 85),
('winter wear', 'trending', 'trending', 75),
('formal wear', 'trending', 'trending', 65),
('casual wear', 'trending', 'trending', 60);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_recent_searches_user_date ON recent_searches(user_id, searched_at DESC);
CREATE INDEX IF NOT EXISTS idx_search_suggestions_type_popularity ON search_suggestions(type, popularity DESC);
CREATE INDEX IF NOT EXISTS idx_saved_locations_user_default ON saved_locations(user_id, is_default);
CREATE INDEX IF NOT EXISTS idx_serviceable_pincodes_active ON serviceable_pincodes(is_active);

-- Create views for common queries
CREATE OR REPLACE VIEW trending_searches_view AS
SELECT query, SUM(popularity) as total_popularity, type
FROM search_suggestions 
WHERE is_active = TRUE 
GROUP BY query, type 
ORDER BY total_popularity DESC 
LIMIT 20;

CREATE OR REPLACE VIEW user_search_summary_view AS
SELECT 
    u.user_id,
    u.full_name,
    COUNT(rs.search_id) as total_searches,
    COUNT(DISTINCT rs.query) as unique_queries,
    MAX(rs.searched_at) as last_searched
FROM users u
LEFT JOIN recent_searches rs ON u.user_id = rs.user_id
WHERE rs.is_active = TRUE
GROUP BY u.user_id, u.full_name
ORDER BY total_searches DESC;
