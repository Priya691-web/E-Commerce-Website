package com.fashionstore.logging;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.Map;

/**
 * Enterprise metrics collector for monitoring and observability
 * Provides Prometheus-compatible metrics with performance optimization
 */
public class MetricsCollector {

    private static final MetricsCollector INSTANCE = new MetricsCollector();
    
    // Counters for various events
    private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();
    private final Map<String, DoubleAdder> gauges = new ConcurrentHashMap<>();
    private final Map<String, Histogram> histograms = new ConcurrentHashMap<>();
    
    // Pre-defined metric names
    public static class Metrics {
        public static final String HTTP_REQUESTS_TOTAL = "http_requests_total";
        public static final String HTTP_REQUEST_DURATION = "http_request_duration_seconds";
        public static final String AUTHENTICATION_TOTAL = "authentication_total";
        public static final String AUTHENTICATION_FAILED = "authentication_failed";
        public static final String PAYMENT_TOTAL = "payment_total";
        public static final String PAYMENT_FAILED = "payment_failed";
        public static final String ADMIN_ACTIONS_TOTAL = "admin_actions_total";
        public static final String INVENTORY_UPDATES = "inventory_updates";
        public static final String DATABASE_OPERATIONS = "database_operations";
        public static final String DATABASE_ERRORS = "database_errors";
        public static final String CACHE_OPERATIONS = "cache_operations";
        public static final String CACHE_HITS = "cache_hits";
        public static final String ACTIVE_USERS = "active_users";
        public static final String CART_SIZE = "cart_size";
        public static final String ORDER_VALUE = "order_value";
        public static final String PRODUCT_VIEWS = "product_views";
        public static final String SEARCH_QUERIES = "search_queries";
    }
    
    private MetricsCollector() {
        // Initialize default metrics
        initializeDefaultMetrics();
    }
    
    public static MetricsCollector getInstance() {
        return INSTANCE;
    }
    
    /**
     * Increment a counter metric
     */
    public void incrementCounter(String metricName) {
        incrementCounter(metricName, 1);
    }
    
    /**
     * Increment a counter metric by a specific amount
     */
    public void incrementCounter(String metricName, long value) {
        counters.computeIfAbsent(metricName, k -> new AtomicLong(0)).addAndGet(value);
    }
    
    /**
     * Increment a counter metric with labels
     */
    public void incrementCounter(String metricName, Map<String, String> labels) {
        incrementCounter(metricName, labels, 1);
    }
    
    /**
     * Increment a counter metric with labels and value
     */
    public void incrementCounter(String metricName, Map<String, String> labels, long value) {
        String labeledMetric = metricName + formatLabels(labels);
        counters.computeIfAbsent(labeledMetric, k -> new AtomicLong(0)).addAndGet(value);
    }
    
    /**
     * Set a gauge metric value
     */
    public void setGauge(String metricName, double value) {
        gauges.computeIfAbsent(metricName, k -> new DoubleAdder()).reset();
        gauges.get(metricName).add(value);
    }
    
    /**
     * Set a gauge metric value with labels
     */
    public void setGauge(String metricName, Map<String, String> labels, double value) {
        String labeledMetric = metricName + formatLabels(labels);
        gauges.computeIfAbsent(labeledMetric, k -> new DoubleAdder()).reset();
        gauges.get(labeledMetric).add(value);
    }
    
    /**
     * Record a histogram metric value
     */
    public void recordHistogram(String metricName, double value) {
        histograms.computeIfAbsent(metricName, k -> new Histogram()).observe(value);
    }
    
    /**
     * Record a histogram metric value with labels
     */
    public void recordHistogram(String metricName, Map<String, String> labels, double value) {
        String labeledMetric = metricName + formatLabels(labels);
        histograms.computeIfAbsent(labeledMetric, k -> new Histogram()).observe(value);
    }
    
    /**
     * Record HTTP request
     */
    public void recordHttpRequest(String method, String path, int statusCode, long duration) {
        Map<String, String> labels = Map.of(
            "method", method,
            "path", path,
            "status", String.valueOf(statusCode)
        );
        
        incrementCounter(Metrics.HTTP_REQUESTS_TOTAL, labels);
        recordHistogram(Metrics.HTTP_REQUEST_DURATION, labels, duration / 1000.0); // Convert to seconds
    }
    
    /**
     * Record authentication event
     */
    public void recordAuthentication(String type, boolean success, String reason) {
        Map<String, String> labels = Map.of(
            "type", type,
            "success", String.valueOf(success),
            "reason", reason != null ? reason : "none"
        );
        
        incrementCounter(Metrics.AUTHENTICATION_TOTAL, labels);
        if (!success) {
            incrementCounter(Metrics.AUTHENTICATION_FAILED, labels);
        }
    }
    
