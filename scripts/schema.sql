CREATE DATABASE IF NOT EXISTS fashionstore;
USE fashionstore;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('CUSTOMER', 'ADMIN') DEFAULT 'CUSTOMER',
    address TEXT,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    image_url VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS product_variants (
    id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT,
    size VARCHAR(50),
    color VARCHAR(50),
    stock_quantity INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS carts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cart_id INT,
    product_variant_id INT,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
    FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT,
    product_variant_id INT,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE NO ACTION
);


-- Sample Data Insertion
INSERT IGNORE INTO categories (id, name, description, image_url) VALUES 
(1, 'Men', 'Men\'s Clothing and Accessories', 'assets/images/men.jpg'),
(2, 'Women', 'Women\'s Clothing and Fashion', 'assets/images/women.jpg'),
(3, 'Kids', 'Kids Apparel', 'assets/images/kids.jpg');

INSERT IGNORE INTO products (id, category_id, name, description, price, image_url) VALUES 
(1, 1, 'Classic White T-Shirt', 'Premium cotton men\'s t-shirt', 25.00, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=500&q=80'),
(2, 1, 'Denim Jacket', 'Vintage style blue denim jacket', 89.99, 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?auto=format&fit=crop&w=500&q=80'),
(3, 2, 'Summer Floral Dress', 'Light and breezy summer dress', 45.50, 'https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?auto=format&fit=crop&w=500&q=80'),
(4, 2, 'Leather Handbag', 'Genuine leather premium handbag', 120.00, 'https://images.unsplash.com/photo-1584917865442-de89df76afd3?auto=format&fit=crop&w=500&q=80'),
(5, 3, 'Kids Sneakers', 'Comfortable running shoes for kids', 35.00, 'https://images.unsplash.com/photo-1514989940723-e8e51635b782?auto=format&fit=crop&w=500&q=80'),
(6, 1, 'Formal Trousers', 'Slim fit formal trousers', 55.00, 'https://images.unsplash.com/photo-1624378439575-d8705ad7ae80?auto=format&fit=crop&w=500&q=80');


INSERT IGNORE INTO product_variants (product_id, size, color, stock_quantity) VALUES 
(1, 'M', 'White', 50),
(1, 'L', 'White', 30),
(2, 'L', 'Blue', 15),
(3, 'S', 'Floral', 20),
(4, 'One Size', 'Brown', 10),
(5, '4', 'Red', 25),
(6, '32', 'Black', 40);
