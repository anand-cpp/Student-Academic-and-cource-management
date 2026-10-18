# Student Academic and Course Management System

A comprehensive Student Academic and Course Management System built with Java Spring Boot (backend) and React TypeScript (frontend), demonstrating Object-Oriented Programming concepts, JDBC database connectivity, and modern web development practices.

## Project Overview

This system manages student academic records, course information, faculty details, enrollments, attendance, assessments, and grades. It demonstrates core OOP principles including encapsulation, inheritance, abstraction, polymorphism, and design patterns.

## Architecture

```
+------------------+      +------------------+      +------------------+
|  React + TS      |----->|  Spring Boot     |----->|  H2 / MySQL 8.0  |
|  Frontend        |      |  Backend API     |      |  Database        |
+------------------+      +------------------+      +------------------+
```

### Backend (Spring Boot 3.3.2 + Java 21)
- **RESTful API** with comprehensive endpoints
- **JDBC** with HikariCP connection pooling
- **Spring Security** with JWT authentication
- **Spring Validation** for request validation
- **SpringDoc OpenAPI** for API documentation

### Frontend (React 18 + TypeScript + Vite)
- **React Router** for navigation
- **TanStack Query** for server state management
- **React Hook Form + Zod** for form validation
- **Tailwind CSS v4** for styling
- **Heroicons** for icons

### Database (H2 for local dev / MySQL 8.0 for production)
- Normalized schema with proper relationships
- Foreign key constraints and indexes
- Audit logging support

## Quick Start

### Prerequisites
- Java 21+
- Node.js 18+
- Maven 3.9+
- MySQL 8.0+ (optional, for production)

### Backend Setup
```bash
cd backend
# Uses H2 in-memory database by default (local profile)
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

### Database Setup (Production)
```sql
CREATE DATABASE student_academic_db;
-- Run migrations from backend/src/main/resources/db/migration/
```

## Features

### Student Management
- CRUD operations for student records
- Student profiles with academic details
- Enrollment history and academic transcripts

### Faculty Management
- Faculty profiles with designations
- Course assignments and workload tracking

### Course Management
- Course catalog with prerequisites
- Semester-based scheduling
- Faculty assignment

### Enrollment System
- Student course enrollment
- Capacity management
- Waitlist support

### Attendance Tracking
- Session-based attendance recording
- Multiple status types (Present, Absent, Late, Excused, Medical)
- Attendance percentage calculation

### Assessment & Grading
- Multiple assessment types (Quiz, Midterm, Final, Assignment, Project)
- Configurable weighting
- Configurable grading policies
- Automatic grade calculation

### Reporting
- Student academic profiles
- Course enrollment reports
- Faculty course assignments
- Attendance reports
- Course results and grade distributions

## Security
- JWT-based authentication
- Role-based access control (Admin, Faculty, Student, Department Head, Registrar)
- Password hashing with BCrypt
- CORS configuration
- Input validation and sanitization

## Project Structure

```
student-academic-management/
+-- backend/
|   +-- src/main/java/com/studentmgmt/
|   |   +-- config/          # Configuration classes
|   |   +-- controller/      # REST controllers
|   |   +-- dto/             # Data Transfer Objects
|   |   +-- exception/       # Custom exceptions
|   |   +-- model/           # JPA entities
|   |   +-- repository/      # Data access layer
|   |   +-- security/        # Security configuration
|   |   +-- service/         # Business logic
|   |   +-- util/            # Utility classes
|   +-- src/main/resources/
|       +-- db/migration/    # SQL migrations
|       +-- application.yml  # Configuration
+-- frontend/
|   +-- src/
|   |   +-- components/      # Reusable UI components
|   |   +-- contexts/        # React contexts
|   |   +-- hooks/           # Custom hooks
|   |   +-- pages/           # Page components
|   |   +-- services/        # API services
|   |   +-- types/           # TypeScript types
|   +-- package.json
+-- docs/
    +-- ARCHITECTURE.md
    +-- DATABASE.md
    +-- API.md
    +-- OOP-CONCEPTS.md
    +-- DEMO-GUIDE.md
```

## Demo Accounts

| Username | Password | Role |
|----------|----------|------|
| admin | password123 | ADMIN |
| prof.smith | password123 | FACULTY |
| student001 | password123 | STUDENT |

## Testing
```bash
# Backend tests
cd backend && ./mvnw test

# Frontend tests
cd frontend && npm test
```

## Deployment
```bash
# Backend JAR
cd backend && ./mvnw package -DskipTests
java -jar target/student-academic-management-1.0.0-SNAPSHOT.jar

# Frontend build
cd frontend && npm run build
# Deploy dist/ folder to static hosting
```

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## License
This project is licensed under the MIT License.

## Acknowledgments
- Spring Boot team for the excellent framework
- React team for the frontend library
- Tailwind CSS for the utility-first CSS framework
- Heroicons for the beautiful icons