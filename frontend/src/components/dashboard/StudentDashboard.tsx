import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, Button, Badge, Avatar } from '@/components/ui';
import { useAuth } from '@/contexts';
import {
  BookOpenIcon,
  CalendarDaysIcon,
  StarIcon,
  DocumentArrowDownIcon,
  ClockIcon,
  ArrowTrendingUpIcon,
  AcademicCapIcon,
  SparklesIcon,
} from '@heroicons/react/24/outline';

export function StudentDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();

  const enrolledCourses = [
    { code: 'CS101', title: 'Introduction to Computer Science', credits: 3, faculty: 'Prof. John Smith', schedule: 'Mon, Wed 09:00 - 10:30', room: 'CS-301', attendance: '100%', grade: 'A' },
    { code: 'CS201', title: 'Data Structures and Algorithms', credits: 4, faculty: 'Prof. John Smith', schedule: 'Tue, Thu 11:00 - 12:30', room: 'Turing-1', attendance: '95%', grade: 'A-' },
    { code: 'MATH101', title: 'Calculus I', credits: 4, faculty: 'Prof. Michael Williams', schedule: 'Mon, Wed, Fri 13:00 - 14:00', room: 'MATH-201', attendance: '94%', grade: 'B+' },
  ];

  const recentScores = [
    { course: 'CS101', assessment: 'Quiz 1: Basics & Operators', obtained: 18, max: 20, pct: '90%', date: 'Jan 30' },
    { course: 'CS101', assessment: 'Assignment 1: Control Structures', obtained: 45, max: 50, pct: '90%', date: 'Feb 06' },
    { course: 'CS101', assessment: 'Midterm Examination', obtained: 85, max: 100, pct: '85%', date: 'Mar 02' },
  ];

  const upcomingDeadlines = [
    { title: 'CS201 Tree Traversal Lab Submission', due: 'Tomorrow, 23:59', type: 'Assignment', urgent: true },
    { title: 'MATH101 Derivative Applications Problem Set', due: 'In 3 days', type: 'Homework', urgent: false },
    { title: 'CS101 Final Exam Schedule', due: 'Apr 15', type: 'Exam', urgent: false },
  ];

  return (
    <div className="space-y-6 animate-in fade-in duration-300">
      {/* Student Academic Identity Hero */}
      <div className="bg-gradient-to-r from-sky-950 via-indigo-950 to-slate-900 text-white rounded-2xl p-6 sm:p-8 shadow-xl relative overflow-hidden border border-sky-800/40">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Avatar name={user?.fullName || 'Alice Anderson'} size="xl" className="border-2 border-sky-400/50 shadow-lg" />
            <div>
              <div className="flex items-center gap-2 mb-1">
                <Badge variant="info" size="sm" className="bg-sky-500/20 text-sky-300 border-sky-500/40 font-mono">
                  ROLL: CS2023001
                </Badge>
                <span className="text-xs text-sky-200/70 font-mono">SEMESTER 3 • ENROLLED</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-white">
                {user?.fullName || 'Alice Anderson'}
              </h1>
              <p className="text-sm text-sky-100/80 mt-0.5">
                B.S. in Computer Science • Academic Advisor: Prof. John Smith • Dean's Honor Roll
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/marks')}
              className="bg-sky-950/60 border-sky-700/60 text-sky-200 hover:bg-sky-900"
            >
              <StarIcon className="h-4 w-4 mr-1.5" />
              My Gradebook
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={() => navigate('/students/1')}
              className="bg-sky-500 hover:bg-sky-600 text-slate-950 font-bold"
            >
              <DocumentArrowDownIcon className="h-4 w-4 mr-1.5" />
              View Official Transcript
            </Button>
          </div>
        </div>
      </div>

      {/* Academic Performance KPI Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Cumulative GPA</span>
            <ArrowTrendingUpIcon className="h-4 w-4 text-emerald-500" />
          </div>
          <div className="flex items-baseline gap-1.5">
            <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">3.84</span>
            <span className="text-xs text-slate-400 font-mono">/ 4.00</span>
          </div>
          <p className="text-[11px] text-emerald-500 font-medium mt-0.5">Dean's Honor List</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Enrolled Credits</span>
            <BookOpenIcon className="h-4 w-4 text-sky-500" />
          </div>
          <div className="flex items-baseline gap-1.5">
            <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">11</span>
            <span className="text-xs text-slate-400 font-mono">Credits (Fall 2024)</span>
          </div>
          <p className="text-[11px] text-slate-400 mt-0.5">3 Enrolled Courses</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Attendance Compliance</span>
            <CalendarDaysIcon className="h-4 w-4 text-indigo-500" />
          </div>
          <div className="flex items-baseline gap-1.5">
            <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">96.4%</span>
            <span className="text-xs text-emerald-500 font-medium">Above Threshold</span>
          </div>
          <p className="text-[11px] text-slate-400 mt-0.5">Min Required: 75.0%</p>
        </Card>

        <Card className="p-4 border-slate-200/80 dark:border-slate-800">
          <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
            <span>Degree Progress</span>
            <AcademicCapIcon className="h-4 w-4 text-violet-500" />
          </div>
          <div className="flex items-baseline gap-1.5">
            <span className="text-2xl font-black font-mono text-slate-900 dark:text-slate-100">48 / 120</span>
            <span className="text-xs text-slate-400 font-mono">Credits</span>
          </div>
          <p className="text-[11px] text-slate-400 mt-0.5">Expected Grad: Spring 2026</p>
        </Card>
      </div>

      {/* Main Grid: My Current Classes & Deadlines */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Classes Table & Grades (2 Cols) */}
        <div className="lg:col-span-2 space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <div>
                <CardTitle>My Class Schedule & Courses</CardTitle>
                <CardDescription>Active course enrollments and classroom locations.</CardDescription>
              </div>
              <Button size="sm" variant="outline" onClick={() => navigate('/attendance')}>
                View Roll Call Logs
              </Button>
            </CardHeader>
            <CardContent className="space-y-3">
              {enrolledCourses.map((c) => (
                <div
                  key={c.code}
                  className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-sky-500 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-mono font-bold text-sky-600 dark:text-sky-400 text-sm">{c.code}</span>
                      <span className="font-bold text-slate-900 dark:text-slate-100 text-sm">{c.title}</span>
                      <Badge variant="neutral" size="sm" className="font-mono text-[10px]">{c.credits} cr</Badge>
                    </div>
                    <div className="flex items-center gap-4 text-xs text-slate-500 dark:text-slate-400 mt-1">
                      <span>{c.faculty}</span>
                      <span>•</span>
                      <span>{c.schedule}</span>
                      <span>•</span>
                      <span>{c.room}</span>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="text-right">
                      <span className="text-xs font-mono font-bold text-emerald-600 dark:text-emerald-400">{c.attendance}</span>
                      <p className="text-[10px] text-slate-400">Attendance</p>
                    </div>
                    <div className="w-10 h-10 rounded-lg bg-sky-50 dark:bg-sky-950/60 border border-sky-200 dark:border-sky-800 flex items-center justify-center font-bold font-mono text-sky-700 dark:text-sky-300">
                      {c.grade}
                    </div>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>

          {/* Recent Assessment Scores */}
          <Card>
            <CardHeader>
              <CardTitle>Recent Evaluated Assessments</CardTitle>
              <CardDescription>Verified scores recorded in the university registrar ledger.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              {recentScores.map((score, idx) => (
                <div
                  key={idx}
                  className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 flex items-center justify-between text-xs"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <Badge variant="neutral" size="sm" className="font-mono text-[10px]">{score.course}</Badge>
                      <span className="font-semibold text-slate-800 dark:text-slate-200">{score.assessment}</span>
                    </div>
                    <span className="text-[11px] text-slate-400">Graded on {score.date}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="font-mono font-bold text-slate-800 dark:text-slate-200 text-sm">
                      {score.obtained} / {score.max}
                    </span>
                    <Badge variant="success" size="sm" className="font-mono">{score.pct}</Badge>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>

        {/* Sidebar: Upcoming Deadlines & Security Protection Notice (1 Col) */}
        <div className="space-y-4">
          <Card>
            <CardHeader className="pb-2">
              <div className="flex items-center gap-2 text-sky-600 dark:text-sky-400 font-bold text-xs uppercase tracking-wider">
                <ClockIcon className="h-4 w-4" /> Academic Deadlines
              </div>
              <CardTitle className="text-base">Upcoming Due Dates</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              {upcomingDeadlines.map((d, i) => (
                <div
                  key={i}
                  className={`p-3 rounded-xl border text-xs ${
                    d.urgent
                      ? 'border-rose-200 dark:border-rose-900 bg-rose-50/40 dark:bg-rose-950/20'
                      : 'border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900'
                  }`}
                >
                  <div className="flex items-start justify-between gap-2">
                    <span className="font-semibold text-slate-800 dark:text-slate-200">{d.title}</span>
                    <Badge variant={d.urgent ? 'danger' : 'neutral'} size="sm" className="text-[10px] flex-shrink-0">
                      {d.due}
                    </Badge>
                  </div>
                  <span className="text-[11px] text-slate-400 block mt-1">{d.type}</span>
                </div>
              ))}
            </CardContent>
          </Card>

          {/* Privacy & IDOR Notice Card */}
          <Card className="border-sky-500/40 bg-sky-50/20 dark:bg-sky-950/20">
            <CardHeader className="pb-2">
              <div className="flex items-center gap-2 text-sky-600 dark:text-sky-400 font-bold text-xs uppercase tracking-wider">
                <SparklesIcon className="h-4 w-4" /> Privacy Guarantee
              </div>
              <CardTitle className="text-sm">Confidential Student Dossier</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2 text-xs text-slate-600 dark:text-slate-400">
              <p>
                Your academic transcripts, GPA metrics, and attendance records are cryptographically sealed with object-level Spring Security filters.
              </p>
              <Button fullWidth variant="outline" size="sm" onClick={() => navigate('/profile')}>
                Account Security & Password
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
