import { describe, it, expect, vi, beforeEach } from 'vitest';
import axios from 'axios';
import { ProductsApi, OrdersApi, AuthApi, CategoriesApi } from '../client.js';

const api = axios.create();

describe('API Integration', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('AuthApi.login uses admin login endpoint', async () => {
    api.post.mockResolvedValue({ data: { success: true } });
    await AuthApi.login('admin@example.com', 'password123');
    expect(api.post).toHaveBeenCalledWith('/login', { email: 'admin@example.com', password: 'password123' });
  });

  it('ProductsApi.list unwraps products payload', async () => {
    api.get.mockResolvedValue({ data: { products: [{ id: 1 }, { id: 2 }] } });
    await expect(ProductsApi.list()).resolves.toHaveLength(2);
    expect(api.get).toHaveBeenCalledWith('/products');
  });

  it('ProductsApi CRUD routes are correct', async () => {
    api.get.mockResolvedValue({ data: { product: { id: 1 } } });
    api.post.mockResolvedValue({ data: { success: true } });
    api.put.mockResolvedValue({ data: { success: true } });
    api.delete.mockResolvedValue({ data: { success: true } });

    await expect(ProductsApi.get(1)).resolves.toMatchObject({ id: 1 });
    await ProductsApi.create({ name: 'Tee' });
    await ProductsApi.update(1, { name: 'Tee 2' });
    await ProductsApi.delete(1);

    expect(api.get).toHaveBeenCalledWith('/products/1');
    expect(api.post).toHaveBeenCalledWith('/products', { name: 'Tee' });
    expect(api.put).toHaveBeenCalledWith('/products/1', { name: 'Tee 2' });
    expect(api.delete).toHaveBeenCalledWith('/products/1');
  });

  it('OrdersApi routes and verbs are correct', async () => {
    api.get.mockResolvedValue({ data: { orders: [{ id: 1 }] } });
    api.put.mockResolvedValue({ data: { success: true } });

    await OrdersApi.list(50);
    await OrdersApi.approve(1);
    await OrdersApi.cancel(1);
    await OrdersApi.ship(1);
    await OrdersApi.deliver(1);
    await OrdersApi.refund(1);

    expect(api.get).toHaveBeenCalledWith('/orders', { params: { limit: 50 } });
    expect(api.put).toHaveBeenCalledWith('/orders/1/approve');
    expect(api.put).toHaveBeenCalledWith('/orders/1/cancel');
    expect(api.put).toHaveBeenCalledWith('/orders/1/ship');
    expect(api.put).toHaveBeenCalledWith('/orders/1/deliver');
    expect(api.put).toHaveBeenCalledWith('/orders/1/refund');
  });

  it('CategoriesApi.list unwraps categories payload', async () => {
    api.get.mockResolvedValue({ data: { categories: [{ id: 1 }] } });
    await expect(CategoriesApi.list()).resolves.toHaveLength(1);
    expect(api.get).toHaveBeenCalledWith('/categories');
  });

  it('propagates network failures', async () => {
    api.get.mockRejectedValue(new Error('Network Error'));
    await expect(ProductsApi.list()).rejects.toThrow('Network Error');
  });
});
