# FashionStore - Visual Architecture Diagrams

## Document Information
- **Project**: FashionStore E-commerce Platform
- **Document Type**: Visual Architecture Diagrams
- **Version**: 1.0
- **Date**: May 15, 2026
- **Author**: Architecture Team
- **Status**: Production-Ready

---

## 1. System Architecture Overview

```mermaid
graph TB
    subgraph Client["Client Layer"]
        Browser[Web Browser]
        Mobile[Mobile App]
        Admin[Admin Panel]
    end
    
    subgraph WebServer["Web Server Layer"]
        Nginx[Nginx Reverse Proxy<br/>Port 80/443]
    end
    
    subgraph AppServer["Application Server Layer"]
        Tomcat[Apache Tomcat<br/>Port 8080]
    end
    
    subgraph DataLayer["Data Layer"]
        MySQL[(MySQL Database<br/>Port 3306)]
        Redis[(Redis Cache<br/>Port 6379)]
    end
    
    Browser --> Nginx
    Mobile --> Nginx
    Admin --> Nginx
    Nginx --> Tomcat
    Tomcat --> MySQL
    Tomcat --> Redis
    
    style Client fill:#e1f5ff
    style WebServer fill:#fff4e1
    style AppServer fill:#e1ffe1
    style DataLayer fill:#ffe1f5
```

---

## 2. Layered Architecture

```mermaid
graph TB
    subgraph Presentation["Presentation Layer"]
        Controllers[Controllers<br/>Servlets]
        Views[Views<br/>JSP Templates]
        Filters[Filters<br/>Security/Auth]
    end
    
    subgraph Business["Business Logic Layer"]
        Services[Service Layer<br/>Business Rules]
        Validation[Validation<br/>Business Logic]
        Integration[External Integration<br/>Payment/Email]
    end
    
    subgraph DataAccess["Data Access Layer"]
        DAO[DAO Interfaces<br/>Data Access Contracts]
        DAOImpl[DAO Implementations<br/>Database Operations]
        Connection[Connection Pool<br/>HikariCP]
    end
    
    subgraph Data["Data Persistence Layer"]
        Database[(MySQL Database)]
        Cache[(Redis Cache)]
    end
    
    Controllers --> Services
    Views --> Controllers
    Filters --> Controllers
    
    Services --> DAO
    Validation --> Services
    Integration --> Services
    
    DAO --> DAOImpl
    DAOImpl --> Connection
    
    Connection --> Database
    Connection --> Cache
    
    style Presentation fill:#e1f5ff
    style Business fill:#fff4e1
    style DataAccess fill:#e1ffe1
    style Data fill:#ffe1f5
```

---

## 3. MVC Pattern Architecture

```mermaid
classDiagram
    class Controller {
        +doGet(request, response)
        +doPost(request, response)
        -handleRequest()
    }
    
    class Service {
        +processData()
        +validateData()
        -businessLogic()
    }
    
    class DAO {
        +findById(id)
        +findAll()
        +save(entity)
        +delete(id)
    }
    
    class Model {
        -id
        -name
        -value
        +getId()
        +setName()
    }
    
    class View {
        +render(model)
        +display()
    }
    
    Controller --> Service : uses
    Service --> DAO : uses
    DAO --> Model : maps to
    Controller --> View : renders
    View --> Model : displays
```

---

## 4. Database Schema Design

```mermaid
erDiagram
    USERS ||--o{ ORDERS : places
    USERS ||--o{ CART_ITEMS : has
    USERS ||--o{ WISHLIST : has
    USERS ||--o{ ADDRESSES : has
    USERS ||--o{ PAYMENT_METHODS : has
    
    PRODUCTS ||--o{ ORDER_ITEMS : contains
    PRODUCTS ||--o{ CART_ITEMS : in
    PRODUCTS ||--o{ WISHLIST : in
    PRODUCTS ||--o{ PRODUCT_SIZES : has
    PRODUCTS ||--|| CATEGORIES : belongs_to
    
    ORDERS ||--o{ ORDER_ITEMS : contains
    ORDERS }o--|| ADDRESSES : ships_to
    ORDERS }o--|| PAYMENT_METHODS : paid_with
    
    CATEGORIES ||--o{ PRODUCTS : includes
    
    USERS {
        int user_id PK
        string email UK
        string password_hash
        string name
        string role
        timestamp created_at
    }
    
    PRODUCTS {
        int product_id PK
        string product_name
        text description
        decimal price
        decimal discount_percent
        int category_id FK
        string image_url
        int stock_quantity
        boolean is_active
        timestamp created_at
    }
    
    ORDERS {
        int order_id PK
        int user_id FK
        decimal total_amount
        string status
        int shipping_address_id FK
        int payment_method_id FK
        timestamp created_at
    }
    
    ORDER_ITEMS {
        int order_item_id PK
        int order_id FK
        int product_id FK
        int quantity
        decimal price_at_purchase
        string size_label
    }
    
    CART_ITEMS {
        int cart_item_id PK
        int user_id FK
        int product_id FK
        int quantity
        string size_label
    }
    
    CATEGORIES {
        int category_id PK
        string category_name
        text description
        int parent_id FK
    }
    
    PRODUCT_SIZES {
        int product_size_id PK
        int product_id FK
        string size_label
        int stock_quantity
        string sku_code
        boolean is_available
    }
```

