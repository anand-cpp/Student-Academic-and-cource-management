import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Assessment, AssessmentType } from '../types'

export const assessmentsApi = {
  getAll: (params?: {
    page?: number
    size?: number
    courseId?: number
    type?: AssessmentType
    isPublished?: boolean
    startDate?: string
    endDate?: string
  }) => api.get<PaginatedResponse<any>>('/assessments', { params }),

  getById: (id: number) => api.get<any>(`/assessments/${id}`),

  getByCourse: (courseId: number) => api.get<any[]>(`/assessments/course/${courseId}`),

  getPublishedByCourse: (courseId: number) => api.get<any[]>(`/assessments/course/${courseId}/published`),

  create: (data: any) => api.post<any>('/assessments', data),

  update: (id: number, data: any) => api.put<any>(`/assessments/${id}`, data),

  delete: (id: number) => api.delete(`/assessments/${id}`),

  publish: (id: number) => api.patch(`/assessments/${id}/publish`),

  unpublish: (id: number) => api.patch(`/assessments/${id}/unpublish`),
}