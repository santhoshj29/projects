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

router.post("/", async (req, res) => {
  const { name, email, password, org_name } = req.body;

  const db = await getDatabaseInstance("./Group4_PartPay.sqlite");

  const insertUserStmt = await db.prepare("INSERT INTO users (name, email, password) VALUES (?, ?, ?)");
  const insertOrgStmt = await db.prepare("INSERT INTO organizations (name) VALUES (?)");
  const insertUserOrgStmt = await db.prepare("INSERT INTO user_orgs (org_id, user_id, role) VALUES (?, ?, ?)");
  try {
    await db.run("BEGIN TRANSACTION");

    const hashedPassword = await hash(password);

    await insertUserStmt.run(name, email, hashedPassword);
    const userId = insertUserStmt.stmt.lastID;

    await insertOrgStmt.run(org_name);
    const orgId = insertOrgStmt.stmt.lastID;

    await insertUserOrgStmt.run(orgId, userId, "admin");

    await db.run("COMMIT");

    await createOrgTables(orgId, org_name, res);

    res.status(201).json({ message: "User signed up successfully." });
  } catch (error) {
    await db.run("ROLLBACK");
    console.log(error);
    if (error.message === "SQLITE_CONSTRAINT: UNIQUE constraint failed: users.email") {
      res.status(500).json({ message: "Email is already registerd" });
    } else if (error.message === "SQLITE_CONSTRAINT: UNIQUE constraint failed: organizations.name") {
      res.status(500).json({ message: "An org is already registerd with this name" });
    } else {
      res.status(500).json({ message: "Internal server error" });
    }
  } finally {
    if (insertUserStmt) await insertUserStmt.finalize();
    if (insertOrgStmt) await insertOrgStmt.finalize();
    if (insertUserOrgStmt) await insertUserOrgStmt.finalize();
    db.close();
  }
});

async function createOrgTables(org_id, org_name, res) {
  const org_db = await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");

  const table_creation_queries = {
    part_time_employees: `CREATE TABLE IF NOT EXISTS parttimeemployee (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        uid INTEGER NOT NULL,
                        pay_per_hour REAL NOT NULL,
                        account_number TEXT,
                        routing_number TEXT
                      );`,
    employee_schedules: `CREATE TABLE IF NOT EXISTS employeeschedules (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        employee_id INTEGER NOT NULL,
                        date DATE NOT NULL,
                        start_time TIMESTAMP NOT NULL,
                        end_time TIMESTAMP NOT NULL,
                        FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE
                    );`,
    overtime_requests: `CREATE TABLE IF NOT EXISTS overtimerequests (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        employee_id INTEGER NOT NULL,
                        date DATE NOT NULL,
                        hours INTEGER NOT NULL,
                        status TEXT CHECK(status IN ('pending', 'approved', 'rejected')) NOT NULL,
                        FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE
                      );`,
    time_sheets: `CREATE TABLE IF NOT EXISTS timesheets (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  employee_id INTEGER NOT NULL,
                  date DATE NOT NULL,
                  actual_start_time TIMESTAMP NOT NULL,
                  actual_end_time TIMESTAMP,
                  FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE
                );`,
    swap_requests: `CREATE TABLE IF NOT EXISTS swaprequests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    shiftid INTEGER NOT NULL,
                    targetshiftid INTEGER NOT NULL,
                    reason TEXT,
                    status TEXT CHECK(status IN ('pending', 'approved', 'rejected')) NOT NULL,
                    FOREIGN KEY (shiftid) REFERENCES employeeschedules(id) ON DELETE CASCADE,
                    FOREIGN KEY (targetshiftid) REFERENCES employeeschedules(id) ON DELETE CASCADE
                );`,
    leave_requests: `CREATE TABLE IF NOT EXISTS leaverequests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    shiftid INTEGER NOT NULL,
                    reason TEXT,
                    status TEXT CHECK(status IN ('pending', 'approved', 'rejected')) NOT NULL,
                    FOREIGN KEY (shiftid) REFERENCES employeeschedules(id) ON DELETE CASCADE
                );`,
    tax_information: `CREATE TABLE IF NOT EXISTS taxinformation (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      name TEXT NOT NULL
                    );`,
    tax_types: `CREATE TABLE IF NOT EXISTS taxtypes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                deduction_percentage INTEGER NOT NULL
              );`,
    tax_tax_types: `CREATE TABLE IF NOT EXISTS taxtaxtype (
                    tax_information_id INTEGER NOT NULL,
                    tax_type_id INTEGER NOT NULL,
                    PRIMARY KEY (tax_information_id, tax_type_id),
                    FOREIGN KEY (tax_information_id) REFERENCES taxinformation(id) ON DELETE CASCADE,
                    FOREIGN KEY (tax_type_id) REFERENCES taxtypes(id) ON DELETE CASCADE
                  );`,
    pay_slips: `CREATE TABLE IF NOT EXISTS payslips (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                employee_id INTEGER NOT NULL,
                generated_date DATE NOT NULL,
                from_date DATE NOT NULL,
                to_date DATE NOT NULL,
                hours_worked INTEGER NOT NULL,
                pay_per_hour INTEGER NOT NULL,
                gross_pay INTEGER NOT NULL,
                net_pay INTEGER NOT NULL,
                tax_id INTEGER NOT NULL,
                FOREIGN KEY (tax_id) REFERENCES taxinformation(id) ON DELETE CASCADE,
                FOREIGN KEY (employee_id) REFERENCES parttimeemployee(id) ON DELETE CASCADE
              );`,
  };

  try {
    await Promise.all(
      Object.values(table_creation_queries).map(async (sql) => {
        await org_db.run(sql);
      })
    );
  } catch (err) {
    console.error(err.message);
    res.status(500).json({ message: err.message });
  } finally {
    await org_db.close();
  }
}

module.exports = router;
