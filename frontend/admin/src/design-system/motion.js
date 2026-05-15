/**
 * Motion System
 * Standardized animations and transitions for consistent motion design
 * Provides reusable animation utilities and motion tokens
 */

import tokens from './tokens.js';

// Motion presets for common UI interactions
export const motionPresets = {
  // Fade animations
  fadeIn: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
    exit: { opacity: 0 },
    transition: { duration: tokens.duration[200] },
  },
  
  fadeOut: {
    initial: { opacity: 1 },
    animate: { opacity: 0 },
    exit: { opacity: 0 },
    transition: { duration: tokens.duration[200] },
  },
  
  // Slide animations
  slideUp: {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: -20 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  slideDown: {
    initial: { opacity: 0, y: -20 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: 20 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  slideLeft: {
    initial: { opacity: 0, x: 20 },
    animate: { opacity: 1, x: 0 },
    exit: { opacity: 0, x: -20 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  slideRight: {
    initial: { opacity: 0, x: -20 },
    animate: { opacity: 1, x: 0 },
    exit: { opacity: 0, x: 20 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  // Scale animations
  scaleIn: {
    initial: { opacity: 0, scale: 0.9 },
    animate: { opacity: 1, scale: 1 },
    exit: { opacity: 0, scale: 0.9 },
    transition: { duration: tokens.duration[200], ease: tokens.easing.out },
  },
  
  scaleOut: {
    initial: { opacity: 1, scale: 1 },
    animate: { opacity: 0, scale: 0.9 },
    exit: { opacity: 0, scale: 0.9 },
    transition: { duration: tokens.duration[200], ease: tokens.easing.in },
  },
  
  // Bounce animations
  bounce: {
    initial: { scale: 1 },
    animate: { scale: [1, 1.05, 1] },
    transition: { duration: tokens.duration[300], ease: tokens.easing.bounce },
  },
  
  // Rotate animations
  spin: {
    animate: { rotate: 360 },
    transition: { duration: tokens.duration[1000], ease: tokens.easing.linear, repeat: Infinity },
  },
  
  pulse: {
    animate: { scale: [1, 1.05, 1] },
    transition: { duration: tokens.duration[2000], ease: tokens.easing['in-out'], repeat: Infinity },
  },
};

// Component-specific motion presets
export const componentMotion = {
  // Modal motion
  modal: {
    overlay: {
      initial: { opacity: 0 },
      animate: { opacity: 1 },
      exit: { opacity: 0 },
      transition: { duration: tokens.duration[200] },
    },
    content: {
      initial: { opacity: 0, scale: 0.95, y: 20 },
      animate: { opacity: 1, scale: 1, y: 0 },
      exit: { opacity: 0, scale: 0.95, y: 20 },
      transition: { duration: tokens.duration[200], ease: tokens.easing.out },
    },
  },
  
  // Dropdown motion
  dropdown: {
    initial: { opacity: 0, y: -10, scale: 0.95 },
    animate: { opacity: 1, y: 0, scale: 1 },
    exit: { opacity: 0, y: -10, scale: 0.95 },
    transition: { duration: tokens.duration[150], ease: tokens.easing.out },
  },
  
  // Tooltip motion
  tooltip: {
    initial: { opacity: 0, scale: 0.9 },
    animate: { opacity: 1, scale: 1 },
    exit: { opacity: 0, scale: 0.9 },
    transition: { duration: tokens.duration[150], ease: tokens.easing.out },
  },
  
  // Notification motion
  notification: {
    initial: { opacity: 0, x: 100, y: 0 },
    animate: { opacity: 1, x: 0, y: 0 },
    exit: { opacity: 0, x: 100, y: 0 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  // Sidebar motion
  sidebar: {
    initial: { x: '-100%' },
    animate: { x: 0 },
    exit: { x: '-100%' },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  // Drawer motion
  drawer: {
    initial: { x: '100%' },
    animate: { x: 0 },
    exit: { x: '100%' },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  // Tab motion
  tab: {
    initial: { opacity: 0, y: 10 },
    animate: { opacity: 1, y: 0 },
    exit: { opacity: 0, y: 10 },
    transition: { duration: tokens.duration[200], ease: tokens.easing.out },
  },
  
  // Card hover motion
  cardHover: {
    hover: {
      y: -4,
      transition: { duration: tokens.duration[200], ease: tokens.easing.out },
    },
  },
  
  // Button press motion
  buttonPress: {
    tap: { scale: 0.95 },
    transition: { duration: tokens.duration[100], ease: tokens.easing.out },
  },
};

// Staggered animations for lists
export const staggeredMotion = {
  container: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
    transition: { staggerChildren: tokens.duration[100] },
  },
  
  item: {
    initial: { opacity: 0, y: 20 },
    animate: { opacity: 1, y: 0 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
};

// Page transition animations
export const pageTransitions = {
  fade: {
    initial: { opacity: 0 },
    animate: { opacity: 1 },
    exit: { opacity: 0 },
    transition: { duration: tokens.duration[300] },
  },
  
  slide: {
    initial: { opacity: 0, x: 20 },
    animate: { opacity: 1, x: 0 },
    exit: { opacity: 0, x: -20 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
  
  scale: {
    initial: { opacity: 0, scale: 0.95 },
    animate: { opacity: 1, scale: 1 },
    exit: { opacity: 0, scale: 0.95 },
    transition: { duration: tokens.duration[300], ease: tokens.easing.out },
  },
};

// Reduced motion support
export const prefersReducedMotion = () => {
  return window.matchMedia('(prefers-reduced-motion: reduce)').matches;
};

// Get motion preset with reduced motion support
export const getMotionPreset = (preset) => {
  if (prefersReducedMotion()) {
    return {
      initial: { opacity: 0 },
      animate: { opacity: 1 },
      exit: { opacity: 0 },
      transition: { duration: 0 },
    };
  }
  return preset;
};

// CSS animation utilities
export const animations = {
  'spin-slow': `spin ${tokens.duration[1000]} linear infinite`,
  'spin-fast': `spin ${tokens.duration[500]} linear infinite`,
  'pulse-slow': `pulse ${tokens.duration[2000]} cubic-bezier(0.4, 0, 0.6, 1) infinite`,
  'bounce': `bounce ${tokens.duration[1000]} infinite`,
  'fade-in': `fadeIn ${tokens.duration[200]} ease-out`,
  'fade-out': `fadeOut ${tokens.duration[200]} ease-in`,
  'slide-up': `slideUp ${tokens.duration[300]} ease-out`,
  'slide-down': `slideDown ${tokens.duration[300]} ease-out`,
};

export default {
  motionPresets,
  componentMotion,
  staggeredMotion,
  pageTransitions,
  prefersReducedMotion,
  getMotionPreset,
  animations,
};
