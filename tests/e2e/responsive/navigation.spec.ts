import { test, expect } from '@playwright/test';

test.describe('Responsive Navigation', () => {
  test('should display desktop navbar on desktop viewport', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopNav = page.locator('.fs-storefront-nav, .navbar, [data-testid="desktop-nav"]');
    await expect(desktopNav).toBeVisible();
    
    const mobileNav = page.locator('.fs-mobile-nav, .mobile-bottom-nav, [data-testid="mobile-nav"]');
    const isMobileVisible = await mobileNav.isVisible().catch(() => false);
    expect(isMobileVisible).toBe(false);
  });

  test('should hide mobile bottom nav on desktop', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileNav = page.locator('.fs-mobile-nav, .mobile-bottom-nav, [data-testid="mobile-nav"]');
    
    // Check computed display style
    const display = await mobileNav.evaluate(el => window.getComputedStyle(el).display);
    expect(display).toBe('none');
  });

  test('should display mobile bottom nav on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileNav = page.locator('.fs-mobile-nav, .mobile-bottom-nav, [data-testid="mobile-nav"]');
    await expect(mobileNav).toBeVisible();
  });

  test('should hide desktop navbar on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopNav = page.locator('.fs-storefront-nav, .navbar, [data-testid="desktop-nav"]');
    
    // Desktop nav might be hidden or collapsed
    const isVisible = await desktopNav.isVisible().catch(() => false);
    
    // Either hidden or collapsed to hamburger menu
    if (isVisible) {
      const hamburger = desktopNav.locator('.hamburger, .menu-toggle, [data-testid="hamburger"]');
      const hasHamburger = await hamburger.count() > 0;
      expect(hasHamburger).toBe(true);
    }
  });

  test('should handle tablet viewport correctly', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const desktopNav = page.locator('.fs-storefront-nav, .navbar, [data-testid="desktop-nav"]');
    const mobileNav = page.locator('.fs-mobile-nav, .mobile-bottom-nav, [data-testid="mobile-nav"]');
    
    // Desktop nav should be visible on tablet
    await expect(desktopNav).toBeVisible();
    
    // Mobile nav should be hidden on tablet
    const isMobileVisible = await mobileNav.isVisible().catch(() => false);
    expect(isMobileVisible).toBe(false);
  });

  test('should handle responsive navigation menu on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Check for mobile navigation items
    const mobileNavItems = page.locator('.fs-mobile-nav a, .mobile-bottom-nav a, [data-testid="mobile-nav"] a');
    const itemCount = await mobileNavItems.count();
    expect(itemCount).toBeGreaterThan(0);
  });

  test('should maintain navigation functionality across viewports', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Test navigation on desktop
    const productsLink = page.locator('a[href="/products"], [data-testid="products-link"]');
    const desktopCount = await productsLink.count();
    
    if (desktopCount > 0) {
      await productsLink.first().click();
      await page.waitForURL('/products');
      expect(page.url()).toContain('/products');
    }
    
    // Switch to mobile and test
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const mobileProductsLink = page.locator('.fs-mobile-nav a[href="/products"], [data-testid="mobile-products-link"]');
    const mobileCount = await mobileProductsLink.count();
    
    if (mobileCount > 0) {
      await mobileProductsLink.first().click();
      await page.waitForURL('/products');
      expect(page.url()).toContain('/products');
    }
  });

  test('should have no horizontal scroll on mobile', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Check for horizontal scroll
    const bodyWidth = await page.evaluate(() => document.body.scrollWidth);
    const viewportWidth = await page.evaluate(() => window.innerWidth);
    
    expect(bodyWidth).toBeLessThanOrEqual(viewportWidth);
  });

  test('should have no horizontal scroll on desktop', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const bodyWidth = await page.evaluate(() => document.body.scrollWidth);
    const viewportWidth = await page.evaluate(() => window.innerWidth);
    
    expect(bodyWidth).toBeLessThanOrEqual(viewportWidth);
  });
});
