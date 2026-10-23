import { useState, useMemo } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Header } from './Header';
import { useAuth } from '@/contexts';
import { UserRole } from '@/types';
import { cn, getRoleWorkspacePath } from '@/utils';
import {
  HomeIcon,
  UsersIcon,
  AcademicCapIcon,
  BookOpenIcon,
  ClipboardDocumentListIcon,
  CalendarDaysIcon,
  DocumentTextIcon,
  StarIcon,
  ChartBarIcon,
  Cog6ToothIcon,
  UserIcon,
} from '@heroicons/react/24/outline';

const roleNavigation: Record<UserRole, Array<{ label: string; href: string; icon: React.ReactNode }>> = {
  ADMIN: [
    { label: 'Dashboard', href: '/app/w-adm89x', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Students', href: '/students', icon: <UsersIcon className="h-5 w-5" /> },
    { label: 'Faculty', href: '/faculty', icon: <AcademicCapIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Attendance', href: '/attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: 'Assessments', href: '/assessments', icon: <DocumentTextIcon className="h-5 w-5" /> },
    { label: 'Marks & Grades', href: '/marks', icon: <StarIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
    { label: 'Settings', href: '/settings', icon: <Cog6ToothIcon className="h-5 w-5" /> },
  ],
  FACULTY: [
    { label: 'Dashboard', href: '/app/w-fac42m', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Attendance', href: '/attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: 'Assessments', href: '/assessments', icon: <DocumentTextIcon className="h-5 w-5" /> },
    { label: 'Marks', href: '/marks', icon: <StarIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
    { label: 'Profile', href: '/profile', icon: <UserIcon className="h-5 w-5" /> },
  ],
  DEPARTMENT_HEAD: [
    { label: 'Dashboard', href: '/app/w-dpt63h', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Students', href: '/students', icon: <UsersIcon className="h-5 w-5" /> },
    { label: 'Faculty', href: '/faculty', icon: <AcademicCapIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
    { label: 'Settings', href: '/settings', icon: <Cog6ToothIcon className="h-5 w-5" /> },
  ],
  REGISTRAR: [
    { label: 'Dashboard', href: '/app/w-reg55r', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'Students', href: '/students', icon: <UsersIcon className="h-5 w-5" /> },
    { label: 'Enrollments', href: '/enrollments', icon: <ClipboardDocumentListIcon className="h-5 w-5" /> },
    { label: 'Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Reports', href: '/reports', icon: <ChartBarIcon className="h-5 w-5" /> },
    { label: 'Settings', href: '/settings', icon: <Cog6ToothIcon className="h-5 w-5" /> },
  ],
  STUDENT: [
    { label: 'Dashboard', href: '/app/w-stu17k', icon: <HomeIcon className="h-5 w-5" /> },
    { label: 'My Courses', href: '/courses', icon: <BookOpenIcon className="h-5 w-5" /> },
    { label: 'Attendance', href: '/attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
    { label: 'Assessments', href: '/assessments', icon: <DocumentTextIcon className="h-5 w-5" /> },
    { label: 'Marks & GPA', href: '/marks', icon: <StarIcon className="h-5 w-5" /> },
    { label: 'My Profile', href: '/profile', icon: <UserIcon className="h-5 w-5" /> },
  ],
};

const routeLabels: Record<string, string> = {
  dashboard: 'Dashboard',
  app: 'Workspace',
  'w-adm89x': 'Admin Console',
  'w-fac42m': 'Faculty Workspace',
  'w-stu17k': 'Student Portal',
  'w-dpt63h': 'Department Workspace',
  'w-reg55r': 'Registrar Console',
  students: 'Students',
  faculty: 'Faculty',
  courses: 'Courses',
  enrollments: 'Enrollments',
  attendance: 'Attendance',
  assessments: 'Assessments',
  marks: 'Marks & Grades',
  reports: 'Reports',
  settings: 'System Settings',
  profile: 'My Profile',
  new: 'Create',
};

export function Layout() {
  const { user } = useAuth();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);
  const location = useLocation();

  const navigation = useMemo(() => {
    return roleNavigation[(user?.role as UserRole) || 'ADMIN'] || roleNavigation.ADMIN;
  }, [user?.role]);

  const breadcrumbs = useMemo(() => {
    const segments = location.pathname.split('/').filter(Boolean);
    const crumbs: Array<{ label: string; href?: string; isCurrent?: boolean }> = [
      { label: 'Home', href: getRoleWorkspacePath(user?.role) },
    ];

    let accumulatedPath = '';
    segments.forEach((segment, index) => {
      accumulatedPath += `/${segment}`;
      const isLast = index === segments.length - 1;
      const label =
        routeLabels[segment] ||
        (segment.length > 10 ? `#${segment.slice(-6)}` : segment.charAt(0).toUpperCase() + segment.slice(1));

      crumbs.push({
        label,
        href: isLast ? undefined : accumulatedPath,
        isCurrent: isLast,
      });
    });

    return crumbs;
  }, [location.pathname, user?.role]);

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-900 dark:text-slate-100 flex flex-col transition-colors">
      <a href="#main-content" className="skip-link">
        Skip to main content
      </a>

      {/* Desktop & Mobile Sidebar */}
      <Sidebar
        items={navigation}
        isOpen={sidebarOpen}
        onToggle={setSidebarOpen}
        collapsed={sidebarCollapsed}
        onCollapseChange={setSidebarCollapsed}
      />

      {/* Main Content Area */}
      <div
        className={cn(
          'flex flex-col flex-1 transition-all duration-300 ease-in-out',
          sidebarCollapsed ? 'lg:pl-20' : 'lg:pl-64'
        )}
      >
        <Header breadcrumbs={breadcrumbs} />

        <main id="main-content" className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          <Outlet />
        </main>

        <footer className="border-t border-slate-200/80 dark:border-slate-800/80 bg-white/50 dark:bg-slate-900/40 py-4 px-6 text-center text-xs text-slate-400">
          <div className="flex flex-col sm:flex-row items-center justify-between gap-2 max-w-7xl mx-auto">
            <span>© 2026 AcademiaPro — Academic & Course Management Suite</span>
            <div className="flex items-center gap-4">
              <span>Spring Boot 3.3.2 + React 18</span>
              <span>•</span>
              <span className="text-emerald-600 dark:text-emerald-400 font-medium">System Online</span>
            </div>
          </div>
        </footer>
      </div>
    </div>
  );
}

export default Layout;
