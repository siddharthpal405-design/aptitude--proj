import React, { useState, useEffect } from 'react';
import { useParams, useSearchParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/client';
import { Question, CheckAnswerResponse } from '../types';
import { ArrowLeft, CheckCircle2, XCircle, Lightbulb, ChevronRight, RotateCcw, Flame } from 'lucide-react';

export const PracticeArenaPage: React.FC = () => {
  const { chapterId } = useParams<{ chapterId: string }>();
  const [searchParams] = useSearchParams();
  const chapterTitle = searchParams.get('title') || 'Chapter Practice';

  const { refreshStreak, streak } = useAuth();
  const navigate = useNavigate();

  const [questions, setQuestions] = useState<Question[]>([]);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [selectedOption, setSelectedOption] = useState<string | null>(null);
  const [answerResult, setAnswerResult] = useState<CheckAnswerResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [solvedCount, setSolvedCount] = useState(0);

  useEffect(() => {
    const fetchQuestions = async () => {
      try {
        const res = await api.get<{ content: Question[] }>(
          `/practice/chapters/${chapterId}/questions?page=0&size=50`
        );
        setQuestions(res.data.content);
      } catch (err) {
        console.error('Failed to load questions', err);
      } finally {
        setLoading(false);
      }
    };
    fetchQuestions();
  }, [chapterId]);

  const handleSelectOption = (key: string) => {
    if (answerResult) return; // Prevent changing after submission
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
    if (currentIndex < questions.length - 1) {
      setCurrentIndex((prev) => prev + 1);
    }
  };

  const currentQuestion = questions[currentIndex];

  if (loading) {
    return (
      <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center">
        <div className="text-center space-y-3">
          <div className="w-10 h-10 border-4 border-sky-600 border-t-transparent rounded-full animate-spin mx-auto"></div>
          <p className="text-sm text-slate-500">Loading practice questions...</p>
        </div>
      </div>
    );
  }

  if (questions.length === 0) {
    return (
      <div className="max-w-xl mx-auto py-16 px-4 text-center space-y-4">
        <div className="w-16 h-16 rounded-full bg-amber-100 text-amber-600 flex items-center justify-center mx-auto text-2xl">
          📝
        </div>
        <h2 className="text-2xl font-bold text-slate-900">No Questions Yet</h2>
        <p className="text-sm text-slate-500">
          This chapter currently has no seeded MCQs. Check back after Phase 2 question bank scaling.
        </p>
        <button
          onClick={() => navigate('/')}
          className="px-4 py-2 bg-slate-900 text-white rounded-xl text-sm font-semibold"
        >
          Back to Dashboard
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">
      {/* Top Navigation & Status Bar */}
      <div className="flex items-center justify-between bg-white p-4 rounded-2xl border border-slate-200 shadow-xs">
        <button
          onClick={() => navigate('/')}
          className="inline-flex items-center space-x-2 text-sm font-semibold text-slate-600 hover:text-slate-900 transition"
        >
          <ArrowLeft className="w-4 h-4" />
          <span>Exit Practice</span>
        </button>

        <div className="text-center">
          <h2 className="text-sm font-bold text-slate-900">{chapterTitle}</h2>
          <span className="text-xs text-slate-500">
            Question {currentIndex + 1} of {questions.length}
          </span>
        </div>

        <div className="flex items-center space-x-1.5 px-3 py-1 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-bold">
          <Flame className="w-4 h-4 text-amber-500 fill-amber-500" />
          <span>{streak?.currentStreak || 0}d</span>
        </div>
      </div>

      {/* Progress Bar */}
      <div className="w-full bg-slate-200 rounded-full h-2 overflow-hidden">
        <div
          className="bg-sky-600 h-2 rounded-full transition-all duration-300"
          style={{ width: `${((currentIndex + 1) / questions.length) * 100}%` }}
        ></div>
      </div>

      {/* MCQ Card */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 sm:p-10 shadow-sm space-y-8">
        {/* Difficulty Badge & Question Prompt */}
        <div className="space-y-4">
          <span
            className={`inline-block text-xs font-bold px-2.5 py-1 rounded-md uppercase tracking-wider ${
              currentQuestion.difficulty === 'EASY'
                ? 'bg-emerald-100 text-emerald-800'
                : currentQuestion.difficulty === 'MEDIUM'
                ? 'bg-amber-100 text-amber-800'
                : 'bg-rose-100 text-rose-800'
            }`}
          >
            {currentQuestion.difficulty}
          </span>

          <h3 className="text-lg sm:text-xl font-semibold text-slate-900 leading-relaxed whitespace-pre-line">
            {currentQuestion.questionText}
          </h3>
        </div>

        {/* 4 Selectable Options */}
        <div className="space-y-3">
          {currentQuestion.options.map((opt) => {
            const isSelected = selectedOption === opt.optionKey;
            const isEvaluated = answerResult !== null;
            const isAnswerCorrect = Boolean(answerResult?.isCorrect || answerResult?.correct);
            const isCorrectOption = answerResult?.correctOption === opt.optionKey;
            const isWrongSelected = isEvaluated && isSelected && !isAnswerCorrect;

            let optionStyle =
              'border-slate-200 hover:border-sky-300 hover:bg-sky-50/50 text-slate-800';

            if (isSelected && !isEvaluated) {
              optionStyle = 'border-sky-600 bg-sky-50 text-sky-900 shadow-xs';
            } else if (isEvaluated) {
              if (isCorrectOption) {
                optionStyle = 'border-emerald-500 bg-emerald-50 text-emerald-900 font-semibold';
              } else if (isWrongSelected) {
                optionStyle = 'border-rose-500 bg-rose-50 text-rose-900';
              } else {
                optionStyle = 'border-slate-200 opacity-60 text-slate-600';
              }
            }

            return (
              <button
                key={opt.id}
                type="button"
                onClick={() => handleSelectOption(opt.optionKey)}
                disabled={isEvaluated}
                className={`w-full text-left p-4 sm:p-5 rounded-2xl border-2 transition-all flex items-center justify-between ${optionStyle}`}
              >
                <div className="flex items-center space-x-4">
                  <span
                    className={`w-9 h-9 rounded-xl flex items-center justify-center font-bold text-sm ${
                      isSelected && !isEvaluated
                        ? 'bg-sky-600 text-white'
                        : isEvaluated && isCorrectOption
                        ? 'bg-emerald-600 text-white'
                        : isEvaluated && isWrongSelected
                        ? 'bg-rose-600 text-white'
                        : 'bg-slate-100 text-slate-700'
                    }`}
                  >
                    {opt.optionKey}
                  </span>
                  <span className="text-sm sm:text-base">{opt.optionText}</span>
                </div>

                {isEvaluated && isCorrectOption && (
                  <CheckCircle2 className="w-6 h-6 text-emerald-600 flex-shrink-0" />
                )}
                {isEvaluated && isWrongSelected && (
                  <XCircle className="w-6 h-6 text-rose-600 flex-shrink-0" />
                )}
              </button>
            );
          })}
        </div>

        {/* Action Button: Check Answer */}
        {!answerResult && (
          <div className="pt-4 flex justify-end">
            <button
              onClick={handleCheckAnswer}
              disabled={!selectedOption || submitting}
              className="px-8 py-3 rounded-xl font-bold text-white bg-sky-600 hover:bg-sky-700 active:scale-95 transition shadow-md shadow-sky-600/20 disabled:opacity-40 disabled:cursor-not-allowed"
            >
              {submitting ? 'Checking...' : 'Check Answer'}
            </button>
          </div>
        )}

        {/* Detailed Explanation Box (Revealed on Submit) */}
        {answerResult && (
          <div className="space-y-6 pt-4 border-t border-slate-100 animate-fadeIn">
            <div
              className={`p-5 rounded-2xl border ${
                Boolean(answerResult.isCorrect || answerResult.correct)
                  ? 'bg-emerald-50/80 border-emerald-200 text-emerald-950'
                  : 'bg-rose-50/80 border-rose-200 text-rose-950'
              }`}
            >
              <div className="flex items-center space-x-3 mb-2">
                {Boolean(answerResult.isCorrect || answerResult.correct) ? (
                  <>
                    <CheckCircle2 className="w-6 h-6 text-emerald-600" />
                    <span className="font-bold text-lg text-emerald-900">
                      Excellent! That is correct.
                    </span>
                  </>
                ) : (
                  <>
                    <XCircle className="w-6 h-6 text-rose-600" />
                    <span className="font-bold text-lg text-rose-900">
                      Incorrect! Correct answer is Option {answerResult.correctOption}.
                    </span>
                  </>
                )}
              </div>
              <p className="text-xs text-slate-600 ml-9">
                Activity logged: Streak maintained at <strong>{answerResult.currentStreak} day(s)</strong>!
              </p>
            </div>

            {/* Step-by-Step Solution Accordion */}
            <div className="bg-slate-50 border border-slate-200 rounded-2xl p-5 sm:p-6 space-y-3">
              <div className="flex items-center space-x-2 text-indigo-700 font-bold text-sm">
                <Lightbulb className="w-5 h-5 text-indigo-600" />
                <span>Step-by-Step Mathematical Solution</span>
              </div>
              <div className="text-slate-800 text-sm sm:text-base leading-relaxed whitespace-pre-line pl-7 font-mono bg-white p-4 rounded-xl border border-slate-200">
                {answerResult.explanation}
              </div>
            </div>

            {/* Next Question Navigation */}
            <div className="flex items-center justify-between pt-2">
              <span className="text-xs text-slate-500">
                Solved this session: {solvedCount} Qs
              </span>
              {currentIndex < questions.length - 1 ? (
                <button
                  onClick={handleNext}
                  className="inline-flex items-center space-x-2 px-8 py-3 rounded-xl font-bold text-white bg-slate-900 hover:bg-slate-800 active:scale-95 transition shadow-md"
                >
                  <span>Next Question</span>
                  <ChevronRight className="w-5 h-5" />
                </button>
              ) : (
                <button
                  onClick={() => navigate('/')}
                  className="inline-flex items-center space-x-2 px-8 py-3 rounded-xl font-bold text-white bg-emerald-600 hover:bg-emerald-700 active:scale-95 transition shadow-md shadow-emerald-600/20"
                >
                  <span>Complete Chapter &amp; Return</span>
                  <CheckCircle2 className="w-5 h-5" />
                </button>
              )}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
