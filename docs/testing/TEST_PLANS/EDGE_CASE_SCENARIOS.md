# Edge-Case Scenarios

## Input Validation Edge Cases

### EDGE-VAL-001: Empty Email Field
**Scenario:** User submits registration with empty email
**Expected Behavior:** Validation error, "Email is required"
**Test Steps:**
1. Navigate to registration
2. Leave email field empty
3. Submit form
4. Verify error message displayed
5. Verify registration not completed

---

### EDGE-VAL-002: Email with Trailing Spaces
**Scenario:** User enters email with leading/trailing whitespace
**Expected Behavior:** Email trimmed and processed correctly
**Test Steps:**
1. Enter "  test@example.com  " in email field
2. Submit registration
3. Verify email stored as "test@example.com"
4. Verify login works with trimmed email

---

### EDGE-VAL-003: Email with Special Characters
**Scenario:** Email contains +, ., - in local part
**Expected Behavior:** Valid email format accepted
**Test Steps:**
1. Enter "user+tag@example.com"
2. Submit registration
3. Verify email accepted
4. Verify email stored correctly

---

### EDGE-VAL-004: Password with Only Numbers
**Scenario:** Password "12345678"
**Expected Behavior:** Rejected as weak password
**Test Steps:**
1. Enter "12345678" as password
2. Submit registration
3. Verify error: "Password must contain letters"
4. Verify registration not completed

---

### EDGE-VAL-005: Password with Only Letters
**Scenario:** Password "password"
**Expected Behavior:** Rejected as weak password
**Test Steps:**
1. Enter "password" as password
2. Submit registration
3. Verify error: "Password must contain numbers"
4. Verify registration not completed

---

### EDGE-VAL-006: Password with Special Characters Only
**Scenario:** Password "!@#$%^&*"
**Expected Behavior:** Rejected as weak password
**Test Steps:**
1. Enter "!@#$%^&*" as password
2. Submit registration
3. Verify error: "Password must contain letters and numbers"
4. Verify registration not completed

---

### EDGE-VAL-007: Phone with Letters
**Scenario:** Phone number "abc123def"
**Expected Behavior:** Rejected as invalid format
**Test Steps:**
1. Enter "abc123def" as phone
2. Submit registration
3. Verify error: "Invalid phone format"
4. Verify registration not completed

---

### EDGE-VAL-008: Phone with Special Characters
**Scenario:** Phone numbers with various formats
**Expected Behavior:** Valid formats accepted, invalid rejected
**Test Steps:**
1. Test "123-456-7890" (valid)
2. Test "(123) 456-7890" (valid)
3. Test "123@456" (invalid)
4. Verify appropriate validation

---

### EDGE-VAL-009: Negative Price
**Scenario:** Product price -10.00
**Expected Behavior:** Rejected, price must be positive
**Test Steps:**
1. Admin enters -10.00 as product price
2. Submit product form
3. Verify error: "Price must be positive"
4. Verify product not created

---

### EDGE-VAL-010: Zero Price
**Scenario:** Product price 0.00
**Expected Behavior:** May be allowed (free product) or rejected
**Test Steps:**
1. Admin enters 0.00 as product price
2. Submit product form
3. Verify system behavior (allow or reject based on requirements)
4. Document expected behavior

---

### EDGE-VAL-011: Very Large Price
**Scenario:** Product price 999999999.99
**Expected Behavior:** May exceed database limits or be rejected
**Test Steps:**
1. Admin enters 999999999.99 as product price
2. Submit product form
3. Verify error or success
4. Check database column limits

---

### EDGE-VAL-012: Negative Quantity
**Scenario:** Cart quantity -1
**Expected Behavior:** Rejected, quantity must be positive
**Test Steps:**
1. User enters -1 in quantity field
2. Click update cart
3. Verify error: "Quantity must be positive"
4. Verify cart not updated

---

