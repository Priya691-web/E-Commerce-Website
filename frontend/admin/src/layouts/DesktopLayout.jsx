/**
 * Desktop Layout Component
 * Dedicated layout for desktop viewports (1024px and above)
 * NEVER renders on mobile or tablet
 * Uses viewport-based conditional rendering to prevent hydration mismatch
 */

import { Outlet } from 'react-router-dom';
import DesktopSidebar from './navigation/DesktopSidebar.jsx';
import DesktopTopbar from './navigation/DesktopTopbar.jsx';
import { zIndexLayers, layoutDimensions } from '../config/breakpoints.js';

export default function DesktopLayout() {
  return (
    <div 
      className="desktop-layout"
      style={{
        display: 'flex',
        minHeight: '100vh',
        backgroundColor: 'var(--color-bg)',
        position: 'relative',
        zIndex: zIndexLayers.base,
      }}
    >
      {/* Desktop Sidebar - Fixed left */}
      <DesktopSidebar />
      
      {/* Main Content Area */}
      <div
        style={{
          flex: 1,
          minWidth: 0,
          marginLeft: layoutDimensions.desktopSidebar,
          display: 'flex',
          flexDirection: 'column',
          position: 'relative',
          zIndex: zIndexLayers.base,
        }}
      >
        {/* Desktop Topbar - Fixed top */}
        <DesktopTopbar />
        
        {/* Page Content */}
        <main
          style={{
            flex: 1,
            padding: `0 ${layoutDimensions.contentPadding.desktop}px`,
            overflow: 'auto',
            position: 'relative',
            zIndex: zIndexLayers.base,
          }}
        >
          <Outlet />
        </main>
      </div>
    </div>
  );
}
