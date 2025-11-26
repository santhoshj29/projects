const sqlite3 = require("sqlite3").verbose();
const path = require("path");
const { open } = require("sqlite");

async function getDatabaseInstance(filename) {
  filename = path.resolve(__dirname, filename);
  try {
    const db = await open({
      filename,
      driver: sqlite3.Database,
      mode: sqlite3.OPEN_READWRITE | sqlite3.OPEN_CREATE,
    });
    await db.run("PRAGMA foreign_keys = ON;");
    return db;
  } catch (err) {
    console.error(err.message);
    throw err;
  }
}

const initDatabase = async () => {
  const db = await getDatabaseInstance("./Group4_PartPay.sqlite");
  try {
    await db.run(`CREATE TABLE IF NOT EXISTS organizations (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL UNIQUE
    )`);
    await db.run(`CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      name TEXT NOT NULL,
      email TEXT NOT NULL UNIQUE,
      password TEXT NOT NULL,
      address TEXT,
      phone TEXT
    )`);
    await db.run(`CREATE TABLE IF NOT EXISTS user_orgs (
      org_id INTEGER NOT NULL,
      user_id INTEGER NOT NULL,
      role TEXT NOT NULL,
      PRIMARY KEY (org_id, user_id),
      FOREIGN KEY (org_id) REFERENCES organizations(id) ON DELETE CASCADE,
      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    )`);
  } catch (err) {
    console.error(err.message);
    throw err;
  } finally {
    await db.close();
  }
};

module.exports = { getDatabaseInstance, initDatabase };
