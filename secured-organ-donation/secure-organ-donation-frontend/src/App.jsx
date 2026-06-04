import { BrowserRouter, Routes, Route } from "react-router-dom";
import AdminDashboard from "./pages/AdminDashboard";
import HospitalDashboard from "./pages/HospitalDashboard";
import UserDashboard from "./pages/UserDashboard";
import AdminLogin from "./pages/AdminLogin";
import HospitalLogin from "./pages/HospitalLogin";
import UserLogin from "./pages/UserLogin";
import ProtectedRoute from "./components/ProtectedRoute";
import HospitalRegister from "./pages/HospitalRegister";
import UserRegister from "./pages/UserRegister";
import Home from "./pages/Home";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* LOGIN ROUTES */}
        <Route path="/" element={<Home />} />
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/hospital/login" element={<HospitalLogin />} />
        <Route path="/user/login" element={<UserLogin />} />
        <Route path="/hospital/register" element={<HospitalRegister />} />
        <Route path="/user/register" element={<UserRegister />} />


        {/* ADMIN */}
        <Route
          path="/admin"
          element={
            <ProtectedRoute allowedRole="ADMIN">
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        {/* HOSPITAL */}
        <Route
          path="/hospital"
          element={
            <ProtectedRoute allowedRole="HOSPITAL">
              <HospitalDashboard />
            </ProtectedRoute>
          }
        />

        {/* USER */}
        <Route
          path="/user"
          element={
            <ProtectedRoute allowedRole="USER">
              <UserDashboard />
            </ProtectedRoute>
          }
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;