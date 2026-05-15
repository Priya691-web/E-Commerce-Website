/**
 * Request and Response Interceptors
 * Centralized interceptor logic for API client
 */

import { handleAuthError, handleNetworkError, handleServerError } from './errors.js';

/**
 * Setup request interceptors
 * Adds authentication tokens, logging, and validation
 */
export function setupRequestInterceptors(axiosInstance) {
  axiosInstance.interceptors.request.use(
    (config) => {
      // Ensure config is valid
      if (!config) {
        console.error('Invalid request config');
        return Promise.reject(new Error('Invalid request configuration'));
      }
      
      // Log request in development
      if (import.meta.env.DEV) {
        console.debug(`[API] ${config.method?.toUpperCase()} ${config.url}`);
      }
      
      return config;
    },
    (err) => {
      console.error('Request interceptor error:', err);
      return Promise.reject(err);
    }
  );
}

/**
 * Setup response interceptors
 * Handles error responses, auth redirects, and response validation
 */
export function setupResponseInterceptors(axiosInstance) {
  axiosInstance.interceptors.response.use(
    (res) => {
      // Validate response structure
      if (!res) {
        console.error('Invalid response received');
        return Promise.reject(new Error('Invalid response from server'));
      }
      
      // Log successful response in development
      if (import.meta.env.DEV) {
        console.debug(`[API] Response ${res.status} from ${res.config?.url}`);
      }
      
      return res;
    },
    (err) => {
      // Handle different error types
      if (!err) {
        console.error('Unknown error occurred');
        return Promise.reject(new Error('Unknown error'));
      }
      
      const status = err.response?.status;
      const message = err.response?.data?.message || err.message || 'Unknown error';
      
      // Log error details
      console.error(`[API Error] Status: ${status}, Message: ${message}`);
      
      // Handle specific error types
      if (status === 401) {
        return handleAuthError(err);
      }
      
      if (status === 403) {
        console.warn('Access forbidden - insufficient permissions');
      }
      
      if (status >= 500) {
        return handleServerError(err);
      }
      
      if (err.code === 'ECONNABORTED') {
        console.error('Request timeout');
        return Promise.reject(new Error('Request timeout - server not responding'));
      }
      
      if (!err.response) {
        return handleNetworkError(err);
      }
      
      return Promise.reject(err);
    }
  );
}
