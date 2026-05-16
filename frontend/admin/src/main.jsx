import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App.jsx';
import ErrorBoundary from './ErrorBoundary.jsx';
import './styles/global.css';

/**
 * Root Entry Point
 * 
 * Provider hierarchy (inside → out):
 *   ErrorBoundary → BrowserRouter → App (→ AppProviders → DeviceRouter)
 *
 * IMPORTANT: Providers are ONLY in AppProviders.jsx.
 * Do NOT duplicate AuthProvider / ThemeProvider / ToastProvider here.
 */
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter basename="/admin">
      <ErrorBoundary>
        <App />
      </ErrorBoundary>
    </BrowserRouter>
  </React.StrictMode>
);
