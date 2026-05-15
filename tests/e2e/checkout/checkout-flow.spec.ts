import { test, expect } from '@playwright/test';

test.describe('Checkout Flow', () => {
  test.beforeEach(async ({ page }) => {
    // Login and add items to cart
    await page.goto('/login');
    await page.fill('input[name="email"]', 'test@example.com');
    await page.fill('input[name="password"]', 'password123');
    await page.click('button[type="submit"]');
    
    await page.goto('/products');
    await page.locator('.product-card').first().locator('.add-to-cart').click();
  });

  test('should display cart with added items', async ({ page }) => {
    await page.goto('/cart');
    
    await expect(page.locator('.cart-item')).toBeVisible();
    await expect(page.locator('.cart-total')).toBeVisible();
  });

  test('should update item quantity', async ({ page }) => {
    await page.goto('/cart');
    
    const quantityInput = page.locator('.quantity-input').first();
    await quantityInput.fill('3');
    
    await expect(page.locator('.cart-total')).toContainText('$');
  });

  test('should proceed to checkout', async ({ page }) => {
    await page.goto('/cart');
    await page.click('.checkout-button');
    
    await expect(page).toHaveURL('/checkout');
    await expect(page.locator('.checkout-form')).toBeVisible();
  });

  test('should fill shipping address', async ({ page }) => {
    await page.goto('/checkout');
    
    await page.fill('input[name="fullName"]', 'John Doe');
    await page.fill('input[name="address"]', '123 Main St');
    await page.fill('input[name="city"]', 'New York');
    await page.fill('input[name="zipCode"]', '10001');
    await page.fill('input[name="country"]', 'USA');
    
    await page.click('.save-address-btn');
    
    await expect(page.locator('.address-saved')).toBeVisible();
  });

  test('should select payment method', async ({ page }) => {
    await page.goto('/checkout');
    
    await page.click('.payment-method-stripe');
    
    await expect(page.locator('.stripe-card-element')).toBeVisible();
  });

  test('should complete order', async ({ page }) => {
    await page.goto('/checkout');
    
    // Fill address
    await page.fill('input[name="fullName"]', 'John Doe');
    await page.fill('input[name="address"]', '123 Main St');
    await page.fill('input[name="city"]', 'New York');
    await page.fill('input[name="zipCode"]', '10001');
    await page.fill('input[name="country"]', 'USA');
    
    // Select payment
    await page.click('.payment-method-stripe');
    
    // Place order
    await page.click('.place-order-btn');
    
    await expect(page).toHaveURL('/order-success');
    await expect(page.locator('.order-confirmation')).toBeVisible();
  });

  test('should display free shipping progress bar', async ({ page }) => {
    await page.goto('/cart');
    
    await expect(page.locator('.free-shipping-progress')).toBeVisible();
    await expect(page.locator('.progress-bar')).toBeVisible();
  });

  test('should apply coupon code', async ({ page }) => {
    await page.goto('/cart');
    
    await page.fill('.coupon-input', 'SAVE10');
    await page.click('.apply-coupon-btn');
    
    await expect(page.locator('.coupon-applied')).toBeVisible();
    await expect(page.locator('.cart-total')).toContainText('$');
  });
});
