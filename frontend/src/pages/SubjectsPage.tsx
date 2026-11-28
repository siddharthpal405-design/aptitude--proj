import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import api from '../api/client';
import { Category } from '../types';
import {
  BookOpen,
  Calculator,
  Cpu,
  BarChart3,
  FileText,
  ListChecks,
  ChevronRight,
} from 'lucide-react';

export const SubjectsPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialSubject = searchParams.get('subject') || 'all';

  const [categories, setCategories] = useState<Category[]>([]);
  const [selectedSlug, setSelectedSlug] = useState<string>(initialSubject);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const paramSlug = searchParams.get('subject') || 'all';
    setSelectedSlug(paramSlug);
  }, [searchParams]);

  useEffect(() => {
    const loadCategories = async () => {
      try {
        const res = await api.get<Category[]>('/categories');
        setCategories(res.data);
      } catch (err) {
        console.error('Failed to load subjects', err);
      } finally {
        setLoading(false);
      }
    };
    loadCategories();
  }, []);

  const handleSelectSubject = (slug: string) => {
    setSelectedSlug(slug);
    if (slug === 'all') {
      setSearchParams({});
    } else {
      setSearchParams({ subject: slug });
    }
  };

  const getSubjectIcon = (slug: string) => {
    if (slug.includes('quant')) return Calculator;
    if (slug.includes('logic')) return Cpu;
    if (slug.includes('data')) return BarChart3;
    return BookOpen;
  };

  const visibleCategories =
    selectedSlug === 'all'
      ? categories
      : categories.filter((c) => c.slug === selectedSlug);

  const totalQuestions = categories.reduce(
    (acc, cat) => acc + cat.chapters.reduce((cAcc, ch) => cAcc + ch.totalQuestions, 0),
    0
  );

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6">
      {/* Header */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">
              Curriculum Hierarchy
            </p>
            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight mt-0.5">
              Subjects, Study Materials &amp; Question Banks
            </h1>
            <p className="text-sm text-slate-500 mt-1 max-w-2xl">
              Browse every subject and topic, review formula sheets and worked examples, and access all 100+ verified MCQs per chapter.
            </p>
          </div>
          <div className="flex flex-wrap items-center gap-2.5 self-start sm:self-auto flex-shrink-0">
            <Link
              to="/short-notes"
              className="px-3 py-1.5 rounded-lg bg-slate-900 hover:bg-slate-800 text-white text-xs font-semibold transition-colors"
            >
              Short Notes &amp; Tricks
            </Link>
            <div className="px-3 py-1.5 rounded-lg bg-slate-50 border border-slate-200 text-xs font-medium text-slate-700">
              Total Bank: <strong className="font-mono text-slate-900">{totalQuestions}</strong> MCQs
            </div>
          </div>
        </div>

        {/* Subject Filter Tabs */}
        <div className="flex flex-wrap items-center gap-1.5 pt-2 border-t border-slate-100">
          <button
            type="button"
            onClick={() => handleSelectSubject('all')}
            className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors ${
              selectedSlug === 'all'
                ? 'bg-slate-900 text-white'
                : 'bg-slate-100 text-slate-600 hover:text-slate-900 hover:bg-slate-200/70'
            }`}
          >
            All Subjects ({categories.length})
          </button>
          {categories.map((cat) => (
            <button
              key={cat.id}
              type="button"
              onClick={() => handleSelectSubject(cat.slug)}
              className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors ${
                selectedSlug === cat.slug
                  ? 'bg-slate-900 text-white'
                  : 'bg-slate-100 text-slate-600 hover:text-slate-900 hover:bg-slate-200/70'
              }`}
            >
              {cat.name}
            </button>
          ))}
        </div>
      </section>

      {/* Subject Hierarchy List */}
      {loading ? (
        <div className="space-y-4">
          {[1, 2].map((i) => (
            <div key={i} className="h-56 rounded-xl bg-slate-200/60 animate-pulse" />
          ))}
        </div>
      ) : (
        <div className="space-y-6">
          {visibleCategories.map((cat) => {
            const Icon = getSubjectIcon(cat.slug);
            const subjectQuestionCount = cat.chapters.reduce(
              (sum, ch) => sum + ch.totalQuestions,
              0
            );

            return (
              <section
                key={cat.id}
                className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-5"
              >
                <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
                  <div className="flex items-center space-x-3">
                    <div className="w-10 h-10 rounded-lg bg-slate-100 border border-slate-200 text-slate-700 flex items-center justify-center flex-shrink-0">
                      <Icon className="w-5 h-5" />
                    </div>
                    <div>
                      <h2 className="text-lg font-bold text-slate-900">{cat.name}</h2>
                      <p className="text-xs text-slate-500">{cat.description}</p>
                    </div>
                  </div>
                  <div className="flex items-center space-x-2 text-xs font-mono">
                    <span className="px-2.5 py-1 rounded-md bg-slate-100 border border-slate-200 text-slate-700">
                      {cat.chapters.length} Topics
                    </span>
                    <span className="px-2.5 py-1 rounded-md bg-blue-50 border border-blue-200 text-blue-700 font-semibold">
                      {subjectQuestionCount} MCQs
                    </span>
                  </div>
                </div>

                {/* Topics Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                  {cat.chapters.map((ch) => (
                    <div
                      key={ch.id}
                      className="rounded-lg border border-slate-200 bg-slate-50/40 p-4 flex flex-col justify-between space-y-4 hover:border-slate-300 transition-colors"
                    >
                      <div className="space-y-2">
                        <div className="flex items-center justify-between">
                          <span className="text-[11px] font-mono font-semibold uppercase text-slate-400">
                            Topic {ch.displayOrder.toString().padStart(2, '0')}
                          </span>
                          <span className="text-[11px] font-mono font-semibold px-2 py-0.5 rounded-md bg-white border border-slate-200 text-slate-800">
                            {ch.totalQuestions} MCQs
                          </span>
                        </div>
                        <h3 className="text-base font-semibold text-slate-900">
                          {ch.title}
                        </h3>
                        <p className="text-xs text-slate-500 leading-relaxed line-clamp-2">
                          {ch.description}
                        </p>
                      </div>

                      <div className="pt-3 border-t border-slate-200/80 flex items-center justify-between gap-2">
                        <div className="flex items-center space-x-2">
                          <Link
                            to={`/chapters/${ch.id}?tab=study`}
                            className="inline-flex items-center space-x-1 px-2.5 py-1.5 rounded-md text-xs font-medium bg-white border border-slate-200 text-slate-700 hover:bg-slate-100 transition-colors"
                          >
                            <FileText className="w-3.5 h-3.5 text-blue-600" />
                            <span>Notes</span>
                          </Link>
                          <Link
                            to={`/chapters/${ch.id}?tab=questions`}
                            className="inline-flex items-center space-x-1 px-2.5 py-1.5 rounded-md text-xs font-medium bg-white border border-slate-200 text-slate-700 hover:bg-slate-100 transition-colors"
                          >
                            <ListChecks className="w-3.5 h-3.5 text-emerald-600" />
                            <span>List</span>
                          </Link>
                        </div>

                        <Link
                          to={`/practice/${ch.id}?title=${encodeURIComponent(ch.title)}`}
                          className="inline-flex items-center space-x-1 text-xs font-semibold text-blue-600 hover:text-blue-700"
                        >
                          <span>Practice</span>
                          <ChevronRight className="w-3.5 h-3.5" />
                        </Link>
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            );
          })}
        </div>
      )}
    </div>
  );
};
