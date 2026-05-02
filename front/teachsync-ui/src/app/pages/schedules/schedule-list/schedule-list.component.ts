import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ClassRoomInfo, GroupCourseInfo, ScheduleBase, WeekDay } from '../../../core/models/schedules/schedule-base.model';
import { ScheduleService } from '../../../core/services/schedule.service';
import { RuleService } from '../../../core/services/role.rule.service';

const DAY_ORDER: WeekDay[] = ['MON', 'TUE', 'WED', 'THU', 'FRI', 'SAT', 'SUN'];

type ScheduleEditForm = {
  startTime: string;
  endTime: string;
  weekDays: WeekDay[];
  groupCourseId: number | null;
  classRoomId: number | null;
};

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
  groupCourses = signal<GroupCourseInfo[]>([]);
  classRooms = signal<ClassRoomInfo[]>([]);
  editing = signal<ScheduleBase | null>(null);
  savingEdit = signal(false);
  editError = signal<string | null>(null);
  editForm = signal<ScheduleEditForm>({
    startTime: '',
    endTime: '',
    weekDays: [] as WeekDay[],
    groupCourseId: null as number | null,
    classRoomId: null as number | null,
  });

  private scheduleService = inject(ScheduleService);
  readonly ruleService = inject(RuleService);
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
    this.scheduleService.getAllGroupCourses().subscribe(d => this.groupCourses.set(d));
    this.scheduleService.getAllClassrooms().subscribe(d => this.classRooms.set(d));
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

  canEdit(): boolean {
    return this.ruleService.isAdmin() || this.ruleService.isManager();
  }

  startEdit(schedule: ScheduleBase): void {
    this.editing.set(schedule);
    this.editError.set(null);
    this.editForm.set({
      startTime: this.formatTime(schedule.startTime),
      endTime: this.formatTime(schedule.endTime),
      weekDays: [...schedule.weekDays],
      groupCourseId: schedule.groupCourseDto?.id ?? null,
      classRoomId: schedule.classRoomBaseDto?.id ?? null,
    });
  }

  closeEdit(): void {
    this.editing.set(null);
    this.editError.set(null);
    this.savingEdit.set(false);
  }

  updateEditField<K extends keyof ScheduleEditForm>(key: K, value: ScheduleEditForm[K]): void {
    this.editForm.update(form => ({ ...form, [key]: value }));
  }

  toggleEditDay(day: WeekDay): void {
    this.editForm.update(form => ({
      ...form,
      weekDays: form.weekDays.includes(day)
        ? form.weekDays.filter(item => item !== day)
        : [...form.weekDays, day],
    }));
  }

  saveEdit(): void {
    const schedule = this.editing();
    const form = this.editForm();
    if (!schedule || !form.startTime || !form.endTime || !form.groupCourseId || !form.classRoomId || !form.weekDays.length) {
      this.editError.set('Заполните время, дни, курс/группу и аудиторию.');
      return;
    }

    this.savingEdit.set(true);
    this.editError.set(null);
    this.scheduleService.update(schedule.id, form).subscribe({
      next: updated => {
        this.schedules.update(list => list.map(item => item.id === updated.id ? updated : item));
        this.closeEdit();
      },
      error: err => {
        this.editError.set(err?.error?.message ?? 'Не удалось изменить расписание');
        this.savingEdit.set(false);
      }
    });
  }
}
