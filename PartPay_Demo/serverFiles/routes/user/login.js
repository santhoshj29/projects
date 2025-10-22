const jwt = require("jsonwebtoken");
const express = require("express");
const router = express.Router();
const bcrypt = require("bcrypt");
const { getDatabaseInstance } = require("../../database/start");

KEY = "7b#E9qR$3tP*5yF!2gM6hN@4wC+8vA1zG$4h&L8zP!s7R#9XmY2cQ+6dF1a@3VnE*5";

router.post("/", async (req, res) => {
  db = await getDatabaseInstance("./Group4_PartPay.sqlite");
  const { email, password, org_name } = req.body;

  const getUserStmt = await db.prepare("SELECT * FROM users WHERE email = ?");
  const userOrgStmt = await db.prepare(
    `SELECT * FROM user_orgs JOIN organizations ON user_orgs.org_id = organizations.id WHERE user_orgs.user_id = ? AND organizations.name = ?;`
  );

  try {
    const user = await getUserStmt.get(email);

    if (!user) {
      return res.status(401).send({ auth: false, message: "User dosent exist" });
    }

    const passwordIsValid = bcrypt.compareSync(password, user.password);

    if (!passwordIsValid) {
      return res.status(401).send({ auth: false, message: "Wrong Password" });
    }

    const userOrg = await userOrgStmt.get(user.id, org_name);

    if (!userOrg) {
      return res.status(404).send({ auth: false, message: "User is not associated with this organization." });
    }

    org_db = await getDatabaseInstance(userOrg.org_id + "_" + org_name + ".sqlite");
    orgEmpStmt = "SELECT id from parttimeemployee where uid = ?";
    const orgEmp = await org_db.get(orgEmpStmt, [userOrg.user_id]);

    const token = jwt.sign(
      { employee_id: orgEmp?.id || null, user_id: userOrg.user_id, org_id: userOrg.org_id, role: userOrg.role, org_name },
      KEY,
      {
        expiresIn: 86400,
      }
    );

    res.status(200).send({
      auth: true,
      token: token,
      role: userOrg.role,
      employee_id: orgEmp?.id || null,
      org_id: userOrg.org_id,
      message: "Login Successful",
    });
  } catch (error) {
    console.error(error);
    return res.status(500).send({ auth: false, message: "Internal Server Error" });
  } finally {
    if (getUserStmt) await getUserStmt.finalize();
    if (userOrgStmt) await userOrgStmt.finalize();
    // db.close();
  }
});

module.exports = router;
