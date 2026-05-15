/**
 * Authentication Module
 * Centralized authentication handling for API requests
 */

/**
 * Get authentication token from storage
 */
export function getAuthToken() {
  // Check for token in localStorage
  const token = localStorage.getItem('authToken');
  if (token) {
    return token;
  }
  
  // Check for token in sessionStorage
  const sessionToken = sessionStorage.getItem('authToken');
  if (sessionToken) {
    return sessionToken;
  }
  
  return null;
}

/**
 * Set authentication token in storage
 */
export function setAuthToken(token, persist = false) {
  if (persist) {
    localStorage.setItem('authToken', token);
  } else {
    sessionStorage.setItem('authToken', token);
  }
}

/**
 * Clear authentication token from storage
 */
export function clearAuthToken() {
  localStorage.removeItem('authToken');
  sessionStorage.removeItem('authToken');
}

/**
 * Check if user is authenticated
 */
export function isAuthenticated() {
  return !!getAuthToken();
}

/**
 * Add authentication headers to request config
 */
export function addAuthHeaders(config = {}) {
  const token = getAuthToken();
  if (token) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${token}`,
    };
  }
  return config;
}

/**
 * Handle token refresh
 * Returns promise that resolves with new token or rejects with error
 */
export async function refreshToken() {
  try {
    // This would call the refresh endpoint
    // For now, we'll just clear the token and force re-login
    clearAuthToken();
    return null;
  } catch (error) {
    console.error('Token refresh failed:', error);
    clearAuthToken();
    throw error;
  }
}
