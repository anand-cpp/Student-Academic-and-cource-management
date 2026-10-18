# API Documentation

## Base Information

- **Base URL**: `http://localhost:8080/api`
- **Content Type**: `application/json`
- **Authentication**: Bearer Token (JWT)
- **API Version**: v1

## Authentication

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "admin",
  "password": "password123"
}
```

**Response** (200):
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@university.edu",
    "fullName": "System Administrator",
    "phone": "+1-555-0100",
    "avatarUrl": null,
    "role": "ADMIN",
    "studentId": null,
    "facultyId": null
  }
}
```

### Refresh Token
```http
POST /api/auth/refresh
Authorization: Bearer <refresh_token>
```

### Get Current User
```http
GET /api/auth/me
Authorization: Bearer <access_token>
```

### Logout
```http
POST /api/auth/logout
Authorization: Bearer <access_token>
```

---

## Students API

### List Students
```http
GET /api/students?page=0&size=20&search=john&status=ACTIVE&departmentId=1
Authorization: Bearer <token>
```

**Query Parameters**:
| Param | Type | Description |
|-------|------|-------------|
| page | int | Page number (0-based) |
| size | int | Page size (max 100) |
| search | string | Search in student ID, name, email |
| status | string | ACTIVE, INACTIVE, GRADUATED, SUSPENDED, WITHDRAWN |
| departmentId | long | Filter by department |

**Response** (200):
```json
{
  "content": [
    {
      "id": 1,
      "studentId": "STU2023001",
      "userId": 8,
      "username": "student001",
      "email": "alice.johnson@student.university.edu",
      "fullName": "Alice Johnson",
      "phone": "+1-555-1001",
      "avatarUrl": null,
      "departmentId": 1,
      "departmentCode": "CS",
      "departmentName": "Computer Science",
      "admissionYear": 2023,
      "currentSemester": 3,
      "status": "ACTIVE",
      "dateOfBirth": "2005-03-15",
      "gender": "FEMALE",
      "address": "123 University Ave, Campus City",
      "emergencyContactName": "Mary Johnson",
      "emergencyContactPhone": "+1-555-2001",
      "bloodGroup": "O+",
      "createdAt": "2024-01-15T10:00:00",
      "updatedAt": "2024-01-15T10:00:00",
      "activeEnrollmentsCount": 4,
      "attendancePercentage": 92.5,
      "overallPercentage": 87.3,
      "overallGrade": "A"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20, "sort": { "sorted": false } },
  "totalPages": 8,
  "totalElements": 150,
  "last": false,
  "first": true,
  "size": 20,
  "number": 0,
  "sort": { "sorted": false, "unsorted": true, "empty": true },
  "numberOfElements": 20,
  "empty": false
}
```

### Get Student by ID
```http
GET /api/students/1
Authorization: Bearer <token>
```

### Get Student by Student ID
```http
GET /api/students/student-id/STU2023001
Authorization: Bearer <token>
```

### Create Student
```http
POST /api/students
Authorization: Bearer <token>
Content-Type: application/json

{
  "studentId": "STU2024001",
  "username": "newstudent",
  "email": "newstudent@university.edu",
  "password": "securePassword123",
  "fullName": "New Student",
  "phone": "+1-555-0199",
  "departmentId": 1,
  "admissionYear": 2024,
  "currentSemester": 1,
  "dateOfBirth": "2006-01-15",
  "gender": "FEMALE",
  "address": "456 College Ave",
  "emergencyContactName": "Parent Name",
  "emergencyContactPhone": "+1-555-0198",
  "bloodGroup": "A+"
}
```

### Update Student
```http
PUT /api/students/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Updated Name",
  "phone": "+1-555-0200",
  "email": "newemail@university.edu",
  "currentSemester": 4,
  "dateOfBirth": "2005-03-15",
  "gender": "FEMALE",
  "address": "789 New Address",
  "emergencyContactName": "New Contact",
  "emergencyContactPhone": "+1-555-0201",
  "bloodGroup": "A+",
  "departmentId": 1
}
```

### Delete Student (Soft Delete)
```http
DELETE /api/students/1
Authorization: Bearer <token>
```

### Change Student Status
```http
PATCH /api/students/1/status?status=SUSPENDED
Authorization: Bearer <token>
```

