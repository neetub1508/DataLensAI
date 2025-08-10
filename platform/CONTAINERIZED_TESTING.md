# 🐳 Containerized Testing Framework

This document describes the fully containerized testing framework for Data Lens AI platform where **ALL tests run inside Docker containers** with no local dependencies.

## 🎯 Overview

The containerized testing framework ensures:
- **Zero Local Dependencies**: No need for Java, Node.js, Maven, or NPM on host machine
- **Consistent Environment**: Same test environment across all machines and CI/CD
- **Isolated Testing**: Each test type runs in its own container
- **Complete Coverage**: Unit, integration, E2E, performance, and security tests
- **Easy Cleanup**: All test artifacts are contained and easily removable

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Test Runner   │    │  Infrastructure │    │   Test Types    │
│                 │    │                 │    │                 │
│ • Orchestrates  │    │ • PostgreSQL    │    │ • Unit Tests    │
│ • Coordinates   │────│ • Redis         │────│ • Integration   │
│ • Collects      │    │ • Networking    │    │ • E2E Tests     │
│ • Reports       │    │                 │    │ • Performance   │
│                 │    │                 │    │ • Security      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
          │                                             │
          └─────────────────────────────────────────────┘
                          Docker Compose
```

## 🚀 Quick Start

### Prerequisites
- Docker and Docker Compose installed
- No other dependencies required!

### Basic Usage
```bash
# Run all unit tests in containers
./test-runner-containerized.sh --unit

# Run all test types
./test-runner-containerized.sh --all

# Run with live logs
./test-runner-containerized.sh --unit --follow-logs

# Keep containers running for debugging
./test-runner-containerized.sh --integration --no-cleanup
```

### Integration with Platform
```bash
# Test then start services
./start.sh --test

# Test only (containerized)
./start.sh --test-only
```

## 📦 Container Services

### Infrastructure Services
- **postgres-test**: Test database with isolated schema
- **redis-test**: Test cache with separate configuration  

### Test Containers
- **backend-test**: Java/Maven unit tests
- **frontend-test**: Node.js/Jest unit tests
- **integration-test**: API and database integration tests
- **e2e-test**: Playwright browser tests
- **performance-test**: K6 load testing
- **security-test**: OWASP dependency scanning

### Application Services (for E2E)
- **backend-app**: Running backend for E2E testing
- **frontend-app**: Running frontend for E2E testing

### Utility Services
- **test-reports**: Collects all test artifacts

## 🛠️ Container Configurations

### Backend Test Container
```dockerfile
FROM eclipse-temurin:17-jdk-jammy
# Optimized for Maven testing with dependency caching
# Includes curl, git for CI/CD integration
# Java 17 for compatibility with testing frameworks
```

### Frontend Test Container  
```dockerfile
FROM node:18-alpine
# Includes Chromium for headless testing
# Optimized package.json caching
# Alpine base for small footprint
```

### E2E Test Container
```dockerfile  
FROM mcr.microsoft.com/playwright:v1.40.1-jammy
# Pre-installed browsers (Chrome, Firefox, Safari)
# All Playwright dependencies included
# Jammy base for stability
```

### Performance Test Container
```dockerfile
FROM grafana/k6:latest
# Official K6 container
# Built-in JavaScript runtime
# JSON output support
```

## 📊 Test Execution Flow

### 1. Environment Setup
```bash
# Infrastructure starts first
postgres-test + redis-test
     ↓ (health checks)
# Test containers start when dependencies ready
```

### 2. Test Execution
```bash
# Each test type runs independently
Unit Tests → Integration Tests → E2E Tests
     ↓              ↓              ↓
   Reports    →   Reports    →   Reports
                     ↓
              Consolidated Report
```

### 3. Report Collection
```bash
# All test artifacts collected into shared volumes
backend_test_reports:/app/target
frontend_test_reports:/app/coverage
e2e_test_reports:/app/playwright-report
performance_test_reports:/results
security_test_reports:/reports
     ↓
# Consolidated into timestamped directory
./test-reports/YYYYMMDD_HHMMSS/
```

## 🎛️ Available Commands

### Test Runner Options
```bash
# Test Types
--unit              Backend & frontend unit tests
--integration       Database & API integration tests  
--e2e              End-to-end browser tests
--performance      Load testing with K6
--security         OWASP dependency scanning
--all              All test types

# Execution Options  
--follow-logs      Stream container logs during execution
--no-cleanup       Keep containers running after tests
--help             Show detailed usage information
```

### Examples
```bash
# Quick unit testing
./test-runner-containerized.sh --unit

# Full test suite with logs
./test-runner-containerized.sh --all --follow-logs

# Debug integration issues
./test-runner-containerized.sh --integration --no-cleanup
# Then: docker logs datalens-integration-test

