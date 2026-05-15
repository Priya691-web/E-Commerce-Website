#!/bin/bash

# Database backup script for FashionStore
# Run daily via cron: 0 2 * * * /path/to/backup-db.sh

BACKUP_DIR="/backup/mysql"
BACKUP_RETENTION_DAYS=30
DB_NAME="fashionstore"
DB_USER="fashionstore"
DB_PASSWORD="${DB_PASSWORD}"
MYSQL_HOST="mysql"

# Create backup directory if it doesn't exist
mkdir -p "$BACKUP_DIR"

# Generate backup filename with timestamp
BACKUP_FILE="$BACKUP_DIR/fashionstore_$(date +%Y%m%d_%H%M%S).sql.gz"

echo "Starting database backup: $(date)"

# Perform backup
docker exec fashionstore-mysql-prod mysqldump \
    -h "$MYSQL_HOST" \
    -u "$DB_USER" \
    -p"$DB_PASSWORD" \
    --single-transaction \
    --quick \
    --lock-tables=false \
    "$DB_NAME" | gzip > "$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "Backup completed successfully: $BACKUP_FILE"
    
    # Remove old backups (retention policy)
    find "$BACKUP_DIR" -name "fashionstore_*.sql.gz" -type f -mtime +$BACKUP_RETENTION_DAYS -delete
    echo "Old backups removed (retention: $BACKUP_RETENTION_DAYS days)"
else
    echo "ERROR: Backup failed"
    exit 1
fi

echo "Backup process completed: $(date)"
