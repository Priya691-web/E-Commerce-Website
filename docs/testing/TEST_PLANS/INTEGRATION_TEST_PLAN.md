# Integration Test Plan

## Test Suite: Authentication Integration

### AUTH-INT-001: Registration → Email Verification → Login
**Priority:** High | **Status:** ⚠️ Partial (email verification not tested)

**Test Steps:**
1. Register new user
2. Verify user created in database (inactive)
3. Simulate email verification link click
4. Verify user activated
5. Attempt login with credentials
6. Verify successful login
7. Verify session created

**Expected Results:**
- User created with inactive status
- Verification activates user
- Login succeeds after verification

---

### AUTH-INT-002: Login → CSRF Token → Protected Request
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Login to application
2. Extract CSRF token from session
3. Make protected POST request with CSRF token
4. Verify request succeeds
5. Make protected POST request without CSRF token
6. Verify request blocked (403)

**Expected Results:**
- CSRF token generated on login
- Requests with valid token succeed
- Requests without token are blocked

---

### AUTH-INT-003: Login → Session Timeout → Re-authentication
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login to application
2. Store session ID
3. Wait for session timeout (simulate)
4. Attempt protected request
5. Verify redirect to login
6. Re-login
7. Verify new session created

**Expected Results:**
- Session expires after timeout
- Protected resources redirect to login
- New session created after re-login

---

### AUTH-INT-004: Password Reset Flow
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to forgot password
2. Enter email
3. Verify reset email sent (simulate)
4. Click reset link from email
5. Enter new password
6. Verify password updated
7. Login with new password
8. Verify old password rejected

**Expected Results:**
- Reset email sent
- Password can be reset via link
- Old password no longer works

---

### AUTH-INT-005: Multiple Tabs → Logout → All Tabs Invalidated
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login in Tab 1
2. Copy session cookie
3. Open Tab 2 with session cookie
4. Verify Tab 2 authenticated
5. Logout from Tab 1
6. Refresh Tab 2
7. Verify Tab 2 redirected to login

**Expected Results:**
- Session shared across tabs
- Logout invalidates all tabs

---

## Test Suite: Cart Integration

### CART-INT-001: Add → Update → Remove Item
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Login
2. Add item to cart
3. Verify cart count increased
4. Update item quantity
5. Verify quantity updated
6. Remove item from cart
7. Verify cart count decreased

**Expected Results:**
- Cart operations work correctly
- Database state matches session state

---

### CART-INT-002: Add Item → Session Expire → Restore Cart
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add items to cart
3. Store cart contents
4. Invalidate session (simulate timeout)
5. Re-login
6. Navigate to cart
7. Verify cart restored from database

**Expected Results:**
- Cart persists in database
- Cart restored after re-login

---

### CART-INT-003: Add Item → Concurrent Update → Consistency
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add item to cart (Thread 1)
3. Update quantity (Thread 2)
4. Remove item (Thread 3)
5. Verify final state consistent
6. Verify no duplicate items
7. Verify correct quantity

**Expected Results:**
- Concurrent operations handled correctly
- Final state is consistent
- No data corruption

---

### CART-INT-004: Add Out-of-Stock Item
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Set product stock to 0
2. Login
3. Attempt to add item to cart
4. Verify error message
5. Verify item not added
6. Verify cart unchanged

**Expected Results:**
- Out-of-stock items rejected
- Appropriate error shown
- Cart remains valid

---

### CART-INT-005: Add Item → Price Change → Total Updated
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add item to cart
3. Note cart total
4. Admin updates product price
5. Refresh cart
6. Verify total updated
7. Verify reflects new price

**Expected Results:**
- Cart totals updated with new prices
- User sees updated prices

---

## Test Suite: Payment Integration

### PAY-INT-001: Create Order → Stripe Payment → Status Update
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Create order from cart
2. Process Stripe payment
3. Verify payment success
4. Verify order status "Paid"
5. Verify payment record created
6. Verify transaction ID stored

**Expected Results:**
- Stripe payment processed
- Order status updated
- Payment record created

---

### PAY-INT-002: Failed Payment → Retry → Success
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Create order
2. Simulate payment failure
3. Verify order status "Pending"
4. Retry payment
5. Verify payment succeeds
6. Verify order status "Paid"

**Expected Results:**
- Failed payment doesn't corrupt order
- Retry mechanism works
- Status updates correctly

---

### PAY-INT-003: COD Order → Delivery
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Create order with COD
2. Verify payment status "pending"
3. Admin marks order as Shipped
4. Admin marks order as Delivered
5. Verify payment status "paid"
6. Verify order complete

**Expected Results:**
- COD workflow works
- Status transitions work
- Payment marked paid on delivery

---

### PAY-INT-004: Payment Webhook → Order Update
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Create order
2. Simulate Stripe webhook callback
3. Verify webhook signature validated
4. Verify order updated
5. Verify payment status updated
6. Verify inventory deducted

**Expected Results:**
- Webhook processed correctly
- Order updated without user action
- Signature validation works

---

### PAY-INT-005: Refund → Inventory Restoration
**Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Create paid order
2. Process refund
3. Verify payment status "refunded"
4. Verify order status "Refunded"
5. Verify inventory restored
6. Verify stock quantities updated

**Expected Results:**
- Refund processed
- Inventory restored correctly

---

## Test Suite: Search Integration

### SEARCH-INT-001: Basic Search Functionality
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to search
2. Enter search query
3. Submit search
4. Verify results returned
5. Verify results match query
6. Verify pagination works

**Expected Results:**
- Search returns relevant results
- Results match query terms
- Pagination works

---

### SEARCH-INT-002: Search with Filters
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to search
2. Enter search query
3. Apply category filter
4. Apply price range filter
5. Submit search
6. Verify results match all criteria

**Expected Results:**
- Combined filters work
- Results match all applied filters

---

### SEARCH-INT-003: Search Suggestions
**Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to search
2. Type partial query
3. Verify suggestions appear
4. Select suggestion
5. Verify search executed
6. Verify results match suggestion

**Expected Results:**
- Autocomplete suggestions work
- Suggestions are relevant
- Selection executes search

---

## Test Suite: Admin Integration

### ADMIN-INT-001: Product Creation to User Purchase
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Admin creates product
2. Admin sets inventory
3. Admin publishes product
4. User searches for product
5. User adds to cart
6. User completes purchase
7. Verify stock deducted

**Expected Results:**
- Product appears for users
- Inventory managed correctly
- Purchase deducts stock

---

### ADMIN-INT-002: Order Status Transitions
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. User creates order
2. Admin views order
3. Admin updates status to Processing
4. Admin updates status to Shipped
5. Admin updates status to Delivered
6. Verify email notifications sent
7. Verify user can track status

**Expected Results:**
- Status transitions work
- Notifications sent
- Tracking updated

---

### ADMIN-INT-003: User Blocking
**Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Admin views user
2. Admin blocks user
3. Verify user status updated
4. User attempts login
5. Verify login denied
6. Admin unblocks user
7. User attempts login
8. Verify login succeeds

**Expected Results:**
- Blocking prevents login
- Unblock restores access
