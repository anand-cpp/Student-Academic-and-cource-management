import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, Button, Badge, Avatar } from '@/components/ui';
import { useAuth } from '@/contexts';
import {
  AcademicCapIcon,
  BookOpenIcon,
  UsersIcon,
  ArrowTrendingUpIcon,
  PlusIcon,
  ChartBarIcon,
} from '@heroicons/react/24/outline';

export function DeptHeadDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const deptCourses = [
    { code: 'CS101', title: 'Introduction to Computer Science', faculty: 'Prof. John Smith', credits: 3, enrolled: 24, max: 30, saturation: '80%' },
    { code: 'CS201', title: 'Data Structures and Algorithms', faculty: 'Prof. John Smith', credits: 4, enrolled: 20, max: 25, saturation: '80%' },
    { code: 'CS301', title: 'Machine Learning', faculty: 'Prof. Emily Johnson', credits: 4, enrolled: 18, max: 20, saturation: '90%' },
    { code: 'CS401', title: 'Artificial Intelligence', faculty: 'Prof. John Smith', credits: 3, enrolled: 15, max: 20, saturation: '75%' },
  ];

  const facultyRoster = [
    { name: 'Prof. John Smith', designation: 'Professor', specialization: 'Artificial Intelligence', courses: 3, email: 'smith@university.edu' },
    { name: 'Prof. Emily Johnson', designation: 'Associate Professor', specialization: 'Data Structures & Algorithms', courses: 2, email: 'johnson@university.edu' },
  ];

  return (
    <div className="space-y-6 animate-in fade-in duration-300">
      {/* Department Banner */}
      <div className="bg-gradient-to-r from-amber-950 via-slate-900 to-slate-900 text-white rounded-2xl p-6 sm:p-8 shadow-xl relative overflow-hidden border border-amber-800/50">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Avatar name={user?.fullName || 'Dr. Robert Davis'} size="xl" className="border-2 border-amber-400/50 shadow-lg" />
            <div>
              <div className="flex items-center gap-2 mb-1">
                <Badge variant="warning" size="sm" className="bg-amber-500/20 text-amber-300 border-amber-500/40 font-mono">
                  DEPARTMENT HEAD
                </Badge>
                <span className="text-xs text-amber-200/70 font-mono">CODE: CS • ENGINEERING DIVISION</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-white">
                Department of Computer Science & Engineering
              </h1>
              <p className="text-sm text-amber-100/80 mt-0.5">
                Chair: {user?.fullName || 'Dr. Robert Davis'} • Academic Year 2024-2025 • Active Term: Fall
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/courses')}
              className="bg-amber-950/60 border-amber-700/60 text-amber-200 hover:bg-amber-900"
            >
              <BookOpenIcon className="h-4 w-4 mr-1.5" />
              Manage Curriculum
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={() => navigate('/faculty')}
              className="bg-amber-500 hover:bg-amber-600 text-slate-950 font-bold"
            >
              <AcademicCapIcon className="h-4 w-4 mr-1.5" />
              Faculty Roster
            </Button>
          </div>
        </div>
      </div>

      {/* Department Key Performance Indicators */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Department Faculty</span>
            <AcademicCapIcon className="h-4 w-4 text-amber-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">4 Staff</span>
          <p className="text-[11px] text-slate-400 mt-0.5">2 Professors, 2 Associates</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Course Offerings</span>
            <BookOpenIcon className="h-4 w-4 text-amber-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">4 Active</span>
          <p className="text-[11px] text-slate-400 mt-0.5">14 Total Credits</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Enrolled Students</span>
            <UsersIcon className="h-4 w-4 text-amber-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">77 Enrollments</span>
          <p className="text-[11px] text-slate-400 mt-0.5">Across All CS Sections</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Average Department GPA</span>
            <ArrowTrendingUpIcon className="h-4 w-4 text-emerald-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">3.52 / 4.0</span>
          <p className="text-[11px] text-emerald-500 font-medium mt-0.5">Nominal Cohort Standing</p>
        </Card>
      </div>

      {/* Main Grid: Curriculum Catalog & Faculty Staffing */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Curriculum Oversight (2 Cols) */}
        <div className="lg:col-span-2 space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <div>
                <CardTitle>Department Course Offerings & Capacity</CardTitle>
                <CardDescription>Curriculum workload and student enrollment capacity utilization.</CardDescription>
              </div>
              <Button size="sm" variant="outline" onClick={() => navigate('/courses')}>
                <PlusIcon className="h-4 w-4 mr-1.5" /> Add Course
              </Button>
            </CardHeader>
            <CardContent className="space-y-3">
              {deptCourses.map((c) => (
                <div
                  key={c.code}
                  className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-mono font-bold text-amber-600 dark:text-amber-400 text-sm">{c.code}</span>
                      <span className="font-bold text-slate-900 dark:text-slate-100 text-sm">{c.title}</span>
                      <Badge variant="neutral" size="sm" className="font-mono text-[10px]">{c.credits} cr</Badge>
                    </div>
                    <p className="text-slate-500 dark:text-slate-400 mt-1">
                      Instructor: <span className="font-semibold text-slate-700 dark:text-slate-300">{c.faculty}</span>
                    </p>
                  </div>
                  <div className="flex items-center gap-4">
                    <div className="text-right">
                      <span className="font-mono font-bold text-slate-800 dark:text-slate-200 text-sm">
                        {c.enrolled} / {c.max}
                      </span>
                      <p className="text-[10px] text-slate-400">Capacity ({c.saturation})</p>
                    </div>
                    <div className="w-16 bg-slate-200 dark:bg-slate-700 h-2 rounded-full overflow-hidden">
                      <div className="bg-amber-500 h-full rounded-full" style={{ width: c.saturation }} />
                    </div>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>

          {/* Department Faculty Overview */}
          <Card>
            <CardHeader>
              <CardTitle>Department Professorial Staff</CardTitle>
              <CardDescription>Active teaching allocations for Computer Science faculty.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              {facultyRoster.map((f, idx) => (
                <div
                  key={idx}
                  className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 flex items-center justify-between text-xs"
                >
                  <div className="flex items-center gap-3">
                    <Avatar name={f.name} size="sm" />
                    <div>
                      <span className="font-bold text-slate-900 dark:text-slate-100 text-sm">{f.name}</span>
                      <p className="text-slate-500">{f.designation} • {f.specialization}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant="success" size="sm">{f.courses} Courses</Badge>
                    <Button size="sm" variant="ghost" onClick={() => navigate('/faculty')}>
                      Profile
                    </Button>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>

        {/* Sidebar: Department Census & Approvals */}
        <div className="space-y-4">
          <Card>
            <CardHeader className="pb-2">
              <div className="flex items-center gap-2 text-amber-600 dark:text-amber-400 font-bold text-xs uppercase tracking-wider">
                <ChartBarIcon className="h-4 w-4" /> Department Analytics
              </div>
              <CardTitle className="text-base">Cohort Performance</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-xs">
              <div className="space-y-1.5">
                <div className="flex justify-between">
                  <span className="text-slate-500">Class Attendance Average</span>
                  <span className="font-bold text-slate-800 dark:text-slate-200 font-mono">94.8%</span>
                </div>
                <div className="w-full bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden">
                  <div className="bg-emerald-500 h-full rounded-full" style={{ width: '94.8%' }} />
                </div>
              </div>

              <div className="space-y-1.5">
                <div className="flex justify-between">
                  <span className="text-slate-500">Enrollment Saturation</span>
                  <span className="font-bold text-slate-800 dark:text-slate-200 font-mono">81.0%</span>
                </div>
                <div className="w-full bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden">
                  <div className="bg-amber-500 h-full rounded-full" style={{ width: '81.0%' }} />
                </div>
              </div>

              <Button fullWidth variant="outline" size="sm" onClick={() => navigate('/reports')} className="mt-2">
                Generate Department Report
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
