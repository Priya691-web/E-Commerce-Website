<%@ page contentType="text/html;charset=UTF-8" %>
<%
    Object userObj = session.getAttribute("user");
    com.fashionstore.model.User user = (userObj instanceof com.fashionstore.model.User) ? (com.fashionstore.model.User) userObj : null;
%>

<!-- ENTERPRISE-GRADE FOOTER ECOSYSTEM -->
<footer class="commerce-footer-v2" data-commerce-footer-v2>
    <!-- FOOTER NEWSLETTER SECTION -->
    <div class="footer-newsletter" id="footer-newsletter">
        <div class="container">
            <div class="newsletter-content">
                <div class="newsletter-info">
                    <h3 class="newsletter-title">Stay in Style</h3>
                    <p class="newsletter-description">Get exclusive offers, new arrivals, and fashion inspiration delivered to your inbox</p>
                </div>
                <form class="newsletter-form" id="footer-newsletter-form" action="<%= request.getContextPath() %>/api/newsletter/subscribe" method="post">
                    <div class="newsletter-input-group">
                        <input type="email" name="email" class="newsletter-input" placeholder="Enter your email address" required>
                        <button type="submit" class="newsletter-btn">
                            <span class="btn-text">Subscribe</span>
                            <svg class="btn-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <line x1="22" y1="2" x2="11" y2="13"></line>
                                <polygon points="22,2 15,22 11,13 2,9 22,2"></polygon>
                            </svg>
                        </button>
                    </div>
                    <div class="newsletter-privacy">
                        <input type="checkbox" id="newsletter-privacy" name="privacy" required>
                        <label for="newsletter-privacy">I agree to the <a href="<%= request.getContextPath() %>/policy/privacy-policy">Privacy Policy</a> and <a href="<%= request.getContextPath() %>/policy/terms-of-service">Terms of Service</a></label>
                    </div>
                </form>
                <div class="newsletter-success" id="newsletter-success" style="display: none;">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                        <polyline points="22,4 12,14.01 9,11.01"></polyline>
                    </svg>
                    <span>Thank you for subscribing!</span>
                </div>
            </div>
        </div>
    </div>

    <!-- MAIN FOOTER CONTENT -->
    <div class="footer-main">
        <div class="container">
            <div class="footer-grid">
                
                <!-- BRAND SECTION -->
                <div class="footer-brand">
                    <div class="footer-brand-logo">
                        <img src="<%= request.getContextPath() %>/assets/images/logo.svg" alt="FashionStore" class="brand-logo">
                    </div>
                    <p class="footer-brand-description">
                        FashionStore is your premier destination for modern fashion and style. Discover curated collections, exclusive deals, and personalized shopping experiences.
                    </p>
                    
                    <!-- Social Media Links -->
                    <div class="footer-social">
                        <h4 class="social-title">Follow Us</h4>
                        <div class="social-links" id="social-links">
                            <!-- Populated by JavaScript -->
                        </div>
                    </div>
                    
                    <!-- App Download -->
                    <div class="footer-apps">
                        <h4 class="apps-title">Download Our App</h4>
                        <div class="app-buttons">
                            <a href="#" class="app-button app-store">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M17.05 20.28c-.98.95-2.05.8-3.08.35-1.09-.46-2.09-1.03-3.07-1.66-1.03-.66-1.94-1.42-2.75-2.3-.83-.89-1.56-1.85-2.29-2.84-.48-.66-.92-1.34-.32-2.3.33-.48.63-.96.97-1.42.23-.31.53-.5.87-.5.35 0 .63.15.87.39.29.29.54.62.79.94.2.26.39.53.58.8.2.28.37.58.53.88.08.15.08.32.01.47-.09.2-.21.36-.36.5-.14.13-.27.27-.41.41-.02.02-.04.04-.07.06-.15.13-.29.28-.4.45-.08.12-.09.25-.04.38.05.15.12.29.2.43.11.2.22.4.35.59.15.22.31.43.49.63.19.21.39.41.61.59.21.18.44.34.68.48.27.16.54.31.82.44.32.15.64.27.96.36.15.04.3.06.45.04.2-.03.39-.09.56-.18.18-.09.35-.2.5-.32.16-.13.31-.26.45-.41.14-.15.27-.3.4-.46.13-.16.25-.32.37-.49.11-.16.22-.32.31-.49.1-.18.19-.36.26-.55.08-.21.15-.42.21-.64.05-.2.09-.4.12-.6.03-.22.05-.44.05-.66 0-.21-.02-.42-.05-.63-.03-.2-.07-.4-.12-.6-.06-.22-.12-.43-.21-.64-.07-.2-.16-.39-.26-.58-.1-.19-.2-.37-.31-.55-.12-.19-.24-.37-.37-.55-.13-.18-.26-.35-.4-.51-.15-.16-.3-.31-.45-.45-.15-.14-.3-.28-.46-.41-.16-.13-.32-.26-.49-.37-.17-.11-.34-.22-.52-.31-.18-.09-.37-.17-.56-.24-.2-.07-.4-.13-.6-.18-.21-.05-.42-.09-.63-.11-.22-.02-.44-.03-.66-.03-.22 0-.44.01-.66.03-.21.02-.42.06-.63.11-.2.05-.4.11-.6.18-.19.07-.38.15-.56.24-.18.09-.35.2-.52.31-.17.11-.34.24-.49.37-.15.13-.3.27-.45.41-.14.14-.28.29-.41.45-.13.16-.25.32-.36.49-.11.18-.21.36-.3.55-.09.19-.17.38-.24.58-.07.21-.13.42-.18.64-.05.2-.09.4-.11.61-.02.21-.03.43-.03.65 0 .22.01.43.03.65.02.21.06.41.11.61.05.22.11.43.18.64.07.2.15.39.24.58.09.19.19.37.3.55.11.17.23.33.36.49.13.16.26.31.41.45.15.14.3.28.45.41.16.13.32.26.49.37.18.11.35.22.53.31.19.09.38.17.58.24.2.07.41.13.62.18.21.05.43.09.64.11.22.02.44.03.66.03.22 0 .44-.01.66-.03.21-.02.42-.06.63-.11.21-.05.42-.11.62-.18.2-.07.39-.15.58-.24.18-.09.35-.2.52-.31.17-.11.34-.24.49-.37.15-.13.3-.27.45-.41.14-.14.28-.29.41-.45.13-.16.25-.32.36-.49.11-.18.21-.36.3-.55.09-.19.17-.38.24-.58.07-.21.13-.42.18-.64.05-.2.09-.4.11-.61.02-.21.03-.43.03-.65 0-.22-.01-.43-.03-.65-.02-.21-.06-.41-.11-.61-.05-.21-.11-.42-.18-.64-.07-.2-.15-.39-.24-.58-.09-.19-.19-.37-.3-.55-.11-.17-.23-.33-.36-.49-.13-.16-.26-.31-.41-.45-.15-.14-.3-.28-.45-.41-.16-.13-.32-.26-.49-.37-.18-.11-.35-.22-.53-.31-.19-.09-.38-.17-.58-.24-.2-.07-.41-.13-.62-.18-.21-.05-.43-.09-.64-.11-.22-.02-.44-.03-.66-.03z"/>
                                </svg>
                                <div class="app-info">
                                    <span class="app-store-text">Download on the</span>
                                    <span class="app-store-name">App Store</span>
                                </div>
                            </a>
                            <a href="#" class="app-button google-play">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                                    <path d="M3 20.5v-17c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5v17c0 .83-.67 1.5-1.5 1.5s-1.5-.67-1.5-1.5zm18 0c0 .83-.67 1.5-1.5 1.5s-1.5-.67-1.5-1.5v-17c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5v17zm-9 0c0 .83-.67 1.5-1.5 1.5s-1.5-.67-1.5-1.5v-17c0-.83.67-1.5 1.5-1.5s1.5.67 1.5 1.5v17z"/>
                                </svg>
                                <div class="app-info">
                                    <span class="google-play-text">Get it on</span>
                                    <span class="google-play-name">Google Play</span>
                                </div>
                            </a>
                        </div>
                    </div>
                </div>

                <!-- GET TO KNOW US -->
                <div class="footer-section">
                    <h3 class="footer-heading">
                        Get to Know Us
                        <button class="footer-toggle" aria-expanded="false" aria-label="Toggle Get to Know Us section">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                    </h3>
                    <div class="footer-links" id="get-to-know-us-links">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>

                <!-- CONNECT WITH US -->
                <div class="footer-section">
                    <h3 class="footer-heading">
                        Connect With Us
                        <button class="footer-toggle" aria-expanded="false" aria-label="Toggle Connect With Us section">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                    </h3>
                    <div class="footer-links" id="connect-with-us-links">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>

                <!-- MAKE MONEY WITH US -->
                <div class="footer-section">
                    <h3 class="footer-heading">
                        Make Money With Us
                        <button class="footer-toggle" aria-expanded="false" aria-label="Toggle Make Money With Us section">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                    </h3>
                    <div class="footer-links" id="make-money-with-us-links">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>

                <!-- CUSTOMER SUPPORT -->
                <div class="footer-section">
                    <h3 class="footer-heading">
                        Customer Support
                        <button class="footer-toggle" aria-expanded="false" aria-label="Toggle Customer Support section">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                    </h3>
                    <div class="footer-links" id="customer-support-links">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>

                <!-- LEGAL & POLICIES -->
                <div class="footer-section">
                    <h3 class="footer-heading">
                        Legal & Policies
                        <button class="footer-toggle" aria-expanded="false" aria-label="Toggle Legal & Policies section">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                    </h3>
                    <div class="footer-links" id="legal-policies-links">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>

                <!-- MEMBERSHIP -->
                <div class="footer-section">
                    <h3 class="footer-heading">
                        Membership
                        <button class="footer-toggle" aria-expanded="false" aria-label="Toggle Membership section">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                    </h3>
                    <div class="footer-links" id="membership-links">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- PAYMENT METHODS & TRUST BADGES -->
    <div class="footer-payment-trust">
        <div class="container">
            <div class="payment-trust-grid">
                <!-- Payment Methods -->
                <div class="payment-methods">
                    <h4 class="payment-trust-title">Payment Methods</h4>
                    <div class="payment-icons" id="payment-icons">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>
                
                <!-- Trust Badges -->
                <div class="trust-badges">
                    <h4 class="payment-trust-title">Why Shop With Us</h4>
                    <div class="trust-badges-list" id="trust-badges-list">
                        <!-- Populated by JavaScript -->
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- FOOTER BOTTOM -->
    <div class="footer-bottom">
        <div class="container">
            <div class="footer-bottom-content">
                <div class="footer-copyright">
                    <p>&copy; 2026 FashionStore. All rights reserved.</p>
                    <p class="copyright-note">FashionStore is a registered trademark. All product names, logos, and brands are property of their respective owners.</p>
                </div>
                
                <div class="footer-bottom-links">
                    <div class="legal-links" id="legal-links">
                        <!-- Populated by JavaScript -->
                    </div>
                    <div class="region-links">
                        <select class="region-selector" id="region-selector">
                            <option value="in">🇮🇳 India</option>
                            <option value="us">🇺🇸 United States</option>
                            <option value="uk">🇬🇧 United Kingdom</option>
                            <option value="ca">🇨🇦 Canada</option>
                            <option value="au">🇦🇺 Australia</option>
                        </select>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- BACK TO TOP BUTTON -->
    <button class="back-to-top" id="back-to-top" aria-label="Back to top">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="12" y1="19" x2="12" y2="5"></line>
            <polyline points="5,12 12,5 19,12"></polyline>
        </svg>
    </button>
