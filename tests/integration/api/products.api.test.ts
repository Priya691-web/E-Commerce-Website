import { test, expect } from '@playwright/test';

test.describe('API Integration - Products', () => {
  const baseURL = 'http://localhost:8080';

  test.beforeEach(async ({ request }) => {
    // Login before each test
    await request.post(`${baseURL}/api/admin/login`, {
      data: {
        email: 'admin@fashionstore.com',
        password: 'admin123'
      }
    });
  });

  test('should get all products', async ({ request }) => {
    const response = await request.get(`${baseURL}/api/admin/products`);
    
    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(body).toHaveProperty('success', true);
    expect(body.data).toBeInstanceOf(Array);
  });

  test('should get product by ID', async ({ request }) => {
    // First get all products to find an ID
    const listResponse = await request.get(`${baseURL}/api/admin/products`);
    const listBody = await listResponse.json();
    
    if (listBody.data && listBody.data.length > 0) {
      const productId = listBody.data[0].id;
      const response = await request.get(`${baseURL}/api/admin/products/${productId}`);
      
      expect(response.status()).toBe(200);
      const body = await response.json();
      expect(body).toHaveProperty('success', true);
      expect(body.data).toHaveProperty('id', productId);
    }
  });

  test('should create new product', async ({ request }) => {
    const response = await request.post(`${baseURL}/api/admin/products`, {
      data: {
        name: 'Test Product',
        price: 99.99,
        description: 'Test product description',
        category: 'clothing',
        stock: 10
      }
    });
    
    expect(response.status()).toBe(200);
    const body = await response.json();
    expect(body).toHaveProperty('success', true);
  });

  test('should update existing product', async ({ request }) => {
    // First create a product
    const createResponse = await request.post(`${baseURL}/api/admin/products`, {
      data: {
        name: 'Test Product for Update',
        price: 99.99,
        description: 'Test product description',
        category: 'clothing',
        stock: 10
      }
    });
    const createBody = await createResponse.json();
    
    if (createBody.data && createBody.data.id) {
      const productId = createBody.data.id;
      
      // Update the product
      const updateResponse = await request.put(`${baseURL}/api/admin/products/${productId}`, {
        data: {
          name: 'Updated Product Name',
          price: 149.99
        }
      });
      
      expect(updateResponse.status()).toBe(200);
      const updateBody = await updateResponse.json();
      expect(updateBody).toHaveProperty('success', true);
    }
  });

  test('should delete product', async ({ request }) => {
    // First create a product
    const createResponse = await request.post(`${baseURL}/api/admin/products`, {
      data: {
        name: 'Test Product for Deletion',
        price: 99.99,
        description: 'Test product description',
        category: 'clothing',
        stock: 10
      }
    });
    const createBody = await createResponse.json();
    
    if (createBody.data && createBody.data.id) {
      const productId = createBody.data.id;
      
      // Delete the product
      const deleteResponse = await request.delete(`${baseURL}/api/admin/products/${productId}`);
      
      expect(deleteResponse.status()).toBe(200);
      const deleteBody = await deleteResponse.json();
      expect(deleteBody).toHaveProperty('success', true);
    }
  });

  test('should return 404 for non-existent product', async ({ request }) => {
    const response = await request.get(`${baseURL}/api/admin/products/999999`);
    
    expect(response.status()).toBe(404);
  });

  test('should validate required product fields', async ({ request }) => {
    const response = await request.post(`${baseURL}/api/admin/products`, {
      data: {
        // Missing required fields
        price: 99.99
      }
    });
    
    expect(response.status()).toBe(400);
    const body = await response.json();
    expect(body).toHaveProperty('success', false);
  });
});
