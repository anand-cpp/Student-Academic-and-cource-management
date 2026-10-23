import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Faculty, FacultyStatus, FilterParams } from '../types'

export const facultyApi = {
  getAll: (params?: FilterParams): Promise<PaginatedResponse<Faculty>> => {
    return api.faculty.getAll(params)
  },

  getById: (id: string): Promise<Faculty | null> => {
    return api.faculty.getById(id)
  },

  getByFacultyId: (facultyId: string): Promise<Faculty | null> => {
    return api.faculty.getById(facultyId)
  },

  create: (data: Omit<Faculty, 'id' | 'createdAt' | 'updatedAt'>): Promise<Faculty> => {
    return api.faculty.create(data)
  },

  update: (id: string, data: Partial<Faculty>): Promise<Faculty> => {
    return api.faculty.update(id, data)
  },

  delete: (id: string): Promise<void> => {
    return api.faculty.delete(id)
  },

  changeStatus: (id: string, status: FacultyStatus): Promise<Faculty> => {
    return api.faculty.update(id, { status })
  },

  getByDepartment: async (departmentId: string): Promise<Faculty[]> => {
    const response = await api.faculty.getAll({ pageSize: 1000 })
    return response.data.filter(f => f.departmentId === departmentId)
  },
}