---

## 5. Authentication Flow

```mermaid
sequenceDiagram
    participant User as 👤 User
    participant Login as 🔐 Login Page
    participant AuthController as 🎛️ Auth Controller
    participant AuthService as 🔑 Auth Service
    participant UserDAO as 📊 User DAO
    participant Database as 💾 Database
    participant Session as 🍪 Session
    
    User->>Login: Enter Credentials
    Login->>AuthController: POST /login
    AuthController->>AuthService: authenticate(email, password)
    AuthService->>UserDAO: findByEmail(email)
    UserDAO->>Database: SELECT * FROM users WHERE email = ?
    Database-->>UserDAO: User Data
    UserDAO-->>AuthService: User Object
    AuthService->>AuthService: verifyPassword(password, hash)
    
    alt Valid Credentials
        AuthService-->>AuthController: User Object
        AuthController->>Session: Set Session Attributes
        AuthController-->>Login: Success Response
        Login-->>User: Redirect to Dashboard
    else Invalid Credentials
        AuthController-->>Login: Error Response
        Login-->>User: Show Error Message
    end
```

---

## 6. Product Purchase Flow

```mermaid
sequenceDiagram
    participant User as 👤 User
    participant Product as 🛍️ Product Page
    participant CartController as 🛒 Cart Controller
    participant CartService as 📦 Cart Service
    participant CartDAO as 📊 Cart DAO
    participant Database as 💾 Database
    participant Cache as ⚡ Redis Cache
    
    User->>Product: View Product
    User->>Product: Add to Cart
    Product->>CartController: POST /cart/add
    CartController->>CartService: addToCart(productId, quantity)
    CartService->>Cache: Check Cache
    Cache-->>CartService: Cache Miss
    CartService->>CartDAO: addItem(cartItem)
    CartDAO->>Database: INSERT INTO cart_items
    Database-->>CartDAO: Success
    CartDAO-->>CartService: Updated Cart
    CartService->>Cache: Update Cache
    CartService-->>CartController: Cart Updated
    CartController-->>Product: JSON Response
    Product-->>User: Update UI
```

---

## 7. Deployment Architecture

```mermaid
graph TB
    subgraph Production["Production Environment"]
        subgraph LB["Load Balancer"]
            Nginx[Nginx<br/>Port 80/443]
        end
        
        subgraph AppTier["Application Tier"]
            App1[App Server 1<br/>Tomcat]
            App2[App Server 2<br/>Tomcat]
            App3[App Server 3<br/>Tomcat]
        end
        
        subgraph DataTier["Data Tier"]
            Master[(MySQL Master<br/>Read/Write)]
            Slave1[(MySQL Slave 1<br/>Read Only)]
            Slave2[(MySQL Slave 2<br/>Read Only)]
            RedisCluster[Redis Cluster<br/>Node 1,2,3]
        end
    end
    
    Nginx --> App1
    Nginx --> App2
    Nginx --> App3
    
    App1 --> Master
    App2 --> Master
    App3 --> Master
    
    App1 --> Slave1
    App2 --> Slave1
    App3 --> Slave1
    
    App1 --> Slave2
    App2 --> Slave2
    App3 --> Slave2
    
    App1 --> RedisCluster
    App2 --> RedisCluster
    App3 --> RedisCluster
    
    Master --> Slave1
    Master --> Slave2
    
    style LB fill:#e1f5ff
    style AppTier fill:#fff4e1
    style DataTier fill:#e1ffe1
```

---

## 8. Security Architecture

