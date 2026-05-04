export function getTotalPages(totalItems: number, pageSize: number): number {
  const safePageSize = Math.max(1, pageSize);
  return Math.max(1, Math.ceil(totalItems / safePageSize));
}

export function paginateItems<T>(items: T[], page: number, pageSize: number): T[] {
  const safePage = Math.max(1, page);
  const safePageSize = Math.max(1, pageSize);
  const start = (safePage - 1) * safePageSize;
  return items.slice(start, start + safePageSize);
}
