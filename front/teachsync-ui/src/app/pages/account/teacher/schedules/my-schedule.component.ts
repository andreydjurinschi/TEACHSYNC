import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ScheduleService } from '../../../../core/services/schedule.service';
import { ScheduleBase } from '../../../../core/models/schedules/schedule-base.model';

const DAY_ORDER = ['MON','TUE','WED','THU','FRI','SAT','SUN'];

@Component({
  selector: 'app-teacher-schedule',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-schedule.component.html'
})
export class TeacherScheduleComponent implements OnInit {
  private scheduleService = inject(ScheduleService);

  schedules = signal<ScheduleBase[]>([]);
  loading   = signal(true);

  readonly DAY_LABELS: Record<string, string> = {
    MON: 'Пн', TUE: 'Вт', WED: 'Ср',
    THU: 'Чт', FRI: 'Пт', SAT: 'Сб', SUN: 'Вс'
  };

  ngOnInit(): void {
    this.scheduleService.getMySchedule().subscribe({
      next: d => { this.schedules.set(d); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }

  formatTime(t: number[]): string {
    if (!t) return '—';
    return `${String(t[0]).padStart(2,'0')}:${String(t[1]).padStart(2,'0')}`;
  }

  sortedDays(days: string[]): string[] {
    return [...days].sort((a, b) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b));
  }
}