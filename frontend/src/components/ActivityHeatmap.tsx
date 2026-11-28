import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { HeatmapDay } from '../types';
import { Calendar } from 'lucide-react';

export const ActivityHeatmap: React.FC = () => {
  const [days, setDays] = useState<HeatmapDay[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchHeatmap = async () => {
      try {
        const res = await api.get<HeatmapDay[]>('/user/streak/heatmap');
        setDays(res.data);
      } catch (err) {
        console.error('Failed to load heatmap', err);
      } finally {
        setLoading(false);
      }
    };
    fetchHeatmap();
  }, []);

  if (loading) {
    return (
      <div className="bg-white p-6 rounded-xl border border-slate-200 animate-pulse h-44" />
    );
  }

  // Group into weeks of 7 days
  const weeks: HeatmapDay[][] = [];
  for (let i = 0; i < days.length; i += 7) {
    weeks.push(days.slice(i, i + 7));
  }

  const getColorClass = (level: number) => {
    switch (level) {
      case 1:
        return 'bg-emerald-200 border-emerald-300/70';
      case 2:
        return 'bg-emerald-400 border-emerald-500/70';
      case 3:
        return 'bg-emerald-600 border-emerald-700/70';
      case 4:
        return 'bg-emerald-800 border-emerald-900/70';
      default:
        return 'bg-slate-100 border-slate-200/80';
    }
  };

  const totalQuestionsYear = days.reduce((acc, d) => acc + d.count, 0);
  const activeDaysCount = days.filter((d) => d.count > 0).length;

  return (
    <section className="bg-white rounded-xl border border-slate-200 p-5 sm:p-6 shadow-xs space-y-4">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-slate-100 pb-4">
        <div className="flex items-center space-x-3">
          <div className="w-9 h-9 rounded-lg bg-slate-100 border border-slate-200 text-slate-700 flex items-center justify-center flex-shrink-0">
            <Calendar className="w-4 h-4" />
          </div>
          <div>
            <h2 className="text-base font-semibold text-slate-900 tracking-tight">
              Practice Consistency
            </h2>
            <p className="text-xs text-slate-500">
              365-day activity log of daily questions solved
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-4 text-xs text-slate-600">
          <div>
            Active days:{' '}
            <span className="font-mono font-semibold text-slate-900">{activeDaysCount}</span>
          </div>
          <span className="text-slate-300">|</span>
          <div>
            Total solved:{' '}
            <span className="font-mono font-semibold text-slate-900">{totalQuestionsYear}</span>
          </div>
        </div>
      </div>

      {/* Heatmap Grid */}
      <div className="overflow-x-auto pb-1">
        <div className="inline-flex gap-1 py-1 min-w-[680px]">
          {weeks.map((week, wIdx) => (
            <div key={wIdx} className="flex flex-col gap-1">
              {week.map((day) => (
                <div
                  key={day.date}
                  className={`w-3 h-3 rounded-[2px] border transition-colors ${getColorClass(
                    day.level
                  )}`}
                  title={`${day.date}: ${day.count} question${day.count === 1 ? '' : 's'} solved`}
                />
              ))}
            </div>
          ))}
        </div>
      </div>

      {/* Legend */}
      <div className="flex items-center justify-between text-[11px] text-slate-400 pt-2 border-t border-slate-100">
        <span>12 months ago</span>
        <div className="flex items-center space-x-1.5">
          <span>Less</span>
          <div className="flex space-x-1">
            <div className="w-2.5 h-2.5 rounded-[2px] bg-slate-100 border border-slate-200" />
            <div className="w-2.5 h-2.5 rounded-[2px] bg-emerald-200 border border-emerald-300/70" />
            <div className="w-2.5 h-2.5 rounded-[2px] bg-emerald-400 border border-emerald-500/70" />
            <div className="w-2.5 h-2.5 rounded-[2px] bg-emerald-600 border border-emerald-700/70" />
            <div className="w-2.5 h-2.5 rounded-[2px] bg-emerald-800 border border-emerald-900/70" />
          </div>
          <span>More</span>
        </div>
        <span>Today</span>
      </div>
    </section>
  );
};
