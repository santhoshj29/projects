import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../../services/axios";
import { Button, TextField, Typography, Link, Container, Box } from "@mui/material";

const Signup = () => {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [orgName, setOrgName] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      navigate("/");
    }
  });

  const handleSignup = async (event) => {
    event.preventDefault();
    try {
      const response = await api.post("/signup", {
        name,
        email,
        password,
        org_name: orgName,
      });
      if (response.status === 201) {
        navigate("/login");
      }
    } catch (error) {
      console.log(error);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Typography component="h1" variant="h5">
          Sign up
        </Typography>
        <Box component="form" onSubmit={handleSignup} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="name"
            label="Full Name"
            name="name"
            autoComplete="name"
            autoFocus
            value={name}
            onChange={(e) => setName(e.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Email Address"
            name="email"
            autoComplete="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="password"
            label="Password"
            type="password"
            id="password"
            autoComplete="new-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <TextField
            margin="normal"
            required
            fullWidth
            name="orgName"
            label="Organization Name"
            type="text"
            id="orgName"
            autoComplete="organization"
            value={orgName}
            onChange={(e) => setOrgName(e.target.value)}
          />
          <Button type="submit" fullWidth variant="contained" sx={{ mt: 3, mb: 2 }}>
            Sign Up
          </Button>
          <Typography variant="body2">
            Already have an account?{" "}
            <Link href="#" variant="body2" onClick={() => navigate("/login")}>
              Sign in here
            </Link>
          </Typography>
        </Box>
      </Box>
    </Container>
  );
};

export default Signup;
