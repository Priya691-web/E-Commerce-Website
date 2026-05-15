/**
 * FashionStore - Centralized API Client
 * Axios instance with base configuration, interceptors, and error handling
 */

// Check if axios is available, if not provide a fallback
const axios = window.axios || (function() {
    // Simple fetch-based fallback if axios is not loaded
    return {
        get: (url, config) => fetch(url, { ...config, method: 'GET' }).then(res => {
            const data = res.json();
            return res.ok ? data : Promise.reject({ response: { status: res.status, data } });
        }),
        post: (url, data, config) => fetch(url, { ...config, method: 'POST', body: JSON.stringify(data), headers: { 'Content-Type': 'application/json', ...config?.headers } }).then(res => {
            const responseData = res.json();
            return res.ok ? responseData : Promise.reject({ response: { status: res.status, data: responseData } });
        }),
        put: (url, data, config) => fetch(url, { ...config, method: 'PUT', body: JSON.stringify(data), headers: { 'Content-Type': 'application/json', ...config?.headers } }).then(res => {
            const responseData = res.json();
            return res.ok ? responseData : Promise.reject({ response: { status: res.status, data: responseData } });
        }),
        delete: (url, config) => fetch(url, { ...config, method: 'DELETE' }).then(res => {
            const data = res.json();
            return res.ok ? data : Promise.reject({ response: { status: res.status, data } });
        }),
        create: (config) => ({
            ...axios,
            defaults: config,
            interceptors: {
                request: { use: (fn) => { /* No-op for fallback */ } },
                response: { use: (fn) => { /* No-op for fallback */ } }
            }
        })
    };
})();

// API Configuration
const API_CONFIG = {
    baseURL: window.contextPath || '',
    timeout: 30000,
    headers: {
        'Content-Type': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    },
    retryConfig: {
        retries: 3,
        retryDelay: 1000,
        retryCondition: (error) => {
            // Retry on network errors and 5xx errors
            return !error.response && (error.code === 'ECONNABORTED' || error.code === 'ENETDOWN') ||
                   error.response && error.response.status >= 500;
        }
    }
};

// Create axios instance
const apiClient = axios.create(API_CONFIG);

// Request interceptor
apiClient.interceptors.request.use(
    (config) => {
        // Add CSRF token if available
        const csrfToken = document.querySelector('meta[name="csrf-token"]')?.getAttribute('content');
        if (csrfToken) {
            config.headers['X-CSRF-Token'] = csrfToken;
        }

        // Add timestamp to prevent caching
        if (config.method === 'GET') {
            config.params = {
                ...config.params,
                _t: Date.now()
            };
        }

        // Log request in development
        if (window.DEBUG_MODE) {
            console.log(`[API Request] ${config.method?.toUpperCase()} ${config.url}`, config.data || config.params);
        }

        return config;
    },
    (error) => {
        console.error('[API Request Error]', error);
        return Promise.reject(error);
    }
);

// Response interceptor
apiClient.interceptors.response.use(
    (response) => {
        // Log response in development
        if (window.DEBUG_MODE) {
            console.log(`[API Response] ${response.config.url}`, response.data);
        }

        // Normalize response structure
        return normalizeResponse(response);
    },
    async (error) => {
        // Log error
        console.error('[API Response Error]', error);

        // Handle session expiry
        if (error.response?.status === 401) {
            handleSessionExpiry();
            return Promise.reject(createApiError(error, 'Session expired. Please login again.', 401));
        }

        // Handle CSRF errors
        if (error.response?.status === 403 && error.response?.data?.includes('CSRF')) {
            handleCsrfError();
            return Promise.reject(createApiError(error, 'Security token expired. Please refresh the page.', 403));
        }

        // Handle server errors
        if (error.response?.status >= 500) {
            return Promise.reject(createApiError(error, 'Server error. Please try again later.', error.response.status));
        }

        // Handle network errors
        if (!error.response) {
            return Promise.reject(createApiError(error, 'Network error. Please check your connection.', 0));
        }

        // Handle other errors
        return Promise.reject(createApiError(error, error.response?.data?.message || 'An error occurred', error.response?.status || 0));
    }
);

/**
 * Normalize response structure
 */
function normalizeResponse(response) {
    const data = response.data;

    // If response already has standard structure, return as-is
    if (data && typeof data === 'object' && ('success' in data || 'status' in data)) {
        return response;
    }

    // Wrap plain data in standard structure
    return {
        ...response,
        data: {
            success: true,
            status: 'success',
            data: data
        }
    };
}

/**
 * Create standardized API error
 */
function createApiError(originalError, message, statusCode) {
    const error = new Error(message);
    error.name = 'ApiError';
    error.statusCode = statusCode;
    error.originalError = originalError;
    error.response = originalError.response;
    error.config = originalError.config;
    return error;
}

/**
 * Handle session expiry
 */
function handleSessionExpiry() {
    // Clear session storage
    sessionStorage.clear();

    // Show notification
    if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
        FashionStore.showToast('Session expired. Redirecting to login...', 'warning');
    }

    // Redirect to login after delay
    setTimeout(() => {
        window.location.href = window.contextPath + '/login';
    }, 2000);
}

/**
 * Handle CSRF errors
 */
function handleCsrfError() {
    if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
        FashionStore.showToast('Security token expired. Refreshing page...', 'warning');
    }

    setTimeout(() => {
        window.location.reload();
    }, 2000);
}

/**
 * Retry logic with exponential backoff
 */
async function retryRequest(config, retryCount = 0) {
    try {
        return await apiClient(config);
    } catch (error) {
        if (retryCount < API_CONFIG.retryConfig.retries && 
            API_CONFIG.retryConfig.retryCondition(error)) {
            const delay = API_CONFIG.retryConfig.retryDelay * Math.pow(2, retryCount);
            await new Promise(resolve => setTimeout(resolve, delay));
            return retryRequest(config, retryCount + 1);
        }
        throw error;
    }
}

/**
 * Enhanced API client with retry support
 */
const api = {
    get: (url, config) => retryRequest({ ...apiClient.defaults, ...config, method: 'GET', url }),
    post: (url, data, config) => retryRequest({ ...apiClient.defaults, ...config, method: 'POST', url, data }),
    put: (url, data, config) => retryRequest({ ...apiClient.defaults, ...config, method: 'PUT', url, data }),
    delete: (url, config) => retryRequest({ ...apiClient.defaults, ...config, method: 'DELETE', url }),
    patch: (url, data, config) => retryRequest({ ...apiClient.defaults, ...config, method: 'PATCH', url, data }),
    
    // Direct access to axios instance for advanced use cases
    instance: apiClient,
    
    // Configuration
    setBaseURL: (baseURL) => {
        apiClient.defaults.baseURL = baseURL;
    },
    setHeader: (key, value) => {
        apiClient.defaults.headers[key] = value;
    },
    removeHeader: (key) => {
        delete apiClient.defaults.headers[key];
    }
};

// Export for use
if (typeof module !== 'undefined' && module.exports) {
    module.exports = api;
}

// Make available globally for backward compatibility
window.FashionStoreAPI = api;
