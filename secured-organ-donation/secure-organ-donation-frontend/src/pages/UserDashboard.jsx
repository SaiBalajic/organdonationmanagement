
import React, { useEffect, useState } from "react";
import api from "../services/api";
import NotificationBell from "../components/NotificationBell";
import { useNavigate } from "react-router-dom";

export default function UserDashboard() {

  const [matches,setMatches] = useState([]);
  const [loading,setLoading] = useState(true);

  const navigate = useNavigate();

  function getCurrentUserId() {
  try {
    const token = localStorage.getItem("token");
    if (!token) return null;

      const payload = JSON.parse(atob(token.split(".")[1]));
      return payload.userId || payload.sub || payload.uid || null;
    } catch {
      return null;
    }
  }

  const currentUserId = getCurrentUserId();

  useEffect(()=>{
    loadMatches();
  },[]);

  async function loadMatches(){

    try{

      const res = await api.get("/matches/my");

      setMatches(res.data || []);

    }catch(err){

      console.error(err);

    }

    setLoading(false);

  }

  async function acceptMatch(matchId){

    const confirm = window.confirm("Accept this organ donation?");
    if(!confirm) return;

    try{

      await api.put(`/matches/${matchId}/accept`);

      alert("Donation accepted");

      loadMatches();

    }catch(err){

      alert(err.response?.data?.message || "Error accepting");

    }

  }

  function handleLogout(){

    localStorage.clear();

    navigate("/user/login");

  }

  return(

    <div className="app-shell">

      <div className="topbar-row">

        <h1>User Dashboard</h1>

        <div style={{display:"flex",gap:12,alignItems:"center"}}>

          <NotificationBell/>

          <button className="btn small" onClick={handleLogout}>
            Logout
          </button>

        </div>

      </div>

      <div className="card section">

        <h2>Organ Matches</h2>

        {loading && (
          <div className="empty">Loading...</div>
        )}

        {!loading && matches.length === 0 && (
          <div className="empty">No matches found</div>
        )}

        {!loading && matches.length > 0 && (

          <div className="grid">

            {matches.map(m => (

              <div className="card match-card" key={m.matchId}>

                <h3>Organ: {m.organ}</h3>

                <div className="sub">
                  Donor: {m.donor?.fullName}
                </div>

                <div className="sub">
                  Recipient: {m.recipient?.fullName}
                </div>

                <div className="sub">
                  Status: {m.status}
                </div>


                {/* ACCEPT BUTTON */}

                {m.status === "PENDING" && m.recipient?.userId === currentUserId && (
                  <button className="btn btn-approve" onClick={()=>acceptMatch(m.matchId)}>
                  Accept Donation
                  </button>
                )}


                {/* ACCEPTED MESSAGE */}

                {m.status === "ACCEPTED" && (

                  <div className="accepted">
                    Your donation has been accepted
                  </div>

                )}


                {/* REJECTED MESSAGE */}

                {m.status === "REJECTED" && (

                  <div className="muted">
                    Another recipient accepted
                  </div>

                )}

              </div>

            ))}

          </div>

        )}

      </div>

    </div>

  );

}