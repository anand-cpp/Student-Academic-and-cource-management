import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import { Card } from '../../components/ui/Card'
import { Button } from '../../components/ui/Button'
import { Badge } from '../../components/ui/Badge'
import { LoadingSpinner } from '../../components/ui/LoadingSpinner'
import {
  AcademicCapIcon,
  UsersIcon,
  BookOpenIcon,
  ClipboardDocumentListIcon,
  CalendarDaysIcon,
  ChartBarIcon,
  ArrowTrendingUpIcon,
  ArrowTrendingDownIcon,
  ExclamationTriangleIcon,
} from '@heroicons/react/24/outline'

export function DashboardPage() {
  const { user } = useAuth()
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        // Mock data for now
        setStats({
          totalStudents: 150,
          totalFaculty: 25,
          totalCourses: 45,
          activeEnrollments: 1200,
          averageAttendancePercentage: 87.5,
          averagePerformancePercentage: 78.2,
          pendingApprovals: 3,
        })
      } catch (err) {
        console.error('Failed to load dashboard statistics')
      } finally {
        setLoading(false)
      }
    }

    fetchStats()
  }, [])

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    )
  }

  const statCards = [
    {
      title: 'Total Students',
      value: stats?.totalStudents || 0,
      icon: AcademicCapIcon,
      color: 'bg-blue-500',
      bgColor: 'bg-blue-50',
      link: '/students',
    },
    {
      title: 'Total Faculty',
      value: stats?.totalFaculty || 0,
      icon: UsersIcon,
      color: 'bg-green-500',
      bgColor: 'bg-green-50',
      link: '/faculty',
    },
    {
      title: 'Total Courses',
      value: stats?.totalCourses || 0,
      icon: BookOpenIcon,
      color: 'bg-purple-500',
      bgColor: 'bg-purple-50',
      link: '/courses',
    },
    {
      title: 'Active Enrollments',
      value: stats?.activeEnrollments || 0,
      icon: ClipboardDocumentListIcon,
      color: 'bg-orange-500',
      bgColor: 'bg-orange-50',
      link: '/enrollments',
    },
  ]

  const metricCards = [
    {
      title: 'Avg. Attendance',
      value: `${stats?.averageAttendancePercentage || 0}%`,
      icon: CalendarDaysIcon,
      trend: 2.3,
      trendUp: true,
    },
    {
      title: 'Avg. Performance',
      value: `${stats?.averagePerformancePercentage || 0}%`,
      icon: ChartBarIcon,
      trend: 1.8,
      trendUp: true,
    },
    {
      title: 'Pending Approvals',
      value: stats?.pendingApprovals || 0,
      icon: ExclamationTriangleIcon,
      trend: 0,
      trendUp: true,
    },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Dashboard</h1>
          <p className="text-gray-500 mt-1">Welcome back, {user?.fullName}! Here's what's happening today.</p>
        </div>
        <div className="flex items-center gap-3">
          <Link to="/students" className="btn-secondary btn-sm">
            <span className="mr-2">+</span> Add Student
          </Link>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6">
        {statCards.map((card) => (
          <Link key={card.title} to={card.link} className="card-hover">
            <Card className="flex items-center gap-4 p-5">
              <div className={`${card.bgColor} ${card.color} p-3 rounded-xl`}>
                <card.icon className="w-6 h-6" />
              </div>
              <div>
                <p className="text-sm text-gray-500">{card.title}</p>
                <p className="text-2xl font-bold text-gray-900">{card.value.toLocaleString()}</p>
              </div>
            </Card>
          </Link>
        ))}

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-6">
          {metricCards.map((card) => (
            <Card key={card.title} className="p-5">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-gray-500">{card.title}</p>
                  <p className="text-2xl font-bold text-gray-900">{card.value}</p>
                </div>
                <div className={`${card.trendUp ? 'text-green-600' : 'text-red-600'} flex items-center gap-1`}>
                  <card.icon className="w-5 h-5" />
                  <span className="text-sm font-medium">{card.trend}%</span>
                  <span className="text-xs text-gray-500">vs last month</span>
                </div>
              </div>
            </Card>
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 sm:gap-6">
          <Card className="p-5">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Quick Actions</h2>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
              <Link to="/students" className="card-hover p-4 text-center">
                <span className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-2 text-blue-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293-.293a1 1 0 00-1.414-1.414l-7-7z" /></svg>
                </span>
                <p className="text-sm font-medium text-gray-700">Add Student</p>
              </Link>
              <Link to="/courses" className="card-hover p-4 text-center">
                <span className="w-8 h-8 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-2 text-green-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M9 2a1 1 0 000 2h2a1 1 0 001-1V1a1 1 0 00-1-1H9z" /><path fillRule="evenodd" d="M4 5a2 2 0 012-2h8a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 011-1h2a1 1 0 100-2H7a1 1 0 100 2h2a1 1 0 011 1v6a1 1 0 01-1 1H7a1 1 0 01-1-1V5z" clipRule="evenodd" /></svg>
                </span>
                <p className="text-sm font-medium text-gray-700">Add Course</p>
              </Link>
              <Link to="/enrollments" className="card-hover p-4 text-center">
                <span className="w-8 h-8 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-2 text-purple-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M9 2a1 1 0 000 2h2a1 1 0 001-1V1a1 1 0 00-1-1H9z" /><path fillRule="evenodd" d="M4 5a2 2 0 012-2h8a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 011-1h2a1 1 0 100-2H7a1 1 0 100 2h2a1 1 0 011 1v6a1 1 0 01-1 1H7a1 1 0 01-1-1V5z" clipRule="evenodd" /></svg>
                </span>
                <p className="text-sm font-medium text-gray-700">Enroll Student</p>
              </Link>
              <Link to="/attendance" className="card-hover p-4 text-center">
                <span className="w-8 h-8 bg-orange-100 rounded-full flex items-center justify-center mx-auto mb-2 text-orange-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293-.293a1 1 0 00-1.414-1.414l-7-7z" /></svg>
                </span>
                <p className="text-sm font-medium text-gray-700">Record Attendance</p>
              </Link>
            </div>
          </Card>

          <Card className="p-5">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-lg font-semibold text-gray-900">Recent Activity</h2>
              <Link to="/reports" className="text-sm text-primary-600 hover:text-primary-700">View all</Link>
            </div>
            <div className="space-y-3">
              <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center text-green-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293-.293a1 1 0 00-1.414-1.414l-7-7z" /></svg>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900">New student enrolled</p>
                  <p className="text-sm text-gray-500">John Doe enrolled in CS101</p>
                </div>
                <span className="text-xs text-gray-500">2 hours ago</span>
              </div>
              <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                <div className="w-10 h-10 rounded-full bg-blue-100 flex items-center justify-center text-blue-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M9 2a1 1 0 000 2h2a1 1 0 001-1V1a1 1 0 00-1-1H9z" /><path fillRule="evenodd" d="M4 5a2 2 0 012-2h8a2 2 0 012 2v10a2 2 0 01-2 2H6a2 2 0 01-2-2V5zm3 4a1 1 0 011-1h2a1 1 0 100-2H7a1 1 0 100 2h2a1 1 0 011 1v6a1 1 0 01-1 1H7a1 1 0 01-1-1V5z" clipRule="evenodd" /></svg>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900">New course created</p>
                  <p className="text-sm text-gray-500">CS301 - Machine Learning</p>
                </div>
                <span className="text-xs text-gray-500">5 hours ago</span>
              </div>
              <div className="flex items-center gap-3 p-3 bg-gray-50 rounded-lg">
                <div className="w-10 h-10 rounded-full bg-yellow-100 flex items-center justify-center text-yellow-600">
                  <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20"><path d="M10.707 2.293a1 1 0 00-1.414 0l-7 7a1 1 0 001.414 1.414L4 10.414V17a1 1 0 001 1h2a1 1 0 001-1v-2a1 1 0 011-1h2a1 1 0 011 1v2a1 1 0 001 1h2a1 1 0 001-1v-6.586l.293-.293a1 1 0 00-1.414-1.414l-7-7z" /></svg>
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900">New enrollment</p>
                  <p className="text-sm text-gray-500">Jane Smith enrolled in CS201</p>
                </div>
                <span className="text-xs text-gray-500">1 day ago</span>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}

interface DashboardStats {
  totalStudents: number
  totalFaculty: number
  totalCourses: number
  activeEnrollments: number
  averageAttendancePercentage: number
  averagePerformancePercentage: number
  pendingApprovals: number
}