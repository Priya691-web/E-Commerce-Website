<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Login");
    request.setAttribute("_pageCSS", "auth");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body class="auth-page">

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<main class="auth-main">
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-card__header">
                <span class="auth-card__tag">Sign In</span>
                <h1 class="auth-card__title">Welcome back</h1>
                <p class="auth-card__subtitle">Login to continue shopping your favorite fashion picks.</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
                <div class="auth-alert auth-alert--error" role="alert">
                    <span class="auth-alert__icon">⚠️</span>
                    <span class="auth-alert__message"><%= request.getAttribute("error") %></span>
                </div>
            <% } %>

            <% if (request.getParameter("registered") != null) { %>
                <div class="auth-alert auth-alert--success" role="alert">
                    <span class="auth-alert__icon">✓</span>
                    <span class="auth-alert__message">Account created successfully! Please login below.</span>
                </div>
            <% } %>

            <form id="loginForm" class="auth-form" action="<%= request.getContextPath() %>/login" method="post" novalidate>
                <input type="hidden" name="csrfToken" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>" />
                
                <div class="auth-field">
                    <label for="email" class="auth-field__label">Email Address</label>
                    <input 
                        type="email" 
                        id="email" 
                        name="email" 
                        placeholder="Enter your email" 
                        autocomplete="email" 
                        required 
                        class="auth-field__input"
                    />
                    <span class="auth-field__error" id="email-error"></span>
                </div>

                <div class="auth-field auth-field--password">
                    <label for="password" class="auth-field__label">Password</label>
                    <div class="auth-password-wrapper">
                        <input 
                            type="password" 
                            id="password" 
                            name="password" 
                            placeholder="Enter your password" 
                            autocomplete="current-password" 
                            required 
                            class="auth-field__input"
                        />
                        <button type="button" class="auth-toggle-password" aria-label="Show password" aria-pressed="false">
                            <svg class="auth-toggle-password__icon--show" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                                <circle cx="12" cy="12" r="3"></circle>
                            </svg>
                            <svg class="auth-toggle-password__icon--hide auth-toggle-password__icon--hidden" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 0 5.06 5.94M9.9 4.24A9.12 9.12 0 0 1 12 5c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path>
                                <line x1="1" y1="1" x2="23" y2="23"></line>
                            </svg>
                        </button>
                    </div>
                    <span class="auth-field__error" id="password-error"></span>
                </div>

                <div class="auth-form__actions">
                    <label class="auth-checkbox">
                        <input type="checkbox" name="remember_me" id="remember_me" class="auth-checkbox__input">
                        <span class="auth-checkbox__label">Remember me</span>
                    </label>
                    <a href="<%= request.getContextPath() %>/forgot-password" class="auth-link">Forgot password?</a>
                </div>

                <button type="submit" class="auth-submit-btn">
                    <span class="auth-submit-btn__text">Sign In</span>
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
                    New to FashionStore?
                    <a href="<%= request.getContextPath() %>/register" class="auth-link">Create account</a>
                </p>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

<script src="<%= request.getContextPath() %>/assets/js/auth.js" defer></script>

</body>
</html>