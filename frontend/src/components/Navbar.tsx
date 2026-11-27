import React from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Flame, LogOut, BookOpen, Clock, Settings, Sparkles } from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, streak, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path: string) => location.pathname === path;

  return (
    <header className="sticky top-0 z-50 bg-white/95 backdrop-blur-sm border-b border-slate-200/80 shadow-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        {/* Brand Logo & Nav Links */}
        <div className="flex items-center space-x-6 sm:space-x-8">
          <Link to="/" className="flex items-center space-x-3 group">
            <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-sky-600 to-indigo-600 flex items-center justify-center text-white shadow-md shadow-sky-500/20 group-hover:scale-105 transition-transform">
              <span className="text-xl">🎯</span>
            </div>
            <div>
              <span className="text-xl font-bold bg-gradient-to-r from-slate-900 to-slate-700 bg-clip-text text-transparent">
                AptiMaster
              </span>
            </div>
          </Link>

          {/* Navigation Items (when logged in) */}
          {user && (
            <nav className="hidden md:flex items-center space-x-1">
              <Link
                to="/"
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition flex items-center space-x-1.5 ${
                  isActive('/')
                    ? 'bg-sky-50 text-sky-700'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <BookOpen className="w-4 h-4" />
                <span>Chapter Syllabus</span>
              </Link>

              <Link
                to="/mock-tests"
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition flex items-center space-x-1.5 ${
                  location.pathname.startsWith('/mock-tests')
                    ? 'bg-indigo-50 text-indigo-700'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Clock className="w-4 h-4" />
                <span>Mock Tests</span>
                <span className="px-1.5 py-0.2 rounded-full bg-indigo-100 text-indigo-800 text-[10px]">
                  Timed
                </span>
              </Link>

              <Link
                to="/admin"
                className={`px-3 py-1.5 rounded-xl text-xs font-bold transition flex items-center space-x-1.5 ${
                  isActive('/admin')
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Settings className="w-4 h-4" />
                <span>Admin / Ingestion</span>
              </Link>
            </nav>
          )}
        </div>

        {/* Right Section: Streak Counter & User Profile */}
        <div className="flex items-center space-x-3 sm:space-x-5">
          {user ? (
            <>
              {/* Daily Streak Flame Widget */}
              <div
                className="flex items-center space-x-2 px-3 py-1.5 rounded-full bg-amber-50 border border-amber-200/80 text-amber-900 shadow-xs cursor-default"
                title={`Current Streak: ${streak?.currentStreak || 0} Days | Longest: ${streak?.longestStreak || 0} Days`}
              >
                <Flame className={`w-5 h-5 ${streak && streak.currentStreak > 0 ? 'text-amber-500 fill-amber-500 animate-pulse' : 'text-slate-400'}`} />
                <span className="font-bold text-sm">
                  {streak?.currentStreak || 0} <span className="hidden sm:inline font-medium text-xs text-amber-700">Days</span>
                </span>
              </div>

              {/* User Avatar & Logout */}
              <div className="flex items-center space-x-3 pl-2 sm:border-l sm:border-slate-200">
                <div className="hidden sm:flex flex-col text-right">
                  <span className="text-xs font-bold text-slate-800 leading-tight">
                    {user.fullName}
                  </span>
                  <span className="text-[10px] text-slate-400 capitalize">
                    {user.role.replace('ROLE_', '').toLowerCase()}
                  </span>
                </div>
                <div className="w-9 h-9 rounded-full bg-sky-100 border border-sky-200 flex items-center justify-center text-sky-700 font-bold text-sm">
                  {user.fullName.charAt(0).toUpperCase()}
                </div>

                <button
                  onClick={handleLogout}
                  className="p-2 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition-colors"
                  title="Logout"
                >
                  <LogOut className="w-5 h-5" />
                </button>
              </div>
            </>
          ) : (
            <div className="flex items-center space-x-3">
              <Link
                to="/login"
                className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-slate-900 rounded-lg hover:bg-slate-100 transition-colors"
              >
                Sign In
              </Link>
              <Link
                to="/register"
                className="px-4 py-2 text-sm font-medium text-white bg-sky-600 hover:bg-sky-700 rounded-lg shadow-sm hover:shadow transition-all"
              >
                Get Started
              </Link>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
