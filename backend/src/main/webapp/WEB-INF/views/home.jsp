<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Product" %>
<%@ page import="com.fashionstore.model.Category" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Home");
    request.setAttribute("_pageCSS", "home");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body>
<div class="page-wrapper">

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    List<Product> products = new ArrayList<>();
    Object obj = request.getAttribute("products");
    if (obj != null && obj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Product> temp = (List<Product>) obj;
        products = temp;
    }

    List<Category> categories = new ArrayList<>();
    Object categoriesObj = request.getAttribute("categories");
    if (categoriesObj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Category> temp = (List<Category>) categoriesObj;
        categories = temp;
    }

    List<Product> trendingProducts = new ArrayList<>();
    Object trendingObj = request.getAttribute("trendingProducts");
    if (trendingObj != null && trendingObj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Product> temp = (List<Product>) trendingObj;
        trendingProducts = temp;
    }

    List<Product> featuredGrid = new ArrayList<>();
    String featuredSourceLabel = "Curated edit";
    if (!products.isEmpty()) {
        featuredGrid = products;
    } else if (!trendingProducts.isEmpty()) {
        featuredGrid = trendingProducts;
        featuredSourceLabel = "Trending now";
    }

    List<Product> recentlyViewedProducts = new ArrayList<>();
    Object recentObj = request.getAttribute("recentlyViewedProducts");
    if (recentObj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Product> temp = (List<Product>) recentObj;
        recentlyViewedProducts = temp;
    }
%>


