/**
 * Mobile Bottom Navigation Component
 * Dedicated bottom navigation for mobile viewports only
 * NEVER renders on desktop or tablet
 * Fixed bottom position with proper z-index and safe-area support
 */

import { NavLink } from 'react-router-dom';
import { Home, ShoppingBag, Users, Settings, LayoutGrid, Package } from 'lucide-react';
import { zIndexLayers, layoutDimensions, safeArea } from '../../config/breakpoints.js';

const navigationItems = [
  { name: 'Dashboard', href: '/admin/dashboard', icon: Home },
  { name: 'Products', href: '/admin/products', icon: Package },
  { name: 'Orders', href: '/admin/orders', icon: LayoutGrid },
  { name: 'Users', href: '/admin/users', icon: Users },
  { name: 'Settings', href: '/admin/settings', icon: Settings },
];

export default function MobileBottomNav() {
  return (
    <nav
      className="mobile-bottom-nav"
      style={{
        position: 'fixed',
        bottom: 0,
        left: 0,
        right: 0,
        height: layoutDimensions.mobileBottomNav,
        backgroundColor: 'var(--color-bg)',
        borderTop: '1px solid var(--color-border)',
        zIndex: zIndexLayers.mobileNavigation,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-around',
        paddingBottom: safeArea.bottom,
        paddingLeft: safeArea.left,
        paddingRight: safeArea.right,
      }}
    >
      {navigationItems.map((item) => {
        const Icon = item.icon;
        return (
          <NavLink
            key={item.name}
            to={item.href}
            end={item.href === '/admin/dashboard'}
            style={({ isActive }) => ({
              display: 'flex',
              flexDirection: 'column',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '0.25rem',
              padding: '0.5rem',
              textDecoration: 'none',
              color: isActive ? 'var(--color-primary)' : 'var(--color-text-secondary)',
              backgroundColor: 'transparent',
              transition: 'all 0.2s ease',
              minWidth: '3rem',
            })}
          >
            <Icon size={20} />
            <span style={{ fontSize: '0.625rem', fontWeight: 500 }}>{item.name}</span>
          </NavLink>
        );
      })}
    </nav>
  );
}
