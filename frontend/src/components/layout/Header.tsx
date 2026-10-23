import { ReactNode, useState, useRef, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { cn } from '@/utils';
import { Button, Avatar, Badge } from '@/components/ui';
import {
  BellIcon,
  MagnifyingGlassIcon,
  Cog6ToothIcon,
  UserCircleIcon,
  ArrowRightOnRectangleIcon,
  SunIcon,
  MoonIcon,
  CheckCircleIcon,
  ExclamationTriangleIcon,
  InformationCircleIcon,
  XMarkIcon,
  ChevronRightIcon
} from '@heroicons/react/24/outline';
import { useAuth, useTheme, useToast } from '@/contexts';

export interface HeaderProps {
  title?: string;
  breadcrumbs?: Array<{ label: string; href?: string; isCurrent?: boolean }>;
  actions?: ReactNode;
  className?: string;
  onSearch?: (query: string) => void;
  searchPlaceholder?: string;
}

interface Notification {
  id: string;
  title: string;
  description: string;
  time: string;
  type: 'info' | 'warning' | 'success';
  read: boolean;
}

const INITIAL_NOTIFICATIONS: Notification[] = [
  {
    id: '1',
    title: 'Grade submission pending',
    description: 'Fall semester midterm evaluations are due in 48 hours.',
    time: '10m ago',
    type: 'warning',
    read: false,
  },
  {
    id: '2',
    title: 'New course enrollment',
    description: '3 students enrolled in CS201 Data Structures.',
    time: '1h ago',
    type: 'info',
    read: false,
  },
  {
    id: '3',
    title: 'System backup complete',
    description: 'Database snapshot was successfully created and verified.',
    time: '3h ago',
    type: 'success',
    read: false,
  },
];

export function Header({
  title,
  breadcrumbs,
  actions,
  className,
  onSearch,
  searchPlaceholder = 'Search courses, students, faculty... (Ctrl+K)',
}: HeaderProps) {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const { resolvedTheme, toggleTheme } = useTheme();
  const { showToast } = useToast();

  const [searchQuery, setSearchQuery] = useState('');
  const [showSearchModal, setShowSearchModal] = useState(false);
  const [notifications, setNotifications] = useState<Notification[]>(INITIAL_NOTIFICATIONS);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  const notifRef = useRef<HTMLDivElement>(null);
  const userRef = useRef<HTMLDivElement>(null);

  const unreadCount = notifications.filter((n) => !n.read).length;

  // Close dropdowns on outside click
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (notifRef.current && !notifRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
      if (userRef.current && !userRef.current.contains(event.target as Node)) {
        setShowUserMenu(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Keyboard shortcut for search
  useEffect(() => {
    function handleKeyDown(e: KeyboardEvent) {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        setShowSearchModal((prev) => !prev);
      }
    }
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  const handleSearchChange = (query: string) => {
    setSearchQuery(query);
    onSearch?.(query);
  };

  const markAllNotificationsRead = () => {
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    showToast({ type: 'info', title: 'Notifications', message: 'All marked as read' });
  };

  const dismissNotification = (id: string) => {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  };

  const quickLinks = [
    { label: 'Students Directory', path: '/students' },
    { label: 'Course Catalog', path: '/courses' },
    { label: 'Faculty Roster', path: '/faculty' },
    { label: 'Attendance Records', path: '/attendance' },
    { label: 'Academic Reports', path: '/reports' },
  ];

  return (
    <>
      <header
        className={cn(
          'sticky top-0 z-30 bg-white/80 dark:bg-slate-900/80 backdrop-blur-md border-b border-slate-200/80 dark:border-slate-800 transition-colors',
          className
        )}
      >
        <div className="flex h-16 items-center justify-between px-4 lg:px-8 gap-4">
          {/* Left: Breadcrumbs / Title */}
          <div className="flex items-center gap-4 flex-1 min-w-0 pl-12 lg:pl-0">
            {breadcrumbs && breadcrumbs.length > 0 ? (
              <nav className="flex items-center gap-1.5 text-xs sm:text-sm" aria-label="Breadcrumb">
                {breadcrumbs.map((crumb, index) => (
                  <span key={crumb.label + index} className="flex items-center gap-1.5 truncate">
                    {index > 0 && <ChevronRightIcon className="h-3.5 w-3.5 text-slate-400 shrink-0" />}
                    {crumb.href && !crumb.isCurrent ? (
                      <Link
                        to={crumb.href}
                        className="text-slate-500 hover:text-brand-600 dark:text-slate-400 dark:hover:text-brand-400 transition-colors truncate"
                      >
                        {crumb.label}
                      </Link>
                    ) : (
                      <span className="font-semibold text-slate-800 dark:text-slate-100 truncate">
                        {crumb.label}
                      </span>
                    )}
                  </span>
                ))}
              </nav>
            ) : title ? (
              <h1 className="text-lg font-bold text-slate-900 dark:text-slate-50 truncate">
                {title}
              </h1>
            ) : null}
          </div>

          {/* Center/Right: Quick Search */}
          <div className="hidden md:flex items-center flex-1 max-w-md">
            <div className="relative w-full">
              <MagnifyingGlassIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
              <input
                type="text"
                placeholder={searchPlaceholder}
                value={searchQuery}
                onChange={(e) => handleSearchChange(e.target.value)}
                onFocus={() => setShowSearchModal(true)}
                className="w-full pl-10 pr-12 py-2 text-xs sm:text-sm bg-slate-100/80 dark:bg-slate-800/80 border border-slate-200 dark:border-slate-700/80 rounded-xl text-slate-900 dark:text-slate-100 placeholder:text-slate-400 focus:outline-none focus:ring-2 focus:ring-brand-500/50 focus:border-brand-500 transition-all"
              />
              <kbd className="absolute right-3 top-1/2 -translate-y-1/2 px-1.5 py-0.5 text-[10px] font-mono text-slate-400 bg-white dark:bg-slate-700 rounded border border-slate-200 dark:border-slate-600">
                ⌘K
              </kbd>
            </div>
          </div>

          {/* Right Actions */}
          <div className="flex items-center gap-2 sm:gap-3 shrink-0">
            {actions}

            {/* Theme Toggle Button */}
            <Button
              variant="ghost"
              size="sm"
              onClick={toggleTheme}
              aria-label="Toggle theme"
              className="text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200 h-9 w-9 p-0 rounded-xl"
              title={`Switch to ${resolvedTheme === 'dark' ? 'Light' : 'Dark'} mode`}
            >
              {resolvedTheme === 'dark' ? (
                <SunIcon className="h-5 w-5 text-amber-400" />
              ) : (
                <MoonIcon className="h-5 w-5 text-indigo-600" />
              )}
            </Button>

            {/* Notifications Dropdown */}
            <div className="relative" ref={notifRef}>
              <Button
                variant="ghost"
                size="sm"
                onClick={() => setShowNotifications(!showNotifications)}
                className="relative text-slate-500 hover:text-slate-700 dark:text-slate-400 dark:hover:text-slate-200 h-9 w-9 p-0 rounded-xl"
                aria-label="View notifications"
              >
                <BellIcon className="h-5 w-5" />
                {unreadCount > 0 && (
                  <span className="absolute 1 top-1 right-1 h-4 w-4 bg-rose-500 text-white text-[10px] font-bold rounded-full flex items-center justify-center animate-pulse-subtle">
                    {unreadCount}
                  </span>
                )}
              </Button>

              {showNotifications && (
                <div className="absolute right-0 mt-2 w-80 sm:w-96 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xl z-50 overflow-hidden">
                  <div className="px-4 py-3 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      <h4 className="font-bold text-sm text-slate-900 dark:text-slate-50">Notifications</h4>
                      {unreadCount > 0 && (
                        <Badge variant="brand" size="sm">
                          {unreadCount} new
                        </Badge>
                      )}
                    </div>
                    {unreadCount > 0 && (
                      <button
                        type="button"
                        onClick={markAllNotificationsRead}
                        className="text-xs text-brand-600 dark:text-brand-400 hover:underline font-medium"
                      >
                        Mark all read
                      </button>
                    )}
                  </div>

                  <div className="max-h-80 overflow-y-auto divide-y divide-slate-100 dark:divide-slate-800">
                    {notifications.length === 0 ? (
                      <div className="p-6 text-center text-xs text-slate-400">
                        No notifications right now.
                      </div>
                    ) : (
                      notifications.map((item) => (
                        <div
                          key={item.id}
                          className={cn(
                            'p-3.5 flex items-start gap-3 transition-colors',
                            item.read
                              ? 'bg-transparent opacity-75'
                              : 'bg-brand-50/40 dark:bg-brand-950/20'
                          )}
                        >
                          <div className="mt-0.5 shrink-0">
                            {item.type === 'warning' && (
                              <ExclamationTriangleIcon className="h-5 w-5 text-amber-500" />
                            )}
                            {item.type === 'success' && (
                              <CheckCircleIcon className="h-5 w-5 text-emerald-500" />
                            )}
                            {item.type === 'info' && (
                              <InformationCircleIcon className="h-5 w-5 text-indigo-500" />
                            )}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-xs font-semibold text-slate-800 dark:text-slate-200">
                              {item.title}
                            </p>
                            <p className="text-xs text-slate-500 dark:text-slate-400 mt-0.5 line-clamp-2">
                              {item.description}
                            </p>
                            <span className="text-[10px] text-slate-400 mt-1 block">
                              {item.time}
                            </span>
                          </div>
                          <button
                            type="button"
                            onClick={() => dismissNotification(item.id)}
                            className="text-slate-400 hover:text-slate-600 dark:hover:text-slate-200 p-1 rounded-md"
                            title="Dismiss"
                          >
                            <XMarkIcon className="h-4 w-4" />
                          </button>
                        </div>
                      ))
                    )}
                  </div>

                  <div className="p-2 border-t border-slate-100 dark:border-slate-800 text-center bg-slate-50 dark:bg-slate-800/40">
                    <Link
                      to="/reports"
                      onClick={() => setShowNotifications(false)}
                      className="text-xs font-medium text-slate-600 dark:text-slate-300 hover:text-brand-600 dark:hover:text-brand-400"
                    >
                      View all system alerts & logs
                    </Link>
                  </div>
                </div>
              )}
            </div>

            {/* User Profile Dropdown */}
            <div className="relative" ref={userRef}>
              <button
                type="button"
                onClick={() => setShowUserMenu(!showUserMenu)}
                className="flex items-center gap-2 p-1 rounded-xl hover:bg-slate-100 dark:hover:bg-slate-800 transition-colors focus:outline-none focus:ring-2 focus:ring-brand-500"
                aria-label="User menu"
              >
                <Avatar
                  name={`${user?.firstName || 'User'} ${user?.lastName || ''}`}
                  size="sm"
                  variant="circle"
                />
                <div className="hidden xl:block text-left text-xs">
                  <div className="font-bold text-slate-800 dark:text-slate-200 truncate max-w-[120px]">
                    {user?.firstName} {user?.lastName}
                  </div>
                  <div className="text-[10px] text-slate-400 uppercase tracking-wider">
                    {user?.role?.replace('_', ' ')}
                  </div>
                </div>
              </button>

              {showUserMenu && (
                <div className="absolute right-0 mt-2 w-56 rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-xl z-50 py-2">
                  <div className="px-4 py-2 border-b border-slate-100 dark:border-slate-800">
                    <p className="text-xs font-bold text-slate-800 dark:text-slate-200">
                      {user?.firstName} {user?.lastName}
                    </p>
                    <p className="text-[11px] text-slate-400 truncate">{user?.email}</p>
                    <div className="mt-1.5">
                      <Badge variant="brand" size="sm">
                        {user?.role?.replace('_', ' ')}
                      </Badge>
                    </div>
                  </div>

                  <div className="py-1">
                    <button
                      type="button"
                      onClick={() => {
                        setShowUserMenu(false);
                        navigate('/profile');
                      }}
                      className="w-full px-4 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center gap-2.5 transition-colors"
                    >
                      <UserCircleIcon className="h-4 w-4 text-slate-400" />
                      My Profile
                    </button>
                    <button
                      type="button"
                      onClick={() => {
                        setShowUserMenu(false);
                        navigate('/settings');
                      }}
                      className="w-full px-4 py-2 text-xs font-medium text-slate-700 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800 flex items-center gap-2.5 transition-colors"
                    >
                      <Cog6ToothIcon className="h-4 w-4 text-slate-400" />
                      System Settings
                    </button>
                  </div>

                  <div className="border-t border-slate-100 dark:border-slate-800 pt-1">
                    <button
                      type="button"
                      onClick={() => {
                        setShowUserMenu(false);
                        logout();
                        navigate('/login');
                      }}
                      className="w-full px-4 py-2 text-xs font-semibold text-rose-600 dark:text-rose-400 hover:bg-rose-50 dark:hover:bg-rose-950/40 flex items-center gap-2.5 transition-colors"
                    >
                      <ArrowRightOnRectangleIcon className="h-4 w-4" />
                      Sign Out
                    </button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </header>

      {/* Quick Search Modal / Command Palette */}
      {showSearchModal && (
        <div
          className="fixed inset-0 z-50 bg-slate-950/60 backdrop-blur-xs flex items-start justify-center pt-20 p-4"
          onClick={() => setShowSearchModal(false)}
        >
          <div
            className="w-full max-w-lg rounded-2xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-2xl overflow-hidden"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-3 border-b border-slate-100 dark:border-slate-800 flex items-center gap-3">
              <MagnifyingGlassIcon className="h-5 w-5 text-brand-600 shrink-0" />
              <input
                autoFocus
                type="text"
                placeholder="Jump to any module or search data..."
                value={searchQuery}
                onChange={(e) => handleSearchChange(e.target.value)}
                className="w-full text-sm bg-transparent border-none text-slate-900 dark:text-slate-100 focus:outline-none placeholder:text-slate-400"
              />
              <button
                type="button"
                onClick={() => setShowSearchModal(false)}
                className="p-1 rounded-lg hover:bg-slate-100 dark:hover:bg-slate-800 text-slate-400"
              >
                <XMarkIcon className="h-5 w-5" />
              </button>
            </div>

            <div className="p-3 max-h-72 overflow-y-auto">
              <p className="text-[11px] font-semibold text-slate-400 uppercase tracking-wider px-2 mb-2">
                Quick Navigation
              </p>
              <div className="space-y-1">
                {quickLinks.map((link) => (
                  <button
                    key={link.path}
                    type="button"
                    onClick={() => {
                      setShowSearchModal(false);
                      navigate(link.path);
                    }}
                    className="w-full flex items-center justify-between px-3 py-2 text-xs font-medium text-slate-700 dark:text-slate-200 hover:bg-brand-50 dark:hover:bg-brand-950/50 hover:text-brand-700 dark:hover:text-brand-300 rounded-xl transition-colors text-left"
                  >
                    <span>{link.label}</span>
                    <span className="text-[10px] text-slate-400 font-mono">{link.path}</span>
                  </button>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default Header;
