import { NavLink as RouterNavLink, NavLinkProps, NavLinkRenderProps } from 'react-router-dom';
import { cn } from '@/utils';
import { ReactNode } from 'react';

export interface NavLinkCustomProps extends Omit<NavLinkProps, 'children'> {
  label: string;
  icon?: ReactNode;
  badge?: string | number;
  disabled?: boolean;
}

export function NavLink({
  label,
  icon,
  badge,
  disabled = false,
  className,
  end = false,
  ...props
}: NavLinkCustomProps) {
  return (
    <RouterNavLink
      {...props}
      end={end}
      className={({ isActive, isPending }: NavLinkRenderProps) =>
        cn(
          'flex items-center gap-3 px-4 py-2.5 text-sm font-medium rounded-lg transition-colors',
          'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 focus-visible:ring-offset-2',
          disabled
            ? 'text-surface-400 dark:text-surface-500 cursor-not-allowed'
            : isActive
            ? 'bg-primary-100 text-primary-700 dark:bg-primary-900/50 dark:text-primary-300'
            : 'text-surface-600 dark:text-surface-400 hover:bg-surface-100 dark:hover:bg-surface-800',
          className
        )
      }
      aria-disabled={disabled}
      tabIndex={disabled ? -1 : undefined}
    >
      {({ isActive }: NavLinkRenderProps) => (
        <>
          {icon && <span className={cn('flex-shrink-0', isActive ? 'text-primary-700 dark:text-primary-300' : 'text-surface-600 dark:text-surface-400')}>{icon}</span>}
          <span className="truncate">{label}</span>
          {badge && (
            <span className={cn(
              'ml-auto px-2 py-0.5 text-xs font-medium rounded-full',
              isActive ? 'bg-primary-100 dark:bg-primary-900/50 text-primary-700 dark:text-primary-300' : 'bg-surface-100 dark:bg-surface-800 text-surface-600 dark:text-surface-400'
            )}>
              {badge}
            </span>
          )}
        </>
      )}
    </RouterNavLink>
  );
}
