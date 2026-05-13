# Regression Test Plan

## Critical Path Regression Tests

These tests must pass on every build/deployment to ensure core functionality remains intact.

### REG-001: User Registration and Login
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Navigate to registration page
2. Fill valid registration form
3. Submit registration
4. Verify registration successful
5. Login with new credentials
6. Verify login successful
7. Verify session created

**Expected Results:**
- Registration completes successfully
- Login works with new credentials
- User can access protected resources

**Failure Impact:** Critical - Users cannot register/login

---

### REG-002: Add to Cart and Checkout
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Login
2. Navigate to product page
3. Add product to cart
4. Navigate to cart
5. Verify item in cart
6. Proceed to checkout
7. Enter valid address
8. Complete checkout
9. Verify order created

**Expected Results:**
- Cart operations work
- Checkout completes successfully
- Order created in database

**Failure Impact:** Critical - Users cannot complete purchases

---

### REG-003: Payment Processing (Stripe)
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Create order from cart
2. Process Stripe payment with test card
3. Verify payment success
4. Verify order status "Paid"
5. Verify payment record created

**Expected Results:**
- Stripe integration works
- Payment processed successfully
- Order status updated

**Failure Impact:** Critical - Revenue generation blocked

---

### REG-004: Product CRUD by Admin
**Frequency:** Every build | **Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Login as admin
2. Navigate to product management
3. Create new product
4. Verify product created
5. Update product details
6. Verify product updated
7. Delete product
8. Verify product deleted

**Expected Results:**
- Product CRUD operations work
- Database state consistent

**Failure Impact:** High - Admin cannot manage inventory

---

### REG-005: Order Status Transitions
**Frequency:** Every build | **Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Create test order
2. Admin updates status to Processing
3. Verify status updated
4. Admin updates status to Shipped
5. Verify status updated
6. Admin updates status to Delivered
7. Verify status updated

**Expected Results:**
- Status transitions work correctly
- Email notifications sent (if configured)

**Failure Impact:** High - Order fulfillment blocked

---

### REG-006: Coupon Application
**Frequency:** Every build | **Priority:** High | **Status:** ⚠️ Partial

**Test Steps:**
1. Create valid coupon
2. Add items to cart
3. Apply coupon code
4. Verify discount applied
5. Verify total calculation correct
6. Complete order
7. Verify coupon usage incremented

**Expected Results:**
- Coupon validation works
- Discount applied correctly
- Usage tracking works

**Failure Impact:** Medium - Promotional features broken

---

### REG-007: CSRF Protection
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Login to application
2. Extract CSRF token
3. Make POST request without CSRF token
4. Verify request blocked (403)
5. Make POST request with valid CSRF token
6. Verify request succeeds

**Expected Results:**
- CSRF protection enabled
- Requests without token blocked
- Requests with token succeed

**Failure Impact:** Critical - Security vulnerability

---

### REG-008: Session Management
**Frequency:** Every build | **Priority:** High | **Status:** ⚠️ Partial

**Test Steps:**
1. Login
2. Store session ID
3. Access protected resource
4. Verify access granted
5. Logout
6. Attempt access with old session
7. Verify access denied

**Expected Results:**
- Session management works
- Logout invalidates session
- Protected resources secured

**Failure Impact:** High - Authentication/authorization broken

---

### REG-009: Stock Deduction on Order
**Frequency:** Every build | **Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Create product with known stock
2. Create order for product
3. Verify stock deducted
4. Cancel order
5. Verify stock restored
6. Verify final stock matches initial

**Expected Results:**
- Stock deducted on order
- Stock restored on cancellation
- No inventory inconsistencies

**Failure Impact:** High - Inventory management broken

---

### REG-010: Search and Filter Functionality
**Frequency:** Every build | **Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to search
2. Enter search query
3. Verify results returned
4. Apply category filter
5. Verify filtered results
6. Apply price filter
7. Verify combined filters work

**Expected Results:**
- Search returns results
- Filters work correctly
- Combined filters work

**Failure Impact:** Medium - Product discovery broken

---

## Smoke Tests (Quick Validation)

Run these for quick validation before full regression suite.

### SMOKE-001: Homepage Loads
**Frequency:** Every deployment | **Priority:** Critical | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to homepage
2. Verify page loads without errors
3. Verify products displayed
4. Verify navigation works

**Expected Results:** Homepage loads successfully

---

### SMOKE-002: Login Works
**Frequency:** Every deployment | **Priority:** Critical | **Status:** ⚠️ Partial

**Test Steps:**
1. Navigate to login page
2. Enter valid credentials
3. Submit login
4. Verify redirect to home
5. Verify session created

**Expected Results:** Login works

---

### SMOKE-003: Database Connection
**Frequency:** Every deployment | **Priority:** Critical | **Status:** ❌ Missing

**Test Steps:**
1. Execute simple query
2. Verify connection successful
3. Verify query returns results

**Expected Results:** Database accessible

---

