import React, { useEffect, useState } from "react";
import api from "../services/api";
import Topbar from "../components/Topbar";

export default function AdminDashboard() {
  const [hospitals, setHospitals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchAll();
  }, []);

  async function fetchAll() {
    setLoading(true);
    setError(null);
    try {
      const res = await api.get("/admin/hospitals/all");
      setHospitals(res.data || []);
    } catch (e) {
      setError("Failed to load hospitals");
    } finally {
      setLoading(false);
    }
  }

  async function doAction(url) {
    try {
      await api.put(url);
      fetchAll();
    } catch (e) {
      alert("Action failed: " + (e.response?.data?.message || e.message));
    }
  }

  async function deleteHospital(id) {
    if (!window.confirm("Delete hospital permanently?")) return;
    try {
      await api.delete(`/admin/hospital/${id}`);
      fetchAll();
    } catch (e) {
      alert("Delete failed: " + (e.response?.data?.message || e.message));
    }
  }

  const pending = hospitals.filter(h => h.status === "PENDING");
  const approved = hospitals.filter(h => h.status === "APPROVED");
  const rejected = hospitals.filter(h => h.status === "REJECTED");

  return (
    <div className="app-shell">
      <Topbar title="Admin Dashboard" />

      <div className="page-header">
        <div>
          <h2 style={{ margin: 0 }}>Hospitals</h2>
          <div className="muted">Pending approvals shown first</div>
        </div>
      </div>

      {loading && <div className="spinner" />}

      {error && <div className="empty">{error}</div>}

      {!loading && !error && (
        <>
          {/* Pending */}
          <div className="section">Pending</div>
          {pending.length === 0 ? (
            <div className="empty">No pending hospitals</div>
          ) : (
            <div className="grid">
              {pending.map(h => (
                <div className="card" key={h.hospitalId}>
                  <h3>{h.hospitalName}</h3>
                  <div className="sub">AuthorizedPerson: {h.authorizedPerson}</div>
                  <div className="sub">Email: {h.email}</div>
                  <div className="sub">License: {h.licenseNumber}</div>
                  
                  <div className="action-row">
                    <button
                      className="btn btn-approve"
                      onClick={() =>
                        doAction(`/admin/hospital/${h.hospitalId}/approve`)
                      }
                    >
                      Approve
                    </button>

                    <button
                      className="btn btn-reject"
                      onClick={() =>
                        doAction(`/admin/hospital/${h.hospitalId}/reject`)
                      }
                    >
                      Reject
                    </button>

                    <button
                      className="btn btn-delete"
                      onClick={() => deleteHospital(h.hospitalId)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Approved */}
          <div className="section">Approved</div>
          {approved.length === 0 ? (
            <div className="empty">No approved hospitals</div>
          ) : (
            <div className="grid">
              {approved.map(h => (
                <div className="card" key={h.hospitalId}>
                  <h3>{h.hospitalName}</h3>
                  <div className="sub">Email: {h.email}</div>
                  <div className="sub">Status: {h.status}</div>

                  <div className="action-row">
                    <button
                      className="btn btn-delete"
                      onClick={() => deleteHospital(h.hospitalId)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Rejected */}
          <div className="section">Rejected</div>
          {rejected.length === 0 ? (
            <div className="empty">No rejected hospitals</div>
          ) : (
            <div className="grid">
              {rejected.map(h => (
                <div className="card" key={h.hospitalId}>
                  <h3>{h.hospitalName}</h3>
                  <div className="sub">Email: {h.email}</div>

                  <div className="action-row">
                    <button
                      className="btn btn-approve"
                      onClick={() =>
                        doAction(`/admin/hospital/${h.hospitalId}/approve`)
                      }
                    >
                      Approve
                    </button>

                    <button
                      className="btn btn-delete"
                      onClick={() => deleteHospital(h.hospitalId)}
                    >
                      Delete
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}