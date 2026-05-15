package com.fashionstore.integration;

import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseTestContainer {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTestContainer.class);
    
    public static final MySQLContainer<?> mysqlContainer;

    static {
        logger.info("Initializing Testcontainers MySQL Container...");
        mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("fashionstore")
            .withUsername("testuser")
            .withPassword("testpass")
            .withInitScript("db/schema.sql"); // Run our production schema on startup
        
        mysqlContainer.start();
        logger.info("Testcontainers MySQL Container started successfully at URL: {}", mysqlContainer.getJdbcUrl());
        
        // Expose credentials to DBConnection class via system properties
        System.setProperty("FASHIONSTORE_DB_URL", mysqlContainer.getJdbcUrl());
        System.setProperty("FASHIONSTORE_DB_USER", mysqlContainer.getUsername());
        System.setProperty("FASHIONSTORE_DB_PASSWORD", mysqlContainer.getPassword());
    }

    public static void start() {
        // Accessing the class will trigger static initializer and boot the container once.
    }
}
