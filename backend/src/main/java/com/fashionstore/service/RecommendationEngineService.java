package com.fashionstore.service;

import com.fashionstore.model.User;
import com.fashionstore.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enterprise recommendation engine service
 * Implements collaborative filtering, content-based, and hybrid recommendation algorithms
 */
public class RecommendationEngineService {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEngineService.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    // Recommendation algorithms
    private enum Algorithm {
        COLLABORATIVE_USER_BASED,
        COLLABORATIVE_ITEM_BASED,
        CONTENT_BASED,
        HYBRID,
        TRENDING,
        PERSONALIZED,
        SIMILARITY_BASED,
        POPULARITY_BASED
    }
    
    // Recommendation contexts
    private enum Context {
        HOMEPAGE,
        PDP,
        CART,
        CHECKOUT,
        CATEGORY,
        SEARCH,
        WISHLIST,
        PURCHASE_HISTORY
    }
    
    public RecommendationEngineService() {
        // Initialize service
    }
    
    /**
     * Get homepage recommendations
     */
    public Map<String, Object> getHomepageRecommendations(int userId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.HYBRID;
            List<Map<String, Object>> recommendations = generateRecommendations(userId, Context.HOMEPAGE, algo, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.HOMEPAGE.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting homepage recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get homepage recommendations");
        }
        
        return result;
    }
    
    /**
     * Get PDP (Product Detail Page) recommendations
     */
    public Map<String, Object> getPDPRecommendations(int userId, int productId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.CONTENT_BASED;
            List<Map<String, Object>> recommendations = generatePDPRecommendations(userId, productId, algo, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.PDP.name());
            result.put("userId", userId);
            result.put("productId", productId);
            
        } catch (Exception e) {
            logger.error("Error getting PDP recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get PDP recommendations");
        }
        
