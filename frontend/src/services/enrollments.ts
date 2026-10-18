import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Enrollment, EnrollmentStatus } from '../types'

export const enrollmentsApi = {
  getAll: (params?: {
    page?: number
    size?: number
    studentId?: number
    courseId?: number
    status?: EnrollmentStatus
  }) => api.get<PaginatedResponse<Enrollment>>('/enrollments', { params }),

  getById: (id: number) => api.get<Enrollment>(`/enrollments/${id}`),

  create: (data: any) => api.post<Enrollment>('/enrollments', data),

  update: (id: number, data: any) => api.put<Enrollment>(`/enrollments/${id}`, data),

  delete: (id: number) => api.delete(`/enrollments/${id}`),

  changeStatus: (id: number, status: EnrollmentStatus) => api.patch(`/enrollments/${id}/status`, null, { params: { status } }),

  getActiveByStudent: (studentId: number) => api.get<Enrollment[]>(`/enrollments/student/${studentId}/active`),

  getActiveByCourse: (courseId: number) => api.get<Enrollment[]>(`/enrollments/course/${courseId}/active`),
}