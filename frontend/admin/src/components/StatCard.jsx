import { memo } from 'react';
import { TrendingUp, TrendingDown, ArrowUpRight, ArrowDownRight } from 'lucide-react';

const ACCENTS = {
  primary: 'bg-gradient-to-br from-[var(--color-primary)] to-[var(--color-primary-hover)] text-white shadow-lg',
  success: 'bg-gradient-to-br from-emerald-500 to-emerald-600 text-white shadow-lg',
  warning: 'bg-gradient-to-br from-amber-500 to-orange-500 text-white shadow-lg',
  info:    'bg-gradient-to-br from-blue-500 to-blue-600 text-white shadow-lg',
  neutral: 'bg-gradient-to-br from-[var(--color-bg-secondary)] to-[var(--color-bg-tertiary)] text-[var(--color-text-primary)] border border-[var(--color-border)]',
};

function StatCard({ icon: Icon, label, value, delta, accent = 'neutral' }) {
  const accentClass = ACCENTS[accent] || ACCENTS.neutral;
  const deltaPositive = typeof delta === 'number' ? delta >= 0 : null;

  return (
    <div className="card group">
      <div className="flex items-start justify-between gap-4">
        <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${accentClass} group-hover:scale-110 transition-transform duration-300`}>
          {Icon ? <Icon size={20} strokeWidth={2.2} /> : null}
        </div>
        {delta != null && (
          <div className={`flex items-center gap-1.5 px-3 py-1.5 rounded-full text-xs font-semibold ${
            deltaPositive 
              ? 'bg-emerald-50 text-emerald-700 dark:bg-emerald-900/30 dark:text-emerald-300 border border-emerald-200/60' 
              : 'bg-rose-50 text-rose-700 dark:bg-rose-900/30 dark:text-rose-300 border border-rose-200/60'
          }`}>
            {deltaPositive ? <TrendingUp size={12} strokeWidth={2.5} /> : <TrendingDown size={12} strokeWidth={2.5} />}
            <span>{Math.abs(delta)}%</span>
          </div>
        )}
      </div>
      
      <div className="mt-4">
        <p className="text-overline mb-2">{label}</p>
        <div className="text-h3 flex items-baseline gap-2">
          {value}
          <span className="text-body-sm">
            {deltaPositive ? <ArrowUpRight size={14} className="text-emerald-500" /> : <ArrowDownRight size={14} className="text-rose-500" />}
          </span>
        </div>
      </div>
    </div>
  );
}

export default memo(StatCard, (prevProps, nextProps) => {
  return (
    prevProps.label === nextProps.label &&
    prevProps.value === nextProps.value &&
    prevProps.delta === nextProps.delta &&
    prevProps.accent === nextProps.accent
  );
});
