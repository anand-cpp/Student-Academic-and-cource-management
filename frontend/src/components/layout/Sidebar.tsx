import { useState, ReactNode } from 'react';
import { NavLink } from 'react-router-dom';
import { cn } from '@/utils';
import { Button } from '@/components/ui';
import { ChevronLeftIcon, ChevronRightIcon, ChevronDownIcon, Bars3Icon, XMarkIcon, AcademicCapIcon, SunIcon, MoonIcon, ComputerDesktopIcon } from '@heroicons/react/24/outline';
import { useTheme, useAuth } from '@/contexts';
import { SidebarItem } from '@/types';

export interface SidebarProps {
  items: SidebarItem[];
  isOpen?: boolean;
  onToggle?: (open: boolean) => void;
  collapsed?: boolean;
  onCollapseChange?: (collapsed: boolean) => void;
  className?: string;
  logo?: ReactNode;
  footer?: ReactNode;
}

function SubmenuItem({ item, collapsed, onNavigate }: { item: SidebarItem; collapsed: boolean; onNavigate?: () => void }) {
  const [expanded, setExpanded] = useState(false);

  return (
    <div>
      <button
        type="button"
        onClick={() => setExpanded(!expanded)}
        className={cn(
          'w-full flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-xl transition-all duration-150',
          'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800/80',
          'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500',
          collapsed && 'justify-center px-2'
        )}
        aria-expanded={expanded}
        disabled={collapsed}
      >
        <span className="flex-shrink-0 text-slate-500 dark:text-slate-400">{item.icon}</span>
        {!collapsed && (
          <>
            <span className="truncate flex-1 text-left">{item.label}</span>
            <ChevronDownIcon className={cn('h-4 w-4 text-slate-400 transition-transform duration-200', expanded && 'rotate-180')} />
            {item.badge && (
              <span className="px-2 py-0.5 text-xs font-semibold rounded-full bg-brand-100 dark:bg-brand-900/60 text-brand-700 dark:text-brand-300">
                {item.badge}
              </span>
            )}
          </>
        )}
      </button>
      {!collapsed && expanded && item.children && (
        <div className="mt-1 ml-4 pl-3 space-y-1 border-l border-slate-200 dark:border-slate-800">
          {item.children.map((child) => (
            <NavLink
              key={child.label}
              to={child.href || '#'}
              onClick={onNavigate}
              className={({ isActive }) =>
                cn(
                  'flex items-center gap-2.5 px-3 py-2 text-xs font-medium rounded-lg transition-colors',
                  isActive
                    ? 'bg-brand-50 text-brand-700 dark:bg-brand-950/60 dark:text-brand-300 font-semibold'
                    : 'text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800'
                )
              }
            >
              {child.icon && <span className="h-4 w-4">{child.icon}</span>}
              <span className="truncate">{child.label}</span>
            </NavLink>
          ))}
        </div>
      )}
    </div>
  );
}

