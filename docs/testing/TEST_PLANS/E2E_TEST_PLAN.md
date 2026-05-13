# End-to-End Test Plan

## Test Suite: Complete User Journey

### E2E-001: Guest Browse to Customer Conversion
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Navigate to homepage as guest
2. Browse products by category
3. View product details
4. Add product to cart
5. Attempt checkout
6. Redirect to registration
7. Complete registration
8. Auto-login after registration
9. Complete checkout
10. Verify order created

**Expected Results:**
- Guest can browse without login
- Cart persists through registration
- User auto-logged in after registration
- Order successfully created

---

### E2E-002: Registered User Complete Purchase with Coupon
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Login with valid credentials
2. Browse products
3. Add multiple items to cart
4. Apply valid coupon code
5. Verify discount applied
6. Proceed to checkout
7. Enter shipping address
8. Select payment method
9. Complete payment
10. Verify order confirmation

**Expected Results:**
- Discount correctly applied
- Total calculation accurate
- Payment processed successfully
- Order status updated to Paid

---

### E2E-003: Purchase → Order Tracking → Product Review
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add product to cart
3. Complete purchase
4. Navigate to order history
5. Click on order details
6. Track order status
7. Wait for delivery (simulate)
8. Navigate to product
9. Write review
10. Submit review
11. Verify review appears on product page

**Expected Results:**
- Order tracking shows correct status
- Review can be submitted after delivery
- Review appears on product page
- Rating affects product average

---

### E2E-004: Wishlist to Cart to Purchase
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Browse products
3. Add product to wishlist
4. Navigate to wishlist
5. Move item to cart
6. Verify item removed from wishlist
7. Verify item added to cart
8. Complete purchase
9. Verify wishlist empty

**Expected Results:**
- Wishlist stores products correctly
- Move to cart works
- Wishlist updates after purchase

---

### E2E-005: Purchase → Cancel → Refund
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Login
2. Add items to cart
3. Complete purchase
4. Navigate to order details
5. Cancel order
6. Verify order status Cancelled
7. Request refund
8. Verify refund processed
9. Verify inventory restored

**Expected Results:**
- Order can be cancelled
- Refund processed successfully
- Stock quantity restored

---

### E2E-006: Admin Product Creation to User Purchase
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. Navigate to product management
3. Create new product with details
4. Set inventory levels
5. Publish product
6. Logout admin
7. Login as user
8. Find new product
9. Add to cart
10. Complete purchase
11. Verify stock deducted

**Expected Results:**
- Admin can create product
- Product appears for users
- Stock management works

---

### E2E-007: Profile Update → Address Management → Purchase
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Navigate to profile
3. Update personal information
4. Add new shipping address
5. Set as default
6. Add billing address
7. Add items to cart
8. Checkout with new address
9. Verify order uses correct address

**Expected Results:**
- Profile updates persist
- Address CRUD works
- Checkout uses selected address

---

### E2E-008: Search → Filter → Sort → Purchase
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Search for product by keyword
3. Filter by category
4. Filter by price range
5. Sort by price (low to high)
6. Select product
7. Add to cart
8. Complete purchase

**Expected Results:**
- Search returns relevant results
- Filters work correctly
- Sorting works
- Combined filters work

---

### E2E-009: Cart Abandonment → Return → Complete
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add items to cart
3. Navigate to cart
4. Close browser (simulate abandonment)
5. Wait 24 hours (simulate)
6. Return to site
7. Login
8. Navigate to cart
9. Verify cart items preserved
10. Complete purchase

**Expected Results:**
- Cart persists across sessions
- Cart items available after return

---

### E2E-010: COD Purchase → Delivery → User Review
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add items to cart
3. Checkout with COD payment
4. Verify order status Pending
5. Admin marks as Shipped
6. Admin marks as Delivered
7. User receives notification
8. User writes review
9. Verify review appears

**Expected Results:**
- COD payment works
- Status transitions work
- Notifications sent
- Review can be submitted

---

## Test Suite: Admin Workflows

### ADMIN-E2E-001: Product Lifecycle Management
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. Create product with images
3. Set inventory levels
4. Set pricing
5. Publish product
6. Update product details
7. Add discount
8. Remove discount
9. Delete product (with no orders)

**Expected Results:**
- Full product CRUD works
- Inventory management works
- Pricing updates work

---

### ADMIN-E2E-002: Order Fulfillment Workflow
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. View new orders
3. Process order (confirm payment)
4. Update status to Processing
5. Update status to Shipped
6. Update status to Delivered
7. Handle return request
8. Process refund

**Expected Results:**
- Order status transitions work
- Email notifications sent
- Inventory updated correctly

---

### ADMIN-E2E-003: Coupon Management
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. Create coupon code
3. Set discount percentage
4. Set usage limit
5. Set expiration date
6. Apply coupon to order
7. Verify discount applied
8. Deactivate coupon
9. Verify coupon no longer works

**Expected Results:**
- Coupon CRUD works
- Coupon validation works
- Usage limits enforced

---

### ADMIN-E2E-004: User Management
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. View all users
3. Search user by email
4. View user details
5. Block user
6. Verify user cannot login
7. Unblock user
8. Verify user can login
9. Reset user password
10. Change user role

**Expected Results:**
- User CRUD works
- Blocking works
- Password reset works
- Role changes work

---

### ADMIN-E2E-005: Analytics Dashboard
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. View dashboard
3. Verify revenue metrics
4. Verify order counts
5. Verify user counts
6. Filter by date range
7. Export analytics report
8. Verify report accuracy

**Expected Results:**
- Dashboard displays correct data
- Filters work
- Export works
