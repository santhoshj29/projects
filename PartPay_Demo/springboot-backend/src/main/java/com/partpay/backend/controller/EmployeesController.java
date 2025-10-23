package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import com.partpay.backend.util.HttpAttributes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    private final DatabaseService databaseService;

    public EmployeesController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping
    public ResponseEntity<?> listUsersInOrg(HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) {
            return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        }
        try (Connection main = databaseService.getMainConnection();
             Connection org = databaseService.getOrgConnection(orgId, orgName)) {
            String userQuery = "SELECT users.id, users.name, users.email, users.address, users.phone, user_orgs.role " +
                    "FROM users JOIN user_orgs ON users.id = user_orgs.user_id WHERE user_orgs.org_id = ?";
            List<Map<String, Object>> users = new ArrayList<>();
            try (PreparedStatement st = main.prepareStatement(userQuery)) {
                st.setInt(1, orgId);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        user.put("id", rs.getInt("id"));
                        user.put("name", rs.getString("name"));
                        user.put("email", rs.getString("email"));
                        user.put("address", rs.getString("address"));
                        user.put("phone", rs.getString("phone"));
                        user.put("role", rs.getString("role"));
                        if ("ptemployee".equals(rs.getString("role"))) {
                            String ptQuery = "SELECT * FROM parttimeemployee WHERE uid = ?";
                            try (PreparedStatement pst = org.prepareStatement(ptQuery)) {
                                pst.setInt(1, rs.getInt("id"));
                                try (ResultSet prs = pst.executeQuery()) {
                                    if (prs.next()) {
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
                        }
                        users.add(user);
                    }
                }
            }
            if (users.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "No users found"));
            }
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving user profiles"));
        }
    }
}
