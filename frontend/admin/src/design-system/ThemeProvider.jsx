/**
 * Theme Provider Component
 * Provides theme context to the entire application
 * Handles theme switching (light/dark mode) and token injection
 */

import React, { createContext, useContext, useState, useEffect, useMemo } from 'react';
import tokens from './tokens.js';

const ThemeContext = createContext({});

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

export function ThemeProvider({ children, defaultTheme = 'light' }) {
  const [theme, setTheme] = useState(() => {
    // Check for saved theme preference
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
      return savedTheme;
    }
    // Check system preference
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return defaultTheme;
  });

  useEffect(() => {
    // Save theme preference
    localStorage.setItem('theme', theme);
    
    // Apply theme to document
    document.documentElement.setAttribute('data-theme', theme);
    
    // Apply CSS variables for tokens
    applyThemeTokens(theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme(prev => prev === 'light' ? 'dark' : 'light');
  };

  const themeValue = useMemo(() => ({
    theme,
    setTheme,
    toggleTheme,
    tokens,
  }), [theme]);

  return (
    <ThemeContext.Provider value={themeValue}>
      {children}
    </ThemeContext.Provider>
  );
}

function applyThemeTokens(theme) {
  const root = document.documentElement;
  
  // Apply color tokens
  Object.entries(tokens.colors).forEach(([colorName, colorScale]) => {
    Object.entries(colorScale).forEach(([shade, value]) => {
      if (shade === 'DEFAULT') return;
      root.style.setProperty(`--color-${colorName}-${shade}`, value);
    });
  });

  // Apply spacing tokens
  Object.entries(tokens.spacing).forEach(([key, value]) => {
    root.style.setProperty(`--spacing-${key}`, value);
  });

  // Apply typography tokens
  Object.entries(tokens.typography.fontSize).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      root.style.setProperty(`--font-size-${key}`, value[0]);
      root.style.setProperty(`--line-height-${key}`, value[1].lineHeight);
    }
  });

  // Apply border radius tokens
  Object.entries(tokens.borderRadius).forEach(([key, value]) => {
    root.style.setProperty(`--radius-${key}`, value);
  });

  // Apply shadow tokens
  Object.entries(tokens.shadows).forEach(([key, value]) => {
    root.style.setProperty(`--shadow-${key}`, value);
  });

  // Apply duration tokens
  Object.entries(tokens.duration).forEach(([key, value]) => {
    root.style.setProperty(`--duration-${key}`, value);
  });

  // Apply easing tokens
  Object.entries(tokens.easing).forEach(([key, value]) => {
    root.style.setProperty(`--easing-${key}`, value);
  });

  // Apply z-index tokens
  Object.entries(tokens.zIndex).forEach(([key, value]) => {
    root.style.setProperty(`--z-${key}`, value);
  });

  // Apply theme-specific overrides
  if (theme === 'dark') {
    root.style.setProperty('--color-bg', tokens.colors.ink[900]);
    root.style.setProperty('--color-text', tokens.colors.ink[50]);
    root.style.setProperty('--color-border', tokens.colors.ink[700]);
  } else {
    root.style.setProperty('--color-bg', tokens.colors.ink[50]);
    root.style.setProperty('--color-text', tokens.colors.ink[900]);
    root.style.setProperty('--color-border', tokens.colors.ink[200]);
  }
}

export default ThemeProvider;
