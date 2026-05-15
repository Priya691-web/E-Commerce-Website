/**
 * Centralized Breakpoint Configuration
 * Single source of truth for all responsive breakpoints
 * Used for viewport-based conditional rendering
 */

export const breakpoints = {
  // Mobile-first breakpoints
  xs: 320,    // Extra small mobile
  sm: 480,    // Small mobile
  md: 768,    // Tablet portrait
  lg: 1024,   // Tablet landscape / Small desktop
  xl: 1280,   // Desktop
  '2xl': 1440, // Large desktop
  '3xl': 1920, // Extra large desktop
};

// Breakpoint ranges for media queries
export const breakpointRanges = {
  mobile: [0, breakpoints.md - 1],
  tablet: [breakpoints.md, breakpoints.lg - 1],
  desktop: [breakpoints.lg, Infinity],
};

// Device type detection thresholds
export const deviceThresholds = {
  mobile: breakpoints.md,      // Below 768px is mobile
  tablet: breakpoints.lg,      // Below 1024px is tablet
  desktop: breakpoints.lg,    // 1024px and above is desktop
};

// Z-index layers to prevent conflicts
export const zIndexLayers = {
  base: 0,
  dropdown: 1000,
  sticky: 1020,
  fixed: 1030,
  modalBackdrop: 1040,
  modal: 1050,
  popover: 1060,
  tooltip: 1070,
  notification: 1080,
  mobileNavigation: 1090,
  desktopNavigation: 1100,
  overlay: 1200,
};

// Safe area spacing for mobile devices
export const safeArea = {
  top: 'env(safe-area-inset-top)',
  right: 'env(safe-area-inset-right)',
  bottom: 'env(safe-area-inset-bottom)',
  left: 'env(safe-area-inset-left)',
};

// Default layout dimensions
export const layoutDimensions = {
  desktopSidebar: 256,      // 16rem
  desktopTopbar: 64,        // 4rem
  mobileBottomNav: 64,      // 4rem
  mobileTopbar: 56,         // 3.5rem
  contentPadding: {
    mobile: 16,
    tablet: 24,
    desktop: 32,
  },
};

// Export as default for convenience
export default {
  breakpoints,
  breakpointRanges,
  deviceThresholds,
  zIndexLayers,
  safeArea,
  layoutDimensions,
};
