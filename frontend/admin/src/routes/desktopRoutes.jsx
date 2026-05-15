/**
 * Desktop Routes Configuration
 * Desktop-specific route definitions
 * No layout wrappers - layout is handled by DesktopLayout component
 */

import { lazy } from 'react';
import { Navigate } from 'react-router-dom';
import { protectedRoutes } from './protectedRoutes.jsx';

// Lazy load public pages
const Login = lazy(() => import('../pages/Login.jsx'));
const Register = lazy(() => import('../pages/Register.jsx'));

// Fallback component for lazy loading
function RouteFallback() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-ink-50 dark:bg-ink-900">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-ink-600 dark:text-ink-300 text-sm">Loading...</p>
      </div>
    </div>
  );
}

// Error boundary fallback
function ErrorFallback() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-ink-50 dark:bg-ink-900">
      <div className="text-center">
        <h1 className="text-2xl font-bold text-ink-900 dark:text-white mb-2">Something went wrong</h1>
        <p className="text-ink-600 dark:text-ink-300">Please try refreshing the page</p>
      </div>
    </div>
  );
}

export const desktopRoutes = [
  // Public routes - no layout
  {
    path: '/login',
    element: Login,
    protected: false,
  },
  {
    path: '/register',
    element: Register,
    protected: false,
  },
  // Protected routes - with layout
  ...protectedRoutes.map((route) => ({
    ...route,
    protected: true,
  })),
  // Catch-all - redirect to dashboard
  {
    path: '*',
    element: () => <Navigate to="/admin/dashboard" replace />,
    protected: false,
  },
];

export { RouteFallback, ErrorFallback };
