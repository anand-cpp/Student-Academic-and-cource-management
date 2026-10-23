import { cn } from '@/utils';

export interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  showPageSize?: boolean;
  pageSize?: number;
  pageSizeOptions?: number[];
  onPageSizeChange?: (pageSize: number) => void;
  totalItems?: number;
  showFirstLast?: boolean;
  showPrevNext?: boolean;
  maxPageButtons?: number;
  className?: string;
}

export function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  showPageSize = false,
  pageSize,
  pageSizeOptions = [10, 25, 50, 100],
  onPageSizeChange,
  totalItems,
  showFirstLast = true,
  showPrevNext = true,
  maxPageButtons = 5,
  className,
}: PaginationProps) {
  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);
  
  let visiblePages: number[] = [];
  
  if (totalPages <= maxPageButtons) {
    visiblePages = pages;
  } else {
    const half = Math.floor(maxPageButtons / 2);
    let start = Math.max(1, currentPage - half);
    const end = Math.min(totalPages, start + maxPageButtons - 1);
    
    if (end - start + 1 < maxPageButtons) {
      start = Math.max(1, end - maxPageButtons + 1);
    }
    
    visiblePages = pages.slice(start - 1, end);
  }

  return (
    <nav
      className={cn('flex flex-wrap items-center justify-between gap-4 px-4 py-3 border-t border-surface-200 dark:border-surface-800', className)}
      aria-label="Pagination"
    >
      <div className="flex items-center gap-3">
        {totalItems !== undefined && (
          <span className="text-sm text-surface-600 dark:text-surface-400">
            Showing <strong>{(currentPage - 1) * (pageSize || 10) + 1}</strong> to <strong>{Math.min(currentPage * (pageSize || 10), totalItems)}</strong> of <strong>{totalItems}</strong> results
          </span>
        )}
        {showPageSize && pageSizeOptions.length > 1 && (
          <select
            value={pageSize || 10}
            onChange={(e) => onPageSizeChange?.(Number(e.target.value))}
            className="px-3 py-1.5 text-sm border border-surface-300 dark:border-surface-700 rounded-md bg-surface-container-lowest dark:bg-surface-900 text-surface-950 dark:text-surface-50 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary-500 transition-colors"
            aria-label="Items per page"
          >
            {pageSizeOptions.map((size) => (
              <option key={size} value={size}>
                {size} per page
              </option>
            ))}
          </select>
        )}
      </div>
      
      <div className="flex items-center gap-1">
        {showFirstLast && (
          <button
            onClick={() => onPageChange(1)}
            disabled={currentPage === 1}
            className="p-2 rounded-md border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="First page"
            title="First page"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 19l-7-7 7-7m8 14l-7-7 7-7" />
            </svg>
          </button>
        )}
        
        {showPrevNext && (
          <button
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 1}
            className="p-2 rounded-md border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Previous page"
            title="Previous page"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" /></svg>
          </button>
        )}
        
        {visiblePages[0] > 1 && (
          <>
            <button
              onClick={() => onPageChange(1)}
              className="px-3 py-1.5 text-sm font-medium rounded-md border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors"
              aria-label="Page 1"
            >
              1
            </button>
            {visiblePages[0] > 2 && (
              <span className="px-2 text-sm text-surface-400 dark:text-surface-500">…</span>
            )}
          </>
        )}
        
        {visiblePages.map((page) => (
          <button
            key={page}
            onClick={() => onPageChange(page)}
            className={cn(
              'px-3 py-1.5 text-sm font-medium rounded-md transition-colors',
              currentPage === page
                ? 'bg-primary-600 text-on-primary border-primary-600'
                : 'border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors'
            )}
            aria-label={`Page ${page}`}
            aria-current={currentPage === page ? 'page' : undefined}
          >
            {page}
          </button>
        ))}
        
        {visiblePages[visiblePages.length - 1] < totalPages && (
          <>
            {visiblePages[visiblePages.length - 1] < totalPages - 1 && (
              <span className="px-2 text-sm text-surface-400 dark:text-surface-500">…</span>
            )}
            <button
              onClick={() => onPageChange(totalPages)}
              className="px-3 py-1.5 text-sm font-medium rounded-md border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 transition-colors"
              aria-label={`Page ${totalPages}`}
            >
              {totalPages}
            </button>
          </>
        )}
        
        {showPrevNext && (
          <button
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="p-2 rounded-md border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Next page"
            title="Next page"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>
          </button>
        )}
        
        {showFirstLast && (
          <button
            onClick={() => onPageChange(totalPages)}
            disabled={currentPage === totalPages}
            className="p-2 rounded-md border border-surface-300 dark:border-surface-700 hover:bg-surface-100 dark:hover:bg-surface-800 disabled:opacity-50 disabled:cursor-not-allowed"
            aria-label="Last page"
            title="Last page"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 5l7 7-7 7M5 12l7 7-7 7" /></svg>
          </button>
        )}
      </div>
    </nav>
  );
}
