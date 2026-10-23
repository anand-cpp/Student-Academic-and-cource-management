import { ReactNode } from 'react';
import { cn } from '@/utils';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ChevronRightIcon } from '@heroicons/react/24/outline';

export interface BreadcrumbItem {
  label: string;
  href?: string;
  isCurrent?: boolean;
  onClick?: () => void;
}

export interface BreadcrumbsProps {
  items: BreadcrumbItem[];
  separator?: ReactNode;
  className?: string;
  maxItems?: number;
}

export function Breadcrumbs({
  items,
  separator = <ChevronRightIcon className="h-4 w-4 text-neutral-400 flex-shrink-0" />,
  className,
  maxItems = 5,
}: BreadcrumbsProps) {
  const location = useLocation();
  const navigate = useNavigate();

  const handleClick = (href: string) => {
    if (href.startsWith('/')) {
      navigate(href);
    } else {
      window.location.href = href;
    }
  };

  let displayItems = items;
  if (items.length > maxItems) {
    const firstItems = items.slice(0, 2);
    const lastItems = items.slice(-(maxItems - 3));
    displayItems = [
      ...firstItems,
      { label: '...', isCurrent: false },
      ...lastItems,
    ];
  }

  return (
    <nav className={cn('flex items-center gap-2 flex-wrap text-sm', className)} aria-label="Breadcrumb">
      <ol className="flex items-center gap-2 flex-wrap">
        {displayItems.map((item, index) => (
          <li key={`${item.label}-${index}`} className="flex items-center gap-2">
            {index > 0 && (
              <span className="text-neutral-400 flex-shrink-0" aria-hidden="true">
                {separator}
              </span>
            )}
            {item.isCurrent || index === displayItems.length - 1 ? (
              <span
                className={cn(
                  'font-medium text-neutral-900 dark:text-neutral-50 truncate',
                  item.isCurrent && 'aria-current="page"'
                )}
                aria-current={item.isCurrent || index === displayItems.length - 1 ? 'page' : undefined}
              >
                {item.label}
              </span>
            ) : item.href ? (
              <Link
                to={item.href}
                onClick={(e) => {
                  e.preventDefault();
                  handleClick(item.href!);
                }}
                className="text-neutral-500 hover:text-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-200 transition-colors truncate max-w-[150px]"
              >
                {item.label}
              </Link>
            ) : item.onClick ? (
              <button
                type="button"
                onClick={item.onClick}
                className="text-neutral-500 hover:text-neutral-700 dark:text-neutral-400 dark:hover:text-neutral-200 transition-colors truncate max-w-[150px] bg-none border-none p-0 text-left"
              >
                {item.label}
              </button>
            ) : (
              <span className="text-neutral-500 truncate max-w-[150px]">{item.label}</span>
            )}
          </li>
        ))}
      </ol>
    </nav>
  );
}

export function useBreadcrumbs(pathname: string, routeLabels: Record<string, string>): BreadcrumbItem[] {
  const segments = pathname.split('/').filter(Boolean);
  const breadcrumbs: BreadcrumbItem[] = [{ label: 'Home', href: '/' }];

  let currentPath = '';
  segments.forEach((segment, index) => {
    currentPath += `/${segment}`;
    const isLast = index === segments.length - 1;
    const label = routeLabels[segment] || segment.replace(/-/g, ' ').replace(/\b\w/g, (l) => l.toUpperCase());
    
    breadcrumbs.push({
      label,
      href: isLast ? undefined : currentPath,
      isCurrent: isLast,
    });
  });

  return breadcrumbs;
}