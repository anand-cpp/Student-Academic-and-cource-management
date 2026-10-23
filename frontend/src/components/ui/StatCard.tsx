import React from 'react';
import { cn } from '../../utils';

interface StatCardProps {
  label: string;
  value: string | number;
  change?: number;
  changeLabel?: string;
  trend?: 'up' | 'down' | 'neutral';
  icon?: React.ReactNode;
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info';
  className?: string;
}

const colorStyles = {
  primary: 'bg-primary-50 text-primary-700 border-primary-200',
  success: 'bg-green-50 text-green-700 border-green-200',
  warning: 'bg-amber-50 text-amber-700 border-amber-200',
  error: 'bg-red-50 text-red-700 border-red-200',
  info: 'bg-blue-50 text-blue-700 border-blue-200',
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
    <div className={cn('p-5 rounded-xl border', colorStyles[color], className)}>
      <div className="flex items-start justify-between">
        <div>
          <p className="text-sm font-medium text-gray-500">{label}</p>
          <p className="mt-1 text-2xl font-bold text-gray-900">{value}</p>
        </div>
        {icon && <div className="p-2 rounded-lg bg-white/50">{icon}</div>}
      </div>
      {(change !== undefined || changeLabel) && (
        <div className="mt-3 flex items-center gap-1 text-sm">
          {trendIcons[trend]}
          <span className={cn('font-medium', trend === 'up' && 'text-green-600', trend === 'down' && 'text-red-600', trend === 'neutral' && 'text-gray-500')}>
            {change !== undefined ? (change >= 0 ? '+' : '') + `${change}%` : ''}
          </span>
          {changeLabel && <span className="text-gray-500">{changeLabel}</span>}
        </div>
      )}
    </div>
  );
}