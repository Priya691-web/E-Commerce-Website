# FashionStore - Design Architecture Document

## Document Information
- **Project**: FashionStore E-commerce Platform
- **Document Type**: Design Architecture
- **Version**: 1.0
- **Date**: May 15, 2026
- **Author**: Architecture Team
- **Status**: Production-Ready

---

## 1. System Component Architecture

### 1.1 Component Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CLIENT LAYER                                  │
├─────────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐             │
│  │   Browser   │    │  Mobile App │    │  Admin UI   │             │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘             │
│         │                  │                  │                     │
└─────────┼──────────────────┼──────────────────┼─────────────────────┘
          │                  │                  │
          └──────────────────┴──────────────────┘
                           │
                    HTTPS/HTTP
                           │
┌──────────────────────────┼────────────────────────────────────────────┐
│                    WEB SERVER LAYER                                  │
├──────────────────────────┼────────────────────────────────────────────┤
│  ┌───────────────────────┴─────────────────────────────────────┐    │
│  │                    Nginx Reverse Proxy                         │    │
│  │  - SSL Termination                                            │    │
│  │  - Load Balancing                                              │    │
│  │  - Static File Serving                                         │    │
│  └───────────────────────┬─────────────────────────────────────┘    │
└──────────────────────────┼────────────────────────────────────────────┘
                           │
                    HTTP/8080
                           │
┌──────────────────────────┼────────────────────────────────────────────┐
│                APPLICATION SERVER LAYER                             │
├──────────────────────────┼────────────────────────────────────────────┤
│  ┌───────────────────────┴─────────────────────────────────────┐    │
│  │              Apache Tomcat 10.1+                              │    │
│  ├─────────────────────────────────────────────────────────────┤    │
│  │  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐       │    │
│  │  │   Servlets   │  │    Filters   │  │     JSP      │       │    │
│  │  └──────────────┘  └──────────────┘  └──────────────┘       │    │
│  └───────────────────────┬─────────────────────────────────────┘    │
└──────────────────────────┼────────────────────────────────────────────┘
                           │
                    JDBC/Redis Protocol
                           │
┌──────────────────────────┼────────────────────────────────────────────┐
│                   DATA LAYER                                        │
├──────────────────────────┼────────────────────────────────────────────┤
│  ┌───────────────────────┴─────────────────────────────────────┐    │
│  │              MySQL Database 8.0                               │    │
│  │  - Users Table                                                │    │
│  │  - Products Table                                             │    │
│  │  - Orders Table                                                │    │
│  │  - Cart Items Table                                           │    │
│  └───────────────────────┬─────────────────────────────────────┘    │
│  ┌───────────────────────┴─────────────────────────────────────┐    │
│  │              Redis Cache 7.0                                  │    │
│  │  - Session Storage                                            │    │
│  │  - Product Cache                                              │    │
│  │  - Cart Cache                                                  │    │
│  └─────────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 2. Layered Architecture Design

### 2.1 Three-Tier Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                    PRESENTATION TIER                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  Web Browsers & Mobile Devices                               │   │
│  │  - HTML/CSS/JavaScript                                        │   │
│  │  - JSP Templates                                              │   │
│  │  - AJAX Requests                                              │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ HTTP Requests
┌───────────────────────────┼─────────────────────────────────────────┐
│                    APPLICATION TIER                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  CONTROLLER LAYER                                             │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │   │
│  │  │ Product      │ │ Cart         │ │ Order        │        │   │
│  │  │ Controller   │ │ Controller   │ │ Controller   │        │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘        │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  SERVICE LAYER                                               │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │   │
│  │  │ Product      │ │ Cart         │ │ Order        │        │   │
│  │  │ Service      │ │ Service      │ │ Service      │        │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘        │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  BUSINESS LOGIC LAYER                                        │   │
│  │  - Validation Rules                                          │   │
│  │  - Business Calculations                                     │   │
│  │  - Workflow Management                                       │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ JDBC/SQL
┌───────────────────────────┼─────────────────────────────────────────┐
│                    DATA TIER                                        │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  DATA ACCESS LAYER                                           │   │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │   │
│  │  │ Product      │ │ Cart         │ │ Order        │        │   │
│  │  │ DAO          │ │ DAO          │ │ DAO          │        │   │
│  │  └──────────────┘ └──────────────┘ └──────────────┘        │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  DATABASE LAYER                                              │   │
│  │  ┌─────────────────────────────────────────────────────┐   │   │
│  │  │  MySQL Database                                        │   │
│  │  │  - Connection Pool (HikariCP)                          │   │
│  │  │  - Prepared Statements                                 │   │
│  │  │  - Transaction Management                              │   │
│  │  └─────────────────────────────────────────────────────┘   │   │
│  │  ┌─────────────────────────────────────────────────────┐   │   │
│  │  │  Redis Cache                                           │   │
│  │  │  - Distributed Cache                                    │   │
│  │  │  - Session Storage                                      │   │
│  │  │  - Query Results Cache                                  │   │
│  │  └─────────────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. Package Architecture

