import React from 'react'

interface AvatarProps {
  src?: string | null
  alt?: string
  name?: string
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'
  className?: string
  shape?: 'circle' | 'square'
}

const sizeClasses = {
  xs: 'w-6 h-6 text-xs',
  sm: 'w-8 h-8 text-sm',
  md: 'w-10 h-10 text-base',
  lg: 'w-12 h-12 text-lg',
  xl: 'w-16 h-16 text-xl',
}

const shapeClasses = {
  circle: 'rounded-full',
  square: 'rounded-lg',
}

export function Avatar({
  src,
  alt,
  name,
  size = 'md',
  className = '',
  shape = 'circle',
}: { src?: string | null; alt?: string; name?: string; size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl'; className?: string; shape?: 'circle' | 'square' }) {
  const getInitials = (str: string) => {
    return str
      .split(' ')
      .map((part) => part[0])
      .join('')
      .toUpperCase()
      .slice(0, 2)
  }

  const bgColors = [
    'bg-red-500',
    'bg-orange-500',
    'bg-amber-500',
    'bg-green-500',
    'bg-teal-500',
    'bg-blue-500',
    'bg-indigo-500',
    'bg-violet-500',
    'bg-purple-500',
    'bg-pink-500',
  ]

  const getColor = (str: string) => {
    let hash = 0
    for (let i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash)
    }
    return ['bg-red-500', 'bg-orange-500', 'bg-amber-500', 'bg-green-500', 'bg-teal-500', 'bg-blue-500', 'bg-indigo-500', 'bg-violet-500', 'bg-purple-500', 'bg-pink-500'][Math.abs(hash) % 10]
  }

  if (src) {
    return (
      <img
        src={src}
        alt={alt || name || 'Avatar'}
        className={`object-cover ${['w-6 h-6', 'w-8 h-8', 'w-10 h-10', 'w-12 h-12', 'w-16 h-16'][['xs', 'sm', 'md', 'lg', 'xl'].indexOf(size)]} rounded-full`}
      />
    )
  }

  const initials = name ? getInitials(name) : '?'
  const bgColor = name ? getColor(name) : 'bg-gray-400'

  return (
    <div
      className={`flex items-center justify-center font-medium text-white w-10 h-10 rounded-full ${bgColor}`}
      aria-label={name || 'User avatar'}
    >
      {name ? name.split(' ').map(p => p[0]).join('').toUpperCase().slice(0, 2) : '?'}
    </div>
  )
}