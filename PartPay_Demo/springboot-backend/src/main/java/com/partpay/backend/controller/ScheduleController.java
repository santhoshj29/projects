package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import com.partpay.backend.util.HttpAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final DatabaseService db;

    public ScheduleController(DatabaseService db) {
        this.db = db;
    }

    public record SchedulePayload(@NotNull Integer employee_id, @NotNull String date, @NotNull String start_time, @NotNull String end_time) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody SchedulePayload payload, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("INSERT INTO employeeschedules (employee_id, date, start_time, end_time) VALUES (?, ?, ?, ?)")) {
                st.setInt(1, payload.employee_id());
                st.setString(2, payload.date());
                st.setString(3, payload.start_time());
                st.setString(4, payload.end_time());
                st.executeUpdate();
            }
            return ResponseEntity.status(201).body(Map.of("message", "Schedule created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating schedule."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            List<Map<String, Object>> schedules = new ArrayList<>();
            try (PreparedStatement st = org.prepareStatement("SELECT * FROM employeeschedules"); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("date", rs.getString("date"));
                    row.put("start_time", rs.getString("start_time"));
                    row.put("end_time", rs.getString("end_time"));
                    schedules.add(row);
                }
            }
            return ResponseEntity.ok(schedules);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching schedules."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("SELECT * FROM employeeschedules WHERE id = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("id", rs.getInt("id"));
                        row.put("employee_id", rs.getInt("employee_id"));
                        row.put("date", rs.getString("date"));
                        row.put("start_time", rs.getString("start_time"));
                        row.put("end_time", rs.getString("end_time"));
                        return ResponseEntity.ok(row);
                    }
                }
            }
            return ResponseEntity.status(404).body(Map.of("message", "Schedule not found."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching schedule."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody SchedulePayload payload, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("UPDATE employeeschedules SET employee_id = ?, date = ?, start_time = ?, end_time = ? WHERE id = ?")) {
                st.setInt(1, payload.employee_id());
                st.setString(2, payload.date());
                st.setString(3, payload.start_time());
                st.setString(4, payload.end_time());
                st.setInt(5, id);
                st.executeUpdate();
            }
            return ResponseEntity.ok(Map.of("message", "Schedule updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating schedule."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("DELETE FROM employeeschedules WHERE id = ?")) {
                st.setInt(1, id);
                st.executeUpdate();
            }
            return ResponseEntity.ok(Map.of("message", "Schedule deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting schedule."));
        }
    }
}
