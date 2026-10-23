import { useEffect, useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, Button, Modal, Badge, Avatar } from '@/components/ui';
import { api } from '@/services';
import {
  PlusIcon,
  MagnifyingGlassIcon,
  PencilIcon,
  TrashIcon,
  EyeIcon,
  ArrowDownTrayIcon,
  UsersIcon,
  FunnelIcon,
} from '@heroicons/react/24/outline';
import { Student } from '@/types';
import { useToast } from '@/contexts';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';

const studentSchema = z.object({
  studentId: z.string().min(1, 'Student ID is required'),
  firstName: z.string().min(1, 'First name is required'),
  lastName: z.string().min(1, 'Last name is required'),
  email: z.string().email('Invalid email address'),
  phone: z.string().optional(),
  dateOfBirth: z.string().min(1, 'Date of birth is required'),
  gender: z.enum(['MALE', 'FEMALE', 'OTHER']),
  enrollmentDate: z.string().min(1, 'Enrollment date is required'),
  departmentId: z.string().min(1, 'Department is required'),
  programId: z.string().min(1, 'Program is required'),
  batchId: z.string().min(1, 'Batch is required'),
  sectionId: z.string().optional(),
});

type StudentFormData = z.infer<typeof studentSchema>;

export function StudentsPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState<Student | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Student | null>(null);

  // Filters
  const [search, setSearch] = useState('');
  const [departmentFilter, setDepartmentFilter] = useState('ALL');
  const [statusFilter, setStatusFilter] = useState('ALL');

  // Reference data
  const [departments, setDepartments] = useState<Array<{ id: string; name: string }>>([]);
  const [programs, setPrograms] = useState<Array<{ id: string; name: string }>>([]);
  const [batches, setBatches] = useState<Array<{ id: string; name: string }>>([]);
  const [sections, setSections] = useState<Array<{ id: string; name: string }>>([]);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<StudentFormData>({
    resolver: zodResolver(studentSchema),
  });

  useEffect(() => {
    loadStudents();
    loadReferenceData();
  }, []);

  const loadStudents = async () => {
    setLoading(true);
    try {
      const response = await api.students.getAll({ page: 1, pageSize: 100 });
      setStudents(response.data);
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load students' });
    } finally {
      setLoading(false);
    }
  };

  const loadReferenceData = async () => {
    try {
      const [depts, progs, bats, secs] = await Promise.all([
        api.departments.getAll(),
        api.programs.getAll(),
        api.batches.getAll(),
        api.sections.getAll(),
      ]);
      setDepartments(depts.map((d) => ({ id: d.id, name: d.name })));
      setPrograms(progs.map((p) => ({ id: p.id, name: p.name })));
      setBatches(bats.map((b) => ({ id: b.id, name: b.name })));
      setSections(secs.map((s) => ({ id: s.id, name: s.name })));
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: StudentFormData) => {
    try {
      await api.students.create({
        ...data,
        status: 'ACTIVE',
      });
      showToast({ type: 'success', title: 'Success', message: 'Student registered successfully' });
      setModalOpen(false);
      reset();
      loadStudents();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to register student' });
    }
  };

  const handleUpdate = async (data: StudentFormData) => {
    if (!editingStudent) return;
    try {
      await api.students.update(editingStudent.id, data);
      showToast({ type: 'success', title: 'Success', message: 'Student updated successfully' });
      setModalOpen(false);
      setEditingStudent(null);
      reset();
      loadStudents();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update student' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.students.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Student deleted successfully' });
      setDeleteConfirm(null);
      loadStudents();
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete student' });
    }
  };

  const openEditModal = (student: Student) => {
    setEditingStudent(student);
    reset({
      studentId: student.studentId,
      firstName: student.firstName,
      lastName: student.lastName,
      email: student.email,
      phone: student.phone || '',
      dateOfBirth: student.dateOfBirth,
      gender: student.gender,
      enrollmentDate: student.enrollmentDate,
      departmentId: student.departmentId,
      programId: student.programId,
      batchId: student.batchId,
      sectionId: student.sectionId || '',
    });
    setModalOpen(true);
  };

  const filteredStudents = useMemo(() => {
    return students.filter((student) => {
      const matchesSearch =
        search === '' ||
        student.firstName.toLowerCase().includes(search.toLowerCase()) ||
        student.lastName.toLowerCase().includes(search.toLowerCase()) ||
        student.studentId.toLowerCase().includes(search.toLowerCase()) ||
        student.email.toLowerCase().includes(search.toLowerCase());

      const matchesDept = departmentFilter === 'ALL' || student.departmentId === departmentFilter;
      const matchesStatus = statusFilter === 'ALL' || student.status === statusFilter;

      return matchesSearch && matchesDept && matchesStatus;
    });
  }, [students, search, departmentFilter, statusFilter]);

  const handleExportCSV = () => {
    const headers = ['Student ID', 'First Name', 'Last Name', 'Email', 'Department', 'Status', 'Enrollment Date'];
    const rows = filteredStudents.map((s) => [
      s.studentId,
      s.firstName,
      s.lastName,
      s.email,
      departments.find((d) => d.id === s.departmentId)?.name || s.departmentId,
      s.status,
      s.enrollmentDate,
    ]);

    const csvContent = 'data:text/csv;charset=utf-8,' + [headers.join(','), ...rows.map((e) => e.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `students_export_${new Date().toISOString().split('T')[0]}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    showToast({ type: 'success', title: 'Export Complete', message: 'CSV downloaded successfully.' });
  };

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Students Directory
            </h1>
            <Badge variant="brand" size="md">
              {filteredStudents.length} Students
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            View, filter, register, and update academic profiles and enrollment status.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Button
            variant="outline"
            size="sm"
            onClick={handleExportCSV}
            leftIcon={<ArrowDownTrayIcon className="h-4 w-4" />}
          >
            Export CSV
          </Button>
          <Button
            size="sm"
            onClick={() => {
              setEditingStudent(null);
              reset({
                studentId: `CS2024${String(students.length + 1).padStart(3, '0')}`,
                firstName: '',
                lastName: '',
                email: '',
                phone: '',
                dateOfBirth: '2005-01-01',
                gender: 'MALE',
                enrollmentDate: new Date().toISOString().split('T')[0],
                departmentId: departments[0]?.id || '1',
                programId: programs[0]?.id || '1',
                batchId: batches[0]?.id || '2',
                sectionId: '1',
              });
              setModalOpen(true);
            }}
            leftIcon={<PlusIcon className="h-4 w-4" />}
          >
            New Student
          </Button>
        </div>
      </div>

      {/* Filter and Search Toolbar */}
      <Card padding="sm">
        <div className="flex flex-col md:flex-row items-stretch md:items-center justify-between gap-3">
          {/* Search */}
          <div className="relative flex-1 max-w-md">
            <MagnifyingGlassIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <input
              type="text"
              placeholder="Filter by name, ID, or email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full pl-9 pr-4 py-2 text-xs sm:text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>

          {/* Select Filters */}
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
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Statuses</option>
              <option value="ACTIVE">Active</option>
              <option value="INACTIVE">Inactive</option>
              <option value="GRADUATED">Graduated</option>
              <option value="SUSPENDED">Suspended</option>
            </select>
          </div>
        </div>
      </Card>

      {/* Main Table */}
      <Card padding="none" className="overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-xs text-slate-500">Loading student directory...</div>
        ) : filteredStudents.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <div className="p-3 bg-slate-100 dark:bg-slate-800 rounded-full w-fit mx-auto text-slate-400">
              <UsersIcon className="h-6 w-6" />
            </div>
            <p className="font-bold text-slate-700 dark:text-slate-300">No students match your filter criteria.</p>
            <p className="text-xs text-slate-400">Try modifying your search term or department filter.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200/80 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Student Name</th>
                  <th className="py-3 px-4">ID Number</th>
                  <th className="py-3 px-4">Department</th>
                  <th className="py-3 px-4">Gender</th>
                  <th className="py-3 px-4">Admitted</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredStudents.map((student) => {
                  const deptName = departments.find((d) => d.id === student.departmentId)?.name || 'General';
                  return (
                    <tr
                      key={student.id}
                      className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors group"
                    >
                      <td className="py-3 px-4">
                        <div className="flex items-center gap-3">
                          <Avatar
                            name={`${student.firstName} ${student.lastName}`}
                            size="sm"
                            variant="circle"
                          />
                          <div>
                            <span
                              onClick={() => navigate(`/students/${student.id}`)}
                              className="font-bold text-slate-900 dark:text-slate-100 hover:text-brand-600 dark:hover:text-brand-400 cursor-pointer transition-colors"
                            >
                              {student.firstName} {student.lastName}
                            </span>
                            <div className="text-[11px] text-slate-400 truncate max-w-[180px]">
                              {student.email}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="py-3 px-4 font-mono font-semibold text-slate-700 dark:text-slate-300">
                        {student.studentId}
                      </td>
                      <td className="py-3 px-4 text-slate-600 dark:text-slate-300 font-medium">
                        {deptName}
                      </td>
                      <td className="py-3 px-4 text-slate-500 capitalize">
                        {student.gender?.toLowerCase() || 'N/A'}
                      </td>
                      <td className="py-3 px-4 text-slate-500">
                        {student.enrollmentDate}
                      </td>
                      <td className="py-3 px-4">
                        <Badge
                          variant={
                            student.status === 'ACTIVE'
                              ? 'success'
                              : student.status === 'GRADUATED'
                              ? 'info'
                              : 'default'
                          }
                          size="sm"
                          dot
                        >
                          {student.status}
                        </Badge>
                      </td>
                      <td className="py-3 px-4 text-right">
                        <div className="flex items-center justify-end gap-1">
                          <button
                            type="button"
                            onClick={() => navigate(`/students/${student.id}`)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-brand-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="View Academic Transcript"
                          >
                            <EyeIcon className="h-4 w-4" />
                          </button>
                          <button
                            type="button"
                            onClick={() => openEditModal(student)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-amber-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Edit Student"
                          >
                            <PencilIcon className="h-4 w-4" />
                          </button>
                          <button
                            type="button"
                            onClick={() => setDeleteConfirm(student)}
                            className="p-1.5 rounded-lg text-slate-400 hover:text-rose-600 hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors"
                            title="Delete Student"
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

      {/* Add / Edit Student Modal */}
      <Modal
        isOpen={modalOpen}
        onClose={() => {
          setModalOpen(false);
          setEditingStudent(null);
          reset();
        }}
        title={editingStudent ? `Edit Student: ${editingStudent.studentId}` : 'Register New Student'}
        size="lg"
      >
        <form onSubmit={handleSubmit(editingStudent ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Student ID
              </label>
              <input
                type="text"
                {...register('studentId')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
              {errors.studentId && <p className="text-[11px] text-rose-500 mt-1">{errors.studentId.message}</p>}
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
                Gender
              </label>
              <select
                {...register('gender')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              >
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
                <option value="OTHER">Other</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Date of Birth
              </label>
              <input
                type="date"
                {...register('dateOfBirth')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Admission Date
              </label>
              <input
                type="date"
                {...register('enrollmentDate')}
                className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
                Phone
              </label>
              <input
                type="tel"
                {...register('phone')}
                placeholder="+1 (555) 000-0000"
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
                setEditingStudent(null);
                reset();
              }}
            >
              Cancel
            </Button>
            <Button type="submit">
              {editingStudent ? 'Save Updates' : 'Confirm Registration'}
            </Button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Confirm Student Record Deletion"
        size="sm"
      >
        <div className="space-y-4">
          <p className="text-xs text-slate-600 dark:text-slate-300">
            Are you sure you want to permanently delete record for{' '}
            <strong className="text-slate-900 dark:text-white">
              {deleteConfirm?.firstName} {deleteConfirm?.lastName}
            </strong>{' '}
            ({deleteConfirm?.studentId})? Associated enrollments and transcripts will be archived.
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

export default StudentsPage;
