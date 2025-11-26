import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import {
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  TextField,
  MenuItem,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Box,
} from "@mui/material";

const Swap = () => {
  const [employees, setEmployees] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [swapRequests, setSwapRequests] = useState([]);
  const [open, setOpen] = useState(false);
  const [selectedShift, setSelectedShift] = useState("");
  const [targetShift, setTargetShift] = useState("");
  const [reason, setReason] = useState("");
  const role = localStorage.getItem("role");
  const userId = localStorage.getItem("employee_id");

  useEffect(() => {
    fetchEmployees();
    fetchSchedules();
    fetchSwapRequests();
  }, []);

  const fetchEmployees = async () => {
    try {
      const { data } = await api.get("/profile/employees");
      setEmployees(data);
    } catch (error) {
      console.error("Failed to fetch employees:", error);
    }
  };

  const fetchSchedules = async () => {
    try {
      const { data } = await api.get("/schedule");
      setSchedules(data);
    } catch (error) {
      console.error("Failed to fetch schedules:", error);
    }
  };

  const fetchSwapRequests = async () => {
    try {
      const { data } = await api.get("/swap");
      setSwapRequests(data);
    } catch (error) {
      console.error("Failed to fetch swap requests:", error);
    }
  };

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleCreateSwap = async () => {
    try {
      await api.post("/swap/new", {
        shiftid: selectedShift,
        targetshiftid: targetShift,
        reason: reason,
        status: "pending",
      });
      fetchSwapRequests();
      handleClose();
    } catch (error) {
      console.error("Failed to create swap request:", error);
    }
  };

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await api.put(`/swap/${id}`, {
        status: newStatus,
      });
      fetchSwapRequests();
    } catch (error) {
      console.error("Failed to update swap request status:", error);
    }
  };

  const handleDeleteSwap = async (id) => {
    try {
      await api.delete(`/swap/${id}`);
      fetchSwapRequests();
    } catch (error) {
      console.error("Failed to delete swap request:", error);
    }
  };

  return (
    <Box sx={{ position: "relative", padding: 2 }}>
      {role !== "manager" && (
        <Button variant="contained" color="primary" onClick={handleClickOpen} sx={{ position: "absolute", right: 0, top: 0 }}>
          Create New Swap Request
        </Button>
      )}
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Create New Swap Request</DialogTitle>
        <DialogContent>
          <TextField
            select
            fullWidth
            label="Select Your Shift"
            value={selectedShift}
            onChange={(e) => setSelectedShift(e.target.value)}
            margin="dense"
          >
            {schedules
              .filter((schedule) => role !== "ptemployee" || schedule.employee_id == userId)
              .map((schedule) => (
                <MenuItem key={schedule.id} value={schedule.id}>
                  {`${schedule.date} (${employees.find((e) => e.id === schedule.employee_id)?.name}) From: ${schedule.start_time} To: ${
                    schedule.end_time
                  }`}
                </MenuItem>
              ))}
          </TextField>
          <TextField
            select
            fullWidth
            label="Select Target Shift"
            value={targetShift}
            onChange={(e) => setTargetShift(e.target.value)}
            margin="dense"
          >
            {schedules
              .filter((schedule) => role !== "ptemployee" || schedule.employee_id != userId)
              .map((schedule) => (
                <MenuItem key={schedule.id} value={schedule.id}>
                  {`${schedule.date} (${employees.find((e) => e.id === schedule.employee_id)?.name}) From: ${schedule.start_time} To: ${
                    schedule.end_time
                  }`}
                </MenuItem>
              ))}
          </TextField>
          <TextField label="Reason" fullWidth value={reason} onChange={(e) => setReason(e.target.value)} margin="dense" />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleCreateSwap} color="primary">
            Submit Request
          </Button>
        </DialogActions>
      </Dialog>
      <h1>Swap Requests</h1>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Shift ID (Employee)</TableCell>
              <TableCell>Target Shift ID (Employee)</TableCell>
              <TableCell>Reason</TableCell>
              <TableCell>Status</TableCell>
              {role !== "ptemployee" && <TableCell>Actions</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {swapRequests
              .filter((sr) => {
                if (role !== "ptemployee") {
                  return true;
                }
                if (sr.status === "approved") {
                  return false;
                }
                return schedules.find((s) => s.id == sr.shiftid)?.employee_id == userId;
              })
              .map((request) => (
                <TableRow key={request.id}>
                  <TableCell>{request.id}</TableCell>
                  <TableCell>
                    {request.shiftid} ({employees.find((e) => e.id === schedules.find((s) => s.id === request.shiftid)?.employee_id)?.name})
                  </TableCell>
                  <TableCell>
                    {request.targetshiftid} (
                    {employees.find((e) => e.id === schedules.find((s) => s.id === request.targetshiftid)?.employee_id)?.name})
                  </TableCell>
                  <TableCell>{request.reason}</TableCell>
                  <TableCell>{request.status}</TableCell>
                  {role !== "ptemployee" && (
                    <TableCell>
                      <Button
                        color="primary"
                        onClick={() => handleUpdateStatus(request.id, "approved")}
                        disabled={request.status === "approved"}
                      >
                        Approve
                      </Button>
                      <Button
                        color="primary"
                        onClick={() => handleUpdateStatus(request.id, "rejected")}
                        disabled={request.status !== "pending"}
                      >
                        Reject
                      </Button>
                      <Button color="secondary" onClick={() => handleDeleteSwap(request.id)} disabled={request.status === "deleted"}>
                        Delete
                      </Button>
                    </TableCell>
                  )}
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
};

export default Swap;
