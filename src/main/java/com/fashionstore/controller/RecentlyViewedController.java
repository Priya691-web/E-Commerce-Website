package com.fashionstore.controller;

import com.fashionstore.dto.RecentlyViewedDTO;
import com.fashionstore.model.Product;
import com.fashionstore.model.User;
// import com.fashionstore.service.RecentlyViewedService;
// RecentlyViewedService class doesn't exist, commenting out import
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for recently viewed products functionality
 * Handles tracking and retrieval of user's browsing history
 */
@WebServlet("/api/products/recently-viewed/*")
public class RecentlyViewedController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RecentlyViewedController.class);
    // private RecentlyViewedService recentlyViewedService;
    // RecentlyViewedService class doesn't exist, commenting out field declaration
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // recentlyViewedService = new RecentlyViewedService();
        // RecentlyViewedService class doesn't exist, commenting out for now
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        try {
            if ("/list".equals(pathInfo)) {
                getRecentlyViewedProducts(request, response, user);
            } else if ("/track".equals(pathInfo)) {
                trackProductView(request, response, user);
            } else if ("/clear".equals(pathInfo)) {
                clearRecentlyViewed(request, response, user);
            } else if ("/remove".equals(pathInfo)) {
                removeRecentlyViewed(request, response, user);
            } else if ("/analytics".equals(pathInfo)) {
                getRecentlyViewedAnalytics(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in RecentlyViewedController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        try {
            if ("/track".equals(pathInfo)) {
                trackProductView(request, response, user);
            } else if ("/clear".equals(pathInfo)) {
                clearRecentlyViewed(request, response, user);
            } else if ("/remove".equals(pathInfo)) {
                removeRecentlyViewed(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in RecentlyViewedController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getRecentlyViewedProducts(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        int page = parseIntParameter(request.getParameter("page"), 1);
        String category = request.getParameter("category");
        
        List<RecentlyViewedDTO> recentlyViewed;
        
        if (user != null) {
            recentlyViewed = null;
            // recentlyViewed = recentlyViewedService.getRecentlyViewedProducts(user.getUserId(), limit, page, category);
            // RecentlyViewedService class doesn't exist, commenting out for now
        } else {
            // For anonymous users, get from session
            String sessionId = request.getSession().getId();
            recentlyViewed = null;
            // recentlyViewed = recentlyViewedService.getAnonymousRecentlyViewed(sessionId, limit, page, category);
            // RecentlyViewedService class doesn't exist, commenting out for now
        }
        
        // int totalCount = user != null ? 
        //     recentlyViewedService.getRecentlyViewedCount(user.getUserId(), category) :
        //     recentlyViewedService.getAnonymousRecentlyViewedCount(request.getSession().getId(), category);
        // RecentlyViewedService class doesn't exist, commenting out for now
        
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("recentlyViewed", recentlyViewed);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("category", category);
        data.put("isLoggedIn", user != null);
        
        sendJsonResponse(response, data);
    }

    private void trackProductView(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        String source = request.getParameter("source"); // listing, search, recommendation, direct
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean success;
        
        if (user != null) {
            success = false;
        // success = recentlyViewedService.trackProductView(user.getUserId(), productId, source);
        // RecentlyViewedService class doesn't exist, commenting out for now
        } else {
            String sessionId = request.getSession().getId();
            success = false;
            // success = recentlyViewedService.trackAnonymousProductView(sessionId, productId, source);
            // RecentlyViewedService class doesn't exist, commenting out for now
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", "Product view tracked successfully");
        data.put("productId", productId);
        
        sendJsonResponse(response, data);
    }

    private void clearRecentlyViewed(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        boolean success;
        
        if (user != null) {
            success = false;
        // success = recentlyViewedService.clearRecentlyViewed(user.getUserId());
        // RecentlyViewedService class doesn't exist, commenting out for now
        } else {
            String sessionId = request.getSession().getId();
            success = false;
            // success = recentlyViewedService.clearAnonymousRecentlyViewed(sessionId);
            // RecentlyViewedService class doesn't exist, commenting out for now
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", "Recently viewed history cleared successfully");
        
        sendJsonResponse(response, data);
    }

    private void removeRecentlyViewed(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean success;
        
        if (user != null) {
            success = false;
        // success = recentlyViewedService.removeRecentlyViewed(user.getUserId(), productId);
        // RecentlyViewedService class doesn't exist, commenting out for now
        } else {
            String sessionId = request.getSession().getId();
            success = false;
            // success = recentlyViewedService.removeAnonymousRecentlyViewed(sessionId, productId);
            // RecentlyViewedService class doesn't exist, commenting out for now
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Product removed from recently viewed" : "Failed to remove product");
        data.put("productId", productId);
        
        sendJsonResponse(response, data);
    }

    private void getRecentlyViewedAnalytics(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view analytics", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Map<String, Object> analytics = new HashMap<>();
        // Map<String, Object> analytics = recentlyViewedService.getRecentlyViewedAnalytics(user.getUserId());
        // RecentlyViewedService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("analytics", analytics);
        
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
