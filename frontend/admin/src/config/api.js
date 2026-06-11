/**
 * Centralized API Configuration
 * Single source of truth for all API-related configuration
 */

import { API_CONFIG, API_ENDPOINTS } from './constants.js';

/**
 * Build full API URL from endpoint
 * @param {string} endpoint - API endpoint path
 * @param {Object} params - URL parameters
 * @returns {string} Full API URL
 */
export const buildApiUrl = (endpoint, params = {}) => {
  let url = `${API_CONFIG.BASE_URL}${endpoint}`;
  
  // Replace path parameters
  Object.keys(params).forEach(key => {
    url = url.replace(`:${key}`, params[key]);
  });
  
  return url;
};

/**
 * Build URL with query parameters
 * @param {string} baseUrl - Base URL
 * @param {Object} queryParams - Query parameters
 * @returns {string} URL with query parameters
 */
export const buildUrlWithQuery = (baseUrl, queryParams = {}) => {
  const url = new URL(baseUrl, window.location.origin);
  Object.keys(queryParams).forEach(key => {
    if (queryParams[key] !== undefined && queryParams[key] !== null) {
      url.searchParams.append(key, queryParams[key]);
    }
  });
  return url.toString();
};

/**
 * Standard API response validator
 * @param {Object} response - API response
 * @returns {boolean} True if response is valid
 */
export const isValidResponse = (response) => {
  return response && 
         typeof response === 'object' && 
         'success' in response &&
         'data' in response;
};

/**
 * Extract error message from API response
 * @param {Object} error - Error object
 * @returns {string} Error message
 */
export const extractErrorMessage = (error) => {
  if (error.response?.data?.message) {
    return error.response.data.message;
  }
  if (error.response?.data?.error) {
    return error.response.data.error;
  }
  if (error.message) {
    return error.message;
  }
  return 'An unexpected error occurred';
};

/**
 * Standard API request configuration
 * @param {Object} options - Request options
 * @returns {Object} Axios request configuration
 */
export const getRequestConfig = (options = {}) => {
  return {
    timeout: API_CONFIG.TIMEOUT,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };
};

export default {
  buildApiUrl,
  buildUrlWithQuery,
  isValidResponse,
  extractErrorMessage,
  getRequestConfig,
  API_CONFIG,
  API_ENDPOINTS,
};
