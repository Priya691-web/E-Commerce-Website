/**
 * Semantic Tokens
 * Context-specific design tokens that map to functional purposes
 * These provide meaning to the design system beyond raw values
 */

import tokens from './tokens.js';

// Semantic color tokens for specific UI purposes
export const semanticColors = {
  // Background colors
  bg: {
    primary: tokens.colors.ink[50],
    secondary: tokens.colors.surface[100],
    tertiary: tokens.colors.ink[100],
    inverse: tokens.colors.ink[900],
  },
  
  // Text colors
  text: {
    primary: tokens.colors.ink[900],
    secondary: tokens.colors.ink[600],
    tertiary: tokens.colors.ink[400],
    inverse: tokens.colors.ink[50],
    disabled: tokens.colors.ink[300],
  },
  
  // Border colors
  border: {
    light: tokens.colors.ink[200],
    medium: tokens.colors.ink[300],
    dark: tokens.colors.ink[400],
  },
  
  // Interactive states
  interactive: {
    default: tokens.colors.accent.DEFAULT,
    hover: tokens.colors.accent[600],
    active: tokens.colors.accent[700],
    disabled: tokens.colors.ink[300],
  },
  
  // Status colors
  status: {
    success: tokens.colors.success[500],
    warning: tokens.colors.warning[500],
    error: tokens.colors.error[500],
    info: tokens.colors.info[500],
  },
  
  // Surface colors
  surface: {
    card: '#ffffff',
    elevated: '#ffffff',
    modal: '#ffffff',
    dropdown: '#ffffff',
  },
};

// Semantic spacing tokens for layout patterns
export const semanticSpacing = {
  // Component spacing
  component: {
    xs: tokens.spacing[2],
    sm: tokens.spacing[3],
    md: tokens.spacing[4],
    lg: tokens.spacing[6],
    xl: tokens.spacing[8],
  },
  
  // Section spacing
  section: {
    sm: tokens.spacing[8],
    md: tokens.spacing[12],
    lg: tokens.spacing[16],
    xl: tokens.spacing[20],
  },
  
  // Layout spacing
  layout: {
    page: tokens.spacing[8],
    container: tokens.spacing[6],
    grid: tokens.spacing[4],
  },
  
  // Form spacing
  form: {
    input: tokens.spacing[3],
    group: tokens.spacing[4],
    section: tokens.spacing[6],
  },
};

// Semantic typography tokens for content hierarchy
export const semanticTypography = {
  // Display typography
  display: {
    hero: tokens.typography.fontSize['5xl'],
    h1: tokens.typography.fontSize['4xl'],
    h2: tokens.typography.fontSize['3xl'],
    h3: tokens.typography.fontSize['2xl'],
  },
  
  // Body typography
  body: {
    large: tokens.typography.fontSize.lg,
    base: tokens.typography.fontSize.base,
    small: tokens.typography.fontSize.sm,
    xs: tokens.typography.fontSize.xs,
  },
  
  // UI typography
  ui: {
    button: tokens.typography.fontSize.base,
    label: tokens.typography.fontSize.sm,
    caption: tokens.typography.fontSize.xs,
    overline: tokens.typography.fontSize.xs,
  },
  
  // Font weights
  weight: {
    light: tokens.typography.fontWeight.light,
    regular: tokens.typography.fontWeight.normal,
    medium: tokens.typography.fontWeight.medium,
    semibold: tokens.typography.fontWeight.semibold,
    bold: tokens.typography.fontWeight.bold,
  },
};

// Semantic border radius tokens for component shapes
export const semanticBorderRadius = {
  // Component shapes
  button: tokens.borderRadius.md,
  input: tokens.borderRadius.md,
  card: tokens.borderRadius.lg,
  modal: tokens.borderRadius.xl,
  dropdown: tokens.borderRadius.md,
  tooltip: tokens.borderRadius.sm,
  badge: tokens.borderRadius.full,
  
  // Layout shapes
  container: tokens.borderRadius.lg,
  panel: tokens.borderRadius.md,
};

// Semantic shadow tokens for elevation
export const semanticShadows = {
  // Elevation levels
  flat: 'none',
  low: tokens.shadows.sm,
  medium: tokens.shadows.DEFAULT,
  high: tokens.shadows.md,
  higher: tokens.shadows.lg,
  highest: tokens.shadows.xl,
  
  // Component shadows
  card: tokens.shadows.card,
  dropdown: tokens.shadows.pop,
  modal: tokens.shadows['2xl'],
  tooltip: tokens.shadows.pop,
  floating: tokens.shadows.glow,
};

// Semantic z-index tokens for layering
export const semanticZIndex = {
  // Layer hierarchy
  base: tokens.zIndex.base,
  dropdown: tokens.zIndex.dropdown,
  sticky: tokens.zIndex.sticky,
  fixed: tokens.zIndex.fixed,
  modal: tokens.zIndex.modal,
  popover: tokens.zIndex.popover,
  tooltip: tokens.zIndex.tooltip,
  notification: tokens.zIndex.notification,
  overlay: tokens.zIndex.overlay,
};

// Responsive semantic tokens
export const responsiveTokens = {
  spacing: {
    mobile: {
      component: tokens.spacing[3],
      section: tokens.spacing[6],
      layout: tokens.spacing[4],
    },
    tablet: {
      component: tokens.spacing[4],
      section: tokens.spacing[8],
      layout: tokens.spacing[6],
    },
    desktop: {
      component: tokens.spacing[4],
      section: tokens.spacing[12],
      layout: tokens.spacing[8],
    },
  },
  
  typography: {
    mobile: {
      display: tokens.typography.fontSize['3xl'],
      h1: tokens.typography.fontSize['2xl'],
      h2: tokens.typography.fontSize.xl,
    },
    tablet: {
      display: tokens.typography.fontSize['4xl'],
      h1: tokens.typography.fontSize['3xl'],
      h2: tokens.typography.fontSize['2xl'],
    },
    desktop: {
      display: tokens.typography.fontSize['5xl'],
      h1: tokens.typography.fontSize['4xl'],
      h2: tokens.typography.fontSize['3xl'],
    },
  },
};

// Export all semantic tokens
export default {
  colors: semanticColors,
  spacing: semanticSpacing,
  typography: semanticTypography,
  borderRadius: semanticBorderRadius,
  shadows: semanticShadows,
  zIndex: semanticZIndex,
  responsive: responsiveTokens,
};
