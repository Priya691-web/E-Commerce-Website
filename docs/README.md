# FashionStore - E-Commerce Platform

A comprehensive, modern e-commerce platform built with Java 21, JSP, React, and MySQL. FashionStore provides a dual-frontend architecture with a JSP-based customer interface and a React-based admin dashboard.

## Features

### Customer Features
- User authentication and profile management
- Product browsing with filtering and sorting
- Advanced search with autocomplete suggestions
- Shopping cart with quantity management
- Wishlist for saved items
- Secure checkout with multiple payment methods
- Order tracking and history
- Address management
- Product reviews and ratings

### Admin Features
- Dashboard with key metrics and analytics
- Full product CRUD operations
- Inventory management with low stock alerts
- Order processing and status updates
- User management with role assignment
- Category management
- Coupon creation and management
- Sales analytics and reporting

## Technology Stack

### Backend
- **Java 21** - Programming language
- **Jakarta Servlet API 6.0** - Web framework
- **Tomcat 10.1** - Servlet container
- **MySQL 8.0** - Database
- **HikariCP 5.0.1** - Connection pooling
- **Redis 7** - Caching
- **jBCrypt 0.6** - Password hashing
- **Gson 2.10.1** - JSON serialization
- **SLF4J 2.0.9** - Logging facade
- **Stripe SDK 24.11.0** - Payment processing

### Frontend
**Customer Frontend:**
- JSP 3.1 - Server-side templating
- JSTL 2.0 - Tag library
- JavaScript ES6+ - Client-side interactivity
- CSS3 - Styling

**Admin Frontend:**
- React 18.3.1 - UI library
- Vite 5.4.10 - Build tool
- React Router DOM 6.27.0 - Routing
- TailwindCSS 3.4.14 - Styling
- Lucide React 0.456.0 - Icons
- Recharts 2.13.3 - Charts
- Axios 1.7.7 - HTTP client

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Service orchestration
- **Nginx** - Reverse proxy

## Project Structure

```
FashionStore/
├── src/main/java/com/fashionstore/
│   ├── cache/              # Caching layer
│   ├── controller/         # Servlet controllers
│   ├── dao/                # DAO interfaces
│   ├── daoimpl/            # DAO implementations
│   ├── filter/             # Servlet filters
│   ├── model/              # Domain models
│   ├── service/            # Service layer
│   └── util/               # Utilities
├── src/main/webapp/
│   ├── assets/             # Static assets (CSS, JS, images)
│   └── WEB-INF/
│       └── views/          # JSP views
├── fashionstore-admin/     # React admin frontend
│   ├── src/
│   │   ├── api/            # API client
│   │   ├── components/     # React components
│   │   ├── pages/          # Page components
│   │   └── context/        # React contexts
│   └── package.json
├── schema.sql              # Database schema
├── docker-compose.yml      # Docker orchestration
├── Dockerfile              # Backend Docker image
├── pom.xml                 # Maven configuration
└── README.md
```

## Prerequisites

- Docker 20.10 or higher
- Docker Compose 2.0 or higher

**Note:** Docker and Docker Compose are required for deployment. The application runs entirely in containers - no local Java, Maven, Node.js, MySQL, or Redis installation is needed.

## Quick Start

### Using Docker Compose (Recommended - Only Official Method)

1. Clone the repository:
```bash
git clone https://github.com/yourusername/FashionStore.git
cd FashionStore
```

2. Create environment file:
```bash
cp .env.example .env
# Edit .env with your configuration
```

3. Start all services:
```bash
docker compose up -d
```

4. Access the application:
- Customer Frontend: http://localhost:8080/home
- Admin Frontend: http://localhost:3000

**Note:** Docker Compose is the only officially supported deployment method for the FashionStore backend.

## Configuration

### Environment Variables

Create a `.env` file in the project root:

```bash
# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_PASSWORD=fashionstore
DB_HOST=localhost
DB_PORT=3306
DB_NAME=fashionstore
DB_USER=root
DB_PASSWORD=rootpassword

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Application
CSRF_ENABLED=true
ENV=production
FASHIONSTORE_ADMIN_KEY=your-secret-admin-key

# Stripe (optional)
STRIPE_SECRET_KEY=sk_live_your_key
STRIPE_PUBLISHABLE_KEY=pk_live_your_key
```

### Database Configuration

Edit `src/main/resources/db.properties`:

```properties
db.host=localhost
db.port=3306
db.name=fashionstore
db.user=root
db.password=rootpassword
db.pool.size=10
```

## Documentation

