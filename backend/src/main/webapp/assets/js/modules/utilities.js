/**
 * FashionStore - Utilities Module
 * Core utility functions for caching, events, and performance optimization
 */

const FashionStoreUtilities = (function() {
    // Performance Optimization - DOM Query Cache
    const cache = {
        _cache: {},
        _cacheAll: {},
        
        get: function(selector) {
            if (!this._cache[selector]) {
                this._cache[selector] = document.querySelector(selector);
            }
            return this._cache[selector];
        },
        
        getAll: function(selector) {
            if (!this._cacheAll[selector]) {
                this._cacheAll[selector] = document.querySelectorAll(selector);
            }
            return this._cacheAll[selector];
        },
        
        clear: function() {
            this._cache = {};
            this._cacheAll = {};
        }
    };
    
    // Event Delegation - Prevent Listener Duplication
    const events = {
        listeners: new Map(),
        
        on: function(element, event, handler, options = {}) {
            const key = `${element}_${event}`;
            if (this.listeners.has(key)) {
                element.removeEventListener(event, this.listeners.get(key));
            }
            element.addEventListener(event, handler, options);
            this.listeners.set(key, handler);
        },
        
        off: function(element, event) {
            const key = `${element}_${event}`;
            if (this.listeners.has(key)) {
                element.removeEventListener(event, this.listeners.get(key));
                this.listeners.delete(key);
            }
        },
        
        clear: function() {
            this.listeners.clear();
        }
    };
    
    // Request Animation Frame Throttling
    function rafThrottle(callback) {
        let rafId = null;
        return function(...args) {
            if (rafId) return;
            rafId = requestAnimationFrame(() => {
                callback.apply(this, args);
                rafId = null;
            });
        };
    }
    
    // Debounce Function
    function debounce(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func.apply(this, args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    }
    
    // Animate Value (for number animations)
    function animateValue(element, start, end, duration = 300) {
        if (!element) return;
        
        const range = end - start;
        const startTime = performance.now();
        
        function update(currentTime) {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            
            // Easing function (ease-out)
            const easedProgress = 1 - Math.pow(1 - progress, 3);
            const current = start + (range * easedProgress);
            
            element.textContent = Math.round(current);
            
            if (progress < 1) {
                requestAnimationFrame(update);
            }
        }
        
        requestAnimationFrame(update);
    }
    
    // Escape HTML to prevent XSS
    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    // Cleanup function
    function cleanup() {
        cache.clear();
        events.clear();
    }
    
    // Public API
    return {
        cache,
        events,
        rafThrottle,
        debounce,
        animateValue,
        escapeHtml,
        cleanup
    };
})();

// Make utilities available globally for backward compatibility
if (typeof window.FashionStore === 'undefined') {
    window.FashionStore = {};
}
window.FashionStore.cache = FashionStoreUtilities.cache;
window.FashionStore.events = FashionStoreUtilities.events;
window.FashionStore.rafThrottle = FashionStoreUtilities.rafThrottle;
window.FashionStore.debounce = FashionStoreUtilities.debounce;
window.FashionStore.animateValue = FashionStoreUtilities.animateValue;

// Export for ES6 modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FashionStoreUtilities;
}
