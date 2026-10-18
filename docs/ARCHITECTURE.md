# Architecture Documentation

## System Overview

The Student Academic and Course Management System follows a **three-tier architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                        │
│  React 18 + TypeScript + Vite + Tailwind CSS                    │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTPS/REST API
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       Application Layer                          │
│  Spring Boot 3.3.2 + Java 21 + Spring Security + JWT           │
└─────────────────────────────┬───────────────────────────────────┘
                              │ JDBC / HikariCP
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        Data Layer                                │
│  MySQL 8.0 + HikariCP + Flyway Migrations                       │
└─────────────────────────────────────────────────────────────────┘
```

## Backend Architecture

### Package Structure
```
com.studentmgmt
├── config/           # Spring configuration (Security, Web, OpenAPI)
├── controller/       # REST Controllers (thin, delegate to services)
├── dto/              # Data Transfer Objects (API contracts)
├── exception/        # Custom exceptions & global handler
├── model/            # Domain entities (JPA-ready, used with JDBC)
├── repository/       # Data access interfaces
├── repository.jdbc/  # JDBC implementations
├── service/          # Business logic & transactions
├── security/         # JWT, Authentication, Authorization
├── validation/       # Custom validators
└── util/             # Utility classes (DTOMapper, etc.)
```

### Design Principles

#### 1. Separation of Concerns
- **Controllers**: Handle HTTP concerns only (request/response, status codes)
- **Services**: Contain all business logic, transactions, validation
- **Repositories**: Encapsulate data access, SQL queries
- **DTOs**: Define API contracts, separate from domain models

#### 2. Layered Architecture
```
Controller → Service → Repository → Database
     ▲           │           │
     │           ▼           ▼
     │        DTOs        Entities
     │           │           │
     └───────────┴───────────┘
            DTOMapper
```

#### 3. Domain-Driven Design Elements
- **Entities**: Rich domain models with behavior
- **Value Objects**: Immutable objects (Address, Grade, etc.)
- **Aggregates**: Student (root), Enrollment, Course
- **Domain Events**: For audit logging (future)

### Key Design Decisions

#### JDBC over JPA/Hibernate
**Rationale**: Explicit SQL visibility for learning objectives
- Explicit `PreparedStatement` usage
- Manual transaction management
- ResultSet mapping via `DTOMapper`
- Connection pooling via HikariCP

#### DTO Pattern
- **Request DTOs**: Validation annotations, immutable
- **Response DTOs**: Flat structure, computed fields
- **Mapper**: Centralized `DTOMapper` for entity↔DTO conversion

#### Security Architecture
- **Stateless JWT**: No server-side sessions
- **Role-Based Access**: `@PreAuthorize` on controllers
- **Password Hashing**: BCrypt via Spring Security
- **Token Refresh**: Separate refresh token endpoint

## Frontend Architecture

### Technology Stack
- **React 18** with TypeScript
- **Vite** for fast development/build
- **React Router v6** for navigation
- **TanStack Query** for server state
- **React Hook Form + Zod** for forms
- **Tailwind CSS** for styling

### Component Architecture
```
src/
├── components/
│   ├── ui/              # Atomic components (Button, Input, Table, etc.)
│   └── layout/          # Layout components (Sidebar, Header, Layout)
├── pages/               # Page-level components (route targets)
├── contexts/            # React Context providers (Auth, Toast)
├── hooks/               # Custom hooks (useApi, useDebounce, etc.)
├── services/            # API service modules
├── types/               # TypeScript interfaces
└── hooks/               # Custom React hooks
```

### State Management Strategy
- **Server State**: TanStack Query (caching, deduplication, invalidation)
- **Client State**: React Context (Auth, Toast)
- **Form State**: React Hook Form + Zod validation
- **URL State**: React Router search params

### API Integration
- **Axios Instance**: Base URL, interceptors for auth/refresh
- **Service Modules**: One per domain (students, courses, etc.)
- **Error Handling**: Global error boundary + toast notifications

## Database Design

### Core Tables
| Table | Purpose | Key Relationships |
|-------|---------|-------------------|
| `users` | Authentication & roles | 1:1 with student/faculty |
| `roles` | Role definitions | Referenced by users |
| `departments` | Academic departments | 1:N with students, faculty, courses |
| `students` | Student profiles | 1:1 user, N:1 department, 1:N enrollment |
| `faculty` | Faculty profiles | 1:1 user, N:1 department, 1:N course |
| `courses` | Course catalog | N:1 department, N:1 faculty |
| `enrollments` | Student-course links | N:1 student, N:1 course |
| `attendance_sessions` | Class sessions | N:1 course |
| `attendance_records` | Attendance per student | N:1 session, N:1 enrollment |
| `assessments` | Course assessments | N:1 course |
| `marks` | Student grades | N:1 assessment, N:1 enrollment |

### Key Constraints
- **Primary Keys**: Auto-increment BIGINT
- **Foreign Keys**: Explicit FK constraints with CASCADE/RESTRICT
- **Unique Constraints**: Student-course enrollment, session-enrollment attendance
- **Check Constraints**: Marks ≥ 0, weightage 0-100
- **Indexes**: On foreign keys, search columns, status columns

## API Design

### REST Conventions
| Operation | Endpoint | Success Code |
|-----------|----------|--------------|
| List | `GET /api/{resource}` | 200 |
| Get | `GET /api/{resource}/{id}` | 200 |
| Create | `POST /api/{resource}` | 201 |
| Update | `PUT /api/{resource}/{id}` | 200 |
| Delete | `DELETE /api/{resource}/{id}` | 204 |

### Response Format
```json
// Success
{ "data": {...}, "meta": {...} }

