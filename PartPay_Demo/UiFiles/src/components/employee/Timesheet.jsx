import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import { Button, Card, CardContent, Typography, Box, Table, TableBody, TableCell, TableHead, TableRow } from "@mui/material";
import TimesheetModal from "./TimesheetModal";

const Timesheet = () => {
  const [timesheets, setTimesheets] = useState([]);
  const [currentUserTimesheet, setCurrentUserTimesheet] = useState(null);
  const [timer, setTimer] = useState(null);
  const [timeElapsed, setTimeElapsed] = useState(0);
  const [totalHoursToday, setTotalHoursToday] = useState(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [editData, setEditData] = useState(null);

  useEffect(() => {
    fetchTimesheets();
  }, []);

  useEffect(() => {
    if (currentUserTimesheet && currentUserTimesheet.actual_start_time && !currentUserTimesheet.actual_end_time) {
      startTimer();
    }
    return () => {
      if (timer) {
        clearInterval(timer);
      }
    };
  }, [currentUserTimesheet]);

  const fetchTimesheets = async () => {
    try {
      const result = await api.get("/timesheet");
      const userId = localStorage.getItem("employee_id");
      const userTimesheets = result.data.filter((ts) => ts.employee_id.toString() === userId);
      setTimesheets(userTimesheets);
      checkCurrentDayTimesheet(userTimesheets);
    } catch (error) {
      console.log(error);
    }
  };

  const handleModalOpen = (data = null) => {
    setEditData(data);
    setModalOpen(true);
  };

  const handleModalClose = () => {
    setModalOpen(false);
  };

  const handleModalSubmit = (formData) => {
    console.log(formData);
    if (editData) {
      editTimesheet(editData.id, formData);
    } else {
      createNewTimesheet(formData);
    }
  };

  const checkCurrentDayTimesheet = (timesheets) => {
    const today = new Date().toISOString().slice(0, 10);
    const todayTimesheet = timesheets.find((ts) => ts.date === today);
    setCurrentUserTimesheet(todayTimesheet);
    if (todayTimesheet && todayTimesheet.actual_end_time) {
      calculateTotalHours(todayTimesheet);
    }
  };

  const startTimer = () => {
    const startTime = new Date(currentUserTimesheet.actual_start_time).getTime();
    setTimer(
      setInterval(() => {
        setTimeElapsed(Math.floor((Date.now() - startTime) / 1000));
      }, 1000)
    );
  };

  const calculateTotalHours = (timesheet) => {
    const startTime = new Date(timesheet.actual_start_time);
    const endTime = new Date(timesheet.actual_end_time);
    const duration = (endTime - startTime) / 3600000;
    setTotalHoursToday(duration.toFixed(2));
  };

  const createNewTimesheet = async (formData) => {
    // This example assumes a modal or form input for creation details.
    const userId = localStorage.getItem("employee_id");
    const newTimesheet = {
      employee_id: parseInt(userId),
      date: formData.date,
      actual_start_time: new Date(`${formData.date}T${formData.startTime}:00.000Z`).toISOString(),
      actual_end_time: formData.endTime ? new Date(`${formData.date}T${formData.endTime}:00.000Z`).toISOString() : null,
    };
    try {
      await api.post("/timesheet/new", newTimesheet);
      fetchTimesheets();
    } catch (error) {
      console.error("Error creating new timesheet:", error);
    }
  };

  const clockIn = async () => {
    const userId = localStorage.getItem("employee_id");
    const newTimesheet = {
      employee_id: parseInt(userId),
      date: new Date().toISOString().slice(0, 10),
      actual_start_time: new Date().toISOString(),
      actual_end_time: null,
    };

    try {
      await api.post("/timesheet/new", newTimesheet);
      fetchTimesheets();
    } catch (error) {
      console.error("Error clocking in:", error);
    }
  };

  const clockOut = async () => {
    if (currentUserTimesheet && timer) {
      clearInterval(timer);
    }

    const updatedTimesheet = {
      ...currentUserTimesheet,
      actual_end_time: new Date().toISOString(),
    };

    try {
      await api.put(`/timesheet/${currentUserTimesheet.id}`, updatedTimesheet);
      calculateTotalHours(updatedTimesheet);
      fetchTimesheets();
    } catch (error) {
      console.error("Error clocking out:", error);
    }
  };

  const editTimesheet = async (id, formData) => {
    const userId = localStorage.getItem("employee_id");
    try {
      await api.put(`/timesheet/${id}`, {
        employee_id: parseInt(userId),
        date: formData.date,
        actual_start_time: new Date(`${formData.date}T${formData.startTime}`).toLocaleString(),
        actual_end_time: formData.endTime ? new Date(`${formData.date}T${formData.endTime}`).toLocaleString() : null,
      });
      fetchTimesheets();
    } catch (error) {
      console.error("Error updating timesheet:", error);
    }
  };

  const deleteTimesheet = async (id) => {
    try {
      await api.delete(`/timesheet/${id}`);
      fetchTimesheets();
    } catch (error) {
      console.error("Error deleting timesheet:", error);
    }
  };

  const formatTimeForDisplay = (isoString) => {
    if (!isoString) return "-";
    const date = new Date(isoString);
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit", hour12: false });
  };

  const renderTimesheetTable = () => (
    <Table sx={{ minWidth: 650 }}>
      <TableHead>
        <TableRow>
          <TableCell>Date</TableCell>
          <TableCell align="right">Start Time</TableCell>
          <TableCell align="right">End Time</TableCell>
          <TableCell align="right">Total Hours</TableCell>
          <TableCell align="right">Actions</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {timesheets.map((row) => (
          <TableRow key={row.id} sx={{ "&:last-child td, &:last-child th": { border: 0 } }}>
            <TableCell component="th" scope="row">
              {row.date}
            </TableCell>
            <TableCell align="right">{formatTimeForDisplay(row.actual_start_time)}</TableCell>
            <TableCell align="right">{formatTimeForDisplay(row.actual_end_time)}</TableCell>
            <TableCell align="right">
              {row.actual_end_time ? ((new Date(row.actual_end_time) - new Date(row.actual_start_time)) / 3600000).toFixed(2) : "-"}
            </TableCell>
            <TableCell align="right">
              <Button key={row.id} onClick={() => handleModalOpen(row)}>
                Edit
              </Button>
              <Button color="error" onClick={() => deleteTimesheet(row.id)}>
                Delete
              </Button>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );

  return (
    <>
      <Typography variant="h4" gutterBottom component="div">
        Timesheets
      </Typography>
      <Card sx={{ maxWidth: 800, mx: "auto", mt: 5 }}>
        <CardContent sx={{ textAlign: "center" }}>
          <Typography variant="h4" gutterBottom component="div">
            Timesheet for Today
          </Typography>
          {currentUserTimesheet ? (
            currentUserTimesheet.actual_end_time ? (
              <Box>
                <Typography variant="h6">Total Hours Today</Typography>
                <Typography variant="h1" style={{ color: "#4285F4" }} sx={{ mb: 2 }}>
                  {totalHoursToday}
                </Typography>
              </Box>
            ) : (
              <Box>
                <Typography variant="h1" style={{ color: "#0F9D58" }} sx={{ mb: 2 }}>
                  {`${Math.floor(timeElapsed / 3600)
                    .toString()
                    .padStart(2, "0")}:${Math.floor((timeElapsed % 3600) / 60)
                    .toString()
                    .padStart(2, "0")}:${(timeElapsed % 60).toString().padStart(2, "0")}`}
                </Typography>
                <Button variant="contained" color="primary" size="large" onClick={clockOut}>
                  Clock Out
                </Button>
              </Box>
            )
          ) : (
            <Button variant="contained" color="primary" size="large" onClick={clockIn}>
              Clock In
            </Button>
          )}
        </CardContent>
      </Card>
      <Box sx={{ mt: 2 }}>
        <Button variant="contained" color="primary" onClick={() => handleModalOpen()}>
          Create New Timesheet
        </Button>
      </Box>
      {renderTimesheetTable()}
      <TimesheetModal open={modalOpen} handleClose={handleModalClose} handleSubmit={handleModalSubmit} initialData={editData} />
    </>
  );
};

export default Timesheet;