### 3.1 Package Structure Diagram

```
com.fashionstore
│
├── controller/                    # Presentation Layer
│   ├── ProductController.java
│   ├── CartController.java
│   ├── OrderController.java
│   ├── AuthController.java
│   └── AdminProductController.java
│
├── service/                       # Business Logic Layer
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   └── AuthService.java
│
├── dao/                           # Data Access Interface Layer
│   ├── ProductDAO.java
│   ├── CartDAO.java
│   ├── OrderDAO.java
│   ├── UserDAO.java
│   └── CategoryDAO.java
│
├── daoimpl/                       # Data Access Implementation Layer
│   ├── ProductDAOImpl.java
│   ├── CartDAOImpl.java
│   ├── OrderDAOImpl.java
│   ├── UserDAOImpl.java
│   └── CategoryDAOImpl.java
│
├── model/                         # Domain Model Layer
│   ├── Product.java
│   ├── User.java
│   ├── Order.java
│   ├── Cart.java
│   └── Category.java
│
├── util/                          # Utility Layer
│   ├── DBConnection.java
│   ├── PasswordUtil.java
│   └── ValidationUtil.java
│
├── filter/                        # Security Filter Layer
│   ├── AuthFilter.java
│   └── SecurityFilter.java
│
└── cache/                         # Caching Layer
    ├── RedisCacheService.java
    └── CacheConfig.java
```

---

## 4. Class Architecture

### 4.1 MVC Pattern Class Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                      CONTROLLER CLASS                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ProductController extends HttpServlet                         │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  - ProductDAO productDAO                                      │   │
│  │  - ProductService productService                                │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  + doGet(HttpServletRequest, HttpServletResponse)             │   │
│  │  + doPost(HttpServletRequest, HttpServletResponse)            │   │
│  │  - handleProductSearch()                                      │   │
│  │  - handleProductFilter()                                     │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ uses
┌───────────────────────────┼─────────────────────────────────────────┐
│                      SERVICE CLASS                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ProductService                                              │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  - ProductDAO productDAO                                      │   │
│  │  - CacheService cacheService                                  │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  + getProductById(int): Product                               │   │
│  │  + searchProducts(String): List<Product>                       │   │
│  │  + getFilteredProducts(Filter): List<Product>                  │   │
│  │  - validateProduct(Product): boolean                           │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ implements
┌───────────────────────────┼─────────────────────────────────────────┐
│                      DAO INTERFACE                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  <<interface>> ProductDAO                                    │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  + getAllProducts(): List<Product>                            │   │
│  │  + getProductById(int): Product                                │   │
│  │  + addProduct(Product): int                                    │   │
│  │  + updateProduct(Product): boolean                             │   │
│  │  + deleteProduct(int): boolean                                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ implements
┌───────────────────────────┼─────────────────────────────────────────┐
│                    DAO IMPLEMENTATION                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  ProductDAOImpl implements ProductDAO                         │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  + getAllProducts(): List<Product>                            │   │
│  │  + getProductById(int): Product                                │   │
│  │  + addProduct(Product): int                                    │   │
│  │  + updateProduct(Product): boolean                             │   │
│  │  + deleteProduct(int): boolean                                 │   │
│  │  - executeQuery(String, Object[]): ResultSet                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
└───────────────────────────┬─────────────────────────────────────────┘
                            │ maps to
