/**
 * FashionStore - Auth API Service
 * Centralized authentication operations with standardized error handling
 */

import api from '../client.js';
import { handleApiError } from '../error.js';

/**
 * Auth API Service
 */
const AuthAPI = {
    /**
     * Login user
     */
    async login(email, password) {
        try {
            const response = await api.post('/login', {
                email,
                password
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Register user
     */
    async register(userData) {
        try {
            const response = await api.post('/register', userData);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Logout user
     */
    async logout() {
        try {
            const response = await api.post('/logout');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Get current user
     */
    async getCurrentUser() {
        try {
            const response = await api.get('/account/profile');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Update user profile
     */
    async updateProfile(profileData) {
        try {
            const response = await api.put('/account/profile', profileData);
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    }
};

// Export for use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = AuthAPI;
}

// Make available globally
window.AuthAPI = AuthAPI;
