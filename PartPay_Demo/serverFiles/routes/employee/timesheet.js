const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new timesheet entry
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { employee_id, date, actual_start_time, actual_end_time } = req.body;

  try {
    const stmt = await org_db.prepare("INSERT INTO timesheets (employee_id, date, actual_start_time, actual_end_time) VALUES (?, ?, ?, ?)");
    await stmt.run(employee_id, date, actual_start_time, actual_end_time);
    await stmt.finalize();
    res.status(201).json({ message: "Timesheet entry created successfully." });
  } catch (err) {
    console.error("Error creating timesheet entry:", err);
    res.status(500).json({ message: "Error creating timesheet entry." });
  }
});

// Get all timesheet entries
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const stmt = await org_db.prepare("SELECT * FROM timesheets");
    const timesheets = await stmt.all();
    await stmt.finalize();
    res.json(timesheets);
  } catch (err) {
    console.error("Error fetching timesheet entries:", err);
    res.status(500).json({ message: "Error fetching timesheet entries." });
  }
});

// Get timesheet entry by ID
router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM timesheets WHERE id = ?");
    const timesheet = await stmt.get(id);
    await stmt.finalize();

    if (timesheet) {
      res.json(timesheet);
    } else {
      res.status(404).json({ message: "Timesheet entry not found." });
    }
  } catch (err) {
    console.error("Error fetching timesheet entry:", err);
    res.status(500).json({ message: "Error fetching timesheet entry." });
  }
});

// Update timesheet entry by ID
router.put("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;
  const { employee_id, date, actual_start_time, actual_end_time } = req.body;

  try {
    const stmt = await org_db.prepare(
      "UPDATE timesheets SET employee_id = ?, date = ?, actual_start_time = ?, actual_end_time = ? WHERE id = ?"
    );
    await stmt.run(employee_id, date, actual_start_time, actual_end_time, id);
    await stmt.finalize();
    res.json({ message: "Timesheet entry updated successfully." });
  } catch (err) {
    console.error("Error updating timesheet entry:", err);
    res.status(500).json({ message: "Error updating timesheet entry." });
  }
});

// Delete timesheet entry by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("DELETE FROM timesheets WHERE id = ?");
    await stmt.run(id);
    await stmt.finalize();
    res.json({ message: "Timesheet entry deleted successfully." });
  } catch (err) {
    console.error("Error deleting timesheet entry:", err);
    res.status(500).json({ message: "Error deleting timesheet entry." });
  }
});

module.exports = router;
