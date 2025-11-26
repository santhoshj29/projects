package com.partpay.controller;

import com.partpay.model.entity.TaxType;
import com.partpay.service.TaxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaxController {
    
    private final TaxService taxService;
    
    // ========== TaxType Endpoints ==========
    
    @PostMapping("/taxtypes/new")
    public ResponseEntity<?> createTaxType(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Integer deductionPercentage = ((Number) request.get("deduction_percentage")).intValue();
            
            Map<String, Object> response = taxService.createTaxType(name, deductionPercentage);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating tax type."));
        }
    }
    
    @GetMapping("/taxtypes")
    public ResponseEntity<?> getAllTaxTypes() {
        try {
            List<TaxType> taxTypes = taxService.getAllTaxTypes();
            return ResponseEntity.ok(taxTypes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching tax types."));
        }
    }
    
    @GetMapping("/taxtypes/{id}")
    public ResponseEntity<?> getTaxTypeById(@PathVariable Long id) {
        try {
            TaxType taxType = taxService.getTaxTypeById(id);
            return ResponseEntity.ok(taxType);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tax type not found."));
        }
    }
    
    @PutMapping("/taxtypes/{id}")
    public ResponseEntity<?> updateTaxType(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Integer deductionPercentage = request.get("deduction_percentage") != null ? 
                    ((Number) request.get("deduction_percentage")).intValue() : null;
            
            Map<String, Object> response = taxService.updateTaxType(id, name, deductionPercentage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating tax type."));
        }
    }
    
    @DeleteMapping("/taxtypes/{id}")
    public ResponseEntity<?> deleteTaxType(@PathVariable Long id) {
        try {
            Map<String, Object> response = taxService.deleteTaxType(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting tax type."));
        }
    }
    
    // ========== TaxInformation Endpoints ==========
    
    @PostMapping("/tax/new")
    public ResponseEntity<?> createTaxInformation(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            @SuppressWarnings("unchecked")
            List<Number> taxTypeIdsNum = (List<Number>) request.get("tax_types");
            List<Long> taxTypeIds = taxTypeIdsNum.stream()
                    .map(Number::longValue)
                    .toList();
            
            Map<String, Object> response = taxService.createTaxInformation(name, taxTypeIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating tax information please check id's of tax types."));
        }
    }
    
    @GetMapping("/tax")
    public ResponseEntity<?> getAllTaxInformation() {
        try {
            List<Map<String, Object>> taxInformation = taxService.getAllTaxInformation();
            return ResponseEntity.ok(taxInformation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching tax information."));
        }
    }
    
    @GetMapping("/tax/{id}")
    public ResponseEntity<?> getTaxInformationById(@PathVariable Long id) {
        try {
            Map<String, Object> taxInformation = taxService.getTaxInformationById(id);
            return ResponseEntity.ok(taxInformation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tax information not found."));
        }
    }
    
    @PutMapping("/tax/{id}")
    public ResponseEntity<?> updateTaxInformation(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            @SuppressWarnings("unchecked")
            List<Number> taxTypeIdsNum = (List<Number>) request.get("tax_types");
            List<Long> taxTypeIds = taxTypeIdsNum.stream()
                    .map(Number::longValue)
                    .toList();
            
            Map<String, Object> response = taxService.updateTaxInformation(id, name, taxTypeIds);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating tax information please check id's of tax types."));
        }
    }
    
    @DeleteMapping("/tax/{id}")
    public ResponseEntity<?> deleteTaxInformation(@PathVariable Long id) {
        try {
            Map<String, Object> response = taxService.deleteTaxInformation(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting tax information."));
        }
    }
}