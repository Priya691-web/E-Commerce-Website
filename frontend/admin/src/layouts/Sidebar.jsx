import { NavLink } from 'react-router-dom';
import { LayoutGrid, ShoppingBag, Users, Tag, Settings, BarChart3, Package, X } from 'lucide-react';

const navigationItems = [
  { name: 'Dashboard', href: '/dashboard', icon: BarChart3 },
  { name: 'Products', href: '/products', icon: Package },
  { name: 'Inventory', href: '/inventory', icon: ShoppingBag },
  { name: 'Orders', href: '/orders', icon: LayoutGrid },
  { name: 'Users', href: '/users', icon: Users },
  { name: 'Categories', href: '/categories', icon: Tag },
  { name: 'Coupons', href: '/coupons', icon: ShoppingBag },
  { name: 'Settings', href: '/settings', icon: Settings },
];

export default function Sidebar({ isOpen, setIsOpen }) {
  return (
    <aside
      className={`
        fixed lg:static inset-y-0 left-0 z-50 w-64
        bg-white dark:bg-ink-800 border-r border-ink-200 dark:border-ink-700
        transform transition-transform duration-300 ease-in-out
        ${isOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
        flex flex-col
      `}
    >
      {/* Mobile close button & Logo */}
      <div className="flex items-center justify-between p-4 border-b border-ink-200 dark:border-ink-700 h-16 shrink-0">
        <div className="flex flex-col">
          <h1 className="text-xl font-bold text-ink-900 dark:text-ink-50">
            FashionStore
          </h1>
          <span className="text-xs text-ink-500 dark:text-ink-400 font-medium tracking-wider uppercase">
            Admin Panel
          </span>
        </div>
        <button
          onClick={() => setIsOpen(false)}
          className="lg:hidden p-2 text-ink-500 hover:bg-ink-100 dark:hover:bg-ink-700 rounded-md transition-colors"
        >
          <X size={20} />
        </button>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
        {navigationItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.name}
              to={item.href}
              end={item.href === '/dashboard'}
              onClick={() => setIsOpen(false)}
              className={({ isActive }) => `
                flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-200
                ${isActive 
                  ? 'bg-primary/10 text-primary dark:text-primary-light font-semibold' 
                  : 'text-ink-600 dark:text-ink-300 hover:bg-ink-50 dark:hover:bg-ink-700/50 hover:text-ink-900 dark:hover:text-ink-100'
                }
              `}
            >
              <Icon size={20} className="shrink-0" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>
      
      {/* User Area - Optional bottom pinning */}
      <div className="p-4 border-t border-ink-200 dark:border-ink-700 shrink-0">
        <div className="flex items-center gap-3 px-3 py-2 rounded-lg bg-ink-50 dark:bg-ink-700/30">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-white font-bold text-sm shrink-0 shadow-sm">
            A
          </div>
          <div className="flex flex-col min-w-0">
            <span className="text-sm font-medium text-ink-900 dark:text-ink-50 truncate">
              Admin User
            </span>
            <span className="text-xs text-ink-500 dark:text-ink-400 truncate">
              admin@fashionstore.com
            </span>
          </div>
        </div>
      </div>
    </aside>
  );
}
