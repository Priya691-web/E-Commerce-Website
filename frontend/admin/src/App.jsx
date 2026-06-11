/**
 * Main App Component
 * Unified responsive rendering entry point.
 */

import React from 'react';
import AppProviders from './app/providers/AppProviders.jsx';
import AppRoutes from './routes/AppRoutes.jsx';

console.log('[Admin App] Rendering App component');

export default function App() {
  console.log('[Admin App] App component render');
  try {
    return (
      <AppProviders>
        <AppRoutes />
      </AppProviders>
    );
  } catch (error) {
    console.error('[Admin App] Error rendering App:', error);
    return <div>Error loading admin panel: {error.message}</div>;
  }
}
