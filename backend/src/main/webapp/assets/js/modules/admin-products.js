/**
 * Admin Products Module
 * Handles admin product search, filtering, and image preview
 */
const AdminProducts = (function() {
    
    function initProductFiltering() {
        const searchInput = document.getElementById('searchProducts');
        const stockFilter = document.getElementById('stockFilter');
        
        if (!searchInput || !stockFilter) {
            console.warn('Product filter elements not found');
            return;
        }
        
        function filterProducts() {
            const searchTerm = searchInput.value.toLowerCase();
            const stockValue = stockFilter.value;
            
            document.querySelectorAll('tbody tr').forEach(row => {
                const productName = row.getAttribute('data-product-name') || '';
                const brand = row.getAttribute('data-brand') || '';
                const stockStatus = row.getAttribute('data-stock') || '';
                
                const matchesSearch = productName.includes(searchTerm) || brand.includes(searchTerm);
                const matchesStock = stockValue === '' || stockStatus === stockValue;
                
                row.style.display = matchesSearch && matchesStock ? '' : 'none';
            });
        }
        
        searchInput.addEventListener('input', filterProducts);
        stockFilter.addEventListener('change', filterProducts);
    }
    
    function initImagePreview() {
        window.showImagePreview = function(imageUrl, productName) {
            const modal = document.getElementById('imageModal');
            if (!modal) {
                console.warn('Image modal not found');
                return;
            }
            
            const modalImage = modal.querySelector('img');
            const modalTitle = modal.querySelector('.modal-title');
            
            if (modalImage) {
                modalImage.src = imageUrl;
            }
            if (modalTitle) {
                modalTitle.textContent = productName;
            }
            
            modal.classList.add('active');
            document.body.style.overflow = 'hidden';
        };
        
        window.closeImagePreview = function() {
            const modal = document.getElementById('imageModal');
            if (!modal) {
                console.warn('Image modal not found');
                return;
            }
            
            modal.classList.remove('active');
            document.body.style.overflow = '';
        };
        
        // Close modal on escape key
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Escape') {
                window.closeImagePreview();
            }
        });
        
        // Close modal on backdrop click
        const modal = document.getElementById('imageModal');
        if (modal) {
            modal.addEventListener('click', function(e) {
                if (e.target === modal) {
                    window.closeImagePreview();
                }
            });
        }
    }
    
    function initFormValidation() {
        window.validateForm = function() {
            const price = document.getElementById('price');
            if (!price) {
                return true;
            }
            
            if (parseFloat(price.value) <= 0) {
                alert('Price must be greater than zero.');
                price.focus();
                return false;
            }
            return true;
        };
    }
    
    function init() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', function() {
                initProductFiltering();
                initImagePreview();
                initFormValidation();
            });
        } else {
            initProductFiltering();
            initImagePreview();
            initFormValidation();
        }
    }
    
    return {
        init: init
    };
})();

// Auto-initialize if on admin products page
if (document.querySelector('.admin-products-page')) {
    AdminProducts.init();
}
