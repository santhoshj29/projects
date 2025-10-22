const jwt = require("jsonwebtoken");

KEY = "7b#E9qR$3tP*5yF!2gM6hN@4wC+8vA1zG$4h&L8zP!s7R#9XmY2cQ+6dF1a@3VnE*5";

function verify(req, res, next) {
  const token = req.headers["jwt-access-token"];
  if (!token) {
    return res.status(440).send({ auth: false, message: "Session Expired Logging Out." });
  }

  jwt.verify(token, KEY, (err, decoded) => {
    if (err) {
      return res.status(440).send({ auth: false, message: "Session Expired Logging Out" });
    }
    req.user_id = decoded.user_id;
    req.org_id = decoded.org_id;
    req.org_name = decoded.org_name;
    req.schema_name = decoded.org_id + "_" + decoded.org_name + ".sqlite";
    req.role = decoded.role;
    next();
  });
}

module.exports = verify;
