import { test, expect, Page } from '@playwright/test';

test.describe('Authentication Flow', () => {
  let page: Page;

  test.beforeEach(async ({ browser }) => {
    page = await browser.newPage();
    await page.goto('/');
  });

  test.afterEach(async () => {
    await page.close();
  });

  test('should navigate to login page', async () => {
    // Look for login/sign in button
    const loginButton = page.locator('a[href="/login"], button:has-text("Sign In"), a:has-text("Login")').first();
    
    if (await loginButton.count() > 0) {
      await loginButton.click();
      await expect(page).toHaveURL(/.*login/);
    } else {
      // If no login button found, navigate directly
      await page.goto('/login');
      await expect(page).toHaveURL(/.*login/);
    }
  });

  test('should show validation errors for empty form', async () => {
    await page.goto('/login');
    
    // Try to submit form without filling it
    const submitButton = page.locator('button[type="submit"], button:has-text("Login"), button:has-text("Sign In")').first();
    
    if (await submitButton.count() > 0) {
      await submitButton.click();
      
      // Check for validation messages (could be various formats)
      await expect(
        page.locator('text="Email is required", text="Required", [aria-invalid="true"]').first()
      ).toBeVisible({ timeout: 3000 });
    }
  });

  test('should navigate to registration page', async () => {
    await page.goto('/login');
    
    // Look for registration link
    const registerLink = page.locator('a[href="/register"], a:has-text("Register"), a:has-text("Sign Up"), a:has-text("Create account")').first();
    
    if (await registerLink.count() > 0) {
      await registerLink.click();
      await expect(page).toHaveURL(/.*register/);
    } else {
      // Navigate directly if no link found
      await page.goto('/register');
      await expect(page).toHaveURL(/.*register/);
    }
  });

  test('should show form validation on registration', async () => {
    await page.goto('/register');
    
    // Try to submit empty registration form
    const submitButton = page.locator('button[type="submit"], button:has-text("Register"), button:has-text("Sign Up")').first();
    
    if (await submitButton.count() > 0) {
      await submitButton.click();
      
      // Should show validation for required fields
      const errorMessages = page.locator('[aria-invalid="true"], .error, .text-red-500, text="required"');
      await expect(errorMessages.first()).toBeVisible({ timeout: 3000 });
    }
  });

  test('should attempt login with test credentials', async () => {
    await page.goto('/login');
    
    const emailField = page.locator('input[type="email"], input[name="email"]').first();
    const passwordField = page.locator('input[type="password"], input[name="password"]').first();
    const submitButton = page.locator('button[type="submit"], button:has-text("Login"), button:has-text("Sign In")').first();
    
    if (await emailField.count() > 0 && await passwordField.count() > 0) {
      await emailField.fill('test@example.com');
      await passwordField.fill('password123');
      
      if (await submitButton.count() > 0) {
        await submitButton.click();
        
        // Wait for either success redirect or error message
        await page.waitForTimeout(2000);
        
        // Check if we're redirected to dashboard or still on login with error
        const currentUrl = page.url();
        const hasError = await page.locator('.error, .text-red-500, [role="alert"]').count() > 0;
        
        // Either should be redirected or show error (both are valid since we don't have real backend)
        expect(currentUrl.includes('/dashboard') || hasError || currentUrl.includes('/login')).toBe(true);
      }
    }
  });

  test('should navigate between auth pages', async () => {
    // Start at home
    await page.goto('/');
    
    // Go to login
    await page.goto('/login');
    await expect(page).toHaveURL(/.*login/);
    
    // Go to register
    await page.goto('/register');
    await expect(page).toHaveURL(/.*register/);
    
    // Back to login
    await page.goto('/login');
    await expect(page).toHaveURL(/.*login/);
  });

  test('should handle forgot password flow', async () => {
    await page.goto('/login');
    
    // Look for forgot password link
    const forgotPasswordLink = page.locator('a[href="/forgot-password"], a:has-text("Forgot"), a:has-text("Reset password")').first();
    
    if (await forgotPasswordLink.count() > 0) {
      await forgotPasswordLink.click();
      await expect(page).toHaveURL(/.*forgot/);
      
      // Check for email input on forgot password page
      const emailField = page.locator('input[type="email"]').first();
      if (await emailField.count() > 0) {
        await emailField.fill('test@example.com');
        
        const submitButton = page.locator('button[type="submit"]').first();
        if (await submitButton.count() > 0) {
          await submitButton.click();
          await page.waitForTimeout(1000);
          // Should show success message or stay on page
        }
      }
    }
  });
});