<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.fashionstore.model.Product" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Product Management");
    request.setAttribute("_pageCSS", "admin");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>
<body class="admin-dashboard">

<div class="admin-layout">
    <button type="button" class="admin-menu-toggle" data-admin-menu-open aria-label="Open admin menu">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
    </button>
    <div class="admin-sidebar-backdrop" data-admin-menu-close tabindex="-1" aria-hidden="true"></div>
    <!-- SIDEBAR -->
    <aside class="admin-sidebar">
        <div class="sidebar-brand">FashionStore Admin</div>
        <nav class="sidebar-nav">
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="7" height="7"/>
                    <rect x="14" y="3" width="7" height="7"/>
                    <rect x="14" y="14" width="7" height="7"/>
                    <rect x="3" y="14" width="7" height="7"/>
                </svg>
                Dashboard
            </a>
            <a href="<%= request.getContextPath() %>/admin/products" class="sidebar-link active">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
                    <line x1="7" y1="7" x2="7.01" y2="7"/>
                </svg>
                Products
            </a>
            <a href="<%= request.getContextPath() %>/admin/orders" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
                    <line x1="3" y1="6" x2="21" y2="6"/>
                    <path d="M16 10a4 4 0 0 1-8 0"/>
                </svg>
                Orders
            </a>
            <a href="<%= request.getContextPath() %>/admin/users" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                Users
            </a>
            <a href="<%= request.getContextPath() %>/products" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
                </svg>
                Back to Store
            </a>
        </nav>
    </aside>

    <!-- MAIN CONTENT -->
    <main class="admin-content">
        <div class="admin-header">
            <h1 class="admin-title">Product Management</h1>
            <a href="<%= request.getContextPath() %>/admin/products?action=add" class="add-btn">+ Add New Product</a>
        </div>

        <!-- PRODUCT SEARCH -->
        <div class="glass-card admin-toolbar-card">
            <div class="admin-filter-row">
                <div class="admin-filter-field admin-filter-field--grow">
                    <label class="admin-filter-label" for="searchProducts">Search Products</label>
                    <input type="text" id="searchProducts" class="admin-filter-input" placeholder="Search by name, brand...">
                </div>
                <div class="admin-filter-field">
                    <label class="admin-filter-label" for="stockFilter">Stock Status</label>
                    <select id="stockFilter" class="admin-filter-select">
                        <option value="">All Products</option>
                        <option value="instock">In Stock</option>
                        <option value="lowstock">Low Stock</option>
                        <option value="outofstock">Out of Stock</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- UI FEEDBACK -->
        <% String message = (String) session.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-success admin-alert-spacing"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(message) %></div>
            <% session.removeAttribute("message"); %>
        <% } %>

        <% String error = (String) session.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger admin-alert-spacing"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(error) %></div>
            <% session.removeAttribute("error"); %>
        <% } %>

        <!-- PRODUCTS TABLE -->
        <div class="admin-table-container glass-card">
            <table class="admin-table" role="table" aria-label="Products table">
                <thead>
                    <tr>
                        <th scope="col">Image</th>
                        <th scope="col">Name</th>
                        <th scope="col">Price</th>
                        <th scope="col">Sizes &amp; Stock</th>
                        <th scope="col">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        List<Product> products = (List<Product>) request.getAttribute("products");
                        if (products != null) {
                            for (Product p : products) {
                    %>
                    <tr data-product-name="<%= p.getProductName() != null ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName().toLowerCase()) : "" %>" data-brand="<%= (p.getBrand() != null ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getBrand().toLowerCase()) : "") %>" data-stock="<%= getStockStatus(p) %>">
                        <td>
                            <div class="product-image-wrapper">
                                <img src="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getImageUrl() != null ? p.getImageUrl() : "") %>" class="prod-img" alt="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName() != null ? p.getProductName() : "Product") %>" onclick="showImagePreview('<%= org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(p.getImageUrl() != null ? p.getImageUrl() : "") %>', '<%= org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(p.getProductName() != null ? p.getProductName() : "Product") %>')" onerror="this.src='<%= request.getContextPath() %>/assets/images/placeholder-product.jpg'; this.onerror=null;">
                            </div>
                        </td>
                        <td>
                            <div class="admin-table-cell-title"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName() != null ? p.getProductName() : "Unnamed Product") %></div>
                            <div class="admin-table-cell-sub"><%= p.getBrand() != null ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getBrand()) : "" %></div>
                        </td>
                        <td>₹<%= String.format("%.2f", p.getPrice()) %></td>
                        <td>
                            <%
                                if (p.getSizes() != null && !p.getSizes().isEmpty()) {
                                    for (com.fashionstore.model.ProductSize s : p.getSizes()) {
                            %>
                                <span class="size-badge <%= s.getStockQuantity() < 10 ? "low-stock" : "" %>"><%= s.getSizeLabel() %>: <%= s.getStockQuantity() %></span>
                            <%
                                    }
                                } else {
                            %>
                                <span class="no-sizes">No sizes set</span>
                            <% } %>
                        </td>
                        <td class="action-btns">
                            <a href="<%= request.getContextPath() %>/admin/products?action=edit&id=<%= p.getProductId() %>" class="edit-link">Edit</a>
                            <a href="<%= request.getContextPath() %>/admin/products?action=delete&id=<%= p.getProductId() %>" class="delete-link" onclick="return confirm('Delete this product?')">Delete</a>
                        </td>
                    </tr>
                    <%
                            }
                        }
                    %>
                </tbody>
            </table>
        </div>
    </main>
