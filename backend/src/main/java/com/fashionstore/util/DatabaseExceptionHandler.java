package com.fashionstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Utility class for safe database resource cleanup and exception handling
 * Prevents connection leaks and provides consistent error logging
 */
public class DatabaseExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseExceptionHandler.class);
    
    /**
     * Safely close a ResultSet with proper logging
     */
    public static void closeResultSet(ResultSet rs, String context) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.warn("Failed to close ResultSet in {}: {}", context, e.getMessage());
            }
        }
    }
    
    /**
     * Safely close a PreparedStatement with proper logging
     */
    public static void closePreparedStatement(PreparedStatement ps, String context) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                logger.warn("Failed to close PreparedStatement in {}: {}", context, e.getMessage());
            }
        }
    }
    
    /**
     * Safely close a Connection with proper logging
     */
    public static void closeConnection(Connection conn, String context) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                logger.warn("Failed to close Connection in {}: {}", context, e.getMessage());
            }
        }
    }
    
    /**
     * Safely rollback a transaction with proper logging
     */
    public static void rollbackTransaction(Connection conn, String context) {
        if (conn != null) {
            try {
                conn.rollback();
                logger.info("Transaction rolled back in {}", context);
            } catch (SQLException e) {
                logger.error("Failed to rollback transaction in {}: {}", context, e.getMessage());
            }
        }
    }
    
    /**
     * Safely commit a transaction with proper logging
     */
    public static void commitTransaction(Connection conn, String context) {
        if (conn != null) {
            try {
                conn.commit();
                logger.info("Transaction committed in {}", context);
            } catch (SQLException e) {
                logger.error("Failed to commit transaction in {}: {}", context, e.getMessage());
            }
        }
    }
    
    /**
     * Handle SQLException with context-aware logging
     */
    public static void handleSQLException(SQLException e, String context, Object... params) {
        String message = String.format("SQL Error in %s: %s", context, e.getMessage());
        if (params.length > 0) {
            message += " [Parameters: ";
            for (int i = 0; i < params.length; i++) {
                message += params[i];
                if (i < params.length - 1) message += ", ";
            }
            message += "]";
        }
        logger.error(message, e);
    }
    
    /**
     * Validate database connectivity
     */
    public static boolean validateConnection() {
        try {
            return DBConnection.isHealthy();
        } catch (Exception e) {
            logger.error("Database connectivity check failed: {}", e.getMessage());
            return false;
        }
    }
}
