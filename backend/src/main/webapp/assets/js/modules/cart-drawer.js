/**
 * FashionStore - Cart Drawer Module
 * Mini cart functionality, cart operations, and UI updates with state management
 */

const FashionStoreCartDrawer = (function() {
    const contextPath = window.contextPath || '';
    
    function init() {
        setupCartDrawerToggle();
        setupMiniCartListeners();
    }
    
    function setupCartDrawerToggle() {
        // Cart drawer toggle is handled by global function toggleMiniCart
        // This is already defined in main.js and called from navbar
    }
    
    function toggleMiniCart(event) {
        if (event) event.preventDefault();
        const overlay = document.getElementById('mini-cart-overlay');
        const drawer = document.getElementById('mini-cart-drawer');
        
        if (!overlay || !drawer) {
            console.warn('Mini cart elements not found');
            return;
        }
        
        if (overlay.classList.contains('active')) {
            overlay.classList.remove('active');
            drawer.classList.remove('active');
            document.body.classList.remove('cart-drawer-open');
            FashionStoreNavbar.unlockScroll();
        } else {
            overlay.classList.add('active');
            drawer.classList.add('active');
            document.body.classList.add('cart-drawer-open');
            FashionStoreNavbar.lockScroll();
            fetchCart();
        }
    }
    
    function setupMiniCartListeners() {
        // Close cart when overlay is clicked
        const overlay = document.getElementById('mini-cart-overlay');
        if (overlay) {
            overlay.addEventListener('click', toggleMiniCart);
        }
        
        // Close cart on escape key
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape') {
                const drawer = document.getElementById('mini-cart-drawer');
                if (drawer && drawer.classList.contains('active')) {
                    toggleMiniCart();
                }
            }
        });
    }
    
    function fetchCart() {
        const cartItems = document.getElementById('mini-cart-items');
        
        // Show loading state
        if (cartItems && typeof StateManager !== 'undefined') {
            StateManager.showInlineLoading('mini-cart-items', true, 'Loading cart...');
        }
        
        const contextPath = window.contextPath || '';
        fetch(`${contextPath}/cart?action=get`, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(res => {
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return res.json();
        })
        .then(data => {
            updateMiniCartUI(data);
            // Hide loading state
            if (cartItems && typeof StateManager !== 'undefined') {
                StateManager.showInlineLoading('mini-cart-items', false);
            }
        })
        .catch(err => {
            console.error('Error fetching cart:', err);
            // Show error state
            if (cartItems && typeof StateManager !== 'undefined') {
                cartItems.innerHTML = `
                    <div class="error-state" style="padding: var(--space-4);">
                        <svg class="error-state__icon" style="width: 32px; height: 32px;" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                        </svg>
                        <p class="error-state__message" style="font-size: var(--text-sm);">Unable to load cart.</p>
                        <button class="fs-btn fs-btn--primary" style="font-size: var(--text-sm); padding: var(--space-2) var(--space-4);" onclick="FashionStoreCartDrawer.fetchCart()">Retry</button>
                    </div>
                `;
            } else {
                cartItems.innerHTML = '<div class="cart-error">Unable to load cart. Please try again.</div>';
            }
        });
    }
    
    function updateMiniCartUI(data) {
        if (!data) return;
        
        // Update cart items
        const cartItemsContainer = document.getElementById('mini-cart-items');
        if (cartItemsContainer && data.items) {
            if (data.items.length === 0) {
                // Show empty state
                cartItemsContainer.innerHTML = `
                    <div class="empty-state" style="padding: var(--space-8) 0;">
                        <svg class="empty-state__icon" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707 1.707 1.707zM17 13l2.293 2.293c.63.63 1.707-.184 1.707-1.707M17 13v4a2 2 0 01-2 2H5a2 2 0 01-2-2v-4"/>
                        </svg>
                        <h3 class="empty-state__title" style="font-size: var(--text-base);">Your cart is empty</h3>
                        <p class="empty-state__message">Add items to get started</p>
                        <button class="fs-btn fs-btn--primary empty-state__action" onclick="FashionStoreCartDrawer.toggleMiniCart(); window.location.href='${contextPath}/products'">Shop Now</button>
                    </div>
                `;
            } else {
                cartItemsContainer.innerHTML = data.items.map(item => `
                    <div class="mini-cart-item" data-id="${item.cartItemId}">
                        <div class="mini-cart-item-info">
                            <span class="mini-cart-item-name">${item.productName}</span>
                            <span class="mini-cart-item-meta">Size: ${item.sizeLabel} × ${item.quantity}</span>
                            <span class="mini-cart-item-price">₹${(item.price * item.quantity).toFixed(2)}</span>
                        </div>
                        <button class="mini-cart-item-remove" data-id="${item.cartItemId}" aria-label="Remove item">✕</button>
                    </div>
                `).join('');
                
                // Attach remove listeners
                cartItemsContainer.querySelectorAll('.mini-cart-item-remove').forEach(btn => {
                    btn.addEventListener('click', () => removeMiniCartItem(btn.dataset.id));
                });
            }
        }
        
        // Update totals
        const subtotalEl = document.getElementById('mini-cart-subtotal');
        if (subtotalEl && data.subtotal) {
            subtotalEl.textContent = `₹${data.subtotal.toFixed(2)}`;
        }
        
        // Update navbar badge
        const badgeEl = document.getElementById('nav-cart-badge');
        if (badgeEl && data.totalItems) {
            badgeEl.textContent = data.totalItems;
            badgeEl.style.display = data.totalItems > 0 ? 'block' : 'none';
        }
        
        // Update empty state
        const emptyState = document.getElementById('mini-cart-empty');
        const cartContent = document.getElementById('mini-cart-content');
        if (emptyState && cartContent) {
            if (data.items && data.items.length > 0) {
                emptyState.style.display = 'none';
                cartContent.style.display = 'block';
            } else {
                emptyState.style.display = 'block';
                cartContent.style.display = 'none';
            }
        }
    }
    
    function updateMiniCartQty(cartItemId, newQty) {
        const contextPath = window.contextPath || '';
        
        if (newQty < 1) {
            removeMiniCartItem(cartItemId);
            return;
        }
        
        const cartItem = document.querySelector(`.mini-cart-item[data-id="${cartItemId}"]`);
        if (cartItem) {
            cartItem.classList.add('loading');
            FashionStore.showLoading(cartItem, 'Updating...');
        }
        
        fetch(`${contextPath}/cart?action=update&cartItemId=${cartItemId}&currentQty=${newQty}`, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(res => {
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return res.json();
        })
        .then(data => {
            updateMiniCartUI(data);
            FashionStore.showToast('Quantity updated', 'success');
        })
        .catch(err => {
            console.error('Error updating cart quantity:', err);
            FashionStore.showToast('Failed to update quantity', 'error');
        })
        .finally(() => {
            if (cartItem) {
                cartItem.classList.remove('loading');
                FashionStore.hideLoading(cartItem);
            }
        });
    }
    
    function removeMiniCartItem(cartItemId) {
        const contextPath = window.contextPath || '';
        
        const cartItem = document.querySelector(`.mini-cart-item[data-id="${cartItemId}"]`);
        if (cartItem) {
            cartItem.classList.add('loading');
            FashionStore.showLoading(cartItem, 'Removing...');
        }
        
        fetch(`${contextPath}/cart?action=remove&cartItemId=${cartItemId}`, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(res => {
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return res.json();
        })
        .then(data => {
            updateMiniCartUI(data);
            FashionStore.showToast('Item removed', 'success');
        })
        .catch(err => {
            console.error('Error removing item:', err);
            FashionStore.showToast('Unable to remove item', 'error');
        })
        .finally(() => {
            if (cartItem) {
                cartItem.classList.remove('loading');
                FashionStore.hideLoading(cartItem);
            }
        });
    }
    
    function addToCart(productId, size = 'M', quantity = 1) {
        const contextPath = window.contextPath || '';
        
        FashionStore.showToast('Adding to cart...', 'info');
        
        fetch(`${contextPath}/cart?action=add&productId=${productId}&size=${size}&quantity=${quantity}`, {
            method: 'POST',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(res => {
            if (!res.ok) throw new Error(`HTTP ${res.status}`);
            return res.json();
        })
        .then(data => {
            FashionStore.showToast('Added to cart', 'success');
            updateMiniCartUI(data);
        })
        .catch(err => {
            console.error('Error adding to cart:', err);
            FashionStore.showToast('Failed to add to cart', 'error');
        });
    }
    
    function cleanup() {
        // Remove event listeners if needed
        const overlay = document.getElementById('mini-cart-overlay');
        if (overlay) {
            overlay.replaceWith(overlay.cloneNode(true));
        }
    }
    
    // Public API
    return {
        init,
        toggleMiniCart,
        fetchCart,
        updateMiniCartQty,
        removeMiniCartItem,
        addToCart,
        cleanup
    };
})();

// Make cart drawer available globally
if (typeof window.FashionStore === 'undefined') {
    window.FashionStore = {};
}
window.FashionStore.cartDrawer = FashionStoreCartDrawer;

// Global toggle function for backward compatibility
window.toggleMiniCart = FashionStoreCartDrawer.toggleMiniCart;

// Initialize on DOM ready
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', FashionStoreCartDrawer.init);
} else {
    FashionStoreCartDrawer.init();
}

// Export for ES6 modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FashionStoreCartDrawer;
}
