import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Package,
  Boxes,
  ShoppingCart,
  Users,
  ShieldCheck,
  X,
} from 'lucide-react';

const NAV = [
  { to: '/dashboard', label: 'Dashboard', Icon: LayoutDashboard },
  { to: '/products',  label: 'Products',  Icon: Package },
  { to: '/inventory', label: 'Inventory', Icon: Boxes },
  { to: '/orders',    label: 'Orders',    Icon: ShoppingCart },
  { to: '/users',     label: 'Users',     Icon: Users },
];

export default function Sidebar({ mobileOpen, onClose }) {
  return (
    <aside
      aria-label="Primary"
      className={[
        'fixed inset-y-0 left-0 z-40 w-60 bg-white dark:bg-ink-800',
        'border-r border-ink-200 dark:border-ink-700',
        'flex flex-col px-3 py-5 transition-transform duration-200',
        mobileOpen ? 'translate-x-0' : '-translate-x-full',
        'lg:translate-x-0',
      ].join(' ')}
    >
      <div className="flex items-center gap-2 px-3 pb-5 mb-3 border-b border-ink-200 dark:border-ink-700">
        <ShieldCheck size={22} strokeWidth={2.2} className="text-ink-900 dark:text-white" />
        <span className="font-bold tracking-tight text-ink-900 dark:text-white">FashionStore</span>
        <span className="ml-auto text-[10px] font-bold uppercase tracking-wider px-1.5 py-0.5 rounded bg-ink-900 text-white dark:bg-white dark:text-ink-900">
          Admin
        </span>
        <button
          onClick={onClose}
          className="lg:hidden ml-2 text-ink-400 hover:text-ink-700 dark:hover:text-ink-100"
          aria-label="Close sidebar"
        >
          <X size={18} />
        </button>
      </div>

      <nav className="flex flex-col gap-0.5">
        {NAV.map(({ to, label, Icon }) => (
          <NavLink
            key={to}
            to={to}
            onClick={onClose}
            className={({ isActive }) =>
              [
                'flex items-center gap-3 px-3 py-2 rounded-lg text-sm font-medium transition',
                isActive
                  ? 'bg-ink-900 text-white dark:bg-white dark:text-ink-900'
                  : 'text-ink-600 hover:bg-ink-100 hover:text-ink-900 dark:text-ink-300 dark:hover:bg-ink-700 dark:hover:text-white',
              ].join(' ')
            }
          >
            <Icon size={18} strokeWidth={2} />
            <span>{label}</span>
          </NavLink>
        ))}
      </nav>

      <div className="mt-auto px-3 py-2 text-[11px] uppercase tracking-wider text-ink-400 dark:text-ink-500">
        v1.0.0
      </div>
    </aside>
  );
}
