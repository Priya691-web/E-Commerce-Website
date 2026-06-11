<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 80px; min-height: 50vh; text-align: center;">
    <div class="card" style="max-width: 600px; margin: 0 auto; padding: 60px 40px;">
        <div style="width: 80px; height: 80px; background-color: rgba(34, 197, 94, 0.1); color: var(--success); border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 30px; font-size: 2.5rem;">
            ✓
        </div>
        <h1 style="margin-bottom: 20px;">Order Confirmed!</h1>
        <p style="color: var(--text-muted); font-size: 1.1rem; margin-bottom: 40px;">
            Thank you for your purchase. Your order has been placed successfully and is now being processed.
        </p>
        <div style="display: flex; gap: 20px; justify-content: center;">
            <a href="${pageContext.request.contextPath}/order?action=my-orders" class="btn btn-outline">View My Orders</a>
            <a href="${pageContext.request.contextPath}/product" class="btn btn-primary">Continue Shopping</a>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
