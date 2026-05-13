export default function StatusBadge({ status }) {
  const map = {
    pending: 'pill-pending',
    processing: 'pill-info',
    shipped: 'pill-shipped',
    delivered: 'pill-delivered',
    cancelled: 'pill-cancelled',
    completed: 'pill-completed',
  };
  return <span className={`pill ${map[status] || 'pill'}`}>{status}</span>;
}
