<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 
    head.jsp: Shared meta tags and font loading.
    USAGE: Call request.setAttribute("_pageTitle","...") and request.setAttribute("_pageCSS","css-name")
    before including this file. Do NOT put a page contentType directive here.
--%>
<%
    String _pageTitle = (String) request.getAttribute("_pageTitle");
    if (_pageTitle == null || _pageTitle.trim().isEmpty()) _pageTitle = "FashionStore";
    String _pageDescription = (String) request.getAttribute("_pageDescription");
    if (_pageDescription == null || _pageDescription.trim().isEmpty()) _pageDescription = "FashionStore - premium fashion marketplace with curated styles for every season.";
    String _canonical = (String) request.getAttribute("_canonical");
    String _pageCSS   = (String) request.getAttribute("_pageCSS");
%>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0, viewport-fit=cover">
<meta name="description" content="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(_pageDescription) %>">

<%-- Skip to content link for keyboard accessibility --%>
<style>
    .skip-to-content {
        position: absolute;
        top: -40px;
        left: 0;
        background: var(--color-ink);
        color: var(--color-surface);
        padding: 8px 16px;
        z-index: 10000;
        text-decoration: none;
        transition: top 0.3s;
    }
    .skip-to-content:focus {
        top: 0;
    }
</style>
<a href="#main-content" class="skip-to-content">Skip to main content</a>

<% if (_canonical != null && !_canonical.trim().isEmpty()) { %>
<link rel="canonical" href="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(_canonical) %>">
<% } %>
<title><%= _pageTitle %> | FashionStore</title>

<%-- Open Graph / Social Sharing --%>
<meta property="og:title" content="<%= _pageTitle %> | FashionStore">
<meta property="og:description" content="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(_pageDescription) %>">
<meta property="og:type" content="website">
<meta property="og:url" content="<%= request.getRequestURL() %>">
<meta property="og:image" content="<%= request.getContextPath() %>/assets/images/logo-mark.svg">

<%-- Favicon & Brand Icons --%>
<link rel="icon" type="image/svg+xml" href="<%= request.getContextPath() %>/assets/images/logo-mark.svg">
<link rel="apple-touch-icon" href="<%= request.getContextPath() %>/assets/images/logo-mark.svg">

<%-- Google Fonts: Optimized loading with display=swap --%>
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link rel="preload" href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700;1,400;1,500&family=Inter:wght@300;400;500;600;700&display=swap" as="style">
<link href="https://fonts.googleapis.com/css2?family=Cormorant+Garamond:ital,wght@0,400;0,500;0,600;0,700;1,400;1,500&family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">

<%-- Preload critical CSS for faster rendering --%>
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/design-tokens.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/reset.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/base.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/layout.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/utilities.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/responsive-utilities.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/premium-core.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/components/product-card.css" as="style">
<link rel="preload" href="<%= request.getContextPath() %>/assets/css/main.css" as="style">

<%-- Design system ALWAYS loads first (correct order) --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/design-tokens.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/reset.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/base.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/layout.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/utilities.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/responsive-utilities.css">
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/premium-core.css">

<%-- Unified component styles --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/components/product-card.css">

<%-- DNS prefetch for external resources --%>
<link rel="dns-prefetch" href="//fonts.googleapis.com">
<link rel="dns-prefetch" href="//fonts.gstatic.com">

<%-- Async load non-critical CSS to prevent render-blocking --%>
<script>
(function() {
    // Load main.css asynchronously
    function loadCSS(href) {
        var link = document.createElement('link');
        link.rel = 'stylesheet';
        link.href = href;
        link.media = 'print';
        link.onload = function() { link.media = 'all'; };
        document.head.appendChild(link);
    }
    
    // Load non-critical CSS after critical CSS
    window.addEventListener('load', function() {
        loadCSS('<%= request.getContextPath() %>/assets/css/main.css');
    });
})();
</script>

<%-- Global context and CSRF must be set before any external script reads them --%>
<script>
    window.contextPath = '<%= request.getContextPath() %>';
    window.csrfToken = '<%= request.getAttribute("csrfToken") != null ? org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(request.getAttribute("csrfToken").toString()) : "" %>';
</script>

<%-- Critical JavaScript (inline for performance) --%>
<script>
    // Theme initialization MUST run before CSS loads to prevent flicker
    (function() {
        const STORAGE_KEY = 'fashionstore-theme';
        const THEME_ATTR = 'data-theme';
        const DARK_CLASS = 'dark-mode';
        
        // Check localStorage first
        const stored = localStorage.getItem(STORAGE_KEY);
        if (stored === 'dark') {
            document.documentElement.setAttribute(THEME_ATTR, 'dark');
            document.documentElement.classList.add(DARK_CLASS);
        } else if (stored === 'light') {
            document.documentElement.removeAttribute(THEME_ATTR);
            document.documentElement.classList.remove(DARK_CLASS);
        } else if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
            // Fall back to system preference
            document.documentElement.setAttribute(THEME_ATTR, 'dark');
            document.documentElement.classList.add(DARK_CLASS);
        }
    })();
    
    // Splash screen - must run before page renders
    document.documentElement.classList.add('splash-active');
    window.addEventListener('load', function() {
        document.documentElement.classList.remove('splash-active');
    });
</script>

<%-- Modular JavaScript - deferred for non-blocking load --%>
<%-- Core utilities (must load first) --%>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/utilities.js"></script>

<%-- Notifications (used by other modules) --%>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/notifications.js"></script>

<%-- Theme module --%>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/theme.js"></script>

<%-- Navbar module --%>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/navbar.js"></script>

<%-- Search module --%>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/search.js"></script>

<%-- Cart drawer module --%>
<% if (_pageCSS == null || !_pageCSS.contains("auth")) { %>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/cart-drawer.js"></script>
<% } %>

<%-- Product interactions module --%>
<% if (_pageCSS == null || !_pageCSS.contains("auth")) { %>
<script defer src="<%= request.getContextPath() %>/assets/js/modules/product-interactions.js"></script>
<% } %>

<%-- Cart.js - skip on auth pages to reduce loading --%>
<% if (_pageCSS == null || !_pageCSS.contains("auth")) { %>
<script defer src="<%= request.getContextPath() %>/assets/js/cart.js"></script>
<% } %>

<%-- Lazy-loading.js - skip on auth pages to reduce loading --%>
<% if (_pageCSS == null || !_pageCSS.contains("auth")) { %>
<script defer src="<%= request.getContextPath() %>/assets/js/lazy-loading.js"></script>
<% } %>

<%-- Consolidated component styles (single HTTP request) --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/main.css">

<%-- Page-level CSS (comma-separated filenames) --%>
<% if (_pageCSS != null && !_pageCSS.trim().isEmpty()) {
       for (String _css : _pageCSS.split(",")) {
           _css = _css.trim();
           if (!_css.isEmpty()) { %>
<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/<%= ("account".equals(_css) || "admin".equals(_css)) ? _css + ".css" : "pages/" + _css + ".css" %>">
<%     }
   }
} %>
<%-- Unified CSS architecture ends above; no overlay layers. --%>
