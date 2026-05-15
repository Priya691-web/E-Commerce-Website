# FashionStore - Software Architecture Design Document

## Document Information
- **Project**: FashionStore E-commerce Platform
- **Document Type**: Software Architecture Design
- **Version**: 1.0
- **Date**: May 15, 2026
- **Author**: Architecture Team
- **Status**: Production-Ready

---

## 1. Executive Summary

FashionStore is a Java-based Model-View-Controller (MVC) e-commerce platform built on Jakarta EE technology stack. The system follows a layered architecture pattern with clear separation of concerns across presentation, business logic, and data access layers. The architecture prioritizes scalability, maintainability, and security while maintaining performance through caching and connection pooling.

### 1.1 Architectural Goals
- **Scalability**: Horizontal scaling capability through stateless design
- **Maintainability**: Clean separation of concerns and modular design
- **Security**: Multi-layer security with authentication, authorization, and data protection
- **Performance**: Connection pooling, caching, and optimized database queries
- **Reliability**: Error handling, transaction management, and data integrity

---

## 2. System Architecture Overview

### 2.1 Architectural Pattern
**Pattern**: Layered MVC Architecture with Data Access Object (DAO) Pattern

The system follows a classic three-tier architecture with additional layers for data access and business logic:

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                    │
│              (JSP Views, Controllers)                   │
├─────────────────────────────────────────────────────────┤
│                  Business Logic Layer                   │
│              (Services, Business Rules)                │
├─────────────────────────────────────────────────────────┤
│                   Data Access Layer                      │
│              (DAO Interfaces, Implementations)           │
├─────────────────────────────────────────────────────────┤
│                   Data Persistence Layer                 │
│              (MySQL Database, Redis Cache)               │
└─────────────────────────────────────────────────────────┘
```

### 2.2 Technology Stack

| Layer | Technology | Version | Purpose |
|-------|-----------|---------|---------|
| **Presentation** | Jakarta Servlet | 6.0.0 | Web request handling |
| **Presentation** | JSP | 3.1.0 | Dynamic content rendering |
| **Presentation** | JSTL | 2.0.0 | Template logic |
| **Business Logic** | Java SE | 21 | Core application logic |
| **Business Logic** | Gson | 2.10.1 | JSON processing |
| **Data Access** | MySQL Connector | 8.3.0 | Database connectivity |
| **Data Access** | HikariCP | 5.1.0 | Connection pooling |
| **Caching** | Jedis | 5.1.0 | Redis client |
| **Caching** | Redis | 7.0 | Distributed caching |
| **Security** | BCrypt | 0.4 | Password hashing |
| **Logging** | SLF4J | 2.0.7 | Logging facade |
| **Logging** | Logback | 1.4.11 | Logging implementation |
| **Build** | Maven | 4.0.0 | Build management |
| **Server** | Tomcat | 10.1+ | Application server |

---

## 3. Layer Architecture

### 3.1 Presentation Layer

**Purpose**: Handle HTTP requests, render views, manage user interaction

**Components**:
- **Controllers**: Jakarta Servlet-based request handlers
- **Views**: JSP templates with JSTL for dynamic content
- **Filters**: Security filters, request validation, response formatting
- **Static Resources**: CSS, JavaScript, images

**Key Controllers**:
- `ProductController` - Product browsing and search
- `CartController` - Shopping cart management
- `OrderController` - Order processing
- `AuthController` - Authentication and authorization
- `AdminProductController` - Admin product management

**Design Patterns**:
- **Front Controller Pattern**: Centralized request handling
- **View Helper Pattern**: Separation of view logic from business logic
- **Filter Chain Pattern**: Request preprocessing and postprocessing

### 3.2 Business Logic Layer

**Purpose**: Implement business rules, coordinate data access, manage transactions

**Components**:
- **Service Classes**: Business logic implementation
- **Business Rules**: Validation, calculations, workflows
- **Transaction Management**: ACID compliance
- **Integration Services**: Payment gateway, email, external APIs

**Key Services**:
- `ProductService` - Product catalog management
- `OrderService` - Order processing and fulfillment
- `CartService` - Shopping cart operations
- `AuthService` - Authentication and authorization
- `PaymentService` - Payment processing integration

**Design Patterns**:
- **Service Layer Pattern**: Business logic encapsulation
- **Transaction Script Pattern**: Simple business workflows
- **Strategy Pattern**: Multiple payment gateway implementations

### 3.3 Data Access Layer

**Purpose**: Abstract database operations, provide CRUD functionality, optimize queries

**Components**:
- **DAO Interfaces**: Data access contracts
- **DAO Implementations**: Database-specific implementations
- **Connection Pooling**: HikariCP for efficient connection management
- **Query Optimization**: Prepared statements, batch operations

**Key DAOs**:
- `ProductDAO` - Product data operations
- `OrderDAO` - Order data operations
- `UserDAO` - User data operations
- `CartDAO` - Shopping cart data operations
- `CategoryDAO` - Category management

**Design Patterns**:
- **Data Access Object Pattern**: Database operation abstraction
- **Factory Pattern**: DAO instance creation
- **Singleton Pattern**: Connection pool management

---

## 4. Data Architecture

### 4.1 Database Schema Design

**Database**: MySQL 8.0  
**Character Set**: UTF-8  
**Collation**: utf8mb4_unicode_ci

**Core Tables**:

| Table | Purpose | Key Relationships |
|-------|---------|-------------------|
| `users` | User accounts | 1:N with orders, cart, wishlist |
| `products` | Product catalog | N:1 with categories, 1:N with sizes |
| `categories` | Product categories | 1:N with products |
| `orders` | Customer orders | N:1 with users, 1:N with order_items |
| `order_items` | Order line items | N:1 with orders, N:1 with products |
| `cart_items` | Shopping cart | N:1 with users, N:1 with products |
| `wishlist` | User wishlist | N:1 with users, N:1 with products |
| `product_sizes` | Product size variants | N:1 with products |
| `addresses` | User addresses | N:1 with users |
| `payment_methods` | Payment methods | N:1 with users |

### 4.2 Data Access Strategy

**Connection Pooling**:
- **Pool Size**: Maximum 10 connections
- **Idle Connections**: Minimum 2, Maximum 5
- **Connection Timeout**: 20 seconds
- **Idle Timeout**: 30 seconds
- **Max Lifetime**: 30 minutes
- **Prepared Statement Caching**: 250 statements, max 2048 characters

**Query Optimization**:
- **Prepared Statements**: All queries use parameterized statements
- **Batch Operations**: Bulk updates for performance
- **Indexing Strategy**: Primary keys, foreign keys, and frequently queried columns
- **Transaction Management**: ACID compliance with proper rollback

---

## 5. Security Architecture

### 5.1 Security Layers

**Network Layer**:
- **SSL/TLS**: Encrypted data transmission
- **Firewall**: Network-level access control
- **DDoS Protection**: Rate limiting and request throttling

**Application Layer**:
- **Authentication**: BCrypt password hashing
- **Authorization**: Role-based access control (RBAC)
- **Session Management**: Secure session handling
- **CSRF Protection**: Token-based validation
- **Input Validation**: Parameterized queries, input sanitization

**Data Layer**:
- **SQL Injection Prevention**: Prepared statements
- **Data Encryption**: Sensitive data encryption at rest
- **Access Control**: Database user permissions
- **Audit Logging**: Security event logging

### 5.2 Authentication & Authorization

**Authentication Flow**:
1. User submits credentials
2. Password hashed with BCrypt
3. Database validation
4. Session creation
5. Role assignment
6. Access token generation

**Authorization Model**:
- **Roles**: ADMIN, USER, GUEST
- **Permissions**: READ, WRITE, DELETE, ADMIN
- **Access Control**: Role-based with resource-level permissions

---

## 6. Caching Architecture

### 6.1 Cache Strategy

**Multi-Layer Caching**:
- **Browser Cache**: Static resources (CSS, JS, images)
- **Application Cache**: Redis for frequently accessed data
- **Database Cache**: MySQL query cache

**Cache Implementation**:
- **Redis**: Distributed caching for session data, product catalog, cart data
- **Cache Keys**: Structured key naming (e.g., `product:123`, `user:456:cart`)
- **TTL Strategy**: Time-based expiration (1 hour default, 24 hours max)
- **Cache Invalidation**: Write-through cache invalidation

### 6.2 Cache Configuration

**Redis Configuration**:
- **Host**: localhost (configurable via environment)
- **Port**: 6379
- **Connection Pool**: 10 max connections, 5 idle
- **Timeout**: 2 seconds
- **Default TTL**: 3600 seconds (1 hour)

**Cacheable Data**:
- Product catalog (frequently accessed)
- User sessions (distributed sessions)
- Shopping cart data (user-specific)
- Category listings (rarely changed)

---

## 7. Performance Architecture

### 7.1 Performance Optimization

**Database Optimization**:
- **Connection Pooling**: HikariCP for efficient connection management
- **Query Optimization**: Prepared statements, proper indexing
- **Batch Operations**: Bulk updates for performance
- **Read Replicas**: Read scalability (future enhancement)

**Application Optimization**:
- **Lazy Loading**: On-demand data loading
- **Pagination**: Large dataset pagination
- **Asynchronous Processing**: Non-blocking operations
- **Resource Management**: Proper connection and statement cleanup

### 7.2 Scalability Considerations

**Horizontal Scaling**:
- **Stateless Design**: Session state in Redis
- **Load Balancing**: Multiple application server instances
- **Database Scaling**: Read replicas, sharding (future)

**Vertical Scaling**:
- **Resource Optimization**: Memory and CPU efficient code
- **Connection Pooling**: Efficient resource utilization
- **Caching**: Reduced database load

---

## 8. Error Handling Architecture

### 8.1 Error Handling Strategy

**Exception Handling**:
- **Try-Catch Blocks**: Localized error handling
- **Global Exception Handler**: Centralized error processing
- **Error Logging**: Comprehensive error logging
- **User-Friendly Messages**: Appropriate error messages

**Error Types**:
- **Validation Errors**: Input validation failures
- **Business Logic Errors**: Rule violations
- **System Errors**: Database, network, resource failures
- **Security Errors**: Authentication, authorization failures

### 8.2 Error Response Strategy

**HTTP Status Codes**:
- **200**: Success
- **400**: Bad Request (validation errors)
- **401**: Unauthorized (authentication required)
- **403**: Forbidden (authorization required)
- **404**: Not Found
- **500**: Internal Server Error

**Error Response Format**:
```json
{
  "error": "Error message",
  "code": "ERROR_CODE",
  "details": "Detailed error information",
  "timestamp": "2026-05-15T12:00:00Z"
}
```

---

## 9. Deployment Architecture

### 9.1 Deployment Strategy

**Container-Based Deployment**:
- **Docker**: Containerization for consistency
- **Docker Compose**: Multi-container orchestration
- **Environment Variables**: Configuration management
- **Volume Mounting**: Persistent data storage

**Deployment Components**:
- **Application Server**: Tomcat 10.1+
- **Database**: MySQL 8.0
- **Cache**: Redis 7.0
- **Web Server**: Nginx (reverse proxy)
- **Monitoring**: Prometheus + Grafana

### 9.2 Environment Configuration

**Development Environment**:
- Local development with Docker Compose
- Local database and cache
- Debug logging enabled
- Hot reload enabled

**Staging Environment**:
- Production-like configuration
- Staging database
- Performance monitoring
- Integration testing

**Production Environment**:
- High availability setup
- Database clustering
- Load balancing
- Comprehensive monitoring
- Security hardening

---

## 10. Monitoring & Observability

### 10.1 Monitoring Strategy

**Application Metrics**:
- Response time
- Error rate
- Request throughput
- Database connection pool usage
- Cache hit rate

**System Metrics**:
- CPU usage
- Memory usage
- Disk I/O
- Network I/O

**Business Metrics**:
- Order conversion rate
- Cart abandonment rate
- User engagement metrics
- Revenue tracking

### 10.2 Logging Strategy

**Log Levels**:
- **ERROR**: Critical errors requiring immediate attention
- **WARN**: Warning conditions that should be investigated
- **INFO**: Informational messages about normal operation
- **DEBUG**: Detailed debugging information

**Log Format**:
```
[timestamp] [level] [class] [thread] message
```

**Log Rotation**:
- Daily log rotation
- 30-day retention
- Compressed archive storage

---

## 11. Design Principles

### 11.1 SOLID Principles

**Single Responsibility Principle**:
- Each class has a single responsibility
- Controllers handle HTTP requests
- Services implement business logic
- DAOs handle data access

**Open/Closed Principle**:
- Open for extension, closed for modification
- Interface-based design
- Strategy pattern for payment gateways

**Liskov Substitution Principle**:
- DAO implementations are interchangeable
- Service implementations follow contracts

**Interface Segregation Principle**:
- Specific, focused interfaces
- No fat interfaces
- Client-specific interfaces

**Dependency Inversion Principle**:
- Depend on abstractions, not concretions
- Interface-based dependencies
- Dependency injection

### 11.2 Additional Principles

**DRY (Don't Repeat Yourself)**:
- Reusable components
- Shared utilities
- Common base classes

**KISS (Keep It Simple, Stupid)**:
- Simple, straightforward solutions
- Avoid over-engineering
- Clear, readable code

**YAGNI (You Aren't Gonna Need It)**:
- Build what's needed now
- Avoid speculative features
- Future-proof through good design

---

## 12. Technology Justification

### 12.1 Java & Jakarta EE

**Rationale**:
- **Maturity**: Proven, stable platform
- **Ecosystem**: Extensive libraries and tools
- **Performance**: High performance with JIT compilation
- **Security**: Built-in security features
- **Scalability**: Proven scalability in enterprise environments

### 12.2 MySQL Database

**Rationale**:
- **Reliability**: ACID compliance
- **Performance**: Optimized for read-heavy workloads
- **Ecosystem**: Wide tooling and support
- **Cost**: Open-source, cost-effective
- **Scalability**: Proven scalability options

### 12.3 Redis Cache

**Rationale**:
- **Performance**: In-memory storage for fast access
- **Scalability**: Distributed caching
- **Flexibility**: Multiple data structures
- **Persistence**: Optional data persistence
- **Community**: Strong open-source community

### 12.4 HikariCP Connection Pool

**Rationale**:
- **Performance**: Fastest connection pool
- **Reliability**: Stable and mature
- **Lightweight**: Minimal overhead
- **Configuration**: Flexible configuration options

---

## 13. Future Architecture Enhancements

### 13.1 Planned Enhancements

**Microservices Migration**:
- Split monolith into microservices
- API Gateway for service orchestration
- Service discovery and load balancing

**Event-Driven Architecture**:
- Message queue integration (RabbitMQ/Kafka)
- Event sourcing for audit trail
- Async processing for long-running tasks

**Advanced Caching**:
- Multi-layer caching strategy
- CDN integration for static content
- Edge computing for global performance

**Enhanced Security**:
- OAuth 2.0 / OpenID Connect
- Multi-factor authentication
- Advanced threat detection

---

## 14. Conclusion

FashionStore's architecture follows industry best practices for Java-based e-commerce platforms. The layered architecture provides clear separation of concerns, while the use of proven technologies ensures reliability and performance. The design prioritizes security, scalability, and maintainability, making it suitable for enterprise deployment.

### 14.1 Architecture Strengths
- **Clean separation of concerns** through layered architecture
- **Proven technology stack** with strong community support
- **Comprehensive security** across all layers
- **Performance optimization** through caching and connection pooling
- **Scalability** through stateless design and distributed caching

### 14.2 Areas for Improvement
- **Service Layer**: More formal service layer implementation
- **Testing**: Comprehensive automated testing suite
- **Monitoring**: Enhanced observability and alerting
- **Documentation**: API documentation and developer guides

---

## Appendix A: System Architecture Diagrams

## System Architecture Overview

```mermaid
graph TB
    subgraph "Client Layer"
        User[User]
        Browser[Browser]
        Mobile[Mobile Device]
    end

    subgraph "Web Server Layer"
        Nginx[Nginx<br/>Port 80/443]
    end

    subgraph "Application Layer"
        Tomcat[Tomcat<br/>Port 8080]
        AdminPanel[Admin Panel<br/>React App]
    end

    subgraph "Data Layer"
        MySQL[MySQL<br/>Port 3306]
        Redis[Redis<br/>Port 6379]
    end

    subgraph "Monitoring Layer"
        Prometheus[Prometheus<br/>Port 9090]
        Grafana[Grafana<br/>Port 3000]
    end

    User --> Browser
    User --> Mobile
    Browser --> Nginx
    Mobile --> Nginx
    Nginx --> Tomcat
    Nginx --> AdminPanel
    Tomcat --> MySQL
    Tomcat --> Redis
    Tomcat --> Prometheus
    AdminPanel --> Prometheus
    Prometheus --> Grafana
