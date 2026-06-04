/* import { useEffect, useState } from "react";
import api from "../services/api";

function NotificationBell() {
  const [unread, setUnread] = useState([]);
  const [all, setAll] = useState([]);
  const [open, setOpen] = useState(false);

  const fetchUnread = async () => {
    try {
      const res = await api.get("/notifications/unread");
      setUnread(res.data);
    } catch (err) {
      console.error("Fetch unread failed", err);
    }
  };

  const fetchAll = async () => {
    try {
      const res = await api.get("/notifications");
      setAll(res.data);
    } catch (err) {
      console.error("Fetch all failed", err);
    }
  };

  useEffect(() => {
    fetchUnread();
    // poll every 15s
    const t = setInterval(fetchUnread, 15000);
    return () => clearInterval(t);
  }, []);

  const markRead = async (id) => {
    try {
      await api.put(`/notifications/${id}/read`);
      fetchUnread();
      fetchAll();
    } catch (err) {
      console.error("Mark read failed", err);
      alert("Could not mark notification read");
    }
  };

  return (
    <div style={{ position: "relative" }}>
      <button onClick={() => { setOpen(!open); if (!open) fetchAll(); }}>
        🔔 {unread.length > 0 && <span>({unread.length})</span>}
      </button>

      {open && (
        <div style={{ position: "absolute", right: 0, width: 320, background: "#fff", border: "1px solid #ddd", zIndex: 999 }}>
          <h4 style={{ margin: 8 }}>Notifications</h4>
          {all.length === 0 && <p style={{ margin: 8 }}>No notifications</p>}
          {all.map(n => (
            <div key={n.notificationId} style={{ padding: 8, borderBottom: "1px solid #eee", background: n.isRead ? "#fff" : "#f9f9f9" }}>
              <div style={{ fontSize: 13 }}>{n.message}</div>
              <div style={{ fontSize: 11, color: "#666" }}>{new Date(n.createdAt).toLocaleString()}</div>
              {!n.isRead && <button onClick={() => markRead(n.notificationId)}>Mark read</button>}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default NotificationBell; */

import React, { useEffect, useState, useRef } from "react";
import api from "../services/api";

export default function NotificationBell() {
  const [count, setCount] = useState(0);
  const [open, setOpen] = useState(false);
  const [items, setItems] = useState([]);
  const ref = useRef();

  useEffect(() => {
    fetchCount();
    // optional: poll every 30s
    // const t = setInterval(fetchCount, 30000);
    // return () => clearInterval(t);
  }, []);

  async function fetchCount() {
    try {
      const res = await api.get("/notifications/unread/count");
      setCount(res.data || 0);
    } catch (e) {
      // silent
    }
  }

  async function openDropdown() {
    if (!open) {
      try {
        const res = await api.get("/notifications");
        setItems(res.data || []);
      } catch (e) {
        setItems([]);
      }
    }
    setOpen(!open);
  }

  // close on outside click
  useEffect(() => {
    function onDoc(e){
      if (ref.current && !ref.current.contains(e.target)) setOpen(false);
    }
    document.addEventListener("click", onDoc);
    return () => document.removeEventListener("click", onDoc);
  }, []);

  return (
    <div style={{position:"relative"}} ref={ref}>
      <div className="bell" onClick={openDropdown} aria-label="Notifications">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none"><path fill="#fff" d="M12 22a2 2 0 0 0 2-2H10a2 2 0 0 0 2 2zM18.7 16V11c0-3.3-2.1-6-5-6V4a2 2 0 10-4 0v1c-2.9 0-5 2.7-5 6v5l-2 2v1h18v-1l-2-2z"/></svg>
        <div className="dot" style={{marginLeft:6}}>{count}</div>
      </div>

      {open && (
        <div className="dropdown" role="menu">
          {items.length === 0 && <div className="item"><div className="title">No notifications</div></div>}
          {items.map(n => (
            <div key={n.notificationId} className="item">
              <div className="title">{n.message}</div>
              <div className="meta">{n.receiverRole} • {new Date(n.createdAt).toLocaleString()}</div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}