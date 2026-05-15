import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';

test.describe('Authentication - Registration Flow', () => {
  test('should display registration page', async ({ page }) => {
    await page.goto('/register');
    
    const nameInput = page.locator('input[name="name"]');
    const emailInput = page.locator('input[name="email"]');
    const passwordInput = page.locator('input[name="password"]');
    const confirmPasswordInput = page.locator('input[name="confirmPassword"]');
    const registerButton = page.locator('button[type="submit"]');
    
    await expect(nameInput).toBeVisible();
    await expect(emailInput).toBeVisible();
    await expect(passwordInput).toBeVisible();
    await expect(confirmPasswordInput).toBeVisible();
    await expect(registerButton).toBeVisible();
  });

  test('should register with valid data', async ({ page }) => {
    const timestamp = Date.now();
    const email = `test${timestamp}@example.com`;
    
    await page.goto('/register');
    
    await page.fill('input[name="name"]', `Test User ${timestamp}`);
    await page.fill('input[name="email"]', email);
    await page.fill('input[name="password"]', 'password123');
    await page.fill('input[name="confirmPassword"]', 'password123');
    
    await page.click('button[type="submit"]');
    
    // Should redirect to login or dashboard
    await page.waitForTimeout(2000);
    const currentUrl = page.url();
    expect(currentUrl).toMatch(/\/login|\/dashboard/);
  });

  test('should show error with mismatched passwords', async ({ page }) => {
    await page.goto('/register');
    
    await page.fill('input[name="name"]', 'Test User');
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');
    await page.fill('input[name="confirmPassword"]', 'different123');
    
    await page.click('button[type="submit"]');
    
    const errorMessage = page.locator('.error-message, .alert-error');
    await errorMessage.waitFor({ state: 'visible', timeout: 5000 });
    const text = await errorMessage.textContent();
    expect(text).toContain('password');
  });

  test('should show error with invalid email format', async ({ page }) => {
    await page.goto('/register');
    
    await page.fill('input[name="name"]', 'Test User');
    await page.fill('input[name="email"]', 'invalid-email');
    await page.fill('input[name="password"]', 'password123');
    await page.fill('input[name="confirmPassword"]', 'password123');
    
    await page.click('button[type="submit"]');
    
    // Browser validation should prevent submission
    const emailInput = page.locator('input[name="email"]');
    const isValid = await emailInput.evaluate(el => (el as HTMLInputElement).checkValidity());
    expect(isValid).toBe(false);
  });

  test('should navigate to login page from registration', async ({ page }) => {
    await page.goto('/register');
    
    const loginLink = page.locator('a[href="/login"]');
    await loginLink.click();
    
    await page.waitForURL('/login');
    expect(page.url()).toContain('/login');
  });
});