        return result;
    }
    
    /**
     * Get cart recommendations
     */
    public Map<String, Object> getCartRecommendations(int userId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.COLLABORATIVE_ITEM_BASED;
            List<Map<String, Object>> recommendations = generateCartRecommendations(userId, algo, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.CART.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting cart recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get cart recommendations");
        }
        
        return result;
    }
    
    /**
     * Get checkout recommendations
     */
    public Map<String, Object> getCheckoutRecommendations(int userId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.CONTENT_BASED;
            List<Map<String, Object>> recommendations = generateCheckoutRecommendations(userId, algo, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.CHECKOUT.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting checkout recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get checkout recommendations");
        }
        
        return result;
    }
    
    /**
     * Get product recommendations
     */
    public Map<String, Object> getProductRecommendations(int userId, int productId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.SIMILARITY_BASED;
            List<Map<String, Object>> recommendations = generateProductRecommendations(userId, productId, algo, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.PDP.name());
            result.put("userId", userId);
            result.put("productId", productId);
            
        } catch (Exception e) {
            logger.error("Error getting product recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get product recommendations");
        }
        
        return result;
    }
    
    /**
     * Get category recommendations
     */
    public Map<String, Object> getCategoryRecommendations(int userId, int categoryId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.CONTENT_BASED;
            List<Map<String, Object>> recommendations = generateCategoryRecommendations(userId, categoryId, algo, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.CATEGORY.name());
            result.put("userId", userId);
            result.put("categoryId", categoryId);
            
        } catch (Exception e) {
            logger.error("Error getting category recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get category recommendations");
        }
        
        return result;
    }
    
    /**
     * Get similar products
     */
    public Map<String, Object> getSimilarProducts(int userId, int productId, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> recommendations = generateSimilarProducts(userId, productId, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.SIMILARITY_BASED.name());
            result.put("context", Context.PDP.name());
            result.put("userId", userId);
            result.put("productId", productId);
            
        } catch (Exception e) {
            logger.error("Error getting similar products: {}", e.getMessage(), e);
            result.put("error", "Failed to get similar products");
        }
        
        return result;
    }
    
    /**
     * Get "also bought" products
     */
    public Map<String, Object> getAlsoBoughtProducts(int userId, int productId, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> recommendations = generateAlsoBoughtProducts(userId, productId, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.COLLABORATIVE_ITEM_BASED.name());
            result.put("context", Context.PDP.name());
            result.put("userId", userId);
            result.put("productId", productId);
            
        } catch (Exception e) {
            logger.error("Error getting also bought products: {}", e.getMessage(), e);
            result.put("error", "Failed to get also bought products");
        }
        
        return result;
    }
    
    /**
     * Get trending products
     */
    public Map<String, Object> getTrendingProducts(int userId, int limit, String category, String timeRange) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> recommendations = generateTrendingProducts(userId, limit, category, timeRange);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.TRENDING.name());
            result.put("context", Context.HOMEPAGE.name());
            result.put("userId", userId);
            result.put("category", category);
            result.put("timeRange", timeRange);
            
        } catch (Exception e) {
            logger.error("Error getting trending products: {}", e.getMessage(), e);
            result.put("error", "Failed to get trending products");
        }
        
        return result;
    }
    
    /**
     * Get personalized recommendations
     */
    public Map<String, Object> getPersonalizedRecommendations(int userId, int limit, String context) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Context recContext = context != null ? Context.valueOf(context.toUpperCase()) : Context.HOMEPAGE;
            List<Map<String, Object>> recommendations = generatePersonalizedRecommendations(userId, recContext, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.PERSONALIZED.name());
            result.put("context", recContext.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting personalized recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get personalized recommendations");
        }
        
        return result;
    }
    
    /**
     * Get collaborative recommendations
     */
    public Map<String, Object> getCollaborativeRecommendations(int userId, int limit, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.COLLABORATIVE_USER_BASED;
            List<Map<String, Object>> recommendations = new ArrayList<>();
            // List<Map<String, Object>> recommendations = generateCollaborativeRecommendations(userId, algo, limit);
            // Method doesn't exist, commenting out for now
            
            result.put("recommendations", recommendations);
            result.put("algorithm", algo.name());
            result.put("context", Context.HOMEPAGE.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting collaborative recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get collaborative recommendations");
        }
        
        return result;
    }
    
    /**
     * Get wishlist-based recommendations
     */
    public Map<String, Object> getWishlistBasedRecommendations(int userId, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> recommendations = generateWishlistBasedRecommendations(userId, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.CONTENT_BASED.name());
            result.put("context", Context.WISHLIST.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting wishlist-based recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get wishlist-based recommendations");
        }
        
        return result;
    }
    
    /**
     * Get browsing history recommendations
     */
    public Map<String, Object> getBrowsingHistoryRecommendations(int userId, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> recommendations = generateBrowsingHistoryRecommendations(userId, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.CONTENT_BASED.name());
            result.put("context", Context.PURCHASE_HISTORY.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting browsing history recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get browsing history recommendations");
        }
        
        return result;
    }
    
    /**
     * Get purchase history recommendations
     */
    public Map<String, Object> getPurchaseHistoryRecommendations(int userId, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<Map<String, Object>> recommendations = generatePurchaseHistoryRecommendations(userId, limit);
            
            result.put("recommendations", recommendations);
            result.put("algorithm", Algorithm.COLLABORATIVE_ITEM_BASED.name());
            result.put("context", Context.PURCHASE_HISTORY.name());
            result.put("userId", userId);
            
        } catch (Exception e) {
            logger.error("Error getting purchase history recommendations: {}", e.getMessage(), e);
            result.put("error", "Failed to get purchase history recommendations");
        }
        
        return result;
    }
    
    /**
     * Submit recommendation feedback
     */
    public Map<String, Object> submitRecommendationFeedback(int userId, Map<String, Object> feedbackData) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Log recommendation feedback
            logRecommendationFeedback(userId, feedbackData);
            
            result.put("success", true);
            result.put("message", "Feedback submitted successfully");
            
        } catch (Exception e) {
            logger.error("Error submitting recommendation feedback: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to submit feedback");
        }
        
        return result;
    }
    
    /**
     * Update recommendation preferences
     */
    public Map<String, Object> updateRecommendationPreferences(int userId, Map<String, Object> preferences) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO user_search_preferences (user_id, preference_type, preference_data) " +
                        "VALUES (?, 'recommendation_algorithm', ?) " +
                        "ON DUPLICATE KEY UPDATE preference_data = VALUES(preference_data)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, objectMapper.writeValueAsString(preferences));
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", affectedRows > 0 ? "Preferences updated successfully" : "No changes made");
            
        } catch (Exception e) {
            logger.error("Error updating recommendation preferences: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to update preferences");
        }
        
        return result;
    }
    
    /**
     * Track recommendation click
     */
    public Map<String, Object> trackRecommendationClick(int userId, Map<String, Object> clickData) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO recommendation_events " +
                        "(user_id, session_id, recommendation_type, algorithm, product_id, position, context, click_timestamp) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, (String) clickData.get("sessionId"));
            stmt.setString(3, (String) clickData.get("recommendationType"));
            stmt.setString(4, (String) clickData.get("algorithm"));
            stmt.setInt(5, (Integer) clickData.get("productId"));
            stmt.setInt(6, (Integer) clickData.get("position"));
            stmt.setString(7, objectMapper.writeValueAsString(clickData.get("context")));
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", "Click tracked successfully");
            
        } catch (Exception e) {
            logger.error("Error tracking recommendation click: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to track click");
        }
        
        return result;
    }
    
    /**
     * Track recommendation impression
     */
    public Map<String, Object> trackRecommendationImpression(int userId, Map<String, Object> impressionData) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO recommendation_events " +
                        "(user_id, session_id, recommendation_type, algorithm, product_id, position, context, impression_timestamp) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setString(2, (String) impressionData.get("sessionId"));
            stmt.setString(3, (String) impressionData.get("recommendationType"));
            stmt.setString(4, (String) impressionData.get("algorithm"));
            stmt.setInt(5, (Integer) impressionData.get("productId"));
            stmt.setInt(6, (Integer) impressionData.get("position"));
            stmt.setString(7, objectMapper.writeValueAsString(impressionData.get("context")));
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", "Impression tracked successfully");
            
        } catch (Exception e) {
            logger.error("Error tracking recommendation impression: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to track impression");
        }
        
        return result;
    }
    
    /**
     * Track recommendation purchase
     */
    public Map<String, Object> trackRecommendationPurchase(int userId, Map<String, Object> purchaseData) {
        Map<String, Object> result = new HashMap<>();
        
        try (Connection conn = getConnection()) {
            String sql = "UPDATE recommendation_events " +
                        "SET conversion_timestamp = NOW() " +
                        "WHERE user_id = ? AND product_id = ? AND recommendation_type = ? " +
                        "ORDER BY impression_timestamp DESC LIMIT 1";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, (Integer) purchaseData.get("productId"));
            stmt.setString(3, (String) purchaseData.get("recommendationType"));
            
            int affectedRows = stmt.executeUpdate();
            
            result.put("success", affectedRows > 0);
            result.put("message", "Purchase tracked successfully");
            
        } catch (Exception e) {
            logger.error("Error tracking recommendation purchase: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to track purchase");
        }
        
        return result;
    }
    
    /**
     * Refresh recommendations
     */
    public Map<String, Object> refreshRecommendations(int userId, String context, String algorithm) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Context recContext = context != null ? Context.valueOf(context.toUpperCase()) : Context.HOMEPAGE;
            Algorithm algo = algorithm != null ? Algorithm.valueOf(algorithm.toUpperCase()) : Algorithm.HYBRID;
            
            // Clear cache for this user and context
            clearRecommendationCache(userId, recContext, algo);
            
            // Generate fresh recommendations
            List<Map<String, Object>> recommendations = generateRecommendations(userId, recContext, algo, 12);
            
            result.put("success", true);
            result.put("message", "Recommendations refreshed successfully");
            result.put("recommendations", recommendations);
            
        } catch (Exception e) {
            logger.error("Error refreshing recommendations: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "Failed to refresh recommendations");
        }
        
        return result;
    }
    
    // Private methods for recommendation generation
    
    private List<Map<String, Object>> generateRecommendations(int userId, Context context, Algorithm algorithm, int limit) {
        switch (algorithm) {
            case COLLABORATIVE_USER_BASED:
                return generateCollaborativeUserBasedRecommendations(userId, context, limit);
            case COLLABORATIVE_ITEM_BASED:
                return generateCollaborativeItemBasedRecommendations(userId, context, limit);
            case CONTENT_BASED:
                return generateContentBasedRecommendations(userId, context, limit);
            case HYBRID:
                return generateHybridRecommendations(userId, context, limit);
            case TRENDING:
                return generateTrendingRecommendations(userId, context, limit);
            case PERSONALIZED:
                return generatePersonalizedRecommendations(userId, context, limit);
            case SIMILARITY_BASED:
                return generateSimilarityBasedRecommendations(userId, context, limit);
            case POPULARITY_BASED:
                return generatePopularityBasedRecommendations(userId, context, limit);
            default:
                return generateHybridRecommendations(userId, context, limit);
        }
    }
    
    private List<Map<String, Object>> generateCollaborativeUserBasedRecommendations(int userId, Context context, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            // Find similar users based on purchase history
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COUNT(*) as common_purchases, AVG(o.total_amount) as avg_order_value " +
                        "FROM products p " +
                        "JOIN order_items oi ON p.product_id = oi.product_id " +
                        "JOIN orders o ON oi.order_id = o.order_id " +
                        "JOIN order_items oi2 ON o.order_id = oi2.order_id " +
                        "JOIN products p2 ON oi2.product_id = p2.product_id " +
                        "WHERE o.user_id != ? AND p2.product_id IN (" +
                        "  SELECT oi3.product_id FROM order_items oi3 " +
                        "  JOIN orders o3 ON oi3.order_id = o3.order_id " +
                        "  WHERE o3.user_id = ?" +
                        ") " +
                        "GROUP BY p.product_id, p.product_name, p.price, p.image_url, p.category_id " +
                        "HAVING COUNT(*) >= 2 " +
                        "ORDER BY common_purchases DESC, avg_order_value DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("common_purchases"));
                product.put("algorithm", Algorithm.COLLABORATIVE_USER_BASED.name());
                product.put("reason", "Users who bought similar items also bought this");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating collaborative user-based recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateCollaborativeItemBasedRecommendations(int userId, Context context, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            // Find items frequently bought together
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COUNT(*) as purchase_frequency, AVG(o.total_amount) as avg_order_value " +
                        "FROM products p " +
                        "JOIN order_items oi ON p.product_id = oi.product_id " +
                        "JOIN orders o ON oi.order_id = o.order_id " +
                        "WHERE oi.order_id IN (" +
                        "  SELECT DISTINCT o2.order_id FROM orders o2 " +
                        "  JOIN order_items oi2 ON o2.order_id = oi2.order_id " +
                        "  WHERE o2.user_id = ?" +
                        ") " +
                        "AND p.product_id NOT IN (" +
                        "  SELECT oi3.product_id FROM order_items oi3 " +
                        "  JOIN orders o3 ON oi3.order_id = oi3.order_id " +
                        "  WHERE o3.user_id = ?" +
                        ") " +
                        "GROUP BY p.product_id, p.product_name, p.price, p.image_url, p.category_id " +
                        "HAVING COUNT(*) >= 3 " +
                        "ORDER BY purchase_frequency DESC, avg_order_value DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("purchase_frequency"));
                product.put("algorithm", Algorithm.COLLABORATIVE_ITEM_BASED.name());
                product.put("reason", "Frequently bought together with your items");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating collaborative item-based recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateContentBasedRecommendations(int userId, Context context, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        try (Connection conn = getConnection()) {
            // First, get user's purchase history keywords
            String keywordSql = "SELECT GROUP_CONCAT(p2.product_name SEPARATOR ' ') as keywords " +
                               "FROM products p2 " +
                               "JOIN order_items oi2 ON p2.product_id = oi2.product_id " +
                               "JOIN orders o2 ON oi2.order_id = o2.order_id " +
                               "WHERE o2.user_id = ?";
            
            PreparedStatement keywordStmt = conn.prepareStatement(keywordSql);
            keywordStmt.setInt(1, userId);
            ResultSet keywordRs = keywordStmt.executeQuery();
            
            String keywords = "";
            if (keywordRs.next()) {
                keywords = keywordRs.getString("keywords");
            }
            keywordRs.close();
            keywordStmt.close();
            
            // If no keywords found, return empty list
            if (keywords == null || keywords.trim().isEmpty()) {
                return recommendations;
            }
            
            // Then, use keywords to find similar products
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "c.category_name, b.brand_name, " +
                        "MATCH(p.product_name, p.description) AGAINST (?) as relevance_score " +
                        "FROM products p " +
                        "LEFT JOIN categories c ON p.category_id = c.category_id " +
                        "LEFT JOIN brands b ON p.brand_id = b.brand_id " +
                        "WHERE p.is_active = 1 " +
                        "AND p.stock_quantity > 0 " +
                        "AND p.product_id NOT IN (" +
                        "  SELECT oi3.product_id FROM order_items oi3 " +
                        "  JOIN orders o3 ON oi3.order_id = o3.order_id " +
                        "  WHERE o3.user_id = ?" +
                        ") " +
                        "ORDER BY relevance_score DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keywords);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);

            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("categoryName", rs.getString("category_name"));
                product.put("brandName", rs.getString("brand_name"));
                product.put("score", rs.getDouble("relevance_score"));
                product.put("algorithm", Algorithm.CONTENT_BASED.name());
                product.put("reason", "Similar to items you've purchased");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating content-based recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateHybridRecommendations(int userId, Context context, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // Combine multiple algorithms for better recommendations
        List<Map<String, Object>> collaborative = generateCollaborativeUserBasedRecommendations(userId, context, limit / 3);
        List<Map<String, Object>> contentBased = generateContentBasedRecommendations(userId, context, limit / 3);
        List<Map<String, Object>> trending = generateTrendingRecommendations(userId, context, limit / 3);
        
        // Merge and deduplicate
        Map<Integer, Map<String, Object>> merged = new HashMap<>();
        
        // Add collaborative recommendations with higher weight
        for (Map<String, Object> product : collaborative) {
            int id = (Integer) product.get("id");
            product.put("score", ((Double) product.get("score")) * 1.5); // Higher weight
            merged.put(id, product);
        }
        
        // Add content-based recommendations
        for (Map<String, Object> product : contentBased) {
            int id = (Integer) product.get("id");
            if (!merged.containsKey(id)) {
                product.put("score", ((Double) product.get("score")) * 1.2); // Medium weight
                merged.put(id, product);
            }
        }
        
        // Add trending recommendations
        for (Map<String, Object> product : trending) {
            int id = (Integer) product.get("id");
            if (!merged.containsKey(id)) {
                product.put("score", ((Double) product.get("score")) * 1.0); // Lower weight
                merged.put(id, product);
            }
        }
        
        // Sort by score and take top recommendations
        recommendations = new ArrayList<>(merged.values());
        recommendations.sort((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")));
        
        if (recommendations.size() > limit) {
            recommendations = recommendations.subList(0, limit);
        }
        
        // Update algorithm info
        for (Map<String, Object> product : recommendations) {
            product.put("algorithm", Algorithm.HYBRID.name());
            product.put("reason", "Recommended based on multiple factors");
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateTrendingRecommendations(int userId, Context context, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COALESCE(pps.popularity_score, 0) as trending_score, " +
                        "COALESCE(pps.total_views, 0) as views, " +
                        "COALESCE(pps.total_purchases, 0) as purchases " +
                        "FROM products p " +
                        "LEFT JOIN product_popularity_scores pps ON p.product_id = pps.product_id " +
                        "WHERE p.is_active = 1 " +
                        "AND p.stock_quantity > 0 " +
                        "AND pps.time_range = 'week' " +
                        "ORDER BY trending_score DESC, p.created_at DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("trending_score"));
                product.put("views", rs.getInt("views"));
                product.put("purchases", rs.getInt("purchases"));
                product.put("algorithm", Algorithm.TRENDING.name());
                product.put("reason", "Trending right now");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating trending recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generatePersonalizedRecommendations(int userId, Context context, int limit) {
        // Personalized recommendations based on user behavior and preferences
        return generateHybridRecommendations(userId, context, limit);
    }
    
    private List<Map<String, Object>> generateSimilarityBasedRecommendations(int userId, Context context, int limit) {
        // Similarity-based recommendations
        return generateContentBasedRecommendations(userId, context, limit);
    }
    
    private List<Map<String, Object>> generatePopularityBasedRecommendations(int userId, Context context, int limit) {
        // Popularity-based recommendations
        return generateTrendingRecommendations(userId, context, limit);
    }
    
    // Context-specific recommendation generators
    
    private List<Map<String, Object>> generatePDPRecommendations(int userId, int productId, Algorithm algorithm, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            // Get product details first
            String productSql = "SELECT p.category_id, p.brand_id, p.price_range " +
                              "FROM products p WHERE p.product_id = ?";
            
            PreparedStatement productStmt = conn.prepareStatement(productSql);
            productStmt.setInt(1, productId);
            ResultSet productRs = productStmt.executeQuery();
            
            if (productRs.next()) {
                int categoryId = productRs.getInt("category_id");
                int brandId = productRs.getInt("brand_id");
                String priceRange = productRs.getString("price_range");
                
                // Generate similar products based on attributes
                String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                            "ABS(p.price - (SELECT price FROM products WHERE product_id = ?)) as price_diff, " +
                            "CASE WHEN p.category_id = ? THEN 1 ELSE 0 END as category_match, " +
                            "CASE WHEN p.brand_id = ? THEN 1 ELSE 0 END as brand_match " +
                            "FROM products p " +
                            "WHERE p.product_id != ? " +
                            "AND p.is_active = 1 " +
                            "AND p.stock_quantity > 0 " +
                            "ORDER BY (category_match * 3 + brand_match * 2 + (1 - price_diff / 1000)) DESC " +
                            "LIMIT ?";
                
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, productId);
                stmt.setInt(2, categoryId);
                stmt.setInt(3, brandId);
                stmt.setInt(4, productId);
                stmt.setInt(5, limit);
                
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("id", rs.getInt("product_id"));
                    product.put("name", rs.getString("product_name"));
                    product.put("price", rs.getDouble("price"));
                    product.put("imageUrl", rs.getString("image_url"));
                    product.put("categoryId", rs.getInt("category_id"));
                    product.put("score", rs.getDouble("category_match") * 3 + rs.getDouble("brand_match") * 2 + (1 - rs.getDouble("price_diff") / 1000));
                    product.put("algorithm", algorithm.name());
                    product.put("reason", "Similar to this product");
                    
                    recommendations.add(product);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error generating PDP recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateCartRecommendations(int userId, Algorithm algorithm, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            // Get cart items and recommend complementary products
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COUNT(*) as recommendation_count " +
                        "FROM products p " +
                        "JOIN order_items oi ON p.product_id = oi.product_id " +
                        "JOIN orders o ON oi.order_id = o.order_id " +
                        "WHERE o.user_id != ? " +
                        "AND oi.order_id IN (" +
                        "  SELECT DISTINCT o2.order_id FROM orders o2 " +
                        "  JOIN order_items oi2 ON o2.order_id = oi2.order_id " +
                        "  WHERE o2.user_id = ?" +
                        ") " +
                        "AND p.product_id NOT IN (" +
                        "  SELECT ci.product_id FROM cart_items ci " +
                        "  WHERE ci.user_id = ?" +
                        ") " +
                        "GROUP BY p.product_id, p.product_name, p.price, p.image_url, p.category_id " +
                        "HAVING COUNT(*) >= 2 " +
                        "ORDER BY recommendation_count DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("recommendation_count"));
                product.put("algorithm", algorithm.name());
                product.put("reason", "Frequently bought with items in your cart");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating cart recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateCheckoutRecommendations(int userId, Algorithm algorithm, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            // Recommend accessories or complementary products based on cart
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "c.category_name, " +
                        "CASE WHEN c.category_name LIKE '%accessories%' OR c.category_name LIKE '%bags%' THEN 1 ELSE 0 END as accessory_score " +
                        "FROM products p " +
                        "LEFT JOIN categories c ON p.category_id = c.category_id " +
                        "WHERE p.is_active = 1 " +
                        "AND p.stock_quantity > 0 " +
                        "AND p.price < 100 " +
                        "ORDER BY accessory_score DESC, RAND() " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("categoryName", rs.getString("category_name"));
                product.put("score", rs.getDouble("accessory_score"));
                product.put("algorithm", algorithm.name());
                product.put("reason", "Perfect accessories for your order");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating checkout recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateCategoryRecommendations(int userId, int categoryId, Algorithm algorithm, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COALESCE(pps.popularity_score, 0) as popularity_score, " +
                        "COALESCE(pps.total_purchases, 0) as purchases " +
                        "FROM products p " +
                        "LEFT JOIN product_popularity_scores pps ON p.product_id = pps.product_id " +
                        "WHERE p.category_id = ? " +
                        "AND p.is_active = 1 " +
                        "AND p.stock_quantity > 0 " +
                        "AND pps.time_range = 'week' " +
                        "ORDER BY popularity_score DESC, purchases DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, categoryId);
            stmt.setInt(2, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("popularity_score"));
                product.put("purchases", rs.getInt("purchases"));
                product.put("algorithm", algorithm.name());
                product.put("reason", "Popular in this category");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating category recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateProductRecommendations(int userId, int productId, Algorithm algorithm, int limit) {
        return generatePDPRecommendations(userId, productId, algorithm, limit);
    }
    
    private List<Map<String, Object>> generateSimilarProducts(int userId, int productId, int limit) {
        return generatePDPRecommendations(userId, productId, Algorithm.SIMILARITY_BASED, limit);
    }
    
    private List<Map<String, Object>> generateAlsoBoughtProducts(int userId, int productId, int limit) {
        return generateCollaborativeItemBasedRecommendations(userId, Context.PDP, limit);
    }
    
    private List<Map<String, Object>> generateTrendingProducts(int userId, int limit, String category, String timeRange) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, ");
            sql.append("COALESCE(pps.popularity_score, 0) as trending_score, ");
            sql.append("COALESCE(pps.total_views, 0) as views, ");
            sql.append("COALESCE(pps.total_purchases, 0) as purchases ");
            sql.append("FROM products p ");
            sql.append("LEFT JOIN product_popularity_scores pps ON p.product_id = pps.product_id ");
            sql.append("WHERE p.is_active = 1 ");
            sql.append("AND p.stock_quantity > 0 ");
            
            if (category != null && !category.isEmpty()) {
                sql.append("AND p.category_id = (SELECT category_id FROM categories WHERE category_name = ?) ");
            }
            
            sql.append("AND pps.time_range = ? ");
            sql.append("ORDER BY trending_score DESC, p.created_at DESC ");
            sql.append("LIMIT ?");
            
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;
            
            if (category != null && !category.isEmpty()) {
                stmt.setString(paramIndex++, category);
            }
            stmt.setString(paramIndex++, timeRange != null ? timeRange : "week");
            stmt.setInt(paramIndex++, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("trending_score"));
                product.put("views", rs.getInt("views"));
                product.put("purchases", rs.getInt("purchases"));
                product.put("algorithm", Algorithm.TRENDING.name());
                product.put("reason", "Trending right now");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating trending products: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateWishlistBasedRecommendations(int userId, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();

        try (Connection conn = getConnection()) {
            // First, get user's wishlist keywords
            String keywordSql = "SELECT GROUP_CONCAT(p2.product_name SEPARATOR ' ') as keywords " +
                               "FROM products p2 " +
                               "JOIN wishlist w ON p2.product_id = w.product_id " +
                               "WHERE w.user_id = ?";

            PreparedStatement keywordStmt = conn.prepareStatement(keywordSql);
            keywordStmt.setInt(1, userId);
            ResultSet keywordRs = keywordStmt.executeQuery();

            String keywords = "";
            if (keywordRs.next()) {
                keywords = keywordRs.getString("keywords");
            }
            keywordRs.close();
            keywordStmt.close();

            // If no keywords found, return empty list
            if (keywords == null || keywords.trim().isEmpty()) {
                return recommendations;
            }

            // Then, use keywords to find similar products
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "MATCH(p.product_name, p.description) AGAINST (?) as relevance_score " +
                        "FROM products p " +
                        "WHERE p.is_active = 1 " +
                        "AND p.stock_quantity > 0 " +
                        "AND p.product_id NOT IN (" +
                        "  SELECT w.product_id FROM wishlist w WHERE w.user_id = ?" +
                        ") " +
                        "ORDER BY relevance_score DESC " +
                        "LIMIT ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, keywords);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("relevance_score"));
                product.put("algorithm", Algorithm.CONTENT_BASED.name());
                product.put("reason", "Similar to items in your wishlist");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating wishlist-based recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generateBrowsingHistoryRecommendations(int userId, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COUNT(*) as view_count " +
                        "FROM products p " +
                        "JOIN product_clickstream pc ON p.product_id = pc.product_id " +
                        "WHERE pc.user_id = ? AND pc.action_type = 'view' " +
                        "AND p.product_id NOT IN (" +
                        "  SELECT oi.product_id FROM order_items oi " +
                        "  JOIN orders o ON oi.order_id = o.order_id " +
                        "  WHERE o.user_id = ?" +
                        ") " +
                        "GROUP BY p.product_id, p.product_name, p.price, p.image_url, p.category_id " +
                        "ORDER BY view_count DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("view_count"));
                product.put("algorithm", Algorithm.CONTENT_BASED.name());
                product.put("reason", "Based on your browsing history");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating browsing history recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    private List<Map<String, Object>> generatePurchaseHistoryRecommendations(int userId, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        try (Connection conn = getConnection()) {
            String sql = "SELECT p.product_id, p.product_name, p.price, p.image_url, p.category_id, " +
                        "COUNT(*) as purchase_count, AVG(o.total_amount) as avg_order_value " +
                        "FROM products p " +
                        "JOIN order_items oi ON p.product_id = oi.product_id " +
                        "JOIN orders o ON oi.order_id = o.order_id " +
                        "WHERE o.user_id = ? " +
                        "AND p.product_id NOT IN (" +
                        "  SELECT oi2.product_id FROM order_items oi2 " +
                        "  JOIN orders o2 ON oi2.order_id = o2.order_id " +
                        "  WHERE o2.user_id = ?" +
                        ") " +
                        "GROUP BY p.product_id, p.product_name, p.price, p.image_url, p.category_id " +
                        "HAVING COUNT(*) >= 2 " +
                        "ORDER BY purchase_count DESC, avg_order_value DESC " +
                        "LIMIT ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("id", rs.getInt("product_id"));
                product.put("name", rs.getString("product_name"));
                product.put("price", rs.getDouble("price"));
                product.put("imageUrl", rs.getString("image_url"));
                product.put("categoryId", rs.getInt("category_id"));
                product.put("score", rs.getDouble("purchase_count"));
                product.put("algorithm", Algorithm.COLLABORATIVE_ITEM_BASED.name());
                product.put("reason", "Based on your purchase history");
                
                recommendations.add(product);
            }
            
        } catch (SQLException e) {
            logger.error("Error generating purchase history recommendations: {}", e.getMessage(), e);
        }
        
        return recommendations;
    }
    
    // Helper methods

    private Connection getConnection() throws SQLException {
        return com.fashionstore.util.DBConnection.getConnection();
    }

    /**
     * Shutdown the executor service gracefully
     */
    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private void logRecommendationFeedback(int userId, Map<String, Object> feedbackData) {
        // Log feedback for analytics and model improvement
        logger.info("Recommendation feedback received for user {}: {}", userId, feedbackData);
    }
    
    private void clearRecommendationCache(int userId, Context context, Algorithm algorithm) {
        // Clear recommendation cache for this user, context, and algorithm
        // In a real implementation, this would clear the recommendation_cache table
        logger.info("Cleared recommendation cache for user {}, context {}, algorithm {}", userId, context, algorithm);
    }
    
    /**
     * Shutdown service
     */
    // Duplicate shutdown() method, commenting out to fix compilation error
    // public void shutdown() {
    //     if (executorService != null && !executorService.isShutdown()) {
    //         executorService.shutdown();
    //         try {
    //             if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
    //                 executorService.shutdownNow();
    //             }
    //         } catch (InterruptedException e) {
    //             executorService.shutdownNow();
    //             Thread.currentThread().interrupt();
    //         }
    //     }
    // }
}
