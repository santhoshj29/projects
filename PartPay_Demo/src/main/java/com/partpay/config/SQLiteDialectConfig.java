package com.partpay.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuration for SQLite-specific settings
 */
@Configuration
public class SQLiteDialectConfig {
    
    @PostConstruct
    public void init() {
        // Enable foreign keys for SQLite
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQLite JDBC driver not found", e);
        }
    }
    
    /**
     * Enable foreign keys for a SQLite connection
     */
    public static void enableForeignKeys(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON;");
        }
    }
    
    /**
     * Get database path for organization
     */
    public static String getOrgDatabasePath(Long orgId, String orgName) {
        return "./serverFiles/database/" + orgId + "_" + orgName + ".sqlite";
    }
    
    /**
     * Get JDBC URL for organization database
     */
    public static String getOrgDatabaseUrl(Long orgId, String orgName) {