```mermaid
graph TB
    subgraph Network["Network Layer"]
        Firewall[Firewall<br/>Port Filtering]
        SSL[SSL/TLS<br/>Encryption]
        DDoS[DDoS Protection<br/>Rate Limiting]
    end
    
    subgraph Application["Application Layer"]
        AuthFilter[Authentication Filter<br/>Session Validation]
        AuthzFilter[Authorization Filter<br/>Role-Based Access]
        CSRF[CSRF Protection<br/>Token Validation]
        InputValidation[Input Validation<br/>Sanitization]
    end
    
    subgraph Data["Data Layer"]
        SQLInjection[SQL Injection Prevention<br/>Prepared Statements]
        Encryption[Data Encryption<br/>BCrypt Hashing]
        AccessControl[Access Control<br/>Least Privilege]
        Audit[Audit Logging<br/>Security Events]
    end
    
    Firewall --> SSL
    SSL --> DDoS
    DDoS --> AuthFilter
    
    AuthFilter --> AuthzFilter
    AuthzFilter --> CSRF
    CSRF --> InputValidation
    
    InputValidation --> SQLInjection
    SQLInjection --> Encryption
    Encryption --> AccessControl
    AccessControl --> Audit
    
    style Network fill:#e1f5ff
    style Application fill:#fff4e1
    style Data fill:#e1ffe1
```

---

## 9. Data Flow Architecture

```mermaid
graph LR
    A[User Request] --> B[Filter Chain]
    B --> C[Controller]
    C --> D[Service Layer]
    D --> E[Cache Check]
    E -->|Cache Hit| F[Return Data]
    E -->|Cache Miss| G[DAO Layer]
    G --> H[Database Query]
    H --> I[Result Mapping]
    I --> J[Cache Update]
    J --> K[Service Processing]
    K --> L[View Rendering]
    L --> M[HTTP Response]
    M --> N[User Browser]
    
    style A fill:#ff6b6b
    style B fill:#feca57
    style C fill:#48dbfb
    style D fill:#ff9ff3
    style E fill:#54a0ff
    style F fill:#5f27cd
    style G fill:#00d2d3
    style H fill:#1dd1a1
    style I fill:#ff6b6b
    style J fill:#feca57
    style K fill:#48dbfb
    style L fill:#ff9ff3
    style M fill:#54a0ff
    style N fill:#5f27cd
```

---

## 10. Technology Stack

```mermaid
mindmap
    root((FashionStore<br/>Tech Stack))
        Frontend
            HTML5
            CSS3
            JavaScript ES6+
            JSP Templates
            JSTL
        Backend
            Java SE 21
            Jakarta Servlet 6.0
            Jakarta JSP 3.1
            Gson JSON
            BCrypt
        Database
            MySQL 8.0
            Redis 7.0
            HikariCP Pool
        Infrastructure
            Apache Tomcat 10.1+
            Nginx Proxy
            Docker
            Docker Compose
        Build
            Maven 4.0
            Git
            GitHub Actions
        Monitoring
            SLF4J
            Logback
            Prometheus
            Grafana
        Security
            SSL/TLS
            BCrypt Hashing
            CSRF Protection
            RBAC
```

---

## 11. Design Patterns

```mermaid
graph TB
    subgraph Patterns["Design Patterns"]
        MVC[MVC Pattern<br/>Model-View-Controller]
        DAO[DAO Pattern<br/>Data Access Object]
        Singleton[Singleton Pattern<br/>Connection Pool]
        Factory[Factory Pattern<br/>DAO Factory]
        Strategy[Strategy Pattern<br/>Payment Gateways]
        Filter[Filter Chain Pattern<br/>Request Pipeline]
        FrontController[Front Controller<br/>Central Dispatcher]
    end
    
    subgraph Implementation["Implementation"]
        Controllers[Servlet Controllers]
        Services[Service Classes]
        DAOs[DAO Interfaces/Impls]
        DBConnection[Connection Manager]
        DAOFactory[DAO Factory]
        PaymentGateway[Payment Interfaces]
        Filters[Security Filters]
        Dispatcher[Servlet Dispatcher]
    end
    
    MVC --> Controllers
    MVC --> Services
    MVC --> DAOs
    
    DAO --> DAOs
    DAO --> DBConnection
    
    Singleton --> DBConnection
    
    Factory --> DAOFactory
    
    Strategy --> PaymentGateway
    
    Filter --> Filters
    
    FrontController --> Dispatcher
    
    style Patterns fill:#e1f5ff
    style Implementation fill:#fff4e1
```

---

## 12. Package Structure

