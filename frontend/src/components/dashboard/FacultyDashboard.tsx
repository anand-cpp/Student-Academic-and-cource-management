import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, Button, Badge, Avatar } from '@/components/ui';
import { useAuth } from '@/contexts';
import {
  BookOpenIcon,
  UsersIcon,
  CheckBadgeIcon,
  CalendarDaysIcon,
  ClockIcon,
  SparklesIcon,
  PlusIcon,
  ClipboardDocumentCheckIcon,
} from '@heroicons/react/24/outline';

export function FacultyDashboard() {
  const navigate = useNavigate();
  const { user } = useAuth();
  const [activeSessionMarked, setActiveSessionMarked] = useState(false);

  const assignedCourses = [
    { code: 'CS101', title: 'Introduction to Computer Science', schedule: 'Mon, Wed 09:00 - 10:30', room: 'Hall CS-301', enrolled: 24, max: 30, attendanceRate: '95.2%' },
    { code: 'CS201', title: 'Data Structures and Algorithms', schedule: 'Tue, Thu 11:00 - 12:30', room: 'Lab Turing-1', enrolled: 20, max: 25, attendanceRate: '92.8%' },
    { code: 'CS401', title: 'Artificial Intelligence', schedule: 'Fri 14:00 - 17:00', room: 'Seminar Room 4', enrolled: 15, max: 20, attendanceRate: '96.0%' },
  ];

  const pendingGrading = [
    { course: 'CS101', assessment: 'Quiz 1: Variables & Data Types', submissions: 24, graded: 24, status: 'Completed' },
    { course: 'CS101', assessment: 'Midterm Examination', submissions: 24, graded: 18, status: 'In Progress' },
    { course: 'CS201', assessment: 'Assignment 2: Binary Search Trees', submissions: 20, graded: 8, status: 'Pending Review' },
  ];

  return (
    <div className="space-y-6 animate-in fade-in duration-300">
      {/* Faculty Workload Header Banner */}
      <div className="bg-gradient-to-r from-emerald-900 via-teal-900 to-slate-900 text-white rounded-2xl p-6 sm:p-8 shadow-xl relative overflow-hidden border border-emerald-800/60">
        <div className="relative z-10 flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            <Avatar name={user?.fullName || 'Prof. Faculty'} size="xl" className="border-2 border-emerald-400/40 shadow-lg" />
            <div>
              <div className="flex items-center gap-2 mb-1">
                <Badge variant="success" size="sm" className="bg-emerald-500/20 text-emerald-300 border-emerald-500/40 font-mono">
                  FACULTY APPOINTMENT
                </Badge>
                <span className="text-xs text-emerald-200/70">DEPARTMENT OF COMPUTER SCIENCE</span>
              </div>
              <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-white">
                {user?.fullName || 'Prof. John Smith'}
              </h1>
              <p className="text-sm text-emerald-100/80 mt-0.5">
                Professor of Computer Science • Office: CS-301 • Office Hours: Mon/Wed 14:00 - 16:00
              </p>
            </div>
          </div>
          <div className="flex items-center gap-2.5">
            <Button
              variant="outline"
              size="sm"
              onClick={() => navigate('/attendance')}
              className="bg-emerald-950/60 border-emerald-700/60 text-emerald-200 hover:bg-emerald-900"
            >
              <CalendarDaysIcon className="h-4 w-4 mr-1.5" />
              Roll Call Register
            </Button>
            <Button
              variant="primary"
              size="sm"
              onClick={() => navigate('/marks')}
              className="bg-emerald-500 hover:bg-emerald-600 text-slate-950 font-bold"
            >
              Gradebook
            </Button>
          </div>
        </div>
      </div>

      {/* Teaching Workload Metrics */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
        {[
          { label: 'Assigned Courses', value: '3 Courses', icon: BookOpenIcon, subtext: '9 Credit Hours' },
          { label: 'Total Enrolled Students', value: '59 Students', icon: UsersIcon, subtext: 'Across 3 Sections' },
          { label: 'Pending Evaluations', value: '18 Papers', icon: ClockIcon, subtext: 'Midterm Grading' },
          { label: 'Overall Attendance', value: '94.7%', icon: CheckBadgeIcon, subtext: 'Above Target (90%)' },
        ].map((item, i) => (
          <Card key={i} className="p-4 border-slate-200/80 dark:border-slate-800">
            <div className="flex items-center justify-between text-xs text-slate-500 mb-1">
              <span>{item.label}</span>
              <item.icon className="h-4 w-4 text-emerald-600 dark:text-emerald-400" />
            </div>
            <p className="text-xl font-bold font-mono text-slate-900 dark:text-slate-100">{item.value}</p>
            <p className="text-[11px] text-slate-400 mt-0.5">{item.subtext}</p>
          </Card>
        ))}
      </div>

      {/* Main Grid: My Active Courses & Quick Lecture Roll-Call */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Assigned Courses (2 Cols) */}
        <div className="lg:col-span-2 space-y-4">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between pb-3">
              <div>
                <CardTitle>My Teaching Workload & Courses</CardTitle>
                <CardDescription>Active course sections assigned for Academic Year 2024-2025.</CardDescription>
              </div>
              <Button size="sm" variant="outline" onClick={() => navigate('/courses')}>
                View Catalog
              </Button>
            </CardHeader>
            <CardContent className="space-y-3">
              {assignedCourses.map((c) => (
                <div
                  key={c.code}
                  className="p-4 rounded-xl border border-slate-200 dark:border-slate-800 bg-white dark:bg-slate-900 hover:border-emerald-500 transition-all flex flex-col sm:flex-row sm:items-center justify-between gap-3"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <span className="font-mono font-bold text-emerald-600 dark:text-emerald-400 text-sm">
                        {c.code}
                      </span>
                      <span className="font-bold text-slate-900 dark:text-slate-100 text-sm">{c.title}</span>
                    </div>
                    <div className="flex items-center gap-4 text-xs text-slate-500 dark:text-slate-400 mt-1">
                      <span className="flex items-center gap-1">
                        <CalendarDaysIcon className="h-3.5 w-3.5" /> {c.schedule}
                      </span>
                      <span>•</span>
                      <span>{c.room}</span>
                    </div>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="text-right">
                      <span className="text-xs font-mono font-bold text-slate-800 dark:text-slate-200">
                        {c.enrolled}/{c.max}
                      </span>
                      <p className="text-[10px] text-slate-400">Enrolled</p>
                    </div>
                    <Button
                      size="sm"
                      variant="outline"
                      onClick={() => navigate('/attendance')}
                      className="text-xs"
                    >
                      Roll Call
                    </Button>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>

          {/* Evaluation & Grading Queue */}
          <Card>
            <CardHeader>
              <CardTitle>Evaluation & Grading Queue</CardTitle>
              <CardDescription>Track grading deadlines and student submission evaluation status.</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              {pendingGrading.map((item, idx) => (
                <div
                  key={idx}
                  className="p-3 rounded-xl bg-slate-50 dark:bg-slate-800/50 border border-slate-200/80 dark:border-slate-800 flex items-center justify-between gap-3 text-xs"
                >
                  <div>
                    <div className="flex items-center gap-2">
                      <Badge variant="neutral" size="sm" className="font-mono text-[10px]">{item.course}</Badge>
                      <span className="font-semibold text-slate-800 dark:text-slate-200">{item.assessment}</span>
                    </div>
                    <p className="text-slate-500 mt-0.5">
                      Progress: {item.graded} of {item.submissions} papers scored
                    </p>
                  </div>
                  <div className="flex items-center gap-2">
                    <Badge variant={item.status === 'Completed' ? 'success' : item.status === 'In Progress' ? 'warning' : 'neutral'} size="sm">
                      {item.status}
                    </Badge>
                    <Button size="sm" variant="ghost" onClick={() => navigate('/marks')}>
                      Grade
                    </Button>
                  </div>
                </div>
              ))}
            </CardContent>
          </Card>
        </div>

        {/* Quick Classroom Operations Sidebar (1 Col) */}
        <div className="space-y-4">
          <Card className="border-emerald-500/40 bg-emerald-50/20 dark:bg-emerald-950/20">
            <CardHeader className="pb-2">
              <div className="flex items-center gap-2 text-emerald-600 dark:text-emerald-400 font-bold text-xs uppercase tracking-wider">
                <SparklesIcon className="h-4 w-4" /> Live Lecture Action
              </div>
              <CardTitle className="text-base">CS101 Today's Attendance</CardTitle>
              <CardDescription>Session 6: Arrays and Pointer Memory</CardDescription>
            </CardHeader>
            <CardContent className="space-y-3">
              <p className="text-xs text-slate-600 dark:text-slate-400">
                Ready to take attendance for the current lecture section (24 registered students).
              </p>
              {activeSessionMarked ? (
                <div className="p-3 rounded-lg bg-emerald-100 dark:bg-emerald-900/60 text-emerald-800 dark:text-emerald-200 text-xs flex items-center gap-2">
                  <CheckBadgeIcon className="h-5 w-5 flex-shrink-0 text-emerald-600 dark:text-emerald-400" />
                  <span>Session recorded! 23 Present, 1 Absent.</span>
                </div>
              ) : (
                <Button
                  fullWidth
                  variant="primary"
                  className="bg-emerald-600 hover:bg-emerald-700 text-white"
                  onClick={() => setActiveSessionMarked(true)}
                >
                  <ClipboardDocumentCheckIcon className="h-4 w-4 mr-1.5" />
                  Mark All Present (Instant)
                </Button>
              )}
              <Button fullWidth variant="outline" size="sm" onClick={() => navigate('/attendance')}>
                Manual Detailed Roll Call
              </Button>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="pb-2">
              <CardTitle className="text-sm">Course Assessment Rubrics</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2 text-xs">
              <p className="text-slate-500 dark:text-slate-400">
                Setup weighted grading policies, quizzes, and project milestones for your courses.
              </p>
              <Button fullWidth variant="outline" size="sm" onClick={() => navigate('/assessments')}>
                <PlusIcon className="h-4 w-4 mr-1.5" /> New Assessment Rubric
              </Button>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
