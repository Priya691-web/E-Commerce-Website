import { Page, Locator } from '@playwright/test';
import { BasePage } from './BasePage';

export class AdminDashboardPage extends BasePage {
  readonly sidebar: Locator;
  readonly topbar: Locator;
  readonly statCards: Locator;
  readonly recentOrders: Locator;
  readonly logoutButton: Locator;
  readonly productsLink: Locator;
  readonly ordersLink: Locator;
  readonly usersLink: Locator;
  readonly categoriesLink: Locator;

  constructor(page: Page) {
    super(page, '/admin/dashboard');
    this.sidebar = page.locator('.sidebar');
    this.topbar = page.locator('.topbar');
    this.statCards = page.locator('.stat-card');
    this.recentOrders = page.locator('.recent-orders');
    this.logoutButton = page.locator('button[aria-label="Logout"]');
    this.productsLink = page.locator('a[href="/admin/products"]');
    this.ordersLink = page.locator('a[href="/admin/orders"]');
    this.usersLink = page.locator('a[href="/admin/users"]');
    this.categoriesLink = page.locator('a[href="/admin/categories"]');
  }

  async navigateToProducts() {
    await this.productsLink.click();
    await this.page.waitForURL('/admin/products');
  }

  async navigateToOrders() {
    await this.ordersLink.click();
    await this.page.waitForURL('/admin/orders');
  }

  async navigateToUsers() {
    await this.usersLink.click();
    await this.page.waitForURL('/admin/users');
  }

  async navigateToCategories() {
    await this.categoriesLink.click();
    await this.page.waitForURL('/admin/categories');
  }

  async logout() {
    await this.logoutButton.click();
    await this.page.waitForURL('/login');
  }

  async getStatCardCount(): Promise<number> {
    await this.statCards.waitFor();
    return await this.statCards.count();
  }
}
