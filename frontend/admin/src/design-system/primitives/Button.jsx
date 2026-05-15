/**
 * Button Primitive
 * Reusable button component with variants and sizes
 * Follows accessibility standards
 */

import { forwardRef } from 'react';

const buttonVariants = {
  primary: 'bg-ink-900 text-white hover:bg-black shadow-glow dark:bg-white dark:text-ink-900 dark:hover:bg-ink-100',
  secondary: 'bg-ink-100 text-ink-900 hover:bg-ink-200 dark:bg-ink-800 dark:text-ink-100 dark:hover:bg-ink-700',
  ghost: 'bg-transparent text-ink-700 border border-ink-200 hover:bg-ink-50 dark:text-ink-200 dark:border-ink-700 dark:hover:bg-ink-800',
  accent: 'bg-accent text-ink-900 hover:bg-accent-dark hover:text-white shadow-card',
  danger: 'bg-error text-white hover:bg-error-600 shadow-md',
  success: 'bg-success text-white hover:bg-success-600 shadow-md',
};

const buttonSizes = {
  sm: 'h-8 px-3 text-xs',
  md: 'h-9 px-4 text-sm',
  lg: 'h-10 px-5 text-base',
  xl: 'h-12 px-6 text-lg',
};

const Button = forwardRef(
  (
    {
      variant = 'primary',
      size = 'md',
      children,
      disabled = false,
      className = '',
      type = 'button',
      ...props
    },
    ref
  ) => {
    return (
      <button
        ref={ref}
        type={type}
        disabled={disabled}
        className={`
          inline-flex items-center justify-center gap-2
          font-semibold rounded-lg
          transition-all duration-200
          active:translate-y-px
          focus:outline-none focus:ring-2 focus:ring-accent focus:ring-offset-2 focus:ring-offset-white dark:focus:ring-offset-ink-900
          disabled:opacity-60 disabled:cursor-not-allowed disabled:active:translate-y-0
          ${buttonVariants[variant]}
          ${buttonSizes[size]}
          ${className}
        `}
        {...props}
      >
        {children}
      </button>
    );
  }
);

Button.displayName = 'Button';

export default Button;
