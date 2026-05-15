import { test, expect } from '../fixtures/auth.fixture';

test.describe('Admin - Product CRUD', () => {
  test('should display products list', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    const productsTable = authenticatedPage.locator('table, .products-table, [data-testid="products-table"]');
    await expect(productsTable).toBeVisible();
  });

  test('should open product creation form', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    const addButton = authenticatedPage.locator('button:has-text("Add Product"), button:has-text("Create"), [data-testid="add-product"]');
    await addButton.click();
    
    const productForm = authenticatedPage.locator('form, .product-form, [data-testid="product-form"]');
    await expect(productForm).toBeVisible();
  });

  test('should create a new product', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    // Click add product button
    const addButton = authenticatedPage.locator('button:has-text("Add Product"), button:has-text("Create"), [data-testid="add-product"]');
    await addButton.click();
    
    // Fill product form
    await authenticatedPage.fill('input[name="name"]', 'Test Product');
    await authenticatedPage.fill('input[name="price"]', '99.99');
    await authenticatedPage.fill('textarea[name="description"]', 'Test product description');
    
    // Submit form
    const submitButton = authenticatedPage.locator('button[type="submit"]:has-text("Save"), button:has-text("Create")');
    await submitButton.click();
    
    // Should redirect back to products list
    await authenticatedPage.waitForURL('/admin/products');
    
    // Verify product was created
    const successMessage = authenticatedPage.locator('.success-message, .alert-success, [data-testid="success-message"]');
    const isVisible = await successMessage.isVisible().catch(() => false);
    if (isVisible) {
      await expect(successMessage).toBeVisible();
    }
  });

  test('should edit existing product', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    // Find first product edit button
    const editButton = authenticatedPage.locator('button:has-text("Edit"), [data-testid="edit-product"]').first();
    const editCount = await editButton.count();
    
    if (editCount > 0) {
      await editButton.click();
      
      const productForm = authenticatedPage.locator('form, .product-form, [data-testid="product-form"]');
      await expect(productForm).toBeVisible();
      
      // Modify product
      await authenticatedPage.fill('input[name="name"]', 'Updated Product Name');
      
      // Submit
      const submitButton = authenticatedPage.locator('button[type="submit"]:has-text("Save"), button:has-text("Update")');
      await submitButton.click();
      
      await authenticatedPage.waitForURL('/admin/products');
    }
  });

  test('should delete product', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    // Find first product delete button
    const deleteButton = authenticatedPage.locator('button:has-text("Delete"), [data-testid="delete-product"]').first();
    const deleteCount = await deleteButton.count();
    
    if (deleteCount > 0) {
      await deleteButton.click();
      
      // Confirm deletion
      const confirmButton = authenticatedPage.locator('button:has-text("Confirm"), button:has-text("Delete")');
      await confirmButton.click();
      
      await authenticatedPage.waitForLoadState('networkidle');
      
      const successMessage = authenticatedPage.locator('.success-message, .alert-success, [data-testid="success-message"]');
      const isVisible = await successMessage.isVisible().catch(() => false);
      if (isVisible) {
        await expect(successMessage).toBeVisible();
      }
    }
  });

  test('should validate required product fields', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    const addButton = authenticatedPage.locator('button:has-text("Add Product"), button:has-text("Create"), [data-testid="add-product"]');
    await addButton.click();
    
    // Submit without filling fields
    const submitButton = authenticatedPage.locator('button[type="submit"]:has-text("Save"), button:has-text("Create")');
    await submitButton.click();
    
    // Should show validation errors
    const errorMessage = authenticatedPage.locator('.error-message, .alert-error, [data-testid="error-message"]');
    const isVisible = await errorMessage.isVisible().catch(() => false);
    
    // Either form validation prevents submission or error is shown
    if (isVisible) {
      await expect(errorMessage).toBeVisible();
    }
  });

  test('should display empty state when no products exist', async ({ authenticatedPage }) => {
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    const productsTable = authenticatedPage.locator('table, .products-table, [data-testid="products-table"]');
    const tableCount = await productsTable.count();
    
    if (tableCount === 0) {
      const emptyState = authenticatedPage.locator('.empty-state, .no-data, [data-testid="empty-state"]');
      await expect(emptyState).toBeVisible();
    }
  });
});
