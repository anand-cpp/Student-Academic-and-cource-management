import { useEffect, useState, useMemo } from 'react';
import { Card, Button, Modal, Badge } from '@/components/ui';
import { api } from '@/services';
import { Attendance, AttendanceStatus, Course } from '@/types';
import { useToast } from '@/contexts';
import {
  CalendarDaysIcon,
  CheckCircleIcon,
  XCircleIcon,
  ClockIcon,
  PlusIcon,
  FunnelIcon,
  ArrowDownTrayIcon,
} from '@heroicons/react/24/outline';

export function AttendancePage() {
  const { showToast } = useToast();
  const [attendance, setAttendance] = useState<Attendance[]>([]);
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);

  // Filters
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [selectedCourse, setSelectedCourse] = useState('ALL');
  const [sessionModalOpen, setSessionModalOpen] = useState(false);

  // Form for new session
  const [newCourseId, setNewCourseId] = useState('1');
  const [newTopic, setNewTopic] = useState('');
  const [newDate, setNewDate] = useState(new Date().toISOString().split('T')[0]);

  useEffect(() => {
    loadAttendance();
    loadCourses();
  }, []);

  const loadAttendance = async () => {
    setLoading(true);
    try {
      const response = await api.attendance.getRecords({ page: 1, pageSize: 100 });
      setAttendance(response.data);
    } catch {
      showToast({ type: 'error', title: 'Error', message: 'Failed to load attendance records' });
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

  const handleStatusChange = (id: string, newStatus: AttendanceStatus) => {
    setAttendance((prev) =>
      prev.map((a) => (a.id === id ? { ...a, status: newStatus, updatedAt: new Date().toISOString() } : a))
    );
    showToast({
      type: 'success',
      title: 'Status Updated',
      message: `Attendance marked as ${newStatus}`,
    });
  };

  const handleMarkAllPresent = () => {
    setAttendance((prev) =>
      prev.map((a) => ({ ...a, status: 'PRESENT', updatedAt: new Date().toISOString() }))
    );
    showToast({
      type: 'success',
      title: 'Roll Call Complete',
      message: 'All visible students marked Present.',
    });
  };

  const handleCreateSession = (e: React.FormEvent) => {
    e.preventDefault();
    const newRecord: Attendance = {
      id: String(Date.now()),
      enrollmentId: newCourseId,
      date: newDate,
      status: 'PRESENT',
      markedBy: 'Prof. Emily Johnson',
      remarks: newTopic || 'Lecture Session',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
    };
    setAttendance([newRecord, ...attendance]);
    setSessionModalOpen(false);
    showToast({
      type: 'success',
      title: 'Session Created',
      message: 'New attendance session opened for roll call.',
    });
  };

  const filteredAttendance = useMemo(() => {
    return attendance.filter((a) => {
      const matchCourse = selectedCourse === 'ALL' || a.enrollmentId === selectedCourse;
      return matchCourse;
    });
  }, [attendance, selectedCourse]);

  const presentCount = filteredAttendance.filter((a) => a.status === 'PRESENT').length;
  const lateCount = filteredAttendance.filter((a) => a.status === 'LATE').length;
  const absentCount = filteredAttendance.filter((a) => a.status === 'ABSENT').length;
  const attendanceRate =
    filteredAttendance.length > 0 ? Math.round((presentCount / filteredAttendance.length) * 100) : 100;

  return (
    <div className="space-y-6 animate-fadeIn">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2.5">
            <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
              Attendance Management
            </h1>
            <Badge variant="brand" size="md">
              {filteredAttendance.length} Records
            </Badge>
          </div>
          <p className="mt-1 text-xs sm:text-sm text-slate-500 dark:text-slate-400">
            Track daily classroom roll calls, medical leaves, and calculate student compliance.
          </p>
        </div>

        <div className="flex items-center gap-2.5">
          <Button variant="outline" size="sm" onClick={handleMarkAllPresent}>
            Mark All Present
          </Button>
          <Button
            size="sm"
            onClick={() => setSessionModalOpen(true)}
            leftIcon={<PlusIcon className="h-4 w-4" />}
          >
            New Session
          </Button>
        </div>
      </div>

      {/* Attendance Stats Highlights */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Compliance Rate</span>
          <p className="text-2xl font-extrabold text-brand-600 dark:text-brand-400 tracking-tight">
            {attendanceRate}%
          </p>
          <span className="text-[10px] text-emerald-600 font-medium">Above 75% requirement</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Present</span>
          <p className="text-2xl font-extrabold text-emerald-600 dark:text-emerald-400 tracking-tight">
            {presentCount} Students
          </p>
          <span className="text-[10px] text-slate-400">Verified in class</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Tardy / Late</span>
          <p className="text-2xl font-extrabold text-amber-600 dark:text-amber-400 tracking-tight">
            {lateCount} Students
          </p>
          <span className="text-[10px] text-slate-400">Arrived after bell</span>
        </Card>

        <Card className="p-4 space-y-1">
          <span className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider">Absent</span>
          <p className="text-2xl font-extrabold text-rose-600 dark:text-rose-400 tracking-tight">
            {absentCount} Students
          </p>
          <span className="text-[10px] text-slate-400">Unexcused absence</span>
        </Card>
      </div>

      {/* Filter and Date Toolbar */}
      <Card padding="sm">
        <div className="flex flex-wrap items-center justify-between gap-3">
          <div className="flex items-center gap-2 text-xs text-slate-500">
            <CalendarDaysIcon className="h-4 w-4 text-brand-600" />
            <span className="font-semibold">Session Date:</span>
            <input
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
            />
          </div>

          <div className="flex items-center gap-2 text-xs text-slate-500">
            <FunnelIcon className="h-4 w-4" />
            <span className="font-semibold">Course:</span>
            <select
              value={selectedCourse}
              onChange={(e) => setSelectedCourse(e.target.value)}
              className="px-3 py-1.5 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-800 dark:text-slate-200 focus:outline-none focus:ring-2 focus:ring-brand-500"
            >
              <option value="ALL">All Courses</option>
              {courses.map((c) => (
                <option key={c.id} value={c.id}>
                  {c.code} - {c.name}
                </option>
              ))}
            </select>
          </div>
        </div>
      </Card>

      {/* Roll Call Table */}
      <Card padding="none" className="overflow-hidden">
        {loading ? (
          <div className="p-8 text-center text-xs text-slate-500">Loading attendance rolls...</div>
        ) : filteredAttendance.length === 0 ? (
          <div className="p-12 text-center space-y-3">
            <CalendarDaysIcon className="h-8 w-8 text-slate-400 mx-auto" />
            <p className="font-bold text-slate-700 dark:text-slate-300">No attendance records for this filter.</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 dark:bg-slate-800/60 border-b border-slate-200/80 dark:border-slate-800 text-slate-500 font-semibold uppercase tracking-wider">
                <tr>
                  <th className="py-3 px-4">Date</th>
                  <th className="py-3 px-4">Student / Enrollment</th>
                  <th className="py-3 px-4">Evaluator</th>
                  <th className="py-3 px-4">Remarks</th>
                  <th className="py-3 px-4">Current Status</th>
                  <th className="py-3 px-4 text-right">Quick Mark</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredAttendance.map((rec) => (
                  <tr
                    key={rec.id}
                    className="hover:bg-slate-50/80 dark:hover:bg-slate-800/40 transition-colors"
                  >
                    <td className="py-3 px-4 font-mono font-medium text-slate-600 dark:text-slate-300">
                      {rec.date}
                    </td>
                    <td className="py-3 px-4">
                      <div className="font-bold text-slate-800 dark:text-slate-200">
                        Enrollment #{rec.enrollmentId.slice(-6)}
                      </div>
                      <div className="text-[11px] text-slate-400 font-mono">
                        CS201 Data Structures
                      </div>
                    </td>
                    <td className="py-3 px-4 text-slate-600 dark:text-slate-300">
                      {rec.markedBy || 'Faculty Instructor'}
                    </td>
                    <td className="py-3 px-4 text-slate-500 italic max-w-xs truncate">
                      {rec.remarks || 'Standard lecture session'}
                    </td>
                    <td className="py-3 px-4">
                      <Badge
                        variant={
                          rec.status === 'PRESENT'
                            ? 'success'
                            : rec.status === 'LATE'
                            ? 'warning'
                            : 'error'
                        }
                        size="sm"
                        dot
                      >
                        {rec.status}
                      </Badge>
                    </td>
                    <td className="py-3 px-4 text-right">
                      <div className="inline-flex items-center gap-1 bg-slate-100 dark:bg-slate-800 p-1 rounded-xl">
                        <button
                          type="button"
                          onClick={() => handleStatusChange(rec.id, 'PRESENT')}
                          title="Mark Present"
                          className={`p-1.5 rounded-lg text-xs font-bold transition-colors ${
                            rec.status === 'PRESENT'
                              ? 'bg-emerald-600 text-white shadow-xs'
                              : 'text-slate-500 hover:text-emerald-600'
                          }`}
                        >
                          P
                        </button>
                        <button
                          type="button"
                          onClick={() => handleStatusChange(rec.id, 'LATE')}
                          title="Mark Late"
                          className={`p-1.5 rounded-lg text-xs font-bold transition-colors ${
                            rec.status === 'LATE'
                              ? 'bg-amber-500 text-white shadow-xs'
                              : 'text-slate-500 hover:text-amber-600'
                          }`}
                        >
                          L
                        </button>
                        <button
                          type="button"
                          onClick={() => handleStatusChange(rec.id, 'ABSENT')}
                          title="Mark Absent"
                          className={`p-1.5 rounded-lg text-xs font-bold transition-colors ${
                            rec.status === 'ABSENT'
                              ? 'bg-rose-600 text-white shadow-xs'
                              : 'text-slate-500 hover:text-rose-600'
                          }`}
                        >
                          A
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </Card>

      {/* New Session Modal */}
      <Modal
        isOpen={sessionModalOpen}
        onClose={() => setSessionModalOpen(false)}
        title="Schedule Attendance Session"
        size="sm"
      >
        <form onSubmit={handleCreateSession} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Select Course
            </label>
            <select
              value={newCourseId}
              onChange={(e) => setNewCourseId(e.target.value)}
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
              Date
            </label>
            <input
              type="date"
              value={newDate}
              onChange={(e) => setNewDate(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div>
            <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1">
              Lecture Topic / Notes
            </label>
            <input
              type="text"
              placeholder="e.g. Binary Search Trees & Heaps"
              value={newTopic}
              onChange={(e) => setNewTopic(e.target.value)}
              className="w-full px-3 py-2 text-xs bg-slate-50 dark:bg-slate-800 border border-slate-200 dark:border-slate-700 rounded-xl"
            />
          </div>

          <div className="flex items-center justify-end gap-2.5 pt-4 border-t border-slate-100 dark:border-slate-800">
            <Button variant="outline" type="button" onClick={() => setSessionModalOpen(false)}>
              Cancel
            </Button>
            <Button type="submit">Open Roll Call</Button>
          </div>
        </form>
      </Modal>
    </div>
  );
}

export default AttendancePage;
