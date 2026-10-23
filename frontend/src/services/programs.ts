import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Program, FilterParams } from '../types'

export const programsApi = {
  getAll: async (params?: FilterParams): Promise<PaginatedResponse<Program>> => {
    const data = await api.programs.getAll()
    return {
      data,
      total: data.length,
      page: params?.page || 1,
      pageSize: params?.pageSize || data.length,
      totalPages: 1,
      hasNext: false,
      hasPrev: false,
    }
  },

  getById: async (id: string): Promise<Program | null> => {
    const data = await api.programs.getAll()
    return data.find(p => p.id === id) || null
  },

  getByDepartment: async (departmentId: string): Promise<Program[]> => {
    const data = await api.programs.getAll()
    return data.filter(p => p.departmentId === departmentId)
  },

  create: async (data: Omit<Program, 'id' | 'createdAt' | 'updatedAt'>): Promise<Program> => {
    return Promise.resolve({ ...data, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() } as Program)
  },

  update: async (id: string, data: Partial<Program>): Promise<Program> => {
    return Promise.resolve({} as Program)
  },

  delete: async (id: string): Promise<void> => {
    return Promise.resolve()
  },
}