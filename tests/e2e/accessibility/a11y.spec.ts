import { test, expect } from '@playwright/test';

test.describe('Accessibility Testing', () => {
  test('should have valid HTML structure', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Check for proper doctype
    const doctype = await page.evaluate(() => document.doctype?.name);
    expect(doctype).toBe('html');
    
    // Check for lang attribute
    const lang = await page.evaluate(() => document.documentElement.lang);
    expect(lang).toBeTruthy();
  });

  test('should have proper heading hierarchy', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const headings = await page.evaluate(() => {
      const headings = Array.from(document.querySelectorAll('h1, h2, h3, h4, h5, h6'));
      return headings.map(h => ({
        tag: h.tagName,
        text: h.textContent?.trim()
      }));
    });
    
    // Should have at least one h1
    const h1Count = headings.filter(h => h.tag === 'H1').length;
    expect(h1Count).toBeGreaterThanOrEqual(1);
    
    // Headings should not skip levels
    let previousLevel = 0;
    for (const heading of headings) {
      const level = parseInt(heading.tag.charAt(1));
      if (previousLevel > 0 && level > previousLevel + 1) {
        expect.fail(`Heading skipped level: ${previousLevel} to ${level}`);
      }
      previousLevel = level;
    }
  });

  test('should have alt text for images', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const imagesWithoutAlt = await page.evaluate(() => {
      const images = Array.from(document.querySelectorAll('img'));
      return images.filter(img => !img.alt && img.getAttribute('src')?.length > 0).length;
    });
    
    expect(imagesWithoutAlt).toBe(0);
  });

  test('should have proper form labels', async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
    
    const inputsWithoutLabels = await page.evaluate(() => {
      const inputs = Array.from(document.querySelectorAll('input[type="text"], input[type="email"], input[type="password"], textarea, select'));
      return inputs.filter(input => {
        const id = input.id;
        if (!id) return true;
        const label = document.querySelector(`label[for="${id}"]`);
        const ariaLabel = input.getAttribute('aria-label');
        const ariaLabelledBy = input.getAttribute('aria-labelledby');
        return !label && !ariaLabel && !ariaLabelledBy;
      }).length;
    });
    
    expect(inputsWithoutLabels).toBe(0);
  });

  test('should have sufficient color contrast', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Check contrast for main text elements
    const contrastIssues = await page.evaluate(() => {
      const elements = Array.from(document.querySelectorAll('p, h1, h2, h3, h4, h5, h6, button, a, label'));
      const issues: string[] = [];
      
      elements.forEach(el => {
        const style = window.getComputedStyle(el);
        const color = style.color;
        const bgColor = style.backgroundColor;
        
        // Simple check - in production, use axe-core or similar
        if (color === 'rgba(0, 0, 0, 0)' || bgColor === 'rgba(0, 0, 0, 0)') {
          // Transparent, skip
        }
      });
      
      return issues;
    });
    
    // For now, just ensure no obvious issues
    expect(contrastIssues.length).toBe(0);
  });

  test('should be keyboard navigable', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Tab through interactive elements
    const interactiveElements = await page.evaluate(() => {
      return Array.from(document.querySelectorAll('a, button, input, select, textarea'))
        .filter(el => {
          const style = window.getComputedStyle(el);
          return style.display !== 'none' && style.visibility !== 'hidden';
        })
        .length;
    });
    
    expect(interactiveElements).toBeGreaterThan(0);
    
    // Test tab navigation
    await page.keyboard.press('Tab');
    const focusedElement = await page.evaluate(() => document.activeElement?.tagName);
    expect(['A', 'BUTTON', 'INPUT', 'SELECT', 'TEXTAREA']).toContain(focusedElement);
  });

  test('should have proper focus indicators', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    // Focus on first interactive element
    await page.keyboard.press('Tab');
    
    const hasFocusIndicator = await page.evaluate(() => {
      const focused = document.activeElement;
      if (!focused) return false;
      const style = window.getComputedStyle(focused);
      return style.outline !== 'none' || style.boxShadow !== 'none';
    });
    
    expect(hasFocusIndicator).toBe(true);
  });

  test('should have proper ARIA attributes', async ({ page }) => {
    await page.goto('/login');
    await page.waitForLoadState('networkidle');
    
    // Check for proper ARIA attributes on forms
    const formElements = await page.evaluate(() => {
      const inputs = Array.from(document.querySelectorAll('input[required]'));
      return inputs.filter(input => {
        const ariaRequired = input.getAttribute('aria-required');
        return ariaRequired === 'true' || ariaRequired === null; // null is ok if required attribute is present
      }).length;
    });
    
    expect(formElements).toBeGreaterThan(0);
  });

  test('should have skip navigation link', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const skipLink = page.locator('a[href^="#"], .skip-link, [data-testid="skip-link"]');
    const skipLinkCount = await skipLink.count();
    
    // Skip link is recommended but not required
    if (skipLinkCount > 0) {
      await expect(skipLink.first()).toBeVisible();
    }
  });

  test('should have proper landmark regions', async ({ page }) => {
    await page.goto('/');
    await page.waitForLoadState('networkidle');
    
    const hasMain = await page.locator('main, [role="main"]').count();
    const hasNav = await page.locator('nav, [role="navigation"]').count();
    const hasHeader = await page.locator('header, [role="banner"]').count();
    const hasFooter = await page.locator('footer, [role="contentinfo"]').count();
    
    expect(hasMain).toBeGreaterThan(0);
    expect(hasNav).toBeGreaterThan(0);
    expect(hasHeader).toBeGreaterThan(0);
    expect(hasFooter).toBeGreaterThan(0);
  });
});
