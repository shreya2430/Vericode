import { Routes, Route } from 'react-router-dom';
import { UserProvider } from './context/UserContext';
import Cursor from './components/Cursor';
import Navbar from './components/Navbar';
import ProtectedRoute from './components/ProtectedRoute';
import LandingPage from './pages/LandingPage';
import PRListPage from './pages/PRListPage';
import PRSubmitPage from './pages/PRSubmitPage';
import PRDetailPage from './pages/PRDetailPage';
import AuthPage from './pages/AuthPage';
import ProfilePage from './pages/ProfilePage';

function App() {
  return (
    <UserProvider>
      <Cursor />
      <Navbar />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
        <Route path="/prs" element={<ProtectedRoute><PRListPage /></ProtectedRoute>} />
        <Route path="/submit" element={<ProtectedRoute><PRSubmitPage /></ProtectedRoute>} />
        <Route path="/pr/:id" element={<ProtectedRoute><PRDetailPage /></ProtectedRoute>} />
      </Routes>
    </UserProvider>
  );
}

export default App;
