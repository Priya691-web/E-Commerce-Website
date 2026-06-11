/**
 * FashionStore - Cart Interactions Module
 * Handles Add to Cart buttons, quantity steppers, and cart-related UI interactions
 * 
 * This module attaches event listeners to all cart-related buttons across the site:
 * - Product detail page Add to Cart button
 * - Product listing page Add to Cart buttons
 * - Quantity steppers
 * - Mobile Add to Cart button
 */

const FashionStoreCartInteractions = (function() {
    const contextPath = window.contextPath || '';
    
    function init() {
        setupAddToCartButtons();
        setupQuantitySteppers();
        setupMobileAddToCart();
    }
    
    /**
     * Setup Add to Cart buttons on product detail page
     */
    function setupAddToCartButtons() {
        // Product detail page main button
        const addToCartBtn = document.getElementById('add-to-cart-btn');
        if (addToCartBtn) {
            addToCartBtn.addEventListener('click', handleProductDetailAddToCart);
        }
        
        // Product listing page buttons
        document.querySelectorAll('.product-card__btn--outline[data-product-id]').forEach(btn => {
            btn.addEventListener('click', handleProductCardAddToCart);
        });
    }
    
    /**
     * Handle Add to Cart from product detail page
     */
    function handleProductDetailAddToCart(e) {
        e.preventDefault();
        
        const productIdEl = document.getElementById('detailsProductId');
        if (!productIdEl) {
            console.error('Product ID not found');
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Failed to add to cart: Product not found', 'error');
            }
            return;
        }
        
        const productId = parseInt(productIdEl.value);
        if (isNaN(productId)) {
            console.error('Invalid Product ID');
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Failed to add to cart: Invalid product', 'error');
            }
            return;
        }
        
        const quantity = parseInt(document.getElementById('detailsQuantity')?.value) || 1;
        if (quantity < 1 || quantity > 10) {
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Quantity must be between 1 and 10', 'error');
            }
            return;
        }
        
        // Get selected size
        const sizeInput = document.querySelector('input[name="size"]:checked');
        const size = sizeInput ? sizeInput.value : 'M';
        
        // Validate size selection
        const sizeInputs = document.querySelectorAll('input[name="size"]');
        if (sizeInputs.length > 0 && !sizeInput) {
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Please select a size', 'error');
            }
            return;
        }
        
        // Call CartManager to add to cart
        if (typeof CartManager !== 'undefined' && CartManager.addToCart) {
            CartManager.addToCart(productId, size, quantity, e.target);
        } else {
            console.error('CartManager not available');
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Cart functionality not available. Please refresh the page.', 'error');
            }
        }
    }
    
    /**
     * Handle Add to Cart from product card (listing page)
     */
    function handleProductCardAddToCart(e) {
        e.preventDefault();
        
        const btn = e.currentTarget;
        const productId = parseInt(btn.dataset.productId);
        
        if (!productId || isNaN(productId)) {
            console.error('Product ID not found on button');
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Failed to add to cart: Invalid product', 'error');
            }
            return;
        }
        
        // For product cards, use default size and quantity
        const size = 'M';
        const quantity = 1;
        
        // Call CartManager to add to cart
        if (typeof CartManager !== 'undefined' && CartManager.addToCart) {
            CartManager.addToCart(productId, size, quantity, btn);
        } else {
            console.error('CartManager not available');
            if (typeof FashionStore !== 'undefined' && FashionStore.showToast) {
                FashionStore.showToast('Cart functionality not available. Please refresh the page.', 'error');
            }
        }
    }
    
    /**
     * Setup quantity steppers on product detail page
     */
    function setupQuantitySteppers() {
        const decreaseBtn = document.getElementById('decrease-qty-btn');
        const increaseBtn = document.getElementById('increase-qty-btn');
        const quantityInput = document.getElementById('detailsQuantity');
        
        if (decreaseBtn && quantityInput) {
            decreaseBtn.addEventListener('click', () => {
                const currentValue = parseInt(quantityInput.value) || 1;
                if (currentValue > 1) {
                    quantityInput.value = currentValue - 1;
                }
            });
        }
        
        if (increaseBtn && quantityInput) {
            increaseBtn.addEventListener('click', () => {
                const currentValue = parseInt(quantityInput.value) || 1;
                if (currentValue < 10) {
                    quantityInput.value = currentValue + 1;
                }
            });
        }
        
        if (quantityInput) {
            quantityInput.addEventListener('change', () => {
                let value = parseInt(quantityInput.value) || 1;
                value = Math.max(1, Math.min(10, value));
                quantityInput.value = value;
            });
        }
    }
    
    /**
     * Setup mobile Add to Cart button
     */
    function setupMobileAddToCart() {
        const mobileAddToCartBtn = document.getElementById('mobile-add-to-cart-btn');
        if (mobileAddToCartBtn) {
            mobileAddToCartBtn.addEventListener('click', handleProductDetailAddToCart);
        }
    }
    
    // Cleanup function
    function cleanup() {
        // Remove event listeners if needed
        const addToCartBtn = document.getElementById('add-to-cart-btn');
        if (addToCartBtn) {
            addToCartBtn.removeEventListener('click', handleProductDetailAddToCart);
        }
        
        document.querySelectorAll('.product-card__btn--outline[data-product-id]').forEach(btn => {
            btn.removeEventListener('click', handleProductCardAddToCart);
        });
    }
    
    // Public API
    return {
        init,
        cleanup
    };
})();

// Make available globally
window.FashionStoreCartInteractions = FashionStoreCartInteractions;

// Register with FashionStoreApp for centralized initialization
if (typeof window.FashionStoreApp !== 'undefined') {
    window.FashionStoreApp.registerModule('cartInteractions', FashionStoreCartInteractions.init, 25);
} else {
    // Fallback: initialize immediately if FashionStoreApp is not available
    document.addEventListener('DOMContentLoaded', () => FashionStoreCartInteractions.init());
}

// Cleanup on page navigation
window.addEventListener('beforeunload', () => {
    FashionStoreCartInteractions.cleanup();
});

window.addEventListener('pagehide', () => {
    FashionStoreCartInteractions.cleanup();
});

// Export for ES6 modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = FashionStoreCartInteractions;
}
