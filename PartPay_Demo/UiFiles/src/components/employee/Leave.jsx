import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import {
  Button,
  TextField,
  MenuItem,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
} from "@mui/material";

const Leave = () => {
  const [shifts, setShifts] = useState([]);
  const [leaveRequests, setLeaveRequests] = useState([]);
  const [selectedShift, setSelectedShift] = useState("");
  const [reason, setReason] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const role = localStorage.getItem("role");
  const userId = localStorage.getItem("employee_id");

  // Fetch shifts
  useEffect(() => {
    api
      .get("/schedule")
      .then((res) => {
        setShifts(res.data);
      })
      .catch((err) => console.error(err));
  }, []);

  // Fetch leave requests
  useEffect(() => {
    api
      .get("/leave")
      .then((res) => {
        setLeaveRequests(res.data);
      })
      .catch((err) => console.error(err));
  }, []);

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSubmitLeave = async () => {
    try {
      await api.post("/leave/new", { shiftid: selectedShift, reason });
      setLeaveRequests([...leaveRequests, { shiftid: selectedShift, reason, status: "pending" }]);
      handleCloseDialog();
    } catch (error) {
      console.error(error);
    }
  };

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await api.put(`/leave/${id}`, {
        status: newStatus,
      });
      const updatedRequests = leaveRequests.map((lr) => (lr.id === id ? { ...lr, status: newStatus } : lr));
      setLeaveRequests(updatedRequests);
    } catch (error) {
      console.error("Failed to update swap request status:", error);
    }
  };

  return (
    <div>
      <h1>Leave Management</h1>
      {role === "ptemployee" && (
        <Button variant="contained" style={{ margin: "20px 0" }} onClick={handleOpenDialog}>
          Request Leave
        </Button>
      )}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Request Leave</DialogTitle>
        <DialogContent style={{ margin: "20px 0" }}>
          <TextField
            style={{ marginTop: "5px" }}
            select
            label="Select Shift"
            fullWidth
            value={selectedShift}
            onChange={(e) => setSelectedShift(e.target.value)}
          >
            {shifts
              .filter((s) => s.employee_id == userId)
              .map((shift) => (
                <MenuItem key={shift.id} value={shift.id}>
                  {`${shift.date} from ${shift.start_time} to ${shift.end_time}`}
                </MenuItem>
              ))}
          </TextField>
          <TextField label="Reason for Leave" fullWidth value={reason} onChange={(e) => setReason(e.target.value)} margin="dense" />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmitLeave}>Submit</Button>
        </DialogActions>
      </Dialog>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Shift ID</TableCell>
              <TableCell>Reason</TableCell>
              <TableCell>Status</TableCell>
              {role !== "ptemployee" && <TableCell>Actions</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {leaveRequests
              .filter((lr) => {
                if (role !== "ptemployee") {
                  return true;
                }
                return shifts.find((s) => s.id == lr.shiftid)?.employee_id == userId;
              })
              .map((request) => (
                <TableRow key={request.id}>
                  <TableCell>{request.shiftid}</TableCell>
                  <TableCell>{request.reason}</TableCell>
                  <TableCell>{request.status}</TableCell>
                  {role !== "ptemployee" && (
                    <TableCell>
                      <Button
                        color="primary"
                        disabled={request.status === "approved"}
                        onClick={() => handleUpdateStatus(request.id, "approved")}
                      >
                        Approve
                      </Button>
                      <Button
                        color="primary"
                        disabled={request.status === "rejected"}
                        onClick={() => handleUpdateStatus(request.id, "rejected")}
                      >
                        Reject
                      </Button>
                    </TableCell>
                  )}
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
};

export default Leave;
