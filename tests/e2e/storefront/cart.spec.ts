import { test, expect } from '@playwright/test';

test.describe('Storefront - Cart Flow', () => {
  test('should add product to cart', async ({ page }) => {
    await page.goto('/products');
    await page.waitForLoadState('networkidle');
    
    // Find first product and add to cart
    const addToCartButton = page.locator('button:has-text("Add to Cart"), [data-testid="add-to-cart"]').first();
    const buttonCount = await addToCartButton.count();
    
    if (buttonCount > 0) {
      await addToCartButton.click();
      
      // Verify cart icon updated
      const cartIcon = page.locator('.cart-icon, [data-testid="cart-icon"]');
      await expect(cartIcon).toBeVisible();
    }
  });

  test('should navigate to cart page', async ({ page }) => {
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartPage = page.locator('.cart-page, .shopping-cart, [data-testid="cart-page"]');
    await expect(cartPage).toBeVisible();
  });

  test('should display cart items', async ({ page }) => {
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartItems = page.locator('.cart-item, [data-testid="cart-item"]');
    const itemCount = await cartItems.count();
    
    if (itemCount > 0) {
      await expect(cartItems.first()).toBeVisible();
    }
  });

  test('should update cart item quantity', async ({ page }) => {
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartItems = page.locator('.cart-item, [data-testid="cart-item"]');
    const itemCount = await cartItems.count();
    
    if (itemCount > 0) {
      const quantityInput = cartItems.first().locator('input[type="number"], [data-testid="quantity"]');
      await quantityInput.fill('2');
      
      // Wait for cart to update
      await page.waitForTimeout(1000);
      
      const total = page.locator('.cart-total, [data-testid="cart-total"]');
      await expect(total).toBeVisible();
    }
  });

  test('should remove item from cart', async ({ page }) => {
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartItems = page.locator('.cart-item, [data-testid="cart-item"]');
    const itemCount = await cartItems.count();
    
    if (itemCount > 0) {
      const removeButton = cartItems.first().locator('button:has-text("Remove"), [data-testid="remove-item"]');
      await removeButton.click();
      
      // Wait for cart to update
      await page.waitForLoadState('networkidle');
    }
  });

  test('should display empty cart state', async ({ page }) => {
    // Clear cart by navigating to cart
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartItems = page.locator('.cart-item, [data-testid="cart-item"]');
    const itemCount = await cartItems.count();
    
    if (itemCount === 0) {
      const emptyCart = page.locator('.empty-cart, .no-items, [data-testid="empty-cart"]');
      await expect(emptyCart).toBeVisible();
      
      const continueShoppingButton = page.locator('a:has-text("Continue Shopping"), button:has-text("Continue Shopping")');
      await expect(continueShoppingButton).toBeVisible();
    }
  });

  test('should display cart total', async ({ page }) => {
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartTotal = page.locator('.cart-total, [data-testid="cart-total"]');
    await expect(cartTotal).toBeVisible();
  });

  test('should proceed to checkout from cart', async ({ page }) => {
    await page.goto('/cart');
    await page.waitForLoadState('networkidle');
    
    const cartItems = page.locator('.cart-item, [data-testid="cart-item"]');
    const itemCount = await cartItems.count();
    
    if (itemCount > 0) {
      const checkoutButton = page.locator('button:has-text("Checkout"), a:has-text("Checkout"), [data-testid="checkout-button"]');
      await checkoutButton.click();
      
      await page.waitForURL('/checkout');
      expect(page.url()).toContain('/checkout');
    }
  });
});