┌───────────────────────────┼─────────────────────────────────────────┐
│                      MODEL CLASS                                    │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  Product                                                     │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  - int productId                                             │   │
│  │  - String productName                                         │   │
│  │  - String description                                         │   │
│  │  - double price                                               │   │
│  │  - String imageUrl                                            │   │
│  │  - int stockQuantity                                          │   │
│  │  - int categoryId                                             │   │
│  │  - List<ProductSize> sizes                                   │   │
│  ├─────────────────────────────────────────────────────────────┤   │
│  │  + getProductId(): int                                       │   │
│  │  + setProductId(int): void                                   │   │
│  │  + getProductName(): String                                   │   │
│  │  + setProductName(String): void                               │   │
│  │  + getPrice(): double                                         │   │
│  │  + setPrice(double): void                                     │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 5. Database Schema Architecture

### 5.1 Entity-Relationship Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                         USERS TABLE                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  user_id (PK)         INT              AUTO_INCREMENT         │   │
│  │  email                VARCHAR(255)      UNIQUE                │   │
│  │  password_hash        VARCHAR(255)                             │   │
│  │  name                 VARCHAR(100)                             │   │
│  │  role                 ENUM('USER','ADMIN')                   │   │
│  │  created_at           TIMESTAMP        DEFAULT CURRENT_TIMESTAMP │   │
│  │  updated_at           TIMESTAMP                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              │ 1:N                                 │
│                              │                                     │
┌───────────────────────────────┼─────────────────────────────────────┤
│                         ORDERS TABLE                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  order_id (PK)        INT              AUTO_INCREMENT         │   │
│  │  user_id (FK)         INT                                      │   │
│  │  total_amount         DECIMAL(10,2)                           │   │
│  │  status               ENUM('PENDING','PROCESSING',            │   │
│  │                       'SHIPPED','DELIVERED','CANCELLED')      │   │
│  │  shipping_address_id  INT                                      │   │
│  │  payment_method_id    INT                                      │   │
│  │  created_at           TIMESTAMP        DEFAULT CURRENT_TIMESTAMP │   │
│  │  updated_at           TIMESTAMP                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              │ 1:N                                 │
│                              │                                     │
┌───────────────────────────────┼─────────────────────────────────────┤
│                     ORDER_ITEMS TABLE                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  order_item_id (PK)   INT              AUTO_INCREMENT         │   │
│  │  order_id (FK)        INT                                      │   │
│  │  product_id (FK)      INT                                      │   │
│  │  quantity             INT                                      │   │
│  │  price_at_purchase    DECIMAL(10,2)                           │   │
│  │  size_label           VARCHAR(10)                              │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                       PRODUCTS TABLE                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  product_id (PK)      INT              AUTO_INCREMENT         │   │
│  │  product_name         VARCHAR(255)                             │   │
│  │  description          TEXT                                     │   │
│  │  price                DECIMAL(10,2)                           │   │
│  │  discount_percent     DECIMAL(5,2)    DEFAULT 0.00            │   │
│  │  category_id (FK)     INT                                      │   │
│  │  image_url            VARCHAR(500)                             │   │
│  │  stock_quantity       INT              DEFAULT 0               │   │
│  │  is_active            BOOLEAN          DEFAULT TRUE             │   │
│  │  created_at           TIMESTAMP        DEFAULT CURRENT_TIMESTAMP │   │
│  │  updated_at           TIMESTAMP                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              │                                     │
│                              │ N:1                                 │
│                              │                                     │
┌───────────────────────────────┼─────────────────────────────────────┤
│                    CATEGORIES TABLE                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  category_id (PK)     INT              AUTO_INCREMENT         │   │
│  │  category_name        VARCHAR(100)      UNIQUE                │   │
│  │  description          TEXT                                     │   │
│  │  parent_id            INT              NULL                   │   │
│  │  created_at           TIMESTAMP        DEFAULT CURRENT_TIMESTAMP │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                   PRODUCT_SIZES TABLE                               │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  product_size_id (PK) INT              AUTO_INCREMENT         │   │
│  │  product_id (FK)     INT                                      │   │
│  │  size_label           VARCHAR(10)                              │   │
│  │  stock_quantity       INT              DEFAULT 0               │   │
│  │  sku_code             VARCHAR(50)       UNIQUE                │   │
│  │  is_available         BOOLEAN          DEFAULT TRUE             │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 6. Sequence Architecture

