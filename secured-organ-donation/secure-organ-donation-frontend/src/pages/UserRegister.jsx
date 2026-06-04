// src/pages/UserRegister.jsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../services/api";

function UserRegister() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    fullName: "",
    dob: "",
    gender: "",
    bloodGroup: "",
    nationalId: "",
    userRoleType: "DONOR", // DONOR | RECIPIENT | BOTH
    email: "",
    phone: "",
    organsToDonate: "", // comma separated
    emergencyContact: "",
    password: "",
    consent: false,
    hospitalLicenseNumber: "",
  });

  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  // Try to fetch approved hospitals (if backend exposes such endpoint).
  const [hospitalsSuggestion, setHospitalsSuggestion] = useState([]);
  useEffect(() => {
    (async () => {
      try {
        const res = await api.get("/hospitals/approved");
        setHospitalsSuggestion(res.data || []);
      } catch (_) {
        setHospitalsSuggestion([]);
      }
    })();
  }, []);

  const validate = () => {
    const e = {};

    if (!form.fullName.trim()) e.fullName = "Full name is required";
    if (!form.dob) e.dob = "Date of birth is required";
    if (!form.gender) e.gender = "Gender is required";
    if (!form.bloodGroup) e.bloodGroup = "Blood group is required";
    if (!form.nationalId.trim()) e.nationalId = "National ID is required";

    if (!["DONOR", "RECIPIENT", "BOTH"].includes(form.userRoleType))
      e.userRoleType = "Choose a valid role type";

    if (!form.email.trim()) e.email = "Email is required";
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) e.email = "Invalid email";

    if (!form.phone.trim()) e.phone = "Phone is required";
    else if (!/^\d{7,15}$/.test(form.phone)) e.phone = "Phone should be 7-15 digits";

    if (form.userRoleType === "DONOR" || form.userRoleType === "BOTH") {
      if (!form.organsToDonate.trim())
        e.organsToDonate = "Please specify organs to donate (comma separated)";
    }

    if (!form.password) e.password = "Password is required";
    else if (form.password.length < 8) e.password = "Password must be at least 8 characters";

    if (!form.consent) e.consent = "Consent is required";

    if (!form.hospitalLicenseNumber.trim()) e.hospitalLicenseNumber = "Hospital (license) is required";

    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setForm((s) => ({ ...s, [name]: type === "checkbox" ? checked : value }));
  };

  const handleSubmit = async (ev) => {
    ev.preventDefault();
    if (!validate()) return;

    setSubmitting(true);

    try {
      const payload = {
        fullName: form.fullName.trim(),
        dob: form.dob,
        gender: form.gender,
        bloodGroup: form.bloodGroup,
        nationalId: form.nationalId.trim(),
        userRoleType: form.userRoleType,
        email: form.email.trim(),
        phone: form.phone.trim(),
        organsToDonate: form.organsToDonate.trim(),
        emergencyContact: form.emergencyContact.trim(),
        password: form.password,
        consent: form.consent,
        hospitalLicenseNumber: form.hospitalLicenseNumber.trim(),
      };

      await api.post("/user/register", payload);

      alert("Registration request sent. Hospital will verify and admin may approve.");
      navigate("/user/login");
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
      <div className="auth-card" role="region" aria-label="User registration">
        <div className="auth-title">User Registration</div>
        <div className="auth-sub">Register to Donate or Receive</div>

        <form onSubmit={handleSubmit} noValidate>
          <div className="input-group">
            <label htmlFor="fullName">Full Name</label>
            <input
              id="fullName"
              name="fullName"
              className="auth-input"
              value={form.fullName}
              onChange={handleChange}
            />
            {errors.fullName && <div className="error">{errors.fullName}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="dob">Date of Birth</label>
            <input
              id="dob"
              name="dob"
              type="date"
              className="auth-input"
              value={form.dob}
              onChange={handleChange}
            />
            {errors.dob && <div className="error">{errors.dob}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="gender">Gender</label>
            <select
              id="gender"
              name="gender"
              className="auth-select"
              value={form.gender}
              onChange={handleChange}
            >
              <option value="">-- select --</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
              <option value="OTHER">Other</option>
            </select>
            {errors.gender && <div className="error">{errors.gender}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="bloodGroup">Blood Group</label>
            <select
              id="bloodGroup"
              name="bloodGroup"
              className="auth-select"
              value={form.bloodGroup}
              onChange={handleChange}
            >
              <option value="">-- select --</option>
              <option value="A+">A+</option>
              <option value="A-">A-</option>
              <option value="B+">B+</option>
              <option value="B-">B-</option>
              <option value="O+">O+</option>
              <option value="O-">O-</option>
              <option value="AB+">AB+</option>
              <option value="AB-">AB-</option>
            </select>
            {errors.bloodGroup && <div className="error">{errors.bloodGroup}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="nationalId">National ID</label>
            <input
              id="nationalId"
              name="nationalId"
              className="auth-input"
              value={form.nationalId}
              onChange={handleChange}
            />
            {errors.nationalId && <div className="error">{errors.nationalId}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="userRoleType">Role</label>
            <select
              id="userRoleType"
              name="userRoleType"
              className="auth-select"
              value={form.userRoleType}
              onChange={handleChange}
            >
              <option value="DONOR">Donor</option>
              <option value="RECIPIENT">Recipient</option>
              <option value="BOTH">Both</option>
            </select>
            {errors.userRoleType && <div className="error">{errors.userRoleType}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              name="email"
              type="email"
              className="auth-input"
              value={form.email}
              onChange={handleChange}
            />
            {errors.email && <div className="error">{errors.email}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="phone">Phone</label>
            <input
              id="phone"
              name="phone"
              className="auth-input"
              value={form.phone}
              onChange={handleChange}
            />
            {errors.phone && <div className="error">{errors.phone}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="organsToDonate">Organs to Donate (comma separated)</label>
            <input
              id="organsToDonate"
              name="organsToDonate"
              className="auth-input"
              value={form.organsToDonate}
              onChange={handleChange}
              placeholder="kidney,liver,heart"
            />
            {errors.organsToDonate && <div className="error">{errors.organsToDonate}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="emergencyContact">Emergency Contact</label>
            <input
              id="emergencyContact"
              name="emergencyContact"
              className="auth-input"
              value={form.emergencyContact}
              onChange={handleChange}
            />
          </div>

          <div className="input-group">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              name="password"
              type="password"
              className="auth-input"
              value={form.password}
              onChange={handleChange}
            />
            {errors.password && <div className="error">{errors.password}</div>}
          </div>

          <div className="input-group">
            <label htmlFor="hospitalLicenseNumber">Hospital License Number</label>
            <input
              id="hospitalLicenseNumber"
              name="hospitalLicenseNumber"
              className="auth-input"
              value={form.hospitalLicenseNumber}
              onChange={handleChange}
              placeholder="Type hospital license number"
            />

            {hospitalsSuggestion.length > 0 && (
              <div className="hospital-suggestions">
                <small>Or pick a hospital:</small>
                <div className="suggestions-list">
                  {hospitalsSuggestion.map((h) => (
                    <button
                      type="button"
                      key={h.licenseNumber || h.hospitalId}
                      onClick={() => setForm((s) => ({ ...s, hospitalLicenseNumber: h.licenseNumber }))}
                      className="suggestion-btn"
                    >
                      {h.hospitalName} ({h.licenseNumber})
                    </button>
                  ))}
                </div>
              </div>
            )}

            {errors.hospitalLicenseNumber && <div className="error">{errors.hospitalLicenseNumber}</div>}
          </div>

          <div className="input-group checkbox-group">
            <label className="checkbox-label">
              <input
                name="consent"
                type="checkbox"
                checked={form.consent}
                onChange={handleChange}
              />
              <span style={{ marginLeft: 8 }}>I consent to organ donation data being used for matching</span>
            </label>
            {errors.consent && <div className="error">{errors.consent}</div>}
          </div>

          <div style={{ marginTop: 8 }}>
            <button type="submit" className="auth-button" disabled={submitting}>
              {submitting ? "Submitting..." : "Register"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default UserRegister;