### EDGE-VAL-013: Zero Quantity
**Scenario:** Cart quantity 0
**Expected Behavior:** Should remove item from cart
**Test Steps:**
1. User enters 0 in quantity field
2. Click update cart
3. Verify item removed from cart
4. Verify cart count decreased

---

### EDGE-VAL-014: Very Large Quantity
**Scenario:** Cart quantity 999999
**Expected Behavior:** Should be limited or rejected
**Test Steps:**
1. User enters 999999 in quantity field
2. Click update cart
3. Verify error or limit enforced
4. Verify reasonable maximum (e.g., 100)

---

### EDGE-VAL-015: Empty Name
**Scenario:** User name with only spaces
**Expected Behavior:** Rejected, name cannot be empty
**Test Steps:**
1. Enter "   " in name field
2. Submit profile update
3. Verify error: "Name cannot be empty"
4. Verify profile not updated

---

### EDGE-VAL-016: Very Long Name
**Scenario:** User name with 500+ characters
**Expected Behavior:** Rejected, exceeds maximum length
**Test Steps:**
1. Enter 500 characters in name field
2. Submit profile update
3. Verify error: "Name too long"
4. Verify profile not updated

---

### EDGE-VAL-017: Invalid Date
**Scenario:** Order date in the past or future
**Expected Behavior:** Should use current date, reject invalid dates
**Test Steps:**
1. Attempt to create order with past date
2. Verify system uses current date
3. Attempt to create order with future date
4. Verify system uses current date

---

### EDGE-VAL-018: Invalid Coupon Code
**Scenario:** Coupon with special characters
**Expected Behavior:** Rejected as invalid format
**Test Steps:**
1. Enter "COUPON@#$%" as coupon code
2. Apply coupon
3. Verify error: "Invalid coupon code"
4. Verify coupon not applied

---

### EDGE-VAL-019: Expired Coupon
**Scenario:** Coupon past expiration date
**Expected Behavior:** Rejected as expired
**Test Steps:**
1. Create coupon with past expiration date
2. Attempt to apply coupon
3. Verify error: "Coupon expired"
4. Verify coupon not applied

---

## Boundary Conditions

### EDGE-BND-001: Minimum Cart Value
**Scenario:** Cart with 0.01 total
**Expected Behavior:** Should be allowed (minimum purchase)
**Test Steps:**
1. Add item with 0.01 price to cart
2. Attempt checkout
3. Verify checkout allowed or minimum enforced
4. Document minimum purchase requirement

---

### EDGE-BND-002: Maximum Cart Value
**Scenario:** Cart with max allowed total
**Expected Behavior:** Should be allowed or rejected based on limits
**Test Steps:**
1. Add items to reach maximum cart value
2. Attempt checkout
3. Verify behavior (allow or reject)
4. Document maximum cart limit

---

### EDGE-BND-003: Minimum Items in Cart
**Scenario:** 1 item in cart
**Expected Behavior:** Should be allowed
**Test Steps:**
1. Add 1 item to cart
2. Attempt checkout
3. Verify checkout succeeds

---

### EDGE-BND-004: Maximum Items in Cart
**Scenario:** 100+ items in cart
**Expected Behavior:** Should be limited or rejected
**Test Steps:**
1. Add 100 items to cart
2. Attempt checkout
3. Verify error or limit enforced
4. Document maximum items limit

---

### EDGE-BND-005: Stock at Zero
**Scenario:** Product with 0 stock
**Expected Behavior:** Should not be purchasable
**Test Steps:**
1. Set product stock to 0
2. Navigate to product page
3. Verify "Out of Stock" displayed
4. Verify add to cart disabled

---

### EDGE-BND-006: Stock at One
**Scenario:** Product with 1 stock
**Expected Behavior:** Should be purchasable, then out of stock
**Test Steps:**
1. Set product stock to 1
2. Add to cart
3. Complete purchase
4. Verify stock now 0
5. Verify product shows out of stock