// Error
{ "timestamp": "...", "status": 400, "error": "Bad Request", "message": "...", "path": "/api/..." }
```

### Pagination
```
GET /api/students?page=0&size=20&search=john&status=ACTIVE
Response: { content: [...], page: 0, size: 20, totalElements: 150, totalPages: 8 }
```

## Security Implementation

### Authentication Flow
```
1. POST /api/auth/login {username, password}
   → 200 { accessToken, refreshToken, user }

2. Subsequent requests: Authorization: Bearer <accessToken>

3. Token expiry → POST /api/auth/refresh {refreshToken}
   → 200 { accessToken, refreshToken }
```

### Authorization Matrix
| Resource | ADMIN | FACULTY | STUDENT | DEPT_HEAD | REGISTRAR |
|----------|-------|---------|---------|-----------|-----------|
| Students CRUD | ✓ | R | - | R | ✓ |
| Faculty CRUD | ✓ | - | - | R | ✓ |
| Courses CRUD | ✓ | C/R/U | R | R/U | ✓ |
| Enrollments | ✓ | R | C/R | R | ✓ |
| Attendance | ✓ | C/R/U | R | R | ✓ |
| Marks | ✓ | C/R/U | R | R | ✓ |
| Reports | ✓ | R | R | R | ✓ |

## Deployment Architecture

### Development
```
Frontend (Vite Dev Server) → :5173
                    │
                    ▼ Proxy /api
Backend (Spring Boot) → :8080
                    │
                    ▼
              MySQL Database → :3306
```

### Production
```
Nginx/CloudFront → Frontend (Static Files)
                    │
                    ▼
              Load Balancer
                    │
                    ▼
              Spring Boot Instances (K8s/ECS)
                    │
                    ▼
              MySQL Cluster / RDS
```

## Monitoring & Observability

### Actuator Endpoints
- `/actuator/health` - Health checks
- `/actuator/metrics` - Micrometer metrics
- `/actuator/prometheus` - Prometheus scrape

### Logging
- Structured JSON logging
- Correlation IDs for request tracing
- Audit logging for sensitive operations

## Scalability Considerations

### Horizontal Scaling
- Stateless backend (JWT, no sessions)
- Database read replicas for reads
- Redis for caching (future)
- CDN for frontend assets

### Database Optimization
- Connection pooling (HikariCP)
- Query optimization with indexes
- Pagination for large datasets
- Read replicas for reporting

## Future Extensions

1. **Real-time**: WebSocket for live attendance/grades
2. **Notifications**: Email/SMS for grade alerts
3. **Mobile App**: React Native shared codebase
4. **Analytics**: BI dashboards with Metabase/Superset
5. **Microservices**: Split by domain (future)