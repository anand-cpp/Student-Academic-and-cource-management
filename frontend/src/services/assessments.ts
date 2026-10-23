import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Assessment, AssessmentType, FilterParams } from '../types'

export const assessmentsApi = {
  getAll: (params?: FilterParams): Promise<PaginatedResponse<Assessment>> => {
    return api.assessments.getAll(params)
  },

  getById: async (id: string): Promise<Assessment | null> => {
    // API doesn't have getById for assessments
    const response = await api.assessments.getAll({ pageSize: 1000 })
    return response.data.find(a => a.id === id) || null
  },

  getByCourse: async (courseId: string): Promise<Assessment[]> => {
    const response = await api.assessments.getAll({ pageSize: 1000 })
    return response.data.filter(a => a.courseId === courseId)
  },

  getPublishedByCourse: async (courseId: string): Promise<Assessment[]> => {
    const response = await api.assessments.getAll({ pageSize: 1000 })
    return response.data.filter(a => a.courseId === courseId && a.isPublished)
  },

  create: (data: Omit<Assessment, 'id' | 'createdAt' | 'updatedAt'>): Promise<Assessment> => {
    return api.assessments.create(data)
  },

  update: async (id: string, data: Partial<Assessment>): Promise<Assessment> => {
    // API doesn't have update for assessments
    const response = await api.assessments.getAll({ pageSize: 1000 })
    const assessment = response.data.find(a => a.id === id)
    if (assessment) {
      return { ...assessment, ...data, updatedAt: new Date().toISOString() }
    }
    throw new Error('Assessment not found')
  },

  delete: async (id: string): Promise<void> => {
    // API doesn't have delete for assessments
    return Promise.resolve()
  },

  publish: async (id: string): Promise<void> => {
    // Not directly supported, use update
    await assessmentsApi.update(id, { isPublished: true })
  },

  unpublish: async (id: string): Promise<void> => {
    // Not directly supported, use update
    await assessmentsApi.update(id, { isPublished: false })
  },

  getByCourseAndType: async (courseId: string, type: AssessmentType): Promise<Assessment[]> => {
    const response = await api.assessments.getAll({ pageSize: 1000 })
    return response.data.filter(a => a.courseId === courseId && a.type === type)
  },
}