</div>

<!-- IMAGE PREVIEW MODAL -->
<div id="imageModal" class="admin-modal" role="dialog" aria-modal="true" aria-labelledby="modalImageName">
    <div class="admin-modal__dialog">
        <button type="button" class="admin-modal__close" onclick="closeImagePreview()" aria-label="Close preview">&times;</button>
        <img id="modalImage" class="admin-modal__img" src="" alt="Product preview">
        <p id="modalImageName" class="admin-modal__caption"></p>
    </div>
</div>

<script>
// Product search and filter functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('searchProducts');
    const stockFilter = document.getElementById('stockFilter');
    
    if (searchInput && stockFilter) {
        function filterProducts() {
            const searchTerm = searchInput.value.toLowerCase();
            const stockValue = stockFilter.value;
            
            document.querySelectorAll('tbody tr').forEach(row => {
                const productName = row.getAttribute('data-product-name') || '';
                const brand = row.getAttribute('data-brand') || '';
                const stockStatus = row.getAttribute('data-stock') || '';
                
                const matchesSearch = productName.includes(searchTerm) || brand.includes(searchTerm);
                const matchesStock = stockValue === '' || stockStatus === stockValue;
                
                row.style.display = matchesSearch && matchesStock ? '' : 'none';
            });
        }
        
        searchInput.addEventListener('input', filterProducts);
        stockFilter.addEventListener('change', filterProducts);
    }
});

// Image preview functionality
function showImagePreview(imageUrl, productName) {
    const modal = document.getElementById('imageModal');
    const modalImage = document.getElementById('modalImage');
    const modalImageName = document.getElementById('modalImageName');
    
    modalImage.src = imageUrl;
    modalImageName.textContent = productName;
    modal.classList.add('is-open');
}

function closeImagePreview() {
    document.getElementById('imageModal').classList.remove('is-open');
}

window.addEventListener('click', function(event) {
    const modal = document.getElementById('imageModal');
    if (modal && event.target === modal) {
        closeImagePreview();
    }
});

<%!
    private String getStockStatus(Product p) {
        if (p.getSizes() == null || p.getSizes().isEmpty()) {
            return "outofstock";
        }
        boolean hasStock = false;
        boolean lowStock = false;
        for (com.fashionstore.model.ProductSize s : p.getSizes()) {
            if (s.getStockQuantity() > 0) {
                hasStock = true;
                if (s.getStockQuantity() < 10) {
                    lowStock = true;
                }
            }
        }
        if (!hasStock) return "outofstock";
        if (lowStock) return "lowstock";
        return "instock";
    }
%>
</script>

</body>
</html>
