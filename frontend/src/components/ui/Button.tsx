import { forwardRef, ButtonHTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'outline' | 'ghost' | 'danger' | 'success' | 'brand';
  size?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  loading?: boolean;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  fullWidth?: boolean;
}

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
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
      font-medium tracking-tight
      rounded-xl
      transition-all duration-150 ease-out
      whitespace-nowrap
      select-none
      cursor-pointer
      focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 dark:focus-visible:ring-offset-slate-900
      disabled:opacity-50 disabled:cursor-not-allowed disabled:pointer-events-none active:scale-[0.98]
    `;

    const variants = {
      primary: 'bg-brand-600 text-white hover:bg-brand-700 active:bg-brand-800 shadow-sm hover:shadow shadow-brand-500/20',
      brand: 'bg-brand-600 text-white hover:bg-brand-700 active:bg-brand-800 shadow-sm hover:shadow shadow-brand-500/20',
      secondary: 'bg-slate-100 text-slate-800 hover:bg-slate-200/80 active:bg-slate-300 dark:bg-slate-800 dark:text-slate-100 dark:hover:bg-slate-700 border border-slate-200/60 dark:border-slate-700/60',
      outline: 'bg-transparent text-slate-700 dark:text-slate-200 border border-slate-300 dark:border-slate-700 hover:bg-slate-50 dark:hover:bg-slate-800/80 active:bg-slate-100 dark:active:bg-slate-800',
      ghost: 'bg-transparent text-slate-600 dark:text-slate-400 hover:bg-slate-100 dark:hover:bg-slate-800 hover:text-slate-900 dark:hover:text-slate-100 active:bg-slate-200/60 dark:active:bg-slate-800/80',
      danger: 'bg-rose-600 text-white hover:bg-rose-700 active:bg-rose-800 shadow-sm shadow-rose-500/20 focus-visible:ring-rose-500',
      success: 'bg-emerald-600 text-white hover:bg-emerald-700 active:bg-emerald-800 shadow-sm shadow-emerald-500/20 focus-visible:ring-emerald-500',
    };

    const sizes = {
      xs: 'px-2.5 py-1 text-xs h-7 gap-1.5',
      sm: 'px-3 py-1.5 text-xs sm:text-sm h-8 gap-1.5',
      md: 'px-4 py-2 text-sm h-10 gap-2',
      lg: 'px-5 py-2.5 text-base h-11 gap-2.5',
      xl: 'px-6 py-3 text-base h-12 gap-3',
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
            className="animate-spin h-4 w-4 text-current"
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
        ) : leftIcon ? (
          <span className="inline-flex shrink-0 items-center">{leftIcon}</span>
        ) : null}
        <span>{children}</span>
        {!loading && rightIcon && (
          <span className="inline-flex shrink-0 items-center">{rightIcon}</span>
        )}
      </button>
    );
  }
);

Button.displayName = 'Button';
export default Button;
