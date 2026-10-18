import React, { useEffect, Fragment } from 'react'
import { createPortal } from 'react-dom'
import { XMarkIcon } from '@heroicons/react/24/outline'

interface ModalProps {
  isOpen: boolean
  onClose: () => void
  title?: string
  description?: string
  children: React.ReactNode
  size?: 'sm' | 'md' | 'lg' | 'xl' | 'full'
  showCloseButton?: boolean
  closeOnOverlayClick?: boolean
  closeOnEscape?: boolean
}

export function Modal({
  isOpen,
  onClose,
  title,
  description,
  children,
  size = 'md',
  showCloseButton = true,
  closeOnOverlayClick = true,
  closeOnEscape = true,
}: ModalProps) {
  const overlayRef = React.useRef<HTMLDivElement>(null)
  const contentRef = React.useRef<HTMLDivElement>(null)
  const previousActiveElement = React.useRef<HTMLElement | null>(null)

  useEffect(() => {
    if (isOpen) {
      previousActiveElement.current = document.activeElement as HTMLElement
      document.body.style.overflow = 'hidden'
      contentRef.current?.focus()
    } else {
      document.body.style.overflow = ''
      previousActiveElement.current?.focus()
    }

    return () => {
      document.body.style.overflow = ''
    }
  }, [isOpen])

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      if (!isOpen) return
      if (event.key === 'Escape' && closeOnEscape) {
        onClose()
      }
      if (event.key === 'Tab') {
        const focusableElements = contentRef.current?.querySelectorAll<HTMLElement>(
          'button, [href], input, select, textarea, [tabindex]:not([tabindex="-1"])'
        )
        if (focusableElements && focusableElements.length > 0) {
          const firstElement = focusableElements[0]
          const lastElement = focusableElements[focusableElements.length - 1]
          if (event.shiftKey && document.activeElement === firstElement) {
            event.preventDefault()
            lastElement.focus()
          } else if (!event.shiftKey && document.activeElement === lastElement) {
            event.preventDefault()
            firstElement.focus()
          }
        }
      }
    }

    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, closeOnEscape])

  if (!isOpen) return null

  const sizeClasses = {
    sm: 'max-w-sm',
    md: 'max-w-md',
    lg: 'max-w-lg',
    xl: 'max-w-xl',
    full: 'max-w-4xl',
  }

  const modalContent = (
    <Fragment>
      <div
        ref={overlayRef}
        className="fixed inset-0 bg-black/50 transition-opacity z-40"
        onClick={closeOnOverlayClick ? onClose : undefined}
        aria-hidden="true"
      />
      <div
        ref={contentRef}
        className={`fixed inset-0 z-50 overflow-y-auto flex items-center justify-center p-4 ${sizeClasses[size]}`}
        tabIndex={-1}
        role="dialog"
        aria-modal="true"
        aria-labelledby={title ? 'modal-title' : undefined}
        aria-describedby={description ? 'modal-description' : undefined}
      >
        <div className="relative w-full bg-white rounded-xl shadow-xl transform transition-all">
          {(title || showCloseButton) && (
            <div className="flex items-start justify-between p-6 border-b border-gray-100">
              <div>
                {title && (
                  <h2 id="modal-title" className="text-lg font-semibold text-gray-900">
                    {title}
                  </h2>
                )}
              </div>
              {showCloseButton && (
                <button
                  type="button"
                  onClick={onClose}
                  className="text-gray-400 hover:text-gray-500 focus:outline-none focus:ring-2 focus:ring-primary-500 rounded-lg p-1"
                  aria-label="Close modal"
                >
                  <XMarkIcon className="w-5 h-5" />
                </button>
              )}
            </div>
          )}
          <div className="p-6 max-h-[calc(100vh-12rem)] overflow-y-auto">
            {children}
          </div>
        </div>
      </div>
    </Fragment>
  )

  return createPortal(modalContent, document.body)
}

const sizeClasses: Record<string, string> = {
  sm: 'max-w-sm',
  md: 'max-w-md',
  lg: 'max-w-lg',
  xl: 'max-w-xl',
  full: 'max-w-4xl',
}

interface ConfirmDialogProps {
  isOpen: boolean
  onClose: () => void
  onConfirm: () => void
  title: string
  message: string
  confirmText?: string
  cancelText?: string
  variant?: 'danger' | 'primary' | 'warning'
  loading?: boolean
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
  const variantClasses = {
    danger: 'btn-danger',
    primary: 'btn-primary',
    warning: 'bg-yellow-600 text-white hover:bg-yellow-700',
  }

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={title} size="sm">
      <div className="space-y-4">
        <p className="text-gray-600">{message}</p>
        <div className="flex justify-end gap-3 pt-4">
          <button onClick={onClose} className="btn-secondary" disabled={loading}>
            {cancelText}
          </button>
          <button
            onClick={onConfirm}
            className={variantClasses[variant]}
            disabled={loading}
          >
            {loading ? 'Processing...' : confirmText}
          </button>
        </div>
      </div>
    </Modal>
  )
}