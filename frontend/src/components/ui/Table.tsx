import { HTMLAttributes, useState, useMemo, ReactNode } from 'react';
import { cn } from '@/utils';
import { Select } from './Select';

export interface Column<T = unknown> {
  key: string;
  header: string;
  accessor?: keyof T | ((row: T) => unknown) | string;
  sortable?: boolean;
  filterable?: boolean;
  filterOptions?: Array<{ value: string; label: string }>;
  render?: (value: unknown, row: T, index: number) => ReactNode;
  width?: string;
  minWidth?: string;
  maxWidth?: string;
  align?: 'left' | 'center' | 'right';
  className?: string;
  headerClassName?: string;
  cellClassName?: string;
}

export interface TableProps<T = unknown> extends HTMLAttributes<HTMLTableElement> {
  columns: Column<T>[];
  data: T[];
  keyAccessor: keyof T | ((row: T) => string);
  sortable?: boolean;
  filterable?: boolean;
  pagination?: boolean;
  pageSize?: number;
  pageSizeOptions?: number[];
  selection?: boolean;
  onSelectionChange?: (selectedKeys: string[]) => void;
  loading?: boolean;
  emptyMessage?: string;
  loadingMessage?: string;
  striped?: boolean;
  hoverable?: boolean;
  bordered?: boolean;
  compact?: boolean;
  className?: string;
}

function TableContainer<T>({
  children,
  className,
  ...props
}: HTMLAttributes<HTMLDivElement> & { children: ReactNode }) {
  return (
    <div className={cn('overflow-x-auto rounded-xl border border-neutral-200 dark:border-neutral-700', className)} {...props}>
      {children}
    </div>
  );
}

function TableHeader({
  columns,
  sortBy,
  sortOrder,
  onSort,
  filters,
  onFilterChange,
  selection,
  onSelectAll,
  compact,
  className,
}: {
  columns: Column[];
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
  onSort?: (key: string) => void;
  filters?: Record<string, unknown>;
  onFilterChange?: (key: string, value: unknown) => void;
  selection?: boolean;
  onSelectAll?: (checked: boolean) => void;
  compact?: boolean;
  className?: string;
}) {
  return (
    <thead className={cn('bg-neutral-50 dark:bg-neutral-800/50 border-b border-neutral-200 dark:border-neutral-700', className)}>
      <tr>
        {selection && (
          <th className={cn('px-4 py-3', compact && 'py-2')}>
            <input
              type="checkbox"
              checked={false}
              onChange={(e) => onSelectAll?.(e.target.checked)}
              className="h-4 w-4 rounded border-neutral-300 text-brand-600 focus:ring-brand-500"
              aria-label="Select all rows"
            />
          </th>
        )}
        {columns.map((column) => (
          <th
            key={column.key}
            scope="col"
            style={{
              width: column.width,
              minWidth: column.minWidth,
              maxWidth: column.maxWidth,
              textAlign: column.align,
            }}
            className={cn(
              'px-4 py-3 text-left font-semibold text-xs text-neutral-500 dark:text-neutral-400 uppercase tracking-wider',
              'whitespace-nowrap',
              column.sortable && 'cursor-pointer select-none hover:bg-neutral-100 dark:hover:bg-neutral-800',
              compact && 'py-2',
              column.headerClassName
            )}
            onClick={() => column.sortable && onSort?.(column.key)}
          >
            <div className="flex items-center gap-2">
              <span>{column.header}</span>
              {column.sortable && sortBy === column.key && (
                <svg
                  className={cn('w-4 h-4 flex-shrink-0', sortOrder === 'desc' && 'rotate-180')}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                  aria-hidden="true"
                >
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 15l7-7 7 7" />
                </svg>
              )}
            </div>
            {column.filterable && column.filterOptions && (
              <Select
                value={String(filters?.[column.key] || '')}
                onChange={(value) => onFilterChange?.(column.key, value)}
                options={[{ value: '', label: 'All' }, ...column.filterOptions]}
                placeholder="Filter"
                size="sm"
                className="mt-2 w-full"
                aria-label={`Filter ${column.header}`}
              />
            )}
          </th>
        ))}
      </tr>
    </thead>
  );
}

