import { forwardRef, InputHTMLAttributes, TextareaHTMLAttributes, LabelHTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface InputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> {
  label?: string;
  error?: string;
  helperText?: string;
  leftIcon?: React.ReactNode;
  rightIcon?: React.ReactNode;
  variant?: 'default' | 'filled' | 'outlined';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      label,
      error,
      helperText,
      leftIcon,
      rightIcon,
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
    const inputId = id || `input-${Math.random().toString(36).substring(2, 9)}`;
    const helperId = helperText ? `${inputId}-helper` : undefined;
    const errorId = error ? `${inputId}-error` : undefined;
    const describedBy = [helperId, errorId].filter(Boolean).join(' ') || undefined;

    const baseStyles = `
      w-full
      font-sans
      rounded-md
      transition-colors duration-200
      text-neutral-900 dark:text-neutral-50
      bg-white dark:bg-neutral-900
      placeholder:text-neutral-400
      disabled:opacity-50 disabled:cursor-not-allowed
      focus-visible:outline-none focus-visible:ring-3
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

    const iconPadding = {
      sm: leftIcon ? 'pl-9' : rightIcon ? 'pr-9' : '',
      md: leftIcon ? 'pl-10' : rightIcon ? 'pr-10' : '',
      lg: leftIcon ? 'pl-11' : rightIcon ? 'pr-11' : '',
    };

    const errorStyles = error
      ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25'
      : '';

    return (
      <div className={cn(fullWidth ? 'w-full' : '', 'relative')}>
        {label && (
          <label
            htmlFor={inputId}
            className={cn(
              'block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5',
              required && 'after:content-["*"] after:ml-0.5 after:text-error-500'
            )}
          >
            {label}
          </label>
        )}
        <div className="relative">
          {leftIcon && (
            <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-neutral-400">
              {leftIcon}
            </div>
          )}
          <input
            ref={ref}
            id={inputId}
            disabled={disabled}
            required={required}
            aria-invalid={error ? 'true' : 'false'}
            aria-describedby={describedBy}
            className={cn(
              baseStyles,
              variants[variant],
              sizes[size],
              iconPadding[size],
              errorStyles,
              className
            )}
            {...props}
          />
          {rightIcon && (
            <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-neutral-400">
              {rightIcon}
            </div>
          )}
        </div>
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

Input.displayName = 'Input';

export interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  helperText?: string;
  variant?: 'default' | 'filled' | 'outlined';
  size?: 'sm' | 'md' | 'lg';
  fullWidth?: boolean;
}

const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  (
    {
      label,
      error,
      helperText,
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
    const textareaId = id || `textarea-${Math.random().toString(36).substring(2, 9)}`;
    const helperId = helperText ? `${textareaId}-helper` : undefined;
    const errorId = error ? `${textareaId}-error` : undefined;
    const describedBy = [helperId, errorId].filter(Boolean).join(' ') || undefined;

    const baseStyles = `
      w-full
      font-sans
      rounded-md
      transition-colors duration-200
      text-neutral-900 dark:text-neutral-50
      bg-white dark:bg-neutral-900
      placeholder:text-neutral-400
      resize-y min-h-[80px]
      disabled:opacity-50 disabled:cursor-not-allowed
      focus-visible:outline-none focus-visible:ring-3
    `;

    const variants = {
      default: 'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
      filled: 'bg-neutral-100 border border-transparent hover:bg-neutral-200 focus:bg-white focus:border-brand-500 focus:ring-brand-500/25 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:focus:bg-neutral-900',
      outlined: 'border-2 border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
    };

    const sizes = {
      sm: 'px-3 py-2 text-sm',
      md: 'px-3 py-3 text-base',
      lg: 'px-4 py-4 text-lg',
    };

    const errorStyles = error
      ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25'
      : '';

    return (
      <div className={cn(fullWidth ? 'w-full' : '')}>
        {label && (
          <label
            htmlFor={textareaId}
            className={cn(
              'block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5',
              required && 'after:content-["*"] after:ml-0.5 after:text-error-500'
            )}
          >
            {label}
          </label>
        )}
        <textarea
          ref={ref}
          id={textareaId}
          disabled={disabled}
          required={required}
          aria-invalid={error ? 'true' : 'false'}
          aria-describedby={describedBy}
          className={cn(baseStyles, variants[variant], sizes[size], errorStyles, className)}
          {...props}
        />
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

Textarea.displayName = 'Textarea';

export interface LabelProps extends LabelHTMLAttributes<HTMLLabelElement> {
  required?: boolean;
}

const Label = forwardRef<HTMLLabelElement, LabelProps>(
  ({ children, required, className, ...props }, ref) => (
    <label
      ref={ref}
      className={cn(
        'block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5',
        required && 'after:content-["*"] after:ml-0.5 after:text-error-500',
        className
      )}
      {...props}
    >
      {children}
    </label>
  )
);

Label.displayName = 'Label';

export { Input, Textarea, Label };