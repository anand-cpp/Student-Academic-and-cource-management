import { api } from './api'
import { PaginatedResponse } from '../types'

export const marksApi = {
  getAll: (params?: {
    page?: number
    size?: number
    assessmentId?: number
    enrollmentId?: number
    isFinalized?: boolean
  }) => api.get<PaginatedResponse<any>>('/marks', { params }),

  getById: (id: number) => api.get<any>(`/marks/${id}`),

  getByAssessment: (assessmentId: number) => api.get<any[]>(`/marks/assessment/${assessmentId}`),

  getByEnrollment: (enrollmentId: number) => api.get<any[]>(`/marks/enrollment/${enrollmentId}`),

  getByStudentAndCourse: (studentId: number, courseId: number) => api.get<any[]>(`/marks/student/${studentId}/course/${courseId}`),

  create: (data: any) => api.post<any>('/marks', data),

  createBulk: (data: any) => api.post<any[]>('/marks/bulk', data),

  update: (id: number, data: any) => api.put<any>(`/marks/${id}`, data),

  finalize: (id: number) => api.patch(`/marks/${id}/finalize`),

  unfinalize: (id: number) => api.patch(`/marks/${id}/unfinalize`),

  getCourseResult: (studentId: number, courseId: number) => api.get<any>(`/marks/student/${studentId}/course/${courseId}/result`),
}