```

## Backend Architecture

```mermaid
graph TB
    subgraph "Controller Layer"
        AuthController[AuthController]
        ProductController[ProductController]
        OrderController[OrderController]
        CartController[CartController]
        UserController[UserController]
        AdminController[AdminController]
    end

    subgraph "Service Layer"
        AuthService[AuthService]
        ProductService[ProductService]
        OrderService[OrderService]
        CartService[CartService]
        UserService[UserService]
        AdminService[AdminService]
    end

    subgraph "Data Access Layer"
        ProductDAO[ProductDAO]
        OrderDAO[OrderDAO]
        CartDAO[CartDAO]
        UserDAO[UserDAO]
        CategoryDAO[CategoryDAO]
    end

    subgraph "Model Layer"
        Product[Product Model]
        Order[Order Model]
        Cart[Cart Model]
        User[User Model]
        Category[Category Model]
    end

    AuthController --> AuthService
    ProductController --> ProductService
    OrderController --> OrderService
    CartController --> CartService
    UserController --> UserService
    AdminController --> AdminService

    AuthService --> UserDAO
    ProductService --> ProductDAO
    OrderService --> OrderDAO
    CartService --> CartDAO
    UserService --> UserDAO
    AdminService --> UserDAO

    ProductDAO --> Product
    OrderDAO --> Order
    CartDAO --> Cart
    UserDAO --> User
    CategoryDAO --> Category
