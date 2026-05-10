<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.fashionstore.model.Order" %>
<%@ page import="com.fashionstore.model.OrderItem" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "My Orders");
    request.setAttribute("_pageCSS", "orders");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body>

<!-- NAVBAR -->
<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    List<Order> orders = new ArrayList<>();

    Object obj = request.getAttribute("orders");

    if (obj != null && obj instanceof List<?>) {
        @SuppressWarnings("unchecked")
        List<Order> temp = (List<Order>) obj;
        orders = temp;
    }
%>

<main class="site-main orders-wrap">
    <div class="orders-head">
        <span class="eyebrow">Your Account</span>
        <h1>My Orders</h1>
        <p>Track your latest purchases and delivery progress in real-time.</p>
    </div>

    <% if (orders != null && !orders.isEmpty()) { %>

        <% for (Order order : orders) { %>

            <article class="order-card card-surface">
                <div class="order-header">
                    <div class="order-info">
                        <h3 class="order-number">Order #<%= order.getOrderId() %></h3>
                        <p class="order-date">Placed on <%= order.getOrderDate() != null ? new java.text.SimpleDateFormat("MMM dd, yyyy").format(order.getOrderDate()) : "Recently" %></p>
                    </div>
                <%
                    String status = (order.getStatus() == null || order.getStatus().isBlank()) ? "Pending" : order.getStatus();
                    String badgeClass = "badge-pending";
                    String statusIcon = "⏳";
                    if ("Shipped".equalsIgnoreCase(status)) {
                        badgeClass = "badge-shipped";
                        statusIcon = "🚚";
                    } else if ("Delivered".equalsIgnoreCase(status)) {
                        badgeClass = "badge-delivered";
                        statusIcon = "✓";
                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                        badgeClass = "badge-cancelled";
                        statusIcon = "✕";
                    } else if ("Packed".equalsIgnoreCase(status)) {
                        badgeClass = "badge-packed";
                        statusIcon = "📦";
                    }
                %>
                    <div class="order-status">
                        <span class="status-badge <%= badgeClass %>">
                            <span class="status-icon"><%= statusIcon %></span>
                            <%= status %>
                        </span>
                    </div>
                </div>

                <div class="order-summary">
                    <div class="order-total">
                        <span class="order-total-label">Total Amount</span>
                        <span class="order-total-value">₹<%= String.format("%.2f", order.getTotalAmount()) %></span>
                    </div>
                    <div class="order-items-count">
                        <span class="items-count-label"><%= order.getItems() != null ? order.getItems().size() : 0 %> items</span>
                    </div>
                </div>

                <div class="order-timeline" aria-label="Order tracking timeline">
                    <div class="timeline-step <%= "done" %>">
                        <div class="timeline-marker"></div>
                        <span class="timeline-label">Processing</span>
                    </div>
                    <div class="timeline-connector <%= "Packed".equalsIgnoreCase(status) || "Shipped".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status) ? "done" : "" %>"></div>
                    <div class="timeline-step <%= "Packed".equalsIgnoreCase(status) || "Shipped".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status) ? "done" : "" %>">
                        <div class="timeline-marker"></div>
                        <span class="timeline-label">Packed</span>
                    </div>
                    <div class="timeline-connector <%= "Shipped".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status) ? "done" : "" %>"></div>
                    <div class="timeline-step <%= "Shipped".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status) ? "done" : "" %>">
                        <div class="timeline-marker"></div>
                        <span class="timeline-label">Shipped</span>
                    </div>
                    <div class="timeline-connector <%= "Delivered".equalsIgnoreCase(status) ? "done" : "" %>"></div>
                    <div class="timeline-step <%= "Delivered".equalsIgnoreCase(status) ? "done" : "" %>">
                        <div class="timeline-marker"></div>
                        <span class="timeline-label">Delivered</span>
                    </div>
                </div>

                <div class="order-items-preview">
                    <h4 class="items-preview-title">Items in this order</h4>
                    <%
                        List<OrderItem> items = order.getItems();
                    %>

                    <% if (items != null && !items.isEmpty()) { %>
                        <div class="items-list">
                        <% for (OrderItem item : items) { %>
                            <div class="order-item-row">
                                <div class="item-details">
                                    <span class="item-id">Product #<%= item.getProductId() %></span>
                                    <span class="item-quantity">Qty: <%= item.getQuantity() %></span>
                                </div>
                                <span class="item-price">₹<%= String.format("%.2f", item.getPrice()) %></span>
                            </div>
                        <% } %>
                        </div>
                    <% } else { %>
                        <p class="no-items">No items found for this order</p>
                    <% } %>
                </div>

                <div class="order-actions">
                    <a class="btn btn-outline" href="<%= request.getContextPath() %>/products">Shop Again</a>
                    <% if ("Delivered".equalsIgnoreCase(status) || "Shipped".equalsIgnoreCase(status)) { %>
                        <button class="btn btn-secondary" type="button" onclick="FashionStore.showToast('Invoice downloaded', 'success')">Download Invoice</button>
                    <% } else { %>
                        <button class="btn btn-secondary" type="button" onclick="FashionStore.showToast('Invoice will be available after shipment', 'info')">Invoice</button>
                    <% } %>
                </div>

            </article>

        <% } %>

    <% } else { %>

        <div class="orders-empty card-surface">
            <div class="empty-state-icon">📦</div>
            <h3 class="empty-state-title">No orders yet</h3>
            <p class="empty-state-description">Start shopping to track your orders here.</p>
            <a href="<%= request.getContextPath() %>/products" class="btn btn-primary">Start Shopping</a>
        </div>

    <% } %>

</main>

<!-- FOOTER -->
<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
