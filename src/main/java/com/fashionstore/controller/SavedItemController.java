package com.fashionstore.controller;

import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
// import com.fashionstore.service.SavedItemService;
// SavedItemService class doesn't exist, commenting out import
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
 * Controller for saved items functionality
 * Handles cart-to-saved movement and saved items management
 */
@WebServlet("/saved-items/*")
public class SavedItemController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SavedItemController.class);
    // private SavedItemService savedItemService;
    // SavedItemService class doesn't exist, commenting out field declaration
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // savedItemService = new SavedItemService();
        // SavedItemService class doesn't exist, commenting out for now
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        try {
            if ("/".equals(pathInfo) || "".equals(pathInfo)) {
                getSavedItems(request, response, user);
            } else if ("/list".equals(pathInfo)) {
                getSavedItemsList(request, response, user);
            } else if ("/cart-items".equals(pathInfo)) {
                getCartItemsForSaving(request, response, user);
            } else if ("/archived".equals(pathInfo)) {
                getArchivedItems(request, response, user);
            } else if ("/analytics".equals(pathInfo)) {
                getSavedItemsAnalytics(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in SavedItemController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // CSRF validation for POST requests
        if (!CSRFProtection.validateRequest(request)) {
            sendErrorResponse(response, "Invalid CSRF token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("/move-from-cart".equals(pathInfo)) {
                moveFromCartToSaved(request, response, user);
            } else if ("/restore-to-cart".equals(pathInfo)) {
                restoreToCart(request, response, user);
            } else if ("/remove".equals(pathInfo)) {
                removeSavedItem(request, response, user);
            } else if ("/archive".equals(pathInfo)) {
                archiveSavedItem(request, response, user);
            } else if ("/unarchive".equals(pathInfo)) {
                unarchiveSavedItem(request, response, user);
            } else if ("/bulk-actions".equals(pathInfo)) {
                handleBulkActions(request, response, user);
            } else if ("/clear".equals(pathInfo)) {
                clearSavedItems(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in SavedItemController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getSavedItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 20);
        String sortBy = request.getParameter("sortBy"); // date_saved, price_low, price_high, name
        boolean includeArchived = parseBooleanParameter(request.getParameter("includeArchived"), false);
        
        List<Map<String, Object>> savedItems = new ArrayList<>();
        // List<Map<String, Object>> savedItems = savedItemService.getSavedItems(
        // SavedItemService class doesn't exist, commenting out for now
        // user.getUserId(), page, limit, sortBy, includeArchived);
        int totalCount = 0;
        // int totalCount = savedItemService.getSavedItemsCount(user.getUserId(), includeArchived);
        // SavedItemService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("savedItems", savedItems);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("sortBy", sortBy);
        data.put("includeArchived", includeArchived);
        
        sendJsonResponse(response, data);
    }

    private void getSavedItemsList(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 50);
        String category = request.getParameter("category");
        
        List<Map<String, Object>> savedItems = new ArrayList<>();
        // List<Map<String, Object>> savedItems = savedItemService.getSavedItemsList(
        // SavedItemService class doesn't exist, commenting out for now
        // user.getUserId(), limit, category);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("savedItems", savedItems);
        data.put("count", savedItems.size());
        data.put("category", category);
        
        sendJsonResponse(response, data);
    }

    private void getCartItemsForSaving(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view cart items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        List<Map<String, Object>> cartItems = new ArrayList<>();
        // List<Map<String, Object>> cartItems = savedItemService.getCartItemsForSaving(user.getUserId());
        // SavedItemService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("cartItems", cartItems);
        data.put("count", cartItems.size());
        
        sendJsonResponse(response, data);
    }

    private void getArchivedItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view archived items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 20);
        
        List<Map<String, Object>> archivedItems = new ArrayList<>();
        // List<Map<String, Object>> archivedItems = savedItemService.getArchivedItems(user.getUserId(), page, limit);
        // int totalCount = savedItemService.getArchivedItemsCount(user.getUserId());
        // SavedItemService class doesn't exist, commenting out for now
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("archivedItems", archivedItems);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        
        sendJsonResponse(response, data);
    }

    private void getSavedItemsAnalytics(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view saved items analytics", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<String, Object> analytics = new HashMap<>();
        // Map<String, Object> analytics = savedItemService.getSavedItemsAnalytics(user.getUserId());
        // SavedItemService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("analytics", analytics);
        
        sendJsonResponse(response, data);
    }

    private void moveFromCartToSaved(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to move items to saved", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.moveFromCartToSaved(user.getUserId(), productId);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        if (result.containsKey("savedItem")) {
            data.put("savedItem", result.get("savedItem"));
        }
        
        sendJsonResponse(response, data);
    }

    private void restoreToCart(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to restore items to cart", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int quantity = parseIntParameter(request.getParameter("quantity"), 1);
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.restoreToCart(user.getUserId(), productId, quantity);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        if (result.containsKey("cartItem")) {
            data.put("cartItem", result.get("cartItem"));
        }
        
        sendJsonResponse(response, data);
    }

    private void removeSavedItem(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to remove saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.removeSavedItem(user.getUserId(), productId);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void archiveSavedItem(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to archive saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.archiveSavedItem(user.getUserId(), productId);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void unarchiveSavedItem(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to unarchive saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int productId = parseIntParameter(request.getParameter("productId"), 0);
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.unarchiveSavedItem(user.getUserId(), productId);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        
        sendJsonResponse(response, data);
    }

    private void handleBulkActions(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to perform bulk actions", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String action = request.getParameter("action"); // remove, archive, unarchive, restore_to_cart
        String[] productIds = request.getParameterValues("productIds");
        
        if (action == null || action.trim().isEmpty()) {
            sendErrorResponse(response, "Action is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (productIds == null || productIds.length == 0) {
            sendErrorResponse(response, "Product IDs are required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.handleBulkActions(user.getUserId(), action, productIds);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        if (result.containsKey("affectedCount")) {
            data.put("affectedCount", result.get("affectedCount"));
        }
        
        sendJsonResponse(response, data);
    }

    private void clearSavedItems(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to clear saved items", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        boolean includeArchived = parseBooleanParameter(request.getParameter("includeArchived"), false);
        
        Map<String, Object> result = new HashMap<>();
        // Map<String, Object> result = savedItemService.clearSavedItems(user.getUserId(), includeArchived);
        // SavedItemService class doesn't exist, commenting out for now
        result.put("success", false);
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", result.get("success"));
        data.put("message", result.get("message"));
        if (result.containsKey("clearedCount")) {
            data.put("clearedCount", result.get("clearedCount"));
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
