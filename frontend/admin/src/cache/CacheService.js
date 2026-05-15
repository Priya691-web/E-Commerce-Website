/**
 * Cache Service
 * Provides client-side caching for API responses and data
 * Implements cache strategies and invalidation
 */

class CacheService {
  constructor() {
    this.cache = new Map();
    this.defaultTTL = 5 * 60 * 1000; // 5 minutes
  }

  /**
   * Get value from cache
   */
  get(key) {
    const cached = this.cache.get(key);
    if (!cached) return null;

    if (Date.now() > cached.expiry) {
      this.cache.delete(key);
      return null;
    }

    return cached.value;
  }

  /**
   * Set value in cache
   */
  set(key, value, ttl = this.defaultTTL) {
    this.cache.set(key, {
      value,
      expiry: Date.now() + ttl,
    });
  }

  /**
   * Delete value from cache
   */
  delete(key) {
    this.cache.delete(key);
  }

  /**
   * Clear all cache
   */
  clear() {
    this.cache.clear();
  }

  /**
   * Clear cache by pattern
   */
  clearPattern(pattern) {
    const regex = new RegExp(pattern);
    for (const key of this.cache.keys()) {
      if (regex.test(key)) {
        this.cache.delete(key);
      }
    }
  }

  /**
   * Get or set cache (cache-aside pattern)
   */
  async getOrSet(key, fetchFn, ttl = this.defaultTTL) {
    const cached = this.get(key);
    if (cached !== null) return cached;

    const value = await fetchFn();
    this.set(key, value, ttl);
    return value;
  }

  /**
   * Cache size
   */
  get size() {
    return this.cache.size;
  }

  /**
   * Clean expired entries
   */
  cleanExpired() {
    const now = Date.now();
    for (const [key, cached] of this.cache.entries()) {
      if (now > cached.expiry) {
        this.cache.delete(key);
      }
    }
  }
}

// Create singleton instance
const cacheService = new CacheService();

// Clean expired entries every minute
setInterval(() => {
  cacheService.cleanExpired();
}, 60 * 1000);

export default cacheService;
