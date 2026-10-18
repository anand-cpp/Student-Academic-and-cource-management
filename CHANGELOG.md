# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Complete backend implementation with Spring Boot 3.3.2
- Complete frontend implementation with React 18 + TypeScript + Vite
- Comprehensive database schema with 16 tables
- JWT-based authentication with refresh tokens
- Role-based access control (ADMIN, FACULTY, STUDENT, DEPARTMENT_HEAD, REGISTRAR)
- Student management (CRUD, search, pagination)
- Faculty management (CRUD, course assignments)
- Course management (CRUD, enrollment capacity)
- Enrollment system (enroll, drop, complete, withdraw)
- Attendance tracking (sessions, records, bulk operations)
- Assessment system (multiple types, weighted grading)
- Marks/Grades system (bulk entry, finalization, grading policies)
- Reporting engine (academic profiles, enrollment reports, attendance reports, course results)
- Dashboard with real-time statistics
- JWT authentication with refresh tokens
- RESTful API with OpenAPI/Swagger documentation
- Responsive React frontend with Tailwind CSS
- Comprehensive test suite (unit, integration, E2E)

### Changed
- N/A (initial release)

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- Frontend white screen issue: Added missing Tailwind v4 animation keyframes (animate-spin, animate-slide-in)
- TypeScript errors in CoursesPage, StudentsPage, FacultyPage (duplicate variable declarations)
- Import path issues: Updated all page components to use path aliases (@/, @components/, @services/, @contexts/, @types/)
- Vite config: Fixed __dirname usage for ES module compatibility
- Tailwind v4 migration: Updated PostCSS config, package.json, and index.css syntax
- Backend H2 configuration: Added local profile with in-memory database for development
- JWT secret property name mismatch in JwtUtil

### Security
- JWT authentication with BCrypt password hashing
- Role-based access control
- Input validation and sanitization
- CORS configuration
- SQL injection prevention via prepared statements

## [1.0.0] - 2026-10-18

### Added
- Initial release of Student Academic and Course Management System
- Backend: Spring Boot 3.3.2, Java 21, Spring Security, Spring Data JPA, MySQL 8.0
- Frontend: React 18, TypeScript, Vite, Tailwind CSS, TanStack Query
- Database: MySQL 8.0 with 16 normalized tables
- Authentication: JWT with refresh tokens, BCrypt password hashing
- Authorization: Role-based access control (5 roles)
- Student Management: Full CRUD, search, pagination, status management
- Faculty Management: Full CRUD, designations, department assignments
- Course Management: Full CRUD, prerequisites, faculty assignment, capacity limits
- Enrollment System: Enroll/drop/withdraw/complete, capacity checks, duplicate prevention
- Attendance System: Session management, bulk recording, status types (PRESENT/ABSENT/LATE/EXCUSED/MEDICAL)
- Assessment System: 9 assessment types, weighted grading, publishing workflow
- Grading System: Configurable policies, automatic grade calculation, bulk entry
- Reporting: Academic profiles, enrollment reports, attendance reports, course results, performance summaries
- Dashboard: Real-time statistics, quick actions, recent activity
- API Documentation: Swagger/OpenAPI 3.0 at `/api/swagger-ui.html`
- Database: MySQL 8.0 with Flyway migrations, 16 tables, proper constraints
- Security: JWT authentication, BCrypt password hashing, RBAC, CORS, input validation
- Testing: Unit tests (JUnit 5, Mockito), Integration tests (Testcontainers), E2E tests (Playwright)
- CI/CD: GitHub Actions workflow for build, test, Docker build
- Documentation: Comprehensive docs (Architecture, Database, API, OOP Concepts, Testing, Demo Guide)

### Changed
- N/A

### Deprecated
- N/A

### Removed
- N/A

### Fixed
- Frontend white screen issue: Added missing Tailwind v4 animation keyframes (animate-spin, animate-slide-in)
- TypeScript errors in CoursesPage, StudentsPage, FacultyPage (duplicate variable declarations)
- Import path issues: Updated all page components to use path aliases (@/, @components/, @services/, @contexts/, @types/)
- Vite config: Fixed __dirname usage for ES module compatibility
- Tailwind v4 migration: Updated PostCSS config, package.json, and index.css syntax
- Backend H2 configuration: Added local profile with in-memory database for development
- JWT secret property name mismatch in JwtUtil

### Security
- JWT token authentication with 24h access / 7d refresh tokens
- BCrypt password hashing (cost factor 10)
- Role-based access control with 5 roles
- CORS configuration for frontend origin
- Input validation on all endpoints
- SQL injection prevention via parameterized queries
- Password strength requirements (min 8 chars)

## Release Notes Template

### Version X.Y.Z - YYYY-MM-DD

#### Added
- Feature descriptions

#### Changed
- Modified functionality

#### Deprecated
- Soon-to-be-removed features

#### Removed
- Deleted features

#### Fixed
- Bug fixes

#### Security
- Security improvements

---

## Migration Guide

### Upgrading to 1.0.0
This is the initial release. No migration needed.

### Future Upgrades
When upgrading between versions:
1. Backup database
2. Run Flyway migrations: `./mvnw flyway:migrate`
3. Update frontend: `npm install && npm run build`
3. Restart services

---

## Support

For questions about releases, please open an issue on GitHub or contact the maintainers.