import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';

/**
 * ProtectedRoute — used as a layout route element:
 *   <Route element={<ProtectedRoute />}>
 *     <Route path="dashboard" ... />
 *   </Route>
 *
 * Must render <Outlet /> (not children) so nested routes render.
 */
export default function ProtectedRoute() {
  const { user, loading } = useAuth();
  const location = useLocation();

  // Show loading spinner while checking authentication
  if (loading) {
    return (
      <div
        className="min-h-screen flex items-center justify-center"
        style={{ backgroundColor: 'var(--color-bg)' }}
        role="status"
        aria-live="polite"
        aria-label="Loading authentication"
      >
        <div className="text-center">
          <div
            className="w-12 h-12 border-4 rounded-full animate-spin mx-auto mb-4"
            style={{
              borderColor: 'var(--color-border)',
              borderTopColor: 'var(--color-primary)',
            }}
          />
          <p style={{ color: 'var(--color-text-secondary)', fontSize: '0.875rem' }}>
            Verifying access...
          </p>
        </div>
      </div>
    );
  }

  // Redirect to login if not authenticated
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  // Render nested routes via Outlet
  return <Outlet />;
}
