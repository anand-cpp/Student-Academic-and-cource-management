import { api } from './api'
import { PaginatedResponse } from '../types'
import type { Attendance, AttendanceStatus, AttendanceSession, FilterParams } from '../types'

export const attendanceApi = {
  getSessions: (params?: FilterParams): Promise<PaginatedResponse<AttendanceSession>> => {
    // The API only has attendance records, not sessions
    // For mock purposes, we'll return empty sessions
    return Promise.resolve({
      data: [],
      total: 0,
      page: params?.page || 1,
      pageSize: params?.pageSize || 10,
      totalPages: 0,
      hasNext: false,
      hasPrev: false,
    })
  },

  getSessionById: async (id: string): Promise<AttendanceSession | null> => {
    return Promise.resolve(null)
  },

  createSession: (data: Omit<AttendanceSession, 'id' | 'createdAt' | 'updatedAt'>): Promise<AttendanceSession> => {
    return Promise.resolve({ ...data, id: String(Date.now()), createdAt: new Date().toISOString(), updatedAt: new Date().toISOString() } as AttendanceSession)
  },

  updateSession: async (id: string, data: Partial<AttendanceSession>): Promise<AttendanceSession> => {
    return Promise.resolve({} as AttendanceSession)
  },

  deleteSession: async (id: string): Promise<void> => {
    return Promise.resolve()
  },

  getRecords: (params?: FilterParams): Promise<PaginatedResponse<Attendance>> => {
    return api.attendance.getAll(params)
  },

  createRecord: (data: Omit<Attendance, 'id' | 'createdAt' | 'updatedAt'>): Promise<Attendance> => {
    return api.attendance.mark(data)
  },

  createBulkRecords: (data: Omit<Attendance, 'id' | 'createdAt' | 'updatedAt'>[]): Promise<Attendance[]> => {
    return api.attendance.bulkMark(data)
  },

  updateRecord: async (id: string, data: Partial<Attendance>): Promise<Attendance> => {
    return Promise.resolve({} as Attendance)
  },

  getStudentSummary: async (studentId: string, courseId: string): Promise<any> => {
    return Promise.resolve({})
  },

  getCourseSummary: async (courseId: string): Promise<any> => {
    return Promise.resolve({})
  },
}