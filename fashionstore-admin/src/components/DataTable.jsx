import { ChevronLeft, ChevronRight } from 'lucide-react';

export default function DataTable({ columns, rows, empty = 'No data available', getRowKey, pagination, onPageChange }) {
  if (!rows || rows.length === 0) {
    return (
      <div className="py-16 text-center">
        <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-ink-100 dark:bg-ink-700 mb-4">
          <svg className="w-8 h-8 text-ink-400 dark:text-ink-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4" />
          </svg>
        </div>
        <p className="text-sm text-ink-400 dark:text-ink-500 font-medium">{empty}</p>
      </div>
    );
  }

  return (
    <div className="space-y-4">
      <div className="overflow-x-auto rounded-2xl border border-ink-200 dark:border-ink-700 bg-white dark:bg-ink-800 shadow-lg hover:shadow-xl transition-shadow duration-300">
        <table className="w-full text-sm text-left">
          <thead className="sticky top-0 z-10">
            <tr className="border-b border-ink-200 dark:border-ink-700 bg-gradient-to-r from-ink-50/80 to-ink-100/80 dark:from-ink-900/80 dark:to-ink-800/80 backdrop-blur-sm">
              {columns.map((c) => (
                <th
                  key={c.key}
                  className="px-6 py-4 font-semibold text-[11px] uppercase tracking-wider text-ink-400 whitespace-nowrap"
                  style={{ width: c.width }}
                >
                  {c.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-ink-200 dark:divide-ink-700">
            {rows.map((row, i) => (
              <tr
                key={getRowKey ? getRowKey(row, i) : i}
                className="hover:bg-gradient-to-r hover:from-ink-50/60 hover:to-ink-100/60 dark:hover:from-ink-700/60 dark:hover:to-ink-600/60 transition-all duration-200 group hover:shadow-sm"
              >
                {columns.map((c) => (
                  <td key={c.key} className="px-6 py-4 text-ink-700 dark:text-ink-200 group-hover:text-ink-900 dark:group-hover:text-white transition-colors">
                    {c.render ? c.render(row) : row[c.key]}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {pagination && (
        <div className="flex items-center justify-between px-4 py-3 bg-white dark:bg-ink-800 rounded-xl border border-ink-200 dark:border-ink-700 shadow-lg hover:shadow-xl transition-shadow duration-300">
          <div className="text-sm text-ink-500 dark:text-ink-400">
            Showing <span className="font-semibold text-ink-900 dark:text-white">{pagination.from}</span> to{' '}
            <span className="font-semibold text-ink-900 dark:text-white">{pagination.to}</span> of{' '}
            <span className="font-semibold text-ink-900 dark:text-white">{pagination.total}</span> results
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={() => onPageChange(pagination.page - 1)}
              disabled={!pagination.hasPrev}
              className="p-2 rounded-lg border border-ink-200 dark:border-ink-700 hover:bg-gradient-to-r hover:from-ink-50 hover:to-ink-100 dark:hover:from-ink-700 dark:hover:to-ink-600 hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
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
                    className={`w-9 h-9 rounded-lg text-sm font-medium transition-all duration-200 hover:shadow-md ${
                      pageNum === pagination.page
                        ? 'bg-gradient-to-r from-ink-900 to-ink-800 text-white dark:from-white dark:to-ink-50 dark:text-ink-900 shadow-lg border border-ink-700 dark:border-white'
                        : 'text-ink-600 hover:bg-gradient-to-r hover:from-ink-50 hover:to-ink-100 dark:text-ink-300 dark:hover:from-ink-700 dark:hover:to-ink-600 hover:text-ink-900 dark:hover:text-white'
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
              className="p-2 rounded-lg border border-ink-200 dark:border-ink-700 hover:bg-gradient-to-r hover:from-ink-50 hover:to-ink-100 dark:hover:from-ink-700 dark:hover:to-ink-600 hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed transition-all duration-200"
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
