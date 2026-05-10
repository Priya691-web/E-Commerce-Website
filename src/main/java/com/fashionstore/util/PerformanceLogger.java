package com.fashionstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance Logger for tracking request execution times
 * and performance metrics.
 */
public class PerformanceLogger {
    
    private static final Logger logger = LoggerFactory.getLogger("com.fashionstore.performance");
    
    private final String operation;
    private final long startTime;
    
    public PerformanceLogger(String operation) {
        this.operation = operation;
        this.startTime = System.currentTimeMillis();
    }
    
    /**
     * Log the execution time of the operation
     */
    public void log() {
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Operation: {} | Execution Time: {}ms", operation, executionTime);
    }
    
    /**
     * Log the execution time with additional context
     */
    public void log(String additionalContext) {
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Operation: {} | Execution Time: {}ms | Context: {}", operation, executionTime, additionalContext);
    }
    
    /**
     * Log if execution time exceeds threshold
     */
    public void logIfSlow(long thresholdMs) {
        long executionTime = System.currentTimeMillis() - startTime;
        if (executionTime > thresholdMs) {
            logger.warn("SLOW OPERATION: {} | Execution Time: {}ms | Threshold: {}ms", operation, executionTime, thresholdMs);
        } else {
            logger.info("Operation: {} | Execution Time: {}ms", operation, executionTime);
        }
    }
    
    /**
     * Create a new PerformanceLogger instance
     */
    public static PerformanceLogger start(String operation) {
        return new PerformanceLogger(operation);
    }
}
