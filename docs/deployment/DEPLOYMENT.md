# FashionStore - DevOps & Deployment Documentation

## Table of Contents
1. [Executive Summary](#executive-summary)
2. [Docker Setup](#docker-setup)
3. [Docker Compose Configuration](#docker-compose-configuration)
4. [Environment Variables](#environment-variables)
5. [Startup Flow](#startup-flow)
6. [Backend Deployment](#backend-deployment)
7. [Frontend Deployment](#frontend-deployment)
8. [Database Initialization](#database-initialization)
9. [Development Workflow](#development-workflow)
10. [Production Deployment](#production-deployment)
11. [Monitoring & Logging](#monitoring--logging)
12. [Backup & Recovery](#backup--recovery)

---

## Executive Summary

The FashionStore platform uses **Docker containerization** for consistent deployment across development, staging, and production environments. The deployment architecture consists of five main services: MySQL database, Redis cache, Java backend, React admin frontend, and Nginx reverse proxy.

**Key Deployment Features:**
- **Containerization**: Docker and Docker Compose for service orchestration
- **Multi-Stage Builds**: Optimized Docker images for production
- **Environment Configuration**: Environment-based configuration management
- **Health Checks**: Automated health monitoring for all services
- **Volume Management**: Persistent storage for database and cache
- **Network Isolation**: Docker network for service communication

---

## Docker Setup

### Backend Dockerfile

**Location**: `/Dockerfile`

```dockerfile
FROM tomcat:10.1-jdk21

ENV CATALINA_OPTS="-Xms512m -Xmx1024m"

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/FashionStore.war /usr/local/tomcat/webapps/ROOT.war

RUN groupadd -r tomcat && useradd -r -g tomcat tomcat
RUN chown -R tomcat:tomcat /usr/local/tomcat

USER tomcat

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/home || exit 1

CMD ["catalina.sh", "run"]
```

### Admin Frontend Dockerfile

**Location**: `/fashionstore-admin/Dockerfile`

```dockerfile
FROM node:18-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=builder /app/dist /usr/share/nginx/html

RUN addgroup -g 1001 -S nginx && \
    adduser -S -D -H -u 1001 -h /var/cache/nginx -s /sbin/nologin -G nginx -g nginx nginx

RUN chown -R nginx:nginx /usr/share/nginx/html

USER nginx

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=10s --start-period=10s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
```

---

## Docker Compose Configuration

### docker-compose.yml

**Location**: `/docker-compose.yml`

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: fashionstore-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD:-rootpassword}
      MYSQL_DATABASE: fashionstore
      MYSQL_USER: fashionstore
      MYSQL_PASSWORD: ${MYSQL_PASSWORD:-fashionstore}
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./schema.sql:/docker-entrypoint-initdb.d/schema.sql:ro
    networks:
      - fashionstore-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    container_name: fashionstore-redis
    restart: unless-stopped
    ports:
      - "6380:6379"
    volumes:
      - redis-data:/data
    networks:
      - fashionstore-network
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
    command: redis-server --appendonly yes

  backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: fashionstore-backend
    restart: unless-stopped
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: fashionstore
      DB_USER: root
      DB_PASSWORD: ${MYSQL_PASSWORD:-rootpassword}
      REDIS_HOST: redis
      REDIS_PORT: 6379
      CSRF_ENABLED: "true"
      ENV: production
    ports:
      - "8080:8080"
    networks:
      - fashionstore-network
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/home"]
      interval: 30s
      timeout: 10s
      retries: 3

  admin-frontend:
    build:
      context: ./fashionstore-admin
      dockerfile: Dockerfile
    container_name: fashionstore-admin-frontend
    restart: unless-stopped
    ports:
      - "3000:80"
    networks:
      - fashionstore-network
    depends_on:
      - backend

networks:
  fashionstore-network:
    driver: bridge

volumes:
  mysql-data:
    driver: local
  redis-data:
    driver: local
```

---

## Environment Variables

### Required Environment Variables

```bash
# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_PASSWORD=fashionstore
DB_HOST=mysql
DB_PORT=3306
DB_NAME=fashionstore
DB_USER=root
DB_PASSWORD=rootpassword
REDIS_HOST=redis
REDIS_PORT=6379

# Application
CSRF_ENABLED=true
ENV=production
FASHIONSTORE_ADMIN_KEY=your-secret-admin-key
FASHIONSTORE_PROFILE=prod
```

---

## Startup Flow

### Service Startup Sequence

1. MySQL starts and initializes database
2. Redis starts and accepts connections
3. Backend waits for MySQL and Redis health checks
4. Backend starts and serves application
5. Admin frontend starts and proxies to backend

### Startup Commands

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down
```

---

## Backend Deployment

### Build Process

The backend uses a multi-stage Docker build that automatically compiles the WAR file and deploys it to Tomcat.

```bash
# Build Docker image (includes Maven build)
docker build -t fashionstore-backend .

# Run container
docker run -p 8080:8080 fashionstore-backend
```

The Dockerfile performs the following:
1. **Stage 1 (Builder)**: Uses Maven to compile the WAR file
2. **Stage 2 (Runtime)**: Deploys the WAR to Tomcat container

---

## Frontend Deployment

### Build Process

```bash
cd fashionstore-admin

# Development
npm run dev

# Production build
npm run build

# Docker build
docker build -t fashionstore-admin .

# Run container
docker run -p 3000:80 fashionstore-admin
```

---

## Database Initialization

### Automatic Initialization

The schema.sql file is automatically executed on first MySQL startup via Docker Compose volume mapping.

### Manual Initialization

```bash
# Connect to MySQL
docker exec -it fashionstore-mysql mysql -u root -p

# Execute schema
mysql -u root -p fashionstore < schema.sql
```

---

## Development Workflow

### Local Development

```bash
# Start all services
docker compose up -d

# View logs
docker compose logs -f

# Rebuild specific service
docker compose up -d --build backend
```

### Testing

```bash
# Run backend tests inside Docker container
docker compose run backend mvn test

# Run frontend tests
cd fashionstore-admin
npm test
```

---

## Production Deployment

### Full Deployment

```bash
# Build and deploy all services
docker compose up -d --build

# Verify services
docker compose ps
```

### Rolling Updates

```bash
# Pull latest images
docker compose pull

# Restart services
docker compose up -d
```

---

## Monitoring & Logging

### View Logs

```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f backend
```

### Health Checks

```bash
# Check service status
docker compose ps

# Check backend health
curl http://localhost:8080/home
```

---

## Backup & Recovery

### Database Backup

```bash
# Backup database
docker exec fashionstore-mysql mysqldump -u root -p fashionstore > backup.sql

# Restore database
docker exec -i fashionstore-mysql mysql -u root -p fashionstore < backup.sql
```

### Volume Backup

```bash
# Backup MySQL volume
docker run --rm -v fashionstore_mysql-data:/data -v $(pwd):/backup \
  alpine tar czf /backup/mysql-backup.tar.gz /data
```

---

## Conclusion

The FashionStore deployment architecture provides a **containerized, scalable, and maintainable** deployment strategy using Docker and Docker Compose. The multi-service architecture ensures separation of concerns while maintaining efficient communication between components.
