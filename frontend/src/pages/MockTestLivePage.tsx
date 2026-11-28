import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/client';
import { StartTestResponse, Question, TestResultResponse } from '../types';
import {
  Timer,
  AlertTriangle,
  ChevronLeft,
  ChevronRight,
  Bookmark,
  LayoutGrid,
  X,
} from 'lucide-react';

export const MockTestLivePage: React.FC = () => {
  const { testId } = useParams<{ testId: string }>();
  const navigate = useNavigate();

  const [testData, setTestData] = useState<StartTestResponse | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState<
    Record<string, { option: string | null; marked: boolean; time: number }>
  >({});
  const [visited, setVisited] = useState<Set<string>>(new Set());
  const [timeLeftSeconds, setTimeLeftSeconds] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [showSubmitModal, setShowSubmitModal] = useState(false);
  const [mobilePaletteOpen, setMobilePaletteOpen] = useState(false);

  // Load Test Data
  useEffect(() => {
    const startTest = async () => {
      try {
        const res = await api.post<StartTestResponse>(`/mock-tests/${testId}/start`);
        setTestData(res.data);

        const initialAnswers: Record<
          string,
          { option: string | null; marked: boolean; time: number }
        > = {};
        const initialVisited = new Set<string>();

        if (res.data.questions.length > 0) {
          initialVisited.add(res.data.questions[0].id);
        }

        res.data.questions.forEach((q) => {
          const saved = res.data.savedAnswers[q.id];
          if (saved) {
            initialAnswers[q.id] = {
              option: saved.selectedOption,
              marked: saved.markedForReview,
              time: saved.timeSpentSeconds,
            };
            initialVisited.add(q.id);
          } else {
            initialAnswers[q.id] = { option: null, marked: false, time: 0 };
          }
        });

        setAnswers(initialAnswers);
        setVisited(initialVisited);

        const startTime = new Date(res.data.startTime).getTime();
        const totalDurationMs = res.data.durationMinutes * 60 * 1000;
        const now = Date.now();
        const remainingMs = Math.max(0, startTime + totalDurationMs - now);
        setTimeLeftSeconds(Math.floor(remainingMs / 1000));
      } catch (err) {
        console.error('Failed to initialize test', err);
        navigate('/mock-tests');
      } finally {
        setLoading(false);
      }
    };
    startTest();
  }, [testId, navigate]);

  // Countdown Timer & Auto-Submit
  useEffect(() => {
    if (loading || !testData || timeLeftSeconds <= 0) return;

    const timer = setInterval(() => {
      setTimeLeftSeconds((prev) => {
        if (prev <= 1) {
          clearInterval(timer);
          handleAutoSubmit();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [loading, testData, timeLeftSeconds]);

  const handleAutoSubmit = async () => {
    if (submitting || !testData) return;
    setSubmitting(true);
    try {
      const res = await api.post<TestResultResponse>(
        `/mock-tests/sessions/${testData.sessionId}/submit`
      );
      navigate(`/mock-tests/result/${res.data.sessionId}`);
    } catch (err) {
      console.error('Auto-submission error', err);
    }
  };

  const handleManualSubmit = async () => {
    if (submitting || !testData) return;
    setSubmitting(true);
    try {
      const res = await api.post<TestResultResponse>(
        `/mock-tests/sessions/${testData.sessionId}/submit`
      );
      navigate(`/mock-tests/result/${res.data.sessionId}`);
    } catch (err) {
      console.error('Submission error', err);
      setSubmitting(false);
    }
  };

  const saveCurrentAnswerToServer = async (
    qId: string,
    option: string | null,
    marked: boolean
  ) => {
    if (!testData) return;
    try {
      await api.post(`/mock-tests/sessions/${testData.sessionId}/save-response`, {
        questionId: qId,
        selectedOption: option,
        markedForReview: marked,
        timeSpentSeconds: answers[qId]?.time || 0,
      });
    } catch (err) {
      console.error('Auto-save response failed', err);
    }
  };

  const handleSelectOption = (key: string) => {
    if (!currentQuestion) return;
    const qId = currentQuestion.id;
    const isMarked = answers[qId]?.marked || false;

    setAnswers((prev) => ({
      ...prev,
      [qId]: { ...prev[qId], option: key },
    }));

    saveCurrentAnswerToServer(qId, key, isMarked);
  };

  const handleClearResponse = () => {
    if (!currentQuestion) return;
    const qId = currentQuestion.id;
    const isMarked = answers[qId]?.marked || false;

    setAnswers((prev) => ({
      ...prev,
      [qId]: { ...prev[qId], option: null },
    }));

    saveCurrentAnswerToServer(qId, null, isMarked);
  };

  const handleToggleMarkReview = () => {
    if (!currentQuestion) return;
    const qId = currentQuestion.id;
    const newMarked = !answers[qId]?.marked;
    const opt = answers[qId]?.option || null;

    setAnswers((prev) => ({
      ...prev,
      [qId]: { ...prev[qId], marked: newMarked },
    }));

    saveCurrentAnswerToServer(qId, opt, newMarked);
  };

  const handleNavigateQuestion = (index: number) => {
    if (!testData || index < 0 || index >= testData.questions.length) return;
    setCurrentIndex(index);
    const targetQId = testData.questions[index].id;
    setVisited((prev) => new Set(prev).add(targetQId));
    setMobilePaletteOpen(false);
  };

  if (loading || !testData) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="text-center space-y-3">
          <div className="w-8 h-8 border-2 border-slate-900 border-t-transparent rounded-full animate-spin mx-auto" />
          <p className="text-xs font-medium text-slate-600">
            Initializing examination environment...
          </p>
        </div>
      </div>
    );
  }

  const currentQuestion: Question = testData.questions[currentIndex];
  const currentAnswer = answers[currentQuestion.id] || { option: null, marked: false };

  const hours = Math.floor(timeLeftSeconds / 3600);
  const mins = Math.floor((timeLeftSeconds % 3600) / 60);
  const secs = timeLeftSeconds % 60;
  const timeFormatted = `${hours > 0 ? `${hours}:` : ''}${mins
    .toString()
    .padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  const getQuestionPaletteState = (q: Question) => {
    const a = answers[q.id];
    const isVis = visited.has(q.id);
    const hasOption = a && a.option !== null;
    const isMarked = a && a.marked;

    if (hasOption && isMarked) return 'ANSWERED_AND_MARKED';
    if (isMarked) return 'MARKED_FOR_REVIEW';
    if (hasOption) return 'ANSWERED';
    if (isVis) return 'NOT_ANSWERED';
    return 'UNVISITED';
  };

  const counts = {
    answered: 0,
    notAnswered: 0,
    marked: 0,
    ansMarked: 0,
    unvisited: 0,
  };

  testData.questions.forEach((q) => {
    const s = getQuestionPaletteState(q);
    if (s === 'ANSWERED') counts.answered++;
    else if (s === 'NOT_ANSWERED') counts.notAnswered++;
    else if (s === 'MARKED_FOR_REVIEW') counts.marked++;
    else if (s === 'ANSWERED_AND_MARKED') counts.ansMarked++;
    else counts.unvisited++;
  });

  const renderPaletteContent = () => (
    <div className="space-y-5">
      <div className="grid grid-cols-2 gap-2 text-xs text-slate-600">
        <div className="flex items-center space-x-2">
          <span className="w-3 h-3 rounded-sm bg-emerald-600 flex-shrink-0" />
          <span>Answered ({counts.answered})</span>
        </div>
        <div className="flex items-center space-x-2">
          <span className="w-3 h-3 rounded-sm bg-rose-600 flex-shrink-0" />
          <span>Unanswered ({counts.notAnswered})</span>
        </div>
        <div className="flex items-center space-x-2">
          <span className="w-3 h-3 rounded-sm bg-violet-600 flex-shrink-0" />
          <span>Marked ({counts.marked})</span>
        </div>
        <div className="flex items-center space-x-2">
          <span className="w-3 h-3 rounded-sm bg-blue-600 flex-shrink-0" />
          <span>Ans &amp; Marked ({counts.ansMarked})</span>
        </div>
        <div className="flex items-center space-x-2 col-span-2">
          <span className="w-3 h-3 rounded-sm bg-slate-200 border border-slate-300 flex-shrink-0" />
          <span>Not Visited ({counts.unvisited})</span>
        </div>
      </div>

      <div className="grid grid-cols-5 gap-2 max-h-72 overflow-y-auto p-1">
        {testData.questions.map((q, idx) => {
          const state = getQuestionPaletteState(q);
          const isCurrent = idx === currentIndex;

          let colorStyle =
            'bg-slate-100 text-slate-700 hover:bg-slate-200 border-slate-200';
          if (state === 'ANSWERED')
            colorStyle = 'bg-emerald-600 text-white border-emerald-700';
          else if (state === 'NOT_ANSWERED')
            colorStyle = 'bg-rose-600 text-white border-rose-700';
          else if (state === 'MARKED_FOR_REVIEW')
            colorStyle = 'bg-violet-600 text-white border-violet-700';
          else if (state === 'ANSWERED_AND_MARKED')
            colorStyle = 'bg-blue-600 text-white border-blue-700';

          return (
            <button
              key={q.id}
              type="button"
              onClick={() => handleNavigateQuestion(idx)}
              className={`h-9 rounded-md border font-mono text-xs font-semibold transition-colors flex items-center justify-center ${colorStyle} ${
                isCurrent ? 'ring-2 ring-slate-900 ring-offset-1' : ''
              }`}
            >
              {idx + 1}
            </button>
          );
        })}
      </div>

      <button
        type="button"
        onClick={() => {
          setMobilePaletteOpen(false);
          setShowSubmitModal(true);
        }}
        className="w-full py-2.5 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors"
      >
        Submit Assessment
      </button>
    </div>
  );

  return (
    <div className="min-h-screen flex flex-col bg-slate-50 select-none">
      {/* Proctor Exam Top Bar */}
      <header className="bg-slate-900 text-white px-4 sm:px-6 py-3 border-b border-slate-800 sticky top-0 z-30">
        <div className="max-w-7xl mx-auto flex items-center justify-between gap-3">
          <div className="min-w-0">
            <h1 className="text-sm sm:text-base font-semibold truncate text-white">
              {testData.title}
            </h1>
            <p className="text-[11px] font-mono text-slate-400">
              {testData.questions.length} Qs • +{testData.totalMarks / (testData.questions.length || 1)} / -{testData.negativeMarkingRate}
            </p>
          </div>

          {/* Countdown Timer */}
          <div
            className={`flex items-center space-x-2 px-3 py-1.5 rounded-md border font-mono font-semibold text-sm sm:text-base flex-shrink-0 ${
              timeLeftSeconds < 180
                ? 'bg-rose-950/80 border-rose-500/70 text-rose-200'
                : timeLeftSeconds < 600
                ? 'bg-amber-950/70 border-amber-500/60 text-amber-200'
                : 'bg-slate-800 border-slate-700 text-slate-100'
            }`}
          >
            <Timer className="w-4 h-4 flex-shrink-0" />
            <span>{timeFormatted}</span>
          </div>

          <div className="flex items-center space-x-2 flex-shrink-0">
            <button
              type="button"
              onClick={() => setMobilePaletteOpen(true)}
              className="lg:hidden inline-flex items-center space-x-1.5 px-2.5 py-1.5 rounded-md text-xs font-medium bg-slate-800 text-slate-200 border border-slate-700 hover:bg-slate-700"
              aria-label="Open question palette"
            >
              <LayoutGrid className="w-4 h-4" />
              <span className="font-mono">
                {counts.answered + counts.ansMarked}/{testData.questions.length}
              </span>
            </button>

            <button
              type="button"
              onClick={() => setShowSubmitModal(true)}
              className="hidden sm:inline-flex px-4 py-1.5 rounded-md text-xs sm:text-sm font-semibold text-white bg-emerald-700 hover:bg-emerald-600 transition-colors"
            >
              Finish Test
            </button>
          </div>
        </div>
      </header>

      {/* Main Arena Grid */}
      <div className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 grid grid-cols-1 lg:grid-cols-12 gap-6 items-start">
        {/* Question Pane */}
        <div className="lg:col-span-8 xl:col-span-9 bg-white rounded-xl border border-slate-200 shadow-xs p-5 sm:p-7 flex flex-col justify-between min-h-[460px] space-y-6">
          <div className="space-y-5">
            <div className="flex items-center justify-between border-b border-slate-100 pb-3.5">
              <span className="text-xs sm:text-sm font-mono font-semibold text-slate-700">
                Question {currentIndex + 1} of {testData.questions.length}
              </span>
              <span
                className={`text-[11px] font-mono font-semibold px-2 py-0.5 rounded-md border uppercase ${
                  currentQuestion.difficulty === 'EASY'
                    ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                    : currentQuestion.difficulty === 'MEDIUM'
                    ? 'bg-blue-50 text-blue-700 border-blue-200'
                    : 'bg-rose-50 text-rose-700 border-rose-200'
                }`}
              >
                {currentQuestion.difficulty}
              </span>
            </div>

            <h2 className="text-base sm:text-lg font-medium text-slate-900 leading-relaxed whitespace-pre-line">
              {currentQuestion.questionText}
            </h2>

            <div className="space-y-2.5 pt-1">
              {currentQuestion.options.map((opt) => {
                const isSelected = currentAnswer.option === opt.optionKey;
                return (
                  <button
                    key={opt.id}
                    type="button"
                    onClick={() => handleSelectOption(opt.optionKey)}
                    className={`w-full text-left p-3.5 sm:p-4 rounded-lg border transition-colors flex items-center space-x-3.5 ${
                      isSelected
                        ? 'border-blue-600 bg-blue-50/40 text-slate-900 font-medium'
                        : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50 text-slate-800'
                    }`}
                  >
                    <span
                      className={`w-7 h-7 rounded-md border flex items-center justify-center font-mono font-semibold text-xs flex-shrink-0 ${
                        isSelected
                          ? 'bg-blue-600 text-white border-blue-600'
                          : 'bg-slate-100 text-slate-600 border-slate-200'
                      }`}
                    >
                      {opt.optionKey}
                    </span>
                    <span className="text-sm sm:text-base">{opt.optionText}</span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Bottom Navigation Bar */}
          <div className="pt-5 border-t border-slate-100 flex flex-col-reverse sm:flex-row sm:items-center justify-between gap-3">
            <div className="flex flex-wrap items-center gap-2">
              <button
                type="button"
                onClick={handleClearResponse}
                disabled={!currentAnswer.option}
                className="px-3.5 py-2 rounded-lg text-xs font-medium text-slate-600 hover:bg-slate-100 border border-slate-200 disabled:opacity-40 transition-colors"
              >
                Clear
              </button>
              <button
                type="button"
                onClick={handleToggleMarkReview}
                className={`inline-flex items-center space-x-1.5 px-3.5 py-2 rounded-lg text-xs font-medium border transition-colors ${
                  currentAnswer.marked
                    ? 'bg-violet-50 border-violet-300 text-violet-800'
                    : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-50'
                }`}
              >
                <Bookmark className="w-3.5 h-3.5" />
                <span>{currentAnswer.marked ? 'Marked' : 'Mark for Review'}</span>
              </button>
            </div>

            <div className="flex items-center justify-between sm:justify-end gap-2.5">
              <button
                type="button"
                onClick={() => handleNavigateQuestion(currentIndex - 1)}
                disabled={currentIndex === 0}
                className="flex-1 sm:flex-none justify-center px-4 py-2 rounded-lg text-xs sm:text-sm font-medium text-slate-700 border border-slate-200 hover:bg-slate-50 disabled:opacity-40 transition-colors inline-flex items-center space-x-1"
              >
                <ChevronLeft className="w-4 h-4" />
                <span>Previous</span>
              </button>
              <button
                type="button"
                onClick={() => handleNavigateQuestion(currentIndex + 1)}
                disabled={currentIndex === testData.questions.length - 1}
                className="flex-1 sm:flex-none justify-center px-5 py-2 rounded-lg text-xs sm:text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 disabled:opacity-40 transition-colors inline-flex items-center space-x-1"
              >
                <span>Save &amp; Next</span>
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Desktop Question Palette Sidebar */}
        <aside className="hidden lg:block lg:col-span-4 xl:col-span-3 bg-white rounded-xl border border-slate-200 shadow-xs p-5 sticky top-20">
          <h3 className="text-xs font-semibold uppercase tracking-wider text-slate-500 border-b border-slate-100 pb-3 mb-4">
            Question Palette
          </h3>
          {renderPaletteContent()}
        </aside>
      </div>

      {/* Mobile Palette Slide-over Drawer */}
      {mobilePaletteOpen && (
        <div className="fixed inset-0 z-50 lg:hidden flex justify-end bg-slate-900/50">
          <div className="w-80 max-w-full bg-white h-full p-5 overflow-y-auto shadow-xl flex flex-col justify-between">
            <div className="space-y-4">
              <div className="flex items-center justify-between border-b border-slate-200 pb-3">
                <h3 className="text-sm font-semibold text-slate-900">Question Palette</h3>
                <button
                  type="button"
                  onClick={() => setMobilePaletteOpen(false)}
                  className="p-1.5 rounded-md text-slate-500 hover:bg-slate-100"
                  aria-label="Close palette"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
              {renderPaletteContent()}
            </div>
          </div>
        </div>
      )}

      {/* Confirmation Modal */}
      {showSubmitModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 p-4">
          <div className="bg-white rounded-xl border border-slate-200 max-w-md w-full p-6 space-y-5 shadow-lg">
            <div className="flex items-start space-x-3">
              <div className="w-10 h-10 rounded-lg bg-amber-50 border border-amber-200 text-amber-600 flex items-center justify-center flex-shrink-0">
                <AlertTriangle className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-base font-bold text-slate-900">Submit Assessment?</h3>
                <p className="text-xs text-slate-500 mt-1">
                  Once submitted, you cannot modify your responses. Review your summary below.
                </p>
              </div>
            </div>

            <div className="bg-slate-50 p-3.5 rounded-lg border border-slate-200 space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-slate-600">Answered:</span>
                <strong className="font-mono text-emerald-700">
                  {counts.answered + counts.ansMarked}
                </strong>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-600">Marked for Review:</span>
                <strong className="font-mono text-violet-700">{counts.marked}</strong>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-600">Unanswered / Skipped:</span>
                <strong className="font-mono text-rose-700">
                  {counts.notAnswered + counts.unvisited}
                </strong>
              </div>
            </div>

            <div className="flex items-center space-x-3">
              <button
                type="button"
                onClick={() => setShowSubmitModal(false)}
                className="flex-1 py-2 rounded-lg text-xs font-semibold border border-slate-300 text-slate-700 hover:bg-slate-50 transition-colors"
              >
                Resume Test
              </button>
              <button
                type="button"
                onClick={handleManualSubmit}
                disabled={submitting}
                className="flex-1 py-2 rounded-lg text-xs font-semibold bg-slate-900 text-white hover:bg-slate-800 transition-colors disabled:opacity-50"
              >
                {submitting ? 'Submitting...' : 'Confirm & Submit'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
