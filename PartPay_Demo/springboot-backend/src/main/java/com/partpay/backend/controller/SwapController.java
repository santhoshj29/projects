package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/swap")
public class SwapController {

    private final DatabaseService db;

    public SwapController(DatabaseService db) {
        this.db = db;
    }

    public record NewSwapRequest(Integer shiftid, Integer targetshiftid, String reason) {}
    public record UpdateSwapRequest(String status) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @RequestBody NewSwapRequest req) {
        String sql = "INSERT INTO swaprequests (shiftid, targetshiftid, reason, status) VALUES (?, ?, ?, 'pending')";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, req.shiftid());
            st.setInt(2, req.targetshiftid());
            st.setString(3, req.reason());
            st.executeUpdate();
            return ResponseEntity.status(201).body(Map.of("message", "Swap request created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating swap request."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        String sql = "SELECT * FROM swaprequests";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("shiftid", rs.getInt("shiftid"));
                    row.put("targetshiftid", rs.getInt("targetshiftid"));
                    row.put("reason", rs.getString("reason"));
                    row.put("status", rs.getString("status"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching swap requests."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestAttribute("org_id") Integer orgId,
                                 @RequestAttribute("org_name") String orgName,
                                 @PathVariable int id) {
        String sql = "SELECT * FROM swaprequests WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return ResponseEntity.status(404).body(Map.of("message", "Swap request not found."));
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("shiftid", rs.getInt("shiftid"));
                row.put("targetshiftid", rs.getInt("targetshiftid"));
                row.put("reason", rs.getString("reason"));
                row.put("status", rs.getString("status"));
                return ResponseEntity.ok(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching swap request."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id,
                                    @RequestBody UpdateSwapRequest req) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            String currentSql = "SELECT status FROM swaprequests WHERE id = ?";
            try (PreparedStatement chk = orgConn.prepareStatement(currentSql)) {
                chk.setInt(1, id);
                try (ResultSet crs = chk.executeQuery()) {
                    if (!crs.next()) return ResponseEntity.status(404).body(Map.of("message", "Swap request not found."));
                    if ("approved".equals(crs.getString("status"))) {
                        return ResponseEntity.status(400).body(Map.of("message", "Cannot modify status of an already approved swap request."));
                    }
                }
            }

            String upd = "UPDATE swaprequests SET status = ? WHERE id = ?";
            try (PreparedStatement ust = orgConn.prepareStatement(upd)) {
                ust.setString(1, req.status());
                ust.setInt(2, id);
                ust.executeUpdate();
            }

            if ("approved".equals(req.status())) {
                try (PreparedStatement get = orgConn.prepareStatement("SELECT shiftid, targetshiftid FROM swaprequests WHERE id = ?")) {
                    get.setInt(1, id);
                    try (ResultSet rs = get.executeQuery()) {
                        if (rs.next()) {
                            int shiftId = rs.getInt("shiftid");
                            int targetShiftId = rs.getInt("targetshiftid");
                            try (PreparedStatement rowSt = orgConn.prepareStatement("SELECT employee_id FROM employeeschedules WHERE id = ?");
                                 PreparedStatement updTarget = orgConn.prepareStatement("UPDATE employeeschedules SET employee_id = ? WHERE id = ?");
                                 PreparedStatement updPresent = orgConn.prepareStatement("UPDATE employeeschedules SET employee_id = ? WHERE id = ?")) {
                                int emp = 0; int targEmp = 0;
                                rowSt.setInt(1, shiftId);
                                try (ResultSet r1 = rowSt.executeQuery()) { if (r1.next()) emp = r1.getInt("employee_id"); }
                                rowSt.setInt(1, targetShiftId);
                                try (ResultSet r2 = rowSt.executeQuery()) { if (r2.next()) targEmp = r2.getInt("employee_id"); }
                                updTarget.setInt(1, targEmp); updTarget.setInt(2, shiftId); updTarget.executeUpdate();
                                updPresent.setInt(1, emp); updPresent.setInt(2, targetShiftId); updPresent.executeUpdate();
                            }
                        }
                    }
                }
            }
            return ResponseEntity.ok(Map.of("message", "Swap request status updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating swap request status."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id) {
        String sql = "DELETE FROM swaprequests WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Swap request deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting swap request."));
        }
    }
}
