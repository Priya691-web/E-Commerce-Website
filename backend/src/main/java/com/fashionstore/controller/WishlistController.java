package com.fashionstore.controller;

import com.fashionstore.controller.api.CustomerApiBaseController;
import com.fashionstore.model.User;
import com.fashionstore.registry.ServiceRegistry;
import com.fashionstore.service.WishlistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
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
 * API Controller for wishlist functionality
 * Handles wishlist add/remove and retrieval
 * Returns JSON responses only - use /wishlist for page rendering
 */
@WebServlet({"/api/wishlist", "/api/wishlist/*"})
public class WishlistController extends CustomerApiBaseController {
    private static final Logger logger = LoggerFactory.getLogger(WishlistController.class);
    private WishlistService wishlistService;

    @Override
    public void init() throws ServletException {
        super.init();
        wishlistService = ServiceRegistry.getInstance().getWishlistService();
    }

    /** Normalize servlet pathInfo: treat null/blank as root slash. */
    private String normalizePath(String pathInfo) {
        return (pathInfo == null || pathInfo.isBlank()) ? "/" : pathInfo;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        applyCors(request, response);
        
        String path = normalizePath(request.getPathInfo());
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/".equals(path)) {
                String action = request.getParameter("action");
                if ("check".equals(action)) {
                    checkWishlistStatus(request, response, user);
                } else {
                    getWishlist(request, response, user);
                }
            } else {
                writeApiResponse(response, 404, ApiResponse.error("Endpoint not found"));
            }
        } catch (Exception e) {
            logger.error("Error in WishlistController doGet: {}", e.getMessage(), e);
            handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        applyCors(request, response);
        
        String path = normalizePath(request.getPathInfo());
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        // CSRF validation disabled for development/demo
        // CSRF validation for POST requests
        // if (!CSRFProtection.validateRequest(request)) {
        //     writeApiResponse(response, 403, ApiResponse.error("Invalid CSRF token"));
        //     return;
        // }

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
                    writeApiResponse(response, 400, ApiResponse.error("Unknown action: " + action));
                }
            } else if ("/add".equals(path)) {
                addToWishlist(request, response, user);
            } else if ("/remove".equals(path)) {
                removeFromWishlist(request, response, user);
            } else {
                writeApiResponse(response, 404, ApiResponse.error("Endpoint not found"));
            }
        } catch (Exception e) {
            logger.error("Error in WishlistController doPost: {}", e.getMessage(), e);
            handleException(response, e);
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
            data.put("isFavorite", false);
            writeApiResponse(response, 401, ApiResponse.success("Please login to update your wishlist", data));
            return;
        }

        // Read productId from JSON body or query parameter
        int productId = 0;
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null && !productIdParam.isEmpty()) {
            productId = parseInt(productIdParam, 0);
        } else if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            // Try to read from JSON body
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> payload = mapper.readValue(request.getReader(), Map.class);
                if (payload != null && payload.containsKey("productId")) {
                    Object productIdObj = payload.get("productId");
                    if (productIdObj != null) {
                        productId = Integer.parseInt(String.valueOf(productIdObj));
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to parse JSON wishlist request", e);
            }
        }
        
        if (productId <= 0) {
            writeApiResponse(response, 400, ApiResponse.error("Valid product ID is required"));
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
        data.put("wishlistCount", wishlistService.getWishlistItems(user.getUserId()).size());
        writeApiResponse(response, 200, ApiResponse.success("Success", data));
    }

    /** Check if a product is in the user's wishlist. Returns isFavorite in response. */
    private void checkWishlistStatus(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        int productId = parseInt(request.getParameter("productId"), 0);
        if (productId <= 0) {
            writeApiResponse(response, 400, ApiResponse.error("Valid product ID is required"));
            return;
        }

        boolean isFavorite = false;
        if (user != null) {
            isFavorite = wishlistService.isProductInWishlist(user.getUserId(), productId);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("isFavorite", isFavorite);
        writeApiResponse(response, 200, ApiResponse.success("Success", data));
    }

    private void getWishlist(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            Map<String, Object> data = new HashMap<>();
            data.put("items", new ArrayList<>());
            data.put("count", 0);
            writeApiResponse(response, 401, ApiResponse.success("Please login to view your wishlist", data));
            return;
        }

        // API endpoint - always return JSON
        Map<String, Object> data = new HashMap<>();
        List<com.fashionstore.model.WishlistItem> items = wishlistService.getWishlistItems(user.getUserId());
        data.put("success", true);
        data.put("items", items);
        data.put("count", items.size());
        writeApiResponse(response, 200, ApiResponse.success("Success", data));
    }

    private void addToWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user == null) {
            writeApiResponse(response, 401, ApiResponse.error("Please login to add items to wishlist"));
            return;
        }

        // Read productId from JSON body or query parameter
        int productId = 0;
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null && !productIdParam.isEmpty()) {
            productId = parseInt(productIdParam, 0);
        } else if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            // Try to read from JSON body
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> payload = mapper.readValue(request.getReader(), Map.class);
                if (payload != null && payload.containsKey("productId")) {
                    Object productIdObj = payload.get("productId");
                    if (productIdObj != null) {
                        productId = Integer.parseInt(String.valueOf(productIdObj));
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to parse JSON wishlist request", e);
            }
        }
        
        if (productId <= 0) {
            writeApiResponse(response, 400, ApiResponse.error("Product ID is required"));
            return;
        }

        Map<String, Object> result = wishlistService.addToWishlist(user.getUserId(), productId);
        boolean success = (Boolean) result.getOrDefault("success", false);
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", result.get("message"));
        data.put("isFavorite", success);
        data.put("wishlistCount", wishlistService.getWishlistItems(user.getUserId()).size());
        writeApiResponse(response, 200, ApiResponse.success("Success", data));
    }

    private void removeFromWishlist(HttpServletRequest request, HttpServletResponse response, User user)
            throws IOException {
        if (user == null) {
            writeApiResponse(response, 401, ApiResponse.error("Please login to remove items from wishlist"));
            return;
        }

        // Read productId from JSON body or query parameter
        int productId = 0;
        String productIdParam = request.getParameter("productId");
        if (productIdParam != null && !productIdParam.isEmpty()) {
            productId = parseInt(productIdParam, 0);
        } else if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            // Try to read from JSON body
            try {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> payload = mapper.readValue(request.getReader(), Map.class);
                if (payload != null && payload.containsKey("productId")) {
                    Object productIdObj = payload.get("productId");
                    if (productIdObj != null) {
                        productId = Integer.parseInt(String.valueOf(productIdObj));
                    }
                }
            } catch (Exception e) {
                logger.warn("Failed to parse JSON wishlist request", e);
            }
        }
        
        if (productId <= 0) {
            writeApiResponse(response, 400, ApiResponse.error("Product ID is required"));
            return;
        }

        Map<String, Object> result = wishlistService.removeFromWishlist(user.getUserId(), productId);
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        data.put("isFavorite", false);
        data.put("wishlistCount", wishlistService.getWishlistItems(user.getUserId()).size());
        writeApiResponse(response, 200, ApiResponse.success("Success", data));
    }
}
