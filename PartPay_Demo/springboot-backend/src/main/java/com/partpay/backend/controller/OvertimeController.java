package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/overtime")
public class OvertimeController {

    private final DatabaseService db;

    public OvertimeController(DatabaseService db) {
        this.db = db;
    }

    public record NewOvertimeRequest(Integer employee_id, String date, Integer hours) {}
    public record UpdateOvertimeRequest(String status) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @RequestBody NewOvertimeRequest req) {
        String sql = "INSERT INTO overtimerequests (employee_id, date, hours, status) VALUES (?, ?, ?, 'pending')";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, req.employee_id());
            st.setString(2, req.date());
            st.setInt(3, req.hours());
            st.executeUpdate();
            return ResponseEntity.status(201).body(Map.of("message", "Overtime request created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating overtime request."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        String sql = "SELECT * FROM overtimerequests";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("date", rs.getString("date"));
                    row.put("hours", rs.getInt("hours"));
                    row.put("status", rs.getString("status"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching overtime requests."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestAttribute("org_id") Integer orgId,
                                 @RequestAttribute("org_name") String orgName,
                                 @PathVariable int id) {
        String sql = "SELECT * FROM overtimerequests WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return ResponseEntity.status(404).body(Map.of("message", "Overtime request not found."));
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("employee_id", rs.getInt("employee_id"));
                row.put("date", rs.getString("date"));
                row.put("hours", rs.getInt("hours"));
                row.put("status", rs.getString("status"));
                return ResponseEntity.ok(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching overtime request."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id,
                                    @RequestBody UpdateOvertimeRequest req) {
        String sql = "UPDATE overtimerequests SET status = ? WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setString(1, req.status());
            st.setInt(2, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Overtime request updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating overtime request."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id) {
        String sql = "DELETE FROM overtimerequests WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Overtime request deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting overtime request."));
        }
    }
}
