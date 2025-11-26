import { useEffect, useState } from "react";
import { api } from "../../services/axios";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import Typography from "@mui/material/Typography";
import Grid from "@mui/material/Grid";
import TextField from "@mui/material/TextField";
import Button from "@mui/material/Button";
import MailOutlineIcon from "@mui/icons-material/MailOutline";
import HomeIcon from "@mui/icons-material/Home";
import PhoneIcon from "@mui/icons-material/Phone";
import AccountBalanceIcon from "@mui/icons-material/AccountBalance";
import EditIcon from "@mui/icons-material/Edit";
import SaveIcon from "@mui/icons-material/Save";
import MonetizationOnIcon from "@mui/icons-material/MonetizationOn";
import PlaceIcon from "@mui/icons-material/Place";
import CancelIcon from "@mui/icons-material/Cancel";
import PersonIcon from "@mui/icons-material/Person";

const Profile = () => {
  const [userDetails, setUserDetails] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [editData, setEditData] = useState({ name: "", address: "", phone: "", account_number: "", routing_number: "" });

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await api.get("/profile");
        setUserDetails(response.data);
        setEditData({
          name: response.data.name,
          address: response.data.address,
          phone: response.data.phone,
          details: {
            account_number: response.data.details?.account_number || "",
            routing_number: response.data.details?.routing_number || "",
          },
        });
      } catch (error) {
        console.error("Error fetching user:", error);
      }
    };

    fetchItems();
  }, []);

  const handleEditChange = (field) => (event) => {
    if (field.includes(".")) {
      const [nestedField, subField] = field.split(".");
      setEditData({
        ...editData,
        [nestedField]: {
          ...editData[nestedField],
          [subField]: event.target.value,
        },
      });
    } else {
      setEditData({
        ...editData,
        [field]: event.target.value,
      });
    }
  };

  const saveEdits = async () => {
    try {
      await api.put("/profile", {
        ...editData,
      });
    } catch (error) {
      console.log(error);
    }
    setEditMode(false);
  };

  return (
    <div style={{ display: "flex", justifyContent: "center", marginTop: "50px" }}>
      <Card sx={{ maxWidth: 600 }}>
        <CardContent>
          <Typography gutterBottom variant="h4" component="div">
            Profile
          </Typography>
          {userDetails ? (
            <Grid container spacing={3}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Name"
                  variant="outlined"
                  disabled={!editMode}
                  value={editData.name}
                  onChange={handleEditChange("name")}
                  InputProps={{
                    startAdornment: <HomeIcon style={{ marginRight: "10px" }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12} style={{ display: "flex", alignItems: "center" }}>
                <TextField
                  fullWidth
                  label="Email"
                  variant="outlined"
                  disabled={true}
                  value={userDetails.email}
                  InputProps={{
                    startAdornment: <MailOutlineIcon style={{ marginRight: "10px" }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12} style={{ display: "flex", alignItems: "center" }}>
                <TextField
                  fullWidth
                  label="Role"
                  variant="outlined"
                  disabled={true}
                  value={userDetails.role}
                  InputProps={{
                    startAdornment: <PersonIcon style={{ marginRight: "10px" }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Address"
                  variant="outlined"
                  disabled={!editMode}
                  value={editData.address}
                  onChange={handleEditChange("address")}
                  InputProps={{
                    startAdornment: <PlaceIcon style={{ marginRight: "10px" }} />,
                  }}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="Phone"
                  variant="outlined"
                  disabled={!editMode}
                  value={editData.phone}
                  onChange={handleEditChange("phone")}
                  InputProps={{
                    startAdornment: <PhoneIcon style={{ marginRight: "10px" }} />,
                  }}
                />
              </Grid>
              {userDetails.role === "ptemployee" && (
                <>
                  <Grid item xs={12} style={{ display: "flex", alignItems: "center" }}>
                    <TextField
                      fullWidth
                      label="Pay Per Hour"
                      variant="outlined"
                      disabled={true}
                      value={userDetails.details.pay_per_hour}
                      InputProps={{
                        startAdornment: <MonetizationOnIcon style={{ marginRight: "10px" }} />,
                      }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Account Number"
                      variant="outlined"
                      disabled={!editMode}
                      value={editData.details.account_number}
                      onChange={handleEditChange("details.account_number")}
                      InputProps={{
                        startAdornment: <AccountBalanceIcon style={{ marginRight: "10px" }} />,
                      }}
                    />
                  </Grid>
                  <Grid item xs={12}>
                    <TextField
                      fullWidth
                      label="Routing Number"
                      variant="outlined"
                      disabled={!editMode}
                      value={editData.details.routing_number}
                      onChange={handleEditChange("details.routing_number")}
                      InputProps={{
                        startAdornment: <AccountBalanceIcon style={{ marginRight: "10px" }} />,
                      }}
                    />
                  </Grid>
                </>
              )}
              <Grid item xs={12}>
                <Button variant="outlined" startIcon={editMode ? <CancelIcon /> : <EditIcon />} onClick={() => setEditMode(!editMode)}>
                  {editMode ? "Cancel" : "Edit"}
                </Button>
                {editMode && (
                  <Button style={{ marginLeft: "10px" }} variant="contained" color="primary" startIcon={<SaveIcon />} onClick={saveEdits}>
                    Save
                  </Button>
                )}
              </Grid>
            </Grid>
          ) : (
            <Typography variant="body1" color="text.secondary">
              Loading user details...
            </Typography>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default Profile;
