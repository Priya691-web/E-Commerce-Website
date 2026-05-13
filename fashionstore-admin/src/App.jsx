import { Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import AdminLayout from './components/AdminLayout.jsx';
import ProtectedRoute from './router/ProtectedRoute.jsx';

// Lazy load all pages for better initial bundle size
const Login = lazy(() => import('./pages/Login.jsx'));
const Register = lazy(() => import('./pages/Register.jsx'));
const Dashboard = lazy(() => import('./pages/dashboard/Dashboard.jsx'));
const Products = lazy(() => import('./pages/products/Products.jsx'));
const ProductForm = lazy(() => import('./pages/products/ProductForm.jsx'));
const Inventory = lazy(() => import('./pages/inventory/Inventory.jsx'));
const Orders = lazy(() => import('./pages/orders/Orders.jsx'));
const Users = lazy(() => import('./pages/users/Users.jsx'));
const Categories = lazy(() => import('./pages/categories/Categories.jsx'));
const Coupons = lazy(() => import('./pages/coupons/Coupons.jsx'));
const Settings = lazy(() => import('./pages/settings/Settings.jsx'));

function RouteFallback() {
  return (
    <div className="min-h-screen flex items-center justify-center bg-ink-50 dark:bg-ink-900">
      <div className="text-center">
        <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin mx-auto mb-4" />
        <p className="text-ink-600 dark:text-ink-300 text-sm">Loading...</p>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Suspense fallback={<RouteFallback />}>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

      <Route
        element={
          <ProtectedRoute>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/products" element={<Products />} />
        <Route path="/products/new" element={<ProductForm />} />
        <Route path="/products/:id/edit" element={<ProductForm />} />
        <Route path="/inventory" element={<Inventory />} />
        <Route path="/orders" element={<Orders />} />
        <Route path="/users" element={<Users />} />
        <Route path="/categories" element={<Categories />} />
        <Route path="/coupons" element={<Coupons />} />
        <Route path="/settings" element={<Settings />} />
      </Route>

      <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </Suspense>
  );
}
