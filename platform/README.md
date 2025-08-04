# Data Lens AI - Analytics Platform v0.1

A modern, full-stack analytics platform built with **Java Spring Boot**, **Next.js**, and **PostgreSQL**, featuring advanced authentication, role-based access control, and AI-driven insights.

## 🏗️ Architecture

### Backend (Java Spring Boot + PostgreSQL)
- **Framework**: Spring Boot 3.2+ with Java 17
- **Security**: Spring Security with JWT authentication
- **Database**: PostgreSQL with Spring Data JPA and Hibernate
- **Migrations**: Flyway for version-controlled database schema
- **Documentation**: OpenAPI 3.0 with Swagger UI
- **Testing**: JUnit 5, Spring Boot Test, TestContainers
- **Build**: Maven with multi-module structure

### Frontend (Next.js + TypeScript)
- **Framework**: Next.js 14+ with TypeScript 5+
- **Styling**: Tailwind CSS 3+ with dark/light theme
- **State Management**: Zustand
- **Form Handling**: React Hook Form with Zod validation
- **HTTP Client**: Axios with automatic token refresh
- **UI Components**: Headless UI + custom components

## 📋 Features Implemented

### ✅ Core Backend Features
- [x] **JPA Entities**: User, Role, Permission with proper relationships and auditing
- [x] **Spring Security**: JWT-based authentication with refresh tokens
- [x] **Repository Layer**: Spring Data JPA with custom queries
- [x] **Database Migrations**: Flyway with initial schema and seed data
- [x] **RBAC System**: Dynamic role and permission management
- [x] **API Documentation**: OpenAPI 3.0 with Swagger UI integration
- [x] **Validation**: Bean Validation (JSR-303) with custom validators
- [x] **Error Handling**: Global exception handling with proper HTTP status codes
- [x] **Auditing**: JPA Auditing for created/updated timestamps

### ✅ Core Frontend Features
- [x] **Project Setup**: Next.js with TypeScript and Tailwind
- [x] **Theme System**: Dark/light mode with system detection
- [x] **Authentication Store**: Zustand store with persistence
- [x] **API Client**: Axios with interceptors and error handling
- [x] **Public Layout**: Navigation, footer, responsive design
- [x] **Home Page**: Landing page with features showcase

### 🚧 In Progress
- [ ] Service layer implementation
- [ ] REST controllers for API endpoints
- [ ] Authentication UI components (login, register forms)
- [ ] Dashboard layout and components
- [ ] Admin panel for user/role management

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose
- Node.js 18+ (for frontend development)

### Using Docker (Recommended)

1. **Clone and navigate to platform directory**:
   ```bash
   cd platform/
   ```

2. **Start all services**:
   ```bash
   docker-compose up -d
   ```
   
   The backend will automatically:
   - Wait for PostgreSQL to be ready
   - Run Flyway database migrations
   - Seed initial data (if needed)
   - Start the Spring Boot application

3. **Access the application**:
   - Backend API: http://localhost:8000/api/v1
   - API Documentation: http://localhost:8000/api/v1/swagger-ui.html
   - Health Check: http://localhost:8000/api/v1/actuator/health
   - Frontend: http://localhost:3000
   - Database: localhost:5433

### Local Development

#### Backend Setup
```bash
cd backend/

# Ensure Java 17+ is installed
java -version

# Run with Maven (requires PostgreSQL running)
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/datalens-backend-0.1.0.jar

# Run tests
./mvnw test

# Generate API documentation
./mvnw clean compile
# Visit: http://localhost:8000/api/v1/swagger-ui.html
```

#### Frontend Setup
```bash
cd frontend/

# Install dependencies
npm install

# Start development server
npm run dev

# Build for production
npm run build
```

## 🔒 Default Credentials

After running the migrations, you can log in with:
- **Email**: admin@datalens.ai
- **Password**: admin123

⚠️ **Important**: Change the admin password after first login!

## 📚 API Documentation

