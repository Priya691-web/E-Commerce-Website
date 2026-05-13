package com.fashionstore.controller;

import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
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
 * Controller for product reviews and ratings
 * Handles review submission, retrieval, and aggregation
 */
@WebServlet("/api/products/reviews/*")
public class ProductReviewController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductReviewController.class);
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        try {
            if ("/product-reviews".equals(pathInfo)) {
                getProductReviews(request, response, user);
            } else if ("/review-summary".equals(pathInfo)) {
                getReviewSummary(request, response);
            } else if ("/user-reviews".equals(pathInfo)) {
                getUserReviews(request, response, user);
            } else if ("/review-stats".equals(pathInfo)) {
                getReviewStats(request, response);
            } else if ("/top-reviews".equals(pathInfo)) {
                getTopReviews(request, response);
            } else if ("/recent-reviews".equals(pathInfo)) {
                getRecentReviews(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in ProductReviewController doGet: {}", e.getMessage(), e);
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
            if ("/submit-review".equals(pathInfo)) {
                submitReview(request, response, user);
            } else if ("/update-review".equals(pathInfo)) {
                updateReview(request, response, user);
            } else if ("/delete-review".equals(pathInfo)) {
                deleteReview(request, response, user);
            } else if ("/helpful-vote".equals(pathInfo)) {
                castHelpfulVote(request, response, user);
            } else if ("/report-review".equals(pathInfo)) {
                reportReview(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in ProductReviewController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getProductReviews(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        String sortBy = request.getParameter("sortBy"); // newest, oldest, highest, lowest, most_helpful
        String ratingFilter = request.getParameter("rating"); // 1,2,3,4,5
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        List<Map<String, Object>> reviews = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("reviews", reviews);
        data.put("productId", productId);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("sortBy", sortBy);
        data.put("ratingFilter", ratingFilter);
        
        sendJsonResponse(response, data);
    }

    private void getReviewSummary(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        Map<String, Object> summary = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("summary", summary);
        data.put("productId", productId);
        
        sendJsonResponse(response, data);
    }

    private void getUserReviews(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to view your reviews", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        String status = request.getParameter("status"); // published, pending, rejected
        
        // Placeholder implementation - review service not available
        List<Map<String, Object>> userReviews = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("reviews", userReviews);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalCount", totalCount);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("status", status);
        
        sendJsonResponse(response, data);
    }

    private void getReviewStats(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("stats", stats);
        data.put("productId", productId);
        
        sendJsonResponse(response, data);
    }

    private void getTopReviews(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        int limit = parseIntParameter(request.getParameter("limit"), 5);
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        List<Map<String, Object>> topReviews = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("topReviews", topReviews);
        data.put("productId", productId);
        data.put("count", topReviews.size());
        
        sendJsonResponse(response, data);
    }

    private void getRecentReviews(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int productId = parseIntParameter(request.getParameter("productId"), 0);
        int limit = parseIntParameter(request.getParameter("limit"), 5);
        
        if (productId <= 0) {
            sendErrorResponse(response, "Product ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        List<Map<String, Object>> recentReviews = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("recentReviews", recentReviews);
        data.put("productId", productId);
        data.put("count", recentReviews.size());
        
        sendJsonResponse(response, data);
    }

    private void submitReview(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to submit a review", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            Map<String, Object> review = objectMapper.readValue(request.getReader(), Map.class);
            review.put("userId", user.getUserId());
            review.put("userName", user.getFullName());
            
            // Placeholder implementation - review service not available
            Map<String, Object> submittedReview = new HashMap<>();
            
            Map<String, Object> data = new HashMap<>();
            if (submittedReview != null) {
                data.put("success", true);
                data.put("review", submittedReview);
                data.put("message", "Review submitted successfully. It will be visible after moderation.");
            } else {
                data.put("success", false);
                data.put("message", "Failed to submit review");
            }
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error parsing review data: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid review data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void updateReview(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to update a review", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int reviewId = parseIntParameter(request.getParameter("reviewId"), 0);
        
        if (reviewId <= 0) {
            sendErrorResponse(response, "Review ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Map<String, Object> review = objectMapper.readValue(request.getReader(), Map.class);
            review.put("reviewId", reviewId);
            review.put("userId", user.getUserId());
            
            // Placeholder implementation - review service not available
            Map<String, Object> updatedReview = new HashMap<>();
            
            Map<String, Object> data = new HashMap<>();
            if (updatedReview != null) {
                data.put("success", true);
                data.put("review", updatedReview);
                data.put("message", "Review updated successfully");
            } else {
                data.put("success", false);
                data.put("message", "Failed to update review");
            }
            
            sendJsonResponse(response, data);
        } catch (Exception e) {
            logger.error("Error parsing review data: {}", e.getMessage(), e);
            sendErrorResponse(response, "Invalid review data", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void deleteReview(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to delete a review", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int reviewId = parseIntParameter(request.getParameter("reviewId"), 0);
        
        if (reviewId <= 0) {
            sendErrorResponse(response, "Review ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Review deleted successfully" : "Failed to delete review");
        
        sendJsonResponse(response, data);
    }

    private void castHelpfulVote(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to vote on reviews", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int reviewId = parseIntParameter(request.getParameter("reviewId"), 0);
        boolean isHelpful = parseBooleanParameter(request.getParameter("helpful"), false);
        
        if (reviewId <= 0) {
            sendErrorResponse(response, "Review ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Vote recorded successfully" : "Failed to record vote");
        
        sendJsonResponse(response, data);
    }

    private void reportReview(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        
        if (user == null) {
            sendErrorResponse(response, "Please login to report a review", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int reviewId = parseIntParameter(request.getParameter("reviewId"), 0);
        String reason = request.getParameter("reason");
        String description = request.getParameter("description");
        
        if (reviewId <= 0) {
            sendErrorResponse(response, "Review ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - review service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Review reported successfully" : "Failed to report review");
        
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
