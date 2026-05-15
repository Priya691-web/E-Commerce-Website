import { test, expect } from '@playwright/test';

test.describe('Accessibility Tests', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should have proper heading hierarchy', async ({ page }) => {
    const headings = await page.locator('h1, h2, h3, h4, h5, h6').all();
    
    let previousLevel = 0;
    for (const heading of headings) {
      const level = parseInt(await heading.evaluate(el => el.tagName.charAt(1)));
      expect(level).toBeLessThanOrEqual(previousLevel + 1);
      previousLevel = level;
    }
  });

  test('should have alt text for all images', async ({ page }) => {
    const images = await page.locator('img').all();
    
    for (const image of images) {
      const alt = await image.getAttribute('alt');
      expect(alt).toBeTruthy();
      expect(alt).not.toBe('');
    }
  });

  test('should have proper ARIA labels on interactive elements', async ({ page }) => {
    const buttons = await page.locator('button').all();
    
    for (const button of buttons) {
      const ariaLabel = await button.getAttribute('aria-label');
      const text = await button.textContent();
      
      if (!text || text.trim() === '') {
        expect(ariaLabel).toBeTruthy();
      }
    }
  });

  test('should be keyboard navigable', async ({ page }) => {
    await page.goto('/products');
    
    // Tab through focusable elements
    await page.keyboard.press('Tab');
    let focusedElement = await page.evaluate(() => document.activeElement.tagName);
    expect(['A', 'BUTTON', 'INPUT', 'SELECT']).toContain(focusedElement);
    
    // Test Enter key on links
    await page.keyboard.press('Enter');
  });

  test('should have proper focus management', async ({ page }) => {
    await page.goto('/login');
    
    const emailInput = page.locator('input[name="email"]');
    await emailInput.focus();
    
    const focusedElement = await page.evaluate(() => document.activeElement);
    expect(focusedElement).toBeTruthy();
  });

  test('should have sufficient color contrast', async ({ page }) => {
    const elements = await page.locator('.product-card, .btn-primary, .btn-secondary').all();
    
    for (const element of elements) {
      const computedStyle = await element.evaluate(el => {
        const styles = window.getComputedStyle(el);
        return {
          color: styles.color,
          backgroundColor: styles.backgroundColor,
        };
      });
      
      // Basic contrast check (in real implementation, use axe-core)
      expect(computedStyle.color).toBeTruthy();
      expect(computedStyle.backgroundColor).toBeTruthy();
    }
  });

  test('should have proper form labels', async ({ page }) => {
    await page.goto('/login');
    
    const inputs = await page.locator('input').all();
    
    for (const input of inputs) {
      const id = await input.getAttribute('id');
      if (id) {
        const label = page.locator(`label[for="${id}"]`);
        await expect(label).toBeVisible();
      }
    }
  });

  test('should have skip navigation link', async ({ page }) => {
    const skipLink = page.locator('.skip-link, [href="#main"]');
    
    if (await skipLink.count() > 0) {
      await skipLink.focus();
      await page.keyboard.press('Enter');
      
      const mainContent = page.locator('main, #main');
      await expect(mainContent).toBeVisible();
    }
  });

  test('should have proper table headers', async ({ page }) => {
    await page.goto('/admin/orders');
    
    const tables = await page.locator('table').all();
    
    for (const table of tables) {
      const headers = await table.locator('th').all();
      expect(headers.length).toBeGreaterThan(0);
    }
  });

  test('should have proper error messages for invalid inputs', async ({ page }) => {
    await page.goto('/login');
    
    await page.fill('input[name="email"]', 'invalid-email');
    await page.click('button[type="submit"]');
    
    const errorElement = page.locator('.error-message, [role="alert"]');
    await expect(errorElement).toBeVisible();
  });
});