---

### EDGE-BND-007: Large Order
**Scenario:** Order with 50+ items
**Expected Behavior:** Should be allowed but may have performance impact
**Test Steps:**
1. Add 50 items to cart
2. Complete checkout
3. Verify order created
4. Verify performance acceptable
5. Verify all items in order

---

### EDGE-BND-008: Small Order
**Scenario:** Order with 1 item
**Expected Behavior:** Should be allowed
**Test Steps:**
1. Add 1 item to cart
2. Complete checkout
3. Verify order created
4. Verify 1 item in order

---

### EDGE-BND-009: Pagination Limits
**Scenario:** Page 1, last page, invalid page
**Expected Behavior:** Handle gracefully
**Test Steps:**
1. Navigate to page 1
2. Navigate to last page
3. Attempt page 999999
4. Verify invalid page handled
5. Verify no errors

---

### EDGE-BND-010: Rate Limit Boundary
**Scenario:** Exactly at rate limit, just above
**Expected Behavior:** At limit allowed, above blocked
**Test Steps:**
1. Make requests up to rate limit
2. Verify all succeed
3. Make one more request
4. Verify rate limited (429)
5. Verify Retry-After header

---

## Error State Edge Cases

### EDGE-ERR-001: Network Timeout During Checkout
**Scenario:** Simulate network timeout during checkout
**Expected Behavior:** Graceful error handling, cart preserved
**Test Steps:**
1. Add items to cart
2. Simulate network timeout during payment
3. Verify error message displayed
4. Verify cart items preserved
5. Verify user can retry

---

### EDGE-ERR-002: Database Connection Failure
**Scenario:** Simulate DB unavailability
**Expected Behavior:** Graceful degradation, error message
**Test Steps:**
1. Stop database
2. Attempt login
3. Verify error message displayed
4. Verify no sensitive info leaked
5. Restart database
6. Verify recovery

---

### EDGE-ERR-003: Payment Gateway Down
**Scenario:** Simulate Stripe unavailable
**Expected Behavior:** Error message, payment not processed
**Test Steps:**
1. Simulate Stripe downtime
2. Attempt payment
3. Verify error message displayed
4. Verify order not created
5. Verify user can retry

---

### EDGE-ERR-004: Cache Failure
**Scenario:** Simulate Redis unavailable
**Expected Behavior:** Fall back to database, performance degraded
**Test Steps:**
1. Stop Redis
2. Attempt product search
3. Verify results from database
4. Verify performance degraded but functional
5. Restart Redis
6. Verify cache repopulated

---

### EDGE-ERR-005: Concurrent Order Creation
**Scenario:** Two users buy last item simultaneously
**Expected Behavior:** One succeeds, one fails gracefully
**Test Steps:**
1. Set product stock to 1
2. Two users add to cart simultaneously
3. Both attempt checkout simultaneously
4. Verify one succeeds
5. Verify one fails with appropriate error
6. Verify stock not negative

---

### EDGE-ERR-006: Session Expired During Checkout
**Scenario:** Session timeout mid-checkout
**Expected Behavior:** Redirect to login, cart preserved
**Test Steps:**
1. Add items to cart
2. Start checkout
3. Invalidate session
4. Attempt to complete checkout
5. Verify redirect to login
6. Re-login
7. Verify cart preserved

---

### EDGE-ERR-007: CSRF Token Expired
**Scenario:** Use expired CSRF token
**Expected Behavior:** Request blocked, new token issued
**Test Steps:**
1. Login and get CSRF token
2. Wait for token expiration
3. Submit form with expired token
4. Verify request blocked (403)
5. Verify new token issued
6. Retry with new token succeeds

---

### EDGE-ERR-008: Invalid CSRF Token
**Scenario:** Use tampered CSRF token
**Expected Behavior:** Request blocked
**Test Steps:**
1. Login and get CSRF token
2. Tamper with token
3. Submit form with tampered token
4. Verify request blocked (403)
5. Verify security log entry

