/**
 * Table Primitive
 * Reusable table component with standardized styling
 */

import { forwardRef } from 'react';

const Table = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <div className="overflow-x-auto">
      <table
        ref={ref}
        className={`w-full border-collapse ${className}`}
        {...props}
      >
        {children}
      </table>
    </div>
  );
});

Table.displayName = 'Table';

const TableHeader = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <thead
      ref={ref}
      className={`bg-ink-50 dark:bg-ink-800/50 ${className}`}
      {...props}
    >
      {children}
    </thead>
  );
});

TableHeader.displayName = 'TableHeader';

const TableBody = forwardRef(({ className = '', children, ...props }, ref) => {
  return <tbody ref={ref} className={className} {...props}>{children}</tbody>;
});

TableBody.displayName = 'TableBody';

const TableRow = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <tr
      ref={ref}
      className={`border-b border-ink-200 dark:border-ink-700 hover:bg-ink-50 dark:hover:bg-ink-800/50 transition ${className}`}
      {...props}
    >
      {children}
    </tr>
  );
});

TableRow.displayName = 'TableRow';

const TableHead = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <th
      ref={ref}
      className={`px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-ink-600 dark:text-ink-400 ${className}`}
      {...props}
    >
      {children}
    </th>
  );
});

TableHead.displayName = 'TableHead';

const TableCell = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <td
      ref={ref}
      className={`px-4 py-3 text-sm text-ink-700 dark:text-ink-200 ${className}`}
      {...props}
    >
      {children}
    </td>
  );
});

TableCell.displayName = 'TableCell';

Table.Header = TableHeader;
Table.Body = TableBody;
Table.Row = TableRow;
Table.Head = TableHead;
Table.Cell = TableCell;

export default Table;
