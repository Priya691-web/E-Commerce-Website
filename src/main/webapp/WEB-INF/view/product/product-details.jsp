<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 60px; margin-bottom: 100px; min-height: 70vh;">
    <div style="display: flex; gap: 60px; flex-wrap: wrap;">
        <!-- Left: Image Gallery -->
        <div style="flex: 1; min-width: 400px;">
            <div style="background-color: #f8f9fa; border-radius: 8px; overflow: hidden; height: 600px; display: flex; align-items: center; justify-content: center; position: sticky; top: 100px;">
                <c:choose>
                    <c:when test="${fn:startsWith(product.imageUrl, 'http')}">
                        <img src="${product.imageUrl}" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;">
                    </c:when>
                    <c:when test="${not empty product.imageUrl}">
                        <img src="${pageContext.request.contextPath}/${product.imageUrl}" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;">
                    </c:when>
                    <c:otherwise>
                        <img src="https://via.placeholder.com/600x800.png?text=${fn:replace(product.name, ' ', '+')}" alt="${product.name}" style="width: 100%; height: 100%; object-fit: cover;">
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        
        <!-- Right: Product Information -->
        <div style="flex: 1; min-width: 350px; padding: 20px 0;">
            <nav style="margin-bottom: 20px; font-size: 0.9rem; color: var(--text-muted);">
                <a href="${pageContext.request.contextPath}/home" style="color: inherit; text-decoration: none;">Home</a> / 
                <a href="${pageContext.request.contextPath}/product" style="color: inherit; text-decoration: none;">Products</a> / 
                <span style="color: var(--text-main); font-weight: 600;">${product.name}</span>
            </nav>
            
            <h1 style="font-size: 2.5rem; font-weight: 800; margin-bottom: 10px; color: var(--text-main); line-height: 1.2;">${product.name}</h1>
            <p style="color: var(--text-muted); font-size: 1.1rem; margin-bottom: 20px;">Premium Quality Collection</p>
            
            <div style="display: flex; align-items: center; gap: 15px; margin-bottom: 30px; border-bottom: 1px solid var(--border); padding-bottom: 30px;">
                <span style="color: var(--text-main); font-size: 2.2rem; font-weight: 800;">$${product.price}</span>
                <span style="background-color: rgba(37, 99, 235, 0.1); color: var(--primary); padding: 4px 12px; border-radius: 4px; font-size: 0.85rem; font-weight: 700; text-transform: uppercase;">In Stock</span>
            </div>
            
            <p style="color: var(--text-main); line-height: 1.8; margin-bottom: 40px; font-size: 1.05rem;">
                ${product.description}
            </p>
            
            <form action="${pageContext.request.contextPath}/cart" method="post">
                <input type="hidden" name="action" value="add">
                
                <div class="form-group" style="margin-bottom: 25px;">
                    <label class="form-label" style="font-weight: 700; text-transform: uppercase; letter-spacing: 1px; font-size: 0.85rem;">Select Size & Color</label>
                    <div style="position: relative;">
                        <select name="variantId" class="form-control" style="appearance: none; padding: 15px 20px; font-size: 1rem; border: 2px solid #e2e8f0; border-radius: 4px; background-color: #fff; cursor: pointer;" required>
                            <option value="" disabled selected>Choose an option</option>
                            <c:forEach var="variant" items="${variants}">
                                <option value="${variant.id}">Size: ${variant.size} | Color: ${variant.color} (${variant.stockQuantity} left)</option>
                            </c:forEach>
                        </select>
                        <div style="position: absolute; right: 20px; top: 50%; transform: translateY(-50%); pointer-events: none;">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="6 9 12 15 18 9"></polyline></svg>
                        </div>
                    </div>
                </div>
                
                <div style="display: flex; gap: 20px; align-items: flex-end; margin-bottom: 40px;">
                    <div class="form-group" style="width: 120px; margin-bottom: 0;">
                        <label class="form-label" style="font-weight: 700; text-transform: uppercase; letter-spacing: 1px; font-size: 0.85rem;">Qty</label>
                        <input type="number" name="quantity" value="1" min="1" class="form-control" style="padding: 15px; text-align: center; font-size: 1.1rem; border: 2px solid #e2e8f0; border-radius: 4px;" required>
                    </div>
                    <button type="submit" class="btn btn-primary" style="flex: 1; padding: 18px 30px; font-size: 1.1rem; font-weight: 700; border-radius: 4px; text-transform: uppercase; letter-spacing: 1px; display: flex; justify-content: center; align-items: center; gap: 10px;">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"></path><line x1="3" y1="6" x2="21" y2="6"></line><path d="M16 10a4 4 0 0 1-8 0"></path></svg>
                        Add to Cart
                    </button>
                </div>
            </form>
            
            <!-- Shipping & Return Info -->
            <div style="display: flex; flex-direction: column; gap: 15px; margin-top: 40px; padding-top: 30px; border-top: 1px solid var(--border);">
                <div style="display: flex; align-items: center; gap: 15px; color: var(--text-main);">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><rect x="1" y="3" width="15" height="13"></rect><polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon><circle cx="5.5" cy="18.5" r="2.5"></circle><circle cx="18.5" cy="18.5" r="2.5"></circle></svg>
                    <span style="font-size: 0.95rem;">Free standard shipping on orders over $100</span>
                </div>
                <div style="display: flex; align-items: center; gap: 15px; color: var(--text-main);">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"></polyline><polyline points="17 6 23 6 23 12"></polyline></svg>
                    <span style="font-size: 0.95rem;">Free 30-day returns policy</span>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
