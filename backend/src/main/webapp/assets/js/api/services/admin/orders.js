/**
 * FashionStore - Admin Orders API Service
 * Centralized admin order operations with standardized error handling
 */

import api from '../client.js';
import { handleApiError } from '../../error.js';

/**
 * Admin Orders API Service
 */
const AdminOrdersAPI = {
    /**
     * Get all orders
     */
    async getOrders(params = {}) {
        try {
            const response = await api.get('/api/admin/orders', { params });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Get order by ID
     */
    async getOrder(orderId) {
        try {
            const response = await api.get(`/api/admin/orders/${orderId}`);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Update order status
     */
    async updateOrderStatus(orderId, status) {
        try {
            const response = await api.put(`/api/admin/orders/${orderId}/status`, { status });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Get recent orders
     */
    async getRecentOrders(limit = 10) {
        try {
            const response = await api.get('/api/admin/orders/recent', { params: { limit } });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }
};

// Export for use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AdminOrdersAPI;
}

// Make available globally
window.AdminOrdersAPI = AdminOrdersAPI;
