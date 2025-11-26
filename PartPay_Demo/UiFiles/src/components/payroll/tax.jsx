import { useState, useEffect } from "react";
import { api } from "../../services/axios";
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  TextField,
} from "@mui/material";

const TaxDashboard = () => {
  const [taxes, setTaxes] = useState([]);
  const [taxTypes, setTaxTypes] = useState([]);
  const [currentTax, setCurrentTax] = useState(null);
  const [currentTaxType, setCurrentTaxType] = useState(null);
  const [openTaxDialog, setOpenTaxDialog] = useState(false);
  const [openTaxTypeDialog, setOpenTaxTypeDialog] = useState(false);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Fetch both taxes and tax types
  useEffect(() => {
    const fetchTaxesAndTypes = async () => {
      try {
        const taxRes = await api.get("/tax");
        const taxTypesRes = await api.get("/taxtypes");
        setTaxes(taxRes.data);
        setTaxTypes(taxTypesRes.data);
      } catch (err) {
        setError("Error fetching data");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchTaxesAndTypes();
  }, []);

  const handleOpenTaxDialog = (tax = null) => {
    setCurrentTax(tax);
    setOpenTaxDialog(true);
  };

  const handleCloseTaxDialog = () => {
    setOpenTaxDialog(false);
    setCurrentTax(null);
  };

  const handleOpenTaxTypeDialog = (taxType = null) => {
    setCurrentTaxType(taxType);
    setOpenTaxTypeDialog(true);
  };

  const handleCloseTaxTypeDialog = () => {
    setOpenTaxTypeDialog(false);
    setCurrentTaxType(null);
  };

  const handleAddOrUpdateTax = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const tax = {
      name: formData.get("name"),
      tax_types: formData
        .get("tax_types")
        .split(",")
        .map((id) => parseInt(id, 10)),
    };

    try {
      if (currentTax) {
        const res = await api.put(`/tax/${currentTax.id}`, tax);
        setTaxes(taxes.map((t) => (t.id === currentTax.id ? res.data : t)));
      } else {
        const res = await api.post("/tax/new", tax);
        setTaxes([...taxes, res.data]);
      }
    } catch (err) {
      console.error(err);
    } finally {
      handleCloseTaxDialog();
    }
  };

  const handleAddOrUpdateTaxType = async (event) => {
    event.preventDefault();
    const formData = new FormData(event.currentTarget);
    const taxType = {
      name: formData.get("name"),
      deduction_percentage: parseFloat(formData.get("deduction_percentage")),
    };

    try {
      if (currentTaxType) {
        const res = await api.put(`/taxtypes/${currentTaxType.id}`, taxType);
        setTaxTypes(taxTypes.map((tt) => (tt.id === currentTaxType.id ? res.data : tt)));
      } else {
        const res = await api.post("/taxtypes/new", taxType);
        setTaxTypes([...taxTypes, res.data]);
      }
    } catch (err) {
      console.error(err);
    } finally {
      handleCloseTaxTypeDialog();
    }
  };

  const handleDeleteTax = async (id) => {
    try {
      await api.delete(`/tax/${id}`);
      setTaxes(taxes.filter((tax) => tax.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  const handleDeleteTaxType = async (id) => {
    try {
      await api.delete(`/taxtypes/${id}`);
      setTaxTypes(taxTypes.filter((taxType) => taxType.id !== id));
    } catch (err) {
      console.error(err);
    }
  };

  if (loading) return <p>Loading...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>Tax Information Dashboard</h1>
      <Button variant="contained" onClick={() => handleOpenTaxDialog()}>
        Add New Tax
      </Button>
      <TableContainer component={Paper} style={{ margin: "25px 0" }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Id</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Tax Types</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {taxes.map((tax) => (
              <TableRow key={tax.id}>
                <TableCell>{tax.id}</TableCell>
                <TableCell>{tax.name}</TableCell>
                <TableCell>{tax.tax_types?.map((t) => taxTypes.find((typ) => typ.id === t.tax_type_id)?.name).join(", ")}</TableCell>
                <TableCell>
                  <Button onClick={() => handleOpenTaxDialog(tax)}>Edit</Button>
                  <Button onClick={() => handleDeleteTax(tax.id)} style={{ marginLeft: "10px" }}>
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Button variant="contained" onClick={() => handleOpenTaxTypeDialog()} style={{ marginLeft: "10px" }}>
        Add New Tax Type
      </Button>
      <TableContainer component={Paper} style={{ margin: "25px 0" }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Id</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Deduction Percentage</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {taxTypes.map((taxType) => (
              <TableRow key={taxType.id}>
                <TableCell>{taxType.id}</TableCell>
                <TableCell>{taxType.name}</TableCell>
                <TableCell>{taxType.deduction_percentage}%</TableCell>
                <TableCell>
                  <Button onClick={() => handleOpenTaxTypeDialog(taxType)}>Edit</Button>
                  <Button onClick={() => handleDeleteTaxType(taxType.id)} style={{ marginLeft: "10px" }}>
                    Delete
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Tax Form Dialog */}
      <Dialog open={openTaxDialog} onClose={handleCloseTaxDialog}>
        <DialogTitle>{currentTax ? "Edit Tax" : "Add New Tax"}</DialogTitle>
        <form onSubmit={handleAddOrUpdateTax}>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              id="name"
              name="name"
              label="Tax Name"
              type="text"
              fullWidth
              variant="outlined"
              defaultValue={currentTax ? currentTax.name : ""}
            />
            <TextField
              margin="dense"
              id="tax_types"
              name="tax_types"
              label="Tax Type IDs (comma-separated)"
              type="text"
              fullWidth
              variant="outlined"
              defaultValue={currentTax && currentTax.tax_types ? currentTax.tax_types.map((t) => t.tax_type_id).join(",") : ""}
              helperText="Enter the IDs of the tax types associated with this tax."
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseTaxDialog}>Cancel</Button>
            <Button type="submit" color="primary">
              {currentTax ? "Update" : "Add"}
            </Button>
          </DialogActions>
        </form>
      </Dialog>

      {/* Tax Type Form Dialog */}
      <Dialog open={openTaxTypeDialog} onClose={handleCloseTaxTypeDialog}>
        <DialogTitle>{currentTaxType ? "Edit Tax Type" : "Add New Tax Type"}</DialogTitle>
        <form onSubmit={handleAddOrUpdateTaxType}>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              id="name"
              name="name"
              label="Tax Type Name"
              type="text"
              fullWidth
              variant="outlined"
              defaultValue={currentTaxType ? currentTaxType.name : ""}
            />
            <TextField
              margin="dense"
              id="deduction_percentage"
              name="deduction_percentage"
              label="Deduction Percentage"
              type="number"
              fullWidth
              variant="outlined"
              defaultValue={currentTaxType ? currentTaxType.deduction_percentage : ""}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCloseTaxTypeDialog}>Cancel</Button>
            <Button type="submit" color="primary">
              {currentTaxType ? "Update" : "Add"}
            </Button>
          </DialogActions>
        </form>
      </Dialog>
    </div>
  );
};

export default TaxDashboard;
