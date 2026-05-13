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
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    User user = (User) session.getAttribute("user");
    List<Address> addresses = (List<Address>) request.getAttribute("addresses");
    int addressCount = (request.getAttribute("addressCount") != null) ? (Integer) request.getAttribute("addressCount") : 0;
%>

<main class="account-page">
    <div class="container">
        <div class="account-header">
            <h1>Address Management</h1>
            <p class="account-greeting">Manage your shipping and billing addresses</p>
        </div>

        <div class="account-layout">
            <jsp:include page="/WEB-INF/views/partials/account-sidebar.jsp" />

            <!-- Main Content -->
            <div class="account-content">
                <section class="account-section">
                    <div class="section-header">
                        <h2>Saved Addresses</h2>
                        <a href="<%= request.getContextPath() %>/account/addresses/add" class="btn btn-primary btn-sm">Add New Address</a>
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
                        <div class="empty-state">
                            <div class="empty-icon">📍</div>
                            <h3>No addresses saved</h3>
                            <p>Add your first address to make checkout faster</p>
                            <a href="<%= request.getContextPath() %>/account/addresses/add" class="btn btn-primary">Add Address</a>
                        </div>
                    <% } else { %>
                        <div class="address-list">
                            <% for (Address address : addresses) { %>
                                <div class="address-item <%= address.isDefault() ? "default" : "" %>">
                                    <div class="address-item-header">
                                        <div class="address-info">
                                            <span class="address-type-badge"><%= address.getAddressType().toUpperCase() %></span>
                                            <% if (address.isDefault()) { %>
                                                <span class="address-badge badge-default">Default</span>
                                            <% } %>
                                        </div>
                                        <div class="address-actions">
                                            <% if (!address.isDefault()) { %>
                                                <button class="btn btn-link btn-sm set-default-btn" 
                                                        data-address-id="<%= address.getAddressId() %>"
                                                        data-address-type="<%= address.getAddressType() %>">
                                                    Set Default
                                                </button>
                                            <% } %>
                                            <a href="<%= request.getContextPath() %>/account/addresses/edit/<%= address.getAddressId() %>" 
                                               class="btn btn-link btn-sm">Edit</a>
                                            <button class="btn btn-link btn-sm delete-btn" 
                                                    data-address-id="<%= address.getAddressId() %>">
                                                Delete
                                            </button>
                                        </div>
                                    </div>
                                    <div class="address-item-body">
                                        <p class="address-name"><%= address.getFullName() %></p>
                                        <p class="address-phone"><%= address.getPhone() %></p>
                                        <p class="address-text"><%= address.getAddressLine1() %></p>
                                        <% if (address.getAddressLine2() != null && !address.getAddressLine2().isEmpty()) { %>
                                            <p class="address-text"><%= address.getAddressLine2() %></p>
                                        <% } %>
                                        <p class="address-location">
                                            <%= address.getCity() %>, <%= address.getState() %> <%= address.getPostalCode() %>
                                        </p>
                                        <p class="address-country"><%= address.getCountry() %></p>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    <% } %>
                </section>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

<script>
document.addEventListener('DOMContentLoaded', function() {
    // Set default address
    document.querySelectorAll('.set-default-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const addressId = this.dataset.addressId;
            if (confirm('Set this address as default?')) {
                const formData = new URLSearchParams();
                formData.append('action', 'setDefault');
                formData.append('addressId', addressId);
                formData.append('csrf_token', window.csrfToken || '');

                fetch('<%= request.getContextPath() %>/account/addresses', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-Requested-With': 'XMLHttpRequest',
                        'X-CSRF-Token': window.csrfToken || ''
                    },
                    body: formData
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        FashionStore.showToast(data.message, 'success');
                        setTimeout(() => window.location.reload(), 1000);
                    } else {
                        FashionStore.showToast(data.message, 'error');
                    }
                })
                .catch(err => {
                    FashionStore.showToast('Failed to set default address', 'error');
                });
            }
        });
    });

    // Delete address
    document.querySelectorAll('.delete-btn').forEach(btn => {
        btn.addEventListener('click', function() {
            const addressId = this.dataset.addressId;
            if (confirm('Are you sure you want to delete this address?')) {
                const formData = new URLSearchParams();
                formData.append('action', 'delete');
                formData.append('addressId', addressId);
                formData.append('csrf_token', window.csrfToken || '');

                fetch('<%= request.getContextPath() %>/account/addresses', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                        'X-Requested-With': 'XMLHttpRequest',
                        'X-CSRF-Token': window.csrfToken || ''
                    },
                    body: formData
                })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        FashionStore.showToast(data.message, 'success');
                        setTimeout(() => window.location.reload(), 1000);
                    } else {
                        FashionStore.showToast(data.message, 'error');
                    }
                })
                .catch(err => {
                    FashionStore.showToast('Failed to delete address', 'error');
                });
            }
        });
    });
});
</script>

</body>
</html>
