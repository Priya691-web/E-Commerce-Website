/**
 * FashionStore - Wishlist Manager
 * Centralized wishlist operations for MVC frontend
 * 
 * Handles:
 * - Add/remove items from wishlist
 * - Check wishlist status
 * - Update UI state
 * - Persist wishlist state
 * - Update navbar count
 */

const WishlistManager = (function() {
    const contextPath = window.contextPath || '';
    let wishlistItems = new Set();
    let initialized = false;
    
    // Track event listeners for proper cleanup
    const eventListeners = [];
    
    function init() {
        if (initialized) {
            console.warn('WishlistManager already initialized');
            return;
        }
        
        // Load wishlist state from localStorage
        loadWishlistState();
        
        // Setup event delegation for wishlist buttons
        setupEventDelegation();
        
        // Check authentication status and fetch wishlist if logged in
        const isLoggedIn = document.getElementById('user-logged-in')?.value === 'true';
        if (isLoggedIn) {
            fetchWishlist();
        }
        
        initialized = true;
        console.log('WishlistManager initialized');
    }
    
    function loadWishlistState() {
        try {
            const saved = localStorage.getItem('wishlistItems');
            if (saved) {
                const items = JSON.parse(saved);
                wishlistItems = new Set(items);
                updateWishlistButtons();
                updateNavbarCount();
            }
        } catch (e) {
            console.error('Error loading wishlist state:', e);
            wishlistItems = new Set();
        }
    }
    
    function saveWishlistState() {
        try {
            localStorage.setItem('wishlistItems', JSON.stringify([...wishlistItems]));
            localStorage.setItem('wishlistCount', wishlistItems.size);
        } catch (e) {
            console.error('Error saving wishlist state:', e);
        }
    }
    
    function setupEventDelegation() {
        if (typeof EventDelegation !== 'undefined') {
            // Handle wishlist button clicks
            EventDelegation.on('click', '[data-product-id].product-card__wishlist, [data-product-id].wishlist-btn, [data-product-id].product-actions__secondary', function(event, target) {
                event.preventDefault();
                const productId = parseInt(target.getAttribute('data-product-id'));
                if (productId && !isNaN(productId)) {
                    toggleWishlist(productId, target);
                }
            });
            
            // Handle wishlist page remove buttons
            EventDelegation.on('click', '.product-card__wishlist--active', function(event, target) {
                event.preventDefault();
                const productId = parseInt(target.getAttribute('data-product-id'));
                if (productId && !isNaN(productId)) {
                    toggleWishlist(productId, target);
                }
            });
        } else {
            // Fallback: attach event listeners directly
            document.addEventListener('click', function(event) {
                const target = event.target.closest('[data-product-id].product-card__wishlist, [data-product-id].wishlist-btn, [data-product-id].product-actions__secondary, .product-card__wishlist--active');
                if (target) {
                    event.preventDefault();
                    const productId = parseInt(target.getAttribute('data-product-id'));
                    if (productId && !isNaN(productId)) {
                        toggleWishlist(productId, target);
                    }
                }
            });
        }
    }
    
    function toggleWishlist(productId, button = null) {
        if (!productId || isNaN(productId)) {
            console.error('toggleWishlist: invalid productId');
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Failed to update wishlist', 'error');
            }
            return Promise.reject('Invalid productId');
        }
        
        // Check authentication
        const isLoggedIn = document.getElementById('user-logged-in')?.value === 'true';
        if (!isLoggedIn) {
            // Redirect to login
            window.location.href = `${contextPath}/login?redirect=${encodeURIComponent(window.location.href)}`;
            return Promise.reject('Not authenticated');
        }
        
        // Show loading state
        if (button) {
            button.classList.add('loading');
            if (typeof FashionStore !== 'undefined' && FashionStore.showLoading) {
                FashionStore.showLoading(button, 'Updating...');
            }
        }
        
        const isCurrentlyInWishlist = wishlistItems.has(productId);
        
        // Use centralized API client
        const api = window.FashionStoreAPI || window.FashionStoreAPI?.api;
        if (api) {
            return api.post('/api/wishlist?action=toggle', { productId })
            .then(response => {
                if (response.success || response.status === 'success') {
                    const responseData = response.data || {};
                    const isFavorite = responseData.isFavorite !== undefined ? responseData.isFavorite : !isCurrentlyInWishlist;
                    
                    if (isFavorite) {
                        wishlistItems.add(productId);
                    } else {
                        wishlistItems.delete(productId);
                    }
                    
                    saveWishlistState();
                    updateWishlistButtons();
                    updateNavbarCount();
                    
                    // Remove item from DOM if on wishlist page
                    if (!isFavorite && window.location.pathname.includes('wishlist')) {
                        const itemElement = document.getElementById(`wishlist-item-${productId}`);
                        if (itemElement) {
                            itemElement.remove();
                            // Check if wishlist is now empty
                            const remainingItems = document.querySelectorAll('.product-card');
                            if (remainingItems.length === 0) {
                                window.location.reload();
                            }
                        }
                    }
                    
                    if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                        FashionStore.showToast(isFavorite ? 'Added to wishlist' : 'Removed from wishlist', 'success');
                    }
                    
                    return { success: true, isFavorite };
                } else {
                    throw new Error(response.message || 'Failed to update wishlist');
                }
            })
            .catch(err => {
                console.error('Error toggling wishlist:', err);
                if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                    FashionStore.showToast('Failed to update wishlist', 'error');
                }
                throw err;
            })
            .finally(() => {
                if (button) {
                    button.classList.remove('loading');
                    if (typeof FashionStore !== 'undefined' && FashionStore.hideLoading) {
                        FashionStore.hideLoading(button);
                    }
                }
            });
        } else {
            // Fallback to direct fetch
            return fetch(`${contextPath}/api/wishlist?action=toggle`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest',
                    'X-CSRF-Token': window.csrfToken || ''
                },
                credentials: 'include',
                body: JSON.stringify({ productId })
            })
            .then(res => {
                if (!res.ok) {
                    if (res.status === 401) {
                        // Redirect to login
                        window.location.href = `${contextPath}/login?redirect=${encodeURIComponent(window.location.href)}`;
                        throw new Error('Authentication required');
                    }
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                if (data.success || data.status === 'success') {
                    const responseData = data.data || {};
                    const isFavorite = responseData.isFavorite !== undefined ? responseData.isFavorite : !isCurrentlyInWishlist;
                    
                    if (isFavorite) {
                        wishlistItems.add(productId);
                    } else {
                        wishlistItems.delete(productId);
                    }
                    
                    saveWishlistState();
                    updateWishlistButtons();
                    updateNavbarCount();
                    
                    // Remove item from DOM if on wishlist page
                    if (!isFavorite && window.location.pathname.includes('wishlist')) {
                        const itemElement = document.getElementById(`wishlist-item-${productId}`);
                        if (itemElement) {
                            itemElement.remove();
                            // Check if wishlist is now empty
                            const remainingItems = document.querySelectorAll('.product-card');
                            if (remainingItems.length === 0) {
                                window.location.reload();
                            }
                        }
                    }
                    
                    if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                        FashionStore.showToast(isFavorite ? 'Added to wishlist' : 'Removed from wishlist', 'success');
                    }
                    
                    return { success: true, isFavorite };
                } else {
                    throw new Error(data.message || 'Failed to update wishlist');
                }
            })
            .catch(err => {
                console.error('Error toggling wishlist:', err);
                if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                    FashionStore.showToast('Failed to update wishlist', 'error');
                }
                throw err;
            })
            .finally(() => {
                if (button) {
                    button.classList.remove('loading');
                    if (typeof FashionStore !== 'undefined' && FashionStore.hideLoading) {
                        FashionStore.hideLoading(button);
                    }
                }
            });
        }
    }
    
    function checkWishlistStatus(productId, button) {
        if (!productId || isNaN(productId)) {
            return Promise.reject('Invalid productId');
        }
        
        // Check local state first
        if (wishlistItems.has(productId)) {
            if (button) {
                button.classList.add('active', 'product-card__wishlist--active');
                button.setAttribute('aria-pressed', 'true');
            }
            return Promise.resolve({ isFavorite: true });
        }
        
        // Check authentication
        const isLoggedIn = document.getElementById('user-logged-in')?.value === 'true';
        if (!isLoggedIn) {
            return Promise.resolve({ isFavorite: false });
        }
        
        // Use centralized API client
        const api = window.FashionStoreAPI || window.FashionStoreAPI?.api;
        if (api) {
            return api.get(`/api/wishlist?action=check&productId=${productId}`)
            .then(response => {
                const isFavorite = response.data && response.data.isFavorite;
                if (isFavorite) {
                    wishlistItems.add(productId);
                    saveWishlistState();
                }
                if (button) {
                    if (isFavorite) {
                        button.classList.add('active', 'product-card__wishlist--active');
                        button.setAttribute('aria-pressed', 'true');
                    } else {
                        button.classList.remove('active', 'product-card__wishlist--active');
                        button.setAttribute('aria-pressed', 'false');
                    }
                }
                return { isFavorite };
            })
            .catch(err => {
                console.error('Error checking wishlist status:', err);
                return { isFavorite: false };
            });
        } else {
            // Fallback to direct fetch
            return fetch(`${contextPath}/api/wishlist?action=check&productId=${productId}`, {
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'include'
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                const isFavorite = data.data && data.data.isFavorite;
                if (isFavorite) {
                    wishlistItems.add(productId);
                    saveWishlistState();
                }
                if (button) {
                    if (isFavorite) {
                        button.classList.add('active', 'product-card__wishlist--active');
                        button.setAttribute('aria-pressed', 'true');
                    } else {
                        button.classList.remove('active', 'product-card__wishlist--active');
                        button.setAttribute('aria-pressed', 'false');
                    }
                }
                return { isFavorite };
            })
            .catch(err => {
                console.error('Error checking wishlist status:', err);
                return { isFavorite: false };
            });
        }
    }
    
    function fetchWishlist() {
        const isLoggedIn = document.getElementById('user-logged-in')?.value === 'true';
        if (!isLoggedIn) {
            return Promise.resolve();
        }
        
        // Use centralized API client
        const api = window.FashionStoreAPI || window.FashionStoreAPI?.api;
        if (api) {
            return api.get('/api/wishlist')
            .then(response => {
                if (response.success && response.data && response.data.items) {
                    wishlistItems = new Set(response.data.items.map(item => item.productId));
                    saveWishlistState();
                    updateWishlistButtons();
                    updateNavbarCount();
                }
            })
            .catch(err => {
                console.error('Error fetching wishlist:', err);
            });
        } else {
            // Fallback to direct fetch
            return fetch(`${contextPath}/api/wishlist`, {
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                },
                credentials: 'include'
            })
            .then(res => {
                if (!res.ok) {
                    throw new Error(`HTTP error! status: ${res.status}`);
                }
                return res.json();
            })
            .then(data => {
                if (data.success && data.data && data.data.items) {
                    wishlistItems = new Set(data.data.items.map(item => item.productId));
                    saveWishlistState();
                    updateWishlistButtons();
                    updateNavbarCount();
                }
            })
            .catch(err => {
                console.error('Error fetching wishlist:', err);
            });
        }
    }
    
    function updateWishlistButtons() {
        document.querySelectorAll('[data-product-id].product-card__wishlist, [data-product-id].wishlist-btn, [data-product-id].product-actions__secondary').forEach(btn => {
            const productId = parseInt(btn.getAttribute('data-product-id'));
            if (wishlistItems.has(productId)) {
                btn.classList.add('active', 'product-card__wishlist--active');
                btn.setAttribute('aria-pressed', 'true');
            } else {
                btn.classList.remove('active', 'product-card__wishlist--active');
                btn.setAttribute('aria-pressed', 'false');
            }
        });
    }
    
    function updateNavbarCount() {
        const count = wishlistItems.size;
        
        // Try to find or create badge
        let badge = document.getElementById('nav-wishlist-badge');
        const wishlistLink = document.querySelector('.navbar-action-btn[aria-label="Wishlist"]');
        
        if (!badge && wishlistLink) {
            badge = document.createElement('span');
            badge.id = 'nav-wishlist-badge';
            badge.className = 'nav-badge';
            badge.style.display = 'none';
            wishlistLink.appendChild(badge);
        }
        
        if (badge) {
            badge.textContent = count;
            badge.style.display = count > 0 ? 'inline-block' : 'none';
        }
    }
    
    function removeWishlistItem(productId) {
        if (!productId || isNaN(productId)) {
            console.error('removeWishlistItem: invalid productId');
            return Promise.reject('Invalid productId');
        }
        
        return toggleWishlist(productId).then(() => {
            const itemElement = document.getElementById(`wishlist-item-${productId}`);
            if (itemElement) {
                itemElement.remove();
                const remainingItems = document.querySelectorAll('.product-card');
                if (remainingItems.length === 0) {
                    window.location.reload();
                }
            }
        });
    }
    
    function cleanup() {
        // Remove all tracked event listeners
        eventListeners.forEach(({ target, event, handler }) => {
            if (target && handler) {
                target.removeEventListener(event, handler);
            }
        });
        
        eventListeners.length = 0;
        initialized = false;
    }
    
    // Public API
    return {
        init,
        toggleWishlist,
        checkWishlistStatus,
        fetchWishlist,
        removeWishlistItem,
        updateWishlistButtons,
        updateNavbarCount,
        cleanup
    };
})();

// Make available globally
window.WishlistManager = WishlistManager;

// Register with FashionStoreApp for centralized initialization
if (typeof window.FashionStoreApp !== 'undefined') {
    window.FashionStoreApp.registerModule('WishlistManager', () => WishlistManager.init(), 12);
} else {
    // Fallback: initialize immediately if FashionStoreApp is not available
    document.addEventListener('DOMContentLoaded', () => WishlistManager.init());
}

// Cleanup on page navigation to prevent memory leaks
window.addEventListener('beforeunload', () => {
    WishlistManager.cleanup();
});

window.addEventListener('pagehide', () => {
    WishlistManager.cleanup();
});

// Export for ES6 modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = WishlistManager;
}
