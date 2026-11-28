import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { ChapterDetail } from '../types';
import {
  Search,
  Zap,
  FunctionSquare,
  BookOpenCheck,
  ChevronDown,
  ChevronRight,
  ListChecks,
  Play,
  ChevronsUpDown,
} from 'lucide-react';

export const ShortNotesPage: React.FC = () => {
  const [chapters, setChapters] = useState<ChapterDetail[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedSubject, setSelectedSubject] = useState<string>('all');
  const [search, setSearch] = useState('');
  const [expandedIds, setExpandedIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    const fetchShortNotes = async () => {
      try {
        const res = await api.get<ChapterDetail[]>('/practice/short-notes');
        setChapters(res.data);
        // Expand the first 3 chapters by default for immediate reading
        const initial = new Set<number>();
        res.data.slice(0, 3).forEach((c) => initial.add(c.id));
        setExpandedIds(initial);
      } catch (err) {
        console.error('Failed to load short notes', err);
      } finally {
        setLoading(false);
      }
    };
    fetchShortNotes();
  }, []);

  const subjects = Array.from(
    new Map(chapters.map((c) => [c.categorySlug, c.categoryName])).entries()
  ).map(([slug, name]) => ({
    slug,
    name,
    count: chapters.filter((c) => c.categorySlug === slug).length,
  }));

  const normalizedSearch = search.trim().toLowerCase();
  const filteredChapters = chapters.filter((c) => {
    const matchesSubject =
      selectedSubject === 'all' || c.categorySlug === selectedSubject;
    if (!matchesSubject) return false;
    if (!normalizedSearch) return true;

    const inTitle = c.title.toLowerCase().includes(normalizedSearch);
    const inOverview = c.studyMaterial?.overview?.toLowerCase().includes(normalizedSearch);
    const inShortcuts = c.studyMaterial?.shortcuts?.some((s) =>
      s.toLowerCase().includes(normalizedSearch)
    );
    const inFormulas = c.studyMaterial?.formulas?.some(
      (f) =>
        f.label.toLowerCase().includes(normalizedSearch) ||
        f.expression.toLowerCase().includes(normalizedSearch)
    );
    return inTitle || inOverview || inShortcuts || inFormulas;
  });

  const toggleExpand = (id: number) => {
    setExpandedIds((prev) => {
      const next = new Set(prev);
      if (next.has(id)) next.delete(id);
      else next.add(id);
      return next;
    });
  };

  const allExpanded =
    filteredChapters.length > 0 &&
    filteredChapters.every((c) => expandedIds.has(c.id));

  const toggleExpandAll = () => {
    if (allExpanded) {
      setExpandedIds(new Set());
    } else {
      setExpandedIds(new Set(filteredChapters.map((c) => c.id)));
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6">
      {/* Header */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">
              Quick Revision Cheat-Sheets
            </p>
            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight mt-0.5">
              Short Notes &amp; Speed-Solving Tricks
            </h1>
            <p className="text-sm text-slate-500 mt-1 max-w-2xl">
              Concise formulas, mental shortcuts, and <span className="font-mono text-slate-700">&lt;30s</span> solving tricks across all {chapters.length} aptitude chapters.
            </p>
          </div>

          <button
            type="button"
            onClick={toggleExpandAll}
            className="inline-flex items-center space-x-1.5 px-3.5 py-2 rounded-lg text-xs font-semibold border border-slate-200 bg-slate-50 hover:bg-slate-100 text-slate-700 transition-colors self-start sm:self-auto flex-shrink-0"
          >
            <ChevronsUpDown className="w-4 h-4" />
            <span>{allExpanded ? 'Collapse All' : 'Expand All'}</span>
          </button>
        </div>

        {/* Search & Subject Tabs */}
        <div className="pt-3 border-t border-slate-100 flex flex-col lg:flex-row lg:items-center justify-between gap-3">
          <div className="flex flex-wrap items-center gap-1.5">
            <button
              type="button"
              onClick={() => setSelectedSubject('all')}
              className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors ${
                selectedSubject === 'all'
                  ? 'bg-slate-900 text-white'
                  : 'bg-slate-100 text-slate-600 hover:bg-slate-200/70'
              }`}
            >
              All ({chapters.length})
            </button>
            {subjects.map((sub) => (
              <button
                key={sub.slug}
                type="button"
                onClick={() => setSelectedSubject(sub.slug)}
                className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors ${
                  selectedSubject === sub.slug
                    ? 'bg-slate-900 text-white'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200/70'
                }`}
              >
                {sub.name} ({sub.count})
              </button>
            ))}
          </div>

          <div className="relative w-full lg:w-72">
            <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search formulas or tricks..."
              className="w-full pl-9 pr-3 py-2 rounded-lg border border-slate-300 text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600"
            />
          </div>
        </div>
      </section>

      {/* Chapters Accordion Cards */}
      {loading ? (
        <div className="space-y-3">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="h-24 rounded-xl bg-slate-200/60 animate-pulse" />
          ))}
        </div>
      ) : filteredChapters.length === 0 ? (
        <div className="bg-white rounded-xl border border-slate-200 p-10 text-center text-sm text-slate-500">
          No chapters or tricks match your search query.
        </div>
      ) : (
        <div className="space-y-3">
          {filteredChapters.map((ch) => {
            const isOpen = expandedIds.has(ch.id);
            return (
              <article
                key={ch.id}
                className="bg-white rounded-xl border border-slate-200 shadow-xs overflow-hidden"
              >
                {/* Card Header (Always Visible) */}
                <div className="p-4 sm:p-5 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                  <button
                    type="button"
                    onClick={() => toggleExpand(ch.id)}
                    className="flex items-start sm:items-center space-x-3 text-left flex-1 min-w-0 group"
                    aria-expanded={isOpen}
                  >
                    <div className="w-8 h-8 rounded-md bg-slate-100 border border-slate-200 text-slate-600 flex items-center justify-center flex-shrink-0 mt-0.5 sm:mt-0">
                      <ChevronDown
                        className={`w-4 h-4 transition-transform ${
                          isOpen ? 'rotate-180 text-blue-600' : ''
                        }`}
                      />
                    </div>
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className="text-[11px] font-semibold uppercase tracking-wider text-blue-600">
                          {ch.categoryName}
                        </span>
                        <span className="text-[11px] font-mono font-medium px-1.5 py-0.5 rounded bg-slate-100 text-slate-700">
                          {ch.totalQuestions} Qs
                        </span>
                      </div>
                      <h2 className="text-base font-bold text-slate-900 group-hover:text-blue-600 transition-colors mt-0.5">
                        {ch.title}
                      </h2>
                    </div>
                  </button>

                  <div className="flex items-center space-x-2 flex-shrink-0 pl-11 sm:pl-0">
                    <Link
                      to={`/chapters/${ch.id}?tab=questions`}
                      className="inline-flex items-center space-x-1 px-2.5 py-1.5 rounded-md text-xs font-medium border border-slate-200 bg-white text-slate-700 hover:bg-slate-50 transition-colors"
                    >
                      <ListChecks className="w-3.5 h-3.5 text-emerald-600" />
                      <span>All {ch.totalQuestions} Qs</span>
                    </Link>
                    <Link
                      to={`/practice/${ch.id}?title=${encodeURIComponent(ch.title)}`}
                      className="inline-flex items-center space-x-1 px-3 py-1.5 rounded-md text-xs font-semibold bg-slate-900 text-white hover:bg-slate-800 transition-colors"
                    >
                      <Play className="w-3 h-3 fill-white" />
                      <span>Practice</span>
                    </Link>
                  </div>
                </div>

                {/* Expandable Short Notes & Speed Tricks Content */}
                {isOpen && ch.studyMaterial && (
                  <div className="border-t border-slate-100 bg-slate-50/50 p-4 sm:p-6 space-y-5">
                    <p className="text-xs sm:text-sm text-slate-600 leading-relaxed">
                      {ch.studyMaterial.overview}
                    </p>

                    <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
                      {/* Core Formulas */}
                      {ch.studyMaterial.formulas && ch.studyMaterial.formulas.length > 0 && (
                        <div className="bg-white rounded-lg border border-slate-200 p-4 space-y-3">
                          <div className="flex items-center space-x-1.5 text-xs font-semibold uppercase tracking-wider text-slate-700">
                            <FunctionSquare className="w-4 h-4 text-blue-600" />
                            <span>Key Formulas</span>
                          </div>
                          <div className="space-y-2">
                            {ch.studyMaterial.formulas.map((f, idx) => (
                              <div
                                key={idx}
                                className="p-2.5 rounded-md bg-slate-50 border border-slate-200/80"
                              >
                                <span className="block text-[11px] font-semibold text-slate-600">
                                  {f.label}
                                </span>
                                <code className="block text-xs font-mono text-blue-700 mt-0.5 break-words">
                                  {f.expression}
                                </code>
                              </div>
                            ))}
                          </div>
                        </div>
                      )}

                      {/* Speed-Solving Tricks */}
                      {ch.studyMaterial.shortcuts && ch.studyMaterial.shortcuts.length > 0 && (
                        <div className="bg-white rounded-lg border border-slate-200 p-4 space-y-3">
                          <div className="flex items-center space-x-1.5 text-xs font-semibold uppercase tracking-wider text-slate-700">
                            <Zap className="w-4 h-4 text-amber-600" />
                            <span>Speed-Solving Tricks (&lt; 30s)</span>
                          </div>
                          <ul className="space-y-2">
                            {ch.studyMaterial.shortcuts.map((trick, idx) => (
                              <li
                                key={idx}
                                className="flex items-start space-x-2 text-xs sm:text-sm text-slate-700"
                              >
                                <span className="w-5 h-5 rounded bg-amber-50 border border-amber-200 text-amber-800 font-mono text-[11px] font-semibold flex items-center justify-center flex-shrink-0 mt-0.5">
                                  {idx + 1}
                                </span>
                                <span className="leading-relaxed">{trick}</span>
                              </li>
                            ))}
                          </ul>
                        </div>
                      )}
                    </div>

                    {/* Worked Example */}
                    {ch.studyMaterial.workedExample && (
                      <div className="bg-white rounded-lg border border-slate-200 p-4 space-y-2">
                        <div className="flex items-center space-x-1.5 text-xs font-semibold uppercase tracking-wider text-emerald-700">
                          <BookOpenCheck className="w-4 h-4" />
                          <span>Fast Worked Example</span>
                        </div>
                        <p className="text-xs sm:text-sm font-medium text-slate-900">
                          {ch.studyMaterial.workedExample.problem}
                        </p>
                        <p className="text-xs font-mono text-slate-700 bg-slate-50 p-2.5 rounded border border-slate-200/80 whitespace-pre-line">
                          {ch.studyMaterial.workedExample.solution}
                        </p>
                      </div>
                    )}
                  </div>
                )}
              </article>
            );
          })}
        </div>
      )}
    </div>
  );
};
