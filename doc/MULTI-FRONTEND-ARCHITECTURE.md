# FashionStore Multi-Frontend Architecture Design

## Executive Summary

This document outlines a portfolio-grade, scalable multi-frontend architecture for FashionStore, separating the customer storefront, admin dashboard, and shared backend API into distinct, independently deployable services.

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              Production Environment                             │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐  │
│  │   Customer SPA   │        │   Admin SPA      │        │   Backend API    │  │
│  │   (React/Vue)    │        │   (React/Vue)    │        │  (Spring Boot)   │  │
│  │   Port 80/443    │        │   Port 80/443    │        │   Port 8080      │  │
│  │   Nginx/CDN      │        │   Nginx/CDN      │        │   Internal Only  │  │
│  └────────┬────────┘        └────────┬────────┘        └────────┬────────┘  │
│           │                          │                          │             │
│           │ HTTPS                   │ HTTPS                   │ HTTP        │
│           │                          │                          │             │
│           └──────────────┬───────────┴──────────────────────────┘             │
│                          │                                                │
│                    ┌─────▼────────┐                                      │
│                    │   Load       │                                      │
│                    │   Balancer   │                                      │
│                    │   (Nginx)    │                                      │
│                    └──────────────┘                                      │
│                                                                              │
│  ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐  │
│  │   MySQL DB      │        │   Redis Cache   │        │   File Storage   │  │
│  │   (Primary)     │        │   (Session/     │        │   (S3/MinIO)     │  │
│  │                 │        │    Cache)       │        │                 │  │
│  └─────────────────┘        └─────────────────┘        └─────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                           Development Environment                               │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                              │
│  ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐  │
│  │   Customer SPA   │        │   Admin SPA      │        │   Backend API    │  │
│  │   localhost:3000 │        │   localhost:3001 │        │   localhost:8080 │  │
│  │   Vite/Dev       │        │   Vite/Dev       │        │   Spring Boot    │  │
│  └────────┬────────┘        └────────┬────────┘        └────────┬────────┘  │
│           │                          │                          │             │
│           │ HTTP/CORS                │ HTTP/CORS                │ JDBC         │
│           │                          │                          │             │
│           └──────────────┬───────────┴──────────────────────────┘             │
│                          │                                                │
│                    ┌─────▼────────┐                                      │
│                    │   MySQL DB     │                                      │
│                    │   localhost:   │                                      │
│                    │   3306         │                                      │
│                    └────────────────┘                                      │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## Folder Structure

