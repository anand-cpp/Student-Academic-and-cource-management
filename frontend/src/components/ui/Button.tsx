import React from 'react'

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost' | 'outline'
  size?: 'sm' | 'md' | 'lg'
  loading?: boolean
  leftIcon?: React.ReactNode
  rightIcon?: React.ReactNode
}

export function Button({
  children,
  variant = 'primary',
  size = 'md',
  loading = false,
  leftIcon,
  rightIcon,
  disabled,
  className = '',
  ...props
}: ButtonProps) {
  const baseClasses = 'inline-flex items-center justify-center font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed'

  const variantClasses = {
    primary: 'bg-primary-600 text-white hover:bg-primary-700 focus:ring-primary-500',
    secondary: 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-50 focus:ring-gray-500',
    danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
    ghost: 'bg-transparent text-gray-600 hover:bg-gray-100 focus:ring-gray-500',
    outline: 'bg-transparent border border-gray-300 text-gray-700 hover:bg-gray-50 focus:ring-gray-500',
  }

  const sizeClasses = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-sm',
    lg: 'px-6 py-3 text-base',
  }

  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      disabled={disabled || loading}
      {...props}
    >
      {loading && <svg className="animate-spin -ml-1 mr-2 h-4 w-4" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" /><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" /></svg>}
      {leftIcon && !loading && <span className="mr-2">{leftIcon}</span>}
      {children}
      {rightIcon && !loading && <span className="ml-2">{rightIcon}</span>}
    </button>
  )
}

export function IconButton({
  children,
  variant = 'ghost',
  size = 'md',
  className = '',
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'ghost' | 'primary' | 'danger'; size?: 'sm' | 'md' | 'lg' }) {
  const variantClasses = {
    ghost: 'text-gray-500 hover:text-gray-700 hover:bg-gray-100',
    primary: 'text-primary-600 hover:text-primary-700 hover:bg-primary-50',
    danger: 'text-red-500 hover:text-red-700 hover:bg-red-50',
  }

  const sizeClasses = {
    sm: 'p-1.5',
    md: 'p-2',
    lg: 'p-3',
  }

  return (
    <button
      className={`inline-flex items-center justify-center rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      {...props}
    >
      {children}
    </button>
  )
}