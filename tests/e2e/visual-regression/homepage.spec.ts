import { test, expect } from '@playwright/test';

test.describe('Visual Regression - Homepage', () => {
  test('should match desktop homepage screenshot', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Wait for images to load
    await page.waitForFunction(() => {
      const images = Array.from(document.querySelectorAll('img'));
      return images.every(img => img.complete);
    });
    
    await expect(page).toHaveScreenshot('homepage-desktop.png', {
      maxDiffPixels: 100,
      threshold: 0.2
    });
  });

  test('should match tablet homepage screenshot', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    await page.waitForFunction(() => {
      const images = Array.from(document.querySelectorAll('img'));
      return images.every(img => img.complete);
    });
    
    await expect(page).toHaveScreenshot('homepage-tablet.png', {
      maxDiffPixels: 100,
      threshold: 0.2
    });
  });

  test('should match mobile homepage screenshot', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    await page.waitForFunction(() => {
      const images = Array.from(document.querySelectorAll('img'));
      return images.every(img => img.complete);
    });
    
    await expect(page).toHaveScreenshot('homepage-mobile.png', {
      maxDiffPixels: 100,
      threshold: 0.2
    });
  });

  test('should match hero section screenshot', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const heroSection = page.locator('.hero, .hero-section, [data-testid="hero"]');
    await expect(heroSection).toBeVisible();
    
    await expect(heroSection).toHaveScreenshot('hero-section.png', {
      maxDiffPixels: 50,
      threshold: 0.15
    });
  });
});
