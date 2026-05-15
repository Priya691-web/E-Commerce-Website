import { test, expect } from '../fixtures/auth.fixture';
import { LoginPage } from '../pages/LoginPage';
import { AdminDashboardPage } from '../pages/AdminDashboardPage';

test.describe('Authentication - Logout Flow', () => {
  test('should logout successfully', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    // Logout
    await dashboardPage.logout();
    
    // Should redirect to login page
    await authenticatedPage.waitForURL('/login');
    expect(authenticatedPage.url()).toContain('/login');
  });

  test('should clear session after logout', async ({ authenticatedPage, adminEmail, adminPassword }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    // Logout
    await dashboardPage.logout();
    
    // Try to access dashboard directly
    await authenticatedPage.goto('/admin/dashboard');
    
    // Should redirect to login (session cleared)
    await authenticatedPage.waitForURL('/login');
    expect(authenticatedPage.url()).toContain('/login');
  });

  test('should require re-login after logout', async ({ authenticatedPage, adminEmail, adminPassword }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    const loginPage = new LoginPage(authenticatedPage);
    
    // Logout
    await dashboardPage.logout();
    
    // Navigate to login
    await loginPage.navigate();
    
    // Login again
    await loginPage.login(adminEmail, adminPassword);
    
    // Should access dashboard successfully
    await authenticatedPage.waitForURL('/admin/dashboard');
    expect(authenticatedPage.url()).toContain('/admin/dashboard');
  });

  test('should show login page after session timeout', async ({ authenticatedPage }) => {
    const dashboardPage = new AdminDashboardPage(authenticatedPage);
    
    // Clear session (simulate session timeout)
    await authenticatedPage.evaluate(() => {
      document.cookie.split(";").forEach(c => {
        document.cookie = c.replace(/^ +/, "").replace(/=.*/, "=;expires=" + new Date().toUTCString() + ";path=/");
      });
    });
    
    // Try to navigate to dashboard
    await authenticatedPage.goto('/admin/dashboard');
    
    // Should redirect to login
    await authenticatedPage.waitForURL('/login');
    expect(authenticatedPage.url()).toContain('/login');
  });
});
