import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'primary' | 'success' | 'warning' | 'error' | 'info' | 'outline';
  size?: 'sm' | 'md' | 'lg';
  dot?: boolean;
  removable?: boolean;
  onRemove?: () => void;
}

const Badge = forwardRef<HTMLSpanElement, BadgeProps>(
  (
    {
      children,
      variant = 'default',
      size = 'md',
      dot = false,
      removable = false,
      onRemove,
      className,
      ...props
    },
    ref
  ) => {
    const variants = {
      default: 'bg-neutral-100 text-neutral-700 dark:bg-neutral-800 dark:text-neutral-300',
      primary: 'bg-brand-100 text-brand-700 dark:bg-brand-900/50 dark:text-brand-300',
      success: 'bg-success-100 text-success-700 dark:bg-success-900/50 dark:text-success-300',
      warning: 'bg-warning-100 text-warning-700 dark:bg-warning-900/50 dark:text-warning-300',
      error: 'bg-error-100 text-error-700 dark:bg-error-900/50 dark:text-error-300',
      info: 'bg-info-100 text-info-700 dark:bg-info-900/50 dark:text-info-300',
      outline: 'bg-transparent border border-neutral-300 text-neutral-700 dark:border-neutral-600 dark:text-neutral-300',
    };

    const sizes = {
      sm: 'px-2 py-0.5 text-xs gap-1',
      md: 'px-2.5 py-1 text-xs gap-1.5',
      lg: 'px-3 py-1 text-sm gap-2',
    };

    const dotColors = {
      default: 'bg-neutral-500',
      primary: 'bg-brand-500',
      success: 'bg-success-500',
      warning: 'bg-warning-500',
      error: 'bg-error-500',
      info: 'bg-info-500',
      outline: 'bg-neutral-500',
    };

    return (
      <span
        ref={ref}
        className={cn(
          'inline-flex items-center font-medium rounded-full',
          variants[variant],
          sizes[size],
          className
        )}
        {...props}
      >
        {dot && (
          <span className={cn('rounded-full flex-shrink-0', dotColors[variant])}>
            <span className="sr-only">{children}, </span>
          </span>
        )}
        {children}
        {removable && (
          <button
            type="button"
            onClick={onRemove}
            className={cn(
              'flex items-center justify-center rounded-full transition-colors',
              'hover:bg-black/10 dark:hover:bg-white/10',
              size === 'sm' && 'w-4 h-4 -ml-1',
              size === 'md' && 'w-5 h-5 -ml-1.5',
              size === 'lg' && 'w-6 h-6 -ml-2'
            )}
            aria-label="Remove"
          >
            <svg className="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        )}
      </span>
    );
  }
);

Badge.displayName = 'Badge';

export { Badge };