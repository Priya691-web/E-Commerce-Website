/**
 * DeviceRouter - Centralized Device Detection Authority
 * Single source of truth for device-specific rendering
 * Prevents hydration mismatch and ensures clean separation
 */

import { useState, useEffect } from 'react';
import DesktopApp from '../desktop/DesktopApp.jsx';
import MobileApp from '../mobile/MobileApp.jsx';
import { deviceThresholds } from '../config/breakpoints.js';

export default function DeviceRouter() {
  // Initialize with server-side safe default (desktop)
  const [isMobile, setIsMobile] = useState(false);
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    // Only run on client to prevent hydration mismatch
    setIsClient(true);

    const checkDevice = () => {
      const width = window.innerWidth;
      setIsMobile(width < deviceThresholds.mobile);
    };

    // Initial check
    checkDevice();

    // Debounced resize listener
    let resizeTimeout;
    const handleResize = () => {
      clearTimeout(resizeTimeout);
      resizeTimeout = setTimeout(checkDevice, 100);
    };

    window.addEventListener('resize', handleResize);
    return () => {
      clearTimeout(resizeTimeout);
      window.removeEventListener('resize', handleResize);
    };
  }, []);

  // During SSR/hydration, render desktop (safe default)
  // After client mount, render based on actual viewport
  if (!isClient) {
    return <DesktopApp />;
  }

  // HARD conditional rendering - only one app mounts
  return isMobile ? <MobileApp /> : <DesktopApp />;
}
