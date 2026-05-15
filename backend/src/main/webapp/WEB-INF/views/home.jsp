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


<main id="main-content" class="fs-home-page">
    <%-- 1. PREMIUM HERO SECTION --%>
    <section class="hero">
        <div class="hero__background">
            <img src="https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=1920&q=80" alt="Hero background" loading="eager">
        </div>
        <div class="hero__content">
            <div class="hero__text">
                <span class="hero__kicker">New Summer Collection 2026</span>
                <h1 class="hero__title">Modern fashion designed for everyday life.</h1>
                <p class="hero__description">Discover our curated selection of premium pieces crafted with exceptional quality and timeless style. From refined tailoring to elevated essentials, find your perfect look.</p>
                <div class="hero__actions">
                    <a href="<%= request.getContextPath() %>/products" class="hero__btn hero__btn--primary">Shop Now</a>
                    <a href="<%= request.getContextPath() %>/products?tag=new" class="hero__btn hero__btn--secondary">Explore New Arrivals</a>
                </div>
            </div>
        </div>
    </section>

    <section class="fs-home-signature-band section-block--compact">
        <div class="shell fs-home-signature-band__grid">
            <div>
                <span class="fs-home-signature-band__label">Complimentary delivery</span>
                <p>On curated prepaid orders and elevated essentials.</p>
            </div>
            <div>
                <span class="fs-home-signature-band__label">Luxury fit guidance</span>
                <p>Clean product storytelling with straightforward sizing cues.</p>
            </div>
            <div>
                <span class="fs-home-signature-band__label">Refined checkout</span>
                <p>Simplified cart-to-payment flow built for low friction.</p>
            </div>
        </div>
    </section>

    <%-- 2. FEATURED CATEGORIES --%>
    <% if (!categories.isEmpty()) { %>
    <section class="section-block section-block--soft">
        <div class="shell">
            <div class="section-heading">
                <div class="section-heading__text">
                    <span class="section-heading__kicker">Browse by Category</span>
                    <h2 class="section-heading__title">Shop Collections</h2>
                </div>
            </div>
            
            <div class="fs-home-category-grid">
            <% for (Category c : categories) { 
                String categoryImg = "https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=800&q=80";
                if ("Men".equalsIgnoreCase(c.getCategoryName())) categoryImg = "https://images.unsplash.com/photo-1617137968427-85924c800a22?auto=format&fit=crop&w=800&q=80";
                if ("Women".equalsIgnoreCase(c.getCategoryName())) categoryImg = "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80";
                if ("Footwear".equalsIgnoreCase(c.getCategoryName())) categoryImg = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=800&q=80";
            %>
                <a class="fs-home-category-card surface-card" href="<%= request.getContextPath() %>/products?category=<%= java.net.URLEncoder.encode(c.getCategorySlug(), "UTF-8") %>">
                    <img data-src="<%= categoryImg %>" alt="<%= c.getCategoryName() %>" loading="lazy" class="lazy-load">
                    <div class="fs-home-category-card__body">
                        <span class="fs-home-category-card__eyebrow">Collection</span>
                        <h3 class="fs-home-category-card__title"><%= c.getCategoryName() %></h3>
                        <span class="fs-home-category-card__link">Explore now</span>
                    </div>
                </a>
            <% } %>
            </div>
        </div>
    </section>
    <% } %>

    <%-- 3. FEATURED PRODUCTS - Premium Grid --%>
    <% if (!featuredGrid.isEmpty()) { %>
    <section class="section-block">
        <div class="shell">
            <div class="section-heading">
                <div class="section-heading__text">
                    <span class="section-heading__kicker">Featured Collection</span>
                    <h2 class="section-heading__title">Shop the Season's Best</h2>
                </div>
                <a href="<%= request.getContextPath() %>/products" class="section-heading__link">View All Products →</a>
            </div>

            <div class="product-card-grid">
            <!-- Skeleton loader for featured products -->
            <div id="featured-skeleton" class="skeleton-product-grid skeleton-product-grid--hidden">
                <% for (int i = 0; i < 4; i++) { %>
                    <div class="skeleton-product-card skeleton">
                        <div class="skeleton-image"></div>
                        <div class="skeleton-text skeleton-title"></div>
                        <div class="skeleton-text skeleton-brand"></div>
                        <div class="skeleton-text skeleton-price"></div>
                    </div>
                <% } %>
            </div>
            
            <script>
                // Show skeleton initially, hide when products load
                if (document.readyState === 'loading') {
                    document.getElementById('featured-skeleton').style.display = 'grid';
                }
            </script>
            
            <% for (Product p : featuredGrid) { %>
                <article class="product-card">
                    <div class="product-card__media">
                        <a href="<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>">
                            <img data-src="<%= p.getImageUrl() %>" alt="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %>" loading="lazy" class="lazy-load" onerror="this.src='<%= request.getContextPath() %>/assets/images/placeholder-product.jpg'; this.onerror=null;">
                        </a>
                        <button class="product-card__wishlist" data-product-id="<%= p.getProductId() %>" onclick="FashionStore.productInteractions.toggleWishlist('<%= p.getProductId() %>', this)" aria-label="Add to wishlist">
                            <svg width="20" height="20" fill="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"></path>
                            </svg>
                        </button>
                    </div>
                    <div class="product-card__body">
                        <span class="product-card__eyebrow"><%= p.getBrand() != null && !p.getBrand().isBlank() ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getBrand()) : "FashionStore" %></span>
                        <a href="<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>" class="product-card__title"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %></a>
                        <div class="product-card__footer">
                            <p class="product-card__price">₹<%= String.format("%.2f", p.getPrice()) %></p>
                            <div class="product-card__actions">
                                <button class="product-card__btn product-card__btn--primary" onclick="window.location.href='<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>'">View</button>
                            </div>
                        </div>
                    </div>
                </article>
            <% } %>
            </div>
        </div>
    </section>
    <% } %>

    <section class="fs-home-editorial section-block section-block--contrast">
        <div class="shell fs-home-editorial__grid">
            <article class="fs-home-editorial__feature surface-card">
                <span class="editorial-kicker">Editorial campaign</span>
                <h2 class="section-display">Quiet luxury, modern motion, and a stronger storefront core.</h2>
                <p class="section-lede">The new customer-facing experience prioritizes stable containment, calmer interactions, and a fashion-first visual hierarchy. That gives your catalog space to breathe while keeping discovery, filtering, and conversion pathways clear.</p>
                <div class="cluster-row fs-home-editorial__actions">
                    <a href="<%= request.getContextPath() %>/products?tag=new" class="fs-btn fs-btn--primary">New arrivals</a>
                    <a href="<%= request.getContextPath() %>/products?tag=deals" class="fs-home-hero__secondary-link">Private sale edit</a>
                </div>
            </article>

            <aside class="fs-home-editorial__list surface-card">
                <div class="stack-sm">
                    <span class="editorial-kicker">Trending now</span>
                    <h3 class="fs-home-editorial__list-title">Most viewed pieces</h3>
                </div>
                <div class="fs-home-editorial__items">
                    <% List<Product> trendSource = !trendingProducts.isEmpty() ? trendingProducts : featuredGrid; %>
                    <% for (int i = 0; i < Math.min(4, trendSource.size()); i++) { Product trend = trendSource.get(i); %>
                        <a class="fs-home-editorial__item" href="<%= request.getContextPath() %>/product?id=<%= trend.getProductId() %>">
                            <span class="fs-home-editorial__item-index">0<%= i + 1 %></span>
                            <span class="fs-home-editorial__item-copy">
                                <strong><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(trend.getProductName()) %></strong>
                                <span>₹<%= String.format("%.2f", trend.getPrice()) %></span>
                            </span>
                        </a>
                    <% } %>
                </div>
            </aside>
        </div>
    </section>

    <%-- 4. TRENDING PRODUCTS --%>
    <% if (!trendingProducts.isEmpty()) { %>
    <section class="section-block">
        <div class="shell">
            <div class="section-heading">
                <div class="section-heading__text">
                    <span class="section-heading__kicker">Trending Now</span>
                    <h2 class="section-heading__title">Most Popular</h2>
                </div>
                <a href="<%= request.getContextPath() %>/products?tag=trending" class="section-heading__link">View All →</a>
            </div>

            <div class="product-grid">
            <!-- Skeleton loader for trending products -->
            <div id="trending-skeleton" class="skeleton-product-grid skeleton-product-grid--hidden">
                <% for (int i = 0; i < 4; i++) { %>
                    <div class="skeleton-product-card skeleton">
                        <div class="skeleton-image"></div>
                        <div class="skeleton-text skeleton-title"></div>
                        <div class="skeleton-text skeleton-brand"></div>
                        <div class="skeleton-text skeleton-price"></div>
                    </div>
                <% } %>
            </div>
            
            <script>
                // Show skeleton initially, hide when products load
                if (document.readyState === 'loading') {
                    document.getElementById('trending-skeleton').style.display = 'grid';
                }
            </script>
            
            <% for (Product p : trendingProducts) { %>
                <article class="product-card">
                    <div class="product-card__media">
                        <a href="<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>">
                            <img src="<%= p.getImageUrl() %>" alt="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %>" loading="lazy">
                        </a>
                    </div>
                    <div class="product-card__body">
                        <span class="product-card__brand"><%= p.getBrand() != null && !p.getBrand().isBlank() ? org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getBrand()) : "FashionStore" %></span>
                        <a href="<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>" class="product-card__title"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %></a>
                        <div class="product-card__footer">
                            <p class="product-card__price">₹<%= String.format("%.2f", p.getPrice()) %></p>
                            <button class="product-card__btn" onclick="window.location.href='<%= request.getContextPath() %>/product?id=<%= p.getProductId() %>'">View</button>
                        </div>
                    </div>
                </article>
            <% } %>
            </div>
        </div>
    </section>
    <% } %>

    <%-- 5. PROMOTIONAL BANNER --%>
    <section class="section-block">
        <div class="shell">
            <div class="promo-banner">
                <div class="promo-banner__content">
                    <span class="promo-banner__kicker">Limited Time Offer</span>
                    <h2 class="promo-banner__title">Get 20% Off Your First Order</h2>
                    <p class="promo-banner__description">Use code WELCOME20 at checkout. Valid for new customers only.</p>
                    <a href="<%= request.getContextPath() %>/products" class="promo-banner__btn">Shop Now</a>
                </div>
                <div class="promo-banner__visual">
                    <img src="https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=800&q=80" alt="Promotional banner" loading="lazy">
                </div>
            </div>
        </div>
    </section>

    <%-- 6. EDITORIAL SECTION --%>
    <section class="section-block section-block--contrast">
        <div class="shell">
            <div class="section-heading">
                <div class="section-heading__text">
                    <span class="section-heading__kicker">The Edit</span>
                    <h2 class="section-heading__title">Style Stories</h2>
                </div>
            </div>

            <div class="editorial-grid">
                <article class="editorial-card">
                    <div class="editorial-card__media">
                        <img data-src="https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=600&q=80" alt="Summer Collection" loading="lazy" class="lazy-load">
                    </div>
                    <div class="editorial-card__body">
                        <span class="editorial-card__kicker">Summer 2026</span>
                        <h3 class="editorial-card__title">Effortless Elegance</h3>
                        <p class="editorial-card__description">Discover pieces that transition seamlessly from day to night.</p>
                        <a href="<%= request.getContextPath() %>/products?tag=new" class="editorial-card__link">Explore →</a>
                    </div>
                </article>

                <article class="editorial-card">
                    <div class="editorial-card__media">
                        <img src="https://images.unsplash.com/photo-1469334031218-e382a71b716b?auto=format&fit=crop&w=600&q=80" alt="Street Style" loading="lazy">
                    </div>
                    <div class="editorial-card__body">
                        <span class="editorial-card__kicker">Street Style</span>
                        <h3 class="editorial-card__title">Urban Edge</h3>
                        <p class="editorial-card__description">Bold silhouettes and modern aesthetics for the city dweller.</p>
                        <a href="<%= request.getContextPath() %>/products?tag=trending" class="editorial-card__link">Explore →</a>
                    </div>
                </article>

                <article class="editorial-card">
                    <div class="editorial-card__media">
                        <img data-src="https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=600&q=80" alt="Classic Pieces" loading="lazy" class="lazy-load">
                    </div>
                    <div class="editorial-card__body">
                        <span class="editorial-card__kicker">Timeless</span>
                        <h3 class="editorial-card__title">Modern Classics</h3>
                        <p class="editorial-card__description">Essential pieces that never go out of style.</p>
                        <a href="<%= request.getContextPath() %>/products?tag=classic" class="editorial-card__link">Explore →</a>
                    </div>
                </article>
            </div>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
