import React, { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { Card } from '../../components/ui/Card'
import { Button } from '../../components/ui/Button'
import { Input } from '../../components/ui/Input'
import { Select } from '../../components/ui/Select'
import { Table } from '../../components/ui/Table'
import { Badge } from '../../components/ui/Badge'
import { LoadingSpinner } from '../../components/ui/LoadingSpinner'
import { PlusIcon, MagnifyingGlassIcon, ArrowPathIcon } from '@heroicons/react/24/outline'
import { useToast } from '../../components/ui/Toast'

interface Course {
  id: number
  courseCode: string
  title: string
  credits: number
  departmentName: string
  semester: number
  facultyName?: string
  maxEnrollment: number
  status: string
}

export function CoursesPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [courses, setCourses] = useState<Course[]>([])
  const [loading, setLoading] = useState(false)
  const [totalPages, setTotalPages] = useState(0)
  const { toast } = useToast()

  const page = parseInt(searchParams.get('page') || '0')
  const size = parseInt(searchParams.get('size') || '10')
  const search = searchParams.get('search') || ''
  const status = searchParams.get('status') || ''
  const departmentId = searchParams.get('departmentId') || ''

  const fetchCourses = async () => {
    setLoading(true)
    try {
      // Mock data for now
      setCourses([
        { id: 1, courseCode: 'CS101', title: 'Introduction to Programming', credits: 4, departmentName: 'Computer Science', semester: 1, facultyName: 'Prof. John Smith', maxEnrollment: 60, status: 'ACTIVE' },
        { id: 2, courseCode: 'CS102', title: 'Data Structures', credits: 4, departmentName: 'Computer Science', semester: 2, facultyName: 'Prof. John Smith', maxEnrollment: 50, status: 'ACTIVE' },
        { id: 3, courseCode: 'CS201', title: 'Database Management', credits: 3, departmentName: 'Computer Science', semester: 3, facultyName: 'Dr. Emily Jones', maxEnrollment: 45, status: 'ACTIVE' },
      ])
      setTotalPages(1)
    } catch (err) {
      toast({ type: 'error', title: 'Error', message: 'Failed to load courses' })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchCourses()
  }, [page, size, search, status, departmentId])

  const page = parseInt(searchParams.get('page') || '0')
  const size = parseInt(searchParams.get('size') || '10')
  const search = searchParams.get('search') || ''
  const status = searchParams.get('status') || ''
  const departmentId = searchParams.get('departmentId') || ''

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setSearchParams({ ...Object.fromEntries(searchParams), search, page: 0 })
  }

  const handleFilterChange = (key: string, value: string) => {
    const newParams = new URLSearchParams(searchParams)
    if (value) newParams.set(key, value)
    else newParams.delete(key)
    newParams.set('page', '0')
    setSearchParams(newParams)
  }

  const clearFilters = () => setSearchParams({})

  const statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'ARCHIVED', label: 'Archived' },
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Courses</h1>
          <p className="text-gray-500 mt-1">Manage courses and curriculum</p>
        </div>
        <Link to="/courses/create" className="btn-primary">
          <PlusIcon className="w-5 h-5 mr-2" />
          Add Course
        </Link>
      </div>

      <Card className="p-4">
        <form onSubmit={(e) => { e.preventDefault(); setSearchParams({ ...Object.fromEntries(searchParams), search, page: 0 }) }} className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1 relative">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input type="search" placeholder="Search courses..." value={search} onChange={(e) => setSearch(e.target.value)} className="input pl-10" />
          </div>
          <Select
            options={[
              { value: '', label: 'All Statuses' },
              { value: 'ACTIVE', label: 'Active' },
              { value: 'INACTIVE', label: 'Inactive' },
              { value: 'ARCHIVED', label: 'Archived' },
            ]}
            value={status}
            onChange={(e) => handleFilterChange('status', e.target.value)}
            placeholder="All Statuses"
            className="w-40"
          />
          {(search || status) && <Button type="button" variant="ghost" onClick={clearFilters}><ArrowPathIcon className="w-5 h-5 mr-2" /> Clear</Button>}
        </form>
      </Card>

      <Card>
        {loading ? (
          <div className="py-12 text-center">
            <LoadingSpinner size="lg" />
          </div>
        ) : courses.length === 0 ? (
          <div className="py-12 text-center">
            <p className="text-gray-500">No courses found</p>
            <Link to="/courses/create" className="btn-primary mt-4 inline-block">
              <PlusIcon className="w-5 h-5 mr-2" />
              Add First Course
            </Link>
          </div>
        ) : (
          <>
            <Table
              data={courses}
              columns={[
                { key: 'courseCode', header: 'Code', render: (c) => <Link to={`/courses/${c.id}`} className="font-mono font-medium">{c.courseCode}</Link> },
                { key: 'title', header: 'Title', render: (c) => <Link to={`/courses/${c.id}`} className="font-medium text-gray-900 hover:text-primary-600">{c.title}</Link> },
                { key: 'credits', header: 'Credits' },
                { key: 'departmentName', header: 'Department', render: (c) => <Badge variant="info">{c.departmentName}</Badge> },
                { key: 'semester', header: 'Sem', render: (c) => <Badge variant="primary">Sem {c.semester}</Badge> },
                { key: 'facultyName', header: 'Faculty', render: (c) => <span>{c.facultyName || '-'}</span> },
                { key: 'maxEnrollment', header: 'Max Enroll.', render: (c) => <span>{c.maxEnrollment}</span> },
                { key: 'status', header: 'Status', render: (c) => <Badge variant={getCourseStatusVariant(c.status)}>{c.status}</Badge> },
                { key: 'actions', header: '', render: (c) => <div className="flex items-center gap-2"><Link to={`/courses/${c.id}`} className="btn-secondary btn-sm">View</Link><Link to={`/courses/${c.id}/edit`} className="btn-secondary btn-sm">Edit</Link></div> },
              ]}
              keyExtractor={(c) => c.id}
              hoverable
            />
            {totalPages > 1 && (
              <TablePagination
                currentPage={page + 1}
                totalPages={totalPages}
                onPageChange={(p) => setSearchParams({ ...Object.fromEntries(searchParams), page: (p - 1).toString() })}
              />
            )}
          </>
        )}
      </Card>
    </div>
  )
}

function getCourseStatusVariant(status: string): 'success' | 'warning' | 'danger' | 'info' | 'gray' | 'primary' {
  switch (status) {
    case 'ACTIVE': return 'success'
    case 'INACTIVE': return 'warning'
    case 'ARCHIVED': return 'gray'
    default: return 'gray'
  }
}

let search = ''
function setSearch(value: string) { search = value }
import { Link } from 'react-router-dom'
import { TablePagination } from '../../components/ui/Table'