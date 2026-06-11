import { createContext, useContext, useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import adminApiClient from '../core/api/client.js';

const AuthContext = createContext(null);

/**
 * AuthContext - Centralized Authentication State Management
 * 
 * AUTHENTICATION ARCHITECTURE:
 * ============================
 * - JWT-based authentication for admin users
 * - Tokens stored in HTTP-only cookies (set by backend)
 * - Session check on mount to verify authentication status
 * - Automatic logout on token expiration
 * - Event-based logout for API failures
 * 
 * SECURITY FEATURES:
 * ===================
 * - No token storage in localStorage (prevents XSS)
 * - HTTP-only cookies (set by backend)
 * - Role validation (admin only)
 * - Automatic cleanup on logout
 * - Protected route integration
 * 
 * RACE CONDITION FIXES:
 * ======================
 * - Single session check per mount
 * - Debounced logout events
 * - Loading state prevents duplicate checks
 * - Cleanup on unmount
 */
export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const navigate = useNavigate();
  const sessionCheckRef = useRef(false);
  const logoutTimeoutRef = useRef(null);

  /**
   * Session check - verifies authentication status on mount
   * Uses single execution flag to prevent race conditions
   * Checks if user is logged in via JWT token
   */
  useEffect(() => {
    // Prevent duplicate session checks
    if (sessionCheckRef.current) {
      return;
    }
    sessionCheckRef.current = true;

    const checkSession = async () => {
      try {
        console.log('[AuthContext] Checking session...');
        // Check if user is authenticated via JWT token
        const response = await adminApiClient.get('me');
        
        if (response.data?.success && response.data?.data) {
          setUser(response.data.data);
          setIsAuthenticated(true);
          console.log('[AuthContext] Session check successful');
        } else {
          setUser(null);
          setIsAuthenticated(false);
          console.log('[AuthContext] Session check failed - no data');
        }
      } catch (err) {
        console.error('[AuthContext] Session check failed:', err);
        // Token might be expired or invalid
        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    checkSession();

    // Cleanup function
    return () => {
      if (logoutTimeoutRef.current) {
        clearTimeout(logoutTimeoutRef.current);
      }
    };
  }, []);

  /**
   * Handle logout event from API client
   * Debounced to prevent multiple rapid logouts
   */
  useEffect(() => {
    const handleLogoutEvent = () => {
      // Clear any existing logout timeout
      if (logoutTimeoutRef.current) {
        clearTimeout(logoutTimeoutRef.current);
      }

      // Debounce logout to prevent rapid successive calls
      logoutTimeoutRef.current = setTimeout(() => {
        setUser(null);
        setIsAuthenticated(false);
        navigate('/admin/login', { replace: true });
      }, 100);
    };

    window.addEventListener('auth:logout', handleLogoutEvent);

    return () => {
      window.removeEventListener('auth:logout', handleLogoutEvent);
      if (logoutTimeoutRef.current) {
        clearTimeout(logoutTimeoutRef.current);
      }
    };
  }, [navigate]);

  /**
   * Login function
   * Validates credentials and sets user state
   * Uses JWT-based authentication via /api/admin/login endpoint
   */
  const login = async (email, password) => {
    try {
      console.log('[AuthContext] Attempting login...');
      const response = await adminApiClient.post('login', { email, password });
      
      console.log('[AuthContext] Login response:', response);
      
      if (response.data?.success) {
        // Login successful - set user state
        if (response.data?.data) {
          setUser(response.data.data);
          setIsAuthenticated(true);
          console.log('[AuthContext] Login successful, user set:', response.data.data);
          return { ok: true };
        }
      }
      
      return { ok: false, message: response.data?.message || 'Login failed' };
    } catch (err) {
      console.error('[AuthContext] Login failed:', err);
      return { ok: false, message: err.response?.data?.message || err.message || 'Login failed' };
    }
  };

  /**
   * Logout function
   * Clears JWT tokens from backend and local state
   */
  const logout = async () => {
    try {
      await adminApiClient.post('logout');
    } catch (err) {
      console.error('Logout API call failed:', err);
    } finally {
      // Clear local state regardless of API call result
      setUser(null);
      setIsAuthenticated(false);
      navigate('/admin/login', { replace: true });
    }
  };

  const value = {
    user,
    loading,
    isAuthenticated,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

/**
 * useAuth hook - access authentication state
 */
export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
