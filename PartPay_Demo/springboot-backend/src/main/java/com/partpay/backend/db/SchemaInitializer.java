package com.partpay.backend.db;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Ensures the main SQLite DB has the base tables needed by the app.
 * Mirrors the Node.js initDatabase() in serverFiles/database/start.js
 */
@Component
public class SchemaInitializer implements ApplicationRunner {

    private final DatabaseService databaseService;

    public SchemaInitializer(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection main = databaseService.getMainConnection();
             Statement st = main.createStatement()) {

            st.execute("CREATE TABLE IF NOT EXISTS organizations (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  name TEXT NOT NULL UNIQUE\n" +
                    ")");

            st.execute("CREATE TABLE IF NOT EXISTS users (\n" +
                    "  id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "  name TEXT NOT NULL,\n" +
                    "  email TEXT NOT NULL UNIQUE,\n" +
                    "  password TEXT NOT NULL,\n" +
                    "  address TEXT,\n" +
                    "  phone TEXT\n" +
                    ")");

            st.execute("CREATE TABLE IF NOT EXISTS user_orgs (\n" +
                    "  org_id INTEGER NOT NULL,\n" +
                    "  user_id INTEGER NOT NULL,\n" +
                    "  role TEXT NOT NULL,\n" +
                    "  PRIMARY KEY (org_id, user_id),\n" +
                    "  FOREIGN KEY (org_id) REFERENCES organizations(id) ON DELETE CASCADE,\n" +
                    "  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE\n" +
                    ")");
        }
    }
}
