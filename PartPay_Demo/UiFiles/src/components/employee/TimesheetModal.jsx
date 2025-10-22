import { useState, useEffect } from "react";
import { Modal, Box, Typography, TextField, Button } from "@mui/material";

function TimesheetModal({ open, handleClose, handleSubmit, initialData }) {
  const [formData, setFormData] = useState({
    date: "",
    startTime: "",
    endTime: "",
  });

  useEffect(() => {
    if (initialData) {
      // Create date objects from the server timestamps
      const startTime = new Date(initialData.actual_start_time);
      const endTime = new Date(initialData.actual_end_time);

      // Convert each time to local time by adjusting for the timezone offset
      const localStartTime = new Date(startTime.getTime() - startTime.getTimezoneOffset() * 60000);
      const localEndTime = new Date(endTime.getTime() - endTime.getTimezoneOffset() * 60000);

      setFormData({
        date: initialData.date,
        startTime: localStartTime.toISOString().substring(11, 16), // Adjusted local time
        endTime: localEndTime.toISOString().substring(11, 16), // Adjusted local time
      });
    }
  }, [initialData]);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const onSubmit = () => {
    handleSubmit(formData);
    handleClose();
  };

  return (
    <Modal open={open} onClose={handleClose} aria-labelledby="modal-modal-title" aria-describedby="modal-modal-description">
      <Box
        sx={{
          position: "absolute",
          top: "50%",
          left: "50%",
          transform: "translate(-50%, -50%)",
          width: 400,
          bgcolor: "background.paper",
          boxShadow: 24,
          p: 4,
        }}
      >
        <Typography id="modal-modal-title" variant="h6" component="h2">
          {initialData ? "Edit Timesheet" : "Create Timesheet"}
        </Typography>
        <form>
          <TextField
            key={`date-${initialData?.date}`}
            margin="normal"
            required
            fullWidth
            label="Date"
            name="date"
            type="date"
            InputLabelProps={{ shrink: true }}
            value={formData.date}
            onChange={handleChange}
          />
          <TextField
            key={`start-time-${initialData?.actual_start_time}`}
            margin="normal"
            required
            fullWidth
            label="Start Time"
            name="startTime"
            type="time"
            InputLabelProps={{ shrink: true }}
            value={formData.startTime}
            onChange={handleChange}
          />
          <TextField
            key={`end-time-${initialData?.actual_end_time}`}
            margin="normal"
            required
            fullWidth
            label="End Time"
            name="endTime"
            type="time"
            InputLabelProps={{ shrink: true }}
            value={formData.endTime}
            onChange={handleChange}
          />

          <Box sx={{ mt: 2, display: "flex", justifyContent: "flex-end" }}>
            <Button onClick={handleClose} sx={{ mr: 1 }}>
              Cancel
            </Button>
            <Button variant="contained" onClick={onSubmit}>
              Save
            </Button>
          </Box>
        </form>
      </Box>
    </Modal>
  );
}

export default TimesheetModal;