```mermaid
graph TB
    subgraph Root["com.fashionstore"]
        controller["controller<br/>Request Handlers"]
        service["service<br/>Business Logic"]
        dao["dao<br/>Data Interfaces"]
        daoimpl["daoimpl<br/>Data Implementations"]
        model["model<br/>Domain Objects"]
        util["util<br/>Utilities"]
        filter["filter<br/>Security Filters"]
        cache["cache<br/>Caching Layer"]
    end
    
    controller -->|uses| service
    service -->|uses| dao
    service -->|uses| cache
    daoimpl -->|implements| dao
    daoimpl -->|uses| model
    daoimpl -->|uses| util
    filter -->|protects| controller
    cache -->|caches| model
    
    style Root fill:#e1f5ff
    style controller fill:#fff4e1
    style service fill:#e1ffe1
    style dao fill:#ffe1f5
    style daoimpl fill:#ff6b6b
    style model fill:#feca57
    style util fill:#48dbfb
    style filter fill:#ff9ff3
    style cache fill:#54a0ff
```

---

## 13. API Architecture

```mermaid
graph TB
    subgraph Public["Public Endpoints"]
        Home[GET /]
        Products[GET /products]
        ProductDetail[GET /products/:id]
        Login[POST /login]
        Register[POST /register]
    end
    
    subgraph Protected["Protected Endpoints"]
        Cart[GET /cart]
        AddToCart[POST /cart/add]
        Wishlist[GET /wishlist]
        Profile[GET /profile]
        Orders[GET /orders]
    end
    
    subgraph Admin["Admin Endpoints"]
        Dashboard[GET /admin/dashboard]
        AdminProducts[GET /admin/products]
        CreateProduct[POST /admin/products]
        AdminOrders[GET /admin/orders]
        Users[GET /admin/users]
    end
    
    subgraph Monitoring["Monitoring Endpoints"]
        Health[GET /health]
        Metrics[GET /metrics]
        Status[GET /status]
    end
    
    style Public fill:#e1f5ff
    style Protected fill:#fff4e1
    style Admin fill:#ffe1f5
    style Monitoring fill:#e1ffe1
```

---

## 14. Caching Architecture

```mermaid
graph TB
    subgraph Cache["Multi-Layer Caching"]
        Browser[Browser Cache<br/>Static Resources]
        CDN[CDN Cache<br/>Global Distribution]
        Redis[Redis Cache<br/>Application Cache]
        QueryCache[Query Cache<br/>Database Cache]
    end
    
    subgraph Data["Cached Data"]
        Products[Product Catalog]
        Sessions[User Sessions]
        Cart[Shopping Cart]
        Categories[Categories]
        Users[User Data]
    end
    
    Browser --> CDN
    CDN --> Redis
    Redis --> QueryCache
    
    Redis --> Products
    Redis --> Sessions
    Redis --> Cart
    Redis --> Categories
    Redis --> Users
    
    style Cache fill:#e1f5ff
    style Data fill:#fff4e1
```

---

## 15. Error Handling Architecture

```mermaid
graph TB
    subgraph Errors["Error Sources"]
        Validation[Validation Errors<br/>Input Issues]
        Business[Business Errors<br/>Rule Violations]
        System[System Errors<br/>Database/Network]
        Security[Security Errors<br/>Auth/Authorization]
    end
    
    subgraph Handling["Error Handling"]
        TryCatch[Try-Catch Blocks<br/>Local Handling]
        Filter[Error Filter<br/>Global Handler]
        Logger[Error Logging<br/>Comprehensive]
        UserMsg[User Messages<br/>Friendly Output]
    end
    
    subgraph Response["Error Response"]
        HTTP[HTTP Status Codes<br/>400/401/403/404/500]
        JSON[JSON Error Response<br/>Structured Format]
        Metrics[Error Metrics<br/>Monitoring]
        Alert[Alerting<br/>Team Notification]
    end
    
    Validation --> TryCatch
    Business --> Filter
    System --> Filter
    Security --> Filter
    
    TryCatch --> Logger
    Filter --> Logger
    
    Logger --> HTTP
    Logger --> JSON
    Logger --> Metrics
    Logger --> Alert
    
    HTTP --> UserMsg
    JSON --> UserMsg
    
    style Errors fill:#ff6b6b
    style Handling fill:#feca57
    style Response fill:#48dbfb
```

---

## 16. Conclusion

This document provides comprehensive visual architecture diagrams for the FashionStore e-commerce platform. All diagrams use Mermaid syntax and can be rendered in any Markdown viewer that supports Mermaid.

### Key Architectural Highlights:
- **Layered Architecture**: Clear separation of concerns
- **Design Patterns**: Industry-standard patterns (MVC, DAO, Singleton)
- **Security**: Multi-layer security approach
- **Scalability**: Stateless design for horizontal scaling
- **Performance**: Caching and connection pooling

---

**Document Status**: Complete  
**Last Updated**: May 15, 2026  
**Next Review**: June 15, 2026
