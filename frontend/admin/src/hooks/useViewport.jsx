/**
 * Viewport-based Conditional Rendering Hook
 * Provides viewport detection for conditional rendering
 * Prevents hydration mismatch by using client-side detection only
 */

import { useState, useEffect } from 'react';
import { breakpoints, deviceThresholds } from '../config/breakpoints.js';

/**
 * Hook to detect viewport width and device type
 * Returns viewport information for conditional rendering
 */
export function useViewport() {
  const [viewport, setViewport] = useState({
    width: typeof window !== 'undefined' ? window.innerWidth : 1280,
    height: typeof window !== 'undefined' ? window.innerHeight : 800,
    isMobile: typeof window !== 'undefined' ? window.innerWidth < deviceThresholds.mobile : false,
    isTablet: typeof window !== 'undefined' ? window.innerWidth >= deviceThresholds.mobile && window.innerWidth < deviceThresholds.desktop : false,
    isDesktop: typeof window !== 'undefined' ? window.innerWidth >= deviceThresholds.desktop : true,
    isXs: typeof window !== 'undefined' ? window.innerWidth < breakpoints.sm : false,
    isSm: typeof window !== 'undefined' ? window.innerWidth >= breakpoints.sm && window.innerWidth < breakpoints.md : false,
    isMd: typeof window !== 'undefined' ? window.innerWidth >= breakpoints.md && window.innerWidth < breakpoints.lg : false,
    isLg: typeof window !== 'undefined' ? window.innerWidth >= breakpoints.lg && window.innerWidth < breakpoints.xl : true,
    isXl: typeof window !== 'undefined' ? window.innerWidth >= breakpoints.xl && window.innerWidth < breakpoints['2xl'] : false,
    is2xl: typeof window !== 'undefined' ? window.innerWidth >= breakpoints['2xl'] : false,
  });

  useEffect(() => {
    // Skip on server to prevent hydration mismatch
    if (typeof window === 'undefined') return;

    const updateViewport = () => {
      const width = window.innerWidth;
      const height = window.innerHeight;

      setViewport({
        width,
        height,
        isMobile: width < deviceThresholds.mobile,
        isTablet: width >= deviceThresholds.mobile && width < deviceThresholds.desktop,
        isDesktop: width >= deviceThresholds.desktop,
        isXs: width < breakpoints.sm,
        isSm: width >= breakpoints.sm && width < breakpoints.md,
        isMd: width >= breakpoints.md && width < breakpoints.lg,
        isLg: width >= breakpoints.lg && width < breakpoints.xl,
        isXl: width >= breakpoints.xl && width < breakpoints['2xl'],
        is2xl: width >= breakpoints['2xl'],
      });
    };

    // Initial update
    updateViewport();

    // Add resize listener with debounce
    let resizeTimeout;
    const handleResize = () => {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(updateViewport, 100);
    };

    window.addEventListener('resize', handleResize);
    window.addEventListener('orientationchange', updateViewport);

    return () => {
      clearTimeout(resizeTimeout);
      window.removeEventListener('resize', handleResize);
      window.removeEventListener('orientationchange', updateViewport);
    };
  }, []);

  return viewport;
}

export default useViewport;
