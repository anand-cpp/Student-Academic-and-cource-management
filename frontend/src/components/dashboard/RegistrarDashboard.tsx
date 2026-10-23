import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, Button, Badge, Avatar } from '@/components/ui';
import { useAuth } from '@/contexts';
import {
  UsersIcon,
  BookOpenIcon,
  ClipboardDocumentCheckIcon,
  DocumentTextIcon,
  PlusIcon,
  IdentificationIcon,
} from '@heroicons/react/24/outline';

export function RegistrarDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const recentRegistrations = [
    { studentId: 'CS2023001', name: 'Alice Anderson', dept: 'Computer Science', semester: 3, status: 'Active', enrolled: '3 Courses' },
    { studentId: 'CS2023002', name: 'Bob Baker', dept: 'Computer Science', semester: 3, status: 'Active', enrolled: '3 Courses' },
    { studentId: 'CS2022001', name: 'Carol Clark', dept: 'Computer Science', semester: 5, status: 'Active', enrolled: '3 Courses' },
    { studentId: 'MATH2023001', name: 'David Davis', dept: 'Mathematics', semester: 3, status: 'Active', enrolled: '3 Courses' },
    { studentId: 'PHYS2023001', name: 'Eva Evans', dept: 'Physics', semester: 3, status: 'Active', enrolled: '3 Courses' },
  ];

  const enrollmentCensus = [
    { code: 'CS101', name: 'Introduction to CS', enrolled: 24, capacity: 30, pct: '80%' },
    { code: 'CS201', name: 'Data Structures', enrolled: 20, capacity: 25, pct: '80%' },
    { code: 'MATH101', name: 'Calculus I', enrolled: 28, capacity: 30, pct: '93%' },
    { code: 'PHYS101', name: 'General Physics I', enrolled: 22, capacity: 25, pct: '88%' },
  ];

  return (
    <div className="space-y-6 animate-in fade-in duration-300">
      {/* Registrar Banner */}
      <div className="bg-gradient-to-r from-purple-950 via-slate-900 to-slate-900 text-white rounded-2xl p-6 sm:p-8 shadow-xl relative overflow-hidden border border-purple-800/50">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Avatar name={user?.fullName || 'Ms. Jennifer Wilson'} size="xl" className="border-2 border-purple-400/50 shadow-lg" />
            <div>
              <div className="flex items-center gap-2 mb-1">
                <Badge variant="primary" size="sm" className="bg-purple-500/20 text-purple-300 border-purple-500/40 font-mono">
                  OFFICE OF THE REGISTRAR
                </Badge>
                <span className="text-xs text-purple-200/70 font-mono">ENROLLMENT & ADMISSIONS</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-white">
                University Registrar Operations
              </h1>
              <p className="text-sm text-purple-100/80 mt-0.5">
                Registrar: {user?.fullName || 'Ms. Jennifer Wilson'} • Academic Census & Records Office
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/students')}
              className="bg-purple-950/60 border-purple-700/60 text-purple-200 hover:bg-purple-900"
            >
              <UsersIcon className="h-4 w-4 mr-1.5" />
              Student Directory
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={() => navigate('/enrollments')}
              className="bg-purple-500 hover:bg-purple-600 text-slate-950 font-bold"
            >
              <ClipboardDocumentCheckIcon className="h-4 w-4 mr-1.5" />
              Course Enrollments
            </Button>
          </div>
        </div>
      </div>

      {/* Registrar Census Metrics */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Enrolled Students</span>
            <UsersIcon className="h-4 w-4 text-purple-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">5 Students</span>
          <p className="text-[11px] text-slate-400 mt-0.5">Active Academic Standing</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Active Enrollments</span>
            <ClipboardDocumentCheckIcon className="h-4 w-4 text-purple-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">15 Records</span>
          <p className="text-[11px] text-slate-400 mt-0.5">Fall 2024 Semester</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Course Sections</span>
            <BookOpenIcon className="h-4 w-4 text-purple-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">8 Sections</span>
          <p className="text-[11px] text-slate-400 mt-0.5">85% Aggregate Capacity</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Transcript Records</span>
            <DocumentTextIcon className="h-4 w-4 text-purple-500" />
          </div>
          <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">100% Verified</span>
          <p className="text-[11px] text-emerald-500 font-medium mt-0.5">Zero Backlog</p>
        </Card>
      </div>

      {/* Main Grid: Student Admissions Ledger & Course Census */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Student Admissions Registry (2 Cols) */}
        <div className="lg:col-span-2 space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <div>
                <CardTitle>Student Dossier Ledger</CardTitle>
                <CardDescription>Verified matriculation records and active academic profiles.</CardDescription>
              </div>
              <Button size="sm" variant="outline" onClick={() => navigate('/students')}>
                <PlusIcon className="h-4 w-4 mr-1.5" /> Register Student
              </Button>
            </CardHeader>
            <CardContent className="space-y-3">
              {recentRegistrations.map((s) => (
                <div
                  key={s.studentId}
                  className="p-3.5 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs"
                >
                  <div className="flex items-center gap-3">
                    <Avatar name={s.name} size="sm" />
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-slate-900 dark:text-slate-100">{s.name}</span>
                        <Badge variant="neutral" size="sm" className="font-mono text-[10px]">{s.studentId}</Badge>
                      </div>
                      <p className="text-slate-500 dark:text-slate-400 mt-0.5">
                        {s.dept} • Semester {s.semester} • {s.enrolled}
                      </p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant="success" size="sm">{s.status}</Badge>
                    <Button size="sm" variant="ghost" onClick={() => navigate('/students')}>
                      Details
                    </Button>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>

        {/* Sidebar: Course Capacity Census (1 Col) */}
        <div className="space-y-4">
          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-base">Course Enrollment Census</CardTitle>
              <CardDescription>Section capacity utilization monitor</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3 text-xs">
              {enrollmentCensus.map((c) => (
                <div key={c.code} className="space-y-1">
                  <div className="flex justify-between">
                    <span className="font-semibold text-slate-800 dark:text-slate-200">{c.code} - {c.name}</span>
                    <span className="font-mono text-slate-500">{c.enrolled}/{c.capacity}</span>
                  </div>
                  <div className="w-full bg-slate-200 dark:bg-slate-700 h-1.5 rounded-full overflow-hidden">
                    <div className="bg-purple-500 h-full rounded-full" style={{ width: c.pct }} />
                  </div>
                </div>
              ))}
              <Button fullWidth variant="outline" size="sm" onClick={() => navigate('/reports')} className="mt-2">
                Census Report
              </Button>
            </CardContent>
          </Card>

          <Card className="border-purple-500/30 bg-purple-50/20 dark:bg-purple-950/20">
            <CardHeader className="pb-2">
              <div className="flex items-center gap-2 text-purple-600 dark:text-purple-400 font-bold text-xs uppercase tracking-wider">
                <IdentificationIcon className="h-4 w-4" /> Academic Records
              </div>
              <CardTitle className="text-sm">Official Transcripts</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2 text-xs text-slate-600 dark:text-slate-400">
              <p>
                Issue and authenticate official degree transcripts and verify course prerequisites across departments.
              </p>
              <Button fullWidth variant="outline" size="sm" onClick={() => navigate('/reports')}>
                Transcript Processing Hub
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
