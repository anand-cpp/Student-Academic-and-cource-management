# Demo Guide

## Quick Demo Setup

### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8.0+
- Maven 3.9+

### 1. Database Setup
```bash
# Create database
mysql -u root -p -e "CREATE DATABASE student_academic_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Run migrations (automatic on backend startup)
# Or manually:
mysql -u root -p student_academic_db < backend/src/main/resources/db/migration/schema.sql
mysql -u root -p student_academic_db < backend/src/main/resources/db/migration/data.sql
```

### 2. Backend Startup
```bash
cd backend
# Set environment variables
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your-secret-key-min-32-chars

# Run
./mvnw spring-boot:run
```
Backend runs on: `http://localhost:8080/api`

### 3. Frontend Setup
```bash
cd frontend
# Create .env file
echo "VITE_API_URL=http://localhost:8080/api" > .env

# Install and run
npm install
npm run dev
```
Frontend runs on: `http://localhost:5173`

---

## Demo Accounts (Seeded Data)

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| **Admin** | `admin` | `password123` | Full system access |
| **Faculty** | `prof.smith` | `password123` | Professor John Smith |
| **Faculty** | `dr.jones` | `password123` | Dr. Emily Jones |
| **Student** | `student001` | `password123` | Alice Johnson (CS) |
| **Student** | `student002` | `password123` | Bob Williams (CS) |
| **Student** | `student003` | `password123` | Carol Davis (CS) |

---

## Demo Scenarios

### Scenario 1: Admin Dashboard Overview
1. Login as `admin` / `password123`
2. Observe dashboard with:
   - Total Students: 10
   - Total Faculty: 6
   - Total Courses: 10
   - Active Enrollments: 20
   - Average Attendance: 87.5%
   - Average Performance: 78.2%
3. Click "Add Student" → Create new student
4. Navigate to Students → Search/Filter students

### Scenario 2: Faculty Course Management
1. Login as `prof.smith` / `password123`
2. View assigned courses (CS101, CS102)
3. Navigate to Courses → View all courses
3. Create new course: CS303 "Advanced Algorithms"
4. View course details and enrolled students

### Scenario 3: Student Enrollment
1. Login as `student001` / `password123`
2. View dashboard with enrolled courses
3. Navigate to Enrollments → View enrolled courses
4. Enroll in available course (if available)

### Scenario 4: Attendance Recording
1. Login as `prof.smith` / `password123`
2. Navigate to Attendance → Select course (CS201)
3. Create new session: Date, Session Number, Topic
3. Record attendance for all enrolled students
4. View attendance summary reports

### Scenario 5: Assessment & Grading
1. Login as `prof.smith` / `password123`
2. Navigate to Assessments → Create new assessment
   - Title: "Midterm Exam"
   - Type: MIDTERM
   - Max Marks: 100
   - Weightage: 30%
   - Date: Next Friday
2. Navigate to Marks → Enter grades for all students
3. View Course Results report

### Scenario 6: Student Academic Profile
1. Login as `student001` / `password123`
2. View dashboard with:
   - Enrolled courses
   - Attendance percentage
   - Current grades
2. Navigate to Profile → View academic transcript
3. View attendance history per course

### Scenario 7: Reporting
1. Login as `admin` / `password123`
2. Navigate to Reports
3. Generate:
   - Student Academic Profile (select student)
   - Course Enrollment Report (select course)
   - Faculty Course Assignment (select faculty)
   - Attendance Report (select course)
   - Course Results Report (select course)
   - Student Performance Summary (select student)

### Scenario 8: Admin User Management
1. Login as `admin` / `password123`
2. Navigate to Settings → User Management
2. Create new faculty account
3. Create new student account
3. Assign roles and departments
4. Deactivate/activate users

---

## API Demo (Postman/curl)

### Authentication
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"usernameOrEmail": "admin", "password": "password123"}'

# Use token in subsequent requests
export TOKEN="eyJhbGciOiJIUzI1NiIs..."

# Get current user
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/auth/me
```

### Student Operations
```bash
# List students
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/students?page=0&size=10"

