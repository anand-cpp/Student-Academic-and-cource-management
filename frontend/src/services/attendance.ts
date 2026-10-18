import { api } from './api'
import { PaginatedResponse } from '../types'
import type { AttendanceSession, AttendanceRecord, AttendanceStatus } from '../types'

export const attendanceApi = {
  getSessions: (params?: {
    page?: number
    size?: number
    courseId?: number
    startDate?: string
    endDate?: string
  }) => api.get<PaginatedResponse<AttendanceSession>>('/attendance/sessions', { params }),

  getSessionById: (id: number) => api.get<AttendanceSession>(`/attendance/sessions/${id}`),

  createSession: (data: any) => api.post<AttendanceSession>('/attendance/sessions', data),

  updateSession: (id: number, data: any) => api.put<AttendanceSession>(`/attendance/sessions/${id}`, data),

  deleteSession: (id: number) => api.delete(`/attendance/sessions/${id}`),

  getRecords: (params?: {
    page?: number
    size?: number
    sessionId?: number
    enrollmentId?: number
    status?: AttendanceStatus
  }) => api.get<PaginatedResponse<any>>('/attendance/records', { params }),

  createRecord: (data: any) => api.post<any>('/attendance/records', data),

  createBulkRecords: (data: any) => api.post<any[]>('/attendance/records/bulk', data),

  updateRecord: (id: number, data: any) => api.put<any>(`/attendance/records/${id}`, data),

  getStudentSummary: (studentId: number, courseId: number) => api.get<any>(`/attendance/student/${studentId}/course/${courseId}/summary`),

  getCourseSummary: (courseId: number) => api.get<any>(`/attendance/course/${courseId}/summary`),
}