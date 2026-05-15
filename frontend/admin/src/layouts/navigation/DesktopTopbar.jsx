/**
 * Desktop Topbar Component
 * Dedicated topbar for desktop viewports only
 * NEVER renders on mobile or tablet
 * Fixed top position with proper z-index
 */

import { Bell, Search, User, LogOut } from 'lucide-react';
import { zIndexLayers, layoutDimensions } from '../../config/breakpoints.js';

export default function DesktopTopbar() {
  return (
    <header
      className="desktop-topbar"
      style={{
        position: 'sticky',
        top: 0,
        height: layoutDimensions.desktopTopbar,
        backgroundColor: 'var(--color-bg)',
        borderBottom: '1px solid var(--color-border)',
        zIndex: zIndexLayers.desktopNavigation,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: '0 2rem',
      }}
    >
      {/* Search Bar */}
      <div style={{ flex: 1, maxWidth: '400px' }}>
        <div
          style={{
            position: 'relative',
            display: 'flex',
            alignItems: 'center',
          }}
        >
          <Search
            size={18}
            style={{
              position: 'absolute',
              left: '0.75rem',
              color: 'var(--color-text-secondary)',
            }}
          />
          <input
            type="text"
            placeholder="Search..."
            style={{
              width: '100%',
              padding: '0.5rem 0.75rem 0.5rem 2.5rem',
              border: '1px solid var(--color-border)',
              borderRadius: '0.5rem',
              fontSize: '0.875rem',
              backgroundColor: 'var(--color-bg)',
              color: 'var(--color-text-primary)',
            }}
          />
        </div>
      </div>

      {/* Right Actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        {/* Notifications */}
        <button
          style={{
            position: 'relative',
            padding: '0.5rem',
            border: 'none',
            backgroundColor: 'transparent',
            color: 'var(--color-text-secondary)',
            cursor: 'pointer',
            borderRadius: '0.5rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Bell size={20} />
          <span
            style={{
              position: 'absolute',
              top: '0.25rem',
              right: '0.25rem',
              width: '0.5rem',
              height: '0.5rem',
              backgroundColor: 'var(--color-error)',
              borderRadius: '50%',
            }}
          />
        </button>

        {/* User Menu */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <div
            style={{
              width: '2rem',
              height: '2rem',
              borderRadius: '50%',
              backgroundColor: 'var(--color-primary)',
              color: 'var(--color-primary-contrast)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontSize: '0.875rem',
              fontWeight: 600,
            }}
          >
            A
          </div>
          <div style={{ display: 'flex', flexDirection: 'column' }}>
            <span style={{ fontSize: '0.875rem', fontWeight: 500, color: 'var(--color-text-primary)' }}>
              Admin User
            </span>
            <span style={{ fontSize: '0.75rem', color: 'var(--color-text-secondary)' }}>
              admin@fashionstore.com
            </span>
          </div>
        </div>

        {/* Logout */}
        <button
          style={{
            padding: '0.5rem',
            border: 'none',
            backgroundColor: 'transparent',
            color: 'var(--color-text-secondary)',
            cursor: 'pointer',
            borderRadius: '0.5rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <LogOut size={20} />
        </button>
      </div>
    </header>
  );
}
