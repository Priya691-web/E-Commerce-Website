<%@ page contentType="text/html;charset=UTF-8" %>
<%
    Object userObj = session.getAttribute("user");
    com.fashionstore.model.User user = (userObj instanceof com.fashionstore.model.User) ? (com.fashionstore.model.User) userObj : null;
    String userInitials = "U";
    if (user != null && user.getFullName() != null && !user.getFullName().isBlank()) {
        String[] parts = user.getFullName().trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase());
        }
        userInitials = sb.toString();
    }

    int initialCartCount = 0;
    Object sessionCartItems = session.getAttribute("cartItems");
    if (sessionCartItems instanceof java.util.List) {
        java.util.List<?> items = (java.util.List<?>) sessionCartItems;
        for (Object o : items) {
            if (o instanceof com.fashionstore.model.CartItem) {
                initialCartCount += ((com.fashionstore.model.CartItem) o).getQuantity();
            }
        }
    }
%>

<!-- AMAZON/FLIPKART-STYLE NAVIGATION HEADER -->
<header class="commerce-header-v2" data-commerce-header-v2>
    <!-- TOP BAR -->
    <div class="header-top-bar">
        <div class="container">
            <div class="top-bar-left">
                <div class="delivery-location-selector" id="delivery-location-selector">
                    <button class="location-trigger" aria-label="Select delivery location">
                        <svg class="location-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                            <circle cx="12" cy="10" r="3"/>
                        </svg>
                        <span class="location-text">
                            <span class="location-label">Deliver to</span>
                            <span class="location-pincode" id="current-pincode">110001</span>
                        </span>
                        <svg class="dropdown-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6,9 12,15 18,9"/>
                        </svg>
                    </button>
                    <div class="location-dropdown" id="location-dropdown">
                        <div class="location-dropdown-header">
                            <h3>Choose your location</h3>
                            <p>Delivery options and speeds</p>
                        </div>
                        <div class="location-input-section">
                            <input type="text" id="pincode-input" placeholder="Enter pincode" maxlength="6">
                            <button type="button" id="validate-pincode-btn" class="btn btn-primary btn-sm">Check</button>
                        </div>
                        <div class="location-saved-section">
                            <h4>Saved addresses</h4>
                            <div class="saved-locations-list" id="saved-locations-list">
                                <!-- Populated by JavaScript -->
                            </div>
                        </div>
                        <div class="location-detect-section">
                            <button type="button" id="detect-location-btn" class="btn btn-outline btn-sm">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="10"/>
                                    <polyline points="12,6 12,12 16,14"/>
                                </svg>
                                Detect my location
                            </button>
                        </div>
                    </div>
                </div>
            </div>
            <div class="top-bar-right">
                <div class="header-links">
                    <% if (user != null) { %>
                        <a href="<%= request.getContextPath() %>/account/profile" class="header-link">My Account</a>
                        <a href="<%= request.getContextPath() %>/orders" class="header-link">Orders</a>
                        <a href="<%= request.getContextPath() %>/wishlist" class="header-link">Wishlist</a>
                    <% } else { %>
                        <a href="<%= request.getContextPath() %>/login" class="header-link">Sign in</a>
                        <a href="<%= request.getContextPath() %>/register" class="header-link">Create account</a>
                    <% } %>
                    <a href="<%= request.getContextPath() %>/help" class="header-link">Help</a>
                </div>
            </div>
        </div>
    </div>

    <!-- MAIN HEADER -->
    <div class="header-main">
        <div class="container">
            <div class="header-main-content">
                <!-- LOGO -->
                <div class="header-logo">
                    <a href="<%= request.getContextPath() %>/home" class="brand-logo-link" aria-label="FashionStore home">
                        <img src="<%= request.getContextPath() %>/assets/images/logo.svg" alt="FashionStore" class="brand-logo">
                    </a>
                </div>

                <!-- SEARCH BAR -->
                <div class="header-search" id="header-search">
                    <form class="search-form" action="<%= request.getContextPath() %>/products" method="get" role="search">
                        <div class="search-input-wrapper">
                            <select class="search-category" name="category" aria-label="Search category">
                                <option value="">All</option>
                                <option value="men">Men</option>
                                <option value="women">Women</option>
                                <option value="kids">Kids</option>
                                <option value="footwear">Footwear</option>
                                <option value="accessories">Accessories</option>
                            </select>
                            <input 
                                type="text" 
                                id="main-search-input" 
                                name="search" 
                                class="search-input" 
                                placeholder="Search for products, brands and more..."
                                autocomplete="off"
                                aria-label="Search products"
                            >
                            <button type="submit" class="search-submit" aria-label="Search">
                                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="11" cy="11" r="8"/>
                                    <path d="m21 21-4.35-4.35"/>
                                </svg>
                            </button>
                        </div>
                    </form>
                    
                    <!-- SEARCH SUGGESTIONS DROPDOWN -->
                    <div class="search-suggestions" id="search-suggestions">
                        <div class="suggestions-header">
                            <span class="suggestions-title">Suggestions</span>
                            <button class="clear-search-history" id="clear-search-history" style="display: none;">
                                Clear history
                            </button>
                        </div>
                        <div class="suggestions-content">
                            <div class="suggestion-section" id="recent-searches-section" style="display: none;">
                                <h4 class="suggestion-section-title">Recent searches</h4>
                                <div class="suggestion-list" id="recent-searches-list"></div>
                            </div>
                            <div class="suggestion-section" id="trending-searches-section">
                                <h4 class="suggestion-section-title">Trending searches</h4>
                                <div class="suggestion-list" id="trending-searches-list"></div>
                            </div>
                            <div class="suggestion-section" id="product-suggestions-section" style="display: none;">
                                <h4 class="suggestion-section-title">Products</h4>
                                <div class="suggestion-list" id="product-suggestions-list"></div>
                            </div>
                            <div class="suggestion-section" id="category-suggestions-section" style="display: none;">
                                <h4 class="suggestion-section-title">Categories</h4>
                                <div class="suggestion-list" id="category-suggestions-list"></div>
                            </div>
                            <div class="suggestion-section" id="brand-suggestions-section" style="display: none;">
                                <h4 class="suggestion-section-title">Brands</h4>
                                <div class="suggestion-list" id="brand-suggestions-list"></div>
                            </div>
                        </div>
                        <div class="suggestions-footer">
                            <div class="search-tips">
                                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="12" cy="12" r="10"/>
                                    <line x1="12" y1="16" x2="12" y2="12"/>
                                    <line x1="12" y1="8" x2="12.01" y2="8"/>
                                </svg>
                                <span>Press Enter to search</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- HEADER ACTIONS -->
                <div class="header-actions">
                    <!-- ACCOUNT -->
                    <div class="header-account" id="header-account">
                        <button class="account-trigger" aria-label="Account menu" aria-expanded="false">
                            <svg class="account-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                <circle cx="12" cy="7" r="4"/>
                            </svg>
                            <span class="account-text">
                                <% if (user != null) { %>
                                    <span class="account-greeting">Hello, <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getFullName().split(" ")[0]) %></span>
                                    <span class="account-label">Account</span>
                                <% } else { %>
                                    <span class="account-greeting">Hello, sign in</span>
                                    <span class="account-label">Account & Lists</span>
                                <% } %>
                            </span>
                            <svg class="dropdown-icon" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <polyline points="6,9 12,15 18,9"/>
                            </svg>
                        </button>
                        
                        <div class="account-dropdown" id="account-dropdown-v2">
                            <% if (user != null) { %>
                                <div class="account-dropdown-header">
                                    <div class="user-avatar"><%= userInitials %></div>
                                    <div class="user-info">
                                        <div class="user-name"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getFullName()) %></div>
                                        <div class="user-email"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getEmail()) %></div>
                                    </div>
                                </div>
                                <div class="account-dropdown-divider"></div>
                                <div class="account-menu-section">
                                    <h4 class="menu-section-title">Your Account</h4>
                                    <a href="<%= request.getContextPath() %>/account/profile" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                                            <circle cx="12" cy="7" r="4"/>
                                        </svg>
                                        My Profile
                                    </a>
                                    <a href="<%= request.getContextPath() %>/account/addresses" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                                            <circle cx="12" cy="10" r="3"/>
                                        </svg>
                                        Addresses
                                    </a>
                                    <a href="<%= request.getContextPath() %>/account/payment-methods" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <rect x="1" y="4" width="22" height="16" rx="2" ry="2"/>
                                            <line x1="1" y1="10" x2="23" y2="10"/>
                                        </svg>
                                        Payment Methods
                                    </a>
                                </div>
                                <div class="account-dropdown-divider"></div>
                                <div class="account-menu-section">
                                    <h4 class="menu-section-title">Orders & Returns</h4>
                                    <a href="<%= request.getContextPath() %>/orders" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M9 11l3 3L22 4"/>
                                            <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
                                        </svg>
                                        Your Orders
                                    </a>
                                    <a href="<%= request.getContextPath() %>/returns" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                            <circle cx="12" cy="12" r="3"/>
                                        </svg>
                                        Returns & Cancellations
                                    </a>
                                </div>
                                <div class="account-dropdown-divider"></div>
                                <div class="account-menu-section">
                                    <h4 class="menu-section-title">More</h4>
                                    <a href="<%= request.getContextPath() %>/wishlist" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                                        </svg>
                                        Wishlist
                                    </a>
                                    <a href="<%= request.getContextPath() %>/coupons" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7z"/>
                                            <circle cx="12" cy="12" r="3"/>
                                        </svg>
                                        Coupons
                                    </a>
                                    <a href="<%= request.getContextPath() %>/membership" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/>
                                        </svg>
                                        Membership
                                    </a>
                                </div>
                                <div class="account-dropdown-divider"></div>
                                <a href="<%= request.getContextPath() %>/logout" class="account-menu-item logout-item">
                                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                        <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                                        <polyline points="16 17 21 12 16 7"/>
                                        <line x1="21" y1="12" x2="9" y2="12"/>
                                    </svg>
                                    Sign out
                                </a>
                            <% } else { %>
                                <div class="account-menu-section">
                                    <a href="<%= request.getContextPath() %>/login" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
                                            <polyline points="10 17 15 12 10 7"/>
                                            <line x1="15" y1="12" x2="3" y2="12"/>
                                        </svg>
                                        Sign in
                                    </a>
                                    <a href="<%= request.getContextPath() %>/register" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                                            <circle cx="8.5" cy="7" r="4"/>
                                            <line x1="20" y1="8" x2="20" y2="14"/>
                                            <line x1="23" y1="11" x2="17" y2="11"/>
                                        </svg>
                                        Create account
                                    </a>
                                </div>
                                <div class="account-dropdown-divider"></div>
                                <div class="account-menu-section">
                                    <h4 class="menu-section-title">Your Orders</h4>
                                    <a href="<%= request.getContextPath() %>/orders" class="account-menu-item">
                                        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                            <path d="M9 11l3 3L22 4"/>
                                            <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
                                        </svg>
                                        Track your order
                                    </a>
                                </div>
                            <% } %>
                        </div>
                    </div>

                    <!-- RETURNS & ORDERS -->
                    <div class="header-returns">
                        <a href="<%= request.getContextPath() %>/orders" class="returns-link">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M9 11l3 3L22 4"/>
                                <path d="M21 12v7a2 2 0 01-2 2H5a2 2 0 01-2-2V5a2 2 0 012-2h11"/>
                            </svg>
                            <span class="returns-text">
                                <span class="returns-label">Returns</span>
                                <span class="returns-sublabel">& Orders</span>
                            </span>
                        </a>
                    </div>

                    <!-- WISHLIST -->
                    <div class="header-wishlist">
                        <a href="<%= request.getContextPath() %>/wishlist" class="wishlist-link" aria-label="Wishlist">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
                            </svg>
                            <span class="wishlist-label">Wishlist</span>
                        </a>
                    </div>

                    <!-- CART -->
                    <div class="header-cart">
                        <a href="<%= request.getContextPath() %>/cart" class="cart-link" aria-label="Shopping cart">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M9 22a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/>
                                <path d="M20 22a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/>
                                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
                            </svg>
                            <span class="cart-label">Cart</span>
                            <span class="cart-badge" id="cart-badge-v2"><%= initialCartCount %></span>
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- NAVIGATION MENU -->
    <div class="header-nav">
        <div class="container">
            <nav class="main-nav" role="navigation" aria-label="Main navigation">
                <ul class="nav-list">
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?category=women" class="nav-link">Women</a>
                    </li>
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?category=men" class="nav-link">Men</a>
                    </li>
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?category=kids" class="nav-link">Kids</a>
                    </li>
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?category=footwear" class="nav-link">Footwear</a>
                    </li>
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?category=accessories" class="nav-link">Accessories</a>
                    </li>
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?tag=new" class="nav-link nav-link-highlight">New</a>
                    </li>
                    <li class="nav-item">
                        <a href="<%= request.getContextPath() %>/products?tag=deals" class="nav-link nav-link-sale">Sale</a>
                    </li>
                </ul>
            </nav>
        </div>
    </div>
