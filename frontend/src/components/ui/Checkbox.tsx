import { forwardRef, InputHTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface CheckboxProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> {
  label?: string;
  description?: string;
  error?: string;
  variant?: 'default' | 'rounded';
  size?: 'sm' | 'md' | 'lg';
  indeterminate?: boolean;
}

const Checkbox = forwardRef<HTMLInputElement, CheckboxProps>(
  (
    {
      label,
      description,
      error,
      variant = 'default',
      size = 'md',
      indeterminate = false,
      className,
      id,
      disabled,
      required,
      'aria-describedby': ariaDescribedBy,
      ...props
    },
    ref
  ) => {
    const checkboxId = id || `checkbox-${Math.random().toString(36).substring(2, 9)}`;
    const errorId = error ? `${checkboxId}-error` : undefined;
    const describedBy = [description ? `${checkboxId}-desc` : undefined, errorId].filter(Boolean).join(' ') || undefined;

    const sizes = {
      sm: 'h-3.5 w-3.5',
      md: 'h-4 w-4',
      lg: 'h-5 w-5',
    };

    const baseStyles = `
      appearance-none
      border-2
      transition-colors duration-200
      cursor-pointer
      flex-shrink-0
      focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25
      disabled:opacity-50 disabled:cursor-not-allowed
      ${indeterminate ? 'bg-brand-500 border-brand-500' : 'bg-white border-neutral-300'}
      ${indeterminate ? 'hover:bg-brand-600 hover:border-brand-600' : 'hover:border-brand-400'}
    `;

    const variantStyles = variant === 'rounded' ? 'rounded-full' : 'rounded-sm';
    const checkedStyles = indeterminate ? '' : 'checked:bg-brand-500 checked:border-brand-500 checked:hover:bg-brand-600 checked:hover:border-brand-600';
    const errorStyles = error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '';

    return (
      <div className="flex items-start gap-3">
        <div className="relative flex items-center">
          <input
            ref={ref}
            type="checkbox"
            id={checkboxId}
            disabled={disabled}
            required={required}
            aria-invalid={error ? 'true' : 'false'}
            aria-describedby={describedBy}
            aria-checked={indeterminate ? 'mixed' : undefined}
            className={cn(baseStyles, variantStyles, sizes[size], checkedStyles, errorStyles, className)}
            {...props}
          />
          {indeterminate && (
            <div className="absolute inset-0 flex items-center justify-center pointer-events-none text-white">
              <svg className="h-2 w-2" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v2H7a1 1 0 100 2h2v2a1 1 0 102 0v-2h2a1 1 0 100-2h-2V7z" clipRule="evenodd" />
              </svg>
            </div>
          )}
        </div>
        <div className="flex-1 min-w-0 pt-0.5">
          {label && (
            <label
              htmlFor={checkboxId}
              className={cn(
                'block text-sm font-medium text-neutral-900 dark:text-neutral-50 cursor-pointer',
                disabled && 'opacity-50 cursor-not-allowed'
              )}
            >
              {label}
            </label>
          )}
          {description && (
            <p id={`${checkboxId}-desc`} className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">
              {description}
            </p>
          )}
          {error && (
            <p id={errorId} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
              {error}
            </p>
          )}
        </div>
      </div>
    );
  }
);

Checkbox.displayName = 'Checkbox';

export interface RadioProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> {
  label?: string;
  description?: string;
  error?: string;
  size?: 'sm' | 'md' | 'lg';
}

const Radio = forwardRef<HTMLInputElement, RadioProps>(
  (
    {
      label,
      description,
      error,
      size = 'md',
      className,
      id,
      disabled,
      required,
      'aria-describedby': ariaDescribedBy,
      ...props
    },
    ref
  ) => {
    const radioId = id || `radio-${Math.random().toString(36).substring(2, 9)}`;
    const errorId = error ? `${radioId}-error` : undefined;
    const describedBy = [description ? `${radioId}-desc` : undefined, errorId].filter(Boolean).join(' ') || undefined;

    const sizes = {
      sm: 'h-3.5 w-3.5',
      md: 'h-4 w-4',
      lg: 'h-5 w-5',
    };

    const baseStyles = `
      appearance-none
      border-2 border-neutral-300
      rounded-full
      transition-colors duration-200
      cursor-pointer
      flex-shrink-0
      focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25
      disabled:opacity-50 disabled:cursor-not-allowed
      bg-white
      checked:border-brand-500
      checked:after:content-[''] checked:after:block checked:after:w-1.5 checked:after:h-1.5 checked:after:bg-brand-500 checked:after:rounded-full checked:after:mt-[2px] checked:after:ml-[2px]
      hover:border-brand-400
    `;

    const errorStyles = error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '';

    return (
      <div className="flex items-start gap-3">
        <div className="relative flex items-center">
          <input
            ref={ref}
            type="radio"
            id={radioId}
            disabled={disabled}
            required={required}
            aria-invalid={error ? 'true' : 'false'}
            aria-describedby={describedBy}
            className={cn(baseStyles, sizes[size], errorStyles, className)}
            {...props}
          />
        </div>
        <div className="flex-1 min-w-0 pt-0.5">
          {label && (
            <label
              htmlFor={radioId}
              className={cn(
                'block text-sm font-medium text-neutral-900 dark:text-neutral-50 cursor-pointer',
                disabled && 'opacity-50 cursor-not-allowed'
              )}
            >
              {label}
            </label>
          )}
          {description && (
            <p id={`${radioId}-desc`} className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">
              {description}
            </p>
          )}
          {error && (
            <p id={errorId} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
              {error}
            </p>
          )}
        </div>
      </div>
    );
  }
);

Radio.displayName = 'Radio';

export interface RadioGroupProps {
  name: string;
  options: Array<{ value: string; label: string; description?: string; disabled?: boolean }>;
  value: string;
  onChange: (value: string) => void;
  label?: string;
  description?: string;
  error?: string;
  orientation?: 'vertical' | 'horizontal';
  required?: boolean;
}

export function RadioGroup({
  name,
  options,
  value,
  onChange,
  label,
  description,
  error,
  orientation = 'vertical',
  required,
}: RadioGroupProps) {
  const groupId = `radiogroup-${Math.random().toString(36).substring(2, 9)}`;
  const errorId = error ? `${groupId}-error` : undefined;
  const describedBy = [description ? `${groupId}-desc` : undefined, errorId].filter(Boolean).join(' ') || undefined;

  return (
    <fieldset className={cn('w-full', orientation === 'horizontal' ? 'flex flex-wrap gap-6' : 'space-y-3')} aria-describedby={describedBy}>
      {label && (
        <legend className={cn(
          'text-sm font-medium text-neutral-900 dark:text-neutral-50 mb-2',
          required && 'after:content-["*"] after:ml-0.5 after:text-error-500'
        )}>
          {label}
        </legend>
      )}
      {description && (
        <p id={`${groupId}-desc`} className="text-sm text-neutral-500 dark:text-neutral-400 mb-2">
          {description}
        </p>
      )}
      <div role="radiogroup" aria-required={required} aria-invalid={error ? 'true' : 'false'}>
        {options.map((option) => (
          <Radio
            key={option.value}
            name={name}
            value={option.value}
            checked={value === option.value}
            onChange={() => onChange(option.value)}
            disabled={option.disabled}
            label={option.label}
            description={option.description}
          />
        ))}
      </div>
      {error && (
        <p id={errorId} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
          {error}
        </p>
      )}
    </fieldset>
  );
}

export interface SwitchProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'size'> {
  label?: string;
  description?: string;
  error?: string;
  size?: 'sm' | 'md' | 'lg';
}

const Switch = forwardRef<HTMLInputElement, SwitchProps>(
  (
    {
      label,
      description,
      error,
      size = 'md',
      className,
      id,
      disabled,
      required,
      'aria-describedby': ariaDescribedBy,
      ...props
    },
    ref
  ) => {
    const switchId = id || `switch-${Math.random().toString(36).substring(2, 9)}`;
    const errorId = error ? `${switchId}-error` : undefined;
    const describedBy = [description ? `${switchId}-desc` : undefined, errorId].filter(Boolean).join(' ') || undefined;

    const sizes = {
      sm: 'h-5 w-9',
      md: 'h-6 w-11',
      lg: 'h-7 w-13',
    };

    const thumbSizes = {
      sm: 'h-4 w-4 translate-x-0.5',
      md: 'h-5 w-5 translate-x-0.5',
      lg: 'h-6 w-6 translate-x-0.5',
    };

    const thumbCheckedPositions = {
      sm: 'translate-x-4.5',
      md: 'translate-x-5.5',
      lg: 'translate-x-6.5',
    };

    const baseStyles = `
      relative
      inline-flex
      flex-shrink-0
      cursor-pointer
      rounded-full
      border-2
      transition-colors duration-200
      focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25
      disabled:opacity-50 disabled:cursor-not-allowed
      bg-neutral-300
      checked:bg-brand-500
      checked:border-brand-500
      hover:bg-neutral-400
      checked:hover:bg-brand-600
    `;

    const errorStyles = error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '';

    return (
      <div className="flex items-start gap-3">
        <div className="relative flex items-center">
          <input
            ref={ref}
            type="checkbox"
            role="switch"
            id={switchId}
            disabled={disabled}
            required={required}
            aria-invalid={error ? 'true' : 'false'}
            aria-describedby={describedBy}
            className={cn('sr-only peer', className)}
            {...props}
          />
          <div
            className={cn(baseStyles, sizes[size], errorStyles)}
            aria-hidden="true"
          >
            <div
              className={cn(
                'pointer-events-none inline-block rounded-full bg-white shadow-sm transform transition-transform duration-200 ease-out',
                thumbSizes[size],
                props.checked && thumbCheckedPositions[size]
              )}
            />
          </div>
        </div>
        <div className="flex-1 min-w-0 pt-1">
          {label && (
            <label
              htmlFor={switchId}
              className={cn(
                'block text-sm font-medium text-neutral-900 dark:text-neutral-50 cursor-pointer',
                disabled && 'opacity-50 cursor-not-allowed'
              )}
            >
              {label}
            </label>
          )}
          {description && (
            <p id={`${switchId}-desc`} className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">
              {description}
            </p>
          )}
          {error && (
            <p id={errorId} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
              {error}
            </p>
          )}
        </div>
      </div>
    );
  }
);

Switch.displayName = 'Switch';

export { Checkbox, Radio, Switch };