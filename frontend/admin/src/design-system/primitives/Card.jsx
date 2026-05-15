/**
 * Card Primitive
 * Reusable card component with variants
 */

import { forwardRef } from 'react';

const cardVariants = {
  default: 'bg-white/90 backdrop-blur-xl border border-ink-200 shadow-card dark:bg-ink-800/90 dark:border-ink-700',
  elevated: 'bg-white border border-ink-200 shadow-lg dark:bg-ink-800 dark:border-ink-700',
  outlined: 'bg-white border-2 border-ink-200 dark:bg-ink-800 dark:border-ink-700',
  ghost: 'bg-transparent border-0 shadow-none',
};

const Card = forwardRef(({ variant = 'default', className = '', children, ...props }, ref) => {
  return (
    <div
      ref={ref}
      className={`
        rounded-2xl overflow-hidden
        ${cardVariants[variant]}
        ${className}
      `}
      {...props}
    >
      {children}
    </div>
  );
});

Card.displayName = 'Card';

const CardHeader = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <div
      ref={ref}
      className={`flex items-center justify-between px-5 py-4 border-b border-ink-200 dark:border-ink-700 ${className}`}
      {...props}
    >
      {children}
    </div>
  );
});

CardHeader.displayName = 'CardHeader';

const CardTitle = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <h2
      ref={ref}
      className={`text-sm font-semibold tracking-tight text-ink-900 dark:text-ink-100 ${className}`}
      {...props}
    >
      {children}
    </h2>
  );
});

CardTitle.displayName = 'CardTitle';

const CardBody = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <div ref={ref} className={`p-5 ${className}`} {...props}>
      {children}
    </div>
  );
});

CardBody.displayName = 'CardBody';

const CardFooter = forwardRef(({ className = '', children, ...props }, ref) => {
  return (
    <div
      ref={ref}
      className={`flex items-center justify-between px-5 py-4 border-t border-ink-200 dark:border-ink-700 ${className}`}
      {...props}
    >
      {children}
    </div>
  );
});

CardFooter.displayName = 'CardFooter';

Card.Header = CardHeader;
Card.Title = CardTitle;
Card.Body = CardBody;
Card.Footer = CardFooter;

export default Card;
