import { api } from './api'
import { PaginatedResponse } from '../types'

export const reportsApi = {
  getStudentAcademicProfile: (studentId: number) => api.get<any>(`/reports/student-academic-profile/${studentId}`),

  getCourseEnrollmentReport: (courseId: number) => api.get<any>(`/reports/course-enrollment/${courseId}`),

  getFacultyCourseAssignmentReport: (facultyId: number) => api.get<any>(`/reports/faculty-course-assignment/${facultyId}`),

  getAttendanceReport: (courseId: number) => api.get<any>(`/reports/attendance/course/${courseId}`),

  getStudentAttendanceReport: (studentId: number, courseId: number) => api.get<any>(`/reports/attendance/student/${studentId}/course/${courseId}`),

  getCourseResultsReport: (courseId: number) => api.get<any>(`/reports/course-results/${courseId}`),

  getStudentPerformanceSummary: (studentId: number) => api.get<any>(`/reports/student-performance/${studentId}`),

  getDepartmentStudentPerformance: (departmentId: number) => api.get<any[]>(`/reports/department/${departmentId}/students`),
}