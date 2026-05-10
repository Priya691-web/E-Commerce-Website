package com.fashionstore.controller;

import com.fashionstore.dao.ProductDAO;
import com.fashionstore.util.ValidationUtil;
import com.fashionstore.util.JsonUtil;
import com.fashionstore.daoimpl.ProductDAOImpl;
import com.fashionstore.model.Product;
import com.fashionstore.model.ProductQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Search Suggestions Controller for intelligent autocomplete
 * Features: Rate limiting, response caching, proper JSON serialization
 */
@WebServlet("/search/suggestions")
public class SearchSuggestionsController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(SearchSuggestionsController.class);
    private static final int MAX_RESULTS = 10;
    private static final int CACHE_TTL_SECONDS = 300; // 5 minutes
    private static final int RATE_LIMIT_PER_MINUTE = 30;
    
    private ProductDAO productDAO;
    
    // Simple in-memory cache with timestamp
    private static final Map<String, CacheEntry> suggestionCache = new ConcurrentHashMap<>();
    // Rate limiting tracker: IP -> List of timestamps
    private static final Map<String, List<Long>> rateLimitTracker = new ConcurrentHashMap<>();

    @Override
    public void init() {
        productDAO = new ProductDAOImpl();
        // Clean up expired cache entries periodically
        new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MINUTES.sleep(5);
                    cleanupCache();
                    cleanupRateLimitTracker();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }, "SearchSuggestionsCacheCleanup").start();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Rate limiting
        String clientIp = getClientIp(request);
        if (!checkRateLimit(clientIp)) {
            response.setStatus(429); // HTTP 429 Too Many Requests
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"Too many requests\"}");
            return;
        }

        String query = ValidationUtil.sanitizeSearchInput(request.getParameter("q"));
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "public, max-age=" + CACHE_TTL_SECONDS);

        PrintWriter out = response.getWriter();

        if (query == null || query.isEmpty() || query.length() < 2) {
            out.write("[]");
            return;
        }

        try {
            // Check cache
            String cacheKey = query.toLowerCase().trim();
            CacheEntry cached = suggestionCache.get(cacheKey);
            if (cached != null && !cached.isExpired()) {
                out.write(cached.data);
                return;
            }

            ProductQuery productQuery = new ProductQuery();
            productQuery.setSearch(query);
            productQuery.setLimit(MAX_RESULTS);
            productQuery.setActiveOnly(true);
            
            List<Product> suggestions = productDAO.getProducts(productQuery);
            List<Map<String, String>> results = new ArrayList<>();

            for (Product product : suggestions) {
                if (product.getProductName() != null && !product.getProductName().isBlank()) {
                    String name = product.getProductName();
                    if (name.toLowerCase().contains(query.toLowerCase())) {
                        Map<String, String> item = new HashMap<>();
                        item.put("type", "product");
                        item.put("value", name);
                        item.put("id", String.valueOf(product.getProductId()));
                        results.add(item);
                        if (results.size() >= MAX_RESULTS) break;
                    }
                }
                if (product.getBrand() != null && !product.getBrand().isBlank()) {
                    String brand = product.getBrand();
                    if (brand.toLowerCase().contains(query.toLowerCase())) {
                        Map<String, String> item = new HashMap<>();
                        item.put("type", "brand");
                        item.put("value", brand);
                        results.add(item);
                        if (results.size() >= MAX_RESULTS) break;
                    }
                }
            }

            String jsonResponse = JsonUtil.toJson(results);
            
            // Cache the response
            suggestionCache.put(cacheKey, new CacheEntry(jsonResponse, System.currentTimeMillis()));
            
            out.write(jsonResponse);

        } catch (Exception e) {
            logger.error("Error in SearchSuggestionsController: {}", e.getMessage(), e);
            out.write("[]");
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        // Take first IP if multiple (X-Forwarded-For can have comma-separated list)
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }

    private boolean checkRateLimit(String clientIp) {
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1);
        
        List<Long> timestamps = rateLimitTracker.computeIfAbsent(clientIp, k -> new ArrayList<>());
        
        // Remove old timestamps
        timestamps.removeIf(t -> t < oneMinuteAgo);
        
        // Check if limit exceeded
        if (timestamps.size() >= RATE_LIMIT_PER_MINUTE) {
            return false;
        }
        
        // Add current request timestamp
        timestamps.add(now);
        return true;
    }

    private void cleanupCache() {
        long now = System.currentTimeMillis();
        long expiryTime = now - TimeUnit.SECONDS.toMillis(CACHE_TTL_SECONDS);
        suggestionCache.entrySet().removeIf(entry -> entry.getValue().timestamp < expiryTime);
    }

    private void cleanupRateLimitTracker() {
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - TimeUnit.MINUTES.toMillis(1);
        for (List<Long> timestamps : rateLimitTracker.values()) {
            timestamps.removeIf(t -> t < oneMinuteAgo);
        }
        // Remove empty entries
        rateLimitTracker.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    private static class CacheEntry {
        final String data;
        final long timestamp;
        
        CacheEntry(String data, long timestamp) {
            this.data = data;
            this.timestamp = timestamp;
        }
        
        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > TimeUnit.SECONDS.toMillis(CACHE_TTL_SECONDS);
        }
    }
}
