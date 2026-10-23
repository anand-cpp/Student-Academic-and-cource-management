import { useEffect, useState, useMemo } from 'react';
import { Card, Button, Modal, Badge } from '@/components/ui';
import { api } from '@/services';
import {
  PlusIcon,
  MagnifyingGlassIcon,
  PencilIcon,
  TrashIcon,
  BookOpenIcon,
  AcademicCapIcon,
  FunnelIcon,
} from '@heroicons/react/24/outline';
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
  const { showToast } = useToast();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState<Course | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Course | null>(null);

  // Filters
  const [search, setSearch] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');
  const [semesterFilter, setSemesterFilter] = useState('ALL');

  // Reference data
  const [departments, setDepartments] = useState<Array<{ id: string; name: string }>>([]);
  const [programs, setPrograms] = useState<Array<{ id: string; name: string }>>([]);

  const {
    register,
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
    } catch {
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
      setDepartments(depts.map((d) => ({ id: d.id, name: d.name })));
      setPrograms(progs.map((p) => ({ id: p.id, name: p.name })));
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: CourseFormData) => {
    try {
      await api.courses.create({
        ...data,
        isActive: true,
      });
      showToast({ type: 'success', title: 'Success', message: 'Course created successfully' });
      setModalOpen(false);
      reset();
      loadCourses();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create course' });
    }
  };

  const handleUpdate = async (data: CourseFormData) => {
    if (!editingCourse) return;
    try {
      await api.courses.update(editingCourse.id, data);
      showToast({ type: 'success', title: 'Success', message: 'Course updated successfully' });
      setModalOpen(false);
      setEditingCourse(null);
      reset();
      loadCourses();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update course' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.courses.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Course removed from catalog' });
      setDeleteConfirm(null);
      loadCourses();
    } catch {
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

  const filteredCourses = useMemo(() => {
    return courses.filter((c) => {
      const matchSearch =
        search === '' ||
        c.code.toLowerCase().includes(search.toLowerCase()) ||
        c.name.toLowerCase().includes(search.toLowerCase());

      const matchDept = departmentFilter === 'ALL' || c.departmentId === departmentFilter;
      const matchSem = semesterFilter === 'ALL' || String(c.semester) === semesterFilter;

      return matchSearch && matchDept && matchSem;
    });
  }, [courses, search, departmentFilter, semesterFilter]);

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Course Catalog
            </h1>
            <Badge variant="brand" size="md">
              {filteredCourses.length} Courses
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Curriculum offerings, credits, prerequisites, and faculty assignments.
          </p>
        </div>

        <Button
          size="sm"
          onClick={() => {
            setEditingCourse(null);
            reset({
              code: 'CS',
              name: '',
              description: '',
              credits: 3,
              theoryHours: 45,
              practicalHours: 15,
              departmentId: departments[0]?.id || '1',
              programId: programs[0]?.id || '1',
              semester: 1,
              type: 'CORE',
            });
            setModalOpen(true);
          }}
          leftIcon={<PlusIcon className="h-4 w-4" />}
        >
          Add New Course
        </Button>
      </div>

      {/* Filter and Search Toolbar */}
      <Card padding="sm">
        <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3">
          <div className="relative flex-1 max-w-md">
            <MagnifyingGlassIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by course code or title..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-xs sm:text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>

          <div className="flex flex-wrap items-center gap-2.5">
            <div className="flex items-center gap-1.5 text-xs text-slate-500">
              <FunnelIcon className="h-4 w-4" />
              <span>Dept:</span>
            </div>
            <select
              value={departmentFilter}
              onChange={(e) => setDepartmentFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Departments</option>
              {departments.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.name}
                </option>
              ))}
            </select>

            <select
              value={semesterFilter}
              onChange={(e) => setSemesterFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Semesters</option>
              <option value="1">Semester 1</option>
              <option value="2">Semester 2</option>
              <option value="3">Semester 3</option>
              <option value="4">Semester 4</option>
              <option value="5">Semester 5</option>
              <option value="6">Semester 6</option>
              <option value="7">Semester 7</option>
              <option value="8">Semester 8</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Course Cards Grid */}
      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3, 4, 5, 6].map((i) => (
            <Card key={i} className="h-48 animate-pulse bg-slate-200 dark:bg-slate-800 rounded-2xl" />
          ))}
        </div>
      ) : filteredCourses.length === 0 ? (
        <Card className="p-12 text-center space-y-3">
          <BookOpenIcon className="h-8 w-8 text-slate-400 mx-auto" />
          <p className="font-bold text-slate-700 dark:text-slate-300">No courses match the filters.</p>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filteredCourses.map((course) => {
            const dept = departments.find((d) => d.id === course.departmentId)?.name || 'General';
            return (
              <Card
                key={course.id}
                variant="interactive"
                className="flex flex-col justify-between group"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-2">
                    <div className="flex items-center gap-2">
                      <span className="font-mono text-base font-extrabold text-brand-600 dark:text-brand-400 tracking-tight">
                        {course.code}
                      </span>
                      <Badge variant="outline" size="sm">
                        Sem {course.semester}
                      </Badge>
                    </div>
                    <Badge variant={course.isActive ? 'success' : 'default'} size="sm" dot>
                      {course.isActive ? 'Active' : 'Inactive'}
                    </Badge>
                  </div>

                  <h3 className="text-base font-bold text-slate-900 dark:text-slate-100 group-hover:text-brand-600 dark:group-hover:text-brand-400 transition-colors">
                    {course.name}
                  </h3>
                  <p className="text-xs text-slate-500 dark:text-slate-400 line-clamp-2 mt-1.5">
                    {course.description || 'Comprehensive university credit curriculum course.'}
                  </p>
                </div>

                <div className="pt-4 mt-4 border-t border-slate-100 dark:border-slate-800 space-y-3">
                  <div className="flex items-center justify-between text-xs text-slate-500 dark:text-slate-400">
                    <span className="truncate">{dept}</span>
                    <span className="font-bold text-slate-800 dark:text-slate-200">
                      {course.credits} Credits
                    </span>
                  </div>

                  {course.prerequisites && course.prerequisites.length > 0 && (
                    <div className="flex flex-wrap items-center gap-1">
                      <span className="text-[10px] text-slate-400">Pre-req:</span>
                      {course.prerequisites.map((pr) => (
                        <span
                          key={pr}
                          className="px-1.5 py-0.5 rounded text-[10px] font-mono bg-slate-100 dark:bg-slate-800 text-slate-600 dark:text-slate-400"
                        >
                          {pr}
                        </span>
                      ))}
                    </div>
                  )}

                  <div className="flex items-center justify-end gap-1.5 pt-1">
                    <button
                      type="button"
                      onClick={() => openEditModal(course)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                      title="Edit Course"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>
                    <button
                      type="button"
                      onClick={() => setDeleteConfirm(course)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                      title="Delete Course"
                    >
                      <TrashIcon className="h-4 w-4" />
                    </button>
                  </div>
                </div>
              </Card>
            );
          })}
        </div>
      )}

      {/* Add / Edit Course Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingCourse(null);
          reset();
        }}
        title={editingCourse ? `Edit Course: ${editingCourse.code}` : 'Add Course to Curriculum'}
        size="lg"
      >
        <form onSubmit={handleSubmit(editingCourse ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Course Code
              </label>
              <input
                type="text"
                {...register('code')}
                placeholder="e.g. CS201"
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.code && <p className="text-[11px] text-rose-500 mt-1">{errors.code.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Course Title
              </label>
              <input
                type="text"
                {...register('name')}
                placeholder="e.g. Data Structures and Algorithms"
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.name && <p className="text-[11px] text-rose-500 mt-1">{errors.name.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Department
              </label>
              <select
                {...register('departmentId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                {departments.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Program
              </label>
              <select
                {...register('programId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                {programs.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.name}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Credits
              </label>
              <input
                type="number"
                {...register('credits', { valueAsNumber: true })}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Semester
              </label>
              <input
                type="number"
                min="1"
                max="8"
                {...register('semester', { valueAsNumber: true })}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Course Type
              </label>
              <select
                {...register('type')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                <option value="CORE">Core</option>
                <option value="ELECTIVE">Elective</option>
                <option value="LAB">Lab</option>
                <option value="PROJECT">Project</option>
                <option value="SEMINAR">Seminar</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Theory Hours
              </label>
              <input
                type="number"
                {...register('theoryHours', { valueAsNumber: true })}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Course Description & Objectives
            </label>
            <textarea
              rows={3}
              {...register('description')}
              placeholder="Syllabus scope, grading criteria, and prerequisite knowledge..."
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button
              variant="outline"
              type="button"
              onClick={() => {
                setModalOpen(false);
                setEditingCourse(null);
                reset();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              {editingCourse ? 'Save Changes' : 'Create Course'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Confirm Course Removal"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600 dark:text-slate-300">
            Are you sure you want to remove{' '}
            <strong className="text-slate-900 dark:text-white">
              {deleteConfirm?.code}: {deleteConfirm?.name}
            </strong>{' '}
            from active curriculum?
          </p>
          <div className="flex items-center justify-end gap-2.5 pt-2">
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleDelete}>
              Delete Course
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default CoursesPage;