</header>

<!-- MOBILE NAVIGATION -->
<nav class="mobile-nav-v2" id="mobile-nav-v2" role="navigation" aria-label="Mobile navigation">
    <div class="mobile-nav-header">
        <div class="mobile-nav-brand">
            <img src="<%= request.getContextPath() %>/assets/images/logo.svg" alt="FashionStore" class="mobile-brand-logo">
        </div>
        <button class="mobile-nav-close" id="mobile-nav-close" aria-label="Close navigation">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
        </button>
    </div>
    
    <div class="mobile-nav-content">
        <!-- Mobile Search -->
        <div class="mobile-search">
            <form class="mobile-search-form" action="<%= request.getContextPath() %>/products" method="get">
                <input type="text" name="search" placeholder="Search products..." class="mobile-search-input">
                <button type="submit" class="mobile-search-btn">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="11" cy="11" r="8"/>
                        <path d="m21 21-4.35-4.35"/>
                    </svg>
                </button>
            </form>
        </div>

        <!-- Mobile Location -->
        <div class="mobile-location">
            <div class="mobile-location-trigger">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/>
                    <circle cx="12" cy="10" r="3"/>
                </svg>
                <span>Deliver to <span id="mobile-current-pincode">110001</span></span>
            </div>
        </div>

        <!-- Mobile Account -->
        <% if (user != null) { %>
            <div class="mobile-account">
                <div class="mobile-account-header">
                    <div class="mobile-user-avatar"><%= userInitials %></div>
                    <div class="mobile-user-info">
                        <div class="mobile-user-name"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getFullName()) %></div>
                        <div class="mobile-user-email"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getEmail()) %></div>
                    </div>
                </div>
            </div>
        <% } %>

        <!-- Mobile Menu -->
        <div class="mobile-menu">
            <ul class="mobile-menu-list">
                <li><a href="<%= request.getContextPath() %>/products?category=women" class="mobile-menu-link">Women</a></li>
                <li><a href="<%= request.getContextPath() %>/products?category=men" class="mobile-menu-link">Men</a></li>
                <li><a href="<%= request.getContextPath() %>/products?category=kids" class="mobile-menu-link">Kids</a></li>
                <li><a href="<%= request.getContextPath() %>/products?category=footwear" class="mobile-menu-link">Footwear</a></li>
                <li><a href="<%= request.getContextPath() %>/products?category=accessories" class="mobile-menu-link">Accessories</a></li>
                <li><a href="<%= request.getContextPath() %>/products?tag=new" class="mobile-menu-link">New Arrivals</a></li>
                <li><a href="<%= request.getContextPath() %>/products?tag=deals" class="mobile-menu-link">Sale</a></li>
            </ul>
        </div>

        <!-- Mobile Actions -->
        <div class="mobile-actions">
            <% if (user != null) { %>
                <a href="<%= request.getContextPath() %>/account/profile" class="mobile-action-link">My Account</a>
                <a href="<%= request.getContextPath() %>/orders" class="mobile-action-link">Orders</a>
                <a href="<%= request.getContextPath() %>/wishlist" class="mobile-action-link">Wishlist</a>
                <a href="<%= request.getContextPath() %>/logout" class="mobile-action-link mobile-logout">Sign Out</a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/login" class="mobile-action-link">Sign In</a>
                <a href="<%= request.getContextPath() %>/register" class="mobile-action-link">Create Account</a>
            <% } %>
        </div>
    </div>
