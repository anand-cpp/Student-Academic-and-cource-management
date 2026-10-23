import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface SkeletonProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'text' | 'circular' | 'rectangular' | 'card';
  width?: string | number;
  height?: string | number;
  lines?: number;
  animation?: 'pulse' | 'wave' | 'none';
}

const Skeleton = forwardRef<HTMLDivElement, SkeletonProps>(
  (
    {
      variant = 'text',
      width,
      height,
      lines = 1,
      animation = 'pulse',
      className,
      ...props
    },
    ref
  ) => {
    const baseStyles = 'bg-neutral-200 dark:bg-neutral-700 rounded overflow-hidden';
    
    const animations = {
      pulse: 'animate-pulse',
      wave: 'animate-wave',
      none: '',
    };

    const variants = {
      text: 'h-4',
      circular: 'rounded-full',
      rectangular: 'rounded-lg',
      card: 'rounded-xl',
    };

    if (variant === 'text' && lines > 1) {
      return (
        <div
          ref={ref}
          className={cn('space-y-2', className)}
          {...props}
        >
          {Array.from({ length: lines }).map((_, i) => (
            <div
              key={i}
              className={cn(
                baseStyles,
                variants[variant],
                animations[animation],
                i === lines - 1 ? 'w-3/4' : 'w-full'
              )}
              style={{ width, height }}
              aria-hidden="true"
            />
          ))}
        </div>
      );
    }

    return (
      <div
        ref={ref}
        className={cn(
          baseStyles,
          variants[variant],
          animations[animation],
          className
        )}
        style={{ width, height }}
        aria-hidden="true"
        {...props}
      />
    );
  }
);

Skeleton.displayName = 'Skeleton';

export interface SkeletonCardProps {
  showHeader?: boolean;
  showAvatar?: boolean;
  lines?: number;
  className?: string;
}

function SkeletonCard({ showHeader = true, showAvatar = false, lines = 3, className }: SkeletonCardProps) {
  return (
    <div className={cn('p-6 space-y-4 animate-pulse', className)}>
      {showHeader && (
        <div className="flex items-center gap-4">
          {showAvatar && <Skeleton variant="circular" width={48} height={48} />}
          <div className="flex-1 space-y-2">
            <Skeleton variant="text" width="40%" height={24} />
            <Skeleton variant="text" width="30%" height={16} />
          </div>
        </div>
      )}
      <div className="space-y-3">
        {Array.from({ length: lines }).map((_, i) => (
          <Skeleton key={i} variant="text" width={i === lines - 1 ? '60%' : '100%'} />
        ))}
      </div>
    </div>
  );
}

export interface SkeletonTableProps {
  columns: number;
  rows?: number;
  className?: string;
}

function SkeletonTable({ columns = 5, rows = 5, className }: SkeletonTableProps) {
  return (
    <div className={cn('overflow-x-auto rounded-xl border border-neutral-200 dark:border-neutral-700', className)}>
      <table className="w-full">
        <thead>
          <tr className="bg-neutral-50 dark:bg-neutral-800/50">
            {Array.from({ length: columns }).map((_, i) => (
              <th key={i} className="px-4 py-3 text-left">
                <Skeleton variant="text" width="80%" height={12} />
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-neutral-200 dark:divide-neutral-700">
          {Array.from({ length: rows }).map((_, rowIndex) => (
            <tr key={rowIndex} className={rowIndex % 2 === 1 ? 'bg-neutral-50 dark:bg-neutral-800/30' : ''}>
              {Array.from({ length: columns }).map((_, colIndex) => (
                <td key={colIndex} className="px-4 py-3">
                  <Skeleton variant="text" width="90%" height={16} />
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export interface SkeletonListProps {
  items?: number;
  showAvatar?: boolean;
  lines?: number;
  className?: string;
}

function SkeletonList({ items = 5, showAvatar = true, lines = 2, className }: SkeletonListProps) {
  return (
    <div className={cn('space-y-4', className)}>
      {Array.from({ length: items }).map((_, i) => (
        <div key={i} className="flex items-start gap-4 p-4 bg-white dark:bg-neutral-900 rounded-xl border border-neutral-200 dark:border-neutral-700 animate-pulse">
          {showAvatar && <Skeleton variant="circular" width={48} height={48} />}
          <div className="flex-1 space-y-2 min-w-0">
            <Skeleton variant="text" width="30%" height={20} />
            <div className="space-y-1.5">
              {Array.from({ length: lines }).map((_, j) => (
                <Skeleton key={j} variant="text" width={j === lines - 1 ? '60%' : '80%'} height={14} />
              ))}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}

export { Skeleton, SkeletonCard, SkeletonTable, SkeletonList };