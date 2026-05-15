/**
 * FashionStore - Cart API Service
 * Centralized cart operations with standardized error handling
 */

import api from '../client.js';
import { handleApiError, isRetryableError } from '../error.js';

/**
 * Cart API Service
 */
const CartAPI = {
    /**
     * Get cart data
     */
    async getCart() {
        try {
            const response = await api.get('/cart/api');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Add item to cart
     */
    async addToCart(productId, size = 'M', quantity = 1) {
        try {
            const response = await api.post('/cart/api/add', {
                productId,
                size,
                quantity
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
            const response = await api.post('/cart/api/update', {
                cartItemId,
                quantity
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Remove item from cart
     */
    async removeCartItem(cartItemId) {
        try {
            const response = await api.post('/cart/api/remove', {
                cartItemId
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
            const response = await api.post('/cart', {
                action: 'applyCoupon',
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
            const response = await api.post('/cart', {
                action: 'saveForLater',
                cartItemId
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Get cart summary
     */
    async getCartSummary() {
        try {
            const response = await api.get('/cart/api');
            const data = response.data;
            return {
                totalItems: data.cartCount || 0,
                subtotal: data.cartTotal || 0,
                items: data.cartItems || []
            };
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
