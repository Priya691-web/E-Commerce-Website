/**
 * FashionStore - Admin Dashboard API Service
 * Centralized admin dashboard operations with standardized error handling
 */

import api from '../client.js';
import { handleApiError } from '../../error.js';

/**
 * Admin Dashboard API Service
 */
const AdminDashboardAPI = {
    /**
     * Get dashboard statistics
     */
    async getDashboardStats() {
        try {
            const response = await api.get('/api/admin/dashboard');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Get sales data
     */
    async getSalesData(params = {}) {
        try {
            const response = await api.get('/api/admin/stats', { params });
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
    },

    /**
     * Get top products
     */
    async getTopProducts(limit = 10) {
        try {
            const response = await api.get('/api/admin/products', { params: { limit, sort: 'popularity' } });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }
};

// Export for use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AdminDashboardAPI;
}

// Make available globally
window.AdminDashboardAPI = AdminDashboardAPI;
