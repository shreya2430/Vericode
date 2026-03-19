import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import PRListPage from './pages/PRListPage';
import PRSubmitPage from './pages/PRSubmitPage';
import PRDetailPage from './pages/PRDetailPage';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<PRListPage />} />
        <Route path="/submit" element={<PRSubmitPage />} />
        <Route path="/pr/:id" element={<PRDetailPage />} />
      </Routes>
    </>
  );
}

export default App;
