-- ============================================================
-- FASHIONSTORE DATABASE PERFORMANCE OPTIMIZATION
-- Critical indexes for production performance
-- ============================================================

USE fashionstore;

-- ============================================================
-- 1. USER TABLE INDEXES
-- ============================================================

-- Index for email lookups (authentication)
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index for role-based queries (admin/customer filtering)
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);

-- Index for active user filtering
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);

-- Composite index for admin user listing
CREATE INDEX IF NOT EXISTS idx_users_role_active ON users(role, is_active, created_at DESC);

-- ============================================================
-- 2. PRODUCT TABLE INDEXES
-- ============================================================

-- Index for product search by name
CREATE INDEX IF NOT EXISTS idx_products_name_search ON products(product_name(255));

-- Index for price range queries
CREATE INDEX IF NOT EXISTS idx_products_price_range ON products(price);

-- Index for active products filtering
CREATE INDEX IF NOT EXISTS idx_products_active ON products(active);

-- Index for category filtering
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category_id);

-- Index for new products
CREATE INDEX IF NOT EXISTS idx_products_new ON products(is_new, created_at DESC);

-- Index for sale products
CREATE INDEX IF NOT EXISTS idx_products_sale ON products(is_sale, discount_percent DESC);

-- Index for trending products
CREATE INDEX IF NOT EXISTS idx_products_trending ON products(is_trending, popular_score DESC);

-- Index for brand filtering
CREATE INDEX IF NOT EXISTS idx_products_brand ON products(brand);

-- Composite index for product listing (most common query pattern)
CREATE INDEX IF NOT EXISTS idx_products_listing ON products(category_id, active, price, created_at DESC);

-- ============================================================
-- 3. ORDER TABLE INDEXES
-- ============================================================

-- Index for user order lookups
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);

-- Index for order status filtering
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);

-- Index for payment status filtering
CREATE INDEX IF NOT EXISTS idx_orders_payment_status ON orders(payment_status);

-- Index for order date sorting
CREATE INDEX IF NOT EXISTS idx_orders_created_at ON orders(created_at DESC);

-- Composite index for user orders with sorting
CREATE INDEX IF NOT EXISTS idx_orders_user_created ON orders(user_id, created_at DESC);

-- Composite index for status filtering with date
CREATE INDEX IF NOT EXISTS idx_orders_status_date ON orders(status, created_at DESC);

-- Index for transaction_id lookups (payment reconciliation)
CREATE INDEX IF NOT EXISTS idx_orders_transaction ON orders(transaction_id);

-- ============================================================
-- 4. CART ITEMS INDEXES
-- ============================================================

-- Composite index for cart lookups (primary key already covers this)
-- CREATE INDEX IF NOT EXISTS idx_cart_user_product ON cart_items(user_id, product_id);

-- Index for user cart retrieval
CREATE INDEX IF NOT EXISTS idx_cart_user ON cart_items(user_id);

-- Index for product cart analysis
CREATE INDEX IF NOT EXISTS idx_cart_product ON cart_items(product_id);

-- Index for added_at sorting
CREATE INDEX IF NOT EXISTS idx_cart_added_at ON cart_items(added_at DESC);

-- ============================================================
-- 5. WISHLIST INDEXES
-- ============================================================

-- Composite index for wishlist lookups (unique constraint already covers this)
-- CREATE INDEX IF NOT EXISTS idx_wishlist_user_product ON wishlist_items(user_id, product_id);

-- Index for user wishlist retrieval
CREATE INDEX IF NOT EXISTS idx_wishlist_user ON wishlist_items(user_id);

-- Index for product popularity analysis
CREATE INDEX IF NOT EXISTS idx_wishlist_product ON wishlist_items(product_id);

-- Index for created_at sorting
CREATE INDEX IF NOT EXISTS idx_wishlist_created_at ON wishlist_items(created_at DESC);

-- ============================================================
-- 6. PAYMENT TABLE INDEXES
-- ============================================================

-- UNIQUE constraint on transaction_id to prevent duplicate payments
ALTER TABLE payments ADD UNIQUE INDEX IF NOT EXISTS idx_payments_transaction_unique (transaction_id);