### Count Students by Department
```http
GET /api/students/count/department/1
Authorization: Bearer <token>
```

### Count Students by Status
```http
GET /api/students/count/status/ACTIVE
Authorization: Bearer <token>
```

---

## Faculty API

### List Faculty
```http
GET /api/faculty?page=0&size=20&search=smith&status=ACTIVE&departmentId=1
Authorization: Bearer <token>
```

### Get Faculty by ID
```http
GET /api/faculty/1
Authorization: Bearer <token>
```

### Get Faculty by Faculty ID
```http
GET /api/faculty/faculty-id/FAC001
Authorization: Bearer <token>
```

### Create Faculty
```http
POST /api/faculty
Authorization: Bearer <token>
Content-Type: application/json

{
  "facultyId": "FAC007",
  "username": "newfaculty",
  "email": "newfaculty@university.edu",
  "password": "securePassword123",
  "fullName": "Dr. New Faculty",
  "phone": "+1-555-0110",
  "departmentId": 1,
  "designation": "ASSISTANT_PROFESSOR",
  "specialization": "Machine Learning",
  "qualification": "Ph.D. Computer Science",
  "hireDate": "2024-01-15",
  "officeLocation": "CS-404",
  "researchInterests": "Deep Learning, NLP"
}
```

### Update Faculty
```http
PUT /api/faculty/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Updated Name",
  "phone": "+1-555-0200",
  "email": "updated@university.edu",
  "designation": "ASSOCIATE_PROFESSOR",
  "specialization": "Updated Specialization",
  "qualification": "Ph.D. Updated",
  "officeLocation": "CS-505",
  "researchInterests": "Updated Interests",
  "departmentId": 1
}
```

### Delete Faculty
```http
DELETE /api/faculty/1
Authorization: Bearer <token>
```

### Change Faculty Status
```http
PATCH /api/faculty/1/status?status=ON_LEAVE
Authorization: Bearer <token>
```

### Get Faculty by Department
```http
GET /api/faculty/department/1
Authorization: Bearer <token>
```

---

## Courses API

### List Courses
```http
GET /api/courses?page=0&size=20&search=CS&status=ACTIVE&departmentId=1&semester=3&facultyId=1&academicYear=2024-2025
Authorization: Bearer <token>
```

### Get Course by ID
```http
GET /api/courses/1
Authorization: Bearer <token>
```

### Get Course by Code
```http
GET /api/courses/code/CS101
Authorization: Bearer <token>
```

### Create Course
```http
POST /api/courses
Authorization: Bearer <token>
Content-Type: application/json

{
  "courseCode": "CS303",
  "title": "Advanced Algorithms",
  "description": "Advanced algorithm design and analysis",
  "credits": 3,
  "departmentId": 1,
  "semester": 5,
  "facultyId": 1,
  "maxEnrollment": 40,
  "prerequisites": "CS102, MATH201",
  "syllabusUrl": "https://syllabus.university.edu/CS303",
  "academicYear": "2024-2025"
}
```

### Update Course
```http
PUT /api/courses/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "description": "Updated description",
  "credits": 4,
  "semester": 6,
  "maxEnrollment": 50,
  "prerequisites": "CS101, CS102",
  "syllabusUrl": "https://syllabus.university.edu/CS101-v2",
  "academicYear": "2024-2025",
  "departmentId": 1,
  "facultyId": 2
}
```

### Delete Course
```http
DELETE /api/courses/1
Authorization: Bearer <token>
```

### Change Course Status
```http
PATCH /api/courses/1/status?status=ARCHIVED
Authorization: Bearer <token>
```

### Get Active Courses by Faculty
```http
GET /api/courses/faculty/1/active
Authorization: Bearer <token>
```

### Get Courses by Department
```http
GET /api/courses/department/1
Authorization: Bearer <token>
```

---

## Enrollments API

### List Enrollments
```http
GET /api/enrollments?page=0&size=20&studentId=1&courseId=3&status=ENROLLED
Authorization: Bearer <token>
```

### Get Enrollment by ID
```http
GET /api/enrollments/1
Authorization: Bearer <token>
```

### Create Enrollment
```http
POST /api/enrollments
Authorization: Bearer <token>
Content-Type: application/json

{
  "studentId": 1,
  "courseId": 3,
  "enrollmentDate": "2024-01-15"
}
```