### 6.1 User Authentication Sequence Diagram

```
USER                    LOGIN PAGE           AUTH CONTROLLER        AUTH SERVICE         USER DAO           DATABASE
  │                          │                      │                  │                  │                  │
  │─── Enter Credentials ───>│                      │                  │                  │                  │
  │                          │                      │                  │                  │                  │
  │                          │─── POST /login ──────>│                  │                  │                  │
  │                          │                      │                  │                  │                  │
  │                          │                      │─── authenticate()─>│                  │                  │
  │                          │                      │                  │                  │                  │
  │                          │                      │                  │─── findByEmail()─>│                  │
  │                          │                      │                  │                  │                  │
  │                          │                      │                  │                  │─── SELECT... ───>│
  │                          │                      │                  │                  │                  │
  │                          │                      │                  │                  │<─── User Data ────│
  │                          │                      │                  │                  │                  │
  │                          │                      │                  │<─── User Object ───│                  │
  │                          │                      │                  │                  │                  │
  │                          │                      │─── verifyPassword()│                  │                  │
  │                          │                      │                  │                  │                  │
  │                          │<─── Success/Error ────│                  │                  │                  │
  │                          │                      │                  │                  │                  │
  │                          │─── Create Session ───>│                  │                  │                  │
  │                          │                      │                  │                  │                  │
  │<─── Redirect/Show Error ─│                      │                  │                  │                  │
  │                          │                      │                  │                  │                  │
```

### 6.2 Product Purchase Sequence Diagram

```
USER                  PRODUCT PAGE         CART CONTROLLER      CART SERVICE         CART DAO          DATABASE
  │                         │                      │                  │                  │                  │
  │─── View Product ──────>│                      │                  │                  │                  │
  │                         │                      │                  │                  │                  │
  │─── Add to Cart ───────>│                      │                  │                  │                  │
  │                         │─── POST /cart/add ───>│                  │                  │                  │
  │                         │                      │                  │                  │                  │
  │                         │                      │─── addToCart() ───>│                  │                  │
  │                         │                      │                  │                  │                  │
  │                         │                      │                  │─── addItem() ────>│                  │
  │                         │                      │                  │                  │                  │
  │                         │                      │                  │                  │─── INSERT... ────>│
  │                         │                      │                  │                  │                  │
  │                         │                      │                  │                  │<─── Success ──────│
  │                         │                      │                  │                  │                  │
  │                         │                      │                  │<─── Updated Cart ──│                  │
  │                         │                      │                  │                  │                  │
  │                         │<─── Cart Updated ────│                  │                  │                  │
  │                         │                      │                  │                  │                  │
  │<─── Update UI ─────────│                      │                  │                  │                  │
  │                         │                      │                  │                  │                  │
```

---

## 7. Deployment Architecture

### 7.1 Deployment Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                    PRODUCTION ENVIRONMENT                             │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  LOAD BALANCER (Nginx)                                        │   │
│  │  Port: 80/443                                                 │   │
│  │  - SSL Termination                                            │   │
│  │  - Round Robin Load Balancing                                 │   │
│  └───────────────┬───────────────────────────────────────────────┘   │
│                  │                                                   │
│    ┌─────────────┼─────────────┐                                   │
│    │             │             │                                   │
│    ▼             ▼             ▼                                   │
│  ┌─────┐     ┌─────┐     ┌─────┐                                   │
│  │ App │     │ App │     │ App │                                   │
│  │  1  │     │  2  │     │  3  │                                   │
│  └──┬──┘     └──┬──┘     └──┬──┘                                   │
│     │           │           │                                      │
│     └───────────┴───────────┘                                      │
│                 │                                                   │
│                 ▼                                                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  DATABASE CLUSTER (MySQL)                                    │   │
│  │  Master (Read/Write)  ──>  Slave 1 (Read Only)               │   │
│  │                        ──>  Slave 2 (Read Only)               │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  REDIS CLUSTER                                                │   │
│  │  Node 1  ──>  Node 2  ──>  Node 3                            │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 8. Security Architecture

