import { test, expect } from '@playwright/test';

test.describe('Storefront Product Browsing', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('should display homepage with hero section', async ({ page }) => {
    await expect(page.locator('.hero-section')).toBeVisible();
    await expect(page.locator('.hero-title')).toBeVisible();
  });

  test('should navigate to products page', async ({ page }) => {
    await page.click('a[href="/products"]');
    await expect(page).toHaveURL('/products');
    await expect(page.locator('.product-grid')).toBeVisible();
  });

  test('should display product cards with correct information', async ({ page }) => {
    await page.goto('/products');
    
    const productCards = page.locator('.product-card');
    await expect(productCards.first()).toBeVisible();
    
    const firstCard = productCards.first();
    await expect(firstCard.locator('.product-name')).toBeVisible();
    await expect(firstCard.locator('.product-price')).toBeVisible();
    await expect(firstCard.locator('.product-image')).toBeVisible();
  });

  test('should filter products by category', async ({ page }) => {
    await page.goto('/products');
    
    await page.click('.filter-toggle-btn');
    await page.click('input[value="clothing"]');
    await page.click('.apply-filters-btn');
    
    await expect(page.locator('.product-grid')).toBeVisible();
  });

  test('should sort products by price', async ({ page }) => {
    await page.goto('/products');
    
    await page.selectOption('.sort-select', 'price-asc');
    
    const prices = await page.locator('.product-price').allTextContents();
    const numericPrices = prices.map(p => parseFloat(p.replace('$', '')));
    
    for (let i = 1; i < numericPrices.length; i++) {
      expect(numericPrices[i]).toBeGreaterThanOrEqual(numericPrices[i - 1]);
    }
  });

  test('should add product to wishlist', async ({ page }) => {
    await page.goto('/products');
    
    const firstCard = page.locator('.product-card').first();
    await firstCard.locator('.wishlist-button').click();
    
    await expect(firstCard.locator('.wishlist-button')).toHaveClass(/active/);
  });

  test('should search for products', async ({ page }) => {
    await page.click('.search-toggle');
    await page.fill('.search-input', 'shirt');
    await page.press('.search-input', 'Enter');
    
    await expect(page).toHaveURL(/.*search=shirt/);
    await expect(page.locator('.product-grid')).toBeVisible();
  });
});
