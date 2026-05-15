import { memo } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';

function DataTable({ columns, rows, empty = 'No data available', getRowKey, pagination, onPageChange }) {
  if (!rows || rows.length === 0) {
    return (
      <div className="py-16 text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-[var(--color-bg-secondary)] mb-4">
          <svg className="w-8 h-8 text-[var(--color-text-muted)]" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
          </svg>
        </div>
        <p className="text-body text-[var(--color-text-muted)] font-medium">{empty}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="overflow-x-auto rounded-xl border border-[var(--color-border)] bg-white shadow-sm hover-lift">
        <table className="w-full text-sm text-left">
          <thead className="sticky top-0 z-10">
            <tr className="border-b border-[var(--color-border)] bg-[var(--color-bg-secondary)]">
              {columns.map((c) => (
                <th
                  key={c.key}
                  className="px-6 py-4 font-semibold text-xs uppercase tracking-wider text-[var(--color-text-muted)] whitespace-nowrap"
                  style={{ width: c.width }}
                >
                  {c.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-[var(--color-border-light)]">
            {rows.map((row, i) => (
              <tr
                key={getRowKey ? getRowKey(row, i) : i}
                className="hover:bg-[var(--color-bg-secondary)] transition-colors duration-200"
              >
                {columns.map((c) => (
                  <td key={c.key} className="px-6 py-4 text-[var(--color-text-primary)]">
                    {c.render ? c.render(row) : row[c.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {pagination && (
        <div className="flex items-center justify-between px-4 py-3 bg-white rounded-xl border border-[var(--color-border)] shadow-sm">
          <div className="text-body-sm text-[var(--color-text-secondary)]">
            Showing <span className="font-semibold text-[var(--color-text-primary)]">{pagination.from}</span> to{' '}
            <span className="font-semibold text-[var(--color-text-primary)]">{pagination.to}</span> of{' '}
            <span className="font-semibold text-[var(--color-text-primary)]">{pagination.total}</span> results
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => onPageChange(pagination.page - 1)}
              disabled={!pagination.hasPrev}
              className="p-2 rounded-lg border border-[var(--color-border)] hover:bg-[var(--color-bg-secondary)] disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              <ChevronLeft size={16} />
            </button>
            <div className="flex items-center gap-1">
              {Array.from({ length: Math.min(5, pagination.totalPages) }, (_, i) => {
                let pageNum = i + 1;
                if (pagination.totalPages > 5) {
                  if (pagination.page > 3) pageNum = pagination.page - 2 + i;
                  if (pageNum > pagination.totalPages) pageNum = pagination.totalPages - 4 + i;
                }
                if (pageNum < 1 || pageNum > pagination.totalPages) return null;
                
                return (
                  <button
                    key={pageNum}
                    onClick={() => onPageChange(pageNum)}
                    className={`w-9 h-9 rounded-lg text-sm font-semibold transition-all duration-200 ${
                      pageNum === pagination.page
                        ? 'bg-[var(--color-primary)] text-white shadow-md'
                        : 'text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-secondary)] hover:text-[var(--color-text-primary)] border border-[var(--color-border)]'
                    }`}
                  >
                    {pageNum}
                  </button>
                );
              })}
            </div>
            <button
              onClick={() => onPageChange(pagination.page + 1)}
              disabled={!pagination.hasNext}
              className="p-2 rounded-lg border border-[var(--color-border)] hover:bg-[var(--color-bg-secondary)] disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

export default memo(DataTable, (prevProps, nextProps) => {
  return (
    prevProps.rows === nextProps.rows &&
    prevProps.columns === nextProps.columns &&
    prevProps.pagination === nextProps.pagination
  );
});
