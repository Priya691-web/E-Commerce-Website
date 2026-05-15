<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.Category" %>
<%@ page import="java.net.URLEncoder" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Catalog");
    request.setAttribute("_pageCSS", "products");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
<script src="<%= request.getContextPath() %>/assets/js/modules/filter-sidebar.js"></script>
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<main class="fs-catalog-page" id="catalog-main">

<%
    List<Product> products = new ArrayList<>();
    Object obj = request.getAttribute("products");
    if (obj != null && obj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Product> temp = (List<Product>) obj;
        products = temp != null ? temp : new ArrayList<>();
    }

    String searchVal = (String) request.getAttribute("search");
    searchVal = searchVal != null ? searchVal.trim() : "";

    String minPriceVal = (String) request.getAttribute("minPrice");
    minPriceVal = minPriceVal != null ? minPriceVal.trim() : "";

    String maxPriceVal = (String) request.getAttribute("maxPrice");
    maxPriceVal = maxPriceVal != null ? maxPriceVal.trim() : "";

    String sortByVal = (String) request.getAttribute("sortBy");
    sortByVal = sortByVal != null ? sortByVal.trim() : "";

    String brandVal = (String) request.getAttribute("brand");
    brandVal = brandVal != null ? brandVal.trim() : "";

    Integer currentCategoryId = (Integer) request.getAttribute("categoryId");
    String currentCategorySlug = (String) request.getAttribute("categorySlug");
    currentCategorySlug = currentCategorySlug != null ? currentCategorySlug.trim() : "";
    String currentTag = (String) request.getAttribute("tag");
    currentTag = currentTag != null ? currentTag.trim() : "";

    List<String> allSizes = Arrays.asList("S", "M", "L", "XL", "7", "8", "9", "10", "OS");
    List<String> selectedSizes = new ArrayList<>();
    Object selectedSizesObj = request.getAttribute("selectedSizes");
    if (selectedSizesObj instanceof List<?>) {
        for (Object s : (List<?>) selectedSizesObj) {
            if (s != null) {
                String sizeStr = s.toString().trim();
                if (!sizeStr.isEmpty()) {
                    selectedSizes.add(sizeStr);
                }
            }
        }
    }

    List<Category> categories = new ArrayList<>();
    Object categoriesObj = request.getAttribute("categories");
    if (categoriesObj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Category> temp = (List<Category>) categoriesObj;
        categories = temp != null ? temp : new ArrayList<>();
    }
%>

<nav class="shell fs-catalog-nav" aria-label="Product categories">
    <%
        StringBuilder base = new StringBuilder();
        if (!searchVal.isBlank()) base.append("&search=").append(URLEncoder.encode(searchVal, "UTF-8"));
        if (!minPriceVal.isBlank()) base.append("&minPrice=").append(URLEncoder.encode(minPriceVal, "UTF-8"));
        if (!maxPriceVal.isBlank()) base.append("&maxPrice=").append(URLEncoder.encode(maxPriceVal, "UTF-8"));
        if (!brandVal.isBlank()) base.append("&brand=").append(URLEncoder.encode(brandVal, "UTF-8"));
        if (!sortByVal.isBlank()) base.append("&sortBy=").append(URLEncoder.encode(sortByVal, "UTF-8"));
        for (String s : selectedSizes) base.append("&size=").append(URLEncoder.encode(s, "UTF-8"));
    %>

    <a href="<%= request.getContextPath() %>/products?<%= base.length() > 0 ? base.substring(1) : "" %>"
       class="fs-pill <%= (currentCategoryId == null && currentTag.isBlank()) ? "fs-pill--active" : "" %>">All</a>

    <a href="<%= request.getContextPath() %>/products?tag=deals<%= base.toString() %>"
       class="fs-pill <%= "deals".equalsIgnoreCase(currentTag) || "sale".equalsIgnoreCase(currentTag) ? "fs-pill--active" : "" %>">Deals</a>

    <% for (Category c : categories) { %>
        <a href="<%= request.getContextPath() %>/products?category=<%= URLEncoder.encode(c.getCategorySlug(), "UTF-8") %><%= base.toString() %>"
           class="fs-pill <%= (currentCategoryId != null && currentCategoryId.intValue() == c.getCategoryId()) ? "fs-pill--active" : "" %>"><%= c.getCategoryName() %></a>
    <% } %>
