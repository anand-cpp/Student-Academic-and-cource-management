import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Section, FilterParams } from '../types'

export const sectionsApi = {
  getAll: async (params?: FilterParams): Promise<PaginatedResponse<Section>> => {
    const data = await api.sections.getAll()
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

  getById: async (id: string): Promise<Section | null> => {
    const data = await api.sections.getAll()
    return data.find(s => s.id === id) || null
  },

  getByBatch: async (batchId: string): Promise<Section[]> => {
    const data = await api.sections.getAll()
    return data.filter(s => s.batchId === batchId)
  },

  create: async (data: Omit<Section, 'id' | 'createdAt' | 'updatedAt'>): Promise<Section> => {
    return Promise.resolve({ ...data, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() } as Section)
  },

  update: async (id: string, data: Partial<Section>): Promise<Section> => {
    return Promise.resolve({} as Section)
  },

  delete: async (id: string): Promise<void> => {
    return Promise.resolve()
  },
}