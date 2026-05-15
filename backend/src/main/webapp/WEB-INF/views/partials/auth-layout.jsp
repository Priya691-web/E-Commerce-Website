<%@ page contentType="text/html;charset=UTF-8" %>
<%-- 
    Unified Auth Layout Component
    Provides consistent layout for all authentication pages
    Usage: Include this partial in auth pages with the form content as the body
--%>

<body class="auth-page">

  <jsp:include page="../partials/navbar.jsp" />

  <main class="auth-main">
    <div class="auth-container">
      <jsp:doBody />
    </div>
  </main>

  <jsp:include page="../partials/footer.jsp" />

</body>
