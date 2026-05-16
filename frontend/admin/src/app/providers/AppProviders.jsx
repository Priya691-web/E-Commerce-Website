/**
 * AppProviders - Unified Provider Architecture
 * Single source of truth for ALL application providers.
 * main.jsx provides ONLY: BrowserRouter + ErrorBoundary.
 * Everything else lives here.
 */

import { ThemeProvider } from '../../auth/ThemeContext.jsx';
import { AuthProvider } from '../../auth/AuthContext.jsx';
import { ToastProvider } from '../../context/ToastContext.jsx';

export default function AppProviders({ children }) {
  return (
    <ThemeProvider>
      <ToastProvider>
        <AuthProvider>
          {children}
        </AuthProvider>
      </ToastProvider>
    </ThemeProvider>
  );
}
