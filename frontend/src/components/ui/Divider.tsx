import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface DividerProps extends HTMLAttributes<HTMLHRElement> {
  orientation?: 'horizontal' | 'vertical';
  label?: string;
  labelPosition?: 'left' | 'center' | 'right';
  variant?: 'solid' | 'dashed' | 'dotted';
  thickness?: 'thin' | 'medium' | 'thick';
}

const Divider = forwardRef<HTMLHRElement, DividerProps>(
  (
    {
      orientation = 'horizontal',
      label,
      labelPosition = 'center',
      variant = 'solid',
      thickness = 'thin',
      className,
      children,
      ...props
    },
    ref
  ) => {
    const orientations = {
      horizontal: 'w-full',
      vertical: 'h-full',
    };

    const variants = {
      solid: 'border-solid',
      dashed: 'border-dashed',
      dotted: 'border-dotted',
    };

    const thicknesses = {
      thin: orientation === 'horizontal' ? 'border-t' : 'border-l',
      medium: orientation === 'horizontal' ? 'border-t-2' : 'border-l-2',
      thick: orientation === 'horizontal' ? 'border-t-4' : 'border-l-4',
    };

    const colors = 'border-neutral-200 dark:border-neutral-700';

    if (label || children) {
      return (
        <div
          ref={ref}
          className={cn(
            'flex items-center gap-4',
            orientation === 'horizontal' ? 'w-full' : 'h-full flex-col',
            className
          )}
          {...props}
        >
          {(orientation === 'horizontal' && labelPosition !== 'right') || (orientation === 'vertical' && labelPosition !== 'right') ? (
            <hr
              className={cn(
                'flex-1 border-0',
                thicknesses.thin,
                variants[variant],
                colors
              )}
              aria-hidden="true"
            />
          ) : null}
          <span
            className={cn(
              'text-sm font-medium text-neutral-500 dark:text-neutral-400 whitespace-nowrap flex-shrink-0',
              orientation === 'vertical' && 'rotate-90 origin-center'
            )}
            aria-hidden="true"
          >
            {label || children}
          </span>
          {(orientation === 'horizontal' && labelPosition !== 'left') || (orientation === 'vertical' && labelPosition !== 'left') ? (
            <hr
              className={cn(
                'flex-1 border-0',
                thicknesses.thin,
                variants[variant],
                colors
              )}
              aria-hidden="true"
            />
          ) : null}
        </div>
      );
    }

    return (
      <hr
        ref={ref}
        className={cn(
          'border-0',
          orientations[orientation],
          thicknesses[thickness],
          variants[variant],
          colors,
          className
        )}
        aria-hidden="true"
        {...props}
      />
    );
  }
);

Divider.displayName = 'Divider';

export { Divider };