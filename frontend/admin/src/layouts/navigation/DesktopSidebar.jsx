/**
 * Desktop Sidebar Component
 * Dedicated sidebar for desktop viewports only
 * NEVER renders on mobile or tablet
 * Fixed left position with proper z-index
 */

import { NavLink } from 'react-router-dom';
import { LayoutGrid, ShoppingBag, Users, Tag, Settings, BarChart3, Package } from 'lucide-react';
import { zIndexLayers, layoutDimensions } from '../../config/breakpoints.js';

const navigationItems = [
  { name: 'Dashboard', href: '/admin/dashboard', icon: BarChart3 },
  { name: 'Products', href: '/admin/products', icon: Package },
  { name: 'Inventory', href: '/admin/inventory', icon: ShoppingBag },
  { name: 'Orders', href: '/admin/orders', icon: LayoutGrid },
  { name: 'Users', href: '/admin/users', icon: Users },
  { name: 'Categories', href: '/admin/categories', icon: Tag },
  { name: 'Coupons', href: '/admin/coupons', icon: ShoppingBag },
  { name: 'Settings', href: '/admin/settings', icon: Settings },
];

export default function DesktopSidebar() {
  return (
    <aside
      className="desktop-sidebar"
      style={{
        position: 'fixed',
        left: 0,
        top: 0,
        width: layoutDimensions.desktopSidebar,
        height: '100vh',
        backgroundColor: 'var(--color-bg-secondary)',
        borderRight: '1px solid var(--color-border)',
        zIndex: zIndexLayers.desktopNavigation,
        overflowY: 'auto',
        overflowX: 'hidden',
      }}
    >
      {/* Logo/Brand */}
      <div
        style={{
          padding: '1.5rem',
          borderBottom: '1px solid var(--color-border)',
        }}
      >
        <h1 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--color-text-primary)' }}>
          FashionStore
        </h1>
        <p style={{ fontSize: '0.875rem', color: 'var(--color-text-secondary)' }}>
          Admin Dashboard
        </p>
      </div>

      {/* Navigation Links */}
      <nav style={{ padding: '1rem' }}>
        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
          {navigationItems.map((item) => {
            const Icon = item.icon;
            return (
              <li key={item.name} style={{ marginBottom: '0.5rem' }}>
                <NavLink
                  to={item.href}
                  end={item.href === '/admin/dashboard'}
                  style={({ isActive }) => ({
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.75rem',
                    padding: '0.75rem 1rem',
                    borderRadius: '0.5rem',
                    textDecoration: 'none',
                    color: isActive ? 'var(--color-primary)' : 'var(--color-text-secondary)',
                    backgroundColor: isActive ? 'var(--color-primary-light)' : 'transparent',
                    transition: 'all 0.2s ease',
                  })}
                >
                  <Icon size={20} />
                  <span style={{ fontSize: '0.875rem', fontWeight: 500 }}>{item.name}</span>
                </NavLink>
              </li>
            );
          })}
        </ul>
      </nav>
    </aside>
  );
}