---

### EDGE-ERR-009: Payment Webhook Missing
**Scenario:** Order created but webhook never received
**Expected Behavior:** Order remains in pending state, manual reconciliation
**Test Steps:**
1. Create order
2. Simulate payment success
3. Block webhook delivery
4. Verify order status remains pending
5. Verify manual reconciliation possible

---

### EDGE-ERR-010: Duplicate Webhook
**Scenario:** Payment webhook received twice
**Expected Behavior:** Idempotent handling, no duplicate processing
**Test Steps:**
1. Create order
2. Send payment webhook
3. Verify order status updated
4. Send same webhook again
5. Verify no duplicate processing
6. Verify order status unchanged

---

## Data Consistency Edge Cases

### EDGE-DATA-001: Cart vs Database Mismatch
**Scenario:** Cart in session doesn't match database
**Expected Behavior:** Reconcile with database as source of truth
**Test Steps:**
1. Add items to cart in session
2. Manually modify database cart
3. Refresh cart page
4. Verify cart reconciled with database
5. Verify no data loss

---

### EDGE-DATA-002: Order Items vs Cart Mismatch
**Scenario:** Order items don't match cart items
**Expected Behavior:** Use cart items as source of truth
**Test Steps:**
1. Add items to cart
2. Create order
3. Manually modify order items in database
4. Verify order uses cart items
5. Verify consistency

---

### EDGE-DATA-003: Payment Amount Mismatch
**Scenario:** Payment amount doesn't match order total
**Expected Behavior:** Reject payment, flag for review
**Test Steps:**
1. Create order with total $100
2. Simulate payment for $90
3. Verify payment rejected
4. Verify order flagged for review
5. Verify error logged

---

### EDGE-DATA-004: Stock Inconsistency
**Scenario:** Stock shows available but actually sold
**Expected Behavior:** Handle at payment time, refund if necessary
**Test Steps:**
1. Set stock to 10
2. Create 10 orders simultaneously
3. Verify some orders fail
4. Verify failed orders refunded
5. Verify stock not negative

---

### EDGE-DATA-005: Address Not Found
**Scenario:** Order references deleted address
**Expected Behavior:** Handle gracefully, use fallback or flag
**Test Steps:**
1. Create order with address
2. Delete address from database
3. View order details
4. Verify graceful handling
5. Verify error not exposed to user

---

### EDGE-DATA-006: User Not Found
**Scenario:** Order references deleted user
**Expected Behavior:** Handle gracefully, preserve order data
**Test Steps:**
1. Create order
2. Delete user from database
3. View order details
4. Verify order data preserved
5. Verify user shown as "deleted"

---

### EDGE-DATA-007: Coupon Not Found
**Scenario:** Order references deleted coupon
**Expected Behavior:** Handle gracefully, recalculate total
**Test Steps:**
1. Create order with coupon
2. Delete coupon from database
3. View order details
4. Verify order preserved
5. Verify discount shown as "expired"

---

### EDGE-DATA-008: Orphaned Cart Items
**Scenario:** Cart items without product reference
**Expected Behavior:** Clean up or flag for review
**Test Steps:**
1. Add item to cart
2. Delete product from database
3. View cart
4. Verify orphaned item handled
5. Verify item removed or flagged

---

## Unicode and Internationalization Edge Cases

### EDGE-UNI-001: Non-Latin Characters in Name
**Scenario:** User name with Chinese, Arabic, Cyrillic characters
**Expected Behavior:** Accepted and stored correctly
**Test Steps:**
1. Enter "张三" (Chinese) in name field
2. Submit profile update
3. Verify name stored correctly
4. Verify name displayed correctly

---

### EDGE-UNI-002: RTL Languages
**Scenario:** Interface in Arabic (right-to-left)
**Expected Behavior:** Layout adjusted for RTL
**Test Steps:**
1. Set language to Arabic
2. Verify layout RTL
3. Verify text direction correct
4. Verify forms work correctly

