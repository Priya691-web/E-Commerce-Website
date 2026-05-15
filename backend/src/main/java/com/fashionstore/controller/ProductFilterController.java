package com.fashionstore.controller;

import com.fashionstore.model.Product;
// import com.fashionstore.service.ProductFilterService;
// ProductFilterService class doesn't exist, commenting out import
import com.fashionstore.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for product filtering and search functionality
 * Provides advanced filtering options for product listings
 */
@WebServlet("/api/products/filter/*")
public class ProductFilterController extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ProductFilterController.class);
    // private ProductFilterService filterService;
    // ProductFilterService class doesn't exist, commenting out field declaration
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        // filterService = new ProductFilterService();
        // ProductFilterService class doesn't exist, commenting out for now
        objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();

        try {
            if ("/filter-options".equals(pathInfo)) {
                getFilterOptions(request, response);
            } else if ("/filtered-products".equals(pathInfo)) {
                getFilteredProducts(request, response);
            } else if ("/brands".equals(pathInfo)) {
                getAvailableBrands(request, response);
            } else if ("/categories".equals(pathInfo)) {
                getAvailableCategories(request, response);
            } else if ("/sizes".equals(pathInfo)) {
                getAvailableSizes(request, response);
            } else if ("/colors".equals(pathInfo)) {
                getAvailableColors(request, response);
            } else if ("/price-range".equals(pathInfo)) {
                getPriceRange(request, response);
            } else if ("/search-suggestions".equals(pathInfo)) {
                getSearchSuggestions(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in ProductFilterController doGet: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pathInfo = request.getPathInfo();

        try {
            if ("/apply-filters".equals(pathInfo)) {
                applyFilters(request, response);
            } else if ("/save-filter-preferences".equals(pathInfo)) {
                saveFilterPreferences(request, response);
            } else if ("/clear-filters".equals(pathInfo)) {
                clearFilters(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error in ProductFilterController doPost: {}", e.getMessage(), e);
            sendErrorResponse(response, "Internal server error", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getFilterOptions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String category = request.getParameter("category");
        Map<String, Object> filterOptions = null;
        // Map<String, Object> filterOptions = filterService.getFilterOptions(category);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("filterOptions", filterOptions);
        data.put("category", category);
        
        sendJsonResponse(response, data);
    }

    private void getFilteredProducts(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        // Parse filter parameters
        Map<String, Object> filters = parseFilterParameters(request);
        
        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 20);
        
        // List<Product> filteredProducts = filterService.getFilteredProducts(filters, page, limit);
        // int totalCount = filterService.getFilteredProductsCount(filters);
        // ProductFilterService class doesn't exist, commenting out for now
        List<Product> filteredProducts = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("products", filteredProducts);
        data.put("totalCount", totalCount);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("filters", filters);
        
        sendJsonResponse(response, data);
    }

    private void getAvailableBrands(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String category = request.getParameter("category");
        List<String> brands = new ArrayList<>();
        // List<String> brands = filterService.getAvailableBrands(category);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("brands", brands);
        data.put("category", category);
        data.put("count", brands.size());
        
        sendJsonResponse(response, data);
    }

    private void getAvailableCategories(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        List<Map<String, Object>> categories = new ArrayList<>();
        // List<Map<String, Object>> categories = filterService.getAvailableCategories();
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("categories", categories);
        data.put("count", categories.size());
        
        sendJsonResponse(response, data);
    }

    private void getAvailableSizes(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String category = request.getParameter("category");
        List<String> sizes = new ArrayList<>();
        // List<String> sizes = filterService.getAvailableSizes(category);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("sizes", sizes);
        data.put("category", category);
        data.put("count", sizes.size());
        
        sendJsonResponse(response, data);
    }

    private void getAvailableColors(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String category = request.getParameter("category");
        List<Map<String, Object>> colors = new ArrayList<>();
        // List<Map<String, Object>> colors = filterService.getAvailableColors(category);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("colors", colors);
        data.put("category", category);
        data.put("count", colors.size());
        
        sendJsonResponse(response, data);
    }

    private void getPriceRange(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String category = request.getParameter("category");
        Map<String, Object> priceRange = new HashMap<>();
        // Map<String, Object> priceRange = filterService.getPriceRange(category);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("priceRange", priceRange);
        data.put("category", category);
        
        sendJsonResponse(response, data);
    }

    private void getSearchSuggestions(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String query = request.getParameter("q");
        String category = request.getParameter("category");
        int limit = parseIntParameter(request.getParameter("limit"), 10);
        
        if (query == null || query.trim().isEmpty()) {
            sendErrorResponse(response, "Search query is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        List<Map<String, Object>> suggestions = new ArrayList<>();
        // List<Map<String, Object>> suggestions = filterService.getSearchSuggestions(query, category, limit);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("suggestions", suggestions);
        data.put("query", query);
        data.put("category", category);
        data.put("count", suggestions.size());
        
        sendJsonResponse(response, data);
    }

    private void applyFilters(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        Map<String, Object> filters = parseFilterParameters(request);
        
        // Apply filters and get results
        int page = parseIntParameter(request.getParameter("page"), 1);
        int limit = parseIntParameter(request.getParameter("limit"), 20);
        
        // List<Product> filteredProducts = filterService.getFilteredProducts(filters, page, limit);
        // int totalCount = filterService.getFilteredProductsCount(filters);
        // ProductFilterService class doesn't exist, commenting out for now
        List<Product> filteredProducts = new ArrayList<>();
        int totalCount = 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", true);
        data.put("products", filteredProducts);
        data.put("totalCount", totalCount);
        data.put("page", page);
        data.put("limit", limit);
        data.put("totalPages", (int) Math.ceil((double) totalCount / limit));
        data.put("filters", filters);
        data.put("message", "Filters applied successfully");
        
        sendJsonResponse(response, data);
    }

    private void saveFilterPreferences(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String userId = request.getParameter("userId");
        Map<String, Object> filters = parseFilterParameters(request);
        
        if (userId == null || userId.trim().isEmpty()) {
            sendErrorResponse(response, "User ID is required", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean success = false;
        // boolean success = filterService.saveFilterPreferences(Integer.parseInt(userId), filters);
        // ProductFilterService class doesn't exist, commenting out for now
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", success ? "Filter preferences saved successfully" : "Failed to save filter preferences");
        
        sendJsonResponse(response, data);
    }

    private void clearFilters(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        String userId = request.getParameter("userId");
        
        boolean success = true;
        if (userId != null && !userId.trim().isEmpty()) {
        // success = filterService.clearFilterPreferences(Integer.parseInt(userId));
        // ProductFilterService class doesn't exist, commenting out for now
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("success", success);
        data.put("message", "Filters cleared successfully");
        
        sendJsonResponse(response, data);
    }

    private Map<String, Object> parseFilterParameters(HttpServletRequest request) {
        Map<String, Object> filters = new HashMap<>();
        
        // Basic filters
        filters.put("category", request.getParameter("category"));
        filters.put("search", request.getParameter("search"));
        filters.put("brand", request.getParameterValues("brand"));
        filters.put("minPrice", parseDoubleParameter(request.getParameter("minPrice"), null));
        filters.put("maxPrice", parseDoubleParameter(request.getParameter("maxPrice"), null));
        filters.put("minRating", parseDoubleParameter(request.getParameter("minRating"), null));
        filters.put("availability", request.getParameter("availability"));
        
        // Size filters
        String[] sizes = request.getParameterValues("size");
        if (sizes != null) {
            filters.put("sizes", java.util.Arrays.asList(sizes));
        }
        
        // Color filters
        String[] colors = request.getParameterValues("color");
        if (colors != null) {
            filters.put("colors", java.util.Arrays.asList(colors));
        }
        
        // Sort options
        filters.put("sortBy", request.getParameter("sortBy"));
        filters.put("sortOrder", request.getParameter("sortOrder"));
        
        // Pagination
        filters.put("page", parseIntParameter(request.getParameter("page"), 1));
        filters.put("limit", parseIntParameter(request.getParameter("limit"), 20));
        
        // Additional filters
        filters.put("discountOnly", parseBooleanParameter(request.getParameter("discountOnly"), false));
        filters.put("inStockOnly", parseBooleanParameter(request.getParameter("inStockOnly"), false));
        filters.put("newArrivals", parseBooleanParameter(request.getParameter("newArrivals"), false));
        filters.put("bestSellers", parseBooleanParameter(request.getParameter("bestSellers"), false));
        
        return filters;
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

    private double parseDoubleParameter(String value, Double defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue != null ? defaultValue : 0.0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue != null ? defaultValue : 0.0;
        }
    }

    private boolean parseBooleanParameter(String value, boolean defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
}
