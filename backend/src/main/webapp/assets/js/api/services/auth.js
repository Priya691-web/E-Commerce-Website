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
            }, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
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
            const response = await api.post('/register', userData, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });
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
     * Check session status
     */
    async checkSession() {
        try {
            const response = await api.get('/auth/status');
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Request password reset
     */
    async requestPasswordReset(email) {
        try {
            const response = await api.post('/password/reset', {
                email
            });
            return response.data;
        } catch (error) {
            throw handleApiError(error);
        }
    },

    /**
     * Reset password with token
     */
    async resetPassword(token, newPassword) {
        try {
            const response = await api.post('/password/reset/confirm', {
                token,
                newPassword
            });
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
