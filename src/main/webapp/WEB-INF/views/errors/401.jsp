<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Authentication Required - FashionStore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css">
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="min-h-screen bg-gray-50 flex items-center justify-center px-4">
    <div class="max-w-md w-full bg-white rounded-lg shadow-lg p-6 text-center">
        <!-- Lock Icon -->
        <div class="mx-auto w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mb-4">
            <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
            </svg>
        </div>

        <!-- Error Message -->
        <h1 class="text-2xl font-bold text-gray-900 mb-2">Authentication Required</h1>
        <p class="text-gray-600 mb-6">
            Please log in to access this page.
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

        <!-- Login Form -->
        <div class="mb-6">
            <form action="${pageContext.request.contextPath}/login" method="POST" class="space-y-4">
                <div>
                    <input type="email" 
                           name="email" 
                           placeholder="Email address" 
                           required
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                </div>
                <div>
                    <input type="password" 
                           name="password" 
                           placeholder="Password" 
                           required
                           class="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent">
                </div>
                <button type="submit" 
                        class="w-full bg-primary text-white py-3 px-4 rounded-lg font-medium hover:bg-primary-dark transition-colors">
                    Log In
                </button>
            </form>
        </div>

        <!-- Alternative Actions -->
        <div class="space-y-3">
            <a href="${pageContext.request.contextPath}/register" 
               class="w-full bg-gray-100 text-gray-700 py-3 px-4 rounded-lg font-medium hover:bg-gray-200 transition-colors inline-block">
                Create Account
            </a>
            <button onclick="history.back()" 
                    class="w-full bg-gray-50 text-gray-600 py-3 px-4 rounded-lg font-medium hover:bg-gray-100 transition-colors">
                Go Back
            </button>
        </div>

        <!-- Help Links -->
        <div class="mt-6 pt-6 border-t border-gray-200">
            <p class="text-sm text-gray-500">
                <a href="${pageContext.request.contextPath}/forgot-password" 
                   class="text-primary hover:text-primary-dark transition-colors">
                    Forgot your password?
                </a>
            </p>
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
