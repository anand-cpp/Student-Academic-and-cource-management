import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Link } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { useAuth } from '@contexts/AuthContext'
import { Button, Input, LoadingSpinner, useToast } from '@/components/ui'

const loginSchema = z.object({
  usernameOrEmail: z.string().min(1, 'Username or email is required'),
  password: z.string().min(1, 'Password is required'),
})

type LoginFormData = z.infer<typeof loginSchema>

export function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const { toast } = useToast()
  const [loading, setLoading] = useState(false)

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const onSubmit = async (data: LoginFormData) => {
    setLoading(true)
    try {
      await login(data.usernameOrEmail, data.password)
      toast({ type: 'success', title: 'Welcome back!', message: 'You have been successfully logged in' })
      navigate('/')
    } catch (error: any) {
      toast({
        type: 'error',
        title: 'Login failed',
        message: error.response?.data?.message || 'Invalid credentials. Please try again.',
      })
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <Link to="/" className="flex justify-center">
          <svg className="w-12 h-12 text-primary-600" fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 3L1 9l11 6 9-4.91V17h2V9L12 3z" />
          </svg>
        </Link>
        <h2 className="mt-6 text-center text-3xl font-heading font-bold text-gray-900">Sign in to your account</h2>
        <p className="mt-2 text-center text-sm text-gray-600">
          Academic Management System
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow-card sm:rounded-xl sm:px-10">
          <form className="space-y-6" onSubmit={handleSubmit(onSubmit)}>
            <div>
              <label htmlFor="usernameOrEmail" className="label">
                Username or Email
              </label>
              <Input
                id="usernameOrEmail"
                name="usernameOrEmail"
                type="text"
                autoComplete="username"
                placeholder="Enter your username or email"
                error={errors.usernameOrEmail?.message}
                {...register('usernameOrEmail')}
              />
            </div>

            <div>
              <label htmlFor="password" className="label">
                Password
              </label>
              <Input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                placeholder="Enter your password"
                error={errors.password?.message}
                {...register('password')}
              />
            </div>

            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  id="remember"
                  name="remember"
                  type="checkbox"
                  className="h-4 w-4 text-primary-600 border-gray-300 rounded focus:ring-primary-500"
                />
                <label htmlFor="remember" className="ml-2 block text-sm text-gray-600">
                  Remember me
                </label>
              </div>
              <a href="#" className="text-sm font-medium text-primary-600 hover:text-primary-500">
                Forgot password?
              </a>
            </div>

            <Button type="submit" className="w-full" size="lg" loading={loading}>
              {loading ? <LoadingSpinner size="sm" /> : 'Sign in'}
            </Button>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-gray-200" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">Demo Credentials</span>
              </div>
            </div>

            <div className="mt-4 grid grid-cols-2 gap-3 text-sm">
              <div className="p-3 bg-gray-50 rounded-lg">
                <p className="font-medium text-gray-900">Admin</p>
                <p className="text-gray-600">admin / password123</p>
              </div>
              <div className="p-3 bg-gray-50 rounded-lg">
                <p className="font-medium text-gray-900">Faculty</p>
                <p className="text-gray-600">prof.smith / password123</p>
              </div>
              <div className="p-3 bg-gray-50 rounded-lg">
                <p className="font-medium text-gray-900">Student</p>
                <p className="text-gray-600">student001 / password123</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}