### Authentication Endpoints
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/register` - User registration
- `POST /api/v1/auth/refresh` - Refresh access token
- `POST /api/v1/auth/logout` - User logout

### User Management
- `GET /api/v1/users/me` - Get current user
- `PUT /api/v1/users/me` - Update current user
- `GET /api/v1/users` - Get all users (admin)
- `GET /api/v1/users/{id}` - Get user by ID (admin)
- `PUT /api/v1/users/{id}` - Update user (admin)
- `DELETE /api/v1/users/{id}` - Delete user (admin)

### Role Management
- `GET /api/v1/roles` - Get all roles
- `POST /api/v1/roles` - Create role (admin)
- `PUT /api/v1/roles/{id}` - Update role (admin)
- `DELETE /api/v1/roles/{id}` - Delete role (admin)

### Permission Management
- `GET /api/v1/permissions` - Get all permissions (admin)
- `POST /api/v1/permissions` - Create permission (admin)
- `DELETE /api/v1/permissions/{id}` - Delete permission (admin)

## 🗃️ Database Schema

### Core Tables
- **users**: User accounts with UUID primary keys, email verification, locale preferences
- **roles**: RBAC role definitions
- **permissions**: Granular permission definitions
- **user_roles**: Many-to-many user-role relationships
- **role_permissions**: Many-to-many role-permission mappings
- **app_version**: Version tracking for database migrations

### Default Roles & Permissions
- **Admin Role**: Full access to all resources
- **User Role**: Basic user permissions
- **Permissions**: user:*, role:*, permission:*, admin:access

## 🔧 Configuration

### Backend Environment Variables
```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/datalens_ai
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=password

# Security
SPRING_SECURITY_JWT_SECRET_KEY=your-super-secret-key
SPRING_SECURITY_JWT_EXPIRATION=86400000

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# CORS
APP_CORS_ALLOWED_ORIGINS=http://localhost:3000
```

### Frontend Environment Variables
```bash
NEXT_PUBLIC_API_URL=http://localhost:8000/api/v1
```

## 🧪 Testing

### Backend Tests
```bash
cd backend/
./mvnw test

# Run with coverage
./mvnw test jacoco:report

# Integration tests with TestContainers
./mvnw verify
```

### Frontend Tests
```bash
cd frontend/
npm test
npm run test:coverage
```

## 🔧 Troubleshooting

### Common Issues

**Backend container not starting:**
```bash
# Check logs
docker-compose logs backend

# Rebuild backend
docker-compose build backend --no-cache
```

**Database connection issues:**
```bash
# Check PostgreSQL status
docker-compose logs postgres

# Verify port 5433 is available
netstat -an | grep 5433
```

**Maven build issues:**
```bash
# Clean and rebuild
./mvnw clean compile

# Update dependencies
./mvnw dependency:resolve
```

**Port conflicts:**
- PostgreSQL: Change `5433:5432` in docker-compose.yml
- Backend API: Change `8000:8000` in docker-compose.yml
- Frontend: Change `3000:3000` in docker-compose.yml

## 📁 Project Structure

```
platform/
├── backend/                 # Java Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/ai/datalens/
│   │   │   │   ├── entity/        # JPA entities
│   │   │   │   ├── repository/    # Spring Data repositories
│   │   │   │   ├── service/       # Business logic
│   │   │   │   ├── controller/    # REST controllers
│   │   │   │   ├── dto/           # Request/Response DTOs
│   │   │   │   ├── security/      # Security configuration
│   │   │   │   └── config/        # Application configuration
│   │   │   └── resources/
│   │   │       ├── db/migration/  # Flyway migrations
│   │   │       └── application.yml
│   │   └── test/              # Test suite
│   ├── pom.xml               # Maven configuration
│   └── Dockerfile            # Docker configuration
├── frontend/                 # Next.js frontend
│   ├── src/
│   │   ├── app/             # Next.js app directory
│   │   ├── components/      # React components
│   │   ├── lib/            # Utilities and API client
│   │   ├── store/          # Zustand stores
│   │   └── types/          # TypeScript types
│   └── package.json        # NPM configuration
└── docker-compose.yml       # Development environment
```

## 🚀 Deployment

### Production Environment
1. Update environment variables for production
2. Use production database
3. Configure HTTPS and domain
4. Set up monitoring and logging
5. Configure backup strategies

### Docker Production
```bash
# Build production images
docker-compose -f docker-compose.prod.yml build

# Deploy
docker-compose -f docker-compose.prod.yml up -d
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Run tests and ensure they pass
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For support, email support@datalens.ai or create an issue in the repository.

---

**Data Lens AI** - Transform your data into actionable insights with enterprise-grade Java Spring Boot! 🚀☕