### 8.1 Security Layer Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                    SECURITY ARCHITECTURE                             │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  NETWORK LAYER                                                │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Firewall Rules                                         │    │   │
│  │  │  - Allow HTTP/HTTPS on port 80/443                    │    │   │
│  │  │  - Block direct database access                        │    │   │
│  │  │  - Rate limiting                                       │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  SSL/TLS Encryption                                   │    │   │
│  │  │  - HTTPS only                                         │    │   │
│  │  │  - Certificate validation                            │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  APPLICATION LAYER                                           │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Authentication Filter                                 │    │   │
│  │  │  - Session validation                                 │    │   │
│  │  │  - Token verification                                 │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Authorization Filter                                 │    │   │
│  │  │  - Role-based access control                          │    │   │
│  │  │  - Permission checking                               │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  CSRF Protection                                      │    │   │
│  │  │  - Token validation                                   │    │   │
│  │  │  - Same-site cookies                                  │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Input Validation                                     │    │   │
│  │  │  - Parameter sanitization                             │    │   │
│  │  │  - Type checking                                      │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  DATA LAYER                                                  │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  SQL Injection Prevention                              │    │   │
│  │  │  - Prepared statements                                 │    │   │
│  │  │  - Parameterized queries                              │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Data Encryption                                       │    │   │
│  │  │  - Password hashing (BCrypt)                          │    │   │
│  │  │  - Sensitive data encryption                          │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Access Control                                        │    │   │
│  │  │  - Database user permissions                          │    │   │
│  │  │  - Principle of least privilege                      │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 9. Data Flow Architecture

### 9.1 Request-Response Data Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                    REQUEST DATA FLOW                                │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  1. USER REQUEST                                             │   │
│  │     HTTP Request → URL: /products?search=tshirt              │   │
│  │     Headers: Cookies, User-Agent                             │   │
│  │     Parameters: search, page, limit                           │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  2. FILTER CHAIN                                             │   │
│  │     Auth Filter → Security Filter → CSRF Filter            │   │
│  │     Request validation and preprocessing                     │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  3. CONTROLLER PROCESSING                                    │   │
│  │     ProductController.doGet()                                │   │
│  │     Extract parameters, validate input                        │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  4. SERVICE LAYER                                            │   │
│  │     ProductService.searchProducts(query)                     │   │
│  │     Business logic, validation, caching                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  5. DATA ACCESS LAYER                                       │   │
│  │     ProductDAO.searchProducts(query)                         │   │
│  │     SQL query execution, result mapping                     │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  6. DATABASE QUERY                                           │   │
│  │     SELECT * FROM products WHERE name LIKE ?                │   │
│  │     Connection pool, prepared statement                      │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  7. RESPONSE DATA FLOW                                       │   │
│  │     ResultSet → Product Objects → List<Product>            │   │
│  │     Set request attributes → Forward to JSP                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  8. VIEW RENDERING                                           │   │
│  │     JSP template processing → HTML generation               │   │
│  │     CSS/JavaScript inclusion                                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  9. HTTP RESPONSE                                            │   │
│  │     HTML Response → Browser rendering                        │   │
│  │     Status: 200 OK, Content-Type: text/html                 │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 10. Technology Stack Architecture

