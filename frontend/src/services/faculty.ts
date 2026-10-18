import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Faculty, FacultyStatus, FacultyDesignation } from '../types'

export const facultyApi = {
  getAll: (params?: {
    page?: number
    size?: number
    departmentId?: number
    status?: FacultyStatus
    search?: string
  }) => api.get<PaginatedResponse<Faculty>>('/faculty', { params }),

  getById: (id: number) => api.get<Faculty>(`/faculty/${id}`),

  getByFacultyId: (facultyId: string) => api.get<Faculty>(`/faculty/faculty-id/${facultyId}`),

  create: (data: any) => api.post<Faculty>('/faculty', data),

  update: (id: number, data: any) => api.put<Faculty>(`/faculty/${id}`, data),

  delete: (id: number) => api.delete(`/faculty/${id}`),

  changeStatus: (id: number, status: FacultyStatus) => api.patch(`/faculty/${id}/status`, null, { params: { status } }),

  getByDepartment: (departmentId: number) => api.get<Faculty[]>(`/faculty/department/${departmentId}`),
}