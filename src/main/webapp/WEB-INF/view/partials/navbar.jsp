<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FashionStore</title>
    <!-- Google Fonts -->
    <link href="https://fonts.googleapis.com/css2?family=Assistant:wght@300;400;600;700;800&display=swap" rel="stylesheet">
    <!-- Custom CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/index.css">
</head>
<body>
    <nav class="navbar">
        <div class="container" style="display: flex; justify-content: space-between; align-items: center;">
            <!-- Brand -->
            <a href="${pageContext.request.contextPath}/home" class="nav-brand">FashionStore</a>
            
            <!-- Main Links -->
            <div class="nav-links">
                <a href="${pageContext.request.contextPath}/home" class="nav-item">Home</a>
                <a href="${pageContext.request.contextPath}/product" class="nav-item">Products</a>
                <c:if test="${not empty sessionScope.user}">
                    <a href="${pageContext.request.contextPath}/cart" class="nav-item">Cart</a>
                    <a href="${pageContext.request.contextPath}/order?action=my-orders" class="nav-item">My Orders</a>
                </c:if>
            </div>
            
            <!-- Actions -->
            <div class="nav-actions">
                <c:choose>
                    <c:when test="${not empty sessionScope.user}">
                        <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline" style="border:none; border-bottom: 2px solid transparent; border-radius: 0; padding: 8px;">Profile</a>
                        <a href="${pageContext.request.contextPath}/logout" class="btn btn-primary">Logout</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Login</a>
                        <a href="${pageContext.request.contextPath}/register" class="btn btn-primary">Sign Up</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </nav>
    <main class="main-content">