    /**
     * Record payment event
     */
    public void recordPayment(String method, String status, double amount) {
        Map<String, String> labels = Map.of(
            "method", method,
            "status", status
        );
        
        incrementCounter(Metrics.PAYMENT_TOTAL, labels);
        if ("failed".equalsIgnoreCase(status) || "error".equalsIgnoreCase(status)) {
            incrementCounter(Metrics.PAYMENT_FAILED, labels);
        }
        
        recordHistogram(Metrics.ORDER_VALUE, labels, amount);
    }
    
    /**
     * Record admin action
     */
    public void recordAdminAction(String action, String target) {
        Map<String, String> labels = Map.of(
            "action", action,
            "target", target
        );
        
        incrementCounter(Metrics.ADMIN_ACTIONS_TOTAL, labels);
    }
    
    /**
     * Record inventory update
     */
    public void recordInventoryUpdate(String operation, int productId, int quantity) {
        Map<String, String> labels = Map.of(
            "operation", operation,
            "product_id", String.valueOf(productId)
        );
        
        incrementCounter(Metrics.INVENTORY_UPDATES, labels);
        setGauge("inventory_quantity", Map.of("product_id", String.valueOf(productId)), quantity);
    }
    
    /**
     * Record database operation
     */
    public void recordDatabaseOperation(String operation, String table, boolean success, long duration) {
        Map<String, String> labels = Map.of(
            "operation", operation,
            "table", table,
            "success", String.valueOf(success)
        );
        
        incrementCounter(Metrics.DATABASE_OPERATIONS, labels);
        if (!success) {
            incrementCounter(Metrics.DATABASE_ERRORS, labels);
        }
        
        recordHistogram("database_operation_duration", labels, duration / 1000.0);
    }
    
    /**
     * Record cache operation
     */
    public void recordCacheOperation(String operation, String key, boolean hit, long duration) {
        Map<String, String> labels = Map.of(
            "operation", operation,
            "key", key
        );
        
        incrementCounter(Metrics.CACHE_OPERATIONS, labels);
        if (hit) {
            incrementCounter(Metrics.CACHE_HITS, labels);
        }
        
        recordHistogram("cache_operation_duration", labels, duration / 1000.0);
    }
    
    /**
     * Update active users gauge
     */
    public void updateActiveUsers(int count) {
        setGauge(Metrics.ACTIVE_USERS, count);
    }
    
    /**
     * Record cart size
     */
    public void recordCartSize(int userId, int itemCount) {
        Map<String, String> labels = Map.of(
            "user_id", String.valueOf(userId)
        );
        
        setGauge(Metrics.CART_SIZE, labels, itemCount);
    }
    
    /**
     * Record product view
     */
    public void recordProductView(int productId, String category) {
        Map<String, String> labels = Map.of(
            "product_id", String.valueOf(productId),
            "category", category != null ? category : "unknown"
        );
        
        incrementCounter(Metrics.PRODUCT_VIEWS, labels);
    }
    
    /**
     * Record search query
     */
    public void recordSearchQuery(String query, int resultCount) {
        Map<String, String> labels = Map.of(
            "query", query.length() > 50 ? query.substring(0, 50) + "..." : query
        );
        
        incrementCounter(Metrics.SEARCH_QUERIES, labels);
        setGauge("search_result_count", labels, resultCount);
    }
    
