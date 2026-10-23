import { Card, Badge, Button } from '@/components';
import { useAuth } from '@/contexts';
import { 
  BuildingOfficeIcon, UserGroupIcon, ClipboardDocumentListIcon, 
  Cog6ToothIcon, BellIcon, ShieldCheckIcon, 
  ArrowPathIcon, KeyIcon, MoonIcon, SunIcon
} from '@heroicons/react/24/outline';

export function OtherPages() {
  const { user } = useAuth();

  const pages = [
    { id: 'departments', title: 'Departments', description: 'Manage academic departments', icon: BuildingOfficeIcon, roles: ['ADMIN', 'DEPARTMENT_HEAD'] },
    { id: 'programs', title: 'Programs', description: 'Manage academic programs', icon: ClipboardDocumentListIcon, roles: ['ADMIN', 'DEPARTMENT_HEAD'] },
    { id: 'batches', title: 'Batches', description: 'Manage student batches', icon: UserGroupIcon, roles: ['ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
    { id: 'sections', title: 'Sections', description: 'Manage class sections', icon: UserGroupIcon, roles: ['ADMIN', 'DEPARTMENT_HEAD'] },
    { id: 'designations', title: 'Designations', description: 'Manage faculty designations', icon: ShieldCheckIcon, roles: ['ADMIN'] },
    { id: 'settings', title: 'Settings', description: 'System configuration', icon: Cog6ToothIcon, roles: ['ADMIN'] },
    { id: 'notifications', title: 'Notifications', description: 'Manage system notifications', icon: BellIcon, roles: ['ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR'] },
    { id: 'audit-logs', title: 'Audit Logs', description: 'View system audit trail', icon: ShieldCheckIcon, roles: ['ADMIN'] },
    { id: 'backup', title: 'Backup & Restore', description: 'Database backup management', icon: ArrowPathIcon, roles: ['ADMIN'] },
    { id: 'api-keys', title: 'API Keys', description: 'Manage API access keys', icon: KeyIcon, roles: ['ADMIN'] },
  ];

  const filteredPages = pages.filter(page => 
    !user || !page.roles || page.roles.includes(user.role)
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-neutral-900 dark:text-neutral-50">Other Pages</h1>
          <p className="text-neutral-500 dark:text-neutral-400">Additional system management pages</p>
        </div>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {filteredPages.map((page) => (
          <Card key={page.id} variant="interactive" className="h-full flex flex-col">
            <div className="flex items-start justify-between mb-4">
              <div className="p-3 rounded-xl bg-brand-100 dark:bg-brand-900/30">
                <page.icon className="h-6 w-6 text-brand-700 dark:text-brand-300" />
              </div>
              <Badge variant="outline" size="sm">Coming Soon</Badge>
            </div>
            <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50 mb-2">{page.title}</h3>
            <p className="text-sm text-neutral-500 dark:text-neutral-400 flex-1 mb-4">{page.description}</p>
            <Button variant="outline" className="mt-auto" disabled>View Page</Button>
          </Card>
        ))}
      </div>

      <Card className="p-6">
        <h3 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50 mb-4">System Quick Actions</h3>
        <div className="flex flex-wrap gap-3">
          <Button variant="outline"><MoonIcon className="h-5 w-5" /> Dark Mode</Button>
          <Button variant="outline"><SunIcon className="h-5 w-5" /> Light Mode</Button>
          <Button variant="outline"><Cog6ToothIcon className="h-5 w-5" /> System Settings</Button>
          <Button variant="outline"><ArrowPathIcon className="h-5 w-5" /> Clear Cache</Button>
          <Button variant="outline"><KeyIcon className="h-5 w-5" /> Rotate Keys</Button>
        </div>
      </Card>
    </div>
  );
}