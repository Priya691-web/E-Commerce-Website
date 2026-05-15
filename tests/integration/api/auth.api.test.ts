import { test, expect } from '@playwright/test';

test.describe('API Integration - Authentication', () => {
  const baseURL = 'http://localhost:8080';

  test('should login with valid credentials', async ({ request }) => {
    const response = await request.post(`${baseURL}/api/admin/login`, {
      data: {
        email: 'admin@fashionstore.com',
        password: 'admin123'
      }
    });

    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(body).toHaveProperty('success', true);
  });

  test('should fail login with invalid credentials', async ({ request }) => {
    const response = await request.post(`${baseURL}/api/admin/login`, {
      data: {
        email: 'invalid@example.com',
        password: 'wrongpassword'
      }
    });

    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(body).toHaveProperty('success', false);
  });

  test('should logout successfully', async ({ request }) => {
    const response = await request.post(`${baseURL}/api/admin/logout`);
    
    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(body).toHaveProperty('success', true);
  });

  test('should get current user when authenticated', async ({ request }) => {
    // First login
    await request.post(`${baseURL}/api/admin/login`, {
      data: {
        email: 'admin@fashionstore.com',
        password: 'admin123'
      }
    });

    // Then get current user
    const response = await request.get(`${baseURL}/api/admin/me`);
    
    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(body).toHaveProperty('success', true);
  });

  test('should return error for unauthenticated request', async ({ request }) => {
    const response = await request.get(`${baseURL}/api/admin/me`);
    
    expect(response.status()).toBe(401);
  });
});
