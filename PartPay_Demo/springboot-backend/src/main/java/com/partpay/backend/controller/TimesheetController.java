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
@RequestMapping("/timesheet")
public class TimesheetController {

    private final DatabaseService db;

    public TimesheetController(DatabaseService db) {
        this.db = db;
    }

    public record TimesheetPayload(@NotNull Integer employee_id, @NotNull String date, @NotNull String actual_start_time, String actual_end_time) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestBody TimesheetPayload payload, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("INSERT INTO timesheets (employee_id, date, actual_start_time, actual_end_time) VALUES (?, ?, ?, ?)")) {
                st.setInt(1, payload.employee_id());
                st.setString(2, payload.date());
                st.setString(3, payload.actual_start_time());
                st.setString(4, payload.actual_end_time());
                st.executeUpdate();
            }
            return ResponseEntity.status(201).body(Map.of("message", "Timesheet entry created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating timesheet entry."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            List<Map<String, Object>> timesheets = new ArrayList<>();
            try (PreparedStatement st = org.prepareStatement("SELECT * FROM timesheets"); ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("date", rs.getString("date"));
                    row.put("actual_start_time", rs.getString("actual_start_time"));
                    row.put("actual_end_time", rs.getString("actual_end_time"));
                    timesheets.add(row);
                }
            }
            return ResponseEntity.ok(timesheets);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching timesheet entries."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable int id, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("SELECT * FROM timesheets WHERE id = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("id", rs.getInt("id"));
                        row.put("employee_id", rs.getInt("employee_id"));
                        row.put("date", rs.getString("date"));
                        row.put("actual_start_time", rs.getString("actual_start_time"));
                        row.put("actual_end_time", rs.getString("actual_end_time"));
                        return ResponseEntity.ok(row);
                    }
                }
            }
            return ResponseEntity.status(404).body(Map.of("message", "Timesheet entry not found."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching timesheet entry."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody TimesheetPayload payload, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("UPDATE timesheets SET employee_id = ?, date = ?, actual_start_time = ?, actual_end_time = ? WHERE id = ?")) {
                st.setInt(1, payload.employee_id());
                st.setString(2, payload.date());
                st.setString(3, payload.actual_start_time());
                st.setString(4, payload.actual_end_time());
                st.setInt(5, id);
                st.executeUpdate();
            }
            return ResponseEntity.ok(Map.of("message", "Timesheet entry updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating timesheet entry."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id, HttpServletRequest request) {
        Integer orgId = HttpAttributes.getOrgId(request);
        String orgName = HttpAttributes.getOrgName(request);
        if (orgId == null || orgName == null) return ResponseEntity.status(440).body(Map.of("message", "Session Expired Logging Out"));
        try (Connection org = db.getOrgConnection(orgId, orgName)) {
            try (PreparedStatement st = org.prepareStatement("DELETE FROM timesheets WHERE id = ?")) {
                st.setInt(1, id);
                st.executeUpdate();
            }
            return ResponseEntity.ok(Map.of("message", "Timesheet entry deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting timesheet entry."));
        }
    }
}
