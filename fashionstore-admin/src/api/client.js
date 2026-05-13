import axios from 'axios';

// In production/Docker, the nginx proxy handles /api routing,
// so we use a relative base URL. In dev (Vite), the proxy forwards to localhost.
// VITE_API_BASE env var can override for custom setups.
const API_BASE = import.meta.env.VITE_API_BASE || '/api/admin';

const api = axios.create({
  baseURL: API_BASE,
  withCredentials: true, // include JSESSIONID cookie
  headers: {
    Accept: 'application/json',
    'Content-Type': 'application/json',
  },
  timeout: 15000,
});

// Response interceptor: bounce to /login on 401, surface useful error payloads
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      // Avoid bounce loops on the login screen itself
      const pathname = window.location.pathname;
      if (!pathname.endsWith('/login') && !pathname.startsWith('/login?')) {
        // Dispatch event for AuthProvider to handle router-aware navigation
        window.dispatchEvent(new CustomEvent('auth:logout'));
      }
    }
    return Promise.reject(err);
  }
);

// Domain-specific helpers
export const AuthApi = {
  login: (email, password) => api.post('/login', { email, password }),
  logout: () => api.post('/logout'),
  me: () => api.get('/me'),
  register: (data) => api.post('/register', data),
};

const unwrapList = (key) => (res) => res.data?.[key] ?? res.data ?? [];
const unwrapOne = (key) => (res) => res.data?.[key] ?? res.data ?? null;
const unwrap = (res) => res.data;

export const DashboardApi = {
  fetch: () => api.get('/dashboard'),
};

export const OrdersApi = {
  list: (limit = 50) => api.get('/orders', { params: { limit } }).then(unwrapList('orders')),
  get: (id) => api.get(`/orders/${id}`).then(unwrapOne('order')),
  updateStatus: (id, status) => api.put(`/orders/${id}/status`, { status }).then(unwrap),
  approve: (id) => api.put(`/orders/${id}/approve`).then(unwrap),
  cancel: (id) => api.put(`/orders/${id}/cancel`).then(unwrap),
  ship: (id) => api.put(`/orders/${id}/ship`).then(unwrap),
  deliver: (id) => api.put(`/orders/${id}/deliver`).then(unwrap),
  refund: (id) => api.put(`/orders/${id}/refund`).then(unwrap),
};

export const ProductsApi = {
  list: () => api.get('/products').then(unwrapList('products')),
  get: (id) => api.get(`/products/${id}`).then(unwrapOne('product')),
  create: (data) => api.post('/products', data).then(unwrap),
  update: (id, data) => api.put(`/products/${id}`, data).then(unwrap),
  delete: (id) => api.delete(`/products/${id}`).then(unwrap),
};

export const UsersApi = {
  list: () => api.get('/users').then(unwrapList('users')),
  get: (id) => api.get(`/users/${id}`).then(unwrapOne('user')),
  update: (id, data) => api.put(`/users/${id}`, data).then(unwrap),
  delete: (id) => api.delete(`/users/${id}`).then(unwrap),
};

export const InventoryApi = {
  list: () => api.get('/inventory').then(unwrapList('products')),
  updateStock: (id, stock) => api.put(`/inventory/${id}/stock`, { stock }).then(unwrap),
  lowStock: () => api.get('/inventory/low-stock').then(unwrapList('products')),
};

export const CategoriesApi = {
  list: () => api.get('/categories').then(unwrapList('categories')),
  create: (data) => api.post('/categories', data).then(unwrap),
  update: (id, data) => api.put(`/categories/${id}`, data).then(unwrap),
  delete: (id) => api.delete(`/categories/${id}`).then(unwrap),
};

export const CouponsApi = {
  list: () => api.get('/coupons').then(unwrapList('coupons')),
  create: (data) => api.post('/coupons', data).then(unwrap),
  update: (id, data) => api.put(`/coupons/${id}`, data).then(unwrap),
  delete: (id) => api.delete(`/coupons/${id}`).then(unwrap),
};

export const AdminApi = {
  stats: () => api.get('/stats'),
  recentOrders: (limit = 10) => api.get('/orders/recent', { params: { limit } }).then(unwrapList('orders')),
  recentUsers: (limit = 10) => api.get('/users/recent', { params: { limit } }).then(unwrapList('users')),
};

export default api;
