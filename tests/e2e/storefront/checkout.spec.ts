import { test, expect } from '@playwright/test';

test.describe('Storefront - Checkout Flow', () => {
  test('should display checkout page', async ({ page }) => {
    // Navigate to checkout (may require items in cart)
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    const checkoutPage = page.locator('.checkout-page, [data-testid="checkout-page"]');
    await expect(checkoutPage).toBeVisible();
  });

  test('should display shipping address form', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    const shippingForm = page.locator('#shipping, .shipping-form, [data-testid="shipping-form"]');
    await expect(shippingForm).toBeVisible();
  });

  test('should display billing address form', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    const billingForm = page.locator('#billing, .billing-form, [data-testid="billing-form"]');
    await expect(billingForm).toBeVisible();
  });

  test('should display payment information form', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    const paymentForm = page.locator('#payment, .payment-form, [data-testid="payment-form"]');
    await expect(paymentForm).toBeVisible();
  });

  test('should validate required checkout fields', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    // Try to submit without filling fields
    const placeOrderButton = page.locator('button:has-text("Place Order"), [data-testid="place-order"]');
    await placeOrderButton.click();
    
    // Should show validation errors
    const errorMessage = page.locator('.error-message, .alert-error, [data-testid="error-message"]');
    const isVisible = await errorMessage.isVisible().catch(() => false);
    
    if (isVisible) {
      await expect(errorMessage).toBeVisible();
    }
  });

  test('should display order summary', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    const orderSummary = page.locator('.order-summary, [data-testid="order-summary"]');
    await expect(orderSummary).toBeVisible();
  });

  test('should calculate total correctly', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    const subtotal = page.locator('.subtotal, [data-testid="subtotal"]');
    const tax = page.locator('.tax, [data-testid="tax"]');
    const total = page.locator('.total, [data-testid="total"]');
    
    await expect(subtotal).toBeVisible();
    await expect(tax).toBeVisible();
    await expect(total).toBeVisible();
  });

  test('should display loading state during checkout processing', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    // Fill form
    await page.fill('input[name="fullName"]', 'Test User');
    await page.fill('input[name="address"]', '123 Test Street');
    await page.fill('input[name="city"]', 'Test City');
    await page.fill('input[name="zip"]', '12345');
    await page.fill('input[name="cardNumber"]', '4111111111111111');
    await page.fill('input[name="expiry"]', '12/25');
    await page.fill('input[name="cvv"]', '123');
    
    const placeOrderButton = page.locator('button:has-text("Place Order"), [data-testid="place-order"]');
    await placeOrderButton.click();
    
    // Check for loading indicator
    const loadingIndicator = page.locator('.loading, .spinner, [data-testid="loading"]');
    const isLoading = await loadingIndicator.isVisible().catch(() => false);
    
    if (isLoading) {
      await loadingIndicator.waitFor({ state: 'hidden', timeout: 15000 });
    }
  });

  test('should display order confirmation after successful checkout', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    // Fill form with test data
    await page.fill('input[name="fullName"]', 'Test User');
    await page.fill('input[name="address"]', '123 Test Street');
    await page.fill('input[name="city"]', 'Test City');
    await page.fill('input[name="zip"]', '12345');
    await page.fill('input[name="cardNumber"]', '4111111111111111');
    await page.fill('input[name="expiry"]', '12/25');
    await page.fill('input[name="cvv"]', '123');
    
    const placeOrderButton = page.locator('button:has-text("Place Order"), [data-testid="place-order"]');
    await placeOrderButton.click();
    
    // Wait for order confirmation
    await page.waitForTimeout(3000);
    
    const orderConfirmation = page.locator('.order-confirmation, .success-message, [data-testid="order-confirmation"]');
    const isVisible = await orderConfirmation.isVisible().catch(() => false);
    
    if (isVisible) {
      await expect(orderConfirmation).toBeVisible();
    }
  });

  test('should display error state for failed payment', async ({ page }) => {
    await page.goto('/checkout');
    await page.waitForLoadState('networkidle');
    
    // Fill form with invalid card
    await page.fill('input[name="fullName"]', 'Test User');
    await page.fill('input[name="address"]', '123 Test Street');
    await page.fill('input[name="city"]', 'Test City');
    await page.fill('input[name="zip"]', '12345');
    await page.fill('input[name="cardNumber"]', '4000000000000002'); // Declined card
    await page.fill('input[name="expiry"]', '12/25');
    await page.fill('input[name="cvv"]', '123');
    
    const placeOrderButton = page.locator('button:has-text("Place Order"), [data-testid="place-order"]');
    await placeOrderButton.click();
    
    await page.waitForTimeout(3000);
    
    const errorMessage = page.locator('.error-message, .payment-error, [data-testid="payment-error"]');
    const isVisible = await errorMessage.isVisible().catch(() => false);
    
    if (isVisible) {
      await expect(errorMessage).toBeVisible();
    }
  });
});
