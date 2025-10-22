const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

const calculateTotalHoursWorked = async (employee_id, start_date, end_date, org_db) => {
  // Calculate total scheduled hours
  const scheduledHoursQuery = `
    SELECT SUM(strftime('%s', end_time) - strftime('%s', start_time)) / 3600 AS scheduled_hours
    FROM employeeschedules
    WHERE employee_id = ? AND date BETWEEN ? AND ?;
  `;
  const scheduledHoursResult = await org_db.get(scheduledHoursQuery, [employee_id, start_date, end_date]);
  let totalScheduledHours = scheduledHoursResult.scheduled_hours || 0;

  // Calculate total approved overtime hours
  const overtimeHoursQuery = `
    SELECT SUM(hours) AS overtime_hours
    FROM overtimerequests
    WHERE employee_id = ? AND date BETWEEN ? AND ? AND status = 'approved';
  `;
  const overtimeHoursResult = await org_db.get(overtimeHoursQuery, [employee_id, start_date, end_date]);
  let totalOvertimeHours = overtimeHoursResult.overtime_hours || 0;

  // Calculate total approved leave hours
  const leaveHoursQuery = `
    SELECT SUM(strftime('%s', end_time) - strftime('%s', start_time)) / 3600 AS leave_hours
    FROM leaverequests
    JOIN employeeschedules ON leaverequests.shiftid = employeeschedules.id
    WHERE employeeschedules.employee_id = ? AND leaverequests.status = 'approved' AND date BETWEEN ? AND ?;
  `;
  const leaveHoursResult = await org_db.get(leaveHoursQuery, [employee_id, start_date, end_date]);
  let totalLeaveHours = leaveHoursResult.leave_hours || 0;

  // Calculate total actual hours worked from timesheets
  const actualHoursQuery = `
    SELECT SUM(strftime('%s', actual_end_time) - strftime('%s', actual_start_time)) / 3600 AS actual_hours
    FROM timesheets
    WHERE employee_id = ? AND date BETWEEN ? AND ?;
  `;
  const actualHoursResult = await org_db.get(actualHoursQuery, [employee_id, start_date, end_date]);
  let totalActualHours = actualHoursResult.actual_hours || 0;

  totalActualHours = Math.min(totalActualHours, totalScheduledHours + totalOvertimeHours - totalLeaveHours);

  return totalActualHours;
};

const calculateGrossPay = (totalHours, payPerHour) => {
  return totalHours * payPerHour;
};

const calculateNetPay = async (grossPay, taxId, org_db) => {
  const taxTypesQuery = `
    SELECT tax_type_id
    FROM taxtaxtype
    WHERE tax_information_id = ?;
  `;
  const taxTypesResult = await org_db.all(taxTypesQuery, [taxId]);

  let totalDeductionPercentage = 0;
  for (const row of taxTypesResult) {
    const taxTypeId = row.tax_type_id;
    const deductionPercentageQuery = "SELECT deduction_percentage FROM taxtypes WHERE id = ?;";
    const deductionPercentageResult = await org_db.get(deductionPercentageQuery, [taxTypeId]);
    totalDeductionPercentage += deductionPercentageResult.deduction_percentage;
  }

  const netPay = grossPay * (1 - totalDeductionPercentage / 100);
  return netPay;
};

router.post("/generate", async (req, res) => {
  const { start_date, end_date, tax_id } = req.body;
  const org_db = await getCurrentDb(req);

  try {
    const employeesQuery = "SELECT id FROM parttimeemployee;";
    const employeesResult = await org_db.all(employeesQuery);

    for (const employee of employeesResult) {
      const employee_id = employee.id;

      const totalActualHours = await calculateTotalHoursWorked(employee_id, start_date, end_date, org_db);

      const payPerHourQuery = "SELECT pay_per_hour FROM parttimeemployee WHERE id = ?;";
      const payPerHourResult = await org_db.get(payPerHourQuery, [employee_id]);
      const payPerHour = payPerHourResult.pay_per_hour;

      const grossPay = calculateGrossPay(totalActualHours, payPerHour);

      const netPay = await calculateNetPay(grossPay, tax_id, org_db);

      const insertPayslipQuery = `
        INSERT INTO payslips (employee_id, generated_date, from_date, to_date, hours_worked, pay_per_hour, gross_pay, net_pay, tax_id)
        VALUES (?, CURRENT_DATE, ?, ?, ?, ?, ?, ?, ?);
      `;
      await org_db.run(insertPayslipQuery, [employee_id, start_date, end_date, totalActualHours, payPerHour, grossPay, netPay, tax_id]);
    }

    res.status(200).json({ message: "Payslips generated successfully" });
  } catch (error) {
    console.error("Error generating payslips:", error);
    res.status(500).json({ message: "Internal server error" });
  }
});

router.get("/:employee_id", async (req, res) => {
  const { employee_id } = req.params;
  const org_db = await getCurrentDb(req);

  try {
    const payslipsQuery = "SELECT * FROM payslips WHERE employee_id = ?;";
    const payslips = await org_db.all(payslipsQuery, [employee_id]);

    res.status(200).json(Object.values(payslips));
  } catch (error) {
    console.error("Error fetching payslips:", error);
    res.status(500).json({ message: "Internal server error" });
  }
});

router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const payslipsQuery = "SELECT * FROM payslips;";
    const payslips = await org_db.all(payslipsQuery);

    res.status(200).json(Object.values(payslips));
  } catch (error) {
    console.error("Error fetching payslips:", error);
    res.status(500).json({ message: "Internal server error" });
  }
});

module.exports = router;