### 10.1 Technology Stack Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                    TECHNOLOGY STACK                                 │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  PRESENTATION LAYER                                           │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Frontend Technologies                               │    │   │
│  │  │  - HTML5, CSS3, JavaScript ES6+                     │    │   │
│  │  │  - JSP 3.1.0, JSTL 2.0.0                            │    │   │
│  │  │  - Bootstrap 5 (optional)                            │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  APPLICATION LAYER                                           │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Backend Technologies                                │    │   │
│  │  │  - Java SE 21                                         │    │   │
│  │  │  - Jakarta Servlet 6.0.0                             │    │   │
│  │  │  - Jakarta JSP 3.1.0                                 │    │   │
│  │  │  - Gson 2.10.1 (JSON processing)                     │    │   │
│  │  │  - BCrypt 0.4 (password hashing)                     │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  DATA ACCESS LAYER                                           │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Database Technologies                               │    │   │
│  │  │  - MySQL Connector/J 8.3.0                           │    │   │
│  │  │  - HikariCP 5.1.0 (connection pool)                 │    │   │
│  │  │  - Jedis 5.1.0 (Redis client)                        │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  INFRASTRUCTURE LAYER                                        │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Server & Infrastructure                             │    │   │
│  │  │  - Apache Tomcat 10.1+                               │    │   │
│  │  │  - MySQL 8.0 Database                                │    │   │
│  │  │  - Redis 7.0 Cache                                   │    │   │
│  │  │  - Nginx (reverse proxy)                             │    │   │
│  │  │  - Docker (containerization)                         │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  BUILD & DEPLOYMENT LAYER                                    │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Build Tools                                         │    │   │
│  │  │  - Maven 4.0.0                                       │    │   │
│  │  │  - Git (version control)                             │    │   │
│  │  │  - GitHub Actions (CI/CD)                            │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
│                              ▼                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  MONITORING LAYER                                            │   │
│  │  ┌─────────────────────────────────────────────────────┐    │   │
│  │  │  Monitoring & Logging                                │    │   │
│  │  │  - SLF4J 2.0.7 (logging facade)                      │    │   │
│  │  │  - Logback 1.4.11 (logging implementation)            │    │   │
│  │  │  - Prometheus (metrics collection)                   │    │   │
│  │  │  - Grafana (visualization)                           │    │   │
│  │  └─────────────────────────────────────────────────────┘    │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 11. Design Patterns Architecture

### 11.1 Design Patterns Implementation

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DESIGN PATTERNS                                   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  MVC PATTERN                                                  │   │
│  │  Model (POJOs) → View (JSP) → Controller (Servlet)          │   │
│  │  Separation of concerns across layers                        │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  DAO PATTERN                                                  │   │
│  │  Interface (ProductDAO) → Implementation (ProductDAOImpl)   │   │
│  │  Database operation abstraction                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  SINGLETON PATTERN                                            │   │
│  │  DBConnection.getInstance() → Single connection pool        │   │
│  │  Resource efficiency                                        │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  FACTORY PATTERN                                              │   │
│  │  DAOFactory.getDAO(type) → Appropriate DAO instance         │   │
│  │  Object creation abstraction                                │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  STRATEGY PATTERN                                             │   │
│  │  PaymentGateway → Stripe/Razorpay implementations           │   │
│  │  Algorithm selection at runtime                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  FILTER CHAIN PATTERN                                        │   │
│  │  AuthFilter → SecurityFilter → CSRFFilter → Resource       │   │
│  │  Request preprocessing pipeline                             │   │
│  └─────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │  FRONT CONTROLLER PATTERN                                    │   │
│  │  Central servlet dispatches to appropriate controllers      │   │
│  │  Centralized request handling                               │   │
│  └─────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 12. Conclusion

This Design Architecture document provides a comprehensive visual representation of the FashionStore e-commerce platform's system design. The diagrams illustrate the component relationships, data flow, security architecture, and technology stack in a format suitable for technical documentation and audit reports.

### 12.1 Architecture Highlights
- **Layered Architecture**: Clear separation of concerns across presentation, business logic, and data access layers
- **Design Patterns**: Implementation of industry-standard patterns (MVC, DAO, Singleton, Factory, Strategy)
- **Security**: Multi-layer security approach with authentication, authorization, and data protection
- **Scalability**: Stateless design enabling horizontal scaling
- **Performance**: Connection pooling, caching, and optimized database queries

### 12.2 Design Principles Applied
- **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion
- **DRY Principle**: Code reusability and maintainability
- **KISS Principle**: Simple, straightforward solutions
- **Separation of Concerns**: Each layer has distinct responsibilities

---

**Document Status**: Complete  
**Last Updated**: May 15, 2026  
**Next Review**: June 15, 2026
