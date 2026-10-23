import { 
  Student, 
  Faculty, 
  Course, 
  Enrollment, 
  Attendance, 
  Assessment, 
  Mark,
  Department,
  Program,
  Batch,
  Section,
  PaginatedResponse,
  FilterParams,
  User,
  UserRole,
} from '@/types';

const delay = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));

const mockStudents: Student[] = [
  {
    id: '1',
    studentId: 'STU2024001',
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@student.university.edu',
    phone: '+1-555-0101',
    dateOfBirth: '2002-05-15',
    gender: 'MALE',
    address: '123 Main St',
    city: 'New York',
    state: 'NY',
    zipCode: '10001',
    country: 'USA',
    nationality: 'American',
    bloodGroup: 'O+',
    emergencyContactName: 'Jane Doe',
    emergencyContactPhone: '+1-555-0102',
    emergencyContactRelation: 'Mother',
    enrollmentDate: '2024-01-15',
    status: 'ACTIVE',
    departmentId: '1',
    programId: '1',
    batchId: '1',
    sectionId: '1',
    advisorId: '1',
    guardianName: 'Robert Doe',
    guardianPhone: '+1-555-0103',
    guardianEmail: 'robert.doe@email.com',
    guardianRelation: 'Father',
    createdAt: '2024-01-15T00:00:00Z',
    updatedAt: '2024-01-15T00:00:00Z',
  },
  {
    id: '2',
    studentId: 'STU2024002',
    firstName: 'Jane',
    lastName: 'Smith',
    email: 'jane.smith@student.university.edu',
    phone: '+1-555-0201',
    dateOfBirth: '2001-08-22',
    gender: 'FEMALE',
    address: '456 Oak Ave',
    city: 'Boston',
    state: 'MA',
    zipCode: '02101',
    country: 'USA',
    nationality: 'American',
    bloodGroup: 'A+',
    emergencyContactName: 'Michael Smith',
    emergencyContactPhone: '+1-555-0202',
    emergencyContactRelation: 'Father',
    enrollmentDate: '2024-01-15',
    status: 'ACTIVE',
    departmentId: '1',
    programId: '1',
    batchId: '1',
    sectionId: '1',
    advisorId: '1',
    guardianName: 'Sarah Smith',
    guardianPhone: '+1-555-0203',
    guardianEmail: 'sarah.smith@email.com',
    guardianRelation: 'Mother',
    createdAt: '2024-01-15T00:00:00Z',
    updatedAt: '2024-01-15T00:00:00Z',
  },
];