```
FashionStore/
├── backend/                          # Shared Backend API (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/fashionstore/
│   │   │   │   ├── api/              # REST Controllers
│   │   │   │   │   ├── public/       # Customer-facing endpoints
│   │   │   │   │   │   ├── ProductController.java
│   │   │   │   │   │   ├── CartController.java
│   │   │   │   │   │   ├── OrderController.java
│   │   │   │   │   │   ├── AuthController.java
│   │   │   │   │   │   └── UserController.java
│   │   │   │   │   ├── admin/        # Admin-only endpoints
│   │   │   │   │   │   ├── AdminProductController.java
│   │   │   │   │   │   ├── AdminOrderController.java
│   │   │   │   │   │   ├── AdminUserController.java
│   │   │   │   │   │   └── DashboardController.java
│   │   │   │   ├── service/          # Business Logic
│   │   │   │   ├── repository/       # Data Access
│   │   │   │   ├── model/            # Domain Models
│   │   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── security/         # JWT/Security
│   │   │   │   ├── config/           # Configuration
│   │   │   │   └── util/             # Utilities
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── application-dev.yml
│   │   │       └── application-prod.yml
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── storefront/                       # Customer Frontend (React/Vue)
│   ├── src/
│   │   ├── components/              # Reusable components
│   │   │   ├── common/              # Shared components
│   │   │   ├── product/             # Product-related
│   │   │   ├── cart/                # Cart components
│   │   │   ├── checkout/            # Checkout flow
│   │   │   ├── account/             # User account
│   │   │   └── layout/              # Layout components
│   │   ├── pages/                   # Page components
│   │   │   ├── Home.tsx
│   │   │   ├── Products.tsx
│   │   │   ├── ProductDetail.tsx
│   │   │   ├── Cart.tsx
│   │   │   ├── Checkout.tsx
│   │   │   ├── Account.tsx
│   │   │   └── Login.tsx
│   │   ├── services/                # API service layer
│   │   │   ├── api.ts               # Axios configuration
│   │   │   ├── productService.ts
│   │   │   ├── cartService.ts
│   │   │   ├── authService.ts
│   │   │   └── userService.ts
│   │   ├── hooks/                   # Custom React hooks
│   │   ├── store/                   # State management (Redux/Zustand)
│   │   ├── utils/                   # Utility functions
│   │   ├── types/                   # TypeScript types
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── Dockerfile
│
├── admin/                            # Admin Dashboard (React/Vue)
│   ├── src/
│   │   ├── components/
│   │   │   ├── common/
│   │   │   ├── dashboard/           # Dashboard widgets
│   │   │   ├── products/            # Product management
│   │   │   ├── orders/              # Order management
│   │   │   ├── users/               # User management
│   │   │   └── analytics/           # Analytics components
│   │   ├── pages/
│   │   │   ├── Dashboard.tsx
│   │   │   ├── Products.tsx
│   │   │   ├── Orders.tsx
│   │   │   ├── Users.tsx
│   │   │   ├── Analytics.tsx
│   │   │   └── Login.tsx
│   │   ├── services/
│   │   │   ├── api.ts
│   │   │   ├── adminProductService.ts
│   │   │   ├── adminOrderService.ts
│   │   │   └── adminAuthService.ts
│   │   ├── hooks/
│   │   ├── store/
│   │   ├── utils/
│   │   ├── types/
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── public/
│   ├── package.json
│   ├── vite.config.ts
│   ├── tsconfig.json
│   └── Dockerfile
│
├── shared/                           # Shared code between frontends
│   ├── types/                         # Shared TypeScript types
│   │   ├── product.types.ts
│   │   ├── user.types.ts
│   │   └── order.types.ts
│   ├── constants/                     # Shared constants
│   │   └── api.constants.ts
│   └── utils/                         # Shared utilities
│       └── validation.utils.ts
│
├── infrastructure/                   # Infrastructure as Code
│   ├── docker/
│   │   ├── docker-compose.yml
│   │   ├── docker-compose.dev.yml
│   │   └── docker-compose.prod.yml
│   ├── kubernetes/                   # K8s manifests (optional)
│   │   ├── backend-deployment.yaml
│   │   ├── storefront-deployment.yaml
│   │   └── admin-deployment.yaml
│   └── nginx/                        # Nginx configuration
│       ├── nginx.conf
│       └── ssl/
│
├── database/                         # Database migrations
│   ├── migrations/
│   │   ├── 001_initial_schema.sql
│   │   ├── 002_user_tables.sql
│   │   └── 003_address_tables.sql
│   └── seeds/                        # Seed data
│
├── scripts/                          # Utility scripts
│   ├── setup.sh                      # Development setup
│   ├── build.sh                      # Build all services
│   └── deploy.sh                     # Deployment script
│
├── docs/                             # Documentation
│   ├── API.md                        # API documentation
│   ├── ARCHITECTURE.md               # This file
│   └── DEPLOYMENT.md                 # Deployment guide
│
├── README.md                         # Project README
├── .gitignore
└── docker-compose.yml                # Root docker-compose
```

---

## Routing Structure

### Backend API Routing (Spring Boot)

