package com.fashionstore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Enterprise Transaction Manager
 * Provides a clean abstraction for handling JDBC transactions
 * Ensures connections are automatically closed and rollbacks are guaranteed on failure.
 */
public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    @FunctionalInterface
    public interface TransactionalTask<T> {
        T execute(Connection conn) throws Exception;
    }

    /**
     * Executes the given task within a transactional context.
     * Guaranteed to commit on success, rollback on exception, and auto-close the connection.
     * 
     * @param task The logic to execute
     * @return The result of the task
     * @throws Exception If any step of the transaction fails
     */
    public static <T> T executeInTransaction(TransactionalTask<T> task) throws Exception {
        // Try-with-resources guarantees the connection is closed.
        try (Connection conn = DBConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                
                T result = task.execute(conn);
                
                conn.commit();
                return result;
            } catch (Exception e) {
                logger.error("Transaction failed, initiating rollback", e);
                try {
                    conn.rollback();
                } catch (SQLException re) {
                    logger.error("CRITICAL: Transaction rollback failed", re);
                }
                throw e; // Rethrow to let the caller handle business-level exception logging
            } finally {
                // Restore original auto-commit state before closing
                try {
                    conn.setAutoCommit(originalAutoCommit);
                } catch (SQLException ce) {
                    logger.error("Failed to restore auto-commit state", ce);
                }
            }
        }
    }
}
