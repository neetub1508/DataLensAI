#!/bin/bash
# Stop script for Data Lens AI platform

set -e

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

# Show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --remove-volumes    Remove data volumes (WARNING: This will delete all data)"
    echo "  --help              Show this help message"
}

# Stop services
stop_services() {
    log_info "Stopping Data Lens AI services..."
    
    if docker-compose ps -q | grep -q .; then
        docker-compose down
        log_info "All services stopped successfully!"
    else
        log_warn "No running services found"
    fi
}

# Remove volumes
remove_volumes() {
    log_warn "Removing data volumes - THIS WILL DELETE ALL DATA!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        docker-compose down -v
        log_info "Volumes removed"
    else
        log_info "Volume removal cancelled"
    fi
}

# Main execution
main() {
    local remove_volumes_flag=false
    
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --remove-volumes)
                remove_volumes_flag=true
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
    
    if [ "$remove_volumes_flag" = true ]; then
        remove_volumes
    else
        stop_services
    fi
}

main "$@"