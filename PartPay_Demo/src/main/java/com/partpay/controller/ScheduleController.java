package com.partpay.controller;

import com.partpay.model.entity.EmployeeSchedule;
import com.partpay.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    @PostMapping("/new")
    public ResponseEntity<?> createSchedule(@RequestBody Map<String, Object> request) {
        try {
            Long employeeId = ((Number) request.get("employee_id")).longValue();
            LocalDate date = LocalDate.parse((String) request.get("date"));
            LocalDateTime startTime = LocalDateTime.parse((String) request.get("start_time"));
            LocalDateTime endTime = LocalDateTime.parse((String) request.get("end_time"));
            
            Map<String, Object> response = scheduleService.createSchedule(employeeId, date, startTime, endTime);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error creating schedule."));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> getAllSchedules() {
        try {
            List<EmployeeSchedule> schedules = scheduleService.getAllSchedules();
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error fetching schedules."));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleById(@PathVariable Long id) {
        try {
            EmployeeSchedule schedule = scheduleService.getScheduleById(id);
            return ResponseEntity.ok(schedule);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Schedule not found."));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long employeeId = request.get("employee_id") != null ? 
                    ((Number) request.get("employee_id")).longValue() : null;
            LocalDate date = request.get("date") != null ? 
                    LocalDate.parse((String) request.get("date")) : null;
            LocalDateTime startTime = request.get("start_time") != null ? 
                    LocalDateTime.parse((String) request.get("start_time")) : null;
            LocalDateTime endTime = request.get("end_time") != null ? 
                    LocalDateTime.parse((String) request.get("end_time")) : null;
            
            Map<String, Object> response = scheduleService.updateSchedule(id, employeeId, date, startTime, endTime);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error updating schedule."));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id) {
        try {
            Map<String, Object> response = scheduleService.deleteSchedule(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error deleting schedule."));
        }
    }
}