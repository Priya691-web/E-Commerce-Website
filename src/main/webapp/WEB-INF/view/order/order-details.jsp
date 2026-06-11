<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 40px; min-height: 50vh;">
    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px;">
        <h2>Order #${order.id} Details</h2>
        <a href="${pageContext.request.contextPath}/order?action=my-orders" class="btn btn-outline">Back to Orders</a>
    </div>

    <div style="display: flex; gap: 40px; flex-wrap: wrap;">
        <div style="flex: 2; min-width: 300px;">
            <div class="card" style="padding: 30px; margin-bottom: 20px;">
                <h3 style="margin-bottom: 20px;">Order Items</h3>
                <table style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead style="background-color: var(--surface-hover);">
                        <tr>
                            <th style="padding: 12px 15px;">Item</th>
                            <th style="padding: 12px 15px;">Price</th>
                            <th style="padding: 12px 15px;">Qty</th>
                            <th style="padding: 12px 15px; text-align: right;">Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${orderItems}">
                            <tr style="border-top: 1px solid var(--border);">
                                <td style="padding: 12px 15px;">Variant #${item.productVariantId}</td>
                                <td style="padding: 12px 15px;">$${item.unitPrice}</td>
                                <td style="padding: 12px 15px;">${item.quantity}</td>
                                <td style="padding: 12px 15px; text-align: right;">$${item.unitPrice * item.quantity}</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div style="flex: 1; min-width: 300px;">
            <div class="card" style="padding: 30px;">
                <h3 style="margin-bottom: 20px;">Summary</h3>
                <div style="margin-bottom: 10px; display: flex; justify-content: space-between;">
                    <span style="color: var(--text-muted);">Status:</span>
                    <span style="font-weight: bold; color: var(--primary);">${order.status}</span>
                </div>
                <div style="margin-bottom: 10px; display: flex; justify-content: space-between;">
                    <span style="color: var(--text-muted);">Date:</span>
                    <span>${order.createdAt}</span>
                </div>
                <div style="margin-bottom: 20px; display: flex; justify-content: space-between;">
                    <span style="color: var(--text-muted);">Shipping:</span>
                    <span>${order.shippingAddress}</span>
                </div>
                <div style="border-top: 1px solid var(--border); padding-top: 15px; display: flex; justify-content: space-between; font-weight: bold; font-size: 1.2rem;">
                    <span>Total</span>
                    <span>$${order.totalAmount}</span>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
