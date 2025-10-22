const express = require("express");
const router = express.Router();
const { getDatabaseInstance } = require("../../database/start");

const getCurrentOrgDb = async (req) => {
  const { org_id, org_name } = req;
  return await getDatabaseInstance(org_id + "_" + org_name + ".sqlite");
};

const getMainDb = async () => {
  return await getDatabaseInstance("./Group4_PartPay.sqlite");
};

router.get("/", async (req, res) => {
  const { role } = req.body;
  const org_db = await getCurrentOrgDb(req);
  const db = await getMainDb();

  try {
    let userQuery = `SELECT users.id, users.name, users.email, users.address, users.phone, user_orgs.role
      FROM users
      JOIN user_orgs ON users.id = user_orgs.user_id
      WHERE user_orgs.org_id = ?`;
    if (role) {
      userQuery += ` AND user_orgs.role = ?`;
      var users = await db.all(userQuery, [req.org_id, role]);
    } else {
      var users = await db.all(userQuery, [req.org_id]);
    }

    if (!users.length) {
      res.status(404).json({ message: "No users found" });
      return;
    }

    const fullProfiles = await Promise.all(
      users.map(async (user) => {
        if (user.role === "ptemployee") {
          const ptEmployeeQuery = `SELECT * FROM parttimeemployee WHERE uid = ?`;
          const [ptEmployee] = await org_db.all(ptEmployeeQuery, [user.id]);

          return ptEmployee ? { ...user, details: ptEmployee } : user;
        }
        return user;
      })
    );

    res.json(fullProfiles);
  } catch (error) {
    console.error("Failed to retrieve profiles:", error);
    res.status(500).json({ message: "Error retrieving user profiles" });
  }
});

module.exports = router;
