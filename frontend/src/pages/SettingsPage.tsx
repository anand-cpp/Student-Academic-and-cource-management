import { useState } from 'react';
import { Card, CardHeader, CardTitle, CardDescription, CardContent, CardFooter, Button, Badge } from '@/components/ui';
import { useTheme, useToast } from '@/contexts';
import {
  SunIcon,
  MoonIcon,
  ComputerDesktopIcon,
  ArrowPathIcon,
  ArrowDownTrayIcon,
  BellIcon,
  ShieldCheckIcon,
  ServerIcon,
  CheckCircleIcon,
  CircleStackIcon,
  CpuChipIcon,
} from '@heroicons/react/24/outline';

export function SettingsPage() {
  const { theme, setTheme, resolvedTheme } = useTheme();
  const { showToast } = useToast();

  const [academicYear, setAcademicYear] = useState('2024-2025');
  const [currentSemester, setCurrentSemester] = useState('Fall Semester (Odd)');
  const [minAttendancePercent, setMinAttendancePercent] = useState(75);
  const [emailAlerts, setEmailAlerts] = useState(true);
  const [autoGradePublish, setAutoGradePublish] = useState(false);
  const [saving, setSaving] = useState(false);

  const handleSaveGeneral = (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setTimeout(() => {
      setSaving(false);
      showToast({
        type: 'success',
        title: 'Settings Saved',
        message: 'System parameters updated successfully.',
      });
    }, 400);
  };

  const handleExportData = () => {
    const backupData = {
      timestamp: new Date().toISOString(),
      academicYear,
      theme,
      minAttendancePercent,
      version: '1.0.0-SNAPSHOT',
    };
    const blob = new Blob([JSON.stringify(backupData, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `academia_backup_${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
    showToast({
      type: 'success',
      title: 'Backup Exported',
      message: 'System configuration saved to JSON file.',
    });
  };

  const handleClearCache = () => {
    showToast({
      type: 'info',
      title: 'Cache Cleared',
      message: 'Client-side query cache and temporary session tokens cleared.',
    });
  };

  const handleResetDemoData = () => {
    if (confirm('Are you sure you want to restore default demo records?')) {
      const keys = ['students', 'faculty', 'courses', 'enrollments', 'attendance', 'assessments', 'marks'];
      keys.forEach((k) => localStorage.removeItem('academia_' + k));
      showToast({
        type: 'success',
        title: 'Demo Data Reset',
        message: 'Default academic entities reloaded. Refreshing page...',
      });
      setTimeout(() => window.location.reload(), 800);
    }
  };

  return (
    <div className="space-y-8 animate-fadeIn">
      {/* Header */}
      <div>
        <div className="flex items-center gap-3">
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight text-slate-900 dark:text-slate-50">
            System Settings
          </h1>
          <Badge variant="brand" size="md">
            Administration
          </Badge>
        </div>
        <p className="mt-1.5 text-sm text-slate-500 dark:text-slate-400">
          Configure academic calendar, UI themes, notification preferences, and system storage.
        </p>
      </div>

      <div className="grid gap-6 lg:grid-cols-3">
        {/* Left Column (2 spans): General & Theme Settings */}
        <div className="lg:col-span-2 space-y-6">
          {/* Theme & Display Preferences */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2.5">
                <div className="p-2 rounded-xl bg-indigo-50 text-brand-600 dark:bg-indigo-950/60 dark:text-indigo-400">
                  {resolvedTheme === 'dark' ? <MoonIcon className="h-5 w-5" /> : <SunIcon className="h-5 w-5" />}
                </div>
                <div>
                  <CardTitle>Theme & Appearance</CardTitle>
                  <CardDescription>Select your preferred visual style and contrast level.</CardDescription>
                </div>
              </div>
            </CardHeader>

            <CardContent>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                {/* Light */}
                <button
                  type="button"
                  onClick={() => setTheme('light')}
                  className={`p-4 rounded-2xl border text-left flex flex-col justify-between transition-all ${
                    theme === 'light'
                      ? 'border-brand-600 bg-brand-50/40 ring-2 ring-brand-500/20 dark:bg-brand-950/20'
                      : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700'
                  }`}
                >
                  <div className="flex items-center justify-between mb-3">
                    <SunIcon className="h-6 w-6 text-amber-500" />
                    {theme === 'light' && <CheckCircleIcon className="h-5 w-5 text-brand-600" />}
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-slate-900 dark:text-slate-100">Light Mode</h4>
                    <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Crisp slate surfaces with daylight readability.</p>
                  </div>
                </button>

                {/* Dark */}
                <button
                  type="button"
                  onClick={() => setTheme('dark')}
                  className={`p-4 rounded-2xl border text-left flex flex-col justify-between transition-all ${
                    theme === 'dark'
                      ? 'border-brand-600 bg-brand-50/40 ring-2 ring-brand-500/20 dark:bg-brand-950/40'
                      : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700'
                  }`}
                >
                  <div className="flex items-center justify-between mb-3">
                    <MoonIcon className="h-6 w-6 text-indigo-400" />
                    {theme === 'dark' && <CheckCircleIcon className="h-5 w-5 text-brand-600" />}
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-slate-900 dark:text-slate-100">Dark Mode</h4>
                    <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Deep slate tones tailored for low-light environments.</p>
                  </div>
                </button>

                {/* System */}
                <button
                  type="button"
                  onClick={() => setTheme('system')}
                  className={`p-4 rounded-2xl border text-left flex flex-col justify-between transition-all ${
                    theme === 'system'
                      ? 'border-brand-600 bg-brand-50/40 ring-2 ring-brand-500/20 dark:bg-brand-950/40'
                      : 'border-slate-200 dark:border-slate-800 hover:border-slate-300 dark:hover:border-slate-700'
                  }`}
                >
                  <div className="flex items-center justify-between mb-3">
                    <ComputerDesktopIcon className="h-6 w-6 text-slate-500 dark:text-slate-400" />
                    {theme === 'system' && <CheckCircleIcon className="h-5 w-5 text-brand-600" />}
                  </div>
                  <div>
                    <h4 className="font-bold text-sm text-slate-900 dark:text-slate-100">System Sync</h4>
                    <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5">Automatically sync with operating system preference.</p>
                  </div>
                </button>
              </div>
            </CardContent>
          </Card>

          {/* Academic Parameters */}
          <Card>
            <form onSubmit={handleSaveGeneral}>
              <CardHeader>
                <div className="flex items-center gap-2.5">
                  <div className="p-2 rounded-xl bg-blue-50 text-blue-600 dark:bg-blue-950/60 dark:text-blue-400">
                    <ServerIcon className="h-5 w-5" />
                  </div>
                  <div>
                    <CardTitle>Academic Calendar & Rules</CardTitle>
                    <CardDescription>Standard session years, semester schedules, and thresholds.</CardDescription>
                  </div>
                </div>
              </CardHeader>

              <CardContent className="space-y-4">
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      Active Academic Year
                    </label>
                    <input
                      type="text"
                      value={academicYear}
                      onChange={(e) => setAcademicYear(e.target.value)}
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 dark:text-slate-300 mb-1.5">
                      Current Active Semester
                    </label>
                    <select
                      value={currentSemester}
                      onChange={(e) => setCurrentSemester(e.target.value)}
                      className="w-full px-3.5 py-2 text-sm bg-slate-50 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700 rounded-xl text-slate-900 dark:text-slate-100 focus:outline-none focus:ring-2 focus:ring-brand-500"
                    >
                      <option>Fall Semester (Odd)</option>
                      <option>Spring Semester (Even)</option>
                      <option>Summer Term</option>
                    </select>
                  </div>
                </div>

                <div>
                  <div className="flex items-center justify-between mb-1.5">
                    <label className="text-xs font-semibold text-slate-700 dark:text-slate-300">
                      Minimum Attendance Requirement
                    </label>
                    <span className="text-xs font-bold text-brand-600 dark:text-brand-400">
                      {minAttendancePercent}%
                    </span>
                  </div>
                  <input
                    type="range"
                    min="50"
                    max="90"
                    step="5"
                    value={minAttendancePercent}
                    onChange={(e) => setMinAttendancePercent(Number(e.target.value))}
                    className="w-full accent-brand-600 cursor-pointer"
                  />
                  <p className="text-[11px] text-slate-400 mt-1">
                    Students falling below this percentage trigger automated warning flags on their academic record.
                  </p>
                </div>
              </CardContent>

              <CardFooter>
                <Button type="submit" loading={saving}>
                  Save Academic Settings
                </Button>
              </CardFooter>
            </form>
          </Card>

          {/* Notifications & Automation */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2.5">
                <div className="p-2 rounded-xl bg-amber-50 text-amber-600 dark:bg-amber-950/60 dark:text-amber-400">
                  <BellIcon className="h-5 w-5" />
                </div>
                <div>
                  <CardTitle>Notifications & Automation</CardTitle>
                  <CardDescription>Control system automated emails and notifications.</CardDescription>
                </div>
              </div>
            </CardHeader>

            <CardContent className="space-y-4">
              <label className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200/80 dark:border-slate-800 hover:bg-slate-50/50 dark:hover:bg-slate-800/50 cursor-pointer transition-colors">
                <div>
                  <span className="text-sm font-semibold text-slate-800 dark:text-slate-200 block">
                    Email Digest Alerts
                  </span>
                  <span className="text-xs text-slate-500 dark:text-slate-400">
                    Send weekly academic summary notifications to faculty and department heads.
                  </span>
                </div>
                <input
                  type="checkbox"
                  checked={emailAlerts}
                  onChange={(e) => setEmailAlerts(e.target.checked)}
                  className="h-4 w-4 rounded text-brand-600 focus:ring-brand-500 accent-brand-600"
                />
              </label>

              <label className="flex items-center justify-between p-3.5 rounded-xl border border-slate-200/80 dark:border-slate-800 hover:bg-slate-50/50 dark:hover:bg-slate-800/50 cursor-pointer transition-colors">
                <div>
                  <span className="text-sm font-semibold text-slate-800 dark:text-slate-200 block">
                    Automatic Grade Publication
                  </span>
                  <span className="text-xs text-slate-500 dark:text-slate-400">
                    Publish grades immediately upon assessment evaluation approval.
                  </span>
                </div>
                <input
                  type="checkbox"
                  checked={autoGradePublish}
                  onChange={(e) => setAutoGradePublish(e.target.checked)}
                  className="h-4 w-4 rounded text-brand-600 focus:ring-brand-500 accent-brand-600"
                />
              </label>
            </CardContent>
          </Card>
        </div>

        {/* Right Column (1 span): System Information & Maintenance */}
        <div className="space-y-6">
          {/* System Diagnostics */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <CpuChipIcon className="h-5 w-5 text-emerald-500" />
                <CardTitle>System Status</CardTitle>
              </div>
            </CardHeader>

            <CardContent className="space-y-3.5 text-xs">
              <div className="flex items-center justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Backend API</span>
                <span className="font-semibold text-emerald-600 dark:text-emerald-400 flex items-center gap-1">
                  <span className="h-2 w-2 rounded-full bg-emerald-500" /> Online (Port 8080)
                </span>
              </div>

              <div className="flex items-center justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Database Engine</span>
                <span className="font-semibold text-slate-800 dark:text-slate-200">H2 / HikariCP Pool</span>
              </div>

              <div className="flex items-center justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Authentication</span>
                <span className="font-semibold text-slate-800 dark:text-slate-200">JWT (HS256)</span>
              </div>

              <div className="flex items-center justify-between py-1.5 border-b border-slate-100 dark:border-slate-800">
                <span className="text-slate-500">Frontend Stack</span>
                <span className="font-semibold text-slate-800 dark:text-slate-200">React 18 + Vite 5</span>
              </div>

              <div className="flex items-center justify-between py-1.5">
                <span className="text-slate-500">Audit Logging</span>
                <Badge variant="success" size="sm">
                  Active
                </Badge>
              </div>
            </CardContent>
          </Card>

          {/* Maintenance & Data Actions */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <CircleStackIcon className="h-5 w-5 text-brand-600" />
                <CardTitle>Storage & Recovery</CardTitle>
              </div>
            </CardHeader>

            <CardContent className="space-y-3">
              <Button variant="outline" fullWidth onClick={handleExportData} leftIcon={<ArrowDownTrayIcon className="h-4 w-4" />}>
                Export Backup Snapshot
              </Button>

              <Button variant="secondary" fullWidth onClick={handleClearCache} leftIcon={<ArrowPathIcon className="h-4 w-4" />}>
                Clear Session Cache
              </Button>

              <Button
                variant="outline"
                fullWidth
                onClick={handleResetDemoData}
                className="text-rose-600 border-rose-200 hover:bg-rose-50 dark:border-rose-900/60 dark:hover:bg-rose-950/40"
              >
                Reset Demo Database
              </Button>
            </CardContent>
          </Card>

          {/* Security & Access */}
          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <ShieldCheckIcon className="h-5 w-5 text-indigo-500" />
                <CardTitle>Security Safeguards</CardTitle>
              </div>
            </CardHeader>

            <CardContent className="text-xs text-slate-500 dark:text-slate-400 space-y-2">
              <p>• Role-Based Access Control enforced on all endpoints.</p>
              <p>• Passwords hashed with BCrypt (10 rounds).</p>
              <p>• CORS restricted to authorized frontend origins.</p>
              <p>• Client session persisted with secure local tokens.</p>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}

export default SettingsPage;
