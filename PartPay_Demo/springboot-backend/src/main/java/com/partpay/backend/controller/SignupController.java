package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

@RestController
@RequestMapping("/signup")
public class SignupController {

    private final DatabaseService db;

    public SignupController(DatabaseService db) {
        this.db = db;
    }

    public record SignupRequest(@NotBlank String name, @Email String email, @NotBlank String password, @NotBlank String org_name) {}

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        try (Connection main = db.getMainConnection()) {
            main.setAutoCommit(false);
            int userId;
            int orgId;
            try (
                PreparedStatement insertUser = main.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement insertOrg = main.prepareStatement("INSERT INTO organizations (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
                PreparedStatement insertUserOrg = main.prepareStatement("INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)")
            ) {
                String hashed = BCrypt.hashpw(req.password(), BCrypt.gensalt(10));
                insertUser.setString(1, req.name());
                insertUser.setString(2, req.email());
                insertUser.setString(3, hashed);
                insertUser.executeUpdate();
                try (ResultSet keys = insertUser.getGeneratedKeys()) {
                    if (!keys.next()) throw new RuntimeException("Failed to get user id");
                    userId = keys.getInt(1);
                }

                insertOrg.setString(1, req.org_name());
                insertOrg.executeUpdate();
                try (ResultSet keys = insertOrg.getGeneratedKeys()) {
                    if (!keys.next()) throw new RuntimeException("Failed to get org id");
                    orgId = keys.getInt(1);
                }

                insertUserOrg.setInt(1, orgId);
                insertUserOrg.setInt(2, userId);
                insertUserOrg.setString(3, "admin");
                insertUserOrg.executeUpdate();

                main.commit();
            } catch (Exception e) {
                main.rollback();
                String msg = e.getMessage();
                if (msg != null && msg.contains("UNIQUE constraint failed: users.email")) {
                    return ResponseEntity.status(500).body(Map.of("message", "Email is already registerd"));
                } else if (msg != null && msg.contains("UNIQUE constraint failed: organizations.name")) {
                    return ResponseEntity.status(500).body(Map.of("message", "An org is already registerd with this name"));
                } else {
                    return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
                }
            }

            // Create org DB tables
            try (Connection org = db.getOrgConnection(orgId, req.org_name())) {
                createOrgTables(org);
            }

            return ResponseEntity.status(201).body(Map.of("message", "User signed up successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    private void createOrgTables(Connection org) throws Exception {
        String[] queries = new String[]{
                // parttimeemployee
                "CREATE TABLE IF NOT EXISTS parttimeemployee (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "uid INTEGER NOT NULL,\n" +
                        "pay_per_hour REAL NOT NULL,\n" +
                        "account_number TEXT,\n" +
                        "routing_number TEXT\n" +
                        ")",
                // employeeschedules
                "CREATE TABLE IF NOT EXISTS employeeschedules (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "employee_id INTEGER NOT NULL,\n" +
                        "date DATE NOT NULL,\n" +
                        "start_time TIMESTAMP NOT NULL,\n" +
                        "end_time TIMESTAMP NOT NULL,\n" +
                        "FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE\n" +
                        ")",
                // overtimerequests
                "CREATE TABLE IF NOT EXISTS overtimerequests (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "employee_id INTEGER NOT NULL,\n" +
                        "date DATE NOT NULL,\n" +
                        "hours INTEGER NOT NULL,\n" +
                        "status TEXT CHECK(status IN ('pending', 'approved', 'rejected')) NOT NULL,\n" +
                        "FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE\n" +
                        ")",
                // timesheets
                "CREATE TABLE IF NOT EXISTS timesheets (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "employee_id INTEGER NOT NULL,\n" +
                        "date DATE NOT NULL,\n" +
                        "actual_start_time TIMESTAMP NOT NULL,\n" +
                        "actual_end_time TIMESTAMP,\n" +
                        "FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE\n" +
                        ")",
                // swaprequests
                "CREATE TABLE IF NOT EXISTS swaprequests (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "shiftid INTEGER NOT NULL,\n" +
                        "targetshiftid INTEGER NOT NULL,\n" +
                        "reason TEXT,\n" +
                        "status TEXT CHECK(status IN ('pending', 'approved', 'rejected')) NOT NULL,\n" +
                        "FOREIGN KEY (shiftid) REFERENCES employeeschedules(id) ON DELETE CASCADE,\n" +
                        "FOREIGN KEY (targetshiftid) REFERENCES employeeschedules(id) ON DELETE CASCADE\n" +
                        ")",
                // leaverequests
                "CREATE TABLE IF NOT EXISTS leaverequests (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "shiftid INTEGER NOT NULL,\n" +
                        "reason TEXT,\n" +
                        "status TEXT CHECK(status IN ('pending', 'approved', 'rejected')) NOT NULL,\n" +
                        "FOREIGN KEY (shiftid) REFERENCES employeeschedules(id) ON DELETE CASCADE\n" +
                        ")",
                // taxinformation
                "CREATE TABLE IF NOT EXISTS taxinformation (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "name TEXT NOT NULL\n" +
                        ")",
                // taxtypes
                "CREATE TABLE IF NOT EXISTS taxtypes (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "name TEXT NOT NULL,\n" +
                        "deduction_percentage INTEGER NOT NULL\n" +
                        ")",
                // taxtaxtype
                "CREATE TABLE IF NOT EXISTS taxtaxtype (\n" +
                        "tax_information_id INTEGER NOT NULL,\n" +
                        "tax_type_id INTEGER NOT NULL,\n" +
                        "PRIMARY KEY (tax_information_id, tax_type_id),\n" +
                        "FOREIGN KEY (tax_information_id) REFERENCES taxinformation(id) ON DELETE CASCADE,\n" +
                        "FOREIGN KEY (tax_type_id) REFERENCES taxtypes(id) ON DELETE CASCADE\n" +
                        ")",
                // payslips
                "CREATE TABLE IF NOT EXISTS payslips (\n" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "employee_id INTEGER NOT NULL,\n" +
                        "generated_date DATE NOT NULL,\n" +
                        "from_date DATE NOT NULL,\n" +
                        "to_date DATE NOT NULL,\n" +
                        "hours_worked INTEGER NOT NULL,\n" +
                        "pay_per_hour INTEGER NOT NULL,\n" +
                        "gross_pay INTEGER NOT NULL,\n" +
                        "net_pay INTEGER NOT NULL,\n" +
                        "tax_id INTEGER NOT NULL,\n" +
                        "FOREIGN KEY (tax_id) REFERENCES taxinformation(id) ON DELETE CASCADE,\n" +
                        "FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE\n" +
                        ")"
        };
        try (Statement st = org.createStatement()) {
            for (String q : queries) {
                st.execute(q);
            }
        }
    }
}
