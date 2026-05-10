export default function DataTable({ columns, rows, empty = 'No data', getRowKey }) {
  if (!rows || rows.length === 0) {
    return (
      <div className="py-12 text-center text-sm text-ink-400 dark:text-ink-500">
        {empty}
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className="w-full text-sm text-left">
        <thead>
          <tr className="border-b border-ink-200 dark:border-ink-700 bg-ink-50/50 dark:bg-ink-900/50">
            {columns.map((c) => (
              <th
                key={c.key}
                className="px-5 py-3 font-semibold text-[11px] uppercase tracking-wider text-ink-400 whitespace-nowrap"
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
              className="hover:bg-ink-50 dark:hover:bg-ink-700/50 transition"
            >
              {columns.map((c) => (
                <td key={c.key} className="px-5 py-3 text-ink-700 dark:text-ink-200">
                  {c.render ? c.render(row) : row[c.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
