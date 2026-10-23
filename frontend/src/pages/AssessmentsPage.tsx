import { useEffect, useState, useMemo } from 'react';
import { Card, Button, Modal, Badge } from '@/components/ui';
import { api } from '@/services';
import {
  PlusIcon,
  MagnifyingGlassIcon,
  PencilIcon,
  TrashIcon,
  DocumentTextIcon,
  CalendarDaysIcon,
  ChartBarIcon,
} from '@heroicons/react/24/outline';
import { Assessment, AssessmentType, Course } from '@/types';
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
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingAssessment, setEditingAssessment] = useState<Assessment | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Assessment | null>(null);

  // Filters
  const [search, setSearch] = useState('');
  const [courseFilter, setCourseFilter] = useState('ALL');
  const [typeFilter, setTypeFilter] = useState('ALL');

  const {
    register,
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
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load assessments' });
    } finally {
      setLoading(false);
    }
  };

  const loadCourses = async () => {
    try {
      const res = await api.courses.getAll({ page: 1, pageSize: 100 });
      setCourses(res.data);
    } catch (error) {
      console.error('Failed to load courses:', error);
    }
  };

  const handleCreate = async (data: AssessmentFormData) => {
    try {
      await api.assessments.create({
        ...data,
        isPublished: true,
      });
      showToast({ type: 'success', title: 'Success', message: 'Assessment evaluation created' });
      setModalOpen(false);
      reset();
      loadAssessments();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create assessment' });
    }
  };

  const handleUpdate = async (data: AssessmentFormData) => {
    if (!editingAssessment) return;
    try {
      await api.assessments.update(editingAssessment.id, data);
      showToast({ type: 'success', title: 'Success', message: 'Assessment updated successfully' });
      setModalOpen(false);
      setEditingAssessment(null);
      reset();
      loadAssessments();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update assessment' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.assessments.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Assessment removed' });
      setDeleteConfirm(null);
      loadAssessments();
    } catch {
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

  const filteredAssessments = useMemo(() => {
    return assessments.filter((a) => {
      const matchSearch =
        search === '' ||
        a.title.toLowerCase().includes(search.toLowerCase()) ||
        a.description?.toLowerCase().includes(search.toLowerCase());

      const matchCourse = courseFilter === 'ALL' || a.courseId === courseFilter;
      const matchType = typeFilter === 'ALL' || a.type === typeFilter;

      return matchSearch && matchCourse && matchType;
    });
  }, [assessments, search, courseFilter, typeFilter]);

  const getTypeVariant = (type: AssessmentType): 'error' | 'info' | 'warning' | 'primary' | 'default' => {
    switch (type) {
      case 'MIDTERM':
      case 'FINAL':
        return 'error';
      case 'PROJECT':
        return 'info';
      case 'LAB':
        return 'warning';
      case 'QUIZ':
        return 'primary';
      default:
        return 'default';
    }
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Assessments & Examinations
            </h1>
            <Badge variant="brand" size="md">
              {filteredAssessments.length} Evaluations
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Define exam rubrics, weightings, scheduled test dates, and publish grading policies.
          </p>
        </div>

        <Button
          size="sm"
          onClick={() => {
            setEditingAssessment(null);
            reset({
              courseId: courses[0]?.id || '1',
              title: '',
              description: '',
              type: 'MIDTERM',
              maxMarks: 100,
              weightage: 25,
              scheduledDate: new Date().toISOString().split('T')[0],
              dueDate: new Date().toISOString().split('T')[0],
            });
            setModalOpen(true);
          }}
          leftIcon={<PlusIcon className="h-4 w-4" />}
        >
          New Assessment
        </Button>
      </div>

      {/* Filter and Search Toolbar */}
      <Card padding="sm">
        <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3">
          <div className="relative flex-1 max-w-md">
            <MagnifyingGlassIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search assessment title or description..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-xs sm:text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>

          <div className="flex flex-wrap items-center gap-2.5">
            <select
              value={courseFilter}
              onChange={(e) => setCourseFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Courses</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>

            <select
              value={typeFilter}
              onChange={(e) => setTypeFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Evaluation Types</option>
              <option value="MIDTERM">Midterm</option>
              <option value="FINAL">Final Exam</option>
              <option value="PROJECT">Project</option>
              <option value="QUIZ">Quiz</option>
              <option value="LAB">Lab Practical</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Main Table */}
      <Card padding="none" className="overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-xs text-slate-500">Loading assessments...</div>
        ) : filteredAssessments.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <DocumentTextIcon className="h-8 w-8 text-slate-400 mx-auto" />
            <p className="font-bold text-slate-700 dark:text-slate-300">No assessments found.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200/80 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Assessment Title</th>
                  <th className="py-3 px-4">Course</th>
                  <th className="py-3 px-4">Type</th>
                  <th className="py-3 px-4">Max Points</th>
                  <th className="py-3 px-4">Weightage</th>
                  <th className="py-3 px-4">Scheduled Date</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredAssessments.map((a) => {
                  const course = courses.find((c) => c.id === a.courseId);
                  return (
                    <tr
                      key={a.id}
                      className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors"
                    >
                      <td className="py-3 px-4">
                        <div className="font-bold text-slate-900 dark:text-slate-100">{a.title}</div>
                        <div className="text-[11px] text-slate-400 truncate max-w-xs">{a.description}</div>
                      </td>
                      <td className="py-3 px-4">
                        <span className="font-mono font-bold text-brand-600 dark:text-brand-400">
                          {course ? course.code : `Course #${a.courseId}`}
                        </span>
                        <div className="text-slate-500 truncate max-w-[160px]">{course?.name}</div>
                      </td>
                      <td className="py-3 px-4">
                        <Badge variant={getTypeVariant(a.type)} size="sm">
                          {a.type}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 font-bold text-slate-800 dark:text-slate-200">
                        {a.maxMarks} pts
                      </td>
                      <td className="py-3 px-4 font-semibold text-brand-600 dark:text-brand-400">
                        {a.weightage}%
                      </td>
                      <td className="py-3 px-4 font-mono text-slate-500">{a.scheduledDate}</td>
                      <td className="py-3 px-4">
                        <Badge variant={a.isPublished ? 'success' : 'default'} size="sm" dot>
                          {a.isPublished ? 'Published' : 'Draft'}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            type="button"
                            onClick={() => openEditModal(a)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Edit Assessment"
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            type="button"
                            onClick={() => setDeleteConfirm(a)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Delete Assessment"
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

      {/* Add / Edit Assessment Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingAssessment(null);
          reset();
        }}
        title={editingAssessment ? 'Edit Assessment Rubric' : 'Create New Assessment'}
        size="md"
      >
        <form onSubmit={handleSubmit(editingAssessment ? handleUpdate : handleCreate)} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Course
            </label>
            <select
              {...register('courseId')}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            >
              {courses.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Assessment Title
            </label>
            <input
              type="text"
              {...register('title')}
              placeholder="e.g. Midterm Exam on Graph Theory"
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
            {errors.title && <p className="text-[11px] text-rose-500 mt-1">{errors.title.message}</p>}
          </div>

          <div className="grid grid-cols-3 gap-3">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Type
              </label>
              <select
                {...register('type')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                <option value="MIDTERM">Midterm</option>
                <option value="FINAL">Final</option>
                <option value="PROJECT">Project</option>
                <option value="QUIZ">Quiz</option>
                <option value="LAB">Lab</option>
                <option value="ASSIGNMENT">Assignment</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Max Marks
              </label>
              <input
                type="number"
                {...register('maxMarks', { valueAsNumber: true })}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Weight %
              </label>
              <input
                type="number"
                min="1"
                max="100"
                {...register('weightage', { valueAsNumber: true })}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Scheduled Date
            </label>
            <input
              type="date"
              {...register('scheduledDate')}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Description & Syllabus Topics
            </label>
            <textarea
              rows={3}
              {...register('description')}
              placeholder="Exam scope, allowable materials, and grading criteria..."
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button
              variant="outline"
              type="button"
              onClick={() => {
                setModalOpen(false);
                setEditingAssessment(null);
                reset();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              {editingAssessment ? 'Save Changes' : 'Create Assessment'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Modal */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Assessment"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600 dark:text-slate-300">
            Are you sure you want to remove assessment <strong>{deleteConfirm?.title}</strong>? All recorded student marks for this assessment will be archived.
          </p>
          <div className="flex items-center justify-end gap-2.5 pt-2">
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleDelete}>
              Delete Assessment
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default AssessmentsPage;
