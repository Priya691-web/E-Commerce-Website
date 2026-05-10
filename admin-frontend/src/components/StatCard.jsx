import { TrendingUp, TrendingDown } from 'lucide-react';

const ACCENTS = {
  primary: 'bg-ink-900 text-white dark:bg-white dark:text-ink-900',
  success: 'bg-emerald-50 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300',
  warning: 'bg-amber-50 text-amber-700 dark:bg-amber-900/30 dark:text-amber-300',
  info:    'bg-blue-50 text-blue-700 dark:bg-blue-900/30 dark:text-blue-300',
  neutral: 'bg-ink-100 text-ink-700 dark:bg-ink-700 dark:text-ink-200',
};

export default function StatCard({ icon: Icon, label, value, delta, accent = 'neutral' }) {
  const accentClass = ACCENTS[accent] || ACCENTS.neutral;
  const deltaPositive = typeof delta === 'number' ? delta >= 0 : null;

  return (
    <div className="card p-5 flex items-center gap-4 hover:shadow-pop transition">
      <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${accentClass}`}>
        {Icon ? <Icon size={18} strokeWidth={2} /> : null}
      </div>
      <div className="flex flex-col gap-0.5 min-w-0">
        <div className="text-[11px] font-semibold uppercase tracking-wider text-ink-400">
          {label}
        </div>
        <div className="text-xl font-bold tracking-tight text-ink-900 dark:text-white truncate">
          {value}
        </div>
        {delta != null && (
          <div className={`text-xs flex items-center gap-1 ${deltaPositive ? 'text-emerald-600' : 'text-rose-600'}`}>
            {deltaPositive ? <TrendingUp size={12} /> : <TrendingDown size={12} />}
            <span>{Math.abs(delta)}%</span>
          </div>
        )}
      </div>
    </div>
  );
}
