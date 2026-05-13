/**
 * FashionStore - Cart AJAX Logic
 */
document.addEventListener('DOMContentLoaded', function() {
    const contextPath = window.contextPath || '';
    
    // Quantity Buttons (+ / -)
    const qtyButtons = document.querySelectorAll('.ajax-qty-btn');
    qtyButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const action = this.getAttribute('data-action');
            const cartItemId = this.getAttribute('data-id');
            const currentQty = parseInt(this.getAttribute('data-qty'));
            
            updateCart(action, cartItemId, currentQty);
        });
    });

    // Remove Buttons
    const removeButtons = document.querySelectorAll('.ajax-remove-btn');
    removeButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const cartItemId = this.getAttribute('data-id');
            updateCart('remove', cartItemId, 0);
        });
    });

    // Save for Later Buttons
    const saveLaterButtons = document.querySelectorAll('.ajax-save-later-btn');
    saveLaterButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const cartItemId = this.getAttribute('data-id');
            updateCart('saveForLater', cartItemId, 0);
        });
    });

    // Quantity Input Field Changes
    const qtyInputs = document.querySelectorAll('.qty-input');
    qtyInputs.forEach(input => {
        input.addEventListener('change', function() {
            const cartItemId = this.getAttribute('data-id');
            const newQty = parseInt(this.value);
            const min = parseInt(this.getAttribute('min')) || 1;
            const max = parseInt(this.getAttribute('max')) || 10;

            if (newQty >= min && newQty <= max) {
                updateCart('update', cartItemId, newQty);
            } else {
                this.value = min;
                showToast(`Quantity must be between ${min} and ${max}`, 'error');
            }
        });
    });

    /**
     * Helper to send AJAX request to CartController
     */
    function updateCart(action, cartItemId, currentQty) {
        // Prevent concurrent requests for same cart item
        if (window.cartUpdateInProgress && window.cartUpdateInProgress.has(cartItemId)) {
            console.warn('Cart update already in progress for item:', cartItemId);
            return;
        }

        // Track ongoing requests
        if (!window.cartUpdateInProgress) {
            window.cartUpdateInProgress = new Set();
        }
        window.cartUpdateInProgress.add(cartItemId);

        const params = new URLSearchParams();
        params.append('action', action);
        params.append('cartItemId', cartItemId);
        params.append('currentQty', currentQty);
        
        // Add CSRF token to request body (fallback for missing header)
        if (window.csrfToken) {
            params.append('csrf_token', window.csrfToken);
        }

        // Visual feedback: disable buttons or show loading
        setLoadingState(true);

        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), 10000);

        fetch(`${contextPath}/cart`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': window.csrfToken || ''
            },
            body: params.toString(),
            signal: controller.signal
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok: ' + response.status);
            }
            if (response.redirected) {
                window.location.href = response.url;
                return;
            }
            return response.json();
        })
        .then(data => {
            if (data.success || data.status === 'success') {
                updateDOM(data);
                syncMiniCart(data);
                if (action === 'remove') {
                    showToast('Item removed from cart', 'success');
                } else if (action === 'saveForLater') {
                    showToast('Saved for later', 'success');
                } else {
                    showToast('Cart updated', 'success');
                }
            } else {
                showToast(data.message || 'Failed to update cart. Please try again.', 'error');
            }
        })
        .catch(error => {
            if (error.name === 'AbortError') {
                console.warn('Cart update request timed out for item:', cartItemId);
                showToast('Request timed out. Please try again.', 'error');
            } else {
                console.error('Cart update error:', error);
                showToast('An error occurred. Please try again.', 'error');
            }
        })
        .finally(() => {
            clearTimeout(timeoutId);
            setLoadingState(false);
            // Remove from tracking set
            if (window.cartUpdateInProgress) {
                window.cartUpdateInProgress.delete(cartItemId);
            }
        });
    }

    /**
     * Update the DOM elements with new values from server
     */
    function updateDOM(data) {
        if (data.removed || data.newQuantity <= 0) {
            // Remove the item card from DOM with animation
            const itemCard = document.querySelector(`.cart-item[data-id="${data.cartItemId}"]`);
            if (itemCard) {
                itemCard.classList.add('removing');
                setTimeout(() => {
                    itemCard.remove();
                    checkEmptyCart();
                }, 300);
            }
        } else {
            // Update quantity input
            const qtyInput = document.querySelector(`.qty-input[data-id="${data.cartItemId}"]`);
            if (qtyInput) qtyInput.value = data.newQuantity;

            // Update item subtotal with pulse animation
            const itemTotal = document.getElementById(`item-total-${data.cartItemId}`);
            if (itemTotal) {
                itemTotal.innerText = `₹${data.itemTotal.toFixed(2)}`;
                itemTotal.classList.remove('updating');
                void itemTotal.offsetWidth;
                itemTotal.classList.add('updating');
            }

            // Update data-qty attribute on buttons; toggle disabled on minus button
            const buttons = document.querySelectorAll(`.ajax-qty-btn[data-id="${data.cartItemId}"]`);
            buttons.forEach(b => {
                b.setAttribute('data-qty', data.newQuantity);
                if (b.getAttribute('data-action') === 'decrease') {
                    b.disabled = data.newQuantity <= 1;
                }
            });
        }

        // Update global totals with animation
        const summarySubtotal = document.getElementById('summary-subtotal');
        if (summarySubtotal) {
            summarySubtotal.innerText = data.cartTotal.toFixed(2);
            summarySubtotal.classList.remove('updating');
            void summarySubtotal.offsetWidth; // Trigger reflow
            summarySubtotal.classList.add('updating');
        }

        const summaryTotal = document.getElementById('summary-total');
        if (summaryTotal) {
            summaryTotal.innerText = data.cartTotal.toFixed(2);
            summaryTotal.classList.remove('updating');
            void summaryTotal.offsetWidth; // Trigger reflow
            summaryTotal.classList.add('updating');
        }

        // Update cart count in header
        const cartCount = document.querySelector('.cart-count');
        if (cartCount) cartCount.innerText = `${data.cartCount} item${data.cartCount !== 1 ? 's' : ''}`;

        // Sync navbar badge
        const navBadge = document.getElementById('nav-cart-badge');
        if (navBadge) {
            const oldCount = parseInt(navBadge.innerText);
            navBadge.innerText = data.cartCount;
            // Animate badge if count changed
            if (oldCount !== data.cartCount) {
                navBadge.classList.remove('animate');
                void navBadge.offsetWidth;
                navBadge.classList.add('animate');
            }
        }

        // Sync mobile bottom nav badge
        const mobileBadge = document.getElementById('mobile-cart-badge');
        if (mobileBadge) {
            mobileBadge.innerText = data.cartCount;
        }
    }

    function setLoadingState(isLoading) {
        const btns = document.querySelectorAll('.ajax-qty-btn, .ajax-remove-btn, .ajax-save-later-btn');
        btns.forEach(b => {
            b.disabled = isLoading;
            b.style.opacity = isLoading ? '0.5' : '1';
        });
    }

    function checkEmptyCart() {
        const remaining = document.querySelectorAll('.cart-item').length;
        if (remaining === 0) {
            window.location.reload();
        }
    }

    /**
     * Sync mini-cart drawer + navbar badge after any cart mutation.
     * Server already returned cartItems/cartTotal/cartCount so we update inline,
     * avoiding an extra round trip.
     */
    function syncMiniCart(data) {
        if (!data) return;
        if (window.FashionStore && typeof window.FashionStore.updateMiniCartUI === 'function') {
            window.FashionStore.updateMiniCartUI(data);
        }
        const badge = document.getElementById('nav-cart-badge');
        if (badge && typeof data.cartCount === 'number') {
            const oldCount = parseInt(badge.innerText, 10);
            badge.innerText = data.cartCount;
            if (oldCount !== data.cartCount) {
                badge.classList.remove('animate');
                void badge.offsetWidth;
                badge.classList.add('animate');
            }
        }
        const mobileBadge = document.getElementById('mobile-cart-badge');
        if (mobileBadge && typeof data.cartCount === 'number') {
            mobileBadge.innerText = data.cartCount;
        }
    }

    // Delegate to global FashionStore.showToast defined in main.js (loaded via navbar.jsp)
    function showToast(message, type) {
        if (typeof window.FashionStore !== 'undefined' && typeof window.FashionStore.showToast === 'function') {
            window.FashionStore.showToast(message, type || 'success');
        } else if (typeof window.showToast === 'function') {
            window.showToast(message, type || 'success');
        }
    }
});
