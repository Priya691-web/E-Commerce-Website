/**
 * Password Validation Module
 * Handles password validation and confirmation
 */
const PasswordValidation = (function() {
    
    function validatePasswordForm() {
        const password = document.getElementById('password');
        const confirmPassword = document.getElementById('confirmPassword');
        const form = document.querySelector('form');
        
        if (!password || !confirmPassword || !form) {
            console.warn('Password form elements not found');
            return;
        }
        
        form.addEventListener('submit', function(e) {
            if (password.value !== confirmPassword.value) {
                e.preventDefault();
                FashionStore.showToast('Passwords do not match', 'error');
                password.focus();
                return false;
            }
            
            if (password.value.length < 8) {
                e.preventDefault();
                FashionStore.showToast('Password must be at least 8 characters', 'error');
                password.focus();
                return false;
            }
            
            return true;
        });
    }
    
    function init() {
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', validatePasswordForm);
        } else {
            validatePasswordForm();
        }
    }
    
    return {
        init: init
    };
})();

// Auto-initialize if on password reset page
if (document.querySelector('.reset-password-page')) {
    PasswordValidation.init();
}
