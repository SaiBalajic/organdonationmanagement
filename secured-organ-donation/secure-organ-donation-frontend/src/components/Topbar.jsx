import React from "react";
import NotificationBell from "./NotificationBell";
import { useNavigate } from "react-router-dom";

export default function Topbar({ title }) {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.clear(); // or sessionStorage if you're using that
    window.location.reload();
  };

  return (
    <div className="topbar">
      <div className="brand">
        <div className="logo">SD</div>
        <div>
          <h1>{title}</h1>
          <div className="muted" style={{fontSize:12}}>Secure Organ Donation - Admin Console</div>
        </div>
      </div>

      <div className="topbar-actions">
        <NotificationBell />
        <button className="logout-btn" onClick={handleLogout}>Logout</button>
      </div>
    </div>
  );
}