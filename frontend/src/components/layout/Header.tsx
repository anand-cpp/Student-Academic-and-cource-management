import { ReactNode } from 'react';
import { cn } from '@/utils';
import { Button, Dropdown, Avatar, AvatarGroup } from '@/components/ui';
import { BellIcon, MagnifyingGlassIcon, Cog6ToothIcon, UserCircleIcon, ArrowRightOnRectangleIcon } from '@heroicons/react/24/outline';
import { useAuth, useTheme } from '@/contexts';
import { DropdownItem } from '@/types';

export interface HeaderProps {
  title?: string;
  breadcrumbs?: Array<{ label: string; href?: string }>;
  actions?: ReactNode;
  className?: string;
  onSearch?: (query: string) => void;
  searchPlaceholder?: string;
}

export function Header({
  title,
  breadcrumbs,
  actions,
  className,
  onSearch,
  searchPlaceholder = 'Search...',
}: HeaderProps) {
  const { user, logout, hasRole } = useAuth();
  const { theme, resolvedTheme, setTheme } = useTheme();

  const userMenuItems: DropdownItem[] = [
    { label: 'Profile', value: 'profile', icon: <UserCircleIcon className="h-5 w-5" />, onClick: () => {} },
    { label: 'Settings', value: 'settings', icon: <Cog6ToothIcon className="h-5 w-5" />, onClick: () => {} },
    { label: 'Theme', value: 'theme', icon: resolvedTheme === 'dark' ? '🌙' : '☀️', onClick: () => setTheme(resolvedTheme === 'dark' ? 'light' : 'dark') },
    { divider: true },
    { label: 'Sign out', value: 'logout', icon: <ArrowRightOnRectangleIcon className="h-5 w-5" />, danger: true, onClick: logout },
  ];

  const notificationItems: DropdownItem[] = [
    { label: 'New enrollment request', value: '1', onClick: () => {} },
    { label: 'Grade submitted for review', value: '2', onClick: () => {} },
    { label: 'Attendance alert', value: '3', onClick: () => {} },
    { divider: true },
    { label: 'View all notifications', value: 'all', onClick: () => {} },
  ];

  const searchItems: DropdownItem[] = [
    { label: 'Students', value: 'students', icon: '👥', onClick: () => {} },
    { label: 'Faculty', value: 'faculty', icon: '👨‍🏫', onClick: () => {} },
    { label: 'Courses', value: 'courses', icon: '📚', onClick: () => {} },
    { label: 'Reports', value: 'reports', icon: '📊', onClick: () => {} },
  ];

  return (
    <header className={cn('sticky top-0 z-30 bg-white/80 dark:bg-neutral-950/80 backdrop-blur-md border-b border-neutral-200 dark:border-neutral-700', className)}>
      <div className="flex h-16 items-center justify-between px-4 lg:px-6 gap-4">
        <div className="flex items-center gap-4 flex-1 min-w-0">
          {title && (
            <h1 className="text-lg font-semibold text-neutral-900 dark:text-neutral-50 truncate">
              {title}
            </h1>
          )}
          {breadcrumbs && breadcrumbs.length > 0 && (
            <nav className="hidden md:flex items-center gap-2 text-sm" aria-label="Breadcrumb">
              {breadcrumbs.map((crumb, index) => (
                <span key={crumb.label} className="flex items-center gap-2">
                  {index > 0 && <span className="text-neutral-400">/</span>}
                  {crumb.href ? (
                    <a href={crumb.href} className="text-neutral-500 hover:text-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-200">
                      {crumb.label}
                    </a>
                  ) : (
                    <span className="text-neutral-900 dark:text-neutral-50 font-medium">{crumb.label}</span>
                  )}
                </span>
              ))}
            </nav>
          )}
        </div>

        <div className="flex items-center gap-3 flex-shrink-0">
          {onSearch && (
            <div className="relative hidden sm:block">
              <MagnifyingGlassIcon className="absolute left-3 top-1/2 -translate-y-1/2 h-5 w-5 text-neutral-400" aria-hidden="true" />
              <input
                type="search"
                placeholder={searchPlaceholder}
                onChange={(e) => onSearch?.(e.target.value)}
                className="w-64 pl-10 pr-4 py-2 text-sm bg-neutral-100 dark:bg-neutral-800 border-none rounded-lg text-neutral-900 dark:text-neutral-50 placeholder:text-neutral-400 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 transition-colors"
                aria-label="Search"
              />
            </div>
          )}

          <Dropdown
            trigger={
              <Button variant="ghost" size="sm" aria-label="Notifications">
                <BellIcon className="h-5 w-5 text-neutral-600 dark:text-neutral-400" />
                <span className="absolute -top-1 -right-1 h-4 w-4 bg-error-500 text-white text-xs font-medium rounded-full flex items-center justify-center">3</span>
              </Button>
            }
            items={notificationItems}
            align="right"
          />

          {hasRole(['ADMIN', 'DEPARTMENT_HEAD', 'REGISTRAR']) && (
            <Dropdown
              trigger={
                <Button variant="ghost" size="sm" aria-label="Quick actions">
                  <MagnifyingGlassIcon className="h-5 w-5 text-neutral-600 dark:text-neutral-400" />
                </Button>
              }
              items={searchItems}
              align="right"
              width={200}
            />
          )}

          <div className="hidden lg:flex items-center gap-2">
            {actions}
          </div>

          <Dropdown
            trigger={
              <div className="flex items-center gap-2 p-1 rounded-lg hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors">
                <AvatarGroup max={3} size="sm">
                  <Avatar name={user?.firstName || 'User'} />
                </AvatarGroup>
                {!user ? null : (
                  <span className="hidden md:block text-sm font-medium text-neutral-700 dark:text-neutral-300 truncate max-w-[150px]">
                    {user.firstName} {user.lastName}
                  </span>
                )}
              </div>
            }
            items={userMenuItems}
            align="right"
            width={200}
          />
        </div>
      </div>
    </header>
  );
}