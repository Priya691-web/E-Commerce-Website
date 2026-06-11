<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="max-width: 500px; margin-top: 80px; margin-bottom: 80px;">
    <div class="card" style="padding: 50px 40px; border-radius: 8px; box-shadow: 0 20px 40px rgba(0,0,0,0.08); border: none;">
        <h2 style="text-align: center; margin-bottom: 10px; font-weight: 800; font-size: 2rem;">Create Account</h2>
        <p style="text-align: center; color: var(--text-muted); margin-bottom: 40px; font-size: 0.95rem;">Join FashionStore today</p>
        
        <c:if test="${not empty error}">
            <div style="background-color: rgba(239, 68, 68, 0.1); color: var(--danger); padding: 14px; border-radius: 4px; margin-bottom: 25px; font-size: 0.9rem; text-align: center;">
                ${error}
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/register" method="post">
            <div style="display: flex; gap: 20px; margin-bottom: 20px;">
                <div class="form-group" style="flex: 1; margin-bottom: 0;">
                    <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">First Name</label>
                    <input type="text" name="firstName" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required placeholder="Jane">
                </div>
                <div class="form-group" style="flex: 1; margin-bottom: 0;">
                    <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Last Name</label>
                    <input type="text" name="lastName" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required placeholder="Doe">
                </div>
            </div>
            <div class="form-group" style="margin-bottom: 20px;">
                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Email Address</label>
                <input type="email" name="email" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required placeholder="jane@example.com">
            </div>
            <div class="form-group" style="margin-bottom: 30px;">
                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Password</label>
                <input type="password" name="password" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required placeholder="Create a password">
            </div>
            <button type="submit" class="btn btn-primary" style="width: 100%; padding: 16px; font-size: 1.05rem; font-weight: 700; border-radius: 4px; text-transform: uppercase; letter-spacing: 1px;">Sign Up</button>
        </form>
        <p style="text-align: center; margin-top: 30px; color: var(--text-muted); font-size: 0.95rem;">
            Already have an account? <a href="${pageContext.request.contextPath}/login" style="color: var(--primary); font-weight: 600;">Sign in</a>
        </p>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
