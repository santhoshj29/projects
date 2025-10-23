package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import com.partpay.backend.util.HttpAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final DatabaseService databaseService;

    public ProfileController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/employees")
    public ResponseEntity<?> listEmployees(HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) {
            return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        }
        try (Connection main = databaseService.getMainConnection();
             Connection org = databaseService.getOrgConnection(orgId, orgName)) {
            String listSql = "SELECT id, uid FROM parttimeemployee";
            List<Map<String, Object>> employees = new ArrayList<>();
            try (PreparedStatement st = org.prepareStatement(listSql);
                 ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int uid = rs.getInt("uid");
                    String name = null;
                    try (PreparedStatement userQuery = main.prepareStatement("SELECT name FROM users WHERE id = ?")) {
                        userQuery.setInt(1, uid);
                        try (ResultSet urs = userQuery.executeQuery()) {
                            if (urs.next()) {
                                name = urs.getString("name");
                            }
                        }
                    }
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("id", rs.getInt("id"));
                    entry.put("name", name);
                    employees.add(entry);
                }
            }
            return ResponseEntity.ok(employees);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving employees"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        Integer userId = HttpAttributes.getUserId(request);
        String role = HttpAttributes.getRole(request);
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (userId == null || role == null || orgId == null || orgName == null) {
            return ResponseEntity.status(440).body(Map.of("message", "User not found"));
        }
        try (Connection main = databaseService.getMainConnection();
             Connection org = databaseService.getOrgConnection(orgId, orgName)) {
            String userQuery = "SELECT users.id, users.name, users.email, users.address, users.phone, user_orgs.role " +
                    "FROM users JOIN user_orgs ON users.id = user_orgs.user_id WHERE users.id = ? AND user_orgs.role = ?";
            Map<String, Object> user = null;
            try (PreparedStatement st = main.prepareStatement(userQuery)) {
                st.setInt(1, userId);
                st.setString(2, role);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        user = new HashMap<>();
                        user.put("id", rs.getInt("id"));
                        user.put("name", rs.getString("name"));
                        user.put("email", rs.getString("email"));
                        user.put("address", rs.getString("address"));
                        user.put("phone", rs.getString("phone"));
                        user.put("role", rs.getString("role"));
                    }
                }
            }
            if (user == null) {
                return ResponseEntity.status(440).body(Map.of("message", "User not found"));
            }
            if ("ptemployee".equals(role)) {
                String ptQuery = "SELECT * FROM parttimeemployee WHERE uid = ?";
                try (PreparedStatement pst = org.prepareStatement(ptQuery)) {
                    pst.setInt(1, userId);
                    try (ResultSet prs = pst.executeQuery()) {
                        if (!prs.next()) {
                            return ResponseEntity.status(404).body(Map.of("message", "Part-time employee details not found"));
                        }
                        Map<String, Object> details = new HashMap<>();
                        details.put("id", prs.getInt("id"));
                        details.put("uid", prs.getInt("uid"));
                        details.put("pay_per_hour", prs.getDouble("pay_per_hour"));
                        details.put("account_number", prs.getString("account_number"));
                        details.put("routing_number", prs.getString("routing_number"));
                        user.put("details", details);
                    }
                }
            }
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving user profile"));
        }
    }

    public record ProfileDetails(Double pay_per_hour, String routing_number, String account_number) {}
    public record ProfileUpdate(@NotBlank String name, String address, String phone, String role, String password, ProfileDetails details, Integer id) {}

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdate update, HttpServletRequest request) {
        String requesterRole = HttpAttributes.getRole(request);
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        Integer userId = update.id() != null ? update.id() : HttpAttributes.getUserId(request);
        if (requesterRole == null || orgId == null || orgName == null || userId == null) {
            return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        }
        try (Connection main = databaseService.getMainConnection();
             Connection org = databaseService.getOrgConnection(orgId, orgName)) {

            if (update.details() != null && update.details().pay_per_hour() != null &&
                    ("admin".equals(requesterRole) || "manager".equals(requesterRole))) {
                try (PreparedStatement pst = org.prepareStatement("UPDATE parttimeemployee SET pay_per_hour = ? WHERE uid = ?")) {
                    pst.setDouble(1, update.details().pay_per_hour());
                    pst.setInt(2, userId);
                    pst.executeUpdate();
                }
            }

            if (update.password() != null && !update.password().isBlank() && "admin".equals(requesterRole)) {
                String hashed = BCrypt.hashpw(update.password(), BCrypt.gensalt(10));
                try (PreparedStatement pst = main.prepareStatement("UPDATE users SET password = ? WHERE id = ?")) {
                    pst.setString(1, hashed);
                    pst.setInt(2, userId);
                    pst.executeUpdate();
                }
            }

            int updatedUsers;
            try (PreparedStatement pst = main.prepareStatement("UPDATE users SET name = ?, address = ?, phone = ? WHERE id = ?")) {
                pst.setString(1, update.name());
                pst.setString(2, update.address());
                pst.setString(3, update.phone());
                pst.setInt(4, userId);
                updatedUsers = pst.executeUpdate();
            }

            String targetRole = update.role();
            if (("ptemployee".equals(targetRole)) || ("ptemployee".equals(requesterRole))) {
                ProfileDetails d = update.details();
                if (d != null) {
                    try (PreparedStatement pst = org.prepareStatement(
                            "UPDATE parttimeemployee SET routing_number = ?, account_number = ? WHERE uid = ?")) {
                        pst.setString(1, d.routing_number());
                        pst.setString(2, d.account_number());
                        pst.setInt(3, userId);
                        int updatedPt = pst.executeUpdate();
                        if (updatedUsers > 0 && updatedPt > 0) {
                            return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
                        }
                    }
                }
            }
            if (updatedUsers > 0) {
                return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
            }
            return ResponseEntity.status(404).body(Map.of("message", "User not found."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating user profile."));
        }
    }
}
