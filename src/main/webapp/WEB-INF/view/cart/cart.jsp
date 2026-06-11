<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 60px; margin-bottom: 100px; min-height: 60vh;">
    <h1 style="font-size: 2.5rem; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 40px; border-bottom: 2px solid var(--surface-hover); padding-bottom: 20px;">Shopping Bag</h1>
    
    <c:if test="${param.error == 'true'}">
        <div style="background-color: rgba(239, 68, 68, 0.1); color: var(--danger); padding: 14px; border-radius: 4px; margin-bottom: 25px; font-size: 0.9rem; text-align: center;">
            An error occurred during checkout. Please try again.
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty cartItems}">
            <div style="text-align: center; padding: 100px 20px;">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom: 20px;"><circle cx="9" cy="21" r="1"></circle><circle cx="20" cy="21" r="1"></circle><path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path></svg>
                <h3 style="color: var(--text-main); margin-bottom: 15px; font-weight: 700;">Your bag is currently empty.</h3>
                <p style="color: var(--text-muted); margin-bottom: 30px;">Must add items on the cart before you proceed to checkout.</p>
                <a href="${pageContext.request.contextPath}/product" class="btn btn-primary" style="padding: 16px 40px; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 1px; font-weight: 700;">Return to Shop</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card" style="border-radius: 8px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: none; overflow: hidden;">
                <table style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead style="background-color: #f8f9fa; border-bottom: 2px solid var(--border);">
                        <tr>
                            <th style="padding: 20px; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Product Variant</th>
                            <th style="padding: 20px; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Quantity</th>
                            <th style="padding: 20px; text-align: right; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${cartItems}">
                            <tr style="border-bottom: 1px solid var(--border); transition: background-color 0.2s;">
                                <td style="padding: 25px 20px;">
                                    <div style="display: flex; align-items: center; gap: 15px;">
                                        <div style="width: 60px; height: 80px; background-color: #f1f5f9; border-radius: 4px; display: flex; align-items: center; justify-content: center;">
                                            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#cbd5e1" stroke-width="2"><rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect><circle cx="8.5" cy="8.5" r="1.5"></circle><polyline points="21 15 16 10 5 21"></polyline></svg>
                                        </div>
                                        <div>
                                            <p style="font-weight: 700; color: var(--text-main); margin-bottom: 5px;">Variant #${item.productVariantId}</p>
                                        </div>
                                    </div>
                                </td>
                                <td style="padding: 25px 20px; font-weight: 600;">${item.quantity}</td>
                                <td style="padding: 25px 20px; text-align: right;">
                                    <form action="${pageContext.request.contextPath}/cart" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="remove">
                                        <input type="hidden" name="cartItemId" value="${item.id}">
                                        <button type="submit" class="btn" style="background-color: transparent; color: var(--text-muted); padding: 8px; border-radius: 50%;" title="Remove Item" onmouseover="this.style.color='var(--danger)'; this.style.backgroundColor='rgba(239, 68, 68, 0.1)';" onmouseout="this.style.color='var(--text-muted)'; this.style.backgroundColor='transparent';">
                                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path></svg>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 40px;">
                <a href="${pageContext.request.contextPath}/product" style="color: var(--text-muted); text-decoration: none; font-weight: 600; display: flex; align-items: center; gap: 8px;">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="19" y1="12" x2="5" y2="12"></line><polyline points="12 19 5 12 12 5"></polyline></svg>
                    Continue Shopping
                </a>
                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary" style="font-size: 1.1rem; padding: 18px 45px; text-transform: uppercase; font-weight: 700; letter-spacing: 1px; box-shadow: 0 10px 20px rgba(255, 62, 108, 0.3);">Proceed to Checkout</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
