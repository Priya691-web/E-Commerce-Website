# Production Deployment Guide

## Overview

This guide provides step-by-step instructions for deploying FashionStore to production using Docker and Nginx.

## Prerequisites

- Linux server (Ubuntu 20.04+ recommended)
- Docker and Docker Compose installed
- Domain name with DNS configured
- SSL certificate (Let's Encrypt recommended)
- Minimum 2GB RAM, 2 CPU cores

## Quick Start

### 1. Clone Repository

```bash
git clone https://github.com/tarun2806/FashionStore.git
cd FashionStore
```

### 2. Configure Environment

```bash
cp .env.example .env
nano .env
```

Update the following variables:
- `DB_PASSWORD` - Strong MySQL password
- `REDIS_PASSWORD` - Strong Redis password
- `JWT_SECRET` - Strong JWT secret key
- `RAZORPAY_KEY_ID` - Razorpay key ID
- `RAZORPAY_KEY_SECRET` - Razorpay key secret

### 3. Build Application

```bash
mvn clean package -DskipTests
```

### 4. Setup SSL Certificate

```bash
# Install certbot
sudo apt update
sudo apt install certbot python3-certbot-nginx

# Generate certificate
sudo certbot --nginx -d fashionstore.com -d www.fashionstore.com

# Certificate will be auto-configured in nginx.conf
```

### 5. Start Services

```bash
docker-compose up -d
```

### 6. Verify Deployment

```bash
# Check service status
docker-compose ps

# Check logs
docker-compose logs -f app

# Test health endpoint
curl http://localhost/health
```

## Detailed Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| APP_ENV | Application environment | production |
| DB_HOST | Database host | mysql |
| DB_PORT | Database port | 3306 |
| DB_NAME | Database name | fashionstore |
| DB_USER | Database user | fashionstore |
| DB_PASSWORD | Database password | fashionstore |
| REDIS_HOST | Redis host | redis |
| REDIS_PORT | Redis port | 6379 |
| REDIS_PASSWORD | Redis password | (empty) |
| REDIS_ENABLED | Enable Redis | true |
| JWT_SECRET | JWT secret key | (change this) |
| JWT_EXPIRATION | JWT expiration (ms) | 86400000 |

### Database Setup

The MySQL container will automatically initialize the database using `schema.sql` on first run.

### Redis Setup

Redis is configured with:
- 256MB memory limit
- LRU eviction policy
- AOF persistence enabled

### Nginx Configuration

Nginx provides:
- SSL/TLS termination
- Reverse proxy to Tomcat
- Static file caching
- Gzip compression
- Rate limiting
- Security headers

## Security Hardening

### 1. Firewall Configuration

```bash
# Allow SSH
sudo ufw allow 22/tcp

# Allow HTTP/HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Enable firewall
sudo ufw enable
```

### 2. Disable Root SSH

```bash
sudo nano /etc/ssh/sshd_config
# Set: PermitRootLogin no
sudo systemctl restart ssh
```

### 3. Automatic Security Updates

```bash
sudo apt install unattended-upgrades
sudo dpkg-reconfigure -plow unattended-upgrades
```

### 4. Database Security

- Change default MySQL root password
- Create dedicated database user with limited privileges
- Enable SSL for database connections (if needed)

## Monitoring

### Application Logs

```bash
# View application logs
docker-compose logs -f app

# View Nginx logs
docker-compose logs -f nginx
```

### Database Monitoring

```bash
# Connect to MySQL
docker exec -it fashionstore-mysql mysql -u root -p

# Check database size
SELECT table_name AS "Table", 
       ROUND(((data_length + index_length) / 1024 / 1024), 2) AS "Size (MB)" 
FROM information_schema.TABLES 
WHERE table_schema = 'fashionstore' 
ORDER BY (data_length + index_length) DESC;
```

### Redis Monitoring

```bash
# Connect to Redis
docker exec -it fashionstore-redis redis-cli

# Check memory usage
INFO memory

# Check keys count
DBSIZE
```

## Backup Strategy

### Database Backup

```bash
# Create backup script
cat > /opt/backup.sh << 'EOF'
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
docker exec fashionstore-mysql mysqldump -u root -p${DB_PASSWORD} fashionstore > /backup/fashionstore_${DATE}.sql
find /backup -name "fashionstore_*.sql" -mtime +7 -delete
EOF

chmod +x /opt/backup.sh

# Add to crontab (daily at 2 AM)
crontab -e
# Add: 0 2 * * * /opt/backup.sh
```

### Redis Backup

```bash
# Redis AOF is enabled by default
# Backup AOF file
docker cp fashionstore-redis:/data/appendonly.aof /backup/redis_${DATE}.aof
```

## Scaling

### Horizontal Scaling

To scale the application:

```yaml
# In docker-compose.yml
app:
  deploy:
    replicas: 3
  # Add load balancer (nginx already handles this)
```

### Database Scaling

For high traffic:
- Use managed database (AWS RDS, Google Cloud SQL)
- Enable read replicas
- Use connection pooling (HikariCP already configured)

## Troubleshooting

### Application Won't Start

```bash
# Check logs
docker-compose logs app

# Check database connection
docker exec -it fashionstore-app ping mysql

# Check Redis connection
docker exec -it fashionstore-app ping redis
```

### High Memory Usage

```bash
# Check container resource usage
docker stats

# Adjust memory limits in docker-compose.yml
app:
  deploy:
    resources:
      limits:
        memory: 1G
```

### Slow Performance

```bash
# Check database queries
docker exec -it fashionstore-mysql mysql -u root -p -e "SHOW PROCESSLIST;"

# Enable slow query log in MySQL
# Add to my.cnf: slow_query_log = 1, long_query_time = 2
```

## Maintenance

### Update Application

```bash
# Pull latest code
git pull

# Rebuild
docker-compose build app

# Restart
docker-compose up -d app
```

### Update Dependencies

```bash
# Update Maven dependencies
mvn versions:display-dependency-updates

# Update Docker images
docker-compose pull
docker-compose up -d
```

## Production Checklist

- [ ] Environment variables configured
- [ ] SSL certificate installed
- [ ] Firewall configured
- [ ] Database backup automated
- [ ] Monitoring enabled
- [ ] Log rotation configured
- [ ] Security headers verified
- [ ] Rate limiting tested
- [ ] Database connection pooling configured
- [ ] Redis caching enabled
- [ ] Error tracking setup
- [ ] Performance monitoring enabled
- [ ] Disaster recovery plan documented
