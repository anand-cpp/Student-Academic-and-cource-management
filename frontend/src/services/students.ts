import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Student, StudentStatus, FilterParams } from '../types'

export const studentsApi = {
  getAll: (params?: FilterParams): Promise<PaginatedResponse<Student>> => {
    return api.students.getAll(params)
  },

  getById: (id: string): Promise<Student | null> => {
    return api.students.getById(id)
  },

  getByStudentId: (studentId: string): Promise<Student | null> => {
    return api.students.getById(studentId)
  },

  create: (data: Omit<Student, 'id' | 'createdAt' | 'updatedAt'>): Promise<Student> => {
    return api.students.create(data)
  },

  update: (id: string, data: Partial<Student>): Promise<Student> => {
    return api.students.update(id, data)
  },

  delete: (id: string): Promise<void> => {
    return api.students.delete(id)
  },

  changeStatus: (id: string, status: StudentStatus): Promise<Student> => {
    return api.students.update(id, { status })
  },

  countByDepartment: async (departmentId: string): Promise<number> => {
    const response = await api.students.getAll({ pageSize: 1000 })
    return response.data.filter(s => s.departmentId === departmentId).length
  },

  countByStatus: async (status: StudentStatus): Promise<number> => {
    const response = await api.students.getAll({ pageSize: 1000 })
    return response.data.filter(s => s.status === status).length
  },
}