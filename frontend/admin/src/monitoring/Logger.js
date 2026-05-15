/**
 * Logger
 * Centralized logging utility for the application
 * Provides structured logging with different levels
 */

class Logger {
  constructor() {
    this.levels = {
      ERROR: 'error',
      WARN: 'warn',
      INFO: 'info',
      DEBUG: 'debug',
    };
  }

  /**
   * Log error
   */
  error(message, data = {}) {
    this.log(this.levels.ERROR, message, data);
  }

  /**
   * Log warning
   */
  warn(message, data = {}) {
    this.log(this.levels.WARN, message, data);
  }

  /**
   * Log info
   */
  info(message, data = {}) {
    this.log(this.levels.INFO, message, data);
  }

  /**
   * Log debug
   */
  debug(message, data = {}) {
    if (process.env.NODE_ENV === 'development') {
      this.log(this.levels.DEBUG, message, data);
    }
  }

  /**
   * Internal log method
   */
  log(level, message, data) {
    const logEntry = {
      timestamp: new Date().toISOString(),
      level,
      message,
      ...data,
    };

    // Log to console
    console[level](logEntry);

    // Send to monitoring service (e.g., Sentry)
    if (window.Sentry && level === this.levels.ERROR) {
      window.Sentry.captureException(new Error(message), {
        extra: data,
      });
    }
  }
}

// Create singleton instance
const logger = new Logger();

export default logger;
