import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { ProtectedRoute } from './components/ProtectedRoute';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { PracticeArenaPage } from './pages/PracticeArenaPage';
import { MockTestListPage } from './pages/MockTestListPage';
import { MockTestLivePage } from './pages/MockTestLivePage';
import { MockTestResultPage } from './pages/MockTestResultPage';
import { AdminPortalPage } from './pages/AdminPortalPage';

export function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <div className="min-h-screen flex flex-col bg-slate-50">
          <Navbar />
          <main className="flex-1">
            <Routes>
              {/* Public Routes */}
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Protected Routes */}
              <Route element={<ProtectedRoute />}>
                <Route path="/" element={<DashboardPage />} />
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
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
