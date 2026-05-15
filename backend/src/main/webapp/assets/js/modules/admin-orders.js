/**
 * Admin Orders Module
 * Handles admin order filtering and search functionality
 */
const AdminOrders = (function() {
    
    function initOrderFiltering() {
        const searchInput = document.getElementById('searchOrders');
        const statusFilter = document.getElementById('statusFilter');
        const dateFilter = document.getElementById('dateFilter');
        
        if (!searchInput || !statusFilter) {
            console.warn('Order filter elements not found');
            return;
        }
        
        function filterOrders() {
            const searchTerm = searchInput.value.toLowerCase();
            const statusValue = statusFilter.value;
            
            document.querySelectorAll('article.admin-order-card').forEach(card => {
                const orderId = card.querySelector('.admin-order-card__title')?.textContent.toLowerCase() || '';
                const statusBadge = card.querySelector('.status-badge')?.textContent.toLowerCase() || '';
                const customerInfo = card.querySelector('.admin-order-card__meta')?.textContent.toLowerCase() || '';
                
                const matchesSearch = orderId.includes(searchTerm) || customerInfo.includes(searchTerm);
                const matchesStatus = statusValue === '' || statusBadge === statusValue.toLowerCase();
                
                card.style.display = matchesSearch && matchesStatus ? 'block' : 'none';
            });
        }
        
        searchInput.addEventListener('input', filterOrders);
        statusFilter.addEventListener('change', filterOrders);
        
        if (dateFilter) {
            dateFilter.addEventListener('change', filterOrders);
        }
    }
    
    function init() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', initOrderFiltering);
        } else {
            initOrderFiltering();
        }
    }
    
    return {
        init: init
    };
})();

// Auto-initialize if on admin orders page
if (document.querySelector('.admin-orders-page')) {
    AdminOrders.init();
}