# Create student
curl -X POST http://localhost:8080/api/students \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "STU2024001",
    "username": "newstudent",
    "email": "new@university.edu",
    "password": "password123",
    "fullName": "New Student",
    "departmentId": 1,
    "admissionYear": 2024
  }'

# Get student detail
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/students/1
```

### Course Operations
```bash
# List courses
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/courses?page=0&size=20"

# Create course
curl -X POST http://localhost:8080/api/courses \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS303",
    "title": "Machine Learning",
    "credits": 4,
    "departmentId": 1,
    "semester": 5,
    "facultyId": 1,
    "maxEnrollment": 35,
    "academicYear": "2024-2025"
  }'
```

### Enrollment
```bash
# Enroll student
curl -X POST http://localhost:8080/api/enrollments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"studentId": 1, "courseId": 3}'
```

### Attendance
```bash
# Create session
curl -X POST http://localhost:8080/api/attendance/sessions \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"courseId": 3, "sessionDate": "2024-01-22", "sessionNumber": 1, "topic": "Introduction", "createdById": 2}'

# Bulk record attendance
curl -X POST http://localhost:8080/api/attendance/records/bulk \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": 1,
    "recordedById": 2,
    "records": [
      {"enrollmentId": 1, "status": "PRESENT"},
      {"enrollmentId": 2, "status": "ABSENT"}
    ]
  }'
```

### Assessments & Marks
```bash
# Create assessment
curl -X POST http://localhost:8080/api/assessments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 3,
    "title": "Midterm Exam",
    "type": "MIDTERM",
    "maxMarks": 100,
    "weightage": 30,
    "assessmentDate": "2024-02-15",
    "createdById": 2
  }'

# Bulk enter marks
curl -X POST http://localhost:8080/api/marks/bulk \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "assessmentId": 1,
    "gradedById": 2,
    "records": [
      {"enrollmentId": 1, "obtainedMarks": 85.5, "isFinalized": true},
      {"enrollmentId": 2, "obtainedMarks": 72.0, "isFinalized": true}
    ]
  }'
```

### Reports
```bash
# Student academic profile
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/reports/student-academic-profile/1

# Course enrollment report
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/reports/course-enrollment/3

# Course results
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/reports/course-results/3
```

---

## Swagger UI
Open: `http://localhost:8080/api/swagger-ui.html`

---

## Troubleshooting

### Backend Won't Start
1. Check Java version: `java -version` (needs 21+)
2. Check MySQL running: `systemctl status mysql`
3. Check database credentials in `application.yml`
4. Check port 8080 available: `netstat -an | grep 8080`

### Frontend Build Issues
```bash
# Clear cache
cd frontend && rm -rf node_modules package-lock.json && npm install

# Check Node version
node --version  # Needs 18+
```

### Database Issues
```bash
# Reset database
mysql -u root -p -e "DROP DATABASE student_academic_db; CREATE DATABASE student_academic_db;"

# Re-run migrations (auto on backend restart)
```

### Port Conflicts
```bash
# Change backend port
# backend/src/main/resources/application.yml
server:
  port: 8081

# Change frontend port
# frontend/vite.config.ts
server: { port: 3000 }
```

---

## Demo Tips

1. **Prepare Data**: Run the seed script for consistent demo data
2. **Browser**: Use Chrome/Edge for best experience
3. **Network**: Use localhost, no VPN/proxy issues
4. **Console**: Keep DevTools open for debugging
5. **Multiple Tabs**: Open different user roles in separate tabs

### Demo Flow (15 minutes)
1. **Login** (1 min) - Admin login
2. **Dashboard** (2 min) - Overview stats
3. **Students** (3 min) - CRUD, search, filter
4. **Courses** (3 min) - Create, assign faculty
5. **Enrollment** (2 min) - Student enrollment
5. **Attendance** (2 min) - Record & report
6. **Grades** (2 min) - Enter marks, view results
7. **Reports** (2 min) - Generate reports

---

## Reset for Next Demo
```bash
# Quick reset
cd backend && ./mvnw flyway:clean flyway:migrate
cd ../frontend && npm run dev
```