### SMOKE-004: Cache Connection
**Frequency:** Every deployment | **Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Write test value to cache
2. Read value from cache
3. Verify value matches
4. Delete test value

**Expected Results:** Cache accessible

---

### SMOKE-005: Admin Login
**Frequency:** Every deployment | **Priority:** High | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to admin login
2. Enter admin credentials
3. Submit login
4. Verify redirect to admin dashboard
5. Verify admin features accessible

**Expected Results:** Admin login works

---

## Feature-Specific Regression Tests

### REG-FEATURE-001: Address Management
**Frequency:** Weekly | **Priority:** Medium | **Status:** ⚠️ Partial

**Test Steps:**
1. Login
2. Navigate to address management
3. Add new address
4. Verify address saved
5. Edit address
6. Verify changes saved
7. Delete address
8. Verify address removed

---

### REG-FEATURE-002: Wishlist Management
**Frequency:** Weekly | **Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Add product to wishlist
3. Verify wishlist updated
4. Remove from wishlist
5. Verify item removed
6. Move wishlist item to cart
7. Verify item in cart

---

### REG-FEATURE-003: Order History
**Frequency:** Weekly | **Priority:** Medium | **Status:** ⚠️ Partial

**Test Steps:**
1. Login
2. Create multiple orders
3. Navigate to order history
4. Verify all orders displayed
5. Filter by status
6. Verify filtered results
7. View order details
8. Verify details correct

---

### REG-FEATURE-004: Product Reviews
**Frequency:** Weekly | **Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Login
2. Purchase product
3. Mark order as delivered
4. Navigate to product
5. Write review
6. Submit review
7. Verify review displayed
8. Verify rating updated

---

### REG-FEATURE-005: Newsletter Subscription
**Frequency:** Weekly | **Priority:** Low | **Status:** ❌ Missing

**Test Steps:**
1. Navigate to homepage
2. Enter email in newsletter form
3. Submit subscription
4. Verify subscription confirmed
5. Unsubscribe
6. Verify unsubscribed

---

## Performance Regression Tests

### REG-PERF-001: Page Load Times
**Frequency:** Weekly | **Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Measure homepage load time
2. Measure product page load time
3. Measure cart page load time
4. Measure checkout page load time
5. Verify all < 3 seconds

**Threshold:** < 3 seconds for all pages

---

### REG-PERF-002: Database Query Performance
**Frequency:** Weekly | **Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Measure product search query time
2. Measure order history query time
3. Measure user authentication query time
4. Verify all < 500ms

**Threshold:** < 500ms for all queries

---

### REG-PERF-003: API Response Times
**Frequency:** Weekly | **Priority:** Medium | **Status:** ❌ Missing

**Test Steps:**
1. Measure API response time for /products
2. Measure API response time for /cart
3. Measure API response time for /orders
4. Verify all < 200ms

**Threshold:** < 200ms for all API calls

---

## Security Regression Tests

### REG-SEC-001: SQL Injection Protection
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Attempt SQL injection in login form
2. Attempt SQL injection in search
3. Attempt SQL injection in product forms
4. Verify all attempts blocked
5. Verify database integrity maintained

---

### REG-SEC-002: XSS Protection
**Frequency:** Every build | **Priority:** High | **Status:** ✅ Implemented

**Test Steps:**
1. Submit XSS payload in user profile
2. Submit XSS payload in product review
3. Verify payload sanitized
4. Verify script not executed

---

### REG-SEC-003: CSRF Protection
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Attempt POST without CSRF token
2. Verify request blocked
3. Attempt POST with valid CSRF token
4. Verify request succeeds

---

### REG-SEC-004: Authentication Enforcement
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Attempt access to protected resource without auth
2. Verify redirect to login
3. Login
4. Verify access granted

---

### REG-SEC-005: Authorization Enforcement
**Frequency:** Every build | **Priority:** Critical | **Status:** ✅ Implemented

**Test Steps:**
1. Login as regular user
2. Attempt access to admin resource
3. Verify access denied
4. Login as admin
5. Verify access granted

---

## Test Execution Schedule

| Test Suite | Frequency | Execution Time | Environment |
|------------|-----------|----------------|-------------|
| Smoke Tests | Every deployment | 5 minutes | Production |
| Critical Path Regression | Every build | 15 minutes | Staging |
| Feature-Specific Regression | Weekly | 30 minutes | Staging |
| Performance Regression | Weekly | 20 minutes | Staging |
| Security Regression | Every build | 10 minutes | Staging |

## Failure Handling

**Critical Test Failure:**
- Block deployment
- Notify development team immediately
- Create bug ticket
- Fix before next deployment

**High Priority Test Failure:**
- Block deployment if affects core features
- Notify development team
- Create bug ticket
- Fix within 24 hours

**Medium Priority Test Failure:**
- Allow deployment with warning
- Notify development team
- Create bug ticket
- Fix within 1 week

**Low Priority Test Failure:**
- Allow deployment
- Log for team review
- Fix in next sprint
