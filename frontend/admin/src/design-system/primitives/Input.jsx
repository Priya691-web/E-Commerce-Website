/**
 * Input Primitive
 * Reusable input component with variants and sizes
 */

import { forwardRef } from 'react';

const inputVariants = {
  default: 'border border-ink-200 bg-white text-ink-900 placeholder:text-ink-400 focus:border-ink-700 focus:ring-2 focus:ring-ink-900/10 dark:bg-ink-800 dark:border-ink-700 dark:text-ink-100 dark:placeholder:text-ink-400 dark:focus:border-white dark:focus:ring-white/10',
  filled: 'border-0 bg-ink-50 text-ink-900 placeholder:text-ink-400 focus:bg-ink-100 focus:ring-2 focus:ring-ink-900/10 dark:bg-ink-800 dark:text-ink-100 dark:placeholder:text-ink-400 dark:focus:bg-ink-700 dark:focus:ring-white/10',
  ghost: 'border-0 bg-transparent text-ink-900 placeholder:text-ink-400 focus:bg-ink-50 focus:ring-2 focus:ring-ink-900/10 dark:bg-transparent dark:text-ink-100 dark:placeholder:text-ink-400 dark:focus:bg-ink-800 dark:focus:ring-white/10',
};

const inputSizes = {
  sm: 'h-8 px-3 text-xs',
  md: 'h-10 px-3.5 text-sm',
  lg: 'h-12 px-4 text-base',
};

const Input = forwardRef(
  ({ variant = 'default', size = 'md', className = '', type = 'text', ...props }, ref) => {
    return (
      <input
        ref={ref}
        type={type}
        className={`
          w-full rounded-xl outline-none transition
          ${inputVariants[variant]}
          ${inputSizes[size]}
          ${className}
        `}
        {...props}
      />
    );
  }
);

Input.displayName = 'Input';

export default Input;
