import { useEffect, useState, useMemo } from 'react';
import { Card, Button, Modal, Badge } from '@/components/ui';
import { api } from '@/services';
import { Mark, Enrollment, Assessment } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  PlusIcon,
  PencilIcon,
  TrashIcon,
  StarIcon,
  CheckCircleIcon,
  FunnelIcon,
} from '@heroicons/react/24/outline';

const markSchema = z.object({
  enrollmentId: z.string().min(1, 'Enrollment is required'),
  assessmentId: z.string().min(1, 'Assessment is required'),
  marksObtained: z.number().min(0, 'Marks cannot be negative').max(100, 'Maximum is 100'),
  gradedBy: z.string().optional(),
  remarks: z.string().optional(),
});

type MarkFormData = z.infer<typeof markSchema>;

function computeGrade(score: number): { letter: string; gpa: number; variant: 'success' | 'info' | 'warning' | 'default' | 'error' } {
  if (score >= 90) return { letter: 'A', gpa: 4.0, variant: 'success' };
  if (score >= 80) return { letter: 'B', gpa: 3.0, variant: 'info' };
  if (score >= 70) return { letter: 'C', gpa: 2.0, variant: 'warning' };
  if (score >= 60) return { letter: 'D', gpa: 1.0, variant: 'default' };
  return { letter: 'F', gpa: 0.0, variant: 'error' };
}

