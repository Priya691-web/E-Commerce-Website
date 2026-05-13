# FashionStore - Workflow Diagrams

## Table of Contents
1. [User Workflows](#user-workflows)
2. [Admin Workflows](#admin-workflows)
3. [System Workflows](#system-workflows)
4. [Data Workflows](#data-workflows)

---

## User Workflows

### Customer Registration Workflow

```mermaid
sequenceDiagram
    participant User as Customer
    participant Browser as Browser
    participant Register as RegisterController
    participant Service as UserService
    participant DAO as UserDAO
    participant DB as Database
    participant Session as HTTP Session
    
    User->>Browser: Navigate to /register
    Browser->>Register: GET /register
    Register->>Browser: Render Registration Form
    Browser->>User: Display Form
    
    User->>Browser: Fill form and submit
    Browser->>Register: POST /register (form data)
    Register->>Service: registerUser(user)
    Service->>Service: Validate email format
    Service->>Service: Validate password strength
    Service->>Service: Hash password (BCrypt)
    Service->>DAO: createUser(user)
    DAO->>DB: INSERT INTO users
    DB-->>DAO: Success
    DAO-->>Service: Success
    Service->>Session: Create session
    Service->>Session: Set user attribute
    Service->>Session: Generate CSRF token
    Service-->>Register: Success
    Register->>Browser: Redirect to /home
    Browser->>User: Display Homepage
```

### Customer Login Workflow

```mermaid
sequenceDiagram
    participant User as Customer
    participant Browser as Browser
    participant Login as LoginController
    participant Service as UserService
    participant DAO as UserDAO
    participant DB as Database
    participant Session as HTTP Session
    
    User->>Browser: Navigate to /login
    Browser->>Login: GET /login
    Login->>Browser: Render Login Form
    Browser->>User: Display Form
    
    User->>Browser: Enter credentials and submit
    Browser->>Login: POST /login (email, password)
    Login->>Service: loginUser(email, password)
    Service->>DAO: getUserByEmail(email)
    DAO->>DB: SELECT * FROM users WHERE email = ?
    DB-->>DAO: User Object
    DAO-->>Service: User Object
    Service->>Service: Check if user exists
    Service->>Service: Check if user is active
    Service->>Service: Verify password (BCrypt)
    
    alt Authentication Successful
        Service->>Session: Create session
        Service->>Session: Set user attribute
        Service->>Session: Generate CSRF token
        Service->>Session: Regenerate session ID
        Service-->>Login: Authenticated User
        Login->>Browser: Redirect to /home
        Browser->>User: Display Homepage
    else Authentication Failed
        Service-->>Login: null
        Login->>Browser: Render login with error
        Browser->>User: Display error message
    end
```

### Product Browsing Workflow

```mermaid
sequenceDiagram
    participant User as Customer
    participant Browser as Browser
    participant Product as ProductController
    participant Service as ProductService
    participant Cache as CacheService
    participant DAO as ProductDAO
    participant DB as Database
    
    User->>Browser: Navigate to /products
    Browser->>Product: GET /products?category=men&sortBy=price
    Product->>Service: getProductsByCategory(category, sortBy)
    Service->>Cache: get(cacheKey)
    
    alt Cache Hit
        Cache-->>Service: Cached products
    else Cache Miss
        Service->>DAO: getProductsByCategory(category, sortBy)
        DAO->>DB: SELECT * FROM products WHERE category = ?
        DB-->>DAO: Product List
        DAO-->>Service: Product List
        Service->>Service: Batch load sizes
        Service->>Cache: put(cacheKey, products)
    end
    
    Service-->>Product: Product List
    Product->>Product: Set request attributes
    Product->>Browser: Render products.jsp
    Browser->>User: Display product listing
```

### Add to Cart Workflow

```mermaid
sequenceDiagram
    participant User as Customer
    participant Browser as Browser
    participant Cart as CartController
    participant Service as CartService
    participant DAO as CartDAO
    participant DB as Database
    participant Session as HTTP Session
    
    User->>Browser: Click "Add to Cart" on product page
    Browser->>Browser: Execute addToCart(productId, size, quantity)
    Browser->>Cart: POST /cart (productId, size, quantity)
    Cart->>Session: Validate session
    Cart->>Service: addToCart(userId, productId, size, quantity)
    Service->>DAO: getCartItem(userId, productId, size)
    DAO->>DB: SELECT * FROM cart_items WHERE ...
    DB-->>DAO: CartItem or null
    DAO-->>Service: CartItem or null
    
    alt Item exists in cart
        Service->>DAO: updateCartItem(cartItemId, newQuantity)
        DAO->>DB: UPDATE cart_items SET quantity = ?
    else Item not in cart
        Service->>DAO: createCartItem(userId, productId, size, quantity)
        DAO->>DB: INSERT INTO cart_items
    end
    
    DB-->>DAO: Success
    DAO-->>Service: Success
    Service-->>Cart: Success
    Cart->>Browser: JSON response
    Browser->>Browser: Update cart count
    Browser->>User: Show "Added to cart" toast
```

### Checkout Workflow

```mermaid
sequenceDiagram
    participant User as Customer
    participant Browser as Browser
    participant Checkout as CheckoutController
    participant Cart as CartService
    participant Order as OrderService
    participant Payment as PaymentService
    participant Stripe as Stripe API
    participant DB as Database
    
    User->>Browser: Navigate to /checkout
    Browser->>Checkout: GET /checkout
    Checkout->>Cart: getCartItems(userId)
    Cart->>DB: SELECT * FROM cart_items WHERE user_id = ?
    DB-->>Cart: Cart Items
    Cart-->>Checkout: Cart Items
    Checkout->>Checkout: Load addresses
    Checkout->>Browser: Render checkout.jsp
    Browser->>User: Display checkout page
    
    User->>Browser: Select address and payment method
    User->>Browser: Click "Place Order"
    Browser->>Checkout: POST /checkout (addressId, paymentMethod)
    Checkout->>Order: createOrder(order)
    Order->>Order: Calculate totals
    Order->>Order: Apply coupon discount
    Order->>DB: INSERT INTO orders
    Order->>DB: INSERT INTO order_items
    Order-->>Checkout: Order Object
    
    alt Card Payment
        Checkout->>Payment: processCardPayment(order)
        Payment->>Stripe: Create payment intent
        Stripe-->>Payment: Payment success
        Payment-->>Checkout: Payment success
    else UPI Payment
        Checkout->>Payment: processUPIPayment(order)
        Payment-->>Checkout: Payment success
    else COD
        Checkout->>Order: markAsPending(order)
    end
    
    Checkout->>Order: confirmOrder(orderId)
    Checkout->>Cart: clearCart(userId)
    Cart->>DB: DELETE FROM cart_items WHERE user_id = ?
    Checkout->>Browser: Redirect to /success
    Browser->>User: Display order confirmation
```

---

## Admin Workflows

### Admin Login Workflow

```mermaid
sequenceDiagram
    participant Admin as Admin User
    participant React as React App
    participant API as AdminApiController
    participant Service as UserService
    participant Session as HTTP Session
    
    Admin->>React: Navigate to /login
    React->>React: Render login form
    Admin->>React: Enter credentials
    React->>API: POST /api/admin/login (JSON)
    API->>Service: loginUser(email, password)
    Service->>Service: Verify credentials
    Service->>Session: Create session
    Service->>Session: Set user attribute
    Service-->>API: User Object
    API->>React: JSON response {user, token}
    React->>React: Store user in context
    React->>React: Redirect to /dashboard
    React->>Admin: Display dashboard
```

### Product Creation Workflow

```mermaid
sequenceDiagram
    participant Admin as Admin User
    participant React as React App
    participant API as AdminApiController
    participant Service as ProductService
    participant DAO as ProductDAO
    participant Cache as CacheService
    participant DB as Database
    
    Admin->>React: Navigate to /products/new
    React->>React: Render ProductForm
    Admin->>React: Fill product details
    Admin->>React: Upload image
    React->>React: Upload to server
    Admin->>React: Click "Save Product"
    React->>API: POST /api/admin/products (JSON)
    API->>Service: createProduct(product)
    Service->>DAO: createProduct(product)
    DAO->>DB: INSERT INTO products
    DB-->>DAO: Product ID
    DAO-->>Service: Product Object
    Service->>DAO: createProductSizes(productSizes)
    DAO->>DB: INSERT INTO product_sizes
    Service->>Cache: invalidatePattern("products:*")
    Service->>Cache: invalidatePattern("featured:*")
    Service-->>API: Success
    API->>React: JSON response {success: true}
    React->>React: Show success toast
    React->>React: Redirect to /products
```

### Order Status Update Workflow

```mermaid
sequenceDiagram
    participant Admin as Admin User
    participant React as React App
    participant API as AdminApiController
    participant Service as OrderService
    participant DAO as OrderDAO
    participant Cache as CacheService
    participant Email as EmailService
    participant DB as Database
    
    Admin->>React: Navigate to /orders
    React->>API: GET /api/admin/orders
    API->>Service: getOrders()
    Service->>DAO: getOrders()
    DAO->>DB: SELECT * FROM orders
    DB-->>DAO: Order List
    DAO-->>Service: Order List
    Service-->>API: Order List
    API-->>React: JSON response
    React->>Admin: Display order table
    
    Admin->>React: Click order action menu
    Admin->>React: Select "Mark as Shipped"
    React->>API: PUT /api/admin/orders/{id}/status (JSON)
    API->>Service: updateOrderStatus(orderId, "Shipped")
    Service->>DAO: updateOrderStatus(orderId, "Shipped")
    DAO->>DB: UPDATE orders SET status = 'Shipped'
    DB-->>DAO: Success
    DAO-->>Service: Success
    Service->>Email: sendShippingNotificationEmail(order)
    Service-->>API: Success
    API->>React: JSON response {success: true}
    React->>React: Show success toast
    React->>React: Refresh order list
```

---

## System Workflows

### Request Processing Workflow

```mermaid
graph TB
    START[HTTP Request] --> FILTER1[CORS Filter]
    FILTER1 --> FILTER2[Security Headers Filter]
    FILTER2 --> FILTER3[Request Logging Filter]
    FILTER3 --> FILTER4[Auth Filter]
    FILTER4 --> AUTH{Authenticated?}
    AUTH -->|No| LOGIN[Redirect to Login]
    AUTH -->|Yes| AUTHZ{Authorized?}
    AUTHZ -->|No| FORBIDDEN[403 Forbidden]
    AUTHZ -->|Yes| FILTER5[CSRF Filter]
    FILTER5 --> CSRF{Valid CSRF?}
    CSRF -->|No| CSRF_ERROR[403 CSRF Error]
    CSRF -->|Yes| SERVLET[Servlet Controller]
    SERVLET --> SERVICE[Service Layer]
    SERVICE --> DAO[DAO Layer]
    DAO --> CACHE[Cache Check]
    CACHE --> CACHE_HIT{Cache Hit?}
    CACHE_HIT -->|Yes| RETURN[Return Cached Data]
    CACHE_HIT -->|No| DB[Database Query]
    DB --> CACHE_STORE[Store in Cache]
    CACHE_STORE --> RETURN
    RETURN --> SERVICE
    SERVICE --> SERVLET
    SERVLET --> RESPONSE[HTTP Response]
```

### Cache Invalidation Workflow

```mermaid
graph TB
    START[Data Update] --> SERVICE[Service Layer]
    SERVICE --> DAO[DAO Layer]
    DAO --> DB[Database Update]
    DB --> SUCCESS{Update Success?}
    SUCCESS -->|Yes| INVALIDATE[Cache Invalidation]
    SUCCESS -->|No| ERROR[Return Error]
    INVALIDATE --> PATTERN[Pattern-based Invalidation]
    PATTERN --> REDIS[Redis Cache]
    PATTERN --> LOCAL[Local Cache]
    REDIS --> REDIS_DELETE[Delete matching keys]
    LOCAL --> LOCAL_DELETE[Delete matching keys]
    REDIS_DELETE --> COMPLETE[Invalidation Complete]
    LOCAL_DELETE --> COMPLETE
    COMPLETE --> RETURN[Return Success]
```

### Error Handling Workflow

```mermaid
graph TB
    START[Request] --> TRY[Try Block]
    TRY --> OPERATION[Operation]
    OPERATION --> SUCCESS{Success?}
    SUCCESS -->|Yes| RESPONSE[Return Response]
    SUCCESS -->|No| CATCH[Catch Block]
    CATCH --> ERROR_TYPE{Error Type}
    ERROR_TYPE -->|SQL Exception| SQL_ERROR[Log SQL Error]
    ERROR_TYPE -->|IO Exception| IO_ERROR[Log IO Error]
    ERROR_TYPE -->|Runtime Exception| RUNTIME_ERROR[Log Runtime Error]
    SQL_ERROR --> USER_ERROR[User-friendly message]
    IO_ERROR --> USER_ERROR
    RUNTIME_ERROR --> USER_ERROR
    USER_ERROR --> ERROR_RESPONSE[Return Error Response]
    ERROR_RESPONSE --> LOG[Log Error]
    LOG --> NOTIFY[Notify Admin]
    NOTIFY --> END[End]
```

---

## Data Workflows

### Product Data Flow

```mermaid
graph LR
    A[Product Creation] --> B[Product Service]
    B --> C[Product DAO]
    C --> D[Database]
    D --> E[Cache Invalidation]
    E --> F[Redis Cache]
    E --> G[Local Cache]
    F --> H[Cache Cleared]
    G --> H
    H --> I[Next Request]
    I --> J[Cache Check]
    J --> K[Cache Miss]
    K --> L[Database Query]
    L --> M[Cache Store]
    M --> N[Return Data]
```

### Order Data Flow

```mermaid
graph LR
    A[Checkout Request] --> B[Order Service]
    B --> C[Create Order]
    C --> D[Database]
    D --> E[Create Order Items]
    E --> F[Database]
    F --> G[Process Payment]
    G --> H[Stripe API]
    H --> I[Payment Result]
    I --> J{Payment Success?}
    J -->|Yes| K[Confirm Order]
    J -->|No| L[Cancel Order]
    K --> M[Clear Cart]
    L --> N[Restore Cart]
    M --> O[Send Confirmation Email]
    N --> P[Show Payment Error]
    O --> Q[Order Confirmation]
```

### User Session Flow

```mermaid
graph TB
    A[Login Request] --> B[User Service]
    B --> C[Verify Credentials]
    C --> D{Valid?}
    D -->|Yes| E[Create Session]
    D -->|No| F[Return Error]
    E --> G[Set User Attribute]
    G --> H[Generate CSRF Token]
    H --> I[Regenerate Session ID]
    I --> J[Set HttpOnly Cookie]
    J --> K[Return Success]
    
    L[Subsequent Request] --> M[Auth Filter]
    M --> N[Validate Session]
    N --> O{Valid Session?}
    O -->|Yes| P[Process Request]
    O -->|No| Q[Redirect to Login]
    
    R[Logout Request] --> S[Invalidate Session]
    S --> T[Clear Session Attributes]
    T --> U[Redirect to Login]
```

---

## Conclusion

The workflow diagrams provided in this document illustrate the key processes and data flows within the FashionStore e-commerce platform. These diagrams serve as valuable references for understanding:

- **User Workflows**: Customer registration, login, product browsing, cart management, and checkout processes
- **Admin Workflows**: Admin authentication, product management, and order processing
- **System Workflows**: Request processing, cache invalidation, and error handling
- **Data Workflows**: Product data flow, order data flow, and session management

These workflows demonstrate the system's design patterns, security measures, and data integrity mechanisms, providing a comprehensive view of how the FashionStore platform operates.
