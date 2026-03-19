import { Link } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  return (
    <nav className="navbar">
      <Link to="/" className="navbar-brand">
        Veri<span className="navbar-brand-accent">code</span>
      </Link>
      <div className="navbar-links">
        <button className="navbar-signin">Sign in</button>
      </div>
    </nav>
  );
}

export default Navbar;
