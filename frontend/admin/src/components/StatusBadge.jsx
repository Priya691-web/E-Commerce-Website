export default function StatusBadge({ status }) {
  const key = (status || '').toLowerCase();
  const map = {
    pending: 'badge badge-warning',
    processing: 'badge badge-info',
    packing: 'badge badge-info',
    shipped: 'badge badge-primary',
    delivered: 'badge badge-success',
    completed: 'badge badge-success',
    cancelled: 'badge badge-error',
    refunded: 'badge badge-error',
    failed: 'badge badge-error',
  };
  const cls = map[key] || 'badge badge-primary';
  const label = key ? key.replace('_', ' ') : 'status';
  return <span className={cls}>{label}</span>;
}