function TableBody<T>({
  columns,
  data,
  keyAccessor,
  selection,
  selectedKeys,
  onSelectionChange,
  onRowClick,
  renderRow,
  striped,
  hoverable,
  compact,
  emptyMessage,
  className,
}: {
  columns: Column[];
  data: T[];
  keyAccessor: keyof T | ((row: T) => string);
  selection?: boolean;
  selectedKeys?: string[];
  onSelectionChange?: (key: string, checked: boolean) => void;
  onRowClick?: (row: T) => void;
  renderRow?: (row: T, index: number) => ReactNode;
  striped?: boolean;
  hoverable?: boolean;
  compact?: boolean;
  emptyMessage?: string;
  className?: string;
}) {
  const getKey = (row: T) => (typeof keyAccessor === 'function' ? keyAccessor(row) : String(row[keyAccessor]));

  if (data.length === 0) {
    return (
      <tbody className={cn(className)}>
        <tr>
          <td colSpan={columns.length + (selection ? 1 : 0)} className="px-4 py-12 text-center text-neutral-500 dark:text-neutral-400">
            {emptyMessage || 'No data available'}
          </td>
        </tr>
      </tbody>
    );
  }

  return (
    <tbody className={cn('divide-y divide-neutral-200 dark:divide-neutral-700', className)}>
      {data.map((row, rowIndex) => {
        const rowKey = getKey(row);
        const isSelected = selectedKeys?.includes(rowKey);

        return (
          <tr
            key={rowKey}
            className={cn(
              'transition-colors',
              isSelected && 'bg-brand-50 dark:bg-brand-900/20',
              striped && rowIndex % 2 === 1 && 'bg-neutral-50 dark:bg-neutral-800/30',
              hoverable && 'hover:bg-neutral-50 dark:hover:bg-neutral-800/30',
              onRowClick && 'cursor-pointer'
            )}
            onClick={() => onRowClick?.(row)}
          >
            {selection && (
              <td className={cn('px-4 py-3', compact && 'py-2')}>
                <input
                  type="checkbox"
                  checked={isSelected}
                  onChange={(e) => onSelectionChange?.(rowKey, e.target.checked)}
                  className="h-4 w-4 rounded border-neutral-300 text-brand-600 focus:ring-brand-500"
                  aria-label={`Select row ${rowIndex + 1}`}
                />
              </td>
            )}
            {columns.map((column) => {
              const value = column.accessor
                ? typeof column.accessor === 'function'
                  ? column.accessor(row)
                  : (row as Record<string, unknown>)[column.accessor as string]
                : undefined;
              const rendered = column.render ? column.render(value, row, rowIndex) : value;

              return (
                <td
                  key={column.key}
                  style={{
                    width: column.width,
                    minWidth: column.minWidth,
                    maxWidth: column.maxWidth,
                    textAlign: column.align,
                  }}
                  className={cn(
                    'px-4 py-3 text-sm text-neutral-900 dark:text-neutral-50',
                    'whitespace-nowrap',
                    compact && 'py-2',
                    column.cellClassName
                  )}
                >
                  {rendered == null || (typeof rendered === 'object' && Object.keys(rendered).length === 0) ? '—' : rendered}
                </td>
              );
            })}
          </tr>
        );
      })}
    </tbody>
  );
}

