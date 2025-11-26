import { useState, useRef } from "react";
import { useNavigate, useLocation, Outlet } from "react-router-dom";
import Box from "@mui/material/Box";
import Drawer from "@mui/material/Drawer";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import AccountCircleIcon from "@mui/icons-material/AccountCircle";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import Button from "@mui/material/Button";
import SupervisorAccountIcon from "@mui/icons-material/SupervisorAccount";
import ScheduleIcon from "@mui/icons-material/Schedule";
import SwapHorizIcon from "@mui/icons-material/SwapHoriz";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday";
import MoreTimeIcon from "@mui/icons-material/MoreTime";
import AccountBalanceIcon from "@mui/icons-material/AccountBalance";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import BarChartIcon from "@mui/icons-material/BarChart";

const drawerWidth = 240;

const Layout = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);
  const closeTimeoutRef = useRef();
  const role = localStorage.getItem("role");

  const handleMouseEnter = () => {
    if (closeTimeoutRef.current) {
      clearTimeout(closeTimeoutRef.current);
    }
    setMobileOpen(true);
  };

  const handleMouseLeave = () => {
    closeTimeoutRef.current = setTimeout(() => {
      setMobileOpen(false);
    }, 200);
  };

  const handleNavigation = (path) => {
    setMobileOpen(false);
    navigate(path);
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    handleNavigation("/login");
  };

  const menuItems = [
    { text: "Profile", icon: <AccountCircleIcon />, path: "/" },
    { text: "Manage Profiles", icon: <SupervisorAccountIcon />, path: "/manage", roles: ["admin", "manager"] },
    { text: "Timesheet", icon: <SupervisorAccountIcon />, path: "/timesheet", roles: ["ptemployee"] },
    { text: "Schedules", icon: <ScheduleIcon />, path: "/schedules", roles: ["scheduler", "manager", "ptemployee"] },
    { text: "Swap Shifts", icon: <SwapHorizIcon />, path: "/swap", roles: ["manager", "ptemployee"] },
    { text: "Leaves", icon: <CalendarTodayIcon />, path: "/leave", roles: ["manager", "ptemployee"] },
    { text: "Overtime", icon: <MoreTimeIcon />, path: "/overtime", roles: ["manager", "ptemployee"] },
    { text: "Payroll System configuration", icon: <AccountBalanceIcon />, path: "/tax", roles: ["admin"] },
    { text: "Payslip Generate", icon: <ReceiptLongIcon />, path: "/generate-payslips", roles: ["admin"] },
    {
      text: role === "admin" ? "Reports" : "Pay Slips",
      icon: role === "admin" ? <BarChartIcon /> : <ReceiptLongIcon />,
      path: "/reports",
      roles: ["admin", "ptemployee"],
    },
  ].filter((item) => !item.roles || item.roles.includes(role));

  return (
    <Box sx={{ display: "flex" }}>
      <Box onMouseEnter={handleMouseEnter} sx={{ zIndex: 1300, width: "30px", height: "100%", position: "fixed" }} />
      <Drawer
        variant="temporary"
        open={mobileOpen}
        onMouseEnter={handleMouseEnter}
        ModalProps={{
          keepMounted: true,
        }}
        sx={{
          display: { xs: "block", sm: "block", zIndex: "13001" },
          "& .MuiDrawer-paper": { boxSizing: "border-box", width: drawerWidth },
        }}
      >
        <List onMouseLeave={handleMouseLeave} style={{ height: "100%" }}>
          {menuItems.map((item, index) => (
            <Box key={index} sx={{ display: "flex", alignItems: "center", gap: 1, padding: 1 }}>
              <Button
                variant="contained"
                color={location.pathname === item.path ? "primary" : "secondary"}
                startIcon={item.icon}
                onClick={() => handleNavigation(item.path)}
                sx={{
                  justifyContent: "flex-start",
                  width: "100%",
                  backgroundColor: location.pathname === item.path ? undefined : "#ffffff",
                  color: location.pathname === item.path ? undefined : "rgba(0, 0, 0, 0.87)",
                  "&:hover": {
                    backgroundColor: location.pathname === item.path ? undefined : "rgba(0, 0, 0, 0.04)",
                  },
                }}
              >
                {item.text}
              </Button>
            </Box>
          ))}
          <ListItem disablePadding>
            <Button
              variant="contained"
              color="error"
              startIcon={<ExitToAppIcon />}
              onClick={handleLogout}
              sx={{
                margin: 1,
                width: "calc(100% - 8px)",
              }}
            >
              Logout
            </Button>
          </ListItem>
        </List>
      </Drawer>
      <Box component="main" sx={{ flexGrow: 1, bgcolor: "background.default", p: 3, height: "100vh" }}>
        <Outlet />
      </Box>
    </Box>
  );
};

export default Layout;
