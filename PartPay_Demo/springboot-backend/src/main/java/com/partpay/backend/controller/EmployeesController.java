package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/employees")
public class EmployeesController {

    private final DatabaseService db;

    public EmployeesController(DatabaseService db) {
        this.db = db;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        try (Connection main = db.getMainConnection(); Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            String usersSql = "SELECT users.id, users.name, users.email, users.address, users.phone, user_orgs.role\n" +
                    "FROM users JOIN user_orgs ON users.id = user_orgs.user_id WHERE user_orgs.org_id = ?";
            List<Map<String, Object>> list = new ArrayList<>();
            try (PreparedStatement st = main.prepareStatement(usersSql)) {
                st.setInt(1, orgId);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> user = new HashMap<>();
                        int uid = rs.getInt("id");
                        user.put("id", uid);
                        user.put("name", rs.getString("name"));
                        user.put("email", rs.getString("email"));
                        user.put("address", rs.getString("address"));
                        user.put("phone", rs.getString("phone"));
                        String role = rs.getString("role");
                        user.put("role", role);
                        if ("ptemployee".equals(role)) {
                            try (PreparedStatement pt = orgConn.prepareStatement("SELECT * FROM parttimeemployee WHERE uid = ?")) {
                                pt.setInt(1, uid);
                                try (ResultSet pr = pt.executeQuery()) {
                                    if (pr.next()) {
                                        Map<String, Object> details = new HashMap<>();
                                        details.put("id", pr.getInt("id"));
                                        details.put("uid", pr.getInt("uid"));
                                        details.put("pay_per_hour", pr.getDouble("pay_per_hour"));
                                        details.put("account_number", pr.getString("account_number"));
                                        details.put("routing_number", pr.getString("routing_number"));
                                        user.put("details", details);
                                    }
                                }
                            }
                        }
                        list.add(user);
                    }
                }
            }
            if (list.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "No users found"));
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error retrieving user profiles"));
        }
    }
}
