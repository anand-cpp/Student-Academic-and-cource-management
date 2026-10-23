import { Fragment, useEffect, useRef, ReactNode } from 'react';
import { createPortal } from 'react-dom';
import { cn } from '@/utils';
import { Button } from './Button';
import { XMarkIcon } from '@heroicons/react/24/outline';

export interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
  description?: string;
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full';
  children: ReactNode;
  footer?: ReactNode;
  closeOnOverlayClick?: boolean;
  closeOnEscape?: boolean;
  showCloseButton?: boolean;
}

export function Modal({
  isOpen,
  onClose,
  title,
  description,
  size = 'md',
  children,
  footer,
  closeOnOverlayClick = true,
  closeOnEscape = true,
  showCloseButton = true,
}: ModalProps) {
  const modalRef = useRef<HTMLDivElement>(null);
  const previousActiveElement = useRef<HTMLElement | null>(null);

  useEffect(() => {
    if (isOpen) {
      previousActiveElement.current = document.activeElement as HTMLElement;
      document.body.style.overflow = 'hidden';
      modalRef.current?.focus();
      
      const handleKeyDown = (event: KeyboardEvent) => {
        if (event.key === 'Escape' && closeOnEscape) {
          onClose();
        }
        if (event.key === 'Tab') {
          const focusableElements = modalRef.current?.querySelectorAll<HTMLElement>(
            'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
          );
          if (focusableElements && focusableElements.length > 0) {
            const firstElement = focusableElements[0];
            const lastElement = focusableElements[focusableElements.length - 1];
            
            if (event.shiftKey && document.activeElement === firstElement) {
              event.preventDefault();
              lastElement.focus();
            } else if (!event.shiftKey && document.activeElement === lastElement) {
              event.preventDefault();
              firstElement.focus();
            }
          }
        }
      };
      
      document.addEventListener('keydown', handleKeyDown);
      return () => {
        document.removeEventListener('keydown', handleKeyDown);
        document.body.style.overflow = '';
        previousActiveElement.current?.focus();
      };
    }
  }, [isOpen, closeOnEscape, onClose]);

  if (!isOpen) return null;

  const sizes = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
    xl: 'max-w-xl',
    full: 'max-w-[calc(100%-2rem)]',
  };

  const modalContent = (
    <div
      className="fixed inset-0 z-50 overflow-y-auto"
      role="dialog"
      aria-modal="true"
      aria-labelledby={title ? 'modal-title' : undefined}
      aria-describedby={description ? 'modal-description' : undefined}
    >
      <div className="flex min-h-full items-center justify-center p-4">
        <div
          onClick={(e) => {
            if (e.target === e.currentTarget && closeOnOverlayClick) {
              onClose();
            }
          }}
          className="fixed inset-0 bg-black/50 backdrop-blur-md transition-opacity"
          aria-hidden="true"
        />
        <div
          ref={modalRef}
          tabIndex={-1}
          className={cn(
            'relative w-full bg-surface-container-lowest rounded-2xl shadow-xl transform transition-all',
            'animate-scale-in',
            sizes[size]
          )}
        >
          {(title || showCloseButton) && (
            <div className="flex items-start justify-between px-6 py-4 border-b border-surface-200 dark:border-surface-800">
              <div>
                {title && (
                  <h2 id="modal-title" className="text-lg font-semibold text-surface-950 dark:text-surface-50">
                    {title}
                  </h2>
                )}
                {description && (
                  <p id="modal-description" className="mt-1 text-sm text-surface-600 dark:text-surface-400">
                    {description}
                  </p>
                )}
              </div>
              {showCloseButton && (
                <button
                  onClick={onClose}
                  className="p-1 rounded-lg text-surface-600 hover:text-surface-950 dark:text-surface-400 dark:hover:text-surface-300 hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500"
                  aria-label="Close modal"
                >
                  <XMarkIcon className="h-5 w-5" />
                </button>
              )}
            </div>
          )}
          <div className="px-6 py-4 max-h-[70vh] overflow-y-auto">
            {children}
          </div>
          {footer && (
            <div className="px-6 py-4 border-t border-surface-200 dark:border-surface-800 flex items-center justify-end gap-3">
              {footer}
            </div>
          )}
        </div>
      </div>
    </div>
  );

  if (typeof window === 'undefined') return null;
  
  return createPortal(modalContent, document.body);
}

export interface ConfirmDialogProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  title: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
  variant?: 'danger' | 'primary';
  loading?: boolean;
}

export function ConfirmDialog({
  isOpen,
  onClose,
  onConfirm,
  title,
  message,
  confirmText = 'Confirm',
  cancelText = 'Cancel',
  variant = 'danger',
  loading = false,
}: ConfirmDialogProps) {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      title={title}
      size="sm"
      footer={
        <>
          <Button variant="ghost" onClick={onClose} disabled={loading}>
            {cancelText}
          </Button>
          <Button variant={variant} onClick={onConfirm} loading={loading}>
            {confirmText}
          </Button>
        </>
      }
    >
      <p className="text-surface-600 dark:text-surface-400">{message}</p>
    </Modal>
  );
}
