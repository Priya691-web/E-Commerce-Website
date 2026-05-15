import { test, expect } from '@playwright/test';

test.describe('Authentication Flow', () => {
  test('should register new user', async ({ page }) => {
    await page.goto('/register');
    
    await page.fill('input[name="fullName"]', 'Test User');
    await page.fill('input[name="email"]', 'newuser@test.com');
    await page.fill('input[name="password"]', 'SecurePass123!');
    await page.fill('input[name="confirmPassword"]', 'SecurePass123!');
    
    await page.click('button[type="submit"]');
    
    await expect(page).toHaveURL('/login');
    await expect(page.locator('.success-message')).toContainText('Registration successful');
  });

  test('should login with valid credentials', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');
    await page.click('button[type="submit"]');
    
    await expect(page).toHaveURL('/');
    await expect(page.locator('.user-avatar')).toBeVisible();
  });

  test('should show error with invalid credentials', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'invalid@test.com');
    await page.fill('input[name="password"]', 'wrongpass');
    await page.click('button[type="submit"]');
    
    await expect(page.locator('.error-message')).toBeVisible();
    await expect(page.locator('.error-message')).toContainText('Invalid credentials');
  });

  test('should validate password strength', async ({ page }) => {
    await page.goto('/register');
    
    await page.fill('input[name="password"]', 'weak');
    await page.fill('input[name="confirmPassword"]', 'weak');
    
    await expect(page.locator('.password-strength')).toContainText('Weak');
  });

  test('should require password confirmation to match', async ({ page }) => {
    await page.goto('/register');
    
    await page.fill('input[name="password"]', 'SecurePass123!');
    await page.fill('input[name="confirmPassword"]', 'DifferentPass123!');
    await page.click('button[type="submit"]');
    
    await expect(page.locator('.error-message')).toContainText('Passwords do not match');
  });

  test('should logout user', async ({ page }) => {
    // Login first
    await page.goto('/login');
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');
    await page.click('button[type="submit"]');
    
    // Logout
    await page.click('.user-menu-toggle');
    await page.click('.logout-button');
    
    await expect(page).toHaveURL('/login');
    await expect(page.locator('.user-avatar')).not.toBeVisible();
  });

  test('should request password reset', async ({ page }) => {
    await page.goto('/login');
    await page.click('.forgot-password-link');
    
    await page.fill('input[name="email"]', 'test@example.com');
    await page.click('.send-reset-link-btn');
    
    await expect(page.locator('.success-message')).toContainText('Reset link sent');
  });

  test('should remember user on login', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');
    await page.check('.remember-me-checkbox');
    await page.click('button[type="submit"]');
    
    // Close and reopen to check if remembered
    await page.context().clearCookies();
    await page.reload();
    
    await expect(page.locator('input[name="email"]')).toHaveValue('test@example.com');
  });
});