</nav>

<section class="shell section-block">
    <nav class="breadcrumb" aria-label="Breadcrumb">
        <a href="<%= request.getContextPath() %>/home">Home</a>
        <span>/</span>
        <a href="<%= request.getContextPath() %>/products">Catalog</a>
        <% if (!currentCategorySlug.isBlank()) { %>
            <span>/</span>
            <span><%= currentCategorySlug.substring(0, 1).toUpperCase() + currentCategorySlug.substring(1) %></span>
        <% } %>
    </nav>
    <div class="fs-catalog-hero">
        <div class="fs-catalog-hero__content">
            <span class="fs-catalog-hero__eyebrow">FashionStore Catalog</span>
            <h1 class="editorial-heading">Shop the complete edit</h1>
            <p>Refined essentials, premium footwear, and polished accessories filtered by real category mapping.</p>
        </div>
        <form class="fs-catalog-hero__search" action="<%= request.getContextPath() %>/products" method="get">
            <input type="search" name="search" value="<%= searchVal %>" placeholder="Search products or brands" aria-label="Search products">
            <% if (currentCategoryId != null) { %><input type="hidden" name="category" value="<%= currentCategorySlug %>"><% } %>
            <button type="submit" class="fs-btn fs-btn--primary">Search</button>
        </form>
    </div>
</section>

<div class="fs-filter-overlay" id="filter-overlay" onclick="closeFilterSidebar()" aria-hidden="true"></div>

