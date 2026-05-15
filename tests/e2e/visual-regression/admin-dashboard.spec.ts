import { test, expect } from '../fixtures/auth.fixture';

test.describe('Visual Regression - Admin Dashboard', () => {
  test('should match admin dashboard screenshot', async ({ authenticatedPage }) => {
    await authenticatedPage.setViewportSize({ width: 1440, height: 900 });
    await authenticatedPage.goto('/admin/dashboard');
    await authenticatedPage.waitForLoadState('networkidle');
    
    await expect(authenticatedPage).toHaveScreenshot('admin-dashboard-desktop.png', {
      maxDiffPixels: 100,
      threshold: 0.2
    });
  });

  test('should match admin products page screenshot', async ({ authenticatedPage }) => {
    await authenticatedPage.setViewportSize({ width: 1440, height: 900 });
    await authenticatedPage.goto('/admin/products');
    await authenticatedPage.waitForLoadState('networkidle');
    
    await expect(authenticatedPage).toHaveScreenshot('admin-products-desktop.png', {
      maxDiffPixels: 100,
      threshold: 0.2
    });
  });

  test('should match admin orders page screenshot', async ({ authenticatedPage }) => {
    await authenticatedPage.setViewportSize({ width: 1440, height: 900 });
    await authenticatedPage.goto('/admin/orders');
    await authenticatedPage.waitForLoadState('networkidle');
    
    await expect(authenticatedPage).toHaveScreenshot('admin-orders-desktop.png', {
      maxDiffPixels: 100,
      threshold: 0.2
    });
  });
});
