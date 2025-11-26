import { useState, useEffect } from "react";
import { Button, TextField, FormControl, InputLabel, Select, MenuItem, Box, Paper, Typography } from "@mui/material";
import { api } from "../../services/axios";

function PayslipGenerator() {
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [taxId, setTaxId] = useState("");
  const [taxOptions, setTaxOptions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  useEffect(() => {
    const fetchTaxes = async () => {
      try {
        const response = await api.get("/tax");
        setTaxOptions(response.data);
      } catch (error) {
        console.error("Failed to fetch tax data:", error);
      }
    };
    fetchTaxes();
  }, []);

  const handleGeneratePayslips = async () => {
    setLoading(true);
    setMessage("");
    try {
      const response = await api.post("/payslips/generate", {
        start_date: fromDate,
        end_date: toDate,
        tax_id: taxId,
      });
      setMessage("Success: " + response.data.message);
    } catch (error) {
      setMessage("Error: " + error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Paper elevation={3} style={{ padding: "20px", maxWidth: "500px", margin: "auto" }}>
      <Typography variant="h6" component="h3" style={{ marginBottom: "20px" }}>
        Generate Payslips
      </Typography>
      <Box component="form" noValidate autoComplete="off">
        <TextField
          fullWidth
          label="From Date"
          type="date"
          value={fromDate}
          onChange={(e) => setFromDate(e.target.value)}
          InputLabelProps={{
            shrink: true,
          }}
          style={{ marginBottom: "20px" }}
        />
        <TextField
          fullWidth
          label="To Date"
          type="date"
          value={toDate}
          onChange={(e) => setToDate(e.target.value)}
          InputLabelProps={{
            shrink: true,
          }}
          style={{ marginBottom: "20px" }}
        />
        <FormControl fullWidth style={{ marginBottom: "20px" }}>
          <InputLabel id="tax-label">Tax</InputLabel>
          <Select labelId="tax-label" id="tax-select" value={taxId} label="Tax" onChange={(e) => setTaxId(e.target.value)}>
            {taxOptions.map((option) => (
              <MenuItem key={option.id} value={option.id}>
                {option.name}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <Button fullWidth variant="contained" color="primary" onClick={handleGeneratePayslips} disabled={loading}>
          Generate Payslips
        </Button>
      </Box>
      {message && (
        <Typography color="textSecondary" style={{ marginTop: "20px" }}>
          {message}
        </Typography>
      )}
    </Paper>
  );
}

export default PayslipGenerator;
