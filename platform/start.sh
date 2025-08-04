#!/bin/bash
# Complete startup script for Data Lens AI platform
# This script will clean, build, and start all services in Docker

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# Show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --clean-all     Remove all containers, images, and volumes"
    echo "  --clean-build   Remove only build artifacts and rebuild"
    echo "  --no-build      Skip building images (use existing)"
    echo "  --help          Show this help message"
}

# Clean all Docker resources
clean_all() {
    log_step "Cleaning all Docker resources..."
    
    # Stop and remove containers
    if [ $(docker ps -aq -f name=datalens | wc -l) -gt 0 ]; then
        log_info "Stopping and removing existing containers..."
        docker-compose down --remove-orphans
        docker container prune -f
    fi
    
    # Remove images
    log_info "Removing Docker images..."
    docker image prune -af
    if [ $(docker images -q datalens* | wc -l) -gt 0 ]; then
        docker rmi $(docker images -q datalens*) || true
    fi
    
    # Remove volumes
    log_info "Removing Docker volumes..."
    docker volume prune -f
    
    log_info "All Docker resources cleaned!"
}

# Clean build artifacts
clean_build() {
    log_step "Cleaning build artifacts..."
    
    # Stop containers
    docker-compose down --remove-orphans || true
    
    # Remove build images only
    docker-compose build --no-cache
    
    log_info "Build artifacts cleaned!"
}

# Check prerequisites
check_prerequisites() {
    log_step "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        log_error "Docker is not installed or not in PATH"
        exit 1
    fi
    
    # Check Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        log_error "Docker Compose is not installed or not in PATH"
        exit 1
    fi
    
    # Check if Docker daemon is running
    if ! docker info &> /dev/null; then
        log_error "Docker daemon is not running"
        exit 1
    fi
    
    log_info "All prerequisites met!"
}

# Build services
build_services() {
    log_step "Building Docker services..."
    
    # Build all services
    docker-compose build --parallel
    
    log_info "All services built successfully!"
}

# Start services
start_services() {
    log_step "Starting all services..."
    
    # Start services in correct order
    docker-compose up -d postgres redis
    log_info "Database services started, waiting for health checks..."
    
    # Wait for database to be healthy
    docker-compose up --wait postgres redis
    
    # Run migrations
    log_info "Running database migrations..."
    docker-compose up db-migrate
    
    # Start application services
    log_info "Starting application services..."
    docker-compose up -d backend frontend
    
    # Wait for all services to be healthy
    docker-compose up --wait
    
    log_info "All services are running!"
}

# Show service status
show_status() {
    log_step "Service Status:"
    docker-compose ps
    
    echo ""
    log_step "Service URLs:"
    echo "Frontend: http://localhost:3000"
    echo "Backend API: http://localhost:8000"
    echo "PostgreSQL: localhost:5432"
    echo "Redis: localhost:6379"
    
    echo ""
    log_step "Default Login Credentials:"
    echo "Admin: admin@datalens.ai / admin123"
    echo "User: user@datalens.ai / admin123"
}

# Main execution
main() {
    local clean_all_flag=false
    local clean_build_flag=false
    local no_build_flag=false
    
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --clean-all)
                clean_all_flag=true
                shift
                ;;
            --clean-build)
                clean_build_flag=true
                shift
                ;;
            --no-build)
                no_build_flag=true
                shift
                ;;
            --help)
                show_usage
                exit 0
                ;;
            *)
                log_error "Unknown option: $1"
                show_usage
                exit 1
                ;;
        esac
    done
    
    log_info "Starting Data Lens AI platform..."
    
    check_prerequisites
    
    if [ "$clean_all_flag" = true ]; then
        clean_all
    elif [ "$clean_build_flag" = true ]; then
        clean_build
    fi
    
    if [ "$no_build_flag" = false ]; then
        build_services
    fi
    
    start_services
    show_status
    
    log_info "Platform startup completed successfully!"
    log_warn "Press Ctrl+C to stop all services"
    
    # Follow logs
    docker-compose logs -f
}

# Handle interruption
trap 'log_info "Stopping all services..."; docker-compose down; exit 0' INT

main "$@"