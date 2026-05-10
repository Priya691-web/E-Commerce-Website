<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.fashionstore.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Address" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "My Profile");
    request.setAttribute("_pageCSS", "account");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    User user = (User) session.getAttribute("user");
    List<Address> addresses = (List<Address>) request.getAttribute("addresses");
    Address defaultShipping = (Address) request.getAttribute("defaultShipping");
    Address defaultBilling = (Address) request.getAttribute("defaultBilling");
    int addressCount = (request.getAttribute("addressCount") != null) ? (Integer) request.getAttribute("addressCount") : 0;
%>

<main class="account-page">
    <div class="container">
        <div class="account-header">
            <h1>My Account</h1>
            <p class="account-greeting">Welcome back, <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getFullName()) %></p>
        </div>

        <div class="account-layout">
            <!-- Sidebar Navigation -->
            <aside class="account-sidebar">
                <nav class="account-nav">
                    <a href="<%= request.getContextPath() %>/account/profile" class="account-nav-item active">
                        <span class="nav-icon">👤</span>
                        <span>Profile</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/account/profile/edit" class="account-nav-item">
                        <span class="nav-icon">✏️</span>
                        <span>Edit Profile</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/account/addresses" class="account-nav-item">
                        <span class="nav-icon">📍</span>
                        <span>Addresses</span>
                        <span class="nav-badge"><%= addressCount %></span>
                    </a>
                    <a href="<%= request.getContextPath() %>/account/profile/settings" class="account-nav-item">
                        <span class="nav-icon">⚙️</span>
                        <span>Settings</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/orders" class="account-nav-item">
                        <span class="nav-icon">📦</span>
                        <span>Orders</span>
                    </a>
                    <a href="<%= request.getContextPath() %>/wishlist" class="account-nav-item">
                        <span class="nav-icon">❤️</span>
                        <span>Wishlist</span>
                    </a>
                </nav>
            </aside>

            <!-- Main Content -->
            <div class="account-content">
                <!-- Profile Overview -->
                <section class="account-section">
                    <div class="section-header">
                        <h2>Profile Information</h2>
                        <a href="<%= request.getContextPath() %>/account/profile/edit" class="btn btn-outline btn-sm">Edit</a>
                    </div>

                    <div class="profile-overview">
                        <div class="profile-card">
                            <div class="profile-avatar">
                                <div class="avatar-placeholder">
                                    <span class="avatar-initials"><%= user.getFullName().substring(0, 1).toUpperCase() %></span>
                                </div>
                            </div>
                            <div class="profile-details">
                                <h3 class="profile-name"><%= user.getFullName() %></h3>
                                <p class="profile-email"><%= user.getEmail() %></p>
                                <p class="profile-phone"><%= user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Not provided" %></p>
                                <p class="profile-role">Role: <%= user.getRole() != null ? user.getRole().substring(0, 1).toUpperCase() + user.getRole().substring(1) : "Customer" %></p>
                            </div>
                        </div>
                    </div>
                </section>

                <!-- Default Addresses -->
                <section class="account-section">
                    <div class="section-header">
                        <h2>Default Addresses</h2>
                        <a href="<%= request.getContextPath() %>/account/addresses" class="btn btn-outline btn-sm">Manage</a>
                    </div>

                    <div class="addresses-grid">
                        <div class="address-card <%= defaultShipping != null ? "" : "empty" %>">
                            <div class="address-header">
                                <span class="address-type">Shipping</span>
                                <% if (defaultShipping != null) { %>
                                    <span class="address-badge badge-default">Default</span>
                                <% } %>
                            </div>
                            <% if (defaultShipping != null) { %>
                                <div class="address-body">
                                    <p class="address-name"><%= defaultShipping.getFullName() %></p>
                                    <p class="address-phone"><%= defaultShipping.getPhone() %></p>
                                    <p class="address-text"><%= defaultShipping.getAddressLine1() %></p>
                                    <% if (defaultShipping.getAddressLine2() != null && !defaultShipping.getAddressLine2().isEmpty()) { %>
                                        <p class="address-text"><%= defaultShipping.getAddressLine2() %></p>
                                    <% } %>
                                    <p class="address-location">
                                        <%= defaultShipping.getCity() %>, <%= defaultShipping.getState() %> <%= defaultShipping.getPostalCode() %>
                                    </p>
                                    <p class="address-country"><%= defaultShipping.getCountry() %></p>
                                </div>
                            <% } else { %>
                                <div class="address-empty">
                                    <p>No default shipping address</p>
                                    <a href="<%= request.getContextPath() %>/account/addresses/add" class="btn btn-primary btn-sm">Add Address</a>
                                </div>
                            <% } %>
                        </div>

                        <div class="address-card <%= defaultBilling != null ? "" : "empty" %>">
                            <div class="address-header">
                                <span class="address-type">Billing</span>
                                <% if (defaultBilling != null) { %>
                                    <span class="address-badge badge-default">Default</span>
                                <% } %>
                            </div>
                            <% if (defaultBilling != null) { %>
                                <div class="address-body">
                                    <p class="address-name"><%= defaultBilling.getFullName() %></p>
                                    <p class="address-phone"><%= defaultBilling.getPhone() %></p>
                                    <p class="address-text"><%= defaultBilling.getAddressLine1() %></p>
                                    <% if (defaultBilling.getAddressLine2() != null && !defaultBilling.getAddressLine2().isEmpty()) { %>
                                        <p class="address-text"><%= defaultBilling.getAddressLine2() %></p>
                                    <% } %>
                                    <p class="address-location">
                                        <%= defaultBilling.getCity() %>, <%= defaultBilling.getState() %> <%= defaultBilling.getPostalCode() %>
                                    </p>
                                    <p class="address-country"><%= defaultBilling.getCountry() %></p>
                                </div>
                            <% } else { %>
                                <div class="address-empty">
                                    <p>No default billing address</p>
                                    <a href="<%= request.getContextPath() %>/account/addresses/add" class="btn btn-primary btn-sm">Add Address</a>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </section>

                <!-- Account Stats -->
                <section class="account-section">
                    <div class="section-header">
                        <h2>Account Statistics</h2>
                    </div>

                    <div class="stats-grid">
                        <div class="stat-card">
                            <div class="stat-icon">📍</div>
                            <div class="stat-info">
                                <span class="stat-value"><%= addressCount %></span>
                                <span class="stat-label">Saved Addresses</span>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon">📦</div>
                            <div class="stat-info">
                                <span class="stat-value">0</span>
                                <span class="stat-label">Total Orders</span>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon">❤️</div>
                            <div class="stat-info">
                                <span class="stat-value">0</span>
                                <span class="stat-label">Wishlist Items</span>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
