import { test, expect } from '../fixtures/auth.fixture';
import { LoginPage } from '../pages/LoginPage';

test.describe('Authentication - Login Flow', () => {
  test('should display login page', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    
    await expect(loginPage.emailInput).toBeVisible();
    await expect(loginPage.passwordInput).toBeVisible();
    await expect(loginPage.loginButton).toBeVisible();
    await expect(loginPage.registerLink).toBeVisible();
  });

  test('should login with valid credentials', async ({ page, adminEmail, adminPassword }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    
    await loginPage.login(adminEmail, adminPassword);
    
    // Should redirect to dashboard
    await page.waitForURL('/admin/dashboard');
    expect(page.url()).toContain('/admin/dashboard');
  });

  test('should show error with invalid credentials', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    
    await loginPage.login('invalid@example.com', 'wrongpassword');
    
    await loginPage.waitForError();
    const errorMessage = await loginPage.getErrorMessage();
    expect(errorMessage).toBeTruthy();
  });

  test('should show error with empty email', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    
    await loginPage.login('', 'password123');
    
    // Browser validation should prevent submission
    const emailInput = loginPage.emailInput;
    const isRequired = await emailInput.getAttribute('required');
    expect(isRequired).toBeTruthy();
  });

  test('should show error with empty password', async ({ page, adminEmail }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    
    await loginPage.login(adminEmail, '');
    
    // Browser validation should prevent submission
    const passwordInput = loginPage.passwordInput;
    const isRequired = await passwordInput.getAttribute('required');
    expect(isRequired).toBeTruthy();
  });

  test('should navigate to registration page', async ({ page }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    
    await loginPage.registerLink.click();
    await page.waitForURL('/register');
    expect(page.url()).toContain('/register');
  });

  test('should maintain session after page refresh', async ({ page, adminEmail, adminPassword }) => {
    const loginPage = new LoginPage(page);
    await loginPage.navigate();
    await loginPage.login(adminEmail, adminPassword);
    await page.waitForURL('/admin/dashboard');
    
    // Refresh page
    await page.reload();
    await page.waitForLoadState('networkidle');
    
    // Should still be on dashboard (session maintained)
    expect(page.url()).toContain('/admin/dashboard');
  });
});
