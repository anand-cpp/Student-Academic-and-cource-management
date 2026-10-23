import { cn } from '@/utils';
import { ReactNode } from 'react';
import { CheckIcon } from '@heroicons/react/24/outline';

export interface StepperProps {
  steps: Array<{
    label: string;
    description?: string;
    icon?: ReactNode;
    completed?: boolean;
    active?: boolean;
    error?: boolean;
  }>;
  currentStep: number;
  orientation?: 'horizontal' | 'vertical';
  className?: string;
}

export function Stepper({
  steps,
  currentStep,
  orientation = 'horizontal',
  className,
}: StepperProps) {
  const isVertical = orientation === 'vertical';

  return (
    <div className={cn('relative', className)} role="navigation" aria-label="Step progress">
      <div
        className={cn(
          'flex',
          isVertical ? 'flex-col items-start' : 'items-center'
        )}
      >
        {steps.map((step, index) => {
          const isCompleted = step.completed ?? index < currentStep;
          const isActive = step.active ?? index === currentStep;
          const isError = step.error;
          const isLast = index === steps.length - 1;

          const stepColor = isError
            ? 'bg-error-100 border-error-500 text-error-600'
            : isCompleted
            ? 'bg-brand-500 border-brand-500 text-white'
            : isActive
            ? 'bg-brand-100 border-brand-500 text-brand-700'
            : 'bg-white border-neutral-300 text-neutral-400 dark:border-neutral-600 dark:bg-neutral-800';

          return (
            <div key={step.label} className={cn('relative flex', isVertical ? 'flex-col items-start w-full' : 'flex-1')}>
              {!isLast && !isVertical && (
                <div className="absolute top-5 left-1/2 w-full h-1 -translate-x-1/2 -translate-y-1/2 z-0">
                  <div
                    className={cn(
                      'h-full rounded',
                      isCompleted ? 'bg-brand-500' : 'bg-neutral-200 dark:bg-neutral-700'
                    )}
                    style={{ width: `${((index + 1) / steps.length) * 100}%` }}
                  />
                </div>
              )}
              {!isLast && isVertical && (
                <div className="absolute top-10 left-5 w-1 h-full -translate-x-1/2 z-0">
                  <div
                    className={cn(
                      'h-full rounded',
                      isCompleted ? 'bg-brand-500' : 'bg-neutral-200 dark:bg-neutral-700'
                    )}
                  />
                </div>
              )}
              <div className={cn('relative z-10 flex', isVertical ? 'flex-row items-start gap-4 w-full' : 'flex-col items-center')}>
                <div
                  className={cn(
                    'flex items-center justify-center w-10 h-10 rounded-full border-2 text-sm font-medium transition-all',
                    stepColor
                  )}
                >
                  {isCompleted && !isError ? (
                    <CheckIcon className="h-5 w-5" />
                  ) : step.icon ? (
                    step.icon
                  ) : (
                    <span>{index + 1}</span>
                  )}
                </div>
                <div className={cn('min-w-0', isVertical ? 'flex-1' : 'mt-2 text-center')}>
                  <p className={cn(
                    'font-medium text-sm truncate',
                    isError ? 'text-error-600' : isActive ? 'text-brand-700 dark:text-brand-300' : 'text-neutral-900 dark:text-neutral-50'
                  )}>
                    {step.label}
                  </p>
                  {step.description && (
                    <p className="text-xs text-neutral-500 dark:text-neutral-400 truncate mt-0.5">
                      {step.description}
                    </p>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}