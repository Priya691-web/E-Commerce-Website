/**
 * ApiLogger - Centralized API error and success logging
 * Queues logs and periodically flushes them to the backend
 */
class ApiLogger {
  constructor() {
    this.logQueue = [];
    this.maxQueueSize = 100;
    this.flushInterval = 5000;
    this.flushTimer = null;
    this.isFlushing = false;
    this.startFlushTimer();
  }

  /**
   * Log API error with comprehensive details
   */
  logApiError(error, request) {
    try {
      // Validate inputs
      if (!error) {
        console.warn('ApiLogger.logApiError: error is required');
        return;
      }

      const errorData = {
        timestamp: new Date().toISOString(),
        url: request?.url || 'unknown',
        method: request?.method || 'unknown',
        status: error?.response?.status || 0,
        statusText: error?.response?.statusText || 'Network Error',
        message: error?.message || 'Unknown error',
        stack: error?.stack,
        userAgent: navigator.userAgent,
        pageUrl: window.location?.href || 'unknown',
      };

      console.error('API Error:', errorData);

      // Add to queue with size limit
      this.logQueue.push(errorData);
      if (this.logQueue.length > this.maxQueueSize) {
        this.logQueue.shift();
      }
    } catch (err) {
      console.error('ApiLogger.logApiError failed:', err);
    }
  }

  /**
   * Log successful API request
   */
  logApiSuccess(request, duration) {
    try {
      // Validate inputs
      if (!request) {
        console.warn('ApiLogger.logApiSuccess: request is required');
        return;
      }

      const successData = {
        timestamp: new Date().toISOString(),
        url: request?.url || 'unknown',
        method: request?.method || 'unknown',
        status: 200,
        duration: duration || 0,
      };

      console.debug('API Success:', successData);
    } catch (err) {
      console.error('ApiLogger.logApiSuccess failed:', err);
    }
  }

  /**
   * Start periodic flush timer
   */
  startFlushTimer() {
    try {
      if (this.flushTimer) {
        clearInterval(this.flushTimer);
      }
      
      this.flushTimer = setInterval(() => {
        this.flush();
      }, this.flushInterval);
    } catch (err) {
      console.error('ApiLogger.startFlushTimer failed:', err);
    }
  }

  /**
   * Flush queued logs to backend
   */
  async flush() {
    try {
      // Prevent concurrent flushes
      if (this.isFlushing || this.logQueue.length === 0) {
        return;
      }

      this.isFlushing = true;
      const errorsToLog = [...this.logQueue];
      this.logQueue = [];

      // Validate data before sending
      if (!Array.isArray(errorsToLog) || errorsToLog.length === 0) {
        this.isFlushing = false;
        return;
      }

      const response = await fetch('/api/log/errors', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ errors: errorsToLog }),
        timeout: 5000, // 5 second timeout
      });

      // Validate response
      if (!response || !response.ok) {
        console.warn(`Failed to flush logs: ${response?.status || 'unknown'}`);
        // Re-queue logs on failure
        this.logQueue.unshift(...errorsToLog);
      }
    } catch (err) {
      console.error('ApiLogger.flush failed:', err);
      // Re-queue logs on error
      if (this.logQueue.length < this.maxQueueSize) {
        this.logQueue.unshift(...errorsToLog);
      }
    } finally {
      this.isFlushing = false;
    }
  }

  /**
   * Cleanup on page unload
   */
  destroy() {
    try {
      if (this.flushTimer) {
        clearInterval(this.flushTimer);
        this.flushTimer = null;
      }
      
      // Final flush before unload
      if (this.logQueue.length > 0) {
        this.flush();
      }
    } catch (err) {
      console.error('ApiLogger.destroy failed:', err);
    }
  }
}

// Create singleton instance
const apiLogger = new ApiLogger();

// Cleanup on page unload
if (typeof window !== 'undefined') {
  window.addEventListener('beforeunload', () => {
    apiLogger.destroy();
  });
}

export default apiLogger;
