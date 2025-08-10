#!/bin/bash
# Containerized Test Runner for Data Lens AI Platform
# All tests run inside Docker containers - no local dependencies required

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
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

log_test() {
    echo -e "${PURPLE}[TEST]${NC} $1"
}

# Configuration
COMPOSE_FILE="docker-compose.test.yml"
PROJECT_NAME="datalens-test"

# Test flags
RUN_UNIT=false
RUN_INTEGRATION=false
RUN_E2E=false
RUN_PERFORMANCE=false
RUN_SECURITY=false
RUN_ALL=false
CLEANUP=true
FOLLOW_LOGS=false

show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Test Types (run inside containers):"
    echo "  --unit              Run backend and frontend unit tests"
    echo "  --integration       Run integration tests"
    echo "  --e2e              Run end-to-end tests"
    echo "  --performance      Run performance tests"
    echo "  --security         Run security tests"
    echo "  --all              Run all test types"
    echo ""
    echo "Options:"
    echo "  --no-cleanup       Don't cleanup containers after tests"
    echo "  --follow-logs      Follow container logs during execution"
    echo "  --help             Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 --unit                    # Run unit tests only"
    echo "  $0 --all                     # Run all test types"
    echo "  $0 --unit --integration      # Run unit and integration tests"
    echo "  $0 --e2e --no-cleanup        # Run E2E tests and keep containers"
}

# Cleanup function
cleanup() {
    if [ "$CLEANUP" = true ]; then
        log_step "Cleaning up test containers..."
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" down --volumes --remove-orphans 2>/dev/null || true
        log_info "Cleanup completed"
    else
        log_info "Skipping cleanup (containers still running)"
        log_info "To cleanup manually: docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down --volumes"
    fi
}

# Setup test environment
setup_test_environment() {
    log_step "Setting up containerized test environment..."
    
    # Check if Docker is running
    if ! docker info >/dev/null 2>&1; then
        log_error "Docker is not running. Please start Docker and try again."
        exit 1
    fi
    
    # Check if compose file exists
    if [ ! -f "$COMPOSE_FILE" ]; then
        log_error "Docker compose file '$COMPOSE_FILE' not found"
        exit 1
    fi
    
    # Create test reports directory
    mkdir -p ./test-reports
    
    # Start base services (databases)
    log_info "Starting test infrastructure (databases)..."
    docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up -d postgres-test redis-test
    
    # Wait for services to be healthy
    log_info "Waiting for infrastructure services to be ready..."
    docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --wait postgres-test redis-test
    
    log_info "Test environment setup completed"
}

# Run unit tests
run_unit_tests() {
    log_test "Running containerized unit tests..."
    
    # Run backend unit tests
    log_info "Starting backend unit tests..."
    if [ "$FOLLOW_LOGS" = true ]; then
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit backend-test
    else
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit --quiet-pull backend-test
    fi
    
    # Run frontend unit tests
    log_info "Starting frontend unit tests..."
    if [ "$FOLLOW_LOGS" = true ]; then
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit frontend-test
    else
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit --quiet-pull frontend-test
    fi
    
    log_info "Unit tests completed"
}

# Run integration tests
run_integration_tests() {
    log_test "Running containerized integration tests..."
    
    if [ "$FOLLOW_LOGS" = true ]; then
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit integration-test
    else
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit --quiet-pull integration-test
    fi
    
    log_info "Integration tests completed"
}

# Run E2E tests
run_e2e_tests() {
    log_test "Running containerized E2E tests..."
    
    # Start application services first
    log_info "Starting application services for E2E testing..."
    docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up -d backend-app frontend-app
    
    # Wait for application services to be ready
    log_info "Waiting for application services to be ready..."
    sleep 30
    
    # Run E2E tests
    log_info "Starting E2E tests..."
    if [ "$FOLLOW_LOGS" = true ]; then
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit e2e-test
    else
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit --quiet-pull e2e-test
    fi
    
    log_info "E2E tests completed"
}

# Run performance tests
run_performance_tests() {
    log_test "Running containerized performance tests..."
    
    # Ensure application services are running
    log_info "Starting application services for performance testing..."
    docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up -d backend-app frontend-app
    
    # Wait for application services to be ready
    log_info "Waiting for application services to be ready..."
    sleep 45
    
    # Run performance tests
    log_info "Starting performance tests..."
    if [ "$FOLLOW_LOGS" = true ]; then
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit performance-test
    else
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit --quiet-pull performance-test
    fi
    
    log_info "Performance tests completed"
}

# Run security tests
run_security_tests() {
    log_test "Running containerized security tests..."
    
    if [ "$FOLLOW_LOGS" = true ]; then
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit security-test
    else
        docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit --quiet-pull security-test
    fi
    
    log_info "Security tests completed"
}

# Collect test reports
collect_reports() {
    log_step "Collecting test reports from containers..."
    
    # Run report collector
    docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" up --abort-on-container-exit test-reports
    
    # Show report location
    LATEST_REPORT=$(ls -t ./test-reports/ | head -n1)
    if [ -n "$LATEST_REPORT" ]; then
        log_info "Test reports collected in: ./test-reports/$LATEST_REPORT"
        log_info "Available reports:"
        ls -la "./test-reports/$LATEST_REPORT"
    else
        log_warn "No test reports found"
    fi
}

# Show container status
show_container_status() {
    log_step "Container Status:"
    docker-compose -f "$COMPOSE_FILE" -p "$PROJECT_NAME" ps
}

# Main execution
main() {
    # Parse arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --unit)
                RUN_UNIT=true
                shift
                ;;
            --integration)
                RUN_INTEGRATION=true
                shift
                ;;
            --e2e)
                RUN_E2E=true
                shift
                ;;
            --performance)
                RUN_PERFORMANCE=true
                shift
                ;;
            --security)
                RUN_SECURITY=true
                shift
                ;;
            --all)
                RUN_ALL=true
                shift
                ;;
            --no-cleanup)
                CLEANUP=false
                shift
                ;;
            --follow-logs)
                FOLLOW_LOGS=true
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
    
    # Set defaults if no specific tests selected
    if [ "$RUN_ALL" = false ] && [ "$RUN_UNIT" = false ] && [ "$RUN_INTEGRATION" = false ] && [ "$RUN_E2E" = false ] && [ "$RUN_PERFORMANCE" = false ] && [ "$RUN_SECURITY" = false ]; then
        log_info "No specific test type selected, running unit tests by default"
        RUN_UNIT=true
    fi
    
    # Set all flags if --all is specified
    if [ "$RUN_ALL" = true ]; then
        RUN_UNIT=true
        RUN_INTEGRATION=true
        RUN_E2E=true
        RUN_PERFORMANCE=true
        RUN_SECURITY=true
    fi
    
    log_info "Starting containerized test execution..."
    
    # Setup test environment
    setup_test_environment
    
    # Run selected tests
    if [ "$RUN_UNIT" = true ]; then
        run_unit_tests
    fi
    
    if [ "$RUN_INTEGRATION" = true ]; then
        run_integration_tests
    fi
    
    if [ "$RUN_E2E" = true ]; then
        run_e2e_tests
    fi
    
    if [ "$RUN_PERFORMANCE" = true ]; then
        run_performance_tests
    fi
    
    if [ "$RUN_SECURITY" = true ]; then
        run_security_tests
    fi
    
    # Collect all reports
    collect_reports
    
    # Show status
    show_container_status
    
    log_info "Containerized test execution completed!"
}

# Handle interruption
trap 'log_warn "Test execution interrupted"; cleanup; exit 1' INT TERM

# Ensure cleanup on exit
trap 'cleanup' EXIT

main "$@"