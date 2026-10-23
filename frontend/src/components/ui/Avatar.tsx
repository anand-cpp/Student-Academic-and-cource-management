import { forwardRef, HTMLAttributes, useMemo } from 'react';
import { cn } from '@/utils';

const sizes = {
  xs: 'h-6 w-6 text-xs',
  sm: 'h-8 w-8 text-sm',
  md: 'h-10 w-10 text-base',
  lg: 'h-12 w-12 text-lg',
  xl: 'h-16 w-16 text-xl',
  '2xl': 'h-24 w-24 text-2xl',
};

const statusSizes = {
  xs: 'h-1.5 w-1.5',
  sm: 'h-2 w-2',
  md: 'h-2.5 w-2.5',
  lg: 'h-3 w-3',
  xl: 'h-4 w-4',
  '2xl': 'h-5 w-5',
};

const statusPositions = {
  'bottom-right': 'bottom-0 right-0',
  'bottom-left': 'bottom-0 left-0',
  'top-right': 'top-0 right-0',
  'top-left': 'top-0 left-0',
};

const statusColors = {
  online: 'bg-success-500',
  offline: 'bg-surface-400',
  busy: 'bg-error-500',
  away: 'bg-warning-500',
};

export interface AvatarProps extends HTMLAttributes<HTMLDivElement> {
  src?: string;
  alt?: string;
  name?: string;
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl';
  shape?: 'circle' | 'square';
  variant?: string;
  status?: 'online' | 'offline' | 'busy' | 'away';
  statusPosition?: 'bottom-right' | 'bottom-left' | 'top-right' | 'top-left';
}

const Avatar = forwardRef<HTMLDivElement, AvatarProps>(
  (
    {
      src,
      alt,
      name,
      size = 'md',
      shape = 'circle',
      status,
      statusPosition = 'bottom-right',
      className,
      ...props
    },
    ref
  ) => {
    const initials = useMemo(() => {
      if (!name) return '?';
      const parts = name.trim().split(/\s+/);
      if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }, [name]);

    const bgColor = useMemo(() => {
      if (!name) return 'bg-surface-300';
      let hash = 0;
      for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
      }
      const hue = hash % 360;
      return `hsl(${hue}, 70%, 45%)`;
    }, [name]);

    return (
      <div
        ref={ref}
        className={cn('relative inline-flex shrink-0 overflow-hidden', sizes[size], shape === 'square' ? 'rounded-lg' : 'rounded-full', className)}
        {...props}
      >
        {src ? (
          <img
            src={src}
            alt={alt || name || 'Avatar'}
            className="h-full w-full object-cover"
            loading="lazy"
          />
        ) : (
          <div
            className="h-full w-full flex items-center justify-center font-medium text-surface-950 select-none"
            style={{ backgroundColor: bgColor }}
            aria-label={name || 'User avatar'}
          >
            {initials}
          </div>
        )}
        {status && (
          <span
            className={cn(
              'absolute rounded-full border-2 border-white dark:border-surface-950',
              statusSizes[size],
              statusColors[status],
              statusPositions[statusPosition]
            )}
            aria-label={`Status: ${status}`}
          />
        )}
      </div>
    );
  }
);

Avatar.displayName = 'Avatar';

export interface AvatarGroupProps extends HTMLAttributes<HTMLDivElement> {
  max?: number;
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl' | '2xl';
  spacing?: number;
}

const AvatarGroup = forwardRef<HTMLDivElement, AvatarGroupProps>(
  ({ children, max, size = 'md', spacing = -8, className, ...props }, ref) => {
    const childArray = Array.isArray(children) ? children : [children];
    const validChildren = childArray.filter((child) => child != null) as React.ReactElement<AvatarProps>[];
    const visibleChildren = max ? validChildren.slice(0, max) : validChildren;
    const remainingCount = max ? validChildren.length - max : 0;

    return (
      <div ref={ref} className={cn('flex items-center', className)} {...props}>
        <div className="flex" style={{ marginLeft: spacing }}>
          {visibleChildren.map((child, index) => (
            <div
              key={child.key || index}
              className={cn('relative z-10', index > 0 && `-ml-${Math.abs(spacing)}`)}
            >
              {child}
            </div>
          ))}
          {remainingCount > 0 && (
            <div
              className={cn(
                'relative z-0 flex items-center justify-center font-medium text-surface-600 dark:text-surface-400 bg-surface-container-lowest border border-surface-200 dark:border-surface-700 rounded-full',
                sizes[size],
                '-ml-2'
              )}
              aria-label={`${remainingCount} more people`}
            >
              +{remainingCount}
            </div>
          )}
        </div>
      </div>
    );
  }
);

AvatarGroup.displayName = 'AvatarGroup';

export { Avatar, AvatarGroup };
