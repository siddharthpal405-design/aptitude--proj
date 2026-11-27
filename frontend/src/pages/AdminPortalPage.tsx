import React, { useState } from 'react';
import api from '../api/client';
import { BulkUploadResult } from '../types';
import { UploadCloud, FileText, CheckCircle2, AlertCircle, Download, Sparkles, RefreshCw } from 'lucide-react';

export const AdminPortalPage: React.FC = () => {
  const [csvText, setCsvText] = useState('');
  const [uploadResult, setUploadResult] = useState<BulkUploadResult | null>(null);
  const [loading, setLoading] = useState(false);
  const [generatingTest, setGeneratingTest] = useState(false);
  const [generatedMsg, setGeneratedMsg] = useState<string | null>(null);

  const sampleCsv = `chapter_slug,question_text,difficulty,option_a,option_b,option_c,option_d,correct_option,explanation
percentages,"If the price of wheat decreases by 20%, by what percent can a family increase consumption with same budget?",EASY,"20%","25%","30%","16.67%",B,"Formula: [R / (100 - R)] * 100 = [20 / 80] * 100 = 25%."
profit-and-loss,"A shopkeeper sells a table for Rs. 540 at a loss of 10%. Find its cost price.",EASY,"Rs. 580","Rs. 600","Rs. 620","Rs. 650",B,"CP = SP * 100 / (100 - Loss%) = 540 * 100 / 90 = Rs. 600."
time-and-work,"Pipe A fills a tank in 4 hours and Pipe B empties in 6 hours. Both opened together take how long?",MEDIUM,"10 hours","12 hours","8 hours","14 hours",B,"Net rate = 1/4 - 1/6 = (3 - 2)/12 = 1/12. Time = 12 hours."
number-systems,"What is the remainder when 2^31 is divided by 5?",MEDIUM,"1","2","3","4",C,"2^1=2, 2^2=4, 2^3=8=3 mod 5, 2^4=1 mod 5. 31 mod 4 = 3, so 2^31 = 2^3 = 3 mod 5."`;

  const handleFillSample = () => {
    setCsvText(sampleCsv);
  };

  const handleUploadRaw = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!csvText.trim()) return;
    setLoading(true);
    setUploadResult(null);

    try {
      const res = await api.post<BulkUploadResult>('/admin/questions/bulk-upload-raw', csvText, {
        headers: { 'Content-Type': 'text/plain' },
      });
      setUploadResult(res.data);
    } catch (err: any) {
      setUploadResult({
        totalProcessed: 0,
        successfullyAdded: 0,
        skippedCount: 1,
        errors: [err.response?.data?.message || 'Failed to upload CSV'],
      });
    } finally {
      setLoading(false);
    }
  };

  const handleGenerateTest = async () => {
    setGeneratingTest(true);
    setGeneratedMsg(null);
    try {
      const res = await api.post('/admin/mock-tests/generate');
      setGeneratedMsg(`Mock test '${res.data.title}' generated successfully with ${res.data.totalQuestions} questions!`);
    } catch (err) {
      console.error('Failed to generate mock test', err);
    } finally {
      setGeneratingTest(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Admin Header */}
      <div className="rounded-3xl bg-slate-900 text-white p-6 sm:p-8 flex flex-col sm:flex-row sm:items-center justify-between gap-4 shadow-xl">
        <div className="space-y-1">
          <span className="text-xs font-bold text-sky-400 uppercase tracking-wider">
            Admin Management
          </span>
          <h1 className="text-2xl sm:text-3xl font-extrabold">
            Question Bank Ingestion &amp; Test Engine
          </h1>
          <p className="text-slate-400 text-xs sm:text-sm">
            Scale chapter question banks to 100+ MCQs via bulk CSV upload or generate new simulated mock tests.
          </p>
        </div>

        <button
          onClick={handleGenerateTest}
          disabled={generatingTest}
          className="inline-flex items-center space-x-2 px-5 py-3 rounded-xl font-bold text-xs text-white bg-indigo-600 hover:bg-indigo-700 active:scale-95 transition shadow-md disabled:opacity-50"
        >
          <Sparkles className="w-4 h-4" />
          <span>{generatingTest ? 'Generating...' : 'Generate New Mock Test'}</span>
        </button>
      </div>

      {generatedMsg && (
        <div className="p-4 rounded-2xl bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-semibold flex items-center space-x-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
          <span>{generatedMsg}</span>
        </div>
      )}

      {/* CSV Ingestion Form */}
      <div className="bg-white rounded-3xl border border-slate-200 p-6 sm:p-8 space-y-6 shadow-xs">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl bg-sky-50 text-sky-600 border border-sky-100 flex items-center justify-center font-bold">
              <UploadCloud className="w-5 h-5" />
            </div>
            <div>
              <h2 className="text-lg font-bold text-slate-900">
                Bulk Question CSV Ingestion
              </h2>
              <p className="text-xs text-slate-500">
                Format: chapter_slug, question_text, difficulty, option_a, option_b, option_c, option_d, correct_option, explanation
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={handleFillSample}
            className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-xl border border-slate-200 text-xs font-semibold text-slate-700 hover:bg-slate-50 transition"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Load Sample CSV</span>
          </button>
        </div>

        <form onSubmit={handleUploadRaw} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold uppercase text-slate-600 mb-1.5">
              CSV Content
            </label>
            <textarea
              rows={9}
              value={csvText}
              onChange={(e) => setCsvText(e.target.value)}
              placeholder="Paste comma-separated rows here..."
              className="w-full p-4 rounded-2xl border border-slate-200 text-xs font-mono focus:outline-none focus:ring-2 focus:ring-sky-500 transition"
            />
          </div>

          <div className="flex items-center justify-between">
            <span className="text-xs text-slate-400">
              Valid chapters: percentages, profit-and-loss, time-and-work, number-systems, ratio-and-proportion, blood-relations, syllogisms, coding-decoding, spotting-errors, sentence-improvement
            </span>

            <button
              type="submit"
              disabled={loading || !csvText.trim()}
              className="px-6 py-2.5 rounded-xl font-bold text-xs text-white bg-sky-600 hover:bg-sky-700 active:scale-95 transition shadow-sm disabled:opacity-40"
            >
              {loading ? 'Ingesting...' : 'Upload & Ingest Questions'}
            </button>
          </div>
        </form>

        {/* Upload Feedback */}
        {uploadResult && (
          <div className="pt-4 border-t border-slate-100 space-y-3">
            <div className="flex items-center space-x-4 bg-slate-50 p-4 rounded-2xl text-xs font-semibold">
              <span className="text-slate-600">Processed: <strong>{uploadResult.totalProcessed}</strong></span>
              <span>•</span>
              <span className="text-emerald-700">Added: <strong>{uploadResult.successfullyAdded}</strong></span>
              <span>•</span>
              <span className="text-rose-600">Skipped: <strong>{uploadResult.skippedCount}</strong></span>
            </div>

            {uploadResult.errors.length > 0 && (
              <div className="p-4 rounded-2xl bg-rose-50 border border-rose-200 space-y-1">
                <span className="text-xs font-bold text-rose-800">Errors Reported:</span>
                <ul className="text-xs text-rose-700 list-disc list-inside space-y-0.5">
                  {uploadResult.errors.map((err, idx) => (
                    <li key={idx}>{err}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};
