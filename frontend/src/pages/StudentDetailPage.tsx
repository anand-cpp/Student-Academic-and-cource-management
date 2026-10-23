import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Card, Badge, Avatar, Button, Tabs, TabPanel, Table } from '@/components';
import { api } from '@/services';
import { Student, Enrollment, Attendance, Assessment, Mark } from '@/types';
import { useToast } from '@/contexts';
import { 
  UserIcon, AcademicCapIcon, CalendarDaysIcon, DocumentTextIcon, StarIcon,
  ArrowLeftIcon, PencilIcon, PhoneIcon, EnvelopeIcon, MapPinIcon, CakeIcon,
  XMarkIcon
} from '@heroicons/react/24/outline';

export function StudentDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [student, setStudent] = useState<Student | null>(null);
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [attendance, setAttendance] = useState<Attendance[]>([]);
  const [assessments, setAssessments] = useState<Assessment[]>([]);
  const [marks, setMarks] = useState<Mark[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState('overview');

  useEffect(() => {
    if (id) {
      loadStudentData();
    }
  }, [id]);

  const loadStudentData = async () => {
    setLoading(true);
    try {
      const [studentData, enrollmentsData, attendanceData, assessmentsData, marksData] = await Promise.all([
        api.students.getById(id!),
        api.enrollments.getAll({ page: 1, pageSize: 100 }),
        api.attendance.getAll({ page: 1, pageSize: 100 }),
        api.assessments.getAll({ page: 1, pageSize: 100 }),
        api.marks.getAll({ page: 1, pageSize: 100 }),
      ]);
      setStudent(studentData);
      setEnrollments(enrollmentsData.data.filter(e => e.studentId === id));
      setAttendance(attendanceData.data.filter(a => {
        const enrollment = enrollmentsData.data.find(e => e.id === a.enrollmentId);
        return enrollment?.studentId === id;
      }));
      setAssessments(assessmentsData.data);
      setMarks(marksData.data.filter(m => {
        const enrollment = enrollmentsData.data.find(e => e.id === m.enrollmentId);
        return enrollment?.studentId === id;
      }));
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load student data' });
      navigate('/students');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="space-y-6 animate-pulse">
        <Card><div className="h-6 bg-neutral-200 dark:bg-neutral-700 rounded w-1/4" /></Card>
        <div className="grid gap-6 md:grid-cols-4">
          {[1,2,3,4].map(i => <Card key={i}><div className="h-4 bg-neutral-200 dark:bg-neutral-700 rounded w-full" /></Card>)}
        </div>
      </div>
    );
  }

  if (!student) {
    return (
      <div className="text-center py-12">
        <h2 className="text-xl font-semibold text-neutral-900 dark:text-neutral-50">Student not found</h2>
        <Button variant="secondary" onClick={() => navigate('/students')} className="mt-4">Back to Students</Button>
      </div>
    );
  }

  const enrollmentColumns = [
    { key: 'courseId', header: 'Course', accessor: (row: Enrollment) => row.courseId },
    { key: 'sectionId', header: 'Section', accessor: 'sectionId' },
    { key: 'enrollmentDate', header: 'Enrolled', accessor: (row: Enrollment) => new Date(row.enrollmentDate).toLocaleDateString() },
    { key: 'status', header: 'Status', accessor: 'status', render: (value: string) => (
      <Badge variant={value === 'ENROLLED' ? 'success' : value === 'COMPLETED' ? 'info' : 'default'} size="sm">{value}</Badge>
    )},
  ];

  const attendanceColumns = [
    { key: 'date', header: 'Date', accessor: (row: Attendance) => new Date(row.date).toLocaleDateString() },
    { key: 'status', header: 'Status', accessor: 'status', render: (value: string) => (
      <Badge variant={value === 'PRESENT' ? 'success' : value === 'ABSENT' ? 'error' : value === 'LATE' ? 'warning' : 'info'} size="sm">{value}</Badge>
    )},
  ];

  const marksColumns = [
    { key: 'assessmentId', header: 'Assessment', accessor: (row: Mark) => row.assessmentId },
    { key: 'marksObtained', header: 'Marks', accessor: 'marksObtained' },
    { key: 'gradedAt', header: 'Graded', accessor: (row: Mark) => new Date(row.gradedAt).toLocaleDateString() },
    { key: 'isPublished', header: 'Published', accessor: 'isPublished', render: (value: boolean) => (
      <Badge variant={value ? 'success' : 'default'} size="sm">{value ? 'Yes' : 'No'}</Badge>
    )},
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <Button variant="ghost" size="sm" onClick={() => navigate('/students')}>
          <ArrowLeftIcon className="h-5 w-5" />
          Back
        </Button>
        <div className="flex gap-3">
          <Button variant="outline"><PencilIcon className="h-5 w-5" /> Edit</Button>
        </div>
      </div>

      <Card className="p-6">
        <div className="flex flex-col md:flex-row md:items-center gap-6">
          <Avatar name={`${student.firstName} ${student.lastName}`} size="2xl" />
          <div className="flex-1 text-center md:text-left">
            <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">
              {student.firstName} {student.lastName}
            </h1>
            <p className="text-neutral-500 dark:text-neutral-400">{student.studentId}</p>
            <div className="mt-3 flex flex-wrap items-center justify-center md:justify-start gap-4 text-sm text-neutral-600 dark:text-neutral-400">
              <span className="flex items-center gap-1"><EnvelopeIcon className="h-4 w-4" /> {student.email}</span>
              {student.phone && <span className="flex items-center gap-1"><PhoneIcon className="h-4 w-4" /> {student.phone}</span>}
              <span className="flex items-center gap-1"><CakeIcon className="h-4 w-4" /> {new Date(student.dateOfBirth).toLocaleDateString()}</span>
            </div>
          </div>
          <div className="flex flex-wrap gap-2">
            <Badge variant={student.status === 'ACTIVE' ? 'success' : student.status === 'INACTIVE' ? 'default' : 'warning'} size="lg">
              {student.status}
            </Badge>
          </div>
        </div>
      </Card>

      <div className="grid gap-6 md:grid-cols-4">
        <StatCard label="Enrolled Courses" value={enrollments.length} icon={<AcademicCapIcon className="h-6 w-6" />} color="primary" />
        <StatCard label="Attendance Rate" value={`${Math.round(attendance.filter(a => a.status === 'PRESENT').length / Math.max(attendance.length, 1) * 100)}%`} icon={<CalendarDaysIcon className="h-6 w-6" />} color="success" />
        <StatCard label="Total Assessments" value={assessments.length} icon={<DocumentTextIcon className="h-6 w-6" />} color="info" />
        <StatCard label="Avg Marks" value={marks.length > 0 ? Math.round(marks.reduce((sum, m) => sum + m.marksObtained, 0) / marks.length) : 0} icon={<StarIcon className="h-6 w-6" />} color="warning" />
      </div>

      <Tabs tabs={[
        { value: 'overview', label: 'Overview', icon: <UserIcon className="h-5 w-5" /> },
        { value: 'enrollments', label: 'Enrollments', icon: <AcademicCapIcon className="h-5 w-5" /> },
        { value: 'attendance', label: 'Attendance', icon: <CalendarDaysIcon className="h-5 w-5" /> },
        { value: 'marks', label: 'Marks', icon: <StarIcon className="h-5 w-5" /> },
      ]} value={activeTab} onChange={setActiveTab} className="space-y-4">
        <TabPanel value="overview">
          <div className="grid gap-6 md:grid-cols-2">
            <Card>
              <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50 mb-4">Personal Information</h3>
              <dl className="space-y-3">
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Student ID</dt><dd className="font-medium">{student.studentId}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Email</dt><dd className="font-medium">{student.email}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Phone</dt><dd className="font-medium">{student.phone || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Date of Birth</dt><dd className="font-medium">{new Date(student.dateOfBirth).toLocaleDateString()}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Gender</dt><dd className="font-medium">{student.gender}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Blood Group</dt><dd className="font-medium">{student.bloodGroup || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Nationality</dt><dd className="font-medium">{student.nationality || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Enrollment Date</dt><dd className="font-medium">{new Date(student.enrollmentDate).toLocaleDateString()}</dd></div>
              </dl>
            </Card>
            <Card>
              <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50 mb-4">Guardian Information</h3>
              <dl className="space-y-3">
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Name</dt><dd className="font-medium">{student.guardianName || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Phone</dt><dd className="font-medium">{student.guardianPhone || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Email</dt><dd className="font-medium">{student.guardianEmail || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Relation</dt><dd className="font-medium">{student.guardianRelation || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Emergency Contact</dt><dd className="font-medium">{student.emergencyContactName || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Emergency Phone</dt><dd className="font-medium">{student.emergencyContactPhone || '—'}</dd></div>
                <div className="flex justify-between"><dt className="text-neutral-500 dark:text-neutral-400">Relation</dt><dd className="font-medium">{student.emergencyContactRelation || '—'}</dd></div>
              </dl>
            </Card>
          </div>
        </TabPanel>
        <TabPanel value="enrollments">
          <Table columns={enrollmentColumns} data={enrollments} keyAccessor="id" title={`${enrollments.length} enrollments`} />
        </TabPanel>
        <TabPanel value="attendance">
          <Table columns={attendanceColumns} data={attendance} keyAccessor="id" title={`${attendance.length} records`} />
        </TabPanel>
        <TabPanel value="marks">
          <Table columns={marksColumns} data={marks} keyAccessor="id" title={`${marks.length} records`} />
        </TabPanel>
      </Tabs>
    </div>
  );
}