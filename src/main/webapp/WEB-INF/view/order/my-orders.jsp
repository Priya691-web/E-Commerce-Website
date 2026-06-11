<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="margin-top: 60px; margin-bottom: 100px; min-height: 60vh;">
    <div style="display: flex; justify-content: space-between; align-items: flex-end; margin-bottom: 40px; border-bottom: 2px solid var(--surface-hover); padding-bottom: 20px;">
        <div>
            <h1 style="font-size: 2.5rem; font-weight: 800; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 5px;">Order History</h1>
            <p style="color: var(--text-muted); font-size: 1rem;">View and track your recent purchases</p>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div style="text-align: center; padding: 100px 20px;">
                <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="var(--text-muted)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom: 20px;"><path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"></path><polyline points="3.27 6.96 12 12.01 20.73 6.96"></polyline><line x1="12" y1="22.08" x2="12" y2="12"></line></svg>
                <h3 style="color: var(--text-main); margin-bottom: 15px; font-weight: 700;">No Orders Yet</h3>
                <p style="color: var(--text-muted); margin-bottom: 30px;">You haven't placed any orders. Start exploring our collection!</p>
                <a href="${pageContext.request.contextPath}/product" class="btn btn-primary" style="padding: 16px 40px; font-size: 1.1rem; text-transform: uppercase; letter-spacing: 1px; font-weight: 700;">Start Shopping</a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="card" style="border-radius: 8px; box-shadow: 0 10px 30px rgba(0,0,0,0.05); border: none; overflow: hidden;">
                <table style="width: 100%; border-collapse: collapse; text-align: left;">
                    <thead style="background-color: #f8f9fa; border-bottom: 2px solid var(--border);">
                        <tr>
                            <th style="padding: 20px; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Order ID</th>
                            <th style="padding: 20px; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Date Placed</th>
                            <th style="padding: 20px; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Total Amount</th>
                            <th style="padding: 20px; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Status</th>
                            <th style="padding: 20px; text-align: right; font-weight: 700; text-transform: uppercase; font-size: 0.85rem; letter-spacing: 1px; color: var(--text-muted);">Details</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr style="border-bottom: 1px solid var(--border); transition: background-color 0.2s;">
                                <td style="padding: 20px; font-weight: 700; color: var(--text-main);">#${order.id}</td>
                                <td style="padding: 20px; color: var(--text-muted);">${order.createdAt}</td>
                                <td style="padding: 20px; font-weight: 600; color: var(--text-main);">$${order.totalAmount}</td>
                                <td style="padding: 20px;">
                                    <span style="padding: 6px 14px; border-radius: 20px; font-size: 0.8rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.5px; background-color: rgba(16, 185, 129, 0.1); color: #10b981; border: 1px solid rgba(16, 185, 129, 0.2);">
                                        ${order.status}
                                    </span>
                                </td>
                                <td style="padding: 20px; text-align: right;">
                                    <a href="${pageContext.request.contextPath}/order?action=details&id=${order.id}" class="btn btn-outline" style="padding: 8px 20px; font-size: 0.9rem; font-weight: 600;">View Details</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
