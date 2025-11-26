const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new overtime request
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { employee_id, date, hours } = req.body;

  try {
    const stmt = await org_db.prepare("INSERT INTO overtimerequests (employee_id, date, hours, status) VALUES (?, ?, ?, 'pending')");
    await stmt.run(employee_id, date, hours);
    await stmt.finalize();
    res.status(201).json({ message: "Overtime request created successfully." });
  } catch (err) {
    console.error("Error creating overtime request:", err);
    res.status(500).json({ message: "Error creating overtime request." });
  }
});

// Get all overtime requests
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const stmt = await org_db.prepare("SELECT * FROM overtimerequests");
    const overtimeRequests = await stmt.all();
    await stmt.finalize();
    res.json(overtimeRequests);
  } catch (err) {
    console.error("Error fetching overtime requests:", err);
    res.status(500).json({ message: "Error fetching overtime requests." });
  }
});

// Get overtime request by ID
router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM overtimerequests WHERE id = ?");
    const overtimeRequest = await stmt.get(id);
    await stmt.finalize();

    if (overtimeRequest) {
      res.json(overtimeRequest);
    } else {
      res.status(404).json({ message: "Overtime request not found." });
    }
  } catch (err) {
    console.error("Error fetching overtime request:", err);
    res.status(500).json({ message: "Error fetching overtime request." });
  }
});

// Update overtime request by ID
router.put("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;
  const { status } = req.body;

  try {
    const stmt = await org_db.prepare("UPDATE overtimerequests SET status = ? WHERE id = ?");
    await stmt.run(status, id);
    await stmt.finalize();
    res.json({ message: "Overtime request updated successfully." });
  } catch (err) {
    console.error("Error updating overtime request:", err);
    res.status(500).json({ message: "Error updating overtime request." });
  }
});

// Delete overtime request by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("DELETE FROM overtimerequests WHERE id = ?");
    await stmt.run(id);
    await stmt.finalize();
    res.json({ message: "Overtime request deleted successfully." });
  } catch (err) {
    console.error("Error deleting overtime request:", err);
    res.status(500).json({ message: "Error deleting overtime request." });
  }
});

module.exports = router;
