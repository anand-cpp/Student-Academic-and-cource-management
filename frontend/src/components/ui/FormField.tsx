import { InputHTMLAttributes, TextareaHTMLAttributes, SelectHTMLAttributes, LabelHTMLAttributes, ReactNode } from 'react';
import { cn } from '@/utils';
import { Controller, ControllerProps, FieldPath, FieldValues, UseFormRegister } from 'react-hook-form';

export interface FormFieldProps<T extends FieldValues = FieldValues> extends Omit<ControllerProps<T>, 'render'> {
  name: FieldPath<T>;
  label: string;
  type?: 'text' | 'email' | 'password' | 'number' | 'tel' | 'url' | 'date' | 'datetime-local' | 'time' | 'textarea' | 'select' | 'checkbox' | 'radio' | 'switch' | 'file';
  placeholder?: string;
  required?: boolean;
  disabled?: boolean;
  readOnly?: boolean;
  options?: Array<{ value: string; label: string; disabled?: boolean }>;
  helperText?: string;
  error?: string;
  className?: string;
  register?: UseFormRegister<T>;
  control?: any;
  inputClassName?: string;
  labelClassName?: string;
  wrapperClassName?: string;
  leftIcon?: ReactNode;
  rightIcon?: ReactNode;
  variant?: 'default' | 'filled' | 'outlined';
  size?: 'sm' | 'md' | 'lg';
}

