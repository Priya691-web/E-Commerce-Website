import { test as base } from '@playwright/test';

type AdminAuthFixtures = {
  authenticatedPage: any;
  adminEmail: string;
  adminPassword: string;
};

export const test = base.extend<AdminAuthFixtures>({
  adminEmail: process.env.ADMIN_EMAIL || 'admin@fashionstore.com',
  adminPassword: process.env.ADMIN_PASSWORD || 'admin123',

  authenticatedPage: async ({ page, adminEmail, adminPassword }, use) => {
    // Navigate to login page
    await page.goto('/login');
    
    // Fill in login form
    await page.fill('input[name="email"]', adminEmail);
    await page.fill('input[name="password"]', adminPassword);
    
    // Submit form
    await page.click('button[type="submit"]');
    
    // Wait for navigation to dashboard
    await page.waitForURL('/admin/dashboard');
    
    // Use authenticated page
    await use(page);
    
    // Cleanup: logout after test
    await page.goto('/logout');
  },
});

export const expect = test.expect;
