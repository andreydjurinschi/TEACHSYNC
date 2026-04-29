import { CommonModule } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ReplacementRequest } from '../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../core/services/replacement.service';
import { RuleService } from '../../core/services/role.rule.service';

@Component({
  selector: 'app-replacements',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './replacements.component.html'
})
export class ReplacementsComponent implements OnInit {
  private replacementService = inject(ReplacementService);
  private ruleService = inject(RuleService);

  replacements = signal<ReplacementRequest[]>([]);
  loading = signal(true);
  actionMessage = signal<string | null>(null);
  actionError = signal<string | null>(null);

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
}
