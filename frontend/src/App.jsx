import { Routes, Route } from 'react-router-dom';
import Cursor from './components/Cursor';
import Navbar from './components/Navbar';
import LandingPage from './pages/LandingPage';
import PRListPage from './pages/PRListPage';
import PRSubmitPage from './pages/PRSubmitPage';
import PRDetailPage from './pages/PRDetailPage';

function App() {
  return (
    <>
      <Cursor />
      <Navbar />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/prs" element={<PRListPage />} />
        <Route path="/submit" element={<PRSubmitPage />} />
        <Route path="/pr/:id" element={<PRDetailPage />} />
      </Routes>
    </>
  );
}

export default App;
