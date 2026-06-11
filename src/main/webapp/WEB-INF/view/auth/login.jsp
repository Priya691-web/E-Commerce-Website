<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="max-width: 450px; margin-top: 80px; margin-bottom: 80px;">
    <div class="card" style="padding: 50px 40px; border-radius: 8px; box-shadow: 0 20px 40px rgba(0,0,0,0.08); border: none;">
        <h2 style="text-align: center; margin-bottom: 10px; font-weight: 800; font-size: 2rem;">Welcome Back</h2>
        <p style="text-align: center; color: var(--text-muted); margin-bottom: 40px; font-size: 0.95rem;">Sign in to continue to FashionStore</p>
        
        <c:if test="${not empty error}">
            <div style="background-color: rgba(239, 68, 68, 0.1); color: var(--danger); padding: 14px; border-radius: 4px; margin-bottom: 25px; font-size: 0.9rem; text-align: center;">
                ${error}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group" style="margin-bottom: 25px;">
                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Email Address</label>
                <input type="email" name="email" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required placeholder="Enter your email">
            </div>
            <div class="form-group" style="margin-bottom: 30px;">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Password</label>
                    <a href="#" style="font-size: 0.85rem; color: var(--primary);">Forgot?</a>
                </div>
                <input type="password" name="password" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required placeholder="Enter your password">
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 16px; font-size: 1.05rem; font-weight: 700; border-radius: 4px; text-transform: uppercase; letter-spacing: 1px;">Sign In</button>
        </form>
        <p style="text-align: center; margin-top: 30px; color: var(--text-muted); font-size: 0.95rem;">
            Don't have an account? <a href="${pageContext.request.contextPath}/register" style="color: var(--primary); font-weight: 600;">Sign up</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