<main id="main-content" class="home-page">
    <%-- 1. LUXURY HERO SECTION --%>
    <section class="hero-split">
        <div class="hero-split__content">
            <div class="container">
                <div class="hero-split__text-wrapper">
                    <span class="hero-kicker">New Collection 2026</span>
                    <h1 class="hero-title">Elevate Your Everyday Style</h1>
                    <p class="hero-description">Experience the perfect blend of modern minimalism and timeless luxury. Our curated collection is designed for the discerning individual.</p>
                    <div class="hero-actions">
                        <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--primary">Shop Collection</a>
                        <a href="<%= request.getContextPath() %>/products?category=new" class="fs-btn fs-btn--outline">Discover New</a>
                    </div>
                </div>
            </div>
        </div>
        <div class="hero-split__visual">
            <img src="<%= request.getContextPath() %>/assets/images/hero-banner.png" alt="New Season Look" loading="eager">
            <div class="hero-split__badge surface-card">
                <span class="badge-title">Summer Edition</span>
                <span class="badge-copy">Handcrafted Excellence</span>
            </div>
        </div>
    </section>

    <%-- 2. SIGNATURE FEATURES --%>
    <section class="features-band">
        <div class="container">
            <div class="features-grid">
                <div class="feature-item">
                    <span class="feature-title">Global Shipping</span>
                    <p class="feature-desc">Premium delivery to over 50 countries worldwide.</p>
                </div>
                <div class="feature-item">
                    <span class="feature-title">Personal Styling</span>
                    <p class="feature-desc">Expert guidance for your perfect wardrobe edit.</p>
                </div>
                <div class="feature-item">
                    <span class="feature-title">Secure Checkout</span>
                    <p class="feature-desc">Low-friction, encrypted payment experience.</p>
                </div>
            </div>
        </div>
    </section>

    <%-- 3. EDITORIAL CATEGORIES --%>
    <% if (!categories.isEmpty()) { %>
    <section class="section">
        <div class="container">
            <header class="section-header">
                <div class="section-header__group">
                    <span class="section-kicker">Collections</span>
                    <h2 class="section-title">Shop by Category</h2>
                </div>
            </header>
            
            <div class="category-masonry">
            <% for (Category c : categories) { 
                String categoryImg = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=800&q=80";
                if ("Men".equalsIgnoreCase(c.getCategoryName())) categoryImg = "https://images.unsplash.com/photo-1617137968427-85924c800a22?auto=format&fit=crop&w=800&q=80";
                if ("Women".equalsIgnoreCase(c.getCategoryName())) categoryImg = "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80";
                if ("Footwear".equalsIgnoreCase(c.getCategoryName())) categoryImg = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80";
            %>
                <a class="category-card" href="<%= request.getContextPath() %>/products?category=<%= java.net.URLEncoder.encode(c.getCategorySlug(), "UTF-8") %>">
                    <div class="category-card__media">
                        <img src="<%= categoryImg %>" alt="<%= c.getCategoryName() %>" loading="lazy">
                    </div>
                    <div class="category-card__content">
                        <h3 class="category-card__title"><%= c.getCategoryName() %></h3>
                        <span class="category-card__link">View All</span>
                    </div>
                </a>
            <% } %>
            </div>
        </div>
    </section>
    <% } %>

    <%-- 4. CURATED PRODUCTS --%>
    <% if (!featuredGrid.isEmpty()) { %>
    <section class="section section--alt">
        <div class="container">
            <header class="section-header section-header--split">
                <div class="section-header__group">
                    <span class="section-kicker">The Edit</span>
                    <h2 class="section-title">New Season Arrivals</h2>
                </div>
                <a href="<%= request.getContextPath() %>/products" class="section-link">Shop All Arrivals</a>
            </header>

            <div class="product-grid">
            <% for (Product p : featuredGrid) { %>
                <article class="product-card">
                    <div class="product-card__media">
                        <a href="<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>">
                            <img src="<%= p.getImageUrl() %>" alt="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %>" loading="lazy">
                        </a>
                        <button class="wishlist-btn" aria-label="Add to wishlist" data-product-id="<%= p.getProductId() %>">
                            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20.3 5.7a5.1 5.1 0 0 0-7.2 0L12 6.8l-1.1-1.1a5.1 5.1 0 0 0-7.2 7.2L12 21l8.3-8.1a5.1 5.1 0 0 0 0-7.2z"></path></svg>
                        </button>
                    </div>
                    <div class="product-card__info">
                        <div class="product-card__meta">
                            <span class="product-brand"><%= p.getBrand() != null && !p.getBrand().isBlank() ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getBrand()) : "FashionStore" %></span>
                            <h3 class="product-title"><a href="<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %></a></h3>
                        </div>
                        <p class="product-price">₹<%= String.format("%.2f", p.getPrice()) %></p>
                    </div>
                </article>
            <% } %>
            </div>
        </div>
    </section>
    <% } %>

    <%-- 5. PROMO BLOCK --%>
    <section class="promo-section">
        <div class="container">
            <div class="promo-card">
                <div class="promo-card__image">
                    <img src="https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80" alt="Promotional Offer" loading="lazy">
                </div>
                <div class="promo-card__content">
                    <span class="promo-kicker">Limited Privilege</span>
                    <h2 class="promo-title">Privée Sale: Up to 30% Off</h2>
                    <p class="promo-desc">Exclusive early access to our seasonal private sale for registered members. Sign in to see discounted prices.</p>
                    <div class="promo-actions">
                        <a href="<%= request.getContextPath() %>/products?tag=sale" class="fs-btn fs-btn--primary">Shop the Sale</a>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <%-- 6. EDITORIAL CONTENT --%>
    <section class="section">
        <div class="container">
            <div class="editorial-row">
                <div class="editorial-main">
                    <img src="https://images.unsplash.com/photo-1469334031218-e382a71b716b?auto=format&fit=crop&w=800&q=90" alt="Street Style" loading="lazy">
                    <div class="editorial-copy">
                        <span class="editorial-kicker">Street Style</span>
                        <h2 class="editorial-title">Urban Minimalist</h2>
                        <p>Modern silhouettes designed for the city pace. Discover versatile pieces that transition seamlessly from morning commute to late-night events.</p>
                        <a href="<%= request.getContextPath() %>/products?tag=street" class="section-link">Explore the Edit</a>
                    </div>
                </div>
                <aside class="editorial-sidebar">
                    <div class="sidebar-header">
                        <span class="sidebar-kicker">Trending</span>
                        <h3 class="sidebar-title">Seasonal Essentials</h3>
                    </div>
                    <div class="sidebar-items">
                        <% List<Product> sideSource = !trendingProducts.isEmpty() ? trendingProducts : featuredGrid; %>
                        <% for (int i = 0; i < Math.min(3, sideSource.size()); i++) { Product s = sideSource.get(i); %>
                            <a href="<%= request.getContextPath() %>/product?id=<%= s.getProductId() %>" class="sidebar-item">
                                <div class="sidebar-item__media">
                                    <img src="<%= s.getImageUrl() %>" alt="<%= s.getProductName() %>" loading="lazy">
                                </div>
                                <div class="sidebar-item__text">
                                    <span class="sidebar-item__title"><%= s.getProductName() %></span>
                                    <span class="sidebar-item__price">₹<%= String.format("%.2f", s.getPrice()) %></span>
                                </div>
                            </a>
                        <% } %>
                    </div>
                </aside>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</div>
</body>
</html>
