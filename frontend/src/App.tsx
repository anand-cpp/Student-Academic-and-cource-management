import { BrowserRouter, Routes, Route, Navigate, Outlet, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { ToastProvider } from '@/contexts/ToastContext';
import { ToastContainer } from '@/components/ui';
import { LoginPage, DashboardPage, StudentsPage, FacultyPage, CoursesPage, EnrollmentsPage, AttendancePage, AssessmentsPage, MarksPage, ReportsPage, StudentDetailPage, OtherPages } from '@/pages';
import { Sidebar, Header } from '@/components/layout';
import { useTheme } from '@/contexts/ThemeContext';
import { useState } from 'react';
import { cn } from '@/utils';
import { 
  HomeIcon, UsersIcon, AcademicCapIcon, BookOpenIcon, ClipboardDocumentListIcon, 
  CalendarDaysIcon, DocumentTextIcon, StarIcon, ChartBarIcon, Cog6ToothIcon, UserIcon
} from '@heroicons/react/24/outline';
import { UserRole } from '@/types';

const roleNavigation: Record<UserRole, Array<{ label: string; href: string; icon: React.ReactNode }>> = {
  ADMIN: [
    { label: 'Dashboard', href: '/dashboard', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Students', href: '/students', icon: <UsersIcon className="h-5 w-5" /> },
    { label: 'Faculty', href: '/faculty', icon: <AcademicCapIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Attendance', href: '/attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: 'Assessments', href: '/assessments', icon: <DocumentTextIcon className="h-5 w-5" /> },
    { label: 'Marks', href: '/marks', icon: <StarIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
    { label: 'Settings', href: '/settings', icon: <Cog6ToothIcon className="h-5 w-5" /> },
  ],
  FACULTY: [
    { label: 'Dashboard', href: '/dashboard', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'My Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Attendance', href: '/attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: 'Assessments', href: '/assessments', icon: <DocumentTextIcon className="h-5 w-5" /> },
    { label: 'Marks', href: '/marks', icon: <StarIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
  ],
  DEPARTMENT_HEAD: [
    { label: 'Dashboard', href: '/dashboard', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Students', href: '/students', icon: <UsersIcon className="h-5 w-5" /> },
    { label: 'Faculty', href: '/faculty', icon: <AcademicCapIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
  ],
  REGISTRAR: [
    { label: 'Dashboard', href: '/dashboard', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Students', href: '/students', icon: <UsersIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
  ],
  STUDENT: [
    { label: 'Dashboard', href: '/dashboard', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'My Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Attendance', href: '/attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: 'Assessments', href: '/assessments', icon: <DocumentTextIcon className="h-5 w-5" /> },
    { label: 'Marks', href: '/marks', icon: <StarIcon className="h-5 w-5" /> },
    { label: 'Profile', href: '/profile', icon: <UserIcon className="h-5 w-5" /> },
  ],
};

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="animate-spin rounded-full h-12 w-12 border-4 border-brand-500 border-t-transparent" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

function Layout() {
  const { user, hasRole } = useAuth();
  const { resolvedTheme } = useTheme();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const location = useLocation();

  const navigation = roleNavigation[user?.role as UserRole] || [];

  const getBreadcrumbs = () => {
    const path = location.pathname;
    const segments = path.split('/').filter(Boolean);
    
    const routeLabels: Record<string, string> = {
      dashboard: 'Dashboard',
      students: 'Students',
      faculty: 'Faculty',
      courses: 'Courses',
      enrollments: 'Enrollments',
      attendance: 'Attendance',
      assessments: 'Assessments',
      marks: 'Marks',
      reports: 'Reports',
      settings: 'Settings',
      profile: 'Profile',
    };

    const breadcrumbs: Array<{ label: string; href: string; isCurrent?: boolean }> = [{ label: 'Home', href: '/', isCurrent: false }];
    
    let currentPath = '';
    segments.forEach((segment, index) => {
      currentPath += `/${segment}`;
      const isLast = index === segments.length - 1;
      const label = routeLabels[segment] || segment.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
      
      breadcrumbs.push({
        label,
        href: isLast ? '' : currentPath,
        isCurrent: isLast,
      });
    });

    return breadcrumbs;
  };

  return (
    <div className={cn('min-h-screen bg-neutral-50 dark:bg-neutral-950 transition-colors', sidebarCollapsed && 'lg:pl-16')}>
      <Sidebar
        items={navigation}
        isOpen={sidebarOpen}
        onToggle={setSidebarOpen}
        collapsed={sidebarCollapsed}
        onCollapseChange={setSidebarCollapsed}
      />
      
      <div className={cn('lg:pl-64 transition-all duration-300', sidebarCollapsed && 'lg:pl-16')}>
        <Header
          title=""
          breadcrumbs={getBreadcrumbs()}
          onSearch={() => {}}
        />
        
        <main className="p-4 lg:p-6">
          <div className="max-w-full mx-auto">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/students" element={<StudentsPage />} />
        <Route path="/students/new" element={<StudentsPage />} />
        <Route path="/students/:id" element={<StudentDetailPage />} />
        <Route path="/faculty" element={<FacultyPage />} />
        <Route path="/courses" element={<CoursesPage />} />
        <Route path="/enrollments" element={<EnrollmentsPage />} />
        <Route path="/attendance" element={<AttendancePage />} />
        <Route path="/assessments" element={<AssessmentsPage />} />
        <Route path="/marks" element={<MarksPage />} />
        <Route path="/reports" element={<ReportsPage />} />
        <Route path="/settings" element={<OtherPages />} />
        <Route path="/profile" element={<OtherPages />} />
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Route>
    </Routes>
  );
}

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <ToastProvider>
          <BrowserRouter>
            <AppRoutes />
            <ToastContainer />
          </BrowserRouter>
        </ToastProvider>
      </AuthProvider>
    </ThemeProvider>
  );
}