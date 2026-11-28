import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { ProtectedRoute } from './components/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { SubjectsPage } from './pages/SubjectsPage';
import { ShortNotesPage } from './pages/ShortNotesPage';
import { ChapterDetailPage } from './pages/ChapterDetailPage';
import { PracticeArenaPage } from './pages/PracticeArenaPage';
import { MockTestListPage } from './pages/MockTestListPage';
import { MockTestLivePage } from './pages/MockTestLivePage';
import { MockTestResultPage } from './pages/MockTestResultPage';
import { AdminPortalPage } from './pages/AdminPortalPage';

const AppLayout: React.FC = () => {
  const location = useLocation();
  const isLiveExam = /^\/mock-tests\/[^/]+\/live$/.test(location.pathname);

  return (
    <div className="min-h-screen flex flex-col bg-slate-50">
      {!isLiveExam && <Navbar />}
      <main className="flex-1">
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Protected Routes */}
          <Route element={<ProtectedRoute />}>
            <Route path="/" element={<DashboardPage />} />
            <Route path="/subjects" element={<SubjectsPage />} />
            <Route path="/short-notes" element={<ShortNotesPage />} />
            <Route path="/chapters/:chapterId" element={<ChapterDetailPage />} />
            <Route path="/practice/:chapterId" element={<PracticeArenaPage />} />
            <Route path="/mock-tests" element={<MockTestListPage />} />
            <Route path="/mock-tests/:testId/live" element={<MockTestLivePage />} />
            <Route path="/mock-tests/result/:sessionId" element={<MockTestResultPage />} />
            <Route path="/admin" element={<AdminPortalPage />} />
          </Route>

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </main>
    </div>
  );
};

export function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppLayout />
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
