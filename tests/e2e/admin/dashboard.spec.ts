import { test, expect } from '../fixtures/auth.fixture';
import { AdminDashboardPage } from '../pages/AdminDashboardPage';

test.describe('Admin Dashboard', () => {
  test('should display dashboard with stat cards', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await expect(dashboardPage.sidebar).toBeVisible();
    await expect(dashboardPage.topbar).toBeVisible();
    
    const statCardCount = await dashboardPage.getStatCardCount();
    expect(statCardCount).toBeGreaterThan(0);
  });

  test('should display recent orders section', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await expect(dashboardPage.recentOrders).toBeVisible();
  });

  test('should navigate to products page', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await dashboardPage.navigateToProducts();
    expect(authenticatedPage.url()).toContain('/admin/products');
  });

  test('should navigate to orders page', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await dashboardPage.navigateToOrders();
    expect(authenticatedPage.url()).toContain('/admin/orders');
  });

  test('should navigate to users page', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await dashboardPage.navigateToUsers();
    expect(authenticatedPage.url()).toContain('/admin/users');
  });

  test('should navigate to categories page', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await dashboardPage.navigateToCategories();
    expect(authenticatedPage.url()).toContain('/admin/categories');
  });

  test('should display sidebar navigation', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await expect(dashboardPage.sidebar).toBeVisible();
    
    const navLinks = dashboardPage.sidebar.locator('a');
    const linkCount = await navLinks.count();
    expect(linkCount).toBeGreaterThan(3);
  });

  test('should display topbar with user info', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    await expect(dashboardPage.topbar).toBeVisible();
    
    const userInfo = dashboardPage.topbar.locator('.user-info, [data-testid="user-info"]');
    await expect(userInfo).toBeVisible();
  });

  test('should show loading state on initial load', async ({ page }) => {
    const loginPage = new (await import('../pages/LoginPage')).LoginPage(page);
    await loginPage.navigate();
    await loginPage.login(process.env.ADMIN_EMAIL || 'admin@fashionstore.com', process.env.ADMIN_PASSWORD || 'admin123');
    
    // Check for loading indicator
    const loadingIndicator = page.locator('.loading, .spinner, [data-testid="loading"]');
    const isVisible = await loadingIndicator.isVisible().catch(() => false);
    
    if (isVisible) {
      await loadingIndicator.waitFor({ state: 'hidden', timeout: 10000 });
    }
    
    await page.waitForURL('/admin/dashboard');
    await expect(page.locator('.stat-card')).toBeVisible();
  });
});