```

## Frontend Architecture

```mermaid
graph TB
    subgraph "Presentation Layer"
        JSP[JSP Templates]
        CSS[CSS Stylesheets]
        JS[JavaScript Modules]
    end

    subgraph "CSS Architecture"
        DesignTokens[design-tokens.css]
        Components[components/]
        Pages[pages/]
        Base[base.css]
        Reset[reset.css]
    end

    subgraph "JavaScript Architecture"
        MainJS[main.js]
        CartJS[cart.js]
        AuthJS[auth.js]
        LazyLoading[lazy-loading.js]
    end

    subgraph "Component Structure"
        Navbar[Navbar]
        Footer[Footer]
        ProductCard[Product Card]
        CartDrawer[Cart Drawer]
        SearchBar[Search Bar]
    end

    JSP --> CSS
    JSP --> JS
    CSS --> DesignTokens
    CSS --> Components
    CSS --> Pages
    CSS --> Base
    CSS --> Reset
    JS --> MainJS
    JS --> CartJS
    JS --> AuthJS
    JS --> LazyLoading
    Components --> Navbar
    Components --> Footer
    Components --> ProductCard
    Components --> CartDrawer
    Components --> SearchBar
```

## Data Flow Architecture

```mermaid
sequenceDiagram
    participant User
    participant Browser
    participant Nginx
    participant Tomcat
    participant MySQL
    participant Redis

    User->>Browser: Request Page
    Browser->>Nginx: HTTP Request
    Nginx->>Tomcat: Forward Request
    Tomcat->>Redis: Check Cache
    alt Cache Hit
        Redis-->>Tomcat: Cached Data
        Tomcat-->>Nginx: Response
    else Cache Miss
        Tomcat->>MySQL: Query Database
        MySQL-->>Tomcat: Data
        Tomcat->>Redis: Cache Data
        Tomcat-->>Nginx: Response
    end
    Nginx-->>Browser: HTTP Response
    Browser-->>User: Render Page
