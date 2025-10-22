import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper } from "@mui/material";

const Reports = () => {
  const [payslips, setPayslips] = useState([]);
  const userId = localStorage.getItem("employee_id");
  const role = localStorage.getItem("role");

  useEffect(() => {
    const getPayslips = async () => {
      try {
        let result;
        if (role !== "ptemployee") {
          result = await api.get("/payslips");
        } else {
          result = await api.get(`/payslips/${userId}`);
        }
        if (result && result.data) {
          setPayslips(result.data);
        }
      } catch (error) {
        console.error("Error fetching payslips:", error);
      }
    };

    getPayslips();
  }, []);

  return (
    <>
      <h1>{role === "ptemployee" ? "Pay Slips" : "Reports"}</h1>
      <TableContainer component={Paper}>
        <Table aria-label="detailed payslip table">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell align="right">Employee ID</TableCell>
              <TableCell align="right">Generated Date</TableCell>
              <TableCell align="right">From Date</TableCell>
              <TableCell align="right">To Date</TableCell>
              <TableCell align="right">Hours Worked</TableCell>
              <TableCell align="right">Pay per Hour</TableCell>
              <TableCell align="right">Gross Pay</TableCell>
              <TableCell align="right">Net Pay</TableCell>
              <TableCell align="right">Tax ID</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {payslips.map((row) => (
              <TableRow key={row.id}>
                <TableCell component="th" scope="row">
                  {row.id}
                </TableCell>
                <TableCell align="right">{row.employee_id}</TableCell>
                <TableCell align="right">{row.generated_date}</TableCell>
                <TableCell align="right">{row.from_date}</TableCell>
                <TableCell align="right">{row.to_date}</TableCell>
                <TableCell align="right">{row.hours_worked}</TableCell>
                <TableCell align="right">{row.pay_per_hour}</TableCell>
                <TableCell align="right">{row.gross_pay}</TableCell>
                <TableCell align="right">{row.net_pay}</TableCell>
                <TableCell align="right">{row.tax_id}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </>
  );
};

export default Reports;
