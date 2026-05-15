<%@ page contentType="text/html;charset=UTF-8" %>

<footer class="footer">
  <div class="footer-container">
    
    <div class="footer-brand">
      <h2>FashionStore</h2>
      <p>Modern fashion marketplace with curated styles.</p>
    </div>

    <div class="footer-links">
      <div>
        <h4>Shop</h4>
        <a href="<%= request.getContextPath() %>/products">All Products</a>
        <a href="<%= request.getContextPath() %>/products?category=men">Men</a>
        <a href="<%= request.getContextPath() %>/products?category=women">Women</a>
      </div>

      <div>
        <h4>Account</h4>
        <a href="<%= request.getContextPath() %>/cart">Cart</a>
        <a href="<%= request.getContextPath() %>/orders">Orders</a>
      </div>

      <div>
        <h4>Legal</h4>
        <a href="#">Privacy Policy</a>
        <a href="#">Terms</a>
      </div>
    </div>
  </div>

  <div class="footer-bottom">
    &copy; 2026 FashionStore. All rights reserved.
  </div>
</footer>
