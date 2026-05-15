/**
 * Admin Dashboard Module
 * Handles admin dashboard charts and analytics
 */
const AdminDashboard = (function() {
    
    function initCharts() {
        const revenueDataEl = document.getElementById('revenue-data');
        const revenueLabelsEl = document.getElementById('revenue-labels');
        
        if (!revenueDataEl || !revenueLabelsEl) {
            console.warn('Revenue data elements not found');
            return;
        }
        
        let chartData = [];
        let chartLabels = [];
        
        try {
            chartData = JSON.parse(revenueDataEl.value);
            chartLabels = JSON.parse(revenueLabelsEl.value);
        } catch (e) {
            console.error('Error parsing chart data:', e);
            return;
        }
        
        if (typeof Chart === 'undefined') {
            console.warn('Chart.js is not loaded');
            return;
        }
        
        const ctx = document.getElementById('revenueChart');
        if (!ctx) {
            console.warn('Revenue chart canvas not found');
            return;
        }
        
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: chartLabels,
                datasets: [{
                    label: 'Revenue',
                    data: chartData,
                    borderColor: 'rgb(75, 192, 192)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: true,
                        position: 'top'
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
    
    function init() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', initCharts);
        } else {
            initCharts();
        }
    }
    
    return {
        init: init
    };
})();

// Auto-initialize if on admin dashboard page
if (document.querySelector('.admin-dashboard')) {
    AdminDashboard.init();
}
