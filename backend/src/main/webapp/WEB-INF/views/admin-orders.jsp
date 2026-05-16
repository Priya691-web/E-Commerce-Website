<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.fashionstore.model.Order" %>
<%@ page import="com.fashionstore.model.OrderItem" %>
<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Order Management");
    request.setAttribute("_pageCSS", "admin");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
<script src="<%= request.getContextPath() %>/assets/js/modules/admin-orders.js"></script>
</head>
<body class="admin-dashboard">

<div class="admin-layout">
    <button type="button" class="admin-menu-toggle" data-admin-menu-open aria-label="Open admin menu">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" aria-hidden="true"><line x1="3" y1="6" x2="21" y2="6"/><line x1="3" y1="12" x2="21" y2="12"/><line x1="3" y1="18" x2="21" y2="18"/></svg>
    </button>
    <div class="admin-sidebar-backdrop" data-admin-menu-close tabindex="-1" aria-hidden="true"></div>
    <!-- SIDEBAR -->
    <aside class="admin-sidebar">
        <div class="sidebar-brand">FashionStore Admin</div>
        <nav class="sidebar-nav">
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <rect x="3" y="3" width="7" height="7"/>
                    <rect x="14" y="3" width="7" height="7"/>
                    <rect x="14" y="14" width="7" height="7"/>
                    <rect x="3" y="14" width="7" height="7"/>
                </svg>
                Dashboard
            </a>
            <a href="<%= request.getContextPath() %>/admin/products" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/>
                    <line x1="7" y1="7" x2="7.01" y2="7"/>
                </svg>
                Products
            </a>
            <a href="<%= request.getContextPath() %>/admin/orders" class="sidebar-link active">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
                    <line x1="3" y1="6" x2="21" y2="6"/>
                    <path d="M16 10a4 4 0 0 1-8 0"/>
                </svg>
                Orders
            </a>
            <a href="<%= request.getContextPath() %>/admin/users" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                    <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                </svg>
                Users
            </a>
            <a href="<%= request.getContextPath() %>/products" class="sidebar-link">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M3 12l2-2m0 0l7-7 7 7M5 10v10a1 1 0 001 1h3m10-11l2 2m-2-2v10a1 1 0 01-1 1h-3m-6 0a1 1 0 001-1v-4a1 1 0 011-1h2a1 1 0 011 1v4a1 1 0 001 1m-6 0h6"/>
                </svg>
                Back to Store
            </a>
        </nav>
    </aside>

    <!-- MAIN CONTENT -->
    <main class="admin-content">
        <div class="admin-header">
            <h1 class="admin-title">Order Management</h1>
            <p class="admin-subtitle">Manage orders, shipments, and refunds</p>
        </div>

        <!-- ORDER FILTERING -->
        <div class="glass-card admin-toolbar-card">
            <div class="admin-filter-row">
                <div class="admin-filter-field admin-filter-field--grow">
                    <label class="admin-filter-label" for="searchOrders">Search Orders</label>
                    <input type="text" id="searchOrders" class="admin-filter-input" placeholder="Search by Order ID, Customer Name...">
                </div>
                <div class="admin-filter-field">
                    <label class="admin-filter-label" for="statusFilter">Status Filter</label>
                    <select id="statusFilter" class="admin-filter-select">
                        <option value="">All Status</option>
                        <option value="Pending">Pending</option>
                        <option value="Confirmed">Confirmed</option>
                        <option value="Processing">Processing</option>
                        <option value="Packing">Packing</option>
                        <option value="Shipped">Shipped</option>
                        <option value="Out for Delivery">Out for Delivery</option>
                        <option value="Delivered">Delivered</option>
                        <option value="Cancelled">Cancelled</option>
                        <option value="Refunded">Refunded</option>
                    </select>
                </div>
                <div class="admin-filter-field">
                    <label class="admin-filter-label" for="dateFilter">Date Range</label>
                    <select id="dateFilter" class="admin-filter-select">
                        <option value="">All Time</option>
                        <option value="7">Last 7 Days</option>
                        <option value="30">Last 30 Days</option>
                        <option value="90">Last 90 Days</option>
                    </select>
                </div>
            </div>
        </div>

        <!-- ALERTS -->
        <% String message = (String) session.getAttribute("message"); %>
        <% if (message != null) { %>
            <div class="alert alert-success admin-alert-spacing"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(message) %></div>
            <% session.removeAttribute("message"); %>
        <% } %>
        <% String error = (String) session.getAttribute("error"); %>
        <% if (error != null) { %>
            <div class="alert alert-danger admin-alert-spacing"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(error) %></div>
            <% session.removeAttribute("error"); %>
        <% } %>

        <!-- ORDERS LIST -->
        <%
            List<Order> orders = new ArrayList<>();
            Object obj = request.getAttribute("orders");
            if (obj instanceof List<?>) {
                @SuppressWarnings("unchecked")
                List<Order> temp = (List<Order>) obj;
                orders = temp;
            }
        %>

        <% if (orders.isEmpty()) { %>
            <div class="glass-card admin-empty-state">
                <p>No orders found.</p>
            </div>
        <% } %>

        <% for (Order order : orders) { %>
            <article class="glass-card admin-order-card">
                <div class="admin-order-card__header">
                    <div>
                        <div class="admin-order-card__title">Order #<%= order.getOrderId() %></div>
                        <div class="admin-order-card__meta">
                            <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getFullName() != null ? order.getFullName() : "User " + order.getUserId()) %> | 
                            Total: ₹<%= String.format("%.2f", order.getTotalAmount()) %> |
                            <%= order.getOrderDate() != null ? new java.text.SimpleDateFormat("MMM dd, yyyy").format(order.getOrderDate()) : "" %>
                        </div>
                    </div>
                    <div>
                        <%
                            String status = order.getStatus() == null ? "Pending" : order.getStatus();
                            String css = "status-pending";
                            if ("Confirmed".equalsIgnoreCase(status)) css = "status-confirmed";
                            if ("Processing".equalsIgnoreCase(status)) css = "status-processing";
                            if ("Packing".equalsIgnoreCase(status)) css = "status-packing";
                            if ("Shipped".equalsIgnoreCase(status)) css = "status-shipped";
                            if ("Out for Delivery".equalsIgnoreCase(status)) css = "status-out-for-delivery";
                            if ("Delivered".equalsIgnoreCase(status)) css = "status-delivered";
                            if ("Cancelled".equalsIgnoreCase(status)) css = "status-cancelled";
                            if ("Refunded".equalsIgnoreCase(status)) css = "status-refunded";
                        %>
                        <span class="status-badge <%= css %>"><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(status) %></span>
                    </div>
                </div>

                <!-- STATUS UPDATE & SHIPMENT SIMULATION -->
                <div class="admin-order-card__actions">
                    <form action="<%= request.getContextPath() %>/admin/orders" method="post" class="status-form">
                        <input type="hidden" name="csrfToken" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>">>
                        <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                        <label for="status-<%= order.getOrderId() %>" class="sr-only">Update order status</label>
                        <select name="status" id="status-<%= order.getOrderId() %>">
                            <option value="Pending" <%= "Pending".equalsIgnoreCase(status) ? "selected" : "" %>>Pending</option>
                            <option value="Confirmed" <%= "Confirmed".equalsIgnoreCase(status) ? "selected" : "" %>>Confirmed</option>
                            <option value="Processing" <%= "Processing".equalsIgnoreCase(status) ? "selected" : "" %>>Processing</option>
                            <option value="Packing" <%= "Packing".equalsIgnoreCase(status) ? "selected" : "" %>>Packing</option>
                            <option value="Shipped" <%= "Shipped".equalsIgnoreCase(status) ? "selected" : "" %>>Shipped</option>
                            <option value="Out for Delivery" <%= "Out for Delivery".equalsIgnoreCase(status) ? "selected" : "" %>>Out for Delivery</option>
                            <option value="Delivered" <%= "Delivered".equalsIgnoreCase(status) ? "selected" : "" %>>Delivered</option>
                            <option value="Cancelled" <%= "Cancelled".equalsIgnoreCase(status) ? "selected" : "" %>>Cancelled</option>
                            <option value="Refunded" <%= "Refunded".equalsIgnoreCase(status) ? "selected" : "" %>>Refunded</option>
                        </select>
                        <button type="submit" name="updateStatus" value="true">Update Status</button>
                        
                        <% if ("Shipped".equalsIgnoreCase(status)) { %>
                            <button type="submit" name="simulateDelivery" value="true">Simulate Delivery</button>
                        <% } %>
                        
                        <% if ("Delivered".equalsIgnoreCase(status)) { %>
                            <button type="submit" name="simulateRefund" value="true">Simulate Refund</button>
                        <% } %>
                    </form>
                </div>

                <!-- ORDER ITEMS -->
                <div class="items">
                    <% List<OrderItem> items = order.getItems(); %>
                    <% if (items != null && !items.isEmpty()) { %>
                        <% for (OrderItem item : items) { %>
                            <div class="item-row">
                                Product #<%= item.getProductId() %> | Size: <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(item.getSizeLabel() != null ? item.getSizeLabel() : "-") %> | Qty: <%= item.getQuantity() %> | ₹<%= String.format("%.2f", item.getPrice()) %>
                            </div>
                        <% } %>
                    <% } else { %>
                        <div class="item-row">No items found.</div>
                    <% } %>
                </div>

                <!-- CUSTOMER DETAILS -->
                <div class="admin-order-card__footer">
                    <div><strong>Shipping Address:</strong> <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getAddress() != null ? order.getAddress() : "-") %>, <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getCity() != null ? order.getCity() : "-") %>, <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getState() != null ? order.getState() : "-") %> - <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getZip() != null ? order.getZip() : "-") %></div>
                    <div class="admin-order-card__footer-row"><strong>Phone:</strong> <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getPhone() != null ? order.getPhone() : "-") %></div>
                    <div class="admin-order-card__footer-row"><strong>Payment Method:</strong> <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(order.getPaymentMethod() != null ? order.getPaymentMethod() : "-") %></div>
                </div>
            </article>
        <% } %>
    </main>
</div>

</body>
</html>
