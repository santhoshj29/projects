package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
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

    public record ScheduleRequest(Integer employee_id, String date, String start_time, String end_time) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @RequestBody ScheduleRequest req) {
        String sql = "INSERT INTO employeeschedules (employee_id, date, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, req.employee_id());
            st.setString(2, req.date());
            st.setString(3, req.start_time());
            st.setString(4, req.end_time());
            st.executeUpdate();
            return ResponseEntity.status(201).body(Map.of("message", "Schedule created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating schedule."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        String sql = "SELECT * FROM employeeschedules";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("date", rs.getString("date"));
                    row.put("start_time", rs.getString("start_time"));
                    row.put("end_time", rs.getString("end_time"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching schedules."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestAttribute("org_id") Integer orgId,
                                 @RequestAttribute("org_name") String orgName,
                                 @PathVariable int id) {
        String sql = "SELECT * FROM employeeschedules WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return ResponseEntity.status(404).body(Map.of("message", "Schedule not found."));
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("employee_id", rs.getInt("employee_id"));
                row.put("date", rs.getString("date"));
                row.put("start_time", rs.getString("start_time"));
                row.put("end_time", rs.getString("end_time"));
                return ResponseEntity.ok(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching schedule."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id,
                                    @RequestBody ScheduleRequest req) {
        String sql = "UPDATE employeeschedules SET employee_id = ?, date = ?, start_time = ?, end_time = ? WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, req.employee_id());
            st.setString(2, req.date());
            st.setString(3, req.start_time());
            st.setString(4, req.end_time());
            st.setInt(5, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Schedule updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating schedule."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id) {
        String sql = "DELETE FROM employeeschedules WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Schedule deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting schedule."));
        }
    }
}
