import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Department, FilterParams } from '../types'

export const departmentsApi = {
  getAll: async (params?: FilterParams): Promise<PaginatedResponse<Department>> => {
    const data = await api.departments.getAll()
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

  getById: async (id: string): Promise<Department | null> => {
    const data = await api.departments.getAll()
    return data.find(d => d.id === id) || null
  },

  getByCode: async (code: string): Promise<Department | null> => {
    const data = await api.departments.getAll()
    return data.find(d => d.code === code) || null
  },

  create: async (data: Omit<Department, 'id' | 'createdAt' | 'updatedAt'>): Promise<Department> => {
    // Not implemented in mock API
    return Promise.resolve({ ...data, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() } as Department)
  },

  update: async (id: string, data: Partial<Department>): Promise<Department> => {
    // Not implemented in mock API
    return Promise.resolve({} as Department)
  },

  delete: async (id: string): Promise<void> => {
    // Not implemented in mock API
    return Promise.resolve()
  },

  getHead: async (departmentId: string): Promise<any> => {
    // Not implemented in mock API
    return Promise.resolve(null)
  },
}