import { forwardRef, HTMLAttributes, ReactNode } from 'react';
import { cn } from '@/utils';
import { XMarkIcon, CheckCircleIcon, ExclamationTriangleIcon, InformationCircleIcon, XCircleIcon } from '@heroicons/react/24/outline';

export interface AlertProps extends HTMLAttributes<HTMLDivElement> {
  type?: 'success' | 'error' | 'warning' | 'info' | 'danger';
  title?: string;
  description?: string;
  dismissible?: boolean;
  onDismiss?: () => void;
  action?: ReactNode;
  icon?: boolean;
  className?: string;
}

const icons = {
  success: 'check-circle',
  error: 'exclamation-triangle',
  warning: 'exclamation-triangle',
  info: 'information-circle',
  danger: 'x-circle',
};

const iconColors = {
  success: 'text-success-500',
  error: 'text-error-500',
  warning: 'text-warning-500',
  info: 'text-info-500',
  danger: 'text-error-500',
};

const bgColors = {
  success: 'bg-success-50 dark:bg-success-900/20 border-success-200 dark:border-success-800',
  error: 'bg-error-50 dark:bg-error-900/20 border-error-200 dark:border-error-800',
  warning: 'bg-warning-50 dark:bg-warning-900/20 border-warning-200 dark:border-warning-800',
  info: 'bg-info-50 dark:bg-info-900/20 border-info-200 dark:border-info-800',
  danger: 'bg-error-50 dark:bg-error-900/20 border-error-200 dark:border-error-800',
};

const Alert = forwardRef<HTMLDivElement, AlertProps>(
  (
    {
      type = 'info',
      title,
      description,
      dismissible = false,
      onDismiss,
      action,
      icon = true,
      className,
      children,
      role = 'alert',
      ...props
    },
    ref
  ) => {
    return (
      <div
        ref={ref}
        role={role}
        className={cn(
          'flex gap-3 p-4 rounded-xl border',
          bgColors[type],
          className
        )}
        {...props}
      >
        {icon && (
          <div className={cn('flex-shrink-0 mt-0.5')}>
            <svg className={cn('h-5 w-5', iconColors[type])} fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={type === 'success' ? 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z' : type === 'error' || type === 'warning' ? 'M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z' : type === 'info' ? 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z' : 'M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m-2 2l2 2'} />
            </svg>
          </div>
        )}
        <div className="flex-1 min-w-0">
          {(title || description) && (
            <div>
              {title && (
                <h4 className={cn('font-medium text-sm', type === 'success' && 'text-success-800 dark:text-success-200', type === 'error' && 'text-error-800 dark:text-error-200', type === 'warning' && 'text-warning-800 dark:text-warning-200', type === 'info' && 'text-info-800 dark:text-info-200', type === 'danger' && 'text-error-800 dark:text-error-200')}>
                  {title}
                </h4>
              )}
              {description && (
                <p className={cn('mt-1 text-sm', type === 'success' && 'text-success-700 dark:text-success-300', type === 'error' && 'text-error-700 dark:text-error-300', type === 'warning' && 'text-warning-700 dark:text-warning-300', type === 'info' && 'text-info-700 dark:text-info-300', type === 'danger' && 'text-error-700 dark:text-error-300')}>
                  {description}
                </p>
              )}
            </div>
          )}
          {children && <div className="mt-2">{children}</div>}
          {action && <div className="mt-3">{action}</div>}
        </div>
        {dismissible && (
          <button
            type="button"
            onClick={onDismiss}
            className={cn(
              'flex-shrink-0 p-1 rounded-lg transition-colors',
              type === 'success' && 'text-success-500 hover:bg-success-100 dark:hover:bg-success-900/30',
              type === 'error' && 'text-error-500 hover:bg-error-100 dark:hover:bg-error-900/30',
              type === 'warning' && 'text-warning-500 hover:bg-warning-100 dark:hover:bg-warning-900/30',
              type === 'info' && 'text-info-500 hover:bg-info-100 dark:hover:bg-info-900/30',
              type === 'danger' && 'text-error-500 hover:bg-error-100 dark:hover:bg-error-900/30',
            )}
            aria-label="Dismiss"
          >
            <XMarkIcon className="h-5 w-5" />
          </button>
        )}
      </div>
    );
  }
);

Alert.displayName = 'Alert';

export { Alert };