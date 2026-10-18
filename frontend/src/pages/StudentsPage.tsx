import React, { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { Card, Button, Input, Select, Table, Badge, LoadingSpinner, useToast, TablePagination } from '@/components/ui'
import { PlusIcon, MagnifyingGlassIcon, ArrowPathIcon } from '@heroicons/react/24/outline'

interface Student {
  id: number
  studentId: string
  fullName: string
  email: string
  departmentName: string
  currentSemester: number
  status: string
}

export function StudentsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [students, setStudents] = useState<Student[]>([])
  const [loading, setLoading] = useState(false)
  const [totalPages, setTotalPages] = useState(0)
  const { toast } = useToast()

  const page = parseInt(searchParams.get('page') || '0')
  const size = parseInt(searchParams.get('size') || '10')
  const search = searchParams.get('search') || ''
  const status = searchParams.get('status') || ''
  const departmentId = searchParams.get('departmentId') || ''

  const fetchStudents = async () => {
    setLoading(true)
    try {
      // Mock data for now
      setStudents([
        { id: 1, studentId: 'STU2023001', fullName: 'Alice Johnson', email: 'alice@example.com', departmentName: 'Computer Science', currentSemester: 3, status: 'ACTIVE' },
        { id: 2, studentId: 'STU2023002', fullName: 'Bob Williams', email: 'bob@example.com', departmentName: 'Computer Science', currentSemester: 3, status: 'ACTIVE' },
        { id: 3, studentId: 'STU2023003', fullName: 'Carol Davis', email: 'carol@example.com', departmentName: 'Computer Science', currentSemester: 3, status: 'ACTIVE' },
      ])
      setTotalPages(1)
    } catch (err) {
      toast({ type: 'error', title: 'Error', message: 'Failed to load students' })
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchStudents()
  }, [page, size, search, status, departmentId])

  const [searchValue, setSearchValue] = useState(search)

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    const params = new URLSearchParams(searchParams)
    params.set('search', searchValue)
    params.set('page', '0')
    setSearchParams(params)
  }

  const handleFilterChange = (key: string, value: string) => {
    const params = new URLSearchParams(searchParams)
    if (value) params.set(key, value)
    else params.delete(key)
    params.set('page', '0')
    setSearchParams(params)
  }

  const clearFilters = () => setSearchParams({})

  const statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'INACTIVE', label: 'Inactive' },
    { value: 'GRADUATED', label: 'Graduated' },
    { value: 'SUSPENDED', label: 'Suspended' },
    { value: 'WITHDRAWN', label: 'Withdrawn' },
  ]

  const departmentOptions = [
    { value: '', label: 'All Departments' },
    { value: '1', label: 'Computer Science' },
    { value: '2', label: 'Electrical Engineering' },
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Students</h1>
          <p className="text-gray-500 mt-1">Manage student records and academic information</p>
        </div>
        <Link to="/students/create" className="btn-primary">
          <PlusIcon className="w-5 h-5 mr-2" />
          Add Student
        </Link>
      </div>

      <Card className="p-4">
        <form onSubmit={(e) => { e.preventDefault(); const params = new URLSearchParams(searchParams); params.set('search', searchValue); params.set('page', '0'); setSearchParams(params) }} className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1 relative">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="search"
              placeholder="Search students..."
              value={searchValue}
              onChange={(e) => setSearchValue(e.target.value)}
              className="input pl-10"
            />
          </div>
          <Select
            options={statusOptions}
            value={status}
            onChange={(e) => handleFilterChange('status', e.target.value)}
            placeholder="All Statuses"
            className="w-40"
          />
          <Select
            options={departmentOptions}
            value={departmentId}
            onChange={(e) => handleFilterChange('departmentId', e.target.value)}
            placeholder="All Departments"
            className="w-48"
          />
          {(search || status || departmentId) ? (
            <Button type="button" variant="ghost" onClick={clearFilters}>
              <ArrowPathIcon className="w-5 h-5 mr-2" />
              Clear
            </Button>
          ) : null}
        </form>
      </Card>

      <Card>
        {loading ? (
          <div className="py-12 text-center">
            <LoadingSpinner size="lg" />
          </div>
        ) : students.length === 0 ? (
          <div className="py-12 text-center">
            <p className="text-gray-500">No students found</p>
            <Link to="/students/create" className="btn-primary mt-4 inline-block">
              <PlusIcon className="w-5 h-5 mr-2" />
              Add First Student
            </Link>
          </div>
        ) : (
          <>
            <Table
              data={students}
              columns={[
                { key: 'studentId', header: 'Student ID', render: (s) => <span className="font-mono text-sm">{s.studentId}</span> },
                { key: 'fullName', header: 'Name', render: (s) => <Link to={`/students/${s.id}`} className="font-medium text-gray-900 hover:text-primary-600">{s.fullName}</Link> },
                { key: 'email', header: 'Email', render: (s) => <span className="text-gray-600">{s.email}</span> },
                { key: 'departmentName', header: 'Department', render: (s) => <Badge variant="info">{s.departmentName}</Badge> },
                { key: 'currentSemester', header: 'Semester', render: (s) => <span>Sem {s.currentSemester}</span> },
                { key: 'status', header: 'Status', render: (s) => <Badge variant={getStatusVariant(s.status)}>{s.status}</Badge> },
                { key: 'actions', header: '', render: (s) => (
                  <div className="flex items-center gap-2">
                    <Link to={`/students/${s.id}`} className="btn-secondary btn-sm">View</Link>
                    <Link to={`/students/${s.id}/edit`} className="btn-secondary btn-sm">Edit</Link>
                  </div>
                ) },
              ]}
              keyExtractor={(s) => s.id}
              rowClassName={(s) => s.status === 'WITHDRAWN' ? 'opacity-50' : ''}
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

// Duplicate code removed