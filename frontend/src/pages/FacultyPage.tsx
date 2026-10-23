import { useEffect, useState, useMemo } from 'react';
import { Card, Button, Modal, Badge, Avatar } from '@/components/ui';
import { api } from '@/services';
import {
  PlusIcon,
  MagnifyingGlassIcon,
  PencilIcon,
  TrashIcon,
  AcademicCapIcon,
  BriefcaseIcon,
  EnvelopeIcon,
  PhoneIcon,
  FunnelIcon,
} from '@heroicons/react/24/outline';
import { Faculty } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const facultySchema = z.object({
  employeeId: z.string().min(1, 'Employee ID is required'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.string().email('Invalid email address'),
  phone: z.string().optional(),
  dateOfBirth: z.string().optional(),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']).optional(),
  qualification: z.string().optional(),
  specialization: z.string().optional(),
  experienceYears: z.number().optional(),
  joiningDate: z.string().min(1, 'Joining date is required'),
  departmentId: z.string().min(1, 'Department is required'),
  designationId: z.string().min(1, 'Designation is required'),
});

type FacultyFormData = z.infer<typeof facultySchema>;

const DESIGNATIONS = [
  { value: 'PROFESSOR', label: 'Full Professor' },
  { value: 'ASSOCIATE_PROFESSOR', label: 'Associate Professor' },
  { value: 'ASSISTANT_PROFESSOR', label: 'Assistant Professor' },
  { value: 'LECTURER', label: 'Senior Lecturer' },
  { value: 'INSTRUCTOR', label: 'Instructor' },
];

export function FacultyPage() {
  const { showToast } = useToast();
  const [faculty, setFaculty] = useState<Faculty[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingFaculty, setEditingFaculty] = useState<Faculty | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Faculty | null>(null);

  // Filters
  const [search, setSearch] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');
  const [designationFilter, setDesignationFilter] = useState('ALL');

  // Reference data
  const [departments, setDepartments] = useState<Array<{ id: string; name: string }>>([]);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FacultyFormData>({
    resolver: zodResolver(facultySchema),
  });

  useEffect(() => {
    loadFaculty();
    loadDepartments();
  }, []);

  const loadFaculty = async () => {
    setLoading(true);
    try {
      const response = await api.faculty.getAll({ page: 1, pageSize: 100 });
      setFaculty(response.data);
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load faculty directory' });
    } finally {
      setLoading(false);
    }
  };

  const loadDepartments = async () => {
    try {
      const depts = await api.departments.getAll();
      setDepartments(depts.map((d) => ({ id: d.id, name: d.name })));
    } catch (error) {
      console.error('Failed to load departments:', error);
    }
  };

  const handleCreate = async (data: FacultyFormData) => {
    try {
      await api.faculty.create({
        ...data,
        status: 'ACTIVE',
      });
      showToast({ type: 'success', title: 'Success', message: 'Faculty member appointed successfully' });
      setModalOpen(false);
      reset();
      loadFaculty();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to add faculty record' });
    }
  };

  const handleUpdate = async (data: FacultyFormData) => {
    if (!editingFaculty) return;
    try {
      await api.faculty.update(editingFaculty.id, data);
      showToast({ type: 'success', title: 'Success', message: 'Faculty record updated successfully' });
      setModalOpen(false);
      setEditingFaculty(null);
      reset();
      loadFaculty();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update faculty record' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.faculty.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Faculty member record removed' });
      setDeleteConfirm(null);
      loadFaculty();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete faculty record' });
    }
  };

  const openEditModal = (fac: Faculty) => {
    setEditingFaculty(fac);
    reset({
      employeeId: fac.employeeId,
      firstName: fac.firstName,
      lastName: fac.lastName,
      email: fac.email,
      phone: fac.phone || '',
      dateOfBirth: fac.dateOfBirth || '',
      gender: fac.gender,
      qualification: fac.qualification || '',
      specialization: fac.specialization || '',
      experienceYears: fac.experienceYears || 0,
      joiningDate: fac.joiningDate,
      departmentId: fac.departmentId,
      designationId: fac.designationId,
    });
    setModalOpen(true);
  };

  const filteredFaculty = useMemo(() => {
    return faculty.filter((f) => {
      const matchSearch =
        search === '' ||
        f.firstName.toLowerCase().includes(search.toLowerCase()) ||
        f.lastName.toLowerCase().includes(search.toLowerCase()) ||
        f.employeeId.toLowerCase().includes(search.toLowerCase()) ||
        f.specialization?.toLowerCase().includes(search.toLowerCase()) ||
        f.email.toLowerCase().includes(search.toLowerCase());

      const matchDept = departmentFilter === 'ALL' || f.departmentId === departmentFilter;
      const matchDesig = designationFilter === 'ALL' || f.designationId === designationFilter;

      return matchSearch && matchDept && matchDesig;
    });
  }, [faculty, search, departmentFilter, designationFilter]);

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Faculty Roster
            </h1>
            <Badge variant="brand" size="md">
              {filteredFaculty.length} Professors
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Teaching staff, professorial designations, research interests, and departmental affiliations.
          </p>
        </div>

        <Button
          size="sm"
          onClick={() => {
            setEditingFaculty(null);
            reset({
              employeeId: `FAC00${faculty.length + 1}`,
              firstName: '',
              lastName: '',
              email: '',
              phone: '',
              qualification: 'Ph.D. in Computer Science',
              specialization: 'Artificial Intelligence & Systems',
              experienceYears: 5,
              joiningDate: new Date().toISOString().split('T')[0],
              departmentId: departments[0]?.id || '1',
              designationId: 'PROFESSOR',
            });
            setModalOpen(true);
          }}
          leftIcon={<PlusIcon className="h-4 w-4" />}
        >
          Appoint Faculty
        </Button>
      </div>

      {/* Filter and Search Toolbar */}
      <Card padding="sm">
        <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3">
          <div className="relative flex-1 max-w-md">
            <MagnifyingGlassIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Search by faculty name, specialization, or ID..."
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
              value={designationFilter}
              onChange={(e) => setDesignationFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Designations</option>
              {DESIGNATIONS.map((d) => (
                <option key={d.value} value={d.value}>
                  {d.label}
                </option>
              ))}
            </select>
          </div>
        </div>
      </Card>

      {/* Faculty Cards Grid */}
      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {[1, 2, 3, 4].map((i) => (
            <Card key={i} className="h-56 animate-pulse bg-slate-200 dark:bg-slate-800 rounded-2xl" />
          ))}
        </div>
      ) : filteredFaculty.length === 0 ? (
        <Card className="p-12 text-center space-y-3">
          <AcademicCapIcon className="h-8 w-8 text-slate-400 mx-auto" />
          <p className="font-bold text-slate-700 dark:text-slate-300">No faculty members found.</p>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filteredFaculty.map((f) => {
            const dept = departments.find((d) => d.id === f.departmentId)?.name || 'General Academic';
            return (
              <Card
                key={f.id}
                variant="interactive"
                className="flex flex-col justify-between group"
              >
                <div>
                  <div className="flex items-start justify-between gap-3 mb-4">
                    <div className="flex items-center gap-3">
                      <Avatar
                        name={`${f.firstName} ${f.lastName}`}
                        size="md"
                        variant="circle"
                      />
                      <div>
                        <h3 className="font-bold text-slate-900 dark:text-slate-100 text-sm group-hover:text-brand-600 dark:group-hover:text-brand-400 transition-colors">
                          Prof. {f.firstName} {f.lastName}
                        </h3>
                        <span className="font-mono text-[11px] text-slate-400">
                          {f.employeeId}
                        </span>
                      </div>
                    </div>
                    <Badge variant={f.status === 'ACTIVE' ? 'success' : 'default'} size="sm" dot>
                      {f.status}
                    </Badge>
                  </div>

                  <div className="space-y-1.5 text-xs text-slate-600 dark:text-slate-300">
                    <p className="font-semibold text-brand-600 dark:text-brand-400">
                      {f.designationId?.replace('_', ' ')} • {dept}
                    </p>
                    <p className="text-[11px] text-slate-500 dark:text-slate-400 truncate">
                      {f.qualification}
                    </p>
                    <div className="p-2 rounded-xl bg-slate-50 dark:bg-slate-800/60 border border-slate-100 dark:border-slate-800 mt-2">
                      <span className="text-[10px] text-slate-400 block font-semibold uppercase">Specialization:</span>
                      <span className="text-xs font-medium text-slate-700 dark:text-slate-300 line-clamp-1">
                        {f.specialization || 'General Engineering & Science'}
                      </span>
                    </div>
                  </div>
                </div>

                <div className="pt-4 mt-4 border-t border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
                  <div className="flex items-center gap-1.5 text-slate-400">
                    <EnvelopeIcon className="h-3.5 w-3.5" />
                    <span className="truncate max-w-[140px]">{f.email}</span>
                  </div>

                  <div className="flex items-center gap-1">
                    <button
                      type="button"
                      onClick={() => openEditModal(f)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                      title="Edit Faculty Record"
                    >
                      <PencilIcon className="h-4 w-4" />
                    </button>
                    <button
                      type="button"
                      onClick={() => setDeleteConfirm(f)}
                      className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                      title="Delete Faculty"
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

      {/* Add / Edit Faculty Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingFaculty(null);
          reset();
        }}
        title={editingFaculty ? `Edit Faculty: ${editingFaculty.employeeId}` : 'Appoint New Faculty Member'}
        size="lg"
      >
        <form onSubmit={handleSubmit(editingFaculty ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Employee ID
              </label>
              <input
                type="text"
                {...register('employeeId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.employeeId && <p className="text-[11px] text-rose-500 mt-1">{errors.employeeId.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Email Address
              </label>
              <input
                type="email"
                {...register('email')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.email && <p className="text-[11px] text-rose-500 mt-1">{errors.email.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                First Name
              </label>
              <input
                type="text"
                {...register('firstName')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.firstName && <p className="text-[11px] text-rose-500 mt-1">{errors.firstName.message}</p>}
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Last Name
              </label>
              <input
                type="text"
                {...register('lastName')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.lastName && <p className="text-[11px] text-rose-500 mt-1">{errors.lastName.message}</p>}
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
                Designation
              </label>
              <select
                {...register('designationId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                {DESIGNATIONS.map((d) => (
                  <option key={d.value} value={d.value}>
                    {d.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Qualification
              </label>
              <input
                type="text"
                {...register('qualification')}
                placeholder="e.g. Ph.D. Computer Science, Stanford"
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Specialization
              </label>
              <input
                type="text"
                {...register('specialization')}
                placeholder="e.g. Artificial Intelligence, Neural Networks"
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Experience (Years)
              </label>
              <input
                type="number"
                {...register('experienceYears', { valueAsNumber: true })}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Appointment Date
              </label>
              <input
                type="date"
                {...register('joiningDate')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button
              variant="outline"
              type="button"
              onClick={() => {
                setModalOpen(false);
                setEditingFaculty(null);
                reset();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              {editingFaculty ? 'Save Updates' : 'Confirm Appointment'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Confirm Faculty Record Deletion"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600 dark:text-slate-300">
            Are you sure you want to remove faculty record for{' '}
            <strong className="text-slate-900 dark:text-white">
              Prof. {deleteConfirm?.firstName} {deleteConfirm?.lastName}
            </strong>{' '}
            ({deleteConfirm?.employeeId})?
          </p>
          <div className="flex items-center justify-end gap-2.5 pt-2">
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>
              Cancel
            </Button>
            <Button variant="danger" onClick={handleDelete}>
              Delete Record
            </Button>
          </div>
        </div>
      </Modal>
    </div>
  );
}

export default FacultyPage;
