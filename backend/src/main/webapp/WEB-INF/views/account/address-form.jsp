<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="com.fashionstore.model.Address" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html lang="en">
<head>
<%
    request.setAttribute("_pageTitle", "Add Address");
    request.setAttribute("_pageCSS", "account");
%>
<jsp:include page="/WEB-INF/views/partials/head.jsp" />
</head>

<body>

<jsp:include page="/WEB-INF/views/partials/navbar.jsp" />

<%
    Address address = (Address) request.getAttribute("address");
    boolean isEdit = (address != null && address.getAddressId() > 0);
    if (address == null) {
        address = new Address();
    }
    @SuppressWarnings("unchecked")
    Map<String, String> fieldErrors = (Map<String, String>) request.getAttribute("fieldErrors");
%>

<main class="account-page">
    <div class="container">
        <div class="account-header">
            <h1><%= isEdit ? "Edit Address" : "Add New Address" %></h1>
            <p class="account-greeting"><%= isEdit ? "Update your address details" : "Add a new shipping or billing address" %></p>
        </div>

        <div class="account-layout">
            <jsp:include page="/WEB-INF/views/partials/account-sidebar.jsp" />

            <!-- Main Content -->
            <div class="account-content">
                <section class="account-section">
                    <div class="section-header">
                        <h2>Address Details</h2>
                    </div>

                    <% if (request.getAttribute("error") != null) { %>
                        <div class="alert alert-error">
                            <%= request.getAttribute("error") %>
                        </div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/account/addresses" method="POST" class="fs-form-grid account-form">
                        <input type="hidden" name="action" value="<%= isEdit ? "edit" : "add" %>">
                        <% if (isEdit) { %>
                            <input type="hidden" name="addressId" value="<%= address.getAddressId() %>">
                        <% } %>
                        <input type="hidden" name="csrfToken" value="<%= request.getAttribute("csrfToken") != null ? request.getAttribute("csrfToken") : "" %>">

                        <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("addressType") ? "has-error" : "" %>">
                            <label for="addressType">Address Type *</label>
                            <select id="addressType" name="addressType" class="fs-form-select" required>
                                <option value="">Select Type</option>
                                <option value="both" <%= "both".equals(address.getAddressType()) ? "selected" : "" %>>Both Shipping & Billing</option>
                                <option value="shipping" <%= "shipping".equals(address.getAddressType()) ? "selected" : "" %>>Shipping Only</option>
                                <option value="billing" <%= "billing".equals(address.getAddressType()) ? "selected" : "" %>>Billing Only</option>
                            </select>
                            <% if (fieldErrors != null && fieldErrors.get("addressType") != null) { %>
                                <span class="field-error"><%= fieldErrors.get("addressType") %></span>
                            <% } %>
                        </div>

                        <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("fullName") ? "has-error" : "" %>">
                            <label for="fullName">Full Name *</label>
                            <input type="text" id="fullName" name="fullName" 
                                   value="<%= address.getFullName() != null ? address.getFullName() : "" %>" 
                                   required
                                   minlength="2"
                                   maxlength="100"
                                   class="fs-form-input">
                            <% if (fieldErrors != null && fieldErrors.get("fullName") != null) { %>
                                <span class="field-error"><%= fieldErrors.get("fullName") %></span>
                            <% } %>
                        </div>

                        <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("phone") ? "has-error" : "" %>">
                            <label for="phone">Phone Number *</label>
                            <input type="tel" id="phone" name="phone" 
                                   value="<%= address.getPhone() != null ? address.getPhone() : "" %>" 
                                   required
                                   pattern="[0-9]{10}"
                                   maxlength="15"
                                   class="fs-form-input">
                            <% if (fieldErrors != null && fieldErrors.get("phone") != null) { %>
                                <span class="field-error"><%= fieldErrors.get("phone") %></span>
                            <% } else { %>
                                <span class="form-hint">10-digit mobile number</span>
                            <% } %>
                        </div>

                        <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("addressLine1") ? "has-error" : "" %>">
                            <label for="addressLine1">Address Line 1 *</label>
                            <input type="text" id="addressLine1" name="addressLine1" 
                                   value="<%= address.getAddressLine1() != null ? address.getAddressLine1() : "" %>" 
                                   required
                                   maxlength="255"
                                   class="fs-form-input">
                            <% if (fieldErrors != null && fieldErrors.get("addressLine1") != null) { %>
                                <span class="field-error"><%= fieldErrors.get("addressLine1") %></span>
                            <% } else { %>
                                <span class="form-hint">Street address, P.O. box, company name</span>
                            <% } %>
                        </div>

                        <div class="fs-form-group">
                            <label for="addressLine2">Address Line 2</label>
                            <input type="text" id="addressLine2" name="addressLine2" 
                                   value="<%= address.getAddressLine2() != null ? address.getAddressLine2() : "" %>" 
                                   maxlength="255"
                                   class="fs-form-input">
                            <span class="form-hint">Apartment, suite, unit, building, floor, etc.</span>
                        </div>

                        <div class="fs-form-row">
                            <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("city") ? "has-error" : "" %>">
                                <label for="city">City *</label>
                                <input type="text" id="city" name="city" 
                                       value="<%= address.getCity() != null ? address.getCity() : "" %>" 
                                       required
                                       maxlength="100"
                                       class="fs-form-input">
                                <% if (fieldErrors != null && fieldErrors.get("city") != null) { %>
                                    <span class="field-error"><%= fieldErrors.get("city") %></span>
                                <% } %>
                            </div>

                            <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("state") ? "has-error" : "" %>">
                                <label for="state">State *</label>
                                <input type="text" id="state" name="state" 
                                       value="<%= address.getState() != null ? address.getState() : "" %>" 
                                       required
                                       maxlength="100"
                                       class="fs-form-input">
                                <% if (fieldErrors != null && fieldErrors.get("state") != null) { %>
                                    <span class="field-error"><%= fieldErrors.get("state") %></span>
                                <% } %>
                            </div>
                        </div>

                        <div class="fs-form-row">
                            <div class="fs-form-group <%= fieldErrors != null && fieldErrors.containsKey("postalCode") ? "has-error" : "" %>">
                                <label for="postalCode">Postal Code *</label>
                                <input type="text" id="postalCode" name="postalCode" 
                                       value="<%= address.getPostalCode() != null ? address.getPostalCode() : "" %>" 
                                       required
                                       maxlength="20"
                                       class="fs-form-input">
                                <% if (fieldErrors != null && fieldErrors.get("postalCode") != null) { %>
                                    <span class="field-error"><%= fieldErrors.get("postalCode") %></span>
                                <% } %>
                            </div>

                            <div class="fs-form-group">
                                <label for="country">Country *</label>
                                <select id="country" name="country" class="fs-form-select" required>
                                    <option value="">Select Country</option>
                                    <option value="India" <%= "India".equals(address.getCountry()) ? "selected" : "" %>>India</option>
                                    <option value="United States" <%= "United States".equals(address.getCountry()) ? "selected" : "" %>>United States</option>
                                    <option value="United Kingdom" <%= "United Kingdom".equals(address.getCountry()) ? "selected" : "" %>>United Kingdom</option>
                                    <option value="Canada" <%= "Canada".equals(address.getCountry()) ? "selected" : "" %>>Canada</option>
                                    <option value="Australia" <%= "Australia".equals(address.getCountry()) ? "selected" : "" %>>Australia</option>
                                </select>
                            </div>
                        </div>

                        <div class="fs-form-group checkbox-group">
                            <label class="fs-form-checkbox">
                                <input type="checkbox" name="isDefault" <%= address.isDefault() ? "checked" : "" %>>
                                <span class="checkbox-text">
                                    <span class="checkbox-title">Set as Default Address</span>
                                    <span class="checkbox-hint">This will be used as your default address for future orders</span>
                                </span>
                            </label>
                        </div>

                        <div class="fs-form-actions fs-form-actions--full-width">
                            <button type="submit" class="fs-btn fs-btn--primary"><%= isEdit ? "Update Address" : "Save Address" %></button>
                            <a href="<%= request.getContextPath() %>/account/addresses" class="fs-btn fs-btn--outline">Cancel</a>
                        </div>
                    </form>
                </section>
            </div>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/views/partials/footer.jsp" />

</body>
</html>
