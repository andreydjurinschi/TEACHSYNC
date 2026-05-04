import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination-controls',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (totalPages > 1) {
      <div class="mt-4 flex flex-col gap-3 border-t border-slate-200 pt-4 sm:flex-row sm:items-center sm:justify-between dark:border-slate-800">

        <div class="flex items-center gap-2">
          <button
            type="button"
            (click)="previous()"
            [disabled]="currentPage <= 1"
            class="app-btn-secondary px-3 py-1.5 text-xs disabled:cursor-not-allowed disabled:opacity-50">
            Назад
          </button>

          <span class="min-w-24 text-center text-xs font-medium text-slate-600 dark:text-slate-300">
            {{ currentPage }} / {{ totalPages }}
          </span>

          <button
            type="button"
            (click)="next()"
            [disabled]="currentPage >= totalPages"
            class="app-btn-secondary px-3 py-1.5 text-xs disabled:cursor-not-allowed disabled:opacity-50">
            Вперед
          </button>
        </div>
      </div>
    }
  `
})
export class PaginationControlsComponent {
  @Input() currentPage = 1;
  @Input() totalPages = 1;
  @Input() totalItems = 0;
  @Input() itemLabel = 'items';
  @Output() pageChange = new EventEmitter<number>();

  previous(): void {
    if (this.currentPage > 1) {
      this.pageChange.emit(this.currentPage - 1);
    }
  }

  next(): void {
    if (this.currentPage < this.totalPages) {
      this.pageChange.emit(this.currentPage + 1);
    }
  }
}
