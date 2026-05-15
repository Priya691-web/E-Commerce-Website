package com.fashionstore.controller;

// import com.fashionstore.dto.WishlistDTO;
// WishlistDTO class doesn't exist, commenting out import
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API Controller for wishlist functionality with premium features
 * Handles wishlist management, saved items, and personalization
 * Returns JSON responses only - use /wishlist for page rendering
 */
@WebServlet({"/api/wishlist", "/api/wishlist/*"})
public class WishlistController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);
    private WishlistService wishlistService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        wishlistService = new WishlistService();
        objectMapper = new ObjectMapper();
    }

    /** Normalize servlet pathInfo: treat null/blank as root slash. */
    private String normalizePath(String pathInfo) {
        return (pathInfo == null || pathInfo.isBlank()) ? "/" : pathInfo;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = normalizePath(request.getPathInfo());
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/".equals(path)) {
                getWishlist(request, response, user);
            } else if ("/items".equals(path)) {
                getWishlistItems(request, response, user);
            } else if ("/saved-items".equals(path)) {
                getSavedItems(request, response, user);
            } else if ("/recommendations".equals(path)) {
                getRecommendations(request, response, user);
            } else if ("/personalized".equals(path)) {
                getPersonalizedRecommendations(request, response, user);
            } else if ("/price-drops".equals(path)) {
                getPriceDropNotifications(request, response, user);
            } else if ("/back-in-stock".equals(path)) {
                getBackInStockNotifications(request, response, user);
            } else if ("/trending".equals(path)) {
                getTrendingItems(request, response, user);
            } else if ("/recently-viewed".equals(path)) {
                getRecentlyViewed(request, response, user);
            } else if ("/analytics".equals(path)) {
                getWishlistAnalytics(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in WishlistController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = normalizePath(request.getPathInfo());
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        // CSRF validation for POST requests
        if (!CSRFProtection.validateRequest(request)) {
            sendErrorResponse(response, "Invalid CSRF token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            // Root path: action-based dispatch (used by the frontend JS)
            if ("/".equals(path)) {
                String action = request.getParameter("action");
                if ("toggle".equals(action)) {
                    toggleWishlist(request, response, user);
                } else if ("add".equals(action)) {
                    addToWishlist(request, response, user);
                } else if ("remove".equals(action)) {
                    removeFromWishlist(request, response, user);
                } else {
                    sendErrorResponse(response, "Unknown action: " + action, HttpServletResponse.SC_BAD_REQUEST);
                }
            } else if ("/add".equals(path)) {
                addToWishlist(request, response, user);
            } else if ("/remove".equals(path)) {
                removeFromWishlist(request, response, user);
            } else if ("/move-to-saved".equals(path)) {
                sendErrorResponse(response, "Method not implemented", HttpServletResponse.SC_NOT_IMPLEMENTED);
            } else if ("/restore-from-saved".equals(path)) {
                sendErrorResponse(response, "Method not implemented", HttpServletResponse.SC_NOT_IMPLEMENTED);
            } else if ("/clear".equals(path)) {
                sendErrorResponse(response, "Method not implemented", HttpServletResponse.SC_NOT_IMPLEMENTED);
            } else if ("/bulk-actions".equals(path)) {
                handleBulkActions(request, response, user);
            } else if ("/notify-price-drop".equals(path)) {
                enablePriceDropNotification(request, response, user);
            } else if ("/notify-back-in-stock".equals(path)) {
                enableBackInStockNotification(request, response, user);
            } else if ("/share".equals(path)) {
                shareWishlist(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in WishlistController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /** Toggle: add if absent, remove if present. Returns isFavorite in response. */
    private void toggleWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("success", false);
            data.put("message", "Please login to update your wishlist");
            data.put("redirect", request.getContextPath() + "/login");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            sendJsonResponse(response, data);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Valid product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean isCurrentlyInWishlist = wishlistService.isProductInWishlist(user.getUserId(), productId);
        Map<String, Object> result;
        boolean isFavorite;

        if (isCurrentlyInWishlist) {
            result = wishlistService.removeFromWishlist(user.getUserId(), productId);
            isFavorite = false;
        } else {
            result = wishlistService.addToWishlist(user.getUserId(), productId);
            isFavorite = (Boolean) result.getOrDefault("success", false);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        data.put("isFavorite", isFavorite);
        sendJsonResponse(response, data);
    }

    private void getWishlist(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your wishlist", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // API endpoint - always return JSON
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("items", wishlistService.getWishlistItems(user.getUserId()));
        sendJsonResponse(response, data);
    }

    private void getWishlistItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your wishlist items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 20);
        String category = request.getParameter("category");
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> items = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("items", items);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("category", category);
        
        sendJsonResponse(response, data);
    }

    private void getSavedItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 20);
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> savedItems = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("savedItems", savedItems);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        
        sendJsonResponse(response, data);
    }

    private void getRecommendations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        String type = request.getParameter("type"); // based_on_wishlist, similar, trending
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("recommendations", recommendations);
        data.put("type", type);
        data.put("count", recommendations.size());
        
        sendJsonResponse(response, data);
    }

    private void getPersonalizedRecommendations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to get personalized recommendations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 12);
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> personalized = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("personalized", personalized);
        data.put("count", personalized.size());
        
        sendJsonResponse(response, data);
    }

    private void getPriceDropNotifications(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view price drop notifications", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> notifications = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("notifications", notifications);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        
        sendJsonResponse(response, data);
    }

    private void getBackInStockNotifications(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view back in stock notifications", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> notifications = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("notifications", notifications);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        
        sendJsonResponse(response, data);
    }

    private void getTrendingItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 12);
        String category = request.getParameter("category");
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> trending = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("trending", trending);
        data.put("category", category);
        data.put("count", trending.size());
        
        sendJsonResponse(response, data);
    }

    private void getRecentlyViewed(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 12);
        
        // Placeholder implementation - wishlist service method not available
        List<Map<String, Object>> recentlyViewed = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("recentlyViewed", recentlyViewed);
        data.put("count", recentlyViewed.size());
        
        sendJsonResponse(response, data);
    }

    private void getWishlistAnalytics(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view wishlist analytics", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Placeholder implementation - wishlist service method not available
        Map<String, Object> analytics = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("analytics", analytics);
        
        sendJsonResponse(response, data);
    }

    private void addToWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to add items to wishlist", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = wishlistService.addToWishlist(user.getUserId(), productId);
        boolean success = (Boolean) result.getOrDefault("success", false);
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", result.get("message"));
        data.put("isFavorite", success);
        sendJsonResponse(response, data);
    }

    private void removeFromWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to remove items from wishlist", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = wishlistService.removeFromWishlist(user.getUserId(), productId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        data.put("isFavorite", false);
        sendJsonResponse(response, data);
    }

    private void handleBulkActions(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to perform bulk actions", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = request.getParameter("action"); // remove, move_to_saved, restore
        String[] productIds = request.getParameterValues("productIds");
        
        if (action == null || action.trim().isEmpty()) {
            sendErrorResponse(response, "Action is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (productIds == null || productIds.length == 0) {
            sendErrorResponse(response, "Product IDs are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - wishlist service method not available
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Handle bulk actions not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        if (result.containsKey("affectedCount")) {
            data.put("affectedCount", result.get("affectedCount"));
        }
        
        sendJsonResponse(response, data);
    }

    private void enablePriceDropNotification(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to enable notifications", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean enable = parseBooleanParameter(request.getParameter("enable"), true);
        
        // Placeholder implementation - wishlist service method not available
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Enable price drop notification not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void enableBackInStockNotification(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to enable notifications", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean enable = parseBooleanParameter(request.getParameter("enable"), true);
        
        // Placeholder implementation - wishlist service method not available
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Enable back in stock notification not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void shareWishlist(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to share wishlist", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String shareMethod = request.getParameter("method"); // email, whatsapp, link
        
        // Placeholder implementation - wishlist service method not available
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "Share wishlist not implemented");
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        if (result.containsKey("shareUrl")) {
            data.put("shareUrl", result.get("shareUrl"));
        }
        
        sendJsonResponse(response, data);
    }

    private void sendJsonResponse(HttpServletResponse response, Map<String, Object> data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(data));
    }

    private void sendErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", message);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }

    private int parseIntParameter(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean parseBooleanParameter(String value, boolean defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