function TableFooter({
  columns,
  pagination,
  page,
  pageCount,
  pageSize,
  pageSizeOptions,
  onPageChange,
  onPageSizeChange,
  total,
  className,
}: {
  columns: Column[];
  pagination?: boolean;
  page: number;
  pageCount: number;
  pageSize: number;
  pageSizeOptions?: number[];
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (pageSize: number) => void;
  total: number;
  className?: string;
}) {
  if (!pagination) return null;

  return (
    <tfoot className={cn('bg-neutral-50 dark:bg-neutral-800/50 border-t border-neutral-200 dark:border-neutral-700', className)}>
      <tr>
        <td colSpan={columns.length} className="px-4 py-3">
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center gap-3">
              <span className="text-sm text-neutral-600 dark:text-neutral-400">
                Showing <strong>{(page - 1) * pageSize + 1}</strong> to <strong>{Math.min(page * pageSize, total)}</strong> of <strong>{total}</strong> results
              </span>
              {pageSizeOptions && pageSizeOptions.length > 1 && (
                <Select
                  value={String(pageSize)}
                  onChange={(value) => onPageSizeChange?.(Number(value))}
                  options={pageSizeOptions.map((opt) => ({ value: String(opt), label: `${opt} per page` }))}
                  size="sm"
                  className="w-auto"
                  aria-label="Rows per page"
                />
              )}
            </div>
            <div className="flex items-center gap-2">
              <button
                onClick={() => onPageChange?.(1)}
                disabled={page === 1}
                className="p-2 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 disabled:opacity-50 disabled:cursor-not-allowed"
                aria-label="First page"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 19l-7-7 7-7m8 14l-7-7 7-7" /></svg>
              </button>
              <button
                onClick={() => onPageChange?.(page - 1)}
                disabled={page === 1}
                className="p-2 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 disabled:opacity-50 disabled:cursor-not-allowed"
                aria-label="Previous page"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" /></svg>
              </button>
              <span className="px-3 text-sm text-neutral-700 dark:text-neutral-300">
                Page <strong>{page}</strong> of <strong>{pageCount}</strong>
              </span>
              <button
                onClick={() => onPageChange?.(page + 1)}
                disabled={page === pageCount}
                className="p-2 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 disabled:opacity-50 disabled:cursor-not-allowed"
                aria-label="Next page"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>
              </button>
              <button
                onClick={() => onPageChange?.(pageCount)}
                disabled={page === pageCount}
                className="p-2 rounded-md border border-neutral-300 dark:border-neutral-600 hover:bg-neutral-100 dark:hover:bg-neutral-700 disabled:opacity-50 disabled:cursor-not-allowed"
                aria-label="Last page"
              >
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 5l7 7-7 7M5 12l7 7-7 7" /></svg>
              </button>
            </div>
          </div>
        </td>
      </tr>
    </tfoot>
  );
}

function TableLoading({
  columns,
  rows = 5,
  compact,
}: {
  columns: Column[];
  rows?: number;
  compact?: boolean;
}) {
  return (
    <tbody>
      {Array.from({ length: rows }).map((_, i) => (
        <tr key={i} className={cn('animate-pulse', compact && 'py-2')}>
          {columns.map((column) => (
            <td key={column.key} className={cn('px-4 py-3', compact && 'py-2')}>
              <div className="h-4 bg-neutral-200 dark:bg-neutral-700 rounded w-3/4 animate-pulse" />
            </td>
          ))}
        </tr>
      ))}
    </tbody>
  );
}

