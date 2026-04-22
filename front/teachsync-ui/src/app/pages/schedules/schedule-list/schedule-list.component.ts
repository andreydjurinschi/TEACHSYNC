import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { GroupCourseInfo, ScheduleBase, WeekDay } from '../../../core/models/schedules/schedule-base.model';
import { ScheduleService } from '../../../core/services/schedule.service';

@Component({
  selector: 'app-schedule-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './schedule-list.html',
})
export class ScheduleList implements OnInit {
  schedules = signal<ScheduleBase[]>([]);
  loading = signal(false);

  private scheduleService = inject(ScheduleService);
  private router = inject(Router);

  readonly DAY_LABELS: Record<WeekDay, string> = {
    MON: 'Пн',
    TUE: 'Вт',
    WED: 'Ср',
    THU: 'Чт',
    FRI: 'Пт',
    SAT: 'Сб',
    SUN: 'Вс',
  };

  readonly DAY_ORDER: WeekDay[] = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];
  unscheduled = signal<GroupCourseInfo[]>([]);
  showUnscheduled = signal(false);

loadUnscheduled(): void {
  this.scheduleService.getUnscheduledGroupCourses().subscribe(d => {
    this.unscheduled.set(d);
    this.showUnscheduled.set(true);
  });
}
  ngOnInit(): void {
    this.loading.set(true);
    this.scheduleService.getAll().subscribe({
      next: data => {
        this.schedules.set(data);
        this.loading.set(false);
      },
      error: err => {
        console.error(err);
        this.loading.set(false);
      }
    });
  }

  sortedDays(days: WeekDay[]): WeekDay[] {
    return [...days].sort((a, b) =>
      this.DAY_ORDER.indexOf(a) - this.DAY_ORDER.indexOf(b)
    );
  }

  formatTime(t: number[]): string {
    if (!t?.length) return '—';
    return `${String(t[0]).padStart(2, '0')}:${String(t[1]).padStart(2, '0')}`;
  }

  createForGroup(gc: GroupCourseInfo): void {
  this.router.navigate(['/schedules/create'], {
    queryParams: { groupCourseId: gc.id }
  });
}
}