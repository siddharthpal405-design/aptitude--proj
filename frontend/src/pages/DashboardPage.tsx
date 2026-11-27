import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import { Category } from '../types';
import { ActivityHeatmap } from '../components/ActivityHeatmap';
import { Flame, Target, BookOpen, ChevronRight, Clock, Trophy, Sparkles, ArrowRight } from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user, streak } = useAuth();
  const [categories, setCategories] = useState<Category[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const res = await api.get<Category[]>('/categories');
        setCategories(res.data);
      } catch (err) {
        console.error('Failed to load categories', err);
      } finally {
        setLoading(false);
      }
    };
    loadCategories();
  }, []);

  const totalQuestionsInSystem = categories.reduce(
    (acc, cat) => acc + cat.chapters.reduce((cAcc, ch) => cAcc + ch.totalQuestions, 0),
    0
  );

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Welcome & Streak Banner */}
      <div className="relative overflow-hidden rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white p-6 sm:p-10 shadow-xl shadow-slate-900/10">
        <div className="absolute top-0 right-0 -mr-16 -mt-16 w-64 h-64 bg-indigo-500/20 rounded-full blur-3xl pointer-events-none"></div>
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-2">
            <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-sky-300">
              <Sparkles className="w-3.5 h-3.5" />
              <span>Aptitude Mastery Platform • Phase 2</span>
            </div>
            <h1 className="text-2xl sm:text-4xl font-extrabold tracking-tight">
              Welcome back, {user?.fullName}! 👋
            </h1>
            <p className="text-slate-300 text-sm sm:text-base max-w-xl">
              Practice chapter-wise MCQs, take timed mock tests under exam conditions, and watch your daily streak thrive.
            </p>
          </div>

          {/* Daily Streak Highlight Widget */}
          <div className="flex items-center space-x-4 bg-white/10 backdrop-blur-md border border-white/15 p-4 sm:p-5 rounded-2xl">
            <div className="w-14 h-14 rounded-2xl bg-amber-500/20 border border-amber-400/30 flex items-center justify-center text-amber-400">
              <Flame className="w-8 h-8 fill-amber-400 animate-bounce" />
            </div>
            <div>
              <div className="flex items-baseline space-x-2">
                <span className="text-3xl font-extrabold text-amber-300">
                  {streak?.currentStreak || 0}
                </span>
                <span className="text-sm font-semibold text-amber-100">Days Active</span>
              </div>
              <div className="flex items-center space-x-3 text-xs text-slate-300 mt-1">
                <span>Best: <strong>{streak?.longestStreak || 0}d</strong></span>
                <span>•</span>
                <span>Today: <strong>{streak?.todayQuestionsSolved || 0} Qs</strong></span>
              </div>
            </div>
          </div>
        </div>

        {/* Daily Goal Progress Bar */}
        <div className="mt-8 pt-6 border-t border-white/10 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center space-x-3">
            <Target className="w-5 h-5 text-sky-400 flex-shrink-0" />
            <span className="text-sm font-medium text-slate-300">
              Daily Target: Practice at least 5 MCQs today to maintain streak!
            </span>
          </div>
          <div className="w-full sm:w-64 bg-slate-800 rounded-full h-2.5 overflow-hidden border border-white/10">
            <div
              className="bg-gradient-to-r from-sky-400 to-indigo-400 h-2.5 rounded-full transition-all duration-500"
              style={{
                width: `${Math.min(100, ((streak?.todayQuestionsSolved || 0) / 5) * 100)}%`,
              }}
            ></div>
          </div>
        </div>
      </div>

      {/* Quick Access Card: Timed Mock Tests (Phase 2 Feature) */}
      <div className="bg-gradient-to-r from-sky-500 to-indigo-600 rounded-3xl p-6 sm:p-8 text-white flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-md">
        <div className="flex items-center space-x-4">
          <div className="w-12 h-12 rounded-2xl bg-white/20 flex items-center justify-center">
            <Clock className="w-6 h-6 text-white" />
          </div>
          <div>
            <h3 className="text-lg font-bold">Ready for a Real Exam Challenge?</h3>
            <p className="text-xs sm:text-sm text-sky-100">
              Take full-length simulated mock tests with live countdown timers, question palettes, and instant scorecards.
            </p>
          </div>
        </div>
        <Link
          to="/mock-tests"
          className="inline-flex items-center space-x-2 px-6 py-3 rounded-2xl font-bold text-xs sm:text-sm bg-white text-indigo-900 hover:bg-slate-100 active:scale-95 transition shadow-sm self-start sm:self-auto"
        >
          <span>Explore Mock Tests</span>
          <ArrowRight className="w-4 h-4" />
        </Link>
      </div>

      {/* 365-Day Daily Activity Heatmap (Phase 2 Feature) */}
      <ActivityHeatmap />

      {/* Syllabus Overview Section */}
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-2xl font-bold text-slate-900 tracking-tight">
              Aptitude Syllabus Chapters
            </h2>
            <p className="text-sm text-slate-500">
              Select a chapter below to start solving MCQs with instant step-by-step solutions.
            </p>
          </div>
          <span className="text-xs font-semibold px-3 py-1.5 rounded-full bg-slate-100 text-slate-700 border border-slate-200">
            {totalQuestionsInSystem} Questions in Bank
          </span>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-48 rounded-2xl bg-slate-200/60 animate-pulse"></div>
            ))}
          </div>
        ) : (
          <div className="space-y-8">
            {categories.map((category) => (
              <div
                key={category.id}
                className="bg-white rounded-3xl border border-slate-200/80 p-6 sm:p-8 shadow-xs space-y-6"
              >
                {/* Category Header */}
                <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 rounded-xl bg-sky-50 text-sky-600 border border-sky-100 flex items-center justify-center font-bold">
                      <BookOpen className="w-5 h-5" />
                    </div>
                    <div>
                      <h3 className="text-xl font-bold text-slate-900">
                        {category.name}
                      </h3>
                      <p className="text-xs text-slate-500">{category.description}</p>
                    </div>
                  </div>
                  <span className="text-xs font-medium px-2.5 py-1 rounded-full bg-slate-100 text-slate-600">
                    {category.chapters.length} Chapters
                  </span>
                </div>

                {/* Chapters Grid */}
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                  {category.chapters.map((chapter) => (
                    <div
                      key={chapter.id}
                      className="group flex flex-col justify-between p-5 rounded-2xl border border-slate-200/70 hover:border-sky-300 hover:shadow-md hover:shadow-sky-500/5 bg-slate-50/50 hover:bg-white transition-all"
                    >
                      <div className="space-y-2">
                        <div className="flex items-center justify-between">
                          <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                            Chapter {chapter.displayOrder}
                          </span>
                          <span className="text-xs font-semibold px-2 py-0.5 rounded-md bg-sky-100/80 text-sky-700">
                            {chapter.totalQuestions} Questions
                          </span>
                        </div>
                        <h4 className="text-base font-bold text-slate-900 group-hover:text-sky-600 transition-colors">
                          {chapter.title}
                        </h4>
                        <p className="text-xs text-slate-500 line-clamp-2">
                          {chapter.description}
                        </p>
                      </div>

                      <div className="pt-4 mt-2 border-t border-slate-100 flex items-center justify-between">
                        <span className="text-xs text-slate-400">MCQ Practice</span>
                        <Link
                          to={`/practice/${chapter.id}?title=${encodeURIComponent(chapter.title)}`}
                          className="inline-flex items-center space-x-1.5 text-xs font-semibold text-sky-600 group-hover:text-sky-700 group-hover:translate-x-0.5 transition-all"
                        >
                          <span>Start Solving</span>
                          <ChevronRight className="w-4 h-4" />
                        </Link>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
