import { Link } from 'react-router-dom';
import './LandingPage.css';

const TAGS = [
  { label: 'Java', color: 'neon-orange' },
  { label: 'Python', color: 'neon-cyan' },
  { label: 'JavaScript', color: 'neon-green' },
  { label: 'Lint', color: 'neon-purple' },
  { label: 'Style', color: 'neon-cyan' },
  { label: 'Security', color: 'neon-orange' },
];

function LandingPage() {
  return (
    <main className="landing">
      <section className="hero">
        <h1 className="hero-title">
          Veri<span className="accent">code</span>
        </h1>
        <p className="hero-subtitle">
          Submit code. Run automated checks. Get peer reviewed.
        </p>

        <div className="tag-row">
          {TAGS.map((t) => (
            <span key={t.label} className={`tag tag--${t.color}`}>
              {t.label}
            </span>
          ))}
        </div>

        <div className="hero-actions">
          <Link to="/submit" className="btn btn--primary">Submit a PR</Link>
          <Link to="/prs" className="btn btn--ghost">Browse PRs</Link>
        </div>
      </section>

      <section className="features">
        <div className="feature-card">
          <span className="feature-icon" style={{ color: 'var(--neon-green)' }}>01</span>
          <h3>Automated Checks</h3>
          <p>Lint, style, and security analysis runs the moment you submit.</p>
        </div>
        <div className="feature-card">
          <span className="feature-icon" style={{ color: 'var(--neon-cyan)' }}>02</span>
          <h3>Review Lifecycle</h3>
          <p>Draft to Merged - every state transition is tracked and enforced.</p>
        </div>
        <div className="feature-card">
          <span className="feature-icon" style={{ color: 'var(--neon-purple)' }}>03</span>
          <h3>Full Action History</h3>
          <p>Every approve, reject, and comment is logged. Undo anytime.</p>
        </div>
      </section>
    </main>
  );
}

export default LandingPage;
