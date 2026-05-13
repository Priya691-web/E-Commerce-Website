<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page Not Found - FashionStore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <div class="max-w-md w-full bg-white rounded-lg shadow-lg p-6 text-center">
        <!-- 404 Icon -->
        <div class="mx-auto w-16 h-16 bg-yellow-100 rounded-full flex items-center justify-center mb-4">
            <svg class="w-8 h-8 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9.172 16.172a4 4 0 015.656 0M9 12h6m-6-4h.01M9 16h.01"/>
            </svg>
        </div>

        <!-- Error Message -->
        <h1 class="text-3xl font-bold text-gray-900 mb-2">Page Not Found</h1>
        <p class="text-gray-600 mb-6">
            The page you're looking for doesn't exist or has been moved.
        </p>

        <!-- Error Details -->
        <c:if test="${error != null}">
            <div class="bg-gray-50 rounded-lg p-4 mb-6 text-left">
                <p class="text-sm font-medium text-gray-700 mb-2">Error Details:</p>
                <div class="text-xs text-gray-600 space-y-1">
                    <p><strong>Error Code:</strong> ${error.error}</p>
                    <p><strong>Type:</strong> ${error.type}</p>
                    <p><strong>Time:</strong> <span class="timestamp">${error.timestamp}</span></p>
                    <c:if test="${error.path != null}">
                        <p><strong>Requested Path:</strong> ${error.path}</p>
                    </c:if>
                </div>
            </div>
        </c:if>

        <!-- Search Bar -->
        <div class="mb-6">
            <form action="${pageContext.request.contextPath}/products" method="GET" class="flex">
                <input type="text" 
                       name="query" 
                       placeholder="Search for products..." 
                       class="flex-1 px-4 py-2 border border-gray-300 rounded-l-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                <button type="submit" 
                        class="bg-primary text-white px-4 py-2 rounded-r-lg hover:bg-primary-dark transition-colors">
                    Search
                </button>
            </form>
        </div>

        <!-- Action Buttons -->
        <div class="space-y-3">
            <a href="${pageContext.request.contextPath}/" 
               class="w-full bg-primary text-white py-3 px-4 rounded-lg font-medium hover:bg-primary-dark transition-colors">
                Go to Homepage
            </a>
            <button onclick="history.back()" 
                    class="w-full bg-gray-100 text-gray-700 py-3 px-4 rounded-lg font-medium hover:bg-gray-200 transition-colors">
                Go Back
            </button>
        </div>

        <!-- Popular Links -->
        <div class="mt-6 pt-6 border-t border-gray-200">
            <p class="text-sm font-medium text-gray-700 mb-3">Popular Pages:</p>
            <div class="grid grid-cols-2 gap-2 text-sm">
                <a href="${pageContext.request.contextPath}/products" 
                   class="text-primary hover:text-primary-dark transition-colors">
                    Products
                </a>
                <a href="${pageContext.request.contextPath}/cart" 
                   class="text-primary hover:text-primary-dark transition-colors">
                    Cart
                </a>
                <a href="${pageContext.request.contextPath}/account" 
                   class="text-primary hover:text-primary-dark transition-colors">
                    Account
                </a>
                <a href="${pageContext.request.contextPath}/admin" 
                   class="text-primary hover:text-primary-dark transition-colors">
                    Admin
                </a>
            </div>
        </div>
    </div>

    <script>
        // Format timestamp
        document.addEventListener('DOMContentLoaded', function() {
            const timestampElements = document.querySelectorAll('.timestamp');
            timestampElements.forEach(function(element) {
                const timestamp = parseInt(element.textContent);
                if (!isNaN(timestamp)) {
                    const date = new Date(timestamp);
                    element.textContent = date.toLocaleString();
                }
            });
        });
    </script>
</body>
</html>
