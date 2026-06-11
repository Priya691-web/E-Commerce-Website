package com.fashionstore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

/**
 * Database Configuration
 * Centralized database configuration management
 * Follows single responsibility principle
 */
public class DatabaseConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
    private static HikariDataSource dataSource;
    
    // Database configuration constants
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/fashionstore?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Tarun@1605";
    
    // Connection pool configuration
    private static final int DEV_POOL_SIZE = 5;
    private static final int PROD_POOL_SIZE = 50; // Increased for production load
    private static final int PROD_MIN_IDLE = 10;
    private static final int DEV_MIN_IDLE = 2;
    private static final int CONNECTION_TIMEOUT = 30000;
    private static final int IDLE_TIMEOUT = 600000;
    private static final int MAX_LIFETIME = 1800000;
    private static final long LEAK_DETECTION_THRESHOLD = 60000; // 1 minute
    
    private DatabaseConfig() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Initialize database connection pool
     */
    public static synchronized void initialize() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Database connection pool already initialized");
            return;
        }
        
        try {
            HikariConfig config = new HikariConfig();
            
            // Load database configuration from environment or defaults
            String url = getEnvOrDefault("FASHIONSTORE_DB_URL", DEFAULT_URL);
            String user = getEnvOrDefault("FASHIONSTORE_DB_USER", DEFAULT_USER);
            String password = getEnvOrDefault("FASHIONSTORE_DB_PASSWORD", DEFAULT_PASSWORD);
            String profile = getEnvOrDefault("FASHIONSTORE_PROFILE", "dev");
            
            config.setJdbcUrl(url);
            config.setUsername(user);
            config.setPassword(password);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            
            // Pool configuration based on profile
            int poolSize = "prod".equalsIgnoreCase(profile) ? PROD_POOL_SIZE : DEV_POOL_SIZE;
            int minIdle = "prod".equalsIgnoreCase(profile) ? PROD_MIN_IDLE : DEV_MIN_IDLE;
            config.setMaximumPoolSize(poolSize);
            config.setMinimumIdle(minIdle);
            config.setConnectionTimeout(CONNECTION_TIMEOUT);
            config.setIdleTimeout(IDLE_TIMEOUT);
            config.setMaxLifetime(MAX_LIFETIME);
            
            // Connection leak detection (production only)
            if ("prod".equalsIgnoreCase(profile)) {
                config.setLeakDetectionThreshold(LEAK_DETECTION_THRESHOLD);
            }
            
            // Performance optimizations
            config.setPoolName("FashionStoreHikariPool");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(config);
            
            // Validate connection
            try (Connection testConn = dataSource.getConnection()) {
                if (testConn.isValid(5)) {
                    logger.info("Database connection pool initialized successfully");
                    logger.info("Pool size: {}, Profile: {}", poolSize, profile);
                } else {
                    throw new RuntimeException("Connection validation failed");
                }
            }
            
        } catch (Exception e) {
            logger.error("Failed to initialize database connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    /**
     * Get data source
     */
    public static HikariDataSource getDataSource() {
        if (dataSource == null || dataSource.isClosed()) {
            initialize();
        }
        return dataSource;
    }
    
    /**
     * Get database connection
     */
    public static Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (Exception e) {
            logger.error("Failed to get database connection", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }
    
    /**
     * Close data source
     */
    public static synchronized void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing database connection pool");
            dataSource.close();
            dataSource = null;
        }
    }
    
    /**
     * Check if database is healthy
     */
    public static boolean isHealthy() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                return false;
            }
            try (Connection conn = dataSource.getConnection()) {
                return conn.isValid(5);
            }
        } catch (Exception e) {
            logger.error("Database health check failed", e);
            return false;
        }
    }
    
    /**
     * Get environment variable or default value
     */
    private static String getEnvOrDefault(String envVar, String defaultValue) {
        String value = System.getenv(envVar);
        if (value == null || value.trim().isEmpty()) {
            logger.debug("Environment variable {} not set, using default", envVar);
            return defaultValue;
        }
        return value.trim();
    }
    
    /**
     * Get current pool statistics
     */
    public static String getPoolStatistics() {
        if (dataSource == null || dataSource.isClosed()) {
            return "Pool not initialized";
        }
        
        HikariPoolMXBean poolProxy = dataSource.getHikariPoolMXBean();
        return String.format(
            "Active: %d, Idle: %d, Total: %d, Waiting: %d",
            poolProxy.getActiveConnections(),
            poolProxy.getIdleConnections(),
            poolProxy.getTotalConnections(),
            poolProxy.getThreadsAwaitingConnection()
        );
    }
}
