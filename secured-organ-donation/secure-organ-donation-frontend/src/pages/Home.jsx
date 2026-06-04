import { useNavigate } from "react-router-dom";
import "../styles/home.css";

function Home() {
  const navigate = useNavigate();

  return (
    <div className="home-wrapper">

      {/* HERO SECTION */}
      <section className="hero">
        <h1>Secure Organ Donation Platform</h1>
        <p>
          A transparent, secure, and hospital-verified system for
          organ donation and recipient matching.
        </p>

        <div className="hero-buttons">
          <button onClick={() => navigate("/user/register")}>
            Register as User
          </button>

          <button onClick={() => navigate("/hospital/register")}>
            Register as Hospital
          </button>

          <button className="secondary" onClick={() => navigate("/user/login")}>
            Login
          </button>
        </div>
      </section>

      {/* ABOUT SECTION */}
      <section className="section">
        <h2>About Our Platform</h2>
        <p>
          Our platform ensures secure and ethical organ donation by
          allowing hospitals to verify donors and recipients before
          approval. Every action is protected with JWT authentication
          and role-based access control.
        </p>
      </section>

      {/* FEATURES */}
      <section className="section">
        <h2>Why Choose Us?</h2>
        <div className="features">
          <div className="feature-card">
            <h3>🔐 Secure Authentication</h3>
            <p>JWT-based authentication with role-based access control.</p>
          </div>

          <div className="feature-card">
            <h3>🏥 Hospital Verification</h3>
            <p>Only approved hospitals can verify users.</p>
          </div>

          <div className="feature-card">
            <h3>📩 Real-time Notifications</h3>
            <p>Get notified about approval and request status.</p>
          </div>
        </div>
      </section>

      {/* HOW IT WORKS */}
      <section className="section">
        <h2>How It Works</h2>
        <ol>
          <li>User registers and selects hospital</li>
          <li>Hospital verifies and approves user</li>
          <li>Admin oversees hospital approvals</li>
          <li>Secure organ matching begins</li>
        </ol>
      </section>

      {/* FINAL CTA */}
      <section className="cta">
        <h2>Be a Life Saver Today</h2>
        <button onClick={() => navigate("/user/register")}>
          Get Started
        </button>
      </section>

    </div>
  );
}

export default Home;