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
@RequestMapping("/adduser")
public class AddUserController {

    private final DatabaseService databaseService;

    public AddUserController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public record NewUserDetails(Double pay_per_hour, String account_number, String routing_number) {}
    public record NewUserRequest(@NotBlank String name, @Email String email, @NotBlank String password,
                                 @NotBlank String role, NewUserDetails details, Double pay_per_hour) {}

    @PostMapping("/new")
    public ResponseEntity<?> addNewUser(@RequestAttribute("org_id") Integer orgId,
                                        @RequestAttribute("org_name") String orgName,
                                        @RequestBody NewUserRequest req) {
        try (Connection main = databaseService.getMainConnection()) {
            main.setAutoCommit(false);
            int userId;
            try (PreparedStatement insertUser = main.prepareStatement(
                    "INSERT INTO users (name, email, password) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement insertUserOrg = main.prepareStatement(
                    "INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)")
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

                // If ptemployee, create org-specific row
                if ("ptemployee".equals(req.role())) {
                    double pph = req.pay_per_hour() != null ? req.pay_per_hour() :
                            (req.details() != null && req.details().pay_per_hour() != null ? req.details().pay_per_hour() : 10.0);
                    try (Connection org = databaseService.getOrgConnection(orgId, orgName);
                         PreparedStatement insertPT = org.prepareStatement(
                                 "INSERT INTO parttimeemployee (uid, pay_per_hour, account_number, routing_number) VALUES (?, ?, ?, ?)"
                         )) {
                        insertPT.setInt(1, userId);
                        insertPT.setDouble(2, pph);
                        insertPT.setString(3, req.details() != null ? req.details().account_number() : null);
                        insertPT.setString(4, req.details() != null ? req.details().routing_number() : null);
                        insertPT.executeUpdate();
                    }
                }

                main.commit();
            } catch (Exception e) {
                main.rollback();
                String msg = e.getMessage();
                if (msg != null && msg.contains("UNIQUE constraint failed: users.email")) {
                    return ResponseEntity.status(500).body(Map.of("message", "Email is already registerd"));
                }
                return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
            }
            return ResponseEntity.status(201).body(Map.of("message", "User created successfully and added to ord successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    public record ExistingUserRequest(@Email String email, @NotBlank String role, NewUserDetails details, Double pay_per_hour) {}

    @PostMapping("/existing")
    public ResponseEntity<?> addExistingUser(@RequestAttribute("org_id") Integer orgId,
                                             @RequestAttribute("org_name") String orgName,
                                             @RequestBody ExistingUserRequest req) {
        try (Connection main = databaseService.getMainConnection()) {
            main.setAutoCommit(false);
            int userId;
            try (PreparedStatement selectUser = main.prepareStatement("SELECT id FROM users WHERE email = ?");
                 PreparedStatement insertUserOrg = main.prepareStatement("INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)")) {
                selectUser.setString(1, req.email());
                try (ResultSet rs = selectUser.executeQuery()) {
                    if (!rs.next()) {
                        return ResponseEntity.status(404).body(Map.of("message", "User not found."));
                    }
                    userId = rs.getInt("id");
                }
                insertUserOrg.setInt(1, orgId);
                insertUserOrg.setInt(2, userId);
                insertUserOrg.setString(3, req.role());
                insertUserOrg.executeUpdate();

                if ("ptemployee".equals(req.role())) {
                    double pph = req.pay_per_hour() != null ? req.pay_per_hour() :
                            (req.details() != null && req.details().pay_per_hour() != null ? req.details().pay_per_hour() : 10.0);
                    try (Connection org = databaseService.getOrgConnection(orgId, orgName);
                         PreparedStatement insertPT = org.prepareStatement("INSERT INTO parttimeemployee (uid, pay_per_hour) VALUES (?, ?)")) {
                        insertPT.setInt(1, userId);
                        insertPT.setDouble(2, pph);
                        insertPT.executeUpdate();
                    }
                }

                main.commit();
            } catch (Exception e) {
                main.rollback();
                return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
            }
            return ResponseEntity.status(201).body(Map.of("message", "User added to org successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
