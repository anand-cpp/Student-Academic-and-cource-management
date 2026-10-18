import React, { useState } from 'react'
import { Link, useLocation, NavLink } from 'react-router-dom'
import {
  HomeIcon,
  AcademicCapIcon,
  UsersIcon,
  UserGroupIcon,
  BookOpenIcon,
  ClipboardDocumentListIcon,
  CalendarDaysIcon,
  CheckCircleIcon,
  ChartBarIcon,
  DocumentTextIcon,
  Cog6ToothIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
  Bars3Icon,
  XMarkIcon,
} from '@heroicons/react/24/outline'
import { useAuth } from '../../contexts/AuthContext'

const navigation = [
  { name: 'Dashboard', href: '/', icon: HomeIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT'] },
  { name: 'Students', href: '/students', icon: AcademicCapIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
  { name: 'Faculty', href: '/faculty', icon: UserGroupIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
  { name: 'Courses', href: '/courses', icon: BookOpenIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT'] },
  { name: 'Enrollments', href: '/enrollments', icon: ClipboardDocumentListIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT'] },
  { name: 'Attendance', href: '/attendance', icon: CalendarDaysIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
  { name: 'Assessments', href: '/assessments', icon: CheckCircleIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
  { name: 'Marks', href: '/marks', icon: ChartBarIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT'] },
  { name: 'Reports', href: '/reports', icon: DocumentTextIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
  { name: 'Settings', href: '/settings', icon: Cog6ToothIcon, roles: ['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR', 'STUDENT'] },
]

export function Sidebar() {
  const [collapsed, setCollapsed] = useState(false)
  const location = useLocation()
  const { user, isAuthenticated } = useAuth()

  if (!isAuthenticated) return null

  const filteredNav = navigation.filter((item) =>
    item.roles.includes(user?.role || '')
  )

  return (
    <>
      <button
        type="button"
        className="lg:hidden fixed top-4 left-4 z-50 btn-primary p-2 rounded-lg shadow-lg"
        onClick={() => setCollapsed(false)}
        aria-label="Open sidebar"
      >
        <Bars3Icon className="w-6 h-6" />
      </button>

      <aside
        className={`fixed inset-y-0 left-0 z-40 bg-white border-r border-gray-200 transition-all duration-300 ease-in-out lg:translate-x-0 ${
          collapsed ? '-translate-x-full' : 'translate-x-0'
        }`}
        style={{ width: collapsed ? '0' : '16rem' }}
        aria-label="Sidebar navigation"
      >
        <div className="flex flex-col h-full">
          <div className="flex items-center justify-between h-16 px-4 border-b border-gray-100">
            {!collapsed && (
              <Link to="/" className="flex items-center gap-2 font-heading font-bold text-lg text-primary-600">
                <svg className="w-8 h-8 text-primary-600" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M12 3L1 9l11 6 9-4.91V17h2V9L12 3z"/>
                </svg>
                <span>Academia</span>
              </Link>
            )}
            <button
              type="button"
              onClick={() => setCollapsed(!collapsed)}
              className={`p-2 rounded-lg text-gray-500 hover:text-gray-700 hover:bg-gray-100 transition-colors ${
                collapsed ? 'ml-auto' : 'ml-2'
              }`}
              aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            >
              {collapsed ? <ChevronRightIcon className="w-5 h-5" /> : <ChevronLeftIcon className="w-5 h-5" />}
            </button>
          </div>

          <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto" aria-label="Main navigation">
            {filteredNav.map((item) => (
              <NavLink
                key={item.name}
                to={item.href}
                className={({ isActive }) => `
                  flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors
                  ${isActive
                    ? 'bg-primary-50 text-primary-700'
                    : 'text-gray-600 hover:bg-gray-50 hover:text-gray-900'
                  }
                `}
                title={collapsed ? item.name : undefined}
              >
                <item.icon className="w-5 h-5 flex-shrink-0" aria-hidden="true" />
                {!collapsed && <span>{item.name}</span>}
              </NavLink>
            ))}
          </nav>

          {!collapsed && (
            <div className="p-4 border-t border-gray-100">
              <div className="flex items-center gap-3 px-3 py-2">
                <div className="w-10 h-10 rounded-full bg-primary-100 flex items-center justify-center text-primary-700 font-medium">
                  {user?.fullName?.charAt(0) || 'U'}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{user?.fullName}</p>
                  <p className="text-xs text-gray-500 capitalize">{user?.role?.toLowerCase()}</p>
                </div>
              </div>
            </div>
          )}
        </div>
      </aside>

      {collapsed && (
        <div className="fixed inset-0 z-30 bg-black/50 lg:hidden" onClick={() => setCollapsed(true)} aria-hidden="true" />
      )}
    </>
  )
}