<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Admin Dashboard");
    request.setAttribute("_pageCSS", "admin");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
<script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
<script src="<%= request.getContextPath() %>/assets/js/modules/admin-dashboard.js"></script>
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
            <a href="<%= request.getContextPath() %>/admin/dashboard" class="sidebar-link active">
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
            <a href="<%= request.getContextPath() %>/admin/orders" class="sidebar-link">
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
        <!-- ANALYTICS CARDS -->
        <div class="dashboard-grid">
            <div class="stat-card">
                <div class="stat-icon sales">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                        <line x1="12" y1="1" x2="12" y2="23"/>
                        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>
                    </svg>
                </div>
                <div class="stat-value">₹<%= StringEscapeUtils.escapeHtml4((String) (request.getAttribute("totalSales") != null ? request.getAttribute("totalSales") : "0")) %></div>
                <div class="stat-label">Total Sales</div>
                <div class="stat-change positive">↑ 12% from last month</div>
            </div>

            <div class="stat-card">
                <div class="stat-icon users">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                        <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
                        <circle cx="9" cy="7" r="4"/>
                        <path d="M23 21v-2a4 4 0 0 0-3-3.87"/>
                        <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                </div>
                <div class="stat-value"><%= StringEscapeUtils.escapeHtml4(String.valueOf(request.getAttribute("totalUsers") != null ? request.getAttribute("totalUsers") : "0")) %></div>
                <div class="stat-label">Total Users</div>
                <div class="stat-change positive">↑ 8% from last month</div>
            </div>

            <div class="stat-card">
                <div class="stat-icon orders">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                        <path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/>
                        <line x1="3" y1="6" x2="21" y2="6"/>
                        <path d="M16 10a4 4 0 0 1-8 0"/>
                    </svg>
                </div>
                <div class="stat-value"><%= StringEscapeUtils.escapeHtml4(String.valueOf(request.getAttribute("totalOrders") != null ? request.getAttribute("totalOrders") : "0")) %></div>
                <div class="stat-label">Total Orders</div>
                <div class="stat-change positive">↑ 15% from last month</div>
            </div>

            <div class="stat-card">
                <div class="stat-icon stock">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" width="24" height="24">
                        <path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/>
                        <line x1="12" y1="9" x2="12" y2="13"/>
                        <line x1="12" y1="17" x2="12.01" y2="17"/>
                    </svg>
                </div>
                <div class="stat-value"><%= StringEscapeUtils.escapeHtml4(String.valueOf(request.getAttribute("lowStockCount") != null ? request.getAttribute("lowStockCount") : "0")) %></div>
                <div class="stat-label">Low Stock Alerts</div>
                <div class="stat-change negative">⚠ Needs attention</div>
            </div>
        </div>

        <!-- REVENUE CHART -->
        <div class="chart-container">
            <div class="chart-header">
                <h3 class="chart-title">Revenue Overview</h3>
                <select id="chartPeriod" class="admin-select">
                    <option value="7">Last 7 Days</option>
                    <option value="30" selected>Last 30 Days</option>
                    <option value="90">Last 90 Days</option>
                </select>
            </div>
            <canvas id="revenueChart" class="chart-canvas"></canvas>
        </div>

        <!-- RECENT ORDERS -->
        <div class="recent-orders">
            <div class="chart-header">
                <h3 class="chart-title">Recent Orders</h3>
                <a href="<%= request.getContextPath() %>/admin/orders" class="add-btn add-btn--compact">View All</a>
            </div>
            <div class="admin-table-container">
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Order ID</th>
                            <th>Customer</th>
                            <th>Amount</th>
                            <th>Status</th>
                            <th>Date</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (request.getAttribute("recentOrders") != null) { 
                           java.util.List<com.fashionstore.model.Order> orders = (java.util.List<com.fashionstore.model.Order>) request.getAttribute("recentOrders");
                           for (com.fashionstore.model.Order order : orders) { %>
                        <tr>
                            <td>#<%= StringEscapeUtils.escapeHtml4(String.valueOf(order.getOrderId())) %></td>
                            <td><%= StringEscapeUtils.escapeHtml4(order.getFullName() != null ? order.getFullName() : "Guest") %></td>
                            <td>₹<%= String.format("%.2f", order.getTotalAmount()) %></td>
                            <td>
                                <span class="status-badge status-<%= order.getStatus() != null ? order.getStatus().toLowerCase() : "pending" %>">
                                    <%= StringEscapeUtils.escapeHtml4(order.getStatus() != null ? order.getStatus() : "Pending") %>
                                </span>
                            </td>
                            <td><%= order.getOrderDate() != null ? new java.text.SimpleDateFormat("MMM dd, yyyy").format(order.getOrderDate()) : "N/A" %></td>
                        </tr>
                        <% } } else { %>
                        <tr>
                            <td colspan="5" class="admin-table-empty">No recent orders</td>
                        </tr>
                        <% } %>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</div>

<!-- Hidden elements for chart data -->
<input type="hidden" id="revenue-data" value='<%= request.getAttribute("revenueDataJson") != null ? request.getAttribute("revenueDataJson") : "[]" %>'>
<input type="hidden" id="revenue-labels" value='<%= request.getAttribute("revenueLabelsJson") != null ? request.getAttribute("revenueLabelsJson") : "[]" %>'>

<script>
(function() {
    const revenueDataEl = document.getElementById('revenue-data');
    const revenueLabelsEl = document.getElementById('revenue-labels');
    
    let chartData = [];
    let labels = [];
    
    try {
        if (revenueDataEl && revenueDataEl.value) {
            chartData = JSON.parse(revenueDataEl.value);
        }
    } catch (e) {
        console.error('Failed to parse revenue data:', e);
        chartData = [];
    }
    
    try {
        if (revenueLabelsEl && revenueLabelsEl.value) {
            labels = JSON.parse(revenueLabelsEl.value);
        }
    } catch (e) {
        console.error('Failed to parse revenue labels:', e);
        labels = [];
    }
    
    const ctx = document.getElementById('revenueChart');
    if (ctx && chartData.length > 0 && labels.length > 0) {
        new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Revenue',
                    data: chartData,
                    borderColor: '#2a2926',
                    backgroundColor: 'rgba(42, 41, 38, 0.06)',
                    fill: true,
                    tension: 0.4,
                    borderWidth: 2,
                    pointBackgroundColor: '#121211',
                    pointBorderColor: '#ffffff',
                    pointBorderWidth: 2,
                    pointRadius: 4
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: {
                            color: 'rgba(0, 0, 0, 0.05)'
                        },
                        ticks: {
                            callback: function(value) {
                                return '₹' + value;
                            }
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    }
})();
</script>

</body>
</html>
