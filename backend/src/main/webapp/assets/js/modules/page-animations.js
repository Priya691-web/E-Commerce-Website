/**
 * Page Animations Module
 * Handles page-specific animations
 */
const PageAnimations = (function() {
    
    function animateSuccessIcon() {
        const successIcon = document.querySelector('.success-icon');
        if (successIcon) {
            successIcon.style.animation = 'successPulse 0.6s ease-out';
        }
    }
    
    function initSuccessPage() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', animateSuccessIcon);
        } else {
            animateSuccessIcon();
        }
    }
    
    function formatTimestamps() {
        const timestampElements = document.querySelectorAll('.timestamp');
        timestampElements.forEach(function(element) {
            const timestamp = parseInt(element.textContent);
            if (!isNaN(timestamp)) {
                const date = new Date(timestamp);
                element.textContent = date.toLocaleString();
            }
        });
    }
    
    function initErrorPage() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', formatTimestamps);
        } else {
            formatTimestamps();
        }
    }
    
    function init() {
        if (document.querySelector('.success-page')) {
            initSuccessPage();
        }
        
        if (document.querySelector('.error-page')) {
            initErrorPage();
        }
    }
    
    return {
        init: init
    };
})();

// Auto-initialize on success and error pages
PageAnimations.init();
