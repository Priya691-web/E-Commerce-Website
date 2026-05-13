# FashionStore - Project Report

## Table of Contents
1. [Introduction](#introduction)
2. [Problem Statement](#problem-statement)
3. [Objectives](#objectives)
4. [Existing System](#existing-system)
5. [Proposed System](#proposed-system)
6. [Technology Stack](#technology-stack)
7. [System Architecture](#system-architecture)
8. [Modules](#modules)
9. [Database Design](#database-design)
10. [Security](#security)
11. [Performance](#performance)
12. [Testing](#testing)
13. [Challenges](#challenges)
14. [Future Enhancements](#future-enhancements)
15. [Conclusion](#conclusion)

---

## Introduction

The FashionStore project is a comprehensive e-commerce platform designed to provide a modern, scalable, and secure online shopping experience for fashion retail. The platform serves two distinct user groups: customers who browse and purchase products, and administrators who manage the store's inventory, orders, and users. This report documents the complete system design, implementation details, and technical considerations of the FashionStore e-commerce platform.

### Project Overview

FashionStore is a full-stack e-commerce application built with Java 21 for the backend, JSP for the customer frontend, and React for the admin dashboard. The platform features a multi-frontend architecture where customers interact with a traditional JSP-based interface while administrators use a modern React Single Page Application (SPA). Both frontends share a common Java backend that handles business logic, data access, and API endpoints.

### Scope of the Project

The project encompasses:
- Customer-facing e-commerce features (browsing, cart, checkout, orders)
- Admin dashboard for store management
- User authentication and authorization
- Product and inventory management
- Order processing and tracking
- Payment integration with Stripe
- Search and recommendation features
- Multi-layer security implementation
- Docker-based deployment
- Comprehensive logging and monitoring

---

## Problem Statement

### Current Challenges in E-Commerce

The e-commerce industry faces several challenges that FashionStore aims to address:

1. **User Experience**: Many e-commerce platforms suffer from poor user interfaces, confusing navigation, and slow performance, leading to high cart abandonment rates.

2. **Security Concerns**: E-commerce platforms are prime targets for cyber attacks including SQL injection, XSS, CSRF, and data breaches. Security must be a foundational consideration rather than an afterthought.

3. **Scalability**: Traditional e-commerce architectures often struggle to handle traffic spikes during sales events, leading to downtime and lost revenue.

4. **Management Complexity**: Store administrators need intuitive tools to manage products, inventory, orders, and customers efficiently. Many platforms provide cumbersome admin interfaces.

5. **Mobile Responsiveness**: With increasing mobile commerce, platforms must provide seamless experiences across all device types.

6. **Performance**: Slow page loads and poor caching strategies frustrate users and impact conversion rates.

### Specific Problems Addressed

FashionStore addresses these problems through:

- **Dual Frontend Architecture**: Optimized interfaces for both customers (JSP for SEO and performance) and administrators (React for interactivity)
- **Multi-Layer Security**: Comprehensive security implementation with authentication, authorization, CSRF protection, XSS prevention, and secure session management
- **Containerized Deployment**: Docker-based architecture for consistent deployment and easy scaling
- **Caching Strategy**: Redis-based caching with local fallback for improved performance
- **Responsive Design**: Mobile-first design approach for both frontends
- **Modern UI/UX**: Luxury design system with smooth animations and intuitive navigation

---

## Objectives

### Primary Objectives

1. **Develop a Complete E-Commerce Platform**: Create a fully functional e-commerce system with customer shopping features and admin management capabilities.

2. **Implement Robust Security**: Establish multiple layers of security to protect against common web vulnerabilities and ensure data protection.

3. **Ensure Scalability**: Design the system to handle growing traffic and data volumes through efficient caching, database optimization, and containerization.

4. **Provide Excellent User Experience**: Create intuitive, responsive, and visually appealing interfaces for both customers and administrators.

5. **Enable Efficient Store Management**: Provide administrators with powerful tools to manage products, inventory, orders, users, and analytics.

### Secondary Objectives

1. **SEO Optimization**: Ensure the customer frontend is search engine friendly through server-side rendering and proper meta tags.

2. **Payment Integration**: Integrate secure payment processing with Stripe for seamless checkout experience.

3. **Search and Discovery**: Implement advanced search functionality with autocomplete, filtering, and recommendations.

4. **Analytics and Reporting**: Provide administrators with insights into sales, user behavior, and product performance.

5. **Mobile Responsiveness**: Ensure the platform works seamlessly across desktop, tablet, and mobile devices.

---

## Existing System

### Traditional E-Commerce Limitations

Traditional e-commerce systems often suffer from several limitations:

1. **Monolithic Architecture**: Single frontend serving both customers and administrators, leading to compromised user experience for both groups.

2. **Limited Security**: Basic authentication without proper CSRF protection, XSS prevention, or secure session management.

3. **Poor Performance**: Lack of caching strategies, inefficient database queries, and no connection pooling.

4. **Difficult Deployment**: Manual deployment processes without containerization, leading to environment inconsistencies.

5. **Limited Analytics**: Minimal or no reporting capabilities for administrators to make data-driven decisions.

6. **Inflexible Design**: Hard-coded styles and components that are difficult to maintain or customize.

### Gap Analysis

FashionStore addresses these gaps through:

- **Multi-Frontend Architecture**: Separate optimized interfaces for customers and administrators
- **Comprehensive Security**: Multi-layer security implementation following industry best practices
- **Performance Optimization**: Redis caching, HikariCP connection pooling, batch loading strategies
- **Containerized Deployment**: Docker-based deployment for consistency and scalability
- **Rich Analytics**: Comprehensive dashboard with sales, user, and product analytics
- **Design Systems**: Token-based design systems for consistent and maintainable styling

---

## Proposed System

### System Overview

FashionStore proposes a modern, multi-frontend e-commerce platform with:

- **Customer Frontend**: JSP-based server-side rendered interface for optimal SEO and performance
- **Admin Frontend**: React-based SPA for rich interactivity and real-time updates
- **Shared Backend**: Java 21 backend serving both frontends through different API endpoints
- **Docker Deployment**: Containerized services for consistent deployment and easy scaling
- **Multi-Layer Security**: Comprehensive security implementation at all layers
- **Performance Optimization**: Caching, connection pooling, and efficient data access

### Key Features

**Customer Features:**
- User registration and authentication
- Product browsing with filtering and sorting
- Advanced search with autocomplete
- Shopping cart management
- Wishlist functionality
- Secure checkout with multiple payment methods
- Order tracking and history
- Address management
- Product reviews and ratings

**Admin Features:**
- Dashboard with key metrics
- Product CRUD operations
- Inventory management with low stock alerts
- Order processing and status updates
- User management with role assignment
- Category management
- Coupon creation and management
- Sales analytics and reporting

### System Benefits

1. **Improved User Experience**: Optimized interfaces for each user group with modern UI/UX design

2. **Enhanced Security**: Multi-layer security protecting against common vulnerabilities

3. **Better Performance**: Caching strategies and optimized database queries

4. **Easier Deployment**: Containerized deployment with Docker Compose

5. **Scalability**: Architecture designed to handle growth through horizontal scaling

6. **Maintainability**: Clean code architecture with separation of concerns

---

## Technology Stack

### Backend Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Programming language |
| Jakarta Servlet API | 6.0 | Web framework |
| Tomcat | 10.1 | Servlet container (Docker) |
| MySQL | 8.0 | Database |
| HikariCP | 5.0.1 | Connection pooling |
| Redis | 7 | Caching |
| Jedis | 4.3.1 | Redis client |
| jBCrypt | 0.6 | Password hashing |
| Gson | 2.10.1 | JSON serialization |
| SLF4J | 2.0.9 | Logging facade |
| Logback | 1.4.11 | Logging implementation |
| Stripe SDK | 24.11.0 | Payment processing |

### Frontend Technologies

**Customer Frontend:**
- JSP 3.1 (JavaServer Pages)
- JSTL 2.0 (JSP Standard Tag Library)
- JavaScript ES6+
- CSS3 with custom design tokens
- HTML5

**Admin Frontend:**
- React 18.3.1
- Vite 5.4.10 (Build tool)
- React Router DOM 6.27.0 (Routing)
- TailwindCSS 3.4.14 (Styling)
- Lucide React 0.456.0 (Icons)
- Recharts 2.13.3 (Charts)
- Axios 1.7.7 (HTTP client)

### DevOps Technologies

| Technology | Purpose |
|------------|---------|
| Docker | Containerization |
| Docker Compose | Service orchestration |
| Nginx | Reverse proxy |
| Maven | Build tool |

### Technology Rationale

**Java 21**: Chosen for its modern features, performance improvements, and long-term support. The platform benefits from pattern matching, records, and enhanced switch expressions.

**JSP for Customer Frontend**: Server-side rendering provides better SEO, faster initial page loads, and progressive enhancement capabilities.

**React for Admin Frontend**: Modern SPA architecture provides rich interactivity, real-time updates, and excellent developer experience.

**MySQL 8.0**: Reliable relational database with excellent performance, ACID compliance, and extensive feature set.

**Redis**: High-performance caching layer to reduce database load and improve response times.

**Docker**: Containerization ensures consistent deployment across environments and simplifies scaling.

---

## System Architecture

### High-Level Architecture

FashionStore follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
├─────────────────────┬───────────────────────────────────┤
│  Customer Frontend  │      Admin Frontend               │
│  (JSP + Vanilla JS) │      (React SPA)                  │
└─────────────────────┴───────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                      │
├─────────────────────────────────────────────────────────┤
│  Controllers │ Services │ Filters │ Validation          │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      Data Access Layer                    │
├─────────────────────────────────────────────────────────┤
│  DAO Interface │ DAO Implementation │ Cache Service      │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                   │
├─────────────────────┬───────────────────────────────────┤
│  MySQL Database     │      Redis Cache                  │
└─────────────────────┴───────────────────────────────────┘
```

### Multi-Frontend Architecture

The platform employs a **dual-frontend architecture**:

**Customer Frontend (JSP):**
- Server-side rendering for SEO
- Traditional MVC pattern
- Progressive enhancement with vanilla JavaScript
- Optimized for performance and accessibility

**Admin Frontend (React):**
- Client-side rendering for interactivity
- Single Page Application (SPA)
- Component-based architecture
- Real-time updates and rich UI

Both frontends communicate with the same Java backend through different API endpoints:
- Customer frontend: Traditional servlet endpoints with JSP views
- Admin frontend: RESTful JSON API under `/api/admin/*`

### Request Flow

```
Customer Request:
Browser → Nginx → Backend Servlet → Service Layer → DAO Layer → Database
                 ↓
              JSP View → Rendered HTML → Browser

Admin Request:
Browser → Nginx → Admin API → Service Layer → DAO Layer → Database
                 ↓
              JSON Response → React → Rendered UI → Browser
```

---

## Modules

### Module Overview

The system is organized into the following modules:

1. **Authentication Module**: User registration, login, logout, password reset
2. **Product Module**: Product browsing, details, search, recommendations
3. **Cart Module**: Shopping cart management, add/remove/update items
4. **Wishlist Module**: Save favorite items, move to cart
5. **Checkout Module**: Order processing, payment integration
6. **Order Module**: Order history, tracking, status updates
7. **Address Module**: Address management, default address
8. **Review Module**: Product reviews, ratings
9. **Admin Dashboard Module**: Key metrics, recent activity
10. **Admin Product Module**: Product CRUD operations
11. **Admin Inventory Module**: Stock management, low stock alerts
12. **Admin Order Module**: Order processing, status management
13. **Admin User Module**: User management, role assignment
14. **Admin Category Module**: Category management
15. **Admin Coupon Module**: Coupon creation and management
16. **Analytics Module**: Sales analytics, user analytics, product analytics

### Module Interactions

```
Authentication → All modules (requires authentication)
Product → Cart, Wishlist, Order
Cart → Checkout
Checkout → Order, Payment
Order → Analytics
Admin Product → Admin Inventory
Admin Order → Analytics
```

---

## Database Design

### Database Overview

FashionStore uses **MySQL 8.0** as its relational database. The database schema consists of 30+ tables organized into logical groups:

- **User Management**: users, user_settings, user_profiles, addresses
- **Product Management**: products, categories, product_sizes, product_attributes
- **Cart & Wishlist**: cart_items, wishlist, saved_items
- **Orders**: orders, order_items, payments, payment_methods
- **Coupons**: coupons, coupon_usage
- **Reviews**: reviews
- **Search**: search_history, search_analytics, recently_viewed
- **Shipping**: shipping_zones, shipping_rates
- **Tax**: tax_rates
- **Email**: email_logs, email_notifications
- **Stripe Integration**: stripe_customers, stripe_webhook_events
- **Refunds**: refunds
- **Invoices**: invoices

### Key Tables

**users**: Stores user account information including authentication credentials and role
```sql
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    gender ENUM('Male', 'Female', 'Other'),
    address TEXT,
    role ENUM('admin', 'customer', 'disabled') DEFAULT 'customer',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**products**: Stores product information
```sql
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    discount_percent DECIMAL(5, 2) DEFAULT 0,
    image_url VARCHAR(500),
    stock_quantity INT DEFAULT 0,
    category_id INT,
    brand VARCHAR(100),
    active BOOLEAN DEFAULT TRUE,
    is_new BOOLEAN DEFAULT FALSE,
    is_sale BOOLEAN DEFAULT FALSE,
    is_trending BOOLEAN DEFAULT FALSE,
    popular_score DECIMAL(10, 2) DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);
```

**orders**: Stores order information
```sql
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    shipping_address_id INT,
    total_amount DECIMAL(10, 2) NOT NULL,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    final_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled') DEFAULT 'Pending',
    payment_method ENUM('card', 'upi', 'cod'),
    payment_status ENUM('Pending', 'Paid', 'Failed', 'Refunded') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (shipping_address_id) REFERENCES addresses(address_id)
);
```

### Database Design Principles

1. **Normalization**: Database is normalized to 3NF to reduce redundancy
2. **Indexing**: Strategic indexes on frequently queried columns
3. **Foreign Keys**: Referential integrity enforced through foreign key constraints
4. **Timestamps**: All tables include created_at and updated_at timestamps
5. **Soft Deletes**: Important records use active flags instead of hard deletes
6. **Views**: Materialized views for common queries (trending products, low stock)

---

## Security

### Security Architecture

FashionStore implements a **defense-in-depth** security strategy with multiple layers of protection:

### Authentication

- **Password Hashing**: BCrypt with 10 rounds for secure password storage
- **Session Management**: HttpOnly cookies, session fixation prevention, 30-minute timeout
- **Role-Based Access Control**: Three roles (admin, customer, disabled) with appropriate permissions

### Authorization

- **AuthFilter**: Servlet filter enforcing authentication on protected routes
- **Role Verification**: Admin-only routes protected with role checks
- **Account Status**: Disabled accounts denied access

### CSRF Protection

- **Token-Based CSRF**: Unique CSRF token generated per session
- **Token Validation**: All state-changing requests (POST, PUT, DELETE) require valid CSRF token
- **Header and Parameter Support**: CSRF token accepted via header or form parameter

### XSS Prevention

- **Input Sanitization**: All user input sanitized before processing
- **Output Encoding**: HTML and JavaScript encoding using Apache Commons Text
- **Content Security Policy**: Restrictive CSP header to prevent script injection

### SQL Injection Prevention

- **Prepared Statements**: All database queries use parameterized statements
- **Input Validation**: User input validated before database operations
- **No String Concatenation**: Strict prohibition of SQL string concatenation

### Security Headers

- **X-Content-Type-Options**: nosniff
- **X-Frame-Options**: DENY
- **X-XSS-Protection**: 1; mode=block
- **Strict-Transport-Security**: max-age=31536000
- **Content-Security-Policy**: Custom policy restricting script sources

### CORS Configuration

- **Configured Origins**: CORS policies configured for cross-origin requests
- **Preflight Handling**: Proper handling of OPTIONS preflight requests
- **Production Restrictions**: Tightened CORS policies in production

---

## Performance

### Performance Optimization Strategies

### Caching

**Redis Caching:**
- Product details cached with TTL
- Featured products cached
- Category listings cached
- Local in-memory fallback when Redis unavailable
- Cache invalidation on data updates

**Cache Service Implementation:**
- Singleton pattern for cache access
- Type-safe retrieval
- Pattern-based invalidation
- Automatic expiry management

### Database Optimization

**Connection Pooling:**
- HikariCP for efficient connection management
- Configured pool sizes for dev/prod
- Connection validation
- Leak detection

**Query Optimization:**
- Prepared statements for all queries
- Batch loading to prevent N+1 queries
- Strategic indexes on frequently queried columns
- Views for complex queries

**Batch Loading Example:**
```java
// Batch load product sizes to avoid N+1 queries
Map<Integer, List<ProductSize>> sizesMap = 
    productSizeDAO.getSizesMapByProductIds(productIds);
```

### Frontend Optimization

**Customer Frontend:**
- Lazy loading images
- CSS minification
- JavaScript async loading
- Browser cache headers

**Admin Frontend:**
- Code splitting by route
- Lazy loading components
- React.memo for component memoization
- Virtual scrolling for large lists

### Monitoring

**Logging:**
- Structured logging with SLF4J/Logback
- Request/response logging
- Error logging with stack traces
- Performance metrics logging

**Health Checks:**
- Docker health checks for all services
- Database health check endpoint
- Cache health monitoring

---

## Testing

### Testing Strategy

### Unit Testing

**Backend:**
- Service layer unit tests with JUnit
- DAO layer tests with H2 in-memory database
- Utility class tests
- Filter tests

**Frontend:**
- React component tests with Jest
- Utility function tests
- Hook tests

### Integration Testing

**API Testing:**
- REST API endpoint tests
- Servlet integration tests
- Database integration tests

### End-to-End Testing

**User Flows:**
- Registration and login
- Product browsing and search
- Add to cart and checkout
- Order placement and tracking
- Admin product management
- Admin order processing

### Testing Tools

- **JUnit**: Unit testing framework for Java
- **Mockito**: Mocking framework for Java
- **Jest**: JavaScript testing framework
- **React Testing Library**: React component testing
- **Postman**: API testing and documentation

---

## Challenges

### Development Challenges

### 1. Multi-Frontend Architecture

**Challenge**: Maintaining two different frontends while sharing a common backend.

**Solution**: 
- Clear API contract documentation
- Separate API endpoints for each frontend
- Shared DTOs for data transfer
- Comprehensive API testing

### 2. Caching Strategy

**Challenge**: Implementing caching that improves performance without data inconsistency.

**Solution**:
- Redis with local fallback
- Cache invalidation on data updates
- Appropriate TTL values
- Cache hit/miss logging

### 3. Security Implementation

**Challenge**: Implementing comprehensive security without compromising usability.

**Solution**:
- Multi-layer security approach
- Security headers
- CSRF tokens
- Regular security audits
- OWASP guidelines compliance

### 4. Performance Optimization

**Challenge**: Ensuring fast page loads and responsive UI with large datasets.

**Solution**:
- Redis caching
- Database query optimization
- Batch loading strategies
- Lazy loading
- Connection pooling

### 5. Docker Deployment

**Challenge**: Setting up containerized deployment with multiple services.

**Solution**:
- Docker Compose for orchestration
- Health checks for service dependencies
- Volume management for persistent data
- Network isolation

---

## Future Enhancements

### Planned Enhancements

### 1. Advanced Search

- Elasticsearch integration for advanced search capabilities
- Faceted search with multiple filters
- Search analytics and optimization
- Personalized search results

### 2. Recommendation Engine

- Collaborative filtering for product recommendations
- "Customers who bought this also bought"
- Personalized homepage based on browsing history
- AI-powered recommendations

### 3. Mobile Application

- Native mobile app for iOS and Android
- Push notifications for order updates
- Mobile-exclusive features
- Offline browsing capability

### 4. Advanced Analytics

- Real-time analytics dashboard
- Cohort analysis
- Funnel analysis
- A/B testing framework

### 5. Payment Options

- Additional payment gateways (PayPal, Razorpay)
- Digital wallets (Apple Pay, Google Pay)
- Buy now, pay later options
- Cryptocurrency payments

### 6. Social Features

- Social sharing integration
- User-generated content (photos, reviews)
- Social login (Google, Facebook)
- Community features

### 7. Multi-Vendor Support

- Multi-vendor marketplace
- Vendor dashboard
- Commission management
- Vendor analytics

### 8. Internationalization

- Multi-language support
- Multi-currency support
- Regional pricing
- Localized content

### 9. Advanced Inventory

- Barcode scanning
- Warehouse management
- Supplier integration
- Automated reordering

### 10. AI Features

- Chatbot for customer support
- Automated customer service
- Image search (find similar products)
- Price optimization

---

## Conclusion

The FashionStore project represents a comprehensive, modern e-commerce platform that addresses the key challenges of online retail. Through its multi-frontend architecture, robust security implementation, performance optimization strategies, and comprehensive feature set, FashionStore provides an excellent shopping experience for customers and powerful management tools for administrators.

### Key Achievements

1. **Complete E-Commerce Platform**: Full-featured platform with customer shopping and admin management capabilities

2. **Multi-Layer Security**: Comprehensive security implementation protecting against common vulnerabilities

3. **Performance Optimization**: Caching, connection pooling, and query optimization ensuring fast response times

4. **Modern Architecture**: Clean, maintainable codebase with separation of concerns

5. **Containerized Deployment**: Docker-based deployment for consistency and scalability

6. **Dual Frontend Design**: Optimized interfaces for both customers and administrators

### Technical Excellence

The project demonstrates technical excellence through:
- Clean architecture with clear separation of concerns
- Comprehensive security implementation following industry best practices
- Performance optimization through caching and database optimization
- Modern development practices with containerization
- Extensive documentation for maintainability

### Future Outlook

FashionStore is positioned for future growth with:
- Scalable architecture supporting horizontal scaling
- Extensible design for adding new features
- Modern technology stack with long-term support
- Comprehensive analytics for data-driven decisions

The platform serves as a solid foundation for a successful e-commerce business while providing an excellent user experience for both customers and administrators.

---

## References

### Technologies and Frameworks

- Jakarta Servlet API: https://eclipse-ee4j.github.io/servlet-api/
- React: https://react.dev/
- MySQL: https://www.mysql.com/
- Redis: https://redis.io/
- Docker: https://www.docker.com/
- Stripe: https://stripe.com/docs

### Security Resources

- OWASP Top 10: https://owasp.org/www-project-top-ten/
- BCrypt: https://github.com/patrickfav/bcrypt
- CSRF Prevention: https://owasp.org/www-community/attacks/csrf

### Performance Resources

- HikariCP: https://github.com/brettwooldridge/HikariCP
- Redis Caching: https://redis.io/docs/manual/patterns/caching/
- Database Optimization: https://dev.mysql.com/doc/refman/8.0/en/optimization.html
