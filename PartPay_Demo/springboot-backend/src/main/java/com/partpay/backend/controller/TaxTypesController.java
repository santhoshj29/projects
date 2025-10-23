package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@RestController
@RequestMapping("/taxtypes")
public class TaxTypesController {

    private final DatabaseService db;

    public TaxTypesController(DatabaseService db) {
        this.db = db;
    }

    public record TaxTypeRequest(String name, Integer deduction_percentage) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @RequestBody TaxTypeRequest req) {
        String sql = "INSERT INTO taxtypes (name, deduction_percentage) VALUES (?, ?)";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName);
             PreparedStatement st = orgConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, req.name());
            st.setInt(2, req.deduction_percentage());
            st.executeUpdate();
            int id = 0;
            try (ResultSet keys = st.getGeneratedKeys()) { if (keys.next()) id = keys.getInt(1); }
            return ResponseEntity.status(201).body(Map.of("id", id, "name", req.name(), "deduction_percentage", req.deduction_percentage(), "message", "Tax type created successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating tax type."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        String sql = "SELECT * FROM taxtypes";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("name", rs.getString("name"));
                    row.put("deduction_percentage", rs.getInt("deduction_percentage"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching tax types."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestAttribute("org_id") Integer orgId,
                                 @RequestAttribute("org_name") String orgName,
                                 @PathVariable int id) {
        String sql = "SELECT * FROM taxtypes WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (!rs.next()) return ResponseEntity.status(404).body(Map.of("message", "Tax type not found."));
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("name", rs.getString("name"));
                row.put("deduction_percentage", rs.getInt("deduction_percentage"));
                return ResponseEntity.ok(row);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching tax type."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id,
                                    @RequestBody TaxTypeRequest req) {
        String sql = "UPDATE taxtypes SET name = ?, deduction_percentage = ? WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setString(1, req.name());
            st.setInt(2, req.deduction_percentage());
            st.setInt(3, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("id", id, "name", req.name(), "deduction_percentage", req.deduction_percentage(), "message", "Tax type updated successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating tax type."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id) {
        String sql = "DELETE FROM taxtypes WHERE id = ?";
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, id);
            st.executeUpdate();
            return ResponseEntity.ok(Map.of("message", "Tax type deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting tax type."));
        }
    }
}
