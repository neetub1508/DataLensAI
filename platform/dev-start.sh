#!/bin/bash
set -e

echo "🚀 Starting Data Lens AI Development Environment..."

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

# Stop any existing services
echo "🛑 Stopping existing services..."
docker-compose down

# Start services
echo "🐘 Starting PostgreSQL and Redis..."
docker-compose up -d postgres redis

# Wait for postgres
echo "⏳ Waiting for PostgreSQL to be ready..."
timeout 60 bash -c 'until docker-compose exec postgres pg_isready -U postgres; do sleep 2; done'

# Start backend
echo "⚡ Starting backend..."
docker-compose up -d backend

# Wait a moment for backend to start
sleep 5

# Show status
echo "📊 Service Status:"
docker-compose ps

echo ""
echo "✅ Development environment is ready!"
echo ""
echo "📋 Available Services:"
echo "  🔗 Backend API: http://localhost:8000"
echo "  📚 API Docs: http://localhost:8000/docs"
echo "  🌐 Frontend: http://localhost:3000 (when ready)"
echo "  🗄️  Database: localhost:5433"
echo ""
echo "🔐 Default Admin Login:"
echo "  📧 Email: admin@datalens.ai"
echo "  🔑 Password: admin123"
echo ""
echo "📝 Useful Commands:"
echo "  docker-compose logs backend    # View backend logs"
echo "  docker-compose logs postgres   # View database logs"
echo "  docker-compose down           # Stop all services"
echo "  docker-compose up -d          # Start all services"
echo ""
echo "🔄 To see live logs: docker-compose logs -f backend"