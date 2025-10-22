const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new leave request
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { shiftid, reason } = req.body;

  try {
    const stmt = await org_db.prepare("INSERT INTO leaverequests (shiftid, reason, status) VALUES (?, ?, 'pending')");
    await stmt.run(shiftid, reason);
    await stmt.finalize();
    res.status(201).json({ message: "Leave request created successfully." });
  } catch (err) {
    console.error("Error creating leave request:", err);
    res.status(500).json({ message: "Error creating leave request." });
  }
});

// Get all leave requests
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const stmt = await org_db.prepare("SELECT * FROM leaverequests");
    const leaveRequests = await stmt.all();
    await stmt.finalize();
    res.json(leaveRequests);
  } catch (err) {
    console.error("Error fetching leave requests:", err);
    res.status(500).json({ message: "Error fetching leave requests." });
  }
});

// Get leave request by ID
router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM leaverequests WHERE id = ?");
    const leaveRequest = await stmt.get(id);
    await stmt.finalize();

    if (leaveRequest) {
      res.json(leaveRequest);
    } else {
      res.status(404).json({ message: "Leave request not found." });
    }
  } catch (err) {
    console.error("Error fetching leave request:", err);
    res.status(500).json({ message: "Error fetching leave request." });
  }
});

// Update leave request by ID
router.put("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;
  const { status } = req.body;

  try {
    const stmt = await org_db.prepare("UPDATE leaverequests SET status = ? WHERE id = ?");
    await stmt.run(status, id);
    await stmt.finalize();
    res.json({ message: "Leave request updated successfully." });
  } catch (err) {
    console.error("Error updating leave request:", err);
    res.status(500).json({ message: "Error updating leave request." });
  }
});

// Delete leave request by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("DELETE FROM leaverequests WHERE id = ?");
    await stmt.run(id);
    await stmt.finalize();
    res.json({ message: "Leave request deleted successfully." });
  } catch (err) {
    console.error("Error deleting leave request:", err);
    res.status(500).json({ message: "Error deleting leave request." });
  }
});

module.exports = router;
