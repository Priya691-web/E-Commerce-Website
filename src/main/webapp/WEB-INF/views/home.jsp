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
%>

<main class="home-page">

    <%-- 1. HERO --%>
    <section class="home-hero hero" id="home-hero" aria-label="Hero">
        <div class="hero-gradient-orb" aria-hidden="true"></div>
        <div class="container">
            <div class="hero-content">
                <span class="hero-badge">New Collection &middot; Spring / Summer 2026</span>
                <h1 class="hero-title">
                    <span>Luxury Everyday</span>
                    <span>Essentials</span>
                </h1>
                <p class="hero-subtitle">A cinematic edit of tailored separates, sculptural accessories, and city-ready footwear &mdash; engineered for the modern wardrobe.</p>
                <div class="hero-actions">
                    <a href="<%= request.getContextPath() %>/products?tag=new" class="btn btn-primary">Explore Collection</a>
                    <a href="<%= request.getContextPath() %>/products?tag=trending" class="btn btn-secondary">Discover the Edit</a>
                </div>
            </div>
        </div>
    </section>

    <div class="divider-luxury" aria-hidden="true"></div>

    <%-- 2. CATEGORY GRID --%>
    <% if (!categories.isEmpty()) { %>
    <section class="home-section home-categories" aria-labelledby="home-categories-heading">
        <div class="container">
            <header class="section-head home-section-head">
                <div>
                    <span class="section-label" id="home-categories-heading">Shop by category</span>
                    <h2 class="home-section-title">Curated collections</h2>
                </div>
            </header>
            <div class="category-grid">
            <% for (Category c : categories) { %>
                <a class="category-tile category-<%= c.getCategorySlug() %>" href="<%= request.getContextPath() %>/products?category=<%= java.net.URLEncoder.encode(c.getCategorySlug(), "UTF-8") %>">
                    <span class="category-tile__name"><%= c.getCategoryName() %></span>
                    <small class="category-tile__hint">Explore edit</small>
                </a>
            <% } %>
                <a class="category-tile category-new" href="<%= request.getContextPath() %>/products?tag=new"><span class="category-tile__name">New Arrivals</span><small class="category-tile__hint">Latest drop</small></a>
                <a class="category-tile category-sale" href="<%= request.getContextPath() %>/products?tag=deals"><span class="category-tile__name">Sale</span><small class="category-tile__hint">Limited offers</small></a>
            </div>
        </div>
    </section>
    <div class="divider-luxury" aria-hidden="true"></div>
    <% } %>

    <%-- 3. FEATURED PRODUCTS (single grid: curated products, else trending fallback) --%>
    <section class="home-section home-featured" aria-labelledby="home-featured-heading">
        <div class="container">
            <div class="section-head">
                <div>
                    <span class="section-label"><%= featuredSourceLabel %></span>
                    <h2 class="home-section-title" id="home-featured-heading">Featured products</h2>
                </div>
                <a class="btn btn-outline" href="<%= request.getContextPath() %>/products">View all</a>
            </div>

            <div class="product-grid home-product-grid">
            <% if (!featuredGrid.isEmpty()) { %>
                <% for (Product p : featuredGrid) { %>
                <article class="product-card">
                    <div class="product-card-image-wrapper">
                        <img class="product-card-image" src="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getImageUrl()) %>" alt="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(p.getProductName()) %>" loading="lazy" onerror="this.src='<%= request.getContextPath() %>/assets/images/placeholder-product.jpg'; this.onerror=null;">
                        <button type="button" class="product-card-wishlist" data-product-id="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(String.valueOf(p.getProductId())) %>" onclick="FashionStore.toggleWishlist('<%= org.apache.commons.text.StringEscapeUtils.escapeEcmaScript(String.valueOf(p.getProductId())) %>', this)" aria-label="Add to wishlist">
                            <svg class="wishlist-icon" width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"/>
                            </svg>
                        </button>
                        <% if (p.isSale()) { %>
                            <span class="product-card-badge badge-sale">Sale</span>
                        <% } else if (p.isNew()) { %>
                            <span class="product-card-badge badge-new">New</span>
                        <% } else if (p.isTrending()) { %>
                            <span class="product-card-badge badge-trending">Trending</span>
                        <% } %>
                    </div>
                    <div class="product-card-content">
                        <span class="product-card-brand"><%= p.getBrand() != null && !p.getBrand().isBlank() ? p.getBrand() : p.getCategoryName() %></span>
                        <h3 class="product-card-name"><%= p.getProductName() %></h3>
                        <% if (p.getCategoryName() != null) { %>
                            <span class="product-card-category"><%= p.getCategoryName() %></span>
                        <% } %>
                        <div class="product-card-bottom">
                            <div class="product-card-price">
                                <% if (p.getDiscountPercent() > 0) { %>
                                    <span class="product-card-price-current">₹<%= String.format("%.2f", p.getPrice() * (1 - p.getDiscountPercent() / 100)) %></span>
                                    <span class="product-card-price-original">₹<%= String.format("%.2f", p.getPrice()) %></span>
                                <% } else { %>
                                    <span class="product-card-price-current">₹<%= String.format("%.2f", p.getPrice()) %></span>
                                <% } %>
                            </div>
                            <div class="product-card-actions">
                                <a href="<%= request.getContextPath() %>/product?id=<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(String.valueOf(p.getProductId())) %>" class="btn btn-primary product-card-add-btn">View details</a>
                            </div>
                        </div>
                    </div>
                </article>
                <% } %>
            <% } else { %>
                <div class="home-empty-state empty-state">
                    <svg class="empty-state-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                    </svg>
                    <h3 class="empty-state-title">No products available yet</h3>
                    <p class="empty-state-description">We're curating our collection. Check back soon for new arrivals and exclusive pieces.</p>
                    <div class="empty-state-action">
                        <a href="<%= request.getContextPath() %>/products?tag=new" class="btn btn-primary">Shop new arrivals</a>
                        <a href="<%= request.getContextPath() %>/products" class="btn btn-outline">Browse all</a>
                    </div>
                </div>
            <% } %>
            </div>
        </div>
    </section>

    <div class="divider-luxury" aria-hidden="true"></div>

    <%-- 4. BRAND STORY / EDITORIAL (story bands + campaigns + social mosaic) --%>
    <section class="home-section home-brand" aria-labelledby="home-brand-heading">
        <div class="container">
            <header class="section-head home-section-head home-brand__masthead">
                <div>
                    <span class="section-label" id="home-brand-heading">Brand story</span>
                    <h2 class="home-section-title">Crafted for modern movement</h2>
                    <p class="home-brand__lede text-secondary">Editorial drops, seasonal campaigns, and a lens on how the community wears FashionStore.</p>
                </div>
            </header>

            <div class="home-editorial-stack">
                <div class="editorial-band editorial-band--story-a">
                    <div class="editorial-copy">
                        <span class="section-label editorial-copy__label">The edit</span>
                        <h3 class="home-editorial-title">Quiet luxury, sculpted lines.</h3>
                        <p class="editorial-copy__body">Tailored separates, relaxed textures, polished footwear, and accessories that carry from morning commute to evening plans.</p>
                        <a href="<%= request.getContextPath() %>/products" class="btn btn-outline editorial-copy__cta">Build a look</a>
                    </div>
                    <div class="editorial-images" aria-hidden="true">
                        <div class="editorial-image editorial-image-primary"></div>
                        <div class="editorial-image editorial-image-secondary"></div>
                    </div>
                </div>

                <div class="editorial-band editorial-band--story-b">
                    <div class="editorial-image editorial-image-urban" aria-hidden="true"></div>
                    <div class="editorial-copy">
                        <span class="section-label editorial-copy__label">Urban essentials</span>
                        <h3 class="home-editorial-title">Street luxury, layered neutrals.</h3>
                        <p class="editorial-copy__body">Minimal tailoring meets relaxed silhouettes &mdash; structured outerwear and pieces built for unscripted city days.</p>
                        <a href="<%= request.getContextPath() %>/products?category=men" class="btn btn-outline editorial-copy__cta">Shop the story</a>
                    </div>
                    <div class="editorial-image editorial-image-tailoring" aria-hidden="true"></div>
                </div>
            </div>

            <div class="home-campaign-grid campaign-grid" role="navigation" aria-label="Seasonal edits">
                <a class="campaign-card campaign-dark" href="<%= request.getContextPath() %>/products?category=women">
                    <span class="campaign-card__label">Women's edit</span>
                    <strong class="campaign-card__title">Fluid tailoring and elevated separates</strong>
                </a>
                <a class="campaign-card campaign-light" href="<%= request.getContextPath() %>/products?category=men">
                    <span class="campaign-card__label">Men's edit</span>
                    <strong class="campaign-card__title">Sharp layers for the city uniform</strong>
                </a>
                <a class="campaign-card campaign-accent" href="<%= request.getContextPath() %>/products?category=footwear">
                    <span class="campaign-card__label">Footwear</span>
                    <strong class="campaign-card__title">Sneakers, boots, and refined leather</strong>
                </a>
            </div>

            <div class="home-social">
                <div class="home-social__intro">
                    <span class="section-label">Seen in the city</span>
                    <h3 class="home-section-title home-section-title--compact">Styled by the FashionStore community</h3>
                </div>
                <div class="social-grid" aria-label="Community gallery">
                    <div class="social-shot shot-1" role="presentation"></div>
                    <div class="social-shot shot-2" role="presentation"></div>
                    <div class="social-shot shot-3" role="presentation"></div>
                    <div class="social-shot shot-4" role="presentation"></div>
                </div>
            </div>
        </div>
    </section>

    <div class="divider-luxury" aria-hidden="true"></div>

    <%-- 5. VALUE PROPOSITIONS --%>
    <section class="home-section home-value" aria-labelledby="home-value-heading">
        <div class="container">
            <header class="section-head home-section-head">
                <div>
                    <span class="section-label" id="home-value-heading">Why shop with us</span>
                    <h2 class="home-section-title">The FashionStore standard</h2>
                </div>
            </header>

            <div class="home-value-strip card-surface" role="list">
                <div class="home-value-item" role="listitem">
                    <h3 class="home-value-item__title">Premium materials</h3>
                    <p class="text-secondary">Curated fabrics and build quality designed for everyday luxury.</p>
                </div>
                <div class="home-value-item" role="listitem">
                    <h3 class="home-value-item__title">Fast delivery</h3>
                    <p class="text-secondary">Quick dispatch and real-time order tracking across major cities.</p>
                </div>
                <div class="home-value-item" role="listitem">
                    <h3 class="home-value-item__title">Secure payments</h3>
                    <p class="text-secondary">Trusted checkout with encrypted transactions and reliable support.</p>
                </div>
            </div>

            <div class="home-trust-block">
                <p class="home-trust-eyebrow section-label">Trust</p>
                <h3 class="home-section-title home-section-title--compact" id="home-trust-title">Why customers stay</h3>
                <p class="home-trust-sub text-secondary">Built like a brand experience, not a basic catalog.</p>
                <div class="home-trust-grid trust-grid" aria-labelledby="home-trust-title">
                    <article class="home-trust-card trust-card card-surface">
                        <h4 class="home-trust-card__title">Style-led catalog</h4>
                        <p class="text-secondary">Collections are structured for discovery, helping customers browse by intent and season.</p>
                    </article>
                    <article class="home-trust-card trust-card card-surface">
                        <h4 class="home-trust-card__title">Conversion-focused UX</h4>
                        <p class="text-secondary">Clear CTAs, polished cards, and frictionless flows increase add-to-cart confidence.</p>
                    </article>
                    <article class="home-trust-card trust-card card-surface">
                        <h4 class="home-trust-card__title">Scalable design system</h4>
                        <p class="text-secondary">Tokenized spacing, typography, and components keep every new page consistent.</p>
                    </article>
                </div>
            </div>
        </div>
    </section>

    <div class="divider-luxury" aria-hidden="true"></div>

    <%-- 6. NEWSLETTER --%>
    <section class="newsletter-section" aria-labelledby="home-newsletter-heading">
        <div class="container newsletter-inner">
            <div class="newsletter-inner__copy">
                <span class="section-label newsletter-section__label">Private list</span>
                <h2 class="newsletter-section__title" id="home-newsletter-heading">First access to new drops and seasonal edits.</h2>
                <p class="newsletter-section__hint text-secondary">No spam. Unsubscribe anytime.</p>
            </div>
            <form class="newsletter-form" action="<%= request.getContextPath() %>/products" method="get">
                <input type="email" placeholder="Email address" aria-label="Email address" autocomplete="email">
                <button class="btn btn-primary" type="submit">Join</button>
            </form>
        </div>
    </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
