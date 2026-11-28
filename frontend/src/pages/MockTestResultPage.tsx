import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../api/client';
import { TestResultResponse } from '../types';
import { CheckCircle2, XCircle, MinusCircle, BookOpenCheck } from 'lucide-react';

export const MockTestResultPage: React.FC = () => {
  const { sessionId } = useParams<{ sessionId: string }>();

  const [result, setResult] = useState<TestResultResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState<'ALL' | 'CORRECT' | 'INCORRECT' | 'SKIPPED'>('ALL');

  useEffect(() => {
    const fetchResult = async () => {
      try {
        const res = await api.get<TestResultResponse>(
          `/mock-tests/sessions/${sessionId}/result`
        );
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
          <div className="w-8 h-8 border-2 border-slate-900 border-t-transparent rounded-full animate-spin mx-auto" />
          <p className="text-xs font-medium text-slate-500">Generating scorecard...</p>
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
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6">
      {/* Executive Scorecard Card */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-7 shadow-xs space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-5">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">
              Examination Scorecard
            </p>
            <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight mt-0.5">
              {result.testTitle}
            </h1>
          </div>
          <div className="flex items-center space-x-2.5">
            <Link
              to="/mock-tests"
              className="px-3.5 py-2 rounded-lg text-xs font-semibold border border-slate-200 text-slate-700 hover:bg-slate-50 transition-colors"
            >
              All Tests
            </Link>
            <Link
              to="/"
              className="px-3.5 py-2 rounded-lg text-xs font-semibold bg-slate-900 text-white hover:bg-slate-800 transition-colors"
            >
              Dashboard
            </Link>
          </div>
        </div>

        {/* Core Metrics Grid */}
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-3 sm:gap-4">
          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 space-y-1">
            <span className="text-xs font-medium text-slate-500">Total Score</span>
            <div className="text-2xl font-bold font-mono text-slate-900">
              {result.totalScore}
              <span className="text-sm font-normal text-slate-400"> / {result.maxMarks}</span>
            </div>
          </div>

          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 space-y-1">
            <span className="text-xs font-medium text-slate-500">Accuracy</span>
            <div className="text-2xl font-bold font-mono text-emerald-700">
              {result.accuracyPercentage}%
            </div>
          </div>

          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 space-y-1">
            <span className="text-xs font-medium text-slate-500">Est. Percentile</span>
            <div className="text-2xl font-bold font-mono text-blue-700">
              {result.percentileEstimate}th
            </div>
          </div>

          <div className="p-4 rounded-lg bg-slate-50 border border-slate-200/80 space-y-1">
            <span className="text-xs font-medium text-slate-500">Time Elapsed</span>
            <div className="text-2xl font-bold font-mono text-slate-900">
              {timeMins}m {timeSecs}s
            </div>
          </div>
        </div>

        {/* Question Breakdown Strip */}
        <div className="flex flex-wrap items-center gap-4 sm:gap-6 pt-2 text-xs font-medium text-slate-600">
          <span>
            Total: <strong className="font-mono text-slate-900">{result.totalQuestions}</strong>
          </span>
          <span>
            Attempted: <strong className="font-mono text-slate-900">{result.attemptedCount}</strong>
          </span>
          <span className="inline-flex items-center space-x-1.5 text-emerald-700">
            <CheckCircle2 className="w-3.5 h-3.5" />
            <span>
              Correct: <strong className="font-mono">{result.correctCount}</strong>
            </span>
          </span>
          <span className="inline-flex items-center space-x-1.5 text-rose-700">
            <XCircle className="w-3.5 h-3.5" />
            <span>
              Incorrect: <strong className="font-mono">{result.incorrectCount}</strong>
            </span>
          </span>
          <span className="inline-flex items-center space-x-1.5 text-slate-500">
            <MinusCircle className="w-3.5 h-3.5" />
            <span>
              Skipped: <strong className="font-mono">{result.skippedCount}</strong>
            </span>
          </span>
        </div>
      </section>

      {/* Solution Review */}
      <section className="space-y-4">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-3">
          <div>
            <h2 className="text-lg font-bold text-slate-900">Solution Review</h2>
            <p className="text-xs text-slate-500">
              Compare your responses against the official step-by-step derivations.
            </p>
          </div>

          {/* Segmented Filter Tabs */}
          <div className="flex flex-wrap items-center gap-1 p-1 bg-slate-100 border border-slate-200 rounded-lg text-xs font-medium">
            <button
              type="button"
              onClick={() => setFilter('ALL')}
              className={`px-3 py-1.5 rounded-md transition-colors ${
                filter === 'ALL'
                  ? 'bg-white text-slate-900 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              All ({result.reviewList.length})
            </button>
            <button
              type="button"
              onClick={() => setFilter('CORRECT')}
              className={`inline-flex items-center space-x-1 px-3 py-1.5 rounded-md transition-colors ${
                filter === 'CORRECT'
                  ? 'bg-white text-emerald-700 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
              <span>Correct ({result.correctCount})</span>
            </button>
            <button
              type="button"
              onClick={() => setFilter('INCORRECT')}
              className={`inline-flex items-center space-x-1 px-3 py-1.5 rounded-md transition-colors ${
                filter === 'INCORRECT'
                  ? 'bg-white text-rose-700 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <XCircle className="w-3.5 h-3.5 text-rose-600" />
              <span>Incorrect ({result.incorrectCount})</span>
            </button>
            <button
              type="button"
              onClick={() => setFilter('SKIPPED')}
              className={`inline-flex items-center space-x-1 px-3 py-1.5 rounded-md transition-colors ${
                filter === 'SKIPPED'
                  ? 'bg-white text-slate-900 shadow-xs'
                  : 'text-slate-600 hover:text-slate-900'
              }`}
            >
              <MinusCircle className="w-3.5 h-3.5 text-slate-400" />
              <span>Skipped ({result.skippedCount})</span>
            </button>
          </div>
        </div>

        {/* Review Items */}
        <div className="space-y-4">
          {filteredQuestions.map((q, idx) => {
            const isCorr = Boolean(q.isCorrect || q.correct);
            const isAtt = Boolean(q.isAttempted || q.attempted);

            return (
              <div
                key={q.questionId}
                className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 space-y-5 shadow-xs"
              >
                <div className="flex items-center justify-between border-b border-slate-100 pb-3.5">
                  <div className="flex items-center space-x-2.5">
                    <span className="text-xs font-mono font-semibold text-slate-500">
                      Q#{idx + 1}
                    </span>
                    <span className="text-[11px] font-mono font-semibold px-2 py-0.5 rounded-md bg-slate-100 text-slate-700 border border-slate-200">
                      {q.difficulty}
                    </span>
                  </div>

                  {isCorr ? (
                    <span className="inline-flex items-center space-x-1.5 text-xs font-semibold text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-200">
                      <CheckCircle2 className="w-3.5 h-3.5" />
                      <span>Correct</span>
                    </span>
                  ) : isAtt ? (
                    <span className="inline-flex items-center space-x-1.5 text-xs font-semibold text-rose-700 bg-rose-50 px-2.5 py-1 rounded-md border border-rose-200">
                      <XCircle className="w-3.5 h-3.5" />
                      <span>Incorrect</span>
                    </span>
                  ) : (
                    <span className="inline-flex items-center space-x-1.5 text-xs font-semibold text-slate-600 bg-slate-100 px-2.5 py-1 rounded-md border border-slate-200">
                      <MinusCircle className="w-3.5 h-3.5" />
                      <span>Skipped</span>
                    </span>
                  )}
                </div>

                <h3 className="text-sm sm:text-base font-medium text-slate-900 leading-relaxed whitespace-pre-line">
                  {q.questionText}
                </h3>

                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                  {q.options.map((opt) => {
                    const isSelectedByUser = q.selectedOption === opt.optionKey;
                    const isActualCorrect = q.correctOption === opt.optionKey;

                    let cardStyle = 'border-slate-200 bg-slate-50/50 text-slate-700';
                    if (isActualCorrect) {
                      cardStyle =
                        'border-emerald-600 bg-emerald-50/60 text-emerald-950 font-medium';
                    } else if (isSelectedByUser && !isActualCorrect) {
                      cardStyle = 'border-rose-500 bg-rose-50/60 text-rose-950';
                    }

                    return (
                      <div
                        key={opt.id}
                        className={`p-3 rounded-lg border flex items-center justify-between gap-2 text-xs sm:text-sm ${cardStyle}`}
                      >
                        <div className="flex items-center space-x-2.5">
                          <span
                            className={`w-6 h-6 rounded-md flex items-center justify-center font-mono font-semibold text-xs flex-shrink-0 ${
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
                          <span className="text-[11px] font-semibold text-emerald-700 flex-shrink-0">
                            Answer
                          </span>
                        )}
                        {isSelectedByUser && !isActualCorrect && (
                          <span className="text-[11px] font-semibold text-rose-700 flex-shrink-0">
                            Your Choice
                          </span>
                        )}
                      </div>
                    );
                  })}
                </div>

                <div className="bg-slate-50 border border-slate-200 rounded-lg p-4 space-y-2">
                  <div className="flex items-center space-x-2 text-slate-900 font-semibold text-xs uppercase tracking-wider">
                    <BookOpenCheck className="w-4 h-4 text-blue-600" />
                    <span>Step-by-Step Solution</span>
                  </div>
                  <div className="text-slate-800 text-xs sm:text-sm font-mono leading-relaxed whitespace-pre-line bg-white p-3.5 rounded-md border border-slate-200">
                    {q.explanation}
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </section>
    </div>
  );
};
