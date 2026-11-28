import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import { Question, CheckAnswerResponse, PaginatedResponse } from '../types';
import {
  ArrowLeft,
  CheckCircle2,
  XCircle,
  BookOpenCheck,
  ChevronRight,
  ChevronLeft,
  Flame,
  FileQuestion,
  ListChecks,
} from 'lucide-react';

export const PracticeArenaPage: React.FC = () => {
  const { chapterId } = useParams<{ chapterId: string }>();
  const [searchParams] = useSearchParams();
  const chapterTitle = searchParams.get('title') || 'Chapter Practice';
  const startGlobalIndex = Math.max(0, parseInt(searchParams.get('index') || '0', 10) || 0);

  const PAGE_SIZE = 20;
  const initialPage = Math.floor(startGlobalIndex / PAGE_SIZE);
  const initialOffset = startGlobalIndex % PAGE_SIZE;

  const { refreshStreak, streak } = useAuth();
  const navigate = useNavigate();

  const [pageData, setPageData] = useState<PaginatedResponse<Question> | null>(null);
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [currentIndex, setCurrentIndex] = useState(initialOffset);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [answerResult, setAnswerResult] = useState<CheckAnswerResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [solvedCount, setSolvedCount] = useState(0);

  useEffect(() => {
    const fetchQuestions = async () => {
      setLoading(true);
      try {
        const res = await api.get<PaginatedResponse<Question>>(
          `/practice/chapters/${chapterId}/questions?page=${currentPage}&size=${PAGE_SIZE}`
        );
        setPageData(res.data);
        if (currentIndex >= res.data.content.length && res.data.content.length > 0) {
          setCurrentIndex(0);
        }
      } catch (err) {
        console.error('Failed to load questions', err);
      } finally {
        setLoading(false);
      }
    };
    fetchQuestions();
  }, [chapterId, currentPage]);

  const handleSelectOption = (key: string) => {
    if (answerResult) return;
    setSelectedOption(key);
  };

  const handleCheckAnswer = async () => {
    if (!selectedOption || !currentQuestion) return;
    setSubmitting(true);

    try {
      const res = await api.post<CheckAnswerResponse>('/practice/check-answer', {
        questionId: currentQuestion.id,
        selectedOption: selectedOption,
      });
      setAnswerResult(res.data);
      setSolvedCount((prev) => prev + 1);
      await refreshStreak();
    } catch (err) {
      console.error('Failed to check answer', err);
    } finally {
      setSubmitting(false);
    }
  };

  const handleNext = () => {
    setSelectedOption(null);
    setAnswerResult(null);
    if (!pageData) return;

    if (currentIndex < pageData.content.length - 1) {
      setCurrentIndex((prev) => prev + 1);
    } else if (currentPage < pageData.totalPages - 1) {
      setCurrentPage((p) => p + 1);
      setCurrentIndex(0);
    }
  };

  const handlePrevious = () => {
    setSelectedOption(null);
    setAnswerResult(null);
    if (currentIndex > 0) {
      setCurrentIndex((prev) => prev - 1);
    } else if (currentPage > 0) {
      setCurrentPage((p) => p - 1);
      setCurrentIndex(PAGE_SIZE - 1);
    }
  };

  const questions = pageData?.content || [];
  const totalElements = pageData?.totalElements || 0;
  const globalNumber = currentPage * PAGE_SIZE + currentIndex + 1;
  const currentQuestion = questions[currentIndex];

  if (loading) {
    return (
      <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center">
        <div className="text-center space-y-3">
          <div className="w-8 h-8 border-2 border-slate-900 border-t-transparent rounded-full animate-spin mx-auto" />
          <p className="text-xs font-medium text-slate-500">Loading chapter questions...</p>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="max-w-md mx-auto py-16 px-4 text-center space-y-4">
        <div className="w-12 h-12 rounded-xl bg-slate-100 border border-slate-200 text-slate-600 flex items-center justify-center mx-auto">
          <FileQuestion className="w-6 h-6" />
        </div>
        <div className="space-y-1">
          <h2 className="text-lg font-bold text-slate-900">No Questions Available</h2>
          <p className="text-sm text-slate-500">
            This chapter currently has no practice questions. You can import MCQs via the Question Bank portal.
          </p>
        </div>
        <button
          onClick={() => navigate('/subjects')}
          className="px-4 py-2 bg-slate-900 hover:bg-slate-800 text-white rounded-lg text-sm font-medium transition-colors"
        >
          Return to Subjects
        </button>
      </div>
    );
  }

  const isAnswerCorrect = Boolean(answerResult?.isCorrect || answerResult?.correct);
  const hasMoreQuestions = globalNumber < totalElements;

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 py-6 sm:py-8 space-y-5">
      {/* Top Bar */}
      <div className="flex items-center justify-between bg-white px-4 py-3 rounded-xl border border-slate-200 shadow-xs gap-2">
        <Link
          to={`/chapters/${chapterId}`}
          className="inline-flex items-center space-x-1.5 text-xs sm:text-sm font-medium text-slate-600 hover:text-slate-900 transition-colors flex-shrink-0"
        >
          <ArrowLeft className="w-4 h-4" />
          <span className="hidden sm:inline">Study &amp; List</span>
          <span className="sm:hidden">Back</span>
        </Link>

        <div className="text-center min-w-0">
          <h1 className="text-sm font-semibold text-slate-900 truncate">
            {chapterTitle}
          </h1>
          <span className="text-[11px] font-mono text-slate-500">
            Question {globalNumber} / {totalElements}
          </span>
        </div>

        <div className="flex items-center space-x-2 flex-shrink-0">
          <Link
            to={`/chapters/${chapterId}?tab=questions`}
            className="hidden sm:inline-flex items-center space-x-1 px-2.5 py-1 rounded-md bg-slate-100 hover:bg-slate-200/70 text-xs font-medium text-slate-700 transition-colors"
            title="View full 100+ question list"
          >
            <ListChecks className="w-3.5 h-3.5" />
            <span>List</span>
          </Link>
          <div className="flex items-center space-x-1.5 px-2.5 py-1 rounded-md bg-amber-50/70 border border-amber-200/80 text-amber-900 text-xs font-mono font-semibold">
            <Flame className="w-3.5 h-3.5 text-amber-600 fill-amber-500" />
            <span>{streak?.currentStreak || 0}d</span>
          </div>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="w-full bg-slate-200 rounded-sm h-1.5 overflow-hidden">
        <div
          className="bg-blue-600 h-1.5 transition-all duration-300"
          style={{ width: `${(globalNumber / Math.max(1, totalElements)) * 100}%` }}
        />
      </div>

      {/* Question Card */}
      <div className="bg-white rounded-xl border border-slate-200 p-5 sm:p-7 shadow-xs space-y-6">
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <span
              className={`inline-block text-[11px] font-mono font-semibold px-2 py-0.5 rounded-md border uppercase tracking-wider ${
                currentQuestion.difficulty === 'EASY'
                  ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                  : currentQuestion.difficulty === 'MEDIUM'
                  ? 'bg-amber-50 text-amber-700 border-amber-200'
                  : 'bg-rose-50 text-rose-700 border-rose-200'
              }`}
            >
              {currentQuestion.difficulty}
            </span>
          </div>

          <h2 className="text-base sm:text-lg font-medium text-slate-900 leading-relaxed whitespace-pre-line">
            {currentQuestion.questionText}
          </h2>
        </div>

        {/* Selectable Options */}
        <div className="space-y-2.5">
          {currentQuestion.options.map((opt) => {
            const isSelected = selectedOption === opt.optionKey;
            const isEvaluated = answerResult !== null;
            const isCorrectOption = answerResult?.correctOption === opt.optionKey;
            const isWrongSelected = isEvaluated && isSelected && !isAnswerCorrect;

            let optionStyle =
              'border-slate-200 hover:border-slate-300 hover:bg-slate-50 text-slate-800';
            let badgeStyle = 'bg-slate-100 text-slate-700 border-slate-200';

            if (isSelected && !isEvaluated) {
              optionStyle = 'border-blue-600 bg-blue-50/40 text-slate-900';
              badgeStyle = 'bg-blue-600 text-white border-blue-600';
            } else if (isEvaluated) {
              if (isCorrectOption) {
                optionStyle = 'border-emerald-600 bg-emerald-50/50 text-emerald-950 font-medium';
                badgeStyle = 'bg-emerald-600 text-white border-emerald-600';
              } else if (isWrongSelected) {
                optionStyle = 'border-rose-600 bg-rose-50/50 text-rose-950';
                badgeStyle = 'bg-rose-600 text-white border-rose-600';
              } else {
                optionStyle = 'border-slate-200 opacity-60 text-slate-500';
              }
            }

            return (
              <button
                key={opt.id}
                type="button"
                onClick={() => handleSelectOption(opt.optionKey)}
                disabled={isEvaluated}
                className={`w-full text-left p-3.5 sm:p-4 rounded-lg border transition-colors flex items-center justify-between gap-3 ${optionStyle}`}
              >
                <div className="flex items-center space-x-3.5">
                  <span
                    className={`w-7 h-7 rounded-md border flex items-center justify-center font-mono font-semibold text-xs flex-shrink-0 ${badgeStyle}`}
                  >
                    {opt.optionKey}
                  </span>
                  <span className="text-sm sm:text-base">{opt.optionText}</span>
                </div>

                {isEvaluated && isCorrectOption && (
                  <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0" />
                )}
                {isEvaluated && isWrongSelected && (
                  <XCircle className="w-5 h-5 text-rose-600 flex-shrink-0" />
                )}
              </button>
            );
          })}
        </div>

        {/* Submit & Nav Bar */}
        {!answerResult && (
          <div className="pt-2 flex items-center justify-between gap-3">
            <button
              type="button"
              onClick={handlePrevious}
              disabled={globalNumber <= 1}
              className="px-4 py-2 rounded-lg text-xs sm:text-sm font-medium border border-slate-200 text-slate-700 hover:bg-slate-50 disabled:opacity-40 inline-flex items-center space-x-1"
            >
              <ChevronLeft className="w-4 h-4" />
              <span>Previous</span>
            </button>
            <button
              onClick={handleCheckAnswer}
              disabled={!selectedOption || submitting}
              className="px-6 py-2.5 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors disabled:opacity-40 disabled:cursor-not-allowed"
            >
              {submitting ? 'Verifying...' : 'Check Answer'}
            </button>
          </div>
        )}

        {/* Solution Breakdown */}
        {answerResult && (
          <div className="space-y-4 pt-4 border-t border-slate-200">
            <div
              className={`p-4 rounded-lg border flex items-start space-x-3 ${
                isAnswerCorrect
                  ? 'bg-emerald-50/70 border-emerald-200 text-emerald-950'
                  : 'bg-rose-50/70 border-rose-200 text-rose-950'
              }`}
            >
              {isAnswerCorrect ? (
                <CheckCircle2 className="w-5 h-5 text-emerald-600 flex-shrink-0 mt-0.5" />
              ) : (
                <XCircle className="w-5 h-5 text-rose-600 flex-shrink-0 mt-0.5" />
              )}
              <div>
                <p className="font-semibold text-sm">
                  {isAnswerCorrect
                    ? 'Correct answer.'
                    : `Incorrect. The correct answer is Option ${answerResult.correctOption}.`}
                </p>
                <p className="text-xs text-slate-600 mt-0.5">
                  Daily activity recorded. Active streak: <span className="font-mono font-semibold">{answerResult.currentStreak} day(s)</span>.
                </p>
              </div>
            </div>

            <div className="bg-slate-50 border border-slate-200 rounded-lg p-4 sm:p-5 space-y-2.5">
              <div className="flex items-center space-x-2 text-slate-900 font-semibold text-xs uppercase tracking-wider">
                <BookOpenCheck className="w-4 h-4 text-blue-600" />
                <span>Step-by-Step Solution</span>
              </div>
              <div className="text-slate-800 text-xs sm:text-sm leading-relaxed whitespace-pre-line font-mono bg-white p-3.5 rounded-md border border-slate-200">
                {answerResult.explanation}
              </div>
            </div>

            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pt-1">
              <span className="text-xs text-slate-500">
                Solved in session: <span className="font-mono font-semibold text-slate-900">{solvedCount}</span>
              </span>
              <div className="flex items-center space-x-2">
                <button
                  type="button"
                  onClick={handlePrevious}
                  disabled={globalNumber <= 1}
                  className="px-4 py-2 rounded-lg text-xs sm:text-sm font-medium border border-slate-200 text-slate-700 hover:bg-slate-50 disabled:opacity-40 inline-flex items-center space-x-1"
                >
                  <ChevronLeft className="w-4 h-4" />
                  <span>Prev</span>
                </button>
                {hasMoreQuestions ? (
                  <button
                    onClick={handleNext}
                    className="inline-flex items-center justify-center space-x-2 px-5 py-2 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors"
                  >
                    <span>Next Question</span>
                    <ChevronRight className="w-4 h-4" />
                  </button>
                ) : (
                  <button
                    onClick={() => navigate(`/chapters/${chapterId}`)}
                    className="inline-flex items-center justify-center space-x-2 px-5 py-2 rounded-lg text-sm font-semibold text-white bg-emerald-700 hover:bg-emerald-800 transition-colors"
                  >
                    <span>Finish Chapter</span>
                    <CheckCircle2 className="w-4 h-4" />
                  </button>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
