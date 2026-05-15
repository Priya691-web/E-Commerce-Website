<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "404 – Page Not Found");
    request.setAttribute("_pageCSS", "");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<main class="site-main error-page premium-fade">
    <div class="error-container premium-reveal">
        <span class="accent-tag">Heritage Collection</span>
        <div class="error-code editorial-title stagger-1">404</div>
        <h1 class="error-title editorial-title stagger-2">Lost in the Collection</h1>
        <p class="error-subtitle stagger-3">The piece you are looking for is currently unavailable or has been moved to our private archives.</p>
        <div class="error-actions stagger-4 error-actions--margin-top">
            <a href="<%= request.getContextPath() %>/home" class="fs-btn fs-btn--primary">Return Home</a>
            <a href="<%= request.getContextPath() %>/products" class="fs-btn fs-btn--outline fs-btn--margin-left">Shop New Arrivals</a>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />
</body>
</html>
