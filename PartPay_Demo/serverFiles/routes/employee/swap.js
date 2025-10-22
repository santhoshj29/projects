const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new swap request
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { shiftid, targetshiftid, reason } = req.body;

  try {
    const stmt = await org_db.prepare("INSERT INTO swaprequests (shiftid, targetshiftid, reason, status) VALUES (?, ?, ?, 'pending')");
    await stmt.run(shiftid, targetshiftid, reason);
    await stmt.finalize();
    res.status(201).json({ message: "Swap request created successfully." });
  } catch (err) {
    console.error("Error creating swap request:", err);
    res.status(500).json({ message: "Error creating swap request." });
  }
});

// Get all swap requests
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const stmt = await org_db.prepare("SELECT * FROM swaprequests");
    const swapRequests = await stmt.all();
    await stmt.finalize();
    res.json(swapRequests);
  } catch (err) {
    console.error("Error fetching swap requests:", err);
    res.status(500).json({ message: "Error fetching swap requests." });
  }
});

// Get swap request by ID
router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM swaprequests WHERE id = ?");
    const swapRequest = await stmt.get(id);
    await stmt.finalize();

    if (swapRequest) {
      res.json(swapRequest);
    } else {
      res.status(404).json({ message: "Swap request not found." });
    }
  } catch (err) {
    console.error("Error fetching swap request:", err);
    res.status(500).json({ message: "Error fetching swap request." });
  }
});

// Update swap request status by ID
router.put("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;
  const { status } = req.body;

  try {
    const checkApprovalStmt = await org_db.prepare("SELECT * FROM swaprequests WHERE id = ?");
    const { status: currentStatus } = await checkApprovalStmt.get(id);
    await checkApprovalStmt.finalize();

    if (currentStatus === "approved") {
      res.status(400).json({ message: "Cannot modify status of an already approved swap request." });
      return;
    }

    const stmt = await org_db.prepare("UPDATE swaprequests SET status = ? WHERE id = ?");
    await stmt.run(status, id);
    await stmt.finalize();

    if (status === "approved") {
      const swapRequestStmt = await org_db.prepare("SELECT shiftid, targetshiftid FROM swaprequests WHERE id = ?");
      const { shiftid, targetshiftid } = await swapRequestStmt.get(id);
      await swapRequestStmt.finalize();

      const rowStmnt = await org_db.prepare("SELECT employee_id FROM employeeschedules WHERE id = ?");

      const { employee_id } = await rowStmnt.get(shiftid);
      const { employee_id: targetEmployeeId } = await rowStmnt.get(targetshiftid);

      const updateTargetShiftStmt = await org_db.prepare("UPDATE employeeschedules SET employee_id = ? WHERE id = ?");
      await updateTargetShiftStmt.run(targetEmployeeId, shiftid);
      await updateTargetShiftStmt.finalize();

      const updatePresentShiftStmt = await org_db.prepare("UPDATE employeeschedules SET employee_id = ? WHERE id = ?");
      await updatePresentShiftStmt.run(employee_id, targetshiftid);
      await updatePresentShiftStmt.finalize();
    }

    res.json({ message: "Swap request status updated successfully." });
  } catch (err) {
    console.error("Error updating swap request status:", err);
    res.status(500).json({ message: "Error updating swap request status." });
  }
});

// Delete swap request by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("DELETE FROM swaprequests WHERE id = ?");
    await stmt.run(id);
    await stmt.finalize();
    res.json({ message: "Swap request deleted successfully." });
  } catch (err) {
    console.error("Error deleting swap request:", err);
    res.status(500).json({ message: "Error deleting swap request." });
  }
});

module.exports = router;