-- Index for order payment lookups
CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);

-- Index for payment status filtering
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);

-- Index for payment method filtering
CREATE INDEX IF NOT EXISTS idx_payments_method ON payments(payment_method);

-- Index for verified payments
CREATE INDEX IF NOT EXISTS idx_payments_verified ON payments(verified);

-- Index for payment date sorting
CREATE INDEX IF NOT EXISTS idx_payments_created_at ON payments(created_at DESC);

-- Index for webhook_id lookups (webhook processing)
CREATE INDEX IF NOT EXISTS idx_payments_webhook ON payments(webhook_id);

-- ============================================================
-- 7. ORDER ITEMS INDEXES
-- ============================================================

-- Index for order item lookups
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);

-- Index for product order analysis
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

-- ============================================================
-- 8. ADDRESS TABLE INDEXES
-- ============================================================

-- Index for user address lookups
CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON addresses(user_id);

-- Index for default address filtering
CREATE INDEX IF NOT EXISTS idx_addresses_default ON addresses(user_id, is_default);

-- Index for address type filtering
CREATE INDEX IF NOT EXISTS idx_addresses_type ON addresses(address_type);

-- ============================================================
-- 9. PRODUCT SIZE INDEXES
-- ============================================================

-- Composite index for size lookups (unique constraint already covers this)
-- CREATE INDEX IF NOT EXISTS idx_product_size_unique ON product_sizes(product_id, size_label);

-- Index for product size availability
CREATE INDEX IF NOT EXISTS idx_product_sizes_available ON product_sizes(is_available);

-- Index for SKU lookups
CREATE INDEX IF NOT EXISTS idx_product_sizes_sku ON product_sizes(sku_code);

-- ============================================================
-- 10. PRODUCT IMAGES INDEXES
-- ============================================================

-- Index for product image lookups
CREATE INDEX IF NOT EXISTS idx_product_images_product ON product_images(product_id);

-- Index for primary image filtering
CREATE INDEX IF NOT EXISTS idx_product_images_primary ON product_images(product_id, is_primary);

-- Index for thumbnail filtering
CREATE INDEX IF NOT EXISTS idx_product_images_thumbnail ON product_images(product_id, is_thumbnail);

-- Index for display order
CREATE INDEX IF NOT EXISTS idx_product_images_order ON product_images(product_id, display_order);

-- ============================================================
-- ANALYTICS QUERIES OPTIMIZATION
-- ============================================================

-- Index for revenue calculations by date
CREATE INDEX IF NOT EXISTS idx_orders_status_amount_date ON orders(status, total_amount, created_at);

-- Index for recent orders dashboard
CREATE INDEX IF NOT EXISTS idx_orders_recent ON orders(created_at DESC, status);

-- Index for product sales analysis
CREATE INDEX IF NOT EXISTS idx_order_items_product_quantity ON order_items(product_id, quantity);

-- ============================================================
-- FULLTEXT SEARCH INDEXES (if supported)
-- ============================================================

-- Fulltext index for product search
-- CREATE FULLTEXT INDEX IF NOT EXISTS ft_products_search ON products(product_name, description);

-- ============================================================
-- INDEX MAINTENANCE
-- ============================================================

-- Analyze tables after index creation
ANALYZE TABLE users;
ANALYZE TABLE products;
ANALYZE TABLE orders;
ANALYZE TABLE cart_items;
ANALYZE TABLE wishlist_items;
ANALYZE TABLE payments;
ANALYZE TABLE order_items;
ANALYZE TABLE addresses;
ANALYZE TABLE product_sizes;
ANALYZE TABLE product_images;

-- ============================================================
-- PERFORMANCE NOTES
-- ============================================================
-- 
-- 1. All indexes are created with IF NOT EXISTS to prevent errors
-- 2. Composite indexes follow the most common query patterns
-- 3. Indexes are optimized for both OLTP and OLAP queries
-- 4. Consider partitioning orders table by date for large datasets
-- 5. Monitor index usage and remove unused indexes
-- 6. Rebuild indexes periodically for optimal performance
-- 7. Consider using covering indexes for frequently accessed columns
-- 
-- ============================================================
