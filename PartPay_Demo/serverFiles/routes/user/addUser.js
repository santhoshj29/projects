const express = require("express");
const router = express.Router();
const bcrypt = require("bcrypt");
const { getDatabaseInstance } = require("../../database/start");

async function hash(plain) {
  const saltRounds = 10;
  try {
    const hash = await bcrypt.hash(plain, saltRounds);
    return hash;
  } catch (error) {
    console.error("Error hashing password:", error);
    throw error;
  }
}

router.post("/new", async (req, res) => {
  const { name, email, password, role, pay_per_hour } = req.body;
  const org_name = req.org_name;

  const db = await getDatabaseInstance("./Group4_PartPay.sqlite");

  const insertUserStmt = await db.prepare("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
  const selectOrgStmt = await db.prepare("SELECT * from organizations WHERE name = ?");
  const insertUserOrgStmt = await db.prepare("INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)");

  try {
    await db.run("BEGIN TRANSACTION");

    const hashedPassword = await hash(password);

    const userId = await insertUserStmt.run(name, email, hashedPassword).then(() => insertUserStmt.stmt.lastID);

    if (!userId) {
      res.status(404).send({ message: "User not found." });
    }

    const org = await selectOrgStmt.get(org_name);

    await insertUserOrgStmt.run(org.id, userId, role);

    if (role === "ptemployee") {
      const org_db = await getDatabaseInstance(org.id + "_" + org_name + ".sqlite");
      const insertPTEmpstmt = await org_db.prepare("INSERT INTO parttimeemployee (uid, pay_per_hour) VALUES (?, ?)");
      try {
        await insertPTEmpstmt.run(userId, pay_per_hour || 10);
      } catch (error) {
        throw error;
      } finally {
        if (insertPTEmpstmt) await insertPTEmpstmt.finalize();
        org_db.close();
      }
    }

    await db.run("COMMIT");

    res.status(201).json({ message: "User created successfully and added to ord successfully." });
  } catch (error) {
    await db.run("ROLLBACK");
    if (error.message === "SQLITE_CONSTRAINT: UNIQUE constraint failed: users.email") {
      res.status(500).json({ message: "Email is already registerd" });
    } else {
      console.log(error);
      res.status(500).json({ message: "Internal server error" });
    }
  } finally {
    if (insertUserStmt) await insertUserStmt.finalize();
    if (selectOrgStmt) await selectOrgStmt.finalize();
    if (insertUserOrgStmt) await insertUserOrgStmt.finalize();
    db.close();
  }
});

router.post("/existing", async (req, res) => {
  const { email, role, pay_per_hour } = req.body;
  const org_name = req.org_name;

  const db = await getDatabaseInstance("./Group4_PartPay.sqlite");

  const selectUserStmt = await db.prepare("SELECT * from users WHERE email = ?");
  const selectOrgStmt = await db.prepare("SELECT * from organizations WHERE name = ?");
  const insertUserOrgStmt = await db.prepare("INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)");

  try {
    await db.run("BEGIN TRANSACTION");

    const user = await selectUserStmt.get(email);

    if (!user) {
      res.status(404).send({ message: "User not found." });
    }

    const org = await selectOrgStmt.get(org_name);

    await insertUserOrgStmt.run(org.id, user.id, role);

    if (role === "ptemployee") {
      const org_db = await getDatabaseInstance(org.id + "_" + org_name + ".sqlite");
      const insertPTEmpstmt = await org_db.prepare("INSERT INTO parttimeemployee (uid, pay_per_hour) VALUES (?, ?)");
      try {
        await insertPTEmpstmt.run(user.id, pay_per_hour || 10);
      } catch (error) {
        throw error;
      } finally {
        if (insertPTEmpstmt) await insertPTEmpstmt.finalize();
        org_db.close();
      }
    }

    await db.run("COMMIT");

    res.status(201).json({ message: "User added to org successfully." });
  } catch (error) {
    await db.run("ROLLBACK");
    console.log(error);
    res.status(500).json({ message: "Internal server error" });
  } finally {
    if (selectUserStmt) await selectUserStmt.finalize();
    if (selectOrgStmt) await selectOrgStmt.finalize();
    if (insertUserOrgStmt) await insertUserOrgStmt.finalize();
    db.close();
  }
});

module.exports = router;
