package com.fashionstore.controller;

import com.fashionstore.model.User;
import com.fashionstore.service.PersonalizationService;
import com.fashionstore.util.JsonUtil;
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
 * Controller for personalization and recommendation features
 * Handles user behavior tracking and personalized content delivery
 */
@WebServlet("/personalization/*")
public class PersonalizationController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(PersonalizationController.class);
    private PersonalizationService personalizationService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        personalizationService = new PersonalizationService();
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/recommendations".equals(pathInfo)) {
                getRecommendations(request, response, user);
            } else if ("/for-you".equals(pathInfo)) {
                getForYouRecommendations(request, response, user);
            } else if ("/based-on-wishlist".equals(pathInfo)) {
                getWishlistBasedRecommendations(request, response, user);
            } else if ("/based-on-purchases".equals(pathInfo)) {
                getPurchaseBasedRecommendations(request, response, user);
            } else if ("/reorder-suggestions".equals(pathInfo)) {
                getReorderSuggestions(request, response, user);
            } else if ("/trending-near-you".equals(pathInfo)) {
                getTrendingNearYou(request, response, user);
            } else if ("/people-also-bought".equals(pathInfo)) {
                getPeopleAlsoBought(request, response, user);
            } else if ("/price-dropped".equals(pathInfo)) {
                getPriceDroppedItems(request, response, user);
            } else if ("/back-in-stock".equals(pathInfo)) {
                getBackInStockItems(request, response, user);
            } else if ("/user-profile".equals(pathInfo)) {
                getUserProfile(request, response, user);
            } else if ("/engagement-metrics".equals(pathInfo)) {
                getEngagementMetrics(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in PersonalizationController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/track-view".equals(pathInfo)) {
                trackProductView(request, response, user);
            } else if ("/track-click".equals(pathInfo)) {
                trackProductClick(request, response, user);
            } else if ("/track-search".equals(pathInfo)) {
                trackSearchQuery(request, response, user);
            } else if ("/track-add-to-cart".equals(pathInfo)) {
                trackAddToCart(request, response, user);
            } else if ("/track-purchase".equals(pathInfo)) {
                trackPurchase(request, response, user);
            } else if ("/update-preferences".equals(pathInfo)) {
                updatePreferences(request, response, user);
            } else if ("/feedback".equals(pathInfo)) {
                submitRecommendationFeedback(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in PersonalizationController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getRecommendations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 12);
        String type = request.getParameter("type"); // for_you, trending, similar
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        // List<Map<String, Object>> recommendations = personalizationService.getRecommendations(
        // PersonalizationService methods don't exist, commenting out for now
        // user != null ? user.getUserId() : null, type, limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("recommendations", recommendations);
        data.put("type", type);
        data.put("count", recommendations.size());
        
        sendJsonResponse(response, data);
    }

    private void getForYouRecommendations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to get personalized recommendations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 12);
        String context = request.getParameter("context"); // homepage, product_page, cart_page
        
        List<Map<String, Object>> forYou = new ArrayList<>();
        // List<Map<String, Object>> forYou = personalizationService.getForYouRecommendations(
        // PersonalizationService methods don't exist, commenting out for now
        // user.getUserId(), limit, context);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("forYou", forYou);
        data.put("context", context);
        data.put("count", forYou.size());
        
        sendJsonResponse(response, data);
    }

    private void getWishlistBasedRecommendations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to get wishlist-based recommendations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 12);
        
        List<Map<String, Object>> wishlistBased = new ArrayList<>();
        // List<Map<String, Object>> wishlistBased = personalizationService.getWishlistBasedRecommendations(
        // PersonalizationService methods don't exist, commenting out for now
        // user.getUserId(), limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("wishlistBased", wishlistBased);
        data.put("count", wishlistBased.size());
        
        sendJsonResponse(response, data);
    }

    private void getPurchaseBasedRecommendations(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to get purchase-based recommendations", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 12);
        
        List<Map<String, Object>> purchaseBased = new ArrayList<>();
        // List<Map<String, Object>> purchaseBased = personalizationService.getPurchaseBasedRecommendations(
        // PersonalizationService methods don't exist, commenting out for now
        // user.getUserId(), limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("purchaseBased", purchaseBased);
        data.put("count", purchaseBased.size());
        
        sendJsonResponse(response, data);
    }

    private void getReorderSuggestions(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to get reorder suggestions", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 8);
        
        List<Map<String, Object>> reorderSuggestions = new ArrayList<>();
        // List<Map<String, Object>> reorderSuggestions = personalizationService.getReorderSuggestions(
        // PersonalizationService methods don't exist, commenting out for now
        // user.getUserId(), limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("reorderSuggestions", reorderSuggestions);
        data.put("count", reorderSuggestions.size());
        
        sendJsonResponse(response, data);
    }

    private void getTrendingNearYou(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 12);
        String location = request.getParameter("location");
        
        List<Map<String, Object>> trendingNearYou = new ArrayList<>();
        // List<Map<String, Object>> trendingNearYou = personalizationService.getTrendingNearYou(
        // PersonalizationService methods don't exist, commenting out for now
        // user != null ? user.getUserId() : null, location, limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("trendingNearYou", trendingNearYou);
        data.put("location", location);
        data.put("count", trendingNearYou.size());
        
        sendJsonResponse(response, data);
    }

    private void getPeopleAlsoBought(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        int limit = parseIntParameter(request.getParameter("limit"), 8);
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        List<Map<String, Object>> peopleAlsoBought = new ArrayList<>();
        // List<Map<String, Object>> peopleAlsoBought = personalizationService.getPeopleAlsoBought(
        // PersonalizationService methods don't exist, commenting out for now
        // productId, user != null ? user.getUserId() : null, limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("peopleAlsoBought", peopleAlsoBought);
        data.put("productId", productId);
        data.put("count", peopleAlsoBought.size());
        
        sendJsonResponse(response, data);
    }

    private void getPriceDroppedItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 12);
        String category = request.getParameter("category");
        
        List<Map<String, Object>> priceDropped = new ArrayList<>();
        // List<Map<String, Object>> priceDropped = personalizationService.getPriceDroppedItems(
        // PersonalizationService methods don't exist, commenting out for now
        // user != null ? user.getUserId() : null, category, limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("priceDropped", priceDropped);
        data.put("category", category);
        data.put("count", priceDropped.size());
        
        sendJsonResponse(response, data);
    }

    private void getBackInStockItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 12);
        String category = request.getParameter("category");
        
        List<Map<String, Object>> backInStock = new ArrayList<>();
        // List<Map<String, Object>> backInStock = personalizationService.getBackInStockItems(
        // PersonalizationService methods don't exist, commenting out for now
        // user != null ? user.getUserId() : null, category, limit);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("backInStock", backInStock);
        data.put("category", category);
        data.put("count", backInStock.size());
        
        sendJsonResponse(response, data);
    }

    private void getUserProfile(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your profile", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<String, Object> userProfile = new HashMap<>();
        // Map<String, Object> userProfile = personalizationService.getUserProfile(user.getUserId());
        // PersonalizationService methods don't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("userProfile", userProfile);
        
        sendJsonResponse(response, data);
    }

    private void getEngagementMetrics(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view engagement metrics", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<String, Object> metrics = new HashMap<>();
        // Map<String, Object> metrics = personalizationService.getEngagementMetrics(user.getUserId());
        // PersonalizationService methods don't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("metrics", metrics);
        
        sendJsonResponse(response, data);
    }

    private void trackProductView(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String source = request.getParameter("source"); // product_list, search, recommendation, direct
        String context = request.getParameter("context"); // homepage, category_page, product_page
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = personalizationService.trackProductView(
        // PersonalizationService methods don't exist, commenting out for now
        result.put("success", false);
        // productId, user != null ? user.getUserId() : null, source, context);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void trackProductClick(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int position = parseIntParameter(request.getParameter("position"), 0);
        String source = request.getParameter("source");

        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = personalizationService.trackProductClick(
        // PersonalizationService methods don't exist, commenting out for now
        result.put("success", false);
        // productId, user != null ? user.getUserId() : null, position, source);

        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));

        sendJsonResponse(response, data);
    }

    private void trackSearchQuery(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        String query = request.getParameter("query");
        if (query == null || query.trim().isEmpty()) {
            sendErrorResponse(response, "Search query is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int resultCount = parseIntParameter(request.getParameter("resultCount"), 0);
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = personalizationService.trackSearchQuery(
        // PersonalizationService methods don't exist, commenting out for now
        result.put("success", false);
        // query, user != null ? user.getUserId() : null, resultCount);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void trackAddToCart(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String source = request.getParameter("source"); // product_page, wishlist, saved_items
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = personalizationService.trackAddToCart(
        // PersonalizationService methods don't exist, commenting out for now
        result.put("success", false);
        // productId, user != null ? user.getUserId() : null, source);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void trackPurchase(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to track purchases", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int orderId = parseIntParameter(request.getParameter("orderId"), 0);
        if (orderId <= 0) {
            sendErrorResponse(response, "Order ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String[] productIds = request.getParameterValues("productIds");
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = personalizationService.trackPurchase(
        // PersonalizationService methods don't exist, commenting out for now
        result.put("success", false);
        // orderId, user != null ? user.getUserId() : null, productIds);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void updatePreferences(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to update preferences", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Map<String, Object> preferences = objectMapper.readValue(request.getReader(), Map.class);
            
            Map<String, Object> result = new HashMap<>();
            // Map<String, Object> result = personalizationService.updatePreferences(user.getUserId(), preferences);
            // PersonalizationService methods don't exist, commenting out for now
            result.put("success", false);
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", result.get("success"));
            data.put("message", result.get("message"));
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error parsing preferences data: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid preferences data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void submitRecommendationFeedback(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to submit feedback", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String feedback = request.getParameter("feedback"); // like, dislike, not_interested
        String reason = request.getParameter("reason"); // not_relevant, wrong_category, etc.
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = personalizationService.submitRecommendationFeedback(
        // PersonalizationService methods don't exist, commenting out for now
        result.put("success", false);
        // user.getUserId(), productId, feedback, reason);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
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
}