```

## Authentication Flow

```mermaid
sequenceDiagram
    participant User
    participant LoginPage
    participant AuthController
    participant AuthService
    participant UserDAO
    participant MySQL
    participant Session

    User->>LoginPage: Enter Credentials
    LoginPage->>AuthController: POST /api/admin/login
    AuthController->>AuthService: authenticate(email, password)
    AuthService->>UserDAO: findByEmail(email)
    UserDAO->>MySQL: SELECT * FROM users WHERE email = ?
    MySQL-->>UserDAO: User Data
    UserDAO-->>AuthService: User Object
    AuthService->>AuthService: verifyPassword(password, hash)
    alt Valid Credentials
        AuthService-->>AuthController: User Object
        AuthController->>Session: Set Session Attributes
        AuthController-->>LoginPage: Success Response
        LoginPage-->>User: Redirect to Dashboard
    else Invalid Credentials
        AuthController-->>LoginPage: Error Response
        LoginPage-->>User: Show Error Message
    end
```

## Deployment Architecture

```mermaid
graph TB
    subgraph "Docker Services"
        subgraph "Web Tier"
            Nginx[Nginx Container<br/>Port 80/443]
        end

        subgraph "App Tier"
            Tomcat[Tomcat Container<br/>Port 8080]
            AdminPanel[Admin Panel Container<br/>Port 3001]
        end

        subgraph "Data Tier"
            MySQL[MySQL Container<br/>Port 3306]
            Redis[Redis Container<br/>Port 6379]
        end

        subgraph "Monitoring Tier"
            Prometheus[Prometheus Container<br/>Port 9090]
            Grafana[Grafana Container<br/>Port 3000]
        end
    end

    subgraph "External Services"
        Stripe[Stripe API]
        Email[Email Service]
    end

    Nginx --> Tomcat
    Nginx --> AdminPanel
    Tomcat --> MySQL
    Tomcat --> Redis
    Tomcat --> Stripe
    Tomcat --> Email
    Tomcat --> Prometheus
    AdminPanel --> Prometheus
    Prometheus --> Grafana
