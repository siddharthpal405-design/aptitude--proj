import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import api from '../api/client';
import { TestResultResponse, QuestionReview } from '../types';
import { Award, CheckCircle2, XCircle, Clock, Target, ArrowRight, RotateCcw, Lightbulb, Filter, BookOpen } from 'lucide-react';

export const MockTestResultPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId: string }>();
  const navigate = useNavigate();

  const [result, setResult] = useState<TestResultResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'ALL' | 'CORRECT' | 'INCORRECT' | 'SKIPPED'>('ALL');

  useEffect(() => {
    const fetchResult = async () => {
      try {
        const res = await api.get<TestResultResponse>(`/mock-tests/sessions/${sessionId}/result`);
        setResult(res.data);
      } catch (err) {
        console.error('Failed to load result', err);
      } finally {
        setLoading(false);
      }
    };
    fetchResult();
  }, [sessionId]);

  if (loading || !result) {
    return (
      <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center">
        <div className="text-center space-y-3">
          <div className="w-10 h-10 border-4 border-sky-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-sm text-slate-500">Calculating your performance metrics...</p>
        </div>
      </div>
    );
  }

  const filteredQuestions = result.reviewList.filter((q) => {
    const isCorr = Boolean(q.isCorrect || q.correct);
    const isAtt = Boolean(q.isAttempted || q.attempted);

    if (filter === 'CORRECT') return isCorr;
    if (filter === 'INCORRECT') return isAtt && !isCorr;
    if (filter === 'SKIPPED') return !isAtt;
    return true;
  });

  const timeMins = Math.floor(result.totalTimeTakenSeconds / 60);
  const timeSecs = result.totalTimeTakenSeconds % 60;

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Scorecard Hero Banner */}
      <div className="rounded-3xl bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white p-6 sm:p-10 shadow-xl space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-white/10 pb-6">
          <div className="space-y-1">
            <span className="text-xs font-bold text-sky-400 uppercase tracking-wider">
              Examination Report
            </span>
            <h1 className="text-2xl sm:text-3xl font-extrabold">{result.testTitle}</h1>
          </div>
          <div className="flex items-center space-x-3">
            <Link
              to="/mock-tests"
              className="px-4 py-2 rounded-xl text-xs font-bold bg-white/10 hover:bg-white/20 border border-white/20 transition"
            >
              All Mock Tests
            </Link>
            <Link
              to="/"
              className="px-4 py-2 rounded-xl text-xs font-bold bg-sky-600 hover:bg-sky-500 transition shadow-sm"
            >
              Dashboard
            </Link>
          </div>
        </div>

        {/* Core Metrics Grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="bg-white/10 backdrop-blur-md border border-white/10 p-5 rounded-2xl text-center space-y-1">
            <span className="text-xs text-slate-300 font-medium">Total Score</span>
            <div className="text-3xl font-extrabold text-sky-400">
              {result.totalScore}
              <span className="text-sm text-slate-400 font-normal"> / {result.maxMarks}</span>
            </div>
          </div>

          <div className="bg-white/10 backdrop-blur-md border border-white/10 p-5 rounded-2xl text-center space-y-1">
            <span className="text-xs text-slate-300 font-medium">Accuracy</span>
            <div className="text-3xl font-extrabold text-emerald-400">
              {result.accuracyPercentage}%
            </div>
          </div>

          <div className="bg-white/10 backdrop-blur-md border border-white/10 p-5 rounded-2xl text-center space-y-1">
            <span className="text-xs text-slate-300 font-medium">Est. Percentile</span>
            <div className="text-3xl font-extrabold text-amber-300">
              {result.percentileEstimate}th
            </div>
          </div>

          <div className="bg-white/10 backdrop-blur-md border border-white/10 p-5 rounded-2xl text-center space-y-1">
            <span className="text-xs text-slate-300 font-medium">Time Taken</span>
            <div className="text-2xl font-extrabold text-slate-100">
              {timeMins}m {timeSecs}s
            </div>
          </div>
        </div>

        {/* Question Split Counter */}
        <div className="flex flex-wrap items-center justify-around bg-slate-950/40 p-4 rounded-2xl border border-white/5 text-xs text-slate-300 gap-4">
          <div>Total Questions: <strong className="text-white font-bold">{result.totalQuestions}</strong></div>
          <div>Attempted: <strong className="text-sky-400 font-bold">{result.attemptedCount}</strong></div>
          <div>Correct (🟢): <strong className="text-emerald-400 font-bold">{result.correctCount}</strong></div>
          <div>Incorrect (🔴): <strong className="text-rose-400 font-bold">{result.incorrectCount}</strong></div>
          <div>Skipped (⚪): <strong className="text-slate-400 font-bold">{result.skippedCount}</strong></div>
        </div>
      </div>

      {/* Review & Solutions Section */}
      <div className="space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div>
            <h2 className="text-xl font-bold text-slate-900">
              Question-by-Question Solution Review
            </h2>
            <p className="text-xs text-slate-500">
              Inspect your answers against the official step-by-step mathematical derivations.
            </p>
          </div>

          {/* Filter Pills */}
          <div className="flex items-center space-x-1.5 p-1 bg-white border border-slate-200 rounded-xl text-xs font-semibold">
            {(['ALL', 'CORRECT', 'INCORRECT', 'SKIPPED'] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => setFilter(tab)}
                className={`px-3 py-1.5 rounded-lg transition ${
                  filter === tab
                    ? 'bg-slate-900 text-white shadow-xs'
                    : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                {tab === 'ALL'
                  ? `All (${result.reviewList.length})`
                  : tab === 'CORRECT'
                  ? `Correct (${result.correctCount})`
                  : tab === 'INCORRECT'
                  ? `Incorrect (${result.incorrectCount})`
                  : `Skipped (${result.skippedCount})`}
              </button>
            ))}
          </div>
        </div>

        {/* Questions Accordion List */}
        <div className="space-y-4">
          {filteredQuestions.map((q, idx) => {
            const isCorr = Boolean(q.isCorrect || q.correct);
            const isAtt = Boolean(q.isAttempted || q.attempted);

            return (
              <div
                key={q.questionId}
                className="bg-white rounded-3xl border border-slate-200 p-6 sm:p-8 space-y-6 shadow-xs"
              >
                {/* Header Badge */}
                <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                  <div className="flex items-center space-x-2">
                    <span className="text-xs font-bold uppercase tracking-wider text-slate-400">
                      Question #{idx + 1}
                    </span>
                    <span className="text-xs font-semibold px-2 py-0.5 rounded-md bg-slate-100 text-slate-700">
                      {q.difficulty}
                    </span>
                  </div>

                  <div>
                    {isCorr ? (
                      <span className="inline-flex items-center space-x-1.5 text-xs font-bold text-emerald-700 bg-emerald-50 px-3 py-1 rounded-full border border-emerald-200">
                        <CheckCircle2 className="w-4 h-4 text-emerald-600" />
                        <span>Correct (+1.00)</span>
                      </span>
                    ) : isAtt ? (
                      <span className="inline-flex items-center space-x-1.5 text-xs font-bold text-rose-700 bg-rose-50 px-3 py-1 rounded-full border border-rose-200">
                        <XCircle className="w-4 h-4 text-rose-600" />
                        <span>Incorrect (-0.25)</span>
                      </span>
                    ) : (
                      <span className="inline-flex items-center space-x-1 text-xs font-bold text-slate-600 bg-slate-100 px-3 py-1 rounded-full">
                        <span>Skipped (0.00)</span>
                      </span>
                    )}
                  </div>
                </div>

                {/* Question Statement */}
                <h3 className="text-base sm:text-lg font-semibold text-slate-900 leading-relaxed whitespace-pre-line">
                  {q.questionText}
                </h3>

                {/* Options Review */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                  {q.options.map((opt) => {
                    const isSelectedByUser = q.selectedOption === opt.optionKey;
                    const isActualCorrect = q.correctOption === opt.optionKey;

                    let cardStyle = 'border-slate-200 bg-slate-50/50 text-slate-700';
                    if (isActualCorrect) {
                      cardStyle = 'border-emerald-500 bg-emerald-50 text-emerald-950 font-semibold';
                    } else if (isSelectedByUser && !isActualCorrect) {
                      cardStyle = 'border-rose-400 bg-rose-50 text-rose-950';
                    }

                    return (
                      <div
                        key={opt.id}
                        className={`p-3.5 rounded-2xl border-2 flex items-center justify-between text-xs sm:text-sm ${cardStyle}`}
                      >
                        <div className="flex items-center space-x-3">
                          <span
                            className={`w-7 h-7 rounded-lg flex items-center justify-center font-bold text-xs ${
                              isActualCorrect
                                ? 'bg-emerald-600 text-white'
                                : isSelectedByUser
                                ? 'bg-rose-600 text-white'
                                : 'bg-slate-200 text-slate-700'
                            }`}
                          >
                            {opt.optionKey}
                          </span>
                          <span>{opt.optionText}</span>
                        </div>

                        {isActualCorrect && (
                          <span className="text-xs font-bold text-emerald-700">Correct Answer</span>
                        )}
                        {isSelectedByUser && !isActualCorrect && (
                          <span className="text-xs font-bold text-rose-600">Your Choice</span>
                        )}
                      </div>
                    );
                  })}
                </div>

                {/* Step-by-Step Solution */}
                <div className="bg-slate-50 border border-slate-200 rounded-2xl p-4 sm:p-5 space-y-2">
                  <div className="flex items-center space-x-2 text-indigo-700 font-bold text-xs">
                    <Lightbulb className="w-4 h-4 text-indigo-600" />
                    <span>Step-by-Step Mathematical Explanation</span>
                  </div>
                  <div className="text-slate-800 text-xs sm:text-sm font-mono leading-relaxed whitespace-pre-line bg-white p-3.5 rounded-xl border border-slate-200">
                    {q.explanation}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
};
