-- FashionStore Footer Ecosystem Schema Updates
-- For enterprise-grade footer and legal infrastructure

-- 1. CMS Pages Table
CREATE TABLE IF NOT EXISTS cms_pages (
    page_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    slug VARCHAR(255) NOT NULL UNIQUE,
    content LONGTEXT,
    meta_title VARCHAR(255),
    meta_description TEXT,
    meta_keywords VARCHAR(500),
    status ENUM('published', 'draft', 'archived') DEFAULT 'draft',
    template VARCHAR(100) DEFAULT 'default',
    author_id INT,
    author_name VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    published_at TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    view_count INT DEFAULT 0,
    featured_image VARCHAR(500),
    excerpt TEXT,
    FOREIGN KEY (author_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_cms_pages_slug (slug),
    INDEX idx_cms_pages_status (status),
    INDEX idx_cms_pages_template (template),
    INDEX idx_cms_pages_published (published_at),
    INDEX idx_cms_pages_active (is_active)
);

-- 2. Footer Links Table
CREATE TABLE IF NOT EXISTS footer_links (
    link_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    section ENUM('get-to-know-us', 'connect-with-us', 'make-money-with-us', 'customer-support', 'legal-policies', 'membership', 'social-media') NOT NULL,
    target ENUM('_self', '_blank') DEFAULT '_self',
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    click_count INT DEFAULT 0,
    INDEX idx_footer_links_section (section),
    INDEX idx_footer_links_active (is_active),
    INDEX idx_footer_links_sort (sort_order)
);

-- 3. Newsletter Subscriptions Table
CREATE TABLE IF NOT EXISTS newsletters (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    preferences JSON,
    status ENUM('active', 'unsubscribed', 'bounced') DEFAULT 'active',
    subscription_token VARCHAR(255) UNIQUE,
    subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    unsubscribed_at TIMESTAMP NULL,
    last_email_sent TIMESTAMP NULL,
    email_count INT DEFAULT 0,
    open_count INT DEFAULT 0,
    click_count INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    source ENUM('footer', 'popup', 'checkout', 'registration', 'profile') DEFAULT 'footer',
    INDEX idx_newsletters_email (email),
    INDEX idx_newsletters_status (status),
    INDEX idx_newsletters_active (is_active),
    INDEX idx_newsletters_token (subscription_token)
);

-- 4. Newsletter Campaigns Table
CREATE TABLE IF NOT EXISTS newsletter_campaigns (
    campaign_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    content_html LONGTEXT,
    content_text TEXT,
    preview_text VARCHAR(500),
    status ENUM('draft', 'scheduled', 'sent', 'paused') DEFAULT 'draft',
    scheduled_at TIMESTAMP NULL,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by INT,
    total_sent INT DEFAULT 0,
    total_opened INT DEFAULT 0,
    total_clicked INT DEFAULT 0,
    total_unsubscribed INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_campaigns_status (status),
    INDEX idx_campaigns_sent (sent_at),
    INDEX idx_campaigns_active (is_active)
);

-- 5. Newsletter Analytics Table
CREATE TABLE IF NOT EXISTS newsletter_analytics (
    analytics_id INT AUTO_INCREMENT PRIMARY KEY,
    campaign_id INT,
    subscription_id INT,
    event_type ENUM('sent', 'delivered', 'opened', 'clicked', 'bounced', 'unsubscribed', 'complained') NOT NULL,
    event_data JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    event_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (campaign_id) REFERENCES newsletter_campaigns(campaign_id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES newsletters(subscription_id) ON DELETE CASCADE,
    INDEX idx_analytics_campaign (campaign_id),
    INDEX idx_analytics_subscription (subscription_id),
    INDEX idx_analytics_event (event_type),
    INDEX idx_analytics_timestamp (event_timestamp)
);

-- 6. Footer Link Analytics Table
CREATE TABLE IF NOT EXISTS footer_link_analytics (
    analytics_id INT AUTO_INCREMENT PRIMARY KEY,
    link_id INT,
    user_id INT,
    session_id VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    referrer VARCHAR(500),
    clicked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (link_id) REFERENCES footer_links(link_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    INDEX idx_footer_analytics_link (link_id),
    INDEX idx_footer_analytics_user (user_id),
    INDEX idx_footer_analytics_session (session_id),
    INDEX idx_footer_analytics_clicked (clicked_at)
);

-- 7. Social Media Links Table
CREATE TABLE IF NOT EXISTS social_media_links (
    social_id INT AUTO_INCREMENT PRIMARY KEY,
    platform ENUM('facebook', 'twitter', 'instagram', 'linkedin', 'youtube', 'pinterest', 'tiktok', 'whatsapp') NOT NULL,
    url VARCHAR(500) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    icon_class VARCHAR(255),
    color VARCHAR(50),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    click_count INT DEFAULT 0,
    INDEX idx_social_platform (platform),
    INDEX idx_social_active (is_active),
    INDEX idx_social_sort (sort_order)
);

-- 8. Payment Methods Table
CREATE TABLE IF NOT EXISTS payment_methods (
    method_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    icon_url VARCHAR(500),
    icon_class VARCHAR(255),
    type ENUM('card', 'wallet', 'bank', 'upi', 'crypto') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_payment_type (type),
    INDEX idx_payment_active (is_active),
    INDEX idx_payment_sort (sort_order)
);

-- 9. Trust Badges Table
CREATE TABLE IF NOT EXISTS trust_badges (
    badge_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    display_name VARCHAR(255),
    description TEXT,
    image_url VARCHAR(500),
    icon_class VARCHAR(255),
    type ENUM('security', 'payment', 'shipping', 'quality', 'certification') NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    sort_order INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trust_type (type),
    INDEX idx_trust_active (is_active),
    INDEX idx_trust_sort (sort_order)
);

-- 10. Legal Documents Table
CREATE TABLE IF NOT EXISTS legal_documents (
    document_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type ENUM('privacy-policy', 'terms-of-service', 'cookie-policy', 'refund-policy', 'shipping-policy', 'accessibility', 'security', 'gdpr', 'ccpa') NOT NULL,
    version VARCHAR(50) NOT NULL,
    content LONGTEXT,
    effective_date DATE NOT NULL,
    expiry_date DATE,
    status ENUM('draft', 'active', 'archived') DEFAULT 'draft',
    created_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_current BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL,
    UNIQUE KEY uk_legal_type_version (type, version),
    INDEX idx_legal_type (type),
    INDEX idx_legal_status (status),
    INDEX idx_legal_effective (effective_date),
    INDEX idx_legal_current (is_current)
);

-- Insert initial CMS pages
INSERT IGNORE INTO cms_pages (title, slug, content, meta_title, meta_description, status, template) VALUES
('About Us', 'about-us', 
'<h1>About FashionStore</h1><p>FashionStore is your premier destination for modern fashion and style...</p>', 
'About FashionStore - Modern Fashion Marketplace', 
'Learn about FashionStore''s mission, vision, and commitment to quality fashion.', 
'published', 'about'),

('Careers', 'careers', 
'<h1>Careers at FashionStore</h1><p>Join our team and build your career in fashion...</p>', 
'Careers - FashionStore Jobs', 
'Explore career opportunities at FashionStore and join our growing team.', 
'published', 'default'),

('Investor Relations', 'investor-relations', 
'<h1>Investor Relations</h1><p>FashionStore is committed to transparency with our investors...</p>', 
'Investor Relations - FashionStore', 
'Financial information and investor resources for FashionStore.', 
'published', 'default'),

('Privacy Policy', 'privacy-policy', 
'<h1>Privacy Policy</h1><p>Your privacy is important to us. This policy explains how we collect, use, and protect your information...</p>', 
'Privacy Policy - FashionStore', 
'FashionStore''s privacy policy and how we protect your data.', 
'published', 'policy'),

('Terms of Service', 'terms-of-service', 
'<h1>Terms of Service</h1><p>These terms govern your use of FashionStore''s services...</p>', 
'Terms of Service - FashionStore', 
'FashionStore''s terms of service and user agreement.', 
'published', 'policy'),

('Cookie Policy', 'cookie-policy', 
'<h1>Cookie Policy</h1><p>This policy explains how FashionStore uses cookies...</p>', 
'Cookie Policy - FashionStore', 
'How FashionStore uses cookies and tracking technologies.', 
'published', 'policy'),

('Refund Policy', 'refund-policy', 
'<h1>Refund Policy</h1><p>Our refund and return policy for customer satisfaction...</p>', 
'Refund Policy - FashionStore', 
'FashionStore''s refund and return policy for customer satisfaction.', 
'published', 'policy'),

('Shipping Policy', 'shipping-policy', 
'<h1>Shipping Policy</h1><p>Information about shipping and delivery...</p>', 
'Shipping Policy - FashionStore', 
'FashionStore''s shipping policy and delivery information.', 
'published', 'policy'),

('Accessibility', 'accessibility', 
'<h1>Accessibility Statement</h1><p>FashionStore is committed to accessibility for all users...</p>', 
'Accessibility - FashionStore', 
'FashionStore''s commitment to accessibility for all users.', 
'published', 'policy'),

('Security', 'security', 
'<h1>Security Statement</h1><p>How FashionStore protects your data and ensures secure shopping...</p>', 
'Security - FashionStore', 
'How FashionStore protects your data and ensures secure shopping.', 
'published', 'policy'),

('Advertise With Us', 'advertise-with-us', 
'<h1>Advertise With FashionStore</h1><p>Partner with us and reach our fashion-conscious audience...</p>', 
'Advertise With Us - FashionStore', 
'Partner with FashionStore and reach our fashion-conscious audience.', 
'published', 'default');

-- Insert initial footer links
INSERT IGNORE INTO footer_links (title, url, section, sort_order) VALUES
-- Get to Know Us
('About Us', '/policy/about-us', 'get-to-know-us', 1),
('Careers', '/policy/careers', 'get-to-know-us', 2),
('Blog', '/blog', 'get-to-know-us', 3),
('Investor Relations', '/policy/investor-relations', 'get-to-know-us', 4),
('Press Center', '/press', 'get-to-know-us', 5),

-- Connect With Us
('Facebook', 'https://facebook.com/fashionstore', 'connect-with-us', 1),
('Instagram', 'https://instagram.com/fashionstore', 'connect-with-us', 2),
('Twitter', 'https://twitter.com/fashionstore', 'connect-with-us', 3),
('LinkedIn', 'https://linkedin.com/company/fashionstore', 'connect-with-us', 4),
('YouTube', 'https://youtube.com/fashionstore', 'connect-with-us', 5),

-- Make Money With Us
('Sell on FashionStore', '/sell', 'make-money-with-us', 1),
('Advertise With Us', '/policy/advertise-with-us', 'make-money-with-us', 2),
('Affiliate Program', '/affiliate', 'make-money-with-us', 3),
('Become a Partner', '/partner', 'make-money-with-us', 4),

-- Customer Support
('Help Center', '/help', 'customer-support', 1),
('Contact Us', '/contact', 'customer-support', 2),
('Track Order', '/track', 'customer-support', 3),
('Returns & Refunds', '/returns', 'customer-support', 4),
('Shipping Info', '/policy/shipping-policy', 'customer-support', 5),
('FAQ', '/faq', 'customer-support', 6),

-- Legal & Policies
('Privacy Policy', '/policy/privacy-policy', 'legal-policies', 1),
('Terms of Service', '/policy/terms-of-service', 'legal-policy', 2),
('Cookie Policy', '/policy/cookie-policy', 'legal-policies', 3),
('Refund Policy', '/policy/refund-policy', 'legal-policies', 4),
('Shipping Policy', '/policy/shipping-policy', 'legal-policies', 5),
('Accessibility', '/policy/accessibility', 'legal-policies', 6),
('Security', '/policy/security', 'legal-policies', 7),
('Sitemap', '/policy/sitemap', 'legal-policies', 8),

-- Membership
('FashionStore Premium', '/premium', 'membership', 1),
('Membership Benefits', '/membership/benefits', 'membership', 2),
('Loyalty Program', '/loyalty', 'membership', 3),
('Gift Cards', '/gift-cards', 'membership', 4);

-- Insert social media links
INSERT IGNORE INTO social_media_links (platform, url, display_name, description, icon_class, color, sort_order) VALUES
('facebook', 'https://facebook.com/fashionstore', 'Facebook', 'Follow us on Facebook for updates and exclusive offers', 'fab fa-facebook', '#1877f2', 1),
('twitter', 'https://twitter.com/fashionstore', 'Twitter', 'Follow us on Twitter for real-time updates', 'fab fa-twitter', '#1da1f2', 2),
('instagram', 'https://instagram.com/fashionstore', 'Instagram', 'Follow us on Instagram for fashion inspiration', 'fab fa-instagram', '#e4405f', 3),
('linkedin', 'https://linkedin.com/company/fashionstore', 'LinkedIn', 'Connect with us on LinkedIn', 'fab fa-linkedin', '#0077b5', 4),
('youtube', 'https://youtube.com/fashionstore', 'YouTube', 'Subscribe to our YouTube channel for fashion videos', 'fab fa-youtube', '#ff0000', 5),
('pinterest', 'https://pinterest.com/fashionstore', 'Pinterest', 'Follow us on Pinterest for style ideas', 'fab fa-pinterest', '#bd081c', 6);

-- Insert payment methods
INSERT IGNORE INTO payment_methods (name, display_name, description, icon_class, type, sort_order) VALUES
('visa', 'Visa', 'Pay with your Visa card', 'fab fa-cc-visa', 'card', 1),
('mastercard', 'Mastercard', 'Pay with your Mastercard', 'fab fa-cc-mastercard', 'card', 2),
('amex', 'American Express', 'Pay with your American Express card', 'fab fa-cc-amex', 'card', 3),
('rupay', 'RuPay', 'Pay with your RuPay card', 'fas fa-credit-card', 'card', 4),
('upi', 'UPI', 'Pay with UPI apps', 'fas fa-mobile-alt', 'upi', 5),
('paytm', 'Paytm', 'Pay with Paytm Wallet', 'fas fa-wallet', 'wallet', 6),
('phonepe', 'PhonePe', 'Pay with PhonePe', 'fas fa-wallet', 'wallet', 7),
('googlepay', 'Google Pay', 'Pay with Google Pay', 'fab fa-google-pay', 'wallet', 8),
('netbanking', 'Net Banking', 'Pay with your bank account', 'fas fa-university', 'bank', 9),
('cod', 'Cash on Delivery', 'Pay when you receive', 'fas fa-money-bill-wave', 'bank', 10);

-- Insert trust badges
INSERT IGNORE INTO trust_badges (name, display_name, description, icon_class, type, sort_order) VALUES
('ssl', 'SSL Secured', 'All transactions are secured with SSL encryption', 'fas fa-lock', 'security', 1),
('safe', 'Safe Shopping', '100% secure shopping guarantee', 'fas fa-shield-alt', 'security', 2),
('authentic', 'Authentic Products', '100% authentic products guarantee', 'fas fa-certificate', 'quality', 3),
('fast-shipping', 'Fast Shipping', 'Fast and reliable shipping', 'fas fa-shipping-fast', 'shipping', 4),
('easy-returns', 'Easy Returns', 'Hassle-free returns and refunds', 'fas fa-undo', 'shipping', 5),
('support', '24/7 Support', 'Round the clock customer support', 'fas fa-headset', 'quality', 6),
('payment', 'Secure Payments', 'Multiple secure payment options', 'fas fa-credit-card', 'payment', 7);

-- Insert legal documents
INSERT IGNORE INTO legal_documents (title, type, version, content, effective_date, status, is_current) VALUES
('Privacy Policy', 'privacy-policy', '1.0', 
'<h1>Privacy Policy</h1><p>Your privacy is important to us...</p>', 
'2024-01-01', 'active', TRUE),

('Terms of Service', 'terms-of-service', '1.0', 
'<h1>Terms of Service</h1><p>These terms govern your use...</p>', 
'2024-01-01', 'active', TRUE),

('Cookie Policy', 'cookie-policy', '1.0', 
'<h1>Cookie Policy</h1><p>This policy explains how we use cookies...</p>', 
'2024-01-01', 'active', TRUE),

('Refund Policy', 'refund-policy', '1.0', 
'<h1>Refund Policy</h1><p>Our refund and return policy...</p>', 
'2024-01-01', 'active', TRUE),

('Shipping Policy', 'shipping-policy', '1.0', 
'<h1>Shipping Policy</h1><p>Information about shipping...</p>', 
'2024-01-01', 'active', TRUE);

-- Create views for common queries
CREATE OR REPLACE VIEW footer_sections_view AS
SELECT 
    section,
    COUNT(*) as link_count,
    GROUP_CONCAT(title ORDER BY sort_order SEPARATOR ', ') as links
FROM footer_links 
WHERE is_active = TRUE 
GROUP BY section 
ORDER BY section;

CREATE OR REPLACE VIEW newsletter_stats_view AS
SELECT 
    COUNT(*) as total_subscribers,
    SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) as active_subscribers,
    SUM(CASE WHEN status = 'unsubscribed' THEN 1 ELSE 0 END) as unsubscribed_subscribers,
    SUM(email_count) as total_emails_sent,
    SUM(open_count) as total_opens,
    SUM(click_count) as total_clicks,
    AVG(CASE WHEN email_count > 0 THEN (open_count * 100.0 / email_count) ELSE 0 END) as avg_open_rate,
    AVG(CASE WHEN open_count > 0 THEN (click_count * 100.0 / open_count) ELSE 0 END) as avg_click_rate
FROM newsletters;

CREATE OR REPLACE VIEW cms_pages_published_view AS
SELECT 
    page_id,
    title,
    slug,
    meta_title,
    meta_description,
    template,
    view_count,
    published_at,
    updated_at
FROM cms_pages 
WHERE status = 'published' AND is_active = TRUE 
ORDER BY published_at DESC;

-- Create indexes for better performance
CREATE INDEX idx_newsletter_analytics_campaign_event ON newsletter_analytics(campaign_id, event_type);
CREATE INDEX idx_footer_link_analytics_link_date ON footer_link_analytics(link_id, clicked_at);
CREATE INDEX idx_cms_pages_view_count ON cms_pages(view_count DESC);
CREATE INDEX idx_newsletters_subscribed ON newsletters(subscribed_at DESC);

-- Add foreign key constraints for better data integrity
ALTER TABLE footer_links ADD CONSTRAINT fk_footer_links_section 
CHECK (section IN ('get-to-know-us', 'connect-with-us', 'make-money-with-us', 'customer-support', 'legal-policies', 'membership', 'social-media'));

ALTER TABLE payment_methods ADD CONSTRAINT fk_payment_methods_type 
CHECK (type IN ('card', 'wallet', 'bank', 'upi', 'crypto'));

ALTER TABLE trust_badges ADD CONSTRAINT fk_trust_badges_type 
CHECK (type IN ('security', 'payment', 'shipping', 'quality', 'certification'));

ALTER TABLE legal_documents ADD CONSTRAINT fk_legal_documents_type 
CHECK (type IN ('privacy-policy', 'terms-of-service', 'cookie-policy', 'refund-policy', 'shipping-policy', 'accessibility', 'security', 'gdpr', 'ccpa'));
