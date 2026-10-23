import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable, Button, Modal, FormField, Card } from '@/components';
import { api } from '@/services';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import { Course } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const courseSchema = z.object({
  code: z.string().min(1, 'Course code is required'),
  name: z.string().min(1, 'Course name is required'),
  description: z.string().optional(),
  credits: z.number().min(1, 'Credits must be at least 1'),
  theoryHours: z.number().min(0),
  practicalHours: z.number().min(0),
  departmentId: z.string().min(1, 'Department is required'),
  programId: z.string().min(1, 'Program is required'),
  semester: z.number().min(1, 'Semester must be at least 1'),
  type: z.enum(['CORE', 'ELECTIVE', 'LAB', 'PROJECT', 'SEMINAR']),
  prerequisites: z.array(z.string()).optional(),
});

type CourseFormData = z.infer<typeof courseSchema>;

export function CoursesPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Course | null>(null);
  const [departments, setDepartments] = useState<Array<{id: string; name: string}>>([]);
  const [programs, setPrograms] = useState<Array<{id: string; name: string}>>([]);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CourseFormData>({
    resolver: zodResolver(courseSchema),
  });

  useEffect(() => {
    loadCourses();
    loadReferenceData();
  }, []);

  const loadCourses = async () => {
    setLoading(true);
    try {
      const response = await api.courses.getAll({ page: 1, pageSize: 100 });
      setCourses(response.data);
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load courses' });
    } finally {
      setLoading(false);
    }
  };

  const loadReferenceData = async () => {
    try {
      const [depts, progs] = await Promise.all([
        api.departments.getAll(),
        api.programs.getAll(),
      ]);
      setDepartments(depts.map(d => ({ id: d.id, name: d.name })));
      setPrograms(progs.map(p => ({ id: p.id, name: p.name })));
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: CourseFormData) => {
    try {
      await api.courses.create(data as any);
      showToast({ type: 'success', title: 'Success', message: 'Course created successfully' });
      setModalOpen(false);
      reset();
      loadCourses();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create course' });
    }
  };

  const handleUpdate = async (data: CourseFormData) => {
    if (!editingCourse) return;
    try {
      await api.courses.update(editingCourse.id, data as any);
      showToast({ type: 'success', title: 'Success', message: 'Course updated successfully' });
      setModalOpen(false);
      setEditingCourse(null);
      reset();
      loadCourses();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update course' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.courses.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Course deleted successfully' });
      setDeleteConfirm(null);
      loadCourses();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete course' });
    }
  };

  const openEditModal = (course: Course) => {
    setEditingCourse(course);
    reset({
      code: course.code,
      name: course.name,
      description: course.description || '',
      credits: course.credits,
      theoryHours: course.theoryHours,
      practicalHours: course.practicalHours,
      departmentId: course.departmentId,
      programId: course.programId,
      semester: course.semester,
      type: course.type,
      prerequisites: course.prerequisites || [],
    });
    setModalOpen(true);
  };

  const columns = [
    { key: 'code', header: 'Code', accessor: 'code' },
    { key: 'name', header: 'Name', accessor: 'name' },
    { key: 'credits', header: 'Credits', accessor: 'credits' },
    { key: 'theoryHours', header: 'Theory Hrs', accessor: 'theoryHours' },
    { key: 'practicalHours', header: 'Practical Hrs', accessor: 'practicalHours' },
    { key: 'semester', header: 'Semester', accessor: 'semester' },
    { key: 'type', header: 'Type', accessor: 'type' },
    { key: 'isActive', header: 'Status', accessor: 'isActive', render: (value: boolean) => (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${value ? 'bg-success-100 text-success-700' : 'bg-neutral-100 text-neutral-700'}`}>{value ? 'Active' : 'Inactive'}</span>
    )},
    { key: 'actions', header: 'Actions', render: (_, row: Course) => (
      <div className="flex items-center justify-center gap-1">
        <button onClick={() => openEditModal(row)} className="p-1.5 rounded-lg text-neutral-500 hover:bg-neutral-100 dark:hover:bg-neutral-800" aria-label="Edit"><PencilIcon className="h-5 w-5" /></button>
        <button onClick={() => setDeleteConfirm(row)} className="p-1.5 rounded-lg text-error-500 hover:bg-error-50 dark:hover:bg-error-900/20" aria-label="Delete"><TrashIcon className="h-5 w-5" /></button>
      </div>
    )},
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Courses</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Manage course catalog</p>
        </div>
        <Button onClick={() => { setEditingCourse(null); reset(); setModalOpen(true); }}>
          <PlusIcon className="h-5 w-5" />
          Add Course
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={courses}
        keyAccessor="id"
        title={`${courses.length} courses`}
        searchable
        searchPlaceholder="Search courses..."
        loading={loading}
      />

      <Modal
        isOpen={modalOpen}
        onClose={() => { setModalOpen(false); setEditingCourse(null); reset(); }}
        title={editingCourse ? 'Edit Course' : 'Add Course'}
        size="lg"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setModalOpen(false); setEditingCourse(null); reset(); }}>Cancel</Button>
            <Button onClick={handleSubmit(editingCourse ? handleUpdate : handleCreate)}>Save</Button>
          </>
        }
      >
        <form onSubmit={handleSubmit(editingCourse ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <FormField name="code" label="Course Code" control={control} error={errors.code?.message} />
            <FormField name="name" label="Course Name" control={control} error={errors.name?.message} />
            <FormField name="description" label="Description" type="textarea" control={control} />
            <FormField name="credits" label="Credits" type="number" control={control} error={errors.credits?.message} />
            <FormField name="theoryHours" label="Theory Hours" type="number" control={control} />
            <FormField name="practicalHours" label="Practical Hours" type="number" control={control} />
            <FormField name="semester" label="Semester" type="number" control={control} error={errors.semester?.message} />
            <FormField name="type" label="Type" type="select" options={[
              { value: 'CORE', label: 'Core' },
              { value: 'ELECTIVE', label: 'Elective' },
              { value: 'LAB', label: 'Lab' },
              { value: 'PROJECT', label: 'Project' },
              { value: 'SEMINAR', label: 'Seminar' },
            ]} control={control} error={errors.type?.message} />
            <FormField name="departmentId" label="Department" type="select" options={departments.map(d => ({ value: d.id, label: d.name }))} control={control} error={errors.departmentId?.message} />
            <FormField name="programId" label="Program" type="select" options={programs.map(p => ({ value: p.id, label: p.name }))} control={control} error={errors.programId?.message} />
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Course"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
            <Button variant="danger" onClick={handleDelete}>Delete</Button>
          </>
        }
      >
        <p>Are you sure you want to delete <strong>{deleteConfirm?.code} - {deleteConfirm?.name}</strong>? This action cannot be undone.</p>
      </Modal>
    </div>
  );
}