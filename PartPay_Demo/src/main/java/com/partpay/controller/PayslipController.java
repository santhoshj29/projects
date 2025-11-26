package com.partpay.controller;

import com.partpay.model.entity.Payslip;
import com.partpay.service.PayslipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payslips")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PayslipController {
    
    private final PayslipService payslipService;
    
    @PostMapping("/generate")
    public ResponseEntity<?> generatePayslips(@RequestBody Map<String, Object> request) {
        try {
            LocalDate startDate = LocalDate.parse((String) request.get("start_date"));
            LocalDate endDate = LocalDate.parse((String) request.get("to_date"));
            Long taxId = ((Number) request.get("tax_id")).longValue();
            
            Map<String, Object> response = payslipService.generatePayslips(startDate, endDate, taxId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }
    
    @GetMapping("/{employee_id}")
    public ResponseEntity<?> getPayslipsByEmployeeId(@PathVariable("employee_id") Long employeeId) {
        try {
            List<Payslip> payslips = payslipService.getPayslipsByEmployeeId(employeeId);
            return ResponseEntity.ok(payslips);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllPayslips() {
        try {
            List<Payslip> payslips = payslipService.getAllPayslips();
            return ResponseEntity.ok(payslips);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Internal server error"));
        }
    }
}