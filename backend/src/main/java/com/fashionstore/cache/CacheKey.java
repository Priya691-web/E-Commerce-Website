package com.fashionstore.cache;

public class CacheKey {
    private static final String PREFIX = "fashionstore:";
    private static final String SEPARATOR = ":";
    
    public static String product(int productId) {
        return PREFIX + "product" + SEPARATOR + productId;
    }
    
    public static String products(String key) {
        return PREFIX + "products" + SEPARATOR + key;
    }
    
    public static String category(int categoryId) {
        return PREFIX + "category" + SEPARATOR + categoryId;
    }
    
    public static String categories(String key) {
        return PREFIX + "categories" + SEPARATOR + key;
    }
    
    public static String featuredProducts() {
        return PREFIX + "featured:products";
    }
    
    public static String trendingProducts() {
        return PREFIX + "trending:products";
    }
    
    public static String newProducts() {
        return PREFIX + "new:products";
    }
    
    public static String saleProducts() {
        return PREFIX + "sale:products";
    }
    
    public static String userCart(int userId) {
        return PREFIX + "cart" + SEPARATOR + userId;
    }
    
    public static String userWishlist(int userId) {
        return PREFIX + "wishlist" + SEPARATOR + userId;
    }
    
    public static String searchResults(String query) {
        return PREFIX + "search" + SEPARATOR + query.hashCode();
    }
    
    public static String searchSuggestions(String query) {
        return PREFIX + "search:suggestions" + SEPARATOR + query.hashCode();
    }
    
    public static String trendingSearches() {
        return PREFIX + "search:trending";
    }
    
    public static String recommendations(int productId) {
        return PREFIX + "recommendations" + SEPARATOR + productId;
    }
    
    public static String relatedProducts(int productId) {
        return PREFIX + "related" + SEPARATOR + productId;
    }
    
    public static String recentlyViewed(int userId) {
        return PREFIX + "recently:viewed" + SEPARATOR + userId;
    }
    
    public static String adminStats() {
        return PREFIX + "admin:stats";
    }
    
    public static String order(int orderId) {
        return PREFIX + "order" + SEPARATOR + orderId;
    }
    
    public static String userOrders(int userId) {
        return PREFIX + "orders" + SEPARATOR + userId;
    }
    
    public static String session(String sessionId) {
        return PREFIX + "session" + SEPARATOR + sessionId;
    }
    
    public static String coupon(String code) {
        return PREFIX + "coupon" + SEPARATOR + code;
    }
    
    public static String productSizes(int productId) {
        return PREFIX + "product:sizes" + SEPARATOR + productId;
    }
    
    public static String lowStockProducts() {
        return PREFIX + "inventory:low:stock";
    }
}
