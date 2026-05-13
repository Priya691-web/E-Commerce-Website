# FashionStore Production Deployment Guide

This guide provides step-by-step instructions for deploying FashionStore to a production environment using Docker and Docker Compose.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Server Preparation](#server-preparation)
- [SSL Certificate Setup](#ssl-certificate-setup)
- [Environment Configuration](#environment-configuration)
- [Database Initialization](#database-initialization)
- [Application Deployment](#application-deployment)
- [Post-Deployment Verification](#post-deployment-verification)
- [Monitoring and Maintenance](#monitoring-and-maintenance)
- [Troubleshooting](#troubleshooting)

---

## Prerequisites

### Server Requirements

- **OS:** Ubuntu 22.04 LTS or later (recommended)
- **CPU:** 4 cores minimum (8 cores recommended)
- **RAM:** 8GB minimum (16GB recommended)
- **Storage:** 50GB SSD minimum (100GB recommended)
- **Network:** Public IP with ports 80 and 443 open

### Software Requirements

- **Docker:** 24.0 or later
- **Docker Compose:** 2.20 or later
- **Git:** 2.30 or later
- **OpenSSL:** 1.1.1 or later

### Domain Requirements

- Domain name pointing to your server (e.g., `fashionstore.com`)
- DNS A record configured for `@` and `www`
- DNS A record configured for `admin` subdomain

---

## Server Preparation

### Step 1: Update System Packages

```bash
sudo apt-get update
sudo apt-get upgrade -y
```

### Step 2: Install Docker

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installation
docker --version
docker-compose --version
```

### Step 3: Install Additional Tools

```bash
# Install Git
sudo apt-get install git -y

# Install Certbot for SSL
sudo apt-get install certbot -y

# Install monitoring tools
sudo apt-get install htop iotop -y
```

### Step 4: Configure Firewall

```bash
# Allow SSH
sudo ufw allow 22/tcp

# Allow HTTP and HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Enable firewall
sudo ufw enable
```

### Step 5: Create Application Directory

```bash
sudo mkdir -p /opt/fashionstore
sudo chown $USER:$USER /opt/fashionstore
cd /opt/fashionstore
```

---

## SSL Certificate Setup

### Step 1: Generate SSL Certificates

Follow the instructions in [SSL_SETUP.md](SSL_SETUP.md) to generate SSL certificates using Let's Encrypt.

### Step 2: Verify Certificates

```bash
ls -la nginx/ssl/
```

Expected output:
```
total 24
-rw-r--r-- 1 root root 1704 May 11 08:00 chain.pem
-rw-r--r-- 1 root root 4412 May 11 08:00 fullchain.pem
-rw------- 1 root root 1704 May 11 08:00 privkey.pem
```

---

## Environment Configuration

### Step 1: Clone Repository

```bash
cd /opt/fashionstore
git clone https://github.com/yourusername/FashionStore.git .
```

### Step 2: Configure Environment Variables

```bash
# Copy production environment template
cp .env.production .env

# Edit environment variables
nano .env
```

**Required variables to update:**

```bash
# Database credentials
DB_PASSWORD=your_secure_db_password
MYSQL_ROOT_PASSWORD=your_secure_root_password

# Redis password
REDIS_PASSWORD=your_secure_redis_password

# Stripe keys
STRIPE_API_KEY=sk_live_your_stripe_key
STRIPE_PUBLIC_KEY=pk_live_your_stripe_public_key
STRIPE_WEBHOOK_SECRET=whsec_your_webhook_secret

# Email credentials
SMTP_PASSWORD=your_smtp_password
```

### Step 3: Set File Permissions

```bash
chmod 600 .env
chmod 644 nginx.production.conf
```

---

## Database Initialization

### Step 1: Start MySQL Container

```bash
docker-compose -f docker-compose.production.yml up -d mysql
```

### Step 2: Wait for MySQL to be Ready

```bash
docker-compose -f docker-compose.production.yml logs -f mysql
```

Wait until you see: `MySQL init process done. Ready for start up.`

### Step 3: Initialize Database Schema

```bash
# Copy SQL schema file
cp src/main/resources/schema.sql docker/mysql/init/

# Restart MySQL to apply schema
docker-compose -f docker-compose.production.yml restart mysql
```

### Step 4: Verify Database Connection

```bash
docker-compose -f docker-compose.production.yml exec mysql mysql -u fashionstore -p fashionstore_production
```

---

## Application Deployment

### Step 1: Build Docker Images

```bash
# Build backend image
docker build -f Dockerfile.production -t fashionstore-backend:latest .

# Build admin frontend image
cd fashionstore-admin
docker build -t fashionstore-admin:latest .
cd ..
```

### Step 2: Start All Services

```bash
docker-compose -f docker-compose.production.yml up -d
```

### Step 3: Verify Service Health

```bash
# Check all services
docker-compose -f docker-compose.production.yml ps

# Check logs
docker-compose -f docker-compose.production.yml logs -f
```

Expected output:
```
NAME                      STATUS
fashionstore-backend       Up (healthy)
fashionstore-admin        Up (healthy)
fashionstore-mysql        Up (healthy)
fashionstore-nginx        Up (healthy)
fashionstore-redis        Up (healthy)
```

---

## Post-Deployment Verification

### Step 1: Verify SSL Configuration

```bash
# Test SSL certificate
openssl s_client -connect fashionstore.com:443 -servername fashionstore.com

# Check SSL rating
curl https://www.ssllabs.com/ssltest/analyze.html?d=fashionstore.com
```

### Step 2: Verify Application Access

```bash
# Test customer storefront
curl -I https://fashionstore.com

# Test admin dashboard
curl -I https://admin.fashionstore.com

# Test API health endpoint
curl https://api.fashionstore.com/api/health
```

### Step 3: Verify Database Connection

```bash
docker-compose -f docker-compose.production.yml exec backend curl -f http://localhost:8080/api/health
```

### Step 4: Verify Redis Connection

```bash
docker-compose -f docker-compose.production.yml exec redis redis-cli ping
```

Expected output: `PONG`

### Step 5: Run Smoke Tests

```bash
# Create test user
curl -X POST https://fashionstore.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test123!","firstName":"Test","lastName":"User"}'

# Test product listing
curl https://fashionstore.com/api/products

# Test admin login
curl -X POST https://api.fashionstore.com/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@fashionstore.com","password":"admin123"}'
```

---

## Monitoring and Maintenance

### Step 1: Set Up Log Rotation

```bash
sudo nano /etc/logrotate.d/fashionstore
```

Add the following:

```
/opt/fashionstore/logs/*.log {
    daily
    rotate 14
    compress
    delaycompress
    notifempty
    create 0644 fashionstore fashionstore
    sharedscripts
    postrotate
        docker-compose -f /opt/fashionstore/docker-compose.production.yml restart nginx
    endscript
}
```

### Step 2: Set Up Automated Backups

```bash
# Create backup script
cat > /opt/fashionstore/scripts/backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/opt/fashionstore/backups"
DATE=$(date +%Y%m%d_%H%M%S)
mkdir -p $BACKUP_DIR

# Backup MySQL
docker-compose -f /opt/fashionstore/docker-compose.production.yml exec mysql \
  mysqldump -u root -p${MYSQL_ROOT_PASSWORD} fashionstore_production > \
  $BACKUP_DIR/mysql_backup_$DATE.sql

# Backup Redis
docker-compose -f /opt/fashionstore/docker-compose.production.yml exec redis \
  redis-cli --rdb /data/dump.rdb BGSAVE

# Compress backup
gzip $BACKUP_DIR/mysql_backup_$DATE.sql

# Remove old backups (keep 30 days)
find $BACKUP_DIR -name "*.sql.gz" -mtime +30 -delete
EOF

chmod +x /opt/fashionstore/scripts/backup.sh

# Add to crontab (daily at 2 AM)
(crontab -l 2>/dev/null; echo "0 2 * * * /opt/fashionstore/scripts/backup.sh >> /var/log/fashionstore-backup.log 2>&1") | crontab -
```

### Step 3: Set Up Monitoring

Install and configure monitoring tools:

```bash
# Install Prometheus and Grafana (optional)
docker run -d \
  --name prometheus \
  -p 9090:9090 \
  -v /opt/fashionstore/monitoring/prometheus.yml:/etc/prometheus/prometheus.yml \
  prom/prometheus

docker run -d \
  --name grafana \
  -p 3001:3000 \
  grafana/grafana
```

---

## Troubleshooting

### Issue: Container Won't Start

```bash
# Check container logs
docker-compose -f docker-compose.production.yml logs backend

# Check resource usage
docker stats

# Restart specific service
docker-compose -f docker-compose.production.yml restart backend
```

### Issue: Database Connection Failed

```bash
# Verify MySQL is running
docker-compose -f docker-compose.production.yml ps mysql

# Check MySQL logs
docker-compose -f docker-compose.production.yml logs mysql

# Test database connection
docker-compose -f docker-compose.production.yml exec mysql mysql -u fashionstore -p
```

### Issue: SSL Certificate Error

```bash
# Verify certificate files exist
ls -la nginx/ssl/

# Check certificate validity
openssl x509 -in nginx/ssl/fullchain.pem -noout -dates

# Renew certificate manually
sudo certbot renew --force-renewal

# Restart nginx
docker-compose -f docker-compose.production.yml restart nginx
```

### Issue: High Memory Usage

```bash
# Check resource usage
docker stats

# Adjust memory limits in docker-compose.production.yml
# Reduce backend memory limit if needed
```

### Issue: Slow Response Time

```bash
# Check Redis connection
docker-compose -f docker-compose.production.yml exec redis redis-cli ping

# Clear Redis cache
docker-compose -f docker-compose.production.yml exec redis redis-cli FLUSHALL

# Check database performance
docker-compose -f docker-compose.production.yml exec mysql \
  mysql -u fashionstore -p fashionstore_production -e "SHOW PROCESSLIST;"
```

---

## Rollback Procedure

If deployment fails, rollback to previous version:

```bash
# Stop all services
docker-compose -f docker-compose.production.yml down

# Switch to previous Git commit
git checkout <previous-commit-hash>

# Rebuild and deploy
docker build -f Dockerfile.production -t fashionstore-backend:latest .
docker-compose -f docker-compose.production.yml up -d

# Verify deployment
docker-compose -f docker-compose.production.yml ps
```

---

## Security Checklist

- [ ] SSL certificates installed and valid
- [ ] Firewall configured correctly
- [ ] Database passwords changed from defaults
- [ ] Redis password configured
- [ ] Environment variables secured (600 permissions)
- [ ] CSRF protection enabled
- [ ] Rate limiting enabled
- [ ] Security headers configured in nginx
- [ ] Automatic backups configured
- [ ] Log rotation configured
- [ ] Monitoring set up
- [ ] SSL auto-renewal configured

---

## Support

For issues or questions:
- Check logs: `docker-compose -f docker-compose.production.yml logs`
- Review troubleshooting section above
- Check GitHub issues
- Contact DevOps team

---

## Next Steps

After successful deployment:
1. Configure CDN for static assets
2. Set up application performance monitoring (APM)
3. Configure error tracking (Sentry, etc.)
4. Set up alerting for critical issues
5. Document custom configurations
6. Train operations team
