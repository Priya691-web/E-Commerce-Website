/**
 * Protected Routes Configuration
 * Shared protected routes that require authentication
 * Prevents layout remounting by keeping layout outside Routes
 */

import { lazy } from 'react';

// Lazy load all pages for better initial bundle size
const Dashboard = lazy(() => import('../pages/dashboard/Dashboard.jsx'));
const Products = lazy(() => import('../pages/products/Products.jsx'));
const ProductForm = lazy(() => import('../pages/products/ProductForm.jsx'));
const Inventory = lazy(() => import('../pages/inventory/Inventory.jsx'));
const Orders = lazy(() => import('../pages/orders/Orders.jsx'));
const Users = lazy(() => import('../pages/users/Users.jsx'));
const Categories = lazy(() => import('../pages/categories/Categories.jsx'));
const Coupons = lazy(() => import('../pages/coupons/Coupons.jsx'));
const Settings = lazy(() => import('../pages/settings/Settings.jsx'));

export const protectedRoutes = [
  {
    path: 'dashboard',
    element: Dashboard,
  },
  {
    path: 'products',
    element: Products,
  },
  {
    path: 'products/new',
    element: ProductForm,
  },
  {
    path: 'products/:id/edit',
    element: ProductForm,
  },
  {
    path: 'inventory',
    element: Inventory,
  },
  {
    path: 'orders',
    element: Orders,
  },
  {
    path: 'users',
    element: Users,
  },
  {
    path: 'categories',
    element: Categories,
  },
  {
    path: 'coupons',
    element: Coupons,
  },
  {
    path: 'settings',
    element: Settings,
  },
];
