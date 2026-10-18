import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Student, StudentStatus } from '../types'

export const studentsApi = {
  getAll: (params?: {
    page?: number
    size?: number
    departmentId?: number
    status?: StudentStatus
    search?: string
  }) => api.get<PaginatedResponse<Student>>('/students', { params }),

  getById: (id: number) => api.get<Student>(`/students/${id}`),

  getByStudentId: (studentId: string) => api.get<Student>(`/students/student-id/${studentId}`),

  create: (data: any) => api.post<Student>('/students', data),

  update: (id: number, data: any) => api.put<Student>(`/students/${id}`, data),

  delete: (id: number) => api.delete(`/students/${id}`),

  changeStatus: (id: number, status: StudentStatus) => api.patch(`/students/${id}/status`, null, { params: { status } }),

  countByDepartment: (departmentId: number) => api.get<number>(`/students/count/department/${departmentId}`),

  countByStatus: (status: StudentStatus) => api.get<number>(`/students/count/status/${status}`),
}