import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Button,
  TextField,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Paper,
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";

const ManageEmployees = () => {
  const initialEmployeeState = {
    name: "",
    email: "",
    password: "",
    role: "",
    phone: "",
    details: {
      pay_per_hour: "",
      account_number: "",
      routing_number: "",
    },
  };
  const [employees, setEmployees] = useState([]);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editableEmployee, setEditableEmployee] = useState(initialEmployeeState);
  const role = localStorage.getItem("role");

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const response = await api.get("/employees");
        setEmployees(response.data.filter((emp) => emp.role !== "admin"));
      } catch (error) {
        console.error("Error fetching employees:", error);
      }
    };
    fetchEmployees();
  }, [role]);

  const handleDialogOpen = (employee) => {
    setIsEditing(Boolean(employee));
    setEditableEmployee(
      employee || {
        name: "",
        email: "",
        password: "",
        role: "ptemployee",
        details: {
          pay_per_hour: 25,
          account_number: "",
          routing_number: "",
        },
      }
    );
    setDialogOpen(true);
  };

  const handleDialogClose = () => {
    setDialogOpen(false);
    setIsEditing(false);
    setEditableEmployee(initialEmployeeState);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "role" && value !== "ptemployee") {
      setEditableEmployee((prev) => ({
        ...prev,
        [name]: value,
        details: {},
      }));
    } else if (name in editableEmployee.details) {
      setEditableEmployee((prev) => ({
        ...prev,
        details: {
          ...prev.details,
          [name]: value,
        },
      }));
    } else {
      setEditableEmployee((prev) => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const saveEmployee = async () => {
    const body = isEditing
      ? editableEmployee
      : {
          ...editableEmployee,
          details: editableEmployee.role === "ptemployee" ? editableEmployee.details : undefined,
        };
    try {
      const method = isEditing ? "put" : "post";
      const endpoint = isEditing ? "/profile" : "/adduser/new";
      await api[method](endpoint, body);
      if (isEditing) {
        setEmployees(employees.map((emp) => (emp.id === editableEmployee.id ? editableEmployee : emp)));
      } else {
        setEmployees([...employees, body]);
      }
      handleDialogClose();
    } catch (error) {
      console.log("Error saving employee data:", error);
    }
  };

  const renderRoleSelector = () => (
    <FormControl fullWidth margin="dense">
      <InputLabel>Role</InputLabel>
      <Select label="Role" name="role" disabled={isEditing} value={editableEmployee.role} onChange={handleChange}>
        <MenuItem value="ptemployee">Part-Time Employee</MenuItem>
        <MenuItem value="manager">Manager</MenuItem>
        <MenuItem value="scheduler">Scheduler</MenuItem>
      </Select>
    </FormControl>
  );

  const renderTable = (role) => (
    <Paper style={{ overflowX: "auto" }}>
      <Table style={{ minWidth: 650 }}>
        <TableHead>
          <TableRow>
            <TableCell>
              <b>Name</b>
            </TableCell>
            <TableCell>
              <b>Email</b>
            </TableCell>
            <TableCell>
              <b>Address</b>
            </TableCell>
            <TableCell>
              <b>Phone</b>
            </TableCell>
            {role === "ptemployee" && (
              <TableCell>
                <b>Pay Per Hour</b>
              </TableCell>
            )}
            {role === "ptemployee" && (
              <TableCell>
                <b>Account Number</b>
              </TableCell>
            )}
            {role === "ptemployee" && (
              <TableCell>
                <b>Routing Number</b>
              </TableCell>
            )}
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {employees.filter((emp) => emp.role === role).length > 0 ? (
            employees
              .filter((emp) => emp.role === role)
              .map((emp) => (
                <TableRow key={emp.id}>
                  <TableCell>{emp.name}</TableCell>
                  <TableCell>{emp.email}</TableCell>
                  <TableCell>{emp.address || "N/A"}</TableCell>
                  <TableCell>{emp.phone || "N/A"}</TableCell>
                  {role === "ptemployee" && <TableCell>{emp.details?.pay_per_hour}</TableCell>}
                  {role === "ptemployee" && <TableCell>{emp.details?.account_number || "N/A"}</TableCell>}
                  {role === "ptemployee" && <TableCell>{emp.details?.routing_number || "N/A"}</TableCell>}
                  <TableCell>
                    <IconButton onClick={() => handleDialogOpen(emp)}>
                      <EditIcon />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))
          ) : (
            <TableRow>
              <TableCell colSpan={role === "ptemployee" ? 8 : 5} style={{ textAlign: "center" }}>
                Nobody is here
              </TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </Paper>
  );

  return (
    <div>
      {role === "admin" && (
        <Button variant="contained" startIcon={<AddIcon />} onClick={() => handleDialogOpen()}>
          Add New Employee
        </Button>
      )}
      <Dialog open={dialogOpen} onClose={handleDialogClose}>
        <DialogTitle>{isEditing ? "Edit Employee" : "Add New Employee"}</DialogTitle>
        <DialogContent>
          {renderRoleSelector()}
          <TextField
            margin="dense"
            label="Name"
            type="text"
            fullWidth
            variant="outlined"
            name="name"
            value={editableEmployee.name}
            onChange={handleChange}
          />
          <TextField
            margin="dense"
            label="Email"
            disabled={role !== "admin"}
            type="email"
            fullWidth
            variant="outlined"
            name="email"
            value={editableEmployee.email}
            onChange={handleChange}
          />
          {role === "admin" && (
            <TextField
              margin="dense"
              label="Password"
              type="password"
              fullWidth
              variant="outlined"
              name="password"
              value={editableEmployee.password}
              onChange={handleChange}
            />
          )}
          {editableEmployee.role === "ptemployee" && (
            <>
              <TextField
                margin="dense"
                label="Pay Per Hour"
                type="number"
                fullWidth
                variant="outlined"
                name="pay_per_hour"
                value={editableEmployee.details.pay_per_hour}
                onChange={handleChange}
              />
              {isEditing && (
                <>
                  <TextField
                    margin="dense"
                    label="Account Number"
                    type="text"
                    fullWidth
                    variant="outlined"
                    name="account_number"
                    value={editableEmployee.details.account_number}
                    onChange={handleChange}
                  />
                  <TextField
                    margin="dense"
                    label="Routing Number"
                    type="text"
                    fullWidth
                    variant="outlined"
                    name="routing_number"
                    value={editableEmployee.details.routing_number}
                    onChange={handleChange}
                  />
                </>
              )}
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose}>Cancel</Button>
          <Button onClick={saveEmployee}>Save</Button>
        </DialogActions>
      </Dialog>
      {(role === "admin" ? ["ptemployee", "manager", "scheduler"] : ["ptemployee", "scheduler"]).map((role) => (
        <div key={role}>
          <h2>{role.charAt(0).toUpperCase() + role.slice(1)}</h2>
          {renderTable(role)}
        </div>
      ))}
    </div>
  );
};

export default ManageEmployees;
