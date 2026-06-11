-- FashionStore Database Indexes for Production
-- This script adds critical indexes to optimize query performance
-- Run this on your production database after schema migration

-- ============================================================
-- USER TABLE INDEXES
-- ============================================================

-- Index for email lookups (login, registration)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index for role-based queries (admin vs customer)
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Composite index for active users
CREATE INDEX IF NOT EXISTS idx_users_active ON users(role, status);

-- ============================================================
-- PRODUCT TABLE INDEXES
-- ============================================================

-- Index for product category filtering
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);

-- Index for product availability filtering
CREATE INDEX IF NOT EXISTS idx_products_availability ON products(is_available);

-- Composite index for category + availability (common query pattern)
CREATE INDEX IF NOT EXISTS idx_products_category_avail ON products(category_id, is_available);

-- Index for product name search (for search functionality)
CREATE INDEX IF NOT EXISTS idx_products_name ON products(name);

-- Index for price range queries
CREATE INDEX IF NOT EXISTS idx_products_price ON products(price);

-- ============================================================
-- CART ITEMS TABLE INDEXES
-- ============================================================

-- Critical index for user's cart lookups (most frequent query)
CREATE INDEX IF NOT EXISTS idx_cart_items_user ON cart_items(user_id);

-- Composite index for user + product (check if product already in cart)
CREATE INDEX IF NOT EXISTS idx_cart_items_user_product ON cart_items(user_id, product_id);

-- Index for cart item deletion
CREATE INDEX IF NOT EXISTS idx_cart_items_id ON cart_items(cart_item_id);

-- ============================================================
-- ORDER TABLE INDEXES
-- ============================================================

-- Critical index for user's order history (most frequent query)
CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id);

-- Index for order status filtering (admin dashboard)
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- Index for order date filtering (reports, analytics)
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);

-- Composite index for user + status (user's active orders)
CREATE INDEX IF NOT EXISTS idx_orders_user_status ON orders(user_id, status);

-- Index for order ID lookups
CREATE INDEX IF NOT EXISTS idx_orders_id ON orders(order_id);

-- ============================================================
-- ORDER ITEMS TABLE INDEXES
-- ============================================================

-- Critical index for order item lookups
CREATE INDEX IF NOT EXISTS idx_order_items_order ON order_items(order_id);

-- Index for product sales analytics
CREATE INDEX IF NOT EXISTS idx_order_items_product ON order_items(product_id);

-- ============================================================
-- WISHLIST TABLE INDEXES
-- ============================================================

-- Index for user's wishlist lookups
CREATE INDEX IF NOT EXISTS idx_wishlist_user ON wishlist(user_id);

-- Composite index for user + product (check if product already in wishlist)
CREATE INDEX IF NOT EXISTS idx_wishlist_user_product ON wishlist(user_id, product_id);

-- ============================================================
-- ADDRESS TABLE INDEXES
-- ============================================================

-- Index for user's address lookups (checkout)
CREATE INDEX IF NOT EXISTS idx_address_user ON addresses(user_id);

-- Index for address type filtering (billing vs shipping)
CREATE INDEX IF NOT EXISTS idx_address_type ON addresses(address_type);

-- ============================================================
-- REVIEWS TABLE INDEXES
-- ============================================================

-- Index for product reviews (product page)
CREATE INDEX IF NOT EXISTS idx_reviews_product ON reviews(product_id);

-- Index for user's reviews (account page)
CREATE INDEX IF NOT EXISTS idx_reviews_user ON reviews(user_id);

-- Composite index for approved reviews
CREATE INDEX IF NOT EXISTS idx_reviews_product_approved ON reviews(product_id, is_approved);

-- ============================================================
-- COUPONS TABLE INDEXES
-- ============================================================

-- Unique index for coupon code (prevent duplicates)
CREATE UNIQUE INDEX IF NOT EXISTS idx_coupons_code ON coupons(coupon_code);

-- Index for active coupons
CREATE INDEX IF NOT EXISTS idx_coupons_active ON coupons(is_active, expiry_date);

-- ============================================================
-- INVENTORY TABLE INDEXES
-- ============================================================

-- Critical index for product stock checks (add to cart)
CREATE INDEX IF NOT EXISTS idx_inventory_product ON inventory(product_id);

-- Index for low stock alerts
CREATE INDEX IF NOT EXISTS idx_inventory_stock ON inventory(stock_quantity);

-- ============================================================
-- PERFORMANCE MONITORING INDEXES
-- ============================================================

-- Add these for production monitoring if you have audit/log tables

-- Example: Index for query performance monitoring
-- CREATE INDEX IF NOT EXISTS idx_audit_logs_date ON audit_logs(created_at);
-- CREATE INDEX IF NOT EXISTS idx_audit_logs_user ON audit_logs(user_id);

-- ============================================================
-- INDEX MAINTENANCE NOTES
-- ============================================================

-- 1. Run ANALYZE TABLE after creating indexes for better query planning
-- ANALYZE TABLE users;
-- ANALYZE TABLE products;
-- ANALYZE TABLE cart_items;
-- ANALYZE TABLE orders;
-- ANALYZE TABLE order_items;
-- ANALYZE TABLE wishlist;
-- ANALYZE TABLE addresses;
-- ANALYZE TABLE reviews;
-- ANALYZE TABLE coupons;
-- ANALYZE TABLE inventory;

-- 2. Monitor index usage with:
-- SELECT * FROM sys.schema_index_statistics;

-- 3. Remove unused indexes periodically to avoid write performance overhead

-- 4. Consider partitioning large tables (orders, reviews) for very large datasets

-- 5. For MySQL 8.0+, consider using invisible indexes for testing:
-- ALTER TABLE users ALTER INDEX idx_users_email INVISIBLE;
