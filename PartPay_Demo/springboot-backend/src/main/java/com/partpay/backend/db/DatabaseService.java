package com.partpay.backend.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class DatabaseService {

    private final Path dbDir;

    public DatabaseService(@Value("${partpay.db.dir}") String dbDir) {
        Path dir = Paths.get(dbDir);
        if (!dir.isAbsolute()) {
            dir = Paths.get("").toAbsolutePath().resolve(dbDir).normalize();
        }
        this.dbDir = dir;
    }

    public Connection getMainConnection() throws SQLException {
        return openConnection("Group4_PartPay.sqlite");
    }

    public Connection getOrgConnection(long orgId, String orgName) throws SQLException {
        String filename = orgId + "_" + orgName + ".sqlite";
        return openConnection(filename);
    }

    private Connection openConnection(String filename) throws SQLException {
        try {
            if (!Files.exists(dbDir)) {
                Files.createDirectories(dbDir);
            }
        } catch (Exception e) {
            throw new SQLException("Failed to ensure DB directory exists: " + dbDir, e);
        }
        String url = "jdbc:sqlite:" + dbDir.resolve(filename).toString();
        Connection conn = DriverManager.getConnection(url);
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
}
