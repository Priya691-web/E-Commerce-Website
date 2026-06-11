import { useState } from 'react';
import { Menu, Bell, Search, LogOut, Sun, Moon, X } from 'lucide-react';
import { useAuth } from '../auth/AuthContext.jsx';
import { useTheme } from '../auth/ThemeContext.jsx';
import { useNavigate } from 'react-router-dom';

export default function Header({ onMenuClick }) {
  const { logout, user } = useAuth();
  const { theme, toggle } = useTheme();
  const navigate = useNavigate();
  const [showNotifications, setShowNotifications] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      // Navigate to products page with search query
      navigate('/admin/products', { state: { search: searchQuery } });
      setSearchQuery('');
    }
  };

  const handleNotificationClick = () => {
    setShowNotifications(!showNotifications);
  };

  return (
    <header className="bg-white dark:bg-ink-800 border-b border-ink-200 dark:border-ink-700 h-16 shrink-0 flex items-center justify-between px-4 sticky top-0 z-30 shadow-sm">
      <div className="flex items-center gap-3">
        {/* Mobile menu toggle */}
        <button
          onClick={onMenuClick}
          className="lg:hidden p-2 text-ink-500 hover:bg-ink-100 dark:hover:bg-ink-700 rounded-md transition-colors"
          aria-label="Open menu"
        >
          <Menu size={24} />
        </button>

        {/* Search */}
        <form onSubmit={handleSearch} className="hidden sm:flex items-center relative max-w-md w-full">
          <Search size={18} className="absolute left-3 text-ink-400" />
          <input
            type="text"
            placeholder="Search products, orders, users..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-10 pr-4 py-2 bg-ink-50 dark:bg-ink-900 border border-ink-200 dark:border-ink-700 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-shadow"
            aria-label="Search"
          />
        </form>
      </div>

      <div className="flex items-center gap-2 sm:gap-4">
        {/* Theme Toggle */}
        <button
          onClick={toggle}
          className="p-2 text-ink-500 hover:bg-ink-100 dark:hover:bg-ink-700 rounded-md transition-colors"
          aria-label="Toggle theme"
        >
          {theme === 'dark' ? <Sun size={20} /> : <Moon size={20} />}
        </button>

        {/* Notifications */}
        <div className="relative">
          <button
            onClick={handleNotificationClick}
            className="relative p-2 text-ink-500 hover:bg-ink-100 dark:hover:bg-ink-700 rounded-md transition-colors"
            aria-label="Notifications"
            aria-expanded={showNotifications}
          >
            <Bell size={20} />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-error rounded-full border-2 border-white dark:border-ink-800" />
          </button>

          {/* Notifications Dropdown */}
          {showNotifications && (
            <>
              <div
                className="fixed inset-0 z-40"
                onClick={() => setShowNotifications(false)}
              />
              <div className="absolute right-0 top-full mt-2 w-80 bg-white dark:bg-ink-800 rounded-lg shadow-xl border border-ink-200 dark:border-ink-700 z-50 overflow-hidden">
                <div className="p-4 border-b border-ink-200 dark:border-ink-700 flex items-center justify-between">
                  <h3 className="font-semibold text-sm">Notifications</h3>
                  <button
                    onClick={() => setShowNotifications(false)}
                    className="p-1 hover:bg-ink-100 dark:hover:bg-ink-700 rounded"
                    aria-label="Close notifications"
                  >
                    <X size={16} />
                  </button>
                </div>
                <div className="p-4 text-center text-sm text-ink-500 dark:text-ink-400">
                  No new notifications
                </div>
              </div>
            </>
          )}
        </div>

        <div className="h-6 w-px bg-ink-200 dark:bg-ink-700 mx-1 hidden sm:block"></div>

        {/* User Info */}
        <div className="hidden sm:flex items-center gap-2">
          <div className="w-8 h-8 rounded-full bg-primary flex items-center justify-center text-white font-bold text-sm">
            {(user?.fullName || user?.email || 'A').split(' ').map((p) => p[0]).join('').slice(0, 2).toUpperCase()}
          </div>
          <div className="text-sm">
            <p className="font-medium text-ink-900 dark:text-ink-50">{user?.fullName || 'Admin'}</p>
          </div>
        </div>

        {/* Logout */}
        <button
          onClick={logout}
          className="flex items-center gap-2 p-2 text-ink-600 dark:text-ink-300 hover:bg-error/10 hover:text-error dark:hover:bg-error/20 rounded-md transition-colors font-medium text-sm"
          title="Logout"
          aria-label="Logout"
        >
          <LogOut size={20} />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
}
