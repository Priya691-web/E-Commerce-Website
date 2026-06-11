#!/bin/bash

# ============================================================
# FashionStore Deployment Script
# Production deployment automation script
# ============================================================

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
DEPLOY_DIR="/var/www/fashionstore"
BACKUP_DIR="/var/backups/fashionstore"
LOG_FILE="/var/log/fashionstore-deploy.log"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

# Functions
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1" | tee -a "$LOG_FILE"
}

# Check if running as root
check_root() {
    if [ "$EUID" -ne 0 ]; then
        error "This script must be run as root"
        exit 1
    fi
}

# Create backup
create_backup() {
    log "Creating backup..."
    
    mkdir -p "$BACKUP_DIR"
    
    # Backup current deployment
    if [ -d "$DEPLOY_DIR" ]; then
        BACKUP_PATH="$BACKUP_DIR/fashionstore_$TIMESTAMP"
        cp -r "$DEPLOY_DIR" "$BACKUP_PATH"
        log "Backup created at: $BACKUP_PATH"
        
        # Keep only last 5 backups
        ls -t "$BACKUP_DIR" | tail -n +6 | xargs -I {} rm -rf "$BACKUP_DIR/{}"
    else
        warn "No existing deployment to backup"
    fi
}

# Stop services
stop_services() {
    log "Stopping services..."
    
    cd "$DEPLOY_DIR"
    
    # Stop docker containers
    docker-compose -f docker-compose.prod.yml down
    
    log "Services stopped"
}

# Pull latest code
pull_code() {
    log "Pulling latest code..."
    
    cd "$DEPLOY_DIR"
    git pull origin main
    
    log "Code pulled successfully"
}

# Build Docker images
build_images() {
    log "Building Docker images..."
    
    cd "$DEPLOY_DIR"
    docker-compose -f docker-compose.prod.yml build --no-cache
    
    log "Docker images built successfully"
}

# Start services
start_services() {
    log "Starting services..."
    
    cd "$DEPLOY_DIR"
    docker-compose -f docker-compose.prod.yml up -d
    
    log "Services started"
}

# Run database migrations
run_migrations() {
    log "Running database migrations..."
    
    cd "$DEPLOY_DIR/backend"
    
    # Execute database indexes
    docker-compose -f ../docker-compose.prod.yml exec -T mysql mysql -u root -p"${MYSQL_ROOT_PASSWORD}" fashionstore < database_indexes.sql
    
    log "Database migrations completed"
}

# Health check
health_check() {
    log "Performing health check..."
    
    MAX_RETRIES=30
    RETRY_COUNT=0
    
    while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
        if curl -f http://localhost:8080/health > /dev/null 2>&1; then
            log "Health check passed"
            return 0
        fi
        
        RETRY_COUNT=$((RETRY_COUNT + 1))
        warn "Health check failed, retrying... ($RETRY_COUNT/$MAX_RETRIES)"
        sleep 5
    done
    
    error "Health check failed after $MAX_RETRIES attempts"
    return 1
}

# Rollback on failure
rollback() {
    error "Deployment failed, initiating rollback..."
    
    LATEST_BACKUP=$(ls -t "$BACKUP_DIR" | head -n 1)
    
    if [ -n "$LATEST_BACKUP" ]; then
        log "Rolling back to: $LATEST_BACKUP"
        
        # Stop services
        docker-compose -f docker-compose.prod.yml down
        
        # Restore backup
        rm -rf "$DEPLOY_DIR"
        cp -r "$BACKUP_DIR/$LATEST_BACKUP" "$DEPLOY_DIR"
        
        # Start services
        docker-compose -f docker-compose.prod.yml up -d
        
        log "Rollback completed"
    else
        error "No backup found for rollback"
        exit 1
    fi
}

# Main deployment function
deploy() {
    log "Starting FashionStore deployment..."
    
    check_root
    create_backup
    stop_services
    pull_code
    build_images
    start_services
    run_migrations
    
    if health_check; then
        log "Deployment completed successfully!"
        log "Application is now live at: https://fashionstore.com"
    else
        error "Deployment failed"
        rollback
        exit 1
    fi
}

# Parse command line arguments
case "${1:-deploy}" in
    deploy)
        deploy
        ;;
    backup)
        create_backup
        ;;
    rollback)
        rollback
        ;;
    health)
        health_check
        ;;
    *)
        echo "Usage: $0 {deploy|backup|rollback|health}"
        exit 1
        ;;
esac
