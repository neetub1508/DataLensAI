# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
Data Lens AI is a modern analytics platform project in early planning stages. The repository currently contains Product Requirements Documents (PRD) defining the project specifications and roadmap.

## Project Architecture
Based on the PRDs, this will be a full-stack analytics platform with the following planned architecture:

### Technology Stack (Current Implementation)
- **Backend**: Java Spring Boot 3.x with Spring Security for enterprise-grade performance and security
- **Database**: PostgreSQL with Spring Data JPA and Hibernate ORM, Redis for caching
- **Frontend**: React with Next.js 14+ and TypeScript
- **UI Framework**: Tailwind CSS (inspired by Beginner React Challenges design)
- **Authentication**: JWT with Spring Security and OAuth2/OpenID Connect support
- **Message Queue**: Apache Kafka or NATS for event-driven architecture (planned)
- **Deployment**: Docker containers with docker-compose for development

### Database Schema (Planned)
The application will use a PostgreSQL-compatible database with these core tables:
- `users` - User accounts with UUID primary keys, email verification, locale preferences
- `roles` - RBAC role definitions
- `user_roles` - Many-to-many user-role relationships
- `permissions` - Granular permission definitions
- `role_permissions` - Role-permission mappings
- `app_version` - Version tracking for database migrations

### Key Features (Planned)
- User registration and authentication with email verification
- Role-based access control (RBAC) system
- Internationalization (i18n) support
- Dark/light theme switching
- Admin dashboard for user and role management
- Version management with separate SQL migration files
- Public pages: Home, Pricing, About Us, Blog, Sign In

## Development Status
Project is currently being implemented with Java Spring Boot backend and Next.js frontend. The backend has been migrated from Python FastAPI to Java Spring Boot for better enterprise features and performance.

## Current Repository Structure
```
/
├── PRD_Data_Lens_AI_v0.1.md  # Initial project requirements
├── PRD_Data_Lens_AI_v0.2.md  # Updated requirements (delta)
└── platform/                 # Implementation directory
    ├── backend/               # Java Spring Boot backend
    ├── frontend/              # Next.js frontend
    └── docker-compose.yml     # Development environment
```

## Planned Milestones
1. Project setup (repo structure, CI/CD, Docker)
2. Database schema implementation
3. User authentication system
4. RBAC implementation
5. Frontend scaffolding with theming
6. Admin dashboard
7. i18n integration
8. Security hardening

## Notes for Development
- Use UUIDs for all entity primary keys for scalability
- Follow PostgreSQL naming conventions for database objects
- Implement proper database migration versioning
- UI/UX should follow the design patterns from Beginner React Challenges
- Ensure WCAG 2.1 accessibility compliance
- All user-facing text must be translatable via i18n