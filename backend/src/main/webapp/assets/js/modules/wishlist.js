/**
 * Wishlist Module
 * Handles wishlist item removal and management with state management
 */
const Wishlist = (function() {
    
    function removeWishlistItem(productId) {
        if (!confirm('Are you sure you want to remove this item from wishlist?')) {
            return;
        }
        
        // Show loading state on the item
        const wishlistItem = document.querySelector(`[data-product-id="${productId}"]`);
        if (wishlistItem) {
            wishlistItem.classList.add('loading');
            if (typeof StateManager !== 'undefined') {
                StateManager.showInlineLoading('wishlist-' + productId, true, 'Removing...');
            }
        }
        
        fetch(window.contextPath + '/wishlist', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': window.csrfToken || ''
            },
            body: new URLSearchParams({
                action: 'remove',
                productId: productId
            })
        })
        .then(res => {
            if (res.redirected) {
                window.location.href = res.url;
                return null;
            }
            return res.json().then(data => {
                if (res.status === 401) {
                    const redirectUrl = (data && data.redirect) || `${window.contextPath}/login`;
                    FashionStore.showToast(data.message || 'Session expired. Please login again.', 'info');
                    setTimeout(() => { window.location.href = redirectUrl; }, 1000);
                    return null;
                }
                if (!res.ok) {
                    throw new Error(data.message || 'Failed to remove item');
                }
                return data;
            });
        })
        .then(data => {
            if (data && data.success) {
                FashionStore.showToast(data.message || 'Item removed from wishlist', 'success');
                setTimeout(() => window.location.reload(), 1000);
            }
        })
        .catch(err => {
            console.error('Error removing wishlist item:', err);
            FashionStore.showToast('Failed to remove item. Please try again.', 'error');
            
            // Show error state
            if (wishlistItem && typeof StateManager !== 'undefined') {
                const errorContainer = document.createElement('div');
                errorContainer.className = 'error-state';
                errorContainer.style.padding = 'var(--space-2)';
                errorContainer.innerHTML = `
                    <svg class="error-state__icon" style="width: 16px; height: 16px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    <span style="font-size: var(--text-sm);">Failed to remove</span>
                `;
                wishlistItem.appendChild(errorContainer);
            }
        })
        .finally(() => {
            if (wishlistItem) {
                wishlistItem.classList.remove('loading');
                if (typeof StateManager !== 'undefined') {
                    StateManager.showInlineLoading('wishlist-' + productId, false);
                }
            }
        });
    }
    
    function init() {
        // Make removeWishlistItem available globally for onclick handlers
        window.removeWishlistItem = removeWishlistItem;
        
        // Add event listeners to remove buttons
        document.querySelectorAll('.remove-wishlist-btn').forEach(btn => {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const productId = this.dataset.productId;
                if (productId) {
                    removeWishlistItem(productId);
                }
            });
        });
        
        // Check if wishlist is empty and show empty state
        const wishlistContainer = document.querySelector('.wishlist-items');
        if (wishlistContainer && !wishlistContainer.children.length) {
            if (typeof StateManager !== 'undefined') {
                StateManager.showEmpty('wishlist-items', {
                    emptyTitle: 'Your wishlist is empty',
                    emptyMessage: 'Save items you love by clicking the heart icon',
                    actionText: 'Browse Products',
                    onAction: function() { window.location.href = window.contextPath + '/products'; }
                });
            }
        }
    }
    
    return {
        init: init,
        removeWishlistItem: removeWishlistItem
    };
})();

// Auto-initialize if on wishlist page
if (document.querySelector('.wishlist-page')) {
    Wishlist.init();
}
