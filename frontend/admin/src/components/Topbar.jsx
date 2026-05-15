import { LogOut, Search, Bell, Sun, Moon, Menu } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';
import { useTheme } from '../auth/ThemeContext.jsx';

export default function Topbar({ onMenuClick }) {
  const { user, logout } = useAuth();
  const { theme, toggle } = useTheme();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  const initials = (user?.fullName || user?.email || '?')
    .split(' ')
    .map((p) => p[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();

  return (
    <header className="sticky top-0 z-20 h-16 bg-white/90 backdrop-blur-xl border-b border-[var(--color-border)] shadow-sm flex items-center gap-4 px-4 sm:px-6">
      <button
        onClick={onMenuClick}
        className="lg:hidden p-2 -ml-2 rounded-lg text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-secondary)] transition-colors"
        aria-label="Open menu"
      >
        <Menu size={20} />
      </button>

      <div className="hidden sm:flex items-center gap-2 px-4 h-10 w-full container-md rounded-full border border-[var(--color-border)] bg-white shadow-sm">
        <Search size={16} className="text-[var(--color-text-muted)]" />
        <input
          type="search"
          placeholder="Search products, orders, users…"
          aria-label="Search"
          className="flex-1 bg-transparent text-sm text-[var(--color-text-primary)] placeholder:text-[var(--color-text-muted)] focus:outline-none"
        />
      </div>

      <div className="ml-auto flex items-center gap-3">
        <button
          onClick={toggle}
          className="w-9 h-9 rounded-full border border-[var(--color-border)] bg-white text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-secondary)] flex items-center justify-center transition shadow-sm"
          aria-label={theme === 'dark' ? 'Switch to light mode' : 'Switch to dark mode'}
          title={theme === 'dark' ? 'Light mode' : 'Dark mode'}
        >
          {theme === 'dark' ? <Sun size={16} /> : <Moon size={16} />}
        </button>

        <button
          className="relative w-9 h-9 rounded-full border border-[var(--color-border)] bg-white text-[var(--color-text-secondary)] hover:bg-[var(--color-bg-secondary)] flex items-center justify-center transition shadow-sm"
          aria-label="Notifications"
        >
          <Bell size={16} />
          <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-[var(--color-error)] ring-2 ring-white" />
        </button>

        <div className="hidden md:flex items-center gap-2 pr-3 border-r border-[var(--color-border)]">
          <div className="w-8 h-8 rounded-full bg-[var(--color-primary)] text-white flex items-center justify-center text-xs font-bold shadow-sm">
            {initials}
          </div>
          <div className="flex flex-col leading-tight">
            <span className="text-sm font-semibold text-[var(--color-text-primary)]">{user?.fullName || 'Admin'}</span>
            <span className="text-[10px] uppercase tracking-wider text-[var(--color-text-muted)]">{user?.role || 'admin'}</span>
          </div>
        </div>

        <button onClick={handleLogout} className="btn btn-ghost">
          <LogOut size={14} />
          <span className="hidden sm:inline">Sign out</span>
        </button>
      </div>
    </header>
  );
}
