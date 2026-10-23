import React from 'react';
import { cn } from '@/utils';

interface StatCardProps {
  label: string;
  value: string | number;
  change?: number;
  changeLabel?: string;
  trend?: 'up' | 'down' | 'neutral';
  icon?: React.ReactNode;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info' | 'secondary';
  className?: string;
}

const colorStyles = {
  primary: 'bg-primary-50 text-primary-600 border-primary-200',
  secondary: 'bg-warm-100 text-warm-600 border-warm-200',
  success: 'bg-success-50 text-success-700 border-success-200',
  warning: 'bg-warning-50 text-warning-700 border-warning-200',
  error: 'bg-error-50 text-error-700 border-error-200',
  info: 'bg-info-50 text-info-700 border-info-200',
};

const trendIcons = {
  up: (
    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 10l7-7m0 0l7 7m-7-7v18" />
    </svg>
  ),
  down: (
    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 14l-7 7m0 0l-7-7m7 7V5" />
    </svg>
  ),
  neutral: (
    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 12h14" />
    </svg>
  ),
};

export function StatCard({
  label,
  value,
  change,
  changeLabel,
  trend = 'neutral',
  icon,
  color = 'primary',
  className,
}: StatCardProps) {
  return (
    <div className={cn('p-6 rounded-xl border', colorStyles[color], className)}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-surface-600 dark:text-surface-400">{label}</p>
          <p className="mt-1 text-2xl font-bold text-surface-950 dark:text-surface-50">{value}</p>
        </div>
        {icon && <div className="p-2 rounded-lg bg-surface-100 dark:bg-surface-800">{icon}</div>}
      </div>
      {(change !== undefined || changeLabel) && (
        <div className="mt-3 flex items-center gap-1 text-sm">
          {trendIcons[trend]}
          <span className={cn('font-medium', trend === 'up' && 'text-success-600', trend === 'down' && 'text-error-600', trend === 'neutral' && 'text-surface-600')}>
            {change !== undefined ? (change >= 0 ? '+' : '') + `${change}%` : ''}
          </span>
          {changeLabel && <span className="text-sm text-surface-600 dark:text-surface-400">{changeLabel}</span>}
        </div>
      )}
    </div>
  );
}
