const express = require("express");
const cors = require("cors");
const { initDatabase } = require("./database/start");
const signupAPIs = require("./routes/user/signup");
const loginAPIs = require("./routes/user/login");
const verify = require("./routes/authentication/verification");
const addUserAPI = require("./routes/user/addUser");
const profileAPI = require("./routes/user/profile");
const scheduleAPIs = require("./routes/employee/schedule");
const swapAPIs = require("./routes/employee/swap");
const leaveAPIs = require("./routes/employee/leave");
const overtimeAPIs = require("./routes/employee/overtime");
const timesheetAPIs = require("./routes/employee/timesheet");
const taxtypesAPIs = require("./routes/payroll/taxtypes");
const taxAPIs = require("./routes/payroll/tax");
const payslipsAPIs = require("./routes/payroll/payslips");
const employeesAPIs = require("./routes/employee");

const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

initDatabase();

app.use("/signup", signupAPIs);
app.use("/login", loginAPIs);

app.use(verify);

app.use("/adduser", addUserAPI);
app.use("/profile", profileAPI);
app.use("/schedule", scheduleAPIs);
app.use("/swap", swapAPIs);
app.use("/leave", leaveAPIs);
app.use("/overtime", overtimeAPIs);
app.use("/timesheet", timesheetAPIs);
app.use("/taxtypes", taxtypesAPIs);
app.use("/tax", taxAPIs);
app.use("/payslips", payslipsAPIs);
app.use("/employees", employeesAPIs);

app.listen(port, () => {
  console.log(`App listening at http://localhost:${port}`);
});
