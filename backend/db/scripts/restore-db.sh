#!/bin/bash

# Database restore script for FashionStore
# Usage: ./restore-db.sh /path/to/backup.sql.gz

if [ $# -ne 1 ]; then
    echo "Usage: $0 <backup_file.sql.gz>"
    exit 1
fi

BACKUP_FILE=$1
DB_NAME="fashionstore"
DB_USER="fashionstore"
DB_PASSWORD="${DB_PASSWORD}"
MYSQL_HOST="mysql"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "ERROR: Backup file not found: $BACKUP_FILE"
    exit 1
fi

echo "Starting database restore from: $BACKUP_FILE"
echo "WARNING: This will overwrite the existing database!"
read -p "Are you sure? (yes/no): " confirm

if [ "$confirm" != "yes" ]; then
    echo "Restore cancelled"
    exit 0
fi

# Stop backend during restore
docker-compose -f docker-compose.prod.yml stop backend

# Perform restore
gunzip -c "$BACKUP_FILE" | docker exec -i fashionstore-mysql-prod mysql \
    -h "$MYSQL_HOST" \
    -u "$DB_USER" \
    -p"$DB_PASSWORD" \
    "$DB_NAME"

if [ $? -eq 0 ]; then
    echo "Restore completed successfully"
    
    # Restart backend
    docker-compose -f docker-compose.prod.yml start backend
    echo "Backend restarted"
else
    echo "ERROR: Restore failed"
    exit 1
fi

echo "Restore process completed"
