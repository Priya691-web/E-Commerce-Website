/**
 * Centralized Constants Configuration
 * Single source of truth for all application constants
 */

// API Configuration
export const API_CONFIG = {
  BASE_URL: import.meta.env.VITE_API_BASE || '/api/admin',
  TIMEOUT: 30000,
  RETRY_ATTEMPTS: 3,
  RETRY_DELAY: 1000,
};

// API Endpoints
export const API_ENDPOINTS = {
  AUTH: {
    LOGIN: '/login',
    LOGOUT: '/logout',
    REGISTER: '/register',
    ME: '/me',
    REFRESH: '/refresh',
  },
  PRODUCTS: {
    LIST: '/products',
    CREATE: '/products',
    UPDATE: '/products/:id',
    DELETE: '/products/:id',
    DETAILS: '/products/:id',
  },
  ORDERS: {
    LIST: '/orders',
    DETAILS: '/orders/:id',
    UPDATE_STATUS: '/orders/:id/status',
  },
  CATEGORIES: {
    LIST: '/categories',
    CREATE: '/categories',
    UPDATE: '/categories/:id',
    DELETE: '/categories/:id',
  },
  USERS: {
    LIST: '/users',
    UPDATE: '/users/:id',
    DELETE: '/users/:id',
  },
  COUPONS: {
    LIST: '/coupons',
    CREATE: '/coupons',
    UPDATE: '/coupons/:id',
    DELETE: '/coupons/:id',
  },
  INVENTORY: {
    LIST: '/inventory',
    UPDATE: '/inventory/:id',
  },
  DASHBOARD: {
    STATS: '/dashboard/stats',
    CHARTS: '/dashboard/charts',
  },
  SETTINGS: {
    GENERAL: '/settings/general',
    UPDATE: '/settings',
  },
};

// Authentication Configuration
export const AUTH_CONFIG = {
  TOKEN_STORAGE_KEY: 'fashionstore_auth',
  REFRESH_THRESHOLD: 5 * 60 * 1000, // 5 minutes
  SESSION_CHECK_INTERVAL: 5 * 60 * 1000, // 5 minutes
};

// Pagination Configuration
export const PAGINATION_CONFIG = {
  DEFAULT_PAGE_SIZE: 20,
  PAGE_SIZE_OPTIONS: [10, 20, 50, 100],
  MAX_PAGE_SIZE: 100,
};

// Status Configuration
export const STATUS_CONFIG = {
  ORDER: {
    PENDING: 'pending',
    PROCESSING: 'processing',
    SHIPPED: 'shipped',
    DELIVERED: 'delivered',
    CANCELLED: 'cancelled',
    REFUNDED: 'refunded',
  },
  PRODUCT: {
    ACTIVE: 'active',
    INACTIVE: 'inactive',
    DRAFT: 'draft',
  },
  USER: {
    ACTIVE: 'active',
    INACTIVE: 'inactive',
    BLOCKED: 'blocked',
  },
  COUPON: {
    ACTIVE: 'active',
    INACTIVE: 'inactive',
    EXPIRED: 'expired',
  },
};

// Error Messages
export const ERROR_MESSAGES = {
  NETWORK_ERROR: 'Network error. Please check your connection.',
  SERVER_ERROR: 'Server error. Please try again later.',
  UNAUTHORIZED: 'You are not authorized to perform this action.',
  FORBIDDEN: 'Access denied.',
  NOT_FOUND: 'Resource not found.',
  VALIDATION_ERROR: 'Please check your input and try again.',
  UNKNOWN_ERROR: 'An unexpected error occurred.',
};

// Success Messages
export const SUCCESS_MESSAGES = {
  CREATE: 'Item created successfully',
  UPDATE: 'Item updated successfully',
  DELETE: 'Item deleted successfully',
  SAVE: 'Changes saved successfully',
  LOGIN: 'Login successful',
  LOGOUT: 'Logout successful',
};

// Toast Configuration
export const TOAST_CONFIG = {
  DEFAULT_DURATION: 4000,
  SUCCESS_DURATION: 3000,
  ERROR_DURATION: 6000,
  POSITION: 'top-right',
  MAX_TOASTS: 5,
};

// Form Configuration
export const FORM_CONFIG = {
  DEBOUNCE_DELAY: 300,
  VALIDATION_DELAY: 500,
  AUTO_SAVE_DELAY: 2000,
};

// Cache Configuration
export const CACHE_CONFIG = {
  DEFAULT_TTL: 5 * 60 * 1000, // 5 minutes
  API_CACHE_TTL: 2 * 60 * 1000, // 2 minutes
  IMAGE_CACHE_TTL: 24 * 60 * 60 * 1000, // 24 hours
};

// File Upload Configuration
export const UPLOAD_CONFIG = {
  MAX_FILE_SIZE: 5 * 1024 * 1024, // 5MB
  ALLOWED_IMAGE_TYPES: ['image/jpeg', 'image/png', 'image/webp', 'image/gif'],
  ALLOWED_DOCUMENT_TYPES: ['application/pdf', 'application/msword'],
  MAX_IMAGE_WIDTH: 2048,
  MAX_IMAGE_HEIGHT: 2048,
};

// Date/Time Configuration
export const DATETIME_CONFIG = {
  DATE_FORMAT: 'YYYY-MM-DD',
  TIME_FORMAT: 'HH:mm:ss',
  DATETIME_FORMAT: 'YYYY-MM-DD HH:mm:ss',
  TIMEZONE: 'UTC',
};

// Export as default for convenience
export default {
  API_CONFIG,
  API_ENDPOINTS,
  AUTH_CONFIG,
  PAGINATION_CONFIG,
  STATUS_CONFIG,
  ERROR_MESSAGES,
  SUCCESS_MESSAGES,
  TOAST_CONFIG,
  FORM_CONFIG,
  CACHE_CONFIG,
  UPLOAD_CONFIG,
  DATETIME_CONFIG,
};
