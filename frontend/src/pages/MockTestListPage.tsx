import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../api/client';
import { MockTestSummary } from '../types';
import { Clock, ArrowRight, FileCheck2 } from 'lucide-react';

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
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6">
      {/* Header */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-2">
        <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">
          Assessment Center
        </p>
        <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
          Timed Mock Examinations
        </h1>
        <p className="text-sm text-slate-500 max-w-2xl">
          Simulate real testing conditions with strict countdown timers, interactive question palettes, negative marking, and full post-exam scorecards.
        </p>
      </section>

      {/* Tests Grid */}
      <section className="space-y-4">
        <h2 className="text-base font-semibold text-slate-900">Available Assessments</h2>

        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {[1, 2].map((i) => (
              <div key={i} className="h-52 rounded-xl bg-slate-200/60 animate-pulse" />
            ))}
          </div>
        ) : tests.length === 0 ? (
          <div className="bg-white p-10 text-center rounded-xl border border-slate-200 space-y-2">
            <FileCheck2 className="w-8 h-8 text-slate-400 mx-auto" />
            <p className="text-sm font-medium text-slate-600">
              No mock examinations are currently scheduled.
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 sm:gap-6">
            {tests.map((test) => (
              <div
                key={test.id}
                className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs hover:border-slate-300 transition-colors flex flex-col justify-between space-y-5"
              >
                <div className="space-y-4">
                  <div className="flex items-center justify-between">
                    <span
                      className={`text-[11px] font-mono font-semibold px-2.5 py-0.5 rounded-md border uppercase tracking-wider ${
                        test.difficulty === 'EASY'
                          ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                          : test.difficulty === 'MEDIUM'
                          ? 'bg-blue-50 text-blue-700 border-blue-200'
                          : 'bg-rose-50 text-rose-700 border-rose-200'
                      }`}
                    >
                      {test.difficulty}
                    </span>
                    <span className="flex items-center space-x-1.5 text-xs font-mono font-medium text-slate-600">
                      <Clock className="w-3.5 h-3.5 text-slate-400" />
                      <span>{test.durationMinutes} min</span>
                    </span>
                  </div>

                  <div>
                    <h3 className="text-lg font-bold text-slate-900 leading-snug">
                      {test.title}
                    </h3>
                    <p className="text-xs sm:text-sm text-slate-500 leading-relaxed mt-1.5">
                      {test.description}
                    </p>
                  </div>

                  <div className="grid grid-cols-3 gap-2.5 text-center">
                    <div className="p-2.5 rounded-lg bg-slate-50 border border-slate-200/80">
                      <span className="block text-[11px] text-slate-500">Questions</span>
                      <strong className="text-sm font-mono font-semibold text-slate-900">
                        {test.totalQuestions}
                      </strong>
                    </div>
                    <div className="p-2.5 rounded-lg bg-slate-50 border border-slate-200/80">
                      <span className="block text-[11px] text-slate-500">Max Marks</span>
                      <strong className="text-sm font-mono font-semibold text-slate-900">
                        {test.totalMarks}
                      </strong>
                    </div>
                    <div className="p-2.5 rounded-lg bg-slate-50 border border-slate-200/80">
                      <span className="block text-[11px] text-slate-500">Negative</span>
                      <strong className="text-sm font-mono font-semibold text-rose-700">
                        -{test.negativeMarkingRate}
                      </strong>
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-slate-100 flex items-center justify-between">
                  <span className="text-xs text-slate-400">
                    Auto-saved responses
                  </span>
                  <Link
                    to={`/mock-tests/${test.id}/live`}
                    className="inline-flex items-center space-x-2 px-4 py-2 rounded-lg text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors"
                  >
                    <span>Begin Test</span>
                    <ArrowRight className="w-4 h-4" />
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>
    </div>
  );
};
