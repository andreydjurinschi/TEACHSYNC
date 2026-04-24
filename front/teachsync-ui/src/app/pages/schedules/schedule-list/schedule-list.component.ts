import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { GroupCourseInfo, ScheduleBase, WeekDay } from '../../../core/models/schedules/schedule-base.model';
import { ScheduleService } from '../../../core/services/schedule.service';

const DAY_ORDER: WeekDay[] = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

@Component({
  selector: 'app-schedule-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './schedule-list.html',
})
export class ScheduleList implements OnInit {
  schedules    = signal<ScheduleBase[]>([]);
  loading      = signal(false);
  unscheduled  = signal<GroupCourseInfo[]>([]);
  showUnscheduled = signal(false);
  activeDay    = signal<WeekDay | 'all'>('all');

  private scheduleService = inject(ScheduleService);
  private router          = inject(Router);

  readonly DAY_ORDER = DAY_ORDER;
  readonly DAY_LABELS: Record<WeekDay, string> = {
    MON: 'Пн', TUE: 'Вт', WED: 'Ср',
    THU: 'Чт', FRI: 'Пт', SAT: 'Сб', SUN: 'Вс',
  };

  readonly todayKey = DAY_ORDER[(new Date().getDay() + 6) % 7];

  slotsForDay(day: WeekDay): ScheduleBase[] {
    return this.schedules()
      .filter(s => s.weekDays.includes(day))
      .sort((a, b) => a.startTime[0] * 60 + a.startTime[1] - (b.startTime[0] * 60 + b.startTime[1]));
  }

  filteredSchedules = computed<ScheduleBase[]>(() => {
    const d = this.activeDay();
    if (d === 'all') return this.schedules();
    return this.slotsForDay(d);
  });

  ngOnInit(): void {
    this.loading.set(true);
    this.scheduleService.getAll().subscribe({
      next:  d => { this.schedules.set(d); this.loading.set(false); },
      error: () => this.loading.set(false),
    });
  }

  setDay(day: WeekDay | 'all'): void { this.activeDay.set(day); }

  loadUnscheduled(): void {
    this.scheduleService.getUnscheduledGroupCourses().subscribe(d => {
      this.unscheduled.set(d);
      this.showUnscheduled.set(true);
    });
  }

  createForGroup(gc: GroupCourseInfo): void {
    this.router.navigate(['/schedules/create'], { queryParams: { groupCourseId: gc.id } });
  }

  formatTime(t: number[]): string {
    if (!t?.length) return '—';
    return `${String(t[0]).padStart(2, '0')}:${String(t[1]).padStart(2, '0')}`;
  }

  sortedDays(days: WeekDay[]): WeekDay[] {
    return [...days].sort((a, b) => DAY_ORDER.indexOf(a) - DAY_ORDER.indexOf(b));
  }
}