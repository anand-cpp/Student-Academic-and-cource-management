export type UserRole = 'ADMIN' | 'FACULTY' | 'STUDENT' | 'DEPARTMENT_HEAD' | 'REGISTRAR';

export type StudentStatus = 'ACTIVE' | 'INACTIVE' | 'GRADUATED' | 'SUSPENDED' | 'WITHDRAWN';
export type FacultyStatus = 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'RETIRED';
export type FacultyDesignation = 'PROFESSOR' | 'ASSOCIATE_PROFESSOR' | 'ASSISTANT_PROFESSOR' | 'LECTURER' | 'INSTRUCTOR' | 'TEACHING_ASSISTANT';
export type CourseStatus = 'ACTIVE' | 'INACTIVE' | 'ARCHIVED';
export type EnrollmentStatus = 'ENROLLED' | 'DROPPED' | 'COMPLETED' | 'FAILED' | 'WITHDRAWN';
export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED';
export type AssessmentType = 'QUIZ' | 'ASSIGNMENT' | 'MIDTERM' | 'FINAL' | 'PROJECT' | 'PRESENTATION' | 'LAB' | 'OTHER';

export interface AttendanceSession {
  id: string;
  courseId: string;
  sectionId: string;
  facultyId: string;
  date: string;
  startTime: string;
  endTime: string;
  topic: string;
  status: 'SCHEDULED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  createdAt: string;
  updatedAt: string;
}

export interface AttendanceRecord {
  id: string;
  sessionId: string;
  studentId: string;
  status: AttendanceStatus;
  markedAt: string;
  remarks?: string;
}

export interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  avatar?: string;
  departmentId?: string;
  createdAt: string;
  updatedAt: string;
  lastLoginAt?: string;
}

export interface Student {
  id: string;
  studentId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth: string;
  gender: 'MALE' | 'FEMALE' | 'OTHER';
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  nationality?: string;
  bloodGroup?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
  emergencyContactRelation?: string;
  enrollmentDate: string;
  status: 'ACTIVE' | 'INACTIVE' | 'GRADUATED' | 'SUSPENDED' | 'WITHDRAWN';
  departmentId: string;
  programId: string;
  batchId: string;
  sectionId?: string;
  advisorId?: string;
  guardianName?: string;
  guardianPhone?: string;
  guardianEmail?: string;
  guardianRelation?: string;
  avatar?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Faculty {
  id: string;
  employeeId: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  dateOfBirth?: string;
  gender?: 'MALE' | 'FEMALE' | 'OTHER';
  address?: string;
  city?: string;
  state?: string;
  zipCode?: string;
  country?: string;
  qualification?: string;
  specialization?: string;
  experienceYears?: number;
  joiningDate: string;
  status: 'ACTIVE' | 'INACTIVE' | 'ON_LEAVE' | 'RETIRED';
  departmentId: string;
  designationId: string;
  reportingTo?: string;
  avatar?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Department {
  id: string;
  code: string;
  name: string;
  description?: string;
  headId?: string;
  establishedDate?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Program {
  id: string;
  code: string;
  name: string;
  description?: string;
  departmentId: string;
  durationYears: number;
  totalCredits: number;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Batch {
  id: string;
  name: string;
  startYear: number;
  endYear: number;
  programId: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Section {
  id: string;
  name: string;
  batchId: string;
  capacity: number;
  currentStrength: number;
  createdAt: string;
  updatedAt: string;
}

export interface Course {
  id: string;
  code: string;
  name: string;
  description?: string;
  credits: number;
  theoryHours: number;
  practicalHours: number;
  departmentId: string;
  programId: string;
  semester: number;
  type: 'CORE' | 'ELECTIVE' | 'LAB' | 'PROJECT' | 'SEMINAR';
  prerequisites?: string[];
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Enrollment {
  id: string;
  studentId: string;
  courseId: string;
  sectionId: string;
  facultyId?: string;
  enrollmentDate: string;
  status: 'ENROLLED' | 'DROPPED' | 'COMPLETED' | 'FAILED' | 'WITHDRAWN';
  grade?: string;
  marks?: number;
  createdAt: string;
  updatedAt: string;
}

export interface Attendance {
  id: string;
  enrollmentId: string;
  date: string;
  status: 'PRESENT' | 'ABSENT' | 'LATE' | 'EXCUSED';
  markedBy: string;
  remarks?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Assessment {
  id: string;
  courseId: string;
  title: string;
  description?: string;
  type: 'QUIZ' | 'ASSIGNMENT' | 'MIDTERM' | 'FINAL' | 'PROJECT' | 'PRESENTATION' | 'LAB' | 'OTHER';
  maxMarks: number;
  weightage: number;
  scheduledDate: string;
  dueDate?: string;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Mark {
  id: string;
  enrollmentId: string;
  assessmentId: string;
  marksObtained: number;
  gradedBy: string;
  gradedAt: string;
  remarks?: string;
  isPublished: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ReportCard {
  id: string;
  studentId: string;
  batchId: string;
  semester: number;
  generatedAt: string;
  generatedBy: string;
  gpa: number;
  cgpa: number;
  status: 'DRAFT' | 'PUBLISHED' | 'ARCHIVED';
}

export interface ApiResponse<T> {
  data: T;
  message?: string;
  success: boolean;
  errors?: Record<string, string[]>;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
  hasNext: boolean;
  hasPrev: boolean;
}

export interface FilterParams {
  page?: number;
  pageSize?: number;
  search?: string;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  filters?: Record<string, unknown>;
}

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface NavItem {
  label: string;
  href: string;
  icon: string;
  children?: NavItem[];
  roles?: UserRole[];
}

export interface BreadcrumbItem {
  label: string;
  href?: string;
  isCurrent?: boolean;
}

export interface StatCardData {
  label: string;
  value: string | number;
  change?: number;
  changeLabel?: string;
  trend?: 'up' | 'down' | 'neutral';
  icon?: string;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
}

export interface TableColumn<T = unknown> {
  key: string;
  header: string;
  accessor?: keyof T | ((row: T) => unknown);
  sortable?: boolean;
  filterable?: boolean;
  render?: (value: unknown, row: T) => React.ReactNode;
  width?: string;
  align?: 'left' | 'center' | 'right';
}

export interface TableState {
  page: number;
  pageSize: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  filters: Record<string, unknown>;
  selection: string[];
}

export interface FormFieldProps {
  name: string;
  label: string;
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'date' | 'datetime-local' | 'time' | 'textarea' | 'select' | 'checkbox' | 'radio' | 'switch' | 'file';
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  readOnly?: boolean;
  options?: SelectOption[];
  validation?: unknown;
  helperText?: string;
  error?: string;
  className?: string;
}

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  description?: string;
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
  children: React.ReactNode;
  footer?: React.ReactNode;
  closeOnOverlayClick?: boolean;
  closeOnEscape?: boolean;
}

export interface ToastProps {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message?: string;
  duration?: number;
  action?: {
    label: string;
    onClick: () => void;
  };
  onClose: (id: string) => void;
}

export interface DropdownItem {
  label?: string;
  value?: string;
  icon?: React.ReactNode;
  disabled?: boolean;
  divider?: boolean;
  danger?: boolean;
  onClick?: () => void;
}

export interface SidebarItem {
  label: string;
  href?: string;
  icon: React.ReactNode;
  children?: SidebarItem[];
  badge?: string;
  roles?: UserRole[];
  isActive?: boolean;
}

export interface Theme {
  theme: 'light' | 'dark' | 'system';
  resolvedTheme: 'light' | 'dark';
  setTheme: (theme: 'light' | 'dark' | 'system') => void;
}