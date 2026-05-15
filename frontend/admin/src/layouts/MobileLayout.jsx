/**
 * Mobile Layout Component
 * Dedicated layout for mobile viewports (below 768px)
 * NEVER renders on desktop or tablet
 * Uses viewport-based conditional rendering to prevent hydration mismatch
 */

import { Outlet } from 'react-router-dom';
import MobileTopbar from './navigation/MobileTopbar.jsx';
import MobileBottomNav from './navigation/MobileBottomNav.jsx';
import { zIndexLayers, layoutDimensions, safeArea } from '../config/breakpoints.js';

export default function MobileLayout() {
  return (
    <div
      className="mobile-layout"
      style={{
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh',
        backgroundColor: 'var(--color-bg)',
        position: 'relative',
        zIndex: zIndexLayers.base,
        paddingTop: safeArea.top,
        paddingBottom: safeArea.bottom,
      }}
    >
      {/* Mobile Topbar - Fixed top */}
      <MobileTopbar />
      
      {/* Page Content */}
      <main
        style={{
          flex: 1,
          padding: `0 ${layoutDimensions.contentPadding.mobile}px`,
          overflow: 'auto',
          position: 'relative',
          zIndex: zIndexLayers.base,
          WebkitOverflowScrolling: 'touch', // Smooth scrolling on iOS
        }}
      >
        <Outlet />
      </main>
      
      {/* Mobile Bottom Navigation - Fixed bottom */}
      <MobileBottomNav />
    </div>
  );
}
