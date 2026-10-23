import { NavLink as RouterNavLink, NavLinkProps } from 'react-router-dom';
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
      className={({ isActive, isPending }) =>
        cn(
          'flex items-center gap-3 px-3 py-2.5 text-sm font-medium rounded-lg transition-colors',
          'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2',
          disabled
            ? 'text-neutral-400 dark:text-neutral-500 cursor-not-allowed'
            : isActive
            ? 'bg-brand-50 dark:bg-brand-900/20 text-brand-700 dark:text-brand-300'
            : 'text-neutral-700 dark:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-800',
          className
        )
      }
      aria-current={({ isActive }) => isActive ? 'page' : undefined}
      aria-disabled={disabled}
      tabIndex={disabled ? -1 : undefined}
    >
      {({ isActive }) => (
        <>
          {icon && <span className={cn('flex-shrink-0', isActive ? 'text-brand-600 dark:text-brand-400' : 'text-neutral-400')}>{icon}</span>}
          <span className="truncate">{label}</span>
          {badge && (
            <span className={cn(
              'ml-auto px-2 py-0.5 text-xs font-medium rounded-full',
              isActive
                ? 'bg-brand-100 dark:bg-brand-900/50 text-brand-700 dark:text-brand-300'
                : 'bg-neutral-100 dark:bg-neutral-800 text-neutral-600 dark:text-neutral-400'
            )}>
              {badge}
            </span>
          )}
        </>
      )}
    </RouterNavLink>
  );
}