export function Sidebar({
  items,
  isOpen: controlledIsOpen,
  onToggle,
  collapsed: controlledCollapsed,
  onCollapseChange,
  className,
  logo,
  footer,
}: SidebarProps) {
  const [uncontrolledIsOpen, setUncontrolledIsOpen] = useState(false);
  const [uncontrolledCollapsed, setUncontrolledCollapsed] = useState(false);

  const isOpen = controlledIsOpen ?? uncontrolledIsOpen;
  const setIsOpen = onToggle ?? setUncontrolledIsOpen;

  const collapsed = controlledCollapsed ?? uncontrolledCollapsed;
  const setCollapsed = onCollapseChange ?? setUncontrolledCollapsed;

  const { theme, resolvedTheme, setTheme } = useTheme();
  const { user, hasRole } = useAuth();

  const filteredItems = items.filter((item) => {
    if (!item.roles || item.roles.length === 0) return true;
    return hasRole(item.roles);
  });

  const handleNavClick = () => {
    // Close sidebar on mobile
    if (window.innerWidth < 1024) {
      setIsOpen(false);
    }
  };

  return (
    <>
      {/* Mobile toggle button */}
      <button
        type="button"
        className="fixed top-3 left-4 z-50 lg:hidden p-2 rounded-xl bg-white dark:bg-slate-900 border border-slate-200 dark:border-slate-800 shadow-md text-slate-700 dark:text-slate-200"
        onClick={() => setIsOpen(!isOpen)}
        aria-label={isOpen ? 'Close sidebar' : 'Open sidebar'}
        aria-expanded={isOpen}
      >
        {isOpen ? <XMarkIcon className="h-5 w-5" /> : <Bars3Icon className="h-5 w-5" />}
      </button>

      {/* Main Sidebar */}
      <aside
        className={cn(
          'fixed inset-y-0 left-0 z-40 bg-white dark:bg-slate-900 border-r border-slate-200/80 dark:border-slate-800 transition-all duration-300 ease-in-out flex flex-col',
          'lg:translate-x-0',
          isOpen ? 'translate-x-0 w-64 shadow-2xl lg:shadow-none' : '-translate-x-full lg:translate-x-0',
          collapsed ? 'lg:w-20' : 'lg:w-64',
          className
        )}
        aria-label="Main sidebar navigation"
      >
        {/* Header / Brand */}
        <div className="flex items-center justify-between h-16 px-4 border-b border-slate-100 dark:border-slate-800">
          {!collapsed ? (
            logo || (
              <div className="flex items-center gap-3">
                <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-brand-600 to-indigo-500 flex items-center justify-center text-white shadow-md shadow-brand-500/30">
                  <AcademicCapIcon className="h-6 w-6" />
                </div>
                <div>
                  <span className="font-extrabold text-base tracking-tight bg-gradient-to-r from-slate-900 to-slate-700 dark:from-white dark:to-slate-300 bg-clip-text text-transparent">
                    Academia<span className="text-brand-600 dark:text-brand-400">Pro</span>
                  </span>
                  <p className="text-[10px] uppercase tracking-wider font-semibold text-slate-400 dark:text-slate-500">
                    Academic Suite
                  </p>
                </div>
              </div>
            )
          ) : (
            <div className="mx-auto">
              <div className="h-10 w-10 rounded-xl bg-gradient-to-tr from-brand-600 to-indigo-500 flex items-center justify-center text-white shadow-md shadow-brand-500/30">
                <AcademicCapIcon className="h-6 w-6" />
              </div>
            </div>
          )}

          <Button
            variant="ghost"
            size="xs"
            onClick={() => setCollapsed(!collapsed)}
            aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            className="hidden lg:inline-flex text-slate-400 hover:text-slate-600 dark:hover:text-slate-200"
          >
            {collapsed ? <ChevronRightIcon className="h-4 w-4" /> : <ChevronLeftIcon className="h-4 w-4" />}
          </Button>
        </div>

        {/* User Role Tag */}
        {!collapsed && user && (
          <div className="px-4 py-2 bg-slate-50/70 dark:bg-slate-800/40 border-b border-slate-100 dark:border-slate-800 flex items-center justify-between text-xs">
            <span className="text-slate-500 dark:text-slate-400 truncate max-w-[140px]">
              {user.firstName} {user.lastName}
            </span>
            <span className="px-2 py-0.5 rounded-full text-[10px] font-bold uppercase tracking-wider bg-indigo-100 text-indigo-700 dark:bg-indigo-950/80 dark:text-indigo-300 border border-indigo-200/50 dark:border-indigo-800/50">
              {user.role.replace('_', ' ')}
            </span>
          </div>
        )}

        {/* Navigation list */}
        <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1" aria-label="Main navigation">
          {filteredItems.map((item) => {
            if (item.children && item.children.length > 0) {
              return (
                <SubmenuItem
                  key={item.label}
                  item={item}
                  collapsed={collapsed}
                  onNavigate={handleNavClick}
                />
              );
            }

            return (
              <NavLink
                key={item.label}
                to={item.href || '#'}
                onClick={handleNavClick}
                title={collapsed ? item.label : undefined}
                className={({ isActive }) =>
                  cn(
                    'flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-xl transition-all duration-150',
                    'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500',
                    isActive
                      ? 'bg-brand-600 text-white shadow-sm shadow-brand-500/25 font-semibold'
                      : 'text-slate-600 dark:text-slate-300 hover:bg-slate-100 dark:hover:bg-slate-800/80 hover:text-slate-900 dark:hover:text-white',
                    collapsed && 'justify-center px-2'
                  )
                }
              >
                <span className={cn('shrink-0 h-5 w-5')}>{item.icon}</span>
                {!collapsed && <span className="truncate flex-1">{item.label}</span>}
                {!collapsed && item.badge && (
                  <span className="ml-auto px-2 py-0.5 text-xs font-semibold rounded-full bg-slate-100 dark:bg-slate-800 text-slate-700 dark:text-slate-300">
                    {item.badge}
                  </span>
                )}
              </NavLink>
            );
          })}
        </nav>

        {/* Footer & Theme Controls */}
        <div className="p-3 border-t border-slate-100 dark:border-slate-800 bg-slate-50/50 dark:bg-slate-900/50">
          {!collapsed && footer}
          
          <div className={cn('flex items-center gap-1', collapsed ? 'flex-col' : 'justify-between px-1')}>
            <div className={cn('flex items-center gap-1 bg-slate-200/70 dark:bg-slate-800 rounded-xl p-1', collapsed && 'flex-col')}>
              <button
                type="button"
                onClick={() => setTheme('light')}
                title="Light Mode"
                className={cn(
                  'p-1.5 rounded-lg text-xs transition-colors',
                  theme === 'light'
                    ? 'bg-white text-amber-500 shadow-xs dark:bg-slate-700'
                    : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
                )}
              >
                <SunIcon className="h-4 w-4" />
              </button>
              <button
                type="button"
                onClick={() => setTheme('dark')}
                title="Dark Mode"
                className={cn(
                  'p-1.5 rounded-lg text-xs transition-colors',
                  theme === 'dark'
                    ? 'bg-slate-900 text-indigo-400 shadow-xs dark:bg-slate-700 dark:text-indigo-300'
                    : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
                )}
              >
                <MoonIcon className="h-4 w-4" />
              </button>
              <button
                type="button"
                onClick={() => setTheme('system')}
                title="System Theme"
                className={cn(
                  'p-1.5 rounded-lg text-xs transition-colors',
                  theme === 'system'
                    ? 'bg-white text-slate-700 shadow-xs dark:bg-slate-700 dark:text-slate-200'
                    : 'text-slate-500 dark:text-slate-400 hover:text-slate-800 dark:hover:text-slate-200'
                )}
              >
                <ComputerDesktopIcon className="h-4 w-4" />
              </button>
            </div>

            {!collapsed && (
              <span className="text-[11px] font-medium text-slate-400 dark:text-slate-500">
                {resolvedTheme === 'dark' ? 'Dark' : 'Light'} Mode
              </span>
            )}
          </div>
        </div>
      </aside>

      {/* Backdrop for mobile */}
      {isOpen && (
        <div
          className="fixed inset-0 z-30 bg-slate-950/60 backdrop-blur-xs lg:hidden transition-opacity"
          onClick={() => setIsOpen(false)}
          aria-hidden="true"
        />
      )}
    </>
  );
}

export default Sidebar;
