<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String currentPath = request.getRequestURI();
    String ctx = request.getContextPath();
    int addrCount = (request.getAttribute("addressCount") != null) ? (Integer) request.getAttribute("addressCount") : 0;
    int orderCount = (request.getAttribute("orderCount") != null) ? (Integer) request.getAttribute("orderCount") : 0;
    int wishlistCount = (request.getAttribute("wishlistCount") != null) ? (Integer) request.getAttribute("wishlistCount") : 0;
%>
<aside class="fs-account-sidebar">
    <nav class="fs-account-nav" aria-label="Account navigation">
        <a href="<%= ctx %>/account/profile" class="fs-account-nav-item <%= currentPath.endsWith("/account/profile") || currentPath.equals(ctx + "/account/profile") ? "fs-account-nav-item--active" : "" %>">
            <span class="fs-account-nav-item__icon" aria-hidden="true">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
            </span>
            <span>Profile</span>
        </a>
        <a href="<%= ctx %>/account/profile/edit" class="fs-account-nav-item <%= currentPath.endsWith("/edit") ? "fs-account-nav-item--active" : "" %>">
            <span class="fs-account-nav-item__icon" aria-hidden="true">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
            </span>
            <span>Edit Profile</span>
        </a>
        <a href="<%= ctx %>/account/addresses" class="fs-account-nav-item <%= currentPath.contains("/account/address") ? "fs-account-nav-item--active" : "" %>">
            <span class="fs-account-nav-item__icon" aria-hidden="true">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"/><circle cx="12" cy="10" r="3"/></svg>
            </span>
            <span>Addresses</span>
            <% if (addrCount > 0) { %><span class="fs-account-nav-item__badge"><%= addrCount %></span><% } %>
        </a>
        <a href="<%= ctx %>/account/profile/settings" class="fs-account-nav-item <%= currentPath.endsWith("/settings") ? "fs-account-nav-item--active" : "" %>">
            <span class="fs-account-nav-item__icon" aria-hidden="true">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06A1.65 1.65 0 0 0 5.09 15a1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 5.09 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06A1.65 1.65 0 0 0 9 5.09a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06A1.65 1.65 0 0 0 18.91 9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>
            </span>
            <span>Settings</span>
        </a>
        <a href="<%= ctx %>/orders" class="fs-account-nav-item <%= currentPath.endsWith("/orders") || currentPath.contains("/orders") ? "fs-account-nav-item--active" : "" %>">
            <span class="fs-account-nav-item__icon" aria-hidden="true">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2L3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4z"/><line x1="3" y1="6" x2="21" y2="6"/><path d="M16 10a4 4 0 0 1-8 0"/></svg>
            </span>
            <span>Orders</span>
            <% if (orderCount > 0) { %><span class="fs-account-nav-item__badge"><%= orderCount %></span><% } %>
        </a>
        <a href="<%= ctx %>/wishlist" class="fs-account-nav-item <%= currentPath.endsWith("/wishlist") || currentPath.contains("/wishlist") ? "fs-account-nav-item--active" : "" %>">
            <span class="fs-account-nav-item__icon" aria-hidden="true">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"/></svg>
            </span>
            <span>Wishlist</span>
            <% if (wishlistCount > 0) { %><span class="fs-account-nav-item__badge"><%= wishlistCount %></span><% } %>
        </a>
    </nav>
</aside>
