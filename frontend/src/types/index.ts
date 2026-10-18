export interface User {
  id: number
  username: string
  email: string
  fullName: string
  phone?: string
  avatarUrl?: string
  role: UserRole
  isActive: boolean
  lastLoginAt?: string
  emailVerified: boolean
  studentId?: number
  facultyId?: number
}

export type UserRole = 'ADMIN' | 'FACULTY' | 'STUDENT' | 'DEPARTMENT_HEAD' | 'REGISTRAR'

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
  user: User
}

export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface Student {
  id: number
  studentId: string
  userId: number
  username: string
  email: string
  fullName: string
  phone?: string
  avatarUrl?: string
  departmentId: number
  departmentCode: string
  departmentName: string
  admissionYear: number
  currentSemester: number
  status: StudentStatus
  dateOfBirth?: string
  gender?: string
  address?: string
  emergencyContactName?: string
  emergencyContactPhone?: string
  bloodGroup?: string
  createdAt: string
  updatedAt: string
  activeEnrollmentsCount?: number
  attendancePercentage?: number
  overallPercentage?: number
  overallGrade?: string
}

export type StudentStatus = 'ACTIVE' | 'INACTIVE' | 'GRADUATED' | 'SUSPENDED' | 'WITHDRAWN'

export interface Faculty {
  id: number
  facultyId: string
  userId: number
  username: string
  email: string
  fullName: string
  phone?: string
  avatarUrl?: string
  departmentId: number
  departmentCode: string
  departmentName: string
  designation: FacultyDesignation
  specialization?: string
  qualification?: string
  hireDate: string
  status: FacultyStatus
  officeLocation?: string
  researchInterests?: string
  createdAt: string
  updatedAt: string
  assignedCoursesCount?: number
  totalStudentsCount?: number
}

export type FacultyDesignation = 'PROFESSOR' | 'ASSOCIATE_PROFESSOR' | 'ASSISTANT_PROFESSOR' | 'LECTURER' | 'ADJUNCT' | 'VISITING'

export type FacultyStatus = 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'RETIRED'

export interface Department {
  id: number
  code: string
  name: string
  description?: string
  headFacultyId?: number
  headFacultyName?: string
  isActive: boolean
  createdAt: string
  updatedAt: string
  studentCount?: number
  facultyCount?: number
  courseCount?: number
}

export interface Course {
  id: number
  courseCode: string
  title: string
  description?: string
  credits: number
  departmentId: number
  departmentCode: string
  departmentName: string
  semester: number
  facultyId?: number
  facultyName?: string
  maxEnrollment: number
  prerequisites?: string
  syllabusUrl?: string
  status: CourseStatus
  academicYear: string
  createdAt: string
  updatedAt: string
  currentEnrollmentCount?: number
  availableSeats?: number
}

export type CourseStatus = 'ACTIVE' | 'INACTIVE' | 'ARCHIVED'

export interface Enrollment {
  id: number
  studentId: number
  studentName: string
  studentCode: string
  courseId: number
  courseCode: string
  courseTitle: string
  enrollmentDate: string
  status: EnrollmentStatus
  grade?: string
  gradePoints?: number
  completionDate?: string
  createdAt: string
  updatedAt: string
}

export type EnrollmentStatus = 'ENROLLED' | 'DROPPED' | 'COMPLETED' | 'WITHDRAWN' | 'FAILED'

export interface AttendanceSession {
  id: number
  courseId: number
  courseCode: string
  courseTitle: string
  sessionDate: string
  sessionNumber: number
  topic?: string
  durationMinutes: number
  isCancelled: boolean
  cancellationReason?: string
  createdById: number
  createdByName: string
  createdAt: string
  updatedAt: string
  totalStudents?: number
  presentCount?: number
  absentCount?: number
  lateCount?: number
  excusedCount?: number
  attendancePercentage?: number
}

export interface AttendanceRecord {
  id: number
  sessionId: number
  sessionDate: string
  sessionNumber: number
  enrollmentId: number
  studentId: number
  studentName: string
  studentCode: string
  status: AttendanceStatus
  remarks?: string
  recordedById: number
  recordedByName: string
  recordedAt: string
  updatedAt: string
}

export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED' | 'MEDICAL'

export interface Assessment {
  id: number
  courseId: number
  courseCode: string
  title: string
  description?: string
  type: AssessmentType
  maxMarks: number
  weightage: number
  assessmentDate: string
  dueDate?: string
  isPublished: boolean
  createdById: number
  createdAt: string
  updatedAt: string
  submittedCount?: number
  gradedCount?: number
  averageScore?: number
  averagePercentage?: number
}

export type AssessmentType = 'QUIZ' | 'MIDTERM' | 'FINAL' | 'ASSIGNMENT' | 'PROJECT' | 'LAB' | 'PRESENTATION' | 'PARTICIPATION' | 'OTHER'

export interface Marks {
  id: number
  assessmentId: number
  assessmentTitle: string
  assessmentType: string
  maxMarks: number
  weightage: number
  enrollmentId: number
  studentId: number
  studentName: string
  obtainedMarks: number
  percentage: number
  weightedScore: number
  gradedById?: number
  gradedAt?: string
  feedback?: string
  isFinalized: boolean
  createdAt: string
  updatedAt: string
}

export interface DashboardStats {
  totalStudents: number
  totalFaculty: number
  totalCourses: number
  activeEnrollments: number
  averageAttendancePercentage: number
  averagePerformancePercentage: number
  pendingApprovals: number
}

export interface PaginatedResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  validationErrors?: Record<string, string>
}