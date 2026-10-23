import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { DataTable, Button, Modal, FormField, Card } from '@/components';
import { api } from '@/services';
import { PlusIcon, PencilIcon, TrashIcon } from '@heroicons/react/24/outline';
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

export function FacultyPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [faculty, setFaculty] = useState<Faculty[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingFaculty, setEditingFaculty] = useState<Faculty | null>(null);
  const [deleteConfirm, setDeleteConfirm] = useState<Faculty | null>(null);
  const [departments, setDepartments] = useState<Array<{id: string; name: string}>>([]);

  const {
    control,
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
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load faculty' });
    } finally {
      setLoading(false);
    }
  };

  const loadDepartments = async () => {
    try {
      const depts = await api.departments.getAll();
      setDepartments(depts.map(d => ({ id: d.id, name: d.name })));
    } catch (error) {
      console.error('Failed to load departments:', error);
    }
  };

  const handleCreate = async (data: FacultyFormData) => {
    try {
      await api.faculty.create(data as any);
      showToast({ type: 'success', title: 'Success', message: 'Faculty created successfully' });
      setModalOpen(false);
      reset();
      loadFaculty();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to create faculty' });
    }
  };

  const handleUpdate = async (data: FacultyFormData) => {
    if (!editingFaculty) return;
    try {
      await api.faculty.update(editingFaculty.id, data as any);
      showToast({ type: 'success', title: 'Success', message: 'Faculty updated successfully' });
      setModalOpen(false);
      setEditingFaculty(null);
      reset();
      loadFaculty();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to update faculty' });
    }
  };

  const handleDelete = async () => {
    if (!deleteConfirm) return;
    try {
      await api.faculty.delete(deleteConfirm.id);
      showToast({ type: 'success', title: 'Success', message: 'Faculty deleted successfully' });
      setDeleteConfirm(null);
      loadFaculty();
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to delete faculty' });
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

  const columns = [
    { key: 'employeeId', header: 'Employee ID', accessor: 'employeeId' },
    { key: 'name', header: 'Name', accessor: (row: Faculty) => `${row.firstName} ${row.lastName}` },
    { key: 'email', header: 'Email', accessor: 'email' },
    { key: 'phone', header: 'Phone', accessor: 'phone' },
    { key: 'qualification', header: 'Qualification', accessor: 'qualification' },
    { key: 'specialization', header: 'Specialization', accessor: 'specialization' },
    { key: 'status', header: 'Status', accessor: 'status', render: (value: string) => (
      <span className={`px-2 py-1 text-xs font-medium rounded-full ${value === 'ACTIVE' ? 'bg-success-100 text-success-700' : value === 'ON_LEAVE' ? 'bg-warning-100 text-warning-700' : 'bg-neutral-100 text-neutral-700'}`}>{value}</span>
    )},
    { key: 'actions', header: 'Actions', render: (_, row: Faculty) => (
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
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Faculty</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Manage faculty records</p>
        </div>
        <Button onClick={() => { setEditingFaculty(null); reset(); setModalOpen(true); }}>
          <PlusIcon className="h-5 w-5" />
          Add Faculty
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={faculty}
        keyAccessor="id"
        title={`${faculty.length} faculty members`}
        searchable
        searchPlaceholder="Search faculty..."
        loading={loading}
      />

      <Modal
        isOpen={modalOpen}
        onClose={() => { setModalOpen(false); setEditingFaculty(null); reset(); }}
        title={editingFaculty ? 'Edit Faculty' : 'Add Faculty'}
        size="lg"
        footer={
          <>
            <Button variant="secondary" onClick={() => { setModalOpen(false); setEditingFaculty(null); reset(); }}>Cancel</Button>
            <Button onClick={handleSubmit(editingFaculty ? handleUpdate : handleCreate)}>Save</Button>
          </>
        }
      >
        <form onSubmit={handleSubmit(editingFaculty ? handleUpdate : handleCreate)} className="space-y-4">
          <div className="grid gap-4 md:grid-cols-2">
            <FormField name="employeeId" label="Employee ID" control={control} error={errors.employeeId?.message} />
            <FormField name="firstName" label="First Name" control={control} error={errors.firstName?.message} />
            <FormField name="lastName" label="Last Name" control={control} error={errors.lastName?.message} />
            <FormField name="email" label="Email" type="email" control={control} error={errors.email?.message} />
            <FormField name="phone" label="Phone" control={control} />
            <FormField name="dateOfBirth" label="Date of Birth" type="date" control={control} />
            <FormField name="gender" label="Gender" type="select" options={[{ value: 'MALE', label: 'Male' }, { value: 'FEMALE', label: 'Female' }, { value: 'OTHER', label: 'Other' }]} control={control} />
            <FormField name="qualification" label="Qualification" control={control} />
            <FormField name="specialization" label="Specialization" control={control} />
            <FormField name="experienceYears" label="Experience (Years)" type="number" control={control} />
            <FormField name="joiningDate" label="Joining Date" type="date" control={control} error={errors.joiningDate?.message} />
            <FormField name="departmentId" label="Department" type="select" options={departments.map(d => ({ value: d.id, label: d.name }))} control={control} error={errors.departmentId?.message} />
            <FormField name="designationId" label="Designation" type="select" options={departments.map(d => ({ value: d.id, label: d.name }))} control={control} error={errors.designationId?.message} />
          </div>
        </form>
      </Modal>

      <Modal
        isOpen={!!deleteConfirm}
        onClose={() => setDeleteConfirm(null)}
        title="Delete Faculty"
        size="sm"
        footer={
          <>
            <Button variant="secondary" onClick={() => setDeleteConfirm(null)}>Cancel</Button>
            <Button variant="danger" onClick={handleDelete}>Delete</Button>
          </>
        }
      >
        <p>Are you sure you want to delete <strong>{deleteConfirm?.firstName} {deleteConfirm?.lastName}</strong> ({deleteConfirm?.employeeId})? This action cannot be undone.</p>
      </Modal>
    </div>
  );
}