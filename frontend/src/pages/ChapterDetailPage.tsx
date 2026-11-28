import React, { useState, useEffect } from 'react';
import { Link, useParams, useSearchParams } from 'react-router-dom';
import api from '../api/client';
import { ChapterDetail, QuestionListItem, PaginatedResponse } from '../types';
import {
  ChevronRight,
  FileText,
  ListChecks,
  Play,
  Search,
  CheckCircle2,
  BookOpenCheck,
  ChevronLeft,
  Lightbulb,
  FunctionSquare,
} from 'lucide-react';

export const ChapterDetailPage: React.FC = () => {
  const { chapterId } = useParams<{ chapterId: string }>();
  const [searchParams, setSearchParams] = useSearchParams();
  const initialTab = searchParams.get('tab') === 'questions' ? 'questions' : 'study';

  const [activeTab, setActiveTab] = useState<'study' | 'questions'>(initialTab);
  const [detail, setDetail] = useState<ChapterDetail | null>(null);
  const [loadingDetail, setLoadingDetail] = useState(true);

  // Question List state
  const [questionsPage, setQuestionsPage] = useState<PaginatedResponse<QuestionListItem> | null>(
    null
  );
  const [loadingList, setLoadingList] = useState(false);
  const [page, setPage] = useState(0);
  const [difficulty, setDifficulty] = useState<'ALL' | 'EASY' | 'MEDIUM' | 'HARD'>('ALL');
  const [search, setSearch] = useState('');
  const [debouncedSearch, setDebouncedSearch] = useState('');
  const [expandedSolutions, setExpandedSolutions] = useState<Set<string>>(new Set());

  const PAGE_SIZE = 20;

  useEffect(() => {
    const tabParam = searchParams.get('tab');
    if (tabParam === 'questions' || tabParam === 'study') {
      setActiveTab(tabParam);
    }
  }, [searchParams]);

  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearch(search);
      setPage(0);
    }, 300);
    return () => clearTimeout(timer);
  }, [search]);

  useEffect(() => {
    const fetchDetails = async () => {
      setLoadingDetail(true);
      try {
        const res = await api.get<ChapterDetail>(`/practice/chapters/${chapterId}/details`);
        setDetail(res.data);
      } catch (err) {
        console.error('Failed to load chapter details', err);
      } finally {
        setLoadingDetail(false);
      }
    };
    fetchDetails();
  }, [chapterId]);

  useEffect(() => {
    if (activeTab !== 'questions') return;
    const fetchQuestionList = async () => {
      setLoadingList(true);
      try {
        const params = new URLSearchParams({
          page: String(page),
          size: String(PAGE_SIZE),
        });
        if (difficulty !== 'ALL') params.set('difficulty', difficulty);
        if (debouncedSearch.trim()) params.set('search', debouncedSearch.trim());

        const res = await api.get<PaginatedResponse<QuestionListItem>>(
          `/practice/chapters/${chapterId}/question-list?${params.toString()}`
        );
        setQuestionsPage(res.data);
      } catch (err) {
        console.error('Failed to load question list', err);
      } finally {
        setLoadingList(false);
      }
    };
    fetchQuestionList();
  }, [chapterId, activeTab, page, difficulty, debouncedSearch]);

  const switchTab = (tab: 'study' | 'questions') => {
    setActiveTab(tab);
    setSearchParams({ tab });
  };

  const toggleSolution = (qId: string) => {
    setExpandedSolutions((prev) => {
      const next = new Set(prev);
      if (next.has(qId)) next.delete(qId);
      else next.add(qId);
      return next;
    });
  };

  if (loadingDetail || !detail) {
    return (
      <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-4">
        <div className="h-32 rounded-xl bg-slate-200/60 animate-pulse" />
        <div className="h-64 rounded-xl bg-slate-200/60 animate-pulse" />
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6">
      {/* Breadcrumb Navigation */}
      <nav className="flex flex-wrap items-center gap-1.5 text-xs text-slate-500" aria-label="Breadcrumb">
        <Link to="/subjects" className="hover:text-slate-900 font-medium">
          Subjects
        </Link>
        <ChevronRight className="w-3.5 h-3.5 text-slate-400" />
        <Link
          to={`/subjects?subject=${detail.categorySlug}`}
          className="hover:text-slate-900 font-medium"
        >
          {detail.categoryName}
        </Link>
        <ChevronRight className="w-3.5 h-3.5 text-slate-400" />
        <span className="text-slate-900 font-semibold">{detail.title}</span>
      </nav>

      {/* Chapter Header Card */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-5">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="space-y-1.5">
            <div className="flex flex-wrap items-center gap-2">
              <span className="text-xs font-semibold uppercase tracking-wider text-blue-600">
                {detail.categoryName}
              </span>
              <span className="text-slate-300">•</span>
              <span className="text-xs font-mono font-semibold text-slate-700">
                {detail.totalQuestions} MCQs Available
              </span>
            </div>
            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
              {detail.title}
            </h1>
            <p className="text-sm text-slate-500 max-w-2xl">{detail.description}</p>
          </div>

          <Link
            to={`/practice/${detail.id}?title=${encodeURIComponent(detail.title)}`}
            className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors self-start md:self-auto flex-shrink-0"
          >
            <Play className="w-4 h-4 fill-white" />
            <span>Start Practice Mode</span>
          </Link>
        </div>

        {/* Difficulty Breakdown & Tab Selector */}
        <div className="pt-4 border-t border-slate-100 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex flex-wrap items-center gap-1 p-1 bg-slate-100 border border-slate-200 rounded-lg self-start">
            <button
              type="button"
              onClick={() => switchTab('study')}
              className={`inline-flex items-center space-x-1.5 px-3.5 py-1.5 rounded-md text-xs sm:text-sm font-semibold transition-colors ${
                activeTab === 'study'
                  ? 'bg-white text-slate-900 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <FileText className="w-4 h-4 text-blue-600" />
              <span>Short Notes &amp; Tricks</span>
            </button>

            <button
              type="button"
              onClick={() => switchTab('questions')}
              className={`inline-flex items-center space-x-1.5 px-3.5 py-1.5 rounded-md text-xs sm:text-sm font-semibold transition-colors ${
                activeTab === 'questions'
                  ? 'bg-white text-slate-900 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <ListChecks className="w-4 h-4 text-emerald-600" />
              <span>All Questions ({detail.totalQuestions})</span>
            </button>
          </div>

          <div className="flex flex-wrap items-center gap-2 text-[11px] font-mono">
            <span className="px-2 py-1 rounded-md bg-emerald-50 border border-emerald-200 text-emerald-800">
              Easy: {detail.easyCount}
            </span>
            <span className="px-2 py-1 rounded-md bg-amber-50 border border-amber-200 text-amber-800">
              Medium: {detail.mediumCount}
            </span>
            <span className="px-2 py-1 rounded-md bg-rose-50 border border-rose-200 text-rose-800">
              Hard: {detail.hardCount}
            </span>
          </div>
        </div>
      </section>

      {/* TAB 1: Study Material & Formulas */}
      {activeTab === 'study' && (
        <div className="space-y-6">
          {/* Concept Overview */}
          <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-3">
            <h2 className="text-base font-semibold text-slate-900">Concept Overview</h2>
            <p className="text-sm text-slate-600 leading-relaxed">
              {detail.studyMaterial.overview}
            </p>
          </section>

          {/* Key Formulas & Rules */}
          {detail.studyMaterial.formulas && detail.studyMaterial.formulas.length > 0 && (
            <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-4">
              <div className="flex items-center space-x-2">
                <FunctionSquare className="w-4 h-4 text-blue-600" />
                <h2 className="text-base font-semibold text-slate-900">
                  Core Formulas &amp; Rules
                </h2>
              </div>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {detail.studyMaterial.formulas.map((f, i) => (
                  <div
                    key={i}
                    className="p-3.5 rounded-lg bg-slate-50 border border-slate-200/80 space-y-1"
                  >
                    <span className="block text-xs font-semibold text-slate-700">{f.label}</span>
                    <code className="block text-xs sm:text-sm font-mono text-blue-700 break-words">
                      {f.expression}
                    </code>
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Exam Shortcuts */}
          {detail.studyMaterial.shortcuts && detail.studyMaterial.shortcuts.length > 0 && (
            <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-3">
              <div className="flex items-center space-x-2">
                <Lightbulb className="w-4 h-4 text-amber-600" />
                <h2 className="text-base font-semibold text-slate-900">
                  Speed-Solving Tricks (&lt; 30 Seconds)
                </h2>
              </div>
              <ul className="space-y-2">
                {detail.studyMaterial.shortcuts.map((tip, i) => (
                  <li key={i} className="flex items-start space-x-2.5 text-sm text-slate-600">
                    <span className="w-5 h-5 rounded-md bg-slate-100 border border-slate-200 text-slate-700 font-mono text-[11px] font-semibold flex items-center justify-center flex-shrink-0 mt-0.5">
                      {i + 1}
                    </span>
                    <span className="leading-relaxed">{tip}</span>
                  </li>
                ))}
              </ul>
            </section>
          )}

          {/* Worked Example */}
          {detail.studyMaterial.workedExample && (
            <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-4">
              <div className="flex items-center space-x-2">
                <BookOpenCheck className="w-4 h-4 text-emerald-600" />
                <h2 className="text-base font-semibold text-slate-900">Worked Example</h2>
              </div>
              <div className="p-4 rounded-lg bg-slate-50 border border-slate-200 space-y-3">
                <div>
                  <span className="text-[11px] font-semibold uppercase tracking-wider text-slate-400">
                    Problem
                  </span>
                  <p className="text-sm font-medium text-slate-900 mt-1">
                    {detail.studyMaterial.workedExample.problem}
                  </p>
                </div>
                <div className="pt-3 border-t border-slate-200">
                  <span className="text-[11px] font-semibold uppercase tracking-wider text-emerald-700">
                    Solution
                  </span>
                  <p className="text-xs sm:text-sm font-mono text-slate-800 mt-1 whitespace-pre-line leading-relaxed">
                    {detail.studyMaterial.workedExample.solution}
                  </p>
                </div>
              </div>

              <div className="flex justify-end">
                <button
                  type="button"
                  onClick={() => switchTab('questions')}
                  className="inline-flex items-center space-x-1.5 px-4 py-2 rounded-lg text-xs sm:text-sm font-semibold text-white bg-blue-600 hover:bg-blue-700 transition-colors"
                >
                  <span>Browse All {detail.totalQuestions} Questions</span>
                  <ChevronRight className="w-4 h-4" />
                </button>
              </div>
            </section>
          )}
        </div>
      )}

      {/* TAB 2: All 100+ Questions List */}
      {activeTab === 'questions' && (
        <div className="space-y-4">
          {/* Search & Difficulty Controls */}
          <div className="bg-white rounded-xl border border-slate-200 p-4 shadow-xs flex flex-col md:flex-row md:items-center justify-between gap-3">
            <div className="relative flex-1 max-w-md">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2" />
              <input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search inside questions..."
                className="w-full pl-9 pr-3 py-2 rounded-lg border border-slate-300 text-xs sm:text-sm text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600"
              />
            </div>

            <div className="flex flex-wrap items-center gap-1.5">
              {(['ALL', 'EASY', 'MEDIUM', 'HARD'] as const).map((diff) => (
                <button
                  key={diff}
                  type="button"
                  onClick={() => {
                    setDifficulty(diff);
                    setPage(0);
                  }}
                  className={`px-3 py-1.5 rounded-md text-xs font-semibold transition-colors ${
                    difficulty === diff
                      ? 'bg-slate-900 text-white'
                      : 'bg-slate-100 text-slate-600 hover:bg-slate-200/70'
                  }`}
                >
                  {diff === 'ALL' ? 'All' : diff}
                </button>
              ))}
            </div>
          </div>

          {/* Question List Items */}
          {loadingList || !questionsPage ? (
            <div className="space-y-3">
              {[1, 2, 3, 4].map((i) => (
                <div key={i} className="h-32 rounded-xl bg-slate-200/60 animate-pulse" />
              ))}
            </div>
          ) : questionsPage.content.length === 0 ? (
            <div className="bg-white rounded-xl border border-slate-200 p-10 text-center text-sm text-slate-500">
              No questions match your search or filter criteria.
            </div>
          ) : (
            <div className="space-y-3">
              {questionsPage.content.map((q, idx) => {
                const globalNumber = page * PAGE_SIZE + idx + 1;
                const isExpanded = expandedSolutions.has(q.id);
                const canJumpDirect = difficulty === 'ALL' && !debouncedSearch.trim();

                return (
                  <div
                    key={q.id}
                    className="bg-white rounded-xl border border-slate-200 p-4 sm:p-5 shadow-xs space-y-4"
                  >
                    <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-100 pb-3">
                      <div className="flex items-center space-x-2.5">
                        <span className="px-2 py-0.5 rounded-md bg-slate-900 text-white font-mono text-xs font-semibold">
                          #{globalNumber.toString().padStart(2, '0')}
                        </span>
                        <span
                          className={`text-[11px] font-mono font-semibold px-2 py-0.5 rounded-md border uppercase ${
                            q.difficulty === 'EASY'
                              ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                              : q.difficulty === 'MEDIUM'
                              ? 'bg-amber-50 text-amber-700 border-amber-200'
                              : 'bg-rose-50 text-rose-700 border-rose-200'
                          }`}
                        >
                          {q.difficulty}
                        </span>
                      </div>

                      <div className="flex items-center space-x-2">
                        <button
                          type="button"
                          onClick={() => toggleSolution(q.id)}
                          className="px-2.5 py-1 rounded-md text-xs font-medium border border-slate-200 text-slate-700 hover:bg-slate-50 transition-colors"
                        >
                          {isExpanded ? 'Hide Solution' : 'Show Answer & Solution'}
                        </button>
                        {canJumpDirect && (
                          <Link
                            to={`/practice/${detail.id}?title=${encodeURIComponent(
                              detail.title
                            )}&index=${globalNumber - 1}`}
                            className="inline-flex items-center space-x-1 px-2.5 py-1 rounded-md text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-200 hover:bg-blue-100 transition-colors"
                          >
                            <span>Practice</span>
                            <ChevronRight className="w-3.5 h-3.5" />
                          </Link>
                        )}
                      </div>
                    </div>

                    <p className="text-sm sm:text-base font-medium text-slate-900 leading-relaxed whitespace-pre-line">
                      {q.questionText}
                    </p>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                      {q.options.map((opt) => {
                        const isCorr = isExpanded && opt.optionKey === q.correctOption;
                        return (
                          <div
                            key={opt.id}
                            className={`px-3 py-2 rounded-lg border text-xs sm:text-sm flex items-center justify-between ${
                              isCorr
                                ? 'bg-emerald-50/70 border-emerald-500 text-emerald-950 font-medium'
                                : 'bg-slate-50/60 border-slate-200 text-slate-700'
                            }`}
                          >
                            <div className="flex items-center space-x-2">
                              <span
                                className={`w-6 h-6 rounded flex items-center justify-center font-mono text-xs font-semibold flex-shrink-0 ${
                                  isCorr
                                    ? 'bg-emerald-600 text-white'
                                    : 'bg-white border border-slate-200 text-slate-700'
                                }`}
                              >
                                {opt.optionKey}
                              </span>
                              <span>{opt.optionText}</span>
                            </div>
                            {isCorr && <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />}
                          </div>
                        );
                      })}
                    </div>

                    {isExpanded && (
                      <div className="p-3.5 rounded-lg bg-slate-50 border border-slate-200 space-y-1.5">
                        <span className="text-[11px] font-semibold uppercase tracking-wider text-emerald-700">
                          Correct Answer: Option {q.correctOption}
                        </span>
                        <p className="text-xs sm:text-sm font-mono text-slate-800 whitespace-pre-line leading-relaxed">
                          {q.explanation}
                        </p>
                      </div>
                    )}
                  </div>
                );
              })}

              {/* Pagination Controls */}
              <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col sm:flex-row items-center justify-between gap-3">
                <span className="text-xs text-slate-500">
                  Showing{' '}
                  <strong className="font-mono text-slate-900">
                    {page * PAGE_SIZE + 1}–
                    {Math.min((page + 1) * PAGE_SIZE, questionsPage.totalElements)}
                  </strong>{' '}
                  of{' '}
                  <strong className="font-mono text-slate-900">
                    {questionsPage.totalElements}
                  </strong>{' '}
                  questions
                </span>

                <div className="flex items-center space-x-1.5">
                  <button
                    type="button"
                    disabled={page === 0}
                    onClick={() => {
                      setPage((p) => p - 1);
                      window.scrollTo({ top: 0, behavior: 'smooth' });
                    }}
                    className="px-3 py-1.5 rounded-md border border-slate-200 text-xs font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-40 inline-flex items-center space-x-1"
                  >
                    <ChevronLeft className="w-3.5 h-3.5" />
                    <span>Prev</span>
                  </button>

                  {Array.from({ length: questionsPage.totalPages }).map((_, pIdx) => (
                    <button
                      key={pIdx}
                      type="button"
                      onClick={() => {
                        setPage(pIdx);
                        window.scrollTo({ top: 0, behavior: 'smooth' });
                      }}
                      className={`w-8 h-8 rounded-md text-xs font-mono font-semibold transition-colors ${
                        pIdx === page
                          ? 'bg-slate-900 text-white'
                          : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                      }`}
                    >
                      {pIdx + 1}
                    </button>
                  ))}

                  <button
                    type="button"
                    disabled={page >= questionsPage.totalPages - 1}
                    onClick={() => {
                      setPage((p) => p + 1);
                      window.scrollTo({ top: 0, behavior: 'smooth' });
                    }}
                    className="px-3 py-1.5 rounded-md border border-slate-200 text-xs font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-40 inline-flex items-center space-x-1"
                  >
                    <span>Next</span>
                    <ChevronRight className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