Comprehensive documentation is available in the following files:

- [ARCHITECTURE.md](ARCHITECTURE.md) - System architecture overview
- [DATABASE.md](DATABASE.md) - Database schema and design
- [FRONTEND.md](FRONTEND.md) - Frontend architecture details
- [BACKEND.md](BACKEND.md) - Backend architecture details
- [SECURITY.md](SECURITY.md) - Security implementation
- [DEPLOYMENT.md](DEPLOYMENT.md) - Deployment guide
- [FEATURES.md](FEATURES.md) - Feature documentation
- [PROJECT_REPORT.md](PROJECT_REPORT.md) - Complete project report

## Default Credentials

### Admin User
- Email: admin@fashionstore.com
- Password: admin123

### Demo Customer
- Email: customer@fashionstore.com
- Password: customer123

**Important:** Change default passwords in production!

## API Documentation

### Customer API

The customer frontend uses traditional servlet endpoints:

- `GET /home` - Homepage
- `GET /products` - Product listing
- `GET /product?id={id}` - Product details
- `POST /cart` - Add to cart
- `GET /cart` - View cart
- `POST /checkout` - Place order
- `GET /orders` - Order history
- `POST /login` - User login
- `POST /register` - User registration

### Admin API

The admin frontend uses RESTful JSON API under `/api/admin/*`:

- `GET /api/admin/me` - Current user
- `POST /api/admin/login` - Admin login
- `GET /api/admin/dashboard` - Dashboard stats
- `GET /api/admin/products` - List products
- `POST /api/admin/products` - Create product
- `PUT /api/admin/products/{id}` - Update product
- `DELETE /api/admin/products/{id}` - Delete product
- `GET /api/admin/orders` - List orders
- `PUT /api/admin/orders/{id}/status` - Update order status
- `GET /api/admin/users` - List users
- `PUT /api/admin/users/{id}/role` - Update user role

For detailed API documentation, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md).

## Development

### Backend Development

The backend is built and deployed through Docker. To develop locally:

```bash
docker compose up -d backend
```

The backend will be available at `http://localhost:8080/home`

To rebuild the backend after code changes:
```bash
docker compose up -d --build backend
```

### Frontend Development

**Customer Frontend:**
The customer frontend is served by the backend as JSP views. Access via Docker Compose at `http://localhost:8080/home`

**Admin Frontend:**
```bash
cd fashionstore-admin
npm install
npm run dev
```

The admin frontend will be available at `http://localhost:5173`

### Running Tests

**Backend Tests:**
```bash
# Run tests inside the Docker container
docker compose run backend mvn test
```

**Frontend Tests:**
```bash
cd fashionstore-admin
npm test
```

## Building for Production

### Backend
The backend is built automatically during Docker Compose build. To build the production Docker image:

```bash
docker compose build backend
```

The Dockerfile uses a multi-stage build that:
1. Builds the WAR file using Maven
2. Deploys the WAR to Tomcat runtime

### Admin Frontend
```bash
cd fashionstore-admin
npm run build
```

This creates optimized build files in `fashionstore-admin/dist`

## Deployment

### Docker Deployment

Build and deploy all services:
```bash
docker compose up -d --build
```

### Manual Deployment

1. Deploy the WAR file to your servlet container
2. Deploy the admin frontend build to your web server
3. Configure Nginx as a reverse proxy
4. Set up MySQL and Redis
5. Configure environment variables

For detailed deployment instructions, see [DEPLOYMENT.md](DEPLOYMENT.md).

## Security

FashionStore implements comprehensive security measures:

- **Authentication**: BCrypt password hashing with session-based auth
- **Authorization**: Role-based access control (RBAC)
- **CSRF Protection**: Token-based CSRF protection
- **XSS Prevention**: Input sanitization and output encoding
- **SQL Injection Prevention**: Prepared statements for all queries
- **Security Headers**: Comprehensive security headers
- **Session Security**: HttpOnly cookies, session fixation prevention

For detailed security documentation, see [SECURITY.md](SECURITY.md).

## Performance

Performance optimizations include:

- Redis caching with local fallback
- HikariCP connection pooling
- Batch loading to prevent N+1 queries
- Lazy loading images
- Code splitting for React
- Strategic database indexes

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For support, email support@fashionstore.com or open an issue on GitHub.

## Acknowledgments

- Jakarta Servlet API
- React and React Router
- TailwindCSS
- Stripe
- Redis
- MySQL

---

**FashionStore** - A modern e-commerce platform built for performance, security, and scalability.
