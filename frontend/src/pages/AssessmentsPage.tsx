import { useEffect, useState } from 'react';
import { DataTable, Button, Modal, FormField, Badge } from '@/components';
import { api } from '@/services';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
import { Assessment } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const assessmentSchema = z.object({
  courseId: z.string().min(1, 'Course is required'),
  title: z.string().min(1, 'Title is required'),
  description: z.string().optional(),
  type: z.enum(['QUIZ', 'ASSIGNMENT', 'MIDTERM', 'FINAL', 'PROJECT', 'PRESENTATION', 'LAB', 'OTHER']),
  maxMarks: z.number().min(1, 'Max marks must be at least 1'),
  weightage: z.number().min(0).max(100, 'Weightage must be between 0 and 100'),
  scheduledDate: z.string().min(1, 'Scheduled date is required'),
  dueDate: z.string().optional(),
});

type AssessmentFormData = z.infer<typeof assessmentSchema>;

export function AssessmentsPage() {
  const { showToast } = useToast();
  const [assessments, setAssessments] = useState<Assessment[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingAssessment, setEditingAssessment] = useState<Assessment | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Assessment | null>(null);
  const [courses, setCourses] = useState<Array<{id: string; name: string}>>([]);

  const {
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AssessmentFormData>({
    resolver: zodResolver(assessmentSchema),
  });

  useEffect(() => {
    loadAssessments();
    loadCourses();
  }, []);

  const loadAssessments = async () => {
    setLoading(true);
    try {
      const response = await api.assessments.getAll({ page: 1, pageSize: 100 });
      setAssessments(response.data);
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load assessments' });
    } finally {
      setLoading(false);
    }
  };

  const loadCourses = async () => {
    try {
      const response = await api.courses.getAll({ page: 1, pageSize: 100 });
      setCourses(response.data.map(c => ({ id: c.id, name: `${c.code} - ${c.name}` })));
    } catch (error) {
      console.error('Failed to load courses:', error);
    }
  };

  const handleCreate = async (data: AssessmentFormData) => {
    try {
      await api.assessments.create(data as any);
      showToast({ type: 'success', title: 'Success', message: 'Assessment created successfully' });
      setModalOpen(false);
      reset();
      loadAssessments();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create assessment' });
    }
  };

  const handleUpdate = async (data: AssessmentFormData) => {
    if (!editingAssessment) return;
    try {
      await api.assessments.update(editingAssessment.id, data as any);
      showToast({ type: 'success', title: 'Success', message: 'Assessment updated successfully' });
      setModalOpen(false);
      setEditingAssessment(null);
      reset();
      loadAssessments();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update assessment' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.assessments.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Assessment deleted successfully' });
      setDeleteConfirm(null);
      loadAssessments();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete assessment' });
    }
  };

  const openEditModal = (assessment: Assessment) => {
    setEditingAssessment(assessment);
    reset({
      courseId: assessment.courseId,
      title: assessment.title,
      description: assessment.description || '',
      type: assessment.type,
      maxMarks: assessment.maxMarks,
      weightage: assessment.weightage,
      scheduledDate: assessment.scheduledDate,
      dueDate: assessment.dueDate || '',
    });
    setModalOpen(true);
  };

  const columns = [
    { key: 'title', header: 'Title', accessor: 'title' },
    { key: 'courseId', header: 'Course', accessor: (row: Assessment) => courses.find(c => c.id === row.courseId)?.name || row.courseId },
    { key: 'type', header: 'Type', accessor: 'type', render: (value: string) => (
      <Badge variant={value === 'MIDTERM' || value === 'FINAL' ? 'error' : value === 'PROJECT' ? 'info' : value === 'LAB' ? 'warning' : 'default'} size="sm">{value}</Badge>
    )},
    { key: 'maxMarks', header: 'Max Marks', accessor: 'maxMarks' },
    { key: 'weightage', header: 'Weightage %', accessor: 'weightage' },
    { key: 'scheduledDate', header: 'Scheduled', accessor: (row: Assessment) => new Date(row.scheduledDate).toLocaleDateString() },
    { key: 'isPublished', header: 'Published', accessor: 'isPublished', render: (value: boolean) => (
      <Badge variant={value ? 'success' : 'default'} size="sm">{value ? 'Yes' : 'No'}</Badge>
    )},
    { key: 'actions', header: 'Actions', render: (_, row: Assessment) => (
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
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Assessments</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Manage assessments and exams</p>
        </div>
        <Button onClick={() => { setEditingAssessment(null); reset(); setModalOpen(true); }}>
          <PlusIcon className="h-5 w-5" />
          Add Assessment
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={assessments}
        keyAccessor="id"
        title={`${assessments.length} assessments`}
        searchable
        searchPlaceholder="Search assessments..."
        loading={loading}
      />

      <Modal
        isOpen={modalOpen}
        onClose={() => { setModalOpen(false); setEditingAssessment(null); reset(); }}
        title={editingAssessment ? 'Edit Assessment' : 'Add Assessment'}
        size="lg"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setModalOpen(false); setEditingAssessment(null); reset(); }}>Cancel</Button>
            <Button onClick={handleSubmit(editingAssessment ? handleUpdate : handleCreate)}>Save</Button>
          </>
        }
      >
        <form onSubmit={handleSubmit(editingAssessment ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <FormField name="courseId" label="Course" type="select" options={courses.map(c => ({ value: c.id, label: c.name }))} control={control} error={errors.courseId?.message} />
            <FormField name="title" label="Title" control={control} error={errors.title?.message} />
            <FormField name="description" label="Description" type="textarea" control={control} />
            <FormField name="type" label="Type" type="select" options={[
              { value: 'QUIZ', label: 'Quiz' },
              { value: 'ASSIGNMENT', label: 'Assignment' },
              { value: 'MIDTERM', label: 'Midterm' },
              { value: 'FINAL', label: 'Final' },
              { value: 'PROJECT', label: 'Project' },
              { value: 'PRESENTATION', label: 'Presentation' },
              { value: 'LAB', label: 'Lab' },
              { value: 'OTHER', label: 'Other' },
            ]} control={control} error={errors.type?.message} />
            <FormField name="maxMarks" label="Max Marks" type="number" control={control} error={errors.maxMarks?.message} />
            <FormField name="weightage" label="Weightage %" type="number" control={control} error={errors.weightage?.message} />
            <FormField name="scheduledDate" label="Scheduled Date" type="date" control={control} error={errors.scheduledDate?.message} />
            <FormField name="dueDate" label="Due Date" type="date" control={control} />
          </div>
        </form>
      </Modal>
    </div>
  );
}