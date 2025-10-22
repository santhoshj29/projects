const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new tax type
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { name, deduction_percentage } = req.body;

  try {
    const stmt = await org_db.prepare("INSERT INTO taxtypes (name, deduction_percentage) VALUES (?, ?)");
    const { lastID } = await stmt.run(name, deduction_percentage);
    await stmt.finalize();
    res.status(201).json({ id: lastID, name, deduction_percentage, message: "Tax type created successfully." });
  } catch (err) {
    console.error("Error creating tax type:", err);
    res.status(500).json({ message: "Error creating tax type." });
  }
});

// Get all tax types
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);

  try {
    const stmt = await org_db.prepare("SELECT * FROM taxtypes");
    const taxTypes = await stmt.all();
    await stmt.finalize();
    res.json(taxTypes);
  } catch (err) {
    console.error("Error fetching tax types:", err);
    res.status(500).json({ message: "Error fetching tax types." });
  }
});

// Get tax type by ID
router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM taxtypes WHERE id = ?");
    const taxType = await stmt.get(id);
    await stmt.finalize();

    if (taxType) {
      res.json(taxType);
    } else {
      res.status(404).json({ message: "Tax type not found." });
    }
  } catch (err) {
    console.error("Error fetching tax type:", err);
    res.status(500).json({ message: "Error fetching tax type." });
  }
});

// Update tax type by ID
router.put("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;
  const { name, deduction_percentage } = req.body;

  try {
    const stmt = await org_db.prepare("UPDATE taxtypes SET name = ?, deduction_percentage = ? WHERE id = ?");
    await stmt.run(name, deduction_percentage, id);
    await stmt.finalize();
    res.json({ id, name, deduction_percentage, message: "Tax type updated successfully." });
  } catch (err) {
    console.error("Error updating tax type:", err);
    res.status(500).json({ message: "Error updating tax type." });
  }
});

// Delete tax type by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("DELETE FROM taxtypes WHERE id = ?");
    await stmt.run(id);
    await stmt.finalize();
    res.json({ message: "Tax type deleted successfully." });
  } catch (err) {
    console.error("Error deleting tax type:", err);
    res.status(500).json({ message: "Error deleting tax type." });
  }
});

module.exports = router;