    /**
     * Get all metrics in Prometheus format
     */
    public String getPrometheusMetrics() {
        StringBuilder sb = new StringBuilder();
        
        // Export counters
        for (Map.Entry<String, AtomicLong> entry : counters.entrySet()) {
            String metric = entry.getKey();
            long value = entry.getValue().get();
            
            if (metric.contains("{")) {
                // Labeled metric
                String baseName = metric.split("\\{")[0];
                String labels = metric.substring(metric.indexOf("{"));
                sb.append("# TYPE ").append(baseName).append(" counter\n");
                sb.append(baseName).append(labels).append(" ").append(value).append("\n");
            } else {
                // Unlabeled metric
                sb.append("# TYPE ").append(metric).append(" counter\n");
                sb.append(metric).append(" ").append(value).append("\n");
            }
        }
        
        // Export gauges
        for (Map.Entry<String, DoubleAdder> entry : gauges.entrySet()) {
            String metric = entry.getKey();
            double value = entry.getValue().sum();
            
            if (metric.contains("{")) {
                String baseName = metric.split("\\{")[0];
                String labels = metric.substring(metric.indexOf("{"));
                sb.append("# TYPE ").append(baseName).append(" gauge\n");
                sb.append(baseName).append(labels).append(" ").append(value).append("\n");
            } else {
                sb.append("# TYPE ").append(metric).append(" gauge\n");
                sb.append(metric).append(" ").append(value).append("\n");
            }
        }
        
        // Export histograms
        for (Map.Entry<String, Histogram> entry : histograms.entrySet()) {
            String metric = entry.getKey();
            Histogram histogram = entry.getValue();
            
            if (metric.contains("{")) {
                String baseName = metric.split("\\{")[0];
                String labels = metric.substring(metric.indexOf("{"));
                sb.append("# TYPE ").append(baseName).append(" histogram\n");
                sb.append("# TYPE ").append(baseName).append("_count counter\n");
                sb.append("# TYPE ").append(baseName).append("_sum counter\n");
                
                Map<String, Double> buckets = histogram.getBuckets();
                for (Map.Entry<String, Double> bucket : buckets.entrySet()) {
                    sb.append(baseName).append("_bucket").append(labels.replace("}", ",le=\"" + bucket.getKey() + "\"}"))
                      .append(" ").append(bucket.getValue().longValue()).append("\n");
                }
                
                sb.append(baseName).append("_bucket").append(labels.replace("}", ",le=\"+Inf\"}"))
                  .append(" ").append(histogram.getCount()).append("\n");
                sb.append(baseName).append("_count").append(labels).append(" ").append(histogram.getCount()).append("\n");
                sb.append(baseName).append("_sum").append(labels).append(" ").append(histogram.getSum()).append("\n");
            } else {
                sb.append("# TYPE ").append(metric).append(" histogram\n");
                sb.append("# TYPE ").append(metric).append("_count counter\n");
                sb.append("# TYPE ").append(metric).append("_sum counter\n");
                
                Map<String, Double> buckets = histogram.getBuckets();
                for (Map.Entry<String, Double> bucket : buckets.entrySet()) {
                    sb.append(metric).append("_bucket{le=\"").append(bucket.getKey()).append("\"} ")
                      .append(bucket.getValue().longValue()).append("\n");
                }
                
                sb.append(metric).append("_bucket{le=\"+Inf\"} ").append(histogram.getCount()).append("\n");
                sb.append(metric).append("_count ").append(histogram.getCount()).append("\n");
                sb.append(metric).append("_sum ").append(histogram.getSum()).append("\n");
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Reset all metrics
     */
    public void reset() {
        counters.clear();
        gauges.clear();
        histograms.clear();
        initializeDefaultMetrics();
    }
    
    /**
     * Get metric value
     */
    public long getCounter(String metricName) {
        AtomicLong counter = counters.get(metricName);
        return counter != null ? counter.get() : 0;
    }
    
    /**
     * Get gauge value
     */
    public double getGauge(String metricName) {
        DoubleAdder gauge = gauges.get(metricName);
        return gauge != null ? gauge.sum() : 0.0;
    }
    
    // Private helper methods
    
    private void initializeDefaultMetrics() {
        // Initialize commonly used metrics
        counters.put(Metrics.HTTP_REQUESTS_TOTAL, new AtomicLong(0));
        counters.put(Metrics.AUTHENTICATION_TOTAL, new AtomicLong(0));
        counters.put(Metrics.PAYMENT_TOTAL, new AtomicLong(0));
        counters.put(Metrics.ADMIN_ACTIONS_TOTAL, new AtomicLong(0));
        counters.put(Metrics.INVENTORY_UPDATES, new AtomicLong(0));
        counters.put(Metrics.DATABASE_OPERATIONS, new AtomicLong(0));
        counters.put(Metrics.CACHE_OPERATIONS, new AtomicLong(0));
        counters.put(Metrics.PRODUCT_VIEWS, new AtomicLong(0));
        counters.put(Metrics.SEARCH_QUERIES, new AtomicLong(0));
        
        gauges.put(Metrics.ACTIVE_USERS, new DoubleAdder());
        gauges.put(Metrics.CART_SIZE, new DoubleAdder());
        gauges.put(Metrics.ORDER_VALUE, new DoubleAdder());
        
        histograms.put(Metrics.HTTP_REQUEST_DURATION, new Histogram());
    }
    
    private String formatLabels(Map<String, String> labels) {
        if (labels == null || labels.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder("{");
        labels.forEach((key, value) -> {
            if (sb.length() > 1) sb.append(",");
            sb.append(key).append("=\"").append(value).append("\"");
        });
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Simple histogram implementation
     */
    private static class Histogram {
        private final AtomicLong count = new AtomicLong(0);
        private final DoubleAdder sum = new DoubleAdder();
        private final Map<String, DoubleAdder> buckets = new ConcurrentHashMap<>();
        
        public Histogram() {
            // Initialize default buckets
            String[] defaultBuckets = {"0.005", "0.01", "0.025", "0.05", "0.1", "0.25", "0.5", "1", "2.5", "5", "10"};
            for (String bucket : defaultBuckets) {
                buckets.put(bucket, new DoubleAdder());
            }
        }
        
        public void observe(double value) {
            count.incrementAndGet();
            sum.add(value);
            
            // Update buckets
            for (String bucket : buckets.keySet()) {
                try {
                    double threshold = Double.parseDouble(bucket);
                    if (value <= threshold) {
                        buckets.get(bucket).add(1);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid bucket
                }
            }
        }
        
        public long getCount() {
            return count.get();
        }
        
        public double getSum() {
            return sum.sum();
        }
        
        public Map<String, Double> getBuckets() {
            Map<String, Double> result = new ConcurrentHashMap<>();
            buckets.forEach((key, value) -> result.put(key, value.sum()));
            return result;
        }
    }
}
