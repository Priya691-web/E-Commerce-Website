/**
 * Typography Primitives
 * Reusable typography components with standardized sizing
 */

import { forwardRef } from 'react';

const typographyVariants = {
  h1: 'text-4xl font-bold tracking-tight text-ink-900 dark:text-white',
  h2: 'text-3xl font-bold tracking-tight text-ink-900 dark:text-white',
  h3: 'text-2xl font-bold tracking-tight text-ink-900 dark:text-white',
  h4: 'text-xl font-semibold tracking-tight text-ink-900 dark:text-white',
  h5: 'text-lg font-semibold tracking-tight text-ink-900 dark:text-white',
  h6: 'text-base font-semibold tracking-tight text-ink-900 dark:text-white',
  body: 'text-base text-ink-700 dark:text-ink-200',
  bodyLarge: 'text-lg text-ink-700 dark:text-ink-200',
  bodySmall: 'text-sm text-ink-700 dark:text-ink-200',
  caption: 'text-xs text-ink-500 dark:text-ink-400',
  label: 'text-sm font-medium text-ink-900 dark:text-white',
  overline: 'text-xs font-semibold uppercase tracking-wider text-ink-500 dark:text-ink-400',
};

const H1 = forwardRef(({ className = '', children, ...props }, ref) => {
  return <h1 ref={ref} className={`${typographyVariants.h1} ${className}`} {...props}>{children}</h1>;
});

const H2 = forwardRef(({ className = '', children, ...props }, ref) => {
  return <h2 ref={ref} className={`${typographyVariants.h2} ${className}`} {...props}>{children}</h2>;
});

const H3 = forwardRef(({ className = '', children, ...props }, ref) => {
  return <h3 ref={ref} className={`${typographyVariants.h3} ${className}`} {...props}>{children}</h3>;
});

const H4 = forwardRef(({ className = '', children, ...props }, ref) => {
  return <h4 ref={ref} className={`${typographyVariants.h4} ${className}`} {...props}>{children}</h4>;
});

const H5 = forwardRef(({ className = '', children, ...props }, ref) => {
  return <h5 ref={ref} className={`${typographyVariants.h5} ${className}`} {...props}>{children}</h5>;
});

const H6 = forwardRef(({ className = '', children, ...props }, ref) => {
  return <h6 ref={ref} className={`${typographyVariants.h6} ${className}`} {...props}>{children}</h6>;
});

const Body = forwardRef(({ className = '', children, ...props }, ref) => {
  return <p ref={ref} className={`${typographyVariants.body} ${className}`} {...props}>{children}</p>;
});

const BodyLarge = forwardRef(({ className = '', children, ...props }, ref) => {
  return <p ref={ref} className={`${typographyVariants.bodyLarge} ${className}`} {...props}>{children}</p>;
});

const BodySmall = forwardRef(({ className = '', children, ...props }, ref) => {
  return <p ref={ref} className={`${typographyVariants.bodySmall} ${className}`} {...props}>{children}</p>;
});

const Caption = forwardRef(({ className = '', children, ...props }, ref) => {
  return <span ref={ref} className={`${typographyVariants.caption} ${className}`} {...props}>{children}</span>;
});

const Label = forwardRef(({ className = '', children, ...props }, ref) => {
  return <label ref={ref} className={`${typographyVariants.label} ${className}`} {...props}>{children}</label>;
});

const Overline = forwardRef(({ className = '', children, ...props }, ref) => {
  return <span ref={ref} className={`${typographyVariants.overline} ${className}`} {...props}>{children}</span>;
});

export { H1, H2, H3, H4, H5, H6, Body, BodyLarge, BodySmall, Caption, Label, Overline };
