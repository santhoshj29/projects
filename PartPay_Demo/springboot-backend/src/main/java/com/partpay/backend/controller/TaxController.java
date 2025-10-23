package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.*;

@RestController
@RequestMapping("/tax")
public class TaxController {

    private final DatabaseService db;

    public TaxController(DatabaseService db) {
        this.db = db;
    }

    public record TaxInfoRequest(String name, List<Integer> tax_types) {}

    @PostMapping("/new")
    public ResponseEntity<?> create(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @RequestBody TaxInfoRequest req) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            orgConn.setAutoCommit(false);
            int id;
            try (PreparedStatement insert = orgConn.prepareStatement("INSERT INTO taxinformation (name) VALUES (?)", Statement.RETURN_GENERATED_KEYS)) {
                insert.setString(1, req.name());
                insert.executeUpdate();
                try (ResultSet keys = insert.getGeneratedKeys()) { if (!keys.next()) throw new RuntimeException("No key"); id = keys.getInt(1);}            }
            try (PreparedStatement insTT = orgConn.prepareStatement("INSERT INTO taxtaxtype (tax_information_id, tax_type_id) VALUES (?, ?)")) {
                for (Integer t : req.tax_types()) { insTT.setInt(1, id); insTT.setInt(2, t); insTT.addBatch(); }
                insTT.executeBatch();
            }
            orgConn.commit();
            Map<String, Object> body = new HashMap<>();
            body.put("id", id); body.put("name", req.name());
            List<Map<String, Object>> types = new ArrayList<>();
            for (Integer t : req.tax_types()) types.add(Map.of("tax_type_id", t));
            body.put("tax_types", types);
            body.put("message", "Tax information created successfully.");
            return ResponseEntity.status(201).body(body);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error creating tax information please check id's of tax types."));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            List<Map<String, Object>> result = new ArrayList<>();
            try (PreparedStatement taxInfoStmt = orgConn.prepareStatement("SELECT * FROM taxinformation")) {
                try (ResultSet rs = taxInfoStmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        List<Map<String, Object>> taxTypes = new ArrayList<>();
                        try (PreparedStatement tt = orgConn.prepareStatement("SELECT * FROM taxtaxtype WHERE tax_information_id = ?")) {
                            tt.setInt(1, id);
                            try (ResultSet trs = tt.executeQuery()) {
                                while (trs.next()) taxTypes.add(Map.of("tax_information_id", trs.getInt("tax_information_id"), "tax_type_id", trs.getInt("tax_type_id")));
                            }
                        }
                        result.add(Map.of("id", id, "name", name, "tax_types", taxTypes));
                    }
                }
            }
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching tax information."));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@RequestAttribute("org_id") Integer orgId,
                                 @RequestAttribute("org_name") String orgName,
                                 @PathVariable int id) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            Map<String, Object> tax;
            try (PreparedStatement st = orgConn.prepareStatement("SELECT * FROM taxinformation WHERE id = ?")) {
                st.setInt(1, id);
                try (ResultSet rs = st.executeQuery()) {
                    if (!rs.next()) return ResponseEntity.status(404).body(Map.of("message", "Tax information not found."));
                    tax = new HashMap<>();
                    tax.put("id", rs.getInt("id"));
                    tax.put("name", rs.getString("name"));
                }
            }
            List<Map<String, Object>> taxTypes = new ArrayList<>();
            try (PreparedStatement tt = orgConn.prepareStatement("SELECT * FROM taxtaxtype WHERE tax_information_id = ?")) {
                tt.setInt(1, id);
                try (ResultSet rs = tt.executeQuery()) { while (rs.next()) taxTypes.add(Map.of("tax_information_id", rs.getInt("tax_information_id"), "tax_type_id", rs.getInt("tax_type_id"))); }
            }
            tax.put("tax_types", taxTypes);
            return ResponseEntity.ok(tax);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error fetching tax type."));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id,
                                    @RequestBody TaxInfoRequest req) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            orgConn.setAutoCommit(false);
            try (PreparedStatement del = orgConn.prepareStatement("DELETE FROM taxtaxtype WHERE tax_information_id = ?")) {
                del.setInt(1, id); del.executeUpdate();
            }
            try (PreparedStatement upd = orgConn.prepareStatement("UPDATE taxinformation SET name = ? WHERE id = ?")) {
                upd.setString(1, req.name());
                upd.setInt(2, id);
                upd.executeUpdate();
            }
            try (PreparedStatement insTT = orgConn.prepareStatement("INSERT INTO taxtaxtype (tax_information_id, tax_type_id) VALUES (?, ?)")) {
                for (Integer t : req.tax_types()) { insTT.setInt(1, id); insTT.setInt(2, t); insTT.addBatch(); }
                insTT.executeBatch();
            }
            orgConn.commit();
            Map<String, Object> body = new HashMap<>();
            body.put("id", id); body.put("name", req.name());
            List<Map<String, Object>> types = new ArrayList<>();
            for (Integer t : req.tax_types()) types.add(Map.of("tax_type_id", t));
            body.put("tax_types", types);
            body.put("message", "Tax information updated successfully.");
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error updating tax information please check id's of tax types."));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestAttribute("org_id") Integer orgId,
                                    @RequestAttribute("org_name") String orgName,
                                    @PathVariable int id) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            orgConn.setAutoCommit(false);
            try (PreparedStatement delInfo = orgConn.prepareStatement("DELETE FROM taxinformation WHERE id = ?");
                 PreparedStatement delTypes = orgConn.prepareStatement("DELETE FROM taxtaxtype WHERE tax_information_id = ?")) {
                delInfo.setInt(1, id); delInfo.executeUpdate();
                delTypes.setInt(1, id); delTypes.executeUpdate();
            }
            orgConn.commit();
            return ResponseEntity.ok(Map.of("message", "Tax information deleted successfully."));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Error deleting tax information."));
        }
    }
}
