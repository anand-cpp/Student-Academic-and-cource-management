import { api } from './api'
import { PaginatedResponse } from '../types'

export const departmentsApi = {
  getAll: (params?: {
    page?: number
    size?: number
    search?: string
  }) => api.get<PaginatedResponse<any>>('/departments', { params }),

  getById: (id: number) => api.get<any>(`/departments/${id}`),

  getByCode: (code: string) => api.get<any>(`/departments/code/${code}`),

  getActive: () => api.get<any[]>('/departments/active'),

  create: (data: any) => api.post<any>('/departments', data),

  update: (id: number, data: any) => api.put<any>(`/departments/${id}`, data),

  delete: (id: number) => api.delete(`/departments/${id}`),
}