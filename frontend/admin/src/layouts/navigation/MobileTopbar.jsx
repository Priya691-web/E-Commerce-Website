/**
 * Mobile Topbar Component
 * Dedicated topbar for mobile viewports only
 * NEVER renders on desktop or tablet
 * Fixed top position with proper z-index and safe-area support
 */

import { Bell, Search, Menu } from 'lucide-react';
import { useState } from 'react';
import { zIndexLayers, layoutDimensions, safeArea } from '../../config/breakpoints.js';

export default function MobileTopbar() {
  const [searchOpen, setSearchOpen] = useState(false);

  return (
    <header
      className="mobile-topbar"
      style={{
        position: 'sticky',
        top: 0,
        height: layoutDimensions.mobileTopbar,
        backgroundColor: 'var(--color-bg)',
        borderBottom: '1px solid var(--color-border)',
        zIndex: zIndexLayers.mobileNavigation,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        padding: `0 ${layoutDimensions.contentPadding.mobile}px`,
        paddingLeft: `calc(${layoutDimensions.contentPadding.mobile}px + ${safeArea.left})`,
        paddingRight: `calc(${layoutDimensions.contentPadding.mobile}px + ${safeArea.right})`,
      }}
    >
      {/* Menu Button */}
      <button
        style={{
          padding: '0.5rem',
          border: 'none',
          backgroundColor: 'transparent',
          color: 'var(--color-text-primary)',
          cursor: 'pointer',
          borderRadius: '0.5rem',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Menu size={24} />
      </button>

      {/* Brand */}
      <div style={{ flex: 1, textAlign: 'center' }}>
        <h1 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--color-text-primary)' }}>
          FashionStore
        </h1>
      </div>

      {/* Right Actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
        {/* Search */}
        <button
          onClick={() => setSearchOpen(!searchOpen)}
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
          <Search size={20} />
        </button>

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
      </div>

      {/* Search Overlay */}
      {searchOpen && (
        <div
          style={{
            position: 'absolute',
            top: '100%',
            left: 0,
            right: 0,
            backgroundColor: 'var(--color-bg)',
            borderBottom: '1px solid var(--color-border)',
            padding: '1rem',
            zIndex: zIndexLayers.modal,
          }}
        >
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
              autoFocus
              style={{
                width: '100%',
                padding: '0.75rem 0.75rem 0.75rem 2.5rem',
                border: '1px solid var(--color-border)',
                borderRadius: '0.5rem',
                fontSize: '0.875rem',
                backgroundColor: 'var(--color-bg-secondary)',
                color: 'var(--color-text-primary)',
              }}
            />
          </div>
        </div>
      )}
    </header>
  );
}
