import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Enrollment, EnrollmentStatus, FilterParams } from '../types'

export const enrollmentsApi = {
  getAll: (params?: FilterParams): Promise<PaginatedResponse<Enrollment>> => {
    return api.enrollments.getAll(params)
  },

  getById: async (id: string): Promise<Enrollment | null> => {
    const response = await api.enrollments.getAll({ pageSize: 1000 })
    return response.data.find(e => e.id === id) || null
  },

  create: (data: Omit<Enrollment, 'id' | 'createdAt' | 'updatedAt'>): Promise<Enrollment> => {
    return api.enrollments.create(data)
  },

  update: async (id: string, data: Partial<Enrollment>): Promise<Enrollment> => {
    // API doesn't have update, so we need to create a new one or use mock
    // For now, return a mock update
    const response = await api.enrollments.getAll({ pageSize: 1000 })
    const enrollment = response.data.find(e => e.id === id)
    if (enrollment) {
      return { ...enrollment, ...data, updatedAt: new Date().toISOString() }
    }
    throw new Error('Enrollment not found')
  },

  delete: async (id: string): Promise<void> => {
    // API doesn't have delete
    // For now, just resolve
    return Promise.resolve()
  },

  changeStatus: async (id: string, status: EnrollmentStatus): Promise<Enrollment> => {
    const response = await api.enrollments.getAll({ pageSize: 1000 })
    const enrollment = response.data.find(e => e.id === id)
    if (enrollment) {
      return { ...enrollment, status, updatedAt: new Date().toISOString() }
    }
    throw new Error('Enrollment not found')
  },

  getActiveByStudent: async (studentId: string): Promise<Enrollment[]> => {
    const response = await api.enrollments.getAll({ pageSize: 1000 })
    return response.data.filter(e => e.studentId === studentId && e.status === 'ENROLLED')
  },

  getActiveByCourse: async (courseId: string): Promise<Enrollment[]> => {
    const response = await api.enrollments.getAll({ pageSize: 1000 })
    return response.data.filter(e => e.courseId === courseId && e.status === 'ENROLLED')
  },
}