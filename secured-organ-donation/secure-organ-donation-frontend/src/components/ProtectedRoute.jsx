


import { Navigate } from "react-router-dom";

function ProtectedRoute({ children, allowedRole }) {

  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");

  if (!token) {
    return <Navigate to={`/${allowedRole.toLowerCase()}/login`} />;
  }

  if (role !== allowedRole) {
    return <Navigate to={`/${role?.toLowerCase() }/login`} />;
  }

  return children;
}

export default ProtectedRoute;