</footer>

<!-- COOKIE CONSENT BANNER -->
<div class="cookie-consent" id="cookie-consent" style="display: none;">
    <div class="cookie-content">
        <div class="cookie-info">
            <h4>🍪 Cookie Notice</h4>
            <p>We use cookies to enhance your experience, analyze site traffic, and personalize content. By continuing to use our site, you agree to our use of cookies.</p>
        </div>
        <div class="cookie-actions">
            <button class="cookie-btn cookie-accept" id="cookie-accept">Accept All</button>
            <button class="cookie-btn cookie-customize" id="cookie-customize">Customize</button>
            <button class="cookie-btn cookie-reject" id="cookie-reject">Reject All</button>
        </div>
    </div>
</div>

<!-- COOKIE PREFERENCES MODAL -->
<div class="cookie-modal" id="cookie-modal" style="display: none;">
    <div class="cookie-modal-content">
        <div class="cookie-modal-header">
            <h3>Cookie Preferences</h3>
            <button class="cookie-modal-close" id="cookie-modal-close" aria-label="Close">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"></line>
                    <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
            </button>
        </div>
        <div class="cookie-modal-body">
            <div class="cookie-category">
                <div class="cookie-category-header">
                    <h4>Essential Cookies</h4>
                    <label class="toggle-switch">
                        <input type="checkbox" checked disabled>
                        <span class="slider"></span>
                    </label>
                </div>
                <p>These cookies are necessary for the website to function and cannot be switched off.</p>
            </div>
            <div class="cookie-category">
                <div class="cookie-category-header">
                    <h4>Performance Cookies</h4>
                    <label class="toggle-switch">
                        <input type="checkbox" id="performance-cookies" checked>
                        <span class="slider"></span>
                    </label>
                </div>
                <p>These cookies help us understand how visitors interact with our website.</p>
            </div>
            <div class="cookie-category">
                <div class="cookie-category-header">
                    <h4>Functional Cookies</h4>
                    <label class="toggle-switch">
                        <input type="checkbox" id="functional-cookies">
                        <span class="slider"></span>
                    </label>
                </div>
                <p>These cookies enable enhanced functionality and personalization.</p>
            </div>
            <div class="cookie-category">
                <div class="cookie-category-header">
                    <h4>Marketing Cookies</h4>
                    <label class="toggle-switch">
                        <input type="checkbox" id="marketing-cookies">
                        <span class="slider"></span>
                    </label>
                </div>
                <p>These cookies are used to deliver advertisements that are relevant to you.</p>
            </div>
        </div>
        <div class="cookie-modal-footer">
            <button class="cookie-btn cookie-save" id="cookie-save">Save Preferences</button>
        </div>
    </div>
