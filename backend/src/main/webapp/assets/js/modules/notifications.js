/**
 * FashionStore - Notifications Module
 * Toast notifications, loading states, and skeleton loaders
 */

const FashionStoreNotifications = (function() {
    const toastContainer = document.createElement('div');
    toastContainer.className = 'toast-container';
    toastContainer.setAttribute('role', 'alert');
    toastContainer.setAttribute('aria-live', 'polite');
    
    function init() {
        document.body.appendChild(toastContainer);
    }
    
    function showToast(message, type = 'success', duration = 3000) {
        const toast = document.createElement('div');
        toast.className = `toast toast--${type}`;
        toast.setAttribute('role', 'alert');
        
        const icon = getToastIcon(type);
        
        toast.innerHTML = `
            <span class="toast__icon">${icon}</span>
            <span class="toast__message">${escapeHtml(message)}</span>
            <button class="toast__close" aria-label="Close notification">✕</button>
        `;
        
        toastContainer.appendChild(toast);
        
        // Close button functionality
        const closeBtn = toast.querySelector('.toast__close');
        closeBtn.addEventListener('click', () => dismissToast(toast));
        
        // Auto-dismiss after duration
        setTimeout(() => dismissToast(toast), duration);
        
        // Animate in
        requestAnimationFrame(() => {
            toast.classList.add('toast--visible');
        });
    }
    
    function dismissToast(toast) {
        if (!toast || !toast.parentNode) return;
        
        toast.classList.remove('toast--visible');
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }
    
    function getToastIcon(type) {
        const icons = {
            success: '✓',
            error: '✕',
            warning: '⚠',
            info: 'ℹ'
        };
        return icons[type] || icons.info;
    }
    
    function showLoading(element, text = 'Loading...') {
        if (!element) return;
        
        const loadingOverlay = document.createElement('div');
        loadingOverlay.className = 'loading-overlay';
        loadingOverlay.innerHTML = `
            <div class="loading-spinner"></div>
            <span class="loading-text">${escapeHtml(text)}</span>
        `;
        
        element.style.position = 'relative';
        element.appendChild(loadingOverlay);
        element.classList.add('is-loading');
    }
    
    function hideLoading(element) {
        if (!element) return;
        
        const loadingOverlay = element.querySelector('.loading-overlay');
        if (loadingOverlay) {
            loadingOverlay.remove();
        }
        element.classList.remove('is-loading');
    }
    
    function showSkeleton(container, count = 3) {
        if (!container) return;
        
        const originalContent = container.innerHTML;
        container.dataset.originalContent = originalContent;
        
        let skeletonHTML = '';
        for (let i = 0; i < count; i++) {
            skeletonHTML += `
                <div class="skeleton">
                    <div class="skeleton__image"></div>
                    <div class="skeleton__content">
                        <div class="skeleton__title"></div>
                        <div class="skeleton__text"></div>
                        <div class="skeleton__text"></div>
                    </div>
                </div>
            `;
        }
        
        container.innerHTML = skeletonHTML;
        container.classList.add('is-skeleton-loading');
    }
    
    function hideSkeleton(container, content) {
        if (!container) return;
        
        if (content) {
            container.innerHTML = content;
        } else if (container.dataset.originalContent) {
            container.innerHTML = container.dataset.originalContent;
        }
        
        container.classList.remove('is-skeleton-loading');
        delete container.dataset.originalContent;
    }
    
    function escapeHtml(text) {
        if (!text) return '';
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
    
    function cleanup() {
        // Remove all toasts
        while (toastContainer.firstChild) {
            toastContainer.removeChild(toastContainer.firstChild);
        }
        
        // Remove container
        if (toastContainer.parentNode) {
            toastContainer.parentNode.removeChild(toastContainer);
        }
    }
    
    // Public API
    return {
        init,
        showToast,
        dismissToast,
        showLoading,
        hideLoading,
        showSkeleton,
        hideSkeleton,
        cleanup
    };
})();

// Make notifications available globally for backward compatibility
if (typeof window.FashionStore === 'undefined') {
    window.FashionStore = {};
}
window.FashionStore.notifications = FashionStoreNotifications;
window.FashionStore.showToast = FashionStoreNotifications.showToast;
window.FashionStore.dismissToast = FashionStoreNotifications.dismissToast;
window.FashionStore.showLoading = FashionStoreNotifications.showLoading;
window.FashionStore.hideLoading = FashionStoreNotifications.hideLoading;
window.FashionStore.showSkeleton = FashionStoreNotifications.showSkeleton;
window.FashionStore.hideSkeleton = FashionStoreNotifications.hideSkeleton;

// Initialize on DOM ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', FashionStoreNotifications.init);
} else {
    FashionStoreNotifications.init();
}

// Export for ES6 modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FashionStoreNotifications;
}
