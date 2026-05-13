# SSL/HTTPS Configuration Guide for FashionStore

This guide explains how to configure SSL/HTTPS for the FashionStore production deployment using Let's Encrypt certificates.

## Prerequisites

- Domain name pointing to your server (e.g., `fashionstore.com`)
- Server with public IP address
- Docker and Docker Compose installed
- Port 80 and 443 open on firewall

## SSL Certificate Setup

### Option 1: Let's Encrypt with Certbot (Recommended)

#### Step 1: Generate SSL Certificates

```bash
# Install certbot
sudo apt-get update
sudo apt-get install certbot

# Generate certificates for your domain
sudo certbot certonly --standalone \
  -d fashionstore.com \
  -d www.fashionstore.com \
  -d admin.fashionstore.com \
  --email your-email@example.com \
  --agree-tos \
  --non-interactive
```

#### Step 2: Copy Certificates to Docker Volume

```bash
# Create SSL directory
mkdir -p nginx/ssl

# Copy certificates
sudo cp /etc/letsencrypt/live/fashionstore.com/fullchain.pem nginx/ssl/
sudo cp /etc/letsencrypt/live/fashionstore.com/privkey.pem nginx/ssl/
sudo cp /etc/letsencrypt/live/fashionstore.com/chain.pem nginx/ssl/

# Set proper permissions
sudo chmod 644 nginx/ssl/fullchain.pem
sudo chmod 600 nginx/ssl/privkey.pem
sudo chmod 644 nginx/ssl/chain.pem
```

#### Step 3: Set Up Auto-Renewal

```bash
# Create renewal script
cat > /usr/local/bin/renew-ssl.sh << 'EOF'
#!/bin/bash
certbot renew --quiet
cp /etc/letsencrypt/live/fashionstore.com/fullchain.pem /path/to/nginx/ssl/
cp /etc/letsencrypt/live/fashionstore.com/privkey.pem /path/to/nginx/ssl/
cp /etc/letsencrypt/live/fashionstore.com/chain.pem /path/to/nginx/ssl/
docker-compose -f docker-compose.production.yml restart nginx
EOF

chmod +x /usr/local/bin/renew-ssl.sh

# Add to crontab (renew twice daily)
(crontab -l 2>/dev/null; echo "0 0,12 * * * /usr/local/bin/renew-ssl.sh >> /var/log/ssl-renewal.log 2>&1") | crontab -
```

### Option 2: Self-Signed Certificate (Development Only)

```bash
# Generate self-signed certificate
mkdir -p nginx/ssl
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/ssl/privkey.pem \
  -out nginx/ssl/fullchain.pem \
  -subj "/C=US/ST=State/L=City/O=Organization/CN=localhost"
```

### Option 3: Commercial SSL Certificate

If you have a commercial SSL certificate (e.g., from DigiCert, Comodo):

```bash
# Copy your certificate files to nginx/ssl/
cp your-certificate.crt nginx/ssl/fullchain.pem
cp your-private-key.key nginx/ssl/privkey.pem
cp ca-bundle.crt nginx/ssl/chain.pem

# Set permissions
chmod 644 nginx/ssl/fullchain.pem
chmod 600 nginx/ssl/privkey.pem
chmod 644 nginx/ssl/chain.pem
```

## Docker Compose SSL Configuration

The `docker-compose.production.yml` file already includes SSL volume mounting:

```yaml
nginx:
  volumes:
    - ./nginx.production.conf:/etc/nginx/nginx.conf:ro
    - ./nginx/ssl:/etc/nginx/ssl:ro
```

## Verify SSL Configuration

### Test SSL Configuration

```bash
# Start services
docker-compose -f docker-compose.production.yml up -d

# Test SSL connection
openssl s_client -connect fashionstore.com:443 -servername fashionstore.com

# Check SSL rating
curl https://www.ssllabs.com/ssltest/analyze.html?d=fashionstore.com
```

### Check Certificate Expiry

```bash
# Check certificate expiry date
openssl x509 -in nginx/ssl/fullchain.pem -noout -dates
```

## Troubleshooting

### Certificate Not Found

If nginx fails to start due to missing certificates:

```bash
# Check if certificates exist
ls -la nginx/ssl/

# Ensure correct permissions
chmod 644 nginx/ssl/fullchain.pem
chmod 600 nginx/ssl/privkey.pem
```

### Certificate Renewal Failed

Check the renewal logs:

```bash
cat /var/log/ssl-renewal.log
```

Manual renewal:

```bash
sudo certbot renew --force-renewal
```

### Mixed Content Warnings

Ensure all resources use HTTPS:
- Update hardcoded HTTP URLs in your application
- Use protocol-relative URLs (`//cdn.example.com/style.css`)
- Configure Content Security Policy (CSP) headers

## Security Best Practices

1. **Use Strong Ciphers:** The nginx configuration already uses strong TLS 1.2/1.3 ciphers
2. **Enable HSTS:** HTTP Strict Transport Security is enabled with a 2-year max-age
3. **OCSP Stapling:** Enabled for faster certificate validation
4. **Certificate Rotation:** Set up automatic renewal before expiration
5. **Monitor Expiry:** Set up alerts 30 days before certificate expiration

## SSL Configuration Files

The following files are used for SSL:

- `nginx/ssl/fullchain.pem` - Full certificate chain
- `nginx/ssl/privkey.pem` - Private key
- `nginx/ssl/chain.pem` - Intermediate certificate chain
- `nginx.production.conf` - Nginx configuration with SSL settings

## Next Steps

After SSL is configured:

1. Update your environment variables in `.env.production`
2. Update CORS and CSP allowed origins to use HTTPS
3. Test the application with HTTPS
4. Set up monitoring for SSL certificate expiration
5. Configure backup for SSL certificates
