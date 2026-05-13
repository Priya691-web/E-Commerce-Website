package com.fashionstore.serviceimpl;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.dao.ProductSizeDAO;
import com.fashionstore.daoimpl.ProductDAOImpl;
import com.fashionstore.daoimpl.ProductSizeDAOImpl;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductSize;
import com.fashionstore.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for inventory management with business logic
 * Handles stock validation, low stock alerts, and inventory updates
 */
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final int LOW_STOCK_THRESHOLD = 10;
    private static final int CRITICAL_STOCK_THRESHOLD = 5;

    private final ProductDAO productDAO;
    private final ProductSizeDAO productSizeDAO;

    public InventoryServiceImpl() {
        this.productDAO = new ProductDAOImpl();
        this.productSizeDAO = new ProductSizeDAOImpl();
    }

    @Override
    public boolean isProductAvailable(int productId, String size, int quantity) {
        if (productId <= 0 || quantity <= 0) {
            logger.warn("Invalid parameters for availability check: productId={}, quantity={}", productId, quantity);
            return false;
        }

        try {
            Product product = productDAO.getProductById(productId);
            if (product == null || !product.isActive()) {
                return false;
            }

            // Check stock for specific size
            List<ProductSize> sizes = productSizeDAO.getSizesByProductId(productId);
            ProductSize productSize = null;
            if (sizes != null) {
                String targetSize = size != null ? size : "M";
                for (ProductSize ps : sizes) {
                    if (targetSize.equals(ps.getSizeLabel())) {
                        productSize = ps;
                        break;
                    }
                }
            }
            if (productSize == null || !productSize.isAvailable()) {
                return false;
            }

            return productSize.getStockQuantity() >= quantity;

        } catch (Exception e) {
            logger.error("Error checking product availability: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int getCurrentStock(int productId, String size) {
        if (productId <= 0) {
            logger.warn("Invalid product ID for stock check: {}", productId);
            return 0;
        }

        try {
            List<ProductSize> sizes = productSizeDAO.getSizesByProductId(productId);
            ProductSize productSize = null;
            if (sizes != null) {
                String targetSize = size != null ? size : "M";
                for (ProductSize ps : sizes) {
                    if (targetSize.equals(ps.getSizeLabel())) {
                        productSize = ps;
                        break;
                    }
                }
            }
            return productSize != null ? productSize.getStockQuantity() : 0;
        } catch (Exception e) {
            logger.error("Error getting current stock: {}", e.getMessage(), e);
            return 0;
        }
    }

    @Override
    public boolean updateStockLevel(int productId, String size, int newQuantity) {
        if (productId <= 0 || newQuantity < 0) {
            logger.warn("Invalid parameters for stock update: productId={}, newQuantity={}", productId, newQuantity);
            return false;
        }

        try {
            List<ProductSize> sizes = productSizeDAO.getSizesByProductId(productId);
            ProductSize productSize = null;
            if (sizes != null) {
                String targetSize = size != null ? size : "M";
                for (ProductSize ps : sizes) {
                    if (targetSize.equals(ps.getSizeLabel())) {
                        productSize = ps;
                        break;
                    }
                }
            }
            if (productSize == null) {
                // Create new size entry
                productSize = new ProductSize();
                productSize.setProductId(productId);
                productSize.setSizeLabel(size != null ? size : "M");
                productSize.setStockQuantity(newQuantity);
                productSize.setAvailable(newQuantity > 0);
                productSizeDAO.addOrUpdateSize(productSize);
                return true;
            } else {
                // Update existing size
                productSize.setStockQuantity(newQuantity);
                productSize.setAvailable(newQuantity > 0);
                productSizeDAO.addOrUpdateSize(productSize);
                return true;
            }
        } catch (Exception e) {
            logger.error("Error updating stock level: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean reserveStock(int productId, String size, int quantity) {
        if (!isProductAvailable(productId, size, quantity)) {
            logger.warn("Insufficient stock for reservation: productId={}, size={}, quantity={}", productId, size, quantity);
            return false;
        }

        try {
            int currentStock = getCurrentStock(productId, size);
            int newStock = currentStock - quantity;
            return updateStockLevel(productId, size, newStock);
        } catch (Exception e) {
            logger.error("Error reserving stock: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean releaseReservedStock(int productId, String size, int quantity) {
        try {
            int currentStock = getCurrentStock(productId, size);
            int newStock = currentStock + quantity;
            return updateStockLevel(productId, size, newStock);
        } catch (Exception e) {
            logger.error("Error releasing reserved stock: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Product> getLowStockProducts(int threshold) {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            List<Product> lowStockProducts = new ArrayList<>();

            for (Product product : allProducts) {
                if (hasLowStock(product, threshold)) {
                    lowStockProducts.add(product);
                }
            }

            return lowStockProducts;
        } catch (Exception e) {
            logger.error("Error getting low stock products: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Product> getOutOfStockProducts() {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            List<Product> outOfStockProducts = new ArrayList<>();

            for (Product product : allProducts) {
                if (isOutOfStock(product)) {
                    outOfStockProducts.add(product);
                }
            }

            return outOfStockProducts;
        } catch (Exception e) {
            logger.error("Error getting out of stock products: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean validateStockForOrder(List<ProductSize> items) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        try {
            for (ProductSize item : items) {
                if (!isProductAvailable(item.getProductId(), item.getSizeLabel(), item.getStockQuantity())) {
                    logger.warn("Insufficient stock for order: productId={}, size={}, requiredQuantity={}", 
                               item.getProductId(), item.getSizeLabel(), item.getStockQuantity());
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error validating stock for order: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean processInventoryAfterOrder(List<ProductSize> items) {
        if (items == null || items.isEmpty()) {
            return true;
        }

        try {
            for (ProductSize item : items) {
                if (!reserveStock(item.getProductId(), item.getSizeLabel(), item.getStockQuantity())) {
                    logger.error("Failed to process inventory for order item: {}", item);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error processing inventory after order: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Product> getInventoryReport() {
        try {
            List<Product> allProducts = productDAO.getAllProducts();
            
            // Add stock information to each product
            for (Product product : allProducts) {
                List<ProductSize> sizes = productSizeDAO.getSizesByProductId(product.getProductId());
                product.setSizes(sizes);
            }
            
            return allProducts;
        } catch (Exception e) {
            logger.error("Error getting inventory report: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean needsRestocking(int productId, String size) {
        int stock = getCurrentStock(productId, size);
        return stock <= LOW_STOCK_THRESHOLD;
    }

    @Override
    public boolean batchUpdateInventory(List<ProductSize> updates) {
        if (updates == null || updates.isEmpty()) {
            return true;
        }

        try {
            for (ProductSize update : updates) {
                if (!updateStockLevel(update.getProductId(), update.getSizeLabel(), update.getStockQuantity())) {
                    logger.error("Failed to batch update inventory for: {}", update);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Error in batch inventory update: {}", e.getMessage(), e);
            return false;
        }
    }

    // Private helper methods
    private boolean hasLowStock(Product product, int threshold) {
        if (product.getSizes() == null || product.getSizes().isEmpty()) {
            return product.getStockQuantity() <= threshold;
        }

        for (ProductSize size : product.getSizes()) {
            if (size.getStockQuantity() <= threshold) {
                return true;
            }
        }
        return false;
    }

    private boolean isOutOfStock(Product product) {
        if (product.getSizes() == null || product.getSizes().isEmpty()) {
            return product.getStockQuantity() == 0;
        }

        for (ProductSize size : product.getSizes()) {
            if (size.getStockQuantity() > 0) {
                return false;
            }
        }
        return true;
    }
}
