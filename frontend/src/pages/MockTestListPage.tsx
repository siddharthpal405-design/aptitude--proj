import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { MockTestSummary } from '../types';
import { Timer, FileCheck2, Award, Clock, AlertTriangle, ArrowRight, Sparkles } from 'lucide-react';

export const MockTestListPage: React.FC = () => {
  const [tests, setTests] = useState<MockTestSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchTests = async () => {
      try {
        const res = await api.get<MockTestSummary[]>('/mock-tests');
        setTests(res.data);
      } catch (err) {
        console.error('Failed to load mock tests', err);
      } finally {
        setLoading(false);
      }
    };
    fetchTests();
  }, []);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header Banner */}
      <div className="rounded-3xl bg-gradient-to-r from-indigo-900 via-sky-900 to-indigo-950 text-white p-6 sm:p-10 shadow-xl space-y-3">
        <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur-md border border-white/15 text-xs font-semibold text-sky-300">
          <Sparkles className="w-3.5 h-3.5" />
          <span>Real Exam Simulation</span>
        </div>
        <h1 className="text-2xl sm:text-4xl font-extrabold tracking-tight">
          Timed Aptitude Mock Tests
        </h1>
        <p className="text-slate-300 text-sm sm:text-base max-w-2xl">
          Test your preparation under real exam conditions with strict countdown timers, question palettes, negative marking, and in-depth performance analytics.
        </p>
      </div>

      {/* Tests Grid */}
      <div className="space-y-4">
        <h2 className="text-xl font-bold text-slate-900">Available Examination Sprints</h2>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {[1, 2].map((i) => (
              <div key={i} className="h-56 rounded-3xl bg-slate-200/60 animate-pulse"></div>
            ))}
          </div>
        ) : tests.length === 0 ? (
          <div className="bg-white p-12 text-center rounded-3xl border border-slate-200 text-slate-500">
            No mock tests currently scheduled. Check back soon!
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {tests.map((test) => (
              <div
                key={test.id}
                className="bg-white rounded-3xl border border-slate-200/90 p-6 sm:p-8 shadow-xs hover:shadow-md hover:border-sky-300 transition-all flex flex-col justify-between space-y-6"
              >
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <span
                      className={`text-xs font-bold px-3 py-1 rounded-md uppercase tracking-wider ${
                        test.difficulty === 'EASY'
                          ? 'bg-emerald-100 text-emerald-800'
                          : test.difficulty === 'MEDIUM'
                          ? 'bg-sky-100 text-sky-800'
                          : 'bg-rose-100 text-rose-800'
                      }`}
                    >
                      {test.difficulty} EXAM
                    </span>
                    <span className="flex items-center space-x-1.5 text-xs font-semibold text-slate-500">
                      <Clock className="w-4 h-4 text-sky-600" />
                      <span>{test.durationMinutes} Minutes</span>
                    </span>
                  </div>

                  <h3 className="text-xl font-bold text-slate-900 leading-snug">
                    {test.title}
                  </h3>

                  <p className="text-xs sm:text-sm text-slate-500 leading-relaxed">
                    {test.description}
                  </p>

                  <div className="grid grid-cols-3 gap-3 pt-2 text-center">
                    <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                      <span className="block text-xs text-slate-400 font-medium">Questions</span>
                      <strong className="text-base text-slate-800">{test.totalQuestions}</strong>
                    </div>
                    <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                      <span className="block text-xs text-slate-400 font-medium">Total Marks</span>
                      <strong className="text-base text-slate-800">{test.totalMarks}</strong>
                    </div>
                    <div className="p-3 rounded-2xl bg-slate-50 border border-slate-100">
                      <span className="block text-xs text-slate-400 font-medium">Negative</span>
                      <strong className="text-base text-rose-600">-{test.negativeMarkingRate}</strong>
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-xs text-slate-400">
                    Timed • Palette Navigation
                  </span>
                  <Link
                    to={`/mock-tests/${test.id}/live`}
                    className="inline-flex items-center space-x-2 px-6 py-2.5 rounded-xl font-bold text-sm text-white bg-sky-600 hover:bg-sky-700 active:scale-95 transition shadow-sm hover:shadow"
                  >
                    <span>Start Test Now</span>
                    <ArrowRight className="w-4 h-4" />
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