### Update Enrollment
```http
PUT /api/enrollments/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "COMPLETED",
  "grade": "A",
  "gradePoints": 9.5
}
```

### Delete Enrollment (Withdraw)
```http
DELETE /api/enrollments/1
Authorization: Bearer <token>
```

### Change Enrollment Status
```http
PATCH /api/enrollments/1/status?status=DROPPED
Authorization: Bearer <token>
```

### Get Active Enrollments by Student
```http
GET /api/enrollments/student/1/active
Authorization: Bearer <token>
```

### Get Active Enrollments by Course
```http
GET /api/enrollments/course/3/active
Authorization: Bearer <token>
```

---

## Attendance API

### List Attendance Sessions
```http
GET /api/attendance/sessions?page=0&size=20&courseId=3&startDate=2024-01-01&endDate=2024-12-31
Authorization: Bearer <token>
```

### Get Session by ID
```http
GET /api/attendance/sessions/1
Authorization: Bearer <token>
```

### Create Attendance Session
```http
POST /api/attendance/sessions
Authorization: Bearer <token>
Content-Type: application/json

{
  "courseId": 3,
  "sessionDate": "2024-01-22",
  "sessionNumber": 1,
  "topic": "Introduction to Databases",
  "durationMinutes": 60,
  "createdById": 2
}
```

### Update Session
```http
PUT /api/attendance/sessions/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "topic": "Updated Topic",
  "durationMinutes": 90,
  "isCancelled": false,
  "cancellationReason": "Rescheduled"
}
```

### Delete Session
```http
DELETE /api/attendance/sessions/1
Authorization: Bearer <token>
```

### List Attendance Records
```http
GET /api/attendance/records?page=0&size=50&sessionId=1&enrollmentId=1&status=PRESENT
Authorization: Bearer <token>
```

### Create Attendance Record
```http
POST /api/attendance/records
Authorization: Bearer <token>
Content-Type: application/json

{
  "sessionId": 1,
  "enrollmentId": 1,
  "status": "PRESENT",
  "remarks": "On time",
  "recordedById": 2
}
```

### Bulk Create Attendance Records
```http
POST /api/attendance/records/bulk
Authorization: Bearer <token>
Content-Type: application/json

{
  "sessionId": 1,
  "recordedById": 2,
  "records": [
    { "enrollmentId": 1, "status": "PRESENT", "remarks": "" },
    { "enrollmentId": 2, "status": "ABSENT", "remarks": "Sick" },
    { "enrollmentId": 3, "status": "LATE", "remarks": "Traffic" }
  ]
}
```

### Update Attendance Record
```http
PUT /api/attendance/records/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "LATE",
  "remarks": "Arrived 15 min late"
}
```

### Get Student Attendance Summary
```http
GET /api/attendance/student/1/course/3/summary
Authorization: Bearer <token>
```

### Get Course Attendance Summary
```http
GET /api/attendance/course/3/summary
Authorization: Bearer <token>
```

---

## Assessments API

### List Assessments
```http
GET /api/assessments?page=0&size=20&courseId=3&type=MIDTERM&isPublished=true
Authorization: Bearer <token>
```

### Get Assessment by ID
```http
GET /api/assessments/1
Authorization: Bearer <token>
```

### Get Assessments by Course
```http
GET /api/assessments/course/3
Authorization: Bearer <token>
```

### Get Published Assessments by Course
```http
GET /api/assessments/course/3/published
Authorization: Bearer <token>
```

### Create Assessment
```http
POST /api/assessments
Authorization: Bearer <token>
Content-Type: application/json

{
  "courseId": 3,
  "title": "Midterm Exam",
  "description": "Covers chapters 1-5",
  "type": "MIDTERM",
  "maxMarks": 100,
  "weightage": 30,
  "assessmentDate": "2024-02-15",
  "dueDate": "2024-02-15T23:59:59",
  "createdById": 2
}
```

### Update Assessment
```http
PUT /api/assessments/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "description": "Updated description",
  "type": "MIDTERM",
  "maxMarks": 100,
  "weightage": 35,
  "assessmentDate": "2024-02-16",
  "dueDate": "2024-02-16T23:59:59"
}
```

