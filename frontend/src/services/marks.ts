import { api } from './api'
import { PaginatedResponse } from '../types'
import type { FilterParams } from '../types'

export const marksApi = {
  getAll: (params?: FilterParams): Promise<PaginatedResponse<any>> => {
    return api.marks.getAll(params)
  },

  getById: async (id: string): Promise<any> => {
    const response = await api.marks.getAll({ pageSize: 1000 })
    return response.data.find(m => m.id === id) || null
  },

  getByAssessment: async (assessmentId: string): Promise<any[]> => {
    const response = await api.marks.getAll({ pageSize: 1000 })
    return response.data.filter(m => m.assessmentId === assessmentId)
  },

  getByEnrollment: async (enrollmentId: string): Promise<any[]> => {
    const response = await api.marks.getAll({ pageSize: 1000 })
    return response.data.filter(m => m.enrollmentId === enrollmentId)
  },

  getByStudentAndCourse: async (studentId: string, courseId: string): Promise<any[]> => {
    // Need to find enrollments first
    return Promise.resolve([])
  },

  create: (data: any): Promise<any> => {
    return api.marks.create(data)
  },

  createBulk: async (data: any[]): Promise<any[]> => {
    // Not supported in mock API
    return Promise.resolve([])
  },

  update: async (id: string, data: any): Promise<any> => {
    const response = await api.marks.getAll({ pageSize: 1000 })
    const mark = response.data.find(m => m.id === id)
    if (mark) {
      return { ...mark, ...data, updatedAt: new Date().toISOString() }
    }
    throw new Error('Mark not found')
  },

  finalize: async (id: string): Promise<void> => {
    // Not directly supported
    return Promise.resolve()
  },

  unfinalize: async (id: string): Promise<void> => {
    // Not directly supported
    return Promise.resolve()
  },

  getCourseResult: async (studentId: string, courseId: string): Promise<any> => {
    return Promise.resolve({})
  },

  delete: async (id: string): Promise<void> => {
    // Not implemented in mock API
    return Promise.resolve()
  },
}