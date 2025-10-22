const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new schedule
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { employee_id, date, start_time, end_time } = req.body;

  try {
    const stmt = await org_db.prepare("INSERT INTO employeeschedules (employee_id, date, start_time, end_time) VALUES (?, ?, ?, ?)");
    await stmt.run(employee_id, date, start_time, end_time);
    await stmt.finalize();
    res.status(201).json({ message: "Schedule created successfully." });
  } catch (err) {
    console.error("Error creating schedule:", err);
    res.status(500).json({ message: "Error creating schedule." });
  }
});

// Get all schedules
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const stmt = await org_db.prepare("SELECT * FROM employeeschedules");
    const schedules = await stmt.all();
    await stmt.finalize();
    res.json(schedules);
  } catch (err) {
    console.error("Error fetching schedules:", err);
    res.status(500).json({ message: "Error fetching schedules." });
  }
});

// Get schedule by ID
router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM employeeschedules WHERE id = ?");
    const schedule = await stmt.get(id);
    await stmt.finalize();

    if (schedule) {
      res.json(schedule);
    } else {
      res.status(404).json({ message: "Schedule not found." });
    }
  } catch (err) {
    console.error("Error fetching schedule:", err);
    res.status(500).json({ message: "Error fetching schedule." });
  }
});

// Update schedule by ID
router.put("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;
  const { employee_id, date, start_time, end_time } = req.body;

  try {
    const stmt = await org_db.prepare("UPDATE employeeschedules SET employee_id = ?, date = ?, start_time = ?, end_time = ? WHERE id = ?");
    await stmt.run(employee_id, date, start_time, end_time, id);
    await stmt.finalize();
    res.json({ message: "Schedule updated successfully." });
  } catch (err) {
    console.error("Error updating schedule:", err);
    res.status(500).json({ message: "Error updating schedule." });
  }
});

// Delete schedule by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("DELETE FROM employeeschedules WHERE id = ?");
    await stmt.run(id);
    await stmt.finalize();
    res.json({ message: "Schedule deleted successfully." });
  } catch (err) {
    console.error("Error deleting schedule:", err);
    res.status(500).json({ message: "Error deleting schedule." });
  }
});

module.exports = router;
