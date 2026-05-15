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
    User user = (User) session.getAttribute("customerAuth");
    List<Address> addresses = (List<Address>) request.getAttribute("addresses");
    Address defaultShipping = (Address) request.getAttribute("defaultShipping");
    Address defaultBilling = (Address) request.getAttribute("defaultBilling");
    int addressCount = (request.getAttribute("addressCount") != null) ? (Integer) request.getAttribute("addressCount") : 0;

    String profileInitials = "U";
    if (user != null && user.getFullName() != null && !user.getFullName().isBlank()) {
        String[] parts = user.getFullName().trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(parts.length, 2); i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase());
        }
        profileInitials = sb.toString();
    }
%>

<main class="shell section-block" id="main-content">
    <div class="fs-account-header">
        <h1 class="editorial-heading">My Account</h1>
        <p class="text-secondary">Welcome back, <%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(user.getFullName()) %></p>
    </div>

    <div class="fs-account-layout">
        <jsp:include page="/WEB-INF/views/partials/account-sidebar.jsp" />

        <div class="fs-account-content">
            <section class="fs-account-section">
                <div class="fs-section-header">
                    <h2 class="editorial-heading">Profile Information</h2>
                    <a href="<%= request.getContextPath() %>/account/profile/edit" class="fs-btn fs-btn--primary fs-btn--sm">Edit</a>
                </div>

                <div class="fs-profile-card">
                    <div class="fs-profile-card__avatar">
                        <div class="fs-avatar">
                            <span><%= profileInitials %></span>
                        </div>
                    </div>
                    <div class="fs-profile-card__details">
                        <h3><%= user.getFullName() %></h3>
                        <p class="text-secondary"><%= user.getEmail() %></p>
                        <p class="text-secondary"><%= user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Not provided" %></p>
                        <p class="text-sm text-secondary">Role: <%= user.getRole() != null ? user.getRole().substring(0, 1).toUpperCase() + user.getRole().substring(1) : "Customer" %></p>
                    </div>
                </div>
            </section>

            <section class="fs-account-section">
                <div class="fs-section-header">
                    <h2 class="editorial-heading">Default Addresses</h2>
                    <a href="<%= request.getContextPath() %>/account/addresses" class="fs-btn fs-btn--primary fs-btn--sm">Manage</a>
                </div>

                <div class="fs-addresses-grid">
                    <div class="fs-address-card <%= defaultShipping != null ? "" : "fs-address-card--empty" %>">
                        <div class="fs-address-card__header">
                            <span class="fs-address-card__type">Shipping</span>
                            <% if (defaultShipping != null) { %>
                                <span class="fs-badge">Default</span>
                            <% } %>
                        </div>
                        <% if (defaultShipping != null) { %>
                            <div class="fs-address-card__body">
                                <p><%= defaultShipping.getFullName() %></p>
                                <p class="text-secondary"><%= defaultShipping.getPhone() %></p>
                                <p class="text-secondary"><%= defaultShipping.getAddressLine1() %></p>
                                <% if (defaultShipping.getAddressLine2() != null && !defaultShipping.getAddressLine2().isEmpty()) { %>
                                    <p class="text-secondary"><%= defaultShipping.getAddressLine2() %></p>
                                <% } %>
                                <p class="text-secondary">
                                    <%= defaultShipping.getCity() %>, <%= defaultShipping.getState() %> <%= defaultShipping.getPostalCode() %>
                                </p>
                                <p class="text-secondary"><%= defaultShipping.getCountry() %></p>
                            </div>
                        <% } else { %>
                            <div class="fs-address-card__empty">
                                <p class="text-secondary">No default shipping address</p>
                                <a href="<%= request.getContextPath() %>/account/addresses/add" class="fs-btn fs-btn--primary fs-btn--sm">Add Address</a>
                            </div>
                        <% } %>
                    </div>

                    <div class="fs-address-card <%= defaultBilling != null ? "" : "fs-address-card--empty" %>">
                        <div class="fs-address-card__header">
                            <span class="fs-address-card__type">Billing</span>
                            <% if (defaultBilling != null) { %>
                                <span class="fs-badge">Default</span>
                            <% } %>
                        </div>
                        <% if (defaultBilling != null) { %>
                            <div class="fs-address-card__body">
                                <p><%= defaultBilling.getFullName() %></p>
                                <p class="text-secondary"><%= defaultBilling.getPhone() %></p>
                                <p class="text-secondary"><%= defaultBilling.getAddressLine1() %></p>
                                <% if (defaultBilling.getAddressLine2() != null && !defaultBilling.getAddressLine2().isEmpty()) { %>
                                    <p class="text-secondary"><%= defaultBilling.getAddressLine2() %></p>
                                <% } %>
                                <p class="text-secondary">
                                    <%= defaultBilling.getCity() %>, <%= defaultBilling.getState() %> <%= defaultBilling.getPostalCode() %>
                                </p>
                                <p class="text-secondary"><%= defaultBilling.getCountry() %></p>
                            </div>
                        <% } else { %>
                            <div class="fs-address-card__empty">
                                <p class="text-secondary">No default billing address</p>
                                <a href="<%= request.getContextPath() %>/account/addresses/add" class="fs-btn fs-btn--primary fs-btn--sm">Add Address</a>
                            </div>
                        <% } %>
                    </div>
                </div>
            </section>

            <section class="fs-account-section">
                <div class="fs-section-header">
                    <h2 class="editorial-heading">Account Statistics</h2>
                </div>

                <div class="fs-stats-grid">
                    <div class="fs-stat-card">
                        <div class="fs-stat-card__icon">📍</div>
                        <div class="fs-stat-card__info">
                            <span><%= addressCount %></span>
                            <span class="text-secondary">Saved Addresses</span>
                        </div>
                    </div>
                    <div class="fs-stat-card">
                        <div class="fs-stat-card__icon">📦</div>
                        <div class="fs-stat-card__info">
                            <span><%= request.getAttribute("orderCount") != null ? request.getAttribute("orderCount") : 0 %></span>
                            <span class="text-secondary">Total Orders</span>
                        </div>
                    </div>
                    <div class="fs-stat-card">
                        <div class="fs-stat-card__icon">❤️</div>
                        <div class="fs-stat-card__info">
                            <span><%= request.getAttribute("wishlistCount") != null ? request.getAttribute("wishlistCount") : 0 %></span>
                            <span class="text-secondary">Wishlist Items</span>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
