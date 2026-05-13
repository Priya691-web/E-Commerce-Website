package com.fashionstore.service;

import com.fashionstore.dto.RecentSearchDTO;
import com.fashionstore.dto.SearchSuggestionDTO;
import java.util.List;
import java.util.Map;

/**
 * Service interface for search suggestions and search history
 */
public interface SearchSuggestionService {
    
    /**
     * Get search suggestions for a query
     */
    List<SearchSuggestionDTO> getSuggestions(String query, String category, int limit);
    
    /**
     * Get trending searches
     */
    List<SearchSuggestionDTO> getTrendingSearches(String category, int limit);
    
    /**
     * Get recent searches for a user
     */
    List<RecentSearchDTO> getRecentSearches(int userId, int limit);
    
    /**
     * Get autocomplete suggestions (products, categories, brands)
     */
    Map<String, List<?>> getAutocompleteSuggestions(String query, int limit);
    
    /**
     * Record a search for logged-in user
     */
    boolean recordSearch(RecentSearchDTO recentSearch);
    
    /**
     * Record an anonymous search (for trending)
     */
    boolean recordAnonymousSearch(String query, String category);
    
    /**
     * Clear search history for a user
     */
    boolean clearSearchHistory(int userId);
    
    /**
     * Delete a specific search from history
     */
    boolean deleteSearch(int searchId, int userId);
    
    /**
     * Update search suggestion popularity
     */
    boolean updateSuggestionPopularity(String query, int increment);
}
