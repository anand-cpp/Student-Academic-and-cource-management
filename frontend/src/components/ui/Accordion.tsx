import { useState, useRef, useEffect, ReactNode } from 'react';
import { cn } from '@/utils';
import { ChevronDownIcon } from '@heroicons/react/24/outline';

export interface AccordionItemProps {
  value: string;
  title: string;
  description?: string;
  children: ReactNode;
  disabled?: boolean;
  className?: string;
}

export interface AccordionProps {
  items: AccordionItemProps[];
  value?: string | string[];
  onChange?: (value: string | string[]) => void;
  multiple?: boolean;
  variant?: 'default' | 'bordered' | 'filled';
  className?: string;
}

function AccordionItem({
  value,
  title,
  description,
  children,
  disabled = false,
  className,
  isOpen,
  onToggle,
}: AccordionItemProps & { isOpen: boolean; onToggle: () => void }) {
  const contentRef = useRef<HTMLDivElement>(null);
  const [contentHeight, setContentHeight] = useState(0);

  useEffect(() => {
    if (isOpen && contentRef.current) {
      setContentHeight(contentRef.current.scrollHeight);
    } else {
      setContentHeight(0);
    }
  }, [isOpen]);

  return (
    <div className={cn('border border-neutral-200 dark:border-neutral-700 rounded-lg overflow-hidden', className)}>
      <button
        type="button"
        onClick={onToggle}
        disabled={disabled}
        className={cn(
          'w-full flex items-center justify-between px-4 py-4 text-left transition-colors',
          'focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-500 focus-visible:ring-offset-2 focus-visible:ring-offset-white dark:focus-visible:ring-offset-neutral-900',
          disabled
            ? 'text-neutral-400 dark:text-neutral-500 cursor-not-allowed bg-neutral-50 dark:bg-neutral-800/50'
            : 'hover:bg-neutral-50 dark:hover:bg-neutral-800/50'
        )}
        aria-expanded={isOpen}
        aria-controls={`accordion-content-${value}`}
        id={`accordion-trigger-${value}`}
      >
        <div className="flex-1">
          <p className="font-medium text-neutral-900 dark:text-neutral-50 disabled:text-neutral-400">
            {title}
          </p>
          {description && (
            <p className="mt-0.5 text-sm text-neutral-500 dark:text-neutral-400">
              {description}
            </p>
          )}
        </div>
        <ChevronDownIcon
          className={cn(
            'h-5 w-5 text-neutral-400 flex-shrink-0 transition-transform duration-200',
            isOpen && 'rotate-180'
          )}
          aria-hidden="true"
        />
      </button>
      <div
        id={`accordion-content-${value}`}
        role="region"
        aria-labelledby={`accordion-trigger-${value}`}
        className="overflow-hidden transition-all duration-200 ease-out"
        style={{ maxHeight: isOpen ? contentHeight : 0 }}
      >
        <div
          ref={contentRef}
          className="px-4 pb-4 pt-0 text-neutral-600 dark:text-neutral-400"
        >
          {children}
        </div>
      </div>
    </div>
  );
}

export function Accordion({
  items,
  value = [],
  onChange,
  multiple = false,
  variant = 'default',
  className,
}: AccordionProps) {
  const [openValues, setOpenValues] = useState<string[]>(
    Array.isArray(value) ? value : value ? [value] : []
  );

  const handleToggle = (itemValue: string) => {
    const newOpenValues = openValues.includes(itemValue)
      ? openValues.filter((v) => v !== itemValue)
      : multiple
      ? [...openValues, itemValue]
      : [itemValue];
    
    setOpenValues(newOpenValues);
    onChange?.(multiple ? newOpenValues : newOpenValues[0]);
  };

  const variantClasses = {
    default: 'space-y-3',
    bordered: 'space-y-0 divide-y divide-neutral-200 dark:divide-neutral-700',
    filled: 'space-y-2',
  };

  return (
    <div className={cn(variantClasses[variant], className)}>
      {items.map((item) => (
        <AccordionItem
          key={item.value}
          value={item.value}
          title={item.title}
          description={item.description}
          disabled={item.disabled}
          isOpen={openValues.includes(item.value)}
          onToggle={() => handleToggle(item.value)}
        >
          {item.children}
        </AccordionItem>
      ))}
    </div>
  );
}