package com.partpay.backend.controller;

import com.partpay.backend.db.DatabaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@RestController
@RequestMapping("/payslips")
public class PayslipsController {

    private final DatabaseService db;

    public PayslipsController(DatabaseService db) {
        this.db = db;
    }

    public record GenerateRequest(String start_date, String end_date, Integer tax_id) {}

    private double calculateTotalHoursWorked(int employeeId, String start, String end, Connection orgConn) throws Exception {
        // scheduled hours
        String scheduledSql = "SELECT SUM(strftime('%s', end_time) - strftime('%s', start_time)) / 3600 AS scheduled_hours FROM employeeschedules WHERE employee_id = ? AND date BETWEEN ? AND ?";
        double scheduled = 0;
        try (PreparedStatement st = orgConn.prepareStatement(scheduledSql)) {
            st.setInt(1, employeeId); st.setString(2, start); st.setString(3, end);
            try (ResultSet rs = st.executeQuery()) { if (rs.next()) scheduled = rs.getDouble("scheduled_hours"); }
        }
        // approved overtime
        String overtimeSql = "SELECT SUM(hours) AS overtime_hours FROM overtimerequests WHERE employee_id = ? AND date BETWEEN ? AND ? AND status = 'approved'";
        double overtime = 0;
        try (PreparedStatement st = orgConn.prepareStatement(overtimeSql)) {
            st.setInt(1, employeeId); st.setString(2, start); st.setString(3, end);
            try (ResultSet rs = st.executeQuery()) { if (rs.next()) overtime = rs.getDouble("overtime_hours"); }
        }
        // approved leave hours
        String leaveSql = "SELECT SUM(strftime('%s', end_time) - strftime('%s', start_time)) / 3600 AS leave_hours FROM leaverequests JOIN employeeschedules ON leaverequests.shiftid = employeeschedules.id WHERE employeeschedules.employee_id = ? AND leaverequests.status = 'approved' AND date BETWEEN ? AND ?";
        double leave = 0;
        try (PreparedStatement st = orgConn.prepareStatement(leaveSql)) {
            st.setInt(1, employeeId); st.setString(2, start); st.setString(3, end);
            try (ResultSet rs = st.executeQuery()) { if (rs.next()) leave = rs.getDouble("leave_hours"); }
        }
        // actual timesheet hours
        String actualSql = "SELECT SUM(strftime('%s', actual_end_time) - strftime('%s', actual_start_time)) / 3600 AS actual_hours FROM timesheets WHERE employee_id = ? AND date BETWEEN ? AND ?";
        double actual = 0;
        try (PreparedStatement st = orgConn.prepareStatement(actualSql)) {
            st.setInt(1, employeeId); st.setString(2, start); st.setString(3, end);
            try (ResultSet rs = st.executeQuery()) { if (rs.next()) actual = rs.getDouble("actual_hours"); }
        }
        return Math.min(actual, scheduled + overtime - leave);
    }

    private double calculateNet(double gross, int taxId, Connection orgConn) throws Exception {
        String taxTypesSql = "SELECT tax_type_id FROM taxtaxtype WHERE tax_information_id = ?";
        double deductionPct = 0;
        try (PreparedStatement st = orgConn.prepareStatement(taxTypesSql)) {
            st.setInt(1, taxId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    int typeId = rs.getInt("tax_type_id");
                    try (PreparedStatement tt = orgConn.prepareStatement("SELECT deduction_percentage FROM taxtypes WHERE id = ?")) {
                        tt.setInt(1, typeId);
                        try (ResultSet tr = tt.executeQuery()) { if (tr.next()) deductionPct += tr.getDouble("deduction_percentage"); }
                    }
                }
            }
        }
        return gross * (1 - deductionPct / 100.0);
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generate(@RequestAttribute("org_id") Integer orgId,
                                      @RequestAttribute("org_name") String orgName,
                                      @RequestBody GenerateRequest req) {
        try (Connection orgConn = db.getOrgConnection(orgId, orgName)) {
            List<Integer> employees = new ArrayList<>();
            try (PreparedStatement st = orgConn.prepareStatement("SELECT id FROM parttimeemployee")) {
                try (ResultSet rs = st.executeQuery()) { while (rs.next()) employees.add(rs.getInt("id")); }
            }
            for (int empId : employees) {
                double hours = calculateTotalHoursWorked(empId, req.start_date(), req.end_date(), orgConn);
                double payPerHour = 0;
                try (PreparedStatement st = orgConn.prepareStatement("SELECT pay_per_hour FROM parttimeemployee WHERE id = ?")) {
                    st.setInt(1, empId); try (ResultSet rs = st.executeQuery()) { if (rs.next()) payPerHour = rs.getDouble("pay_per_hour"); }
                }
                double gross = hours * payPerHour;
                double net = calculateNet(gross, req.tax_id(), orgConn);
                try (PreparedStatement ins = orgConn.prepareStatement("INSERT INTO payslips (employee_id, generated_date, from_date, to_date, hours_worked, pay_per_hour, gross_pay, net_pay, tax_id) VALUES (?, CURRENT_DATE, ?, ?, ?, ?, ?, ?, ?)")) {
                    ins.setInt(1, empId);
                    ins.setString(2, req.start_date());
                    ins.setString(3, req.end_date());
                    ins.setDouble(4, hours);
                    ins.setDouble(5, payPerHour);
                    ins.setDouble(6, gross);
                    ins.setDouble(7, net);
                    ins.setInt(8, req.tax_id());
                    ins.executeUpdate();
                }
            }
            return ResponseEntity.ok(Map.of("message", "Payslips generated successfully"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestAttribute("org_id") Integer orgId,
                                  @RequestAttribute("org_name") String orgName) {
        String sql = "SELECT * FROM payslips";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("generated_date", rs.getString("generated_date"));
                    row.put("from_date", rs.getString("from_date"));
                    row.put("to_date", rs.getString("to_date"));
                    row.put("hours_worked", rs.getDouble("hours_worked"));
                    row.put("pay_per_hour", rs.getDouble("pay_per_hour"));
                    row.put("gross_pay", rs.getDouble("gross_pay"));
                    row.put("net_pay", rs.getDouble("net_pay"));
                    row.put("tax_id", rs.getInt("tax_id"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("/{employee_id}")
    public ResponseEntity<?> listByEmployee(@RequestAttribute("org_id") Integer orgId,
                                            @RequestAttribute("org_name") String orgName,
                                            @PathVariable("employee_id") int employeeId) {
        String sql = "SELECT * FROM payslips WHERE employee_id = ?";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection orgConn = db.getOrgConnection(orgId, orgName); PreparedStatement st = orgConn.prepareStatement(sql)) {
            st.setInt(1, employeeId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("employee_id", rs.getInt("employee_id"));
                    row.put("generated_date", rs.getString("generated_date"));
                    row.put("from_date", rs.getString("from_date"));
                    row.put("to_date", rs.getString("to_date"));
                    row.put("hours_worked", rs.getDouble("hours_worked"));
                    row.put("pay_per_hour", rs.getDouble("pay_per_hour"));
                    row.put("gross_pay", rs.getDouble("gross_pay"));
                    row.put("net_pay", rs.getDouble("net_pay"));
                    row.put("tax_id", rs.getInt("tax_id"));
                    list.add(row);
                }
            }
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }
}
