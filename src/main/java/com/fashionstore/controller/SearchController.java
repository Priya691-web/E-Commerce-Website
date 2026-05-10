package com.fashionstore.controller;

import com.fashionstore.model.Product;
import com.fashionstore.service.SearchService;
import com.fashionstore.util.JsonUtil;
import com.fashionstore.util.ValidationUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Search controller for intelligent search and autocomplete
 */
@WebServlet("/search")
public class SearchController extends HttpServlet {
    
    private SearchService searchService;

    @Override
    public void init() throws ServletException {
        searchService = new SearchService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        
        if ("autocomplete".equals(action)) {
            handleAutocomplete(req, resp);
        } else if ("suggestions".equals(action)) {
            handleSuggestions(req, resp);
        } else if ("categories".equals(action)) {
            handleCategorySuggestions(req, resp);
        } else if ("fuzzy".equals(action)) {
            handleFuzzySearch(req, resp);
        } else if ("advanced".equals(action)) {
            handleAdvancedSearch(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    /**
     * Handle autocomplete search
     */
    private void handleAutocomplete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = ValidationUtil.sanitizeSearchInput(req.getParameter("q"));
        int limit = ValidationUtil.clampSearchLimit(req.getParameter("limit"), 10);
        
        List<Product> products = searchService.autocomplete(query, limit);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JsonUtil.toJson(products));
    }
    
    /**
     * Handle keyword suggestions
     */
    private void handleSuggestions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = ValidationUtil.sanitizeSearchInput(req.getParameter("q"));
        int limit = ValidationUtil.clampSearchLimit(req.getParameter("limit"), 10);
        
        List<String> suggestions = searchService.getKeywordSuggestions(query, limit);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JsonUtil.toJson(suggestions));
    }
    
    /**
     * Handle category suggestions
     */
    private void handleCategorySuggestions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = ValidationUtil.sanitizeSearchInput(req.getParameter("q"));
        
        List<String> categories = searchService.getCategorySuggestions(query);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JsonUtil.toJson(categories));
    }
    
    /**
     * Handle fuzzy search (typo tolerance)
     */
    private void handleFuzzySearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = ValidationUtil.sanitizeSearchInput(req.getParameter("q"));
        int maxDistance = ValidationUtil.clampFuzzDistance(req.getParameter("distance"), 2);
        int limit = ValidationUtil.clampSearchLimit(req.getParameter("limit"), 10);
        
        List<Product> products = searchService.fuzzySearch(query, maxDistance, limit);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JsonUtil.toJson(products));
    }
    
    /**
     * Handle advanced search with filters
     */
    private void handleAdvancedSearch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String query = ValidationUtil.sanitizeSearchInput(req.getParameter("q"));
        String category = ValidationUtil.truncatePlaintext(req.getParameter("category"), 80);
        String color = ValidationUtil.truncatePlaintext(req.getParameter("color"), 40);
        String material = ValidationUtil.truncatePlaintext(req.getParameter("material"), 60);
        String season = ValidationUtil.truncatePlaintext(req.getParameter("season"), 40);
        String occasion = ValidationUtil.truncatePlaintext(req.getParameter("occasion"), 60);
        double minPrice = ValidationUtil.clampPrice(req.getParameter("minPrice"), 0);
        double maxPrice = ValidationUtil.clampPrice(req.getParameter("maxPrice"), ValidationUtil.MAX_FILTER_PRICE);
        if (minPrice > maxPrice) {
            double tmp = minPrice;
            minPrice = maxPrice;
            maxPrice = tmp;
        }
        String sortBy = ValidationUtil.sanitizeAdvancedSearchSort(req.getParameter("sortBy"));
        int limit = ValidationUtil.clampSearchLimit(req.getParameter("limit"), 20);
        
        List<Product> products = searchService.advancedSearch(query, category, color, material, season, occasion, 
                                                         minPrice, maxPrice, sortBy, limit);
        
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(JsonUtil.toJson(products));
    }
}
