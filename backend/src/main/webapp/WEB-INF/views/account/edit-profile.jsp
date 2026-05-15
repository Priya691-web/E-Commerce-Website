<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.fashionstore.model.User" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Edit Profile");
    request.setAttribute("_pageCSS", "account");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        user = (User) session.getAttribute("customerAuth");
    }
    @SuppressWarnings("unchecked")
    Map<String, String> fieldErrors = (Map<String, String>) request.getAttribute("fieldErrors");
%>

<main class="shell section-block" id="main-content">
    <div class="fs-account-header">
        <h1 class="editorial-heading">Edit Profile</h1>
        <p class="text-secondary">Update your personal information</p>
    </div>

    <div class="fs-account-layout">
        <jsp:include page="/WEB-INF/views/partials/account-sidebar.jsp" />

        <div class="fs-account-content">
            <section class="fs-account-section">
                <div class="fs-section-header">
                    <h2 class="editorial-heading">Personal Information</h2>
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

                <form action="<%= request.getContextPath() %>/account/profile" method="POST" class="fs-form-grid">
                    <input type="hidden" name="action" value="updateProfile">
                    <input type="hidden" name="csrf_token" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>">

                    <div class="fs-form-group">
                        <label for="fullName">Full Name *</label>
                        <input type="text" id="fullName" name="fullName" 
                               value="<%= user.getFullName() != null ? user.getFullName() : "" %>" 
                               required
                               minlength="2"
                               maxlength="100"
                               class="fs-form-input">
                        <span class="text-sm text-secondary">Your full name as it appears on orders</span>
                    </div>

                    <div class="fs-form-group">
                        <label for="email">Email Address</label>
                        <input type="email" id="email" name="email" 
                               value="<%= user.getEmail() != null ? user.getEmail() : "" %>" 
                               disabled
                               class="fs-form-input">
                        <span class="text-sm text-secondary">Email cannot be changed. Contact support for assistance.</span>
                    </div>

                    <div class="fs-form-group">
                        <label for="phone">Phone Number</label>
                        <input type="tel" id="phone" name="phone" 
                               value="<%= user.getPhone() != null ? user.getPhone() : "" %>" 
                               pattern="[0-9]{10}"
                               maxlength="15"
                               class="fs-form-input">
                        <span class="text-sm text-secondary">For delivery updates and order confirmation</span>
                    </div>

                    <div class="fs-form-group">
                        <label for="gender">Gender</label>
                        <select id="gender" name="gender" class="fs-form-select">
                            <option value="">Select Gender</option>
                            <option value="male" <%= "male".equals(user.getGender()) ? "selected" : "" %>>Male</option>
                            <option value="female" <%= "female".equals(user.getGender()) ? "selected" : "" %>>Female</option>
                            <option value="other" <%= "other".equals(user.getGender()) ? "selected" : "" %>>Other</option>
                            <option value="prefer_not_to_say" <%= "prefer_not_to_say".equals(user.getGender()) ? "selected" : "" %>>Prefer not to say</option>
                        </select>
                    </div>

                    <div class="fs-form-group">
                        <label for="address">Default Address</label>
                        <textarea id="address" name="address" rows="3" maxlength="500" class="fs-form-textarea"><%= user.getAddress() != null ? user.getAddress() : "" %></textarea>
                        <span class="text-sm text-secondary">This will be used as your default address for orders</span>
                    </div>

                    <div class="fs-form-actions fs-form-actions--full-width">
                        <button type="submit" class="fs-btn fs-btn--primary">Save Changes</button>
                        <a href="<%= request.getContextPath() %>/account/profile" class="fs-btn fs-btn--outline">Cancel</a>
                    </div>
                </form>
            </section>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
