<%@ page contentType="text/html;charset=UTF-8" %>
<%
    Object customerAuthObj = session.getAttribute("customerAuth");
    com.fashionstore.model.User user = (customerAuthObj instanceof com.fashionstore.model.User) ? (com.fashionstore.model.User) customerAuthObj : null;
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

<header class="navbar">
    <div class="navbar-container">
        <div class="logo">
            <a href="<%= request.getContextPath() %>/home" class="fs-storefront-nav__brand" aria-label="FashionStore home">
                <span class="fs-storefront-nav__logo">FashionStore</span>
            </a>
        </div>

        <nav class="nav-links" aria-label="Primary categories">
            <a href="<%= request.getContextPath() %>/products?category=women" class="fs-storefront-nav__link">Women</a>
            <a href="<%= request.getContextPath() %>/products?category=men" class="fs-storefront-nav__link">Men</a>
            <a href="<%= request.getContextPath() %>/products?category=footwear" class="fs-storefront-nav__link">Footwear</a>
            <a href="<%= request.getContextPath() %>/products?category=accessories" class="fs-storefront-nav__link">Accessories</a>
        </nav>

        <div class="nav-actions">
            <form class="fs-storefront-nav__search" action="<%= request.getContextPath() %>/products" method="get" role="search">
                <input type="text" name="search" placeholder="Search collection..." autocomplete="off">
                <button type="submit" aria-label="Search">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round"><circle cx="11" cy="11" r="7"></circle><line x1="20" y1="20" x2="16.2" y2="16.2"></line></svg>
                </button>
            </form>

            <nav class="fs-storefront-nav__tools">
                <button id="dark-mode-toggle" class="fs-storefront-nav__tool" aria-label="Toggle theme">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line><line x1="1" y1="12" x2="3" y2="12"></line><line x1="21" y1="12" x2="23" y2="12"></line><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line></svg>
                </button>
                
                <a href="<%= request.getContextPath() %>/wishlist" class="fs-storefront-nav__tool" aria-label="Wishlist">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20.3 5.7a5.1 5.1 0 0 0-7.2 0L12 6.8l-1.1-1.1a5.1 5.1 0 0 0-7.2 7.2L12 21l8.3-8.1a5.1 5.1 0 0 0 0-7.2z"></path></svg>
                </a>

                <button type="button" class="fs-storefront-nav__tool" onclick="toggleMiniCart(event)" aria-label="Shopping Cart">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                    <span class="fs-storefront-nav__badge" id="nav-cart-badge"><%= initialCartCount %></span>
                </button>

                <details class="fs-storefront-nav__account">
                    <summary class="fs-storefront-nav__tool">
                        <span class="fs-storefront-nav__avatar"><%= userInitials %></span>
                    </summary>
                    <div class="fs-storefront-nav__dropdown surface-card">
                        <% if (user != null) { %>
                            <div class="fs-storefront-nav__account-header">
                                <span class="fs-storefront-nav__avatar-large"><%= userInitials %></span>
                                <div class="stack-sm">
                                    <span class="fs-storefront-nav__account-name"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getFullName()) %></span>
                                    <span class="fs-storefront-nav__account-email"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getEmail()) %></span>
                                </div>
                            </div>
                            <div class="u-border-top"></div>
                            <a href="<%= request.getContextPath() %>/account/profile" class="fs-storefront-nav__dropdown-link">My Profile</a>
                            <a href="<%= request.getContextPath() %>/orders" class="fs-storefront-nav__dropdown-link">Orders</a>
                            <a href="<%= request.getContextPath() %>/wishlist" class="fs-storefront-nav__dropdown-link">Wishlist</a>
                            <div class="u-border-top"></div>
                            <a href="<%= request.getContextPath() %>/logout" class="fs-storefront-nav__dropdown-link fs-storefront-nav__dropdown-link--danger">Logout</a>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/login" class="fs-storefront-nav__dropdown-link">Sign in</a>
                            <a href="<%= request.getContextPath() %>/register" class="fs-storefront-nav__dropdown-link">Create account</a>
                            <a href="<%= request.getContextPath() %>/orders" class="fs-storefront-nav__dropdown-link">Track orders</a>
                        <% } %>
                    </div>
                </details>
            </nav>
        </div>
    </div>
</header>

<div class="fs-storefront-nav__overlay" id="mobile-nav-overlay"></div>

<input type="hidden" id="user-logged-in" value="<%= user != null ? "true" : "false" %>">
<div id="toast-container" class="toast-container"></div>

<%-- Mobile navigation - will be conditionally rendered by JavaScript --%>
<%-- Do NOT render in JSP - use JavaScript conditional rendering to prevent hidden-but-mounted components --%>

<div id="mini-cart-overlay" class="mini-cart-overlay" onclick="toggleMiniCart(event)"></div>
<div id="mini-cart-drawer" class="mini-cart-drawer">
    <div class="mini-cart-header">
        <h3>Your Cart</h3>
        <button class="close-cart-btn" onclick="toggleMiniCart(event)" aria-label="Close cart">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
    </div>
    <div class="mini-cart-items" id="mini-cart-items"></div>
    <div class="mini-cart-footer">
        <div class="mini-cart-subtotal">
            <span class="mini-cart-subtotal-label">Subtotal</span>
            <span class="mini-cart-subtotal-value" id="mini-cart-total-price">₹0.00</span>
        </div>
        <div class="mini-cart-actions">
            <a href="<%= request.getContextPath() %>/checkout" class="fs-btn fs-btn--primary">
                Checkout
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <line x1="5" y1="12" x2="19" y2="12"></line>
                    <polyline points="12 5 19 12 12 19"></polyline>
                </svg>
            </a>
            <a href="<%= request.getContextPath() %>/cart" class="fs-btn fs-btn--outline">View Cart</a>
        </div>
    </div>
</div>
