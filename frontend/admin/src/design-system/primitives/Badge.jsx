/**
 * Badge Primitive
 * Reusable badge component for status indicators
 */

import { forwardRef } from 'react';

const badgeVariants = {
  default: 'bg-ink-100 text-ink-700 border-ink-200 dark:bg-ink-700 dark:text-ink-200 dark:border-ink-600',
  success: 'bg-success-50 text-success-700 border-success-200 dark:bg-success-900/30 dark:text-success-300 dark:border-success-800',
  warning: 'bg-warning-50 text-warning-700 border-warning-200 dark:bg-warning-900/30 dark:text-warning-300 dark:border-warning-800',
  error: 'bg-error-50 text-error-700 border-error-200 dark:bg-error-900/30 dark:text-error-300 dark:border-error-800',
  info: 'bg-info-50 text-info-700 border-info-200 dark:bg-info-900/30 dark:text-info-300 dark:border-info-800',
  accent: 'bg-accent text-ink-900 border-accent-dark',
};

const Badge = forwardRef(({ variant = 'default', className = '', children, ...props }, ref) => {
  return (
    <span
      ref={ref}
      className={`
        inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold uppercase tracking-wider border
        ${badgeVariants[variant]}
        ${className}
      `}
      {...props}
    >
      {children}
    </span>
  );
});

Badge.displayName = 'Badge';

export default Badge;
