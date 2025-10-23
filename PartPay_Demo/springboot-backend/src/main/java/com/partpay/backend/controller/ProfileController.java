package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final DatabaseService db;

    public ProfileController(DatabaseService db) {
        this.db = db;
    }

    public record UpdateDetails(Double pay_per_hour, String routing_number, String account_number) {}
    public record UpdateProfileRequest(Integer id, String name, String address, String phone, String role, String password, UpdateDetails details) {}

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestAttribute("user_id") Integer userId,
                                        @RequestAttribute("org_id") Integer orgId,
                                        @RequestAttribute("org_name") String orgName,
                                        @RequestAttribute(value = "role", required = false) String role) {
        try (Connection main = db.getMainConnection()) {
            String userQuery = "SELECT users.id, users.name, users.email, users.address, users.phone, user_orgs.role\n" +
                    "FROM users JOIN user_orgs ON users.id = user_orgs.user_id\n" +
                    "WHERE users.id = ? AND user_orgs.org_id = ?";
            Map<String, Object> user = null;
            try (PreparedStatement st = main.prepareStatement(userQuery)) {
                st.setInt(1, userId);
                st.setInt(2, orgId);
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next()) return ResponseEntity.status(440).body(Map.of("message", "User not found"));
                    user = new HashMap<>();
                    user.put("id", rs.getInt("id"));
                    user.put("name", rs.getString("name"));
                    user.put("email", rs.getString("email"));
                    user.put("address", rs.getString("address"));
                    user.put("phone", rs.getString("phone"));
                    user.put("role", rs.getString("role"));
                }
            }

            if ("ptemployee".equals(user.get("role"))) {
                try (Connection orgConn = db.getOrgConnection(orgId, orgName);
                     PreparedStatement pt = orgConn.prepareStatement("SELECT * FROM parttimeemployee WHERE uid = ?")) {
                    pt.setInt(1, userId);
                    try (ResultSet rs = pt.executeQuery()) {
                        if (rs.next()) {
                            Map<String, Object> details = new HashMap<>();
                            details.put("id", rs.getInt("id"));
                            details.put("uid", rs.getInt("uid"));
                            details.put("pay_per_hour", rs.getDouble("pay_per_hour"));
                            details.put("account_number", rs.getString("account_number"));
                            details.put("routing_number", rs.getString("routing_number"));
                            user.put("details", details);
                        }
                    }
                }
            }

            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving user profile"));
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestAttribute("user_id") Integer requesterUserId,
                                           @RequestAttribute("org_id") Integer orgId,
                                           @RequestAttribute("org_name") String orgName,
                                           @RequestAttribute("role") String requesterRole,
                                           @RequestBody UpdateProfileRequest req) {
        try (Connection main = db.getMainConnection()) {
            int targetUserId = req.id() != null ? req.id() : requesterUserId;

            // Update users table
            try (PreparedStatement upd = main.prepareStatement("UPDATE users SET name = ?, address = ?, phone = ? WHERE id = ?")) {
                upd.setString(1, req.name());
                upd.setString(2, req.address());
                upd.setString(3, req.phone());
                upd.setInt(4, targetUserId);
                int changed = upd.executeUpdate();
                if (changed == 0) return ResponseEntity.status(404).body(Map.of("message", "User not found."));
            }

            // If pt employee fields supplied
            if (req.details() != null && (Objects.equals(req.role(), "ptemployee") || Objects.equals(requesterRole, "ptemployee") || Objects.equals(requesterRole, "admin") || Objects.equals(requesterRole, "manager"))) {
                try (Connection orgConn = db.getOrgConnection(orgId, orgName);
                     PreparedStatement ptUpd = orgConn.prepareStatement("UPDATE parttimeemployee SET routing_number = ?, account_number = ?, pay_per_hour = COALESCE(?, pay_per_hour) WHERE uid = ?")) {
                    ptUpd.setString(1, req.details().routing_number());
                    ptUpd.setString(2, req.details().account_number());
                    ptUpd.setObject(3, req.details().pay_per_hour());
                    ptUpd.setInt(4, targetUserId);
                    ptUpd.executeUpdate();
                }
            }

            return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating user profile."));
        }
    }

    @GetMapping("/employees")
    public ResponseEntity<?> listEmployees(@RequestAttribute("org_id") Integer orgId,
                                           @RequestAttribute("org_name") String orgName) {
        try (Connection main = db.getMainConnection(); Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            String userQuery = "SELECT users.id, users.name FROM users JOIN user_orgs ON users.id = user_orgs.user_id WHERE user_orgs.org_id = ? AND user_orgs.role = 'ptemployee'";
            List<Map<String, Object>> result = new ArrayList<>();
            try (PreparedStatement st = main.prepareStatement(userQuery)) {
                st.setInt(1, orgId);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        int userId = rs.getInt("id");
                        // get employee id
                        try (PreparedStatement emp = orgConn.prepareStatement("SELECT id FROM parttimeemployee WHERE uid = ?")) {
                            emp.setInt(1, userId);
                            try (ResultSet er = emp.executeQuery()) {
                                if (er.next()) {
                                    result.add(Map.of("id", er.getInt("id"), "name", rs.getString("name")));
                                }
                            }
                        }
                    }
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving employees"));
        }
    }
}
