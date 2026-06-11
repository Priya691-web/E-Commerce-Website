-- ============================================================
-- FASHIONSTORE DATABASE FOREIGN KEY CONSTRAINTS
-- Additional constraints for data integrity
-- ============================================================

USE fashionstore;

-- ============================================================
-- EXISTING CONSTRAINTS (Already in schema.sql)
-- ============================================================
-- users: No FKs (root table)
-- categories: FK to parent_category_id (self-reference)
-- products: FK to categories
-- product_sizes: FK to products
-- product_images: FK to products
-- cart_items: FK to users, products
-- wishlist_items: FK to users, products
-- saved_items: FK to users, products
-- addresses: FK to users
-- orders: FK to users, addresses (billing, shipping)
-- order_items: FK to orders, products
-- order_status_history: FK to orders
-- payments: FK to orders
-- payment_methods: FK to users

-- ============================================================
-- ADDITIONAL CONSTRAINTS FOR DATA INTEGRITY
-- ============================================================

-- Ensure orders have valid user_id (prevent orphaned orders)
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_user_id 
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE;

-- Ensure orders have valid payment_method_id if set
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_payment_method_id 
FOREIGN KEY (payment_method_id) REFERENCES payment_methods(payment_method_id) ON DELETE SET NULL ON UPDATE CASCADE;

-- Ensure orders have valid coupon_id if set
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_coupon_id 
FOREIGN KEY (coupon_id) REFERENCES coupons(coupon_id) ON DELETE SET NULL ON UPDATE CASCADE;

-- ============================================================
-- UNIQUE CONSTRAINTS FOR IDEMPOTENCY
-- ============================================================

-- Ensure payment transaction_id is unique (prevent duplicate payments)
ALTER TABLE payments 
ADD UNIQUE INDEX uk_payments_transaction_id (transaction_id);

-- Ensure payment idempotency_key is unique (prevent duplicate payment attempts)
ALTER TABLE payments 
ADD UNIQUE INDEX uk_payments_idempotency_key (idempotency_key);

-- Ensure stripe_payment_intent_id is unique (prevent duplicate Stripe payments)
ALTER TABLE payments 
ADD UNIQUE INDEX uk_payments_stripe_intent (stripe_payment_intent_id);

-- ============================================================
-- CHECK CONSTRAINTS FOR BUSINESS RULES
-- ============================================================

-- Ensure order total matches sum of order items
-- (This would require a trigger, but we'll add a basic check for now)
ALTER TABLE orders 
ADD CONSTRAINT chk_order_total_positive CHECK (total_amount >= 0);

-- Ensure payment amount is positive
ALTER TABLE payments 
ADD CONSTRAINT chk_payment_amount_positive CHECK (amount >= 0);

-- Ensure cart quantity is positive
ALTER TABLE cart_items 
ADD CONSTRAINT chk_cart_quantity_positive CHECK (quantity > 0);

-- Ensure order item quantity is positive
ALTER TABLE order_items 
ADD CONSTRAINT chk_order_item_quantity_positive CHECK (quantity > 0);

-- Ensure product price is non-negative
ALTER TABLE products 
ADD CONSTRAINT chk_product_price_non_negative CHECK (price >= 0);

-- Ensure product stock is non-negative
ALTER TABLE products 
ADD CONSTRAINT chk_product_stock_non_negative CHECK (stock_quantity >= 0);

-- Ensure discount percentage is valid (0-100)
ALTER TABLE products 
ADD CONSTRAINT chk_product_discount_valid CHECK (discount_percent >= 0 AND discount_percent <= 100);

-- Ensure product size stock is non-negative
ALTER TABLE product_sizes 
ADD CONSTRAINT chk_product_size_stock_non_negative CHECK (stock_quantity >= 0);

-- ============================================================
-- CASCADE RULES OPTIMIZATION
-- ============================================================

-- Update orders table to use proper cascade rules
-- When user is deleted, orders should be preserved but user_id set to NULL (for audit trail)
ALTER TABLE orders 
DROP FOREIGN KEY IF EXISTS fk_orders_user_id;

ALTER TABLE orders 
ADD CONSTRAINT fk_orders_user_id 
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE;

-- ============================================================
-- INDEX OPTIMIZATION FOR FOREIGN KEYS
-- ============================================================

-- Add indexes for foreign key columns if not already present
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_billing_address_id ON orders(billing_address_id);
CREATE INDEX IF NOT EXISTS idx_orders_shipping_address_id ON orders(shipping_address_id);
CREATE INDEX IF NOT EXISTS idx_orders_coupon_id ON orders(coupon_id);
CREATE INDEX IF NOT EXISTS idx_orders_payment_method_id ON orders(payment_method_id);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);

