// src/pages/HospitalRegister.jsx
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";
import "../styles/auth.css";

function HospitalRegister() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    hospitalName: "",
    licenseNumber: "",
    hospitalType: "",
    address: "",
    email: "",
    contactNumber: "",
    authorizedPerson: "",
    password: "",
  });

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const e = {};

    if (!form.hospitalName.trim()) e.hospitalName = "Hospital name is required";
    if (!form.licenseNumber.trim()) e.licenseNumber = "License number is required";
    if (!form.hospitalType.trim()) e.hospitalType = "Hospital type is required";
    if (!form.address.trim()) e.address = "Address is required";

    if (!form.email.trim()) e.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) e.email = "Invalid email";

    if (!form.contactNumber.trim()) e.contactNumber = "Contact number is required";
    else if (!/^\d{7,15}$/.test(form.contactNumber)) e.contactNumber = "Contact number should be 7-15 digits";

    if (!form.authorizedPerson.trim()) e.authorizedPerson = "Authorized person is required";

    if (!form.password) e.password = "Password is required";
    else if (form.password.length < 8) e.password = "Password must be at least 8 characters";

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((s) => ({ ...s, [name]: value }));
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    setSubmitting(true);
    try {
      const payload = {
        hospitalName: form.hospitalName.trim(),
        licenseNumber: form.licenseNumber.trim(),
        hospitalType: form.hospitalType.trim(),
        address: form.address.trim(),
        email: form.email.trim(),
        contactNumber: form.contactNumber.trim(),
        authorizedPerson: form.authorizedPerson.trim(),
        password: form.password,
      };

      await api.post("/hospital/register", payload);

      alert("Registration request submitted. Wait for admin approval.");
      navigate("/hospital/login");
    } catch (err) {
      console.error(err);
      const msg = err?.response?.data?.message || "Registration failed";
      alert(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <div className="auth-card" style={{ maxWidth: 720 }}>
        <div className="auth-title">Hospital Registration</div>
        <div className="auth-sub">Register your hospital to enable verification & approvals</div>

        <form onSubmit={handleSubmit} noValidate>
          <div className="input-group">
            <label>Hospital Name</label>
            <input className="auth-input" name="hospitalName" value={form.hospitalName} onChange={handleChange} />
            {errors.hospitalName && <div className="error">{errors.hospitalName}</div>}
          </div>

          <div className="input-group">
            <label>License Number</label>
            <input className="auth-input" name="licenseNumber" value={form.licenseNumber} onChange={handleChange} />
            {errors.licenseNumber && <div className="error">{errors.licenseNumber}</div>}
          </div>

          <div className="input-group">
            <label>Hospital Type</label>
            <select className="auth-select" name="hospitalType" value={form.hospitalType} onChange={handleChange}>
              <option value="">Select</option>
              <option value="GOVT">Government</option>
              <option value="PRIVATE">Private</option>
              <option value="CHARITY">Charity</option>
              <option value="OTHER">Other</option>
            </select>
            {errors.hospitalType && <div className="error">{errors.hospitalType}</div>}
          </div>

          <div className="input-group">
            <label>Address</label>
            <textarea
              className="auth-input"
              name="address"
              value={form.address}
              onChange={handleChange}
              rows={3}
              style={{ resize: "vertical" }}
            />
            {errors.address && <div className="error">{errors.address}</div>}
          </div>

          <div className="input-group">
            <label>Email</label>
            <input className="auth-input" name="email" value={form.email} onChange={handleChange} type="email" />
            {errors.email && <div className="error">{errors.email}</div>}
          </div>

          <div className="input-group">
            <label>Contact Number</label>
            <input className="auth-input" name="contactNumber" value={form.contactNumber} onChange={handleChange} />
            {errors.contactNumber && <div className="error">{errors.contactNumber}</div>}
          </div>

          <div className="input-group">
            <label>Authorized Person</label>
            <input className="auth-input" name="authorizedPerson" value={form.authorizedPerson} onChange={handleChange} />
            {errors.authorizedPerson && <div className="error">{errors.authorizedPerson}</div>}
          </div>

          <div className="input-group">
            <label>Password</label>
            <input className="auth-input" name="password" type="password" value={form.password} onChange={handleChange} />
            {errors.password && <div className="error">{errors.password}</div>}
          </div>

          <div className="input-group">
            <button className="auth-button" type="submit" disabled={submitting}>
              {submitting ? "Submitting..." : "Register Hospital"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default HospitalRegister;