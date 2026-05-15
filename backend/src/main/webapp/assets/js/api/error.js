/**
 * FashionStore - API Error Handling Utilities
 * Standardized error codes, messages, and error handling
 */

/**
 * API Error Codes
 */
export const ERROR_CODES = {
    NETWORK_ERROR: 'NETWORK_ERROR',
    TIMEOUT_ERROR: 'TIMEOUT_ERROR',
    SERVER_ERROR: 'SERVER_ERROR',
    AUTH_ERROR: 'AUTH_ERROR',
    SESSION_EXPIRED: 'SESSION_EXPIRED',
    CSRF_ERROR: 'CSRF_ERROR',
    VALIDATION_ERROR: 'VALIDATION_ERROR',
    NOT_FOUND: 'NOT_FOUND',
    CONFLICT: 'CONFLICT',
    RATE_LIMIT: 'RATE_LIMIT',
    UNKNOWN_ERROR: 'UNKNOWN_ERROR'
};

/**
 * API Error Messages
 */
export const ERROR_MESSAGES = {
    [ERROR_CODES.NETWORK_ERROR]: 'Network error. Please check your connection.',
    [ERROR_CODES.TIMEOUT_ERROR]: 'Request timeout. Please try again.',
    [ERROR_CODES.SERVER_ERROR]: 'Server error. Please try again later.',
    [ERROR_CODES.AUTH_ERROR]: 'Authentication failed. Please login.',
    [ERROR_CODES.SESSION_EXPIRED]: 'Session expired. Please login again.',
    [ERROR_CODES.CSRF_ERROR]: 'Security token expired. Please refresh the page.',
    [ERROR_CODES.VALIDATION_ERROR]: 'Invalid request data. Please check your input.',
    [ERROR_CODES.NOT_FOUND]: 'Resource not found.',
    [ERROR_CODES.CONFLICT]: 'Resource conflict. Please refresh and try again.',
    [ERROR_CODES.RATE_LIMIT]: 'Too many requests. Please wait and try again.',
    [ERROR_CODES.UNKNOWN_ERROR]: 'An unexpected error occurred.'
};

/**
 * API Error Class
 */
export class ApiError extends Error {
    constructor(message, code, statusCode, details = null) {
        super(message);
        this.name = 'ApiError';
        this.code = code || ERROR_CODES.UNKNOWN_ERROR;
        this.statusCode = statusCode || 0;
        this.details = details;
        this.timestamp = new Date().toISOString();
    }

    toJSON() {
        return {
            name: this.name,
            message: this.message,
            code: this.code,
            statusCode: this.statusCode,
            details: this.details,
            timestamp: this.timestamp
        };
    }
}

/**
 * Create API error from axios error
 */
export function createApiError(error, defaultMessage = ERROR_MESSAGES.UNKNOWN_ERROR) {
    let code = ERROR_CODES.UNKNOWN_ERROR;
    let message = defaultMessage;
    let statusCode = 0;

    if (!error.response) {
        // Network error
        if (error.code === 'ECONNABORTED') {
            code = ERROR_CODES.TIMEOUT_ERROR;
            message = ERROR_MESSAGES.TIMEOUT_ERROR;
        } else {
            code = ERROR_CODES.NETWORK_ERROR;
            message = ERROR_MESSAGES.NETWORK_ERROR;
        }
    } else {
        statusCode = error.response.status;

        // Handle HTTP status codes
        switch (statusCode) {
            case 400:
                code = ERROR_CODES.VALIDATION_ERROR;
                message = error.response.data?.message || ERROR_MESSAGES.VALIDATION_ERROR;
                break;
            case 401:
                code = ERROR_CODES.SESSION_EXPIRED;
                message = ERROR_MESSAGES.SESSION_EXPIRED;
                break;
            case 403:
                if (error.response.data?.includes('CSRF')) {
                    code = ERROR_CODES.CSRF_ERROR;
                    message = ERROR_MESSAGES.CSRF_ERROR;
                } else {
                    code = ERROR_CODES.AUTH_ERROR;
                    message = ERROR_MESSAGES.AUTH_ERROR;
                }
                break;
            case 404:
                code = ERROR_CODES.NOT_FOUND;
                message = ERROR_MESSAGES.NOT_FOUND;
                break;
            case 409:
                code = ERROR_CODES.CONFLICT;
                message = ERROR_MESSAGES.CONFLICT;
                break;
            case 429:
                code = ERROR_CODES.RATE_LIMIT;
                message = ERROR_MESSAGES.RATE_LIMIT;
                break;
            case 500:
            case 502:
            case 503:
            case 504:
                code = ERROR_CODES.SERVER_ERROR;
                message = ERROR_MESSAGES.SERVER_ERROR;
                break;
            default:
                code = ERROR_CODES.UNKNOWN_ERROR;
                message = error.response.data?.message || defaultMessage;
        }
    }

    return new ApiError(message, code, statusCode, {
        originalError: error,
        response: error.response,
        config: error.config
    });
}

/**
 * Handle API error globally
 */
export function handleApiError(error) {
    const apiError = createApiError(error);

    // Log error
    console.error('[API Error]', apiError);

    // Show user notification
    if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
        const type = apiError.code === ERROR_CODES.SERVER_ERROR ? 'error' : 'warning';
        FashionStore.showToast(apiError.message, type);
    }

    // Handle session expiry
    if (apiError.code === ERROR_CODES.SESSION_EXPIRED) {
        handleSessionExpiry();
    }

    // Handle CSRF error
    if (apiError.code === ERROR_CODES.CSRF_ERROR) {
        handleCsrfError();
    }

    return apiError;
}

/**
 * Handle session expiry
 */
function handleSessionExpiry() {
    sessionStorage.clear();

    if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
        FashionStore.showToast('Session expired. Redirecting to login...', 'warning');
    }

    setTimeout(() => {
        window.location.href = window.contextPath + '/login';
    }, 2000);
}

/**
 * Handle CSRF error
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
 * Check if error is retryable
 */
export function isRetryableError(error) {
    const apiError = createApiError(error);
    
    return [
        ERROR_CODES.NETWORK_ERROR,
        ERROR_CODES.TIMEOUT_ERROR,
        ERROR_CODES.SERVER_ERROR
    ].includes(apiError.code);
}

/**
 * Get user-friendly error message
 */
export function getUserFriendlyError(error) {
    const apiError = createApiError(error);
    return apiError.message;
}

// Export for non-module environments
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        ERROR_CODES,
        ERROR_MESSAGES,
        ApiError,
        createApiError,
        handleApiError,
        isRetryableError,
        getUserFriendlyError
    };
}

// Make available globally
window.APIErrorUtils = {
    ERROR_CODES,
    ERROR_MESSAGES,
    ApiError,
    createApiError,
    handleApiError,
    isRetryableError,
    getUserFriendlyError
};
