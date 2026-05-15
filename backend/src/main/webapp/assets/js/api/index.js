/**
 * FashionStore - API Layer Index
 * Centralized export point for all API services
 */

// API Client
import api from './client.js';

// Error Handling
import { ERROR_CODES, ERROR_MESSAGES, ApiError, createApiError, handleApiError, isRetryableError, getUserFriendlyError } from './error.js';

// API Services
import CartAPI from './services/cart.js';
import WishlistAPI from './services/wishlist.js';
import ProductAPI from './services/product.js';
import AuthAPI from './services/auth.js';
import AdminProductsAPI from './services/admin/products.js';
import AdminOrdersAPI from './services/admin/orders.js';
import AdminDashboardAPI from './services/admin/dashboard.js';

// Export all services
export {
    // Client
    api,
    
    // Error Handling
    ERROR_CODES,
    ERROR_MESSAGES,
    ApiError,
    createApiError,
    handleApiError,
    isRetryableError,
    getUserFriendlyError,
    
    // API Services
    CartAPI,
    WishlistAPI,
    ProductAPI,
    AuthAPI,
    AdminProductsAPI,
    AdminOrdersAPI,
    AdminDashboardAPI
};

// Default export for convenience
export default {
    api,
    CartAPI,
    WishlistAPI,
    ProductAPI,
    AuthAPI,
    AdminProductsAPI,
    AdminOrdersAPI,
    AdminDashboardAPI
};

// Make available globally for non-module environments
if (typeof window !== 'undefined') {
    window.FashionStoreAPI = {
        api,
        ERROR_CODES,
        ERROR_MESSAGES,
        CartAPI,
        WishlistAPI,
        ProductAPI,
        AuthAPI,
        AdminProductsAPI,
        AdminOrdersAPI,
        AdminDashboardAPI
    };
}
