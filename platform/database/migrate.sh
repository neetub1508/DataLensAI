#!/bin/bash
# Database migration script for Data Lens AI
# This script applies all SQL migrations in order

set -e

# Configuration
DB_HOST=${DB_HOST:-localhost}
DB_PORT=${DB_PORT:-5432}
DB_NAME=${DB_NAME:-datalensai}
DB_USER=${DB_USER:-postgres}
DB_PASSWORD=${DB_PASSWORD:-postgres}

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Wait for database to be ready
wait_for_db() {
    log_info "Waiting for database to be ready..."
    local max_attempts=60
    local attempt=0
    
    while [ $attempt -lt $max_attempts ]; do
        if PGPASSWORD=$DB_PASSWORD psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c '\q' >/dev/null 2>&1; then
            log_info "Database is ready!"
            return 0
        fi
        
        attempt=$((attempt + 1))
        log_warn "Database is unavailable - attempt $attempt/$max_attempts - sleeping"
        sleep 2
    done
    
    log_error "Database failed to become available after $max_attempts attempts"
    exit 1
}

# Apply migrations
apply_migrations() {
    log_info "Applying database migrations..."
    
    MIGRATION_DIR="$(dirname "$0")/migrations"
    
    if [ ! -d "$MIGRATION_DIR" ]; then
        log_error "Migration directory not found: $MIGRATION_DIR"
        exit 1
    fi
    
    # Apply each migration file in order
    for migration_file in "$MIGRATION_DIR"/V*.sql; do
        if [ -f "$migration_file" ]; then
            filename=$(basename "$migration_file")
            log_info "Applying migration: $filename"
            
            PGPASSWORD=$DB_PASSWORD psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$migration_file"
            
            if [ $? -eq 0 ]; then
                log_info "Successfully applied: $filename"
            else
                log_error "Failed to apply migration: $filename"
                exit 1
            fi
        fi
    done
}

# Apply seed data
apply_seeds() {
    log_info "Applying seed data..."
    
    SEED_DIR="$(dirname "$0")/seeds"
    
    if [ ! -d "$SEED_DIR" ]; then
        log_warn "Seed directory not found: $SEED_DIR"
        return
    fi
    
    for seed_file in "$SEED_DIR"/*.sql; do
        if [ -f "$seed_file" ]; then
            filename=$(basename "$seed_file")
            log_info "Applying seed: $filename"
            
            PGPASSWORD=$DB_PASSWORD psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$seed_file"
            
            if [ $? -eq 0 ]; then
                log_info "Successfully applied seed: $filename"
            else
                log_warn "Failed to apply seed: $filename (this may be expected if data already exists)"
            fi
        fi
    done
}

# Main execution
main() {
    log_info "Starting database migration process..."
    
    wait_for_db
    apply_migrations
    
    if [ "$1" = "--with-seeds" ]; then
        apply_seeds
    fi
    
    log_info "Database migration completed successfully!"
}

# Show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --with-seeds    Apply seed data after migrations"
    echo "  --help          Show this help message"
    echo ""
    echo "Environment Variables:"
    echo "  DB_HOST         Database host (default: localhost)"
    echo "  DB_PORT         Database port (default: 5432)"
    echo "  DB_NAME         Database name (default: datalensai)"
    echo "  DB_USER         Database user (default: postgres)"
    echo "  DB_PASSWORD     Database password (default: postgres)"
}

# Parse arguments
case "${1:-}" in
    --help)
        show_usage
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac