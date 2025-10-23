package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import com.partpay.backend.util.HttpAttributes;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/adduser")
public class AddUserController {

    private final DatabaseService db;

    public AddUserController(DatabaseService db) {
        this.db = db;
    }

    public record NewUserRequest(@NotBlank String name, @Email String email, @NotBlank String password, @NotBlank String role, Double pay_per_hour) {}
    public record ExistingUserRequest(@Email String email, @NotBlank String role, Double pay_per_hour) {}

    @PostMapping("/new")
    public ResponseEntity<?> addNew(@RequestBody NewUserRequest req, HttpServletRequest request) {
        String orgName = HttpAttributes.getOrgName(request);
        Integer orgId = HttpAttributes.getOrgId(request);
        if (orgName == null || orgId == null) {
            return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        }
        try (Connection main = db.getMainConnection()) {
            main.setAutoCommit(false);
            int userId;
            try (
                PreparedStatement insertUser = main.prepareStatement("INSERT INTO users (name, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
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
                insertUserOrg.setInt(1, orgId);
                insertUserOrg.setInt(2, userId);
                insertUserOrg.setString(3, req.role());
                insertUserOrg.executeUpdate();
                main.commit();
            } catch (Exception e) {
                main.rollback();
                String msg = e.getMessage();
                if (msg != null && msg.contains("UNIQUE constraint failed: users.email")) {
                    return ResponseEntity.status(500).body(Map.of("message", "Email is already registerd"));
                }
                throw e;
            }

            if ("ptemployee".equals(req.role())) {
                try (Connection org = db.getOrgConnection(orgId, orgName)) {
                    try (PreparedStatement pst = org.prepareStatement("INSERT INTO parttimeemployee (uid, pay_per_hour) VALUES (?, ?)")) {
                        pst.setInt(1, userId);
                        pst.setDouble(2, req.pay_per_hour() != null ? req.pay_per_hour() : 10.0);
                        pst.executeUpdate();
                    }
                }
            }
            return ResponseEntity.status(201).body(Map.of("message", "User created successfully and added to ord successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @PostMapping("/existing")
    public ResponseEntity<?> addExisting(@RequestBody ExistingUserRequest req, HttpServletRequest request) {
        String orgName = HttpAttributes.getOrgName(request);
        Integer orgId = HttpAttributes.getOrgId(request);
        if (orgName == null || orgId == null) {
            return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        }
        try (Connection main = db.getMainConnection()) {
            int userId;
            try (PreparedStatement selUser = main.prepareStatement("SELECT id FROM users WHERE email = ?");
                 PreparedStatement insUserOrg = main.prepareStatement("INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)")) {
                selUser.setString(1, req.email());
                try (ResultSet rs = selUser.executeQuery()) {
                    if (!rs.next()) {
                        return ResponseEntity.status(404).body(Map.of("message", "User not found."));
                    }
                    userId = rs.getInt("id");
                }
                insUserOrg.setInt(1, orgId);
                insUserOrg.setInt(2, userId);
                insUserOrg.setString(3, req.role());
                insUserOrg.executeUpdate();
            }

            if ("ptemployee".equals(req.role())) {
                try (Connection org = db.getOrgConnection(orgId, orgName)) {
                    try (PreparedStatement pst = org.prepareStatement("INSERT INTO parttimeemployee (uid, pay_per_hour) VALUES (?, ?)")) {
                        pst.setInt(1, userId);
                        pst.setDouble(2, req.pay_per_hour() != null ? req.pay_per_hour() : 10.0);
                        pst.executeUpdate();
                    }
                }
            }
            return ResponseEntity.status(201).body(Map.of("message", "User added to org successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
