/**
 * Centralized Design Tokens
 * Single source of truth for all design system values
 * Used across components and Tailwind configuration
 */

// Spacing Scale - 8pt grid system
export const spacing = {
  0: '0',
  px: '1px',
  0.5: '2px',
  1: '4px',
  1.5: '6px',
  2: '8px',
  2.5: '10px',
  3: '12px',
  3.5: '14px',
  4: '16px',
  5: '20px',
  6: '24px',
  7: '28px',
  8: '32px',
  9: '36px',
  10: '40px',
  11: '44px',
  12: '48px',
  14: '56px',
  16: '64px',
  20: '80px',
  24: '96px',
  28: '112px',
  32: '128px',
  36: '144px',
  40: '160px',
  44: '176px',
  48: '192px',
  52: '208px',
  56: '224px',
  60: '240px',
  64: '256px',
  72: '288px',
  80: '320px',
  96: '384px',
};

// Typography Scale - Modular scale
export const typography = {
  fontFamily: {
    sans: ['Inter', 'ui-sans-serif', 'system-ui', '-apple-system', 'Segoe UI', 'Roboto', 'sans-serif'],
    mono: ['ui-monospace', 'SFMono-Regular', 'Menlo', 'Monaco', 'Consolas', 'monospace'],
  },
  fontSize: {
    xs: ['0.75rem', { lineHeight: '1rem' }], // 12px
    sm: ['0.875rem', { lineHeight: '1.25rem' }], // 14px
    base: ['1rem', { lineHeight: '1.5rem' }], // 16px
    lg: ['1.125rem', { lineHeight: '1.75rem' }], // 18px
    xl: ['1.25rem', { lineHeight: '1.75rem' }], // 20px
    '2xl': ['1.5rem', { lineHeight: '2rem' }], // 24px
    '3xl': ['1.875rem', { lineHeight: '2.25rem' }], // 30px
    '4xl': ['2.25rem', { lineHeight: '2.5rem' }], // 36px
    '5xl': ['3rem', { lineHeight: '1' }], // 48px
  },
  fontWeight: {
    light: 300,
    normal: 400,
    medium: 500,
    semibold: 600,
    bold: 700,
    extrabold: 800,
  },
  letterSpacing: {
    tighter: '-0.05em',
    tight: '-0.025em',
    normal: '0',
    wide: '0.025em',
    wider: '0.05em',
    widest: '0.1em',
  },
};

// Color Palette - Luxury neutrals + gold accent
export const colors = {
  // Neutral/Ink scale
  ink: {
    50: '#f7f7f5',
    100: '#eceae6',
    200: '#dedbd4',
    300: '#c7c1b6',
    400: '#9f988a',
    500: '#6f685c',
    600: '#504a3f',
    700: '#353027',
    800: '#211d17',
    900: '#14110d',
    950: '#0a0908',
  },
  // Accent/Gold scale
  accent: {
    50: '#fdf6e8',
    100: '#f9e7c7',
    200: '#f4d5a5',
    300: '#f0c383',
    400: '#ecb161',
    DEFAULT: '#c5a46d', // Primary gold
    500: '#c5a46d',
    600: '#a8854f',
    700: '#8b6631',
    800: '#6e4713',
    900: '#512805',
  },
  // Surface scale
  surface: {
    50: '#fdfcf9',
    100: '#f6f2e9',
    200: '#eee7d7',
    300: '#e6d6c5',
    400: '#dec5b3',
    500: '#d6b4a1',
    600: '#cea38f',
    700: '#c6927d',
    800: '#be816b',
    900: '#b67059',
  },
  // Semantic colors
  success: {
    50: '#d1fae5',
    100: '#a7f3d0',
    200: '#6ee7b7',
    300: '#34d399',
    400: '#10b981',
    500: '#059669',
    600: '#047857',
    700: '#065f46',
    800: '#064e3b',
    900: '#022c22',
  },
  warning: {
    50: '#fef3c7',
    100: '#fde68a',
    200: '#fcd34d',
    300: '#fbbf24',
    400: '#f59e0b',
    500: '#d97706',
    600: '#b45309',
    700: '#92400e',
    800: '#78350f',
    900: '#451a03',
  },
  error: {
    50: '#fee2e2',
    100: '#fecaca',
    200: '#fca5a5',
    300: '#f87171',
    400: '#ef4444',
    500: '#dc2626',
    600: '#b91c1c',
    700: '#991b1b',
    800: '#7f1d1d',
    900: '#450a0a',
  },
  info: {
    50: '#dbeafe',
    100: '#bfdbfe',
    200: '#93c5fd',
    300: '#60a5fa',
    400: '#3b82f6',
    500: '#2563eb',
    600: '#1d4ed8',
    700: '#1e40af',
    800: '#1e3a8a',
    900: '#1e3a8a',
  },
};

