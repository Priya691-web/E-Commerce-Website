/**
 * Error Handling Module
 * Centralized error handling for API errors
 */

/**
 * Handle authentication errors (401)
 * Redirects to login and dispatches logout event
 */
export function handleAuthError(err) {
  const pathname = window.location?.pathname || '';
  
  // Avoid bounce loops on the login screen itself
  if (!pathname.endsWith('/login') && !pathname.startsWith('/login?')) {
    // Dispatch event for AuthProvider to handle router-aware navigation
    try {
      window.dispatchEvent(new CustomEvent('auth:logout'));
    } catch (e) {
      console.error('Failed to dispatch logout event:', e);
    }
  }
  
  return Promise.reject(err);
}

/**
 * Handle network errors
 * No response received from server
 */
export function handleNetworkError(err) {
  console.error('Network error:', err.message);
  return Promise.reject(new Error('Network error - unable to reach server'));
}

/**
 * Handle server errors (500+)
 * Server-side errors
 */
export function handleServerError(err) {
  const status = err.response?.status;
  const message = err.response?.data?.message || err.message || 'Unknown error';
  console.error('Server error:', status, message);
  return Promise.reject(new Error(`Server error: ${message}`));
}

/**
 * Custom API Error class
 */
export class APIError extends Error {
  constructor(status, message, data = null) {
    super(message);
    this.name = 'APIError';
    this.status = status;
    this.data = data;
  }
}

/**
 * Create error from axios error
 */
export function createErrorFromAxios(err) {
  if (!err) {
    return new Error('Unknown error');
  }
  
  const status = err.response?.status;
  const message = err.response?.data?.message || err.message || 'Unknown error';
  const data = err.response?.data;
  
  return new APIError(status, message, data);
}