export function FormField<T extends FieldValues = FieldValues>({
  name,
  label,
  type = 'text',
  placeholder,
  required,
  disabled,
  readOnly,
  options,
  helperText,
  error,
  className,
  register,
  control,
  inputClassName,
  labelClassName,
  wrapperClassName,
  leftIcon,
  rightIcon,
  variant = 'default',
  size = 'md',
  rules,
  ...props
}: FormFieldProps<T>) {
  const hasControl = !!control;

  const renderInput = (field: any, fieldState: any) => {
    const inputProps = {
      ...field,
      ...props,
      disabled: disabled || field.disabled,
      readOnly: readOnly || field.readOnly,
      'aria-invalid': error || fieldState.invalid,
      'aria-describedby': [helperText ? `${name}-helper` : null, error || fieldState.error ? `${name}-error` : null].filter(Boolean).join(' ') || undefined,
    };

    switch (type) {
      case 'textarea':
        return (
          <textarea
            {...inputProps}
            className={cn(
              'w-full font-sans rounded-md transition-colors text-neutral-900 dark:text-neutral-50 bg-white dark:bg-neutral-900 placeholder:text-neutral-400 resize-y min-h-[80px]',
              'disabled:opacity-50 disabled:cursor-not-allowed',
              'focus-visible:outline-none focus-visible:ring-3',
              variant === 'default' && 'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
              variant === 'filled' && 'bg-neutral-100 border border-transparent hover:bg-neutral-200 focus:bg-white focus:border-brand-500 focus:ring-brand-500/25 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:focus:bg-neutral-900',
              variant === 'outlined' && 'border-2 border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
              error || fieldState.error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '',
              size === 'sm' && 'px-3 py-2 text-sm',
              size === 'md' && 'px-3 py-3 text-base',
              size === 'lg' && 'px-4 py-4 text-lg',
              inputClassName
            )}
          />
        );
      case 'select':
        return (
          <select
            {...inputProps}
            className={cn(
              'w-full font-sans rounded-md transition-colors text-neutral-900 dark:text-neutral-50 bg-white dark:bg-neutral-900 disabled:opacity-50 disabled:cursor-not-allowed',
              'focus-visible:outline-none focus-visible:ring-3',
              'appearance-none bg-[url("data:image/svg+xml,%3csvg xmlns=\'http://www.w3.org/2000/svg\' fill=\'none\' viewBox=\'0 0 20 20\'%3e%3cpath stroke=\'%236b7280\' stroke-linecap=\'round\' stroke-linejoin=\'round\' stroke-width=\'1.5\' d=\'M6 8l4 4 4-4\'/%3e%3c/svg%3e")] bg-[length:1.5rem_1.5rem] bg-[right_0.5rem_center] bg-no-repeat pr-10',
              variant === 'default' && 'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
              variant === 'filled' && 'bg-neutral-100 border border-transparent hover:bg-neutral-200 focus:bg-white focus:border-brand-500 focus:ring-brand-500/25 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:focus:bg-neutral-900',
              variant === 'outlined' && 'border-2 border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
              error || fieldState.error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '',
              size === 'sm' && 'px-3 h-9 text-sm',
              size === 'md' && 'px-3 h-11 text-base',
              size === 'lg' && 'px-4 h-13 text-lg',
              inputClassName
            )}
          >
            {placeholder && <option value="" disabled>{placeholder}</option>}
            {options?.map((option) => (
              <option key={option.value} value={option.value} disabled={option.disabled}>
                {option.label}
              </option>
            ))}
          </select>
        );
      case 'checkbox':
        return (
          <div className="flex items-start gap-3">
            <input
              type="checkbox"
              {...inputProps}
              className={cn(
                'h-4 w-4 appearance-none border-2 rounded-sm transition-colors cursor-pointer flex-shrink-0',
                'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25',
                'disabled:opacity-50 disabled:cursor-not-allowed',
                'bg-white border-neutral-300 checked:bg-brand-500 checked:border-brand-500 checked:hover:bg-brand-600 checked:hover:border-brand-600 hover:border-brand-400'
              )}
            />
            <label className="mt-0.5 text-sm font-medium text-neutral-900 dark:text-neutral-50 cursor-pointer">
              {label}
            </label>
          </div>
        );
      case 'radio':
        return (
          <div className="flex items-start gap-3">
            <input
              type="radio"
              {...inputProps}
              className={cn(
                'h-4 w-4 appearance-none border-2 border-neutral-300 rounded-full transition-colors cursor-pointer flex-shrink-0',
                'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25',
                'disabled:opacity-50 disabled:cursor-not-allowed',
                'bg-white checked:border-brand-500 checked:after:content-[""] checked:after:block checked:after:w-1.5 checked:after:h-1.5 checked:after:bg-brand-500 checked:after:rounded-full checked:after:mt-[2px] checked:after:ml-[2px] hover:border-brand-400'
              )}
            />
            <label className="mt-0.5 text-sm font-medium text-neutral-900 dark:text-neutral-50 cursor-pointer">
              {label}
            </label>
          </div>
        );
      case 'switch':
        return (
          <div className="flex items-start gap-3">
            <label className="relative flex items-center cursor-pointer">
              <input
                type="checkbox"
                role="switch"
                {...inputProps}
                className="sr-only peer"
              />
              <div className={cn(
                'relative inline-flex flex-shrink-0 cursor-pointer rounded-full border-2 transition-colors',
                'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25',
                'disabled:opacity-50 disabled:cursor-not-allowed',
                'bg-neutral-300 checked:bg-brand-500 checked:border-brand-500 hover:bg-neutral-400 checked:hover:bg-brand-600',
                size === 'sm' && 'h-5 w-9',
                size === 'md' && 'h-6 w-11',
                size === 'lg' && 'h-7 w-13',
              )}>
                <div className={cn(
                  'pointer-events-none inline-block rounded-full bg-white shadow-sm transform transition-transform duration-200 ease-out',
                  size === 'sm' && 'h-4 w-4 translate-x-0.5',
                  size === 'md' && 'h-5 w-5 translate-x-0.5',
                  size === 'lg' && 'h-6 w-6 translate-x-0.5',
                  field.value && (size === 'sm' && 'translate-x-4.5'),
                  field.value && (size === 'md' && 'translate-x-5.5'),
                  field.value && (size === 'lg' && 'translate-x-6.5'),
                )} />
              </div>
            </label>
            <span className="mt-1 text-sm font-medium text-neutral-900 dark:text-neutral-50">{label}</span>
          </div>
        );
      case 'file':
        return (
          <div className="border-2 border-dashed border-neutral-300 dark:border-neutral-600 rounded-lg p-6 text-center transition-colors hover:border-brand-500">
            <input
              type="file"
              {...inputProps}
              className="sr-only"
              onChange={(e) => field.onChange(e.target.files)}
            />
            <svg className="mx-auto h-12 w-12 text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
            </svg>
            <p className="mt-2 text-sm text-neutral-600 dark:text-neutral-400">
              {placeholder || 'Drag and drop a file here, or click to select'}
            </p>
          </div>
        );
      default:
        return (
          <div className="relative">
            {leftIcon && (
              <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none text-neutral-400">
                {leftIcon}
              </div>
            )}
            <input
              {...inputProps}
              type={type}
              className={cn(
                'w-full font-sans rounded-md transition-colors text-neutral-900 dark:text-neutral-50 bg-white dark:bg-neutral-900 placeholder:text-neutral-400',
                'disabled:opacity-50 disabled:cursor-not-allowed',
                'focus-visible:outline-none focus-visible:ring-3',
                variant === 'default' && 'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
                variant === 'filled' && 'bg-neutral-100 border border-transparent hover:bg-neutral-200 focus:bg-white focus:border-brand-500 focus:ring-brand-500/25 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:focus:bg-neutral-900',
                variant === 'outlined' && 'border-2 border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
                error || fieldState.error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '',
                size === 'sm' && 'px-3 h-9 text-sm',
                size === 'md' && 'px-3 h-11 text-base',
                size === 'lg' && 'px-4 h-13 text-lg',
                leftIcon && (size === 'sm' && 'pl-9'),
                leftIcon && (size === 'md' && 'pl-10'),
                leftIcon && (size === 'lg' && 'pl-11'),
                rightIcon && (size === 'sm' && 'pr-9'),
                rightIcon && (size === 'md' && 'pr-10'),
                rightIcon && (size === 'lg' && 'pr-11'),
                inputClassName
              )}
            />
            {rightIcon && (
              <div className="absolute inset-y-0 right-0 flex items-center pr-3 pointer-events-none text-neutral-400">
                {rightIcon}
              </div>
            )}
          </div>
        );
    }
  };

  if (hasControl) {
    return (
      <div className={cn('w-full', wrapperClassName, className)}>
        <Controller
          name={name}
          control={control}
          rules={rules}
          render={({ field, fieldState }) => (
            <>
              <label htmlFor={name} className={cn('block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5', labelClassName)}>
                {label}
              </label>
              {renderInput(field, fieldState)}
              {helperText && !error && !fieldState.error && (
                <p id={`${name}-helper`} className="mt-1.5 text-sm text-neutral-500 dark:text-neutral-400">
                  {helperText}
                </p>
              )}
              {(error || fieldState.error) && (
                <p id={`${name}-error`} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
                  {error || fieldState.error?.message}
                </p>
              )}
            </>
          )}
        />
      </div>
    );
  }

  return (
    <div className={cn('w-full', wrapperClassName, className)}>
      <label htmlFor={name} className={cn('block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5', labelClassName)}>
        {label}
      </label>
      <input
        {...register?.(name, rules as any)}
        type={type}
        id={name}
        placeholder={placeholder}
        disabled={disabled}
        readOnly={readOnly}
        required={required}
        aria-invalid={!!error}
        aria-describedby={[helperText ? `${name}-helper` : null, error ? `${name}-error` : null].filter(Boolean).join(' ') || undefined}
        className={cn(
          'w-full font-sans rounded-md transition-colors text-neutral-900 dark:text-neutral-50 bg-white dark:bg-neutral-900 placeholder:text-neutral-400',
          'disabled:opacity-50 disabled:cursor-not-allowed',
          'focus-visible:outline-none focus-visible:ring-3',
          variant === 'default' && 'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
          variant === 'filled' && 'bg-neutral-100 border border-transparent hover:bg-neutral-200 focus:bg-white focus:border-brand-500 focus:ring-brand-500/25 dark:bg-neutral-800 dark:hover:bg-neutral-700 dark:focus:bg-neutral-900',
          variant === 'outlined' && 'border-2 border-neutral-300 hover:border-neutral-400 focus:border-brand-500 focus:ring-brand-500/25 dark:border-neutral-600 dark:hover:border-neutral-500',
          error ? 'border-error-500 focus:border-error-500 focus:ring-error-500/25' : '',
          size === 'sm' && 'px-3 h-9 text-sm',
          size === 'md' && 'px-3 h-11 text-base',
          size === 'lg' && 'px-4 h-13 text-lg',
          leftIcon && (size === 'sm' && 'pl-9'),
          leftIcon && (size === 'md' && 'pl-10'),
          leftIcon && (size === 'lg' && 'pl-11'),
          rightIcon && (size === 'sm' && 'pr-9'),
          rightIcon && (size === 'md' && 'pr-10'),
          rightIcon && (size === 'lg' && 'pr-11'),
          inputClassName
        )}
      />
      {helperText && !error && (
        <p id={`${name}-helper`} className="mt-1.5 text-sm text-neutral-500 dark:text-neutral-400">
          {helperText}
        </p>
      )}
      {error && (
        <p id={`${name}-error`} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}

export interface FormSectionProps {
  title?: string;
  description?: string;
  children: ReactNode;
  className?: string;
  divider?: boolean;
}

export function FormSection({ title, description, children, className, divider = false }: FormSectionProps) {
  return (
    <fieldset className={cn('space-y-4', divider && 'pt-6 border-t border-neutral-200 dark:border-neutral-700', className)}>
      {(title || description) && (
        <div className="mb-4">
          {title && <legend className="text-lg font-semibold text-neutral-900 dark:text-neutral-50">{title}</legend>}
          {description && <p className="mt-1 text-sm text-neutral-500 dark:text-neutral-400">{description}</p>}
        </div>
      )}
      {children}
    </fieldset>
  );
}

export interface DatePickerProps {
  label?: string;
  name?: string;
  value?: Date | string;
  onChange?: (date: Date | undefined) => void;
  placeholder?: string;
  disabled?: boolean;
  required?: boolean;
  error?: string;
  helperText?: string;
  minDate?: Date;
  maxDate?: Date;
  showTime?: boolean;
  className?: string;
}

export function DatePicker({
  label,
  name,
  value,
  onChange,
  placeholder,
  disabled,
  required,
  error,
  helperText,
  minDate,
  maxDate,
  showTime = false,
  className,
}: DatePickerProps) {
  const inputId = name || `datepicker-${Math.random().toString(36).substring(2, 9)}`;
  const formattedValue = value ? (value instanceof Date ? value.toISOString().split('T')[0] : value.split('T')[0]) : '';

  return (
    <div className={className}>
      {label && (
        <label htmlFor={inputId} required={required} className="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5">
          {label}
        </label>
      )}
      <input
        type={showTime ? 'datetime-local' : 'date'}
        id={inputId}
        value={formattedValue}
        onChange={(e) => onChange?.(e.target.value ? new Date(e.target.value) : undefined)}
        min={minDate?.toISOString().split('T')[0]}
        max={maxDate?.toISOString().split('T')[0]}
        placeholder={placeholder}
        disabled={disabled}
        required={required}
        aria-invalid={!!error}
        aria-describedby={[helperText ? `${inputId}-helper` : null, error ? `${inputId}-error` : null].filter(Boolean).join(' ') || undefined}
        className={cn(
          'w-full px-3 h-11 text-base font-sans rounded-md transition-colors text-neutral-900 dark:text-neutral-50 bg-white dark:bg-neutral-900',
          'disabled:opacity-50 disabled:cursor-not-allowed',
          'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand-500/25',
          'border border-neutral-300 hover:border-neutral-400 focus:border-brand-500 dark:border-neutral-600 dark:hover:border-neutral-500',
          error && 'border-error-500 focus:border-error-500 focus:ring-error-500/25'
        )}
      />
      {error && <p id={`${inputId}-error`} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">{error}</p>}
      {helperText && !error && <p id={`${inputId}-helper`} className="mt-1.5 text-sm text-neutral-500 dark:text-neutral-400">{helperText}</p>}
    </div>
  );
}

export interface FileUploadProps {
  label?: string;
  name?: string;
  value?: FileList | File | null;
  onChange?: (files: FileList | null) => void;
  accept?: string;
  multiple?: boolean;
  maxSize?: number;
  maxFiles?: number;
  disabled?: boolean;
  required?: boolean;
  error?: string;
  helperText?: string;
  className?: string;
}

export function FileUpload({
  label,
  name,
  value,
  onChange,
  accept,
  multiple = false,
  maxSize = 10 * 1024 * 1024,
  maxFiles = 1,
  disabled,
  required,
  error,
  helperText,
  className,
}: FileUploadProps) {
  const inputId = name || `fileupload-${Math.random().toString(36).substring(2, 9)}`;
  const [dragActive, setDragActive] = useState(false);

  const handleDrag = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const files = multiple ? e.dataTransfer.files : e.dataTransfer.files[0];
      onChange?.(multiple ? e.dataTransfer.files : e.dataTransfer.files[0] ? [e.dataTransfer.files[0]] : null);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      onChange?.(multiple ? e.target.files : e.target.files[0] ? [e.target.files[0]] : null);
    } else {
      onChange?.(null);
    }
  };

  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  };

  const filesArray = value ? (multiple ? Array.from(value) : [value as File]) : [];

  return (
    <div className={className}>
      {label && <label htmlFor={inputId} required={required} className="block text-sm font-medium text-neutral-700 dark:text-neutral-300 mb-1.5">{label}</label>}
      <div
        onDragEnter={handleDrag}
        onDragLeave={handleDrag}
        onDragOver={handleDrag}
        onDrop={handleDrop}
        className={cn(
          'relative border-2 border-dashed rounded-lg p-6 text-center transition-colors',
          dragActive
            ? 'border-brand-500 bg-brand-50 dark:bg-brand-900/20'
            : 'border-neutral-300 dark:border-neutral-600 hover:border-brand-500',
          disabled && 'opacity-50 cursor-not-allowed'
        )}
      >
        <input
          type="file"
          id={inputId}
          accept={accept}
          multiple={multiple}
          disabled={disabled}
          required={required}
          onChange={handleChange}
          className="sr-only"
          aria-describedby={[helperText ? `${inputId}-helper` : null, error ? `${inputId}-error` : null].filter(Boolean).join(' ') || undefined}
        />
        <svg className="mx-auto h-12 w-12 text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
        </svg>
        <p className="mt-2 text-sm text-neutral-600 dark:text-neutral-400">
          {dragActive ? 'Drop files here...' : 'Drag and drop files here, or click to select'}
        </p>
        <p className="mt-1 text-xs text-neutral-500 dark:text-neutral-400">
          {accept ? `Accepted: ${accept}` : 'All file types accepted'} • Max size: {formatFileSize(maxSize)} • Max files: {maxFiles}
        </p>
      </div>
      {filesArray.length > 0 && (
        <div className="mt-3 space-y-2">
          {filesArray.map((file, index) => (
            <div key={index} className="flex items-center justify-between p-3 bg-neutral-50 dark:bg-neutral-800 rounded-lg border border-neutral-200 dark:border-neutral-700">
              <div className="flex items-center gap-3">
                <svg className="h-6 w-6 text-neutral-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15.172 7l-6.586 6.586a.5.5 0 00.707.708l6.586-6.586a.5.5 0 00-.707-.708z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
                <div>
                  <p className="text-sm font-medium text-neutral-900 dark:text-neutral-50 truncate max-w-[200px]">{file.name}</p>
                  <p className="text-xs text-neutral-500 dark:text-neutral-400">{formatFileSize(file.size)}</p>
                </div>
              </div>
              <button
                type="button"
                onClick={() => onChange?.(multiple ? new DataTransfer().files : null)}
                className="text-neutral-400 hover:text-error-600 dark:hover:text-error-400 transition-colors"
                aria-label="Remove file"
              >
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>
          ))}
        </div>
      )}
      {error && <p id={`${inputId}-error`} className="mt-1.5 text-sm text-error-600 dark:text-error-400" role="alert">{error}</p>}
      {helperText && !error && <p id={`${inputId}-helper`} className="mt-1.5 text-sm text-neutral-500 dark:text-neutral-400">{helperText}</p>}
    </div>
  );
}

import { useState } from 'react';