# Performance testing only
./test-runner-containerized.sh --performance
```

## 📁 Volume Management

### Persistent Volumes
- **maven_cache**: Maven dependencies (.m2 directory)
- **frontend_node_modules**: NPM packages for faster rebuilds
- **postgres_test_data**: Database data persistence

### Report Volumes  
- **backend_test_reports**: JUnit XML, Surefire reports
- **frontend_test_reports**: Jest coverage, test results
- **integration_test_reports**: Integration test outputs
- **e2e_test_reports**: Playwright HTML reports  
- **e2e_test_results**: Screenshots, videos, traces
- **performance_test_reports**: K6 JSON results
- **security_test_reports**: OWASP scan results

## 🔧 Configuration

### Environment Variables
```yaml
# Backend Test Environment
SPRING_PROFILES_ACTIVE: test
DATABASE_URL: jdbc:postgresql://postgres-test:5432/datalens_ai_test
DATABASE_USERNAME: postgres
DATABASE_PASSWORD: testpassword
REDIS_HOST: redis-test
REDIS_PASSWORD: testpassword

# Frontend Test Environment  
NODE_ENV: test
CI: true
BASE_URL: http://frontend-app:3000
API_URL: http://backend-app:8000

# Performance Test Environment
API_URL: http://backend-app:8000
```

### Network Configuration
```yaml
networks:
  test-network:
    driver: bridge
    # All containers communicate via this isolated network
    # No external dependencies or conflicts
```

## 🐛 Debugging

### Container Status
```bash
# Check all container status
docker-compose -f docker-compose.test.yml -p datalens-test ps

# View specific container logs
docker logs datalens-backend-test
docker logs datalens-frontend-test
docker logs datalens-e2e-test
```

### Interactive Debugging
```bash
# Run tests with no cleanup
./test-runner-containerized.sh --unit --no-cleanup

# Execute commands inside test container
docker exec -it datalens-backend-test bash
docker exec -it datalens-frontend-test sh

# View live logs
docker logs -f datalens-backend-test
```

### Common Issues
```bash
# Port conflicts
# Solution: Tests use different ports (5434, 6380, 8001, 3001)

# Container build failures  
# Solution: Check Dockerfile.test and build logs

# Test failures
# Solution: Check test reports in ./test-reports/
```

## 🔄 CI/CD Integration

### GitHub Actions
```yaml
- name: Run Containerized Tests
  run: |
    cd platform
    ./test-runner-containerized.sh --all
    
- name: Upload Test Reports
  uses: actions/upload-artifact@v3
  with:
    name: test-reports
    path: platform/test-reports/
```

### Local CI Simulation
```bash
# Run full CI pipeline locally
./test-runner-containerized.sh --all --follow-logs

# Parallel test execution (faster)
./test-runner-containerized.sh --unit &
./test-runner-containerized.sh --integration &
wait
```

## 📈 Performance Benefits

### Container Advantages
- **Parallel Execution**: Multiple test types run simultaneously
- **Resource Isolation**: Each test gets dedicated resources
- **Dependency Caching**: Maven and NPM caches persist
- **Quick Startup**: Pre-built images with dependencies

### Benchmark Results
```bash
# Typical execution times (local machine)
Unit Tests:      ~2-3 minutes
Integration:     ~3-4 minutes  
E2E Tests:       ~5-7 minutes
Performance:     ~3-5 minutes
Security Scan:   ~2-3 minutes

# Total (parallel): ~7-10 minutes
# Total (serial):  ~15-22 minutes
```

## 🧹 Cleanup & Maintenance

### Manual Cleanup
```bash
# Remove all test containers and volumes
docker-compose -f docker-compose.test.yml -p datalens-test down --volumes --remove-orphans

# Remove test images
docker image prune -f
docker rmi $(docker images -q "*test*")

# Clean test reports
rm -rf ./test-reports/*
```

### Automated Cleanup
```bash
# Built-in cleanup (default)
./test-runner-containerized.sh --unit
# Automatically cleans up containers

# Keep containers for debugging
./test-runner-containerized.sh --unit --no-cleanup
# Manual cleanup required
```

## 📋 Best Practices

### Development Workflow
1. **Make code changes**
2. **Run relevant tests**: `./test-runner-containerized.sh --unit`
3. **Check reports**: `./test-reports/latest/`
4. **Debug if needed**: `--no-cleanup` + `docker logs`
5. **Commit when tests pass**

### CI/CD Integration
1. **Use containerized tests in CI**: Consistent with local
2. **Parallel execution**: Faster pipeline execution  
3. **Artifact collection**: Always upload test reports
4. **Proper cleanup**: Prevent resource leaks

### Performance Optimization
1. **Layer caching**: Optimize Dockerfile layers
2. **Volume persistence**: Cache dependencies
3. **Parallel execution**: Run independent tests together
4. **Resource limits**: Set appropriate container limits

## 🎉 Benefits Summary

✅ **Zero Local Dependencies**: Only Docker required  
✅ **Consistent Environment**: Same results everywhere  
✅ **Complete Isolation**: No conflicts with host system  
✅ **Easy Debugging**: Container logs and interactive access  
✅ **CI/CD Ready**: Perfect for automated pipelines  
✅ **Scalable**: Easy to add new test types  
✅ **Maintainable**: Clear separation of concerns  
✅ **Fast**: Optimized containers with caching  

The containerized testing framework provides a robust, scalable, and maintainable solution for comprehensive testing without any local dependencies!