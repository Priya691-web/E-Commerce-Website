/**
 * FashionStore - Cart API Service
 * Centralized cart operations with standardized error handling
 */

import api from '../client.js';
import { handleApiError } from '../error.js';

/**
 * Cart API Service
 */
const CartAPI = {
    /**
     * Get cart items
     */
    async getCart() {
        try {
            const response = await api.get('/cart?action=get');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Add item to cart
     */
    async addToCart(productId, quantity = 1, size = 'M') {
        try {
            const response = await api.post('/cart?action=add', {
                productId,
                quantity,
                size
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Update cart item quantity
     */
    async updateCartItem(cartItemId, quantity) {
        try {
            const response = await api.post('/cart?action=update', {
                cartItemId,
                currentQty: quantity
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Remove item from cart
     */
    async removeFromCart(cartItemId) {
        try {
            const response = await api.post('/cart?action=remove', {
                cartItemId
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Increase item quantity
     */
    async increaseQuantity(cartItemId, currentQty) {
        try {
            const response = await api.post('/cart?action=increase', {
                cartItemId,
                currentQty
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Decrease item quantity
     */
    async decreaseQuantity(cartItemId, currentQty) {
        try {
            const response = await api.post('/cart?action=decrease', {
                cartItemId,
                currentQty
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Apply coupon code
     */
    async applyCoupon(couponCode) {
        try {
            const response = await api.post('/cart?action=applyCoupon', {
                couponCode
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Save item for later
     */
    async saveForLater(cartItemId) {
        try {
            const response = await api.post('/cart?action=saveForLater', {
                cartItemId
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }
};

// Export for use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CartAPI;
}

// Make available globally
window.CartAPI = CartAPI;