export function MarksPage() {
  const { showToast } = useToast();
  const [marks, setMarks] = useState<Mark[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingMark, setEditingMark] = useState<Mark | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Mark | null>(null);

  // Filters
  const [assessmentFilter, setAssessmentFilter] = useState('ALL');

  // References
  const [enrollments, setEnrollments] = useState<Enrollment[]>([]);
  const [assessments, setAssessments] = useState<Assessment[]>([]);

  const {
    register,
    handleSubmit,
    reset,
    watch,
    formState: { errors },
  } = useForm<MarkFormData>({
    resolver: zodResolver(markSchema),
  });

  const liveMarks = watch('marksObtained', 85);
  const liveGrade = computeGrade(Number(liveMarks) || 0);

  useEffect(() => {
    loadMarks();
    loadReferenceData();
  }, []);

  const loadMarks = async () => {
    setLoading(true);
    try {
      const response = await api.marks.getAll({ page: 1, pageSize: 100 });
      setMarks(response.data);
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load gradebook' });
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
      setEnrollments(enrolls.data);
      setAssessments(assesses.data);
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: MarkFormData) => {
    try {
      await api.marks.create({
        ...data,
        gradedBy: 'Admin Evaluator',
        gradedAt: new Date().toISOString(),
        isPublished: true,
      });
      showToast({ type: 'success', title: 'Success', message: 'Student grade recorded' });
      setModalOpen(false);
      reset();
      loadMarks();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to record mark' });
    }
  };

  const handleUpdate = async (data: MarkFormData) => {
    if (!editingMark) return;
    try {
      await api.marks.update(editingMark.id, data);
      showToast({ type: 'success', title: 'Success', message: 'Grade score updated' });
      setModalOpen(false);
      setEditingMark(null);
      reset();
      loadMarks();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update mark' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.marks.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Grade entry deleted' });
      setDeleteConfirm(null);
      loadMarks();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete mark' });
    }
  };

  const openEditModal = (mark: Mark) => {
    setEditingMark(mark);
    reset({
      enrollmentId: mark.enrollmentId,
      assessmentId: mark.assessmentId,
      marksObtained: mark.marksObtained,
      gradedBy: mark.gradedBy || '',
      remarks: mark.remarks || '',
    });
    setModalOpen(true);
  };

  const filteredMarks = useMemo(() => {
    return marks.filter((m) => {
      const matchAssess = assessmentFilter === 'ALL' || m.assessmentId === assessmentFilter;
      return matchAssess;
    });
  }, [marks, assessmentFilter]);

  const avgScore =
    filteredMarks.length > 0
      ? Math.round(filteredMarks.reduce((sum, m) => sum + m.marksObtained, 0) / filteredMarks.length)
      : 85;

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Gradebook & Marks
            </h1>
            <Badge variant="brand" size="md">
              {filteredMarks.length} Graded Submissions
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Record assessment scores, compute GPA weights, and manage course evaluation results.
          </p>
        </div>

        <Button
          size="sm"
          onClick={() => {
            setEditingMark(null);
            reset({
              enrollmentId: enrollments[0]?.id || '1',
              assessmentId: assessments[0]?.id || '1',
              marksObtained: 85,
              gradedBy: 'Prof. Emily Johnson',
              remarks: 'Well-reasoned test solution',
            });
            setModalOpen(true);
          }}
          leftIcon={<PlusIcon className="h-4 w-4" />}
        >
          Enter Marks
        </Button>
      </div>

      {/* Grade Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Class Average</span>
          <p className="text-2xl font-extrabold text-brand-600 dark:text-brand-400 tracking-tight">
            {avgScore} / 100
          </p>
          <span className="text-[10px] text-emerald-600 font-medium">Letter Grade B+</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Evaluations Logged</span>
          <p className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 tracking-tight">
            {filteredMarks.length} Students
          </p>
          <span className="text-[10px] text-slate-400">Published to transcripts</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Honor Grades (A/A-)</span>
          <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 tracking-tight">
            {filteredMarks.filter((m) => m.marksObtained >= 85).length} Students
          </p>
          <span className="text-[10px] text-slate-400">Top quartile</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Grading Scale</span>
          <p className="text-2xl font-extrabold text-slate-800 dark:text-slate-100 tracking-tight">
            4.0 Scale
          </p>
          <span className="text-[10px] text-slate-400">Standard institutional policy</span>
        </Card>
      </div>

      {/* Filter Toolbar */}
      <Card padding="sm">
        <div className="flex items-center gap-2 text-xs text-slate-500">
          <FunnelIcon className="h-4 w-4" />
          <span className="font-semibold">Filter Assessment:</span>
          <select
            value={assessmentFilter}
            onChange={(e) => setAssessmentFilter(e.target.value)}
            className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
          >
            <option value="ALL">All Assessments</option>
            {assessments.map((a) => (
              <option key={a.id} value={a.id}>
                {a.title} ({a.maxMarks} pts)
              </option>
            ))}
          </select>
        </div>
      </Card>

      {/* Main Table */}
      <Card padding="none" className="overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-xs text-slate-500">Loading grade records...</div>
        ) : filteredMarks.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <StarIcon className="h-8 w-8 text-slate-400 mx-auto" />
            <p className="font-bold text-slate-700 dark:text-slate-300">No marks recorded yet.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200/80 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Student / Enrollment</th>
                  <th className="py-3 px-4">Assessment Evaluation</th>
                  <th className="py-3 px-4">Points</th>
                  <th className="py-3 px-4">Letter Grade</th>
                  <th className="py-3 px-4">Graded By</th>
                  <th className="py-3 px-4">Remarks</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredMarks.map((m) => {
                  const assessment = assessments.find((a) => a.id === m.assessmentId);
                  const grade = computeGrade(m.marksObtained);

                  return (
                    <tr
                      key={m.id}
                      className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors"
                    >
                      <td className="py-3 px-4">
                        <div className="font-bold text-slate-900 dark:text-slate-100">
                          Enrollment #{m.enrollmentId.slice(-6)}
                        </div>
                        <div className="text-[11px] text-slate-400">Class of 2027</div>
                      </td>
                      <td className="py-3 px-4">
                        <div className="font-semibold text-brand-600 dark:text-brand-400">
                          {assessment?.title || `Assessment #${m.assessmentId}`}
                        </div>
                        <div className="text-[11px] text-slate-400 font-mono">
                          Max: {assessment?.maxMarks || 100} pts • {assessment?.weightage || 25}% weight
                        </div>
                      </td>
                      <td className="py-3 px-4 font-extrabold text-sm text-slate-900 dark:text-slate-100">
                        {m.marksObtained}
                      </td>
                      <td className="py-3 px-4">
                        <Badge variant={grade.variant} size="sm">
                          {grade.letter} ({grade.gpa.toFixed(1)})
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-slate-600 dark:text-slate-300">
                        {m.gradedBy || 'Faculty'}
                      </td>
                      <td className="py-3 px-4 text-slate-500 italic max-w-xs truncate">
                        {m.remarks || 'Standard evaluation recorded'}
                      </td>
                      <td className="py-3 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            type="button"
                            onClick={() => openEditModal(m)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Edit Mark"
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            type="button"
                            onClick={() => setDeleteConfirm(m)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Delete Mark"
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

      {/* Record Marks Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingMark(null);
          reset();
        }}
        title={editingMark ? 'Edit Assessment Grade' : 'Enter Assessment Score'}
        size="md"
      >
        <form onSubmit={handleSubmit(editingMark ? handleUpdate : handleCreate)} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Select Student Enrollment
            </label>
            <select
              {...register('enrollmentId')}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            >
              {enrollments.map((e) => (
                <option key={e.id} value={e.id}>
                  Enrollment #{e.id.slice(-6)} (Course #{e.courseId})
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Select Assessment
            </label>
            <select
              {...register('assessmentId')}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            >
              {assessments.map((a) => (
                <option key={a.id} value={a.id}>
                  {a.title} ({a.maxMarks} pts)
                </option>
              ))}
            </select>
          </div>

          <div>
            <div className="flex items-center justify-between mb-1">
              <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">
                Score / Points Obtained
              </label>
              <span className="text-xs font-bold text-brand-600 dark:text-brand-400">
                Grade: {liveGrade.letter} ({liveGrade.gpa.toFixed(1)} GPA)
              </span>
            </div>
            <input
              type="number"
              min="0"
              max="100"
              {...register('marksObtained', { valueAsNumber: true })}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
            {errors.marksObtained && (
              <p className="text-[11px] text-rose-500 mt-1">{errors.marksObtained.message}</p>
            )}
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Evaluator / Faculty Name
            </label>
            <input
              type="text"
              {...register('gradedBy')}
              placeholder="e.g. Prof. Emily Johnson"
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Instructor Remarks & Feedback
            </label>
            <textarea
              rows={2}
              {...register('remarks')}
              placeholder="Evaluation comments..."
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button
              variant="outline"
              type="button"
              onClick={() => {
                setModalOpen(false);
                setEditingMark(null);
                reset();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">{editingMark ? 'Save Updates' : 'Record Score'}</Button>
          </div>
        </form>
      </Modal>

      {/* Delete Modal */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Grade Entry"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600 dark:text-slate-300">
            Are you sure you want to remove this grade entry? Student GPA will recalculate automatically.
          </p>
          <div className="flex items-center justify-end gap-2.5 pt-2">
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleDelete}>
              Delete Entry
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default MarksPage;
