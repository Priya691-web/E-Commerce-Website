# API Test Coverage Document

## API Endpoints Coverage Matrix

| Endpoint | Method | Controller | Test Coverage | Status | Priority |
|----------|--------|------------|---------------|--------|----------|
| /register | POST | RegisterController | 70% | ⚠️ Partial | High |
| /login | POST | LoginController | 85% | ✅ Good | High |
| /logout | POST | LogoutController | 80% | ✅ Good | High |
| /cart/add | POST | CartController | 60% | ⚠️ Partial | High |
| /cart/update | POST | CartController | 50% | ⚠️ Partial | High |
| /cart/remove | POST | CartController | 50% | ⚠️ Partial | High |
| /cart/items | GET | CartController | 60% | ⚠️ Partial | High |
| /checkout/* | POST/GET | CheckoutControllerV2 | 70% | ⚠️ Partial | High |
| /payment/process | POST | PaymentController | 70% | ⚠️ Partial | High |
| /payment/callback | POST | PaymentController | 40% | ❌ Weak | High |
| /payment/retry | POST | PaymentRetryController | 0% | ❌ Missing | High |
| /products | GET | ProductController | 40% | ❌ Weak | High |
| /products/{id} | GET | ProductDetailsController | 50% | ⚠️ Partial | High |
| /products/search | GET | ProductController | 0% | ❌ Missing | High |
| /products/filter | GET | ProductFilterController | 0% | ❌ Missing | High |
| /search | GET | SearchController | 0% | ❌ Missing | High |
| /search/suggestions | GET | SearchSuggestionController | 0% | ❌ Missing | Medium |
| /orders | GET | OrderController | 60% | ⚠️ Partial | High |
| /orders/{id} | GET | OrderController | 50% | ⚠️ Partial | High |
| /orders/{id}/cancel | POST | OrderController | 40% | ❌ Weak | High |
| /orders/{id}/track | GET | OrderTrackingController | 30% | ❌ Weak | Medium |
| /account/profile | GET/POST | ProfileController | 40% | ❌ Weak | High |
| /account/profile/edit | GET/POST | ProfileController | 40% | ❌ Weak | High |
| /account/addresses | GET/POST | AddressController | 45% | ❌ Weak | High |
| /account/addresses/{id} | DELETE | AddressController | 40% | ❌ Weak | Medium |
| /admin/products | CRUD | AdminProductController | 50% | ⚠️ Partial | High |
| /admin/orders | CRUD | AdminOrderController | 45% | ❌ Weak | High |
| /admin/users | CRUD | AdminUsersController | 40% | ❌ Weak | High |
| /admin/dashboard | GET | AdminDashboardController | 0% | ❌ Missing | High |
| /admin/analytics | GET | AdminAnalyticsController | 0% | ❌ Missing | Medium |
| /api/admin/login | POST | AdminApiController | 60% | ⚠️ Partial | High |
| /api/products | GET | ProductController | 30% | ❌ Weak | Medium |
| /api/cart | GET/POST | CartController | 40% | ❌ Weak | Medium |
| /api/checkout | POST | CheckoutController | 50% | ⚠️ Partial | Medium |

---

## Detailed API Test Coverage Analysis

### Authentication APIs

#### POST /register
**Test Coverage:** 70%
**Tested Scenarios:**
- ✅ Valid registration
- ✅ Duplicate email handling
- ✅ Password validation
- ❌ Email with trailing spaces
- ❌ Email with special characters
- ❌ Phone format validation
- ❌ Name length validation
- ❌ Email verification flow

**Missing Test Cases:**
- Registration with invalid phone format
- Registration with weak password rejection
- Registration with special characters in name
- Email verification (if implemented)
- Registration rate limiting

#### POST /login
**Test Coverage:** 85%
**Tested Scenarios:**
- ✅ Valid login
- ✅ Invalid password
- ✅ Non-existent email
- ✅ SQL injection attempt
- ❌ Login after password reset
- ❌ Login with expired session
- ❌ Login with locked account
- ❌ Remember me functionality

**Missing Test Cases:**
- Login after password reset
- Session timeout handling
- Account lockout after failed attempts
- Remember me cookie validation

#### POST /logout
**Test Coverage:** 80%
**Tested Scenarios:**
- ✅ Logout invalidates session
- ✅ CSRF token invalidation
- ❌ Logout with invalid session
- ❌ Logout during active transaction
- ❌ Multiple tabs logout

---

### Cart APIs

#### POST /cart/add
**Test Coverage:** 60%
**Tested Scenarios:**
- ✅ Add item to cart
- ✅ Add multiple items
- ❌ Add out-of-stock item
- ❌ Add with invalid quantity
- ❌ Add non-existent product
- ❌ Add duplicate item (update quantity)
- ❌ Add with maximum quantity limit

**Missing Test Cases:**
- Add item with zero quantity
- Add item with negative quantity
- Add item exceeding available stock
- Add item with price change between add and checkout

#### POST /cart/update
**Test Coverage:** 50%
**Tested Scenarios:**
- ✅ Update quantity
- ❌ Update to zero (remove item)
- ❌ Update beyond available stock
- ❌ Update with invalid value
- ❌ Concurrent updates

**Missing Test Cases:**
- Update quantity to zero removes item
- Update quantity exceeds stock
- Update with negative value
- Update with non-numeric value

#### POST /cart/remove
**Test Coverage:** 50%
**Tested Scenarios:**
- ✅ Remove item
- ❌ Remove non-existent item
- ❌ Remove during checkout
- ❌ Clear entire cart

**Missing Test Cases:**
- Remove item that doesn't exist
- Remove item while in checkout flow
- Clear all items from cart

---

### Checkout APIs

#### POST /checkout/process
**Test Coverage:** 70%
**Tested Scenarios:**
- ✅ Valid checkout
- ✅ Checkout with coupon
- ✅ Checkout with COD
- ❌ Checkout with empty cart
- ❌ Checkout with invalid address
- ❌ Checkout with expired coupon
- ❌ Checkout during stock depletion
- ❌ Checkout timeout

**Missing Test Cases:**
- Checkout with empty cart
- Checkout with missing required fields
- Checkout with invalid coupon code
- Checkout when stock becomes unavailable
- Checkout with concurrent stock changes

---

### Payment APIs

#### POST /payment/process
**Test Coverage:** 70%
**Tested Scenarios:**
- ✅ Stripe payment success
- ✅ COD payment
- ✅ Payment refund
- ❌ Payment with expired card
- ❌ Payment with insufficient funds
- ❌ Payment with 3D Secure
- ❌ Payment timeout
- ❌ Payment webhook verification

**Missing Test Cases:**
- Payment with declined card
- Payment with insufficient funds
- Payment with 3D Secure authentication
- Payment timeout handling
- Duplicate payment handling

#### POST /payment/callback
**Test Coverage:** 40%
**Tested Scenarios:**
- ✅ Webhook signature validation (partial)
- ❌ Duplicate webhook handling
- ❌ Invalid webhook signature
- ❌ Webhook for failed payment
- ❌ Webhook for partial refund

**Missing Test Cases:**
- Handle duplicate webhook events
- Reject webhook with invalid signature
- Process webhook for failed payments
- Process webhook for refunds

#### POST /payment/retry
**Test Coverage:** 0%
**Status:** ❌ Missing

**Required Test Cases:**
- Retry failed payment
- Retry with different payment method
- Retry limit enforcement
- Retry status tracking
- Retry after timeout

---

### Product APIs

#### GET /products
**Test Coverage:** 40%
**Tested Scenarios:**
- ✅ Get all products
- ✅ Get product by ID
- ❌ Pagination
- ❌ Sorting
- ❌ Filtering
- ❌ Search

**Missing Test Cases:**
- Get products with pagination
- Get products sorted by price
- Get products filtered by category
- Get products with stock filter

#### GET /products/search
**Test Coverage:** 0%
**Status:** ❌ Missing

**Required Test Cases:**
- Basic search by keyword
- Search with special characters
- Search with empty query
- Search pagination
- Search sorting
- Search results relevance

#### GET /products/filter
**Test Coverage:** 0%
**Status:** ❌ Missing

**Required Test Cases:**
- Filter by category
- Filter by price range
- Filter by size
- Filter by color
- Filter by brand
- Multiple filter combinations

---

### Order APIs

#### GET /orders
**Test Coverage:** 60%
**Tested Scenarios:**
- ✅ Get user orders
- ✅ Get order by ID
- ❌ Order pagination
- ❌ Order filtering
- ❌ Order search

**Missing Test Cases:**
- Get orders with pagination
- Filter orders by status
- Search orders by date range
- Get orders with items loaded

#### POST /orders/{id}/cancel
**Test Coverage:** 40%
**Tested Scenarios:**
- ✅ Cancel pending order
- ❌ Cancel shipped order
- ❌ Cancel delivered order
- ❌ Cancel with refund
- ❌ Cancel without refund

**Missing Test Cases:**
- Attempt to cancel shipped order
- Attempt to cancel delivered order
- Cancel with automatic refund
- Cancel without refund

---

### Admin APIs

#### CRUD /admin/products
**Test Coverage:** 50%
**Tested Scenarios:**
- ✅ Create product
- ✅ Update product
- ✅ Delete product
- ❌ Product with image upload
- ❌ Product variant management
- ❌ Product size management
- ❌ Bulk operations

**Missing Test Cases:**
- Create product with image upload
- Update product image
- Delete product with active orders
- Bulk product import
- Bulk product delete

#### CRUD /admin/orders
**Test Coverage:** 45%
**Tested Scenarios:**
- ✅ Get all orders
- ✅ Get order by ID
- ❌ Order status transitions
- ❌ Bulk status update
- ❌ Order cancellation
- ❌ Order refund
- ❌ Order export

**Missing Test Cases:**
- Update order status
- Bulk update order status
- Cancel order as admin
- Refund order as admin
- Export orders to CSV

#### GET /admin/dashboard
**Test Coverage:** 0%
**Status:** ❌ Missing

**Required Test Cases:**
- Dashboard revenue calculation
- Dashboard order counts
- Dashboard user counts
- Dashboard recent orders
- Dashboard top products

#### GET /admin/analytics
**Test Coverage:** 0%
**Status:** ❌ Missing

**Required Test Cases:**
- Analytics aggregation
- Date range filtering
- Real-time updates
- Export analytics data

---

## API Test Requirements

### Request Validation Tests

For each API endpoint, test:
- Required field validation
- Data type validation
- Format validation (email, phone, etc.)
- Length validation
- Range validation (numeric values)
- Enum validation (status, role, etc.)

### Response Validation Tests

For each API endpoint, test:
- Success response structure
- Error response structure
- HTTP status codes
- Response headers
- Response time performance
- Response data accuracy

### Authentication Tests

For protected endpoints, test:
- Request without authentication (401)
- Request with invalid token (401)
- Request with expired token (401)
- Request with valid token (200/201)
- Token refresh mechanism

### Authorization Tests

For role-based endpoints, test:
- Regular user accessing admin resource (403)
- Admin accessing user resource (200)
- User accessing own resource (200)
- User accessing another user's resource (403)

### Error Handling Tests

For each API endpoint, test:
- Invalid input (400)
- Resource not found (404)
- Method not allowed (405)
- Server error (500)
- Rate limiting (429)
- Service unavailable (503)

### Rate Limiting Tests

For rate-limited endpoints, test:
- Normal request succeeds
- Requests within limit succeed
- Request at limit succeeds
- Request over limit fails (429)
- Retry-After header present

### CORS Tests

For API endpoints, test:
- OPTIONS preflight request
- Access-Control-Allow-Origin header
- Access-Control-Allow-Methods header
- Access-Control-Allow-Headers header
- Credentials handling

---

## API Test Automation Strategy

### Test Framework: REST Assured (Java)

```java
// Example API Test
@Test
public void testAddToCartSuccess() {
    given()
        .auth().oauth2(validToken)
        .contentType("application/json")
        .body("{\"productId\": 1, \"quantity\": 2, \"size\": \"M\"}")
    .when()
        .post("/api/cart/add")
    .then()
        .statusCode(200)
        .body("success", equalTo(true))
        .body("message", equalTo("Item added to cart"))
        .body("cartCount", equalTo(1));
}
```

### Test Framework: Playwright (JavaScript)

```javascript
// Example API Test
test('Add to cart API', async ({ request }) => {
  const response = await request.post('/api/cart/add', {
    data: {
      productId: 1,
      quantity: 2,
      size: 'M'
    },
    headers: {
      'Authorization': `Bearer ${validToken}`
    }
  });
  
  expect(response.ok()).toBeTruthy();
  const data = await response.json();
  expect(data.success).toBe(true);
  expect(data.cartCount).toBe(1);
});
```

---

## API Test Coverage Goals

### Short-term Goals (1-2 weeks)
- Achieve 80% coverage for critical payment APIs
- Achieve 70% coverage for cart APIs
- Add tests for missing search/filter APIs
- Add tests for admin dashboard APIs

### Medium-term Goals (1 month)
- Achieve 90% coverage for all authentication APIs
- Achieve 80% coverage for all order APIs
- Achieve 70% coverage for all admin APIs
- Implement API contract testing

### Long-term Goals (3 months)
- Achieve 85% overall API coverage
- Implement API performance monitoring
- Implement API security scanning
- Implement API chaos testing
