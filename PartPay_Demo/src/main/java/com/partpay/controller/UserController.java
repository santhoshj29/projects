package com.partpay.controller;

import com.partpay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @PostMapping("/adduser/new")
    public ResponseEntity<?> addNewUser(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            String name = (String) request.get("name");
            String email = (String) request.get("email");
            String password = (String) request.get("password");
            String role = (String) request.get("role");
            Double payPerHour = request.get("pay_per_hour") != null ? 
                    ((Number) request.get("pay_per_hour")).doubleValue() : null;
            String orgName = (String) httpRequest.getAttribute("org_name");
            
            Map<String, Object> response = userService.addNewUser(name, email, password, role, payPerHour, orgName);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PostMapping("/adduser/existing")
    public ResponseEntity<?> addExistingUser(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            String email = (String) request.get("email");
            String role = (String) request.get("role");
            Double payPerHour = request.get("pay_per_hour") != null ? 
                    ((Number) request.get("pay_per_hour")).doubleValue() : null;
            String orgName = (String) httpRequest.getAttribute("org_name");
            
            Map<String, Object> response = userService.addExistingUser(email, role, payPerHour, orgName);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("user_id");
            String role = (String) httpRequest.getAttribute("role");
            
            Map<String, Object> profile = userService.getUserProfile(userId, role);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody Map<String, Object> request, HttpServletRequest httpRequest) {
        try {
            Long userId = request.get("id") != null ? 
                    ((Number) request.get("id")).longValue() : 
                    (Long) httpRequest.getAttribute("user_id");
            
            String name = (String) request.get("name");
            String address = (String) request.get("address");
            String phone = (String) request.get("phone");
            String password = (String) request.get("password");
            String role = (String) httpRequest.getAttribute("role");
            String targetRole = (String) request.get("role");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) request.get("details");
            Double payPerHour = null;
            String accountNumber = null;
            String routingNumber = null;
            
            if (details != null) {
                payPerHour = details.get("pay_per_hour") != null ? 
                        ((Number) details.get("pay_per_hour")).doubleValue() : null;
                accountNumber = (String) details.get("account_number");
                routingNumber = (String) details.get("routing_number");
            }
            
            Map<String, Object> response = userService.updateUserProfile(
                    userId, name, address, phone, password, role, targetRole, 
                    payPerHour, accountNumber, routingNumber);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/employees")
    public ResponseEntity<?> getEmployees(@RequestBody(required = false) Map<String, String> request, 
                                          HttpServletRequest httpRequest) {
        try {
            Long orgId = (Long) httpRequest.getAttribute("org_id");
            String role = request != null ? request.get("role") : null;
            
            List<Map<String, Object>> employees = userService.getEmployees(orgId, role);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @GetMapping("/profile/employees")
    public ResponseEntity<?> getEmployeesList() {
        try {
            List<Map<String, Object>> employees = userService.getEmployeesList();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}