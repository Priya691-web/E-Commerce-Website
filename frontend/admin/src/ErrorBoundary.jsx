/**
 * Error Boundary Component
 * Catches JavaScript errors in component tree and displays fallback UI
 * Provides centralized error handling for the entire application
 */

import React, { Component } from 'react';
import { useNavigate } from 'react-router-dom';

class ErrorBoundaryClass extends Component {
  constructor(props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  static getDerivedStateFromError(error) {
    // Update state so the next render will show the fallback UI
    return { hasError: true };
  }

  componentDidCatch(error, errorInfo) {
    // Log error to console
    console.error('Error Boundary caught an error:', error, errorInfo);

    // Log error to error reporting service (e.g., Sentry)
    if (window.Sentry) {
      window.Sentry.captureException(error, {
        extra: errorInfo,
      });
    }

    // Store error in state for display
    this.setState({
      error,
      errorInfo,
    });
  }

  handleReset = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  render() {
    if (this.state.hasError) {
      return <ErrorFallback error={this.state.error} onReset={this.handleReset} />;
    }

    return this.props.children;
  }
}

function ErrorFallback({ error, onReset }) {
  const navigate = useNavigate();

  const handleGoHome = () => {
    onReset();
    navigate('/');
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-ink-50 dark:bg-ink-900 px-4">
      <div className="max-w-md w-full bg-white dark:bg-ink-800 rounded-lg shadow-lg p-8">
        <div className="text-center">
          <div className="mx-auto flex items-center justify-center h-16 w-16 rounded-full bg-red-100 dark:bg-red-900 mb-4">
            <svg
              className="h-8 w-8 text-red-600 dark:text-red-400"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
              />
            </svg>
          </div>
          <h2 className="text-2xl font-bold text-ink-900 dark:text-ink-50 mb-2">
            Something went wrong
          </h2>
          <p className="text-ink-600 dark:text-ink-300 mb-6">
            We're sorry for the inconvenience. An unexpected error occurred.
          </p>
          
          {error && process.env.NODE_ENV === 'development' && (
            <details className="mb-6 text-left">
              <summary className="cursor-pointer text-sm font-medium text-ink-700 dark:text-ink-300 mb-2">
                Error Details
              </summary>
              <pre className="mt-2 p-4 bg-ink-100 dark:bg-ink-700 rounded text-xs text-ink-800 dark:text-ink-200 overflow-auto">
                {error.toString()}
                {error.stack}
              </pre>
            </details>
          )}
          
          <div className="flex gap-3 justify-center">
            <button
              onClick={onReset}
              className="px-4 py-2 bg-accent text-ink-900 rounded-lg hover:bg-accent-600 transition-colors"
            >
              Try Again
            </button>
            <button
              onClick={handleGoHome}
              className="px-4 py-2 bg-ink-200 dark:bg-ink-700 text-ink-900 dark:text-ink-50 rounded-lg hover:bg-ink-300 dark:hover:bg-ink-600 transition-colors"
            >
              Go Home
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default function ErrorBoundary({ children }) {
  return <ErrorBoundaryClass>{children}</ErrorBoundaryClass>;
}