</div>

<script>
// FashionStore Footer V2 - Enterprise-Grade Footer Ecosystem
(function() {
    'use strict';

    // Configuration
    const CONFIG = {
        apiEndpoints: {
            footerContent: '<%= request.getContextPath() %>/api/footer/content',
            footerLinks: '<%= request.getContextPath() %>/api/footer/links',
            socialLinks: '<%= request.getContextPath() %>/api/footer/social-links',
            paymentMethods: '<%= request.getContextPath() %>/api/footer/payment-methods',
            trustBadges: '<%= request.getContextPath() %>/api/footer/trust-badges',
            newsletterSubscribe: '<%= request.getContextPath() %>/api/newsletter/subscribe',
            trackLinkClick: '<%= request.getContextPath() %>/api/footer/track-link-click'
        },
        cookieConsentKey: 'fashionstore_cookie_consent',
        backToTopThreshold: 300,
        newsletterDebounceDelay: 500
    };

    // DOM Elements
    let elements = {};

    // Initialize
    function init() {
        cacheElements();
        bindEvents();
        loadFooterContent();
        initializeCookieConsent();
        initializeBackToTop();
        initializeNewsletter();
        initializeMobileToggles();
    }

    // Cache DOM elements
    function cacheElements() {
        elements = {
            // Footer sections
            getToKnowUsLinks: document.getElementById('get-to-know-us-links'),
            connectWithUsLinks: document.getElementById('connect-with-us-links'),
            makeMoneyLinks: document.getElementById('make-money-with-us-links'),
            customerSupportLinks: document.getElementById('customer-support-links'),
            legalPoliciesLinks: document.getElementById('legal-policies-links'),
            membershipLinks: document.getElementById('membership-links'),
            legalLinks: document.getElementById('legal-links'),
            
            // Social and payment
            socialLinks: document.getElementById('social-links'),
            paymentIcons: document.getElementById('payment-icons'),
            trustBadgesList: document.getElementById('trust-badges-list'),
            
            // Newsletter
            newsletterForm: document.getElementById('footer-newsletter-form'),
            newsletterSuccess: document.getElementById('newsletter-success'),
            
            // Cookie consent
            cookieConsent: document.getElementById('cookie-consent'),
            cookieModal: document.getElementById('cookie-modal'),
            cookieAccept: document.getElementById('cookie-accept'),
            cookieCustomize: document.getElementById('cookie-customize'),
            cookieReject: document.getElementById('cookie-reject'),
            cookieModalClose: document.getElementById('cookie-modal-close'),
            cookieSave: document.getElementById('cookie-save'),
            
            // Back to top
            backToTop: document.getElementById('back-to-top'),
            
            // Region selector
            regionSelector: document.getElementById('region-selector'),
            
            // Mobile toggles
            footerToggles: document.querySelectorAll('.footer-toggle')
        };
    }

    // Bind events
    function bindEvents() {
        // Newsletter form
        if (elements.newsletterForm) {
            elements.newsletterForm.addEventListener('submit', handleNewsletterSubmit);
        }

        // Cookie consent
        if (elements.cookieAccept) {
            elements.cookieAccept.addEventListener('click', () => handleCookieConsent('all'));
        }
        if (elements.cookieCustomize) {
            elements.cookieCustomize.addEventListener('click', showCookieModal);
        }
        if (elements.cookieReject) {
            elements.cookieReject.addEventListener('click', () => handleCookieConsent('essential'));
        }
        if (elements.cookieModalClose) {
            elements.cookieModalClose.addEventListener('click', hideCookieModal);
        }
        if (elements.cookieSave) {
            elements.cookieSave.addEventListener('click', saveCookiePreferences);
        }

        // Back to top
        if (elements.backToTop) {
            elements.backToTop.addEventListener('click', scrollToTop);
        }

        // Region selector
        if (elements.regionSelector) {
            elements.regionSelector.addEventListener('change', handleRegionChange);
        }

        // Mobile toggles
        elements.footerToggles.forEach(toggle => {
            toggle.addEventListener('click', handleFooterToggle);
        });

        // Scroll events
        window.addEventListener('scroll', handleScroll);

        // Click outside to close modals
        document.addEventListener('click', handleOutsideClick);
    }

    // Load footer content
    function loadFooterContent() {
        loadFooterLinks();
        loadSocialLinks();
        loadPaymentMethods();
        loadTrustBadges();
    }

    // Load footer links
    function loadFooterLinks() {
        fetch(CONFIG.apiEndpoints.footerLinks, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                renderFooterLinks(data.links);
            }
        })
        .catch(error => {
            console.error('Error loading footer links:', error);
            // Fallback to static links
            renderStaticFooterLinks();
        });
    }

    function renderFooterLinks(links) {
        const sections = {
            'get-to-know-us': elements.getToKnowUsLinks,
            'connect-with-us': elements.connectWithUsLinks,
            'make-money-with-us': elements.makeMoneyLinks,
            'customer-support': elements.customerSupportLinks,
            'legal-policies': elements.legalPoliciesLinks,
            'membership': elements.membershipLinks
        };

        links.forEach(link => {
            const container = sections[link.section];
            if (container) {
                const linkElement = createFooterLink(link);
                container.appendChild(linkElement);
            }
        });

        // Add legal links to bottom
        if (elements.legalLinks) {
            const legalLinks = links.filter(link => link.section === 'legal-policies');
            legalLinks.forEach(link => {
                const linkElement = createFooterLink(link, 'footer-legal-link');
                elements.legalLinks.appendChild(linkElement);
            });
        }
    }

    function createFooterLink(link, className = 'footer-link') {
        const a = document.createElement('a');
        a.href = link.url;
        a.className = className;
        a.textContent = link.title;
        a.target = link.target || '_self';
        a.setAttribute('data-link-id', link.linkId);
        a.setAttribute('data-section', link.section);
        
        // Track link clicks
        a.addEventListener('click', (e) => trackLinkClick(e, link));
        
        return a;
    }

    function renderStaticFooterLinks() {
        // Fallback static links if API fails
        const staticLinks = {
            'get-to-know-us': [
                { title: 'About Us', url: '/policy/about-us', section: 'get-to-know-us' },
                { title: 'Careers', url: '/policy/careers', section: 'get-to-know-us' },
                { title: 'Blog', url: '/blog', section: 'get-to-know-us' }
            ],
            'customer-support': [
                { title: 'Help Center', url: '/help', section: 'customer-support' },
                { title: 'Contact Us', url: '/contact', section: 'customer-support' },
                { title: 'Track Order', url: '/track', section: 'customer-support' }
            ],
            'legal-policies': [
                { title: 'Privacy Policy', url: '/policy/privacy-policy', section: 'legal-policies' },
                { title: 'Terms of Service', url: '/policy/terms-of-service', section: 'legal-policies' },
                { title: 'Cookie Policy', url: '/policy/cookie-policy', section: 'legal-policies' }
            ]
        };

        Object.values(staticLinks).forEach(links => {
            renderFooterLinks(links);
        });
    }

    // Load social links
    function loadSocialLinks() {
        fetch(CONFIG.apiEndpoints.socialLinks, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                renderSocialLinks(data.socialLinks);
            }
        })
        .catch(error => {
            console.error('Error loading social links:', error);
        });
    }

    function renderSocialLinks(socialLinks) {
        if (!elements.socialLinks) return;

        socialLinks.forEach(social => {
            const a = document.createElement('a');
            a.href = social.url;
            a.className = 'social-link';
            a.target = '_blank';
            a.rel = 'noopener noreferrer';
            a.setAttribute('aria-label', `Follow us on ${social.display_name}`);
            
            a.innerHTML = `
                <i class="${social.icon_class}" style="color: ${social.color}"></i>
            `;
            
            elements.socialLinks.appendChild(a);
        });
    }

    // Load payment methods
    function loadPaymentMethods() {
        fetch(CONFIG.apiEndpoints.paymentMethods, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                renderPaymentMethods(data.paymentMethods);
            }
        })
        .catch(error => {
            console.error('Error loading payment methods:', error);
        });
    }

    function renderPaymentMethods(paymentMethods) {
        if (!elements.paymentIcons) return;

        paymentMethods.forEach(method => {
            const div = document.createElement('div');
            div.className = 'payment-icon';
            div.setAttribute('title', method.display_name);
            
            div.innerHTML = `
                <i class="${method.icon_class}"></i>
            `;
            
            elements.paymentIcons.appendChild(div);
        });
    }

    // Load trust badges
    function loadTrustBadges() {
        fetch(CONFIG.apiEndpoints.trustBadges, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                renderTrustBadges(data.trustBadges);
            }
        })
        .catch(error => {
            console.error('Error loading trust badges:', error);
        });
    }

    function renderTrustBadges(trustBadges) {
        if (!elements.trustBadgesList) return;

        trustBadges.forEach(badge => {
            const div = document.createElement('div');
            div.className = 'trust-badge';
            div.setAttribute('title', badge.display_name);
            
            div.innerHTML = `
                <i class="${badge.icon_class}"></i>
                <span>${badge.display_name}</span>
            `;
            
            elements.trustBadgesList.appendChild(div);
        });
    }

    // Newsletter functionality
    function initializeNewsletter() {
        // Newsletter is initialized in bindEvents
    }

    function handleNewsletterSubmit(e) {
        e.preventDefault();
        
        const formData = new FormData(elements.newsletterForm);
        const email = formData.get('email');
        
        if (!email) return;

        // Show loading state
        const submitBtn = elements.newsletterForm.querySelector('.newsletter-btn');
        const originalText = submitBtn.querySelector('.btn-text').textContent;
        submitBtn.querySelector('.btn-text').textContent = 'Subscribing...';
        submitBtn.disabled = true;

        fetch(CONFIG.apiEndpoints.newsletterSubscribe, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': typeof csrfToken !== 'undefined' ? csrfToken : ''
            },
            body: new URLSearchParams(formData)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                showNewsletterSuccess();
                elements.newsletterForm.reset();
            } else {
                showNewsletterError(data.message || 'Subscription failed');
            }
        })
        .catch(error => {
            console.error('Newsletter subscription error:', error);
            showNewsletterError('An error occurred. Please try again.');
        })
        .finally(() => {
            submitBtn.querySelector('.btn-text').textContent = originalText;
            submitBtn.disabled = false;
        });
    }

    function showNewsletterSuccess() {
        elements.newsletterForm.style.display = 'none';
        elements.newsletterSuccess.style.display = 'flex';
        
        // Hide success message after 5 seconds
        setTimeout(() => {
            elements.newsletterSuccess.style.display = 'none';
            elements.newsletterForm.style.display = 'flex';
        }, 5000);
    }

    function showNewsletterError(message) {
        // You could implement a toast notification here
        console.error('Newsletter error:', message);
    }

    // Cookie consent functionality
    function initializeCookieConsent() {
        const consent = localStorage.getItem(CONFIG.cookieConsentKey);
        
        if (!consent) {
            elements.cookieConsent.style.display = 'block';
        }
    }

    function handleCookieConsent(level) {
        const preferences = {
            essential: true,
            performance: level === 'all',
            functional: level === 'all',
            marketing: level === 'all'
        };
        
        localStorage.setItem(CONFIG.cookieConsentKey, JSON.stringify(preferences));
        localStorage.setItem('fashionstore_cookies_accepted', 'true');
        
        elements.cookieConsent.style.display = 'none';
        
        // Apply cookie preferences
        applyCookiePreferences(preferences);
    }

    function showCookieModal() {
        elements.cookieModal.style.display = 'flex';
        document.body.style.overflow = 'hidden';
    }

    function hideCookieModal() {
        elements.cookieModal.style.display = 'none';
        document.body.style.overflow = '';
    }

    function saveCookiePreferences() {
        const preferences = {
            essential: true,
            performance: document.getElementById('performance-cookies').checked,
            functional: document.getElementById('functional-cookies').checked,
            marketing: document.getElementById('marketing-cookies').checked
        };
        
        localStorage.setItem(CONFIG.cookieConsentKey, JSON.stringify(preferences));
        localStorage.setItem('fashionstore_cookies_accepted', 'true');
        
        hideCookieModal();
        elements.cookieConsent.style.display = 'none';
        
        applyCookiePreferences(preferences);
    }

    function applyCookiePreferences(preferences) {
        // Apply cookie preferences (e.g., enable/disable analytics, marketing scripts)
        // This would typically be done by your analytics/marketing tools
        
        if (preferences.performance) {
            // Enable performance/analytics cookies
            console.log('Performance cookies enabled');
        }
        
        if (preferences.functional) {
            // Enable functional cookies
            console.log('Functional cookies enabled');
        }
        
        if (preferences.marketing) {
            // Enable marketing cookies
            console.log('Marketing cookies enabled');
        }
    }

    // Back to top functionality
    function initializeBackToTop() {
        // Back to top is initialized in bindEvents
    }

    function handleScroll() {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
        
        // Show/hide back to top button
        if (elements.backToTop) {
            if (scrollTop > CONFIG.backToTopThreshold) {
                elements.backToTop.classList.add('visible');
            } else {
                elements.backToTop.classList.remove('visible');
            }
        }
    }

    function scrollToTop() {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    }

    // Mobile toggles
    function initializeMobileToggles() {
        // Mobile toggles are initialized in bindEvents
    }

    function handleFooterToggle(e) {
        const toggle = e.currentTarget;
        const isExpanded = toggle.getAttribute('aria-expanded') === 'true';
        const footerSection = toggle.closest('.footer-section');
        const links = footerSection.querySelector('.footer-links');
        
        if (isExpanded) {
            toggle.setAttribute('aria-expanded', 'false');
            links.style.maxHeight = '0px';
            links.classList.remove('expanded');
        } else {
            toggle.setAttribute('aria-expanded', 'true');
            links.style.maxHeight = links.scrollHeight + 'px';
            links.classList.add('expanded');
        }
    }

    // Region change
    function handleRegionChange(e) {
        const region = e.target.value;
        
        // Handle region change (e.g., redirect to regional site, update currency)
        console.log('Region changed to:', region);
        
        // You could implement region-specific logic here
        // For example: redirect to regional subdomain, update prices, etc.
    }

    // Link click tracking
    function trackLinkClick(e, link) {
        const formData = new URLSearchParams({
            linkId: link.linkId,
            linkUrl: link.url,
            section: link.section
        });
        
        fetch(CONFIG.apiEndpoints.trackLinkClick, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': typeof csrfToken !== 'undefined' ? csrfToken : ''
            },
            body: formData
        }).catch(error => {
            console.error('Error tracking link click:', error);
        });
    }

    // Outside click handler
    function handleOutsideClick(e) {
        // Close cookie modal if clicking outside
        if (elements.cookieModal && elements.cookieModal.style.display === 'flex') {
            if (!e.target.closest('.cookie-modal-content')) {
                hideCookieModal();
            }
        }
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
</script>
