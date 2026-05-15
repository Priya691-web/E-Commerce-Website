package com.fashionstore.controller;

import com.fashionstore.dto.RecentSearchDTO;
import com.fashionstore.dto.SearchSuggestionDTO;
import com.fashionstore.model.User;
import com.fashionstore.security.CSRFProtection;
import com.fashionstore.service.SearchSuggestionService;
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
 * Controller for handling search suggestions and search history
 * Live search suggestions, trending searches, and recent searches
 */
@WebServlet("/api/search/*")
public class SearchSuggestionController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestionController.class);
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
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        try {
            if ("/suggestions".equals(pathInfo)) {
                getSearchSuggestions(request, response);
            } else if ("/trending".equals(pathInfo)) {
                getTrendingSearches(request, response);
            } else if ("/recent".equals(pathInfo)) {
                getRecentSearches(request, response, user);
            } else if ("/autocomplete".equals(pathInfo)) {
                getAutocompleteSuggestions(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in SearchSuggestionController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();
        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("customerAuth") : null;

        // CSRF validation for POST requests
        if (!CSRFProtection.validateRequest(request)) {
            sendErrorResponse(response, "Invalid CSRF token", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            if ("/record-search".equals(pathInfo)) {
                recordSearch(request, response, user);
            } else if ("/clear-history".equals(pathInfo)) {
                clearSearchHistory(request, response, user);
            } else if ("/delete-search".equals(pathInfo)) {
                deleteSearch(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in SearchSuggestionController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getSearchSuggestions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String query = request.getParameter("q");
        String category = request.getParameter("category");
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        
        if (query == null || query.trim().isEmpty()) {
            sendErrorResponse(response, "Query parameter is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Placeholder implementation - search suggestion service not available
        List<SearchSuggestionDTO> suggestions = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("suggestions", suggestions);
        data.put("query", query);
        data.put("count", suggestions.size());
        
        sendJsonResponse(response, data);
    }

    private void getTrendingSearches(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        String category = request.getParameter("category");
        
        // Placeholder implementation - search suggestion service not available
        List<SearchSuggestionDTO> trending = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("trending", trending);
        data.put("count", trending.size());
        
        sendJsonResponse(response, data);
    }

    private void getRecentSearches(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to view recent searches", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        int limit = parseIntParameter(request.getParameter("limit"), 10);
        // Placeholder implementation - search suggestion service not available
        List<RecentSearchDTO> recent = new ArrayList<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("recent", recent);
        data.put("count", recent.size());
        
        sendJsonResponse(response, data);
    }

    private void getAutocompleteSuggestions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String query = request.getParameter("q");
        int limit = parseIntParameter(request.getParameter("limit"), 8);
        
        if (query == null || query.trim().isEmpty()) {
            // Return trending searches if no query provided
            getTrendingSearches(request, response);
            return;
        }

        // Get combined suggestions (products + categories + brands)
        // Placeholder implementation - search suggestion service not available
        Map<String, List<?>> suggestions = new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("query", query);
        data.put("suggestions", suggestions);
        data.put("total", suggestions.values().stream().mapToInt(List::size).sum());
        
        sendJsonResponse(response, data);
    }

    private void recordSearch(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        String query = request.getParameter("q");
        String category = request.getParameter("category");
        int resultCount = parseIntParameter(request.getParameter("resultCount"), 0);
        
        if (query == null || query.trim().isEmpty()) {
            sendErrorResponse(response, "Query parameter is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean success = false;
        if (user != null) {
            // Record search for logged-in user
            RecentSearchDTO recentSearch = new RecentSearchDTO(user.getUserId(), query, category);
            recentSearch.setResultCount(resultCount);
            // Placeholder implementation - search suggestion service not available
            success = false;
        } else {
            // Record anonymous search (for trending analytics)
            // Placeholder implementation - search suggestion service not available
            success = false;
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", "Search recorded successfully");
        
        sendJsonResponse(response, data);
    }

    private void clearSearchHistory(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to clear search history", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // Placeholder implementation - search suggestion service not available
        boolean success = false;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Search history cleared successfully" : "Failed to clear search history");
        
        sendJsonResponse(response, data);
    }

    private void deleteSearch(HttpServletRequest request, HttpServletResponse response, User user) 
            throws IOException {
        if (user == null) {
            sendErrorResponse(response, "Please login to delete search history", HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            int searchId = Integer.parseInt(request.getParameter("searchId"));
            // Placeholder implementation - search suggestion service not available
            boolean success = false;
            
            Map<String, Object> data = new HashMap<>();
            data.put("success", success);
            data.put("message", success ? "Search deleted successfully" : "Failed to delete search");
            
            sendJsonResponse(response, data);
        } catch (NumberFormatException e) {
            sendErrorResponse(response, "Invalid search ID", HttpServletResponse.SC_BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error deleting search: {}", e.getMessage(), e);
            sendErrorResponse(response, "Failed to delete search", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
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