function Table<T = unknown>({
  columns,
  data,
  keyAccessor,
  sortable = true,
  filterable = false,
  pagination = true,
  pageSize: initialPageSize = 10,
  pageSizeOptions = [10, 25, 50, 100],
  selection = false,
  onSelectionChange,
  loading = false,
  emptyMessage,
  loadingMessage,
  striped = true,
  hoverable = true,
  bordered = true,
  compact = false,
  className,
  ...props
}: TableProps<T>) {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(initialPageSize);
  const [sortBy, setSortBy] = useState<string | undefined>();
  const [sortOrder, setSortOrder] = useState<'asc' | 'desc'>('asc');
  const [filters, setFilters] = useState<Record<string, unknown>>({});
  const [selectedKeys, setSelectedKeys] = useState<string[]>([]);

  const getKey = (row: T) => (typeof keyAccessor === 'function' ? keyAccessor(row) : String(row[keyAccessor]));

  const handleSort = (key: string) => {
    if (!sortable) return;
    if (sortBy === key) {
      setSortOrder((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortBy(key);
      setSortOrder('asc');
    }
    setPage(1);
  };

  const handleFilterChange = (key: string, value: unknown) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    setPage(1);
  };

  const handleSelectAll = (checked: boolean) => {
    if (checked) {
      setSelectedKeys(data.map(getKey));
    } else {
      setSelectedKeys([]);
    }
  };

  const handleSelectionChange = (key: string, checked: boolean) => {
    setSelectedKeys((prev) =>
      checked ? [...prev, key] : prev.filter((k) => k !== key)
    );
    onSelectionChange?.(checked ? [...selectedKeys, key] : selectedKeys.filter((k) => k !== key));
  };

  const processedData = useMemo(() => {
    let result = [...data];

    // Apply filters
    Object.entries(filters).forEach(([key, value]) => {
      if (value !== '' && value !== undefined && value !== null) {
        const column = columns.find((c) => c.key === key);
        if (column?.accessor) {
          result = result.filter((row) => {
            const cellValue = typeof column.accessor === 'function' ? column.accessor(row) : (row as Record<string, unknown>)[column.accessor as string];
            return String(cellValue).toLowerCase().includes(String(value).toLowerCase());
          });
        }
      }
    });

    // Apply sorting
    if (sortBy) {
      const column = columns.find((c) => c.key === sortBy);
      if (column?.accessor) {
        result.sort((a, b) => {
          const aVal = typeof column.accessor === 'function' ? column.accessor(a) : (a as Record<string, unknown>)[column.accessor as string];
          const bVal = typeof column.accessor === 'function' ? column.accessor(b) : (b as Record<string, unknown>)[column.accessor as string];
          const comparison = String(aVal).localeCompare(String(bVal), undefined, { numeric: true });
          return sortOrder === 'asc' ? comparison : -comparison;
        });
      }
    }

    return result;
  }, [data, columns, filters, sortBy, sortOrder]);

  const totalItems = processedData.length;
  const pageCount = Math.ceil(totalItems / pageSize);
  const paginatedData = pagination
    ? processedData.slice((page - 1) * pageSize, page * pageSize)
    : processedData;

  if (page > pageCount && page > 1) {
    setPage(pageCount);
  }

  return (
    <TableContainer className={className}>
      <table className="w-full" {...props}>
        <TableHeader
          columns={columns as Column[]}
          sortBy={sortBy}
          sortOrder={sortOrder}
          onSort={handleSort}
          filters={filters}
          onFilterChange={handleFilterChange}
          selection={selection}
          onSelectAll={handleSelectAll}
          compact={compact}
        />
        {loading ? (
          <TableLoading columns={columns as Column[]} compact={compact} />
        ) : (
          <>
            <TableBody
              columns={columns as Column[]}
              data={paginatedData}
              keyAccessor={keyAccessor}
              selection={selection}
              selectedKeys={selectedKeys}
              onSelectionChange={handleSelectionChange}
              striped={striped}
              hoverable={hoverable}
              compact={compact}
              emptyMessage={emptyMessage}
            />
            <TableFooter
              columns={columns as Column[]}
              pagination={pagination}
              page={page}
              pageCount={pageCount}
              pageSize={pageSize}
              pageSizeOptions={pageSizeOptions}
              onPageChange={setPage}
              onPageSizeChange={setPageSize}
              total={totalItems}
            />
          </>
        )}
      </table>
    </TableContainer>
  );
}

export { Table, TableContainer, TableHeader, TableBody, TableFooter, TableLoading };
export { Table as DataTable };