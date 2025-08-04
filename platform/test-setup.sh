#!/bin/bash
set -e

echo "🧪 Testing Data Lens AI Setup..."

# Test if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "✅ Docker is running"

# Test if docker-compose is available
if ! command -v docker-compose &> /dev/null; then
    echo "❌ docker-compose not found. Please install docker-compose."
    exit 1
fi

echo "✅ docker-compose is available"

# Check if we're in the right directory
if [ ! -f "docker-compose.yml" ]; then
    echo "❌ docker-compose.yml not found. Please run this script from the platform directory."
    exit 1
fi

echo "✅ Found docker-compose.yml"

# Test building the backend
echo "🔨 Testing backend build..."
docker-compose build backend --no-cache

echo "✅ Backend built successfully"

# Start services
echo "🚀 Starting services..."
docker-compose up -d postgres redis

# Wait for postgres to be ready
echo "⏳ Waiting for PostgreSQL..."
timeout 60 bash -c 'until docker-compose exec postgres pg_isready -U postgres; do sleep 2; done'

echo "✅ PostgreSQL is ready"

# Test backend startup
echo "🌟 Starting backend..."
docker-compose up -d backend

# Wait for backend to be healthy
echo "⏳ Waiting for backend to be healthy..."
sleep 10

# Test API endpoints
echo "🔍 Testing API endpoints..."

# Test root endpoint
if curl -f http://localhost:8000/ > /dev/null 2>&1; then
    echo "✅ Root endpoint is working"
else
    echo "❌ Root endpoint failed"
    docker-compose logs backend
    exit 1
fi

# Test health endpoint
if curl -f http://localhost:8000/health > /dev/null 2>&1; then
    echo "✅ Health endpoint is working"
else
    echo "❌ Health endpoint failed"
    docker-compose logs backend
    exit 1
fi

# Test API docs
if curl -f http://localhost:8000/docs > /dev/null 2>&1; then
    echo "✅ API documentation is accessible"
else
    echo "❌ API documentation failed"
fi

echo "🎉 All tests passed! Data Lens AI is ready to use."
echo ""
echo "📋 Quick Start:"
echo "  - API: http://localhost:8000"
echo "  - Docs: http://localhost:8000/docs"
echo "  - Frontend: http://localhost:3000 (when ready)"
echo "  - Database: localhost:5433"
echo ""
echo "🔐 Default Admin Credentials:"
echo "  - Email: admin@datalens.ai"
echo "  - Password: admin123"
echo ""
echo "🛑 To stop services: docker-compose down"