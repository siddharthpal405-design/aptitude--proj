import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import { Category } from '../types';
import { ActivityHeatmap } from '../components/ActivityHeatmap';
import {
  Flame,
  Target,
  BookOpen,
  Calculator,
  Cpu,
  BarChart3,
  ChevronRight,
  Clock,
  ArrowRight,
  Layers,
} from 'lucide-react';

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

  const getCategoryIcon = (slug: string) => {
    if (slug.includes('quant')) return Calculator;
    if (slug.includes('logic')) return Cpu;
    if (slug.includes('data')) return BarChart3;
    return BookOpen;
  };

  const todayCount = streak?.todayQuestionsSolved || 0;
  const dailyProgressPct = Math.min(100, (todayCount / 5) * 100);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6 sm:space-y-8">
      {/* Executive Welcome & Overview Header */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-6">
        <div className="flex flex-col lg:flex-row lg:items-center justify-between gap-4 border-b border-slate-100 pb-5">
          <div className="space-y-1">
            <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">
              Preparation Overview
            </p>
            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
              Welcome back, {user?.fullName}
            </h1>
            <p className="text-sm text-slate-500 max-w-2xl">
              Explore structured study notes, browse 100+ verified MCQs per chapter, or launch a timed mock examination.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-2.5 self-start lg:self-auto">
            <Link
              to="/short-notes"
              className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-lg text-sm font-medium text-slate-700 bg-white border border-slate-300 hover:bg-slate-50 transition-colors"
            >
              <span>Short Notes &amp; Tricks</span>
            </Link>

            <Link
              to="/subjects"
              className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-lg text-sm font-medium text-slate-700 bg-white border border-slate-300 hover:bg-slate-50 transition-colors"
            >
              <BookOpen className="w-4 h-4 text-slate-500" />
              <span>All Subjects</span>
            </Link>

            <Link
              to="/mock-tests"
              className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors"
            >
              <Clock className="w-4 h-4 text-slate-300" />
              <span>Timed Mock Tests</span>
              <ArrowRight className="w-4 h-4" />
            </Link>
          </div>
        </div>

        {/* 3-Column Metrics Strip */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 flex items-center space-x-3.5">
            <div className="w-10 h-10 rounded-lg bg-amber-50 border border-amber-200 flex items-center justify-center flex-shrink-0">
              <Flame
                className={`w-5 h-5 ${
                  streak && streak.currentStreak > 0
                    ? 'text-amber-600 fill-amber-500'
                    : 'text-slate-400'
                }`}
              />
            </div>
            <div>
              <p className="text-xs font-medium text-slate-500">Current Study Streak</p>
              <div className="flex items-baseline space-x-2 mt-0.5">
                <span className="text-xl font-bold font-mono text-slate-900">
                  {streak?.currentStreak || 0}
                </span>
                <span className="text-xs text-slate-600">
                  {streak?.currentStreak === 1 ? 'day' : 'days'}
                </span>
                <span className="text-xs text-slate-400">
                  (Best:{' '}
                  <span className="font-mono font-medium text-slate-600">
                    {streak?.longestStreak || 0}d
                  </span>
                  )
                </span>
              </div>
            </div>
          </div>

          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 flex flex-col justify-center space-y-2">
            <div className="flex items-center justify-between text-xs">
              <span className="font-medium text-slate-500 flex items-center space-x-1.5">
                <Target className="w-3.5 h-3.5 text-blue-600" />
                <span>Daily Goal Progress</span>
              </span>
              <span className="font-mono font-semibold text-slate-900">
                {todayCount} / 5 Qs
              </span>
            </div>
            <div className="w-full bg-slate-200 rounded-sm h-2 overflow-hidden">
              <div
                className="bg-blue-600 h-2 rounded-sm transition-all duration-300"
                style={{ width: `${dailyProgressPct}%` }}
              />
            </div>
          </div>

          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 flex items-center space-x-3.5">
            <div className="w-10 h-10 rounded-lg bg-blue-50 border border-blue-200 flex items-center justify-center flex-shrink-0">
              <Layers className="w-5 h-5 text-blue-600" />
            </div>
            <div>
              <p className="text-xs font-medium text-slate-500">Total Question Bank</p>
              <div className="flex items-baseline space-x-2 mt-0.5">
                <span className="text-xl font-bold font-mono text-slate-900">
                  {totalQuestionsInSystem}
                </span>
                <span className="text-xs text-slate-600">MCQs across all chapters</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Activity Heatmap */}
      <ActivityHeatmap />

      {/* Syllabus Directory */}
      <section className="space-y-5">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
          <div>
            <h2 className="text-lg sm:text-xl font-bold text-slate-900 tracking-tight">
              Subjects &amp; Chapter Curriculum
            </h2>
            <p className="text-xs sm:text-sm text-slate-500">
              Select any topic to access its Study Material, browse the 100+ Question List, or start interactive practice.
            </p>
          </div>
          <Link
            to="/subjects"
            className="text-xs sm:text-sm font-semibold text-blue-600 hover:text-blue-700 inline-flex items-center space-x-1 self-start sm:self-auto"
          >
            <span>Open Curriculum Explorer</span>
            <ChevronRight className="w-4 h-4" />
          </Link>
        </div>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="h-44 rounded-xl bg-slate-200/60 animate-pulse" />
            ))}
          </div>
        ) : (
          <div className="space-y-6">
            {categories.map((category) => {
              const CategoryIcon = getCategoryIcon(category.slug);
              return (
                <div
                  key={category.id}
                  className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-5"
                >
                  <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                    <div className="flex items-center space-x-3">
                      <div className="w-9 h-9 rounded-lg bg-slate-100 border border-slate-200 text-slate-700 flex items-center justify-center flex-shrink-0">
                        <CategoryIcon className="w-4 h-4" />
                      </div>
                      <div>
                        <h3 className="text-base font-semibold text-slate-900">
                          {category.name}
                        </h3>
                        <p className="text-xs text-slate-500">{category.description}</p>
                      </div>
                    </div>
                    <span className="text-xs font-mono font-medium px-2.5 py-1 rounded-md bg-slate-100 text-slate-700 border border-slate-200 flex-shrink-0">
                      {category.chapters.length} chapters
                    </span>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
                    {category.chapters.map((chapter) => (
                      <Link
                        key={chapter.id}
                        to={`/chapters/${chapter.id}`}
                        className="group flex flex-col justify-between p-4 rounded-lg border border-slate-200 bg-slate-50/40 hover:bg-white hover:border-blue-500/60 hover:shadow-xs transition-all"
                      >
                        <div className="space-y-2">
                          <div className="flex items-center justify-between">
                            <span className="text-[11px] font-mono font-semibold uppercase tracking-wider text-slate-400">
                              Ch. {chapter.displayOrder.toString().padStart(2, '0')}
                            </span>
                            <span className="text-[11px] font-mono font-medium px-2 py-0.5 rounded-md bg-white border border-slate-200 text-slate-700">
                              {chapter.totalQuestions} Qs
                            </span>
                          </div>
                          <h4 className="text-sm font-semibold text-slate-900 group-hover:text-blue-600 transition-colors">
                            {chapter.title}
                          </h4>
                          <p className="text-xs text-slate-500 line-clamp-2 leading-relaxed">
                            {chapter.description}
                          </p>
                        </div>

                        <div className="pt-3 mt-3 border-t border-slate-200/70 flex items-center justify-between text-xs font-medium text-slate-600 group-hover:text-blue-600">
                          <span>Study Material &amp; List</span>
                          <ChevronRight className="w-4 h-4 transition-transform group-hover:translate-x-0.5" />
                        </div>
                      </Link>
                    ))}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </section>
    </div>
  );
};
