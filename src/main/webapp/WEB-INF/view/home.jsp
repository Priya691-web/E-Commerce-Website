<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<!-- Hero Banner Section -->
<section class="hero" style="background: linear-gradient(to right, rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.1)), url('${pageContext.request.contextPath}/assets/images/banner.jpg') center/cover no-repeat; height: 75vh; display: flex; align-items: center; justify-content: flex-start; margin-bottom: 60px;">
    <div class="container" style="padding-left: 60px;">
        <h1 class="hero-heading" style="font-size: 4.5rem; font-weight: 800; letter-spacing: 2px; text-transform: uppercase; color: #fff; line-height: 1.1; margin-bottom: 24px;">Elevate<br>Your Style</h1>
        <p style="font-size: 1.25rem; font-weight: 400; color: #f8f9fa; max-width: 500px; margin-bottom: 40px;">Discover the new arrivals from top premium brands. Unveil your true aesthetic.</p>
        <a href="${pageContext.request.contextPath}/product" class="btn btn-primary" style="padding: 16px 48px; font-size: 1.1rem;">Explore Now</a>
    </div>
</section>

<div class="container" style="margin-bottom: 80px;">
    <!-- Shop By Category Section -->
    <div style="text-align: center; margin-bottom: 40px;">
        <h2 style="font-size: 2.2rem; text-transform: uppercase; letter-spacing: 2px; font-weight: 800; color: var(--text-main);">Shop By Category</h2>
        <div style="width: 60px; height: 3px; background-color: var(--primary); margin: 15px auto 0;"></div>
    </div>
    
    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(350px, 1fr)); gap: 30px; margin-bottom: 100px;">
        <c:forEach var="category" items="${categories}">
            <a href="${pageContext.request.contextPath}/product?categoryId=${category.id}">
                <div class="category-card">
                    <c:choose>
                        <c:when test="${fn:startsWith(category.imageUrl, 'http')}">
                            <img src="${category.imageUrl}" alt="${category.name}">
                        </c:when>
                        <c:when test="${not empty category.imageUrl}">
                            <img src="${pageContext.request.contextPath}/${category.imageUrl}" alt="${category.name}">
                        </c:when>
                        <c:otherwise>
                            <img src="https://via.placeholder.com/600x800.png?text=Category+${category.name}" alt="${category.name}">
                        </c:otherwise>
                    </c:choose>
                    <div class="category-overlay">
                        <h3 class="category-title">${category.name}</h3>
                        <p style="color: rgba(255,255,255,0.8); font-size: 1rem; font-weight: 400;">Explore ${category.name}</p>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>

    <!-- Featured Products Section -->
    <div style="text-align: center; margin-bottom: 40px;">
        <h2 style="font-size: 2.2rem; text-transform: uppercase; letter-spacing: 2px; font-weight: 800; color: var(--text-main);">Trending Styles</h2>
        <div style="width: 60px; height: 3px; background-color: var(--primary); margin: 15px auto 0;"></div>
    </div>
    
    <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 30px; margin-bottom: 50px;">
        <!-- Show only 8 latest products -->
        <c:forEach var="product" items="${featuredProducts}" end="7">
            <a href="${pageContext.request.contextPath}/product?action=details&id=${product.id}" style="text-decoration: none; display: block;">
                <div class="product-card">
                    <div class="product-image-container">
                        <c:choose>
                            <c:when test="${fn:startsWith(product.imageUrl, 'http')}">
                                <img src="${product.imageUrl}" alt="${product.name}" class="product-img">
                            </c:when>
                            <c:when test="${not empty product.imageUrl}">
                                <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}" class="product-img">
                            </c:when>
                            <c:otherwise>
                                <img src="https://via.placeholder.com/500x600.png?text=${fn:replace(product.name, ' ', '+')}" alt="${product.name}" class="product-img">
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="product-details">
                        <h3 class="product-brand">${product.name}</h3>
                        <p class="product-title">${product.description}</p>
                        <div class="product-price-row">
                            <span class="product-price">$${product.price}</span>
                        </div>
                        <div style="margin-top: 16px;">
                            <button class="btn btn-outline" style="width: 100%; padding: 10px; font-size: 0.9rem;">View Details</button>
                        </div>
                    </div>
                </div>
            </a>
        </c:forEach>
    </div>
    
    <div style="text-align: center;">
        <a href="${pageContext.request.contextPath}/product" class="btn btn-primary" style="padding: 16px 48px; font-size: 1.1rem;">View All Products</a>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