```

## Security Architecture

```mermaid
graph TB
    subgraph "Security Layers"
        subgraph "Network Layer"
            Firewall[Firewall]
            SSL[TLS/SSL]
        end

        subgraph "Application Layer"
            CSRF[CSRF Protection]
            RateLimit[Rate Limiting]
            AuthFilter[Authentication Filter]
            SecurityFilter[Security Filter]
        end

        subgraph "Data Layer"
            PasswordHash[Password Hashing]
            Encryption[Data Encryption]
            SQLInjection[SQL Injection Prevention]
        end
    end

    Firewall --> SSL
    SSL --> CSRF
    CSRF --> RateLimit
    RateLimit --> AuthFilter
    AuthFilter --> SecurityFilter
    SecurityFilter --> PasswordHash
    PasswordHash --> Encryption
    Encryption --> SQLInjection
```

## Testing Architecture

```mermaid
graph TB
    subgraph "Test Types"
        subgraph "E2E Tests"
            Playwright[Playwright<br/>Cross-browser E2E]
            Cypress[Cypress<br/>Component Testing]
        end

        subgraph "Integration Tests"
            API[API Integration Tests]
            Service[Service Layer Tests]
        end

        subgraph "Unit Tests"
            Vitest[Vitest<br/>React Components]
            JUnit[JUnit<br/>Java Unit Tests]
        end

        subgraph "Performance Tests"
            Lighthouse[Lighthouse CI]
            LoadTest[Load Testing]
        end

        subgraph "Security Tests"
            OWASP[OWASP ZAP]
            DependencyScan[Dependency Scanning]
        end
    end

    Playwright --> CI[CI/CD Pipeline]
    Cypress --> CI
    API --> CI
    Service --> CI
    Vitest --> CI
    JUnit --> CI
    Lighthouse --> CI
    LoadTest --> CI
    OWASP --> CI
    DependencyScan --> CI

    CI --> Coverage[Coverage Reports]
    CI --> Artifacts[Test Artifacts]
