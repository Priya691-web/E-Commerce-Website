/**
 * AppProviders - Unified Provider Architecture
 * Centralized provider management to reduce nesting and complexity
 * Only client-state providers remain; server-state moved to React Query
 */

import { ThemeProvider } from '../../design-system/ThemeProvider.jsx';
import { AuthProvider } from '../../auth/AuthContext.jsx';

export default function AppProviders({ children }) {
  return (
    <ThemeProvider>
      <AuthProvider>
        {children}
      </AuthProvider>
    </ThemeProvider>
  );
}
