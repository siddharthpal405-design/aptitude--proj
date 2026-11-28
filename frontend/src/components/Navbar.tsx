import React, { useState, useEffect, useRef } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { BrandMark } from './BrandMark';
import api from '../api/client';
import { Category } from '../types';
import {
  Flame,
  LogOut,
  LayoutDashboard,
  BookOpen,
  Clock,
  Database,
  Menu,
  X,
  ChevronDown,
  ChevronRight,
  Calculator,
  Cpu,
  BarChart3,
  Zap,
} from 'lucide-react';

export const Navbar: React.FC = () => {
  const { user, streak, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const [categories, setCategories] = useState<Category[]>([]);
  const [subjectsDropdownOpen, setSubjectsDropdownOpen] = useState(false);
  const [activeDesktopCategoryId, setActiveDesktopCategoryId] = useState<number | null>(null);
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [expandedMobileSubjectId, setExpandedMobileSubjectId] = useState<number | null>(null);

  const dropdownRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!user) return;
    const loadHierarchy = async () => {
      try {
        const res = await api.get<Category[]>('/categories');
        setCategories(res.data);
        if (res.data.length > 0) {
          setActiveDesktopCategoryId(res.data[0].id);
          setExpandedMobileSubjectId(res.data[0].id);
        }
      } catch (err) {
        console.error('Failed to load navigation subjects', err);
      }
    };
    loadHierarchy();
  }, [user]);

  // Close desktop dropdown on route change or outside click
  useEffect(() => {
    setSubjectsDropdownOpen(false);
    setMobileMenuOpen(false);
  }, [location.pathname, location.search]);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target as Node)) {
        setSubjectsDropdownOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    setMobileMenuOpen(false);
    logout();
    navigate('/login');
  };

  const getSubjectIcon = (slug: string) => {
    if (slug.includes('quant')) return Calculator;
    if (slug.includes('logic')) return Cpu;
    if (slug.includes('data')) return BarChart3;
    return BookOpen;
  };

  const isSubjectAreaActive =
    location.pathname.startsWith('/subjects') ||
    location.pathname.startsWith('/chapters') ||
    location.pathname.startsWith('/practice');

  const activeCategory =
    categories.find((c) => c.id === activeDesktopCategoryId) || categories[0];

  return (
    <header className="sticky top-0 z-40 bg-white border-b border-slate-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-14 sm:h-16 flex items-center justify-between">
        {/* Left: Brand & Desktop Hierarchical Nav */}
        <div className="flex items-center space-x-6 lg:space-x-8">
          <Link
            to="/"
            onClick={() => setMobileMenuOpen(false)}
            className="focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-600 rounded-md"
          >
            <BrandMark size="md" />
          </Link>

          {user && (
            <nav className="hidden md:flex items-center space-x-1" aria-label="Main navigation">
              <Link
                to="/"
                className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center space-x-2 ${
                  location.pathname === '/'
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <LayoutDashboard
                  className={`w-4 h-4 ${
                    location.pathname === '/' ? 'text-blue-600' : 'text-slate-400'
                  }`}
                />
                <span>Dashboard</span>
              </Link>

              {/* Hierarchical Subjects -> Topics Dropdown */}
              <div className="relative" ref={dropdownRef}>
                <button
                  type="button"
                  onClick={() => setSubjectsDropdownOpen((prev) => !prev)}
                  className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center space-x-1.5 ${
                    isSubjectAreaActive || subjectsDropdownOpen
                      ? 'bg-slate-100 text-slate-900'
                      : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                  }`}
                  aria-expanded={subjectsDropdownOpen}
                >
                  <BookOpen
                    className={`w-4 h-4 ${
                      isSubjectAreaActive ? 'text-blue-600' : 'text-slate-400'
                    }`}
                  />
                  <span>Subjects &amp; Topics</span>
                  <ChevronDown
                    className={`w-3.5 h-3.5 text-slate-400 transition-transform ${
                      subjectsDropdownOpen ? 'rotate-180' : ''
                    }`}
                  />
                </button>

                {subjectsDropdownOpen && (
                  <div className="absolute left-0 mt-2 w-[640px] bg-white rounded-xl border border-slate-200 shadow-lg overflow-hidden z-50">
                    <div className="grid grid-cols-12">
                      {/* Left Column: Subjects */}
                      <div className="col-span-5 bg-slate-50 border-r border-slate-200 p-2 space-y-1 max-h-[340px] overflow-y-auto">
                        <p className="px-2.5 py-1.5 text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                          Subjects
                        </p>
                        {categories.map((cat) => {
                          const Icon = getSubjectIcon(cat.slug);
                          const isSelected = activeCategory?.id === cat.id;
                          return (
                            <button
                              key={cat.id}
                              type="button"
                              onMouseEnter={() => setActiveDesktopCategoryId(cat.id)}
                              onClick={() => {
                                setSubjectsDropdownOpen(false);
                                navigate(`/subjects?subject=${cat.slug}`);
                              }}
                              className={`w-full text-left px-2.5 py-2 rounded-lg text-xs font-medium flex items-center justify-between transition-colors ${
                                isSelected
                                  ? 'bg-white text-slate-900 border border-slate-200 shadow-xs'
                                  : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                              }`}
                            >
                              <span className="flex items-center space-x-2 truncate">
                                <Icon
                                  className={`w-4 h-4 flex-shrink-0 ${
                                    isSelected ? 'text-blue-600' : 'text-slate-400'
                                  }`}
                                />
                                <span className="truncate">{cat.name}</span>
                              </span>
                              <ChevronRight className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                            </button>
                          );
                        })}
                      </div>

                      {/* Right Column: Topics / Chapters */}
                      <div className="col-span-7 p-3 space-y-2 max-h-[340px] overflow-y-auto">
                        {activeCategory && (
                          <>
                            <div className="flex items-center justify-between px-2 pb-1 border-b border-slate-100">
                              <span className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                                {activeCategory.name} Topics
                              </span>
                              <Link
                                to={`/subjects?subject=${activeCategory.slug}`}
                                className="text-[11px] font-medium text-blue-600 hover:text-blue-700"
                              >
                                View subject
                              </Link>
                            </div>
                            <div className="space-y-1">
                              {activeCategory.chapters.map((ch) => (
                                <Link
                                  key={ch.id}
                                  to={`/chapters/${ch.id}`}
                                  className="flex items-center justify-between px-2.5 py-2 rounded-lg hover:bg-slate-50 border border-transparent hover:border-slate-200 transition-colors group"
                                >
                                  <div className="min-w-0 pr-2">
                                    <p className="text-xs font-semibold text-slate-800 group-hover:text-blue-600 truncate">
                                      {ch.title}
                                    </p>
                                    <p className="text-[11px] text-slate-400 truncate">
                                      Study notes &amp; question bank
                                    </p>
                                  </div>
                                  <span className="text-[11px] font-mono font-medium px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 flex-shrink-0">
                                    {ch.totalQuestions} Qs
                                  </span>
                                </Link>
                              ))}
                            </div>
                          </>
                        )}
                      </div>
                    </div>

                    <div className="bg-slate-50 border-t border-slate-200 px-4 py-2.5 flex items-center justify-between">
                      <span className="text-xs text-slate-500">
                        Explore study materials &amp; 100+ MCQs per chapter
                      </span>
                      <Link
                        to="/subjects"
                        className="text-xs font-semibold text-blue-600 hover:text-blue-700 flex items-center space-x-1"
                      >
                        <span>All Subjects</span>
                        <ChevronRight className="w-3.5 h-3.5" />
                      </Link>
                    </div>
                  </div>
                )}
              </div>

              <Link
                to="/short-notes"
                className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center space-x-2 ${
                  location.pathname === '/short-notes'
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <Zap
                  className={`w-4 h-4 ${
                    location.pathname === '/short-notes' ? 'text-blue-600' : 'text-slate-400'
                  }`}
                />
                <span>Short Notes</span>
              </Link>

              <Link
                to="/mock-tests"
                className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center space-x-2 ${
                  location.pathname.startsWith('/mock-tests')
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <Clock
                  className={`w-4 h-4 ${
                    location.pathname.startsWith('/mock-tests') ? 'text-blue-600' : 'text-slate-400'
                  }`}
                />
                <span>Mock Tests</span>
              </Link>

              <Link
                to="/admin"
                className={`px-3 py-1.5 rounded-md text-sm font-medium transition-colors flex items-center space-x-2 ${
                  location.pathname === '/admin'
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
                }`}
              >
                <Database
                  className={`w-4 h-4 ${
                    location.pathname === '/admin' ? 'text-blue-600' : 'text-slate-400'
                  }`}
                />
                <span>Manage Bank</span>
              </Link>
            </nav>
          )}
        </div>

        {/* Right: Streak, User Info & Mobile Hamburger */}
        <div className="flex items-center space-x-2.5 sm:space-x-3">
          {user ? (
            <>
              <div
                className="flex items-center space-x-1.5 px-2.5 py-1 rounded-md bg-amber-50/70 border border-amber-200/80 text-amber-900 cursor-default"
                title={`Current Streak: ${streak?.currentStreak || 0} days | Longest Streak: ${streak?.longestStreak || 0} days`}
              >
                <Flame
                  className={`w-4 h-4 ${
                    streak && streak.currentStreak > 0
                      ? 'text-amber-600 fill-amber-500'
                      : 'text-slate-400'
                  }`}
                />
                <span className="font-mono font-semibold text-xs sm:text-sm">
                  {streak?.currentStreak || 0}
                </span>
                <span className="hidden sm:inline text-xs font-medium text-amber-800">
                  {streak?.currentStreak === 1 ? 'day' : 'days'}
                </span>
              </div>

              <div className="hidden md:flex items-center space-x-3 pl-3 border-l border-slate-200">
                <div className="flex flex-col text-right">
                  <span className="text-xs font-semibold text-slate-900 leading-tight">
                    {user.fullName}
                  </span>
                  <span className="text-[11px] text-slate-500 capitalize">
                    {user.role.replace('ROLE_', '').toLowerCase()}
                  </span>
                </div>
                <div className="w-8 h-8 rounded-md bg-slate-900 text-white flex items-center justify-center font-semibold text-xs">
                  {user.fullName.charAt(0).toUpperCase()}
                </div>
                <button
                  onClick={handleLogout}
                  className="p-1.5 text-slate-500 hover:text-rose-600 hover:bg-rose-50 rounded-md transition-colors"
                  title="Sign out"
                  aria-label="Sign out"
                >
                  <LogOut className="w-4 h-4" />
                </button>
              </div>

              {/* Mobile Hamburger Button (44px touch target) */}
              <button
                type="button"
                onClick={() => setMobileMenuOpen((prev) => !prev)}
                className="md:hidden w-10 h-10 flex items-center justify-center rounded-md text-slate-700 hover:text-slate-900 hover:bg-slate-100 transition-colors"
                aria-label={mobileMenuOpen ? 'Close navigation menu' : 'Open navigation menu'}
                aria-expanded={mobileMenuOpen}
              >
                {mobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
              </button>
            </>
          ) : (
            <div className="flex items-center space-x-2">
              <Link
                to="/login"
                className="px-3 py-1.5 text-sm font-medium text-slate-600 hover:text-slate-900 rounded-md hover:bg-slate-50 transition-colors"
              >
                Sign in
              </Link>
              <Link
                to="/register"
                className="px-3.5 py-1.5 text-sm font-medium text-white bg-slate-900 hover:bg-slate-800 rounded-md transition-colors"
              >
                Create account
              </Link>
            </div>
          )}
        </div>
      </div>

      {/* 100% Responsive Mobile Accordion Drawer */}
      {user && mobileMenuOpen && (
        <div className="md:hidden border-t border-slate-200 bg-white max-h-[calc(100vh-3.5rem)] overflow-y-auto shadow-md">
          <div className="px-4 py-3 space-y-3">
            <nav className="space-y-1" aria-label="Mobile navigation">
              <Link
                to="/"
                className={`flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium ${
                  location.pathname === '/'
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-700 hover:bg-slate-50'
                }`}
              >
                <LayoutDashboard className="w-4 h-4 text-slate-500" />
                <span>Dashboard</span>
              </Link>

              {/* Subjects & Topics Hierarchy Accordion */}
              <div className="rounded-lg border border-slate-200 bg-slate-50/60 overflow-hidden">
                <div className="px-3 py-2.5 flex items-center justify-between border-b border-slate-200 bg-white">
                  <span className="flex items-center space-x-2.5 text-sm font-semibold text-slate-900">
                    <BookOpen className="w-4 h-4 text-blue-600" />
                    <span>Subjects &amp; Topics</span>
                  </span>
                  <Link
                    to="/subjects"
                    className="text-xs font-medium text-blue-600 hover:text-blue-700"
                  >
                    View all
                  </Link>
                </div>

                <div className="divide-y divide-slate-200/80">
                  {categories.map((cat) => {
                    const Icon = getSubjectIcon(cat.slug);
                    const isExpanded = expandedMobileSubjectId === cat.id;
                    return (
                      <div key={cat.id}>
                        <button
                          type="button"
                          onClick={() =>
                            setExpandedMobileSubjectId(isExpanded ? null : cat.id)
                          }
                          className="w-full px-3 py-2.5 flex items-center justify-between text-left text-xs font-semibold text-slate-800 hover:bg-slate-100/70 transition-colors"
                          aria-expanded={isExpanded}
                        >
                          <span className="flex items-center space-x-2">
                            <Icon className="w-4 h-4 text-slate-500" />
                            <span>{cat.name}</span>
                          </span>
                          <ChevronDown
                            className={`w-4 h-4 text-slate-400 transition-transform ${
                              isExpanded ? 'rotate-180' : ''
                            }`}
                          />
                        </button>

                        {isExpanded && (
                          <div className="bg-white px-2 py-1.5 space-y-1 border-t border-slate-100">
                            {cat.chapters.map((ch) => (
                              <Link
                                key={ch.id}
                                to={`/chapters/${ch.id}`}
                                className="flex items-center justify-between px-3 py-2 rounded-md text-xs text-slate-700 hover:bg-slate-50 hover:text-blue-600"
                              >
                                <span className="font-medium truncate pr-2">{ch.title}</span>
                                <span className="font-mono text-[11px] px-1.5 py-0.5 rounded bg-slate-100 text-slate-600 flex-shrink-0">
                                  {ch.totalQuestions} Qs
                                </span>
                              </Link>
                            ))}
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              </div>

              <Link
                to="/short-notes"
                className={`flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium ${
                  location.pathname === '/short-notes'
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-700 hover:bg-slate-50'
                }`}
              >
                <Zap className="w-4 h-4 text-slate-500" />
                <span>Short Notes &amp; Tricks</span>
              </Link>

              <Link
                to="/mock-tests"
                className={`flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium ${
                  location.pathname.startsWith('/mock-tests')
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-700 hover:bg-slate-50'
                }`}
              >
                <Clock className="w-4 h-4 text-slate-500" />
                <span>Mock Tests</span>
              </Link>

              <Link
                to="/admin"
                className={`flex items-center space-x-3 px-3 py-2.5 rounded-lg text-sm font-medium ${
                  location.pathname === '/admin'
                    ? 'bg-slate-100 text-slate-900'
                    : 'text-slate-700 hover:bg-slate-50'
                }`}
              >
                <Database className="w-4 h-4 text-slate-500" />
                <span>Manage Bank</span>
              </Link>
            </nav>

            <div className="pt-3 border-t border-slate-200 flex items-center justify-between">
              <div className="flex items-center space-x-2.5 min-w-0">
                <div className="w-8 h-8 rounded-md bg-slate-900 text-white flex items-center justify-center font-semibold text-xs flex-shrink-0">
                  {user.fullName.charAt(0).toUpperCase()}
                </div>
                <div className="min-w-0">
                  <p className="text-xs font-semibold text-slate-900 truncate">{user.fullName}</p>
                  <p className="text-[11px] text-slate-500 truncate">{user.email}</p>
                </div>
              </div>
              <button
                onClick={handleLogout}
                className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-md text-xs font-medium text-rose-700 bg-rose-50 border border-rose-200 hover:bg-rose-100 transition-colors flex-shrink-0"
              >
                <LogOut className="w-3.5 h-3.5" />
                <span>Sign out</span>
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
};
