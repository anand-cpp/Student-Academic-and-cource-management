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

const roleNavigation = {
  ADMIN: [
    { label: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { label: 'Students', href: '/students', icon: UsersIcon },
    { label: 'Faculty', href: '/faculty', icon: AcademicCapIcon },
    { label: 'Courses', href: '/courses', icon: BookOpenIcon },
    { label: 'Enrollments', href: '/enrollments', icon: ClipboardDocumentListIcon },
    { label: 'Attendance', href: '/attendance', icon: CalendarDaysIcon },
    { label: 'Assessments', href: '/assessments', icon: DocumentTextIcon },
    { label: 'Marks', href: '/marks', icon: StarIcon },
    { label: 'Reports', href: '/reports', icon: ChartBarIcon },
    { label: 'Settings', href: '/settings', icon: Cog6ToothIcon },
  ],
  FACULTY: [
    { label: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { label: 'My Courses', href: '/courses', icon: BookOpenIcon },
    { label: 'Enrollments', href: '/enrollments', icon: ClipboardDocumentListIcon },
    { label: 'Attendance', href: '/attendance', icon: CalendarDaysIcon },
    { label: 'Assessments', href: '/assessments', icon: DocumentTextIcon },
    { label: 'Marks', href: '/marks', icon: StarIcon },
    { label: 'Reports', href: '/reports', icon: ChartBarIcon },
  ],
  DEPARTMENT_HEAD: [
    { label: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { label: 'Students', href: '/students', icon: UsersIcon },
    { label: 'Faculty', href: '/faculty', icon: AcademicCapIcon },
    { label: 'Courses', href: '/courses', icon: BookOpenIcon },
    { label: 'Enrollments', href: '/enrollments', icon: ClipboardDocumentListIcon },
    { label: 'Reports', href: '/reports', icon: ChartBarIcon },
  ],
  REGISTRAR: [
    { label: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { label: 'Students', href: '/students', icon: UsersIcon },
    { label: 'Enrollments', href: '/enrollments', icon: ClipboardDocumentListIcon },
    { label: 'Courses', href: '/courses', icon: BookOpenIcon },
    { label: 'Reports', href: '/reports', icon: ChartBarIcon },
  ],
  STUDENT: [
    { label: 'Dashboard', href: '/dashboard', icon: HomeIcon },
    { label: 'My Courses', href: '/courses', icon: BookOpenIcon },
    { label: 'Attendance', href: '/attendance', icon: CalendarDaysIcon },
    { label: 'Assessments', href: '/assessments', icon: DocumentTextIcon },
    { label: 'Marks', href: '/marks', icon: StarIcon },
    { label: 'Profile', href: '/profile', icon: UserIcon },
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

    const breadcrumbs = [{ label: 'Home', href: '/' }];
    
    let currentPath = '';
    segments.forEach((segment, index) => {
      currentPath += `/${segment}`;
      const isLast = index === segments.length - 1;
      const label = routeLabels[segment] || segment.replace(/-/g, ' ').replace(/\b\w/g, l => l.toUpperCase());
      
      breadcrumbs.push({
        label,
        href: isLast ? undefined : currentPath,
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