import React, { useState } from 'react';
import api from '../api/client';
import { BulkUploadResult } from '../types';
import { UploadCloud, CheckCircle2, FileDown, PlusCircle, AlertCircle } from 'lucide-react';

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
      setGeneratedMsg(
        `Mock test "${res.data.title}" generated with ${res.data.totalQuestions} questions.`
      );
    } catch (err) {
      console.error('Failed to generate mock test', err);
    } finally {
      setGeneratingTest(false);
    }
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-6 sm:py-8 space-y-6">
      {/* Header */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="space-y-1">
          <p className="text-xs font-semibold uppercase tracking-wider text-blue-600">
            Content Management
          </p>
          <h1 className="text-xl sm:text-2xl font-bold text-slate-900 tracking-tight">
            Question Bank Ingestion &amp; Test Generation
          </h1>
          <p className="text-sm text-slate-500">
            Import bulk MCQ batches via CSV to scale chapters or assemble new randomized mock examinations.
          </p>
        </div>

        <button
          type="button"
          onClick={handleGenerateTest}
          disabled={generatingTest}
          className="inline-flex items-center justify-center space-x-2 px-4 py-2.5 rounded-lg text-xs sm:text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors disabled:opacity-50 self-start sm:self-auto flex-shrink-0"
        >
          <PlusCircle className="w-4 h-4" />
          <span>{generatingTest ? 'Generating...' : 'Generate Mock Test'}</span>
        </button>
      </section>

      {generatedMsg && (
        <div className="p-3.5 rounded-lg bg-emerald-50 border border-emerald-200 text-emerald-900 text-xs sm:text-sm font-medium flex items-center space-x-2.5">
          <CheckCircle2 className="w-4 h-4 text-emerald-600 flex-shrink-0" />
          <span>{generatedMsg}</span>
        </div>
      )}

      {/* CSV Ingestion Card */}
      <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-7 shadow-xs space-y-5">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
          <div className="flex items-center space-x-3">
            <div className="w-9 h-9 rounded-lg bg-slate-100 border border-slate-200 text-slate-700 flex items-center justify-center flex-shrink-0">
              <UploadCloud className="w-4 h-4" />
            </div>
            <div>
              <h2 className="text-base font-semibold text-slate-900">
                CSV Batch Question Importer
              </h2>
              <p className="text-xs font-mono text-slate-500 break-all">
                chapter_slug, question_text, difficulty, option_a, option_b, option_c, option_d, correct_option, explanation
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={handleFillSample}
            className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg border border-slate-200 text-xs font-medium text-slate-700 hover:bg-slate-50 transition-colors self-start sm:self-auto flex-shrink-0"
          >
            <FileDown className="w-3.5 h-3.5" />
            <span>Load Sample CSV</span>
          </button>
        </div>

        <form onSubmit={handleUploadRaw} className="space-y-4">
          <div>
            <label htmlFor="csv-input" className="block text-xs font-semibold text-slate-700 mb-1.5">
              CSV Data Payload
            </label>
            <textarea
              id="csv-input"
              rows={9}
              value={csvText}
              onChange={(e) => setCsvText(e.target.value)}
              placeholder="Paste comma-separated MCQ rows here..."
              className="w-full p-3.5 rounded-lg border border-slate-300 text-xs font-mono text-slate-900 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-blue-600/20 focus:border-blue-600 transition"
            />
          </div>

          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <p className="text-[11px] text-slate-500 leading-relaxed">
              Supported chapter slugs: <span className="font-mono text-slate-700">percentages, profit-and-loss, time-and-work, number-systems, ratio-and-proportion, blood-relations, syllogisms, coding-decoding, spotting-errors, sentence-improvement</span>
            </p>

            <button
              type="submit"
              disabled={loading || !csvText.trim()}
              className="px-5 py-2.5 rounded-lg text-xs sm:text-sm font-semibold text-white bg-slate-900 hover:bg-slate-800 transition-colors disabled:opacity-40 flex-shrink-0"
            >
              {loading ? 'Ingesting...' : 'Ingest Questions'}
            </button>
          </div>
        </form>

        {uploadResult && (
          <div className="pt-4 border-t border-slate-100 space-y-3">
            <div className="flex flex-wrap items-center gap-4 bg-slate-50 border border-slate-200 p-3.5 rounded-lg text-xs font-medium">
              <span className="text-slate-600">
                Processed: <strong className="font-mono text-slate-900">{uploadResult.totalProcessed}</strong>
              </span>
              <span className="text-slate-300">|</span>
              <span className="text-emerald-700">
                Added: <strong className="font-mono">{uploadResult.successfullyAdded}</strong>
              </span>
              <span className="text-slate-300">|</span>
              <span className="text-rose-700">
                Skipped: <strong className="font-mono">{uploadResult.skippedCount}</strong>
              </span>
            </div>

            {uploadResult.errors.length > 0 && (
              <div className="p-3.5 rounded-lg bg-rose-50 border border-rose-200 space-y-1.5">
                <div className="flex items-center space-x-1.5 text-xs font-semibold text-rose-800">
                  <AlertCircle className="w-4 h-4" />
                  <span>Ingestion Warnings</span>
                </div>
                <ul className="text-xs text-rose-700 list-disc list-inside space-y-0.5 font-mono">
                  {uploadResult.errors.map((err, idx) => (
                    <li key={idx}>{err}</li>
                  ))}
                </ul>
              </div>
            )}
          </div>
        )}
      </section>
    </div>
  );
};
