import { useEffect, useState, useMemo } from 'react';
import { Card, Button, Modal, Badge } from '@/components/ui';
import { api } from '@/services';
import {
  PlusIcon,
  MagnifyingGlassIcon,
  PencilIcon,
  TrashIcon,
  ClipboardDocumentCheckIcon,
  FunnelIcon,
} from '@heroicons/react/24/outline';
import { Enrollment, Student, Course, Section, Faculty } from '@/types';
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

  // Filters
  const [courseFilter, setCourseFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Reference data
  const [students, setStudents] = useState<Student[]>([]);
  const [courses, setCourses] = useState<Course[]>([]);
  const [sections, setSections] = useState<Section[]>([]);
  const [faculty, setFaculty] = useState<Faculty[]>([]);

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
    } catch {
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
      setStudents(studs.data);
      setCourses(crses.data);
      setSections(secs);
      setFaculty(facs.data);
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: EnrollmentFormData) => {
    try {
      await api.enrollments.create({
        ...data,
        status: 'ENROLLED',
        enrollmentDate: new Date().toISOString().split('T')[0],
      });
      showToast({ type: 'success', title: 'Success', message: 'Student successfully enrolled in course' });
      setModalOpen(false);
      reset();
      loadEnrollments();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create enrollment' });
    }
  };

  const handleUpdate = async (data: EnrollmentFormData) => {
    if (!editingEnrollment) return;
    try {
      await api.enrollments.update(editingEnrollment.id, data);
      showToast({ type: 'success', title: 'Success', message: 'Enrollment updated successfully' });
      setModalOpen(false);
      setEditingEnrollment(null);
      reset();
      loadEnrollments();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update enrollment' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.enrollments.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Enrollment dropped' });
      setDeleteConfirm(null);
      loadEnrollments();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete enrollment' });
    }
  };

  const openEditModal = (enrollment: Enrollment) => {
    setEditingEnrollment(enrollment);
    reset({
      studentId: enrollment.studentId,
      courseId: enrollment.courseId,
      sectionId: enrollment.sectionId,
      facultyId: enrollment.facultyId || '',
    });
    setModalOpen(true);
  };

  const filteredEnrollments = useMemo(() => {
    return enrollments.filter((e) => {
      const matchCourse = courseFilter === 'ALL' || e.courseId === courseFilter;
      const matchStatus = statusFilter === 'ALL' || e.status === statusFilter;
      return matchCourse && matchStatus;
    });
  }, [enrollments, courseFilter, statusFilter]);

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Student Enrollments
            </h1>
            <Badge variant="brand" size="md">
              {filteredEnrollments.length} Active Records
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Course registrations, classroom section allocations, and semester enrollment states.
          </p>
        </div>

        <Button
          size="sm"
          onClick={() => {
            setEditingEnrollment(null);
            reset({
              studentId: students[0]?.id || '1',
              courseId: courses[0]?.id || '1',
              sectionId: sections[0]?.id || '1',
              facultyId: faculty[0]?.id || '1',
            });
            setModalOpen(true);
          }}
          leftIcon={<PlusIcon className="h-4 w-4" />}
        >
          New Enrollment
        </Button>
      </div>

      {/* Filter Toolbar */}
      <Card padding="sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-xs text-slate-500">
            <FunnelIcon className="h-4 w-4" />
            <span className="font-semibold">Filter Courses:</span>
            <select
              value={courseFilter}
              onChange={(e) => setCourseFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Enrolled Courses</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>
          </div>

          <div className="flex items-center gap-2 text-xs text-slate-500">
            <span className="font-semibold">Status:</span>
            <select
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Statuses</option>
              <option value="ENROLLED">Enrolled</option>
              <option value="COMPLETED">Completed</option>
              <option value="DROPPED">Dropped</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Main Table */}
      <Card padding="none" className="overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-xs text-slate-500">Loading enrollment records...</div>
        ) : filteredEnrollments.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <ClipboardDocumentCheckIcon className="h-8 w-8 text-slate-400 mx-auto" />
            <p className="font-bold text-slate-700 dark:text-slate-300">No enrollment records found.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200/80 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Enrollment ID</th>
                  <th className="py-3 px-4">Student</th>
                  <th className="py-3 px-4">Course</th>
                  <th className="py-3 px-4">Section</th>
                  <th className="py-3 px-4">Enrolled Date</th>
                  <th className="py-3 px-4">Grade</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredEnrollments.map((enr) => {
                  const student = students.find((s) => s.id === enr.studentId);
                  const course = courses.find((c) => c.id === enr.courseId);
                  const section = sections.find((s) => s.id === enr.sectionId);

                  return (
                    <tr
                      key={enr.id}
                      className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors"
                    >
                      <td className="py-3 px-4 font-mono font-medium text-slate-500">
                        #{enr.id.slice(-6)}
                      </td>
                      <td className="py-3 px-4">
                        <div className="font-bold text-slate-900 dark:text-slate-100">
                          {student ? `${student.firstName} ${student.lastName}` : `Student #${enr.studentId}`}
                        </div>
                        <div className="text-[11px] text-slate-400 font-mono">
                          {student?.studentId}
                        </div>
                      </td>
                      <td className="py-3 px-4">
                        <div className="font-bold text-brand-600 dark:text-brand-400">
                          {course ? course.code : `Course #${enr.courseId}`}
                        </div>
                        <div className="text-slate-600 dark:text-slate-300 truncate max-w-xs">
                          {course?.name}
                        </div>
                      </td>
                      <td className="py-3 px-4 text-slate-600 dark:text-slate-300 font-medium">
                        {section?.name || 'Section A'}
                      </td>
                      <td className="py-3 px-4 text-slate-500">{enr.enrollmentDate}</td>
                      <td className="py-3 px-4 font-bold text-emerald-600 dark:text-emerald-400">
                        {enr.grade ? `${enr.grade} (${enr.marks}%)` : 'In Progress'}
                      </td>
                      <td className="py-3 px-4">
                        <Badge
                          variant={enr.status === 'ENROLLED' ? 'success' : enr.status === 'COMPLETED' ? 'info' : 'warning'}
                          size="sm"
                          dot
                        >
                          {enr.status}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            type="button"
                            onClick={() => openEditModal(enr)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Edit Enrollment"
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            type="button"
                            onClick={() => setDeleteConfirm(enr)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Drop Enrollment"
                          >
                            <TrashIcon className="h-4 w-4" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* Add / Edit Enrollment Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingEnrollment(null);
          reset();
        }}
        title={editingEnrollment ? 'Edit Course Enrollment' : 'Enroll Student in Course'}
        size="md"
      >
        <form onSubmit={handleSubmit(editingEnrollment ? handleUpdate : handleCreate)} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Select Student
            </label>
            <select
              {...register('studentId')}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            >
              {students.map((s) => (
                <option key={s.id} value={s.id}>
                  {s.firstName} {s.lastName} ({s.studentId})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Select Course
            </label>
            <select
              {...register('courseId')}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            >
              {courses.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code} - {c.name} ({c.credits} cr)
                </option>
              ))}
            </select>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Classroom Section
              </label>
              <select
                {...register('sectionId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                {sections.map((sec) => (
                  <option key={sec.id} value={sec.id}>
                    {sec.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Faculty Instructor
              </label>
              <select
                {...register('facultyId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                {faculty.map((f) => (
                  <option key={f.id} value={f.id}>
                    Prof. {f.firstName} {f.lastName}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button
              variant="outline"
              type="button"
              onClick={() => {
                setModalOpen(false);
                setEditingEnrollment(null);
                reset();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              {editingEnrollment ? 'Save Changes' : 'Confirm Enrollment'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Modal */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Confirm Enrollment Drop"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600 dark:text-slate-300">
            Are you sure you want to drop this course enrollment? Student will lose seat in section.
          </p>
          <div className="flex items-center justify-end gap-2.5 pt-2">
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleDelete}>
              Drop Course
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default EnrollmentsPage;