```

## CI/CD Pipeline

```mermaid
graph LR
    A[Push to Git] --> B[Build Docker Images]
    B --> C[Run Unit Tests]
    C --> D[Run Integration Tests]
    D --> E[Run E2E Tests]
    E --> F[Security Scanning]
    F --> G[Generate Coverage Reports]
    G --> H{Quality Gates}
    H -->|Pass| I[Deploy to Staging]
    H -->|Fail| J[Notify Team]
    I --> K[Run Smoke Tests]
    K --> L[Deploy to Production]
    L --> M[Health Checks]
    M --> N[Monitor Performance]
```

## Component Interaction Diagram

```mermaid
graph TB
    subgraph "Frontend Components"
        Navbar[Navbar Component]
        Hero[Hero Section]
        ProductGrid[Product Grid]
        ProductCard[Product Card]
        CartDrawer[Cart Drawer]
        Footer[Footer Component]
    end

    subgraph "Backend Services"
        ProductService[Product Service]
        CartService[Cart Service]
        AuthService[Auth Service]
        OrderService[Order Service]
    end

    Navbar --> AuthService
    Hero --> ProductService
    ProductGrid --> ProductService
    ProductCard --> ProductService
    ProductCard --> CartService
    CartDrawer --> CartService
    CartDrawer --> AuthService
    Footer --> ProductService

    ProductService --> MySQL[(MySQL Database)]
    CartService --> Redis[(Redis Cache)]
    CartService --> MySQL
    AuthService --> MySQL
    OrderService --> MySQL
