import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import './Navbar.css';

function getInitials(name) {
  if (!name) return '?';
  return name
    .split(' ')
    .map((w) => w[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();
}

function Navbar() {
  const { user, logout } = useUser();
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  function handleSignOut() {
    logout();
    setOpen(false);
    navigate('/');
  }

  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        Veri<span className="navbar-brand-accent">code</span>
      </Link>

      <div className="navbar-links">
        {user ? (
          <div className="navbar-user" ref={dropdownRef}>
            <button
              className="navbar-user-btn"
              onClick={() => setOpen((prev) => !prev)}
              aria-haspopup="true"
              aria-expanded={open}
            >
              <span className="navbar-avatar">{getInitials(user.name)}</span>
              <span className="navbar-user-name">Hello, {user.username}!</span>
              <span className={`navbar-chevron${open ? ' navbar-chevron--open' : ''}`} />
            </button>

            {open && (
              <div className="navbar-dropdown">
                <div className="navbar-dropdown-header">
                  <span className="navbar-dropdown-name">{user.name}</span>
                  <span className="navbar-dropdown-email">{user.email}</span>
                </div>
                <div className="navbar-dropdown-divider" />
                <button
                  className="navbar-dropdown-item"
                  onClick={() => { setOpen(false); navigate('/profile'); }}
                >
                  Profile
                </button>
                <button
                  className="navbar-dropdown-item navbar-dropdown-item--danger"
                  onClick={handleSignOut}
                >
                  Sign out
                </button>
              </div>
            )}
          </div>
        ) : (
          <button className="navbar-signin" onClick={() => navigate('/auth')}>
            Sign in
          </button>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
