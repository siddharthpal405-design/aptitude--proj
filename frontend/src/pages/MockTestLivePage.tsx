import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/client';
import { StartTestResponse, Question, SavedAnswerState, TestResultResponse } from '../types';
import { Timer, AlertTriangle, CheckCircle2, ChevronLeft, ChevronRight, Bookmark, RotateCcw, Send } from 'lucide-react';

export const MockTestLivePage: React.FC = () => {
  const { testId } = useParams<{ testId: string }>();
  const navigate = useNavigate();

  const [testData, setTestData] = useState<StartTestResponse | null>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [answers, setAnswers] = useState<Record<string, { option: string | null; marked: boolean; time: number }>>({});
  const [visited, setVisited] = useState<Set<string>>(new Set());
  const [timeLeftSeconds, setTimeLeftSeconds] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [showSubmitModal, setShowSubmitModal] = useState(false);

  // Load Test Data
  useEffect(() => {
    const startTest = async () => {
      try {
        const res = await api.post<StartTestResponse>(`/mock-tests/${testId}/start`);
        setTestData(res.data);

        // Initialize answers map from backend saved state
        const initialAnswers: Record<string, { option: string | null; marked: boolean; time: number }> = {};
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

        // Calculate remaining seconds
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
      const res = await api.post<TestResultResponse>(`/mock-tests/sessions/${testData.sessionId}/submit`);
      navigate(`/mock-tests/result/${res.data.sessionId}`);
    } catch (err) {
      console.error('Auto-submission error', err);
    }
  };

  const handleManualSubmit = async () => {
    if (submitting || !testData) return;
    setSubmitting(true);
    try {
      const res = await api.post<TestResultResponse>(`/mock-tests/sessions/${testData.sessionId}/submit`);
      navigate(`/mock-tests/result/${res.data.sessionId}`);
    } catch (err) {
      console.error('Submission error', err);
      setSubmitting(false);
    }
  };

  const saveCurrentAnswerToServer = async (qId: string, option: string | null, marked: boolean) => {
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
  };

  if (loading || !testData) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-900 text-white">
        <div className="text-center space-y-4">
          <div className="w-12 h-12 border-4 border-sky-400 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-sm text-slate-400">Loading Mock Test Environment...</p>
        </div>
      </div>
    );
  }

  const currentQuestion: Question = testData.questions[currentIndex];
  const currentAnswer = answers[currentQuestion.id] || { option: null, marked: false };

  // Format Time Remaining
  const hours = Math.floor(timeLeftSeconds / 3600);
  const mins = Math.floor((timeLeftSeconds % 3600) / 60);
  const secs = timeLeftSeconds % 60;
  const timeFormatted = `${hours > 0 ? `${hours}:` : ''}${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

  // Palette State Calculation
  const getQuestionPaletteState = (q: Question) => {
    const a = answers[q.id];
    const isVis = visited.has(q.id);
    const hasOption = a && a.option !== null;
    const isMarked = a && a.marked;

    if (hasOption && isMarked) return 'ANSWERED_AND_MARKED'; // Blue
    if (isMarked) return 'MARKED_FOR_REVIEW'; // Purple
    if (hasOption) return 'ANSWERED'; // Green
    if (isVis) return 'NOT_ANSWERED'; // Red
    return 'UNVISITED'; // Gray
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

  return (
    <div className="min-h-screen flex flex-col bg-slate-100 select-none">
      {/* Top Test Header with Live Timer */}
      <header className="bg-slate-900 text-white px-4 sm:px-8 py-3 flex items-center justify-between border-b border-slate-800 shadow-md sticky top-0 z-40">
        <div>
          <h1 className="text-base sm:text-lg font-bold truncate max-w-xs sm:max-w-md">
            {testData.title}
          </h1>
          <span className="text-xs text-slate-400">
            {testData.questions.length} Questions • +{testData.totalMarks / (testData.questions.length || 1)} / -{testData.negativeMarkingRate} Marks
          </span>
        </div>

        {/* Real-time Countdown Timer */}
        <div
          className={`flex items-center space-x-2 px-4 py-2 rounded-xl border font-mono font-bold text-base sm:text-lg transition-colors ${
            timeLeftSeconds < 180
              ? 'bg-rose-500/20 border-rose-500 text-rose-400 animate-pulse'
              : timeLeftSeconds < 600
              ? 'bg-amber-500/20 border-amber-500 text-amber-400'
              : 'bg-slate-800 border-slate-700 text-sky-400'
          }`}
        >
          <Timer className="w-5 h-5 flex-shrink-0" />
          <span>{timeFormatted}</span>
        </div>

        <button
          onClick={() => setShowSubmitModal(true)}
          className="px-5 py-2 rounded-xl font-bold text-xs sm:text-sm text-white bg-emerald-600 hover:bg-emerald-700 active:scale-95 transition shadow-md shadow-emerald-600/20"
        >
          Submit Test
        </button>
      </header>

      {/* Main Examination Arena */}
      <div className="flex-1 max-w-7xl w-full mx-auto p-4 sm:p-6 grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* Left/Center Pane: Active Question */}
        <div className="lg:col-span-3 flex flex-col justify-between bg-white rounded-3xl border border-slate-200/90 shadow-sm p-6 sm:p-8 space-y-6">
          <div className="space-y-6">
            <div className="flex items-center justify-between border-b border-slate-100 pb-4">
              <span className="text-sm font-bold text-slate-700">
                Question {currentIndex + 1} of {testData.questions.length}
              </span>
              <span
                className={`text-xs font-bold px-2.5 py-0.5 rounded-md ${
                  currentQuestion.difficulty === 'EASY'
                    ? 'bg-emerald-100 text-emerald-800'
                    : currentQuestion.difficulty === 'MEDIUM'
                    ? 'bg-sky-100 text-sky-800'
                    : 'bg-rose-100 text-rose-800'
                }`}
              >
                {currentQuestion.difficulty}
              </span>
            </div>

            {/* Question Text */}
            <h2 className="text-base sm:text-lg font-semibold text-slate-900 leading-relaxed whitespace-pre-line">
              {currentQuestion.questionText}
            </h2>

            {/* Options */}
            <div className="space-y-3 pt-2">
              {currentQuestion.options.map((opt) => {
                const isSelected = currentAnswer.option === opt.optionKey;
                return (
                  <button
                    key={opt.id}
                    type="button"
                    onClick={() => handleSelectOption(opt.optionKey)}
                    className={`w-full text-left p-4 rounded-2xl border-2 transition-all flex items-center justify-between ${
                      isSelected
                        ? 'border-sky-600 bg-sky-50 text-sky-900 shadow-xs font-semibold'
                        : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50 text-slate-800'
                    }`}
                  >
                    <div className="flex items-center space-x-3">
                      <span
                        className={`w-8 h-8 rounded-lg flex items-center justify-center font-bold text-sm ${
                          isSelected ? 'bg-sky-600 text-white' : 'bg-slate-100 text-slate-600'
                        }`}
                      >
                        {opt.optionKey}
                      </span>
                      <span className="text-sm sm:text-base">{opt.optionText}</span>
                    </div>
                  </button>
                );
              })}
            </div>
          </div>

          {/* Bottom Action Controls */}
          <div className="pt-6 border-t border-slate-100 flex flex-wrap items-center justify-between gap-3">
            <div className="flex items-center space-x-2">
              <button
                onClick={handleClearResponse}
                disabled={!currentAnswer.option}
                className="px-4 py-2 rounded-xl text-xs font-bold text-slate-600 hover:bg-slate-100 border border-slate-200 disabled:opacity-40 transition"
              >
                Clear Selection
              </button>
              <button
                onClick={handleToggleMarkReview}
                className={`inline-flex items-center space-x-1.5 px-4 py-2 rounded-xl text-xs font-bold border transition ${
                  currentAnswer.marked
                    ? 'bg-purple-100 border-purple-300 text-purple-800'
                    : 'bg-white border-slate-200 text-slate-700 hover:bg-slate-100'
                }`}
              >
                <Bookmark className="w-3.5 h-3.5" />
                <span>{currentAnswer.marked ? 'Marked for Review' : 'Mark for Review'}</span>
              </button>
            </div>

            <div className="flex items-center space-x-3">
              <button
                onClick={() => handleNavigateQuestion(currentIndex - 1)}
                disabled={currentIndex === 0}
                className="px-4 py-2 rounded-xl text-xs font-bold text-slate-700 border border-slate-200 hover:bg-slate-100 disabled:opacity-40 transition flex items-center space-x-1"
              >
                <ChevronLeft className="w-4 h-4" />
                <span>Previous</span>
              </button>
              <button
                onClick={() => handleNavigateQuestion(currentIndex + 1)}
                disabled={currentIndex === testData.questions.length - 1}
                className="px-6 py-2 rounded-xl text-xs font-bold text-white bg-slate-900 hover:bg-slate-800 active:scale-95 transition flex items-center space-x-1 shadow-sm"
              >
                <span>Save &amp; Next</span>
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>
        </div>

        {/* Right Pane: Question Palette */}
        <div className="bg-white rounded-3xl border border-slate-200/90 shadow-sm p-5 space-y-6 flex flex-col justify-between">
          <div className="space-y-4">
            <h3 className="text-sm font-bold text-slate-800 uppercase tracking-wider border-b border-slate-100 pb-3">
              Question Palette
            </h3>

            {/* Palette Legend */}
            <div className="grid grid-cols-2 gap-2 text-xs text-slate-600">
              <div className="flex items-center space-x-2">
                <span className="w-3.5 h-3.5 rounded-full bg-emerald-500"></span>
                <span>Answered ({counts.answered})</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="w-3.5 h-3.5 rounded-full bg-rose-500"></span>
                <span>Unanswered ({counts.notAnswered})</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="w-3.5 h-3.5 rounded-full bg-purple-500"></span>
                <span>Marked ({counts.marked})</span>
              </div>
              <div className="flex items-center space-x-2">
                <span className="w-3.5 h-3.5 rounded-full bg-blue-500"></span>
                <span>Ans &amp; Marked ({counts.ansMarked})</span>
              </div>
              <div className="flex items-center space-x-2 col-span-2">
                <span className="w-3.5 h-3.5 rounded-full bg-slate-200"></span>
                <span>Not Visited ({counts.unvisited})</span>
              </div>
            </div>

            {/* Palette Buttons Grid */}
            <div className="grid grid-cols-5 gap-2 max-h-72 overflow-y-auto p-1">
              {testData.questions.map((q, idx) => {
                const state = getQuestionPaletteState(q);
                const isCurrent = idx === currentIndex;

                let colorStyle = 'bg-slate-100 text-slate-700 hover:bg-slate-200 border-slate-200';
                if (state === 'ANSWERED') colorStyle = 'bg-emerald-500 text-white font-bold border-emerald-600';
                else if (state === 'NOT_ANSWERED') colorStyle = 'bg-rose-500 text-white font-bold border-rose-600';
                else if (state === 'MARKED_FOR_REVIEW') colorStyle = 'bg-purple-500 text-white font-bold border-purple-600';
                else if (state === 'ANSWERED_AND_MARKED') colorStyle = 'bg-blue-500 text-white font-bold border-blue-600';

                return (
                  <button
                    key={q.id}
                    onClick={() => handleNavigateQuestion(idx)}
                    className={`h-9 rounded-xl border text-xs transition-transform active:scale-90 flex items-center justify-center ${colorStyle} ${
                      isCurrent ? 'ring-2 ring-slate-900 ring-offset-2' : ''
                    }`}
                  >
                    {idx + 1}
                  </button>
                );
              })}
            </div>
          </div>

          <button
            onClick={() => setShowSubmitModal(true)}
            className="w-full py-3 rounded-2xl font-bold text-sm text-white bg-slate-900 hover:bg-slate-800 active:scale-95 transition shadow-sm"
          >
            Submit Entire Test
          </button>
        </div>
      </div>

      {/* Confirmation Modal */}
      {showSubmitModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-xs p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 sm:p-8 space-y-6 shadow-2xl">
            <div className="text-center space-y-2">
              <div className="w-12 h-12 rounded-2xl bg-amber-100 text-amber-600 flex items-center justify-center mx-auto">
                <AlertTriangle className="w-6 h-6" />
              </div>
              <h3 className="text-xl font-bold text-slate-900">Confirm Submission</h3>
              <p className="text-xs text-slate-500">
                Are you sure you want to finalize your exam? Once submitted, you cannot alter your answers.
              </p>
            </div>

            <div className="bg-slate-50 p-4 rounded-2xl space-y-2 text-xs">
              <div className="flex justify-between">
                <span className="text-slate-500">Answered Questions:</span>
                <strong className="text-emerald-700">{counts.answered + counts.ansMarked}</strong>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Marked for Review:</span>
                <strong className="text-purple-700">{counts.marked}</strong>
              </div>
              <div className="flex justify-between">
                <span className="text-slate-500">Unanswered / Skipped:</span>
                <strong className="text-rose-700">{counts.notAnswered + counts.unvisited}</strong>
              </div>
            </div>

            <div className="flex items-center space-x-3">
              <button
                type="button"
                onClick={() => setShowSubmitModal(false)}
                className="flex-1 py-2.5 rounded-xl text-xs font-bold border border-slate-200 text-slate-700 hover:bg-slate-50 transition"
              >
                Continue Exam
              </button>
              <button
                type="button"
                onClick={handleManualSubmit}
                disabled={submitting}
                className="flex-1 py-2.5 rounded-xl text-xs font-bold bg-emerald-600 text-white hover:bg-emerald-700 active:scale-95 transition disabled:opacity-50"
              >
                {submitting ? 'Submitting...' : 'Yes, Submit'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