```
/api/v1/
├── /public/                          # Customer-facing endpoints
│   ├── /products
│   │   ├── GET    /                  # List products
│   │   ├── GET    /{id}             # Get product detail
│   │   ├── GET    /search           # Search products
│   │   └── GET    /category/{cat}    # Products by category
│   ├── /cart
│   │   ├── GET    /                  # Get user cart
│   │   ├── POST   /add              # Add to cart
│   │   ├── PUT    /update/{id}      # Update cart item
│   │   ├── DELETE /{id}            # Remove cart item
│   │   └── DELETE /                  # Clear cart
│   ├── /orders
│   │   ├── GET    /                  # Get user orders
│   │   ├── GET    /{id}             # Get order detail
│   │   └── POST   /                 # Create order
│   ├── /auth
│   │   ├── POST   /register         # User registration
│   │   ├── POST   /login            # User login
│   │   ├── POST   /logout           # User logout
│   │   ├── POST   /refresh          # Refresh JWT token
│   │   └── GET    /me               # Get current user
│   ├── /account
│   │   ├── GET    /profile          # Get user profile
│   │   ├── PUT    /profile          # Update profile
│   │   ├── GET    /addresses        # Get user addresses
│   │   ├── POST   /addresses        # Add address
│   │   └── DELETE /addresses/{id}   # Delete address
│   └── /checkout
│       ├── POST   /                 # Process checkout
│       └── GET    /                 # Get checkout summary
│
└── /admin/                           # Admin-only endpoints
    ├── /products
    │   ├── GET    /                  # List all products
    │   ├── POST   /                  # Create product
    │   ├── PUT    /{id}             # Update product
    │   ├── DELETE /{id}             # Delete product
    │   └── POST   /{id}/stock       # Update stock
    ├── /orders
    │   ├── GET    /                  # List all orders
    │   ├── GET    /{id}             # Get order detail
    │   ├── PUT    /{id}/status      # Update order status
    │   └── GET    /stats            # Order statistics
    ├── /users
    │   ├── GET    /                  # List all users
    │   ├── GET    /{id}             # Get user detail
    │   ├── PUT    /{id}/role        # Update user role
    │   └── DELETE /{id}             # Delete user
    ├── /dashboard
    │   ├── GET    /stats            # Dashboard statistics
    │   ├── GET    /sales            # Sales data
    │   └── GET    /analytics        # Analytics data
    └── /settings
        ├── GET    /                 # Get system settings
        └── PUT    /                 # Update system settings
```

### Storefront Frontend Routing (React Router)

```
/                                    # Home page
/products                            # Product listing
/product/:id                         # Product detail
/cart                                # Shopping cart
/checkout                            # Checkout flow
/account                             # Account dashboard
/account/profile                      # Profile settings
/account/orders                       # Order history
/account/addresses                    # Address management
/account/wishlist                     # Wishlist
/login                               # Login page
/register                            # Registration page
/forgot-password                      # Forgot password
/reset-password/:token               # Reset password
```

### Admin Dashboard Routing (React Router)

```
/admin/login                          # Admin login
/admin/dashboard                      # Dashboard
/admin/products                       # Product management
/admin/products/new                   # Create product
/admin/products/:id/edit             # Edit product
/admin/orders                         # Order management
/admin/orders/:id                     # Order detail
/admin/users                          # User management
/admin/users/:id                      # User detail
/admin/analytics                      # Analytics
/admin/settings                       # System settings
```

---

## Backend API Structure

### Technology Stack
- **Framework**: Spring Boot 3.x (Java 21)
- **Build Tool**: Maven
- **Database**: MySQL 8.0
- **Cache**: Redis
- **Security**: Spring Security + JWT
- **API Documentation**: OpenAPI/Swagger

### Package Structure

