import { useEffect, useState } from 'react';
import { Card, StatCard, Button, Select, Badge } from '@/components';
import { api } from '@/services';
import { useAuth } from '@/contexts';
import { UserRole } from '@/types';
import { DocumentTextIcon, ChartBarIcon, AcademicCapIcon, UsersIcon, ArrowDownTrayIcon, PrinterIcon } from '@heroicons/react/24/outline';

const reportTypes = [
  { value: 'student-transcript', label: 'Student Transcript', description: 'Generate official student transcripts' },
  { value: 'grade-report', label: 'Grade Report', description: 'Course-wise grade distribution' },
  { value: 'attendance-report', label: 'Attendance Report', description: 'Student attendance summary' },
  { value: 'enrollment-report', label: 'Enrollment Report', description: 'Current enrollment statistics' },
  { value: 'faculty-workload', label: 'Faculty Workload', description: 'Faculty teaching load analysis' },
  { value: 'department-summary', label: 'Department Summary', description: 'Department-wise overview' },
];

export function ReportsPage() {
  const { user, hasRole } = useAuth();
  const [selectedReport, setSelectedReport] = useState(reportTypes[0].value);
  const [dateRange, setDateRange] = useState({ start: '', end: '' });
  const [generating, setGenerating] = useState(false);

  const availableReports = hasRole(['ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR']) 
    ? reportTypes 
    : reportTypes.filter(r => ['student-transcript', 'grade-report', 'attendance-report'].includes(r.value));

  const generateReport = async () => {
    setGenerating(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1500));
      alert(`Report "${selectedReport}" generated successfully!`);
    } catch (error) {
      console.error('Failed to generate report:', error);
    } finally {
      setGenerating(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Reports</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Generate and view academic reports</p>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {availableReports.map((report) => (
          <Card 
            key={report.value} 
            variant="interactive" 
            className={selectedReport === report.value ? 'ring-2 ring-brand-500' : ''}
            onClick={() => setSelectedReport(report.value)}
          >
            <div className="flex items-start justify-between">
              <div className="flex-1">
                <h3 className="font-semibold text-neutral-900 dark:text-neutral-50">{report.label}</h3>
                <p className="mt-1 text-sm text-neutral-500 dark:text-neutral-400">{report.description}</p>
              </div>
              {selectedReport === report.value && (
                <span className="text-brand-500">✓</span>
              )}
            </div>
          </Card>
        ))}
      </div>

      <Card className="p-6">
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div className="flex flex-col md:flex-row items-start md:items-center gap-4">
            <Select
              value={selectedReport}
              onChange={setSelectedReport}
              options={availableReports}
              placeholder="Select report type"
              className="w-full md:w-64"
            />
            <div className="flex gap-3">
              <input
                type="date"
                value={dateRange.start}
                onChange={(e) => setDateRange({ ...dateRange, start: e.target.value })}
                className="px-3 py-2 text-sm border border-neutral-300 dark:border-neutral-600 rounded-lg bg-white dark:bg-neutral-900"
                placeholder="Start date"
              />
              <input
                type="date"
                value={dateRange.end}
                onChange={(e) => setDateRange({ ...dateRange, end: e.target.value })}
                className="px-3 py-2 text-sm border border-neutral-300 dark:border-neutral-600 rounded-lg bg-white dark:bg-neutral-900"
                placeholder="End date"
              />
            </div>
          </div>
          <div className="flex gap-3">
            <Button variant="outline" onClick={generateReport} loading={generating}>
              <DownloadIcon className="h-5 w-5" />
              Download
            </Button>
            <Button variant="outline">
              <PrinterIcon className="h-5 w-5" />
              Print
            </Button>
            <Button onClick={generateReport} loading={generating}>
              <DocumentTextIcon className="h-5 w-5" />
              Generate Report
            </Button>
          </div>
        </div>
      </Card>

      <Card>
        <div className="p-6 border-b border-neutral-200 dark:border-neutral-700">
          <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50">Recent Reports</h3>
        </div>
        <div className="divide-y divide-neutral-200 dark:divide-neutral-700">
          {[
            { name: 'Fall 2024 Grade Report', type: 'Grade Report', date: '2024-01-15', status: 'Completed' },
            { name: 'Student Transcripts - Batch 2024', type: 'Student Transcript', date: '2024-01-10', status: 'Completed' },
            { name: 'Monthly Attendance Summary', type: 'Attendance Report', date: '2024-01-05', status: 'Completed' },
            { name: 'Faculty Workload Analysis', type: 'Faculty Workload', date: '2024-01-01', status: 'Pending' },
          ].map((report, index) => (
            <div key={index} className="p-6 flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div className="p-3 rounded-lg bg-brand-100 dark:bg-brand-900/30">
                  <DocumentTextIcon className="h-6 w-6 text-brand-700 dark:text-brand-300" />
                </div>
                <div>
                  <p className="font-medium text-neutral-900 dark:text-neutral-50">{report.name}</p>
                  <p className="text-sm text-neutral-500 dark:text-neutral-400">{report.type} • {new Date(report.date).toLocaleDateString()}</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <Badge variant={report.status === 'Completed' ? 'success' : 'warning'} size="sm">{report.status}</Badge>
                <Button variant="ghost" size="sm"><DownloadIcon className="h-5 w-5" /></Button>
              </div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}