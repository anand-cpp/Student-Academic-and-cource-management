import { useEffect, useState } from 'react';
import { DataTable, Button, Modal, FormField, Select, Badge } from '@/components';
import { api } from '@/services';
import { Mark } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { PlusIcon, PencilIcon, TrashIcon, EyeIcon } from '@heroicons/react/24/outline';

const markSchema = z.object({
  enrollmentId: z.string().min(1, 'Enrollment is required'),
  assessmentId: z.string().min(1, 'Assessment is required'),
  marksObtained: z.number().min(0, 'Marks cannot be negative'),
  gradedBy: z.string().optional(),
  remarks: z.string().optional(),
});

type MarkFormData = z.infer<typeof markSchema>;

export function MarksPage() {
  const { showToast } = useToast();
  const [marks, setMarks] = useState<Mark[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingMark, setEditingMark] = useState<Mark | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Mark | null>(null);
  const [enrollments, setEnrollments] = useState<Array<{id: string; name: string}>>([]);
  const [assessments, setAssessments] = useState<Array<{id: string; name: string}>>([]);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<MarkFormData>({
    resolver: zodResolver(markSchema),
  });

  useEffect(() => {
    loadMarks();
    loadReferenceData();
  }, []);

  const loadMarks = async () => {
    setLoading(true);
    try {
      const response = await api.marks.getAll({ page: 1, pageSize: 100 });
      setMarks(response.data);
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load marks' });
    } finally {
      setLoading(false);
    }
  };

  const loadReferenceData = async () => {
    try {
      const [enrolls, assesses] = await Promise.all([
        api.enrollments.getAll({ page: 1, pageSize: 100 }),
        api.assessments.getAll({ page: 1, pageSize: 100 }),
      ]);
      setEnrollments(enrolls.data.map(e => ({ id: e.id, name: `Enrollment #${e.id.slice(-6)}` })));
      setAssessments(assesses.data.map(a => ({ id: a.id, name: `${a.title} (${a.maxMarks} pts)` })));
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: MarkFormData) => {
    try {
      await api.marks.create(data as any);
      showToast({ type: 'success', title: 'Success', message: 'Marks recorded successfully' });
      setModalOpen(false);
      reset();
      loadMarks();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to record marks' });
    }
  };

  const columns = [
    { key: 'id', header: 'ID', accessor: (row: Mark) => `#${row.id.slice(-6)}` },
    { key: 'enrollmentId', header: 'Enrollment', accessor: (row: Mark) => enrollments.find(e => e.id === row.enrollmentId)?.name || row.enrollmentId },
    { key: 'assessmentId', header: 'Assessment', accessor: (row: Mark) => assessments.find(a => a.id === row.assessmentId)?.name || row.assessmentId },
    { key: 'marksObtained', header: 'Marks', accessor: 'marksObtained' },
    { key: 'gradedBy', header: 'Graded By', accessor: 'gradedBy' },
    { key: 'gradedAt', header: 'Graded At', accessor: (row: Mark) => new Date(row.gradedAt).toLocaleDateString() },
    { key: 'isPublished', header: 'Published', accessor: 'isPublished', render: (value: boolean) => (
      <Badge variant={value ? 'success' : 'default'} size="sm">{value ? 'Yes' : 'No'}</Badge>
    )},
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Marks</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Manage student grades and marks</p>
        </div>
        <Button onClick={() => { setEditingMark(null); reset(); setModalOpen(true); }}>
          <PlusIcon className="h-5 w-5" />
          Add Marks
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={marks}
        keyAccessor="id"
        title={`${marks.length} records`}
        searchable
        searchPlaceholder="Search marks..."
        loading={loading}
      />
    </div>
  );
}