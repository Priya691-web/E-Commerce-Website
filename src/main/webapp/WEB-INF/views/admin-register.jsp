<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Admin Registration");
    request.setAttribute("_pageCSS", "auth");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<main class="site-main auth-page">
    <section class="auth-card">
        <h1>Admin Registration</h1>
        <p>Create a new admin account for FashionStore.</p>

        <% if (request.getAttribute("error") != null) { %>
            <p class="auth-error">
                <%= request.getAttribute("error") %>
            </p>
        <% } %>

        <form action="<%= request.getContextPath() %>/admin/register" method="post" class="auth-form">
            <input type="hidden" name="csrf_token" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>" />

            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" name="fullName" placeholder="John Doe" autocomplete="name" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" placeholder="admin@fashionstore.com" autocomplete="email" required>

            <label for="phone">Phone</label>
            <input type="tel" id="phone" name="phone" placeholder="9876543210" autocomplete="tel" required>

            <label for="password">Password</label>
            <div class="password-field">
                <input type="password" id="password" name="password" placeholder="Enter a strong password" autocomplete="new-password" required minlength="8">
                <button type="button" class="password-toggle" aria-label="Show password" aria-pressed="false" onclick="togglePassword(this)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                        <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                </button>
            </div>

            <label for="confirmPassword">Confirm Password</label>
            <div class="password-field">
                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Re-enter password" autocomplete="new-password" required minlength="8">
                <button type="button" class="password-toggle" aria-label="Show confirm password" aria-pressed="false" onclick="togglePassword(this)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                        <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                </button>
            </div>

            <label for="adminKey">Admin Secret Key</label>
            <div class="password-field">
                <input type="password" id="adminKey" name="adminKey" placeholder="Enter admin secret key" required>
                <button type="button" class="password-toggle" aria-label="Show admin secret key" aria-pressed="false" onclick="togglePassword(this)">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                        <circle cx="12" cy="12" r="3"></circle>
                    </svg>
                </button>
            </div>
            <small class="form-hint">Contact the system owner for the admin secret key.</small>

            <button type="submit" class="btn btn-primary">Create Admin Account</button>
        </form>

        <p class="auth-links">
            Already have an account?
            <a href="<%= request.getContextPath() %>/login">Login</a>
        </p>
    </section>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
