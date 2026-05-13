package com.fashionstore.serviceimpl;

import com.fashionstore.dao.CouponDAO;
import com.fashionstore.daoimpl.CouponDAOImpl;
import com.fashionstore.model.Coupon;
import com.fashionstore.service.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Service implementation for coupon operations with business logic
 * Handles coupon validation, application, and management
 */
public class CouponServiceImpl implements CouponService {

    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final CouponDAO couponDAO;

    public CouponServiceImpl() {
        this.couponDAO = new CouponDAOImpl();
    }

    @Override
    public Coupon validateCoupon(String couponCode) {
        if (couponCode == null || couponCode.trim().isEmpty()) {
            return null;
        }

        try {
            Coupon coupon = couponDAO.getCouponByCode(couponCode.trim().toUpperCase());
            if (coupon == null) {
                logger.warn("Coupon not found: {}", couponCode);
                return null;
            }

            // Validate coupon status and dates
            if (!isCouponActive(coupon)) {
                logger.warn("Coupon is not active: {}", couponCode);
                return null;
            }

            return coupon;
        } catch (Exception e) {
            logger.error("Error validating coupon {}: {}", couponCode, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean applyCoupon(int userId, String couponCode) {
        Coupon coupon = validateCoupon(couponCode);
        if (coupon == null) {
            return false;
        }

        try {
            // Additional business logic for applying coupon to user session/cart
            // This would typically involve storing the applied coupon in user session
            logger.info("Coupon {} applied for user {}", couponCode, userId);
            return true;
        } catch (Exception e) {
            logger.error("Error applying coupon {} for user {}: {}", couponCode, userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean removeCoupon(int userId) {
        try {
            // Remove coupon from user session/cart
            logger.info("Coupon removed for user {}", userId);
            return true;
        } catch (Exception e) {
            logger.error("Error removing coupon for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public double calculateDiscount(double cartTotal, Coupon coupon) {
        if (coupon == null || cartTotal <= 0) {
            return 0.0;
        }

        try {
            double discount = 0.0;
            
            if ("percentage".equalsIgnoreCase(coupon.getDiscountType())) {
                discount = cartTotal * (coupon.getDiscountValue() / 100.0);
                
                // Apply maximum discount limit if set
                if (coupon.getMaximumDiscountAmount() != null && discount > coupon.getMaximumDiscountAmount()) {
                    discount = coupon.getMaximumDiscountAmount();
                }
            } else {
                // Fixed amount discount
                discount = Math.min(coupon.getDiscountValue(), cartTotal);
            }
            
            return Math.round(discount * 100.0) / 100.0; // Round to 2 decimal places
        } catch (Exception e) {
            logger.error("Error calculating discount: {}", e.getMessage(), e);
            return 0.0;
        }
    }

    @Override
    public boolean isCouponValidForUser(int userId, Coupon coupon) {
        if (coupon == null || userId <= 0) {
            return false;
        }

        try {
            // Check if user has already used this coupon
            // This would typically involve checking order history
            // For now, we'll assume all coupons are valid for all users
            return isCouponActive(coupon);
        } catch (Exception e) {
            logger.error("Error checking coupon validity for user {}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isCouponValidForAmount(double cartAmount, Coupon coupon) {
        if (coupon == null || cartAmount <= 0) {
            return false;
        }

        try {
            // Check minimum order amount
            if (coupon.getMinimumOrderAmount() > 0 && cartAmount < coupon.getMinimumOrderAmount()) {
                logger.debug("Cart amount {} below minimum required {}", cartAmount, coupon.getMinimumOrderAmount());
                return false;
            }
            
            return isCouponActive(coupon);
        } catch (Exception e) {
            logger.error("Error checking coupon validity for amount {}: {}", cartAmount, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean hasUsageLimit(Coupon coupon) {
        return coupon != null && coupon.getUsageLimit() != null && coupon.getUsageLimit() > 0;
    }

    @Override
    public boolean isCouponExpired(Coupon coupon) {
        if (coupon == null) {
            return true;
        }

        try {
            java.util.Date now = new java.util.Date();
            
            // Check if coupon has started
            if (coupon.getValidFrom() != null && coupon.getValidFrom().after(now)) {
                return true;
            }
            
            // Check if coupon has expired
            if (coupon.getValidUntil() != null && coupon.getValidUntil().before(now)) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            logger.error("Error checking coupon expiration: {}", e.getMessage(), e);
            return true;
        }
    }

    @Override
    public boolean incrementCouponUsage(int couponId) {
        if (couponId <= 0) {
            return false;
        }

        try {
            Coupon coupon = couponDAO.getCouponById(couponId);
            if (coupon == null) {
                return false;
            }

            // Check usage limit
            if (hasUsageLimit(coupon) && coupon.getUsageCount() >= coupon.getUsageLimit()) {
                logger.warn("Coupon usage limit reached: {}", couponId);
                return false;
            }

            // Increment usage count
            coupon.setUsageCount(coupon.getUsageCount() + 1);
            return couponDAO.updateCoupon(coupon);
        } catch (Exception e) {
            logger.error("Error incrementing coupon usage {}: {}", couponId, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Coupon> getAvailableCoupons(int userId) {
        try {
            List<Coupon> allCoupons = couponDAO.getAllCoupons();
            List<Coupon> availableCoupons = new ArrayList<>();

            for (Coupon coupon : allCoupons) {
                if (isCouponActive(coupon) && isCouponValidForUser(userId, coupon)) {
                    availableCoupons.add(coupon);
                }
            }

            return availableCoupons;
        } catch (Exception e) {
            logger.error("Error getting available coupons for user {}: {}", userId, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<Coupon> getAllActiveCoupons() {
        try {
            List<Coupon> allCoupons = couponDAO.getAllCoupons();
            List<Coupon> activeCoupons = new ArrayList<>();

            for (Coupon coupon : allCoupons) {
                if (isCouponActive(coupon)) {
                    activeCoupons.add(coupon);
                }
            }

            return activeCoupons;
        } catch (Exception e) {
            logger.error("Error getting all active coupons: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean createCoupon(Coupon coupon) {
        if (coupon == null) {
            return false;
        }

        try {
            // Validate coupon data
            if (!validateCouponData(coupon)) {
                return false;
            }

            // Set default values
            if (coupon.getValidFrom() == null) {
                coupon.setValidFrom(new java.sql.Timestamp(System.currentTimeMillis()));
            }
            
            if (coupon.getValidUntil() == null) {
                // Default to 30 days from now
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                calendar.add(java.util.Calendar.DAY_OF_MONTH, 30);
                coupon.setValidUntil(new java.sql.Timestamp(calendar.getTimeInMillis()));
            }
            
            coupon.setUsageCount(0);
            coupon.setActive(true);

            return couponDAO.addCoupon(coupon);
        } catch (Exception e) {
            logger.error("Error creating coupon: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateCoupon(Coupon coupon) {
        if (coupon == null || coupon.getCouponId() <= 0) {
            return false;
        }

        try {
            if (!validateCouponData(coupon)) {
                return false;
            }

            return couponDAO.updateCoupon(coupon);
        } catch (Exception e) {
            logger.error("Error updating coupon {}: {}", coupon.getCouponId(), e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteCoupon(int couponId) {
        if (couponId <= 0) {
            return false;
        }

        try {
            return couponDAO.deleteCoupon(couponId);
        } catch (Exception e) {
            logger.error("Error deleting coupon {}: {}", couponId, e.getMessage(), e);
            return false;
        }
    }

    // Private helper methods
    private boolean isCouponActive(Coupon coupon) {
        return coupon != null && 
               coupon.isActive() && 
               !isCouponExpired(coupon) &&
               (!hasUsageLimit(coupon) || coupon.getUsageCount() < coupon.getUsageLimit());
    }

    private boolean validateCouponData(Coupon coupon) {
        if (coupon == null) {
            return false;
        }

        if (coupon.getCode() == null || coupon.getCode().trim().isEmpty()) {
            logger.error("Coupon code is required");
            return false;
        }

        if (coupon.getDiscountValue() <= 0) {
            logger.error("Coupon discount value must be positive");
            return false;
        }

        if ("percentage".equalsIgnoreCase(coupon.getDiscountType()) && coupon.getDiscountValue() > 100) {
            logger.error("Percentage discount cannot exceed 100%");
            return false;
        }

        if (coupon.getValidFrom() != null && coupon.getValidUntil() != null && 
            coupon.getValidFrom().after(coupon.getValidUntil())) {
            logger.error("Coupon valid from date must be before valid until date");
            return false;
        }

        return true;
    }
}
