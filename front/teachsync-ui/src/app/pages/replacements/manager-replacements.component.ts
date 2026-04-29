import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { ReplacementRequest } from '../../core/models/replacements/replacement.model';
import { ReplacementService } from '../../core/services/replacement.service';

@Component({
  selector: 'app-manager-replacements',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manager-replacements.component.html'
})
export class ManagerReplacementsComponent implements OnInit {
  private replacementService = inject(ReplacementService);

  replacements = signal<ReplacementRequest[]>([]);
  loading = signal(true);
  loadError = signal<string | null>(null);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.loadError.set(null);
    this.replacementService.getProblematic().subscribe({
      next: data => {
        this.replacements.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loadError.set('Не удалось загрузить проблемные заявки на замену.');
        this.loading.set(false);
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
