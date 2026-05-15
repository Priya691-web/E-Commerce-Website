-- Product Images Schema
-- Supports multiple images per product with proper ordering and metadata

DROP TABLE IF EXISTS product_images;

CREATE TABLE product_images (
    product_image_id INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_alt_text VARCHAR(255),
    display_order INT DEFAULT 0,
    is_primary BOOLEAN DEFAULT FALSE,
    is_thumbnail BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key to products table
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) 
        REFERENCES products(product_id) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- Constraints
    CONSTRAINT chk_product_images_order CHECK (display_order >= 0),
    CONSTRAINT chk_product_images_url_not_empty CHECK (image_url IS NOT NULL AND image_url != '')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for performance
CREATE INDEX idx_product_images_product ON product_images(product_id, display_order);
CREATE INDEX idx_product_images_primary ON product_images(product_id, is_primary);
CREATE INDEX idx_product_images_thumbnail ON product_images(product_id, is_thumbnail);
