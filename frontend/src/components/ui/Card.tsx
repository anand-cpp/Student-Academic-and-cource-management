import { forwardRef, HTMLAttributes } from 'react';
import { cn } from '@/utils';

export interface CardProps extends HTMLAttributes<HTMLDivElement> {
  variant?: 'default' | 'elevated' | 'outlined' | 'filled' | 'interactive' | 'glass';
  padding?: 'none' | 'sm' | 'md' | 'lg';
}

export const Card = forwardRef<HTMLDivElement, CardProps>(
  ({ children, variant = 'default', padding = 'md', className, ...props }, ref) => {
    const variants = {
      default: 'bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm',
      elevated: 'bg-white dark:bg-slate-900 border border-slate-200/60 dark:border-slate-800/80 shadow-md hover:shadow-lg transition-shadow duration-200',
      outlined: 'bg-transparent border border-slate-300 dark:border-slate-700',
      filled: 'bg-slate-100/80 dark:bg-slate-800/60 border border-transparent',
      interactive: 'bg-white dark:bg-slate-900 border border-slate-200/80 dark:border-slate-800 shadow-sm hover:shadow-md hover:border-brand-500/50 dark:hover:border-brand-400/50 hover:-translate-y-0.5 transition-all duration-200 cursor-pointer',
      glass: 'glass-panel shadow-sm',
    };

    const paddings = {
      none: '',
      sm: 'p-3 sm:p-4',
      md: 'p-4 sm:p-6',
      lg: 'p-6 sm:p-8',
    };

    return (
      <div
        ref={ref}
        className={cn('rounded-2xl transition-colors', variants[variant], paddings[padding], className)}
        {...props}
      >
        {children}
      </div>
    );
  }
);

Card.displayName = 'Card';

export type CardHeaderProps = HTMLAttributes<HTMLDivElement>;

export const CardHeader = forwardRef<HTMLDivElement, CardHeaderProps>(
  ({ children, className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn('mb-4 flex flex-col space-y-1.5', className)}
      {...props}
    >
      {children}
    </div>
  )
);

CardHeader.displayName = 'CardHeader';

export type CardTitleProps = HTMLAttributes<HTMLHeadingElement>;

export const CardTitle = forwardRef<HTMLHeadingElement, CardTitleProps>(
  ({ children, className, ...props }, ref) => (
    <h3
      ref={ref}
      className={cn('text-lg font-bold tracking-tight text-slate-900 dark:text-slate-50', className)}
      {...props}
    >
      {children}
    </h3>
  )
);

CardTitle.displayName = 'CardTitle';

export type CardDescriptionProps = HTMLAttributes<HTMLParagraphElement>;

export const CardDescription = forwardRef<HTMLParagraphElement, CardDescriptionProps>(
  ({ children, className, ...props }, ref) => (
    <p
      ref={ref}
      className={cn('text-sm text-slate-500 dark:text-slate-400', className)}
      {...props}
    >
      {children}
    </p>
  )
);

CardDescription.displayName = 'CardDescription';

export type CardContentProps = HTMLAttributes<HTMLDivElement>;

export const CardContent = forwardRef<HTMLDivElement, CardContentProps>(
  ({ children, className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn('', className)}
      {...props}
    >
      {children}
    </div>
  )
);

CardContent.displayName = 'CardContent';

export type CardFooterProps = HTMLAttributes<HTMLDivElement>;

export const CardFooter = forwardRef<HTMLDivElement, CardFooterProps>(
  ({ children, className, ...props }, ref) => (
    <div
      ref={ref}
      className={cn('mt-5 flex items-center justify-end gap-3 pt-3 border-t border-slate-100 dark:border-slate-800', className)}
      {...props}
    >
      {children}
    </div>
  )
);

CardFooter.displayName = 'CardFooter';

export default Card;