### Delete Assessment
```http
DELETE /api/assessments/1
Authorization: Bearer <token>
```

### Publish/Unpublish Assessment
```http
PATCH /api/assessments/1/publish
Authorization: Bearer <token>

PATCH /api/assessments/1/unpublish
Authorization: Bearer <token>
```

---

## Marks API

### List Marks
```http
GET /api/marks?page=0&size=50&assessmentId=1&enrollmentId=1&isFinalized=true
Authorization: Bearer <token>
```

### Get Marks by ID
```http
GET /api/marks/1
Authorization: Bearer <token>
```

### Get Marks by Assessment
```http
GET /api/marks/assessment/1
Authorization: Bearer <token>
```

### Get Marks by Enrollment
```http
GET /api/marks/enrollment/1
Authorization: Bearer <token>
```

### Get Marks by Student and Course
```http
GET /api/marks/student/1/course/3
Authorization: Bearer <token>
```

### Create Marks
```http
POST /api/marks
Authorization: Bearer <token>
Content-Type: application/json

{
  "assessmentId": 1,
  "enrollmentId": 1,
  "obtainedMarks": 85.5,
  "gradedById": 2,
  "feedback": "Good work",
  "isFinalized": true
}
```

### Bulk Create Marks
```http
POST /api/marks/bulk
Authorization: Bearer <token>
Content-Type: application/json

{
  "assessmentId": 1,
  "gradedById": 2,
  "records": [
    { "enrollmentId": 1, "obtainedMarks": 85.5, "feedback": "Good", "isFinalized": true },
    { "enrollmentId": 2, "obtainedMarks": 72.0, "feedback": "Needs improvement", "isFinalized": true }
  ]
}
```

### Update Marks
```http
PUT /api/marks/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "obtainedMarks": 90.0,
  "feedback": "Excellent improvement",
  "isFinalized": true
}
```

### Finalize/Unfinalize Marks
```http
PATCH /api/marks/1/finalize
Authorization: Bearer <token>

PATCH /api/marks/1/unfinalize
Authorization: Bearer <token>
```

### Get Course Result for Student
```http
GET /api/marks/student/1/course/3/result
Authorization: Bearer <token>
```

---

## Departments API

### List Departments
```http
GET /api/departments?page=0&size=20&search=CS
Authorization: Bearer <token>
```

### Get Department by ID
```http
GET /api/departments/1
Authorization: Bearer <token>
```

### Get Department by Code
```http
GET /api/departments/code/CS
Authorization: Bearer <token>
```

### Get Active Departments
```http
GET /api/departments/active
Authorization: Bearer <token>
```

### Create Department
```http
POST /api/departments
Authorization: Bearer <token>
Content-Type: application/json

{
  "code": "BIO",
  "name": "Biology",
  "description": "Department of Biological Sciences",
  "headFacultyId": 1
}
```

### Update Department
```http
PUT /api/departments/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Name",
  "description": "Updated description",
  "isActive": true,
  "headFacultyId": 2
}
```

### Delete Department
```http
DELETE /api/departments/1
Authorization: Bearer <token>
```

---

## Grading Policies API

### List Grading Policies
```http
GET /api/grading-policies
Authorization: Bearer <token>
```

### Get Default Grading Policy
```http
GET /api/grading-policies/default
Authorization: Bearer <token>
```

### Create Grading Policy
```http
POST /api/grading-policies
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "ENGINEERING",
  "description": "Engineering grading scale",
  "isDefault": false,
  "gradeBoundaries": {
    "A+": 95, "A": 90, "A-": 85,
    "B+": 80, "B": 75, "B-": 70,
    "C+": 65, "C": 60, "C-": 55,
    "D": 50, "F": 0
  }
}
```

### Update Grading Policy
```http
PUT /api/grading-policies/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "UPDATED_NAME",
  "description": "Updated description",
  "isDefault": true,
  "gradeBoundaries": {
    "A+": 90, "A": 85, "A-": 80,
    "B+": 75, "B": 70, "B-": 65,
    "C+": 60, "C": 55, "C-": 50,
    "D": 40, "F": 0
  }
}
```

### Delete Grading Policy
```http
DELETE /api/grading-policies/1
Authorization: Bearer <token>
```

### Set as Default
```http
POST /api/grading-policies/1/set-default
Authorization: Bearer <token>
```