</nav>

<!-- MOBILE BOTTOM NAVIGATION -->
<nav class="mobile-bottom-nav-v2" role="navigation" aria-label="Mobile bottom navigation">
    <a href="<%= request.getContextPath() %>/home" class="mobile-bottom-nav-item">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/>
            <polyline points="9 22 9 12 15 12 15 22"/>
        </svg>
        <span>Home</span>
    </a>
    <a href="<%= request.getContextPath() %>/products" class="mobile-bottom-nav-item">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
            <line x1="7" y1="7" x2="7.01" y2="7"/>
        </svg>
        <span>Shop</span>
    </a>
    <a href="<%= request.getContextPath() %>/cart" class="mobile-bottom-nav-item">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 22a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/>
            <path d="M20 22a1 1 0 1 0 0-2 1 1 0 0 0 0 2z"/>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
        </svg>
        <span>Cart</span>
        <span class="mobile-nav-badge" id="mobile-cart-badge-v2"><%= initialCartCount %></span>
    </a>
    <a href="<%= request.getContextPath() %>/wishlist" class="mobile-bottom-nav-item">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/>
        </svg>
        <span>Wishlist</span>
    </a>
    <% if (user != null) { %>
        <a href="<%= request.getContextPath() %>/account/profile" class="mobile-bottom-nav-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
            </svg>
            <span>Account</span>
        </a>
    <% } else { %>
        <a href="<%= request.getContextPath() %>/login" class="mobile-bottom-nav-item">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
            </svg>
            <span>Login</span>
        </a>
    <% } %>
