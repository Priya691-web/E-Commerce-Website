import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_BASE || '/api/admin';

const api = axios.create({
  baseURL: API_BASE,
  withCredentials: true, // include JSESSIONID cookie cross-origin in dev (proxy rewrites it)
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
      if (!window.location.pathname.endsWith('/login')) {
        window.location.replace('/login');
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
};

export const DashboardApi = {
  fetch: () => api.get('/dashboard'),
};

export const OrdersApi = {
  list: (limit = 50) => api.get('/orders', { params: { limit } }),
};

export const ProductsApi = {
  list: () => api.get('/products'),
};

export const UsersApi = {
  list: () => api.get('/users'),
};

export default api;
