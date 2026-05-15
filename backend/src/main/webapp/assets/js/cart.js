/**
 * FashionStore - Cart AJAX Logic
 * Handles cart operations with proper state management and error handling
 */
document.addEventListener('DOMContentLoaded', function() {
    const contextPath = window.contextPath || '';
    
    // Track in-flight requests to prevent duplicates
    const requestTracker = new Map();
    const MAX_REQUEST_TIMEOUT = 10000; // 10 seconds

    const setLoadingState = (isLoading) => {
        const btns = document.querySelectorAll('.ajax-qty-btn, .ajax-remove-btn, .ajax-save-later-btn');
        btns.forEach(b => {
            b.disabled = isLoading;
            b.style.opacity = isLoading ? '0.5' : '1';
        });
    };

    const checkEmptyCart = () => {
        const remaining = document.querySelectorAll('.cart-item').length;
        if (remaining === 0) {
            window.location.reload();
        }
    };

    const syncMiniCart = (data) => {
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
    };

    const showToast = (message, type) => {
        if (typeof window.FashionStore !== 'undefined' && typeof window.FashionStore.showToast === 'function') {
            window.FashionStore.showToast(message, type || 'success');
        } else if (typeof window.showToast === 'function') {
            window.showToast(message, type || 'success');
        }
    };

    const updateDOM = (data) => {
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
    };

    const updateCart = (action, cartItemId, currentQty) => {
        // Create unique request key
        const requestKey = `${action}-${cartItemId}`;
        
        // Prevent concurrent requests for same item and action
        if (requestTracker.has(requestKey)) {
            const existingRequest = requestTracker.get(requestKey);
            if (Date.now() - existingRequest.timestamp < 500) {
                console.warn(`Cart update already in progress: ${requestKey}`);
                return;
            } else {
                // Request is stale, clean it up
                requestTracker.delete(requestKey);
            }
        }

        // Validate input parameters
        if (!Number.isInteger(cartItemId) || cartItemId <= 0) {
            console.error('Invalid cartItemId:', cartItemId);
            showToast('Invalid cart item', 'error');
            return;
        }

        if (!Number.isInteger(currentQty) || currentQty < 0) {
            console.error('Invalid currentQty:', currentQty);
            showToast('Invalid quantity', 'error');
            return;
        }

        // Build request parameters
        const params = new URLSearchParams();
        params.append('action', action);
        params.append('cartItemId', cartItemId);
        params.append('currentQty', currentQty);
        
        // Add CSRF token to request body (fallback for missing header)
        if (window.csrfToken) {
            params.append('csrf_token', window.csrfToken);
        }

        // Track this request
        const requestRecord = {
            timestamp: Date.now(),
            controller: null,
            timeoutId: null
        };
        requestTracker.set(requestKey, requestRecord);

        // Visual feedback: disable buttons or show loading
        setLoadingState(true);

        const controller = new AbortController();
        const timeoutId = setTimeout(() => {
            controller.abort();
            console.warn(`Cart update timeout: ${requestKey}`);
        }, MAX_REQUEST_TIMEOUT);
        
        requestRecord.controller = controller;
        requestRecord.timeoutId = timeoutId;

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
            if (response.redirected) {
                window.location.href = response.url;
                return null;
            }
            return response.json().then(data => {
                if (response.status === 401) {
                    const redirectUrl = (data && data.redirect) || `${contextPath}/login`;
                    showToast(data.message || 'Session expired. Please login again.', 'info');
                    setTimeout(() => { window.location.href = redirectUrl; }, 1000);
                    return null;
                }
                if (!response.ok) {
                    throw new Error((data && data.message) || `HTTP error! status: ${response.status}`);
                }
                return data;
            });
        })
        .then(data => {
            if (!data) return; // Handled by redirect or session expire
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
                console.warn(`Cart update timeout: ${requestKey}`);
                showToast('Request timed out. Please try again.', 'error');
            } else if (error instanceof TypeError) {
                console.error(`Network error for ${requestKey}:`, error);
                showToast('Network error. Please check your connection.', 'error');
            } else {
                console.error(`Cart update error for ${requestKey}:`, error);
                showToast(error.message || 'An error occurred. Please try again.', 'error');
            }
        })
        .finally(() => {
            // Clean up timeout and request tracking
            if (requestRecord.timeoutId) {
                clearTimeout(requestRecord.timeoutId);
            }
            requestTracker.delete(requestKey);
            setLoadingState(false);
        });
    };
    
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
        // Store original value for rollback on error
        const originalValue = input.value;
        
        input.addEventListener('change', function() {
            const cartItemId = parseInt(this.getAttribute('data-id'));
            const newQty = parseInt(this.value);
            const min = parseInt(this.getAttribute('min')) || 1;
            const max = parseInt(this.getAttribute('max')) || 10;

            // Validate quantity
            if (!Number.isInteger(newQty) || newQty < min || newQty > max) {
                // Restore original value on invalid input
                this.value = originalValue;
                showToast(`Quantity must be between ${min} and ${max}`, 'error');
                return;
            }

            // Only update if quantity actually changed
            if (newQty !== parseInt(originalValue)) {
                // Update original value reference for next change
                const oldValue = originalValue;
                originalValue = newQty;
                
                updateCart('update', cartItemId, newQty);
            }
        });
        
        // Prevent invalid input in real-time
        input.addEventListener('input', function() {
            const min = parseInt(this.getAttribute('min')) || 1;
            const max = parseInt(this.getAttribute('max')) || 10;
            let value = parseInt(this.value);
            
            if (isNaN(value)) {
                this.value = '';
                return;
            }
            
            if (value < min) {
                this.value = min;
            } else if (value > max) {
                this.value = max;
            }
        });
    });

});
