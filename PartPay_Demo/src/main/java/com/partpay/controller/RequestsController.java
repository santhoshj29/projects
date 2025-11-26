package com.partpay.controller;

import com.partpay.model.entity.*;
import com.partpay.service.SwapLeaveOvertimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RequestsController {
    
    private final SwapLeaveOvertimeService requestService;
    
    // ========== Swap Request Endpoints ==========
    
    @PostMapping("/swap/new")
    public ResponseEntity<?> createSwapRequest(@RequestBody Map<String, Object> request) {
        try {
            Long shiftId = ((Number) request.get("shiftid")).longValue();
            Long targetShiftId = ((Number) request.get("targetshiftid")).longValue();
            String reason = (String) request.get("reason");
            
            Map<String, Object> response = requestService.createSwapRequest(shiftId, targetShiftId, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating swap request."));
        }
    }
    
    @GetMapping("/swap")
    public ResponseEntity<?> getAllSwapRequests() {
        try {
            List<SwapRequest> swapRequests = requestService.getAllSwapRequests();
            return ResponseEntity.ok(swapRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching swap requests."));
        }
    }
    
    @GetMapping("/swap/{id}")
    public ResponseEntity<?> getSwapRequestById(@PathVariable Long id) {
        try {
            SwapRequest swapRequest = requestService.getSwapRequestById(id);
            return ResponseEntity.ok(swapRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Swap request not found."));
        }
    }
    
    @PutMapping("/swap/{id}")
    public ResponseEntity<?> updateSwapRequestStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            Map<String, Object> response = requestService.updateSwapRequestStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", e.getMessage()));
        }
    }
    
    @DeleteMapping("/swap/{id}")
    public ResponseEntity<?> deleteSwapRequest(@PathVariable Long id) {
        try {
            Map<String, Object> response = requestService.deleteSwapRequest(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting swap request."));
        }
    }
    
    // ========== Leave Request Endpoints ==========
    
    @PostMapping("/leave/new")
    public ResponseEntity<?> createLeaveRequest(@RequestBody Map<String, Object> request) {
        try {
            Long shiftId = ((Number) request.get("shiftid")).longValue();
            String reason = (String) request.get("reason");
            
            Map<String, Object> response = requestService.createLeaveRequest(shiftId, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating leave request."));
        }
    }
    
    @GetMapping("/leave")
    public ResponseEntity<?> getAllLeaveRequests() {
        try {
            List<LeaveRequest> leaveRequests = requestService.getAllLeaveRequests();
            return ResponseEntity.ok(leaveRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching leave requests."));
        }
    }
    
    @GetMapping("/leave/{id}")
    public ResponseEntity<?> getLeaveRequestById(@PathVariable Long id) {
        try {
            LeaveRequest leaveRequest = requestService.getLeaveRequestById(id);
            return ResponseEntity.ok(leaveRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Leave request not found."));
        }
    }
    
    @PutMapping("/leave/{id}")
    public ResponseEntity<?> updateLeaveRequestStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            Map<String, Object> response = requestService.updateLeaveRequestStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating leave request."));
        }
    }
    
    @DeleteMapping("/leave/{id}")
    public ResponseEntity<?> deleteLeaveRequest(@PathVariable Long id) {
        try {
            Map<String, Object> response = requestService.deleteLeaveRequest(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting leave request."));
        }
    }
    
    // ========== Overtime Request Endpoints ==========
    
    @PostMapping("/overtime/new")
    public ResponseEntity<?> createOvertimeRequest(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = ((Number) request.get("employee_id")).longValue();
            LocalDate date = LocalDate.parse((String) request.get("date"));
            Integer hours = ((Number) request.get("hours")).intValue();
            
            Map<String, Object> response = requestService.createOvertimeRequest(employeeId, date, hours);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating overtime request."));
        }
    }
    
    @GetMapping("/overtime")
    public ResponseEntity<?> getAllOvertimeRequests() {
        try {
            List<OvertimeRequest> overtimeRequests = requestService.getAllOvertimeRequests();
            return ResponseEntity.ok(overtimeRequests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching overtime requests."));
        }
    }
    
    @GetMapping("/overtime/{id}")
    public ResponseEntity<?> getOvertimeRequestById(@PathVariable Long id) {
        try {
            OvertimeRequest overtimeRequest = requestService.getOvertimeRequestById(id);
            return ResponseEntity.ok(overtimeRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Overtime request not found."));
        }
    }
    
    @PutMapping("/overtime/{id}")
    public ResponseEntity<?> updateOvertimeRequestStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            Map<String, Object> response = requestService.updateOvertimeRequestStatus(id, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating overtime request."));
        }
    }
    
    @DeleteMapping("/overtime/{id}")
    public ResponseEntity<?> deleteOvertimeRequest(@PathVariable Long id) {
        try {
            Map<String, Object> response = requestService.deleteOvertimeRequest(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting overtime request."));
        }
    }
}