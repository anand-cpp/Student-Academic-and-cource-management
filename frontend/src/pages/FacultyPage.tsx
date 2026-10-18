import React, { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { facultyApi } from '@services/faculty'
import { departmentsApi } from '@services/departments'
import { Card, Button, Input, Select, Table, Badge, LoadingSpinner, useToast, TablePagination } from '@/components/ui'
import { PaginatedResponse, Faculty, FacultyStatus, Department } from '@/types'
import { PlusIcon, MagnifyingGlassIcon, FunnelIcon, ArrowPathIcon } from '@heroicons/react/24/outline'

export function FacultyPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [faculty, setFaculty] = useState<Faculty[]>([])
  const [departments, setDepartments] = useState<Department[]>([])
  const [loading, setLoading] = useState(false)
  const [totalPages, setTotalPages] = useState(0)
  const { toast } = useToast()

  const page = parseInt(searchParams.get('page') || '0')
  const size = parseInt(searchParams.get('size') || '10')
  const search = searchParams.get('search') || ''
  const status = searchParams.get('status') || ''
  const departmentId = searchParams.get('departmentId') || ''
  const [searchValue, setSearchValue] = useState(search)

  const fetchFaculty = async () => {
    setLoading(true)
    try {
      const params: any = { page, size }
      if (search) params.search = search
      if (status) params.status = status
      if (departmentId) params.departmentId = departmentId

      const response = await facultyApi.getAll(params)
      setFaculty(response.data.content)
      setTotalPages(response.data.totalPages)
    } catch (err) {
      toast({ type: 'error', title: 'Error', message: 'Failed to load faculty' })
    } finally {
      setLoading(false)
    }
  }

  const fetchDepartments = async () => {
    try {
      const response = await departmentsApi.getActive()
      setDepartments(response.data)
    } catch (err) {}
  }

  useEffect(() => {
    fetchFaculty()
    fetchDepartments()
  }, [page, size, search, status, departmentId])

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
    { value: 'ON_LEAVE', label: 'On Leave' },
    { value: 'RETIRED', label: 'Retired' },
  ]

  const designationOptions = [
    { value: '', label: 'All Designations' },
    { value: 'PROFESSOR', label: 'Professor' },
    { value: 'ASSOCIATE_PROFESSOR', label: 'Associate Professor' },
    { value: 'ASSISTANT_PROFESSOR', label: 'Assistant Professor' },
    { value: 'LECTURER', label: 'Lecturer' },
    { value: 'ADJUNCT', label: 'Adjunct' },
    { value: 'VISITING', label: 'Visiting' },
  ]

  const departmentOptions = [
    { value: '', label: 'All Departments' },
    ...departments.map(d => ({ value: d.id.toString(), label: d.name })),
  ]

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Faculty</h1>
          <p className="text-gray-500 mt-1">Manage faculty records and assignments</p>
        </div>
        <Link to="/faculty/create" className="btn-primary">
          <PlusIcon className="w-5 h-5 mr-2" />
          Add Faculty
        </Link>
      </div>

      <Card className="p-4">
        <form onSubmit={(e) => { e.preventDefault(); const params = new URLSearchParams(searchParams); params.set('search', searchValue); params.set('page', '0'); setSearchParams(params) }} className="flex flex-col sm:flex-row gap-4">
          <div className="flex-1 relative">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input type="search" placeholder="Search faculty..." value={searchValue} onChange={(e) => setSearchValue(e.target.value)} className="input pl-10" />
          </div>
          <Select options={statusOptions} value={status} onChange={(e) => handleFilterChange('status', e.target.value)} placeholder="All Statuses" className="w-40" />
          <Select options={designationOptions} value={searchParams.get('designation') || ''} onChange={(e) => handleFilterChange('designation', e.target.value)} placeholder="All Designations" className="w-48" />
          <Select options={departmentOptions} value={departmentId} onChange={(e) => handleFilterChange('departmentId', e.target.value)} placeholder="All Departments" className="w-48" />
          {(search || status || departmentId) && <Button type="button" variant="ghost" onClick={() => setSearchParams({})}><ArrowPathIcon className="w-5 h-5 mr-2" /> Clear</Button>}
        </form>
      </Card>

      <Card>
        {loading ? <div className="py-12 text-center"><LoadingSpinner size="lg" /></div> : (
          <>
            <Table
              data={faculty}
              columns={[
                { key: 'facultyId', header: 'Faculty ID', render: (f) => <span className="font-mono text-sm">{f.facultyId}</span> },
                { key: 'fullName', header: 'Name', render: (f) => <Link to={`/faculty/${f.id}`} className="font-medium text-gray-900 hover:text-primary-600">{f.fullName}</Link> },
                { key: 'email', header: 'Email', render: (f) => <span className="text-gray-600">{f.email}</span> },
                { key: 'departmentName', header: 'Department', render: (f) => <Badge variant="info">{f.departmentName}</Badge> },
                { key: 'designation', header: 'Designation', render: (f) => <Badge variant="primary">{f.designation}</Badge> },
                { key: 'status', header: 'Status', render: (f) => <Badge variant={getFacultyStatusVariant(f.status)}>{f.status}</Badge> },
                { key: 'actions', header: '', render: (f) => <div className="flex items-center gap-2"><Link to={`/faculty/${f.id}`} className="btn-secondary btn-sm">View</Link><Link to={`/faculty/${f.id}/edit`} className="btn-secondary btn-sm">Edit</Link></div> },
              ]}
              keyExtractor={(f) => f.id}
              hoverable
            />
            {totalPages > 1 && <TablePagination currentPage={page + 1} totalPages={totalPages} onPageChange={(p) => setSearchParams({ ...Object.fromEntries(searchParams), page: (p - 1).toString() })} />}
          </>
        )}
      </Card>
    </div>
  )
}

function getFacultyStatusVariant(status: FacultyStatus): 'success' | 'warning' | 'danger' | 'info' | 'gray' | 'primary' {
  switch (status) {
    case 'ACTIVE': return 'success'
    case 'INACTIVE': return 'gray'
    case 'ON_LEAVE': return 'warning'
    case 'RETIRED': return 'gray'
    default: return 'gray'
  }
}

// Duplicate code removed