import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable, Button, Modal, Input, Select, FormField, Card } from '@/components';
import { api } from '@/services';
import { PlusIcon, MagnifyingGlassIcon, PencilIcon, TrashIcon, EyeIcon } from '@heroicons/react/24/outline';
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
  const [departments, setDepartments] = useState<Array<{id: string; name: string}>>([]);
  const [programs, setPrograms] = useState<Array<{id: string; name: string}>>([]);
  const [batches, setBatches] = useState<Array<{id: string; name: string}>>([]);
  const [sections, setSections] = useState<Array<{id: string; name: string}>>([]);

  const {
    control,
    handleSubmit,
    reset,
    setValue,
    watch,
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
    } catch (error) {
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
      setDepartments(depts.map(d => ({ id: d.id, name: d.name })));
      setPrograms(progs.map(p => ({ id: p.id, name: p.name })));
      setBatches(bats.map(b => ({ id: b.id, name: b.name })));
      setSections(secs.map(s => ({ id: s.id, name: s.name })));
    } catch (error) {
      console.error('Failed to load reference data:', error);
    }
  };

  const handleCreate = async (data: StudentFormData) => {
    try {
      await api.students.create(data as any);
      showToast({ type: 'success', title: 'Success', message: 'Student created successfully' });
      setModalOpen(false);
      reset();
      loadStudents();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create student' });
    }
  };

  const handleUpdate = async (data: StudentFormData) => {
    if (!editingStudent) return;
    try {
      await api.students.update(editingStudent.id, data as any);
      showToast({ type: 'success', title: 'Success', message: 'Student updated successfully' });
      setModalOpen(false);
      setEditingStudent(null);
      reset();
      loadStudents();
    } catch (error) {
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
    } catch (error) {
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

  const columns = [
    { key: 'studentId', header: 'Student ID', accessor: 'studentId' },
    { key: 'name', header: 'Name', accessor: (row: Student) => `${row.firstName} ${row.lastName}` },
    { key: 'email', header: 'Email', accessor: 'email' },
    { key: 'phone', header: 'Phone', accessor: 'phone' },
    { key: 'status', header: 'Status', accessor: 'status', render: (value: string) => (
      <Badge variant={value === 'ACTIVE' ? 'success' : value === 'INACTIVE' ? 'default' : value === 'GRADUATED' ? 'info' : 'warning'} size="sm">{value}</Badge>
    )},
    { key: 'departmentId', header: 'Department', accessor: (row: Student) => departments.find(d => d.id === row.departmentId)?.name || row.departmentId },
    { key: 'actions', header: 'Actions', render: (_, row: Student) => (
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
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Students</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Manage student records</p>
        </div>
        <Button onClick={() => { setEditingStudent(null); reset(); setModalOpen(true); }}>
          <PlusIcon className="h-5 w-5" />
          Add Student
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={students}
        keyAccessor="id"
        title={`${students.length} students`}
        searchable
        searchPlaceholder="Search students..."
        exportable
        onExport={(data) => console.log('Export', data)}
        loading={loading}
      />

      <Modal
        isOpen={modalOpen}
        onClose={() => { setModalOpen(false); setEditingStudent(null); reset(); }}
        title={editingStudent ? 'Edit Student' : 'Add Student'}
        size="lg"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setModalOpen(false); setEditingStudent(null); reset(); }}>Cancel</Button>
            <Button onClick={handleSubmit(editingStudent ? handleUpdate : handleCreate)}>Save</Button>
          </>
        }
      >
        <form onSubmit={handleSubmit(editingStudent ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <FormField name="studentId" label="Student ID" control={control} error={errors.studentId?.message} />
            <FormField name="firstName" label="First Name" control={control} error={errors.firstName?.message} />
            <FormField name="lastName" label="Last Name" control={control} error={errors.lastName?.message} />
            <FormField name="email" label="Email" type="email" control={control} error={errors.email?.message} />
            <FormField name="phone" label="Phone" control={control} />
            <FormField name="dateOfBirth" label="Date of Birth" type="date" control={control} error={errors.dateOfBirth?.message} />
            <FormField name="gender" label="Gender" type="select" options={[{ value: 'MALE', label: 'Male' }, { value: 'FEMALE', label: 'Female' }, { value: 'OTHER', label: 'Other' }]} control={control} error={errors.gender?.message} />
            <FormField name="enrollmentDate" label="Enrollment Date" type="date" control={control} error={errors.enrollmentDate?.message} />
            <FormField name="departmentId" label="Department" type="select" options={departments.map(d => ({ value: d.id, label: d.name }))} control={control} error={errors.departmentId?.message} />
            <FormField name="programId" label="Program" type="select" options={programs.map(p => ({ value: p.id, label: p.name }))} control={control} error={errors.programId?.message} />
            <FormField name="batchId" label="Batch" type="select" options={batches.map(b => ({ value: b.id, label: b.name }))} control={control} error={errors.batchId?.message} />
            <FormField name="sectionId" label="Section" type="select" options={[{ value: '', label: 'Select section' }, ...sections.map(s => ({ value: s.id, label: s.name }))]} control={control} />
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Student"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
            <Button variant="danger" onClick={handleDelete}>Delete</Button>
          </>
        }
      >
        <p>Are you sure you want to delete <strong>{deleteConfirm?.firstName} {deleteConfirm?.lastName}</strong> ({deleteConfirm?.studentId})? This action cannot be undone.</p>
      </Modal>
    </div>
  );
}