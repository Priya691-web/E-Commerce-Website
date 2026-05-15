# FashionStore - Complete Documentation

## 📋 Table of Contents

1. [Project Overview](#project-overview)
2. [Getting Started](#getting-started)
3. [Project Structure](#project-structure)
4. [Architecture](#architecture)
5. [Design System](#design-system)
6. [Deployment](#deployment)
7. [API Documentation](#api-documentation)
8. [Security](#security)
9. [Testing](#testing)
10. [Maintenance](#maintenance)

---

## Project Overview

**Project Name**: FashionStore  
**Version**: 1.0.0  
**Status**: Production-Ready  
**Last Updated**: May 15, 2026  

FashionStore is a premium luxury fashion e-commerce platform featuring:
- **Modern Frontend**: Cinematic luxury design with dark theme support
- **Robust Backend**: Spring-based Java application with comprehensive APIs
- **Production Infrastructure**: Docker-based deployment with monitoring
- **Mobile-First Design**: Responsive across all devices
- **Premium UX**: Editorial-quality navigation and interactions

### Technology Stack

**Frontend**:
- HTML5, CSS3 (with custom properties), JavaScript (ES6+)
- JSP templates
- Responsive design with mobile-first approach

**Backend**:
- Java 17
- Spring Framework
- Tomcat servlet container
- Jakarta EE
- Maven build system

**Database**:
- MySQL 8.0
- Redis 7.0
- JDBC for data access

**Infrastructure**:
- Docker & Docker Compose
- Nginx web server
- Prometheus monitoring
- Grafana dashboards

---

## Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 17+
- Maven 3.8+
- Node.js 16+ (for admin frontend)

### Quick Start

```bash
# Navigate to project directory
cd /Users/pc/eclipse-workspace/FashionStore

# Start all services
docker-compose -f docker-compose.prod.yml up -d

# Wait for services to be healthy (30-60 seconds)
docker-compose -f docker-compose.prod.yml ps
```

### Access the Application

- **Frontend**: http://localhost/home
- **Admin Panel**: http://localhost/admin
- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090

### Stop the Application

```bash
docker-compose -f docker-compose.prod.yml down
```

### Initialize Database

```bash
docker exec fashionstore-mysql-prod mysql -u root -pTarun@1605 fashionstore < db/schema.sql
docker exec fashionstore-mysql-prod mysql -u root -pTarun@1605 fashionstore < db/schema_updates.sql
```

### Common Tasks

**View Logs**:
```bash
docker-compose -f docker-compose.prod.yml logs -f backend
```

**Access Database**:
```bash
docker exec -it fashionstore-mysql-prod mysql -u root -pTarun@1605 fashionstore
```

**Clear Cache**:
```bash
docker exec fashionstore-redis-prod redis-cli FLUSHALL
```

**Rebuild Services**:
```bash
docker-compose -f docker-compose.prod.yml down
docker-compose -f docker-compose.prod.yml up --build -d
```

### Troubleshooting

**Services Won't Start**:
1. Check Docker is running: `docker ps`
2. Check ports are available
3. View logs: `docker-compose logs`

**Database Connection Error**:
1. Wait 30 seconds for MySQL to start
2. Check credentials in `.env`
3. Verify database is initialized

---

## Project Structure

### Directory Organization

```
FashionStore/
├── src/
│   ├── main/
│   │   ├── java/com/fashionstore/          # Java backend code
│   │   │   ├── controller/                 # Request handlers
│   │   │   ├── service/                    # Business logic
│   │   │   ├── daoimpl/                    # Database access
│   │   │   ├── model/                      # Data models
│   │   │   ├── dto/                        # Data transfer objects
│   │   │   ├── mapper/                     # Entity-DTO mappers
│   │   │   ├── filter/                     # Request filters
│   │   │   ├── security/                   # Security components
│   │   │   └── util/                       # Utility classes
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   └── views/                  # JSP templates
│   │       │       ├── partials/           # Reusable components
│   │       │       ├── pages/              # Page templates
│   │       │       └── admin/              # Admin templates
│   │       └── assets/
│   │           ├── css/                    # Stylesheets
│   │           │   ├── components/         # Component styles
│   │           │   ├── pages/              # Page-specific styles
│   │           │   └── design-tokens.css   # Design system tokens
│   │           ├── js/                     # JavaScript files
│   │           └── images/                 # Static images
│   └── test/                               # Test files
│
├── db/                                     # Database schemas
├── docker-compose.prod.yml                 # Production Docker setup
├── Dockerfile                              # Backend Docker image
├── Dockerfile.production                   # Production build
├── pom.xml                                 # Maven configuration
│
├── frontend/                               # Frontend applications
│   └── admin/                              # Admin panel (React)
│       ├── src/                            # Source code
│       ├── package.json                    # Dependencies
│       └── vite.config.js                  # Build config
│
├── docker/                                 # Docker configurations
│   ├── Dockerfile                          # Backend Dockerfile
│   ├── Dockerfile.production               # Production Dockerfile
│   └── docker-compose.prod.yml            # Production compose
│
├── tests/                                  # Test files
│   ├── e2e/                                # E2E tests (Playwright)
│   ├── integration/                        # Integration tests
│   └── unit/                               # Unit tests (Vitest)
│
├── scripts/                                # Utility scripts
│   ├── backup-db.sh                        # Database backup
│   ├── build.sh                            # Build project
│   ├── clean.sh                            # Clean artifacts
│   └── logs.sh                             # View logs
│
├── nginx/                                  # Nginx configuration
│   ├── nginx.conf                          # Main config
│   └── ssl/                                # SSL certificates
│
└── docs/                                   # Documentation
```

### File Organization

**CSS Files (36 total)**:
- **design-tokens.css**: Single source of truth for design system
- **components/**: Reusable component styles (11 files)
- **pages/**: Page-specific styles (20 files)
- **main.css**: Component imports
- **base.css**: Base styles
- **reset.css**: CSS reset
- **premium-core.css**: Premium styles

**JavaScript Files (4 main files)**:
- **main.js**: Main application logic
- **cart.js**: Cart functionality
- **auth.js**: Authentication
- **lazy-loading.js**: Image lazy loading

### Naming Conventions

**CSS Classes**:
- Use kebab-case: `.product-card`, `.nav-icon-btn`
- Component prefix: `.navbar-*`, `.product-*`, `.cart-*`

**JavaScript**:
- Variables: camelCase (productId, cartItems)
- Constants: UPPER_SNAKE_CASE (MAX_ITEMS, API_URL)

**Java**:
- Classes: PascalCase (ProductController, ProductService)
- Methods: camelCase (getProducts, addToCart)

**Database**:
- Tables: snake_case (products, product_sizes)
- Columns: snake_case (product_id, category_name)

---

## Architecture

### System Architecture

FashionStore follows a classic three-tier architecture:

1. **Presentation Layer**: JSP templates with modern CSS/JavaScript
2. **Business Layer**: Spring-based service layer with business logic
3. **Data Layer**: MySQL database with Redis caching

### Backend Architecture

**Controller Layer**:
- `@WebServlet` annotations for URL mapping
- Request parameter parsing and validation
- Response formatting (JSON/JSP)
- Error handling and logging

**Service Layer**:
- Business logic implementation
- Transaction management
- Data transformation
- Cache management

**Data Access Layer**:
- DAO pattern implementation
- JDBC for database operations
- SQL query execution
- Result set mapping

### Frontend Architecture

**CSS Architecture**:
- Design tokens for consistency
- Component-based organization
- Mobile-first responsive design
- Dark theme support via CSS custom properties

**JavaScript Architecture**:
- Modular file organization
- Event-driven interactions
- AJAX for API calls
- Client-side validation

### Docker Services

**Core Services**:
- **MySQL**: Database (port 3306)
- **Redis**: Cache (port 6379)
- **Tomcat**: Backend API (port 8080)
- **Nginx**: Web server (port 80, 443)

**Monitoring**:
- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Dashboards (port 3000)

---

## Design System

### Colors

**Light Theme**:
- Primary: Warm neutrals with black accents
- Background: #ffffff
- Text: #111111
- Accent: #d4af37 (gold)

**Dark Theme**:
- Primary: Ultra-deep black with premium surfaces
- Background: #050507
- Text: #f8f8fa
- Accent: #d4af37 (gold)

**Semantic Colors**:
- Success: #10b981
- Danger: #ef4444
- Warning: #f59e0b
- Info: #3b82f6

### Typography

**Font Families**:
- Display: Cormorant Garamond (serif)
- Body: Inter (sans-serif)

**Font Sizes**:
- 8 responsive scales using clamp()
- Range: 12px to 64px

**Font Weights**:
- Light: 300
- Regular: 400
- Medium: 500
- Semibold: 600
- Bold: 700

### Spacing

**4px Rhythm Scale**:
- 12 levels: 4px, 8px, 12px, 16px, 20px, 24px, 32px, 40px, 48px, 64px, 80px, 96px

**Usage**:
- Component padding: 16px-32px
- Section spacing: 48px-96px
- Element spacing: 8px-24px

### Responsive Breakpoints

- **Mobile**: 320px
- **Mobile Large**: 375px
- **Tablet**: 768px
- **Laptop**: 1024px
- **Desktop**: 1280px
- **Wide**: 1440px

### Shadows

**Elevation Levels**:
- Level 1: 0 1px 2px rgba(0,0,0,0.1)
- Level 2: 0 4px 6px rgba(0,0,0,0.1)
- Level 3: 0 10px 15px rgba(0,0,0,0.1)

### Border Radius

- Small: 4px
- Medium: 8px
- Large: 16px
- Extra Large: 24px

---

## Deployment

### Docker Deployment

**Production Setup**:
```bash
# Build and start all services
docker-compose -f docker/docker-compose.prod.yml up -d

# Check service status
docker-compose -f docker/docker-compose.prod.yml ps

# View logs
docker-compose -f docker/docker-compose.prod.yml logs -f
```

**Environment Variables**:
```bash
MYSQL_ROOT_PASSWORD=Tarun@1605
DB_PASSWORD=Tarun@1605
RATE_LIMIT_ENABLED=true
CSRF_ENABLED=true
QA_MODE=false
LOG_LEVEL=INFO
```

### Nginx Configuration

**Reverse Proxy**:
- Routes requests to backend services
- SSL/TLS termination
- Static file serving
- Gzip compression

**Load Balancing**:
- Round-robin distribution
- Health checks
- Automatic failover

### Monitoring

**Prometheus**:
- Metrics collection
- Performance monitoring
- Alert configuration

**Grafana**:
- Dashboards for visualization
- Real-time monitoring
- Historical data analysis

---

## API Documentation

### Authentication Endpoints

**POST /api/admin/login**
- Description: Admin login
- Request Body: `{ email, password }`
- Response: `{ success, message, data }`

**POST /api/admin/logout**
- Description: Admin logout
- Response: `{ success, message }`

**GET /api/admin/me**
- Description: Get current admin user
- Response: `{ success, data: { id, email, name } }`

### Product Endpoints

**GET /api/admin/products**
- Description: Get all products
- Response: `{ success, data: [products] }`

**GET /api/admin/products/:id**
- Description: Get product by ID
- Response: `{ success, data: { product } }`

**POST /api/admin/products**
- Description: Create new product
- Request Body: `{ name, price, description, category, stock }`
- Response: `{ success, data: { product } }`

**PUT /api/admin/products/:id**
- Description: Update product
- Request Body: `{ name, price, description, category, stock }`
- Response: `{ success, data: { product } }`

**DELETE /api/admin/products/:id**
- Description: Delete product
- Response: `{ success, message }`

### Order Endpoints

**GET /api/admin/orders**
- Description: Get all orders
- Response: `{ success, data: [orders] }`

**GET /api/admin/orders/:id**
- Description: Get order by ID
- Response: `{ success, data: { order } }`

**PUT /api/admin/orders/:id/status**
- Description: Update order status
- Request Body: `{ status }`
- Response: `{ success, data: { order } }`

### Response Format

All API responses follow this format:
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

Error responses:
```json
{
  "success": false,
  "message": "Error message",
  "error": "Error details"
}
```

---

## Security

### Authentication

- Session-based authentication
- Secure password hashing (jBCrypt)
- Session timeout (30 minutes)
- CSRF protection on all forms

### Authorization

- Role-based access control (RBAC)
- Admin panel protection
- User-specific data access
- API endpoint protection

### Security Headers

- Content Security Policy (CSP)
- X-Frame-Options: DENY
- X-Content-Type-Options: nosniff
- HSTS enabled in production
- X-XSS-Protection

### Rate Limiting

- Request rate limiting
- Brute force protection
- Login attempt throttling
- API endpoint throttling

### Data Protection

- Password encryption
- Secure session management
- SQL injection prevention
- XSS protection
- CSRF token validation

---

## Testing

### E2E Testing (Playwright)

**Test Coverage**:
- Authentication flows (login, register, logout)
- Admin dashboard
- Product CRUD operations
- Cart flow
- Checkout flow
- Responsive navigation
- Mobile footer
- Accessibility (WCAG 2.1)
- Visual regression

**Run Tests**:
```bash
# Install Playwright
npm install -D @playwright/test
npx playwright install

# Run E2E tests
npx playwright test

# Run with UI
npx playwright test --ui
```

### API Integration Testing

**Test Coverage**:
- Authentication API
- Products API
- Orders API
- Error handling
- Validation

**Run Tests**:
```bash
npx playwright test tests/integration
```

### Component Testing (Vitest)

**Test Coverage**:
- React components
- Form validation
- API client
- Protected routes
- Dashboard components

**Run Tests**:
```bash
cd frontend/admin
npm test
```

### Coverage Reporting

**Frontend Coverage**:
- Vitest coverage reports
- HTML, JSON, LCOV formats
- Threshold: 80% minimum

**Backend Coverage**:
- JaCoCo for Java
- Maven plugin integration
- Threshold: 70% minimum

---

## Maintenance

### Regular Tasks

**Daily**:
- Monitor application logs
- Check Grafana dashboards
- Review error rates

**Weekly**:
- Database performance check
- Security vulnerability scan
- Dependency updates review

**Monthly**:
- Database backup verification
- Log rotation
- Performance optimization review
- Security audit

### Useful Commands

**View Logs**:
```bash
docker-compose logs -f backend
```

**Access Database**:
```bash
docker exec -it fashionstore-mysql-prod mysql -u root -pTarun@1605
```

**Clear Cache**:
```bash
docker exec fashionstore-redis-prod redis-cli FLUSHALL
```

**Rebuild Services**:
```bash
docker-compose down && docker-compose up --build -d
```

**Database Backup**:
```bash
./scripts/backup-db.sh
```

### Troubleshooting

**Services Won't Start**:
1. Check Docker is running: `docker ps`
2. Check ports are available
3. View logs: `docker-compose logs`

**Database Connection Error**:
1. Wait 30 seconds for MySQL to start
2. Check credentials in `.env`
3. Verify database is initialized

**High Memory Usage**:
1. Check Redis cache size
2. Review database connections
3. Monitor JVM memory

**Slow Performance**:
1. Check database query performance
2. Review cache hit ratio
3. Check for memory leaks

---

## Version History

### v1.0.0 (May 15, 2026)
- Initial production release
- Complete frontend redesign
- Backend optimization
- Security hardening
- Mobile-first responsive design
- Comprehensive documentation
- QA automation implementation
- Repository cleanup and optimization

---

## Support

### Documentation
- This file: Complete documentation
- ARCHITECTURE_DIAGRAM.md: System architecture diagram
- Docker configuration files

### Troubleshooting
- Check Docker logs: `docker-compose logs`
- Verify services: `docker-compose ps`
- Test endpoints: `curl http://localhost/home`

### Contact
- For issues: Check logs first
- For questions: Review documentation
- For bugs: Create issue with details

---

## Conclusion

FashionStore is a **production-ready premium fashion e-commerce platform** with:
- ✅ Luxury design aesthetic
- ✅ Robust backend infrastructure
- ✅ Mobile-first responsive design
- ✅ Comprehensive security
- ✅ Complete documentation
- ✅ Easy deployment process
- ✅ QA automation
- ✅ Monitoring and logging

**The application is ready for production deployment and user access.**

---

**Project Completion Date**: May 15, 2026  
**Status**: ✅ COMPLETE & PRODUCTION-READY
