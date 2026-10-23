import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button, Input, Card } from '@/components/ui';
import { useAuth } from '@/contexts';
import { useToast } from '@/contexts';

const loginSchema = z.object({
  email: z.string().email('Please enter a valid email address'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();
  const { showToast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: 'admin@university.edu',
      password: 'password123',
    },
  });

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      await login(data.email, data.password);
      showToast({
        type: 'success',
        title: 'Welcome back!',
        message: 'You have been successfully logged in.',
      });
      navigate('/dashboard');
    } catch (error) {
      showToast({
        type: 'error',
        title: 'Login failed',
        message: error instanceof Error ? error.message : 'Invalid credentials',
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-neutral-50 dark:bg-neutral-950 px-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-neutral-900 dark:text-neutral-50">Student Academic System</h1>
          <p className="mt-2 text-neutral-500 dark:text-neutral-400">Sign in to your account</p>
        </div>

        <Card className="p-6">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
            <Input
              {...register('email')}
              label="Email"
              type="email"
              placeholder="Enter your email"
              error={errors.email?.message}
              leftIcon={<svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" /></svg>}
              autoComplete="email"
            />

            <Input
              {...register('password')}
              label="Password"
              type="password"
              placeholder="Enter your password"
              error={errors.password?.message}
              leftIcon={<svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" /></svg>}
              autoComplete="current-password"
            />

            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" className="h-4 w-4 rounded border-neutral-300 text-brand-600 focus:ring-brand-500" />
                <span className="text-sm text-neutral-600 dark:text-neutral-400">Remember me</span>
              </label>
              <a href="#" className="text-sm text-brand-600 hover:text-brand-700 dark:text-brand-400">Forgot password?</a>
            </div>

            <Button type="submit" fullWidth loading={isLoading}>
              Sign In
            </Button>
          </form>

          <div className="mt-6 p-4 bg-neutral-50 dark:bg-neutral-800 rounded-lg text-sm">
            <p className="font-medium text-neutral-900 dark:text-neutral-50 mb-2">Demo Credentials:</p>
            <div className="space-y-1 text-neutral-600 dark:text-neutral-400 font-mono text-xs">
              <div>Admin: admin@university.edu / password123</div>
              <div>Faculty: faculty@university.edu / password123</div>
              <div>Student: student@university.edu / password123</div>
              <div>Dept Head: hod@university.edu / password123</div>
              <div>Registrar: registrar@university.edu / password123</div>
            </div>
          </div>
        </Card>

        <p className="mt-6 text-center text-sm text-neutral-500 dark:text-neutral-400">
          © 2024 Student Academic Management System. All rights reserved.
        </p>
      </div>
    </div>
  );
}