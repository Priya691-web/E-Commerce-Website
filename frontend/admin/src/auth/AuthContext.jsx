import { createContext, useContext, useEffect, useState, useCallback, useRef } from 'react';
import { AuthApi } from '../api/client.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const mountedRef = useRef(true);

  // On mount, check whether an admin session already exists (page refresh case).
  useEffect(() => {
    mountedRef.current = true;
    (async () => {
      try {
        setError(null);
        const response = await AuthApi.me();
        
        if (mountedRef.current) {
          // Validate response structure
          if (!response || !response.data) {
            console.warn('Invalid response from AuthApi.me()');
            setUser(null);
            return;
          }
          
          const { data } = response;
          
          // Check for success flag and user data
          if (data?.success && data.data) {
            setUser(data.data);
          } else if (data?.data) {
            // Some backends may not include success flag
            setUser(data.data);
          } else {
            setUser(null);
          }
        }
      } catch (err) {
        // 401 -> not authenticated (expected on first load)
        // Other errors should be logged but not break the app
        if (mountedRef.current) {
          if (err.response?.status !== 401) {
            console.error('Error checking auth status:', err.message);
            setError(err.message);
          }
          setUser(null);
        }
      } finally {
        if (mountedRef.current) {
          setLoading(false);
        }
      }
    })();
    
    return () => {
      mountedRef.current = false;
    };
  }, []);

  // Listen for 401 errors from API interceptor
  useEffect(() => {
    const handleLogout = () => {
      if (mountedRef.current) {
        setUser(null);
        setError(null);
      }
    };
    
    window.addEventListener('auth:logout', handleLogout);
    return () => window.removeEventListener('auth:logout', handleLogout);
  }, []);

  const login = useCallback(async (email, password) => {
    try {
      // Validate inputs
      if (!email || !password) {
        return { ok: false, message: 'Email and password are required' };
      }
      
      setError(null);
      const response = await AuthApi.login(email, password);
      
      // Validate response
      if (!response || !response.data) {
        return { ok: false, message: 'Invalid response from server' };
      }
      
      const { data } = response;
      
      // Check for success and user data
      if (data?.success && data.data) {
        setUser(data.data);
        return { ok: true };
      } else if (data?.data) {
        // Some backends may not include success flag
        setUser(data.data);
        return { ok: true };
      }
      
      // Login failed
      const message = data?.message || 'Login failed';
      setError(message);
      return { ok: false, message };
    } catch (err) {
      const message = err.response?.data?.message || err.message || 'Login failed';
      setError(message);
      console.error('Login error:', err);
      return { ok: false, message };
    }
  }, []);

  const logout = useCallback(async () => {
    try {
      // Attempt to notify backend
      await AuthApi.logout();
    } catch (err) {
      // Log but don't fail - we still want to clear local state
      console.error('Logout API error:', err);
    } finally {
      // Always clear local state
      setUser(null);
      setError(null);
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, loading, error, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
}
