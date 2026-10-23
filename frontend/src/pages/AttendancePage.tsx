import { useEffect, useState } from 'react';
import { DataTable, Button, Card, Select, Badge } from '@/components';
import { api } from '@/services';
import { Attendance } from '@/types';
import { useToast } from '@/contexts';
import { CalendarIcon } from '@heroicons/react/24/outline';

export function AttendancePage() {
  const { showToast } = useToast();
  const [attendance, setAttendance] = useState<Attendance[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [selectedCourse, setSelectedCourse] = useState('');
  const [courses, setCourses] = useState<Array<{id: string; name: string}>>([]);

  useEffect(() => {
    loadAttendance();
    loadCourses();
  }, [selectedDate, selectedCourse]);

  const loadAttendance = async () => {
    setLoading(true);
    try {
      const response = await api.attendance.getAll({ page: 1, pageSize: 100 });
      let data = response.data;
      if (selectedCourse) {
        data = data.filter(a => a.enrollmentId === selectedCourse);
      }
      setAttendance(data);
    } catch (error) {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load attendance' });
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

  const columns = [
    { key: 'id', header: 'ID', accessor: (row: Attendance) => `#${row.id.slice(-6)}` },
    { key: 'enrollmentId', header: 'Enrollment', accessor: (row: Attendance) => row.enrollmentId },
    { key: 'date', header: 'Date', accessor: (row: Attendance) => new Date(row.date).toLocaleDateString() },
    { key: 'status', header: 'Status', accessor: 'status', render: (value: string) => (
      <Badge variant={value === 'PRESENT' ? 'success' : value === 'ABSENT' ? 'error' : value === 'LATE' ? 'warning' : 'info'} size="sm">{value}</Badge>
    )},
    { key: 'markedBy', header: 'Marked By', accessor: 'markedBy' },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Attendance</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Track and manage student attendance</p>
        </div>
        <div className="flex items-center gap-4">
          <div className="relative">
            <CalendarIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-neutral-400" />
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="pl-10 pr-4 py-2 text-sm bg-white dark:bg-neutral-900 border border-neutral-300 dark:border-neutral-600 rounded-lg focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500"
            />
          </div>
          <Select
            value={selectedCourse}
            onChange={setSelectedCourse}
            options={[{ value: '', label: 'All Courses' }, ...courses]}
            placeholder="Filter by course"
            className="w-64"
          />
        </div>
      </div>

      <DataTable
        columns={columns}
        data={attendance}
        keyAccessor="id"
        title={`${attendance.length} records`}
        searchable
        searchPlaceholder="Search attendance..."
        loading={loading}
      />
    </div>
  );
}