```

## Database Schema

```mermaid
erDiagram
    USERS ||--o{ ORDERS : places
    USERS ||--o{ CART_ITEMS : has
    USERS ||--o{ WISHLIST : has
    PRODUCTS ||--o{ ORDER_ITEMS : contains
    PRODUCTS ||--o{ CART_ITEMS : in
    PRODUCTS ||--o{ WISHLIST : in
    PRODUCTS ||--o{ PRODUCT_SIZES : has
    PRODUCTS ||--|| CATEGORIES : belongs_to
    ORDERS ||--o{ ORDER_ITEMS : contains
    CATEGORIES ||--o{ PRODUCTS : includes

    USERS {
        int id PK
        string email UK
        string password
        string name
        string role
        datetime created_at
    }

    PRODUCTS {
        int id PK
        string name
        string description
        decimal price
        int category_id FK
        string image_url
        boolean active
        datetime created_at
    }

    ORDERS {
        int id PK
        int user_id FK
        decimal total_amount
        string status
        datetime created_at
    }

    CART_ITEMS {
        int id PK
        int user_id FK
        int product_id FK
        int quantity
    }

    WISHLIST {
        int id PK
        int user_id FK
        int product_id FK
        datetime created_at
    }

    CATEGORIES {
        int id PK
        string name
        string description
    }

    PRODUCT_SIZES {
        int id PK
        int product_id FK
        string size
        int stock
    }

    ORDER_ITEMS {
        int id PK
        int order_id FK
        int product_id FK
        int quantity
        decimal price
    }
```

## API Endpoint Structure

```mermaid
graph TB
    subgraph "Public Endpoints"
        Home[GET /]
        Products[GET /products]
        ProductDetail[GET /products/:id]
        Login[POST /api/admin/login]
        Register[POST /api/admin/register]
    end

    subgraph "Protected Endpoints"
        Cart[GET /api/cart]
        AddToCart[POST /api/cart/add]
        Wishlist[GET /api/wishlist]
        AddToWishlist[POST /api/wishlist/add]
    end

    subgraph "Admin Endpoints"
        AdminDashboard[GET /admin/dashboard]
        AdminProducts[GET /admin/products]
        AdminOrders[GET /admin/orders]
        AdminUsers[GET /admin/users]
        CreateProduct[POST /api/admin/products]
        UpdateOrder[PUT /api/admin/orders/:id/status]
    end

    subgraph "Monitoring Endpoints"
        Health[GET /health]
        Metrics[GET /api/metrics]
        Status[GET /status]
    end
```

## Design System Architecture

```mermaid
graph TB
    subgraph "Design Tokens"
        Colors[Color Tokens]
        Typography[Typography Tokens]
        Spacing[Spacing Tokens]
        Shadows[Shadow Tokens]
        Radius[Border Radius Tokens]
    end

    subgraph "Component Styles"
        Buttons[Button Styles]
        Forms[Form Styles]
        Cards[Card Styles]
        Navigation[Navigation Styles]
        Modals[Modal Styles]
    end

    subgraph "Page Styles"
        Home[Home Page Styles]
        Products[Products Page Styles]
        Cart[Cart Page Styles]
        Checkout[Checkout Page Styles]
        Admin[Admin Page Styles]
    end

    Colors --> Buttons
    Colors --> Forms
    Colors --> Cards
    Typography --> Buttons
    Typography --> Forms
    Typography --> Navigation
    Spacing --> Buttons
    Spacing --> Forms
    Spacing --> Cards
    Shadows --> Cards
    Shadows --> Modals
    Radius --> Buttons
    Radius --> Cards
    Radius --> Modals

    Buttons --> Home
    Buttons --> Products
    Buttons --> Cart
    Buttons --> Checkout
    Forms --> Home
    Forms --> Products
    Forms --> Cart
    Forms --> Checkout
    Cards --> Home
    Cards --> Products
    Navigation --> Home
    Navigation --> Products
    Navigation --> Cart
    Navigation --> Checkout
    Modals --> Cart
    Modals --> Admin
```

## Responsive Design Breakpoints

```mermaid
graph LR
    A[Mobile<br/>320px] --> B[Mobile Large<br/>375px]
    B --> C[Tablet<br/>768px]
    C --> D[Laptop<br/>1024px]
    D --> E[Desktop<br/>1280px]
    E --> F[Wide<br/>1440px]

    style A fill:#e1f5ff
    style B fill:#e1f5ff
    style C fill:#fff4e1
    style D fill:#fff4e1
    style E fill:#e1ffe1
    style F fill:#e1ffe1
```

## Cache Architecture

```mermaid
graph TB
    subgraph "Cache Layers"
        subgraph "Browser Cache"
            StaticFiles[Static Files<br/>CSS, JS, Images]
            APIResponses[API Responses]
        end

        subgraph "CDN Cache"
            CDN[CDN Layer<br/>Optional]
        end

        subgraph "Application Cache"
            Redis[Redis Cache<br/>Session Data<br/>Product Data<br/>Cart Data]
        end

        subgraph "Database Cache"
            QueryCache[Query Cache<br/>MySQL Query Cache]
        end
    end

    StaticFiles --> CDN
    CDN --> Redis
    APIResponses --> Redis
    Redis --> QueryCache
    QueryCache --> MySQL[(MySQL Database)]
```

## Error Handling Architecture

```mermaid
graph TB
    subgraph "Error Sources"
        ClientError[Client Errors<br/>Validation, Format]
        ServerError[Server Errors<br/>500, 503]
        NetworkError[Network Errors<br/>Timeout, Connection]
        AuthError[Auth Errors<br/>401, 403]
    end

    subgraph "Error Handling"
        TryCatch[Try-Catch Blocks]
        ErrorFilter[Error Filter]
        GlobalHandler[Global Exception Handler]
    end

    subgraph "Error Response"
        Log[Log Error]
        Notify[Notify Team]
        UserMessage[User-Friendly Message]
        Metrics[Error Metrics]
    end

    ClientError --> TryCatch
    ServerError --> ErrorFilter
    NetworkError --> TryCatch
    AuthError --> ErrorFilter

    TryCatch --> Log
    ErrorFilter --> GlobalHandler
    GlobalHandler --> Log

    Log --> Notify
    Log --> Metrics
    Log --> UserMessage
```

## Monitoring Architecture

```mermaid
graph TB
    subgraph "Metrics Collection"
        AppMetrics[Application Metrics<br/>Response Time, Error Rate]
        SystemMetrics[System Metrics<br/>CPU, Memory, Disk]
        DBMetrics[Database Metrics<br/>Query Time, Connections]
        CacheMetrics[Cache Metrics<br/>Hit Rate, Size]
    end

    subgraph "Monitoring Stack"
        Prometheus[Prometheus<br/>Metrics Storage]
        Grafana[Grafana<br/>Visualization]
        AlertManager[Alert Manager<br/>Notifications]
    end

    subgraph "Alerting"
        Email[Email Alerts]
        Slack[Slack Notifications]
        PagerDuty[PagerDuty Integration]
    end

    AppMetrics --> Prometheus
    SystemMetrics --> Prometheus
    DBMetrics --> Prometheus
    CacheMetrics --> Prometheus

    Prometheus --> Grafana
    Prometheus --> AlertManager

    AlertManager --> Email
    AlertManager --> Slack
    AlertManager --> PagerDuty
```

## File Upload Flow

```mermaid
sequenceDiagram
    participant User
    participant Form
    participant UploadController
    participant StorageService
    participant S3[Object Storage]
    participant Database

    User->>Form: Select File
    Form->>UploadController: POST /upload
    UploadController->>UploadController: Validate File
    UploadController->>StorageService: store(file)
    StorageService->>S3: Upload File
    S3-->>StorageService: File URL
    StorageService-->>UploadController: File Metadata
    UploadController->>Database: Save File Record
    Database-->>UploadController: Success
    UploadController-->>Form: File URL
    Form-->>User: Upload Complete
```

## Search Architecture

```mermaid
graph TB
    subgraph "Search Flow"
        UserSearch[User Search Query]
        SearchController[Search Controller]
        SearchService[Search Service]
        Cache[Redis Cache]
        Database[MySQL Full-Text Search]
        Results[Search Results]
    end

    UserSearch --> SearchController
    SearchController --> SearchService
    SearchService --> Cache
    Cache -->|Cache Hit| Results
    Cache -->|Cache Miss| Database
    Database --> SearchService
    SearchService --> Cache
    Cache --> Results
    Results --> UserSearch
```

## Payment Flow

```mermaid
sequenceDiagram
    participant User
    participant CheckoutPage
    participant OrderController
    participant StripeService
    participant Stripe[Stripe API]
    participant Database
    participant EmailService

    User->>CheckoutPage: Enter Payment Details
    CheckoutPage->>OrderController: POST /checkout
    OrderController->>OrderController: Validate Order
    OrderController->>StripeService: createPaymentIntent(amount)
    StripeService->>Stripe: POST /v1/payment_intents
    Stripe-->>StripeService: Payment Intent
    StripeService-->>OrderController: Client Secret
    OrderController-->>CheckoutPage: Client Secret
    CheckoutPage->>Stripe: Confirm Payment
    Stripe-->>CheckoutPage: Payment Success
    CheckoutPage->>OrderController: POST /confirm-order
    OrderController->>Database: Create Order
    Database-->>OrderController: Order Created
    OrderController->>EmailService: sendConfirmation(order)
    EmailService->>User: Order Confirmation Email
    OrderController-->>CheckoutPage: Order Success
```

## Session Management

```mermaid
graph TB
    subgraph "Session Lifecycle"
        Create[Create Session<br/>Login]
        Validate[Validate Session<br/>Each Request]
        Refresh[Refresh Session<br/>Activity]
        Invalidate[Invalidate Session<br/>Logout/Timeout]
    end

    subgraph "Session Storage"
        HttpSession[HttpSession<br/>Server-Side]
        RedisSession[Redis Session<br/>Distributed]
        Cookie[Session Cookie<br/>Client-Side]
    end

    Create --> HttpSession
    Create --> RedisSession
    Create --> Cookie

    HttpSession --> Validate
    RedisSession --> Validate
    Cookie --> Validate

    Validate -->|Valid| Refresh
    Validate -->|Invalid| Invalidate

    Refresh --> HttpSession
    Invalidate --> HttpSession
    Invalidate --> RedisSession
    Invalidate --> Cookie
```

## Technology Stack Summary

```mermaid
mindmap
    root((FashionStore))
        Frontend
            HTML5
            CSS3
            JavaScript ES6+
            JSP Templates
            React Admin Panel
        Backend
            Java 17
            Spring Framework
            Tomcat
            Jakarta EE
            Maven
        Database
            MySQL 8.0
            Redis 7.0
            JDBC
        Infrastructure
            Docker
            Docker Compose
            Nginx
        Monitoring
            Prometheus
            Grafana
        Testing
            Playwright
            Cypress
            Vitest
            JUnit
        Security
            CSRF Protection
            Rate Limiting
            Password Hashing
            SSL/TLS
```

## Deployment Environments

```mermaid
graph TB
    subgraph "Development"
        DevLocal[Local Development<br/>Docker Compose]
        DevDatabase[Local Database<br/>MySQL]
        DevCache[Local Cache<br/>Redis]
    end

    subgraph "Staging"
        StageServer[Staging Server<br/>Docker Swarm]
        StageDatabase[Staging Database<br/>MySQL]
        StageCache[Staging Cache<br/>Redis]
        StageMonitoring[Staging Monitoring<br/>Prometheus/Grafana]
    end

    subgraph "Production"
        ProdServer[Production Server<br/>Kubernetes/Docker]
        ProdDatabase[Production Database<br/>MySQL Cluster]
        ProdCache[Production Cache<br/>Redis Cluster]
        ProdMonitoring[Production Monitoring<br/>Prometheus/Grafana]
        ProdCDN[CDN<br/>CloudFlare]
    end

    DevLocal --> DevDatabase
    DevLocal --> DevCache

    StageServer --> StageDatabase
    StageServer --> StageCache
    StageServer --> StageMonitoring

    ProdServer --> ProdDatabase
    ProdServer --> ProdCache
    ProdServer --> ProdMonitoring
    ProdServer --> ProdCDN
```

---

**Note**: This architecture diagram provides a comprehensive view of the FashionStore system architecture. All diagrams use Mermaid syntax and can be rendered in any Markdown viewer that supports Mermaid.
