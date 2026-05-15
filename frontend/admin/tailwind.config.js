/** @type {import('tailwindcss').Config} */
import designTokens from './src/design-system/tokens.js';

export default {
  content: ['./index.html', './src/**/*.{js,jsx,ts,tsx}'],
  darkMode: 'class',
  theme: {
    extend: {
      fontFamily: designTokens.typography.fontFamily,
      fontSize: designTokens.typography.fontSize,
      fontWeight: designTokens.typography.fontWeight,
      letterSpacing: designTokens.typography.letterSpacing,
      colors: {
        ink: designTokens.colors.ink,
        accent: designTokens.colors.accent,
        surface: designTokens.colors.surface,
        success: designTokens.colors.success,
        warning: designTokens.colors.warning,
        error: designTokens.colors.error,
        info: designTokens.colors.info,
      },
      boxShadow: designTokens.shadows,
      borderRadius: designTokens.borderRadius,
      spacing: designTokens.spacing,
      screens: designTokens.breakpoints,
      duration: designTokens.duration,
      easing: designTokens.easing,
      zIndex: designTokens.zIndex,
      maxWidth: designTokens.container,
    },
  },
  plugins: [],
};