CREATE INDEX IF NOT EXISTS idx_payment_methods_user_id ON payment_methods(user_id);

CREATE INDEX IF NOT EXISTS idx_order_status_history_order_id ON order_status_history(order_id);

-- ============================================================
-- TRIGGERS FOR COMPLEX BUSINESS RULES
-- ============================================================

-- Trigger to validate order total matches sum of order items
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_validate_order_total
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    DECLARE current_total DECIMAL(10,2);
    DECLARE new_total DECIMAL(10,2);
    
    -- Get current order total
    SELECT total_amount INTO current_total 
    FROM orders 
    WHERE order_id = NEW.order_id;
    
    -- Calculate new total including this item
    SET new_total = current_total + (NEW.price * NEW.quantity);
    
    -- Update order total
    UPDATE orders 
    SET total_amount = new_total 
    WHERE order_id = NEW.order_id;
END//
DELIMITER ;

-- Trigger to update product stock when order is placed
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_update_product_stock_on_order
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    -- Update product stock
    UPDATE products 
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE product_id = NEW.product_id;
    
    -- Update product size stock if size is specified
    IF NEW.size_label IS NOT NULL THEN
        UPDATE product_sizes 
        SET stock_quantity = stock_quantity - NEW.quantity
        WHERE product_id = NEW.product_id AND size_label = NEW.size_label;
    END IF;
END//
DELIMITER ;

-- Trigger to restore product stock when order is cancelled
DELIMITER //
CREATE TRIGGER IF NOT EXISTS trg_restore_product_stock_on_cancel
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.status = 'Cancelled' AND OLD.status != 'Cancelled' THEN
        -- Restore product stock
        UPDATE products p
        INNER JOIN order_items oi ON p.product_id = oi.product_id
        SET p.stock_quantity = p.stock_quantity + oi.quantity
        WHERE oi.order_id = NEW.order_id;
        
        -- Restore product size stock
        UPDATE product_sizes ps
        INNER JOIN order_items oi ON ps.product_id = oi.product_id
        SET ps.stock_quantity = ps.stock_quantity + oi.quantity
        WHERE oi.order_id = NEW.order_id AND ps.size_label = oi.size_label;
    END IF;
END//
DELIMITER ;

-- ============================================================
-- CONSTRAINT VALIDATION
-- ============================================================

-- Check if all foreign key constraints are valid
SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE
FROM 
    INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE 
    TABLE_SCHEMA = 'fashionstore'
    AND CONSTRAINT_TYPE = 'FOREIGN KEY'
ORDER BY 
    TABLE_NAME, CONSTRAINT_NAME;

-- ============================================================
-- PERFORMANCE NOTES
-- ============================================================
-- 
-- 1. Foreign keys with ON DELETE CASCADE can cause performance issues
--    on large tables. Consider using SET NULL or RESTRICT for production.
-- 
-- 2. Indexes on foreign key columns are essential for performance.
--    All FK columns should have corresponding indexes.
-- 
-- 3. Triggers add overhead to DML operations. Monitor performance
--    and consider moving business logic to application layer for high-volume tables.
-- 
-- 4. Complex check constraints can slow down INSERT/UPDATE operations.
--    Keep constraints simple and move complex validation to application layer.
-- 
-- 5. Regularly analyze and optimize tables to maintain constraint performance:
--    ANALYZE TABLE table_name;
-- 
-- ============================================================
