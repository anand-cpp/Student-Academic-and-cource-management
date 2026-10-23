import { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button, Card, Badge } from '@/components/ui';
import { useAuth, useTheme, useToast } from '@/contexts';
import {
  AcademicCapIcon,
  LockClosedIcon,
  UserIcon,
  EyeIcon,
  EyeSlashIcon,
  SunIcon,
  MoonIcon,
  ShieldCheckIcon,
  SparklesIcon,
} from '@heroicons/react/24/outline';

const loginSchema = z.object({
  usernameOrEmail: z.string().min(3, 'Username or email must be at least 3 characters'),
  password: z.string().min(6, 'Password must be at least 6 characters'),
});

type LoginFormData = z.infer<typeof loginSchema>;

const DEMO_PRESETS = [
  { role: 'Admin', user: 'admin@university.edu', desc: 'Full System Access', color: 'brand' as const },
  { role: 'Faculty', user: 'smith@university.edu', desc: 'Courses, Grades & Attendance', color: 'success' as const },
  { role: 'Student', user: 'student001@university.edu', desc: 'Classes, Marks & Transcripts', color: 'info' as const },
  { role: 'Dept Head', user: 'cshead@university.edu', desc: 'Curriculum & Department', color: 'warning' as const },
  { role: 'Registrar', user: 'registrar@university.edu', desc: 'Admissions & Records', color: 'primary' as const },
];

export function LoginPage() {
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();
  const { showToast } = useToast();
  const { resolvedTheme, toggleTheme } = useTheme();
  const [isLoading, setIsLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      usernameOrEmail: 'admin@university.edu',
      password: 'password123',
    },
  });

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />;
  }

  const onSubmit = async (data: LoginFormData) => {
    setIsLoading(true);
    try {
      await login(data.usernameOrEmail, data.password);
      showToast({
        type: 'success',
        title: 'Authentication Successful',
        message: 'Welcome to AcademiaPro Academic Suite.',
      });
      navigate('/dashboard');
    } catch (error) {
      showToast({
        type: 'error',
        title: 'Sign In Failed',
        message: error instanceof Error ? error.message : 'Invalid credentials',
      });
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelectPreset = (user: string) => {
    setValue('usernameOrEmail', user);
    setValue('password', 'password123');
    showToast({
      type: 'info',
      title: 'Preset Selected',
      message: `Credentials filled for ${user}`,
    });
  };

  return (
    <div className="min-h-screen relative flex items-center justify-center p-4 bg-slate-50 dark:bg-slate-950 transition-colors">
      {/* Background Ambient Glow */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -left-40 w-96 h-96 bg-brand-500/10 dark:bg-brand-500/20 rounded-full blur-3xl" />
        <div className="absolute -bottom-40 -right-40 w-96 h-96 bg-indigo-500/10 dark:bg-indigo-500/20 rounded-full blur-3xl" />
      </div>

      {/* Theme Switcher Button (Top Right) */}
      <button
        type="button"
        onClick={toggleTheme}
        className="absolute top-4 right-4 p-2.5 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-sm text-slate-600 dark:text-slate-300 hover:text-brand-600 dark:hover:text-brand-400 transition-colors"
        title="Toggle Light/Dark Theme"
      >
        {resolvedTheme === 'dark' ? <SunIcon className="h-5 w-5 text-amber-400" /> : <MoonIcon className="h-5 w-5 text-indigo-600" />}
      </button>

      <div className="w-full max-w-md relative z-10 space-y-6">
        {/* Brand Header */}
        <div className="text-center space-y-2">
          <div className="inline-flex items-center justify-center h-14 w-14 rounded-2xl bg-gradient-to-tr from-brand-600 to-indigo-500 text-white shadow-lg shadow-brand-500/30 mb-2">
            <AcademicCapIcon className="h-8 w-8" />
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
            Academia<span className="text-brand-600 dark:text-brand-400">Pro</span>
          </h1>
          <p className="text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Student Academic & Course Management Suite
          </p>
        </div>

        {/* Login Card */}
        <Card variant="elevated" className="p-6 sm:p-8">
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                Username or Email
              </label>
              <div className="relative">
                <UserIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
                <input
                  type="text"
                  {...register('usernameOrEmail')}
                  placeholder="admin@university.edu or student001"
                  autoComplete="username"
                  className="w-full pl-10 pr-4 py-2.5 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500 transition-all"
                />
              </div>
              {errors.usernameOrEmail && (
                <p className="mt-1 text-xs text-rose-500 font-medium">
                  {errors.usernameOrEmail.message}
                </p>
              )}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                Password
              </label>
              <div className="relative">
                <LockClosedIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
                <input
                  type={showPassword ? 'text' : 'password'}
                  {...register('password')}
                  placeholder="Enter password (default: password123)"
                  autoComplete="current-password"
                  className="w-full pl-10 pr-10 py-2.5 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500 transition-all"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3.5 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
                >
                  {showPassword ? <EyeSlashIcon className="h-5 w-5" /> : <EyeIcon className="h-5 w-5" />}
                </button>
              </div>
              {errors.password && (
                <p className="mt-1 text-xs text-rose-500 font-medium">
                  {errors.password.message}
                </p>
              )}
            </div>

            <div className="flex items-center justify-between text-xs pt-1">
              <label className="flex items-center gap-2 cursor-pointer text-slate-600 dark:text-slate-400">
                <input
                  type="checkbox"
                  defaultChecked
                  className="h-4 w-4 rounded border-slate-300 text-brand-600 focus:ring-brand-500 accent-brand-600"
                />
                <span>Remember session</span>
              </label>
              <span className="text-slate-400 text-[11px]">BCrypt Encrypted</span>
            </div>

            <Button type="submit" fullWidth size="lg" loading={isLoading} className="mt-2">
              Sign In to Dashboard
            </Button>
          </form>

          {/* Demo Quick-Select Chips */}
          <div className="mt-6 pt-5 border-t border-slate-100 dark:border-slate-800">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-bold uppercase tracking-wider text-slate-500 dark:text-slate-400 flex items-center gap-1.5">
                <SparklesIcon className="h-4 w-4 text-brand-500" /> Quick Demo Roles
              </span>
              <span className="text-[11px] font-mono text-slate-400">password123</span>
            </div>

            <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
              {DEMO_PRESETS.map((preset) => (
                <button
                  key={preset.role}
                  type="button"
                  onClick={() => handleSelectPreset(preset.user)}
                  className="p-2 text-left rounded-xl border border-slate-200/80 dark:border-slate-800 hover:border-brand-500 hover:bg-brand-50/50 dark:hover:bg-brand-950/40 transition-all text-xs"
                >
                  <span className="font-bold text-slate-800 dark:text-slate-200 block truncate">
                    {preset.role}
                  </span>
                  <span className="text-[10px] text-slate-400 truncate block">
                    {preset.desc}
                  </span>
                </button>
              ))}
            </div>
          </div>
        </Card>

        {/* Security Trust Footnote */}
        <div className="text-center text-xs text-slate-400 flex items-center justify-center gap-1.5">
          <ShieldCheckIcon className="h-4 w-4 text-emerald-500" />
          <span>Spring Boot 3.3.2 • JWT 24hr Token • Hikari Connection Pool</span>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
