package com.fashionstore.serviceimpl;

import com.fashionstore.cache.CacheKey;
import com.fashionstore.cache.CacheService;
import com.fashionstore.cache.CacheServiceImpl;
import com.fashionstore.dao.CartDAO;
import com.fashionstore.dao.CouponDAO;
import com.fashionstore.dao.ProductDAO;
import com.fashionstore.daoimpl.CartDAOImpl;
import com.fashionstore.daoimpl.CouponDAOImpl;
import com.fashionstore.daoimpl.ProductDAOImpl;
import com.fashionstore.model.CartItem;
import com.fashionstore.model.Coupon;
import com.fashionstore.model.Product;
import com.fashionstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service implementation for cart operations with business logic
 * Handles cart calculations, validations, and coupon processing
 */
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);
    private static final int MAX_QUANTITY_PER_ITEM = 100;
    private static final int MAX_CART_ITEMS = 50;

    private final CartDAO cartDAO;
    private final CouponDAO couponDAO;
    private final ProductDAO productDAO;
    private final CacheService cacheService;

    public CartServiceImpl() {
        this.cartDAO = new CartDAOImpl();
        this.couponDAO = new CouponDAOImpl();
        this.productDAO = new ProductDAOImpl();
        this.cacheService = CacheServiceImpl.getInstance();
    }

    @Override
    public List<CartItem> getCartItems(int userId) {
        if (userId <= 0) {
            logger.warn("Invalid user ID: {}", userId);
            return new ArrayList<>();
        }
        
        String key = CacheKey.userCart(userId);
        try {
            CartItem[] cached = cacheService.get(key, CartItem[].class);
            if (cached != null) {
                logger.info("Cart cache hit for user #{}", userId);
                List<CartItem> list = new ArrayList<>();
                for (CartItem ci : cached) {
                    list.add(ci);
                }
                return list;
            }
        } catch (Exception e) {
            logger.warn("Error reading from cart cache for user #{}", userId, e);
        }
        
        try {
            List<CartItem> items = cartDAO.getCartItemsByUserId(userId);
            // Validate and filter invalid items
            List<CartItem> validItems = new ArrayList<>();
            for (CartItem item : items) {
                if (isValidCartItem(item)) {
                    validItems.add(item);
                } else {
                    logger.warn("Removing invalid cart item: {}", item);
                    cartDAO.removeCartItem(item.getCartItemId(), userId);
                }
            }
            
            // Put in cache as array
            try {
                CartItem[] array = validItems.toArray(new CartItem[0]);
                cacheService.put(key, array, 30, TimeUnit.MINUTES);
            } catch (Exception e) {
                logger.warn("Error writing to cart cache for user #{}", userId, e);
            }
            
            return validItems;
        } catch (Exception e) {
            logger.error("Error getting cart items for user {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean addToCart(int userId, int productId, String size, int quantity) {
        if (userId <= 0 || productId <= 0 || quantity <= 0) {
            logger.warn("Invalid parameters for add to cart: userId={}, productId={}, quantity={}", 
                       userId, productId, quantity);
            return false;
        }

        // Validate quantity limits
        if (quantity > MAX_QUANTITY_PER_ITEM) {
            logger.warn("Quantity exceeds maximum: {}", quantity);
            return false;
        }

        // Check if product exists and is available
        Product product = productDAO.getProductById(productId);
        if (product == null || !product.isActive()) {
            logger.warn("Product not available: {}", productId);
            return false;
        }

        // Check current cart size
        List<CartItem> currentItems = getCartItems(userId);
        if (currentItems.size() >= MAX_CART_ITEMS) {
            logger.warn("Cart size limit exceeded for user: {}", userId);
            return false;
        }

        try {
            // Evict cache before state change to prevent race conditions
            cacheService.remove(CacheKey.userCart(userId));
            
            // Check if item already exists
            CartItem existingItem = findCartItem(currentItems, productId, size);
            boolean result;
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                    logger.warn("Combined quantity exceeds maximum: {}", newQuantity);
                    return false;
                }
                result = cartDAO.updateQuantity(existingItem.getCartItemId(), userId, newQuantity);
            } else {
                CartItem newItem = new CartItem();
                newItem.setUserId(userId);
                newItem.setProductId(productId);
                newItem.setSizeLabel(size != null ? size : "M");
                newItem.setQuantity(quantity);
                newItem.setPrice(product.getPrice());
                newItem.setProductName(product.getProductName());
                newItem.setImageUrl(product.getImageUrl());
                
                result = cartDAO.addToCart(newItem) > 0;
            }
            
            // Evict cache again after state change to ensure absolute consistency
            cacheService.remove(CacheKey.userCart(userId));
            return result;
        } catch (Exception e) {
            logger.error("Error adding item to cart: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateCartItemQuantity(int cartItemId, int userId, int quantity) {
        if (cartItemId <= 0 || userId <= 0 || quantity <= 0) {
            logger.warn("Invalid parameters for update quantity: cartItemId={}, userId={}, quantity={}", 
                       cartItemId, userId, quantity);
            return false;
        }

        if (quantity > MAX_QUANTITY_PER_ITEM) {
            logger.warn("Quantity exceeds maximum: {}", quantity);
            return false;
        }

        try {
            cacheService.remove(CacheKey.userCart(userId));
            boolean result = cartDAO.updateQuantity(cartItemId, userId, quantity);
            cacheService.remove(CacheKey.userCart(userId));
            return result;
        } catch (Exception e) {
            logger.error("Error updating cart item quantity: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeCartItem(int cartItemId, int userId) {
        if (cartItemId <= 0 || userId <= 0) {
            logger.warn("Invalid parameters for remove item: cartItemId={}, userId={}", cartItemId, userId);
            return false;
        }

        try {
            cacheService.remove(CacheKey.userCart(userId));
            boolean result = cartDAO.removeCartItem(cartItemId, userId);
            cacheService.remove(CacheKey.userCart(userId));
            return result;
        } catch (Exception e) {
            logger.error("Error removing cart item: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public double calculateCartTotal(int userId) {
        List<CartItem> items = getCartItems(userId);
        double total = 0.0;
        
        for (CartItem item : items) {
            if (isValidCartItem(item)) {
                total += item.getPrice() * item.getQuantity();
            }
        }
        
        return Math.round(total * 100.0) / 100.0; // Round to 2 decimal places
    }

    @Override
    public double calculateCartTotalWithCoupon(int userId, String couponCode) {
        double cartTotal = calculateCartTotal(userId);
        
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return cartTotal;
        }

        try {
            Coupon coupon = couponDAO.getCouponByCode(couponCode);
            if (coupon != null && isCouponValid(coupon, cartTotal)) {
                double discount = calculateDiscount(cartTotal, coupon);
                return Math.max(0, cartTotal - discount);
            }
        } catch (Exception e) {
            logger.error("Error applying coupon: {}", e.getMessage(), e);
        }
        
        return cartTotal;
    }

    @Override
    public boolean validateCartForCheckout(int userId) {
        List<CartItem> items = getCartItems(userId);
        
        if (items.isEmpty()) {
            logger.warn("Empty cart for user: {}", userId);
            return false;
        }

        // Validate each item
        for (CartItem item : items) {
            if (!isValidCartItem(item)) {
                logger.warn("Invalid cart item found: {}", item);
                return false;
            }
            
            // Check product availability
            Product product = productDAO.getProductById(item.getProductId());
            if (product == null || !product.isActive()) {
                logger.warn("Product not available: {}", item.getProductId());
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean clearCart(int userId) {
        if (userId <= 0) {
            logger.warn("Invalid user ID for clear cart: {}", userId);
            return false;
        }

        try {
            cacheService.remove(CacheKey.userCart(userId));
            boolean result = cartDAO.clearCartByUserId(userId);
            cacheService.remove(CacheKey.userCart(userId));
            return result;
        } catch (Exception e) {
            logger.error("Error clearing cart: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public int getCartItemCount(int userId) {
        List<CartItem> items = getCartItems(userId);
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    @Override
    public boolean isProductInCart(int userId, int productId, String size) {
        List<CartItem> items = getCartItems(userId);
        return findCartItem(items, productId, size) != null;
    }

    // Private helper methods
    private boolean isValidCartItem(CartItem item) {
        return item != null && 
               item.getCartItemId() > 0 && 
               item.getUserId() > 0 && 
               item.getProductId() > 0 && 
               item.getQuantity() > 0 && 
               item.getPrice() > 0;
    }

    private CartItem findCartItem(List<CartItem> items, int productId, String size) {
        String normalizedSize = size != null ? size.trim() : "M";
        for (CartItem item : items) {
            if (item.getProductId() == productId) {
                String itemSize = item.getSizeLabel() != null ? item.getSizeLabel().trim() : "M";
                if (itemSize.equals(normalizedSize)) {
                    return item;
                }
            }
        }
        return null;
    }

    private boolean isCouponValid(Coupon coupon, double cartTotal) {
        if (coupon == null || !coupon.isActive()) {
            return false;
        }

        // Check expiration
        if (coupon.getValidUntil() != null && coupon.getValidUntil().before(new java.util.Date())) {
            return false;
        }

        // Check minimum order amount
        if (coupon.getMinimumOrderAmount() > 0 && cartTotal < coupon.getMinimumOrderAmount()) {
            return false;
        }

        // Check usage limits
        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return false;
        }

        return true;
    }

    private double calculateDiscount(double cartTotal, Coupon coupon) {
        if ("percentage".equalsIgnoreCase(coupon.getDiscountType())) {
            double discount = cartTotal * (coupon.getDiscountValue() / 100.0);
            // Apply maximum discount limit if set
            if (coupon.getMaximumDiscountAmount() != null && discount > coupon.getMaximumDiscountAmount()) {
                discount = coupon.getMaximumDiscountAmount();
            }
            return discount;
        } else {
            return Math.min(coupon.getDiscountValue(), cartTotal);
        }
    }
}
