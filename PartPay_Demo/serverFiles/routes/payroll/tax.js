const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

// Create a new tax information entry with associated tax types
router.post("/new", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { name, tax_types: taxTypes } = req.body;

  try {
    await org_db.exec("BEGIN");
    const insertTaxInfoStmt = await org_db.prepare("INSERT INTO taxinformation (name) VALUES (?)");
    const { lastID } = await insertTaxInfoStmt.run(name);
    await insertTaxInfoStmt.finalize();

    const insertTaxTypeStmt = await org_db.prepare("INSERT INTO taxtaxtype (tax_information_id, tax_type_id) VALUES (?, ?)");
    for (const taxTypeId of taxTypes) {
      await insertTaxTypeStmt.run(lastID, taxTypeId);
    }
    await insertTaxTypeStmt.finalize();
    await org_db.exec("COMMIT");
    res.status(201).json({
      id: lastID,
      name,
      tax_types: taxTypes.map((t) => {
        return { tax_type_id: t };
      }),
      message: "Tax information created successfully.",
    });
  } catch (err) {
    await org_db.exec("ROLLBACK");
    console.error("Error creating tax information:", err);
    res.status(500).json({ message: "Error creating tax information please check id's of tax types." });
  }
});

// Get all tax information entries with associated tax types
router.get("/", async (req, res) => {
  const org_db = await getCurrentDb(req);
  let result = {};
  try {
    const taxInfoStmt = await org_db.prepare("SELECT * FROM taxinformation");
    const taxInfoRows = await taxInfoStmt.all();
    await taxInfoStmt.finalize();

    for (let taxInfoRow of taxInfoRows) {
      const taxTypeStmt = await org_db.prepare("SELECT * FROM taxtaxtype WHERE tax_information_id = ?");
      const taxTypes = await taxTypeStmt.all(taxInfoRow.id);
      await taxTypeStmt.finalize();
      taxInfoRow = { ...taxInfoRow, tax_types: taxTypes };
      result = { ...result, [taxInfoRow.id]: taxInfoRow };
    }

    res.json(Object.values(result));
  } catch (err) {
    console.error("Error fetching tax information:", err);
    res.status(500).json({ message: "Error fetching tax information." });
  }
});

router.get("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    const stmt = await org_db.prepare("SELECT * FROM taxinformation WHERE id = ?");
    const tax = await stmt.get(id);
    await stmt.finalize();

    const taxTypeStmt = await org_db.prepare("SELECT * FROM taxtaxtype WHERE tax_information_id = ?");
    const taxTypes = await taxTypeStmt.all(id);
    await taxTypeStmt.finalize();

    if (tax) {
      res.json({ ...tax, tax_types: taxTypes });
    } else {
      res.status(404).json({ message: "Tax information not found." });
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
  const { name, tax_types: taxTypes } = req.body;

  try {
    await org_db.exec("BEGIN");
    // Delete existing tax types associated with the tax information entry
    const deleteTaxTypeStmt = await org_db.prepare("DELETE FROM taxtaxtype WHERE tax_information_id = ?");
    await deleteTaxTypeStmt.run(id);
    await deleteTaxTypeStmt.finalize();

    // Update tax information entry
    const updateTaxInfoStmt = await org_db.prepare("UPDATE taxinformation SET name = ? WHERE id = ?");
    await updateTaxInfoStmt.run(name, id);
    await updateTaxInfoStmt.finalize();

    // Insert new tax types associated with the tax information entry
    const insertTaxTypeStmt = await org_db.prepare("INSERT INTO taxtaxtype (tax_information_id, tax_type_id) VALUES (?, ?)");
    for (const taxTypeId of taxTypes) {
      await insertTaxTypeStmt.run(id, taxTypeId);
    }
    await insertTaxTypeStmt.finalize();
    await org_db.exec("COMMIT");
    res.json({
      id,
      name,
      tax_types: taxTypes.map((t) => {
        return { tax_type_id: t };
      }),
      message: "Tax information updated successfully.",
    });
  } catch (err) {
    await org_db.exec("ROLLBACK");
    console.error("Error updating tax information:", err);
    res.status(500).json({ message: "Error updating tax information please check id's of tax types." });
  }
});

// Delete tax information entry and associated tax types by ID
router.delete("/:id", async (req, res) => {
  const org_db = await getCurrentDb(req);
  const { id } = req.params;

  try {
    await org_db.exec("BEGIN");
    const deleteTaxInfoStmt = await org_db.prepare("DELETE FROM taxinformation WHERE id = ?");
    await deleteTaxInfoStmt.run(id);
    await deleteTaxInfoStmt.finalize();

    const deleteTaxTypeStmt = await org_db.prepare("DELETE FROM taxtaxtype WHERE tax_information_id = ?");
    await deleteTaxTypeStmt.run(id);
    await deleteTaxTypeStmt.finalize();
    await org_db.exec("COMMIT");
    res.json({ message: "Tax information deleted successfully." });
  } catch (err) {
    await org_db.exec("ROLLBACK");
    console.error("Error deleting tax information:", err);
    res.status(500).json({ message: "Error deleting tax information." });
  }
});

module.exports = router;
