import React, { useEffect, useState } from "react";
import api from "../services/api";
import Topbar from "../components/Topbar";
import { useNavigate } from "react-router-dom";

export default function HospitalDashboard() {
  const [pending, setPending] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => { fetchPending(); }, []);

  async function fetchPending() {
    setLoading(true);
    try {
      const res = await api.get("/hospital/users/pending");
      setPending(res.data || []);
    } catch (e) {
      alert("Failed to load pending users: " + (e.response?.data?.message || e.message));
    } finally {
      setLoading(false);
    }
  }

  async function action(userId, mode) {
    try {
      await api.put(`/hospital/user/${userId}/${mode}`);
      fetchPending();
    } catch (e) {
      alert(mode + " failed: " + (e.response?.data?.message || e.message));
    }
  }

  return (
    <div className="app-shell">
      <Topbar title="Hospital Dashboard" />
      <div className="page-header">
        <h2>Pending Users</h2>
        <div className="muted">Approve or reject registrations</div>
      </div>

      {loading && <div className="spinner" />}
      {!loading && pending.length === 0 && <div className="empty">No pending users</div>}

      {pending.length > 0 && (
        <div className="grid">
          {pending.map(u => (
            <div className="card" key={u.userId}>
              <h3>{u.fullName}</h3>
              <div className="sub">Email: {u.email}</div>
              <div className="sub">Blood Group: {u.bloodGroup}</div>
              <div className="sub">Role Type: {u.userRoleType}</div>
              <div className="sub">National ID: {u.nationalId}</div>
              <div className="sub">Age: {u.age}</div>
              {u.organsToDonate && <div className="sub">Organ(s) to Donate: {u.organsToDonate}</div>}
              {u.organsToReceive && <div className="sub">Organ(s) to Receive: {u.organsToReceive}</div>}
              <div className="action-row">
                <button className="btn btn-approve" onClick={() => action(u.userId, "approve")}>Approve</button>
                <button className="btn btn-reject" onClick={() => action(u.userId, "reject")}>Reject</button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
