/**
 * Authentication Provider
 * Manages authentication state and provides auth context to the application
 */

import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { AuthApi } from '../../api/client.js';

const AuthContext = createContext({});

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const token = localStorage.getItem('auth_token');
      if (!token) {
        setLoading(false);
        return;
      }

      const response = await AuthApi.me();
      
      setUser(response.data || response);
    } catch (err) {
      localStorage.removeItem('auth_token');
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  const login = async (credentials) => {
    try {
      setError(null);
      const response = await AuthApi.login(credentials.email, credentials.password);
      
      if (response.data?.token) {
        localStorage.setItem('auth_token', response.data.token);
      }
      setUser(response.data?.user || response.data);
      
      return { success: true };
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    }
  };

  const logout = useCallback(() => {
    localStorage.removeItem('auth_token');
    setUser(null);
    setError(null);
    AuthApi.logout().catch(() => {
      // Ignore logout errors
    });
  }, []);

  const register = async (userData) => {
    try {
      setError(null);
      const response = await AuthApi.register(userData);
      
      if (response.data?.token) {
        localStorage.setItem('auth_token', response.data.token);
      }
      setUser(response.data?.user || response.data);
      
      return { success: true };
    } catch (err) {
      setError(err.message);
      return { success: false, error: err.message };
    }
  };

  const value = {
    user,
    loading,
    error,
    login,
    logout,
    register,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
}

export default AuthProvider;
