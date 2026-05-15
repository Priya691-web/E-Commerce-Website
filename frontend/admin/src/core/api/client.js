/**
 * Centralized API Client
 * Enterprise-grade axios instance with configuration
 */

import axios from 'axios';
import { setupRequestInterceptors } from './interceptors.js';
import { setupResponseInterceptors } from './interceptors.js';

// In production/Docker, the nginx proxy handles /api routing,
// so we use a relative base URL. In dev (Vite), the proxy forwards to localhost.
// VITE_API_BASE env var can override for custom setups.
const API_BASE = import.meta.env.VITE_API_BASE || '/api/admin';

// Validate API base URL
if (!API_BASE || typeof API_BASE !== 'string') {
  console.error('Invalid API_BASE configuration:', API_BASE);
}

/**
 * Create centralized axios instance
 * Single source of truth for all API communication
 */
const apiClient = axios.create({
  baseURL: API_BASE,
  withCredentials: true, // include JSESSIONID cookie
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

// Setup interceptors
setupRequestInterceptors(apiClient);
setupResponseInterceptors(apiClient);

export default apiClient;
