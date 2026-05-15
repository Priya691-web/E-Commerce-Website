/**
 * FashionStore - Admin Products API Service
 * Centralized admin product operations with standardized error handling
 */

import api from '../client.js';
import { handleApiError } from '../../error.js';

/**
 * Admin Products API Service
 */
const AdminProductsAPI = {
    /**
     * Get all products
     */
    async getProducts(params = {}) {
        try {
            const response = await api.get('/api/admin/products', { params });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Get product by ID
     */
    async getProduct(productId) {
        try {
            const response = await api.get(`/api/admin/products/${productId}`);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Create product
     */
    async createProduct(productData) {
        try {
            const response = await api.post('/api/admin/products', productData);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Update product
     */
    async updateProduct(productId, productData) {
        try {
            const response = await api.put(`/api/admin/products/${productId}`, productData);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Delete product
     */
    async deleteProduct(productId) {
        try {
            const response = await api.delete(`/api/admin/products/${productId}`);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }
};

// Export for use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AdminProductsAPI;
}

// Make available globally
window.AdminProductsAPI = AdminProductsAPI;
