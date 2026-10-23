import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatDate(date: Date | string, options?: Intl.DateTimeFormatOptions): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    ...options,
  });
}

export function formatDateTime(date: Date | string): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function formatTime(date: Date | string): string {
  const d = typeof date === 'string' ? new Date(date) : date;
  return d.toLocaleTimeString('en-US', {
    hour: '2-digit',
    minute: '2-digit',
  });
}

export function truncate(str: string, length: number): string {
  if (str.length <= length) return str;
  return str.slice(0, length - 3) + '...';
}

export function capitalize(str: string): string {
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

export function slugify(str: string): string {
  return str
    .toLowerCase()
    .trim()
    .replace(/[^\w\s-]/g, '')
    .replace(/[\s_-]+/g, '-')
    .replace(/^-+|-+$/g, '');
}

export function generateId(prefix = ''): string {
  return `${prefix}${Math.random().toString(36).substring(2, 9)}`;
}

export function debounce<T extends (...args: unknown[]) => unknown>(
  func: T,
  wait: number
): (...args: Parameters<T>) => void {
  let timeout: ReturnType<typeof setTimeout> | null = null;
  return (...args: Parameters<T>) => {
    if (timeout) clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  };
}

export function throttle<T extends (...args: unknown[]) => unknown>(
  func: T,
  limit: number
): (...args: Parameters<T>) => void {
  let inThrottle = false;
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  };
}

export function groupBy<T>(array: T[], key: keyof T | ((item: T) => string)): Record<string, T[]> {
  return array.reduce((groups, item) => {
    const groupKey = typeof key === 'function' ? key(item) : String(item[key]);
    if (!groups[groupKey]) groups[groupKey] = [];
    groups[groupKey].push(item);
    return groups;
  }, {} as Record<string, T[]>);
}

function compareValues(a: unknown, b: unknown): number {
  if (a == null && b == null) return 0;
  if (a == null) return -1;
  if (b == null) return 1;
  
  if (typeof a === 'number' && typeof b === 'number') {
    return a - b;
  }
  if (typeof a === 'string' && typeof b === 'string') {
    return a.localeCompare(b);
  }
  if (a instanceof Date && b instanceof Date) {
    return a.getTime() - b.getTime();
  }
  
  return String(a).localeCompare(String(b));
}

export function sortBy<T>(array: T[], key: keyof T | ((item: T) => unknown), direction: 'asc' | 'desc' = 'asc'): T[] {
  const sorted = [...array].sort((a, b) => {
    const aVal = typeof key === 'function' ? key(a) : (a as Record<string, unknown>)[key as string];
    const bVal = typeof key === 'function' ? key(b) : (b as Record<string, unknown>)[key as string];
    const result = compareValues(aVal, bVal);
    return direction === 'asc' ? result : -result;
  });
  return sorted;
}

export function uniqueBy<T>(array: T[], key: keyof T | ((item: T) => unknown)): T[] {
  const seen = new Set<unknown>();
  return array.filter((item) => {
    const val = typeof key === 'function' ? key(item) : (item as Record<string, unknown>)[key as string];
    if (seen.has(val)) return false;
    seen.add(val);
    return true;
  });
}

export function chunk<T>(array: T[], size: number): T[][] {
  const chunks: T[][] = [];
  for (let i = 0; i < array.length; i += size) {
    chunks.push(array.slice(i, i + size));
  }
  return chunks;
}

export function omit<T extends Record<string, unknown>, K extends keyof T>(obj: T, keys: K[]): Omit<T, K> {
  const result = { ...obj };
  keys.forEach((key) => delete result[key]);
  return result;
}

export function pick<T extends Record<string, unknown>, K extends keyof T>(obj: T, keys: K[]): Pick<T, K> {
  const result = {} as Pick<T, K>;
  keys.forEach((key) => {
    if (key in obj) result[key] = obj[key];
  });
  return result;
}

export * from './routes';
