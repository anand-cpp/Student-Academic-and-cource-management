import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface SpinnerProps extends HTMLAttributes<HTMLDivElement> {
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  variant?: 'default' | 'primary' | 'white';
  thickness?: number;
}

const Spinner = forwardRef<HTMLDivElement, SpinnerProps>(
  (
    {
      size = 'md',
      variant = 'primary',
      thickness = 3,
      className,
      'aria-label': ariaLabel = 'Loading',
      ...props
    },
    ref
  ) => {
    const sizes = {
      xs: 'h-3 w-3',
      sm: 'h-5 w-5',
      md: 'h-8 w-8',
      lg: 'h-10 w-10',
      xl: 'h-12 w-12',
    };

    const variants = {
      default: 'text-primary-600',
      primary: 'text-primary-600',
      white: 'text-surface-50',
    };

    return (
      <div
        ref={ref}
        role="status"
        aria-label={ariaLabel}
        aria-live="polite"
        className={cn(
          'inline-flex',
          sizes[size],
          variants[variant],
          className
        )}
        {...props}
      >
        <svg
          className="animate-spin h-full w-full"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth={thickness}
          />
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
        <span className="sr-only">{ariaLabel}</span>
      </div>
    );
  }
);

Spinner.displayName = 'Spinner';

export interface SpinnerOverlayProps {
  isVisible: boolean;
  message?: string;
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

function SpinnerOverlay({ isVisible, message, size = 'md', className }: SpinnerOverlayProps) {
  if (!isVisible) return null;

  return (
    <div
      className={cn(
        'fixed inset-0 z-50 flex items-center justify-center bg-surface-container-lowest/90 dark:bg-surface-950/90 backdrop-blur-sm',
        className
      )}
      role="status"
      aria-live="polite"
      aria-label={message || 'Loading'}
    >
      <div className="flex flex-col items-center gap-3 p-6 rounded-xl bg-surface-container-lowest dark:bg-surface-900 shadow-lg border border-surface-200 dark:border-surface-800">
        <Spinner size={size} />
        {message && <p className="text-sm text-surface-600 dark:text-surface-400">{message}</p>}
      </div>
    </div>
  );
}

export { Spinner, SpinnerOverlay };
