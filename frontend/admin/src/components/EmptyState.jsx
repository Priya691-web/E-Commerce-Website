import { Package, Users, ShoppingCart, FileText, Search, Plus } from 'lucide-react';

const ICONS = {
  products: Package,
  users: Users,
  orders: ShoppingCart,
  default: FileText,
  search: Search,
};

export default function EmptyState({ 
  icon = 'default', 
  title = 'No data found', 
  description = 'There are no items to display at this time.',
  action,
  actionLabel = 'Add new item',
  onAction,
}) {
  const Icon = ICONS[icon] || ICONS.default;

  return (
    <div className="relative flex flex-col items-center justify-center py-16 px-6 text-center rounded-2xl border border-[var(--color-border)] bg-white shadow-sm hover-lift">
      <div className="absolute inset-0 pointer-events-none" style={{
        backgroundImage: 'radial-gradient(circle at 20% 15%, rgba(184, 149, 110, 0.08), transparent 32%), radial-gradient(circle at 80% 10%, rgba(74, 69, 62, 0.05), transparent 30%)'
      }} />
      <div className="relative">
        <div className="w-20 h-20 rounded-2xl bg-[var(--color-bg-secondary)] flex items-center justify-center mb-6">
          <Icon size={32} strokeWidth={1.5} className="text-[var(--color-text-muted)]" />
        </div>
        <div className="absolute -top-2 -right-2 w-6 h-6 bg-[var(--color-primary)]/20 rounded-full blur-sm" />
        <div className="absolute -bottom-3 -left-3 w-10 h-10 bg-[var(--color-bg-secondary)] rounded-full opacity-30" />
      </div>
      
      <h3 className="text-h3 mb-3">
        {title}
      </h3>
      
      <p className="text-body text-[var(--color-text-secondary)] container-sm mb-6 leading-relaxed max-w-md">
        {description}
      </p>
      
      {action || onAction ? (
        <button
          onClick={onAction || action}
          className="inline-flex items-center gap-2 px-5 py-2.5 bg-[var(--color-primary)] text-white hover:bg-[var(--color-primary-hover)] rounded-lg text-sm font-semibold transition-all duration-200 shadow-md hover:shadow-lg"
        >
          <Plus size={16} strokeWidth={2} />
          {actionLabel}
        </button>
      ) : null}
    </div>
  );
}
