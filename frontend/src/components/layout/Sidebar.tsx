import { useState, ReactNode } from 'react';
import { cn } from '@/utils';
import { Button } from '@/components/ui';
import { ChevronLeftIcon, ChevronRightIcon, Bars3Icon, XMarkIcon } from '@heroicons/react/24/outline';
import { useTheme } from '@/contexts';
import { SidebarItem } from '@/types';
import { useAuth } from '@/contexts';

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
  const setIsOpen = onToggle ? (v: boolean) => onToggle(v) : setUncontrolledIsOpen;
  
  const collapsed = controlledCollapsed ?? uncontrolledCollapsed;
  const setCollapsed = onCollapseChange ? (v: boolean) => onCollapseChange(v) : setUncontrolledCollapsed;
  
  const { theme, resolvedTheme, setTheme } = useTheme();
  const { user, hasRole } = useAuth();

  const filteredItems = items.filter((item) => {
    if (!item.roles || item.roles.length === 0) return true;
    return hasRole(item.roles);
  });

  const renderItem = (item: SidebarItem, level = 0, parentPath = '') => {
    const href = item.href ? `${parentPath}${item.href}` : undefined;
    const isActive = item.isActive;
    const hasChildren = item.children && item.children.length > 0;

    if (hasChildren) {
      const [expanded, setExpanded] = useState(false);
      
      return (
        <div key={item.label}>
          <button
            type="button"
            onClick={() => setExpanded(!expanded)}
            className={cn(
              'w-full flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg transition-colors',
              'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2',
              collapsed
                ? 'justify-center px-2'
                : 'text-neutral-700 dark:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            )}
            aria-expanded={expanded}
            aria-controls={`${item.label}-submenu`}
            disabled={collapsed}
          >
            <span className="flex-shrink-0">{item.icon}</span>
            {!collapsed && (
              <>
                <span className="truncate flex-1">{item.label}</span>
                <span className={cn('transition-transform', expanded && 'rotate-180')}>
                  <ChevronRightIcon className="h-4 w-4 text-neutral-400" />
                </span>
                {item.badge && (
                  <span className="px-2 py-0.5 text-xs font-medium rounded-full bg-brand-100 dark:bg-brand-900/50 text-brand-700 dark:text-brand-300">
                    {item.badge}
                  </span>
                )}
              </>
            )}
          </button>
          {!collapsed && expanded && (
            <div id={`${item.label}-submenu`} className="mt-1 ml-8 space-y-1 border-l border-neutral-200 dark:border-neutral-700 pl-3">
              {item.children!.map((child) => renderItem(child, level + 1, href || ''))}
            </div>
          )}
        </div>
      );
    }

    return (
      <a
        key={item.label}
        href={href}
        className={cn(
          'flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg transition-colors',
          'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2',
          isActive
            ? 'bg-brand-50 dark:bg-brand-900/20 text-brand-700 dark:text-brand-300'
            : 'text-neutral-700 dark:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-800',
          collapsed && 'justify-center px-2'
        )}
        aria-current={isActive ? 'page' : undefined}
        title={collapsed ? item.label : undefined}
      >
        <span className="flex-shrink-0">{item.icon}</span>
        {!collapsed && <span className="truncate">{item.label}</span>}
        {item.badge && !collapsed && (
          <span className="ml-auto px-2 py-0.5 text-xs font-medium rounded-full bg-brand-100 dark:bg-brand-900/50 text-brand-700 dark:text-brand-300">
            {item.badge}
          </span>
        )}
      </a>
    );
  };

  return (
    <>
      <button
        type="button"
        className="fixed top-4 left-4 z-50 lg:hidden p-2 rounded-lg bg-white dark:bg-neutral-900 border border-neutral-200 dark:border-neutral-700 shadow-lg"
        onClick={() => setIsOpen(!isOpen)}
        aria-label={isOpen ? 'Close sidebar' : 'Open sidebar'}
        aria-expanded={isOpen}
      >
        {isOpen ? <XMarkIcon className="h-6 w-6" /> : <Bars3Icon className="h-6 w-6" />}
      </button>

      <aside
        className={cn(
          'fixed inset-y-0 left-0 z-40 bg-white dark:bg-neutral-900 border-r border-neutral-200 dark:border-neutral-700 transition-all duration-300 ease-out flex flex-col',
          'lg:translate-x-0',
          isOpen ? 'translate-x-0 w-64' : '-translate-x-full lg:w-64',
          collapsed && 'w-16 lg:w-16',
          className
        )}
        aria-label="Sidebar navigation"
      >
        <div className="flex items-center justify-between h-16 px-4 border-b border-neutral-200 dark:border-neutral-700">
          {!collapsed && logo}
          {collapsed && (
            <div className="flex items-center justify-center">
              {logo}
            </div>
          )}
          {!collapsed && (
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setCollapsed(!collapsed)}
              aria-label={collapsed ? 'Expand sidebar' : 'Collapse sidebar'}
            >
              {collapsed ? <ChevronRightIcon className="h-5 w-5" /> : <ChevronLeftIcon className="h-5 w-5" />}
            </Button>
          )}
        </div>

        <nav className="flex-1 overflow-y-auto p-3 space-y-1" aria-label="Main navigation">
          {filteredItems.map((item) => renderItem(item))}
        </nav>

        <div className="p-3 border-t border-neutral-200 dark:border-neutral-700">
          {!collapsed && footer}
          {!collapsed && (
            <div className="flex items-center gap-3 px-3 py-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setTheme(resolvedTheme === 'dark' ? 'light' : 'dark')}
                className="flex-1 justify-center"
              >
                {resolvedTheme === 'dark' ? '☀️ Light' : '🌙 Dark'}
              </Button>
            </div>
          )}
        </div>
      </aside>

      {isOpen && (
        <div
          className="fixed inset-0 z-30 bg-black/50 lg:hidden"
          onClick={() => setIsOpen(false)}
          aria-hidden="true"
        />
      )}
    </>
  );
}