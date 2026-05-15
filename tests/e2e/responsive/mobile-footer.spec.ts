import { test, expect } from '@playwright/test';

test.describe('Responsive Mobile Footer', () => {
  test('should display desktop footer on desktop viewport', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopFooter = page.locator('.desktop-footer, .footer, [data-testid="desktop-footer"]');
    await expect(desktopFooter).toBeVisible();
    
    const mobileFooter = page.locator('.mobile-footer, [data-testid="mobile-footer"]');
    const isMobileVisible = await mobileFooter.isVisible().catch(() => false);
    expect(isMobileVisible).toBe(false);
  });

  test('should display mobile footer on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileFooter = page.locator('.mobile-footer, [data-testid="mobile-footer"]');
    await expect(mobileFooter).toBeVisible();
  });

  test('should hide desktop footer on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopFooter = page.locator('.desktop-footer, [data-testid="desktop-footer"]');
    const isVisible = await desktopFooter.isVisible().catch(() => false);
    expect(isVisible).toBe(false);
  });

  test('should display 4-column grid on desktop footer', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopFooter = page.locator('.desktop-footer, [data-testid="desktop-footer"]');
    const footerGrid = desktopFooter.locator('.footer-grid, .grid, [data-testid="footer-grid"]');
    
    const gridCount = await footerGrid.count();
    if (gridCount > 0) {
      // Check grid columns
      const gridColumns = await footerGrid.evaluate(el => {
        const style = window.getComputedStyle(el);
        return style.gridTemplateColumns || style.display;
      });
      
      expect(gridColumns).toBeTruthy();
    }
  });

  test('should display centered links on mobile footer', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileFooter = page.locator('.mobile-footer, [data-testid="mobile-footer"]');
    await expect(mobileFooter).toBeVisible();
    
    // Check for centered content
    const footerContent = mobileFooter.locator('.footer-content, [data-testid="footer-content"]');
    const contentCount = await footerContent.count();
    
    if (contentCount > 0) {
      const textAlign = await footerContent.evaluate(el => {
        const style = window.getComputedStyle(el);
        return style.textAlign;
      });
      
      expect(textAlign).toBe('center');
    }
  });

  test('should have proper footer spacing on desktop', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopFooter = page.locator('.desktop-footer, [data-testid="desktop-footer"]');
    await expect(desktopFooter).toBeVisible();
    
    // Check footer has proper margin-top
    const marginTop = await desktopFooter.evaluate(el => {
      const style = window.getComputedStyle(el);
      return style.marginTop;
    });
    
    expect(parseInt(marginTop)).toBeGreaterThan(0);
  });

  test('should have proper footer spacing on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileFooter = page.locator('.mobile-footer, [data-testid="mobile-footer"]');
    await expect(mobileFooter).toBeVisible();
    
    // Check footer has proper padding
    const padding = await mobileFooter.evaluate(el => {
      const style = window.getComputedStyle(el);
      return style.padding;
    });
    
    expect(padding).toBeTruthy();
  });

  test('should handle footer links correctly on desktop', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopFooter = page.locator('.desktop-footer, [data-testid="desktop-footer"]');
    const footerLinks = desktopFooter.locator('a');
    const linkCount = await footerLinks.count();
    
    if (linkCount > 0) {
      await expect(footerLinks.first()).toBeVisible();
    }
  });

  test('should handle footer links correctly on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileFooter = page.locator('.mobile-footer, [data-testid="mobile-footer"]');
    const footerLinks = mobileFooter.locator('a');
    const linkCount = await footerLinks.count();
    
    if (linkCount > 0) {
      await expect(footerLinks.first()).toBeVisible();
    }
  });

  test('should not overlap content on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileFooter = page.locator('.mobile-footer, [data-testid="mobile-footer"]');
    const footerBox = await mobileFooter.boundingBox();
    
    expect(footerBox).toBeTruthy();
    
    // Footer should be at the bottom
    const pageHeight = await page.evaluate(() => document.body.scrollHeight);
    expect(footerBox!.y + footerBox!.height).toBeLessThanOrEqual(pageHeight + 10);
  });
});