const mockFaculty: Faculty[] = [
  {
    id: '1',
    employeeId: 'FAC001',
    firstName: 'Robert',
    lastName: 'Johnson',
    email: 'robert.johnson@university.edu',
    phone: '+1-555-1001',
    dateOfBirth: '1975-03-10',
    gender: 'MALE',
    address: '789 Faculty Row',
    city: 'Cambridge',
    state: 'MA',
    zipCode: '02138',
    country: 'USA',
    qualification: 'PhD Computer Science',
    specialization: 'Artificial Intelligence',
    experienceYears: 15,
    joiningDate: '2010-08-15',
    status: 'ACTIVE',
    departmentId: '1',
    designationId: '1',
    createdAt: '2010-08-15T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

const mockCourses: Course[] = [
  {
    id: '1',
    code: 'CS101',
    name: 'Introduction to Computer Science',
    description: 'Fundamentals of programming and computer science concepts',
    credits: 3,
    theoryHours: 45,
    practicalHours: 30,
    departmentId: '1',
    programId: '1',
    semester: 1,
    type: 'CORE',
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: '2',
    code: 'CS201',
    name: 'Data Structures and Algorithms',
    description: 'Advanced data structures and algorithm design',
    credits: 4,
    theoryHours: 60,
    practicalHours: 30,
    departmentId: '1',
    programId: '1',
    semester: 2,
    type: 'CORE',
    prerequisites: ['CS101'],
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

const mockDepartments: Department[] = [
  {
    id: '1',
    code: 'CS',
    name: 'Computer Science',
    description: 'Department of Computer Science and Engineering',
    headId: '1',
    establishedDate: '1990-01-01',
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

const mockPrograms: Program[] = [
  {
    id: '1',
    code: 'BSCS',
    name: 'Bachelor of Science in Computer Science',
    description: '4-year undergraduate program in Computer Science',
    departmentId: '1',
    durationYears: 4,
    totalCredits: 120,
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

const mockBatches: Batch[] = [
  {
    id: '1',
    name: '2024 Batch',
    startYear: 2024,
    endYear: 2028,
    programId: '1',
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

const mockSections: Section[] = [
  {
    id: '1',
    name: 'Section A',
    batchId: '1',
    capacity: 60,
    currentStrength: 45,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
];

const mockEnrollments: Enrollment[] = [
  {
    id: '1',
    studentId: '1',
    courseId: '1',
    sectionId: '1',
    facultyId: '1',
    enrollmentDate: '2024-01-20',
    status: 'ENROLLED',
    createdAt: '2024-01-20T00:00:00Z',
    updatedAt: '2024-01-20T00:00:00Z',
  },
];

const mockAttendance: Attendance[] = [
  {
    id: '1',
    enrollmentId: '1',
    date: '2024-01-22',
    status: 'PRESENT',
    markedBy: '1',
    createdAt: '2024-01-22T08:00:00Z',
    updatedAt: '2024-01-22T08:00:00Z',
  },
];

const mockAssessments: Assessment[] = [
  {
    id: '1',
    courseId: '1',
    title: 'Midterm Exam',
    description: 'Covers chapters 1-5',
    type: 'MIDTERM',
    maxMarks: 100,
    weightage: 30,
    scheduledDate: '2024-03-01',
    dueDate: '2024-03-01',
    isPublished: true,
    createdAt: '2024-01-15T00:00:00Z',
    updatedAt: '2024-01-15T00:00:00Z',
  },
];

const mockMarks: Mark[] = [
  {
    id: '1',
    enrollmentId: '1',
    assessmentId: '1',
    marksObtained: 85,
    gradedBy: '1',
    gradedAt: '2024-03-05T00:00:00Z',
    isPublished: true,
    createdAt: '2024-03-05T00:00:00Z',
    updatedAt: '2024-03-05T00:00:00Z',
  },
];

export const api = {
  auth: {
    login: async (email: string, password: string): Promise<User> => {
      await delay(500);
      const users: Record<string, User> = {
        'admin@university.edu': { id: '1', email: 'admin@university.edu', firstName: 'Admin', lastName: 'User', role: 'ADMIN', departmentId: '1', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
        'faculty@university.edu': { id: '2', email: 'faculty@university.edu', firstName: 'John', lastName: 'Smith', role: 'FACULTY', departmentId: '1', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
        'student@university.edu': { id: '3', email: 'student@university.edu', firstName: 'Jane', lastName: 'Doe', role: 'STUDENT', departmentId: '1', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
        'hod@university.edu': { id: '4', email: 'hod@university.edu', firstName: 'Robert', lastName: 'Johnson', role: 'DEPARTMENT_HEAD', departmentId: '1', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
        'registrar@university.edu': { id: '5', email: 'registrar@university.edu', firstName: 'Mary', lastName: 'Williams', role: 'REGISTRAR', departmentId: '1', createdAt: '2024-01-01T00:00:00Z', updatedAt: '2024-01-01T00:00:00Z' },
      };
      const user = users[email];
      if (user && password === 'password123') return user;
      throw new Error('Invalid credentials');
    },
  },

  students: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Student>> => {
      await delay(300);
      let data = [...mockStudents];
      if (params?.search) {
        const search = params.search.toLowerCase();
        data = data.filter(s => 
          s.firstName.toLowerCase().includes(search) ||
          s.lastName.toLowerCase().includes(search) ||
          s.studentId.toLowerCase().includes(search) ||
          s.email.toLowerCase().includes(search)
        );
      }
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return {
        data: data.slice(start, start + pageSize),
        total: data.length,
        page,
        pageSize,
        totalPages: Math.ceil(data.length / pageSize),
        hasNext: start + pageSize < data.length,
        hasPrev: page > 1,
      };
    },
    getById: async (id: string): Promise<Student | null> => {
      await delay(200);
      return mockStudents.find(s => s.id === id) || null;
    },
    create: async (student: Omit<Student, 'id' | 'createdAt' | 'updatedAt'>): Promise<Student> => {
      await delay(500);
      const newStudent: Student = { ...student, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockStudents.push(newStudent);
      return newStudent;
    },
    update: async (id: string, student: Partial<Student>): Promise<Student> => {
      await delay(500);
      const index = mockStudents.findIndex(s => s.id === id);
      if (index === -1) throw new Error('Student not found');
      mockStudents[index] = { ...mockStudents[index], ...student, updatedAt: new Date().toISOString() };
      return mockStudents[index];
    },
    delete: async (id: string): Promise<void> => {
      await delay(300);
      const index = mockStudents.findIndex(s => s.id === id);
      if (index !== -1) mockStudents.splice(index, 1);
    },
  },

  faculty: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Faculty>> => {
      await delay(300);
      let data = [...mockFaculty];
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return { data: data.slice(start, start + pageSize), total: data.length, page, pageSize, totalPages: Math.ceil(data.length / pageSize), hasNext: start + pageSize < data.length, hasPrev: page > 1 };
    },
    getById: async (id: string): Promise<Faculty | null> => {
      await delay(200);
      return mockFaculty.find(f => f.id === id) || null;
    },
    create: async (faculty: Omit<Faculty, 'id' | 'createdAt' | 'updatedAt'>): Promise<Faculty> => {
      await delay(500);
      const newFaculty: Faculty = { ...faculty, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockFaculty.push(newFaculty);
      return newFaculty;
    },
    update: async (id: string, faculty: Partial<Faculty>): Promise<Faculty> => {
      await delay(500);
      const index = mockFaculty.findIndex(f => f.id === id);
      if (index === -1) throw new Error('Faculty not found');
      mockFaculty[index] = { ...mockFaculty[index], ...faculty, updatedAt: new Date().toISOString() };
      return mockFaculty[index];
    },
    delete: async (id: string): Promise<void> => {
      await delay(300);
      const index = mockFaculty.findIndex(f => f.id === id);
      if (index !== -1) mockFaculty.splice(index, 1);
    },
  },

  courses: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Course>> => {
      await delay(300);
      let data = [...mockCourses];
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return { data: data.slice(start, start + pageSize), total: data.length, page, pageSize, totalPages: Math.ceil(data.length / pageSize), hasNext: start + pageSize < data.length, hasPrev: page > 1 };
    },
    getById: async (id: string): Promise<Course | null> => {
      await delay(200);
      return mockCourses.find(c => c.id === id) || null;
    },
    create: async (course: Omit<Course, 'id' | 'createdAt' | 'updatedAt'>): Promise<Course> => {
      await delay(500);
      const newCourse: Course = { ...course, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockCourses.push(newCourse);
      return newCourse;
    },
    update: async (id: string, course: Partial<Course>): Promise<Course> => {
      await delay(500);
      const index = mockCourses.findIndex(c => c.id === id);
      if (index === -1) throw new Error('Course not found');
      mockCourses[index] = { ...mockCourses[index], ...course, updatedAt: new Date().toISOString() };
      return mockCourses[index];
    },
    delete: async (id: string): Promise<void> => {
      await delay(300);
      const index = mockCourses.findIndex(c => c.id === id);
      if (index !== -1) mockCourses.splice(index, 1);
    },
  },

  enrollments: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Enrollment>> => {
      await delay(300);
      let data = [...mockEnrollments];
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return { data: data.slice(start, start + pageSize), total: data.length, page, pageSize, totalPages: Math.ceil(data.length / pageSize), hasNext: start + pageSize < data.length, hasPrev: page > 1 };
    },
    create: async (enrollment: Omit<Enrollment, 'id' | 'createdAt' | 'updatedAt'>): Promise<Enrollment> => {
      await delay(500);
      const newEnrollment: Enrollment = { ...enrollment, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockEnrollments.push(newEnrollment);
      return newEnrollment;
    },
  },

  attendance: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Attendance>> => {
      await delay(300);
      let data = [...mockAttendance];
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return { data: data.slice(start, start + pageSize), total: data.length, page, pageSize, totalPages: Math.ceil(data.length / pageSize), hasNext: start + pageSize < data.length, hasPrev: page > 1 };
    },
    mark: async (attendance: Omit<Attendance, 'id' | 'createdAt' | 'updatedAt'>): Promise<Attendance> => {
      await delay(300);
      const newAttendance: Attendance = { ...attendance, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockAttendance.push(newAttendance);
      return newAttendance;
    },
    bulkMark: async (records: Omit<Attendance, 'id' | 'createdAt' | 'updatedAt'>[]): Promise<Attendance[]> => {
      await delay(500);
      return records.map(r => ({ ...r, id: String(Date.now() + Math.random()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() }));
    },
  },

  assessments: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Assessment>> => {
      await delay(300);
      let data = [...mockAssessments];
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return { data: data.slice(start, start + pageSize), total: data.length, page, pageSize, totalPages: Math.ceil(data.length / pageSize), hasNext: start + pageSize < data.length, hasPrev: page > 1 };
    },
    create: async (assessment: Omit<Assessment, 'id' | 'createdAt' | 'updatedAt'>): Promise<Assessment> => {
      await delay(500);
      const newAssessment: Assessment = { ...assessment, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockAssessments.push(newAssessment);
      return newAssessment;
    },
  },

  marks: {
    getAll: async (params?: FilterParams): Promise<PaginatedResponse<Mark>> => {
      await delay(300);
      let data = [...mockMarks];
      const page = params?.page || 1;
      const pageSize = params?.pageSize || 10;
      const start = (page - 1) * pageSize;
      return { data: data.slice(start, start + pageSize), total: data.length, page, pageSize, totalPages: Math.ceil(data.length / pageSize), hasNext: start + pageSize < data.length, hasPrev: page > 1 };
    },
    create: async (mark: Omit<Mark, 'id' | 'createdAt' | 'updatedAt'>): Promise<Mark> => {
      await delay(300);
      const newMark: Mark = { ...mark, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() };
      mockMarks.push(newMark);
      return newMark;
    },
  },

  departments: {
    getAll: async (): Promise<Department[]> => {
      await delay(200);
      return [...mockDepartments];
    },
  },

  programs: {
    getAll: async (): Promise<Program[]> => {
      await delay(200);
      return [...mockPrograms];
    },
  },

  batches: {
    getAll: async (): Promise<Batch[]> => {
      await delay(200);
      return [...mockBatches];
    },
  },

  sections: {
    getAll: async (): Promise<Section[]> => {
      await delay(200);
      return [...mockSections];
    },
  },

  dashboard: {
    getStats: async (role: UserRole): Promise<Record<string, number>> => {
      await delay(200);
      const stats: Record<UserRole, Record<string, number>> = {
        ADMIN: { totalStudents: 245, totalFaculty: 89, totalCourses: 156, totalDepartments: 12, pendingApprovals: 23, activeEnrollments: 1890 },
        FACULTY: { myCourses: 5, myStudents: 145, pendingGrades: 12, upcomingClasses: 8, attendanceRate: 94 },
        STUDENT: { enrolledCourses: 6, attendanceRate: 92, upcomingAssessments: 4, currentGPA: 3.7, completedCredits: 78 },
        DEPARTMENT_HEAD: { departmentStudents: 450, departmentFaculty: 25, departmentCourses: 45, pendingReviews: 8 },
        REGISTRAR: { totalEnrollments: 2100, pendingRegistrations: 45, graduationCandidates: 120, transcriptRequests: 35 },
      };
      return stats[role] || stats.ADMIN;
    },
  },
};