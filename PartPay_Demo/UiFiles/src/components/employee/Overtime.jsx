import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import {
  Button,
  TextField,
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

const Overtime = () => {
  const [overtimeEntries, setOvertimeEntries] = useState([]);
  const [selectedDate, setSelectedDate] = useState("");
  const [hours, setHours] = useState("");
  const [openDialog, setOpenDialog] = useState(false);
  const userId = localStorage.getItem("employee_id");
  const role = localStorage.getItem("role");

  // Fetch overtime entries
  useEffect(() => {
    api
      .get("/overtime")
      .then((res) => {
        setOvertimeEntries(res.data);
      })
      .catch((err) => console.error(err));
  }, []);

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const handleSubmitOvertime = async () => {
    try {
      const payload = {
        employee_id: userId,
        date: selectedDate,
        hours: parseInt(hours, 10),
      };
      await api.post("/overtime/new", payload);
      setOvertimeEntries([...overtimeEntries, { ...payload, status: "pending" }]);
      handleCloseDialog();
    } catch (error) {
      console.error(error);
    }
  };

  const handleUpdateStatus = async (entryId, newStatus) => {
    try {
      await api.put(`/overtime/${entryId}`, {
        status: newStatus,
      });
      const updatedEntries = overtimeEntries.map((entry) => (entry.id === entryId ? { ...entry, status: newStatus } : entry));
      setOvertimeEntries(updatedEntries);
    } catch (error) {
      console.error("Failed to update overtime status:", error);
    }
  };

  return (
    <div>
      <h1>Overtime Management</h1>
      {role === "ptemployee" && (
        <Button variant="contained" style={{ margin: "20px 0" }} onClick={handleOpenDialog}>
          Request Overtime
        </Button>
      )}
      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Request Overtime</DialogTitle>
        <DialogContent style={{ margin: "20px 0" }}>
          <TextField
            type="date"
            label="Date"
            InputLabelProps={{ shrink: true }}
            fullWidth
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            margin="dense"
          />
          <TextField label="Hours" fullWidth type="number" value={hours} onChange={(e) => setHours(e.target.value)} margin="dense" />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button onClick={handleSubmitOvertime}>Submit</Button>
        </DialogActions>
      </Dialog>
      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Date</TableCell>
              <TableCell>Hours</TableCell>
              <TableCell>Status</TableCell>
              {role !== "ptemployee" && <TableCell>Actions</TableCell>}
            </TableRow>
          </TableHead>
          <TableBody>
            {overtimeEntries.map((entry) => (
              <TableRow key={entry.id}>
                <TableCell>{entry.date}</TableCell>
                <TableCell>{entry.hours}</TableCell>
                <TableCell>{entry.status}</TableCell>
                {role !== "ptemployee" && (
                  <TableCell>
                    <Button color="primary" disabled={entry.status === "approved"} onClick={() => handleUpdateStatus(entry.id, "approved")}>
                      Approve
                    </Button>
                    <Button color="primary" disabled={entry.status === "rejected"} onClick={() => handleUpdateStatus(entry.id, "rejected")}>
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

export default Overtime;
