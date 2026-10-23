import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Course, CourseStatus, FilterParams } from '../types'

export const coursesApi = {
  getAll: (params?: FilterParams): Promise<PaginatedResponse<Course>> => {
    return api.courses.getAll(params)
  },

  getById: (id: string): Promise<Course | null> => {
    return api.courses.getById(id)
  },

  getByCode: async (courseCode: string): Promise<Course | null> => {
    const response = await api.courses.getAll({ pageSize: 1000 })
    return response.data.find(c => c.code === courseCode) || null
  },

  create: (data: Omit<Course, 'id' | 'createdAt' | 'updatedAt'>): Promise<Course> => {
    return api.courses.create(data)
  },

  update: (id: string, data: Partial<Course>): Promise<Course> => {
    return api.courses.update(id, data)
  },

  delete: (id: string): Promise<void> => {
    return api.courses.delete(id)
  },

  changeStatus: (id: string, status: CourseStatus): Promise<Course> => {
    return api.courses.update(id, { isActive: status === 'ACTIVE' })
  },

  getActiveByFaculty: async (facultyId: string): Promise<Course[]> => {
    const response = await api.courses.getAll({ pageSize: 1000 })
    // Course doesn't have facultyId directly, need to check enrollments
    // For now, return all active courses
    return response.data.filter(c => c.isActive)
  },

  getByDepartment: async (departmentId: string): Promise<Course[]> => {
    const response = await api.courses.getAll({ pageSize: 1000 })
    return response.data.filter(c => c.departmentId === departmentId)
  },
}