---

### EDGE-UNI-003: Emoji in User Input
**Scenario:** User enters emoji in review or profile
**Expected Behavior:** Accepted and displayed correctly
**Test Steps:**
1. Enter "Great product! 👍" in review
2. Submit review
3. Verify emoji stored correctly
4. Verify emoji displayed correctly

---

### EDGE-UNI-004: Very Long Unicode String
**Scenario:** Very long string with multi-byte characters
**Expected Behavior:** Handled correctly, no truncation issues
**Test Steps:**
1. Enter 500 characters of Chinese text
2. Submit form
3. Verify stored correctly
4. Verify displayed correctly

---

### EDGE-UNI-005: Mixed Scripts
**Scenario:** Name with mixed Latin and non-Latin characters
**Expected Behavior:** Accepted and stored correctly
**Test Steps:**
1. Enter "José García" in name field
2. Submit profile update
3. Verify name stored correctly
4. Verify name displayed correctly

---

## Time and Date Edge Cases

### EDGE-TIME-001: Leap Year February 29
**Scenario:** Date calculation on leap year
**Expected Behavior:** Correct date handling
**Test Steps:**
1. Set date to February 29, 2024
2. Verify date accepted
3. Verify calculations correct
4. Verify display correct

---

### EDGE-TIME-002: Daylight Saving Time Transition
**Scenario:** Order during DST transition
**Expected Behavior:** Correct time handling
**Test Steps:**
1. Create order during DST transition
2. Verify timestamp correct
3. Verify display correct
4. Verify calculations correct

---

### EDGE-TIME-003: Timezone Differences
**Scenario:** User in different timezone than server
**Expected Behavior:** Display in user's timezone
**Test Steps:**
1. Set user timezone to UTC+5:30
2. Create order
3. Verify display in user's timezone
4. Verify storage in UTC

---

### EDGE-TIME-004: Year 2038 Problem
**Scenario:** Dates beyond 32-bit timestamp limit
**Expected Behavior:** Handle correctly if using 64-bit
**Test Steps:**
1. Set date to January 1, 2040
2. Verify date handled correctly
3. Verify no overflow errors

---

### EDGE-TIME-005: Negative Timestamps
**Scenario:** Dates before Unix epoch (1970)
**Expected Behavior:** Handle correctly if needed
**Test Steps:**
1. Set date to January 1, 1960 (if applicable)
2. Verify date handled correctly
3. Verify no errors

---

## File Upload Edge Cases

### EDGE-FILE-001: Very Large File Upload
**Scenario:** Upload 100MB image
**Expected Behavior:** Rejected or limited
**Test Steps:**
1. Attempt to upload 100MB image
2. Verify error or limit enforced
3. Document maximum file size

---

### EDGE-FILE-002: Invalid File Type
**Scenario:** Upload executable (.exe)
**Expected Behavior:** Rejected
**Test Steps:**
1. Attempt to upload .exe file
2. Verify error: "Invalid file type"
3. Verify upload rejected

---

### EDGE-FILE-003: Malicious File
**Scenario:** Upload file with malicious content
**Expected Behavior:** Scanned and rejected
**Test Steps:**
1. Attempt to upload EICAR test file
2. Verify virus scan
3. Verify file rejected if malicious

---

### EDGE-FILE-004: Corrupted Image
**Scenario:** Upload corrupted image file
**Expected Behavior:** Rejected or handled gracefully
**Test Steps:**
1. Upload corrupted image file
2. Verify error or graceful handling
3. Verify no crash

---

### EDGE-FILE-005: Filename with Special Characters
**Scenario:** Upload file with special characters in name
**Expected Behavior:** Sanitized filename
**Test Steps:**
1. Upload file named "image@#$%.jpg"
2. Verify filename sanitized
3. Verify file stored correctly
