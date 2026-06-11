<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 60px; margin-bottom: 100px; min-height: 60vh;">
    <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 40px; border-bottom: 2px solid var(--surface-hover); padding-bottom: 20px;">
        <div>
            <h1 style="font-size: 2.5rem; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 5px;">Collection</h1>
            <p style="color: var(--text-muted); font-size: 1rem;">Showing ${fn:length(products)} premium styles</p>
        </div>
        <div>
            <form action="${pageContext.request.contextPath}/product" method="get" style="display: flex; gap: 15px; align-items: center;">
                <select name="categoryId" class="form-control" style="width: 250px; padding: 12px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0; font-weight: 600;" onchange="this.form.submit()">
                    <option value="">All Categories</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.id}" ${param.categoryId == category.id ? 'selected' : ''}>${category.name}</option>
                    </c:forEach>
                </select>
                <noscript><button type="submit" class="btn btn-primary">Filter</button></noscript>
            </form>
        </div>
    </div>
    
    <c:choose>
        <c:when test="${empty products}">
            <div style="text-align: center; padding: 100px 20px;">
                <h3 style="color: var(--text-muted); margin-bottom: 20px;">No products found in this category.</h3>
                <a href="${pageContext.request.contextPath}/product" class="btn btn-outline" style="padding: 12px 30px;">Clear Filter</a>
            </div>
        </c:when>
        <c:otherwise>
            <div style="display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 30px;">
                <c:forEach var="product" items="${products}">
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
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
