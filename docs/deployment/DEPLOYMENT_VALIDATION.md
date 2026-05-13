# FashionStore Production Deployment Validation

This document provides validation procedures to ensure the FashionStore platform is ready for production deployment.

## Pre-Deployment Checklist

### Infrastructure Validation

- [ ] Server meets minimum requirements (4 CPU cores, 8GB RAM, 50GB SSD)
- [ ] Docker 24.0+ installed and verified
- [ ] Docker Compose 2.20+ installed and verified
- [ ] Ports 80 and 443 open on firewall
- [ ] Domain DNS configured correctly
- [ ] SSL certificates obtained and valid
- [ ] Database backup strategy in place

### Configuration Validation

- [ ] Environment variables configured for production
- [ ] Database passwords changed from defaults
- [ ] Redis password configured
- [ ] Stripe API keys configured (production keys)
- [ ] SMTP credentials configured
- [ ] CORS origins set to production domains
- [ ] CSP frame ancestors set to production domains

### Security Validation

- [ ] SSL/TLS certificates valid and not expiring soon
- [ ] Security headers configured in nginx
- [ ] CSRF protection enabled
- [ ] Rate limiting enabled
- [ ] File permissions set correctly (600 for .env)
- [ ] Non-root user configured in Docker containers
- [ ] Health checks configured for all services

## Build Pipeline Validation

### Backend Build Validation

```bash
# Test backend build locally
cd /Users/pc/eclipse-workspace/FashionStore
docker build -f Dockerfile.production -t fashionstore-backend:test .

# Verify image was created
docker images | grep fashionstore-backend

# Test image runs correctly
docker run --rm fashionstore-backend:test catalina.sh version
```

**Expected Output:**
- Docker image created successfully
- No build errors or warnings
- Tomcat version displayed

### Admin Frontend Build Validation

```bash
# Test admin frontend build locally
cd /Users/pc/eclipse-workspace/FashionStore/fashionstore-admin
docker build -t fashionstore-admin:test .

# Verify image was created
docker images | grep fashionstore-admin

# Test image runs correctly
docker run --rm -p 3001:3000 fashionstore-admin:test
```

**Expected Output:**
- Docker image created successfully
- No build errors or warnings
- Application starts on port 3001

### Docker Compose Validation

```bash
# Validate Docker Compose configuration
docker-compose -f docker-compose.production.yml config

# Check for syntax errors
docker-compose -f docker-compose.production.yml config --quiet
```

**Expected Output:**
- No syntax errors
- All services defined correctly
- Volume mounts configured properly
- Network configuration valid

## Service Health Validation

### Startup Validation

```bash
# Start all services
docker-compose -f docker-compose.production.yml up -d

# Wait for services to start (60 seconds)
sleep 60

# Check service status
docker-compose -f docker-compose.production.yml ps
```

**Expected Output:**
```
NAME                      STATUS
fashionstore-backend       Up (healthy)
fashionstore-admin        Up (healthy)
fashionstore-mysql        Up (healthy)
fashionstore-nginx        Up (healthy)
fashionstore-redis        Up (healthy)
```

### Health Check Validation

```bash
# Test backend health endpoint
curl -f http://localhost:8080/api/health

# Test admin frontend
curl -f http://localhost:3000/

# Test nginx proxy
curl -f http://localhost/

# Test SSL (if configured)
curl -f https://localhost/ -k
```

**Expected Output:**
- All endpoints return 200 OK
- Health endpoint returns healthy status
- No connection errors

## Database Validation

### MySQL Connection Validation

```bash
# Test MySQL connection
docker-compose -f docker-compose.production.yml exec mysql \
  mysql -u fashionstore -p fashionstore_production -e "SELECT 1;"

# Check database schema
docker-compose -f docker-compose.production.yml exec mysql \
  mysql -u fashionstore -p fashionstore_production -e "SHOW TABLES;"
```

**Expected Output:**
- Connection successful
- All required tables present:
  - users
  - products
  - categories
  - orders
  - order_items
  - cart
  - cart_items
  - wishlist
  - sizes

### Data Integrity Validation

```bash
# Check user table has admin user
docker-compose -f docker-compose.production.yml exec mysql \
  mysql -u fashionstore -p fashionstore_production \
  -e "SELECT email, role FROM users WHERE role='admin';"

# Check foreign key constraints
docker-compose -f docker-compose.production.yml exec mysql \
  mysql -u fashionstore -p fashionstore_production \
  -e "SELECT COUNT(*) FROM products WHERE category_id NOT IN (SELECT id FROM categories);"
```

**Expected Output:**
- Admin user exists with email: admin@fashionstore.com
- Foreign key violations: 0

## Redis Validation

### Redis Connection Validation

```bash
# Test Redis connection
docker-compose -f docker-compose.production.yml exec redis redis-cli ping

# Test Redis info
docker-compose -f docker-compose.production.yml exec redis redis-cli info
```

**Expected Output:**
- `PONG` response
- Redis info displays server information

### Cache Functionality Validation

```bash
# Test cache set/get
docker-compose -f docker-compose-production.yml exec redis redis-cli SET test_key "test_value"
docker-compose -f docker-compose-production.yml exec redis redis-cli GET test_key

# Clean up test data
docker-compose -f docker-compose.production.yml exec redis redis-cli DEL test_key
```

**Expected Output:**
- SET returns `OK`
- GET returns `test_value`

## API Validation

### Authentication Validation

```bash
# Test user registration
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"validation@example.com","password":"Valid123!","firstName":"Validation","lastName":"User"}'

# Test user login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fashionstore.com","password":"admin123"}'
```

