import { forwardRef, ButtonHTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'success';
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  loading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  fullWidth?: boolean;
}

const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  (
    {
      children,
      variant = 'primary',
      size = 'md',
      loading = false,
      leftIcon,
      rightIcon,
      fullWidth = false,
      disabled,
      className,
      type = 'button',
      ...props
    },
    ref
  ) => {
    const baseStyles = `
      inline-flex items-center justify-center gap-2
      font-sans font-medium
      rounded-md
      transition-all duration-200 ease-out
      whitespace-nowrap
      select-none
      focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25
      disabled:opacity-50 disabled:cursor-not-allowed disabled:pointer-events-none
    `;

    const variants = {
      primary: 'bg-brand-500 text-white hover:bg-brand-600 active:bg-brand-700',
      secondary: 'bg-neutral-100 text-neutral-900 border border-neutral-200 hover:bg-neutral-200 active:bg-neutral-300 dark:bg-neutral-800 dark:text-neutral-50 dark:border-neutral-700 dark:hover:bg-neutral-700 dark:active:bg-neutral-600',
      outline: 'bg-transparent text-brand-600 border border-brand-300 hover:bg-brand-50 active:bg-brand-100 dark:text-brand-400 dark:border-brand-700 dark:hover:bg-brand-900 dark:active:bg-brand-800',
      ghost: 'bg-transparent text-neutral-600 hover:bg-neutral-100 hover:text-neutral-900 active:bg-neutral-200 dark:text-neutral-400 dark:hover:bg-neutral-800 dark:hover:text-neutral-50 dark:active:bg-neutral-700',
      danger: 'bg-error-600 text-white hover:bg-error-700 active:bg-error-900 focus-visible:ring-error-500/25',
      success: 'bg-success-600 text-white hover:bg-success-700 active:bg-success-900 focus-visible:ring-success-500/25',
    };

    const sizes = {
      xs: 'px-2 py-1 h-7 min-w-7 text-xs',
      sm: 'px-3 py-1 h-8 min-w-8 text-sm',
      md: 'px-4 py-2 h-10 min-w-10 text-sm',
      lg: 'px-6 py-3 h-12 min-w-12 text-base',
      xl: 'px-8 py-4 h-14 min-w-14 text-base',
    };

    const widthStyles = fullWidth ? 'w-full' : '';

    return (
      <button
        ref={ref}
        type={type}
        disabled={disabled || loading}
        className={cn(baseStyles, variants[variant], sizes[size], widthStyles, className)}
        {...props}
      >
        {loading ? (
          <svg
            className="animate-spin h-4 w-4"
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
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
        ) : (
          <>
            {leftIcon && <span className="flex-shrink-0">{leftIcon}</span>}
            {children}
            {rightIcon && <span className="flex-shrink-0">{rightIcon}</span>}
          </>
        )}
      </button>
    );
  }
);

Button.displayName = 'Button';

export { Button };