<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.fashionstore.model.User" %>
<%@ page import="java.util.List" %>
<%@ page import="com.fashionstore.model.Address" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Address Management");
    request.setAttribute("_pageCSS", "account");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
<script src="<%= request.getContextPath() %>/assets/js/modules/address-management.js"></script>
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    User user = (User) session.getAttribute("customerAuth");
    List<Address> addresses = (List<Address>) request.getAttribute("addresses");
    int addressCount = (request.getAttribute("addressCount") != null) ? (Integer) request.getAttribute("addressCount") : 0;
%>

<main class="shell section-block" id="main-content">
    <div class="fs-account-header">
        <h1 class="editorial-heading">Address Management</h1>
        <p class="text-secondary">Manage your shipping and billing addresses</p>
    </div>

    <div class="fs-account-layout">
        <jsp:include page="/WEB-INF/views/partials/account-sidebar.jsp" />

        <div class="fs-account-content">
            <section class="fs-account-section">
                <div class="fs-section-header">
                    <h2 class="editorial-heading">Saved Addresses</h2>
                    <a href="<%= request.getContextPath() %>/account/addresses/add" class="fs-btn fs-btn--primary fs-btn--sm">Add New Address</a>
                </div>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-error">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>

                <% if (request.getAttribute("success") != null) { %>
                    <div class="alert alert-success">
                        <%= request.getAttribute("success") %>
                    </div>
                <% } %>

                <% if (addresses == null || addresses.isEmpty()) { %>
                    <div class="fs-empty-state">
                        <div class="fs-empty-state__visual">📍</div>
                        <h2 class="editorial-heading">No addresses saved</h2>
                        <p class="text-secondary">Add your first address to make checkout faster</p>
                        <a href="<%= request.getContextPath() %>/account/addresses/add" class="fs-btn fs-btn--primary">Add Address</a>
                    </div>
                <% } else { %>
                    <div class="fs-address-list">
                        <% for (Address address : addresses) { %>
                            <div class="fs-address-item <%= address.isDefault() ? "fs-address-item--default" : "" %>">
                                <div class="fs-address-item__header">
                                    <div class="fs-address-item__info">
                                        <span class="fs-address-item__type"><%= address.getAddressType().toUpperCase() %></span>
                                        <% if (address.isDefault()) { %>
                                            <span class="fs-badge">Default</span>
                                        <% } %>
                                    </div>
                                    <div class="fs-address-item__actions">
                                        <% if (!address.isDefault()) { %>
                                            <button class="fs-btn fs-btn--outline fs-btn--sm set-default-btn" 
                                                    data-address-id="<%= address.getAddressId() %>"
                                                    data-address-type="<%= address.getAddressType() %>">
                                                Set Default
                                            </button>
                                        <% } %>
                                        <a href="<%= request.getContextPath() %>/account/addresses/edit/<%= address.getAddressId() %>" 
                                           class="fs-btn fs-btn--outline fs-btn--sm">Edit</a>
                                        <button class="fs-btn fs-btn--outline fs-btn--sm delete-btn" 
                                                data-address-id="<%= address.getAddressId() %>">
                                            Delete
                                        </button>
                                    </div>
                                </div>
                                <div class="fs-address-item__body">
                                    <p><%= address.getFullName() %></p>
                                    <p class="text-secondary"><%= address.getPhone() %></p>
                                    <p class="text-secondary"><%= address.getAddressLine1() %></p>
                                    <% if (address.getAddressLine2() != null && !address.getAddressLine2().isEmpty()) { %>
                                        <p class="text-secondary"><%= address.getAddressLine2() %></p>
                                    <% } %>
                                    <p class="text-secondary">
                                        <%= address.getCity() %>, <%= address.getState() %> <%= address.getPostalCode() %>
                                    </p>
                                    <p class="text-secondary"><%= address.getCountry() %></p>
                                </div>
                            </div>
                        <% } %>
                    </div>
                <% } %>
            </section>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
