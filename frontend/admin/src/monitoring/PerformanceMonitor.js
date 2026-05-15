/**
 * Performance Monitor
 * Tracks application performance metrics and sends to monitoring service
 */

class PerformanceMonitor {
  constructor() {
    this.metrics = [];
    this.isEnabled = process.env.NODE_ENV === 'production';
  }

  /**
   * Track a metric
   */
  track(name, value, tags = {}) {
    if (!this.isEnabled) return;

    const metric = {
      name,
      value,
      timestamp: Date.now(),
      tags,
    };

    this.metrics.push(metric);

    // Send to monitoring service (e.g., Sentry, Datadog)
    if (window.Sentry) {
      window.Sentry.addBreadcrumb({
        category: 'performance',
        message: `${name}: ${value}`,
        level: 'info',
        data: tags,
      });
    }
  }

  /**
   * Track API request timing
   */
  trackApiCall(endpoint, duration, status) {
    this.track('api_call', duration, {
      endpoint,
      status,
    });
  }

  /**
   * Track page load time
   */
  trackPageLoad() {
    if (typeof window !== 'undefined' && window.performance) {
      const navigation = window.performance.getEntriesByType('navigation')[0];
      if (navigation) {
        this.track('page_load', navigation.loadEventEnd - navigation.fetchStart, {
          domContentLoaded: navigation.domContentLoadedEventEnd - navigation.fetchStart,
          firstPaint: navigation.responseStart - navigation.fetchStart,
        });
      }
    }
  }

  /**
   * Track user interaction
   */
  trackInteraction(action, duration) {
    this.track('user_interaction', duration, {
      action,
    });
  }

  /**
   * Get all metrics
   */
  getMetrics() {
    return this.metrics;
  }

  /**
   * Clear metrics
   */
  clearMetrics() {
    this.metrics = [];
  }
}

// Create singleton instance
const performanceMonitor = new PerformanceMonitor();

export default performanceMonitor;
