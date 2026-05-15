/**
 * Config Module
 * Handles server-side configuration and global variables
 */
const Config = (function() {
    
    let config = {
        contextPath: '',
        stripePublishableKey: '',
        userEmail: '',
        csrfToken: ''
    };
    
    function init(serverConfig) {
        if (serverConfig) {
            config = { ...config, ...serverConfig };
        }
        
        // Make config available globally
        window.contextPath = config.contextPath || '';
        window.stripePublishableKey = config.stripePublishableKey || '';
        window.userEmail = config.userEmail || '';
        window.csrfToken = config.csrfToken || '';
    }
    
    function get(key) {
        return config[key];
    }
    
    function set(key, value) {
        config[key] = value;
    }
    
    return {
        init: init,
        get: get,
        set: set
    };
})();