// Border Radius Scale
export const borderRadius = {
  none: '0',
  sm: '4px',
  DEFAULT: '8px',
  md: '8px',
  lg: '12px',
  xl: '16px',
  '2xl': '20px',
  '3xl': '24px',
  full: '9999px',
};

// Shadow System
export const shadows = {
  xs: '0 1px 2px 0 rgba(20, 17, 13, 0.05)',
  sm: '0 1px 3px 0 rgba(20, 17, 13, 0.1), 0 1px 2px 0 rgba(20, 17, 13, 0.06)',
  DEFAULT: '0 4px 6px -1px rgba(20, 17, 13, 0.1), 0 2px 4px -1px rgba(20, 17, 13, 0.06)',
  md: '0 10px 15px -3px rgba(20, 17, 13, 0.1), 0 4px 6px -2px rgba(20, 17, 13, 0.05)',
  lg: '0 20px 25px -5px rgba(20, 17, 13, 0.1), 0 10px 10px -5px rgba(20, 17, 13, 0.04)',
  xl: '0 25px 50px -12px rgba(20, 17, 13, 0.25)',
  '2xl': '0 35px 60px -15px rgba(20, 17, 13, 0.3)',
  // Custom shadows
  card: '0 6px 20px rgba(20, 17, 13, 0.08), 0 1px 3px rgba(20, 17, 13, 0.08)',
  pop: '0 14px 42px rgba(20, 17, 13, 0.12)',
  glow: '0 0 0 1px rgba(197, 164, 109, 0.24), 0 18px 50px rgba(0, 0, 0, 0.18)',
  inner: 'inset 0 2px 4px 0 rgba(20, 17, 13, 0.06)',
};

// Container Widths
export const container = {
  sm: '640px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1440px',
  '3xl': '1920px',
};

// Breakpoints
export const breakpoints = {
  xs: '320px',
  sm: '480px',
  md: '768px',
  lg: '1024px',
  xl: '1280px',
  '2xl': '1440px',
  '3xl': '1920px',
};

// Animation durations
export const duration = {
  75: '75ms',
  100: '100ms',
  150: '150ms',
  200: '200ms',
  300: '300ms',
  500: '500ms',
  700: '700ms',
  1000: '1000ms',
};

// Animation easings
export const easing = {
  linear: 'linear',
  in: 'cubic-bezier(0.4, 0, 1, 1)',
  out: 'cubic-bezier(0, 0, 0.2, 1)',
  'in-out': 'cubic-bezier(0.4, 0, 0.2, 1)',
  bounce: 'cubic-bezier(0.68, -0.55, 0.265, 1.55)',
};

// Z-index layers
export const zIndex = {
  base: 0,
  dropdown: 1000,
  sticky: 1020,
  fixed: 1030,
  'modal-backdrop': 1040,
  modal: 1050,
  popover: 1060,
  tooltip: 1070,
  notification: 1080,
  'mobile-navigation': 1090,
  'desktop-navigation': 1100,
  overlay: 1200,
};

// Export as default
export default {
  spacing,
  typography,
  colors,
  borderRadius,
  shadows,
  container,
  breakpoints,
  duration,
  easing,
  zIndex,
};
