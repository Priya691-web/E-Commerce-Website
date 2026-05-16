import { Menu, Bell, Search, LogOut, Sun, Moon } from 'lucide-react';
import { useAuth } from '../auth/AuthContext.jsx';
import { useTheme } from '../auth/ThemeContext.jsx';

export default function Header({ onMenuClick }) {
  const { logout } = useAuth();
  const { theme, toggle } = useTheme();

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
        <div className="hidden sm:flex items-center relative max-w-md w-full">
          <Search size={18} className="absolute left-3 text-ink-400" />
          <input
            type="text"
            placeholder="Search..."
            className="w-full pl-10 pr-4 py-2 bg-ink-50 dark:bg-ink-900 border border-ink-200 dark:border-ink-700 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-shadow"
          />
        </div>
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
        <button className="relative p-2 text-ink-500 hover:bg-ink-100 dark:hover:bg-ink-700 rounded-md transition-colors">
          <Bell size={20} />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-error rounded-full border-2 border-white dark:border-ink-800" />
        </button>

        <div className="h-6 w-px bg-ink-200 dark:bg-ink-700 mx-1 hidden sm:block"></div>

        {/* Logout */}
        <button
          onClick={logout}
          className="flex items-center gap-2 p-2 text-ink-600 dark:text-ink-300 hover:bg-error/10 hover:text-error dark:hover:bg-error/20 rounded-md transition-colors font-medium text-sm"
          title="Logout"
        >
          <LogOut size={20} />
          <span className="hidden sm:inline">Logout</span>
        </button>
      </div>
    </header>
  );
}