</nav>

<!-- MOBILE NAVIGATION OVERLAY -->
<div class="mobile-nav-overlay-v2" id="mobile-nav-overlay-v2"></div>

<!-- HAMBURGER MENU BUTTON -->
<button class="mobile-hamburger-btn" id="mobile-hamburger-btn" aria-label="Open navigation" aria-expanded="false">
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <line x1="3" y1="6" x2="21" y2="6"/>
        <line x1="3" y1="12" x2="21" y2="12"/>
        <line x1="3" y1="18" x2="21" y2="18"/>
    </svg>
</button>

<!-- Hidden elements for JavaScript -->
<input type="hidden" id="user-logged-in-v2" value="<%= user != null ? "true" : "false" %>">
<input type="hidden" id="context-path" value="<%= request.getContextPath() %>">

<script>
// FashionStore Navigation V2 - Amazon/Flipkart Style
(function() {
    'use strict';

    // Configuration
    const CONFIG = {
        searchDebounceDelay: 300,
        minSearchLength: 2,
        maxSuggestions: 8,
        apiEndpoints: {
            searchSuggestions: '<%= request.getContextPath() %>/api/search/suggestions',
            trendingSearches: '<%= request.getContextPath() %>/api/search/trending',
            recentSearches: '<%= request.getContextPath() %>/api/search/recent',
            autocomplete: '<%= request.getContextPath() %>/api/search/autocomplete',
            recordSearch: '<%= request.getContextPath() %>/api/search/record-search',
            clearHistory: '<%= request.getContextPath() %>/api/search/clear-history',
            validatePincode: '<%= request.getContextPath() %>/api/location/validate-pincode',
            savedLocations: '<%= request.getContextPath() %>/api/location/saved-locations',
            detectLocation: '<%= request.getContextPath() %>/api/location/detect-location'
        }
    };

    // DOM Elements
    let elements = {};

    // Initialize
    function init() {
        cacheElements();
        bindEvents();
        initializeSearch();
        initializeLocation();
        initializeAccount();
        initializeMobileNav();
        initializeStickyHeader();
        loadInitialData();
    }

    // Cache DOM elements
    function cacheElements() {
        elements = {
            // Header
            header: document.querySelector('[data-commerce-header-v2]'),
            
            // Search
            searchInput: document.getElementById('main-search-input'),
            searchForm: document.querySelector('.search-form'),
            searchSuggestions: document.getElementById('search-suggestions'),
            recentSearchesList: document.getElementById('recent-searches-list'),
            trendingSearchesList: document.getElementById('trending-searches-list'),
            productSuggestionsList: document.getElementById('product-suggestions-list'),
            categorySuggestionsList: document.getElementById('category-suggestions-list'),
            brandSuggestionsList: document.getElementById('brand-suggestions-list'),
            clearHistoryBtn: document.getElementById('clear-search-history'),
            
            // Location
            locationTrigger: document.querySelector('.location-trigger'),
            locationDropdown: document.getElementById('location-dropdown'),
            pincodeInput: document.getElementById('pincode-input'),
            validatePincodeBtn: document.getElementById('validate-pincode-btn'),
            detectLocationBtn: document.getElementById('detect-location-btn'),
            savedLocationsList: document.getElementById('saved-locations-list'),
            currentPincode: document.getElementById('current-pincode'),
            mobileCurrentPincode: document.getElementById('mobile-current-pincode'),
            
            // Account
            accountTrigger: document.querySelector('.account-trigger'),
            accountDropdown: document.getElementById('account-dropdown-v2'),
            
            // Mobile
            hamburgerBtn: document.getElementById('mobile-hamburger-btn'),
            mobileNav: document.getElementById('mobile-nav-v2'),
            mobileNavClose: document.getElementById('mobile-nav-close'),
            mobileNavOverlay: document.getElementById('mobile-nav-overlay-v2'),
            
            // Cart badges
            cartBadge: document.getElementById('cart-badge-v2'),
            mobileCartBadge: document.getElementById('mobile-cart-badge-v2'),
            
            // Hidden inputs
            userLoggedIn: document.getElementById('user-logged-in-v2'),
            contextPath: document.getElementById('context-path')
        };
    }

    // Bind events
    function bindEvents() {
        // Search events
        if (elements.searchInput) {
            let searchTimeout;
            elements.searchInput.addEventListener('input', function(e) {
                clearTimeout(searchTimeout);
                searchTimeout = setTimeout(() => handleSearchInput(e.target.value), CONFIG.searchDebounceDelay);
            });
            
            elements.searchInput.addEventListener('focus', () => showSearchSuggestions());
            elements.searchInput.addEventListener('blur', () => hideSearchSuggestionsDelayed());
            
            elements.searchInput.addEventListener('keydown', function(e) {
                handleSearchKeydown(e);
            });
        }

        // Location events
        if (elements.locationTrigger) {
            elements.locationTrigger.addEventListener('click', toggleLocationDropdown);
        }
        
        if (elements.validatePincodeBtn) {
            elements.validatePincodeBtn.addEventListener('click', validatePincode);
        }
        
        if (elements.detectLocationBtn) {
            elements.detectLocationBtn.addEventListener('click', detectUserLocation);
        }
        
        if (elements.pincodeInput) {
            elements.pincodeInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    validatePincode();
                }
            });
        }

        // Account events
        if (elements.accountTrigger) {
            elements.accountTrigger.addEventListener('click', toggleAccountDropdown);
        }

        // Mobile navigation events
        if (elements.hamburgerBtn) {
            elements.hamburgerBtn.addEventListener('click', openMobileNav);
        }
        
        if (elements.mobileNavClose) {
            elements.mobileNavClose.addEventListener('click', closeMobileNav);
        }
        
        if (elements.mobileNavOverlay) {
            elements.mobileNavOverlay.addEventListener('click', closeMobileNav);
        }

        // Close dropdowns on outside click
        document.addEventListener('click', handleOutsideClick);

        // Scroll events for sticky header
        window.addEventListener('scroll', handleScroll);
    }

    // Search functionality
    function initializeSearch() {
        if (!elements.searchInput) return;
        
        // Load trending searches on focus
        elements.searchInput.addEventListener('focus', loadTrendingSearches);
    }

    function handleSearchInput(query) {
        if (query.length < CONFIG.minSearchLength) {
            showTrendingSearches();
            return;
        }
        
        loadSearchSuggestions(query);
    }

    function loadSearchSuggestions(query) {
        const url = `${CONFIG.apiEndpoints.autocomplete}?q=${encodeURIComponent(query)}&limit=${CONFIG.maxSuggestions}`;
        
        fetch(url, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                displaySearchSuggestions(data.suggestions, query);
            }
        })
        .catch(error => {
            console.error('Error loading search suggestions:', error);
        });
    }

    function displaySearchSuggestions(suggestions, query) {
        if (!elements.searchSuggestions) return;
        
        // Clear existing suggestions
        clearSuggestionLists();
        
        // Display different types of suggestions
        if (suggestions.recent && suggestions.recent.length > 0) {
            displayRecentSearches(suggestions.recent);
        }
        
        if (suggestions.products && suggestions.products.length > 0) {
            displayProductSuggestions(suggestions.products);
        }
        
        if (suggestions.categories && suggestions.categories.length > 0) {
            displayCategorySuggestions(suggestions.categories);
        }
        
        if (suggestions.brands && suggestions.brands.length > 0) {
            displayBrandSuggestions(suggestions.brands);
        }
        
        if (suggestions.trending && suggestions.trending.length > 0) {
            displayTrendingSearchesList(suggestions.trending);
        }
        
        showSearchSuggestions();
    }

    function displayRecentSearches(recent) {
        const section = document.getElementById('recent-searches-section');
        const list = elements.recentSearchesList;
        
        if (!section || !list) return;
        
        list.innerHTML = '';
        recent.forEach(search => {
            const item = createSuggestionItem(search.query, 'recent', search.searched_at);
            list.appendChild(item);
        });
        
        section.style.display = 'block';
        if (elements.clearHistoryBtn) {
            elements.clearHistoryBtn.style.display = 'block';
        }
    }

    function displayTrendingSearchesList(trending) {
        const section = document.getElementById('trending-searches-section');
        const list = elements.trendingSearchesList;
        
        if (!section || !list) return;
        
        list.innerHTML = '';
        trending.forEach(search => {
            const item = createSuggestionItem(search.query, 'trending', null, search.popularity);
            list.appendChild(item);
        });
        
        section.style.display = 'block';
    }

    function displayProductSuggestions(products) {
        const section = document.getElementById('product-suggestions-section');
        const list = elements.productSuggestionsList;
        
        if (!section || !list) return;
        
        list.innerHTML = '';
        products.forEach(product => {
            const item = createSuggestionItem(product, 'product');
            list.appendChild(item);
        });
        
        section.style.display = 'block';
    }

    function displayCategorySuggestions(categories) {
        const section = document.getElementById('category-suggestions-section');
        const list = elements.categorySuggestionsList;
        
        if (!section || !list) return;
        
        list.innerHTML = '';
        categories.forEach(category => {
            const item = createSuggestionItem(category, 'category');
            list.appendChild(item);
        });
        
        section.style.display = 'block';
    }

    function displayBrandSuggestions(brands) {
        const section = document.getElementById('brand-suggestions-section');
        const list = elements.brandSuggestionsList;
        
        if (!section || !list) return;
        
        list.innerHTML = '';
        brands.forEach(brand => {
            const item = createSuggestionItem(brand, 'brand');
            list.appendChild(item);
        });
        
        section.style.display = 'block';
    }

    function createSuggestionItem(text, type, metadata, popularity) {
        const item = document.createElement('div');
        item.className = 'suggestion-item';
        item.setAttribute('data-type', type);
        item.setAttribute('data-query', text);
        
        let icon = '';
        let label = text;
        
        switch (type) {
            case 'recent':
                icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>';
                break;
            case 'trending':
                icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>';
                label += ` (${popularity} searches)`;
                break;
            case 'product':
                icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>';
                break;
            case 'category':
                icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>';
                break;
            case 'brand':
                icon = '<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/></svg>';
                break;
        }
        
        item.innerHTML = `
            <div class="suggestion-icon">${icon}</div>
            <div class="suggestion-content">
                <div class="suggestion-text">${label}</div>
                ${metadata ? `<div class="suggestion-meta">${formatMetadata(metadata, type)}</div>` : ''}
            </div>
        `;
        
        item.addEventListener('click', () => selectSuggestion(text, type));
        
        return item;
    }

    function formatMetadata(metadata, type) {
        switch (type) {
            case 'recent':
                return `Searched ${formatRelativeTime(metadata)}`;
            default:
                return '';
        }
    }

    function formatRelativeTime(dateString) {
        const date = new Date(dateString);
        const now = new Date();
        const diffMs = now - date;
        const diffMins = Math.floor(diffMs / 60000);
        const diffHours = Math.floor(diffMs / 3600000);
        const diffDays = Math.floor(diffMs / 86400000);
        
        if (diffMins < 60) {
            return diffMins + ' minutes ago';
        } else if (diffHours < 24) {
            return diffHours + ' hours ago';
        } else {
            return diffDays + ' days ago';
        }
    }

    function selectSuggestion(query, type) {
        elements.searchInput.value = query;
        hideSearchSuggestions();
        
        // Record search if user is logged in
        if (elements.userLoggedIn.value === 'true') {
            recordSearch(query);
        }
        
        // Submit search
        elements.searchForm.submit();
    }

    function recordSearch(query) {
        const url = CONFIG.apiEndpoints.recordSearch;
        const formData = new URLSearchParams({
            q: query,
            category: elements.searchForm.category.value || ''
        });
        
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': typeof csrfToken !== 'undefined' ? csrfToken : ''
            },
            body: formData
        }).catch(error => {
            console.error('Error recording search:', error);
        });
    }

    function loadTrendingSearches() {
        const url = `${CONFIG.apiEndpoints.trendingSearches}?limit=5`;
        
        fetch(url, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.trending.length > 0) {
                displayTrendingSearchesList(data.trending);
            }
        })
        .catch(error => {
            console.error('Error loading trending searches:', error);
        });
    }

    function showTrendingSearches() {
        clearSuggestionLists();
        loadTrendingSearches();
        showSearchSuggestions();
    }

    function clearSuggestionLists() {
        const lists = [
            elements.recentSearchesList,
            elements.trendingSearchesList,
            elements.productSuggestionsList,
            elements.categorySuggestionsList,
            elements.brandSuggestionsList
        ];
        
        lists.forEach(list => {
            if (list) list.innerHTML = '';
        });
        
        const sections = [
            document.getElementById('recent-searches-section'),
            document.getElementById('trending-searches-section'),
            document.getElementById('product-suggestions-section'),
            document.getElementById('category-suggestions-section'),
            document.getElementById('brand-suggestions-section')
        ];
        
        sections.forEach(section => {
            if (section) section.style.display = 'none';
        });
        
        if (elements.clearHistoryBtn) {
            elements.clearHistoryBtn.style.display = 'none';
        }
    }

    function showSearchSuggestions() {
        if (elements.searchSuggestions) {
            elements.searchSuggestions.classList.add('active');
        }
    }

    function hideSearchSuggestions() {
        if (elements.searchSuggestions) {
            elements.searchSuggestions.classList.remove('active');
        }
    }

    function hideSearchSuggestionsDelayed() {
        setTimeout(hideSearchSuggestions, 150);
    }

    function handleSearchKeydown(e) {
        if (e.key === 'ArrowDown' || e.key === 'ArrowUp') {
            e.preventDefault();
            navigateSuggestions(e.key === 'ArrowDown' ? 1 : -1);
        } else if (e.key === 'Escape') {
            hideSearchSuggestions();
            elements.searchInput.blur();
        } else if (e.key === 'Enter') {
            if (elements.searchSuggestions.classList.contains('active')) {
                e.preventDefault();
                const activeItem = elements.searchSuggestions.querySelector('.suggestion-item.active');
                if (activeItem) {
                    const query = activeItem.getAttribute('data-query');
                    const type = activeItem.getAttribute('data-type');
                    selectSuggestion(query, type);
                }
            }
        }
    }

    function navigateSuggestions(direction) {
        const items = elements.searchSuggestions.querySelectorAll('.suggestion-item');
        if (items.length === 0) return;
        
        const activeIndex = Array.from(items).findIndex(item => item.classList.contains('active'));
        let newIndex = activeIndex + direction;
        
        if (newIndex < 0) newIndex = items.length - 1;
        if (newIndex >= items.length) newIndex = 0;
        
        items.forEach(item => item.classList.remove('active'));
        items[newIndex].classList.add('active');
        
        // Update input value
        const query = items[newIndex].getAttribute('data-query');
        elements.searchInput.value = query;
    }

    // Location functionality
    function initializeLocation() {
        // Load saved locations when location dropdown is opened
        if (elements.locationTrigger) {
            elements.locationTrigger.addEventListener('click', loadSavedLocations);
        }
        
        // Load current location from localStorage or detect
        loadCurrentLocation();
    }

    function toggleLocationDropdown(e) {
        e.stopPropagation();
        const isOpen = elements.locationDropdown.classList.toggle('active');
        elements.locationTrigger.setAttribute('aria-expanded', isOpen);
        
        if (isOpen) {
            loadSavedLocations();
        }
    }

    function loadSavedLocations() {
        if (elements.userLoggedIn.value !== 'true') return;
        
        const url = CONFIG.apiEndpoints.savedLocations;
        
        fetch(url, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                displaySavedLocations(data.locations);
            }
        })
        .catch(error => {
            console.error('Error loading saved locations:', error);
        });
    }

    function displaySavedLocations(locations) {
        if (!elements.savedLocationsList) return;
        
        elements.savedLocationsList.innerHTML = '';
        
        if (locations.length === 0) {
            elements.savedLocationsList.innerHTML = '<div class="no-saved-locations">No saved locations</div>';
            return;
        }
        
        locations.forEach(location => {
            const item = document.createElement('div');
            item.className = 'saved-location-item';
            item.setAttribute('data-pincode', location.pincode);
            
            item.innerHTML = `
                <div class="location-info">
                    <div class="location-main">${location.city}, ${location.state} ${location.pincode}</div>
                    ${location.area ? `<div class="location-area">${location.area}</div>` : ''}
                    <div class="location-estimate">${location.estimatedDeliveryDays} days • ${location.deliveryTimeSlot}</div>
                </div>
                ${location.isDefault ? '<span class="location-default">Default</span>' : ''}
            `;
            
            item.addEventListener('click', () => selectLocation(location));
            
            elements.savedLocationsList.appendChild(item);
        });
    }

    function selectLocation(location) {
        updateCurrentLocation(location);
        hideLocationDropdown();
    }

    function validatePincode() {
        const pincode = elements.pincodeInput.value.trim();
        if (!pincode) return;
        
        const url = `${CONFIG.apiEndpoints.validatePincode}?pincode=${pincode}`;
        
        fetch(url, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.location) {
                selectLocation(data.location);
            } else {
                showLocationError('Pincode not serviceable');
            }
        })
        .catch(error => {
            console.error('Error validating pincode:', error);
            showLocationError('Error validating pincode');
        });
    }

    function detectUserLocation() {
        if (!navigator.geolocation) {
            showLocationError('Geolocation not supported');
            return;
        }
        
        elements.detectLocationBtn.disabled = true;
        elements.detectLocationBtn.textContent = 'Detecting...';
        
        navigator.geolocation.getCurrentPosition(
            position => {
                // In a real implementation, you would send coordinates to a geocoding service
                // For now, just detect by IP
                detectLocationByIP();
            },
            error => {
                console.error('Geolocation error:', error);
                detectLocationByIP();
            }
        );
    }

    function detectLocationByIP() {
        const url = CONFIG.apiEndpoints.detectLocation;
        
        fetch(url, {
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.location) {
                selectLocation(data.location);
            } else {
                showLocationError('Could not detect location');
            }
        })
        .catch(error => {
            console.error('Error detecting location:', error);
            showLocationError('Error detecting location');
        })
        .finally(() => {
            elements.detectLocationBtn.disabled = false;
            elements.detectLocationBtn.innerHTML = `
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <circle cx="12" cy="12" r="10"/>
                    <polyline points="12,6 12,12 16,14"/>
                </svg>
                Detect my location
            `;
        });
    }

    function updateCurrentLocation(location) {
        const pincode = location.pincode;
        const locationText = `${location.city} ${pincode}`;
        
        if (elements.currentPincode) {
            elements.currentPincode.textContent = pincode;
        }
        
        if (elements.mobileCurrentPincode) {
            elements.mobileCurrentPincode.textContent = pincode;
        }
        
        // Update location text
        const locationTextElement = elements.locationTrigger.querySelector('.location-text');
        if (locationTextElement) {
            locationTextElement.innerHTML = `
                <span class="location-label">Deliver to</span>
                <span class="location-pincode">${locationText}</span>
            `;
        }
        
        // Save to localStorage
        localStorage.setItem('selectedLocation', JSON.stringify(location));
    }

    function loadCurrentLocation() {
        const savedLocation = localStorage.getItem('selectedLocation');
        if (savedLocation) {
            try {
                const location = JSON.parse(savedLocation);
                updateCurrentLocation(location);
            } catch (error) {
                console.error('Error parsing saved location:', error);
            }
        }
    }

    function hideLocationDropdown() {
        if (elements.locationDropdown) {
            elements.locationDropdown.classList.remove('active');
            elements.locationTrigger.setAttribute('aria-expanded', 'false');
        }
    }

    function showLocationError(message) {
        // You could implement a toast notification here
        console.error('Location error:', message);
    }

    // Account functionality
    function initializeAccount() {
        // Account dropdown is handled by toggleAccountDropdown
    }

    function toggleAccountDropdown(e) {
        e.stopPropagation();
        const isOpen = elements.accountDropdown.classList.toggle('active');
        elements.accountTrigger.setAttribute('aria-expanded', isOpen);
    }

    // Mobile navigation
    function initializeMobileNav() {
        // Mobile navigation is handled by openMobileNav/closeMobileNav
    }

    function openMobileNav() {
        if (elements.mobileNav) {
            elements.mobileNav.classList.add('active');
            elements.mobileNavOverlay.classList.add('active');
            elements.hamburgerBtn.setAttribute('aria-expanded', 'true');
            document.body.style.overflow = 'hidden';
        }
    }

    function closeMobileNav() {
        if (elements.mobileNav) {
            elements.mobileNav.classList.remove('active');
            elements.mobileNavOverlay.classList.remove('active');
            elements.hamburgerBtn.setAttribute('aria-expanded', 'false');
            document.body.style.overflow = '';
        }
    }

    // Sticky header
    function initializeStickyHeader() {
        if (!elements.header) return;
        
        let lastScrollY = window.scrollY;
        let ticking = false;
        
        function updateHeader() {
            const scrollY = window.scrollY;
            
            if (scrollY > 100) {
                elements.header.classList.add('scrolled');
                
                // Hide/show based on scroll direction
                if (scrollY > lastScrollY && scrollY > 200) {
                    elements.header.classList.add('hidden');
                } else {
                    elements.header.classList.remove('hidden');
                }
            } else {
                elements.header.classList.remove('scrolled', 'hidden');
            }
            
            lastScrollY = scrollY;
            ticking = false;
        }
        
        function requestTick() {
            if (!ticking) {
                requestAnimationFrame(updateHeader);
                ticking = true;
            }
        }
        
        handleScroll = requestTick;
    }

    function handleScroll() {
        // This will be replaced by requestTick in initializeStickyHeader
    }

    // Outside click handler
    function handleOutsideClick(e) {
        // Close dropdowns if clicking outside
        if (!e.target.closest('.header-account') && elements.accountDropdown) {
            elements.accountDropdown.classList.remove('active');
            elements.accountTrigger.setAttribute('aria-expanded', 'false');
        }
        
        if (!e.target.closest('.delivery-location-selector') && elements.locationDropdown) {
            hideLocationDropdown();
        }
        
        if (!e.target.closest('.header-search') && elements.searchSuggestions) {
            hideSearchSuggestions();
        }
    }

    // Load initial data
    function loadInitialData() {
        // Update cart count for logged-in users
        if (elements.userLoggedIn.value === 'true') {
            updateCartCount();
        }
    }

    function updateCartCount() {
        const url = `${elements.contextPath.value}/cart`;
        const formData = new URLSearchParams({ action: 'get' });
        
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-Token': typeof csrfToken !== 'undefined' ? csrfToken : ''
            },
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.cartCount !== undefined) {
                updateCartBadges(data.cartCount);
            }
        })
        .catch(error => {
            console.error('Error updating cart count:', error);
        });
    }

    function updateCartBadges(count) {
        if (elements.cartBadge) {
            const oldCount = parseInt(elements.cartBadge.textContent);
            elements.cartBadge.textContent = count;
            
            if (oldCount !== count) {
                elements.cartBadge.classList.remove('animate');
                void elements.cartBadge.offsetWidth; // Trigger reflow
                elements.cartBadge.classList.add('animate');
            }
        }
        
        if (elements.mobileCartBadge) {
            elements.mobileCartBadge.textContent = count;
        }
    }

    // Clear search history
    if (elements.clearHistoryBtn) {
        elements.clearHistoryBtn.addEventListener('click', function() {
            const url = CONFIG.apiEndpoints.clearHistory;
            
            fetch(url, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'X-CSRF-Token': typeof csrfToken !== 'undefined' ? csrfToken : ''
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    clearSuggestionLists();
                    loadTrendingSearches();
                }
            })
            .catch(error => {
                console.error('Error clearing search history:', error);
            });
        });
    }

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
</script>
