import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Course, CourseStatus } from '../types'

export const coursesApi = {
  getAll: (params?: {
    page?: number
    size?: number
    departmentId?: number
    semester?: number
    facultyId?: number
    status?: CourseStatus
    academicYear?: string
    search?: string
  }) => api.get<PaginatedResponse<Course>>('/courses', { params }),

  getById: (id: number) => api.get<Course>(`/courses/${id}`),

  getByCode: (courseCode: string) => api.get<Course>(`/courses/code/${courseCode}`),

  create: (data: any) => api.post<Course>('/courses', data),

  update: (id: number, data: any) => api.put<Course>(`/courses/${id}`, data),

  delete: (id: number) => api.delete(`/courses/${id}`),

  changeStatus: (id: number, status: CourseStatus) => api.patch(`/courses/${id}/status`, null, { params: { status } }),

  getActiveByFaculty: (facultyId: number) => api.get<Course[]>(`/courses/faculty/${facultyId}/active`),

  getByDepartment: (departmentId: number) => api.get<Course[]>(`/courses/department/${departmentId}`),
}