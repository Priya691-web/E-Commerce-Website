/**
 * Main App Component
 * Unified responsive rendering entry point.
 */

import AppProviders from './app/providers/AppProviders.jsx';
import AppRoutes from './routes/AppRoutes.jsx';

export default function App() {
  return (
    <AppProviders>
      <AppRoutes />
    </AppProviders>
  );
}
