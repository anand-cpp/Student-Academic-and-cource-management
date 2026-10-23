import { useEffect, useState, ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { cn } from '@/utils';
import { XMarkIcon, CheckCircleIcon, ExclamationTriangleIcon, InformationCircleIcon } from '@heroicons/react/24/outline';
import { useToast as useToastContext } from '@/contexts/ToastContext';

interface ToastProps {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message?: string;
  duration?: number;
  action?: {
    label: string;
    onClick: () => void;
  };
  onClose: (id: string) => void;
}

const icons = {
  success: CheckCircleIcon,
  error: ExclamationTriangleIcon,
  warning: ExclamationTriangleIcon,
  info: InformationCircleIcon,
};

const iconColors = {
  success: 'text-success-500',
  error: 'text-error-500',
  warning: 'text-warning-500',
  info: 'text-info-500',
};

const bgColors = {
  success: 'bg-success-50 dark:bg-success-900/20 border-success-200 dark:border-success-800',
  error: 'bg-error-50 dark:bg-error-900/20 border-error-200 dark:border-error-800',
  warning: 'bg-warning-50 dark:bg-warning-900/20 border-warning-200 dark:border-warning-800',
  info: 'bg-info-50 dark:bg-info-900/20 border-info-200 dark:border-info-800',
};

function ToastComponent({
  id,
  type,
  title,
  message,
  duration = 5000,
  action,
  onClose,
}: ToastProps) {
  const [visible, setVisible] = useState(true);
  const Icon = icons[type];

  useEffect(() => {
    if (duration !== 0) {
      const timer = setTimeout(() => {
        setVisible(false);
        setTimeout(() => onClose(id), 300);
      }, duration);
      return () => clearTimeout(timer);
    }
  }, [duration, id, onClose]);

  if (!visible) return null;

  const toast = (
    <div
      className={cn(
        'flex items-start gap-3 p-4 rounded-xl border shadow-lg',
        'animate-slide-in',
        'min-w-[320px] max-w-md',
        bgColors[type]
      )}
      role="alert"
      aria-live="polite"
    >
      <div className={cn('flex-shrink-0 mt-0.5', iconColors[type])}>
        <Icon className="h-5 w-5" aria-hidden="true" />
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-neutral-900 dark:text-neutral-50">{title}</p>
        {message && (
          <p className="mt-1 text-sm text-neutral-600 dark:text-neutral-400">{message}</p>
        )}
        {action && (
          <button
            onClick={action.onClick}
            className="mt-2 text-sm font-medium text-brand-600 hover:text-brand-700 dark:text-brand-400 dark:hover:text-brand-300 underline"
          >
            {action.label}
          </button>
        )}
      </div>
      <button
        onClick={() => {
          setVisible(false);
          setTimeout(() => onClose(id), 300);
        }}
        className="flex-shrink-0 p-1 rounded-lg text-neutral-400 hover:text-neutral-600 dark:hover:text-neutral-300 hover:bg-neutral-100 dark:hover:bg-neutral-800 transition-colors"
        aria-label="Dismiss"
      >
        <XMarkIcon className="h-5 w-5" />
      </button>
    </div>
  );

  if (typeof window === 'undefined') return null;
  
  return createPortal(toast, document.getElementById('toast-container') || document.body);
}

export function ToastContainer() {
  const { toasts, hideToast } = useToastContext();

  return (
    <div
      id="toast-container"
      className="fixed bottom-4 right-4 z-50 flex flex-col gap-3 pointer-events-none"
      aria-live="polite"
      aria-label="Notifications"
    >
      {toasts.map((toast) => (
        <div key={toast.id} className="pointer-events-auto">
          <ToastComponent {...toast} onClose={hideToast} />
        </div>
      ))}
    </div>
  );
}

export function useToast() {
  return useToastContext();
}