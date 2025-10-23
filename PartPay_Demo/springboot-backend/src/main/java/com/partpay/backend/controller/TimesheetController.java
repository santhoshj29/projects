package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
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

    public record NewTimesheetRequest(Integer employee_id, String date, String actual_start_time, String actual_end_time) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @RequestBody NewTimesheetRequest req) {
        String sql = "INSERT INTO timesheets (employee_id, date, actual_start_time, actual_end_time) VALUES (?, ?, ?, ?)";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, req.employee_id());
            st.setString(2, req.date());
            st.setString(3, req.actual_start_time());
            st.setString(4, req.actual_end_time());
            st.executeUpdate();
            return ResponseEntity.status(201).body(Map.of("message", "Timesheet entry created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating timesheet entry."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        String sql = "SELECT * FROM timesheets";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("date", rs.getString("date"));
                    row.put("actual_start_time", rs.getString("actual_start_time"));
                    row.put("actual_end_time", rs.getString("actual_end_time"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching timesheet entries."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestAttribute("org_id") Integer orgId,
                                 @RequestAttribute("org_name") String orgName,
                                 @PathVariable int id) {
        String sql = "SELECT * FROM timesheets WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return ResponseEntity.status(404).body(Map.of("message", "Timesheet entry not found."));
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("employee_id", rs.getInt("employee_id"));
                row.put("date", rs.getString("date"));
                row.put("actual_start_time", rs.getString("actual_start_time"));
                row.put("actual_end_time", rs.getString("actual_end_time"));
                return ResponseEntity.ok(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching timesheet entry."));
        }
    }

    public record UpdateTimesheetRequest(Integer employee_id, String date, String actual_start_time, String actual_end_time) {}

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id,
                                    @RequestBody UpdateTimesheetRequest req) {
        String sql = "UPDATE timesheets SET employee_id = ?, date = ?, actual_start_time = ?, actual_end_time = ? WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, req.employee_id());
            st.setString(2, req.date());
            st.setString(3, req.actual_start_time());
            st.setString(4, req.actual_end_time());
            st.setInt(5, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Timesheet entry updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating timesheet entry."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id) {
        String sql = "DELETE FROM timesheets WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Timesheet entry deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting timesheet entry."));
        }
    }
}
