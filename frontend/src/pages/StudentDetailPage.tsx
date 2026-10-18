import React, { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { Card } from '../../components/ui/Card'
import { Badge } from '../../components/ui/Badge'
import { LoadingSpinner } from '../../components/ui/LoadingSpinner'
import { Button } from '../../components/ui/Button'
import { AcademicCapIcon, ClipboardDocumentListIcon, CalendarDaysIcon, ChartBarIcon, ArrowLeftIcon } from '@heroicons/react/24/outline'
import { useToast } from '../../components/ui/Toast'

interface Student {
  id: number
  studentId: string
  fullName: string
  email: string
  departmentName: string
  currentSemester: number
  status: string
  phone?: string
  dateOfBirth?: string
  gender?: string
  address?: string
}

interface Enrollment {
  id: number
  courseCode: string
  courseTitle: string
  credits: number
  status: string
  grade?: string
}

export function StudentDetailPage() {
  const { id } = useParams<{ id: string }>()
  const [student, setStudent] = useState<Student | null>(null)
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [loading, setLoading] = useState(true)
  const { toast } = useToast()

  useEffect(() => {
    const fetchData = async () => {
      if (!id) return
      try {
        // Mock data for now
        setStudent({
          id: 1,
          studentId: 'STU2023001',
          fullName: 'Alice Johnson',
          email: 'alice@example.com',
          departmentName: 'Computer Science',
          currentSemester: 3,
          status: 'ACTIVE',
          phone: '+1-555-1001',
          dateOfBirth: '2005-03-15',
          gender: 'FEMALE',
          address: '123 University Ave, Campus City',
        })
        setEnrollments([
          { id: 1, courseCode: 'CS201', courseTitle: 'Database Management', credits: 3, status: 'ENROLLED', grade: 'A' },
          { id: 2, courseCode: 'CS202', courseTitle: 'Operating Systems', credits: 3, status: 'ENROLLED', grade: 'B+' },
        ])
      } catch (err) {
        toast({ type: 'error', title: 'Error', message: 'Failed to load student details' })
      } finally {
        setLoading(false)
      }
    }
    fetchData()
  }, [id])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  if (!student) {
    return (
      <div className="text-center py-12">
        <p className="text-gray-500">Student not found</p>
        <Link to="/students" className="btn-primary mt-4 inline-block">Back to Students</Link>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Link to="/students" className="btn-secondary btn-sm">
            <ArrowLeftIcon className="w-5 h-5 mr-2" />
            Back
          </Link>
          <div>
            <h1 className="text-2xl font-heading font-bold text-gray-900">{student.fullName}</h1>
            <p className="text-gray-500">{student.studentId} • {student.departmentName} • Semester {student.currentSemester}</p>
          </div>
        </div>
        <div className="flex items-center gap-3">
          <Badge variant={getStatusVariant(student.status)}>{student.status}</Badge>
          <Link to={`/students/${student.id}/edit`} className="btn-secondary">Edit</Link>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <Card>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Enrollments</h2>
              <Link to={`/enrollments?studentId=${student.id}`} className="text-sm text-primary-600 hover:text-primary-700">View all</Link>
            </div>
            <Table
              data={enrollments}
              columns={[
                { key: 'courseCode', header: 'Course', render: (e) => <Link to={`/courses/${e.id}`} className="font-medium">{e.courseCode}</Link> },
                { key: 'courseTitle', header: 'Title', render: (e) => <span className="text-gray-600">{e.courseTitle}</span> },
                { key: 'credits', header: 'Credits' },
                { key: 'status', header: 'Status', render: (e) => <Badge variant={getEnrollmentStatusVariant(e.status)}>{e.status}</Badge> },
                { key: 'grade', header: 'Grade', render: (e) => e.grade ? <Badge variant="primary">{e.grade}</Badge> : <span className="text-gray-400">-</span> },
              ]}
              keyExtractor={(e) => e.id}
              hoverable
            />
          </Card>
        </div>

        <div className="space-y-6">
          <Card>
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Profile Information</h2>
            </div>
            <dl className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <dl>
                <dt className="text-sm text-gray-500">Student ID</dt>
                <dd className="font-mono font-medium">{student.studentId}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Email</dt>
                <dd className="font-medium">{student.email}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Phone</dt>
                <dd className="font-medium">{student.phone || '-'}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Department</dt>
                <dd className="font-medium">{student.departmentName}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Admission Year</dt>
                <dd className="font-medium">{student.id % 10 + 2020}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Current Semester</dt>
                <dd className="font-medium">Semester {student.currentSemester}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Date of Birth</dt>
                <dd className="font-medium">{student.dateOfBirth || '-'}</dd>
              </dl>
              <dl>
                <dt className="text-sm text-gray-500">Gender</dt>
                <dd className="font-medium capitalize">{student.gender?.toLowerCase() || '-'}</dd>
              </dl>
            </dl>
          </Card>
        </div>
      </div>
    </div>
  )
}

function getStatusVariant(status: string): 'success' | 'warning' | 'danger' | 'info' | 'gray' | 'primary' {
  switch (status) {
    case 'ACTIVE': return 'success'
    case 'INACTIVE': return 'gray'
    case 'GRADUATED': return 'primary'
    case 'SUSPENDED': return 'warning'
    case 'WITHDRAWN': return 'danger'
    default: return 'gray'
  }
}

function getEnrollmentStatusVariant(status: string): 'success' | 'warning' | 'danger' | 'info' | 'gray' | 'primary' {
  switch (status) {
    case 'ENROLLED': return 'success'
    case 'DROPPED': return 'warning'
    case 'COMPLETED': return 'primary'
    case 'WITHDRAWN': return 'warning'
    case 'FAILED': return 'danger'
    default: return 'gray'
  }
}

import { Table } from '../../components/ui/Table'