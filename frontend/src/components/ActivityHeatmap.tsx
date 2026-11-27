import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { HeatmapDay } from '../types';
import { Calendar, Flame } from 'lucide-react';

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
      <div className="bg-white p-6 rounded-3xl border border-slate-200 animate-pulse h-48"></div>
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
        return 'bg-emerald-200 border-emerald-300';
      case 2:
        return 'bg-emerald-400 border-emerald-500';
      case 3:
        return 'bg-emerald-500 border-emerald-600';
      case 4:
        return 'bg-emerald-700 border-emerald-800';
      default:
        return 'bg-slate-100 border-slate-200/80';
    }
  };

  const totalQuestionsYear = days.reduce((acc, d) => acc + d.count, 0);
  const activeDaysCount = days.filter((d) => d.count > 0).length;

  return (
    <div className="bg-white rounded-3xl border border-slate-200/80 p-6 sm:p-8 shadow-xs space-y-5">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-slate-100 pb-4">
        <div className="flex items-center space-x-3">
          <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-600 border border-emerald-100 flex items-center justify-center font-bold">
            <Calendar className="w-5 h-5" />
          </div>
          <div>
            <h3 className="text-lg font-bold text-slate-900">
              365-Day Practice Activity Heatmap
            </h3>
            <p className="text-xs text-slate-500">
              Your daily aptitude problem solving frequency and study intensity.
            </p>
          </div>
        </div>

        <div className="flex items-center space-x-4 text-xs font-semibold text-slate-600">
          <span>Active Days: <strong className="text-emerald-700 font-bold">{activeDaysCount}</strong></span>
          <span>•</span>
          <span>Total Solved: <strong className="text-indigo-700 font-bold">{totalQuestionsYear} Qs</strong></span>
        </div>
      </div>

      {/* Heatmap Grid Overflow Container */}
      <div className="overflow-x-auto pb-2">
        <div className="inline-flex gap-1.5 p-1 min-w-[720px]">
          {weeks.map((week, wIdx) => (
            <div key={wIdx} className="flex flex-col gap-1.5">
              {week.map((day) => (
                <div
                  key={day.date}
                  className={`w-3.5 h-3.5 rounded-xs border transition-transform hover:scale-125 cursor-pointer ${getColorClass(
                    day.level
                  )}`}
                  title={`${day.date}: ${day.count} question(s) solved`}
                />
              ))}
            </div>
          ))}
        </div>
      </div>

      {/* Heatmap Legend */}
      <div className="flex items-center justify-between text-xs text-slate-400 pt-2 border-t border-slate-100">
        <span>365 days ago</span>
        <div className="flex items-center space-x-2">
          <span>Less</span>
          <div className="flex space-x-1">
            <div className="w-3 h-3 rounded-xs bg-slate-100 border border-slate-200"></div>
            <div className="w-3 h-3 rounded-xs bg-emerald-200 border border-emerald-300"></div>
            <div className="w-3 h-3 rounded-xs bg-emerald-400 border border-emerald-500"></div>
            <div className="w-3 h-3 rounded-xs bg-emerald-500 border border-emerald-600"></div>
            <div className="w-3 h-3 rounded-xs bg-emerald-700 border border-emerald-800"></div>
          </div>
          <span>More</span>
        </div>
        <span>Today</span>
      </div>
    </div>
  );
};
