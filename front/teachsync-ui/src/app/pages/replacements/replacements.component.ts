import { CommonModule } from '@angular/common';
import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { ReplacementRequest } from '../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../core/services/replacement.service';
import { RuleService } from '../../core/services/role.rule.service';
import { PaginationControlsComponent } from '../../shared/pagination/pagination-controls.component';
import { getTotalPages, paginateItems } from '../../shared/pagination/pagination.utils';

@Component({
  selector: 'app-replacements',
  standalone: true,
  imports: [CommonModule, PaginationControlsComponent],
  templateUrl: './replacements.component.html'
})
export class ReplacementsComponent implements OnInit {
  private replacementService = inject(ReplacementService);
  private ruleService = inject(RuleService);

  replacements = signal<ReplacementRequest[]>([]);
  loading = signal(true);
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);
  pendingPage = signal(1);
  requestedPage = signal(1);
  acceptedPage = signal(1);
  closedPage = signal(1);
  private readonly pageSize = 4;

  pendingForMe = computed(() => {
    const teacherId = this.ruleService.getId();
    return this.replacements().filter(item =>
      item.status === 'PENDING' &&
      item.teacherBaseInfoRequest.id !== teacherId &&
      !item.approvedByTeacherBaseInfoRequest
    );
  });

  requestedByMe = computed(() => {
    const teacherId = this.ruleService.getId();
    return this.replacements().filter(item => item.teacherBaseInfoRequest.id === teacherId);
  });

  acceptedByMe = computed(() => {
    const teacherId = this.ruleService.getId();
    return this.replacements().filter(item => item.approvedByTeacherBaseInfoRequest?.id === teacherId);
  });

  closedRequests = computed(() => {
    const teacherId = this.ruleService.getId();
    return this.replacements().filter(item =>
      item.status !== 'PENDING' &&
      item.status !== 'APPROVED' &&
      (item.teacherBaseInfoRequest.id === teacherId || item.approvedByTeacherBaseInfoRequest?.id === teacherId)
    );
  });

  pendingTotalPages = computed(() => getTotalPages(this.pendingForMe().length, this.pageSize));
  requestedTotalPages = computed(() => getTotalPages(this.requestedByMe().length, this.pageSize));
  acceptedTotalPages = computed(() => getTotalPages(this.acceptedByMe().length, this.pageSize));
  closedTotalPages = computed(() => getTotalPages(this.closedRequests().length, this.pageSize));

  pendingForMePage = computed(() => paginateItems(this.pendingForMe(), this.pendingPage(), this.pageSize));
  requestedByMePage = computed(() => paginateItems(this.requestedByMe(), this.requestedPage(), this.pageSize));
  acceptedByMePage = computed(() => paginateItems(this.acceptedByMe(), this.acceptedPage(), this.pageSize));
  closedRequestsPage = computed(() => paginateItems(this.closedRequests(), this.closedPage(), this.pageSize));

  constructor() {
    this.bindPage(this.pendingPage, this.pendingTotalPages);
    this.bindPage(this.requestedPage, this.requestedTotalPages);
    this.bindPage(this.acceptedPage, this.acceptedTotalPages);
    this.bindPage(this.closedPage, this.closedTotalPages);
  }

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) {
      this.loading.set(false);
      return;
    }
    this.loading.set(true);
    this.replacementService.getForTeacher(teacherId).subscribe({
      next: data => {
        this.replacements.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.actionError.set('Не удалось загрузить заявки на замену.');
        this.loading.set(false);
      }
    });
  }

  approve(requestId: number): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) return;
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.approve(requestId, teacherId).subscribe({
      next: request => {
        this.actionMessage.set('Замена подтверждена.');
        this.replacements.update(list => [request, ...list.filter(item => item.id !== request.id)]);
      },
      error: err => {
        this.actionError.set(err?.error?.message ?? 'Не удалось подтвердить замену.');
      }
    });
  }

  decline(requestId: number): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) return;
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.decline(requestId, teacherId).subscribe({
      next: request => {
        this.actionMessage.set('Приглашение на замену отклонено.');
        this.replacements.update(list => [request, ...list.filter(item => item.id !== request.id)]);
      },
      error: err => {
        this.actionError.set(err?.error?.message ?? 'Не удалось отклонить замену.');
      }
    });
  }

  cancel(requestId: number): void {
    const teacherId = this.ruleService.getId();
    if (teacherId == null) return;
    this.actionMessage.set(null);
    this.actionError.set(null);
    this.replacementService.cancel(requestId, teacherId).subscribe({
      next: request => {
        this.actionMessage.set('Заявка на замену отменена.');
        this.replacements.update(list => [request, ...list.filter(item => item.id !== request.id)]);
      },
      error: err => {
        this.actionError.set(err?.error?.message ?? 'Не удалось отменить заявку.');
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
      PENDING: 'Поиск',
      APPROVED: 'Подтверждена',
      DECLINED: 'Отклонена',
      EXPIRED: 'Просрочена',
      CANCELLED: 'Отменена',
      AUTO_CLOSED: 'Автозакрыта'
    };
    return map[status];
  }

  private bindPage(page: ReturnType<typeof signal<number>>, totalPages: ReturnType<typeof computed<number>>): void {
    effect(() => {
      const maxPage = totalPages();
      if (page() > maxPage) {
        page.set(maxPage);
      }
    });
  }
}
