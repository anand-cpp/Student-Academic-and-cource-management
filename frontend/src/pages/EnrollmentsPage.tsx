import { useEffect, useState } from 'react';
import { DataTable, Button, Modal, FormField, Card } from '@/components';
import { api } from '@/services';
import { PlusIcon, PencilIcon, TrashIcon, EyeIcon } from '@heroicons/react/24/outline';
import { Enrollment } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const enrollmentSchema = z.object({
  studentId: z.string().min(1, 'Student is required'),
  courseId: z.string().min(1, 'Course is required'),
  sectionId: z.string().min(1, 'Section is required'),
  facultyId: z.string().optional(),
});

type EnrollmentFormData = z.infer<typeof enrollmentSchema>;

export function EnrollmentsPage() {
  const { showToast } = useToast();
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingEnrollment, setEditingEnrollment] = useState<Enrollment | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Enrollment | null>(null);
  const [students, setStudents] = useState<Array<{id: string; name: string}>>([]);
  const [courses, setCourses] = useState<Array<{id: string; name: string}>>([]);
  const [sections, setSections] = useState<Array<{id: string; name: string}>>([]);
  const [faculty, setFaculty] = useState<Array<{id: string; name: string}>>([]);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<EnrollmentFormData>({
    resolver: zodResolver(enrollmentSchema),
  });

  useEffect(() => {
    loadEnrollments();
    loadReferenceData();
  }, []);

  const loadEnrollments = async () => {
    setLoading(true);
    try {
      const response = await api.enrollments.getAll({ page: 1, pageSize: 100 });
      setEnrollments(response.data);
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load enrollments' });
    } finally {
      setLoading(false);
    }
  };

  const loadReferenceData = async () => {
    try {
      const [studs, crses, secs, facs] = await Promise.all([
        api.students.getAll({ page: 1, pageSize: 100 }),
        api.courses.getAll({ page: 1, pageSize: 100 }),
        api.sections.getAll(),
        api.faculty.getAll({ page: 1, pageSize: 100 }),
      ]);
      setStudents(studs.data.map(s => ({ id: s.id, name: `${s.firstName} ${s.lastName} (${s.studentId})` })));
      setCourses(crses.data.map(c => ({ id: c.id, name: `${c.code} - ${c.name}` })));
      setSections(secs.map(s => ({ id: s.id, name: s.name })));
      setFaculty(facs.data.map(f => ({ id: f.id, name: `${f.firstName} ${f.lastName}` })));
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: EnrollmentFormData) => {
    try {
      await api.enrollments.create(data as any);
      showToast({ type: 'success', title: 'Success', message: 'Enrollment created successfully' });
      setModalOpen(false);
      reset();
      loadEnrollments();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create enrollment' });
    }
  };

  const columns = [
    { key: 'id', header: 'ID', accessor: (row: Enrollment) => `#${row.id.slice(-6)}` },
    { key: 'studentId', header: 'Student', accessor: (row: Enrollment) => students.find(s => s.id === row.studentId)?.name || row.studentId },
    { key: 'courseId', header: 'Course', accessor: (row: Enrollment) => courses.find(c => c.id === row.courseId)?.name || row.courseId },
    { key: 'sectionId', header: 'Section', accessor: (row: Enrollment) => sections.find(s => s.id === row.sectionId)?.name || row.sectionId },
    { key: 'facultyId', header: 'Faculty', accessor: (row: Enrollment) => row.facultyId ? faculty.find(f => f.id === row.facultyId)?.name || row.facultyId : '—' },
    { key: 'enrollmentDate', header: 'Date', accessor: (row: Enrollment) => new Date(row.enrollmentDate).toLocaleDateString() },
    { key: 'status', header: 'Status', accessor: 'status', render: (value: string) => (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${value === 'ENROLLED' ? 'bg-success-100 text-success-700' : value === 'COMPLETED' ? 'bg-info-100 text-info-700' : value === 'DROPPED' ? 'bg-warning-100 text-warning-700' : 'bg-error-100 text-error-700'}`}>{value}</span>
    )},
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Enrollments</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Manage student enrollments</p>
        </div>
        <Button onClick={() => { setEditingEnrollment(null); reset(); setModalOpen(true); }}>
          <PlusIcon className="h-5 w-5" />
          Add Enrollment
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={enrollments}
        keyAccessor="id"
        title={`${enrollments.length} enrollments`}
        searchable
        searchPlaceholder="Search enrollments..."
        loading={loading}
      />
    </div>
  );
}