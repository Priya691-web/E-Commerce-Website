<%@ page contentType="text/html;charset=UTF-8" %>

<!-- MINI CART OVERLAY AND DRAWER -->
<div id="mini-cart-overlay" class="mini-cart-overlay"></div>
<div id="mini-cart-drawer" class="mini-cart-drawer">
    <div class="mini-cart-header">
        <h2>Your Cart</h2>
        <button class="mini-cart-close" id="mini-cart-close" aria-label="Close cart">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
    </div>
    
    <div id="mini-cart-empty" class="mini-cart-empty mini-cart-empty--hidden">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <circle cx="9" cy="21" r="1"></circle>
            <circle cx="20" cy="21" r="1"></circle>
            <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
        </svg>
        <p>Your cart is empty</p>
        <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--primary">Continue Shopping</a>
    </div>
    
    <div id="mini-cart-content" class="mini-cart-content">
        <div id="mini-cart-items" class="mini-cart-items">
            <!-- Cart items will be dynamically loaded here -->
        </div>
        
        <div class="mini-cart-footer">
            <div class="mini-cart-total">
                <span>Subtotal</span>
                <span id="mini-cart-total">₹0.00</span>
            </div>
            <a href="<%= request.getContextPath() %>/cart" class="fs-btn fs-btn--primary fs-btn--full-width">View Cart</a>
        </div>
    </div>
</div>

<footer class="footer">
  <div class="footer-container">
    <div class="footer-grid">
        <div class="footer-brand">
          <h2>FashionStore</h2>
          <p>A luxury marketplace for curated, premium fashion. Discover the latest editorial collections and elevate your wardrobe.</p>
          <div class="footer-socials">
            <a href="#" aria-label="Instagram"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="2" width="20" height="20" rx="5" ry="5"></rect><path d="M16 11.37A4 4 0 1 1 12.63 8 4 4 0 0 1 16 11.37z"></path><line x1="17.5" y1="6.5" x2="17.51" y2="6.5"></line></svg></a>
            <a href="#" aria-label="Twitter"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M23 3a10.9 10.9 0 0 1-3.14 1.53 4.48 4.48 0 0 0-7.86 3v1A10.66 10.66 0 0 1 3 4s-4 9 5 13a11.64 11.64 0 0 1-7 2c9 5 20 0 20-11.5a4.5 4.5 0 0 0-.08-.83A7.72 7.72 0 0 0 23 3z"></path></svg></a>
            <a href="#" aria-label="Facebook"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z"></path></svg></a>
          </div>
        </div>

        <div class="footer-links">
          <h4>Shop Links</h4>
          <a href="<%= request.getContextPath() %>/products">All Collections</a>
          <a href="<%= request.getContextPath() %>/products?category=women">Womenswear</a>
          <a href="<%= request.getContextPath() %>/products?category=men">Menswear</a>
          <a href="<%= request.getContextPath() %>/products?category=accessories">Accessories</a>
        </div>

        <div class="footer-support">
          <h4>Support</h4>
          <a href="<%= request.getContextPath() %>/login">Sign In / Account</a>
        </div>
    </div>
  </div>


  <div class="footer-bottom">
    <div class="footer-container footer-bottom-inner">
      <div class="footer-copyright">
        &copy; 2026 FashionStore. All rights reserved.
      </div>
    </div>
  </div>
</footer>
