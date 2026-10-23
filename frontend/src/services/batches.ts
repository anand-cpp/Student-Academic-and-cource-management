import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Batch, FilterParams } from '../types'

export const batchesApi = {
  getAll: async (params?: FilterParams): Promise<PaginatedResponse<Batch>> => {
    const data = await api.batches.getAll()
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

  getById: async (id: string): Promise<Batch | null> => {
    const data = await api.batches.getAll()
    return data.find(b => b.id === id) || null
  },

  getByProgram: async (programId: string): Promise<Batch[]> => {
    const data = await api.batches.getAll()
    return data.filter(b => b.programId === programId)
  },

  create: async (data: Omit<Batch, 'id' | 'createdAt' | 'updatedAt'>): Promise<Batch> => {
    return Promise.resolve({ ...data, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() } as Batch)
  },

  update: async (id: string, data: Partial<Batch>): Promise<Batch> => {
    return Promise.resolve({} as Batch)
  },

  delete: async (id: string): Promise<void> => {
    return Promise.resolve()
  },
}