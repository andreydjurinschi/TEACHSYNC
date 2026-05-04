import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { ReplacementRequest } from '../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../core/services/replacement.service';
import { PaginationControlsComponent } from '../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-manager-replacements',
  standalone: true,
  imports: [CommonModule, PaginationControlsComponent],
  templateUrl: './manager-replacements.component.html'
})
export class ManagerReplacementsComponent implements OnInit {
  private replacementService = inject(ReplacementService);

  replacements = signal<ReplacementRequest[]>([]);
  loading = signal(true);
  loadError = signal<string | null>(null);
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);
  currentPage = signal(1);
  private readonly pageSize = 6;
  totalPages = computed(() => getTotalPages(this.replacements().length, this.pageSize));
  visibleReplacements = computed(() => paginateItems(this.replacements(), this.currentPage(), this.pageSize));

  constructor() {
    effect(() => {
      const maxPage = this.totalPages();
      if (this.currentPage() > maxPage) {
        this.currentPage.set(maxPage);
      }
    });
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.replacementService.getProblematic().subscribe({
      next: data => {
        this.replacements.set(data);
        this.currentPage.set(1);
        this.loading.set(false);
      },
      error: () => {
        this.loadError.set('Не удалось загрузить проблемные заявки на замену.');
        this.loading.set(false);
      }
    });
  }

  deleteRequest(requestId: number): void {
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.delete(requestId).subscribe({
      next: () => {
        this.replacements.update(list => list.filter(item => item.id !== requestId));
        this.actionMessage.set('Заявка на замену удалена.');
      },
      error: err => {
        this.actionError.set(err?.error?.message ?? 'Не удалось удалить заявку на замену.');
      }
    });
  }

  teacherName(teacher?: { name?: string; surname?: string; fullName?: string }): string {
    if (!teacher) return '—';
    return teacher.fullName || `${teacher.name ?? ''} ${teacher.surname ?? ''}`.trim();
  }

  formatTime(t?: number[]): string {
    if (!t) return '—';
    return `${String(t[0]).padStart(2, '0')}:${String(t[1]).padStart(2, '0')}`;
  }

  statusLabel(status: ReplacementRequest['status']): string {
    const map: Record<ReplacementRequest['status'], string> = {
      PENDING: 'Требует внимания',
      APPROVED: 'Подтверждена',
      DECLINED: 'Отклонена',
      EXPIRED: 'Просрочена',
      CANCELLED: 'Отменена',
      AUTO_CLOSED: 'Автозакрыта'
    };
    return map[status];
  }
}
