# Local Development Guide

This guide covers setting up and running the Student Academic and Course Management System locally using the H2 in-memory database.

## Quick Start

### Prerequisites
- **Java 21+** (required for Spring Boot 3.3.2)
- **Node.js 18+** (required for Vite 8.x)
- **Maven 3.9+** (or use included wrapper `./mvnw`)
- **No MySQL required** - H2 in-memory database is used by default

### Backend (Spring Boot)

```bash
cd backend

# Run with H2 local profile (default)
./mvnw spring-boot:run

# Or explicitly set profile
SPRING_PROFILES_ACTIVE=local ./mvnw spring-boot:run
```

**Backend URLs:**
- API Base: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`
- Health Check: `http://localhost:8080/api/actuator/health`
- H2 Console: `http://localhost:8080/api/h2-console`

**H2 Console Connection:**
- JDBC URL: `jdbc:h2:mem:student_academic_db`
- Username: `sa`
- Password: (empty)

### Frontend (React + Vite)

```bash
cd frontend

# Install dependencies
npm install

# Start dev server
npm run dev
```

**Frontend URL:** `http://localhost:5173`

## Configuration

### Backend Profiles

| Profile | Database | Use Case |
|---------|----------|----------|
| `local` | H2 (in-memory) | Local development, testing |
| `dev` | MySQL 8.0 | Development with persistent DB |
| `test` | Testcontainers | Automated testing |

### Application Properties

**application.yml** (main config):
```yaml
spring:
  profiles:
    active: local  # Defaults to H2
```

**application-local.yml** (H2 config):
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:student_academic_db;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  sql:
    init:
      mode: always
      schema-locations: classpath:db/migration/schema-h2.sql
      data-locations: classpath:db/migration/data-h2.sql
  h2:
    console:
      enabled: true
      path: /h2-console
```

### Frontend Configuration

**vite.config.ts:**
```typescript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
}
```

**.env (optional):**
```env
VITE_API_URL=http://localhost:8080/api
```

## Database Schema

The H2 schema (`schema-h2.sql`) is compatible with H2 syntax:
- No `SET FOREIGN_KEY_CHECKS` (not supported)
- Uses `BIGINT NOT NULL AUTO_INCREMENT` instead of `BIGINT UNSIGNED NOT NULL AUTO_INCREMENT`
- Uses standard SQL types compatible with H2
- Circular FK between `faculty` and `departments` handled with `ALTER TABLE` after table creation

Seed data (`data-h2.sql`) includes:
- 5 Roles (ADMIN, FACULTY, STUDENT, DEPARTMENT_HEAD, REGISTRAR)
- 8 Users (admin, 4 faculty, 1 dept head, 1 registrar, 5 students)
- 4 Departments (CS, MATH, PHYS, ENG)
- 4 Faculty members
- 5 Students
- 8 Courses
- 15 Enrollments
- Sample attendance, assessments, marks
- Grading policies
- Notifications

## Running Tests

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## Building for Production

### Backend JAR
```bash
cd backend
./mvnw package -DskipTests
java -jar target/student-academic-management-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### Frontend Build
```bash
cd frontend
npm run build
# Output in dist/ folder
```

## IDE Setup

### IntelliJ IDEA
1. Import as Maven project
2. Set JDK to 21
3. Enable annotation processing for Lombok
4. Run `StudentAcademicManagementApplication`

### VS Code
1. Install Extension Pack for Java
2. Install Spring Boot Extension Pack
3. Open `backend` folder
4. Run `./mvnw spring-boot:run` in terminal

## Common Issues

### Port Already in Use
```bash
# Find process on port 8080
netstat -an | findstr 8080
# or
lsof -i :8080

# Kill process
kill -9 <PID>
```

### H2 Console Not Accessible
1. Ensure `spring.h2.console.enabled=true` in application-local.yml
2. Check path: `http://localhost:8080/api/h2-console`
3. Verify JDBC URL: `jdbc:h2:mem:student_academic_db`

### Frontend Build Fails
```bash
# Clear cache and reinstall
cd frontend
rm -rf node_modules package-lock.json
npm install
npm run build
```

### CORS Issues
- Backend configured with CORS for `http://localhost:5173`
- Check `app.cors.allowed-origins` in application.yml

## Debugging

### Backend Debugging
```bash
# Add debug agent
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

### Frontend Debugging
- Open Chrome DevTools (F12)
- React DevTools extension recommended
- Network tab for API calls

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | `local` | Spring profile |
| `DB_USERNAME` | `root` | MySQL username (dev profile) |
| `DB_PASSWORD` | `` | MySQL password (dev profile) |
| `JWT_SECRET` | auto-generated | JWT signing key |
| `VITE_API_URL` | `http://localhost:8080/api` | Frontend API URL |

## Useful Commands

```bash
# Backend
./mvnw clean compile          # Clean compile
./mvnw spring-boot:run        # Run dev server
./mvnw test                   # Run tests
./mvnw package                # Build JAR
./mvnw flyway:migrate         # Run migrations
./mvnw flyway:clean           # Clean database

# Frontend
npm install                   # Install deps
npm run dev                   # Dev server
npm run build                 # Production build
npm run lint                  # Lint check
npm run preview               # Preview build

# Database (H2)
# Access via H2 Console at http://localhost:8080/api/h2-console
```