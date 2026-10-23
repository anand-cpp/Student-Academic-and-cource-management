import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from '@/contexts/AuthContext';
import { ThemeProvider } from '@/contexts/ThemeContext';
import { ToastProvider } from '@/contexts/ToastContext';
import { ToastContainer } from '@/components/ui';
import {
  LoginPage,
  DashboardPage,
  StudentsPage,
  FacultyPage,
  CoursesPage,
  EnrollmentsPage,
  AttendancePage,
  AssessmentsPage,
  MarksPage,
  ReportsPage,
  StudentDetailPage,
  SettingsPage,
  ProfilePage,
  AccessDeniedPage,
} from '@/pages';
import {
  AdminDashboard,
  FacultyDashboard,
  StudentDashboard,
  DeptHeadDashboard,
  RegistrarDashboard,
} from '@/components/dashboard';
import { Layout } from '@/components/layout/Layout';
import { UserRole } from '@/types';

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth();
  const location = useLocation();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 dark:bg-slate-950">
        <div className="flex flex-col items-center gap-3">
          <div className="animate-spin rounded-full h-12 w-12 border-4 border-brand-500 border-t-transparent" />
          <p className="text-xs font-semibold text-slate-500 tracking-wider uppercase">Loading AcademiaPro...</p>
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return <>{children}</>;
}

function RoleRoute({
  allowedRoles,
  children,
}: {
  allowedRoles: UserRole[];
  children: React.ReactNode;
}) {
  const { user } = useAuth();

  if (!user || !allowedRoles.includes(user.role as UserRole)) {
    return <AccessDeniedPage requiredRoles={allowedRoles} />;
  }

  return <>{children}</>;
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />
        
        {/* Opaque Role Workspace Routes */}
        <Route
          path="/app/w-adm89x"
          element={
            <RoleRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/app/w-fac42m"
          element={
            <RoleRoute allowedRoles={['FACULTY']}>
              <FacultyDashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/app/w-stu17k"
          element={
            <RoleRoute allowedRoles={['STUDENT']}>
              <StudentDashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/app/w-dpt63h"
          element={
            <RoleRoute allowedRoles={['DEPARTMENT_HEAD']}>
              <DeptHeadDashboard />
            </RoleRoute>
          }
        />
        <Route
          path="/app/w-reg55r"
          element={
            <RoleRoute allowedRoles={['REGISTRAR']}>
              <RegistrarDashboard />
            </RoleRoute>
          }
        />
        
        {/* Student Directory: Admin, Faculty, Dept Head, Registrar */}
        <Route
          path="/students"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR']}>
              <StudentsPage />
            </RoleRoute>
          }
        />
        <Route
          path="/students/new"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'REGISTRAR']}>
              <StudentsPage />
            </RoleRoute>
          }
        />
        <Route path="/students/:id" element={<StudentDetailPage />} />

        {/* Faculty Roster: Admin, Faculty, Dept Head, Registrar */}
        <Route
          path="/faculty"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'FACULTY', 'DEPARTMENT_HEAD', 'REGISTRAR']}>
              <FacultyPage />
            </RoleRoute>
          }
        />

        {/* Courses & Curriculum: All authenticated roles */}
        <Route path="/courses" element={<CoursesPage />} />

        {/* Enrollments: All authenticated roles */}
        <Route path="/enrollments" element={<EnrollmentsPage />} />

        {/* Attendance: All authenticated roles */}
        <Route path="/attendance" element={<AttendancePage />} />

        {/* Assessments: All authenticated roles */}
        <Route path="/assessments" element={<AssessmentsPage />} />

        {/* Marks: All authenticated roles */}
        <Route path="/marks" element={<MarksPage />} />

        {/* Reports: All authenticated roles */}
        <Route path="/reports" element={<ReportsPage />} />

        {/* System Settings: Restricted to Admin, Dept Head, Registrar */}
        <Route
          path="/settings"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR']}>
              <SettingsPage />
            </RoleRoute>
          }
        />

        {/* User Profile: All roles */}
        <Route path="/profile" element={<ProfilePage />} />

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
