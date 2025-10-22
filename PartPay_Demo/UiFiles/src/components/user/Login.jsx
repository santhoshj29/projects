import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { api } from "../../services/axios";
import { Button, TextField, Typography, Link, Container, Box } from "@mui/material";

const Login = () => {
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

  const handleLogin = async (event) => {
    event.preventDefault();
    try {
      const response = await api.post("/login", {
        email,
        password,
        org_name: orgName,
      });
      if (response.status === 200) {
        const { token, role, org_id, employee_id } = response.data;
        localStorage.setItem("token", token);
        localStorage.setItem("role", role);
        localStorage.setItem("org_id", org_id);
        localStorage.setItem("employee_id", employee_id);
        navigate("/");
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
          Sign in
        </Typography>
        <Box component="form" onSubmit={handleLogin} noValidate sx={{ mt: 1 }}>
          <TextField
            margin="normal"
            required
            fullWidth
            id="email"
            label="Email Address"
            name="email"
            autoComplete="email"
            autoFocus
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
            autoComplete="current-password"
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
            Sign In
          </Button>
          <Typography variant="body2">
            Don&apos;t have an account yet?{" "}
            <Link href="#" variant="body2" onClick={() => navigate("/signup")}>
              Create one here
            </Link>
          </Typography>
        </Box>
      </Box>
    </Container>
  );
};

export default Login;