```
com.fashionstore.api
├── config/
│   ├── SecurityConfig.java           # JWT + CORS configuration
│   ├── WebConfig.java                # Web MVC configuration
│   ├── RedisConfig.java             # Redis configuration
│   └── DataSourceConfig.java       # Database configuration
│
├── security/
│   ├── JwtTokenProvider.java        # JWT token generation/validation
│   ├── JwtAuthenticationFilter.java # JWT filter
│   ├── UserDetailsServiceImpl.java  # User details service
│   └── RoleBasedAccessControl.java  # RBAC logic
│
├── public/
│   ├── ProductController.java       # Public product endpoints
│   ├── CartController.java          # Public cart endpoints
│   ├── OrderController.java         # Public order endpoints
│   ├── AuthController.java          # Public auth endpoints
│   └── UserController.java         # Public user endpoints
│
├── admin/
│   ├── AdminProductController.java  # Admin product management
│   ├── AdminOrderController.java   # Admin order management
│   ├── AdminUserController.java    # Admin user management
│   ├── DashboardController.java    # Dashboard analytics
│   └── SettingsController.java     # System settings
│
├── service/
│   ├── ProductService.java
│   ├── CartService.java
│   ├── OrderService.java
│   ├── AuthService.java
│   ├── UserService.java
│   └── NotificationService.java
│
├── repository/
│   ├── ProductRepository.java       # JPA Repository
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   ├── UserRepository.java
│   └── CustomRepository.java      # Custom queries
│
├── model/
│   ├── Product.java
│   ├── Cart.java
│   ├── Order.java
│   ├── User.java
│   └── BaseEntity.java            # Common fields (id, timestamps)
│
├── dto/
│   ├── request/                     # Request DTOs
│   │   ├── ProductRequest.java
│   │   ├── CartRequest.java
│   │   └── OrderRequest.java
│   └── response/                    # Response DTOs
│       ├── ProductResponse.java
│       ├── CartResponse.java
│       └── OrderResponse.java
│
└── exception/
    ├── ResourceNotFoundException.java
    ├── ValidationException.java
    └── GlobalExceptionHandler.java
```

### API Response Format

```json
{
  "success": true,
  "data": { /* actual data */ },
  "message": "Operation successful",
  "timestamp": "2026-05-10T10:00:00Z",
  "path": "/api/v1/public/products"
}
```

### Error Response Format

```json
{
  "success": false,
  "error": {
    "code": "PRODUCT_NOT_FOUND",
    "message": "Product with id 123 not found",
    "details": []
  },
  "timestamp": "2026-05-10T10:00:00Z",
  "path": "/api/v1/public/products/123"
}
```

---

## Authentication Flow

### Customer Authentication

```
┌──────────────┐
│ Customer SPA │
└──────┬───────┘
       │ 1. POST /api/v1/public/auth/login
       │    { email, password }
       │
       ▼
┌──────────────┐
│ Backend API  │
└──────┬───────┘
       │ 2. Validate credentials
       │ 3. Generate JWT (role: CUSTOMER)
       │ 4. Return JWT + user data
       │
       ▼
┌──────────────┐
│ Customer SPA │
└──────┬───────┘
       │ 5. Store JWT in localStorage/cookie
       │ 6. Include JWT in Authorization header
       │    Bearer <token>
       │
       ▼
┌──────────────┐
│ Backend API  │
└──────┬───────┘
       │ 7. Validate JWT
       │ 8. Check role == CUSTOMER
       │ 9. Process request
```

### Admin Authentication

```
┌──────────────┐
│  Admin SPA   │
└──────┬───────┘
       │ 1. POST /api/v1/admin/auth/login
       │    { email, password }
       │
       ▼
┌──────────────┐
│ Backend API  │
└──────┬───────┘
       │ 2. Validate credentials
       │ 3. Check role == ADMIN
       │ 4. Generate JWT (role: ADMIN)
       │ 5. Return JWT + user data
       │
       ▼
┌──────────────┐
│  Admin SPA   │
└──────┬───────┘
       │ 6. Store JWT in localStorage/cookie
       │ 7. Include JWT in Authorization header
       │    Bearer <token>
       │
       ▼
┌──────────────┐
│ Backend API  │
└──────┬───────┘
       │ 8. Validate JWT
       │ 9. Check role == ADMIN
       │ 10. Process request
```

