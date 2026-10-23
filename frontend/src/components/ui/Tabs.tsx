import { useState, ReactNode, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface Tab {
  value: string;
  label: string;
  disabled?: boolean;
  icon?: ReactNode;
  badge?: string | number;
}

export interface TabsProps {
  tabs: Tab[];
  value: string;
  onChange: (value: string) => void;
  variant?: 'line' | 'enclosed' | 'soft';
  orientation?: 'horizontal' | 'vertical';
  fullWidth?: boolean;
  className?: string;
}

export function Tabs({
  tabs,
  value,
  onChange,
  variant = 'line',
  orientation = 'horizontal',
  fullWidth = false,
  className,
}: TabsProps) {
  const variants = {
    line: {
      container: 'border-b border-neutral-200 dark:border-neutral-700',
      tab: 'border-b-2 -mb-px',
      active: 'border-brand-500 text-brand-600 dark:text-brand-400',
      inactive: 'border-transparent text-neutral-500 hover:text-neutral-700 dark:hover:text-neutral-300',
    },
    enclosed: {
      container: '',
      tab: 'rounded-md',
      active: 'bg-brand-500 text-white shadow-sm',
      inactive: 'text-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-800 dark:text-neutral-400',
    },
    soft: {
      container: '',
      tab: 'rounded-md',
      active: 'bg-brand-100 text-brand-700 dark:bg-brand-900/50 dark:text-brand-300',
      inactive: 'text-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-800 dark:text-neutral-400',
    },
  };

  const v = variants[variant];
  const isVertical = orientation === 'vertical';

  return (
    <div className={cn(className)}>
      <div
        role="tablist"
        aria-orientation={orientation}
        className={cn(
          v.container,
          isVertical ? 'flex flex-col gap-1 w-fit' : 'flex gap-1',
          fullWidth && !isVertical && 'w-full'
        )}
      >
        {tabs.map((tab) => (
          <button
            key={tab.value}
            role="tab"
            aria-selected={value === tab.value}
            aria-controls={`panel-${tab.value}`}
            id={`tab-${tab.value}`}
            disabled={tab.disabled}
            onClick={() => !tab.disabled && onChange(tab.value)}
            className={cn(
              'inline-flex items-center justify-center gap-2 px-4 py-2.5 text-sm font-medium transition-all duration-200',
              'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2',
              v.tab,
              value === tab.value ? v.active : v.inactive,
              tab.disabled && 'opacity-50 cursor-not-allowed',
              isVertical ? 'w-full text-left' : '',
              fullWidth && !isVertical && 'flex-1'
            )}
          >
            {tab.icon && <span className="flex-shrink-0">{tab.icon}</span>}
            {tab.label}
            {tab.badge && (
              <span className="px-1.5 py-0.5 text-xs font-medium rounded-full bg-neutral-200 dark:bg-neutral-700 text-neutral-700 dark:text-neutral-300">
                {tab.badge}
              </span>
            )}
          </button>
        ))}
      </div>
      <div className="mt-4">
        {tabs.map((tab) => (
          <div
            key={`panel-${tab.value}`}
            role="tabpanel"
            id={`panel-${tab.value}`}
            aria-labelledby={`tab-${tab.value}`}
            hidden={value !== tab.value}
            className={cn(value === tab.value ? 'animate-fade-in' : 'hidden')}
          >
            {typeof tab === 'object' && 'children' in tab ? (tab as any).children : null}
          </div>
        ))}
      </div>
    </div>
  );
}

export interface TabPanelProps extends HTMLAttributes<HTMLDivElement> {
  value: string;
  selected: string;
  children: ReactNode;
}

export function TabPanel({ value, selected, children, className, ...props }: TabPanelProps) {
  if (value !== selected) return null;
  
  return (
    <div
      role="tabpanel"
      className={cn('animate-fade-in', className)}
      {...props}
    >
      {children}
    </div>
  );
}