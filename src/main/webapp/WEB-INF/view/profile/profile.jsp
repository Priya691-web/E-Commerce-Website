<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/WEB-INF/view/partials/navbar.jsp" />

<div class="container" style="max-width: 650px; margin-top: 80px; margin-bottom: 80px;">
    <div class="card" style="padding: 50px 40px; border-radius: 8px; box-shadow: 0 10px 40px rgba(0,0,0,0.06); border: none;">
        <div style="display: flex; align-items: center; gap: 20px; margin-bottom: 35px; border-bottom: 1px solid var(--border); padding-bottom: 20px;">
            <div style="width: 64px; height: 64px; background-color: var(--primary); color: #fff; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 1.5rem; font-weight: 800;">
                ${fn:substring(sessionScope.user.firstName, 0, 1)}${fn:substring(sessionScope.user.lastName, 0, 1)}
            </div>
            <div>
                <h2 style="margin-bottom: 5px; font-weight: 800; text-transform: uppercase; letter-spacing: 1px;">My Profile</h2>
                <p style="color: var(--text-muted); font-size: 0.95rem; margin: 0;">Manage your account details and preferences</p>
            </div>
        </div>
        
        <c:if test="${not empty success}">
            <div style="background-color: rgba(16, 185, 129, 0.1); color: #10b981; padding: 14px; border-radius: 4px; margin-bottom: 25px; font-size: 0.9rem; text-align: center; border: 1px solid rgba(16, 185, 129, 0.2);">
                ${success}
            </div>
        </c:if>
        <c:if test="${not empty error}">
            <div style="background-color: rgba(239, 68, 68, 0.1); color: var(--danger); padding: 14px; border-radius: 4px; margin-bottom: 25px; font-size: 0.9rem; text-align: center;">
                ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/profile" method="post">
            <div style="display: flex; gap: 20px; margin-bottom: 25px;">
                <div class="form-group" style="flex: 1; margin-bottom: 0;">
                    <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">First Name</label>
                    <input type="text" name="firstName" value="${sessionScope.user.firstName}" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required>
                </div>
                <div class="form-group" style="flex: 1; margin-bottom: 0;">
                    <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Last Name</label>
                    <input type="text" name="lastName" value="${sessionScope.user.lastName}" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" required>
                </div>
            </div>
            
            <div class="form-group" style="margin-bottom: 25px;">
                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Email Address <span style="color: var(--text-muted); font-weight: 400; text-transform: none;">(Cannot be changed)</span></label>
                <input type="email" value="${sessionScope.user.email}" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f1f5f9; border: 1px solid #e2e8f0; color: #94a3b8; cursor: not-allowed;" disabled>
            </div>
            
            <div class="form-group" style="margin-bottom: 25px;">
                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Phone Number</label>
                <input type="text" name="phone" value="${sessionScope.user.phone}" class="form-control" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0;" placeholder="Enter phone number">
            </div>
            
            <div class="form-group" style="margin-bottom: 35px;">
                <label class="form-label" style="font-weight: 600; font-size: 0.85rem; text-transform: uppercase; letter-spacing: 0.5px;">Default Shipping Address</label>
                <textarea name="address" class="form-control" rows="4" style="padding: 14px; border-radius: 4px; background-color: #f8f9fa; border: 1px solid #e2e8f0; resize: vertical;" placeholder="Enter your full address...">${sessionScope.user.address}</textarea>
            </div>
            
            <div style="display: flex; gap: 15px; justify-content: flex-end;">
                <button type="reset" class="btn btn-outline" style="padding: 14px 25px; font-weight: 600;">Cancel</button>
                <button type="submit" class="btn btn-primary" style="padding: 14px 35px; font-weight: 700; text-transform: uppercase; letter-spacing: 1px; box-shadow: 0 4px 14px rgba(255, 62, 108, 0.3);">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/view/partials/footer.jsp" />
