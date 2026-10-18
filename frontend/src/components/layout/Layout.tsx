import React from 'react'
import { Outlet } from 'react-router-dom'
import { Sidebar } from './Sidebar'
import { Header } from './Header'
import { ToastProvider } from '../ui/Toast'

export function Layout() {
  return (
    <ToastProvider>
      <div className="min-h-screen bg-gray-50">
        <Sidebar />
        <div className="lg:pl-64 flex flex-col min-h-screen">
          <Header />
          <main className="flex-1 p-4 sm:p-6 lg:p-8">
            <Outlet />
          </main>
          <footer className="bg-white border-t border-gray-100 py-4 px-4 sm:px-6 lg:px-8">
            <div className="max-w-7xl mx-auto text-center text-sm text-gray-500">
              © 2024 Academic Management System. All rights reserved.
            </div>
          </footer>
        </div>
      </div>
    </ToastProvider>
  )
}