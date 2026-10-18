import React, { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import { MagnifyingGlassIcon, UserCircleIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { useToast } from '../../components/ui/Toast'
import { Avatar } from '../ui/Avatar'
import { Button } from '../ui/Button'
import { Input } from '../ui/Input'

export function Header() {
  const { user, logout, isAuthenticated } = useAuth()
  const location = useLocation()
  const { toast } = useToast()
  const [showProfileMenu, setShowProfileMenu] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [showSearch, setShowSearch] = useState(false)

  if (!isAuthenticated) return null

  const handleLogout = () => {
    logout()
    toast({ type: 'success', title: 'Logged out', message: 'You have been successfully logged out' })
  }

  return (
    <header className="sticky top-0 z-30 bg-white border-b border-gray-100 shadow-sm">
      <div className="flex items-center justify-between h-16 px-4 sm:px-6 lg:px-8">
        <div className="flex items-center gap-4 lg:hidden">
          <button
            type="button"
            className="p-2 rounded-lg text-gray-500 hover:text-gray-700 hover:bg-gray-100"
            onClick={() => setShowSearch(true)}
            aria-label="Search"
          >
            <MagnifyingGlassIcon className="w-5 h-5" />
          </button>
        </div>

        <div className="flex-1 flex items-center justify-center max-w-md mx-4">
          <div className="relative w-full max-w-md">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" aria-hidden="true" />
            <input
              type="search"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="input pl-10 w-full"
              placeholder="Search..."
            />
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="relative" onClick={() => setShowProfileMenu(!showProfileMenu)}>
            <button
              type="button"
              className="flex items-center gap-3 p-2 rounded-lg hover:bg-gray-50 transition-colors"
              aria-label="User menu"
              aria-expanded={showProfileMenu}
            >
              <Avatar src={user?.avatarUrl} name={user?.fullName} size="md" />
              <span className="hidden sm:block text-sm font-medium text-gray-700">{user?.fullName}</span>
            </button>

            {showProfileMenu && (
              <>
                <div className="fixed inset-0 z-40" onClick={() => setShowProfileMenu(false)} />
                <div className="absolute right-0 mt-2 w-56 bg-white rounded-xl shadow-lg border border-gray-100 py-1 z-50 animate-slide-in">
                  <div className="px-4 py-3 border-b border-gray-100">
                    <p className="text-sm font-medium text-gray-900">{user?.fullName}</p>
                    <p className="text-xs text-gray-500 capitalize">{user?.role?.toLowerCase()}</p>
                    <p className="text-xs text-gray-400 truncate">{user?.email}</p>
                  </div>
                  <Link
                    to="/settings"
                    className="flex items-center gap-2 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50"
                    onClick={() => setShowProfileMenu(false)}
                  >
                    <UserCircleIcon className="w-5 h-5" />
                    Profile
                  </Link>
                  <button
                    type="button"
                    onClick={handleLogout}
                    className="flex items-center gap-2 w-full px-4 py-2 text-sm text-red-600 hover:bg-gray-50"
                  >
                    <XMarkIcon className="w-5 h-5" />
                    Sign out
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </header>
  )
}