**Expected Output:**
- Registration returns 201 Created
- Login returns 200 OK with JWT token

### Product API Validation

```bash
# Test product listing
curl http://localhost:8080/api/products

# Test product search
curl http://localhost:8080/api/products?search=shirt

# Test product filtering
curl http://localhost:8080/api/products?category=1
```

**Expected Output:**
- Product listing returns JSON array
- Search returns filtered results
- Filtering returns category-specific products

### Admin API Validation

```bash
# Test admin login
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fashionstore.com","password":"admin123"}'

# Store token
TOKEN=$(curl -s -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fashionstore.com","password":"admin123"}' | jq -r '.token')

# Test admin products endpoint
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/admin/products
```

**Expected Output:**
- Admin login returns token
- Products endpoint returns product list with admin data

## Performance Validation

### Response Time Validation

```bash
# Measure API response times
for i in {1..10}; do
  time curl -s http://localhost:8080/api/products > /dev/null
done
```

**Expected Output:**
- Average response time < 500ms
- No timeouts
- Consistent performance

### Resource Usage Validation

```bash
# Check resource usage
docker stats --no-stream

# Check container memory limits
docker inspect fashionstore-backend | grep Memory
docker inspect fashionstore-mysql | grep Memory
```

**Expected Output:**
- Backend memory usage < 2GB
- MySQL memory usage < 2GB
- No resource exhaustion

## Security Validation

### SSL/TLS Validation

```bash
# Test SSL configuration
openssl s_client -connect localhost:443 -servername localhost

# Check SSL certificate
openssl x509 -in nginx/ssl/fullchain.pem -noout -dates

# Test SSL protocols
nmap --script ssl-enum-ciphers -p 443 localhost
```

**Expected Output:**
- SSL certificate valid
- Only TLS 1.2 and 1.3 enabled
- Strong ciphers only

### Security Headers Validation

```bash
# Check security headers
curl -I http://localhost/

# Check HTTPS headers
curl -I https://localhost/ -k
```

**Expected Output:**
- Strict-Transport-Security header present
- X-Frame-Options header present
- X-Content-Type-Options header present
- X-XSS-Protection header present
- Content-Security-Policy header present

## CI/CD Pipeline Validation

### GitHub Actions Workflow Validation

1. Push to `develop` branch
2. Verify workflow triggers
3. Check quality-check job completes
4. Check build-backend job completes
5. Check build-admin job completes
6. Check integration-test job completes
7. Check deploy-staging job completes (if on develop branch)

**Expected Output:**
- All jobs pass successfully
- Docker images pushed to registry
- Security scans complete
- Integration tests pass
- Staging deployment successful

### Manual Build Validation

```bash
# Test local build
mvn clean package -DskipTests -Pproduction

# Verify WAR file created
ls -lh target/FashionStore.war

# Test WAR deployment to local Tomcat
cp target/FashionStore.war $TOMCAT_HOME/webapps/ROOT.war
```

**Expected Output:**
- WAR file created successfully
- No compilation errors
- Application deploys to Tomcat

## Rollback Validation

### Rollback Procedure Validation

```bash
# Stop services
docker-compose -f docker-compose.production.yml down

# Switch to previous version
git checkout <previous-commit-hash>

# Rebuild and deploy
docker build -f Dockerfile.production -t fashionstore-backend:previous .
docker-compose -f docker-compose.production.yml up -d

# Verify rollback
docker-compose -f docker-compose.production.yml ps
```

**Expected Output:**
- Services stop cleanly
- Previous version builds successfully
- Services start with previous version
- Application functions correctly

## Monitoring Validation

### Log Validation

```bash
# Check backend logs
docker-compose -f docker-compose.production.yml logs backend

# Check nginx logs
docker-compose -f docker-compose.production.yml logs nginx

# Check for errors
docker-compose -f docker-compose.production.yml logs | grep -i error
```

**Expected Output:**
- No critical errors
- No stack traces
- Normal startup sequence
- No connection refused errors

### Metrics Validation

```bash
# Check if metrics endpoint is accessible
curl http://localhost:9090/metrics

# Check Prometheus targets (if configured)
curl http://localhost:9090/api/v1/targets
```

**Expected Output:**
- Metrics endpoint returns data
- Prometheus targets are healthy
- No metric collection errors

## Final Validation Checklist

### Functionality Validation

- [ ] Customer storefront loads correctly
- [ ] Product listing displays products
- [ ] Product search works
- [ ] Shopping cart functions
- [ ] Checkout process works
- [ ] User registration works
- [ ] User login works
- [ ] Admin dashboard loads
- [ ] Admin product management works
- [ ] Admin order management works

### Performance Validation

- [ ] Page load time < 3 seconds
- [ ] API response time < 500ms
- [ ] No memory leaks
- [ ] No CPU spikes
- [ ] Database query performance acceptable

### Security Validation

- [ ] SSL certificate valid
- [ ] Security headers present
- [ ] CSRF protection working
- [ ] Rate limiting working
- [ ] SQL injection protection working
- [ ] XSS protection working

### Reliability Validation

- [ ] Health checks passing
- [ ] Automatic restarts working
- [ ] Database connections stable
- [ ] Redis cache working
- [ ] No service crashes

## Deployment Approval

After completing all validation steps:

- [ ] All validation steps passed
- [ ] No critical issues found
- [ ] Rollback procedure tested
- [ ] Monitoring configured
- [ ] Backup strategy verified
- [ ] Team notified
- [ ] Deployment approved

**Approval Signature:** __________________________

**Date:** __________________________

**Notes:** __________________________
