package com.fashionstore.metrics;

/**
 * Stub implementation of MetricsRegistry to fix compilation errors
 * TODO: Implement proper metrics registry
 */
public class MetricsRegistry {
    
    private static final MetricsRegistry INSTANCE = new MetricsRegistry();
    
    private MetricsRegistry() {}
    
    public static MetricsRegistry getInstance() {
        return INSTANCE;
    }
    
    public String scrape() {
        // Stub implementation - return empty metrics
        return "# No metrics available\n";
    }
}
