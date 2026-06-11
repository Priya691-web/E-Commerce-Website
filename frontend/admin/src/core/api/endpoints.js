/**
 * Centralized API Endpoints
 * Provides typed API methods for all admin operations
 * Uses the centralized adminApiClient with proper error handling and retry logic
 */

import adminApiClient from './client.js';
import { unwrapList, unwrapOne, unwrap } from './index.js';

// Auth Endpoints
export const AuthApi = {
  login: (email, password) => adminApiClient.post('/login', { email, password }),
  logout: () => adminApiClient.post('/logout'),
  me: () => adminApiClient.get('/me'),
  register: (data) => adminApiClient.post('/register', data),
};

// Dashboard Endpoints
export const DashboardApi = {
  fetch: () => adminApiClient.get('/dashboard').then(unwrap),
};

// Orders Endpoints
export const OrdersApi = {
  list: (limit = 50) => adminApiClient.get('/orders', { params: { limit } }).then(unwrapList('orders')),
  get: (id) => adminApiClient.get(`/orders/${id}`).then(unwrapOne('order')),
  updateStatus: (id, status) => adminApiClient.put(`/orders/${id}/status`, { status }).then(unwrap),
  approve: (id) => adminApiClient.put(`/orders/${id}/approve`).then(unwrap),
  cancel: (id) => adminApiClient.put(`/orders/${id}/cancel`).then(unwrap),
  ship: (id) => adminApiClient.put(`/orders/${id}/ship`).then(unwrap),
  deliver: (id) => adminApiClient.put(`/orders/${id}/deliver`).then(unwrap),
  refund: (id) => adminApiClient.put(`/orders/${id}/refund`).then(unwrap),
};

// Products Endpoints
export const ProductsApi = {
  list: () => adminApiClient.get('/products').then(unwrapList('products')),
  get: (id) => adminApiClient.get(`/products/${id}`).then(unwrapOne('product')),
  create: (data) => adminApiClient.post('/products', data).then(unwrap),
  update: (id, data) => adminApiClient.put(`/products/${id}`, data).then(unwrap),
  delete: (id) => adminApiClient.delete(`/products/${id}`).then(unwrap),
};

// Users Endpoints
export const UsersApi = {
  list: () => adminApiClient.get('/users').then(unwrapList('users')),
  get: (id) => adminApiClient.get(`/users/${id}`).then(unwrapOne('user')),
  update: (id, data) => adminApiClient.put(`/users/${id}`, data).then(unwrap),
  delete: (id) => adminApiClient.delete(`/users/${id}`).then(unwrap),
};

// Inventory Endpoints
export const InventoryApi = {
  list: () => adminApiClient.get('/inventory').then(unwrapList('products')),
  updateStock: (id, stock) => adminApiClient.put(`/inventory/${id}/stock`, { stock }).then(unwrap),
  lowStock: () => adminApiClient.get('/inventory/low-stock').then(unwrapList('products')),
};

// Categories Endpoints
export const CategoriesApi = {
  list: () => adminApiClient.get('/categories').then(unwrapList('categories')),
  create: (data) => adminApiClient.post('/categories', data).then(unwrap),
  update: (id, data) => adminApiClient.put(`/categories/${id}`, data).then(unwrap),
  delete: (id) => adminApiClient.delete(`/categories/${id}`).then(unwrap),
};

// Coupons Endpoints
export const CouponsApi = {
  list: () => adminApiClient.get('/coupons').then(unwrapList('coupons')),
  create: (data) => adminApiClient.post('/coupons', data).then(unwrap),
  update: (id, data) => adminApiClient.put(`/coupons/${id}`, data).then(unwrap),
  delete: (id) => adminApiClient.delete(`/coupons/${id}`).then(unwrap),
};

// Admin Endpoints
export const AdminApi = {
  stats: () => adminApiClient.get('/stats').then(unwrap),
  recentOrders: (limit = 10) => adminApiClient.get('/orders/recent', { params: { limit } }).then(unwrapList('orders')),
  recentUsers: (limit = 10) => adminApiClient.get('/users/recent', { params: { limit } }).then(unwrapList('users')),
  settings: {
    get: () => adminApiClient.get('/settings').then(unwrap),
    update: (data) => adminApiClient.put('/settings', data).then(unwrap),
  },
};

export default {
  Auth: AuthApi,
  Dashboard: DashboardApi,
  Orders: OrdersApi,
  Products: ProductsApi,
  Users: UsersApi,
  Inventory: InventoryApi,
  Categories: CategoriesApi,
  Coupons: CouponsApi,
  Admin: AdminApi,
};
