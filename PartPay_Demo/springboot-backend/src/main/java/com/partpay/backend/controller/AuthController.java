package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import com.partpay.backend.security.JwtService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping
public class AuthController {

    private final DatabaseService databaseService;
    private final JwtService jwtService;

    public AuthController(DatabaseService databaseService, JwtService jwtService) {
        this.databaseService = databaseService;
        this.jwtService = jwtService;
    }

    public record LoginRequest(@Email String email, @NotBlank String password, @NotBlank String org_name) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try (Connection main = databaseService.getMainConnection()) {
            String getUserSql = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement getUser = main.prepareStatement(getUserSql)) {
                getUser.setString(1, request.email());
                try (ResultSet rs = getUser.executeQuery()) {
                    if (!rs.next()) {
                        return ResponseEntity.status(401).body(Map.of("auth", false, "message", "User dosent exist"));
                    }
                    int userId = rs.getInt("id");
                    String hashed = rs.getString("password");
                    if (!BCrypt.checkpw(request.password(), hashed)) {
                        return ResponseEntity.status(401).body(Map.of("auth", false, "message", "Wrong Password"));
                    }

                    String orgJoinSql = "SELECT * FROM user_orgs JOIN organizations ON user_orgs.org_id = organizations.id WHERE user_orgs.user_id = ? AND organizations.name = ?";
                    try (PreparedStatement join = main.prepareStatement(orgJoinSql)) {
                        join.setInt(1, userId);
                        join.setString(2, request.org_name());
                        try (ResultSet orgRs = join.executeQuery()) {
                            if (!orgRs.next()) {
                                return ResponseEntity.status(404).body(Map.of("auth", false, "message", "User is not associated with this organization."));
                            }
                            int orgId = orgRs.getInt("org_id");
                            String role = orgRs.getString("role");

                            // Fetch employee id if present
                            Integer employeeId = null;
                            try (Connection orgConn = databaseService.getOrgConnection(orgId, request.org_name())) {
                                String empSql = "SELECT id from parttimeemployee where uid = ?";
                                try (PreparedStatement empStmt = orgConn.prepareStatement(empSql)) {
                                    empStmt.setInt(1, userId);
                                    try (ResultSet empRs = empStmt.executeQuery()) {
                                        if (empRs.next()) {
                                            employeeId = empRs.getInt("id");
                                        }
                                    }
                                }
                            }

                            Map<String, Object> claims = new HashMap<>();
                            claims.put("employee_id", employeeId);
                            claims.put("user_id", userId);
                            claims.put("org_id", orgId);
                            claims.put("role", role);
                            claims.put("org_name", request.org_name());
                            String token = jwtService.generateToken(claims, 86400);

                            Map<String, Object> body = new HashMap<>();
                            body.put("auth", true);
                            body.put("token", token);
                            body.put("role", role);
                            body.put("employee_id", employeeId);
                            body.put("org_id", orgId);
                            body.put("message", "Login Successful");
                            return ResponseEntity.ok(body);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("auth", false, "message", "Internal Server Error"));
        }
    }
}