---

## Reports API

### Student Academic Profile
```http
GET /api/reports/student-academic-profile/1
Authorization: Bearer <token>
```

### Course Enrollment Report
```http
GET /api/reports/course-enrollment/3
Authorization: Bearer <token>
```

### Faculty Course Assignment Report
```http
GET /api/reports/faculty-course-assignment/1
Authorization: Bearer <token>
```

### Attendance Report (Course)
```http
GET /api/reports/attendance/course/3
Authorization: Bearer <token>
```

### Student Attendance Report
```http
GET /api/reports/attendance/student/1/course/3
Authorization: Bearer <token>
```

### Course Results Report
```http
GET /api/reports/course-results/3
Authorization: Bearer <token>
```

### Student Performance Summary
```http
GET /api/reports/student-performance/1
Authorization: Bearer <token>
```

### Department Student Performance
```http
GET /api/reports/department/1/students
Authorization: Bearer <token>
```

---

## Dashboard API

### Admin Dashboard Stats
```http
GET /api/dashboard/stats
Authorization: Bearer <token>
```

**Response**:
```json
{
  "totalStudents": 150,
  "totalFaculty": 25,
  "totalCourses": 45,
  "activeEnrollments": 1200,
  "averageAttendancePercentage": 87.5,
  "averagePerformancePercentage": 78.2,
  "pendingApprovals": 3
}
```

### Faculty Dashboard
```http
GET /api/dashboard/stats/faculty
Authorization: Bearer <token>
```

### Student Dashboard
```http
GET /api/dashboard/stats/student
Authorization: Bearer <token>
```

---

## Users API

### List Users
```http
GET /api/users?page=0&size=20&role=STUDENT&search=john
Authorization: Bearer <token>
```

### Get User by ID
```http
GET /api/users/1
Authorization: Bearer <token>
```

### Get Current User
```http
GET /api/users/me
Authorization: Bearer <token>
```

### Update User
```http
PUT /api/users/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Updated Name",
  "phone": "+1-555-0200",
  "email": "newemail@university.edu",
  "avatarUrl": "https://example.com/avatar.jpg",
  "role": "FACULTY"
}
```

### Update Current User
```http
PUT /api/users/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "Updated Name",
  "phone": "+1-555-0200",
  "email": "newemail@university.edu",
  "avatarUrl": "https://example.com/avatar.jpg"
}
```

### Change Password
```http
PUT /api/users/me/password
Authorization: Bearer <token>
Content-Type: application/json

{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword123",
  "confirmPassword": "newPassword123"
}
```

### Change User Status
```http
PATCH /api/users/1/status?isActive=false
Authorization: Bearer <token>
```

### Delete User
```http
DELETE /api/users/1
Authorization: Bearer <token>
```

---

## Notifications API

### Get My Notifications
```http
GET /api/notifications?page=0&size=20&isRead=false
Authorization: Bearer <token>
```

### Get Unread Count
```http
GET /api/notifications/unread-count
Authorization: Bearer <token>
```

### Mark as Read
```http
PATCH /api/notifications/1/read
Authorization: Bearer <token>
```

### Mark All as Read
```http
PATCH /api/notifications/read-all
Authorization: Bearer <token>
```

### Delete Notification
```http
DELETE /api/notifications/1
Authorization: Bearer <token>
```

---

## Error Responses

### Standard Error Format
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Student not found with id: 999",
  "path": "/api/students/999"
}
```

### Validation Error
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "path": "/api/students",
  "validationErrors": {
    "email": "Invalid email format",
    "studentId": "Student ID already exists"
  }
}
```

### Common HTTP Status Codes
| Code | Description |
|------|-------------|
| 200 | Success |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request / Validation Error |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict (Duplicate) |
| 500 | Internal Server Error |

---

## Rate Limiting
- **Default**: 100 requests/minute per IP
- **Auth endpoints**: 10 requests/minute per IP
- **Headers**: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`

## Pagination
All list endpoints support pagination:
```
GET /api/resource?page=0&size=20&sort=field,asc
```
- `page`: Zero-based page index
- `size`: Page size (max 100)
- `sort`: Field and direction (field,asc|desc)

---

## Webhooks (Future)
- Student enrollment webhook
- Grade submission webhook
- Attendance recording webhook