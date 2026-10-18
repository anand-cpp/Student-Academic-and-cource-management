import React from 'react'

interface BadgeProps {
  children: React.ReactNode
  variant?: 'success' | 'warning' | 'danger' | 'info' | 'gray' | 'primary'
  size?: 'sm' | 'md'
  className?: string
  dot?: boolean
}

export function Badge({
  children,
  variant = 'gray',
  size = 'md',
  className = '',
  dot = false,
}: BadgeProps) {
  const variantClasses = {
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    danger: 'bg-red-100 text-red-800',
    info: 'bg-blue-100 text-blue-800',
    gray: 'bg-gray-100 text-gray-800',
    primary: 'bg-primary-100 text-primary-800',
  }

  const sizeClasses = {
    sm: 'px-2 py-0.5 text-xs',
    md: 'px-2.5 py-0.5 text-xs',
  }

  const dotColors = {
    success: 'bg-green-500',
    warning: 'bg-yellow-500',
    danger: 'bg-red-500',
    info: 'bg-blue-500',
    gray: 'bg-gray-500',
    primary: 'bg-primary-500',
  }

  return (
    <span className={`inline-flex items-center gap-1 rounded-full font-medium ${variantClasses[variant]} ${sizeClasses[size]} ${children ? '' : 'hidden'}`}>
      {dot && <span className={`w-1.5 h-1.5 rounded-full ${dotColors[variant]}`} />}
      {children}
    </span>
  )
}

export function StatusBadge({ status }: { status: string }) {
  const statusMap: Record<string, { variant: 'success' | 'warning' | 'danger' | 'info' | 'gray' | 'primary'; label: string }> = {
    ACTIVE: { variant: 'success', label: 'Active' },
    INACTIVE: { variant: 'gray', label: 'Inactive' },
    ENROLLED: { variant: 'info', label: 'Enrolled' },
    DROPPED: { variant: 'warning', label: 'Dropped' },
    COMPLETED: { variant: 'success', label: 'Completed' },
    WITHDRAWN: { variant: 'warning', label: 'Withdrawn' },
    FAILED: { variant: 'danger', label: 'Failed' },
    PRESENT: { variant: 'success', label: 'Present' },
    ABSENT: { variant: 'danger', label: 'Absent' },
    LATE: { variant: 'warning', label: 'Late' },
    EXCUSED: { variant: 'info', label: 'Excused' },
    MEDICAL: { variant: 'info', label: 'Medical' },
    PENDING: { variant: 'warning', label: 'Pending' },
    APPROVED: { variant: 'success', label: 'Approved' },
    REJECTED: { variant: 'danger', label: 'Rejected' },
    PUBLISHED: { variant: 'success', label: 'Published' },
    DRAFT: { variant: 'gray', label: 'Draft' },
    ARCHIVED: { variant: 'gray', label: 'Archived' },
  }

  const config = statusMap[status] || { variant: 'gray', label: status }
  return <Badge variant={config.variant}>{config.label}</Badge>
}