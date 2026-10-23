import { forwardRef, SelectHTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface SelectProps extends Omit<SelectHTMLAttributes<HTMLSelectElement>, 'size'> {
  label?: string;
  error?: string;
  helperText?: string;
  options: SelectOption[];
  placeholder?: string;
  variant?: 'default' | 'filled' | 'outlined';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
}

const Select = forwardRef<HTMLSelectElement, SelectProps>(
  (
    {
      label,
      error,
      helperText,
      options,
      placeholder,
      variant = 'default',
      size = 'md',
      fullWidth = true,
      className,
      id,
      disabled,
      required,
      'aria-describedby': ariaDescribedBy,
      ...props
    },
    ref
  ) => {
    const selectId = id || `select-${Math.random().toString(36).substring(2, 9)}`;
    const helperId = helperText ? `${selectId}-helper` : undefined;
    const errorId = error ? `${selectId}-error` : undefined;
    const describedBy = [helperId, errorId].filter(Boolean).join(' ') || undefined;

    const baseStyles = `
      w-full
      font-sans
      rounded-md
      transition-colors duration-200
      text-neutral-900 dark:text-neutral-50
      bg-white dark:bg-neutral-900
      disabled:opacity-50 disabled:cursor-not-allowed
      focus-visible:outline-none focus-visible:ring-3
      appearance-none
      bg-[url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 20 20'%3e%3cpath stroke='%236b7280' stroke-linecap='round' stroke-linejoin='round' stroke-width='1.5' d='M6 8l4 4 4-4'/%3e%3c/svg%3e")] bg-[length:1.5rem_1.5rem] bg-[right_0.5rem_center] bg-no-repeat
      pr-10
    `;

    const variants = {
      default: 'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
      filled: 'bg-neutral-100 border border-transparent hover:bg-neutral-200 focus:bg-white focus:border-brand-500 focus:ring-brand-500/25 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:focus:bg-neutral-900',
      outlined: 'border-2 border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
    };

    const sizes = {
      sm: 'px-3 h-9 text-sm',
      md: 'px-3 h-11 text-base',
      lg: 'px-4 h-13 text-lg',
    };

    const errorStyles = error
      ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25'
      : '';

    return (
      <div className={cn(fullWidth ? 'w-full' : '')}>
        {label && (
          <label
            htmlFor={selectId}
            className={cn(
              'block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5',
              required && 'after:content-["*"] after:ml-0.5 after:text-error-500'
            )}
          >
            {label}
          </label>
        )}
        <select
          ref={ref}
          id={selectId}
          disabled={disabled}
          required={required}
          aria-invalid={error ? 'true' : 'false'}
          aria-describedby={describedBy}
          className={cn(baseStyles, variants[variant], sizes[size], errorStyles, className)}
          {...props}
        >
          {placeholder && (
            <option value="" disabled>
              {placeholder}
            </option>
          )}
          {options.map((option) => (
            <option key={option.value} value={option.value} disabled={option.disabled}>
              {option.label}
            </option>
          ))}
        </select>
        {error && (
          <p id={errorId} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
            {error}
          </p>
        )}
        {helperText && !error && (
          <p id={helperId} className="mt-1.5 text-sm text-neutral-500 dark:text-neutral-400">
            {helperText}
          </p>
        )}
      </div>
    );
  }
);

Select.displayName = 'Select';

export { Select };