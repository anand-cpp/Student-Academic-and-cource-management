import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: 'default' | 'primary' | 'success' | 'warning' | 'error' | 'info' | 'outline' | 'brand' | 'neutral' | 'danger';
  size?: 'sm' | 'md' | 'lg';
  dot?: boolean;
  removable?: boolean;
  onRemove?: () => void;
}

export const Badge = forwardRef<HTMLSpanElement, BadgeProps>(
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
      default: 'bg-slate-100 text-slate-700 border-slate-200/80 dark:bg-slate-800 dark:text-slate-300 dark:border-slate-700',
      neutral: 'bg-slate-100 text-slate-700 border-slate-200/80 dark:bg-slate-800 dark:text-slate-300 dark:border-slate-700',
      primary: 'bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-950/50 dark:text-indigo-300 dark:border-indigo-800/60',
      brand: 'bg-indigo-50 text-indigo-700 border-indigo-200 dark:bg-indigo-950/50 dark:text-indigo-300 dark:border-indigo-800/60',
      success: 'bg-emerald-50 text-emerald-700 border-emerald-200 dark:bg-emerald-950/50 dark:text-emerald-300 dark:border-emerald-800/60',
      warning: 'bg-amber-50 text-amber-700 border-amber-200 dark:bg-amber-950/50 dark:text-amber-300 dark:border-amber-800/60',
      error: 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950/50 dark:text-rose-300 dark:border-rose-800/60',
      danger: 'bg-rose-50 text-rose-700 border-rose-200 dark:bg-rose-950/50 dark:text-rose-300 dark:border-rose-800/60',
      info: 'bg-sky-50 text-sky-700 border-sky-200 dark:bg-sky-950/50 dark:text-sky-300 dark:border-sky-800/60',
      outline: 'bg-transparent border-slate-300 text-slate-700 dark:border-slate-700 dark:text-slate-300',
    };

    const sizes = {
      sm: 'px-2 py-0.5 text-[11px] gap-1 font-medium',
      md: 'px-2.5 py-0.5 text-xs gap-1.5 font-medium',
      lg: 'px-3 py-1 text-xs sm:text-sm gap-2 font-medium',
    };

    const dotColors = {
      default: 'bg-slate-400 dark:bg-slate-500',
      neutral: 'bg-slate-400 dark:bg-slate-500',
      primary: 'bg-indigo-500',
      brand: 'bg-indigo-500',
      success: 'bg-emerald-500',
      warning: 'bg-amber-500',
      error: 'bg-rose-500',
      danger: 'bg-rose-500',
      info: 'bg-sky-500',
      outline: 'bg-slate-400',
    };

    return (
      <span
        ref={ref}
        className={cn(
          'inline-flex items-center rounded-full border shadow-2xs transition-colors',
          variants[variant],
          sizes[size],
          className
        )}
        {...props}
      >
        {dot && (
          <span
            className={cn('h-1.5 w-1.5 rounded-full shrink-0', dotColors[variant])}
            aria-hidden="true"
          />
        )}
        <span>{children}</span>
        {removable && onRemove && (
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              onRemove();
            }}
            className="hover:bg-black/10 dark:hover:bg-white/10 rounded-full p-0.5 transition-colors -mr-1"
            aria-label="Remove badge"
          >
            <svg className="h-3 w-3" fill="currentColor" viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                clipRule="evenodd"
              />
            </svg>
          </button>
        )}
      </span>
    );
  }
);

Badge.displayName = 'Badge';
export default Badge;
