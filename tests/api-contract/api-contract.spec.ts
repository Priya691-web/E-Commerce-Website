import { test, expect } from '@playwright/test';

const API_BASE_URL = 'http://localhost:8080/api';

test.describe('API Contract Tests', () => {
  test('GET /api/products should return correct response structure', async ({ request }) => {
    const response = await request.get(`${API_BASE_URL}/products`);
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(Array.isArray(data)).toBe(true);
    if (data.length > 0) {
      expect(data[0]).toHaveProperty('id');
      expect(data[0]).toHaveProperty('name');
      expect(data[0]).toHaveProperty('price');
      expect(data[0]).toHaveProperty('category');
    }
  });

  test('GET /api/products/:id should return product details', async ({ request }) => {
    const response = await request.get(`${API_BASE_URL}/products/1`);
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('id', 1);
    expect(data).toHaveProperty('name');
    expect(data).toHaveProperty('price');
    expect(data).toHaveProperty('description');
    expect(data).toHaveProperty('images');
  });

  test('POST /api/cart should add item to cart', async ({ request }) => {
    const response = await request.post(`${API_BASE_URL}/cart`, {
      data: {
        productId: 1,
        quantity: 2,
      },
    });
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('success', true);
    expect(data).toHaveProperty('cartItems');
  });

  test('GET /api/cart should return cart items', async ({ request }) => {
    const response = await request.get(`${API_BASE_URL}/cart`);
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('items');
    expect(data).toHaveProperty('total');
    expect(data).toHaveProperty('subtotal');
  });

  test('POST /api/auth/login should return auth token', async ({ request }) => {
    const response = await request.post(`${API_BASE_URL}/auth/login`, {
      data: {
        email: 'test@example.com',
        password: 'password123',
      },
    });
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('token');
    expect(data).toHaveProperty('user');
  });

  test('POST /api/auth/register should create user', async ({ request }) => {
    const response = await request.post(`${API_BASE_URL}/auth/register`, {
      data: {
        fullName: 'Test User',
        email: 'newuser@example.com',
        password: 'SecurePass123!',
      },
    });
    
    expect(response.status()).toBe(201);
    const data = await response.json();
    
    expect(data).toHaveProperty('success', true);
    expect(data).toHaveProperty('userId');
  });

  test('GET /api/categories should return categories', async ({ request }) => {
    const response = await request.get(`${API_BASE_URL}/categories`);
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(Array.isArray(data)).toBe(true);
    if (data.length > 0) {
      expect(data[0]).toHaveProperty('id');
      expect(data[0]).toHaveProperty('name');
      expect(data[0]).toHaveProperty('slug');
    }
  });

  test('POST /api/wishlist should add item to wishlist', async ({ request }) => {
    const response = await request.post(`${API_BASE_URL}/wishlist`, {
      data: {
        productId: 1,
      },
      headers: {
        Authorization: 'Bearer test-token',
      },
    });
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('success', true);
  });

  test('GET /api/orders should return user orders', async ({ request }) => {
    const response = await request.get(`${API_BASE_URL}/orders`, {
      headers: {
        Authorization: 'Bearer test-token',
      },
    });
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('orders');
    expect(Array.isArray(data.orders)).toBe(true);
  });

  test('POST /api/checkout should create order', async ({ request }) => {
    const response = await request.post(`${API_BASE_URL}/checkout`, {
      data: {
        shippingAddress: {
          fullName: 'John Doe',
          address: '123 Main St',
          city: 'New York',
          zipCode: '10001',
          country: 'USA',
        },
        paymentMethod: 'stripe',
      },
      headers: {
        Authorization: 'Bearer test-token',
      },
    });
    
    expect(response.status()).toBe(200);
    const data = await response.json();
    
    expect(data).toHaveProperty('orderId');
    expect(data).toHaveProperty('success', true);
  });
});
