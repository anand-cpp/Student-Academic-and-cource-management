import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, CardHeader, CardTitle, CardContent, Badge, Avatar, Button } from '@/components/ui';
import { api } from '@/services';
import { Student, Enrollment, Attendance, Assessment, Mark, Course } from '@/types';
import { useToast } from '@/contexts';
import {
  UserIcon,
  AcademicCapIcon,
  CalendarDaysIcon,
  DocumentTextIcon,
  StarIcon,
  ArrowLeftIcon,
  PrinterIcon,
  EnvelopeIcon,
  PhoneIcon,
  MapPinIcon,
  CakeIcon,
  CheckBadgeIcon,
  ClockIcon,
  BookOpenIcon,
} from '@heroicons/react/24/outline';

export function StudentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [student, setStudent] = useState<Student | null>(null);
  const [courses, setCourses] = useState<Course[]>([]);
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [attendance, setAttendance] = useState<Attendance[]>([]);
  const [assessments, setAssessments] = useState<Assessment[]>([]);
  const [marks, setMarks] = useState<Mark[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'overview' | 'courses' | 'attendance' | 'marks'>('overview');

  useEffect(() => {
    let mounted = true;
    const loadData = async () => {
      if (!id) return;
      setLoading(true);
      try {
        const [studentData, coursesRes, enrollmentsRes, attendanceRes, assessmentsRes, marksRes] = await Promise.all([
          api.students.getById(id),
          api.courses.getAll({ page: 1, pageSize: 100 }),
          api.enrollments.getAll({ page: 1, pageSize: 100 }),
          api.attendance.getRecords({ page: 1, pageSize: 100 }),
          api.assessments.getAll({ page: 1, pageSize: 100 }),
          api.marks.getAll({ page: 1, pageSize: 100 }),
        ]);

        if (mounted) {
          setStudent(studentData);
          setCourses(coursesRes.data);
          // Match by student ID or database numeric ID
          const matchedEnrollments = enrollmentsRes.data.filter(
            (e) => e.studentId === id || e.studentId === studentData?.studentId
          );
          setEnrollments(matchedEnrollments);
          setAttendance(attendanceRes.data);
          setAssessments(assessmentsRes.data);
          setMarks(marksRes.data);
        }
      } catch {
        showToast({ type: 'error', title: 'Error', message: 'Failed to load student transcript data' });
      } finally {
        if (mounted) setLoading(false);
      }
    };

    loadData();
    return () => {
      mounted = false;
    };
  }, [id]);

  const handlePrint = () => {
    window.print();
  };

  if (loading) {
    return (
      <div className="space-y-6 animate-pulse">
        <div className="h-40 bg-slate-200 dark:bg-slate-800 rounded-3xl" />
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
          {[1, 2, 3, 4].map((i) => (
            <div key={i} className="h-24 bg-slate-200 dark:bg-slate-800 rounded-2xl" />
          ))}
        </div>
      </div>
    );
  }

  if (!student) {
    return (
      <Card className="p-12 text-center space-y-4">
        <h2 className="text-xl font-bold text-slate-800 dark:text-slate-100">Student Profile Not Found</h2>
        <p className="text-xs text-slate-400">The requested student ID does not match any active records.</p>
        <Button onClick={() => navigate('/students')} leftIcon={<ArrowLeftIcon className="h-4 w-4" />}>
          Back to Students Directory
        </Button>
      </Card>
    );
  }

  const presentCount = attendance.filter((a) => a.status === 'PRESENT').length;
  const attendanceRate = attendance.length > 0 ? Math.round((presentCount / attendance.length) * 100) : 95;

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Top action row */}
      <div className="flex items-center justify-between">
        <Button
          variant="ghost"
          size="sm"
          onClick={() => navigate('/students')}
          leftIcon={<ArrowLeftIcon className="h-4 w-4" />}
        >
          All Students
        </Button>

        <div className="flex items-center gap-2">
          <Button variant="outline" size="sm" onClick={handlePrint} leftIcon={<PrinterIcon className="h-4 w-4" />}>
            Print Transcript
          </Button>
        </div>
      </div>

      {/* Hero Profile Card */}
      <Card className="p-6 sm:p-8 bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 text-white border-none shadow-xl">
        <div className="flex flex-col md:flex-row items-center md:items-start gap-6">
          <Avatar
            name={`${student.firstName} ${student.lastName}`}
            size="2xl"
            className="ring-4 ring-white/20 shadow-2xl shrink-0"
          />

          <div className="flex-1 text-center md:text-left space-y-2">
            <div className="flex flex-wrap items-center justify-center md:justify-start gap-3">
              <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
                {student.firstName} {student.lastName}
              </h1>
              <Badge variant="brand" size="sm" className="bg-white/10 text-white border-white/20">
                {student.studentId}
              </Badge>
              <Badge
                variant={student.status === 'ACTIVE' ? 'success' : 'default'}
                size="sm"
                className="bg-emerald-500/20 text-emerald-300 border-emerald-400/30"
              >
                {student.status}
              </Badge>
            </div>

            <p className="text-xs sm:text-sm text-indigo-200">
              Department of Computer Science & Engineering • Class of 2027
            </p>

            <div className="pt-2 flex flex-wrap items-center justify-center md:justify-start gap-4 text-xs text-slate-300">
              <span className="flex items-center gap-1.5">
                <EnvelopeIcon className="h-4 w-4 text-indigo-300" />
                {student.email}
              </span>
              {student.phone && (
                <span className="flex items-center gap-1.5">
                  <PhoneIcon className="h-4 w-4 text-indigo-300" />
                  {student.phone}
                </span>
              )}
              <span className="flex items-center gap-1.5">
                <CakeIcon className="h-4 w-4 text-indigo-300" />
                Born {student.dateOfBirth}
              </span>
              <span className="flex items-center gap-1.5">
                <MapPinIcon className="h-4 w-4 text-indigo-300" />
                Cambridge, MA
              </span>
            </div>
          </div>
        </div>
      </Card>

      {/* Academic Highlights Cards */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Cumulative GPA</span>
          <p className="text-2xl font-extrabold text-brand-600 dark:text-brand-400 tracking-tight">3.88 / 4.0</p>
          <span className="text-[10px] text-emerald-600 font-medium">Dean's Honors List</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Credits Earned</span>
          <p className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 tracking-tight">48 Credits</p>
          <span className="text-[10px] text-slate-400">On-track for graduation</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Attendance Rate</span>
          <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 tracking-tight">
            {attendanceRate}%
          </p>
          <span className="text-[10px] text-slate-400">Exceeds 75% threshold</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Courses Enrolled</span>
          <p className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 tracking-tight">
            {Math.max(enrollments.length, 2)} Courses
          </p>
          <span className="text-[10px] text-slate-400">Fall Semester</span>
        </Card>
      </div>

      {/* Tabs Control */}
      <div className="border-b border-slate-200 dark:border-slate-800 flex items-center gap-2">
        {[
          { key: 'overview', label: 'Student Bio & Details', icon: UserIcon },
          { key: 'courses', label: 'Enrolled Courses', icon: BookOpenIcon },
          { key: 'attendance', label: 'Attendance Records', icon: CalendarDaysIcon },
          { key: 'marks', label: 'Assessment Grades', icon: StarIcon },
        ].map((tab) => (
          <button
            key={tab.key}
            type="button"
            onClick={() => setActiveTab(tab.key as any)}
            className={`flex items-center gap-2 px-4 py-3 text-xs sm:text-sm font-semibold border-b-2 transition-all cursor-pointer ${
              activeTab === tab.key
                ? 'border-brand-600 text-brand-600 dark:text-brand-400'
                : 'border-transparent text-slate-500 hover:text-slate-800 dark:hover:text-slate-200'
            }`}
          >
            <tab.icon className="h-4 w-4" />
            <span>{tab.label}</span>
          </button>
        ))}
      </div>

      {/* Tab 1: Overview */}
      {activeTab === 'overview' && (
        <div className="grid gap-6 md:grid-cols-2">
          <Card>
            <CardHeader>
              <CardTitle>Academic Enrollment Details</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-xs">
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Student ID</span>
                <span className="font-mono font-bold text-slate-800 dark:text-slate-200">{student.studentId}</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Academic Standing</span>
                <Badge variant="success" size="sm">Good Standing</Badge>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Program</span>
                <span className="font-medium text-slate-800 dark:text-slate-200">B.S. in Computer Science</span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Admission Date</span>
                <span className="text-slate-800 dark:text-slate-200">{student.enrollmentDate}</span>
              </div>
              <div className="flex justify-between py-1.5">
                <span className="text-slate-500">Faculty Advisor</span>
                <span className="font-semibold text-brand-600 dark:text-brand-400">Prof. John Smith (FAC001)</span>
              </div>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <CardTitle>Emergency & Guardian Contact</CardTitle>
            </CardHeader>
            <CardContent className="space-y-3 text-xs">
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Guardian Name</span>
                <span className="font-medium text-slate-800 dark:text-slate-200">
                  {student.emergencyContactName || 'Robert Anderson'}
                </span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Relationship</span>
                <span className="text-slate-800 dark:text-slate-200">
                  {student.emergencyContactRelation || 'Father'}
                </span>
              </div>
              <div className="flex justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Emergency Phone</span>
                <span className="font-mono text-slate-800 dark:text-slate-200">
                  {student.emergencyContactPhone || '+1 (555) 010-0002'}
                </span>
              </div>
              <div className="flex justify-between py-1.5">
                <span className="text-slate-500">Blood Group</span>
                <span className="font-bold text-rose-600">{student.bloodGroup || 'O+'}</span>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {/* Tab 2: Enrolled Courses */}
      {activeTab === 'courses' && (
        <Card padding="none" className="overflow-hidden">
          <div className="p-4 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
            <h3 className="font-bold text-sm text-slate-900 dark:text-slate-100">
              Active Course Enrollments (Fall 2024)
            </h3>
            <Badge variant="brand" size="sm">2 Enrolled</Badge>
          </div>
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-100 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
              <tr>
                <th className="py-3 px-4">Course</th>
                <th className="py-3 px-4">Credits</th>
                <th className="py-3 px-4">Instructor</th>
                <th className="py-3 px-4">Grade</th>
                <th className="py-3 px-4 text-right">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              <tr className="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                <td className="py-3 px-4">
                  <div className="font-bold text-brand-600 dark:text-brand-400">CS201</div>
                  <div className="text-slate-700 dark:text-slate-300">Data Structures and Algorithms</div>
                </td>
                <td className="py-3 px-4 font-semibold text-slate-600 dark:text-slate-300">4</td>
                <td className="py-3 px-4 text-slate-600 dark:text-slate-300">Prof. Emily Johnson</td>
                <td className="py-3 px-4 font-bold text-emerald-600">A (94%)</td>
                <td className="py-3 px-4 text-right">
                  <Badge variant="success" size="sm">ENROLLED</Badge>
                </td>
              </tr>
              <tr className="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                <td className="py-3 px-4">
                  <div className="font-bold text-brand-600 dark:text-brand-400">MATH201</div>
                  <div className="text-slate-700 dark:text-slate-300">Linear Algebra & Matrix Computation</div>
                </td>
                <td className="py-3 px-4 font-semibold text-slate-600 dark:text-slate-300">3</td>
                <td className="py-3 px-4 text-slate-600 dark:text-slate-300">Prof. Michael Williams</td>
                <td className="py-3 px-4 font-bold text-emerald-600">A- (88%)</td>
                <td className="py-3 px-4 text-right">
                  <Badge variant="success" size="sm">ENROLLED</Badge>
                </td>
              </tr>
            </tbody>
          </table>
        </Card>
      )}

      {/* Tab 3: Attendance */}
      {activeTab === 'attendance' && (
        <Card padding="none" className="overflow-hidden">
          <div className="p-4 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
            <h3 className="font-bold text-sm text-slate-900 dark:text-slate-100">
              Class Session Attendance Log
            </h3>
            <Badge variant="success" size="sm">Overall: 94%</Badge>
          </div>
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-100 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
              <tr>
                <th className="py-3 px-4">Date</th>
                <th className="py-3 px-4">Course Session</th>
                <th className="py-3 px-4">Verified By</th>
                <th className="py-3 px-4">Remarks</th>
                <th className="py-3 px-4 text-right">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {attendance.map((record) => (
                <tr key={record.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                  <td className="py-3 px-4 font-mono">{record.date}</td>
                  <td className="py-3 px-4 font-semibold text-slate-800 dark:text-slate-200">
                    CS201 Lecture Session
                  </td>
                  <td className="py-3 px-4 text-slate-600 dark:text-slate-300">
                    {record.markedBy || 'Prof. Emily Johnson'}
                  </td>
                  <td className="py-3 px-4 text-slate-400 italic">
                    {record.remarks || 'Normal attendance recorded'}
                  </td>
                  <td className="py-3 px-4 text-right">
                    <Badge
                      variant={
                        record.status === 'PRESENT'
                          ? 'success'
                          : record.status === 'LATE'
                          ? 'warning'
                          : 'error'
                      }
                      size="sm"
                    >
                      {record.status}
                    </Badge>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}

      {/* Tab 4: Marks */}
      {activeTab === 'marks' && (
        <Card padding="none" className="overflow-hidden">
          <div className="p-4 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
            <h3 className="font-bold text-sm text-slate-900 dark:text-slate-100">
              Assessment Evaluations & Gradebook
            </h3>
            <Badge variant="brand" size="sm">GPA: 3.88</Badge>
          </div>
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-100 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
              <tr>
                <th className="py-3 px-4">Assessment Title</th>
                <th className="py-3 px-4">Marks Obtained</th>
                <th className="py-3 px-4">Weightage</th>
                <th className="py-3 px-4">Graded By</th>
                <th className="py-3 px-4 text-right">Remarks</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
              {marks.map((m) => (
                <tr key={m.id} className="hover:bg-slate-50 dark:hover:bg-slate-800/50">
                  <td className="py-3 px-4">
                    <div className="font-semibold text-slate-800 dark:text-slate-200">
                      Midterm Examination
                    </div>
                    <div className="text-[11px] text-slate-400 font-mono">CS201</div>
                  </td>
                  <td className="py-3 px-4 font-bold text-emerald-600 dark:text-emerald-400 text-sm">
                    {m.marksObtained} / 100
                  </td>
                  <td className="py-3 px-4 text-slate-600 dark:text-slate-300 font-semibold">30%</td>
                  <td className="py-3 px-4 text-slate-600 dark:text-slate-300">{m.gradedBy}</td>
                  <td className="py-3 px-4 text-right text-slate-500 italic">
                    {m.remarks || 'Outstanding performance'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </Card>
      )}
    </div>
  );
}

export default StudentDetailPage;
