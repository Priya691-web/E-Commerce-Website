# FashionStore - ER Diagram Documentation

## Table of Contents
1. [Entity Relationship Overview](#entity-relationship-overview)
2. [Core Entities](#core-entities)
3. [Relationship Diagrams](#relationship-diagrams)
4. [Entity Details](#entity-details)
5. [Relationship Constraints](#relationship-constraints)
6. [Index Strategy](#index-strategy)

---

## Entity Relationship Overview

The FashionStore database consists of 30+ tables organized into logical groups:

- **User Management**: users, user_settings, user_profiles, addresses
- **Product Management**: products, categories, product_sizes, product_attributes
- **Cart & Wishlist**: cart_items, wishlist, saved_items
- **Orders**: orders, order_items, payments, payment_methods
- **Coupons**: coupons, coupon_usage
- **Reviews**: reviews
- **Search**: search_history, search_analytics, recently_viewed
- **Shipping**: shipping_zones, shipping_rates
- **Tax**: tax_rates
- **Email**: email_logs, email_notifications
- **Stripe Integration**: stripe_customers, stripe_webhook_events
- **Refunds**: refunds
- **Invoices**: invoices

---

## Core Entities

### Primary Entities

**users**: Central entity for user accounts
**products**: Central entity for products
**orders**: Central entity for orders
**categories**: Product categorization
**addresses**: User addresses
**reviews**: Product reviews

### Supporting Entities

**cart_items**: Shopping cart items
**wishlist**: User wishlist
**coupons**: Discount coupons
**payments**: Payment records
**product_sizes**: Product size variants
**order_items**: Order line items

---

## Relationship Diagrams

### High-Level ER Diagram

```
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│   users     │         │  orders     │         │  products   │
├─────────────┤         ├─────────────┤         ├─────────────┤
│ user_id  PK│────────<│ order_id PK │────────<│product_id PK│
│ email      │         │ user_id  FK │         │category_idFK│
│ password   │         │ total_amount│         │ price       │
│ role       │         │ status      │         │ stock_qty   │
└─────────────┘         └─────────────┘         └─────────────┘
       │                      │                      │
       │                      │                      │
       ▼                      ▼                      ▼
┌─────────────┐         ┌─────────────┐         ┌─────────────┐
│ addresses   │         │order_items  │         │categories  │
├─────────────┤         ├─────────────┤         ├─────────────┤
│address_id PK│         │item_id   PK │         │category_idPK│
│ user_id  FK │         │order_id  FK │         │name        │
│ address    │         │product_idFK │         │slug        │
└─────────────┘         │quantity    │         └─────────────┘
                        └─────────────┘
```

### Detailed Relationship Diagram

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   users     │       │cart_items   │       │  products   │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ user_id  PK│──────<│cart_id   PK │──────<│product_id PK│
│ email      │       │ user_id  FK │       │category_idFK│
│ password   │       │product_idFK │       │ price       │
│ role       │       │ quantity    │       │ discount    │
└─────────────┘       └─────────────┘       │ stock_qty   │
       │                                      └─────────────┘
       │                                             │
       │                                             │
       ▼                                             ▼
┌─────────────┐                               ┌─────────────┐
│ addresses   │                               │product_sizes│
├─────────────┤                               ├─────────────┤
│address_id PK│                               │size_id   PK │
│ user_id  FK │                               │product_idFK │
│ address    │                               │ size_label  │
└─────────────┘                               │ stock_qty   │
                                               └─────────────┘

┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│  orders     │       │order_items  │       │  reviews    │
├─────────────┤       ├─────────────┤       ├─────────────┤
│ order_id PK│──────<│item_id   PK │       │review_id  PK │
│ user_id  FK │       │order_id  FK │       │user_id   FK │
│address_idFK│       │product_idFK │──────<│product_idFK │
│ total_amt  │       │ quantity    │       │ rating      │
│ status     │       └─────────────┘       │ review_text │
└─────────────┘                               └─────────────┘
       │
       │
       ▼
┌─────────────┐
│  payments   │
├─────────────┤
│payment_id PK│
│ order_id FK │
│ amount      │
│ status      │
└─────────────┘
```

---

## Entity Details

### users

**Purpose**: Store user account information

**Columns**:
- `user_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `full_name` (VARCHAR(100), NOT NULL): User's full name
- `email` (VARCHAR(100), UNIQUE, NOT NULL): User's email
- `phone` (VARCHAR(20)): User's phone number
- `password` (VARCHAR(255), NOT NULL): BCrypt hashed password
- `gender` (ENUM('Male', 'Female', 'Other')): User's gender
- `address` (TEXT): User's address
- `role` (ENUM('admin', 'customer', 'disabled'), DEFAULT 'customer'): User role
- `active` (BOOLEAN, DEFAULT TRUE): Account active status
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- One-to-many with addresses
- One-to-many with orders
- One-to-many with cart_items
- One-to-many with wishlist
- One-to-many with reviews

### products

**Purpose**: Store product information

**Columns**:
- `product_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `product_name` (VARCHAR(200), NOT NULL): Product name
- `description` (TEXT): Product description
- `price` (DECIMAL(10,2), NOT NULL): Product price
- `discount_percent` (DECIMAL(5,2), DEFAULT 0): Discount percentage
- `image_url` (VARCHAR(500)): Product image URL
- `stock_quantity` (INT, DEFAULT 0): Total stock quantity
- `category_id` (INT, FK): Category reference
- `brand` (VARCHAR(100)): Product brand
- `active` (BOOLEAN, DEFAULT TRUE): Active status
- `is_new` (BOOLEAN, DEFAULT FALSE): New arrival flag
- `is_sale` (BOOLEAN, DEFAULT FALSE): Sale flag
- `is_trending` (BOOLEAN, DEFAULT FALSE): Trending flag
- `popular_score` (DECIMAL(10,2), DEFAULT 0): Popularity score
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- Many-to-one with categories
- One-to-many with product_sizes
- One-to-many with order_items
- One-to-many with cart_items
- One-to-many with wishlist
- One-to-many with reviews

### orders

**Purpose**: Store order information

**Columns**:
- `order_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `user_id` (INT, FK, NOT NULL): User reference
- `shipping_address_id` (INT, FK): Shipping address reference
- `total_amount` (DECIMAL(10,2), NOT NULL): Total order amount
- `discount_amount` (DECIMAL(10,2), DEFAULT 0): Discount amount
- `final_amount` (DECIMAL(10,2), NOT NULL): Final amount after discount
- `status` (ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled'), DEFAULT 'Pending'): Order status
- `payment_method` (ENUM('card', 'upi', 'cod')): Payment method
- `payment_status` (ENUM('Pending', 'Paid', 'Failed', 'Refunded'), DEFAULT 'Pending'): Payment status
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- Many-to-one with users
- Many-to-one with addresses
- One-to-many with order_items
- One-to-one with payments

### categories

**Purpose**: Store product categories

**Columns**:
- `category_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `category_name` (VARCHAR(100), NOT NULL): Category name
- `category_slug` (VARCHAR(100), UNIQUE, NOT NULL): URL-friendly slug
- `description` (TEXT): Category description
- `active` (BOOLEAN, DEFAULT TRUE): Active status
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- One-to-many with products

### addresses

**Purpose**: Store user addresses

**Columns**:
- `address_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `user_id` (INT, FK, NOT NULL): User reference
- `full_name` (VARCHAR(100), NOT NULL): Recipient name
- `address_line1` (VARCHAR(255), NOT NULL): Address line 1
- `address_line2` (VARCHAR(255)): Address line 2
- `city` (VARCHAR(100), NOT NULL): City
- `state` (VARCHAR(100), NOT NULL): State
- `zip_code` (VARCHAR(20), NOT NULL): ZIP code
- `country` (VARCHAR(100), NOT NULL): Country
- `phone` (VARCHAR(20)): Phone number
- `is_default` (BOOLEAN, DEFAULT FALSE): Default address flag
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- Many-to-one with users
- One-to-many with orders

### cart_items

**Purpose**: Store shopping cart items

**Columns**:
- `cart_item_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `user_id` (INT, FK, NOT NULL): User reference
- `product_id` (INT, FK, NOT NULL): Product reference
- `size` (VARCHAR(10)): Product size
- `quantity` (INT, NOT NULL): Quantity
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- Many-to-one with users
- Many-to-one with products

### wishlist

**Purpose**: Store user wishlist items

**Columns**:
- `wishlist_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `user_id` (INT, FK, NOT NULL): User reference
- `product_id` (INT, FK, NOT NULL): Product reference
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp

**Relationships**:
- Many-to-one with users
- Many-to-one with products

### order_items

**Purpose**: Store order line items

**Columns**:
- `order_item_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `order_id` (INT, FK, NOT NULL): Order reference
- `product_id` (INT, FK, NOT NULL): Product reference
- `product_name` (VARCHAR(200), NOT NULL): Product name at time of order
- `size` (VARCHAR(10)): Product size
- `quantity` (INT, NOT NULL): Quantity
- `price` (DECIMAL(10,2), NOT NULL): Price at time of order
- `discount_percent` (DECIMAL(5,2), DEFAULT 0): Discount at time of order
- `total_price` (DECIMAL(10,2), NOT NULL): Total price

**Relationships**:
- Many-to-one with orders
- Many-to-one with products

### product_sizes

**Purpose**: Store product size variants

**Columns**:
- `size_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `product_id` (INT, FK, NOT NULL): Product reference
- `size_label` (VARCHAR(10), NOT NULL): Size label (S, M, L, etc.)
- `stock_quantity` (INT, DEFAULT 0): Stock quantity for this size

**Relationships**:
- Many-to-one with products

### reviews

**Purpose**: Store product reviews

**Columns**:
- `review_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `user_id` (INT, FK, NOT NULL): User reference
- `product_id` (INT, FK, NOT NULL): Product reference
- `rating` (INT, NOT NULL): Rating (1-5)
- `review_text` (TEXT): Review text
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- Many-to-one with users
- Many-to-one with products

### payments

**Purpose**: Store payment information

**Columns**:
- `payment_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `order_id` (INT, FK, NOT NULL): Order reference
- `amount` (DECIMAL(10,2), NOT NULL): Payment amount
- `payment_method` (ENUM('card', 'upi', 'cod')): Payment method
- `status` (ENUM('Pending', 'Paid', 'Failed', 'Refunded'), DEFAULT 'Pending'): Payment status
- `transaction_id` (VARCHAR(100)): Transaction ID
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- One-to-one with orders

### coupons

**Purpose**: Store discount coupons

**Columns**:
- `coupon_id` (INT, PK, AUTO_INCREMENT): Unique identifier
- `coupon_code` (VARCHAR(50), UNIQUE, NOT NULL): Coupon code
- `discount_type` (ENUM('percentage', 'fixed')): Discount type
- `discount_value` (DECIMAL(10,2), NOT NULL): Discount value
- `min_order_value` (DECIMAL(10,2)): Minimum order value
- `usage_limit` (INT): Usage limit
- `expiry_date` (DATE): Expiry date
- `active` (BOOLEAN, DEFAULT TRUE): Active status
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Creation timestamp
- `updated_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE): Update timestamp

**Relationships**:
- One-to-many with coupon_usage

---

## Relationship Constraints

### Foreign Key Constraints

**users → addresses**:
```sql
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
```

**users → orders**:
```sql
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
```

**users → cart_items**:
```sql
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
```

**users → wishlist**:
```sql
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
```

**users → reviews**:
```sql
FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
```

**products → product_sizes**:
```sql
FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
```

**products → order_items**:
```sql
FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT
```

**products → cart_items**:
```sql
FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
```

**products → wishlist**:
```sql
FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
```

**products → reviews**:
```sql
FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
```

**orders → order_items**:
```sql
FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
```

**orders → payments**:
```sql
FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
```

**categories → products**:
```sql
FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE SET NULL
```

### Unique Constraints

- `users.email`
- `categories.category_slug`
- `coupons.coupon_code`

### Check Constraints

- `products.discount_percent` >= 0 AND <= 100
- `reviews.rating` >= 1 AND <= 5
- `cart_items.quantity` > 0
- `order_items.quantity` > 0
- `product_sizes.stock_quantity` >= 0

---

## Index Strategy

### Primary Key Indexes

All tables have primary key indexes automatically created on their primary key columns.

### Foreign Key Indexes

Indexes are created on all foreign key columns for efficient join operations:

- `addresses.user_id`
- `cart_items.user_id`, `cart_items.product_id`
- `wishlist.user_id`, `wishlist.product_id`
- `reviews.user_id`, `reviews.product_id`
- `product_sizes.product_id`
- `order_items.order_id`, `order_items.product_id`
- `payments.order_id`
- `products.category_id`

### Unique Indexes

- `users.email`
- `categories.category_slug`
- `coupons.coupon_code`

### Performance Indexes

**products**:
- `idx_products_active` on `active`
- `idx_products_category` on `category_id`
- `idx_products_price` on `price`
- `idx_products_created` on `created_at`
- `idx_products_popular` on `popular_score`

**orders**:
- `idx_orders_user` on `user_id`
- `idx_orders_status` on `status`
- `idx_orders_created` on `created_at`

**reviews**:
- `idx_reviews_product` on `product_id`
- `idx_reviews_rating` on `rating`

**search_history**:
- `idx_search_user` on `user_id`
- `idx_search_query` on `query`
- `idx_search_created` on `created_at`

### Composite Indexes

**products**:
- `idx_products_category_active` on `(category_id, active)`
- `idx_products_active_new` on `(active, is_new)`
- `idx_products_active_trending` on `(active, is_trending)`

**orders**:
- `idx_orders_user_status` on `(user_id, status)`
- `idx_orders_status_created` on `(status, created_at)`

---

## Conclusion

The FashionStore ER diagram demonstrates a **well-designed relational database** with proper normalization, appropriate relationships, and strategic indexing. The database design supports the application's requirements for user management, product catalog, order processing, and customer engagement while maintaining data integrity and query performance.
