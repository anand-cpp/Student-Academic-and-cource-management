import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { StatCard, DataTable, Card, Button, Badge } from '@/components';
import { api } from '@/services';
import { UsersIcon, AcademicCapIcon, BookOpenIcon, BuildingOfficeIcon, ClockIcon, ChartBarIcon, ArrowTrendingUpIcon, ArrowTrendingDownIcon, MinusIcon } from '@heroicons/react/24/outline';
import { useAuth } from '@/contexts';
import { Student, Faculty, Course, Enrollment, Attendance, Assessment, Mark, UserRole } from '@/types';

export function DashboardPage() {
  const navigate = useNavigate();
  const { user, hasRole } = useAuth();
  const [stats, setStats] = useState<Record<string, number>>({});
  const [recentStudents, setRecentStudents] = useState<Student[]>([]);
  const [recentFaculty, setRecentFaculty] = useState<Faculty[]>([]);
  const [recentCourses, setRecentCourses] = useState<Course[]>([]);
  const [recentEnrollments, setRecentEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboard = async () => {
      if (!user) return;
      try {
        const [statsData, studentsData, facultyData, coursesData, enrollmentsData] = await Promise.all([
          api.dashboard.getStats(user.role),
          api.students.getAll({ page: 1, pageSize: 5 }),
          api.faculty.getAll({ page: 1, pageSize: 5 }),
          api.courses.getAll({ page: 1, pageSize: 5 }),
          api.enrollments.getAll({ page: 1, pageSize: 5 }),
        ]);
        setStats(statsData);
        setRecentStudents(studentsData.data);
        setRecentFaculty(facultyData.data);
        setRecentCourses(coursesData.data);
        setRecentEnrollments(enrollmentsData.data);
      } catch (error) {
        console.error('Failed to load dashboard:', error);
      } finally {
        setLoading(false);
      }
    };
    loadDashboard();
  }, [user]);

  const getStatCards = () => {
    if (!user) return [];
    
    const roleStats: Record<UserRole, Array<{ label: string; value: string | number; icon: React.ReactNode; color: 'primary' | 'success' | 'warning' | 'error' | 'info'; change?: number; trend?: 'up' | 'down' | 'neutral' }>> = {
      ADMIN: [
        { label: 'Total Students', value: stats.totalStudents || 0, icon: <UsersIcon className="h-6 w-6" />, color: 'primary', change: 12, trend: 'up' },
        { label: 'Total Faculty', value: stats.totalFaculty || 0, icon: <AcademicCapIcon className="h-6 w-6" />, color: 'success', change: 5, trend: 'up' },
        { label: 'Total Courses', value: stats.totalCourses || 0, icon: <BookOpenIcon className="h-6 w-6" />, color: 'info', change: 8, trend: 'up' },
        { label: 'Departments', value: stats.totalDepartments || 0, icon: <BuildingOfficeIcon className="h-6 w-6" />, color: 'warning', change: 0, trend: 'neutral' },
        { label: 'Pending Approvals', value: stats.pendingApprovals || 0, icon: <ClockIcon className="h-6 w-6" />, color: 'error', change: -3, trend: 'down' },
        { label: 'Active Enrollments', value: stats.activeEnrollments || 0, icon: <ChartBarIcon className="h-6 w-6" />, color: 'primary', change: 15, trend: 'up' },
      ],
      FACULTY: [
        { label: 'My Courses', value: stats.myCourses || 0, icon: <BookOpenIcon className="h-6 w-6" />, color: 'primary' },
        { label: 'My Students', value: stats.myStudents || 0, icon: <UsersIcon className="h-6 w-6" />, color: 'success' },
        { label: 'Pending Grades', value: stats.pendingGrades || 0, icon: <ClockIcon className="h-6 w-6" />, color: 'warning', trend: 'down' },
        { label: 'Upcoming Classes', value: stats.upcomingClasses || 0, icon: <ChartBarIcon className="h-6 w-6" />, color: 'info' },
      ],
      STUDENT: [
        { label: 'Enrolled Courses', value: stats.enrolledCourses || 0, icon: <BookOpenIcon className="h-6 w-6" />, color: 'primary' },
        { label: 'Attendance Rate', value: `${stats.attendanceRate || 0}%`, icon: <ChartBarIcon className="h-6 w-6" />, color: 'success', change: 2, trend: 'up' },
        { label: 'Upcoming Assessments', value: stats.upcomingAssessments || 0, icon: <ClockIcon className="h-6 w-6" />, color: 'warning' },
        { label: 'Current GPA', value: stats.currentGPA || 0, icon: <ArrowTrendingUpIcon className="h-6 w-6" />, color: 'info', change: 0.1, trend: 'up' },
      ],
      DEPARTMENT_HEAD: [
        { label: 'Dept Students', value: stats.departmentStudents || 0, icon: <UsersIcon className="h-6 w-6" />, color: 'primary' },
        { label: 'Dept Faculty', value: stats.departmentFaculty || 0, icon: <AcademicCapIcon className="h-6 w-6" />, color: 'success' },
        { label: 'Dept Courses', value: stats.departmentCourses || 0, icon: <BookOpenIcon className="h-6 w-6" />, color: 'info' },
        { label: 'Pending Reviews', value: stats.pendingReviews || 0, icon: <ClockIcon className="h-6 w-6" />, color: 'warning' },
      ],
      REGISTRAR: [
        { label: 'Total Enrollments', value: stats.totalEnrollments || 0, icon: <UsersIcon className="h-6 w-6" />, color: 'primary' },
        { label: 'Pending Registrations', value: stats.pendingRegistrations || 0, icon: <ClockIcon className="h-6 w-6" />, color: 'warning' },
        { label: 'Graduation Candidates', value: stats.graduationCandidates || 0, icon: <AcademicCapIcon className="h-6 w-6" />, color: 'success' },
        { label: 'Transcript Requests', value: stats.transcriptRequests || 0, icon: <ChartBarIcon className="h-6 w-6" />, color: 'info' },
      ],
    };

    return roleStats[user.role] || roleStats.ADMIN;
  };

  if (loading) {
    return (
      <div className="space-y-6">
        <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <StatCard key={i} label="Loading..." value="—" className="animate-pulse" />
          ))}
        </div>
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
          {[1, 2, 3, 4].map((i) => (
            <Card key={i} className="animate-pulse">
              <div className="h-6 bg-neutral-200 dark:bg-neutral-700 rounded w-3/4 mb-4" />
              <div className="space-y-3">
                {[1, 2, 3].map((j) => (
                  <div key={j} className="h-4 bg-neutral-200 dark:bg-neutral-700 rounded" />
                ))}
              </div>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Dashboard</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Welcome back, {user?.firstName}! Here's what's happening.</p>
        </div>
        <div className="flex gap-3">
          <Button variant="outline" onClick={() => navigate('/students')}>View All Students</Button>
          <Button onClick={() => navigate('/students/new')}>Add Student</Button>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6">
        {getStatCards().map((stat, index) => (
          <StatCard
            key={index}
            label={stat.label}
            value={stat.value}
            change={stat.change}
            trend={stat.trend}
            icon={stat.icon}
            color={stat.color}
          />
        ))}
      </div>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        {hasRole(['ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR']) && recentStudents.length > 0 && (
          <Card>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50">Recent Students</h3>
              <Button variant="ghost" size="sm" onClick={() => navigate('/students')}>View All</Button>
            </div>
            <div className="space-y-3">
              {recentStudents.slice(0, 3).map((student) => (
                <div key={student.id} className="flex items-center justify-between py-2 border-b border-neutral-100 dark:border-neutral-800 last:border-0">
                  <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-full bg-brand-100 dark:bg-brand-900/30 flex items-center justify-center text-brand-700 dark:text-brand-300 font-medium">
                      {student.firstName[0]}{student.lastName[0]}
                    </div>
                    <div>
                      <p className="font-medium text-sm text-neutral-900 dark:text-neutral-50">{student.firstName} {student.lastName}</p>
                      <p className="text-xs text-neutral-500 dark:text-neutral-400">{student.studentId}</p>
                    </div>
                  </div>
                  <Badge variant={student.status === 'ACTIVE' ? 'success' : 'default'} size="sm">{student.status}</Badge>
                </div>
              ))}
            </div>
          </Card>
        )}

        {hasRole(['ADMIN', 'DEPARTMENT_HEAD']) && recentFaculty.length > 0 && (
          <Card>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50">Recent Faculty</h3>
              <Button variant="ghost" size="sm" onClick={() => navigate('/faculty')}>View All</Button>
            </div>
            <div className="space-y-3">
              {recentFaculty.slice(0, 3).map((faculty) => (
                <div key={faculty.id} className="flex items-center justify-between py-2 border-b border-neutral-100 dark:border-neutral-800 last:border-0">
                  <div className="flex items-center gap-3">
                    <div className="h-10 w-10 rounded-full bg-success-100 dark:bg-success-900/30 flex items-center justify-center text-success-700 dark:text-success-300 font-medium">
                      {faculty.firstName[0]}{faculty.lastName[0]}
                    </div>
                    <div>
                      <p className="font-medium text-sm text-neutral-900 dark:text-neutral-50">{faculty.firstName} {faculty.lastName}</p>
                      <p className="text-xs text-neutral-500 dark:text-neutral-400">{faculty.employeeId}</p>
                    </div>
                  </div>
                  <Badge variant={faculty.status === 'ACTIVE' ? 'success' : 'default'} size="sm">{faculty.status}</Badge>
                </div>
              ))}
            </div>
          </Card>
        )}

        {recentCourses.length > 0 && (
          <Card>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50">Recent Courses</h3>
              <Button variant="ghost" size="sm" onClick={() => navigate('/courses')}>View All</Button>
            </div>
            <div className="space-y-3">
              {recentCourses.slice(0, 3).map((course) => (
                <div key={course.id} className="flex items-center justify-between py-2 border-b border-neutral-100 dark:border-neutral-800 last:border-0">
                  <div>
                    <p className="font-medium text-sm text-neutral-900 dark:text-neutral-50">{course.code} - {course.name}</p>
                    <p className="text-xs text-neutral-500 dark:text-neutral-400">{course.credits} credits • Semester {course.semester}</p>
                  </div>
                  <Badge variant={course.isActive ? 'success' : 'default'} size="sm">{course.type}</Badge>
                </div>
              ))}
            </div>
          </Card>
        )}

        {recentEnrollments.length > 0 && (
          <Card>
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50">Recent Enrollments</h3>
              <Button variant="ghost" size="sm" onClick={() => navigate('/enrollments')}>View All</Button>
            </div>
            <div className="space-y-3">
              {recentEnrollments.slice(0, 3).map((enrollment) => (
                <div key={enrollment.id} className="flex items-center justify-between py-2 border-b border-neutral-100 dark:border-neutral-800 last:border-0">
                  <div className="text-sm">
                    <p className="font-medium text-neutral-900 dark:text-neutral-50">Enrollment #{enrollment.id.slice(-6)}</p>
                    <p className="text-xs text-neutral-500 dark:text-neutral-400">{new Date(enrollment.enrollmentDate).toLocaleDateString()}</p>
                  </div>
                  <Badge variant={enrollment.status === 'ENROLLED' ? 'success' : enrollment.status === 'COMPLETED' ? 'info' : 'default'} size="sm">{enrollment.status}</Badge>
                </div>
              ))}
            </div>
          </Card>
        )}
      </div>
    </div>
  );
}