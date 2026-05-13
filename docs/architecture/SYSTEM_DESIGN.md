# FashionStore - System Design Document

## Table of Contents
1. [System Overview](#system-overview)
2. [Architecture Patterns](#architecture-patterns)
3. [Component Design](#component-design)
4. [Data Flow](#data-flow)
5. [Interface Design](#interface-design)
6. [Database Schema Design](#database-schema-design)
7. [Security Design](#security-design)
8. [Performance Design](#performance-design)
9. [Scalability Design](#scalability-design)
10. [Deployment Design](#deployment-design)

---

## System Overview

FashionStore is a comprehensive e-commerce platform designed with a **multi-frontend architecture** serving both customers and administrators through optimized interfaces while sharing a common backend.

### System Goals

1. **Provide Excellent User Experience**: Intuitive, responsive interfaces for both customers and administrators
2. **Ensure Security**: Multi-layer security protecting against common vulnerabilities
3. **Optimize Performance**: Fast response times through caching and optimization strategies
4. **Enable Scalability**: Architecture designed to handle growth through horizontal scaling
5. **Maintain Maintainability**: Clean code architecture with separation of concerns

### System Scope

The system encompasses:
- Customer-facing e-commerce features (browsing, cart, checkout, orders)
- Admin dashboard for store management
- User authentication and authorization
- Product and inventory management
- Order processing and tracking
- Payment integration
- Search and recommendations
- Multi-layer security
- Containerized deployment

---

## Architecture Patterns

### Layered Architecture

FashionStore follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                   │
│  Customer Frontend (JSP) │ Admin Frontend (React)      │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Application Layer                      │
│  Controllers │ Services │ Filters │ Validation          │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      Data Access Layer                    │
│  DAO Interface │ DAO Implementation │ Cache Service      │
└─────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────┐
│                    Infrastructure Layer                   │
│  MySQL Database │ Redis Cache │ File Storage           │
└─────────────────────────────────────────────────────────┘
```

### Multi-Frontend Pattern

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

### DAO Pattern

The Data Access Object (DAO) pattern is used for database operations:

```java
// DAO Interface
public interface ProductDAO {
    Product getProductById(int productId);
    List<Product> getAllProducts();
    boolean addProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int productId);
}

// DAO Implementation
public class ProductDAOImpl implements ProductDAO {
    // Implementation details
}
```

### Service Layer Pattern

The Service Layer pattern encapsulates business logic:

```java
public class ProductService {
    private ProductDAO productDAO;
    private CacheService cacheService;
    
    public Product getProductById(int productId) {
        // Check cache first
        Product cached = cacheService.get(CacheKey.product(productId));
        if (cached != null) return cached;
        
        // Fetch from database
        Product product = productDAO.getProductById(productId);
        
        // Cache result
        cacheService.put(CacheKey.product(productId), product);
        
        return product;
    }
}
```

### Singleton Pattern

The Cache Service uses the Singleton pattern:

```java
public class CacheService {
    private static volatile CacheService instance;
    
    private CacheService() {
        // Private constructor
    }
    
    public static CacheService getInstance() {
        if (instance == null) {
            synchronized (CacheService.class) {
                if (instance == null) {
                    instance = new CacheService();
                }
            }
        }
        return instance;
    }
}
```

---

## Component Design

### Backend Components

**Controllers:**
- Handle HTTP requests
- Validate input
- Call service layer
- Prepare responses
- Manage session

**Services:**
- Implement business logic
- Coordinate DAO operations
- Handle transactions
- Manage cache

**DAOs:**
- Execute database queries
- Map result sets to objects
- Handle SQL exceptions
- Implement CRUD operations

**Filters:**
- CORS handling
- Security headers
- Request logging
- Authentication
- CSRF protection

**Models:**
- Represent domain entities
- Encapsulate business rules
- Provide getters/setters
- Implement validation

### Frontend Components

**Customer Frontend (JSP):**
- JSP pages for views
- JavaScript for interactivity
- CSS for styling
- Partials for reusability

**Admin Frontend (React):**
- Functional components
- Hooks for state management
- Context for global state
- Custom hooks for reusable logic

---

## Data Flow

### Request Flow

```
Customer Request:
1. Browser sends HTTP request
2. Nginx receives request
3. Nginx forwards to backend
4. Filters process request (CORS, Security, Auth, CSRF)
5. Controller handles request
6. Service layer processes business logic
7. DAO layer accesses database
8. Database returns data
9. Service layer processes data
10. Controller prepares response
11. JSP renders HTML
12. Response sent to browser

Admin Request:
1. React app sends HTTP request
2. Nginx receives request
3. Nginx forwards to backend
4. Filters process request
5. Admin API controller handles request
6. Service layer processes business logic
7. DAO layer accesses database
8. Database returns data
9. Service layer processes data
10. Controller returns JSON
11. React app receives JSON
12. React renders UI
```

### Data Access Flow

```
Service Layer Request:
1. Service receives request
2. Service checks cache
3. If cache hit, return cached data
4. If cache miss, call DAO
5. DAO creates prepared statement
6. DAO executes query
7. DAO maps result set to object
8. DAO returns object to service
9. Service processes object
10. Service caches object
11. Service returns object to controller
```

---

## Interface Design

### RESTful API Design

**Admin API follows REST principles:**

- **GET**: Retrieve resources
- **POST**: Create resources
- **PUT**: Update resources
- **DELETE**: Delete resources

**URL Structure:**
```
GET    /api/admin/products          - List products
POST   /api/admin/products          - Create product
GET    /api/admin/products/{id}     - Get product
PUT    /api/admin/products/{id}     - Update product
DELETE /api/admin/products/{id}     - Delete product
```

### JSP View Design

**JSP pages follow MVC pattern:**

- Controllers prepare data
- Controllers set request attributes
- JSP pages render data
- Partials provide reusability

**Example:**
```jsp
<%@ page import="com.fashionstore.model.Product" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
%>
<% for (Product p : products) { %>
    <div class="product-card">
        <h3><%= p.getProductName() %></h3>
        <span>₹<%= p.getPrice() %></span>
    </div>
<% } %>
```

---

## Database Schema Design

### Database Normalization

The database is normalized to **3NF**:

- **1NF**: All columns are atomic
- **2NF**: No partial dependencies
- **3NF**: No transitive dependencies

### Table Design Principles

1. **Primary Keys**: Every table has a primary key
2. **Foreign Keys**: Referential integrity enforced
3. **Indexes**: Strategic indexes on frequently queried columns
4. **Timestamps**: All tables include created_at and updated_at
5. **Soft Deletes**: Important records use active flags
6. **Constraints**: Appropriate constraints (NOT NULL, UNIQUE, CHECK)

### Key Relationships

```
users (1) ----< (N) orders
orders (1) ----< (N) order_items
products (1) ----< (N) order_items
categories (1) ----< (N) products
products (1) ----< (N) product_sizes
users (1) ----< (N) addresses
users (1) ----< (N) cart_items
users (1) ----< (N) wishlist
```

---

## Security Design

### Multi-Layer Security

**Layer 1: Network**
- Firewall rules
- DDoS protection
- SSL/TLS encryption

**Layer 2: Application**
- Authentication filters
- Authorization checks
- CSRF protection
- Input validation

**Layer 3: Data**
- Password hashing (BCrypt)
- Encrypted sensitive data
- Secure session management
- SQL injection prevention

**Layer 4: Browser**
- Security headers
- Content Security Policy
- HttpOnly cookies
- X-Frame-Options

### Authentication Design

**Password Hashing:**
- BCrypt with 10 rounds
- Salt automatically handled
- No plain text storage

**Session Management:**
- HttpOnly cookies
- Secure flag (production)
- Session fixation prevention
- 30-minute timeout

### Authorization Design

**Role-Based Access Control (RBAC):**
- Three roles: admin, customer, disabled
- Role checks on protected routes
- Admin-only routes protected
- Disabled accounts denied access

---

## Performance Design

### Caching Strategy

**Redis Caching:**
- Product details cached
- Featured products cached
- Category listings cached
- Local fallback when Redis unavailable

**Cache Invalidation:**
- Invalidate on data updates
- Pattern-based invalidation
- TTL-based expiration
- Manual invalidation support

### Database Optimization

**Connection Pooling:**
- HikariCP for efficient pooling
- Configured pool sizes
- Connection validation
- Leak detection

**Query Optimization:**
- Prepared statements
- Batch loading
- Strategic indexes
- Query optimization

### Frontend Optimization

**Customer Frontend:**
- Lazy loading images
- CSS minification
- JavaScript async loading
- Browser caching

**Admin Frontend:**
- Code splitting
- Lazy loading components
- React.memo
- Virtual scrolling

---

## Scalability Design

### Horizontal Scaling

**Backend Scaling:**
- Stateless design enables horizontal scaling
- Load balancer distributes requests
- Session storage externalized (Redis)
- Database read replicas

**Frontend Scaling:**
- Static assets served by CDN
- Admin frontend as SPA
- Customer frontend server-side rendered
- Nginx as reverse proxy

### Vertical Scaling

**Database Scaling:**
- Connection pooling
- Query optimization
- Indexing strategy
- Read replicas

**Cache Scaling:**
- Redis clustering
- Cache partitioning
- Local fallback
- TTL optimization

---

## Deployment Design

### Containerization

**Docker Containers:**
- Backend: Tomcat + Java application
- Admin Frontend: Nginx + React build
- MySQL: Database container
- Redis: Cache container
- Nginx: Reverse proxy

### Orchestration

**Docker Compose:**
- Service definition
- Network configuration
- Volume management
- Health checks

### Environment Configuration

**Environment Variables:**
- Database credentials
- Redis configuration
- Application settings
- Security keys

---

## Conclusion

The FashionStore system design demonstrates a **well-architected, scalable, and secure** e-commerce platform. The layered architecture provides clear separation of concerns, the multi-frontend design optimizes user experience for different user groups, and comprehensive security measures protect against common vulnerabilities. The system is designed for scalability and maintainability, providing a solid foundation for future growth and enhancement.
