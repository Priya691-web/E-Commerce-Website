<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 60px; margin-bottom: 100px; min-height: 60vh;">
    <h1 style="font-size: 2.5rem; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 40px; border-bottom: 2px solid var(--surface-hover); padding-bottom: 20px;">Secure Checkout</h1>
    
    <c:choose>
        <c:when test="${empty cartItems}">
            <div style="text-align: center; padding: 100px 20px;">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom: 20px;"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                <h3 style="color: var(--text-main); margin-bottom: 15px; font-weight: 700;">Checkout Unavailable</h3>
                <p style="color: var(--text-muted); margin-bottom: 30px;">You cannot proceed to checkout with an empty bag.</p>
                <a href="${pageContext.request.contextPath}/product" class="btn btn-primary" style="padding: 16px 40px; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 1px; font-weight: 700;">Return to Shop</a>
            </div>
        </c:when>
        <c:otherwise>
            <div style="display: flex; gap: 40px; flex-wrap: wrap;">
                <div style="flex: 2; min-width: 350px;">
                    <div class="card" style="padding: 40px; border-radius: 8px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: none;">
                        <div style="display: flex; align-items: center; gap: 15px; margin-bottom: 30px;">
                            <div style="background-color: var(--text-main); color: #fff; width: 32px; height: 32px; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-weight: 800;">1</div>
                            <h3 style="margin: 0; font-weight: 800; text-transform: uppercase; letter-spacing: 1px;">Shipping Details</h3>
                        </div>
                        
                        <form action="${pageContext.request.contextPath}/order" method="post">
                            <div class="form-group" style="margin-bottom: 25px;">
                                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Full Name</label>
                                <input type="text" class="form-control" value="${sessionScope.user.firstName} ${sessionScope.user.lastName}" style="padding: 14px; border-radius: 4px; background-color: #f1f5f9; border: 1px solid #e2e8f0; color: #64748b;" disabled>
                            </div>
                            <div class="form-group" style="margin-bottom: 30px;">
                                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Delivery Address</label>
                                <textarea name="shippingAddress" class="form-control" rows="4" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0; resize: vertical;" required placeholder="Enter your full shipping address...">${sessionScope.user.address}</textarea>
                            </div>
                            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 18px; font-size: 1.1rem; font-weight: 800; border-radius: 4px; text-transform: uppercase; letter-spacing: 1px; box-shadow: 0 10px 20px rgba(255, 62, 108, 0.3);">Confirm & Place Order</button>
                        </form>
                    </div>
                </div>
                
                <div style="flex: 1; min-width: 300px;">
                    <div class="card" style="padding: 40px; border-radius: 8px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: none; background-color: #f8f9fa;">
                        <h3 style="margin-bottom: 30px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; border-bottom: 2px solid var(--border); padding-bottom: 15px;">Order Summary</h3>
                        
                        <div style="margin-bottom: 30px;">
                            <c:forEach var="item" items="${cartItems}">
                                <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px dashed var(--border);">
                                    <div style="display: flex; flex-direction: column;">
                                        <span style="font-weight: 700; color: var(--text-main);">Variant #${item.productVariantId}</span>
                                        <span style="font-size: 0.85rem; color: var(--text-muted);">Qty: ${item.quantity}</span>
                                    </div>
                                    <span style="font-weight: 600; color: var(--text-muted);">-</span>
                                </div>
                            </c:forEach>
                        </div>
                        
                        <div style="display: flex; justify-content: space-between; margin-bottom: 15px; color: var(--text-muted); font-size: 0.95rem;">
                            <span>Subtotal</span>
                            <span>Calculated at checkout</span>
                        </div>
                        <div style="display: flex; justify-content: space-between; margin-bottom: 20px; color: var(--text-muted); font-size: 0.95rem;">
                            <span>Shipping</span>
                            <span style="color: #10b981; font-weight: 600;">Free</span>
                        </div>
                        
                        <div style="border-top: 2px solid var(--border); padding-top: 20px; display: flex; justify-content: space-between; align-items: center;">
                            <span style="font-weight: 800; text-transform: uppercase; letter-spacing: 1px;">Total</span>
                            <span style="color: var(--primary); font-size: 1.5rem; font-weight: 800;">$100.00</span>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
