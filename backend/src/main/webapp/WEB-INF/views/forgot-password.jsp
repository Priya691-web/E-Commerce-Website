<%@ page contentType="text/html;charset=UTF-8" %>
<%
    request.setAttribute("_pageTitle", "Forgot Password");
    request.setAttribute("_pageCSS", "auth");
%>
<!DOCTYPE html>
<html lang="en">
<head>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body class="auth-page">

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<main class="auth-main">
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-card__header">
                <span class="auth-card__tag">Forgot Password</span>
                <h1 class="auth-card__title">Reset Password</h1>
                <p class="auth-card__subtitle">Enter your email address and we'll send you a link to reset your password.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="auth-alert auth-alert--error" role="alert">
                    <span class="auth-alert__icon">⚠️</span>
                    <span class="auth-alert__message"><%= request.getAttribute("error") %></span>
                </div>
            <% } %>

            <% if (request.getAttribute("success") != null) { %>
                <div class="auth-alert auth-alert--success" role="alert">
                    <span class="auth-alert__icon">✓</span>
                    <span class="auth-alert__message"><%= request.getAttribute("success") != null ? request.getAttribute("success") : "" %></span>
                </div>
                <% if (request.getAttribute("resetLink") != null) { %>
                    <div class="alert alert-info" role="alert">
                        <strong>Development Mode - Reset Link:</strong><br>
                        <a href="<%= request.getAttribute("resetLink") != null ? request.getAttribute("resetLink") : "#" %>" target="_blank" rel="noopener noreferrer"><%= request.getAttribute("resetLink") != null ? request.getAttribute("resetLink") : "" %></a>
                    </div>
                <% } %>
            <% } %>

            <form id="forgotPasswordForm" class="auth-form" action="<%= request.getContextPath() %>/forgot-password" method="post" novalidate>
                <% if (request.getAttribute("csrfToken") != null) { %>
                <input type="hidden" name="csrf_token" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>">
                <% } %>
                
                <div class="auth-field">
                    <label for="email" class="auth-field__label">Email Address</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        required 
                        class="auth-field__input"
                        placeholder="Enter your email address"
                        autocomplete="email"
                    />
                    <span class="text-sm text-secondary" style="font-size: var(--text-xs); color: var(--color-text-tertiary); margin-top: var(--space-1);">We'll send a password reset link to this email</span>
                    <span class="auth-field__error" id="email-error"></span>
                </div>

                <button type="submit" class="auth-submit-btn">
                    <span class="auth-submit-btn__text">Send Reset Link</span>
                    <svg class="auth-submit-btn__spinner" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10" stroke-opacity="0.25"></circle>
                        <path d="M12 2a10 10 0 0 1 10 10" stroke-dasharray="32" stroke-dashoffset="32">
                            <animateTransform attributeName="transform" type="rotate" from="0 12 12" to="360 12 12" dur="1s" repeatCount="indefinite"/>
                        </path>
                    </svg>
                </button>
            </form>

            <div class="auth-card__footer">
                <p class="auth-card__footer-text">
                    Remember your password?
                    <a href="<%= request.getContextPath() %>/login" class="auth-link">Sign in</a>
                </p>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

<script src="<%= request.getContextPath() %>/assets/js/auth.js" defer></script>

</body>
</html>
