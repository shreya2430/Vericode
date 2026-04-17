import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { loginUser, registerUser } from '../services/userService';
import { useUser } from '../context/UserContext';
import authImg from '../public/auth-img.jpg';
import './AuthPage.css';

const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

const PASSWORD_RULES = [
  { label: 'At least 8 characters',      test: (p) => p.length >= 8 },
  { label: 'One uppercase letter',        test: (p) => /[A-Z]/.test(p) },
  { label: 'One number',                  test: (p) => /[0-9]/.test(p) },
  { label: 'One special character',       test: (p) => /[^A-Za-z0-9]/.test(p) },
];

function getStrength(password) {
  return PASSWORD_RULES.filter((r) => r.test(password)).length;
}

function validateSignup(form) {
  const errors = {};
  if (!form.name.trim()) errors.name = 'Full name is required.';
  if (!form.username.trim()) errors.username = 'Username is required.';
  if (!EMAIL_RE.test(form.email)) errors.email = 'Enter a valid email address.';
  if (getStrength(form.password) < PASSWORD_RULES.length) {
    errors.password = 'Password does not meet all requirements.';
  }
  return errors;
}

function AuthPage() {
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState({ name: '', username: '', email: '', password: '' });
  const [fieldErrors, setFieldErrors] = useState({});
  const [touched, setTouched] = useState({});
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useUser();
  const from = location.state?.from?.pathname || '/prs';

  function handleChange(e) {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    setError('');
    if (mode === 'signup' && touched[name]) {
      validateField(name, value);
    }
  }

  function handleBlur(e) {
    const { name, value } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    if (mode === 'signup') validateField(name, value);
  }

  function validateField(name, value) {
    let msg = '';
    if (name === 'name' && !value.trim()) msg = 'Full name is required.';
    if (name === 'username' && !value.trim()) msg = 'Username is required.';
    if (name === 'email' && value && !EMAIL_RE.test(value)) msg = 'Enter a valid email address.';
    if (name === 'password' && value && getStrength(value) < PASSWORD_RULES.length) {
      msg = 'Password does not meet all requirements.';
    }
    setFieldErrors((prev) => ({ ...prev, [name]: msg }));
  }

  function switchMode(next) {
    setMode(next);
    setForm({ name: '', username: '', email: '', password: '' });
    setFieldErrors({});
    setTouched({});
    setError('');
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    if (mode === 'signup') {
      const errors = validateSignup(form);
      if (Object.keys(errors).length > 0) {
        setFieldErrors(errors);
        setTouched({ name: true, username: true, email: true, password: true });
        return;
      }
    }

    setLoading(true);
    try {
      if (mode === 'login') {
        const userData = await loginUser(form.username, form.password);
        login(userData);
        navigate(from, { replace: true });
      } else {
        const userData = await registerUser(form.name, form.username, form.email, form.password);
        login(userData);
        navigate(from, { replace: true });
      }
    } catch (err) {
      const msg =
        err?.response?.data?.error ||
        err?.response?.data?.message ||
        (mode === 'login' ? 'Invalid username or password.' : 'Registration failed. Please try again.');
      setError(typeof msg === 'string' ? msg : 'Something went wrong.');
    } finally {
      setLoading(false);
    }
  }

  const strength = getStrength(form.password);
  const showStrength = mode === 'signup' && form.password.length > 0;

  return (
    <div className="auth-page">
      {/* Left: form panel */}
      <div className="auth-form-panel">
        <div className="auth-form-inner">
          <div className="auth-tab-row">
            <button
              className={`auth-tab${mode === 'login' ? ' auth-tab--active' : ''}`}
              onClick={() => switchMode('login')}
              type="button"
            >
              Sign in
            </button>
            <button
              className={`auth-tab${mode === 'signup' ? ' auth-tab--active' : ''}`}
              onClick={() => switchMode('signup')}
              type="button"
            >
              Sign up
            </button>
          </div>

          <h2 className="auth-heading">
            {mode === 'login' ? 'Welcome back' : 'Create your account'}
          </h2>
          <p className="auth-subheading">
            {mode === 'login'
              ? 'Sign in to continue to Vericode.'
              : 'Join Vericode and start reviewing code.'}
          </p>

          <form className="auth-form" onSubmit={handleSubmit} noValidate>
            {mode === 'signup' && (
              <div className={`auth-field${fieldErrors.name ? ' auth-field--error' : ''}`}>
                <label htmlFor="name">Full name</label>
                <input
                  id="name"
                  name="name"
                  type="text"
                  autoComplete="name"
                  placeholder="Ada Lovelace"
                  value={form.name}
                  onChange={handleChange}
                  onBlur={handleBlur}
                />
                {fieldErrors.name && <span className="auth-field-error">{fieldErrors.name}</span>}
              </div>
            )}

            <div className={`auth-field${fieldErrors.username ? ' auth-field--error' : ''}`}>
              <label htmlFor="username">Username</label>
              <input
                id="username"
                name="username"
                type="text"
                autoComplete="username"
                placeholder="ada"
                value={form.username}
                onChange={handleChange}
                onBlur={handleBlur}
              />
              {fieldErrors.username && <span className="auth-field-error">{fieldErrors.username}</span>}
            </div>

            {mode === 'signup' && (
              <div className={`auth-field${fieldErrors.email ? ' auth-field--error' : ''}`}>
                <label htmlFor="email">Email</label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  placeholder="ada@example.com"
                  value={form.email}
                  onChange={handleChange}
                  onBlur={handleBlur}
                />
                {fieldErrors.email && <span className="auth-field-error">{fieldErrors.email}</span>}
              </div>
            )}

            <div className={`auth-field${fieldErrors.password ? ' auth-field--error' : ''}`}>
              <label htmlFor="password">Password</label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete={mode === 'login' ? 'current-password' : 'new-password'}
                placeholder="••••••••"
                value={form.password}
                onChange={handleChange}
                onBlur={handleBlur}
              />
              {fieldErrors.password && <span className="auth-field-error">{fieldErrors.password}</span>}

              {showStrength && (
                <div className="auth-strength">
                  <div className="auth-strength-bar">
                    {PASSWORD_RULES.map((_, i) => (
                      <div
                        key={i}
                        className={`auth-strength-segment${i < strength ? ` auth-strength-segment--${strength}` : ''}`}
                      />
                    ))}
                  </div>
                  <ul className="auth-strength-rules">
                    {PASSWORD_RULES.map((rule) => (
                      <li
                        key={rule.label}
                        className={`auth-strength-rule${rule.test(form.password) ? ' auth-strength-rule--met' : ''}`}
                      >
                        {rule.test(form.password) ? '+ ' : '- '}{rule.label}
                      </li>
                    ))}
                  </ul>
                </div>
              )}
            </div>

            {error && <p className="auth-error">{error}</p>}

            <button className="auth-submit" type="submit" disabled={loading}>
              {loading ? 'Please wait...' : mode === 'login' ? 'Sign in' : 'Create account'}
            </button>
          </form>

          <p className="auth-toggle-hint">
            {mode === 'login' ? "Don't have an account? " : 'Already have an account? '}
            <button
              className="auth-toggle-link"
              type="button"
              onClick={() => switchMode(mode === 'login' ? 'signup' : 'login')}
            >
              {mode === 'login' ? 'Sign up' : 'Sign in'}
            </button>
          </p>
        </div>
      </div>

      {/* Right: image panel */}
      <div className="auth-image-panel">
        <img src={authImg} alt="" className="auth-image" />
        <div className="auth-image-overlay" />
        <div className="auth-image-content">
          <p className="auth-tagline-eyebrow">Veri<span>code</span></p>
          <h2 className="auth-tagline">
            Submit code.<br />Run automated checks.<br />Get peer reviewed.
          </h2>
        </div>
      </div>
    </div>
  );
}

export default AuthPage;