<div class="shell fs-catalog-layout">

    <aside class="fs-filter-sidebar" id="filter-sidebar" aria-label="Product filters">
        <button class="fs-filter-sidebar__close" onclick="closeFilterSidebar()" aria-label="Close filters">
            <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
            Close Filters
        </button>

        <form action="<%= request.getContextPath() %>/products" method="get" aria-label="Filter products">
            <input type="hidden" name="search" value="<%= searchVal %>">
            <% if (currentCategoryId != null) { %>
                <input type="hidden" name="category" value="<%= currentCategorySlug %>">
            <% } %>
            <% if (!currentTag.isBlank()) { %>
                <input type="hidden" name="tag" value="<%= currentTag %>">
            <% } %>
            <% if (!sortByVal.isBlank()) { %>
                <input type="hidden" name="sortBy" value="<%= sortByVal %>">
            <% } %>

            <div class="fs-filter-group">
                <h3 class="fs-filter-group__title">Price Range</h3>
                <div class="fs-filter-group__price-inputs">
                    <input type="number" name="minPrice" placeholder="Min" class="fs-form-input" value="<%= minPriceVal %>">
                    <span>—</span>
                    <input type="number" name="maxPrice" placeholder="Max" class="fs-form-input" value="<%= maxPriceVal %>">
                </div>
            </div>

            <div class="fs-filter-group">
                <h3 class="fs-filter-group__title">Size</h3>
                <div class="fs-filter-group__checkbox-list">
                    <% for (String size : allSizes) { %>
                        <label class="fs-form-checkbox">
                            <input type="checkbox" name="size" value="<%= size %>" <%= selectedSizes.contains(size) ? "checked" : "" %>>
                            <span><%= size %></span>
                        </label>
                    <% } %>
                </div>
            </div>

            <button type="submit" class="fs-btn fs-btn--primary fs-btn--full-width">Apply Filters</button>
            <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--outline fs-btn--full-width">Clear All</a>
        </form>
    </aside>

    <main class="fs-catalog-main">
        <% if (!searchVal.isBlank() || !minPriceVal.isBlank() || !maxPriceVal.isBlank() || !brandVal.isBlank() || !sortByVal.isBlank() || !selectedSizes.isEmpty() || currentTag != null && !currentTag.isBlank()) { %>
        <div class="fs-active-filters">
            <span class="fs-active-filters__label">Active Filters:</span>
            <% if (!searchVal.isBlank()) { %>
                <span class="fs-filter-chip">
                    Search: "<%= searchVal %>"
                    <a href="<%= request.getContextPath() %>/products?category=<%= currentCategorySlug %>&tag=<%= currentTag %>" class="fs-filter-chip__remove" aria-label="Remove search filter">✕</a>
                </span>
            <% } %>
            <% if (!minPriceVal.isBlank() && !maxPriceVal.isBlank()) { %>
                <span class="fs-filter-chip">
                    ₹<%= minPriceVal %> - ₹<%= maxPriceVal %>
                    <a href="<%= request.getContextPath() %>/products?search=<%= URLEncoder.encode(searchVal, "UTF-8") %>&category=<%= currentCategorySlug %>&tag=<%= currentTag %>&brand=<%= URLEncoder.encode(brandVal, "UTF-8") %>&sortBy=<%= sortByVal %>" class="fs-filter-chip__remove" aria-label="Remove price filter">✕</a>
                </span>
            <% } %>
            <% if (!brandVal.isBlank()) { %>
                <span class="fs-filter-chip">
                    Brand: <%= brandVal %>
                    <a href="<%= request.getContextPath() %>/products?search=<%= URLEncoder.encode(searchVal, "UTF-8") %>&category=<%= currentCategorySlug %>&tag=<%= currentTag %>&minPrice=<%= minPriceVal %>&maxPrice=<%= maxPriceVal %>&sortBy=<%= sortByVal %>" class="fs-filter-chip__remove" aria-label="Remove brand filter">✕</a>
                </span>
            <% } %>
            <% if (!sortByVal.isBlank()) { %>
                <span class="fs-filter-chip">
                    Sort: <%= sortByVal %>
                    <a href="<%= request.getContextPath() %>/products?search=<%= URLEncoder.encode(searchVal, "UTF-8") %>&category=<%= currentCategorySlug %>&tag=<%= currentTag %>&minPrice=<%= minPriceVal %>&maxPrice=<%= maxPriceVal %>&brand=<%= URLEncoder.encode(brandVal, "UTF-8") %>" class="fs-filter-chip__remove" aria-label="Remove sort filter">✕</a>
                </span>
            <% } %>
            <% for (String size : selectedSizes) { %>
                <span class="fs-filter-chip">
                    Size: <%= size %>
                    <a href="<%= request.getContextPath() %>/products?search=<%= URLEncoder.encode(searchVal, "UTF-8") %>&category=<%= currentCategorySlug %>&tag=<%= currentTag %>&minPrice=<%= minPriceVal %>&maxPrice=<%= maxPriceVal %>&brand=<%= URLEncoder.encode(brandVal, "UTF-8") %>&sortBy=<%= sortByVal %>" class="fs-filter-chip__remove" aria-label="Remove size filter">✕</a>
                </span>
            <% } %>
            <% if (!currentTag.isBlank()) { %>
                <span class="fs-filter-chip">
                    Tag: <%= currentTag %>
                    <a href="<%= request.getContextPath() %>/products?search=<%= URLEncoder.encode(searchVal, "UTF-8") %>&category=<%= currentCategorySlug %>" class="fs-filter-chip__remove" aria-label="Remove tag filter">✕</a>
                </span>
            <% } %>
            <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--outline fs-btn--small">Clear All</a>
        </div>
        <% } %>
        
        <div class="fs-catalog-toolbar">
            <div class="fs-catalog-toolbar__info">
                <span><strong><%= products.size() %></strong> styles shown</span>
                <% if (!searchVal.isBlank()) { %><span>for "<%= searchVal %>"</span><% } %>
            </div>
            <form action="<%= request.getContextPath() %>/products" method="get" class="fs-catalog-toolbar__sort">
                <% if (!searchVal.isBlank()) { %><input type="hidden" name="search" value="<%= searchVal %>"><% } %>
                <% if (currentCategoryId != null) { %><input type="hidden" name="category" value="<%= currentCategorySlug %>"><% } %>
                <% if (!currentTag.isBlank()) { %><input type="hidden" name="tag" value="<%= currentTag %>"><% } %>
                <% if (!minPriceVal.isBlank()) { %><input type="hidden" name="minPrice" value="<%= minPriceVal %>"><% } %>
                <% if (!maxPriceVal.isBlank()) { %><input type="hidden" name="maxPrice" value="<%= maxPriceVal %>"><% } %>
                <% for (String s : selectedSizes) { %><input type="hidden" name="size" value="<%= s %>"><% } %>
                <label for="sortBy">Sort by</label>
                <select id="sortBy" name="sortBy" onchange="this.form.submit()" class="fs-form-select">
                    <option value="" <%= sortByVal.isBlank() ? "selected" : "" %>>Newest</option>
                    <option value="popular" <%= "popular".equals(sortByVal) ? "selected" : "" %>>Trending</option>
                    <option value="price_asc" <%= "price_asc".equals(sortByVal) ? "selected" : "" %>>Price low to high</option>
                    <option value="price_desc" <%= "price_desc".equals(sortByVal) ? "selected" : "" %>>Price high to low</option>
                    <option value="name_asc" <%= "name_asc".equals(sortByVal) ? "selected" : "" %>>Name A-Z</option>
                </select>
            </form>
        </div>
        
        <button class="fs-filter-toggle" id="filter-toggle-btn" onclick="openFilterSidebar()" aria-controls="filter-sidebar" aria-expanded="false">
            <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 4a1 1 0 011-1h16a1 1 0 010 2H4a1 1 0 01-1-1zm3 6a1 1 0 011-1h10a1 1 0 010 2H7a1 1 0 01-1-1zm3 6a1 1 0 011-1h4a1 1 0 010 2h-4a1 1 0 01-1-1z"/>
            </svg>
            Filters
        </button>

        <div class="product-card-grid">
            <!-- Skeleton loader for initial page load -->
            <div id="product-skeleton" class="skeleton-product-grid skeleton-product-grid--hidden">
                <% for (int i = 0; i < 8; i++) { %>
                    <div class="skeleton-product-card skeleton">
                        <div class="skeleton-image"></div>
                        <div class="skeleton-text skeleton-title"></div>
                        <div class="skeleton-text skeleton-brand"></div>
                        <div class="skeleton-text skeleton-price"></div>
                    </div>
                <% } %>
            </div>
            
            <% if (!products.isEmpty()) { %>
                <script>
                    // Hide skeleton when products are loaded
                    document.getElementById('product-skeleton').style.display = 'none';
                </script>
                <% for (int i = 0; i < products.size(); i++) {
                    Product p = products.get(i);
                    double originalPrice = (p.getDiscountPercent() > 0 && p.getDiscountPercent() < 100)
                            ? p.getPrice() / (1 - (p.getDiscountPercent() / 100.0)) : 0;
                %>
                    <article class="product-card">
                        <div class="product-card__media">
                            <a href="<%= request.getContextPath() %>/product?id=<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(String.valueOf(p.getProductId())) %>">
                                <img data-src="<%= p.getImageUrl() %>" alt="<%= p.getProductName() %>" loading="lazy" class="lazy-load" onerror="this.src='<%= request.getContextPath() %>/assets/images/placeholder-product.jpg'; this.onerror=null;">
                            </a>
                            
                            <div class="product-card__badges">
                                <% if (p.isNew()) { %>
                                    <span class="product-card__badge product-card__badge--new">New</span>
                                <% } %>
                                <% if (p.isSale()) { %>
                                    <span class="product-card__badge product-card__badge--sale">Sale</span>
                                <% } %>
                                <% if (p.isTrending()) { %>
                                    <span class="product-card__badge product-card__badge--trending">Trending</span>
                                <% } %>
                            </div>
                            
                            <button class="product-card__wishlist" data-product-id="<%= p.getProductId() %>" onclick="FashionStore.productInteractions.toggleWishlist('<%= org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(String.valueOf(p.getProductId())) %>', this)" aria-label="Add <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %> to wishlist">
                                <svg width="20" height="20" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"></path>
                                </svg>
                            </button>
                        </div>
                        <div class="product-card__body">
                            <span class="product-card__eyebrow"><%= p.getBrand() != null && !p.getBrand().isBlank() ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getBrand()) : org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getCategoryName()) %></span>
                            <a href="<%= request.getContextPath() %>/product?id=<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(String.valueOf(p.getProductId())) %>" class="product-card__title"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %></a>
                            <div class="product-card__footer">
                                <p class="product-card__price">₹<%= String.format("%.2f", p.getPrice()) %></p>
                                <% if (originalPrice > p.getPrice()) { %>
                                    <span class="product-card__price-original">₹<%= String.format("%.2f", originalPrice) %></span>
                                <% } %>
                            </div>
                            <div class="product-card__actions">
                                <a href="<%= request.getContextPath() %>/product?id=<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(String.valueOf(p.getProductId())) %>" class="product-card__btn product-card__btn--primary">View Details</a>
                                <button class="product-card__btn product-card__btn--outline" onclick="FashionStore.cartDrawer.addToCart('<%= org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(String.valueOf(p.getProductId())) %>')" aria-label="Add <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %> to cart">
                                    <svg fill="none" stroke="currentColor" viewBox="0 0 24 24" width="16" height="16" aria-hidden="true">
                                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path>
                                    </svg>
                                </button>
                            </div>
                        </div>
                    </article>
                <% } %>
            <% } else { %>
                <div class="fs-empty-state">
                    <svg class="fs-empty-state__icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"></path>
                    </svg>
                    <h3 class="fs-empty-state__title">No products found</h3>
                    <p class="fs-empty-state__description">We couldn't find any products matching your criteria. Try adjusting your filters or search terms.</p>
                    <div class="fs-empty-state__actions">
                        <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--primary">Clear filters</a>
                        <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--outline">View all products</a>
                    </div>
                </div>
            <% } %>
        </div>
            
        <% if (request.getAttribute("totalPages") != null && (Integer)request.getAttribute("totalPages") > 1) { %>
            <div class="fs-pagination">
                <% 
                    int currentPage = request.getAttribute("currentPage") != null ? (Integer)request.getAttribute("currentPage") : 1;
                    int totalPages = request.getAttribute("totalPages") != null ? (Integer)request.getAttribute("totalPages") : 1;
                    String search = (String)request.getAttribute("search");
                    search = search != null ? search.trim() : "";
                    String minPrice = (String)request.getAttribute("minPrice");
                    minPrice = minPrice != null ? minPrice.trim() : "";
                    String maxPrice = (String)request.getAttribute("maxPrice");
                    maxPrice = maxPrice != null ? maxPrice.trim() : "";
                    String sortBy = (String)request.getAttribute("sortBy");
                    sortBy = sortBy != null ? sortBy.trim() : "";
                    String brand = (String)request.getAttribute("brand");
                    brand = brand != null ? brand.trim() : "";
                    Integer categoryId = (Integer)request.getAttribute("categoryId");
                    String tag = (String)request.getAttribute("tag");
                    tag = tag != null ? tag.trim() : "";
                    Object selectedSizesObj2 = request.getAttribute("selectedSizes");
                    List<String> selectedSizes2 = new ArrayList<>();
                    if (selectedSizesObj2 instanceof List<?>) {
                        for (Object s : (List<?>) selectedSizesObj2) {
                            if (s != null) {
                                String sizeStr = s.toString().trim();
                                if (!sizeStr.isEmpty()) {
                                    selectedSizes2.add(sizeStr);
                                }
                            }
                        }
                    }
                    
                    StringBuilder queryParams = new StringBuilder();
                    if (!search.isEmpty()) {
                        queryParams.append("&search=").append(java.net.URLEncoder.encode(search, "UTF-8"));
                    }
                    if (categoryId != null) {
                        String categorySlug = (String)request.getAttribute("categorySlug");
                        categorySlug = categorySlug != null ? categorySlug.trim() : "";
                        if (!categorySlug.isEmpty()) {
                            queryParams.append("&category=").append(java.net.URLEncoder.encode(categorySlug, "UTF-8"));
                        } else {
                            queryParams.append("&categoryId=").append(categoryId);
                        }
                    }
                    if (!tag.isEmpty()) {
                        queryParams.append("&tag=").append(java.net.URLEncoder.encode(tag, "UTF-8"));
                    }
                    if (!minPrice.isEmpty()) {
                        queryParams.append("&minPrice=").append(java.net.URLEncoder.encode(minPrice, "UTF-8"));
                    }
                    if (!maxPrice.isEmpty()) {
                        queryParams.append("&maxPrice=").append(java.net.URLEncoder.encode(maxPrice, "UTF-8"));
                    }
                    if (!brand.isEmpty()) {
                        queryParams.append("&brand=").append(java.net.URLEncoder.encode(brand, "UTF-8"));
                    }
                    if (!sortBy.isEmpty()) {
                        queryParams.append("&sortBy=").append(java.net.URLEncoder.encode(sortBy, "UTF-8"));
                    }
                    for (String s : selectedSizes2) {
                        queryParams.append("&size=").append(java.net.URLEncoder.encode(s, "UTF-8"));
                    }
                %>
                
                <% if (currentPage > 1) { %>
                    <a href="<%= request.getContextPath() %>/products?page=<%= currentPage - 1 %><%= queryParams.toString() %>" 
                       class="fs-pagination__link" aria-label="Previous page">
                        <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                        </svg>
                        Prev
                    </a>
                <% } else { %>
                    <span class="fs-pagination__link fs-pagination__link--disabled">
                        <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7"></path>
                        </svg>
                        Prev
                    </span>
                <% } %>
                
                <% for (int i = 1; i <= totalPages; i++) { 
                    if (i == currentPage) { %>
                        <span class="fs-pagination__link fs-pagination__link--active" aria-current="page"><%= i %></span>
                    <% } else if (i == 1 || i == totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) { %>
                        <a href="<%= request.getContextPath() %>/products?page=<%= i %><%= queryParams.toString() %>" 
                           class="fs-pagination__link"><%= i %></a>
                    <% } else if (i == currentPage - 2 || i == currentPage + 2) { %>
                        <span class="fs-pagination__ellipsis">...</span>
                    <% } 
                } %>
                
                <% if (currentPage < totalPages) { %>
                    <a href="<%= request.getContextPath() %>/products?page=<%= currentPage + 1 %><%= queryParams.toString() %>" 
                       class="fs-pagination__link" aria-label="Next page">
                        Next
                        <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                        </svg>
                    </a>
                <% } else { %>
                    <span class="fs-pagination__link fs-pagination__link--disabled">
                        Next
                        <svg width="16" height="16" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7"></path>
                        </svg>
                    </span>
                <% } %>
            </div>
        <% } %>
        </main>

    </div>
</div>

</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

<div class="modal-overlay" id="quickViewModal" aria-hidden="true" role="dialog" aria-modal="true">
    <div class="modal-content">
        <button class="modal-close modal-close--positioned" onclick="FashionStore.closeQuickView()" aria-label="Close modal">
            <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
        </button>
        <div class="modal-body" id="modalContent"></div>
    </div>
</div>

</body>
</html>
