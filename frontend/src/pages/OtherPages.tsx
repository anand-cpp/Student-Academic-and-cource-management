import React from 'react'
import { Link } from 'react-router-dom'
import { Card, Button, LoadingSpinner } from '@/components/ui'
import { PlusIcon, MagnifyingGlassIcon, FunnelIcon, ArrowPathIcon } from '@heroicons/react/24/outline'

export function FacultyPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Faculty</h1>
          <p className="text-gray-500 mt-1">Manage faculty records and assignments</p>
        </div>
        <Button className="btn-primary"><PlusIcon className="w-5 h-5 mr-2" /> Add Faculty</Button>
      </div>
      <Card className="p-8 text-center">
        <p className="text-gray-500">Faculty management coming soon...</p>
      </Card>
    </div>
  )
}

export function EnrollmentsPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Enrollments</h1>
          <p className="text-gray-500 mt-1">Manage student course enrollments</p>
        </div>
        <Button className="btn-primary"><PlusIcon className="w-5 h-5 mr-2" /> Enroll Student</Button>
      </div>
      <Card className="p-8 text-center">
        <p className="text-gray-500">Enrollments management coming soon...</p>
      </Card>
    </div>
  )
}

export function AttendancePage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Attendance</h1>
          <p className="text-gray-500 mt-1">Record and manage student attendance</p>
        </div>
        <Button className="btn-primary"><PlusIcon className="w-5 h-5 mr-2" /> Record Attendance</Button>
      </div>
      <Card className="p-8 text-center">
        <p className="text-gray-500">Attendance management coming soon...</p>
      </Card>
    </div>
  )
}

export function AssessmentsPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Assessments</h1>
          <p className="text-gray-500 mt-1">Manage course assessments and exams</p>
        </div>
        <Button className="btn-primary"><PlusIcon className="w-5 h-5 mr-2" /> Create Assessment</Button>
      </div>
      <Card className="p-8 text-center">
        <p className="text-gray-500">Assessments management coming soon...</p>
      </Card>
    </div>
  )
}

export function MarksPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Marks & Grades</h1>
          <p className="text-gray-500 mt-1">Record and manage student marks</p>
        </div>
        <Button className="btn-primary"><PlusIcon className="w-5 h-5 mr-2" /> Enter Marks</Button>
      </div>
      <Card className="p-8 text-center">
        <p className="text-gray-500">Marks management coming soon...</p>
      </Card>
    </div>
  )
}

export function ReportsPage() {
  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-heading font-bold text-gray-900">Reports</h1>
          <p className="text-gray-500 mt-1">Generate academic reports and analytics</p>
        </div>
      </div>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {[
          { title: 'Student Academic Profile', desc: 'Complete academic profile for a student', icon: '👤' },
          { title: 'Course Enrollment Report', desc: 'Enrollment statistics for a course', icon: '📊' },
          { title: 'Faculty Course Assignment', desc: 'Courses assigned to faculty', icon: '👨‍🏫' },
          { title: 'Attendance Report', desc: 'Attendance statistics for a course', icon: '📅' },
          { title: 'Course Results Report', desc: 'Grade distribution for a course', icon: '📈' },
          { title: 'Student Performance', desc: 'Academic performance summary', icon: '🎓' },
        ].map((report) => (
          <div key={report.title} className="card-hover p-6 border border-gray-200 rounded-xl">
            <div className="text-3xl mb-3">{report.icon}</div>
            <h3 className="font-semibold text-gray-900">{report.title}</h3>
            <p className="text-gray-500 text-sm mt-1">{report.desc}</p>
            <button className="btn-primary mt-4 w-full text-sm">Generate</button>
          </div>
        ))}
      </div>
    </div>
  )
}

export function SettingsPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-heading font-bold text-gray-900">Settings</h1>
        <p className="text-gray-500 mt-1">Manage your account and preferences</p>
      </div>
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <div className="card p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Profile Settings</h2>
            <form className="space-y-4">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div><label className="label">Full Name</label><input type="text" className="input" defaultValue="John Doe" /></div>
                <div><label className="label">Email</label><input type="email" className="input" defaultValue="john@example.com" /></div>
              </div>
              <div><label className="label">Phone</label><input type="tel" className="input" placeholder="Phone number" /></div>
              <button type="submit" className="btn-primary">Save Changes</button>
            </form>
          </div>
          <div className="card p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Change Password</h2>
            <form className="space-y-4 max-w-md">
              <div><label className="label">Current Password</label><input type="password" className="input" /></div>
              <div><label className="label">New Password</label><input type="password" className="input" /></div>
              <div><label className="label">Confirm New Password</label><input type="password" className="input" /></div>
              <button type="submit" className="btn-primary">Update Password</button>
            </form>
          </div>
        </div>
        <div className="space-y-6">
          <div className="card p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Appearance</h2>
            <div className="space-y-3">
              <label className="flex items-center justify-between">
                <span className="font-medium">Dark Mode</span>
                <button className="relative inline-flex h-6 w-11 items-center rounded-full bg-gray-200 transition-colors" role="switch" aria-checked="false"><span className="translate-x-0.5 inline-block h-5 w-5 rounded-full bg-white shadow ring-0 transition-transform"></span></button>
              </label>
              <label className="flex items-center justify-between">
                <span className="font-medium">Compact Mode</span>
                <button className="relative inline-flex h-6 w-11 items-center rounded-full bg-gray-200 transition-colors" role="switch" aria-checked="false"><span className="translate-x-0.5 inline-block h-5 w-5 rounded-full bg-white shadow ring-0 transition-transform"></span></button>
              </label>
            </div>
          </div>
          <div className="card p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">Notifications</h2>
            <div className="space-y-3">
              <label className="flex items-center justify-between"><span className="font-medium">Email Notifications</span><button className="relative inline-flex h-6 w-11 items-center rounded-full bg-primary-600 transition-colors" role="switch" aria-checked="true"><span className="translate-x-5 inline-block h-5 w-5 rounded-full bg-white shadow ring-0 transition-transform"></span></button></label>
              <label className="flex items-center justify-between"><span className="font-medium">Push Notifications</span><button className="relative inline-flex h-6 w-11 items-center rounded-full bg-primary-600 transition-colors" role="switch" aria-checked="true"><span className="translate-x-5 inline-block h-5 w-5 rounded-full bg-white shadow ring-0 transition-transform"></span></button></label>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}