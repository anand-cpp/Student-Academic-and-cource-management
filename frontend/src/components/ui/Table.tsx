import React from 'react'

interface Column<T> {
  key: string
  header: string
  render?: (row: T, index: number) => React.ReactNode
  className?: string
  headerClassName?: string
  width?: string
}

interface TableProps<T> {
  data: T[]
  columns: Column<T>[]
  keyExtractor: (row: T) => string | number
  loading?: boolean
  emptyMessage?: string
  className?: string
  rowClassName?: (row: T, index: number) => string
  onRowClick?: (row: T) => void
  striped?: boolean
  hoverable?: boolean
  bordered?: boolean
}

export function Table<T>({
  data,
  columns,
  keyExtractor,
  loading = false,
  emptyMessage = 'No data available',
  className = '',
  rowClassName,
  onRowClick,
  striped = true,
  hoverable = true,
  bordered = true,
}: TableProps<T>) {
  if (loading) {
    return (
      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key} className={column.headerClassName} style={{ width: column.width }}>
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr>
              <td colSpan={columns.length} className="px-4 py-8 text-center">
                <div className="flex items-center justify-center">
                  <svg className="animate-spin h-8 w-8 text-primary-600" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                  </svg>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }

  if (data.length === 0) {
    return (
      <div className="table-container">
        <table className="table">
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={column.key} className={column.headerClassName} style={{ width: column.width }}>
                  {column.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            <tr>
              <td colSpan={columns.length} className="px-4 py-12 text-center text-gray-500">
                {emptyMessage}
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }

  return (
    <div className={`table-container ${className}`}>
      <table className="table">
        <thead>
          <tr>
            {columns.map((column) => (
              <th
                key={column.key}
                className={column.headerClassName}
                style={{ width: column.width }}
              >
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr
              key={keyExtractor(row)}
              className={`${rowClassName ? rowClassName(row, index) : ''} ${onRowClick ? 'cursor-pointer' : ''}`}
              onClick={() => onRowClick && onRowClick(row)}
            >
              {columns.map((column) => (
                <td key={column.key} className={column.className}>
                  {column.render ? column.render(row, index) : (row as any)[column.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

interface TablePaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  showPageSizeSelector?: boolean
  pageSize?: number
  onPageSizeChange?: (size: number) => void
}

function renderPageButton(page: number | string, currentPage: number, totalPages: number, onPageChange: (page: number) => void) {
  if (page === '...') {
    return <span key="ellipsis" className="px-2 text-gray-400">...</span>
  }

  const pageNum = page as number
  const isActive = currentPage === pageNum

  return (
    <button
      key={`page-${pageNum}`}
      onClick={() => onPageChange(pageNum)}
      className={`btn-sm ${isActive ? 'btn-primary' : 'btn-secondary'}`}
      aria-label={`Page ${pageNum}`}
      aria-current={isActive ? 'page' : undefined}
    >
      {pageNum}
    </button>
  )
}

function renderPaginationButtons(
  pages: (number | string)[],
  currentPage: number,
  totalPages: number,
  onPageChange: (page: number) => void
) {
  return pages.map((page, index) => {
    if (page === '...') {
      return <span key={`ellipsis-${index}`} className="px-2 text-gray-400">...</span>
    }

    const pageNum = page as number
    const isActive = currentPage === pageNum

    return (
      <button
        key={`page-${pageNum}`}
        onClick={() => onPageChange(pageNum)}
        className={`btn-sm ${isActive ? 'btn-primary' : 'btn-secondary'}`}
        aria-label={`Page ${pageNum}`}
        aria-current={isActive ? 'page' : undefined}
      >
        {pageNum}
      </button>
    )
  })
}

export function TablePagination({
  currentPage,
  totalPages,
  onPageChange,
  showPageSizeSelector = false,
  pageSize,
  onPageSizeChange,
}: TablePaginationProps) {
  const pages = React.useMemo(() => {
    const result: (number | string)[] = []
    const maxVisible = 5
    let start = Math.max(1, currentPage - Math.floor(maxVisible / 2))
    let end = Math.min(totalPages, start + maxVisible - 1)

    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1)
    }

    if (start > 1) {
      result.push(1)
      if (start > 2) result.push('...')
    }

    for (let i = start; i <= end; i++) {
      result.push(i)
    }

    if (end < totalPages) {
      if (end < totalPages - 1) result.push('...')
      result.push(totalPages)
    }

    return result
  }, [currentPage, totalPages])

  return (
    <div className="flex items-center justify-between px-4 py-3 border-t border-gray-100">
      <div className="text-sm text-gray-500">
        Page {currentPage} of {totalPages}
      </div>
      <div className="flex items-center gap-2">
        {showPageSizeSelector && (
          <select
            value={pageSize}
            onChange={(e) => onPageSizeChange?.(Number(e.target.value))}
            className="input input-sm w-auto"
          >
            <option value={10}>10 per page</option>
            <option value={25}>25 per page</option>
            <option value={50}>50 per page</option>
            <option value={100}>100 per page</option>
          </select>
        )}
        <button
          onClick={() => onPageChange(1)}
          disabled={currentPage === 1}
          className="btn-secondary btn-sm"
          aria-label="First page"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 19l-7-7 7-7m8 14l-7-7 7-7" /></svg>
        </button>
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="btn-secondary btn-sm"
          aria-label="Previous page"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" /></svg>
        </button>
        {pages.map((page, index) => {
          if (page === '...') {
            return <span key={`ellipsis-${index}`} className="px-2 text-gray-400">...</span>
          }

          const pageNum = page as number
          const isActive = currentPage === pageNum

          return (
            <button
              key={`page-${pageNum}`}
              onClick={() => onPageChange(pageNum)}
              className={`btn-sm ${isActive ? 'btn-primary' : 'btn-secondary'}`}
              aria-label={`Page ${pageNum}`}
              aria-current={isActive ? 'page' : undefined}
            >
              {pageNum}
            </button>
          )
        })}
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="btn-secondary btn-sm"
          aria-label="Next page"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" /></svg>
        </button>
        <button
          onClick={() => onPageChange(totalPages)}
          disabled={currentPage === totalPages}
          className="btn-secondary btn-sm"
          aria-label="Last page"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 5l7 7-7 7" /></svg>
        </button>
      </div>
    </div>
  )
}