### JWT Token Structure

```json
{
  "sub": "user_id",
  "email": "user@example.com",
  "role": "CUSTOMER | ADMIN",
  "iat": 1715328000,
  "exp": 1715414400,
  "iss": "fashionstore-api"
}
```

### Security Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().configurationSource(corsConfigurationSource())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/public/auth/**").permitAll()
                .requestMatchers("/api/v1/public/**").hasRole("CUSTOMER")
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",  // Storefront
            "http://localhost:3001",  // Admin
            "https://store.fashionstore.com",
            "https://admin.fashionstore.com"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## Development Workflow

### Local Development Setup

```bash
# 1. Clone repository
git clone https://github.com/yourusername/fashionstore.git
cd fashionstore

# 2. Start infrastructure services
docker-compose up -d mysql redis

# 3. Start Backend API (port 8080)
cd backend
./mvnw spring-boot:run

# 4. Start Storefront (port 3000)
cd storefront
npm install
npm run dev

# 5. Start Admin Dashboard (port 3001)
cd admin
npm install
npm run dev
```

### Development Configuration

**Backend (application-dev.yml)**
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fashionstore
    username: root
    password: password
  
  jpa:
    hibernate:
      ddl-auto: update
  
  redis:
    host: localhost
    port: 6379

cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:3001

jwt:
  secret: your-dev-secret-key
  expiration: 86400000  # 24 hours
