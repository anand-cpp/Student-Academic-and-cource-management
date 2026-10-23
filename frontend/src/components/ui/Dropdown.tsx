import { useState, useRef, useEffect, ReactNode, Fragment } from 'react';
import { createPortal } from 'react-dom';
import { cn } from '@/utils';
import { ChevronDownIcon } from '@heroicons/react/24/outline';

export interface DropdownItem {
  label: string;
  value?: string;
  icon?: ReactNode;
  disabled?: boolean;
  danger?: boolean;
  divider?: boolean;
  onClick?: () => void;
}

export interface DropdownProps {
  trigger: ReactNode;
  items: DropdownItem[];
  align?: 'left' | 'right';
  width?: 'auto' | 'trigger' | number;
  closeOnSelect?: boolean;
  className?: string;
}

export function Dropdown({
  trigger,
  items,
  align = 'left',
  width = 'auto',
  closeOnSelect = true,
  className,
}: DropdownProps) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const triggerRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        if (triggerRef.current && !triggerRef.current.contains(event.target as Node)) {
          setIsOpen(false);
        }
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (isOpen) {
      const handleKeyDown = (event: KeyboardEvent) => {
        if (event.key === 'Escape') {
          setIsOpen(false);
          triggerRef.current?.focus();
        }
      };
      document.addEventListener('keydown', handleKeyDown);
      return () => document.removeEventListener('keydown', handleKeyDown);
    }
  }, [isOpen]);

  const handleItemClick = (item: DropdownItem) => {
    item.onClick?.();
    if (closeOnSelect && !item.divider && !item.disabled) {
      setIsOpen(false);
    }
  };

  const dropdownContent = (
    <Fragment>
      <button
        ref={triggerRef}
        type="button"
        onClick={() => setIsOpen(!isOpen)}
        className="relative"
        aria-haspopup="true"
        aria-expanded={isOpen}
        aria-label="Open dropdown menu"
      >
        {trigger}
      </button>
      {isOpen && (
        <div
          ref={dropdownRef}
          className={cn(
            'absolute z-50 mt-2 min-w-[160px] bg-white dark:bg-neutral-900 rounded-lg border border-neutral-200 dark:border-neutral-700 shadow-lg',
            'animate-fade-in animate-slide-down',
            align === 'right' ? 'right-0' : 'left-0',
            width === 'trigger' ? 'w-full' : width !== 'auto' ? `w-[${width}px]` : '',
            className
          )}
          role="menu"
          tabIndex={-1}
        >
          {items.map((item, index) => (
            item.divider ? (
              <div key={`${index}-divider`} className="h-px bg-neutral-200 dark:bg-neutral-700 my-1" role="separator" />
            ) : (
              <button
                key={`${index}-${item.value || item.label}`}
                type="button"
                onClick={() => handleItemClick(item)}
                disabled={item.disabled}
                className={cn(
                  'w-full px-4 py-2 text-left text-sm transition-colors',
                  'focus-visible:outline-none focus-visible:bg-neutral-100 dark:focus-visible:bg-neutral-800',
                  item.disabled
                    ? 'text-neutral-400 dark:text-neutral-500 cursor-not-allowed'
                    : item.danger
                    ? 'text-error-600 hover:bg-error-50 dark:hover:bg-error-900/20'
                    : 'text-neutral-900 dark:text-neutral-50 hover:bg-neutral-100 dark:hover:bg-neutral-800'
                )}
                role="menuitem"
                tabIndex={-1}
                aria-disabled={item.disabled}
              >
                <div className="flex items-center gap-3">
                  {item.icon && <span className="flex-shrink-0 h-5 w-5">{item.icon}</span>}
                  <span className="truncate">{item.label}</span>
                </div>
              </button>
            )
          ))}
        </div>
      )}
    </Fragment>
  );

  if (typeof window === 'undefined') return <>{trigger}</>;
  
  return createPortal(dropdownContent, document.body);
}

export interface DropdownMenuProps {
  isOpen: boolean;
  onClose: () => void;
  items: DropdownItem[];
  align?: 'left' | 'right';
  className?: string;
}

export function DropdownMenu({
  isOpen,
  onClose,
  items,
  align = 'left',
  className,
}: DropdownMenuProps) {
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (isOpen) {
      const handleKeyDown = (event: KeyboardEvent) => {
        if (event.key === 'Escape') {
          onClose();
        }
      };
      document.addEventListener('keydown', handleKeyDown);
      menuRef.current?.focus();
      return () => document.removeEventListener('keydown', handleKeyDown);
    }
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  const menuContent = (
    <div
      ref={menuRef}
      className={cn(
        'fixed z-50 min-w-[160px] bg-white dark:bg-neutral-900 rounded-lg border border-neutral-200 dark:border-neutral-700 shadow-lg',
        'animate-fade-in animate-slide-down',
        className
      )}
      role="menu"
      tabIndex={-1}
      style={{
        top: 0,
        left: 0,
        [align === 'right' ? 'right' : 'left']: 0,
      }}
    >
      {items.map((item, index) => (
        item.divider ? (
          <div key={`${index}-divider`} className="h-px bg-neutral-200 dark:bg-neutral-700 my-1" role="separator" />
        ) : (
          <button
            key={`${index}-${item.value || item.label}`}
            type="button"
            onClick={() => {
              item.onClick?.();
              onClose();
            }}
            disabled={item.disabled}
            className={cn(
              'w-full px-4 py-2 text-left text-sm transition-colors',
              'focus-visible:outline-none focus-visible:bg-neutral-100 dark:focus-visible:bg-neutral-800',
              item.disabled
                ? 'text-neutral-400 dark:text-neutral-500 cursor-not-allowed'
                : item.danger
                ? 'text-error-600 hover:bg-error-50 dark:hover:bg-error-900/20'
                : 'text-neutral-900 dark:text-neutral-50 hover:bg-neutral-100 dark:hover:bg-neutral-800'
            )}
            role="menuitem"
            tabIndex={-1}
            aria-disabled={item.disabled}
          >
            <div className="flex items-center gap-3">
              {item.icon && <span className="flex-shrink-0 h-5 w-5">{item.icon}</span>}
              <span className="truncate">{item.label}</span>
            </div>
          </button>
        )
      ))}
    </div>
  );

  if (typeof window === 'undefined') return null;
  
  return createPortal(menuContent, document.body);
}