```

**Storefront (vite.config.ts)**
```typescript
export default defineConfig({
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

**Admin (vite.config.ts)**
```typescript
export default defineConfig({
  server: {
    port: 3001,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

### Git Workflow

```
main
├── develop
│   ├── feature/customer-auth
│   ├── feature/admin-dashboard
│   └── feature/api-refactor
└── hotfix/security-patch
```

---

## Deployment Strategy

### Development Deployment

```yaml
# docker-compose.dev.yml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    ports:
      - "3306:3306"
    environment:
      MYSQL_ROOT_PASSWORD: password
      MYSQL_DATABASE: fashionstore
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: dev
    depends_on:
      - mysql
      - redis
  
  storefront:
    build: ./storefront
    ports:
      - "3000:80"
  
  admin:
    build: ./admin
    ports:
      - "3001:80"
```

### Production Deployment

#### Option 1: Docker Compose (Simplified)

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  backend:
    image: fashionstore/backend:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DB_HOST: mysql-production
      REDIS_HOST: redis-production
    networks:
      - internal
    depends_on:
      - mysql-production
      - redis-production
  
  storefront:
    image: fashionstore/storefront:latest
    ports:
      - "80:80"
    networks:
      - public
  
  admin:
    image: fashionstore/admin:latest
    ports:
      - "8081:80"
    networks:
      - public
  
  mysql-production:
    image: mysql:8.0
    volumes:
      - mysql-data:/var/lib/mysql
    networks:
      - internal
  
  redis-production:
    image: redis:7-alpine
    volumes:
      - redis-data:/data
    networks:
      - internal
  
  nginx:
    image: nginx:alpine
    ports:
      - "443:443"
      - "80:80"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf
      - ./nginx/ssl:/etc/nginx/ssl
    networks:
      - public
      - internal
    depends_on:
      - storefront
      - admin

networks:
  public:
  internal:

volumes:
  mysql-data:
  redis-data:
```

#### Option 2: Kubernetes (Scalable)

```yaml
# backend-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: fashionstore-backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: backend
  template:
    metadata:
      labels:
        app: backend
    spec:
      containers:
      - name: backend
        image: fashionstore/backend:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
---
apiVersion: v1
kind: Service
metadata:
  name: backend-service
spec:
  selector:
    app: backend
  ports:
  - port: 8080
  type: ClusterIP
```

#### Nginx Configuration

```nginx
# nginx.conf
upstream storefront {
    server storefront:80;
}

upstream admin {
    server admin:80;
}

upstream backend {
    server backend:8080;
}

server {
    listen 80;
    server_name store.fashionstore.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name store.fashionstore.com;
    
    ssl_certificate /etc/nginx/ssl/store.crt;
    ssl_certificate_key /etc/nginx/ssl/store.key;
    
    location / {
        proxy_pass http://storefront;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

server {
    listen 80;
    server_name admin.fashionstore.com;
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name admin.fashionstore.com;
    
    ssl_certificate /etc/nginx/ssl/admin.crt;
    ssl_certificate_key /etc/nginx/ssl/admin.key;
    
    location / {
        proxy_pass http://admin;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
    
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### CI/CD Pipeline

```yaml
# .github/workflows/deploy.yml
name: Deploy

on:
  push:
    branches: [main]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Build Backend
      run: |
        cd backend
        ./mvnw package -DskipTests
    
    - name: Build Storefront
      run: |
        cd storefront
        npm ci
        npm run build
    
    - name: Build Admin
      run: |
        cd admin
        npm ci
        npm run build
    
    - name: Docker Build & Push
      run: |
        docker build -t fashionstore/backend ./backend
        docker build -t fashionstore/storefront ./storefront
        docker build -t fashionstore/admin ./admin
        docker push fashionstore/backend
        docker push fashionstore/storefront
        docker push fashionstore/admin
    
    - name: Deploy to Production
      run: |
        ssh user@server 'cd /opt/fashionstore && docker-compose pull && docker-compose up -d'
```

---

## Migration Strategy

### Phase 1: Backend API Refactoring (2-3 weeks)
- Extract existing servlet logic into Spring Boot REST controllers
- Implement JWT authentication
- Create DTOs for request/response
- Set up CORS configuration
- Write unit tests

### Phase 2: Storefront Frontend (3-4 weeks)
- Set up React/Vue project
- Implement core pages (Home, Products, Product Detail, Cart, Checkout)
- Integrate with backend API
- Implement authentication flow
- Add responsive design

### Phase 3: Admin Dashboard (2-3 weeks)
- Set up React/Vue project
- Implement admin pages (Dashboard, Products, Orders, Users)
- Integrate with backend API
- Implement admin authentication
- Add analytics and reporting

### Phase 4: Infrastructure Setup (1-2 weeks)
- Set up Docker Compose for development
- Configure CI/CD pipeline
- Set up production infrastructure
- Configure Nginx reverse proxy
- Set up SSL certificates

### Phase 5: Testing & Launch (1-2 weeks)
- End-to-end testing
- Performance testing
- Security audit
- Gradual rollout
- Monitor and optimize

---

## Benefits of This Architecture

1. **Scalability**: Each service can be scaled independently based on load
2. **Maintainability**: Clear separation of concerns makes code easier to maintain
3. **Team Productivity**: Frontend and backend teams can work independently
4. **Technology Flexibility**: Each service can use optimal technology stack
5. **Security**: Admin and customer access are completely separated
6. **Performance**: Static frontend assets can be served via CDN
7. **Portfolio Appeal**: Modern, industry-standard architecture
8. **Deployment Flexibility**: Can deploy to various platforms (Docker, K8s, Cloud)

---

## Next Steps

1. **Create project structure**: Set up the folder structure
2. **Initialize backend**: Set up Spring Boot project with required dependencies
3. **Initialize frontends**: Set up React/Vue projects for storefront and admin
4. **Set up infrastructure**: Configure Docker Compose for local development
5. **Implement authentication**: Set up JWT authentication in backend
6. **Migrate existing code**: Gradually move existing servlet logic to REST controllers
7. **Build frontend features**: Implement core features in both frontends
8. **Test and deploy**: Test thoroughly and deploy to production

---

## Conclusion

This multi-frontend architecture provides a solid foundation for scaling FashionStore while maintaining clean separation between customer and admin interfaces. The architecture follows modern best practices and will make the project portfolio-ready.
